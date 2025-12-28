package my.edu.wix1002.goldenhour.dataAnalytics;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * SalesAnalytics
 * computes total sales per day, week, month
 *  most sold product model, and average daily revenue
 * support user-defined date range
 */

public class SalesAnalytics {

    private static final String SALES_CSV = "data/sales.csv";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        //Load sales records from csv
        List<SaleRecord> records = loadSalesRecords();
        if (records.isEmpty()) {
            System.out.println("No sales records found.");
            return;
        }

        Scanner sc = new Scanner(System.in);
        System.out.println("Enter start date (yyyy-MM-dd):");
        LocalDate starDate = LocalDate.parse(sc.nextLine(), DATE_FMT);

        System.out.println("Enter end date (yyyy-MM-dd):");
        LocalDate endDate = LocalDate.parse(sc.nextLine(), DATE_FMT);

        System.out.println();

        System.out.println("=== Analytical Summary of Sales Records ===");

        System.out.println("\n=== Total Sales Per Day ===");
        printTotalSalesPerDay(records, starDate, endDate);

        System.out.println("\n=== Total Sales Per Week ===");
        printTotalSalesPerWeek(records, starDate, endDate);

        System.out.println("\n=== Total Sales Per Month ===");
        printTotalSalesPerMonth(records, starDate, endDate);

        System.out.println("\n=== Most Sold Product Model ===");
        printMostSoldProduct(records, starDate, endDate);

        printAverageDailyRevenue(records, starDate, endDate);
        
        System.out.println();
        sc.close();
    }

    /** Load sales.csv into list of SaleRecord */
    private static List<SaleRecord> loadSalesRecords() {
        List<SaleRecord> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(SALES_CSV))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; } //skip header
                String[] cols = line.split(",", -1);
                if (cols.length < 11) continue;

                //since csv file have "2025-12-25", so we need to remove"""
                String dateStr = cols[9].replace("\"", "").trim();
                LocalDate date = LocalDate.parse(dateStr, DATE_FMT);

                String model = cols[4].replace("\"", "").trim();
                int quantity = Integer.parseInt(cols[5].replace("\"", "").trim());
                BigDecimal subtotal = new BigDecimal(cols[7].replace("\"", "").trim());

                records.add(new SaleRecord(date, model, quantity, subtotal));
            }
        } catch (IOException e) {
            System.err.println("Error reading sales.csv: " + e.getMessage());
        }
        return records;
    }

    /** Total sales per day in date range */
    private static void printTotalSalesPerDay(List<SaleRecord> records, LocalDate start, LocalDate end) {
        Map<LocalDate, BigDecimal> map = new TreeMap<>();
        for (SaleRecord r : records) {
            if (!r.date.isBefore(start) && !r.date.isAfter(end)) {
                map.put(r.date, map.getOrDefault(r.date, BigDecimal.ZERO).add(r.subtotal));
            }
        }
        map.forEach((date, total) -> System.out.println(date + " : RM" + total));
    }    

    /*Total sales per week in a date range*/
    private static void printTotalSalesPerWeek(List<SaleRecord> records, LocalDate start, LocalDate end) {
        Map<String, BigDecimal> map = new TreeMap<>(); //week number -> total
        for (SaleRecord r : records) {
            if (!r.date.isBefore(start) && !r.date.isAfter(end)) {
                int week = r.date.get(java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR);
                String key = r.date.getYear() + "-W" + week;
                map.put(key, map.getOrDefault(key, BigDecimal.ZERO).add(r.subtotal));
            }
        }
        map.forEach((week, total) -> System.out.println("Week " + week + " : RM" + total ));
    }

    /*Total sales per month in a date range */
    private static void printTotalSalesPerMonth(List<SaleRecord> records, LocalDate start, LocalDate end) {
        Map<String, BigDecimal> map = new TreeMap<>();  // yyyy-MM -> total
        for (SaleRecord r : records) {
            if (!r.date.isBefore(start) && !r.date.isAfter(end)) {
                String month = r.date.format(DateTimeFormatter.ofPattern("yyyy-MM"));
                map.put(month, map.getOrDefault(month, BigDecimal.ZERO).add(r.subtotal));
            }
        }
        map.forEach((month, total) -> System.out.println(month + " : RM" + total));
    }

    /*most sold prodyct model( by total quantity) */
    private static void printMostSoldProduct(List<SaleRecord> records, LocalDate start, LocalDate end) {
        Map<String, Integer> map = new HashMap<>();
        for (SaleRecord r : records) {
            if (!r.date.isBefore(start) && !r.date.isAfter(end)) {
                map.put(r.model, map.getOrDefault(r.model, 0) + r.quantity);
            }
        }
        String topModel = map.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse("None");
        int qty = map.getOrDefault(topModel, 0);
        System.out.println(topModel + " : " + qty + " units sold");
    }

    /*Average daily Revenue in a date range */
    private static void printAverageDailyRevenue(List<SaleRecord> records, LocalDate start, LocalDate end) {
        Map<LocalDate, BigDecimal> dailySales = new HashMap<>();
        for (SaleRecord r : records) {
            if (!r.date.isBefore(start) && !r.date.isAfter(end)) {
            dailySales.put(r.date, dailySales.getOrDefault(r.date, BigDecimal.ZERO).add(r.subtotal));
            }
        }
        BigDecimal total = dailySales.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        if (!dailySales.isEmpty()) {
            BigDecimal avg = total.divide(BigDecimal.valueOf(dailySales.size()), 2, RoundingMode.HALF_UP);
            System.out.println("\n=== Average Daily Revenue ===");
            System.out.println("RM" + avg);
        } else {
            System.out.println("No sales in the selected range.");
        }
    }

    /** Inner class to represent one line of sales.csv */
    private static final class SaleRecord {
        LocalDate date;
        String model;
        int quantity;
        BigDecimal subtotal;

        SaleRecord(LocalDate date, String model, int quantity, BigDecimal subtotal) {
            this.date = date;
            this.model = model;
            this.quantity = quantity;
            this.subtotal = subtotal;

        
        }
    }
}
