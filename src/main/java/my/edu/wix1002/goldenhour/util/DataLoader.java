package my.edu.wix1002.goldenhour.util;

import my.edu.wix1002.goldenhour.model.Employee;
import my.edu.wix1002.goldenhour.model.Model;
import my.edu.wix1002.goldenhour.model.Outlet;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataLoader {

    public static final String EMPLOYEE_FILE_PATH = "C:/Users/mwikacha/Desktop/fop-assignment1/goldenhour-system/data/employee.csv"; 
    private static final String OUTLET_FILE_PATH = "C:/Users/mwikacha/Desktop/fop-assignment1/goldenhour-system/data/outlet.csv";
    public static final String MODEL_FILE_PATH = "C:/Users/mwikacha/Desktop/fop-assignment1/goldenhour-system/data/model.csv";

    public static List<Employee> loadEmployees() {
        List<Employee> employeeList = new ArrayList<>();
        // Use try-with-resources to ensure the file reader is closed properly
        try (CSVReader reader = new CSVReader(new FileReader(EMPLOYEE_FILE_PATH))) {

            // 1. Skip the header line
            reader.readNext(); 

            // 2. Read all remaining lines
            String[] nextRecord;
            while ((nextRecord = reader.readNext()) != null) {
                // nextRecord is an array of strings representing one row in the CSV

                // Basic check to ensure the row has enough columns
                if (nextRecord.length >= 4) {
                    Employee employee = new Employee(
                        nextRecord[0],  // EmployeeID
                        nextRecord[1],  // EmployeeName
                        nextRecord[2],  // Role
                        nextRecord[3],  // Password
                        nextRecord[0].substring(0, 3)          // Default outlet code
                    );
                    employeeList.add(employee);
                }
            }
            // System.out.println("Successfully loaded " + employeeList.size() + " employees.");

        } catch (IOException e) {
            // Handle file not found or reading errors (Error Handling)
            System.err.println("Error loading employee data: " + e.getMessage());
        } catch (Exception e) {
            // Handle other unexpected errors
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
        return employeeList;
    }

    public static List<Outlet> loadOutlets() throws CsvValidationException {
        List<Outlet> outletList = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(OUTLET_FILE_PATH))) {
            // Skip header
            reader.readNext(); 
            
            String[] nextRecord;
            while ((nextRecord = reader.readNext()) != null) {
                if (nextRecord.length >= 2) {
                    Outlet outlet = new Outlet(
                        nextRecord[0],  // OutletCode
                        nextRecord[1]   // OutletName
                    );
                    outletList.add(outlet);
                }
            }
            // System.out.println("Successfully loaded " + outletList.size() + " outlets.");
        } catch (IOException e) {
            System.err.println("Error loading outlet data: " + e.getMessage());
        }
        return outletList;
    }

    public static List<Model> loadModels() {
        List<Model> modelList = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(MODEL_FILE_PATH))) {
            // Read header to get outlet codes
            String[] header = reader.readNext();
            List<String> outletCodes = Arrays.asList(header).subList(2, header.length);

            String[] nextRecord;
            while ((nextRecord = reader.readNext()) != null) {
                String modelId = nextRecord[0];
                double price = Double.parseDouble(nextRecord[1]);
                Model model = new Model(modelId, price);

                // Add stock quantities for each outlet
                for (int i = 0; i < outletCodes.size(); i++) {
                    int stock = Integer.parseInt(nextRecord[i + 2]);
                    model.addStock(outletCodes.get(i), stock);
                }
                modelList.add(model);
            }
            // System.out.println("Successfully loaded " + modelList.size() + " models.");
        } catch (IOException | CsvValidationException e) {
            System.err.println("Error loading model data: " + e.getMessage());
        }
        return modelList;
    }
}
