package my.edu.wix1002.goldenhour;

import my.edu.wix1002.goldenhour.model.Employee;
import my.edu.wix1002.goldenhour.model.Model;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * SalesSystem - handles interactive sale recording, stock update and receipt generation.
 *
 * Integration:
 * - Call SalesSystem.recordNewSale(scanner, loggedInEmployee, allModels)
 *   from Main.showEmployeeMenu when user selects "Record New Sale".
 *
 * Notes:
 * - Adjust MODEL_CSV_PATH if your DataLoader exposes a public constant for the model CSV location.
 * - Employee.getOutletCode() must be set (Employee constructor must assign outletCode).
 */
public class salesSystem {

    // Change this path if DataLoader.MODEL_FILE_PATH becomes public or you want a relative path.
    private static final String MODEL_CSV_PATH = "C:/Users/Chin Shi Er/OneDrive/Documents/Coding/system_operation_management_fop/data/model.csv";
    private static final Path SALES_DIR = Paths.get("data", "sales");
    /*ofPattern(): create DateTimeFormatter instance by specifying a custom date and/or time pattern.
     * converting a string to a date/time object and vice versa.　
    */
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    // a: AM/PM marker
    private static final DateTimeFormatter TIME_PRINT_FMT = DateTimeFormatter.ofPattern("hh:mm a");
    private static final DateTimeFormatter TX_ID_FMT = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssSSS");

