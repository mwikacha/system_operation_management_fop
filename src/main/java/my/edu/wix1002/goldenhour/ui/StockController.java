package my.edu.wix1002.goldenhour.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.ReadOnlyStringWrapper;

import my.edu.wix1002.goldenhour.model.Model;
import my.edu.wix1002.goldenhour.util.DataLoader;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StockController {

    @FXML private TableView<Model> table;
    @FXML private TableColumn<Model, String> colModel;
    @FXML private TableColumn<Model, Double> colPrice;
    @FXML private TableColumn<Model, String> colStock;

    public void initialize() {
        colModel.setCellValueFactory(new PropertyValueFactory<>("modelId"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
    }

    public void loadData() {
        List<Model> models = DataLoader.loadModels();
        ObservableList<Model> obs = FXCollections.observableArrayList(models);

        // create string summary for stock per model
        colStock.setCellValueFactory(cellData -> {
            Model m = cellData.getValue();
            Map<String, Integer> stockMap = m.getStockByOutlet();
            String summary = stockMap.entrySet().stream()
                    .map(e -> e.getKey() + ":" + e.getValue())
                    .collect(Collectors.joining(" | "));
            return new ReadOnlyStringWrapper(summary);
        });

        table.setItems(obs);
    }
}