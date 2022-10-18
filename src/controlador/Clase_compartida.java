package controlador;

import java.net.InetAddress;
import modelo.Usuario;

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
import modelo.EnviarEmail;

import static modelo.ProtocoloServer.*;

/**
 * created by sinNombre on dic., 27/12/2021
 */
public class Clase_compartida {
    private Connection conexion=null;
    private PreparedStatement pre_state;
     private ResultSet rs;
     private int id_usuario;
    
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
    

//---------INSERTAR NEW USUARIO- return boolean
// inserta en la tabla ususario y en la tabla usu_tiene_rol, 
//el email =Unique K
// se add al listado de logueados
    public boolean insertar_newUsuario(Usuario usuario, Socket socket) {//ok
        //System.out.println("---"+usuario.toString());
        boolean registerOk=false;
       try {
           this.conexion =  Conexion.getConnection();
            this.sql = "Insert into usuario(nombre_usuario,nick,  email, password)"
                    + " values(?,?,?,?) ";
            this.pre_state=conexion.prepareCall(sql);
            this.pre_state.setString(1, usuario.getNombre());
            this.pre_state.setString(2,  usuario.getNick());
            this.pre_state.setString(3, usuario.getEmail());
            this.pre_state.setString(4,  usuario.getPw());
            
             if(!pre_state.execute()){
                 System.out.println(" Fila insertada");
                 int cod_usu = 0;
               
                 sql="select cod_usuario from usuario where email=?";
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
        pre_state.close();
           
       } catch (SQLException ex) {
           System.out.println("clasC SQLException insertar usu "+ex.getMessage());
       }  finally{
            try {
                pre_state.close();
            } catch (SQLException ex) {
                Logger.getLogger(Clase_compartida.class.getName()).log(Level.SEVERE, null, ex);
            }
       }  
       return registerOk;
    }

    // devuelve boolean si se ha insertado or not
    // si no se inserta deberia hacer rollback-- de la insercion del usuario
    // hay q controlar si el email No existe
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
        } catch (SQLException ex) {
            System.out.println(" SQLException - insertar_usuarioTieneRol "+ex.getMessage());
           
        }
        return insertardo;
 }    
    
    //---------------FIN INSERTAR NEW USUARIO Y USUARIO TIENE ROL-------------------------


    //-------------------------INSERTAR ESTAFA
    
           
       
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
   
    
    //---------------fin insertar Estafa
    
    

    // mirar en la bd si el email corresponde a un usuario
    // si corresponde --> se abre otro frag/ventana swing
    public boolean existeEmail_usuario(String email) {
        boolean existeEmail=false;
        try {
            this.conexion =  Conexion.getConnection();
            this.sql="SELECT * FROM usuario WHERE email=?";
            this.pre_state= conexion.prepareStatement(sql);
            this.pre_state.setString(1, email.trim());
            ResultSet rs=null;
            rs=pre_state.executeQuery();
            while(rs.next()){
                existeEmail=true;
            }        
        } catch (SQLException ex) {
            System.out.println(" SQLException - getUsuario_x_email2 "+ex.getMessage());
        }
        return existeEmail;
    }
    
        
    public String cambiarPwCliente(String email, String new_pw) {
        boolean changePwOk=false;
        String salida= PW_CAMBIADO_NOTOK+SEPARADOR;;
       if(existeEmail_usuario(email)){ // se comprueba nuevamente
            try {
            this.conexion =  Conexion.getConnection();
            this.sql="UPDATE usuario set password=? where email=?";  
            this.pre_state= conexion.prepareStatement(sql);
            this.pre_state.setString(1, new_pw.trim());
            this.pre_state.setString(2, email.trim());
            ResultSet rs=null;
            int n=pre_state.executeUpdate();
                System.out.println("--n  "+n);
           if(n>=1){
              // changePwOk=true;
               salida=PW_CAMBIADO_OK+SEPARADOR;
           }
      
        } catch (SQLException ex) {
            System.out.println(" SQLException - getUsuario_x_email2 "+ex.getMessage());
        }
     
       }
       return  salida;
              
 }

    
    public boolean comprobarLogin2(String nick, String pw) {
        // miro si existe ese nick y esa pw
        System.out.println("NICK "+nick+" PW "+pw);
        boolean existeNick_yPw=false;
        this.sql="SELECT* FROM usuario where nick=? and password=?";
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
        } catch (Exception e) {
        }
        
        System.out.println("existe PW "+existeNick_yPw);
        return existeNick_yPw;
    }
    
    
        public String getCategories() {
        String salida="";
        this.sql="SELECT* FROM CATEGORIA"; 
        try {
            this.state=this.conexion.createStatement();
            ResultSet rs=this.state.executeQuery(this.sql);
            while(rs.next()){
              salida+=rs.getString("nombre")+SEPARADOR;
            }
            
        } catch (SQLException ex) {
            System.out.println(" SQLException - getCategories "+ex.getMessage());
        }
        return salida;
    }
    
    // get Tags para rellenar el spinner
       public String getTags() {
        String salida="";
        this.sql="SELECT* FROM tags"; 
        try {
            this.conexion =  Conexion.getConnection();
            this.state=this.conexion.createStatement();
            ResultSet rs=this.state.executeQuery(this.sql);
            while(rs.next()){
              salida+=rs.getString("nombre")+SEPARADOR;
            }
            
        } catch (SQLException ex) {
            System.out.println(" SQLException - getTags "+ex.getMessage());
        }
        return salida;
       }
       
      public String getTagsBy(String id_estafa) {
           String salida="";
          int id=Integer.parseInt(id_estafa);
           
            this.sql="SELECT t.nombre FROM estafa_tiene_tag e join tags t ON " +
                        "e.id_tag=t.id_tag where id_estafa=?"; 
            try {
               this.pre_state= conexion.prepareStatement(sql);
               this.pre_state.setInt(1, id);

               ResultSet rs=null;
               rs=pre_state.executeQuery();

                while(rs.next()){
                   salida+="#"+rs.getString("nombre")+"";
                }

            } catch (SQLException ex) {
                System.out.println(" SQLException - getTagsBy "+ex.getMessage());
            }
        return salida;
           }

           //-----------------Fin get Tags
       
       
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


      //select nombre from tags t ,estafa_tiene_tag e where t.id_tag=e.id_tag and id_estafa=15
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
   
   
   
   //INSERT into comentario_publico (id_estafa, descripcion, cod_usu,fecha) values(24, 
                //"stoy hasta el coño", 1, "2021/01/01")
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
   
           public String getComentarios_by(int id) {
                String salida="";
                 String sql="SELECT fecha, descripcion,id_estafa, nick " +
                "from comentario_publico c JOIN usuario u ON u.cod_usuario=c.cod_usu and id_estafa=?";
           try {
             this.conexion =  Conexion.getConnection();
             this.pre_state= conexion.prepareStatement(sql);

             this.pre_state.setInt(1,id);
             ResultSet rs=null;
             rs=pre_state.executeQuery();
            
            while(rs.next()){
                salida+=rs.getString("fecha")+SEPARADOR;
                salida+=rs.getString("descripcion")+SEPARADOR;
                salida+=rs.getString("nick");
                salida+=PUNTO_Y_COMA;
            }
               System.out.println("salida---->"+salida);
           } catch (Exception e) {
           }
            
                return salida;
           }
           
           
  // ACTION1 ES ESCRITORIO          
    public String getUsuario() {
         String salida="";
        this.sql="SELECT* FROM usuario"; 
        try {
            this.conexion =  Conexion.getConnection();
            this.state=this.conexion.createStatement();
            ResultSet rs=this.state.executeQuery(this.sql);
            while(rs.next()){
                 salida+=rs.getString("cod_usuario")+SEPARADOR;
                 salida+=rs.getString("nombre_usuario")+SEPARADOR;
                 salida+=rs.getString("email")+SEPARADOR;
                 salida+=rs.getString("nick")+PUNTO_Y_COMA;
            }
            rs.close();
        } catch (SQLException ex) {
            System.out.println(" SQLException - getTags "+ex.getMessage());
        }
        return salida;
    }

   public String getInformacionEstafador() {
     String salida="";
        this.sql="SELECT CONCAT(group_concat(p2.valor),\".\") as datos, p2.id_estafador as id " +
                    "FROM perfil_estafador p join perfil_estafador_tiene_dato p2 " +
                    "on p.id_estafador=p2.id_estafador " +
                    "group by  p.id_estafador"; 
        try {
            this.conexion =  Conexion.getConnection();
            this.state=this.conexion.createStatement();
            ResultSet rs=this.state.executeQuery(this.sql);
            while(rs.next()){
                 salida+=rs.getString("datos")+SEPARADOR;
                 salida+=rs.getString("id")+PUNTO_Y_COMA;
            }
            rs.close();
        } catch (SQLException ex) {
            System.out.println(" SQLException - getTags "+ex.getMessage());
        }
        return salida;
   }
   
    public String getComentarios() {
        String salida="";
        this.sql="SELECT e.id_estafa as ID, c.descripcion as Comment, u.nick as nick from estafa e join comentario_publico c " +
                    "join usuario u  " +
                    "on c.id_estafa=e.id_estafa AND " +
                    "u.cod_usuario=c.cod_usu"; 
        try {
            this.conexion =  Conexion.getConnection();
            this.state=this.conexion.createStatement();
            ResultSet rs=this.state.executeQuery(this.sql);
            while(rs.next()){
                 salida+=rs.getString("ID")+SEPARADOR;
                  salida+=rs.getString("Comment")+SEPARADOR;
                 salida+=rs.getString("nick")+PUNTO_Y_COMA;
            }
            rs.close();
        } catch (SQLException ex) {
            System.out.println(" SQLException - getTags "+ex.getMessage());
        }
        return salida;
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

    public String getTagsAction4() {
    String salida="";
        this.sql="SELECT* FROM tags"; 
        try {
            this.conexion =  Conexion.getConnection();
            this.state=this.conexion.createStatement();
            ResultSet rs=this.state.executeQuery(this.sql);
            while(rs.next()){
              salida+=rs.getInt("id_tag")+SEPARADOR;
              salida+=rs.getString("nombre")+PUNTO_Y_COMA;
            }
            
        } catch (SQLException ex) {
            System.out.println(" SQLException - getTags "+ex.getMessage());
        }
        return salida;
    }

    public String deleteBy(String id) {
        String salida="No se ha eliminado";
             id_usuario=Integer.parseInt(id);
            String sql="Delete from usuario where cod_usuario=?";
         try {
            this.conexion =  Conexion.getConnection();
            this.pre_state= conexion.prepareStatement(sql);

            this.pre_state.setInt(1, id_usuario);

            if(!pre_state.execute()){
                    salida="Usuario con codigo "+id_usuario+" Eliminado";
                }
        } catch (Exception e) {
        }
        return salida;    
    }

    public String updateBy(String mensaje) {//UPDATE:109:ana:n3:55
        String [] vstr=mensaje.split(SEPARADOR);
        String salida="No se ha actualizado";
          id_usuario=Integer.parseInt(vstr[1]);
          String nombre=vstr[1];
          String nick=vstr[2];
          String email=vstr[2];
          
          String sql="Update usuario set nombre_usuario=? , email=? , nick=? where cod_usuario=?";
         try {
            this.conexion =  Conexion.getConnection();
            this.pre_state= conexion.prepareStatement(sql);
            this.pre_state.setString(1, nombre);
            this.pre_state.setString(2, email);
            this.pre_state.setString(3, nick);
            this.pre_state.setInt(4, id_usuario);

            if(!pre_state.execute()){
                    salida="Usuario con codigo "+id_usuario+" actualizado";
                }
        } catch (Exception e) {
        }
        return salida;  
    }

    public String addTag(String mensaje) {//ADDTAG:tag
         String [] vstr=mensaje.split(SEPARADOR);
         String salida="No se ha actualizado";

         String sql="Insert into Tags(nombre) value(?)";
         try {
            this.conexion =  Conexion.getConnection();
            this.pre_state= conexion.prepareStatement(sql);
            this.pre_state.setString(1, vstr[1]);

            if(!pre_state.execute()){
                    salida="Tag insertada";
                }
        } catch (Exception e) {
        }
        return salida;
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
                   salida= "Email enviado";
            }else{
                 salida= "Error al enviar el email";
            }
           } catch (Exception e) {
               System.out.println(" Exception  enviar email "+e.getMessage());
               salida= "Error al enviar el email";
           }
        return  salida;
    }


}

    
