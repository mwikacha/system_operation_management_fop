package my.edu.wix1002.goldenhour;
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

// input & output
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
// used to reprensent time
import java.text.SimpleDateFormat;
import java.util.Date;
// used to insert inputs
import java.util.Scanner;


class Model 
{
        // basic attributes
        String modelCode;
        int price;

        static String[] recordedOutlet;
        int[] plannedStock;
        
        // for one outlet recorded in model.csv, what is the recorded number of the model in each outlet
        
}


class Outlet 
{
        String outletCode;
        String outletName;
}

    
public class SearchInformation {

    //for this class
    private static Scanner scanner = new Scanner(System.in); 

    // automately shows this form of time: Y/M/D
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    

    //###############################################################################################################//

    // To count recorded outlets from model.csv
    private static int countModelOutlets(){

        int totalLines = 0;

            try 
            {
                BufferedReader inputStream = new BufferedReader (new FileReader("data/model.csv"));
                String line = inputStream.readLine();
                String[] title = line.split(",");
                totalLines = title.length -2;
                Model.recordedOutlet = new String[totalLines];
                for (int i = 2; i < title.length; i++)
                {
                    Model.recordedOutlet[i-2] = title[i];
                }
                inputStream.close();
            }
            catch (FileNotFoundException e) 
            {
            System.out.println("The file \"model.csv\" was not found");   
            } 
            catch (IOException e) 
            {
            System.out.println("Error reading from file");
            }
        return totalLines;    
    }
        
