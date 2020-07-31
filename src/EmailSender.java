import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * 
 */

/**
 * Christopher Brislin
 * 30 Jul 2020
 * JUserRegistration
 */
public class EmailSender {
	
	public void sendMessage(String userEmail, String content, String subject) {
		
        Properties prop = new Properties();
		prop.put("mail.smtp.host", Main.SMTP_HOST);
        prop.put("mail.smtp.port", Main.SMTP_PORT);
        prop.put("mail.smtp.auth", "true");
        
        prop.put("mail.smtp.starttls.enable","true"); 
        prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        
       // prop.put("mail.smtp.socketFactory.port", Main.SMTP_PORT);
       // prop.put("mail.smtp.socketFactory.class", 
        //        "javax.net.ssl.SSLSocketFactory"); 
      
        if(Main.DEBUG) {
        	System.out.println("Using host: "+Main.SMTP_HOST+" On port: "+Main.SMTP_PORT+" Sending from: "+Main.SEND_ADDRESS+" Authorised by: "+Main.SEND_EMAIL_PASS);
        }
        
        Session session = Session.getDefaultInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(Main.SEND_ADDRESS, Main.SEND_EMAIL_PASS);
                    }
                });
        
        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("brislinchris@gmail.com", "TheLogbook"));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(userEmail)
            );
            message.setSubject(subject);
            message.setText(content);

            Transport.send(message);

            if(Main.DEBUG)System.out.println("Email sent to: "+userEmail);

        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        
        
	}

}
