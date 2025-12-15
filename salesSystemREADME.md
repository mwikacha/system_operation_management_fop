1) Module purposes:
- This module (salesSystem) handles recording sales transactions:
  - Read sale input from the employee (customer name, model(s) and quantity, payment method).
  - Generate a unique SaleID (timestamp + random suffix) and timestamp the sale.
  - Append a CSV record (one row per item) to data/sales.csv.
  - Update in-memory stock and persist changed stock back to data/model.csv.
  - Write a human-readable receipt file under data/sales/sales_yyyy-MM-dd.txt (one file per day, appended for multiple sales on same day).
  - Roll back the CSV append or in-memory stock if persisting model.csv fails (best-effort).

2) Main structure / components
- Class: salesSystem
  - Constants:
    - MODEL_CSV_PATH (from DataLoader), SALES_DIR (data/sales directory), SALES_CSV (data/sales.csv)
    - DATE_FMT, TIME_PRINT_FMT, TX_ID_FMT — DateTimeFormatter instances for date/time formatting
  - Public method:
    - recordNewSale(Scanner scanner, Employee loggedInEmployee, List<Model> allModels)
      - Interactive flow: prompts user, validates input, constructs Sale object, appends CSV, persists model.csv, writes receipt.
  - Private helpers:
    - appendSaleToCsv(Sale sale) — writes rows to data/sales.csv
    - persistModelCsv(...) — updates data/model.csv atomically (writes temp file then moves)
    - appendReceipt(Sale sale) — writes human-readable receipt file for the day
    - removeSaleFromCsv(String saleId) — attempts to remove appended rows if persistModelCsv fails
    - escapeCsv(String field) — simple CSV escaping (quotes fields when needed)
    - formatCurrency(BigDecimal) — normalize currency string
  - Inner classes:
    - SaleItem (modelId, unitPrice, quantity)
    - Sale (transactionId, timestamp, employee, items, paymentMethod, subtotal)

3) How a sale is written into data/sales.csv (step-by-step)
- Build Sale and SaleItem objects in memory, compute line subtotals and total.
- appendSaleToCsv(sale) does the CSV append:
  1. Ensure the data/ directory exists (Files.createDirectories).
  2. Decide whether a header needs to be written: needHeader if the file does not exist or is empty.
  3. Check whether the existing csv file ends with a newline (RandomAccessFile used to read the last byte). This prevents the header and the first appended row from appearing on the same physical line.
     - If the file exists and does not end with newline, writer.newLine() is called before writing new rows.
     - If file is new/empty, write header then newline, then rows.
  4. For each SaleItem, produce a String[] fields:
     SaleID,EmployeeID,OutletCode,CustomerName,Model,Quantity,UnitPrice,Subtotal,PaymentMethod,Date,Time
     - Fields that may contain commas or quotes are escaped by escapeCsv(): internal quotes doubled and field wrapped in quotes if needed.
  5. Join columns with commas: String.join(",", cols), then writer.newLine() to end the row.
  6. The writer is created with Files.newBufferedWriter(SALES_CSV, UTF_8, CREATE, APPEND) and used inside try-with-resources (automatically closed).
- Important: one CSV row is written per SaleItem (so multi-item sales create multiple rows sharing the same SaleID).

4) Why header + row used to appear on the same line (and how it was fixed)
- Cause: When the file already contained a header but the file did not end with a newline character (e.g., header line ended with no trailing newline), opening the file and appending begins writing exactly after the last byte — so the new row continues on the same physical line as the header.
- Fix used: before appending we check the last byte using java.io.RandomAccessFile:
  - If last byte is not '\n' or '\r', writer.newLine() is written first so the next appended row starts at the next line.
  - If the file is new or empty we write the header and writer.newLine() explicitly.
- This approach is safe and simple (suitable for beginner use) and prevents header+row concatenation without adding extra blank lines.

5) Libraries and why they were used
- java.nio.file (Files, Paths, Path, StandardOpenOption, StandardCopyOption)
  - Modern file I/O APIs (since Java 7). Provide convenience methods to read/write files, create directories, and move/rename files.
  - Files.newBufferedWriter simplifies opening a writer with chosen charset and options (CREATE, APPEND).
  - Files.createTempFile + Files.move(..., ATOMIC_MOVE) used to make persisting model.csv atomic (write to a temp file, then replace the original). Atomic move reduces risk of leaving a partially written model.csv if the process crashes during write.
- java.nio.charset.StandardCharsets
  - Ensure UTF-8 encoding used consistently for cross-platform safety.
- java.time (LocalDateTime, DateTimeFormatter)
  - Modern date/time API (thread-safe, clearer than legacy Date/Calendar).
  - Used for timestamping transaction and formatting Date and Time for CSV and receipt.
