package controlador;

import java.net.InetAddress;
import modelo.objetos.Usuario;

import java.net.Socket;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.Conexion;
import modelo.objetos.EnviarEmail;

import static modelo.ProtocoloServer.*;

/**
 * created by sinNombre on dic., 27/12/2021
 */
public class Clase_compartida {
    private Connection conexion=null;
    private PreparedStatement pre_state;
    private ResultSet rs;
    private int id_usuario;
    
    private AndroidGestiones app_android=new AndroidGestiones();
    
  //  private Map<String, String> datosLogin=new TreeMap();// (nick, pw) [comprobar_login]
  //  private Map <String , Usuario> map_nick_Usuario=new TreeMap<>();// (nick, USUARIO)
  //  public List<Usuario> lista_usuarios=new ArrayList<>();// USUARIO
    
    public Map<Socket, Usuario> map_socket_Usuario =new HashMap<>();//( socket cl, Usuario)  
    public Map<Socket, String> map_socket_strSocket =new HashMap<>();//(socket, remoteIp)

    // con bd
    protected List<String> lista_hosts_guardado=new ArrayList<>();//(remoteIp)no tengo claro si lo necesito 
    private String sql=null;
    private Statement state;
    
    public Clase_compartida() {
    }

 
       
 //------------------------------Ms BD
    
      //M para comprabar al iniciar si anteriormente activo checkbox en login-
    //Tengo la ipdel cl y devuelve el usuario
    // usado en ESTADO_CHECKBOX
    public Usuario getUsario_x_remoteIp(String remoteIp) {
         
             for(Map.Entry<Socket, String> entry:map_socket_strSocket.entrySet()){
            
             if(entry.getValue().equals(remoteIp)){
                 System.out.println("si contiene la ip ");
                 Socket s =entry.getKey();// obtengo el socket --> 
                 // busco el usario
                  for(Map.Entry<Socket, Usuario> entryUsu:map_socket_Usuario.entrySet()){
                       if(entryUsu.getKey().equals(s)){
                           Usuario usuario=entryUsu.getValue();
                           return usuario;
                       }
                  }
             }
            }
          
             return null;
    }
    
    


    // pendiente no lo tengo claro--Giardar los conectados
    public void listSocketAdd(Socket socket){
//        this.listSocket.add(socket);
//        System.out.println("listSocket.size "+listSocket.size());
    }
 
    public List<String> getSet_strSockets_guardados() {// control del checkbox
        return lista_hosts_guardado;
    }

    // en login para guardar los usus logged
    public void addStringSocket(String socketToString) {
          lista_hosts_guardado.add(socketToString);
          for(String s :  lista_hosts_guardado){
              System.out.println(" Clase C add String "+ s); 
          }
          
    }


/*
InetAddress addy = socket.getInetAddress();
String remoteIp = addy.getHostAddress();*/



    public Map<Socket, Usuario> getListSocket_usuario() {
        return map_socket_Usuario;
    }

    public void setListSocket_usuario(Map<Socket, Usuario> listSocket_usuario) {
        this.map_socket_Usuario = listSocket_usuario;
    }


    //CONTACTO:miasunto:miemail:n:contenido
    //String destino,String asunto,
     //     String txt,String contra16Digitos
    public String enviarEmail(String mensaje) {
        String salida="0";
        String[] vstr=mensaje.split(SEPARADOR);
        EnviarEmail enviarMail=null;
        String asunto=vstr[1];
        String email_el_q_envia=vstr[2];
        String nick_destinatario=vstr[3];
        String contenido=vstr[4];
        String email_destino="";
           
     try {
         String sql="Select email from usuario where nick=?";
            this.conexion =  Conexion.getConnection();
            this.pre_state= conexion.prepareStatement(sql);
            this.pre_state.setString(1,nick_destinatario);
            ResultSet rs=null;
            rs=pre_state.executeQuery();
            
            while(rs.next()){
                email_destino=rs.getString("email");
            }
              
           if(!email_destino.isEmpty()){
                enviarMail=new EnviarEmail(email_destino, asunto, mensaje, "111111");
                enviarMail.envioDeMensajes();
                salida= OK+SEPARADOR+"Email enviado";
            }else{
                salida= NOT_OK+SEPARADOR+"No se ha podido enviar el email, comprueben que los datos son correctos o intentelo mas tarde.";
            }
           } catch (Exception e) {
               System.out.println(" Exception  enviar email "+e.getMessage());
               salida= NOT_OK+SEPARADOR+"Error al enviar el email. El nick del usuario o el email no son validos";
           }
        return  salida;
    }



    public List<String> getLista_hosts_guardado() {
        return lista_hosts_guardado;
    }

    public void setLista_hosts_guardado(List<String> lista_hosts_guardado) {
        this.lista_hosts_guardado = lista_hosts_guardado;
    }

    

}

    
