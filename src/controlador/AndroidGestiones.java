/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import java.net.InetAddress;
import java.net.Socket;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.Conexion;
import static modelo.ProtocoloServer.COMENTARIO;
import static modelo.ProtocoloServer.EMAIL;
import static modelo.ProtocoloServer.NOMBRE;
import static modelo.ProtocoloServer.PUNTO_Y_COMA;
import static modelo.ProtocoloServer.SEPARADOR;
import static modelo.ProtocoloServer.SEPARADOR_dats;
import static modelo.ProtocoloServer.TELEFONO;
import static modelo.ProtocoloServer.URL;
import modelo.objetos.Usuario;

/**
 *
 * @author sinNombre
 */
class AndroidGestiones {
    private Connection conexion=null;
    private PreparedStatement pre_state;
    private ResultSet rs;
    private int id_usuario;
     private String sql=null;
    private Statement state;
    boolean registerOk=false;
    
    
    //-------------------------INSERTAR ESTAFA
       //
       public boolean addEstafa(String mensaje) {
        String nick="";
        String titulo="";String categoria=""; String comment="";String fecha;// campos seguros
        String nombreE=""; String tel="";String email="";String url="";// campos variados
        
        List<String> listaTagsCheckboxs=new ArrayList<>();// se almacenan los checkboxs ?valor:?valor:
        String [] vStr=mensaje.split(SEPARADOR);// descompone mensaje
        Map<String,String>mimap=new TreeMap<>(); // NOMBRE¿valor
        titulo=vStr[1];
        categoria=vStr[2];
        comment=vStr[3];
        fecha=vStr[vStr.length-1];
        
        nick=vStr[vStr.length-2];
           
        boolean registrado=false;
        
        String [] nombreValor=new String[2];
        for(String s : vStr){
           
            if(!s.equals("")|| s!=null){
                 System.out.println("CC-addEstafa "+s);
                
                 if(s.startsWith("?")){
                listaTagsCheckboxs.add(s.substring(1));
            }
            if(s.startsWith(NOMBRE)){
                 nombreValor=  s.split(SEPARADOR_dats); 
                 nombreE=nombreValor[1];
                 mimap.put(NOMBRE, nombreE);
            }
            if(s.startsWith(TELEFONO)){
                 nombreValor=  s.split(SEPARADOR_dats); 
                 tel=nombreValor[1];
                 mimap.put(TELEFONO, tel);
            }
            if(s.startsWith(EMAIL)){
                 nombreValor=  s.split(SEPARADOR_dats); 
                 email=nombreValor[1];
                 mimap.put(EMAIL, email);
            }
            if(s.startsWith(URL)){
                 nombreValor=  s.split(SEPARADOR_dats); 
                 url=nombreValor[1];
                 mimap.put(URL, url);
            }
            }     
        }
        
        System.out.println(titulo+"  "+categoria);
        System.out.println(listaTagsCheckboxs.toString());
        System.out.println(" ");
        System.out.println(""+mimap);
        int id_estafador=0;int id_estafa=0;int id_usuario=0;
        
        //0-obtener el id del usuario
        //id_usuario=obtenerId_usuario(email_usuario);
         id_usuario=dameId_usuario(nick);
        
        if(id_usuario!=0){
            // 1-insertar en estafador y obtener id-OK
        id_estafador=insertarTabla_estafador("observaciones");
           
        if(id_estafador!=0){
              // 2-insertar en estafador_tiene_tipo_dato, le paso el map 
              insertarTabla_perfil_estafador_tieneDatos(id_estafador,mimap);
                
              //3-insertar ESTAFA
              id_estafa=insertarEstafa(titulo, comment,fecha, id_usuario, categoria, id_estafador);
              System.out.println("ID_estafa "+id_estafa);
              if(id_estafa!=0){
                  //4- insertar estafa_tiene_tag- le paso la lista checkboxs
                  insertarEstafa_tieneTags( listaTagsCheckboxs, id_estafa); 
              }
                
           }
           System.out.println("ID_ESTAFADOR "+id_estafador);
            
        }
        if(id_estafa!=0 && id_estafador!=0 && id_usuario!=0)
            return true;
        else
            return false;
     }
    
