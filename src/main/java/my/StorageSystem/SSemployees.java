package my.StorageSystem;

import my.edu.wix1002.goldenhour.model.Employee;
import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class SSemployees {

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
}
