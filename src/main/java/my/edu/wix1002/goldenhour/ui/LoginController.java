package my.edu.wix1002.goldenhour.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import my.edu.wix1002.goldenhour.model.Employee;
import my.edu.wix1002.goldenhour.util.DataLoader;

import java.util.List;

public class LoginController {

    @FXML private TextField employeeIdField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    @FXML
    private void onLogin() {
        String id = employeeIdField.getText().trim();
        String pw = passwordField.getText();

        if (id.isEmpty() || pw.isEmpty()) {
            messageLabel.setText("Please enter both fields.");
            return;
        }

        List<Employee> employees = DataLoader.loadEmployees();
        Employee matched = employees.stream()
                .filter(e -> e.getEmployeeID().equals(id) && e.getPassword().equals(pw))
                .findFirst().orElse(null);

        if (matched == null) {
            messageLabel.setText("Invalid credentials.");
            return;
        }

        // Open dashboard and pass the logged-in employee
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
            Scene scene = new Scene(loader.load());
            DashboardController controller = loader.getController();
            controller.initSession(matched);

            Stage stage = (Stage) employeeIdField.getScene().getWindow();
            stage.setTitle("Dashboard - " + matched.getName());
            stage.setScene(scene);
        } catch (Exception ex) {
            messageLabel.setText("Failed to open dashboard: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @FXML
    private void onExit() {
        Stage stage = (Stage) employeeIdField.getScene().getWindow();
        stage.close();
    }
}