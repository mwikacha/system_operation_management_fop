package my.edu.wix1002.goldenhour;

import my.edu.wix1002.goldenhour.model.Model;
import my.edu.wix1002.goldenhour.model.Outlet;
import my.edu.wix1002.goldenhour.AutoEmail.AutoEmail;
import my.edu.wix1002.goldenhour.StorageSystem.StoreManager;
import my.edu.wix1002.goldenhour.model.Sales;
import my.edu.wix1002.goldenhour.util.DataLoader;

import java.util.List;
import java.util.Scanner;

public class EditInformation {
    private final Scanner scanner;
    private final List<Model> allWatches;
    private final List<Outlet> allOutlets;

    //need to be logged in employee to edit
    private final String LoggedInEmployeeID = "EMP001"; //for testing

    public EditInformation(List<Model> allWatches, List<Outlet> allOutlets) {
        this.scanner = new Scanner(System.in);
        this.allWatches = allWatches;
        this.allOutlets = allOutlets;
    }

    public void editMenu() {
        editStockInformation();
    }

    private static final String DEFAULT_OUTLET_CODE = "C60";

    //EDIT STOCK INFORMATION
    public void editStockInformation() {
        System.out.println("=== Edit Stock Information ===");
        System.out.print("Enter Model Name: ");
        String modelId = scanner.nextLine();
        String outletCode = DEFAULT_OUTLET_CODE; //using default outlet code

        Model modelFound = allWatches.stream()
                .filter(m -> m.getModelId().equalsIgnoreCase(modelId))
                .findFirst()
                .orElse(null);

        if (modelFound == null) {
            System.out.print("Error: Model " + modelId + " not found.");
            return;
        }
        if (!modelFound.getStockByOutlet().containsKey(outletCode)) {
             System.out.println("Error: Outlet code '" + outletCode + "' not found. Cannot proceed.");
             return;
        }
    

        int currentStock = modelFound.getStockByOutlet().get(outletCode);
        System.out.println("Current stock (in outlet " + outletCode + "): " + currentStock);

        //get new stock
        int newStock = -1;
        while (newStock < 0) {
            System.out.print("Enter new stock quantity: ");
            try {
                newStock = Integer.parseInt(scanner.nextLine().trim());
                if (newStock < 0) {
                    System.out.println("Stock cannot be negative. Please enter a valid quantity.");
                    newStock = -1;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                newStock = -1;
            }
        }

        //Audit Log
        modelFound.getStockByOutlet().put(outletCode, newStock);
        try {
            StoreManager.saveModels(allWatches, allOutlets);
            System.out.println("Stock information updated successfully!");
        } catch (Exception e) {
            //when encounter error
            System.err.println("Error updating stock information: " + e.getMessage());
        }
    }


public void editSalesInformation() {
    System.out.println("=== Edit Sales Information ===");
    List <Sales> allSales = DataLoader.loadSales();

    if (allSales.isEmpty()) {
        System.out.println("No sales records found to edit.");
        return;
    }

    //2. Get input and find sale record
    System.out.print("Enter Transaction Date (YYYY-MM-DD): ");
     String searchDate = scanner.nextLine().trim();

     System.out.print("Enter Customer Name: ");
        String searchName = scanner.nextLine().trim();

    //find Sales Record matching both the Date and Name
        Sales targetSale = allSales.stream()
                .filter(s -> s.getDate().equals(searchDate) && s.getCustomerName().equalsIgnoreCase(searchName))
                .findFirst()
                .orElse(null);

        if (targetSale == null) {
        System.out.println("No matching sales record found for the given date and customer name.");
        return;
        }

        //if Sales found, display current info
        System.out.println("Sales Record Found:");
        System.out.println("Model: " + targetSale.getModel() + "Quantity: " + targetSale.getQuantity());
        System.out.println("Total: RM " + String.format("%.2f", targetSale.getSubtotal()));
        System.out.println("Transaction Method: " + targetSale.getTransactionMethod());

        //3. Selecting which field to edit
        int choice = -1;
        while (choice !=0) {
          System.out.println("Select field to edit:");
          System.out.println("1. Name\t\t2. Model\t3. Quantity\t4. Total\t5. Transaction Method");
          System.out.println("0. Save and Exit");
            System.out.print("> ");

            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= 1 && choice <= 5) {
                    handleSalesEditChoice(choice, targetSale); //Pass targetSale only
                } else if (choice != 0) {
                    System.out.println("Invalid choice. Please select 1 through 5");
                    }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number (0-5).");
            }
        }

