package my.edu.wix1002.goldenhour.StorageSystem;

import my.edu.wix1002.goldenhour.model.Employee;
import my.edu.wix1002.goldenhour.model.Model;
import my.edu.wix1002.goldenhour.model.Outlet;

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class StoreManager {

    private static final String EMPLOYEE_FILE_PATH = "data/employee.csv";

    //SAVE EMPLOYEES
    public static void saveEmployees(List<Employee> employees) {
        try(CSVWriter writer = new CSVWriter(new FileWriter(EMPLOYEE_FILE_PATH))){

            //write header
            writer.writeNext(new String[]{"EmployeeID", "EmployeeName", "Role", "Password", "OutletCode"});

            //write each employee
            for (Employee e : employees){
                writer.writeNext(new String[]{
                    e.getEmployeeID(),
                    e.getName(),
                    e.getRole(),
                    e.getPassword(),
                    e.getOutletCode()
                });
            }

            System.out.println("Employees saved successfully!");

        } catch (IOException ex){
            System.err.println("Error saving employees: " + ex.getMessage());
        }
    }
    //SAVE MODELS
    public static void saveModels(List<Model> models, List<Outlet> outlets) {
        
        try (CSVWriter writer = new CSVWriter(new FileWriter("data/model.csv"))) {

            //1. Create header : Model, Price, C60, C61, ...
            String[] header = new String[outlets.size() + 2];
            header[0] = "Model";
            header[1] = "Price";

            for(int i = 0; i < outlets.size(); i++) {
                header[i + 2] = outlets.get(i).getOutletCode();
            }

            writer.writeNext(header);

            //2. Write model rows
            for (Model model : models) {
                String[] row = new String[outlets.size() + 2];

                row[0] = model.getModelId();
                row[1] = String.valueOf(model.getPrice());

                for (int i = 0; i < outlets.size(); i++) {
                    String outletCode = outlets.get(i).getOutletCode();
                    row[i + 2] = String.valueOf(
                        model.getStockByOutlet().getOrDefault(outletCode, 0)
                    );
                }

                writer.writeNext(row);
            }

            System.out.println("Model data saved to model.csv");


        } catch (IOException e){
            System.err.println("Error saving model data: " + e.getMessage());
        }
        
    }
    //SAVE ATTENDANCES (clock in)
    public static void appendAttendance(String[] record) {
        try (CSVWriter writer = new CSVWriter(
            new FileWriter("data/attendance.csv", true))) {

                writer.writeNext(record);

            } catch (IOException e) {
                System.err.println("Error appending attendance record: " + e.getMessage());
            }
    }
    //clock out
    public static void updateClockOut(String employeeID, String dateStr, String clockOutTime) {
        String filePath = "data/attendance.csv";
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            for (int i = 1; i < lines.size(); i++){
                String[] parts = lines.get(i).split("," , -1);
                if (parts.length >= 5 && parts[0].equals(employeeID) && parts[1].equals(dateStr)){
                    parts[3] = clockOutTime;
                    lines.set(i, String.join(",", parts));
                    break;
                }
            }
            Files.write(Paths.get(filePath), lines);
        } catch (IOException e) {
            System.err.println("Error updating clock out: " + e.getMessage());
        }
        
    }

    //SAVES SALES RECORD
    public static void appendSalesRecord(String[] saleRecord) {
        try(CSVWriter writer = new CSVWriter(
            new FileWriter("data/sales.csv", true))) {

            writer.writeNext(saleRecord);
            System.out.println("Sale record saved.");

        } catch (IOException e) {
            System.err.println("Error saving sale record: " + e.getMessage());
        }   
    }

    //SAVES STOCK STORAGE

    //1. Save morning stock count
    public static void appendMorningStock(String[] record) {
        String filePath = "data/morning_stock.csv";

        try (CSVWriter writer = new CSVWriter(
            new FileWriter(filePath, true))) {

            //If file is new, write header
            if (Files.size(Paths.get(filePath)) == 0) {
                writer.writeNext(new String[]{
                    "EmployeeID", "OutletCode", "Date", "ModelID", "CountedQty"
                });
            }

            writer.writeNext(record);

        } catch(IOException e){
                System.err.println("Error saving morning stock count: " + e.getMessage());
        }
    }

    //2. Save stock-in transaction
    public static void appendStockIn(String[] record) {
        String filePath = "data/stock_in.csv";

        try (CSVWriter writer = new CSVWriter(
                new FileWriter(filePath, true))) {

            if (Files.size(Paths.get(filePath)) == 0) {
                writer.writeNext(new String[]{
                    "EmployeeID", "OutledCode", "Date", "ModelID", "QuantityIn"
                });
            }

            writer.writeNext(record);

        } catch (IOException e) {
            System.err.println("Error saving stock-in: " + e.getMessage());
        }  
    }

    //3. Save stock-out transaction
    public static void appendStockOut(String[] record) {
        String filePath = "data/stock_out.csv";

        try (CSVWriter writer = new CSVWriter(
                new FileWriter(filePath, true))) {

            if (Files.size(Paths.get(filePath)) == 0) {
                writer.writeNext(new String[]{
                        "EmployeeID", "OutletCode", "Date", "ModelID", "QuantityOut", "Reason" 
                });
            }

            writer.writeNext(record);
            
        } catch (IOException e) {
            System.err.println("Error saving stock-out: " + e.getMessage());
        }
    }
}

