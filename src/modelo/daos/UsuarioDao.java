/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.daos;

import controlador.Clase_compartida;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.Conexion;
import static modelo.ProtocoloServer.*;

import modelo.objetos.Usuario;
import static util.SentenciasSql.*;


/**
 *
 * @author sinNombre
 */
public class UsuarioDao {

    private String sql=null;
    private Statement state;
    private Connection conexion=null;
    private PreparedStatement pre_state;
    private ResultSet rs;
    private int id_usuario;
    protected List<String> lista_hosts_guardado=new ArrayList<>();//(remoteIp)no tengo claro si lo necesito       
        

// inserta en la tabla ususario y en la tabla usu_tiene_rol, 
//el email =Unique K
// se add al listado de logueados
    public boolean insertar_newUsuario(Usuario usuario, Socket socket) {//ok
        //System.out.println("---"+usuario.toString());
        boolean registerOk=false;
       try {
            this.conexion =  Conexion.getConnection();
            this.sql =str_INSERT_USUARIO;
            this.pre_state=conexion.prepareCall(sql);
            this.pre_state.setString(1, usuario.getNombre());
            this.pre_state.setString(2,  usuario.getNick());
            this.pre_state.setString(3, usuario.getEmail());
            this.pre_state.setString(4,  usuario.getPw());
            
             if(!pre_state.execute()){
                 System.out.println(" Fila insertada");
                 int cod_usu = 0;
               
                 sql=str_SELECT_IdUSER_ByEMAIL;//"select cod_usuario from usuario where email=?";
               //  cod_usu=obtenerId_usuario_conEmail(usuario.getEmail());
                 this.pre_state=conexion.prepareCall(sql);
                 pre_state.setString(1, usuario.getEmail());
                    rs=pre_state.executeQuery();
                         while(rs.next()){
                         cod_usu=rs.getInt("cod_usuario");
                         }
                 
                 boolean insertado=insertar_usuarioTieneRol(cod_usu, conexion);
                 
                 if(insertado)
                       registerOk=true;
                 
                InetAddress intAddress = socket.getInetAddress();
                String remoteIp = intAddress.getHostAddress();
                lista_hosts_guardado.add(remoteIp);
             }                
          
       } catch (SQLException ex) {
           System.out.println("clasC SQLException insertar usu "+ex.getMessage());
       }  finally{
            try {
                rs.close();
                pre_state.close();
            } catch (SQLException ex) {
                Logger.getLogger(Clase_compartida.class.getName()).log(Level.SEVERE, null, ex);
            }
       }  
       return registerOk;
    }        
    

    // devuelve boolean si se ha insertado or not
    // si no se inserta deberia hacer rollback-- de la insercion del usuario--PENDIENTE
    // hay q controlar si el email existe orNot existe
    private boolean insertar_usuarioTieneRol(int codigo, Connection conexion1) {
        boolean insertardo=false;
        int codigoUsario=codigo;
       // int codigo_control=0;
        System.out.println("Codigo usu "+codigoUsario);
        try {
             this.sql="Insert into usuaria_tiene_rol(cod_usu, id_rol) VALUES (?, ( Select id_rol from rol where dominio='abogado'))";
             this.pre_state=conexion.prepareCall(sql);
             this.pre_state.setInt(1, codigoUsario);
             if(!pre_state.execute()){
                 insertardo=true;
             }
             pre_state.close();
        } catch (SQLException ex) {
            System.out.println(" SQLException - insertar_usuarioTieneRol "+ex.getMessage());
           
        }
        return insertardo;
 }    
    
    //---------------FIN INSERTAR NEW USUARIO Y USUARIO TIENE ROL-------------------------
    
    public String getUsuarios() {
         String salida="";
        this.sql=str_SELECT_USUARIOS;
        try {
            this.conexion =  Conexion.getConnection();
            this.state=this.conexion.createStatement();
            this.rs=this.state.executeQuery(this.sql);
            while(rs.next()){
                 salida+=rs.getString("cod_usuario")+SEPARADOR;
                 salida+=rs.getString("nombre_usuario")+SEPARADOR;
                 salida+=rs.getString("email")+SEPARADOR;
                 salida+=rs.getString("nick")+PUNTO_Y_COMA;
            }
            rs.close();
            state.close();
        } catch (SQLException ex) {
            System.out.println(" SQLException - getUsuarios "+ex.getMessage());
        }
        return salida;
    }
    
    
    public int dameId_usuario_conNick(String nick) {
           int id=0;
           this.sql=str_SELECT_IdUSUARIOS_ByNICK;
           try {
             this.conexion =  Conexion.getConnection();
             this.pre_state= conexion.prepareStatement(sql);

             this.pre_state.setString(1,nick);
             ResultSet rs=null;
             rs=pre_state.executeQuery();
            
            while(rs.next())
                id=rs.getInt("codigo");
            
            rs.close();
            pre_state.close();
           } catch (Exception e) {
               System.out.println(" SQLException - dameId_usuario_conNick "+e.getMessage());
           }
            
           return id;
       }
           
        
          private int obtenerId_usuario_conEmail(String email) {//0-
            int id_usuario=0;
            try {
                this.conexion =  Conexion.getConnection();
                this.sql=str_SELECT_IdUSER_ByEMAIL;
                this.pre_state= conexion.prepareStatement(sql);
                this.pre_state.setString(1, email.trim());
                ResultSet rs=null;
                rs=pre_state.executeQuery();
                while(rs.next()){
                   id_usuario=rs.getInt("cod_usuario");
                }  
             rs.close();
             pre_state.close();    
            } catch (SQLException ex) {
                System.out.println(" SQLException - obtenerId_usuario_conEmail "+ex.getMessage());
            }    
                
           return id_usuario;
          }
              
