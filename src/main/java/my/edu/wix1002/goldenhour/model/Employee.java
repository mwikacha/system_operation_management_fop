package my.edu.wix1002.goldenhour.model;

// This class represents an Employee object and applies the OOP principle of Encapsulation.
public class Employee {
    // 1. Attributes (Fields) - Declared as private
    private String employeeID;  // Must be unique
    private String name;
    private String role;        // e.g., Manager, Full-time, Part-time
    private String password;
    private String outletCode;  // Required for stock and attendance tracking

    // 2. Constructor for CSV Loading (4 parameters)
    // This assumes the input CSV only has ID, Name, Role, and Password,
    // and we hardcode the OutletCode to "C60" (Kuala Lumpur City Centre) 
    public Employee(String employeeID, String name, String role, String password,String outletCode) { 
        this.employeeID = employeeID;
        this.name = name;
        this.role = role;
        this.password = password;
        // this.outletCode = "C60"; // Hardcoded to KLCC outlet as a temporary fix
    }

    // 3. Getters (Accessors) - Public methods to READ the private data
    public String getEmployeeID() {
        return employeeID;
    }

    public String getName() {
        return name;
    }
    
    public String getRole() {
        return role;
    }

    public String getPassword() {
        return password; 
    }
    
    public String getOutletCode() {
        return outletCode;
    }

    // 4. Utility Method
    // Method to check if the employee has Manager privileges (for registration/metrics)
    public boolean isManager() {
        return "Manager".equalsIgnoreCase(role);
    }
    
    // 5. toString() for easy printing/debugging
    @Override
    public String toString() {
        return "ID: " + employeeID + ", Name: " + name + ", Role: " + role + ", Outlet: " + outletCode;
    }

    // 6. Setters (Mutators) - Add these later when you implement the "Edit Information" feature
    // public void setPassword(String newPassword) {
    //     this.password = newPassword;
    // }
}