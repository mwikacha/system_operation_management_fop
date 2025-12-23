package my.edu.wix1002.goldenhour.model;

public class Sales {
    private String saleID;
    private String employeeID;
    private String outletCode;
    private String customerName;
    private String model;
    private int quantity;
    private double unitPrice;
    private double subtotal; //total price for the specific item line
    private String transactionMethod;
    private String date;
    private String time;
    
    //Total number of fields is 11

    public Sales(String saleID, String employeeID, String outletCode, String customerName, 
                 String model, int quantity, double unitPrice, double subtotal, 
                 String transactionMethod, String date, String time) {
        this.saleID = saleID;
        this.employeeID = employeeID;
        this.outletCode = outletCode;
        this.customerName = customerName;
        this.model = model;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = subtotal;
        this.transactionMethod = transactionMethod;
        this.date = date;
        this.time = time;
    }
    
    //for displaying current values
    public String getSaleID() { return saleID; }
    public String getEmployeeID() { return employeeID; }
    public String getOutletCode() { return outletCode; }
    public String getCustomerName() { return customerName; }
    public String getModel() { return model; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public double getSubtotal() { return subtotal; }
    public String getTransactionMethod() { return transactionMethod; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    
    //SETTERS FOR EDITABLE FIELDS
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setModel(String model) { this.model = model; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    public void setTransactionMethod(String transactionMethod) { this.transactionMethod = transactionMethod; }  

    //Data Saving, for storesystem to use
    @Override
    public String toString() {
        return String.join(",", 
            saleID, employeeID, outletCode, customerName, model, 
            String.valueOf(quantity), String.valueOf(unitPrice), 
            String.valueOf(subtotal), transactionMethod, date, time);
    }
}
