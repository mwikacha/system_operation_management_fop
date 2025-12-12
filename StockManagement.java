/*
This module enables employees to manage and update stock-related 
information in the store. 
1. Morning and Night Stock Count
Employees must perform stock counting twice a day, once during 
opening (morning count) and another before closing (night count). For 
each model, the employee will key in the total number of items counted
in-store (assuming all models are displayed). The system should display 
a confirmation message if the numbers match. However, if the numbers 
do not tally, a warning message should appear.
2. Stock In and Stock Out
This feature records stock movements between outlets or from the 
service center. 
• Stock In - When new models are received from the service center 
or other outlets. 
• Stock Out - When models are transferred out to another outlet.
Each stock movement must generate a text-based receipt containing:
• Transaction Type (Stock In/Out)
• Date and Time (automatic)
• From (Outlet Code)
• To (Outlet Code)
• Model Name(s) with Quantity
• Total Quantity
• Name of Employee in Charge (automatic, based on the currently 
logged-in account)
Receipts should be saved by date, ensuring that records from previous 
days are not overwritten. All stock movements for the same day should
be appended to the same file.
 */

/*
This module enables employees to manage and update stock-related 
information in the store. 
1. Morning and Night Stock Count
Employees must perform stock counting twice a day, once during 
opening (morning count) and another before closing (night count). For 
each model, the employee will key in the total number of items counted
in-store (assuming all models are displayed). The system should display 
a confirmation message if the numbers match. However, if the numbers 
do not tally, a warning message should appear.
2. Stock In and Stock Out
This feature records stock movements between outlets or from the 
service center. 
• Stock In - When new models are received from the service center 
or other outlets. 
• Stock Out - When models are transferred out to another outlet.
Each stock movement must generate a text-based receipt containing:
• Transaction Type (Stock In/Out)
• Date and Time (automatic)
• From (Outlet Code)
• To (Outlet Code)
• Model Name(s) with Quantity
• Total Quantity
• Name of Employee in Charge (automatic, based on the currently 
logged-in account)
Receipts should be saved by date, ensuring that records from previous 
days are not overwritten. All stock movements for the same day should
be appended to the same file.
 */


// for input
import java.util.Scanner;
// to generate time
import java.text.SimpleDateFormat;
import java.util.Date;
// possible error
import java.util.InputMismatchException;
//read,write file
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

// only this class can use (help to check the model status)
class Model {
    String modelCode;
    int price;
    static String [] recordedOutlet = StockManagement.modelOutlets();
    int line = StockManagement.countModelOutlets();
    
    int[] plannedStock = new int[line]; // C60 to C69
    
}

// only this class can use (help to confirm the outlet)
class Outlet {
    String outletCode;
    String outletName;
    int line = StockManagement.countOutlets();
    
}

public class StockManagement {

    // employee name!!!
    private static String employeeName = "";

    private static Scanner allScanner = new Scanner(System.in);

    // time format
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat checkFormat_h = new SimpleDateFormat("HH");
    private static SimpleDateFormat checkFormat_m = new SimpleDateFormat("mm");

    // main method need to call
    public static void setEmployeeName(String name) {
        employeeName = name;
    }

    // present time in 12 hours
    private static String _12FormatTime(Date checkTime){
        String _12Time = "";
        Integer h = Integer.valueOf(checkFormat_h.format(checkTime));
        if (h > 12){
            _12Time = (h - 12) + ":" + checkFormat_m.format(checkTime) + " p.m.";
        }
        else if (h == 12){
            if (Integer.valueOf(checkFormat_m.format(checkTime)) == 0){
                _12Time = "12:" + checkFormat_m.format(checkTime) + " a.m.";
            }
            else{
                _12Time = "12:" + checkFormat_m.format(checkTime) + " p.m.";
            }
        }
        else{
            _12Time = (h) + ":" + checkFormat_m.format(checkTime) + " a.m.";
        }

        return _12Time;
    }

