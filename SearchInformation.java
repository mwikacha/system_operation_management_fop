/*
Search Information (1 mark)
This feature allows employees to quickly retrieve information related to stocks and sales.
1. Stock Information
Employees can search by model name to view current stock availability 
in-store and across other outlets.
2. Sales Information
To verify the authenticity of transactions, employees can search sales 
records by date, customer name, or model name.
 */



// SearchInformation.java
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
//time
import java.text.SimpleDateFormat;
import java.util.Date;
//inputs
import java.util.Scanner;
//read zip file?
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;

public class SearchInformation {

    // for users to enter value
    static Scanner scanner = new Scanner(System.in); 
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    // Inner classes to match your existing structure
    static class Model {
        String modelCode;
        int price;
        String[] recordedOutlet;
        int[] plannedStock;
        
        Model(int outletCount) {
            plannedStock = new int[outletCount];
        }
    }
    
    // title of outlet.csv
    static class Outlet {
        String outletCode;
        String outletName;
    }
    
    // read outlet.csv and return number of outlets recorded in the file
    public static int countOutlets() {
        int totalLines = 0;
        try 
        {
            BufferedReader inputStream = new BufferedReader(new FileReader("outlet.csv"));
            String line;
            // skip the title line
            boolean firstLine = true;
            while ((line = inputStream.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                //count the total lines
                totalLines++;
            }
            inputStream.close();
        } 
        catch (FileNotFoundException e) 
        {
            System.out.println("The file \"outlet.csv\" was not found");
        } 
        catch (IOException e) 
        {
            System.out.println("Error reading from file");
        }
        return totalLines;
    }
    
    // Method to count model outlets from model.csv
    public static int countModelOutlets() {
        int totalLines = 0;
        try 
        {
            BufferedReader inputStream = new BufferedReader(new FileReader("model.csv"));
            String line = inputStream.readLine();
            if (line != null) {
                String[] title = line.split(",");
                totalLines = title.length - 2;
            }
            inputStream.close();
        } catch (FileNotFoundException e) {
            System.out.println("The file \"model.csv\" was not found");
        } catch (IOException e) {
            System.out.println("Error reading from file");
        }
        return totalLines;
    }
    
    // Method to read outlet data
    private static Outlet[] readOutlets() {
        int totalLines = countOutlets();
        Outlet[] outlets = new Outlet[totalLines];
        
        try {
            BufferedReader inputStream = new BufferedReader(new FileReader("outlet.csv"));
            String line;
            boolean firstLine = true;
            int index = 0;
            
            while ((line = inputStream.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                
                String[] values = line.split(",");
                Outlet outlet = new Outlet();
                outlet.outletCode = values[0];
                outlet.outletName = values[1];
                outlets[index] = outlet;
                index++;
            }
            inputStream.close();
        } catch (FileNotFoundException e) {
            System.out.println("The file \"outlet.csv\" was not found");
        } catch (IOException e) {
            System.out.println("Error reading from file");
        }
        
        return outlets;
    }
    
    // Method to read model data
    private static Model[] readModels() {
        int outletCount = countModelOutlets();
        int modelCount = countModels();
        Model[] models = new Model[modelCount];
        
        try {
            BufferedReader inputStream = new BufferedReader(new FileReader("model.csv"));
            String line;
            boolean firstLine = true;
            int index = 0;
            
            // Read outlet names from first line
            String[] outletNames = null;
            if (firstLine) {
                line = inputStream.readLine();
                String[] title = line.split(",");
                outletNames = new String[outletCount];
                for (int i = 2; i < title.length; i++) {
                    outletNames[i-2] = title[i];
                }
                firstLine = false;
            }
            
            // Read model data
            while ((line = inputStream.readLine()) != null) {
                String[] values = line.split(",");
                Model model = new Model(outletCount);
                model.modelCode = values[0];
                model.price = Integer.parseInt(values[1]);
                model.recordedOutlet = outletNames;
                
                for (int i = 0; i < outletCount; i++) {
                    model.plannedStock[i] = Integer.parseInt(values[i + 2]);
                }
                
                models[index] = model;
                index++;
            }
            inputStream.close();
        } catch (FileNotFoundException e) {
            System.out.println("The file \"model.csv\" was not found");
        } catch (IOException e) {
            System.out.println("Error reading from file");
        }
        
        return models;
    }
    
    // Method to count total models
    private static int countModels() {
        int totalLines = 0;
        try {
            BufferedReader inputStream = new BufferedReader(new FileReader("model.csv"));
            String line;
            boolean firstLine = true;
            while ((line = inputStream.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                totalLines++;
            }
            inputStream.close();
        } catch (FileNotFoundException e) {
            System.out.println("The file \"model.csv\" was not found");
        } catch (IOException e) {
            System.out.println("Error reading from file");
        }
        return totalLines;
    }
    
    // 1. Stock Information Search
    public static void searchStockInformation() {
        System.out.println("\n=== Search Stock Information ===");
        System.out.print("Enter Model Name: ");
        String searchTerm = scanner.nextLine().trim();
        
        Model[] models = readModels();
        Outlet[] outlets = readOutlets();
        
        System.out.println("\nSearching:");
        System.out.println("===================");
        
        boolean found = false;
        for (Model model : models) {
            if (model.modelCode.equalsIgnoreCase(searchTerm)) {
                found = true;
                System.out.println("\nModel: " + model.modelCode);
                System.out.println("Unit Price: RM" + model.price);
                System.out.println("Stock by outlets:");
                
                // here, compare to the sample outputs, I split them into different lines, and add the outlet codes
                for (int i = 0; i < model.recordedOutlet.length; i++) {
                    String outletCode = model.recordedOutlet[i];
                    String outletName = getOutletName(outlets, outletCode);
                    System.out.println("  " + outletCode + " (" + outletName + "): " + model.plannedStock[i]);
                }
                
                // If specific search, break after finding
                if (!searchTerm.isEmpty()) {
                    break;
                }
            }
        }
        
        if (!searchTerm.isEmpty() && !found) {
            System.out.println("Model \"" + searchTerm + "\" not found.");
        }
    }
    
    // Helper method to get outlet name from code
    private static String getOutletName(Outlet[] outlets, String outletCode) {
        for (Outlet outlet : outlets) {
            if (outlet.outletCode.equals(outletCode)) {
                return outlet.outletName;
            }
        }
        return "Unknown Outlet";
    }
    
    // 2. Sales Information Search
    public static void searchSalesInformation() {
    System.out.println("\n=== Search Sales Information ===");
    System.out.print("Search keyword: ");
    
    String keyword = scanner.nextLine().trim();
    
    // start to search
    searchInZipFiles(keyword);
    }
    
    // Method to search in ZIP files
    private static void searchInZipFiles(String searchTerm) {
    System.out.println("\n=== Search Sales Information ===");
    System.out.println("Search keyword: " + searchTerm);
    System.out.println("Searching...");
    
    try {
        String zipFileName = "sales.zip";
        
        // === 纯手动遍历：用ZipInputStream ===
        FileInputStream fis = new FileInputStream(zipFileName);
        ZipInputStream zis = new ZipInputStream(fis);
        
        boolean found = false;
        
        // 手动循环获取每个条目
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            // === 从这里开始，解析逻辑完全不变 ===
            if (entry.getName().endsWith(".txt")) {
                // 读取文件内容
                BufferedReader reader = new BufferedReader(new InputStreamReader(zis));
                
                String line;
                // 存储一条记录的数据 - 根据实际格式
                String date = null, time = null, employee = null, customer = null;
                String item = null, quantity = null, unitPrice = null, method = null;
                String subtotal = null, transactionId = null;
                
                boolean recordStarted = false;
                boolean recordMatches = false;
                boolean inItemsSection = false;
                
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    
                    if (line.isEmpty()) continue;
                    
                    // === 根据实际格式解析（按你的格式）===
                    if (line.startsWith("Date:")) {
                        date = line.substring(5).trim();
                        recordStarted = true;
                        if (searchTerm.matches("\\d{4}-\\d{2}-\\d{2}") && date.contains(searchTerm)) {
                            recordMatches = true;
                        }
                    } 
                    else if (line.startsWith("Time:")) {
                        time = line.substring(5).trim();
                    } 
                    else if (line.startsWith("Employee:")) {
                        employee = line.substring(9).trim();
                    } 
                    else if (line.startsWith("Customer Name:")) {
                        customer = line.substring(14).trim();
                        if (customer.toLowerCase().contains(searchTerm.toLowerCase())) {
                            recordMatches = true;
                        }
                    } 
                    else if (line.equals("Item(s) Purchased:")) {
                        inItemsSection = true;
                    }
                    else if (inItemsSection && line.startsWith("Enter Model:")) {
                        item = line.substring(12).trim();
                        if (item.contains(searchTerm)) {
                            recordMatches = true;
                        }
                    }
                    else if (inItemsSection && line.startsWith("Enter Quantity:")) {
                        quantity = line.substring(15).trim();
                    }
                    else if (inItemsSection && line.startsWith("Unit Price:")) {
                        unitPrice = line.substring(11).trim();
                    }
                    else if (line.startsWith("Transaction Method:")) {
                        method = line.substring(19).trim();
                        inItemsSection = false;
                    }
                    else if (line.startsWith("Subtotal:")) {
                        subtotal = line.substring(9).trim();
                    }
                    else if (line.startsWith("Transaction ID:")) {
                        transactionId = line.substring(15).trim();
                    }
                    else if (line.startsWith("-----------------------------------------------------")) {
                        // 一条记录结束
                        if (recordStarted && recordMatches) {
                            // === 输出实际数据 ===
                            System.out.println("Sales Record Found:");
                            System.out.println("Date: " + date + " Time: " + convertTimeFormat(time));
                            System.out.println("Customer: " + customer);
                            System.out.println("Item(s): " + item + " Quantity: " + quantity);
                            System.out.println("Total: " + subtotal);
                            System.out.println("Transaction Method: " + method);
                            System.out.println("Employee: " + extractEmployeeName(employee));
                            System.out.println("Status: Transaction verified.");
                            System.out.println();
                            found = true;
                        }
                        
                        // 重置变量
                        date = time = employee = customer = item = quantity = unitPrice = null;
                        method = subtotal = transactionId = null;
                        recordStarted = false;
                        recordMatches = false;
                        inItemsSection = false;
                    }
                    
                    // 全文搜索
                    if (!recordMatches && line.contains(searchTerm)) {
                        recordMatches = true;
                    }
                }
                
                // 处理文件末尾的最后一条记录
                if (recordStarted && recordMatches) {
                    System.out.println("Sales Record Found:");
                    System.out.println("Date: " + date + " Time: " + convertTimeFormat(time));
                    System.out.println("Customer: " + customer);
                    System.out.println("Item(s): " + item + " Quantity: " + quantity);
                    System.out.println("Total: " + subtotal);
                    System.out.println("Transaction Method: " + method);
                    System.out.println("Employee: " + extractEmployeeName(employee));
                    System.out.println("Status: Transaction verified.");
                    System.out.println();
                    found = true;
                }
            }
            
            zis.closeEntry();
        }
        
        zis.close();
        fis.close();
        
        if (!found) {
            System.out.println("No sales records found for: " + searchTerm);
        }
        
    } 
    catch (Exception e) 
    {
        System.out.println("Error: " + e.getMessage());
    }
    }
   

    // 时间格式转换：10:13 AM → 10:13 a.m.
    private static String convertTimeFormat(String time) {
        if (time == null) return "";
        
        // 如果已经是 a.m./p.m. 格式，直接返回
        if (time.contains("a.m.") || time.contains("p.m.")) {
            return time;
        }
        
        // 转换 AM/PM 为 a.m./p.m.
        if (time.toUpperCase().endsWith(" AM")) {
            return time.substring(0, time.length() - 3) + " a.m.";
        } else if (time.toUpperCase().endsWith(" PM")) {
            return time.substring(0, time.length() - 3) + " p.m.";
        }
        
        return time;
    }

    // 提取员工姓名：C6002 - Adam bin Abu → Adam bin Abu
    private static String extractEmployeeName(String employeeStr) {
        if (employeeStr == null) return "";
        
        int dashIndex = employeeStr.indexOf(" - ");
        if (dashIndex != -1) {
            return employeeStr.substring(dashIndex + 3);
        }
        
        return employeeStr;
    }
    
    // Main menu for SearchInformation
    public static void main(String[] args) {
        System.out.println("=== Search Information System ===");
        
        while (true) {
            System.out.println("\nSelect search option:");
            System.out.println("1. Stock Information");
            System.out.println("2. Sales Information");
            System.out.println("3. Exit");
            System.out.print("Enter your choice (1-3): ");
            
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                
                switch (choice) {
                    case 1:
                        searchStockInformation();
                        break;
                    case 2:
                        searchSalesInformation();
                        break;
                    case 3:
                        System.out.println("Exiting Search Information System. Goodbye!");
                        return;
                    default:
                        System.out.println("Invalid choice. Please enter 1, 2, or 3.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }
}