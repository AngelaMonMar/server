/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
https://fernando-gaitan.com.ar/conectar-java-con-mysql-en-netbeans/
* https://www.androfast.com/2017/04/como-subir-una-imagen-desde-android-una.html
 * @author sinNombre
 */
public class Conexion {
   private static Connection cnx = null;
   private static String nombreBd="a_proyectoapp";
   private static String user="root";
   private static String password="";
   
   public static Connection getConnection() throws SQLException {
      if (cnx == null) {
         try {
            Class.forName("com.mysql.jdbc.Driver");
            cnx = DriverManager.getConnection("jdbc:mysql://localhost/"+nombreBd, user, password);
         }  catch (ClassNotFoundException ex) {
             System.out.println("ClassNotFoundException "+ex.getMessage());
         }
      }
      return cnx;
   }
   public static void cerrar() throws SQLException {
      if (cnx != null) {
         cnx.close();
      }
   }
}