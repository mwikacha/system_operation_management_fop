package my.edu.wix1002.goldenhour; 

import my.edu.wix1002.goldenhour.model.Employee;
import my.edu.wix1002.goldenhour.model.Model;
import my.edu.wix1002.goldenhour.model.Outlet;
import my.edu.wix1002.goldenhour.util.DataLoader;
import my.edu.wix1002.goldenhour.StorageSystem.StoreManager;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.IOException;
import java.util.List;
import com.opencsv.exceptions.CsvValidationException;
import java.util.Scanner;

// import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws CsvValidationException{
        System.out.println("== Store Operation Management System ==");
        System.out.println("== Employee Login ==");

        // Load all initial data from CSV files (Data Load State)
        List<Employee> allEmployees = DataLoader.loadEmployees();
        List<Outlet> allOutlets = DataLoader.loadOutlets();
        List<Model> allModels = DataLoader.loadModels();

        // System.out.println("Number of employees loaded: " + allEmployees.size());
        // for (Employee emp : allEmployees) {
        //     System.out.println("Loaded: " + emp.getEmployeeID() + " - " + emp.getPassword());
        // }

        // if (!allEmployees.isEmpty()) {
        //     System.out.println("First employee loaded: " + allEmployees.get(0));
        // } else {
        //     System.out.println("No employees loaded.");
        // }

        // if (!allOutlets.isEmpty()) {
        //     System.out.println("First outlet loaded: " + allOutlets.get(0));
        // } else {
        //     System.out.println("No outlets loaded. Check CSV file path/content.");
        // }

        // if (!allModels.isEmpty()) {
        //     System.out.println("First model loaded: " + allModels.get(0));
        // } else {
        //     System.out.println("No models loaded. Check CSV file path/content.");
        // }
        
        // The main menu/login loop will start here 
        Scanner input = new Scanner(System.in);
        boolean isLoggedIn = false;
        boolean running = true;
        Employee loggedInEmployee = null;
        

        while (!isLoggedIn) {
                System.out.print("Enter Employee ID (or 'exit' to quit): ");
                String employeeID = input.nextLine();
                
                if (employeeID.equalsIgnoreCase("exit")) {
                    running = false;
                    break;
                }

                System.out.print("Enter Password: ");
                String password = input.nextLine();

                // Find employee with matching ID and password
                for (Employee employee : allEmployees) {
                    if (employee.getEmployeeID().equals(employeeID) && 
                        employee.getPassword().equals(password)) {
                        isLoggedIn = true;
                        loggedInEmployee = employee;
                        break;
                    }
                }

                if (isLoggedIn) {
                    String outletCode = loggedInEmployee.getEmployeeID().substring(0, 3);
                    System.out.println("\nLogin Successful!");
                    System.out.println("Welcome, " + loggedInEmployee.getName() + " (" + outletCode + ")");

                    if (loggedInEmployee.getRole().equals("Manager")) {
                        showManagerMenu(input, allEmployees, outletCode);
                    } else {
                        showEmployeeMenu(input, loggedInEmployee, allModels, allOutlets);
                    }
                } else {
                    System.out.println("\nLogin Failed: Invalid User ID or Password. ");
                }
        }

        System.out.println("Thank you for using the Store Operation Management System!");
        input.close();
    }

    private static void showManagerMenu(Scanner input, List<Employee> allEmployees, String outletCode) {
        System.out.println("\n=== Manager Menu ===");
        System.out.println("1. Register New Employee");
        System.out.println("2. Exit");
        System.out.print("Enter choice: ");
        
        String choice = input.nextLine();
        if (choice.equals("1")) {
            registerNewEmployee(input, allEmployees, outletCode);
        } else if (choice.equals("2")) {
            System.out.println("Exiting Manager Menu...");
        } else {
            System.out.println("Invalid choice! Please try again.");
        }
    } //add loop here if need to key in multiple employees

    private static void registerNewEmployee(Scanner input, List<Employee> allEmployees, String outletCode) {
        System.out.println("\n=== Register New Employee ===");
        
        // Get employee details
        System.out.print("Enter Employee Name: ");
        String name = input.nextLine();
        
        // Get and validate employee ID
        String validEmployeeId = null;
        while (true) {
            System.out.print("Enter Employee ID: ");
            String tempId = input.nextLine(); 
            
            // Check if ID already exists
            final String idToCheck = tempId;
            boolean isDuplicate = allEmployees.stream()
                .anyMatch(emp -> emp.getEmployeeID().equals(idToCheck));
            
            if (isDuplicate) {
                System.out.println("Error: Employee ID already exists!");
                continue;
            }
            
            // Validate ID format (should start with outlet code)
            if (!tempId.startsWith(outletCode)) {
                System.out.println("Error: Employee ID must start with " + outletCode);
                continue;
            }
            validEmployeeId = tempId;
            break;
        }

        System.out.print("Set Password: ");
        String password = input.nextLine();
        
        // Get and validate role
        String role;
        while (true) {
            System.out.print("Set Role (P=Part-time/F=Full-time): ");
            role = input.nextLine().trim().toUpperCase();
            if (role.equalsIgnoreCase("P") || role.equalsIgnoreCase("Part-time")) {
                role = "Part-time";
                break;
            } else if (role.equalsIgnoreCase("F") || role.equalsIgnoreCase("Full-time")) {
                role = "Full-time";
                break;
            }
            System.out.println("Error: Invalid role! Please enter 'Part-time' or 'Full-time'");
        } //adding more flexible input

        // create new employee and save to CSV using StoreManager
        try {
            // Create new employee and add to the in-memory list
            Employee newEmployee = new Employee(validEmployeeId, name, role, password, outletCode);
            allEmployees.add(newEmployee);

            // using StoreManager which handles writing the full CSV
            StoreManager.saveEmployees(allEmployees);

            System.out.println("\nEmployee successfully registered!");

        } catch (Exception e) {
            System.err.println("Error saving employee data: " + e.getMessage());
        }
    }
    private static void showEmployeeMenu(Scanner input, Employee loggedInEmployee, List<Model> allModels, List<Outlet> allOutlets) {
        boolean running = true;
        while (running) {
            System.out.println("\n=== Employee Menu ===");
            System.out.println("1. Log Attendance");
            System.out.println("2. Stock Management");
            System.out.println("3. Record New Sale");
            System.out.println("4. Search Stock Information");
            System.out.println("5. Edit Stock Information");
            System.out.println("6. Edit Sales Information");
            System.out.println("7. Exit");
            System.out.print("Enter choice: ");

            String choice = input.nextLine();
            switch (choice) {
                case "1":
                    AttendanceSystem.showAttendanceMenu(input, loggedInEmployee, allOutlets);
                    break;
                case "2":
                    StockManagement.setEmployeeName(loggedInEmployee.getName());
                    StockManagement.main(new String[]{});
                    break;
                case "3":
                    salesSystem.recordNewSale(input, loggedInEmployee, allModels);
                    break;
                case "4":
                    System.out.println("Search Stock Information - Coming soon!");
                    running = false;
                    break;
                case "5":
                    System.out.println("Edit Stock Information - Coming soon!");
                    running = false;
                    break;
                case "6":
                    System.out.println("Edit Sales Information - Coming soon!");
                    running = false;
                    break;
                case "7":
                    running = false;
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }
}