    private int obtenerId_usuario(String email) {//0-
            int id_usuario=0;
            try {
                this.conexion =  Conexion.getConnection();
                this.sql="SELECT cod_usuario FROM usuario WHERE email=?";
                this.pre_state= conexion.prepareStatement(sql);
                this.pre_state.setString(1, email.trim());
                ResultSet rs=null;
                rs=pre_state.executeQuery();
                while(rs.next()){
                   id_usuario=rs.getInt("cod_usuario");
                }        
            } catch (SQLException ex) {
                System.out.println(" SQLException - getUsuario_x_email2 "+ex.getMessage());
            }    
                
           return id_usuario;
          }
       
      private int insertarTabla_estafador(String observacion) {//1-OK
            int id_estafador=0;
        try {
            CallableStatement callableState;
          
            String plsql="{call insertPerfilEstafador(?,?)}";
            callableState=conexion.prepareCall(plsql);
            callableState.setString(1, observacion);
            callableState.registerOutParameter(2, java.sql.Types.INTEGER);
            callableState.execute();
            id_estafador=callableState.getInt(2);
            
            callableState.close();
                    
        } catch (SQLException ex) {
            Logger.getLogger(Clase_compartida.class.getName()).log(Level.SEVERE, null, ex);
        }
         return id_estafador;
     }
      
    private void insertarTabla_perfil_estafador_tieneDatos(int id, Map<String, String> mimap) {  //2-ok

        for(Map.Entry entry:mimap.entrySet()){
            try {
                String nombre=entry.getKey().toString();
                String valor=entry.getValue().toString();
                String sql="INSERT INTO PERFIL_ESTAFADOR_TIENE_DATO(ID_ESTAFADOR, ID_TIPO_DATOS, VALOR)  "
                        + " VALUES (?, "
                        + "( select id_tipo from tipo_dato where valor=?),?)";
                 this.pre_state= conexion.prepareStatement(sql);
                 this.pre_state.setInt(1, id);
                 this.pre_state.setString (2, nombre);
                this.pre_state.setString (3, valor);
                if(! this.pre_state.execute()){
                    System.out.println("Done ");
                }
                
               this.pre_state.close();
            } catch (SQLException ex) {
                System.out.println("SQLException "+ex.getMessage());
            }
             
         }  
    }



            //call insertarEstafa("T1","miCom","2020-01-01",6,"otros",21, @id);
            //select @id; 
    private int insertarEstafa(String titulo, String comment, String fecha, int idUsu, String categoria, int id_estafador) {//3-OK
            int cod_ususario=0;
             try {
            CallableStatement callableState;
          
            String plsql="{call insertarEstafa(?,?, ?,?,?,?,?)}";
            callableState=conexion.prepareCall(plsql);
            callableState.setString(1, titulo);
             callableState.setString(2, comment);
              callableState.setString(3, fecha);
               callableState.setInt(4, idUsu);
                callableState.setString(5, categoria);
                 callableState.setInt(6, id_estafador);
            callableState.registerOutParameter(7, java.sql.Types.INTEGER);
            callableState.execute();
            cod_ususario=callableState.getInt(7);
            
            callableState.close();
                    
        } catch (SQLException ex) {
            Logger.getLogger(Clase_compartida.class.getName()).log(Level.SEVERE, null, ex);
        }
            
            return cod_ususario;
    }
    
     private void insertarEstafa_tieneTags(List<String> listaTagsCheckboxs, int id_estafa) {//4-OK
         //id_tag, id_estafa    
         for(String tag : listaTagsCheckboxs){
                  try {

                  String sql="INSERT INTO estafa_tiene_tag(ID_TAG, id_estafa) VALUES"
                               + " ((SELECT ID_TAG FROM TAGS WHERE NOMBRE=?), ?)";
                 
                pre_state= (PreparedStatement)conexion.prepareStatement(sql);
                pre_state.setString(1, tag);
                pre_state.setInt(2, id_estafa);
                if(!pre_state.execute()){
                    System.out.println("Done ");
                }
                
                pre_state.close();
            } catch (SQLException ex) {
                System.out.println("SQLException "+ex.getMessage());
            }     
     } 
     }
   
  
     
     
     
     
     
