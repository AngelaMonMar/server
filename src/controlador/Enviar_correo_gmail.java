/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.JOptionPane;

/**
 * Importante:
 * google
 *1- Iniciar sesión en Google -->activar los dos pasos
 *2- crea pw en google
 * 
 * @author https://www.youtube.com/watch?v=s5RYPoQTXBg
 */
public class Enviar_correo_gmail {
        private String correoDeOrigen; //="angydust666@gmail.com";
        private String pw; //="socpcsgsbwgjasme";
        private String correoDestino; //="angydust666@gmail.com";   
        private String asunto; //="ASUNTO";
        private String txt; //="contenido ";

    public Enviar_correo_gmail(String correoDeOrigen, String pw, String correoDestino, String asunto, String txt) {
        this.correoDeOrigen = correoDeOrigen;
        this.pw = pw;
        this.correoDestino = correoDestino;
        this.asunto = asunto;
        this.txt = txt;
    }

  public void ennviarEmail(){
           
        Properties p=new Properties();
        p.put("mail.smtp.host", "smtp.gmail.com");
        p.setProperty("mail.smtp.starttls.enable", "true");
        p.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        p.setProperty("mail.smtp.port", "587");
        p.setProperty("mail.smtp.user",correoDeOrigen);
        p.setProperty("mail.smtp.auth", "true");
          
        Session s = Session.getDefaultInstance(p); 
        MimeMessage mensaje = new MimeMessage(s);
        try {
         mensaje.setFrom(new InternetAddress(correoDeOrigen));
        mensaje.addRecipient(Message.RecipientType.TO, new InternetAddress(correoDestino));
        mensaje.setSubject(asunto);
        mensaje.setText(txt);
            
        } catch (AddressException ex) {
           System.out.println("AddressException "+ex.getMessage());
        } catch (MessagingException ex) {
            System.err.println("MessagingException :-:"+ex.getMessage());
        }
         
        
        try {
           Transport t = s.getTransport("smtp");
           t.connect(correoDeOrigen,pw);
           t.sendMessage(mensaje, mensaje.getAllRecipients());
           t.close();
            
            JOptionPane.showMessageDialog(null, "Mensaje enviaDO");
            
        } catch (NoSuchProviderException ex) {
            System.out.println("NoSuchProviderException "+ex.getMessage());
        } catch (MessagingException ex) {
            System.out.println("MessagingException ::"+ex.getMessage());    
        }
  }
        
        
    
    public static void main(String [] args){
     Enviar_correo_gmail c = new Enviar_correo_gmail(
            "angydust666@gmail.com",
             "socpcsgsbwgjasme",
             "angydust666@gmail.com",
             "ASUNTO",
             "contenido "
     );
     c.ennviarEmail();
    }
    
}