    // read model.csv
    public static int countModelOutlets(){

        int totalLines = 0;

            try 
            {
                BufferedReader inputStream = new BufferedReader (new FileReader("model.csv"));
                String line = inputStream.readLine();
                String[] title = line.split(",");
                totalLines = title.length -2;
                Model.recordedOutlet = new String[totalLines];
                for (int i = 2; i < title.length; i++)
                {
                    Model.recordedOutlet[i-2] = title[i];
                }
                inputStream.close();
            }
            catch (FileNotFoundException e) 
            {
            System.out.println("The file \"model.csv\" was not found");   
            } 
            catch (IOException e) 
            {
            System.out.println("Error reading from file");
            }
        return totalLines;    
    }

    public static String[] modelOutlets(){
            String [] recordedOutlet = new String [countModelOutlets()];
            try 
            {
                BufferedReader inputStream = new BufferedReader (new FileReader("model.csv"));
                String line = inputStream.readLine();
                String[] title = line.split(",");
                for (int i = 2; i < title.length; i++)
                {
                    recordedOutlet[i-2] = title[i];
                }
                inputStream.close();
            }
            catch (FileNotFoundException e) 
            {
            System.out.println("The file \"model.csv\" was not found");   
            } 
            catch (IOException e) 
            {
            System.out.println("Error reading from file");
            }
        return recordedOutlet;    
    }

    private static int countModels()
    {
        int totalLines = 0;

            try 
            {
                BufferedReader inputStream = new BufferedReader (new FileReader("model.csv"));
                String line;
                boolean firstLine = true;

                while ((line = inputStream.readLine()) != null) 
                {
                    // skip the title
                    if (firstLine) {
                        firstLine = false; 
                        continue;
                    }
                    
                    totalLines ++;
                } 
                inputStream.close();
            }
            catch (FileNotFoundException e) 
            {
            System.out.println("The file \"model.csv\" was not found");   
            } 
            catch (IOException e) 
            {
            System.out.println("Error reading from file");
            }
        return totalLines;
    }

    private static Model[] createCSVModels()
    {
        int totalLines = countModels();
        Model[] a = new Model[totalLines];
        return a;
    }
    
    private static void modelsRead(Model[] models){
        try 
        {
            BufferedReader inputStream = new BufferedReader (new FileReader("model.csv"));
            String line;
            boolean firstLine = true;
            int index = 0;
            

            while ((line = inputStream.readLine()) != null) {
                // skip the title
                if (firstLine) {
                    firstLine = false; 
                    continue;
                }   
                // basic information - attributes
                String[] values = line.split(",");
                Model model = new Model(); //one object
                model.modelCode = values[0]; // the first element is the code of the object
                model.price = Integer.valueOf(values[1]); // the second element is the price of the object
                    
                // read C60-C69（index 2 to 11, fill in the plannedstock)
                for (int i = 0; i < 10; i++) 
                {
                    model.plannedStock[i] = Integer.valueOf(values[i + 2]);
                }

                models[index] = model; 
                index ++;         
            }
        inputStream.close();
        } 
        catch (FileNotFoundException e) {
        System.out.println("The file \"model.csv\" was not found");  
        } 
        catch (IOException e) {
        System.out.println("Error reading from file");
        }

    }

    // model check

    private static int modelsCheckIndex(String outlet)
    {
        int index = 0;
        for (int i = 0; i < Model.recordedOutlet.length; i++){
            if (Model.recordedOutlet[i].equals(outlet)){
                index = i;
                break;
            }
        }
        return index;
    }

    private static int modelsCheck(Model[] models, int index)
    {
        int check = 0;
        int correct = 0;
        int wrong = 0;
        for (int i = 0; i < models.length; i++)
        {
            System.out.print("Model: " + models[i].modelCode + " - Counted: ");
            int count = allScanner.nextInt();
            allScanner.nextLine(); 
            int record = models[i].plannedStock[index];
            System.out.println("Store Record: " + record);
            if (count == record){
                correct ++;
                System.out.println("Stock tally correct.");
            }
            else{
                wrong ++;
                int diff = Math.abs(count - record);
                System.out.println("! Mismatch detected (" + diff + " unit difference)");
            }
            check ++;
        }
        System.out.println("Total Models Checked: " + check);
        System.out.println("Tally Correct: " + correct);
        System.out.println("Mismatches:" + wrong);
        
        return wrong;
    
    }