        // mirar en la bd si el email corresponde a un usuario
    // si corresponde --> se abre otro frag/ventana swing
    public boolean existeEmail_usuario(String email) {
        boolean existeEmail=false;
        try {
            this.conexion =  Conexion.getConnection();
            this.sql=str_SELECT_USUARIOS_ByEMAIL;
            this.pre_state= conexion.prepareStatement(sql);
            this.pre_state.setString(1, email.trim());
            ResultSet rs=null;
            rs=pre_state.executeQuery();
            while(rs.next()){
                existeEmail=true;
            } 
          rs.close();
          pre_state.close();   
        } catch (SQLException ex) {
            System.out.println(" SQLException - existeEmail_usuario "+ex.getMessage());
        }
        return existeEmail;
    }
      

 public String cambiarPwCliente(String email, String new_pw) {
        boolean changePwOk=false;
        String salida= PW_CAMBIADO_NOTOK+SEPARADOR;;
       if(existeEmail_usuario(email)){ // se comprueba nuevamente
            try {
            this.conexion =  Conexion.getConnection();
            this.sql=str_UPDATE_PW;
            this.pre_state= conexion.prepareStatement(sql);
            this.pre_state.setString(1, new_pw.trim());
            this.pre_state.setString(2, email.trim());
          
            int n=pre_state.executeUpdate();
            if(n>=1){
              // changePwOk=true;
               salida=PW_CAMBIADO_OK+SEPARADOR;
            }
        pre_state.close();
        } catch (SQLException ex) {
            System.out.println(" SQLException - getUsuario_x_email2 "+ex.getMessage());
        }
     
       }
       return  salida;
              
 }

 
     public boolean comprobarLogin(String nick, String pw) {
        // miro si existe ese nick y esa pw
        System.out.println("NICK "+nick+" PW "+pw);
        boolean existeNick_yPw=false;
        this.sql=str_SELECT_USUARIOS_ByNICK_PW;
        try {
            this.conexion =  Conexion.getConnection();
            this.pre_state= conexion.prepareStatement(sql);
            this.pre_state.setString(1, nick.trim());
            this.pre_state.setString(2, pw.trim());
            ResultSet rs=null;
            rs=pre_state.executeQuery();
            while(rs.next()){
              existeNick_yPw=true;
            }
            
            rs.close();
            pre_state.close();
        } catch (Exception e) {
        }
        
        System.out.println("existe PW "+existeNick_yPw);
        return existeNick_yPw;
    }

   
     
      

    public String deleteUsuario(String id) {
        String salida=NOT_OK+SEPARADOR+"No se ha eliminado";
             id_usuario=Integer.parseInt(id);
            String sql=str_DELETE_USUARIO;
         try {
            this.conexion =  Conexion.getConnection();
            this.pre_state= conexion.prepareStatement(sql);

            this.pre_state.setInt(1, id_usuario);

            if(!pre_state.execute()){
                    salida=OK+SEPARADOR+"Usuario con codigo "+id_usuario+" Eliminado";
                }
            pre_state.close();
        } catch (Exception e) {
        }
        return salida;    
    }

    public String updateBy_Id(String mensaje) {//UPDATE:id:name:nick:email;
          String [] vstr=mensaje.split(SEPARADOR);
          String salida=NOT_OK+SEPARADOR+"No se ha actualizado";
          id_usuario=Integer.parseInt(vstr[1]);
          String nombre=vstr[2];
          String nick=vstr[3];
          String email=vstr[4];
          
         String sql=str_UPDATE_USUARIO;
         try {
            this.conexion =  Conexion.getConnection();
            this.pre_state= conexion.prepareStatement(sql);
            this.pre_state.setString(1, nombre);
            this.pre_state.setString(2, email);
            this.pre_state.setString(3, nick);
            this.pre_state.setInt(4, id_usuario);

            if(!pre_state.execute()){
                    salida=OK+SEPARADOR+"Usuario con codigo "+id_usuario+" actualizado";
                }
            pre_state.close();
        } catch (Exception e) {
        }
        return salida;  
    }
}