        //confirmation before saving
        System.out.print("Confirm Update? (Y/N): ");
        String confirm = scanner.nextLine().trim();


        if (confirm.equalsIgnoreCase("Y")) {
            StoreManager.saveSales(allSales);
            try {
                 my.edu.wix1002.goldenhour.StorageSystem.StoreManager.saveSales(allSales);
                 System.out.println("Sales information updated successfully."); 

            } catch (Exception e) {
                System.err.println("Error updating sales information: " + e.getMessage());}
            } else {
            System.out.println("Update cancelled. Changes were not saved to file.");
            }
        }
        
    
        private void handleSalesEditChoice(int choice, Sales targetSale) {
          
        switch (choice) {

        case 1: // Added Case 1 Logic
            System.out.print("Enter New Name: ");
            String newName = scanner.nextLine().trim();
            targetSale.setCustomerName(newName);
            System.out.println("Sales Information updated successfully.");
            break;

        case 2: // Edit Model
            String oldModel = targetSale.getModel();
            System.out.print("Enter New Model ID (e.g., DW3000-5): ");
            String newModelId = scanner.nextLine().trim();

            //Ensure the new model exists in the system

            boolean modelExists = allWatches.stream()
            .anyMatch(m -> m.getModelId().equalsIgnoreCase(newModelId));
                
            if (modelExists) {
            //Updating the Model object in memory
            targetSale.setModel(newModelId);
            System.out.println("Sales Information updated successfully.");

            } else {
            System.out.println("Error: Model ID '" + newModelId + "' does not exist in the system. Update cancelled.");
            } break;


        case 3: { // Edit Quantity
            int oldQuantity = targetSale.getQuantity();
            double oldSubtotal = targetSale.getSubtotal();
            double unitPrice = targetSale.getUnitPrice();

            int newQuantity = -1;
            boolean validQuantity = false;

            // Loop until we get a valid, positive integer
            while (!validQuantity) { 
            try {
            System.out.print("Enter New Quantity: ");
            String input = scanner.nextLine().trim();
            newQuantity = Integer.parseInt(input);
                        
            // Check if +ve
            if (newQuantity <= 0) {
                System.out.println("Error: Quantity must be a positive whole number (1 or more).");
            } else {
                validQuantity = true; // Input is valid, exit the loop
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid whole number.");
                // validQuantity remains false, so the loop repeats
                }
            }
                
            //calculate new subtotal
            double newSubtotal = newQuantity * unitPrice;
            //Set the values
            targetSale.setQuantity(newQuantity);
            targetSale.setSubtotal(newSubtotal);
            //output confirmation
            System.out.println("Sales Information updated successfully.");
            break; 
        }

        case 4: {// Edit Subtotal 
            double oldSubtotal = targetSale.getSubtotal();
            double newSubtotal = -1.0;
            boolean validSubtotal = false;

            while (!validSubtotal) {
            try {
                System.out.print("Enter NEW Subtotal (Total Price): ");
                String input = scanner.nextLine().trim();
                newSubtotal = Double.parseDouble(input);
                if (newSubtotal <= 0) {
                    System.out.println("Error: Subtotal must be greater than RM 0.00.");
                } else {
                    validSubtotal = true;
                    }
                } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                }
            }
            targetSale.setSubtotal(newSubtotal);
            System.out.println("Sales Information updated successfully.");
            break;
        }
        

        case 5: // Edit transaction method
            boolean validMethod = false;
            String selectedMethod = "";
            while (!validMethod) {
                System.out.println("Select New Transaction Method:");
                System.out.println("- Cash\n- Credit Card\n- Debit Card\n- E-wallet");
                System.out.print("> ");
                selectedMethod = scanner.nextLine().trim();

            if (selectedMethod.equalsIgnoreCase("Cash") || 
                selectedMethod.equalsIgnoreCase("Credit Card") || 
                selectedMethod.equalsIgnoreCase("Debit Card") || 
                selectedMethod.equalsIgnoreCase("E-wallet")) {
                targetSale.setTransactionMethod(selectedMethod);
                System.out.println("Sales Information updated successfully.");
                validMethod = true; 
            } else {
                System.out.println("Invalid method. Please try again.");
                }
                } 
                break;   
    }

}

    private void performFinalSaveAndAudit(List<Sales> allSales) {
        try {
            // Overwrite sales.csv with the updated list
            my.edu.wix1002.goldenhour.StorageSystem.StoreManager.saveSales(allSales); 
            AutoEmail.sendDailyReport(allSales.size());
        } catch (Exception e) {
            System.err.println("An error occurred during save: " + e.getMessage());
        }
    } 
}