    // read outlet.csv
    public static int countOutlets()
    {
        int totalLines = 0;

            try 
            {
                BufferedReader inputStream = new BufferedReader (new FileReader("outlet.csv"));
                String line;
                boolean firstLine = true;

                while ((line = inputStream.readLine()) != null) 
                {
                    // skip the title
                    if (firstLine) {
                        firstLine = false; 
                        continue;
                    }
                    
                    totalLines ++;
                } 
                inputStream.close();
            }
            catch (FileNotFoundException e) 
            {
            System.out.println("The file \"model.csv\" was not found");   
            } 
            catch (IOException e) 
            {
            System.out.println("Error reading from file");
            }
        return totalLines;
    }

    private static Outlet[] createCSVOutlet()
    {
        int totalLines = countOutlets();
        Outlet[] a = new Outlet[totalLines];
        return a;
    }
   
    private static void outletsRead(Outlet[]outlets){
        try 
        {
            BufferedReader inputStream = new BufferedReader (new FileReader("outlet.csv"));
            String line;
            boolean firstLine = true;
            int index = 0;
            

            while ((line = inputStream.readLine()) != null) {
                // skip the title
                if (firstLine) {
                    firstLine = false; 
                    continue;
                }   
                // basic information - attributes
                String[] values = line.split(",");
                Outlet outlet = new Outlet(); //one object
                outlet.outletCode = values[0]; // the first element is the code of the outlet
                outlet.outletName = values[1]; // the second element is the name of the outlet
                    
                outlets[index] = outlet; 
                index ++;         
            }
        inputStream.close();
        } 
        catch (FileNotFoundException e) {
        System.out.println("The file \"model.csv\" was not found");  
        } 
        catch (IOException e) {
        System.out.println("Error reading from file");
        }

    }

    private static String matchName(Outlet[] outlets, String code){
        String name = "";
        for (int i = 0; i< outlets.length; i++)
        {
            if(outlets[i].outletCode.equals(code))
            {
                name = outlets[i].outletName;
                break;
            }
        }
        return name;
    }
    
    // how many times of stock count is needed
    public static void countTime(String _12Time){
        if (_12Time.charAt(_12Time.length()-4) == 'p'){
            nightCount += 1;
        }
        else{
            morningCount += 1;
        }
    }

    //begin 
    private static String whichOutlet(){
        boolean valid = false;
        while (true)
        {
            System.out.print("The store's code is: ");
            String outlet = allScanner.nextLine();
            for (String outletC : Model.recordedOutlet)
            {
                if (outletC.equals(outlet)){
                    valid = true;
                    break;
                }
            }
            if (valid)
            {
                return outlet;
            }
            else
            {
                System.out.println("You entered an invalid outlet code, try again.");
            }

        }
        
    }
    
    private static int[] selectPurpose(){
        String purposes = "";
        System.out.println("Select what purpose(s) you want to accomplish:" );
        System.out.println("Enter 1 for stock Count; \nEnter 2 for Stock In \nEnter 3 for stock Out; \nEnter 0 for finish");

        while (true) {
            try {
                System.out.print("Your choice: ");
                int p = allScanner.nextInt();
                allScanner.nextLine(); 
                
                if (p == 1 || p == 2 || p==3) {
                    purposes += p;
                } 
                else if(p == 0){
                    break;
                }
                else {
                    System.out.println("Please enter 1,2 or 3 to select task(s) you want to do; enter 0 to finish selection.");
                }
            } 
            catch (InputMismatchException e) {
                System.out.println("Please Enter an integer.");
                allScanner.next(); 
            }
        }

        int[] tasks = new int[purposes.length()];
        for(int i = 0; i < tasks.length; i++){
            tasks[i] = purposes.charAt(i) - '0';
        }

        return tasks;
    }

    // stock count
    private static int morningCount = 0;
    private static int nightCount = 0;

    private static void todoList(Date checkTime){
        System.out.println("Todo:");
        String check = _12FormatTime(checkTime);
        if (morningCount < 1){
            System.out.println("1. Morning Stock Count - 1 time");
            if (check.charAt(check.length()-4)=='p'){
                System.out.println("... You missed it today :(");
            }
        }
        else{
            System.out.println("1. Morning stock Count - 1 time (finished " + morningCount + " times)");
        }

        if (nightCount < 1){
            System.out.println("2. Night Stock Count - 1 time");
        }
        else{
            System.out.println("2. Night stock Count - 1 time (finished " + nightCount + " times)");
        }

        System.out.println();
    }