     //-1-OK
   public String getLista_estafas() {
         String salida="";
         
        String sql="select e.id_estafa,titulo,fecha, descripcion, c.nombre as categoria "
                + " from estafa e join  categoria c on e.id_category=c.id_category";

        try {
            this.conexion =  Conexion.getConnection();
            this.state=this.conexion.createStatement();
            ResultSet rs=this.state.executeQuery(sql);
            while(rs.next()){
              salida+=Integer.toString(rs.getInt("id_estafa"))+SEPARADOR;
              salida+=rs.getString("titulo")+SEPARADOR;
              salida+=rs.getString("fecha")+SEPARADOR;
              salida+=rs.getString("descripcion")+SEPARADOR;
              salida+=rs.getString("categoria")+SEPARADOR;
              salida+=";";
            }
            
        } catch (SQLException ex) {
            System.out.println(" SQLException - getCategories "+ex.getMessage());
        }
        return salida;
      }
   
   
         //2-ok select nombre from tags t ,estafa_tiene_tag e where t.id_tag=e.id_tag and id_estafa=15
   public String getDetail_estafas(String str_id) {
        String salida="";
        int id=Integer.parseInt(str_id);
        System.out.println("----- "+id);
        
        String sql="select t.valor as valor , t.id_tipo as clave from estafa e join perfil_estafador p join perfil_estafador_tiene_dato pp join tipo_dato t " +
                   "on e.id_estafador=p.id_estafador AND  " +
                   "p.id_estafador=pp.id_estafador AND " +
                   "t.id_tipo=pp.id_tipo_datos and id_estafa=?";
        
        try {
            this.conexion =  Conexion.getConnection();
            
            this.pre_state= conexion.prepareStatement(sql);
            this.pre_state.setInt(1, id);

            ResultSet rs=null;
            rs=pre_state.executeQuery();

            while(rs.next()){
              int id_tipo=rs.getInt("clave");
              String str_tipo="";
              String valor=rs.getString("valor");
                System.out.println("Id tipo "+id_tipo+ " :: "+valor);
                switch (id_tipo) {
                    case 1://nombre
                        str_tipo=NOMBRE;
                        break;
                      case 2://email
                         str_tipo=EMAIL;
                        break;
                      case 3://tel
                         str_tipo=TELEFONO;
                        break;
                      case 4://url
                         str_tipo=URL;
                        break;    
                    default:
                        throw new AssertionError();
                }
              
              salida+=str_tipo+SEPARADOR_dats+valor+PUNTO_Y_COMA;
            }
 //          rs.close();
//          conexion.close();
         
        } catch (SQLException ex) {
            System.out.println(" SQLException - getDetail "+ex.getMessage());
        }
        return salida;
   }
   
   //3-ok
      public String addComment_estafa(int id_estafa, String comentario, String fecha, String nick) {
        String salida=COMENTARIO+SEPARADOR;
        // obtener id_usuario con nick
        int id_usuario=dameId_usuario(nick);
        
        String sql="INSERT into comentario_publico (id_estafa, descripcion, cod_usu,fecha) values(?,?,?,?)";
         try {
            this.conexion =  Conexion.getConnection();
            this.pre_state= conexion.prepareStatement(sql);

            this.pre_state.setInt(1, id_estafa);
            this.pre_state.setString(2, comentario);
            this.pre_state.setInt(3, id_usuario);
            this.pre_state.setString(4, fecha.toString());

            if(!pre_state.execute()){
                    salida+="Gracias por su comentario";
                }
        } catch (Exception e) {
        }
        return salida;
       }
   

   
   // llamada al insertar una estafa
          private int dameId_usuario(String nick) {
           int id=0;
           String sql="SELECT cod_usuario as codigo from usuario where nick=? limit 1";
           try {
             this.conexion =  Conexion.getConnection();
             this.pre_state= conexion.prepareStatement(sql);

             this.pre_state.setString(1,"n");
             ResultSet rs=null;
             rs=pre_state.executeQuery();
            
            while(rs.next())
                id=rs.getInt("codigo");
            
           } catch (Exception e) {
           }
            
           return id;
       }
}
