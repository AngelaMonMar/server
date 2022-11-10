/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.objetos;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JOptionPane;

/**
 *
 * @author sinNombre
 */
public class EnviarEmail {
    
    private String correoDeOrigen="angela.montoya.martinez.alu@iesfernandoaguilar.es";//EL MIO
    private String correoDeDestino;// EL CLIENTE, lo obtengo con su nick
    private String asunto;
    private String mensajeDeTexto;
    private String contraseña16Digitos="gsqhzdklfiqgwahp";// No esta activo
            
  public EnviarEmail(String destino,String asunto,
          String txt,String contra16Digitos){

   this.correoDeDestino = destino;
   this.asunto = asunto;
   this.mensajeDeTexto = txt;
   this.contraseña16Digitos = contra16Digitos;
   
  }

  public void envioDeMensajes(){
      try{
  Properties p = new Properties();  
        p.put("mail.smtp.host", "smtp.gmail.com");
        p.setProperty("mail.smtp.starttls.enable", "true");
        p.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        p.setProperty("mail.smtp.port", "587");
        p.setProperty("mail.smtp.user",correoDeOrigen);
        p.setProperty("mail.smtp.auth", "true");
        Session s = Session.getDefaultInstance(p); 
        MimeMessage mensaje = new MimeMessage(s);
        mensaje.setFrom(new InternetAddress(correoDeOrigen));
        mensaje.addRecipient(Message.RecipientType.TO, new InternetAddress(correoDeDestino));
        mensaje.setSubject(asunto);
        mensaje.setText(mensajeDeTexto);
            
          Transport t = s.getTransport("smtp");
          t.connect(correoDeOrigen,contraseña16Digitos);
          t.sendMessage(mensaje, mensaje.getAllRecipients());
          t.close();
          JOptionPane.showMessageDialog(null,"Mensaje enviado");
          } catch (MessagingException e) {
           
        }
  }
}
