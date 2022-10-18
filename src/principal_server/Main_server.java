/*

Funcionamiento:
LADO CL
 FRAME
 M-inicializar --> jButton_X.addActionListener(controlador);
 crea el evento al boton --> ActionPerformed
 switch-->comprobar q el actioncommand

ControladorCliente:
 recibe el evento con en el M  actionPerformed(ActionEvent e)
 evalua el actionCommand recibido (y los dats intro x cl)
 envia msg al servidor con el protocolo como [0]+ (el mensaje[1]del cliente )

LADO SERVER
 HiloDelCliente recibe lo q cliente ha enviado
 switch --> evalua lo recibido
 envia respuesta al cliente

LADO CL
 ModeloCliente recibe lo enviado x el server

diseño links :
https://www.shutterstock.com/es/image-vector/login-register-form-blue-theme-desktop-1698560326
https://medium.com/uxlatam/50-login-y-formularios-de-registro-para-inspirarse-96f9282e4c3e

 */
package principal_server;

import controlador.ControladorServidor;
import controlador.Clase_compartida;
import modelo.MS_hiloDelCliente;

import vista.VistaJFrame_log_servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.Conexion;

/**
 *
 * @author sinNombre
 */
public class Main_server {

    /**
     * link comandos git https://viviryaprenderweb.com/10-comandos-git-esenciales-para-saber-por-donde-empezar/
     */
    public static void main(String[] args) throws IOException {
        VistaJFrame_log_servidor vista = new VistaJFrame_log_servidor(); // la ventana del servidor
        Clase_compartida clase_compartida=new Clase_compartida();

        int port=19999;
        ServerSocket ss=null;

        try {
            ss=new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("IOException "+port);
        }
        System.out.println("<Server>: conected.. ");
        ControladorServidor controlador;
        
//       Conexion instancia;
//       Connection conexion;
//       
//       // la conexion se la paso x pm
//            try {
//            conexion= Conexion.getConnection();
//            String sql = "SELECT * from rol";
//                 
//                PreparedStatement ps = conexion.prepareStatement(sql);
//                ResultSet rs = ps.executeQuery();
//                while(rs.next()){
//                    System.out.print(rs.getInt("id_rol"));
//                    System.out.print(" - "+rs.getString("dominio"));
//                    System.out.println("");
//                    }
//            ps.close();
//       
//
//        } catch (SQLException ex) {
//                System.out.println("SQLException-error "+ex.getMessage());
//        } 
        

        boolean seguir=true;
        while (seguir){
            // para cada cliente se crea un hilo y se arranca en la class controlador

            MS_hiloDelCliente modelo_hiloCl = new MS_hiloDelCliente(ss, clase_compartida);
            controlador = new ControladorServidor(vista, modelo_hiloCl);
            modelo_hiloCl.setControlador(controlador);

            controlador.arrancar();

        }

        ss.close();
        
    }
}

