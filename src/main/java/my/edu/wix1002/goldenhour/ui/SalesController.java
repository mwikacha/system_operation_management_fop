package my.edu.wix1002.goldenhour.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import my.edu.wix1002.goldenhour.model.Model;
import my.edu.wix1002.goldenhour.model.Employee;
import my.edu.wix1002.goldenhour.StorageSystem.StoreManager;
import my.edu.wix1002.goldenhour.util.DataLoader;

import java.util.List;

public class SalesController {

    @FXML private TextField customerField;
    @FXML private ComboBox<String> modelCombo;
    @FXML private Label unitPriceLabel;
    @FXML private TextField qtyField;
    @FXML private TextField payMethodField;
    @FXML private Label statusLabel;

    private Employee loggedIn;
    private List<Model> allModels;

    public void initialize() {
        allModels = DataLoader.loadModels();
        if(allModels != null) {
            for (Model m : allModels) {
                modelCombo.getItems().add(m.getModelId());
            }
        }
        
        modelCombo.setOnAction(e -> {
            String id = modelCombo.getValue();
            if (id != null && allModels != null) {
                Model m = allModels.stream().filter(x -> x.getModelId().equalsIgnoreCase(id)).findFirst().orElse(null);
                if (m != null) {
                    unitPriceLabel.setText("RM " + String.format("%.2f", m.getPrice()));
                }
            }
        });
    }

    public void initSession(Employee e) {
        this.loggedIn = e;
    }

    @FXML
    private void onSave() {
        try {
            String customer = customerField.getText().trim();
            String modelId = modelCombo.getValue();
            int qty = Integer.parseInt(qtyField.getText().trim());
            String payment = payMethodField.getText().trim();

            if (customer.isEmpty() || modelId == null || qty <= 0 || payment.isEmpty()) {
                statusLabel.setText("Please fill all fields correctly.");
                return;
            }

            String saleId = "TX" + System.currentTimeMillis();
            Model selected = allModels.stream().filter(x -> x.getModelId().equalsIgnoreCase(modelId)).findFirst().orElse(null);
            
            if (selected == null) {
                 statusLabel.setText("Selected model not found.");
                 return;
            }

            double unitPrice = selected.getPrice();
            double subtotal = unitPrice * qty;
            String[] row = new String[] {
                    saleId,
                    loggedIn.getEmployeeID(),
                    loggedIn.getOutletCode(),
                    customer,
                    modelId,
                    String.valueOf(qty),
                    String.valueOf(unitPrice),
                    String.valueOf(subtotal),
                    payment,
                    java.time.LocalDate.now().toString(),
                    java.time.LocalTime.now().toString()
            };

            StoreManager.appendSalesRecord(row);
            statusLabel.setText("Sale recorded: " + saleId);
        } catch (NumberFormatException ex) {
            statusLabel.setText("Quantity must be a whole number.");
        } catch (Exception ex) {
            statusLabel.setText("Failed to save sale: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}