    public static void recordNewSale(Scanner scanner, Employee loggedInEmployee, List<Model> allModels) {
        if (loggedInEmployee == null) {
            System.out.println("Error: no logged-in employee.");
            return;
        }
        String outletCode = loggedInEmployee.getOutletCode();
        if (outletCode == null || outletCode.isEmpty()) {
            System.out.println("Error: employee outlet code not set. Please fix Employee data.");
            return;
        }

        // Quick lookup map modelId -> Model (uppercase keys)
        Map<String, Model> modelMap = new HashMap<>();
        for (Model m : allModels) {
            if (m != null && m.getModelId() != null) {
                modelMap.put(m.getModelId().toUpperCase(), m);
            }
        }

        LocalDateTime now = LocalDateTime.now();
        System.out.println("\n=== Record New Sale ===");
        System.out.println("Date: " + now.toLocalDate().format(DATE_FMT));
        System.out.println("Time: " + now.format(TIME_PRINT_FMT));
        System.out.print("Customer Name: ");
        String customerName = scanner.nextLine().trim();

        List<SaleItem> items = new ArrayList<>();

        while (true) {
            System.out.print("Enter Model: ");
            String modelInput = scanner.nextLine().trim().toUpperCase();
            if (modelInput.isEmpty()) {
                System.out.println("Model cannot be empty. Try again.");
                continue;
            }
            Model model = modelMap.get(modelInput);
            if (model == null) {
                System.out.println("Model not found: " + modelInput);
                continue;
            }

            // Show unit price and available stock at employee outlet
            BigDecimal unitPrice = BigDecimal.valueOf(model.getPrice());
            int currentStockVal = model.getStockByOutlet().getOrDefault(outletCode, 0);
            System.out.println("Unit Price: RM" + formatCurrency(unitPrice) + " | Available at " + outletCode + ": " + currentStockVal);

            System.out.print("Enter Quantity: ");
            String qtyLine = scanner.nextLine().trim();
            int quantityInput;
            try {
                quantityInput = Integer.parseInt(qtyLine);
                if (quantityInput <= 0) {
                    System.out.println("Quantity must be greater than zero. Try again.");
                    continue;
                }
                //NumberFormatException: avoid  string containing a decimal point or comma, "hello" or "N64"
            } catch (NumberFormatException ex) {
                System.out.println("Invalid quantity. Try again.");
                continue;
            }

            if (quantityInput > currentStockVal) {
                System.out.println("Insufficient stock. Available: " + currentStockVal + ". Try again.");
                continue;
            }

            items.add(new SaleItem(modelInput, unitPrice, quantityInput));

            System.out.print("Are there more items purchased? (Y/N): ");
            String moreInput = scanner.nextLine().trim().toUpperCase();
            if (!moreInput.equals("Y")) break;
        }

        if (items.isEmpty()) {
            System.out.println("No items added. Cancelling transaction.");
            return;
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        for (SaleItem it : items) {
            subtotal = subtotal.add(it.unitPrice.multiply(BigDecimal.valueOf(it.quantity)));
        }

        System.out.print("Enter transaction method (Cash, Debit Card, Credit Card, E-wallet, Other): ");
        String paymentMethod = scanner.nextLine().trim();
        System.out.println("Subtotal: RM" + formatCurrency(subtotal));

        System.out.print("Confirm sale? (Y/N): ");
        String confirmPayment = scanner.nextLine().trim().toUpperCase();
        if (!confirmPayment.equals("Y")) {
            System.out.println("Transaction cancelled.");
            return;
        }

        // Backup quantities for rollback if needed
        Map<String, Integer> backups = new HashMap<>();
        for (SaleItem it : items) {
            Model m = modelMap.get(it.modelId);
            int curr = m.getStockByOutlet().getOrDefault(outletCode, 0);
            backups.put(it.modelId, curr);
            m.getStockByOutlet().put(outletCode, curr - it.quantity);
        }

        // Persist model.csv with updated quantities for this outlet
        boolean persisted = persistModelCsv(allModels, outletCode);
        if (!persisted) {
            System.out.println("Failed to persist stock changes. Transaction aborted.");
            // Undo in-memory changes
            for (Map.Entry<String, Integer> e : backups.entrySet()) {
                Model m = modelMap.get(e.getKey());
                if (m != null) {
                    m.getStockByOutlet().put(outletCode, e.getValue());
                }
            }
            return;
        }

        // Append receipt (transaction id to help tracing)
        String txId = now.format(TX_ID_FMT) + "-" + (new Random().nextInt(900) + 100);
        Sale sale = new Sale(txId, now, loggedInEmployee, customerName, items, paymentMethod, subtotal);
        try {
            appendReceipt(sale);
            //to catch error : A file doesn't exist or is inaccessible/Permission issues/loss o trnasfer
        } catch (IOException e) {
            System.err.println("Failed to generate receipt: " + e.getMessage());
            // Note: model.csv already updated; for strict atomicity you'd implement compensating actions or journaling.
            return;
        }

        System.out.println("Transaction successful.");
        System.out.println("Sale recorded successfully.");
        System.out.println("Model quantities updated successfully.");
        System.out.println("Receipt generated: sales_" + now.toLocalDate().format(DATE_FMT) + ".txt");
        System.out.println("Transaction ID: " + txId);
    }

    /**
     * Persist the model CSV by updating the column for the given outlet code.
     * Returns true on success, false on error.
     */
    private static boolean persistModelCsv(List<Model> allModels, String outletCode) {
        Path modelPath = Paths.get(MODEL_CSV_PATH);
        if (!Files.exists(modelPath)) {
            System.err.println("Model CSV not found: " + MODEL_CSV_PATH);
            return false;
        }

        try {
            //readAllLines(): read all lines from a file into a List<String>.Charset is used to decoding the bytes from the file into characters
            //largefile use BufferedReader
            List<String> lines = Files.readAllLines(modelPath, StandardCharsets.UTF_8);
            if (lines.isEmpty()) {
                System.err.println("Model CSV is empty: " + MODEL_CSV_PATH);
                return false;
            }

            /*read the headder row (first line) of the CSV file, and split the headerRow into col 
            using comma as separator
            The second argument -1 ensures trailing empty columns are included (important if some rows end with commas).
            Result: headers is an array of header strings, e.g. headers[0] = "Model", headers[2] = "C60".
            */
            String headerLine = lines.get(0);
            String[] headers = headerLine.split(",", -1);
            /*Remove BOM(Byte Order Mark- It is a Unicode code point U+FEFF used at the start of a text stream) if present on first header 
            Microsoft Excel and some Windows tools prepend the three-byte UTF-8 BOM sequence (0xEF,0xBB,0xBF) at the start of files.*/ 
            headers[0] = headers[0].replace("\uFEFF", "");

            //for loop searches headers which equals the outletCode for updating stock
            //outletIndex = -1 : sentinel value meaning "not found", valid col index return >=0
            int outletIndex = -1;
            for (int i = 0; i < headers.length; i++) {
                if (headers[i].trim().equalsIgnoreCase(outletCode)) {
                    outletIndex = i;
                    break;
                }
            }
            //outletIndex == -1 tells the code the requested outlet column was not present in the CSV header
            if (outletIndex == -1) {
                System.err.println("Outlet code " + outletCode + " not found in model CSV header.");
                return false;
            }

            // Build quick map modelId -> Model
            Map<String, Model> map = new HashMap<>();
            for (Model m : allModels) {
                if (m != null && m.getModelId() != null) {
                    map.put(m.getModelId().toUpperCase(), m);
                }
            }

            //Creates a new list to hold the output CSV lines (in memory) that will write back to disk
            List<String> outLines = new ArrayList<>();
            // Preserve the header exactly as read (including BOM if present)
            outLines.add(headerLine);

            //iterate each CSV row,fetch the line and preserve blank lines
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line == null || line.trim().isEmpty()) {
                    outLines.add(line);
                    continue;
                }
                String[] cols = line.split(",", -1);
                String modelId = cols[0].trim().toUpperCase();
                Model m = map.get(modelId); //look up Model object by modelId
                if (m != null) {
                    Integer qty = m.getStockByOutlet().get(outletCode);
                    //valueof(): convert Integer to String
                    cols[outletIndex] = String.valueOf(qty == null ? 0 : qty);
                }
                //rebuild the CSV row from the cols array by joining columns with commas and add to outLines
                outLines.add(String.join(",", cols));
            }

            /*Write to temp file in same directory then move atomically,getParent(): return the have original CSV file path
            temp-file + move pattern prevents interrupted writes during midway crashes. Renaming ensure the ori. file remains intact 
            AtomicMoveNotSupportedException ensure that supported move are ont eh same filesystem
            UTF-8 supported by JVM (safer then JVM default sometimes) and other editors/Excel
            Example output: modelPath = .../data/model.csv; outlines : 
            "Model,Price,C60,C61"
            "SW2500-1,845,3,2"
            "DW2300-1,399,1,4"
            then, tmp file created as .../data/model-1234567890.tmp,writes 3 lines (with the platform line separator between lines).
            Files.move(tmp, modelPath, REPLACE_EXISTING, ATOMIC_MOVE) renames model-1234567890.tmp to model.csv
            */ 
            Path dir = modelPath.getParent();
            Path tmp = Files.createTempFile(dir, "model-", ".tmp");
            Files.write(tmp, outLines, StandardCharsets.UTF_8);

            //Replace the ori. file atomically(to avoid partial writes if interrupted)
            try {
                Files.move(tmp, modelPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException amnse) {
                // Fallback if atomic move not supported
                Files.move(tmp, modelPath, StandardCopyOption.REPLACE_EXISTING);
            }

            return true;
        } catch (IOException e) {
            System.err.println("Error persisting model CSV: " + e.getMessage());
            return false;
        }
    }

