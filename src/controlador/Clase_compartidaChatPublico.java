
package controlador;

import java.net.Socket;

/**
 *
 * @author sinNombre
 */
public class Clase_compartidaChatPublico {


 
    public String algo(Socket socket, String name, String mensaje) {
//        System.out.println("Socket "+socket);
//         System.out.println("Name "+name);
//          System.out.println("mensaje "+mensaje);
         return "Socket "+socket+"Name "+name+"mensaje "+mensaje;
    }
    
}