    private static void morningCheck(Date checkTime,String store){
        
        System.out.println("\n=== Morning Stock Count ===");
        System.out.println("Date: " + dateFormat.format(checkTime));
        String time = _12FormatTime(checkTime);
        countTime(time);
        System.out.println("Time: " + time);

        // employee information
        
        // if there is no name
        if (!employeeName.isEmpty()) {
            System.out.println("Employee: " + employeeName);
        }

        Model[] models = createCSVModels();
        modelsRead(models);
        int index = modelsCheckIndex(store);
        int wrong = modelsCheck(models,index);
        System.out.println("Morning stock count completed.");
        if (wrong > 0)
        {
            System.out.println("Warning: Please verify stock.");
        }
    }

    private static void nightCheck(Date checkTime,String store){
        System.out.println("\n=== Night Stock Count ===");
        System.out.println("Date: " + dateFormat.format(checkTime));
        String time = _12FormatTime(checkTime);
        countTime(time);
        System.out.println("Time: " + time);
        // if there is no name
        if (!employeeName.isEmpty()) {
            System.out.println("Employee: " + employeeName);
        }
        Model[] models = createCSVModels();
        modelsRead(models);
        int index = modelsCheckIndex(store);
        int wrong = modelsCheck(models,index);
        System.out.println("Night stock count completed.");
        if (wrong > 0)
        {
            System.out.println("Warning: Please verify stock.");
        }
    }


    // stock in

    private static String[] information1(String[]f_t){
        System.out.println("\nWhere are the new models are received from? \nEnter 1 for HQ(Service Center) \nEnter 2 for other outlets");

        Outlet[] outlets = createCSVOutlet();
        outletsRead(outlets);

        //from
        /////////////////////////////////////////////////////////////////////////////////////////
         try {
            int from = allScanner.nextInt();
            allScanner.nextLine(); 
            if (from == 1) {
                f_t[0] = "From: HQ (Service Center)";
            } 
            else if(from ==2){
                String outlet = whichOutlet();
                String name = matchName(outlets, outlet);
                f_t[0]= "From: " + outlet + " (" + name + ")";
            }
            else {
                System.out.println("Please enter 1 or 2 to select outlet the model(s) from.");
            }
        } 
        
        catch (InputMismatchException e) {
            System.out.println("Please Enter an integer.");
            allScanner.next(); 
        }

        //to
        ///////////////////////////////////////////////////////////////////////////////////////////
        System.out.println("Where are the models reveived by?");
        String outlet_ = whichOutlet();
        String name_ = matchName(outlets, outlet_);
        f_t[1] = "To: " + outlet_ + " (" + name_ + ")";

        //models
        /////////////////////////////////////////////////////////////////////////////////////////
        String models = "";
        System.out.println("What model(s) the store receives:(Press enter to stop) ");
        while (true){
            String model = allScanner.nextLine();
            if (model.equals(""))
            {
                break;
            }
            Model[] models_ = createCSVModels();
            modelsRead(models_);
            boolean valid = false;
            for (int i = 0; i<models_.length;i++)
            {
                if (model.equals(models_[i].modelCode))
                {
                    valid = true;
                    models += model + ",";
                    break;
                }
            }
            if (!valid)
            {
                System.out.println("Invalid model code, try again!");
            }
            
        }
        String[] modelCode = models.split(",");
        return modelCode;
        
    }

    private static int[] information2 (int[] quantity,String[] modelCode)
    {
        for(int i = 0; i < modelCode.length; i++)
        {
            System.out.print("How many " + modelCode[i] + " are received? ");
            int many = allScanner.nextInt();
            quantity[i] = many;
        } 
        allScanner.nextLine(); 
        return quantity;
    }

    
    private static String[][] receiptForm1(){
        //get information
        ////////////////////////////////////////////////////////////
        System.out.println();
        String[] from_to = new String[2];
        String[] modelCode = information1(from_to);        
        String[][] a = {from_to,modelCode};
        return a;
    }


