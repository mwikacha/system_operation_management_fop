package my.edu.wix1002.goldenhour.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import my.edu.wix1002.goldenhour.model.Model;
import my.edu.wix1002.goldenhour.model.Employee;
import my.edu.wix1002.goldenhour.model.Sales;
import my.edu.wix1002.goldenhour.StorageSystem.StoreManager;
import my.edu.wix1002.goldenhour.util.DataLoader;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SalesController {

    // --- Sales entry fields ---
    @FXML private TextField customerField;
    @FXML private ComboBox<String> modelCombo;
    @FXML private Label unitPriceLabel;
    @FXML private TextField qtyField;
    @FXML private TextField payMethodField;
    @FXML private Label statusLabel;

    // --- History UI ---
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<String> sortCombo;
    @FXML private Button filterBtn;
    @FXML private Button clearBtn;

    @FXML private TableView<Sales> historyTable;
    @FXML private TableColumn<Sales, String> colDate;
    @FXML private TableColumn<Sales, String> colTime;
    @FXML private TableColumn<Sales, String> colSaleId;
    @FXML private TableColumn<Sales, String> colCustomer;
    @FXML private TableColumn<Sales, String> colModel;
    @FXML private TableColumn<Sales, Integer> colQty;
    @FXML private TableColumn<Sales, Double> colUnitPrice;
    @FXML private TableColumn<Sales, Double> colSubtotal;

    @FXML private Label totalLabel;

    private Employee loggedIn;
    private List<Model> allModels;
    private ObservableList<Sales> allSales = FXCollections.observableArrayList();

    public void initialize() {
        // models for sale
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

        // setup history table
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        colSaleId.setCellValueFactory(new PropertyValueFactory<>("saleID"));
        colCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        // format currency columns
        Callback<TableColumn<Sales, Double>, TableCell<Sales, Double>> moneyFormatter = col -> new TableCell<>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                } else {
                    setText(String.format("RM %.2f", value));
                }
            }
        };
        colUnitPrice.setCellFactory(moneyFormatter);
        colSubtotal.setCellFactory(moneyFormatter);

        // populate sort options
        sortCombo.getItems().addAll("Date ▲", "Date ▼", "Amount ▲", "Amount ▼", "Customer A-Z", "Customer Z-A");

        // load sales data
        refreshHistory();

        // apply default (show all)
        applyFilterAndSort();
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

            // refresh history to show new record
            refreshHistory();
            applyFilterAndSort();
        } catch (NumberFormatException ex) {
            statusLabel.setText("Quantity must be a whole number.");
        } catch (Exception ex) {
            statusLabel.setText("Failed to save sale: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @FXML
    private void onApplyFilter() {
        applyFilterAndSort();
    }

    @FXML
    private void onClearFilter() {
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        sortCombo.setValue(null);
        applyFilterAndSort();
    }

    private void refreshHistory() {
        List<Sales> loaded = DataLoader.loadSales();
        allSales.setAll(loaded);
    }

    private void applyFilterAndSort() {
        // filter by date range
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();

        List<Sales> filtered = allSales.stream().filter(s -> {
            try {
                LocalDate d = LocalDate.parse(s.getDate());
                boolean afterStart = (start == null) || !d.isBefore(start);
                boolean beforeEnd = (end == null) || !d.isAfter(end);
                return afterStart && beforeEnd;
            } catch (DateTimeParseException ex) {
                return false;
            }
        }).collect(Collectors.toList());

        // sorting
        String sort = sortCombo.getValue();
        if (sort != null) {
            Comparator<Sales> cmp = null;
            switch (sort) {
                case "Date ▲": cmp = Comparator.comparing((Sales s) -> LocalDate.parse(s.getDate())); break;
                case "Date ▼": cmp = Comparator.comparing((Sales s) -> LocalDate.parse(s.getDate())).reversed(); break;
                case "Amount ▲": cmp = Comparator.comparingDouble(Sales::getSubtotal); break;
                case "Amount ▼": cmp = Comparator.comparingDouble(Sales::getSubtotal).reversed(); break;
                case "Customer A-Z": cmp = Comparator.comparing(Sales::getCustomerName, String.CASE_INSENSITIVE_ORDER); break;
                case "Customer Z-A": cmp = Comparator.comparing(Sales::getCustomerName, String.CASE_INSENSITIVE_ORDER).reversed(); break;
            }
            if (cmp != null) filtered = filtered.stream().sorted(cmp).collect(Collectors.toList());
        }

        // show in table
        historyTable.setItems(FXCollections.observableArrayList(filtered));

        // compute total
        double total = filtered.stream().mapToDouble(Sales::getSubtotal).sum();
        totalLabel.setText(String.format("RM %.2f", total));
    }
}