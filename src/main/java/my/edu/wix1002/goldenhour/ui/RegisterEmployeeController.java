package my.edu.wix1002.goldenhour.ui;

import javafx.fxml. FXML;
import javafx.scene.control.*;
import javafx.scene.layout. VBox;
import javafx. stage.Stage;

import my.edu.wix1002.goldenhour.model.Employee;
import my.edu.wix1002.goldenhour.util.DataLoader;
import my.edu.wix1002.goldenhour.StorageSystem.StoreManager;

import java.util.List;

public class RegisterEmployeeController {
    @FXML private TextField nameField;
    @FXML private TextField employeeIdField;
    @FXML private PasswordField passwordField;
    @FXML private RadioButton fullTimeRadio;
    @FXML private RadioButton partTimeRadio;
    @FXML private Label messageLabel;
    @FXML private Label outletCodeHint;
    @FXML private VBox successBox;
    @FXML private Label successLabel;

    private Employee loggedInManager;
    private String outletCode;

    public void initSession(Employee manager) {
        this.loggedInManager = manager;
        this.outletCode = manager.getEmployeeID().substring(0, 3);
        outletCodeHint.setText("(Must start with " + outletCode + ")");
    }

    @FXML
    private void onRegister() {
        //reset messages
        messageLabel.setText("");
        messageLabel.setStyle("-fx-text-fill:red;");
        successBox.setVisible(false);
        successBox.setManaged(false);

        //get input values
        String name = nameField.getText().trim();
        String employedId = employeeIdField.getText().trim();
        String password = passwordField.getText();

        if (name.isEmpty() || employedId.isEmpty() || password.isEmpty()) {
            messageLabel.setText("All fields are required.");
            return;
        }

        if (!employedId.startsWith(outletCode)) {
            messageLabel.setText("Error: Employee ID must start with " + outletCode + ".");
            return;
        }

        List<Employee> employees = DataLoader.loadEmployees();
        boolean isDuplicate = employees.stream()
            .anyMatch(e -> e.getEmployeeID().equals(employedId));

        if (isDuplicate) {
            messageLabel.setText("Error : Employee ID already exists.");
            return;
        }

        String role = fullTimeRadio.isSelected() ? "Full-Time" : "Part-Time";

        try {
            Employee newEmployee = new Employee(employedId, name, role, password, outletCode);
            employees.add(newEmployee);

            //save to csv 
            StoreManager.saveEmployees(employees);

            successBox.setVisible(true);
            successBox.setManaged(true);
            successLabel.setText("Employee successfully registered!\nID: " + employedId + " | Name: " + name + " | Role: " + role);

            clearForm();
        } catch (Exception e) {
            messageLabel.setText("Error saving employee: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onClear() {
        clearForm();
        messageLabel.setText("");
        successBox.setVisible(false);
        successBox.setManaged(false);
    }

    @FXML
    private void onClose() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    private void clearForm() {
        nameField.clear();
        employeeIdField.clear();
        passwordField.clear();
        fullTimeRadio.setSelected(true);
    }
}