    private static void generateReceipt(Date checkTime, String[] from_to, String[] modelCode,int[] quantity)
      {
        
        String fileName = "receipts_" + dateFormat.format(checkTime) + ".txt";
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName, true))) {
            writer.println("\n=== Stock In ===");
            writer.println("Date: " + dateFormat.format(checkTime));
            String time = _12FormatTime(checkTime);
            writer.println("Time: " + time);
            
            // employee
            if (!employeeName.isEmpty()) {
                writer.println("Employee in Charge: " + employeeName);
            }

            for (String a : from_to)
            {
            writer.println(a);
            }

            int total = 0;
            for (int a : quantity){total += a;}

            writer.println("Models received:");
            for (int i = 0;i < modelCode.length; i++)
            {
                writer.println("- " + modelCode[i] + " (Quantity: " + quantity[i] + ")");
            }
            writer.println("Total Quantity: " + total);
            writer.println("Model quantities updated successfully.");

            writer.println(); 
            
        } catch (IOException e) {
            System.err.println("Error generating receipt: " + e.getMessage());
        }
        
        // represent the receipt
        System.out.println("\n=== Stock In ===");
        System.out.println("Date: " + dateFormat.format(checkTime));
        System.out.println("Time: " + _12FormatTime(checkTime));
        if (!employeeName.isEmpty()) {
            System.out.println("Employee in Charge: " + employeeName);
        }
        for (String a : from_to) {
            System.out.println(a);
        }
        
        int total = 0;
        System.out.println("Models received:");
        for (int i = 0; i < modelCode.length; i++) {
            System.out.println("- " + modelCode[i] + " (Quantity: " + quantity[i] + ")");
            total += quantity[i];
        }
        System.out.println("Total Quantity: " + total);
        System.out.println("Model quantities updated successfully.");
        System.out.println("Stock In recorded.");
        System.out.println("Receipt generated: " + fileName);
    }

    private static void stockIn(Date checkTime, String[] from_to, String[] modelCode,int[] quantity) {
        generateReceipt(checkTime, from_to, modelCode, quantity);
    }

    // stock out

    private static String[] information1_out(String[]f_t){
        System.out.println("\nWhere are the models transferred from? \nEnter 1 for HQ(Service Center) \nEnter 2 for other outlets");

        Outlet[] outlets = createCSVOutlet();
        outletsRead(outlets);

        //from (source for stock out)
        /////////////////////////////////////////////////////////////////////////////////////////
         try {
            int from = allScanner.nextInt();
            allScanner.nextLine(); 
            if (from == 1) {
                f_t[0] = "From: HQ (Service Center)";
            } 
            else if(from ==2){
                String outlet = whichOutlet();
                String name = matchName(outlets, outlet);
                f_t[0]= "From: " + outlet + " (" + name + ")";
            }
            else {
                System.out.println("Please enter 1 or 2 to select outlet the model(s) from.");
            }
        } 
        
        catch (InputMismatchException e) {
            System.out.println("Please Enter an integer.");
            allScanner.next(); 
        }

        //to (destination for stock out) - ALWAYS an outlet
        ///////////////////////////////////////////////////////////////////////////////////////////
        System.out.println("Where are the models transferred to?");
        String outlet_ = whichOutlet();
        String name_ = matchName(outlets, outlet_);
        f_t[1] = "To: " + outlet_ + " (" + name_ + ")";

        //models
        /////////////////////////////////////////////////////////////////////////////////////////
        String models = "";
        System.out.println("What model(s) the store transfers out:(Press enter to stop) ");
        while (true){
            String model = allScanner.nextLine();
            if (model.equals(""))
            {
                break;
            }
            Model[] models_ = createCSVModels();
            modelsRead(models_);
            boolean valid = false;
            for (int i = 0; i<models_.length;i++)
            {
                if (model.equals(models_[i].modelCode))
                {
                    valid = true;
                    models += model + ",";
                    break;
                }
            }
            if (!valid)
            {
                System.out.println("Invalid model code, try again!");
            }
            
        }
        String[] modelCode = models.split(",");
        return modelCode;
        
    }

    private static int[] information2_out (int[] quantity,String[] modelCode)
    {
        for(int i = 0; i < modelCode.length; i++)
        {
            System.out.print("How many " + modelCode[i] + " are transferred out? ");
            int many = allScanner.nextInt();
            quantity[i] = many;
        } 
        allScanner.nextLine(); 
        return quantity;
    }

    private static String[][] receiptForm1_out(){
        //get information
        ////////////////////////////////////////////////////////////
        System.out.println();
        String[] from_to = new String[2];
        String[] modelCode = information1_out(from_to);        
        String[][] a = {from_to,modelCode};
        return a;
    }

    private static void generateReceipt_out(Date checkTime, String[] from_to, String[] modelCode,int[] quantity)
      {
        
        String fileName = "receipts_" + dateFormat.format(checkTime) + ".txt";
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName, true))) {
            writer.println("\n=== Stock Out ===");
            writer.println("Date: " + dateFormat.format(checkTime));
            String time = _12FormatTime(checkTime);
            writer.println("Time: " + time);
            
            // employee
            if (!employeeName.isEmpty()) {
                writer.println("Employee in Charge: " + employeeName);
            }

            for (String a : from_to)
            {
            writer.println(a);
            }

            int total = 0;
            for (int a : quantity){total += a;}

            writer.println("Models transferred out:");
            for (int i = 0;i < modelCode.length; i++)
            {
                writer.println("- " + modelCode[i] + " (Quantity: " + quantity[i] + ")");
            }
            writer.println("Total Quantity: " + total);
            writer.println("Model quantities updated successfully.");

            writer.println(); 
            
        } catch (IOException e) {
            System.err.println("Error generating receipt: " + e.getMessage());
        }
        
        // represent the receipt
        System.out.println("\n=== Stock Out ===");
        System.out.println("Date: " + dateFormat.format(checkTime));
        System.out.println("Time: " + _12FormatTime(checkTime));
        if (!employeeName.isEmpty()) {
            System.out.println("Employee in Charge: " + employeeName);
        }
        for (String a : from_to) {
            System.out.println(a);
        }
        
        int total = 0;
        System.out.println("Models transferred out:");
        for (int i = 0; i < modelCode.length; i++) {
            System.out.println("- " + modelCode[i] + " (Quantity: " + quantity[i] + ")");
            total += quantity[i];
        }
        System.out.println("Total Quantity: " + total);
        System.out.println("Model quantities updated successfully.");
        System.out.println("Stock Out recorded.");
        System.out.println("Receipt generated: " + fileName);
    }

    private static void stockOut(Date checkTime, String[] from_to, String[] modelCode,int[] quantity) {
        generateReceipt_out(checkTime, from_to, modelCode, quantity);
    }



