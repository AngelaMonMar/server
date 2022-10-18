/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import controlador.Clase_compartidaChatPublico;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import static modelo.ProtocoloServer.*;

/**
 *
 * @author sinNombre
 */
public class Hilo_chatPublico  extends Thread{
    Clase_compartidaChatPublico classCompartidaChat;
    Socket socket_cl;
    String name;
    String mensaje;
    
      private PrintWriter out;
    private BufferedReader in;

    public Hilo_chatPublico(Clase_compartidaChatPublico cc, 
            Socket s, String name, String mensaje) {
        this.classCompartidaChat = cc;
        this.socket_cl = s;
        this.name = name;
        this.mensaje = mensaje;
    }

  

   

    @Override
    public void run() {
        super.run(); 
        
       
       String s =this.classCompartidaChat.algo(this.socket_cl, this.name, this.mensaje);
        System.out.println("stoy aqui "+s);
            try {
            out = new PrintWriter(socket_cl.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket_cl.getInputStream()));

           String fromCliente, toCliente;

            boolean salir=false;
            while(!salir){

                if (in.ready()) {
                    fromCliente = in.readLine();// lo recibido 
                     String str= evaluarRecibido(fromCliente);
                     out.println(str);
                  
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                out.close();
                in.close();
                socket_cl.close();
            } catch (IOException e) {
                System.out.println("close cl ");
            }
        }

    }
    
    
     protected String evaluarRecibido(String fromCliente) {
        String [] vector=fromCliente.split(SEPARADOR);


//            switch (vector[0]){
//                case CHAT:
//                    break;
//            }
        
        return "OKKKK";
     }
    
      public void processOutput(String s) {

        PrintWriter pw_out=null;
        try {
            pw_out=new PrintWriter(socket_cl.getOutputStream(), true);
            pw_out.println(s);
            pw_out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