    /**
     * Append a receipt block to data/sales/sales_yyyy-MM-dd.txt
     */
    private static void appendReceipt(Sale sale) throws IOException {
        if (!Files.exists(SALES_DIR)) {
            Files.createDirectories(SALES_DIR);
        }
        LocalDate date = sale.timestamp.toLocalDate();
        Path receiptFile = SALES_DIR.resolve("sales_" + date.format(DATE_FMT) + ".txt");

        StringBuilder sb = new StringBuilder();
        sb.append("Date: ").append(date.format(DATE_FMT)).append(System.lineSeparator());
        sb.append("Time: ").append(sale.timestamp.format(TIME_PRINT_FMT)).append(System.lineSeparator());
        sb.append("Employee: ").append(sale.employee.getEmployeeID())
          .append(" - ").append(sale.employee.getName()).append(System.lineSeparator());
        sb.append("Customer Name: ").append(sale.customerName).append(System.lineSeparator());
        sb.append("Item(s) Purchased:").append(System.lineSeparator());
        for (SaleItem it : sale.items) {
            sb.append("Enter Model: ").append(it.modelId).append(System.lineSeparator());
            sb.append("Enter Quantity: ").append(it.quantity).append(System.lineSeparator());
            sb.append("Unit Price: RM").append(formatCurrency(it.unitPrice)).append(System.lineSeparator());
        }
        sb.append("Transaction Method: ").append(sale.transactionMethod).append(System.lineSeparator());
        sb.append("Subtotal: RM").append(formatCurrency(sale.subtotal)).append(System.lineSeparator());
        sb.append("Transaction ID: ").append(sale.transactionId).append(System.lineSeparator());
        sb.append("-----------------------------------------------------").append(System.lineSeparator());

        try (BufferedWriter writer = Files.newBufferedWriter(receiptFile, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(sb.toString());
        }
    }

    // Helper classes

    private static class SaleItem {
        String modelId;
        BigDecimal unitPrice;
        int quantity;

        SaleItem(String modelId, BigDecimal unitPrice, int quantity) {
            this.modelId = modelId;
            this.unitPrice = unitPrice;
            this.quantity = quantity;
        }
    }

    private static class Sale {
        String transactionId;
        LocalDateTime timestamp;
        Employee employee;
        String customerName;
        List<SaleItem> items;
        String transactionMethod;
        BigDecimal subtotal;

        Sale(String transactionId, LocalDateTime timestamp, Employee employee, String customerName,
             List<SaleItem> items, String transactionMethod, BigDecimal subtotal) {
            this.transactionId = transactionId;
            this.timestamp = timestamp;
            this.employee = employee;
            this.customerName = customerName;
            this.items = items;
            this.transactionMethod = transactionMethod;
            this.subtotal = subtotal;
        }
    }

    private static String formatCurrency(BigDecimal value) {
        value = value.setScale(2, RoundingMode.HALF_UP);
        if (value.stripTrailingZeros().scale() <= 0) {
            return value.toBigInteger().toString();
        } else {
            return value.toPlainString();
        }
    }
}