public static void main(String[] args) {

    // 2 mothods we can get the employee name 
    if (employeeName.isEmpty()) {
        System.out.println("=== Stock Management System (Standalone Mode) ===");
    } else {
        System.out.println("=== Stock Management System ===");
        System.out.println("Employee: " + employeeName);
    }

    String store = whichOutlet();

    /////////////////////////////////////////////////////////////////////////////////////////////
    int[] purposes = selectPurpose(); // The tasks which the user wants to accomplish by this program :)
    
    System.out.println();
    /////////////////////////////////////////////////////////////////////////////////////////////

    Date now = new Date(); //generate the time fpr now

    for (int purpose : purposes){
        if(purpose == 1)
        { // means stock count
            /*
            Employees must perform stock counting twice a day, 
            once during opening (morning count) and another before closing (night count). 
            */
            todoList(now); // mini tasks under stock count

            String checkNow = _12FormatTime(now);
            if (checkNow.charAt(checkNow.length()-4) == 'a')
            { // a.m. -> morningcheck
                morningCheck(now,store);
            }
            else
            { //p.m. -> nightcheck
                nightCheck(now,store);
            }
        }
        else if (purpose == 2)
        {
            String[][] ft_mc = receiptForm1();
            String[] from_to = ft_mc[0];
            String[] modelCode = ft_mc[1];
            int[] quantity = new int[modelCode.length];
            quantity = information2(quantity, modelCode);
            
            stockIn(now, from_to, modelCode, quantity);
        }
        else if (purpose == 3)
        {
            String[][] ft_mc = receiptForm1_out();
            String[] from_to = ft_mc[0];
            String[] modelCode = ft_mc[1];
            int[] quantity = new int[modelCode.length];
            quantity = information2_out(quantity, modelCode);
            
            stockOut(now, from_to, modelCode, quantity);
        }

    }
    
    System.out.println("\n=== Program completed ===");
}
}