- java.math.BigDecimal, RoundingMode
  - Used for currency math to avoid floating-point rounding errors and to format prices reliably.
- java.io.BufferedWriter / RandomAccessFile / IOException
  - BufferedWriter for efficient text output; RandomAccessFile is used to check the last byte of an existing file when deciding whether to insert newline.
- java.util (List, Map, HashMap, ArrayList, Random)
  - Standard Java collections and utility (Random used to add a small suffix to transaction ID ensuring uniqueness).

6) Less-common syntax and constructs explained
- try-with-resources:
  - Example: try (BufferedWriter writer = Files.newBufferedWriter(...)) { ... }
  - Automatically closes the writer when the block finishes (even on exceptions). Beginner tip: prevents resource leaks.
- Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND)
  - Opens or creates a file using UTF-8 and sets the writer to append mode. CREATE will create the file if it doesn’t exist.
- Files.createTempFile(dir, "model-", ".tmp") and Files.move(tmp, modelPath, REPLACE_EXISTING, ATOMIC_MOVE)
  - Pattern for atomic writes: write contents to a temp file in the same directory then replace the original file by moving the temp file over it. ATOMIC_MOVE tries to move in a single atomic filesystem operation.
- String.join(",", cols)
  - Combines an array or list of strings using comma separators to build the CSV line.
- escapeCsv(field)
  - Simple CSV escaping: if field contains comma, quote, newline or CR, it replaces each double quote with two double quotes and wraps the field in double quotes. This is the CSV standard convention.
- RandomAccessFile to read last byte:
  - A simple approach to test whether the existing file ends with newline without reading whole file.
  - Example: raf.seek(raf.length() - 1); int last = raf.read(); fileEndsWithNewline = (last == '\n' || last == '\r');

7) persistModelCsv behavior
- Reads the model.csv fully into memory: List<String> lines = Files.readAllLines(...)
- Finds which CSV column corresponds to the outlet code (find outletIndex by matching header).
- Builds outLines by replacing the value at outletIndex with updated in-memory model quantities (map from Model objects).
- Writes outLines to a temporary file then moves it to overwrite the original model.csv (atomic update). This approach ensures either the whole file is updated or the original remains untouched if an error occurs while writing.

8) Rollback strategy
- We update in-memory quantities first (but keep backups map of previous values).
- Option B flow in your current code: append to sales.csv first, then persist model.csv, then write receipt.
- If persistModelCsv fails after we already appended to sales.csv, removeSaleFromCsv attempts to delete the appended rows (by reading whole file, filtering out lines starting with SaleID, and rewriting the file). Then we restore in-memory quantities from backups.
- This rollback is “best-effort.” If someone else wrote to sales.csv concurrently or manual edits were made, removal might be imperfect; for a robust production system you'd use file-locking or a transactional database.

9) CSV layout decisions
- Every sale produces one CSV row per item. Columns used are:
  SaleID,EmployeeID,OutletCode,CustomerName,Model,Quantity,UnitPrice,Subtotal,PaymentMethod,Date,Time
- Re-using the SaleID for multi-item sales lets you group multiple lines together when you need to reconstruct a single sale.

10) Why BigDecimal.formatCurrency works the way it does
- It sets scale to 2 decimals with HALF_UP rounding and then:
  - If after stripping trailing zeros the value has no fractional digits, it returns an integer-like string (no decimal point). This produces "845" instead of "845.00" when the value is whole — matching your project's previous formatting style.

11) Concurrency and limitations
- Current code is fine for single-user or single-process usage.
- Race conditions may occur if multiple processes append to sales.csv concurrently or update model.csv at the same time:
  - Append is usually safe in simple setups but not guaranteed atomic with multiple processes. To reduce risk use FileChannel.lock() for exclusive write locks or move to a small DB (SQLite) for transactional guarantees.
  - persistModelCsv uses atomic move which helps avoid partial writes for model.csv.
- removeSaleFromCsv uses a full read and rewrite — OK for small files, but slow for very large CSV files.

12) Testing notes
- Normal single-item sale:
  - sales.csv has header on first line and new row(s) on subsequent lines (no header+row merged).
  - model.csv updated correctly for outlet column.
  - data/sales/sales_YYYY-MM-DD.txt contains the receipt (appended).
- Multi-item sale:
  - sales.csv contains multiple lines with identical SaleID (one per item).
- Simulate model.csv write failure:
  - Make model.csv read-only, run sale; verify code attempts to remove appended rows and restores in-memory stock (check console).
- Test customer name with comma/quotes:
  - Use a name like: John "JJ", Smith — verify the CSV escape wraps the field and duplicates quotes.