    // To count recorded outlets from outlet.csv
    private static int countOutlets() 
    {
        int totalLines = 0;
        try 
        {
            BufferedReader inputStream = new BufferedReader(new FileReader("data/outlet.csv"));
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
    
    // Method to count total models
    private static int countModels() {
        int totalLines = 0;
        try {
            BufferedReader inputStream = new BufferedReader(new FileReader("data/model.csv"));
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
        } 
        catch (FileNotFoundException e) 
        {
            System.out.println("The file \"model.csv\" was not found");
        } 
        catch (IOException e) 
        {
            System.out.println("Error reading from file");
        }
        return totalLines;
    }
    

    //###############################################################################################################//

    
    // Method to read outlet data
    private static Outlet[] readOutlets() 
    {
        int totalLines = countOutlets();
        Outlet[] outlets = new Outlet[totalLines];
        
        try {
            BufferedReader inputStream = new BufferedReader(new FileReader("data/outlet.csv"));
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
        } 
        catch (FileNotFoundException e) 
        {
            System.out.println("The file \"outlet.csv\" was not found");
        } 
        catch (IOException e) 
        {
            System.out.println("Error reading from file");
        }
        
        return outlets;
    }
    
    // Method to read model data
    private static Model[] readModels() {
        int outletCount = countModelOutlets();
        int modelCount = countModels();
        
        // to record the models
        Model[] models = new Model[modelCount];
        
        try {
            BufferedReader inputStream = new BufferedReader(new FileReader("data/model.csv"));
            String line;
            boolean firstLine = true;
            int index = 0;
            
            // Read outlet names from first line
            String[] outletNames = new String[outletCount];
            if (firstLine) 
            {
                // this is the title line
                line = inputStream.readLine();
                String[] title = line.split(",");
                // information 1 : recorded outlets in model.csv
                for (int i = 2; i < title.length; i++) {
                    outletNames[i-2] = title[i];
                }
                firstLine = false;
            }
            
            
            // Read model data
            // Go through the model.csv (without title line)
            // Each line matches a model
            // Each model has its basic attributes listed in class Model
            // recorded all the models listed in the model.csv in Model[] models and return it

            while ((line = inputStream.readLine()) != null) 
            {
                String[] values = line.split(",");
                Model model = new Model();
                model.plannedStock = new int[outletCount];

                model.modelCode = values[0];
                // A very useful method to change String to int
                model.price = Integer.parseInt(values[1]);
                model.recordedOutlet = outletNames;
                
                for (int i = 0; i < outletCount; i++) {
                    model.plannedStock[i] = Integer.parseInt(values[i + 2]);
                }

                // record the model
                models[index] = model;
                index++;
            }
            inputStream.close();

        } 
        catch (FileNotFoundException e) 
        {
            System.out.println("The file \"model.csv\" was not found");
        } 
        catch (IOException e) 
        {
            System.out.println("Error reading from file");
        }
        
        return models;
    }
 
    //###############################################################################################################//

    // We need to connect the outletcode to its name when we search and print the information 
    private static String getOutletName(Outlet[] outlets, String outletCode) 
    {
        for (Outlet outlet : outlets) {
            if (outlet.outletCode.equals(outletCode)) {
                return outlet.outletName;
            }
        }
        return "Unknown Outlet";
    }

    //###############################################################################################################//
   
  
    // 1. Stock Information Search (method 1)
    public static void searchStockInformation() {
        System.out.println("\n=== Search Stock Information ===");
        System.out.print("Enter Model Name: ");

        // user give an input model name
        // trim() to reduce the unexpected spaces/symbols
        String searchTerm = scanner.nextLine().trim();
        
        // read model.csv
        Model[] models = readModels();
        Outlet[] outlets = readOutlets();
        
        System.out.println("\nSearching:");
        System.out.println();
        
        boolean found = false;

        for (Model model : models) {
            if (model.modelCode.equalsIgnoreCase(searchTerm)) 
                {
                found = true;
                System.out.println("\nModel: " + model.modelCode);
                System.out.println("Unit Price: RM" + model.price);
                System.out.println("Stock by outlets:");
                
                // here, compare to the sample outputs, I think it's better to
                // ① split them into different lines, 
                // ② and add the outlet codes
                // Also, I think there is no need to remove the outlets with 0 stock count(if it is then it doesn't matter)

                //Go through all the recorded outlets matched with the searched model
                for (int i = 0; i < model.recordedOutlet.length; i++) 
                {
                    String outletCode = model.recordedOutlet[i];
                    String outletName = getOutletName(outlets, outletCode);
                    System.out.println("  " + outletCode + " (" + outletName + "): " + model.plannedStock[i]);
                }
                
            }
        }
        
        // If this model is not in the list
        if (!searchTerm.isEmpty() && !found) 
        {
            System.out.println("Model \"" + searchTerm + "\" not found.");
        }
    }
    
    //###############################################################################################################//

    // C6002 - Adam bin Abu → Adam bin Abu
    private static String employeeName(String employeeInfo) 
    {
        if (employeeInfo == null) 
        {
            return "";
        }
        
        int index = employeeInfo.indexOf(" - ");
        //if " - " is found
        if (index != -1) 
        {
            return employeeInfo.substring(index + 3);
        }
        //if the name is pure already
        return employeeInfo;
    }

    // 2. Sales Information Search (method 2 - employee)
    public static void searchSalesInformation() {
        System.out.println("\n=== Search Sales Information ===");
        System.out.print("Search keyword: ");
        
        String keyword = scanner.nextLine().trim();
        
        // start to search
        searchInSalesFiles(keyword);
    }
    
    // search in sales text files
    private static void searchInSalesFiles(String searchTerm) {
        System.out.println("\n=== Search Sales Information ===");
        System.out.println("Search keyword: " + searchTerm);
        System.out.println("Searching...");
        System.out.println();

        // ! To read the txt in the sales block, first set sales as a file
        File salesInfo = new File("data/sales");
        
        // get all the txts
        File[] salesFiles = null;

        // Interesting method :o
        // listFiles (FilenameFilter)
        // FilenameFilter: (d,f) -> (condition)
        // means from the d, what f fullfill the condtions is/are returned
        salesFiles = salesInfo.listFiles((directory,filename) -> 
        filename.toLowerCase().endsWith(".txt") && filename.startsWith("sales_"));

        //startsWith and endsWith are also useful
        
        if (salesFiles == null || salesFiles.length == 0) 
        {
            System.out.println("No sales files found in data/sales directory.");
            return;
        }
        
        boolean found = false;
        
        // Go through the selected files:

        for (File salesFile : salesFiles) {
            try (BufferedReader reader = new BufferedReader(new FileReader(salesFile))) 
            {
                String line;
                
                // set all the detailed information as default
                String date = null, time = null, employee = null, customer = null,
                item = null, quantity = null, unitPrice = null, method = null,
                subtotal = null, transactionId = null;
                
                boolean recordStarted = false, recordMatches = false,inItemsSection = false;
                

                while ((line = reader.readLine()) != null) {
                    // form it first
                    line = line.trim();
                    
                    if (line.isEmpty()) 
                    {
                        continue;
                    }
                    
                    // can searched by date, customer, and item

                    //date *

                    if (line.startsWith("Date:")) 
                    {
                        //from index 5 to the end is the date
                        date = line.substring(5).trim();
                        recordStarted = true;
                        //to determine whether the provided keyword is a data
                        //new method - matches("a date form,we use yyyy-mm-dd");
                        //new method - contains(...) check for substring
                        if (searchTerm.matches("\\d{4}-\\d{2}-\\d{2}") && date.contains(searchTerm)) 
                        {
                            recordMatches = true;
                        }
                    } 

                    //time
                    else if (line.startsWith("Time:")) 
                    {
                        time = line.substring(5).trim();
                    } 

                    //employee
                    else if (line.startsWith("Employee:")) 
                    {
                        employee = line.substring(9).trim();
                    } 

                    //customer*
                    else if (line.startsWith("Customer Name:")) 
                    {
                        customer = line.substring(14).trim();

                        //to determine whether the provided keyword is a customer's name
                        if (customer.toLowerCase().contains(searchTerm.toLowerCase())) {
                            recordMatches = true;
                        }
                    } 

                    //items*
                    else if (line.equals("Item(s) Purchased:")) 
                    {
                        inItemsSection = true;
                    }
                    //to determine whether the provided keyword is a model's name
                    else if (inItemsSection && line.startsWith("Enter Model:")) {
                        item = line.substring(12).trim();
                        if (item.contains(searchTerm)) {
                            recordMatches = true;
                        }
                    }

                    //quantity
                    else if (inItemsSection && line.startsWith("Enter Quantity:")) {
                        quantity = line.substring(15).trim();
                    }

                    // unit price
                    else if (inItemsSection && line.startsWith("Unit Price:")) {
                        unitPrice = line.substring(11).trim();
                    }
                    
                    //pay method 
                    else if (line.startsWith("Transaction Method:")) {
                        method = line.substring(19).trim();
                        inItemsSection = false;
                    }

                    //subtotal
                    else if (line.startsWith("Subtotal:")) {
                        subtotal = line.substring(9).trim();
                    }

                    // pay ID
                    else if (line.startsWith("Transaction ID:")) {
                        transactionId = line.substring(15).trim();
                    }

                    // end(one sale recording list)
                    else if (line.startsWith("-----------------------------------------------------")) 
                    {
                        // The model is found!
                        // matches the sample output
                        if (recordStarted && recordMatches) 
                        {
                            System.out.println("Sales Record Found:");
                            System.out.println("Date: " + date + " Time: " + time);
                            System.out.println("Customer: " + customer);
                            System.out.println("Item(s): " + item + " Quantity: " + quantity);
                            System.out.println("Total: " + subtotal);
                            System.out.println("Transaction Method: " + method);
                            System.out.println("Employee: " + employeeName(employee));
                            System.out.println("Status: Transaction verified.");
                            System.out.println();
                            found = true;
                        }
                        
                        // set the information as default again
                        date = time = employee = customer = item = quantity = unitPrice = 
                        method = subtotal = transactionId = null;

                        recordStarted = recordMatches = inItemsSection = false;
                    }
                    
                }
                
                // final recording piece(without ---)
                if (recordStarted && recordMatches) 
                {
                    System.out.println("Sales Record Found:");
                    System.out.println("Date: " + date + " Time: " + time);
                    System.out.println("Customer: " + customer);
                    System.out.println("Item(s): " + item + " Quantity: " + quantity);
                    System.out.println("Total: " + subtotal);
                    System.out.println("Transaction Method: " + method);
                    System.out.println("Employee: " + employeeName(employee));
                    System.out.println("Status: Transaction verified.");
                    System.out.println();
                    found = true;
                }
                
            } 
            catch (IOException e) 
            {
                System.out.println("Error reading file: " + salesFile.getName());
            }
        }
        
        if (!found) {
            System.out.println("No sales records found for: " + searchTerm);
        }
    }
   

    // start from here
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
                        System.out.println("Exiting Search Information System :)");
                        return;
                    default:
                        System.out.println("Invalid choice. Please enter 1, 2, or 3.");
                }
            } 
            catch (Exception e) 
            {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }
}