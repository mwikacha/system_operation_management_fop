package my.edu.wix1002.goldenhour.AutoEmail;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class AutoEmail {
    public static void sendDailyReport(int totalTransactions) {
        final String myEmail = "goldenhour410@gmail.com"; 
        final String pass = "wrbewkmeefczkaro"; //

        Properties prop = new Properties();
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");

        try {
            javax.mail.Session session = javax.mail.Session.getInstance(prop, new javax.mail.Authenticator() {
                protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                    return new javax.mail.PasswordAuthentication(myEmail, pass);
                }
            });

            javax.mail.Message message = new javax.mail.internet.MimeMessage(session);
            message.setFrom(new javax.mail.internet.InternetAddress(myEmail));
            message.setRecipients(javax.mail.Message.RecipientType.TO, javax.mail.internet.InternetAddress.parse(myEmail));
            message.setSubject("Daily Sales Report - " + java.time.LocalDate.now()); //
            
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText("Automated Summary:\nTotal Transactions: " + totalTransactions);

            MimeBodyPart attachmentPart = new MimeBodyPart();
            String filename = "data/sales.csv"; // Make sure this path matches your project!
            DataSource source = new FileDataSource(filename);
            attachmentPart.setDataHandler(new DataHandler(source));
            attachmentPart.setFileName("Daily_Sales_Report.csv");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            multipart.addBodyPart(attachmentPart);

            // Set the content of the message
            message.setContent(multipart);

            javax.mail.Transport.send(message);
            System.out.println("SUCCESS: Automated email sent to headquarters."); //

        } catch (Exception e) {
            System.out.println("EMAIL ERROR: " + e.getMessage());
        }
    }
}