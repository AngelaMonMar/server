/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import modelo.objetos.Usuario;
import controlador.Clase_compartida;

import controlador.ControladorServidor;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static modelo.ProtocoloServer.*;
import modelo.daos.CategoryDao;
import modelo.daos.CommentDao;
import modelo.daos.EstafaDao;
import modelo.daos.EstafadorDao;
import modelo.daos.TagDao;
import modelo.daos.UsuarioDao;

/**
 *
 * @author sinNombre
 */
public class MS_hiloDelCliente extends Thread {
    private ControladorServidor controlador;
    private final int PUERTO = 19999;
    
    private String ESTADO;

    private Socket socket;
    private final ServerSocket serverSocket;

    private BufferedReader br;
    private BufferedWriter bw;
    private int contador=1; // para contar los fallos al loguear

    private Clase_compartida clase_compartida;
    private Usuario usuario;
    private UsuarioDao usuarioDao=new UsuarioDao();
    private CommentDao commentDao=new CommentDao();
    private EstafadorDao estafadorDao=new EstafadorDao();
    private EstafaDao estafaDao=new EstafaDao();
    private TagDao tagDao=new TagDao();
    private CategoryDao categoryDao=new CategoryDao();
   
    public MS_hiloDelCliente(ServerSocket ss, Clase_compartida cc ){
         this.serverSocket =ss;
         this.clase_compartida=cc;
         this.ESTADO=ESTADO_PETICION;
    }

    public void setControlador(ControladorServidor controlador){
        this.controlador = controlador;
    }

    
    public void acceptSocket(){//esperarAlCliente
        try {
            socket = serverSocket.accept();
            clase_compartida.listSocketAdd(socket);// add el socket cl al listado

        } catch (IOException ex) {
            Logger.getLogger(MS_hiloDelCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void crearFlujos(){
        try {
            //obtengo flujo entrada
            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            br = new BufferedReader(isr);

            //flujo salida
            OutputStream os = socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            bw = new BufferedWriter(osw);
        } catch (IOException ex) {
            Logger.getLogger(MS_hiloDelCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void enviarMensaje(String mensaje){
        try {
            bw.write(mensaje);
            bw.newLine();
            bw.flush();
        } catch (IOException ex) {
            Logger.getLogger(MS_hiloDelCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String recibirMensaje(){
        // if(clase_compartida.getLista_hosts_guardado().size()>0){
        try {
            String mensaje = br.readLine();
            System.out.println("MS mensaje "+mensaje);
            if(!mensaje.isEmpty()|| mensaje.getBytes().length>=1)
                return mensaje;
        } catch (IOException ex) {
            System.err.println("-------IOException ERROR recibirMensaje--- PENDIENTE CORREGIR "+ex.getMessage());
            
            
        }
        return "";
    }

    public int getPUERTO() {
        return PUERTO;
    }

    public Socket getSocket() {
        return socket;
    }
    

      String salida="";
    //servidor procesa la peticion recibido del cliente
    public void run(){
        String email=null;
        String fecha="";
        String str_id="";

        
        while(true){
             String mensaje = recibirMensaje();
//             System.out.println("mensaje.isEmpty() " +mensaje.isEmpty());
//              System.out.println(" isBlank " +mensaje.isBlank());
//               System.out.println(" equals " +mensaje.equals(""));
               
                
             if(!mensaje.isEmpty() || !mensaje.equals("")){
                 
                 controlador.server_vista_muestra_msg("MS Recibido del  cliente <---- "+mensaje);
                 System.out.println("MS :Recibido del  cliente -> "+mensaje);
          
                 String [] strSplit=mensaje.split(SEPARADOR);

                 //--- switch evalua la palabra protocol       
                 switch (strSplit[0]) {
                     
                   case ESTADO_CHECKBOX://--> la 1ª vez entra aqui!!ESTADO_CHECKBOX+SEPARADOR+remoteIp+SEPARADOR+CHECKBOX_REGISTRO_FALSE
                        String remoteIp = strSplit[1];
                        // System.out.println("-ESTADO_CHECKBOX???-- "+clase_compartida.getSet_strSockets_guardados().contains(remoteIp ));
                        ArrayList<String> al = (ArrayList<String>) clase_compartida.getSet_strSockets_guardados();

                        if (!al.isEmpty()) {
                            if (clase_compartida.map_socket_strSocket.values().contains(remoteIp)) {// ya se habia logueado antes
                                usuario = clase_compartida.getUsario_x_remoteIp(remoteIp);
                                System.out.println("MS ESTADO_CHECKBOX : " + usuario);
                                if (usuario.isCheckbox_register()) {
                                    this.salida=(ESTADO_CHECKBOX + SEPARADOR + CHECKBOX_REGISTRO_TRUE + SEPARADOR + usuario.getNombre());
                                } else {
                                      this.salida=(ESTADO_CHECKBOX + SEPARADOR + CHECKBOX_REGISTRO_FALSE + SEPARADOR + usuario.getNombre());
                                }

                            } else {
                                  this.salida=(ESTADO_CHECKBOX + SEPARADOR + CHECKBOX_REGISTRO_FALSE);
                            }
                        } else {
                               this.salida=(ESTADO_CHECKBOX + SEPARADOR + CHECKBOX_REGISTRO_FALSE);
                        }

                        break;

                    case LOGIN:// comprueba q existe, comprueba si se ha pulsado checkbox LOGIN:NOMBRE:PW
                        System.out.println("\tstrSplit"+strSplit.length);
                        if (strSplit[1].equals(SIN_DATOS)) {//== cuando hay datos erroneos en vista login
                              this.salida=(LOGIN_NOT_OK + SEPARADOR + " CAMPOS estan vacio " + contador);
                            contador++;

                        } else if (strSplit.length >= 4) {// es la primera vez q se loguea LOGIN:NICK:PW:CHECKBOX:ROL
                            String nick = strSplit[1];
                            String pw = strSplit[2];
                            boolean checkbox_isChecked = sePulsoCheckBox_registro(strSplit[3]);
                            String rol = strSplit[4];
                            System.out.println("\t"+nick+"\t"+pw+"\t"+rol);
                           // controlador.server_vista_muestra_msg("El cliente dice: " + nick + "\t pw " + pw + "\t" + checkbox_isChecked);


                            if (usuarioDao.comprobarLogin(nick, pw)) {// comprueba si existe registro

                                System.out.println(" esta activo la casilla " + checkbox_isChecked);
                                if (checkbox_isChecked) {// si checkbox fue activado
                                    clase_compartida.addStringSocket(this.getSocket().toString());// guarda el socket del cl
                                    this.salida=(LOGIN_OK + SEPARADOR + nick + SEPARADOR + CHECKBOX_REGISTRO_TRUE);// entra en frame menu

                                } else {
                                       this.salida=(LOGIN_OK + SEPARADOR + nick + SEPARADOR + CHECKBOX_REGISTRO_FALSE);// entra en login menu
                                }

                            } else {
                                   this.salida=(LOGIN_NOT_OK + SEPARADOR + "ELSE ERROR AL LOGUEAR " + contador);
                                contador++;
                            }

                        }
                        break;

                    case LOGIN_NOT_OK:
                        this.salida=(LOGIN_NOT_OK + SEPARADOR + " No han enviado datos " + contador);
                        contador++;
                        break;
                    case RECORDAR_DATOS_LOGIN:
                        System.out.println("Entro recordar RECORDAR_DATOS_LOGIN  ");
                        this.salida=(RECORDAR_DATOS_LOGIN + SEPARADOR + RECORDAR_DATOS_LOGIN);
                        break;
                    case REGISTER:// pulso boton_register en vistaLogin
                        this.salida=(REGISTER + SEPARADOR + " ....");
                        break;

                    case REGISTER_FORM: /// REGISTER_FORM : nombre : nick : email : password : ROL_APPCL
                        if (strSplit[1].equals(SIN_DATOS)) { // datos erroneos en __Server4App.vista register
                               this.salida=(REGISTER_FORM_NOT_OK + SEPARADOR + " CAMPOS formulario no pueden estar vacio ");

                        } else { // insercion delos datos registro en la bd- tabla usuario + tabla usu_tiene_rol
                            String nombre = strSplit[1];
                            String nick = strSplit[2];
                            email = strSplit[3];
                            String pw = strSplit[4];
                            controlador.server_vista_muestra_msg("\n<<CL dice: >>  " + nombre + "\t " + nick + "\t " + email + "\t " + pw);
                            //Pendiente  si crear Usuario con socket o poner un setSocket
                            usuario = new Usuario(nombre, nick, email, pw, this.getSocket());
                            
                            //llama M en la clase_compartida para guardar los datos cl+ el socket del mismo
                            //clase_compartida.addDatos_registroBD(usuario, this.getSocket());//prueba en lista
                            boolean isRegisterOk=usuarioDao.insertar_newUsuario(usuario, this.getSocket());
                            
             System.out.println("\n BOOLEAN SI SE HA INSERTADO ----"+isRegisterOk);
                            
                            if(isRegisterOk){
                               // clase_compartida.map_socket_usuario_register.put(this.getSocket(), usuario);// directamente al map
                                  this.salida=(REGISTER_FORM_OK + SEPARADOR + nick);
                            }else{
                                     this.salida=(REGISTER_FORM_NOT_OK + SEPARADOR + "Deben de introducir datos validos "); 
                            }
                        }
                        break;


                    case VOLVER:
                        this.salida=(VOLVER + SEPARADOR + VOLVER);
                        break;

                    case CHANGE_PW://CHANGE_PW:email:newPw-- pendiente en appescritorio
                        email = strSplit[1];
                        String newpw = strSplit[2];
                        String s = usuarioDao.cambiarPwCliente(email, newpw);
                        System.out.println("MS- string CHANGE_PW "+s);
                        this.salida=(s);
                        break;

                    // abre VistaJFrame_olvideContraseña-- android abre frag recuperarPW
                    case RECUPERAR_PW:
                        Usuario usuario = null;
                        email = strSplit[1];
                        try {
                            //usuario = clase_compartida.getUsuario_x_email(email);
                            boolean emailExiste= usuarioDao.existeEmail_usuario(email);
                            System.out.println("MS EMAIL "+email+" "+emailExiste);
                            
                            if (emailExiste) {// antes usuario != null    
                                System.out.println("ok");
                                   this.salida=(VOLVER_VISTA_LOGIN + SEPARADOR + "OK" + SEPARADOR );
                            } else {
                                System.out.println("nok");
                                  this.salida=(VOLVER_VISTA_LOGIN + SEPARADOR + "NOK" + SEPARADOR + "NO EXITS PW");
                            }
                        } catch (NullPointerException e) {
                            System.out.println("MS- NullPointerException" );
                               this.salida=(VOLVER_VISTA_LOGIN + SEPARADOR + "NOK" + SEPARADOR + " NO estan registrado.");
                        }

                        //     this.salida=(VOLVER_VISTA_LOGIN+SEPARADOR+"NADA");
//FALTA COMO IMPLEMENTAR ENVIAR UN EMAIL????
                        break;
                    case VOLVER_VISTA_LOGIN:
                        System.out.println("Pulso volver al login");
                           this.salida=(VOLVER_VISTA_LOGIN + SEPARADOR + "NOK" + SEPARADOR + "Introduzca su nick y contraseña");
                        break;

                        // solo  escritorio
                        //MENU usuario cases
                    case MENU_USUARIO:
                        String usuarios="";
                        usuarios=usuarioDao.getUsuarios();
                        this.salida=(!usuarios.equals(""))? (MENU_USUARIO + SEPARADOR +OK+SEPARADOR+ usuarios):(MENU_USUARIO + SEPARADOR + usuarios);
                        break;                  
                    case INSERT_USUARIO:// quitar, se hace desde menu
                        //System.out.println("INSERT_USUARIO "+mensaje);
                        respuesta=usuarioDao.updateBy_Id(mensaje);
                        this.salida=(DELETE_USUARIO+SEPARADOR+respuesta);
                        break; 
                    case UPDATE_USUARIO:
                       // System.out.println("UPDATE_USUARIO "+mensaje);
                        respuesta=usuarioDao.updateBy_Id(mensaje);//UPDATE:id:name:nick:email;
                        this.salida=(UPDATE_USUARIO+SEPARADOR+respuesta);
                        break; 
                    case DELETE_USUARIO:
                       // System.out.println("STOY DELETE_USUARIO "+mensaje);
                        respuesta=usuarioDao.deleteUsuario(strSplit[1]);
                        this.salida=(DELETE_USUARIO+SEPARADOR+respuesta);
                        break; 


                     case MENU_ESTAFADOR:
                        String estafadores="";
                        estafadores=estafadorDao.getInformacionEstafador();
                        this.salida=(!estafadores.equals(""))? (MENU_ESTAFADOR + SEPARADOR+OK+SEPARADOR + estafadores):(MENU_ESTAFADOR + SEPARADOR +NOT_OK);
                        break;  
  
                    case MENU_COMENTARIO:
                        String comentario_estafas="";
                        comentario_estafas=commentDao.getComentarios();
                        this.salida=(!comentario_estafas.equals(""))? (MENU_COMENTARIO + SEPARADOR +OK+SEPARADOR+ comentario_estafas):(MENU_COMENTARIO + SEPARADOR );
                        break;
                     case UPDATE_COMMENT:
                         respuesta=commentDao.updateComment(mensaje);
                         this.salida=UPDATE_COMMENT+SEPARADOR+respuesta;
                         break; 
                     case DELETE_COMMENT:
                       // System.out.println("STOY DELETE_USUARIO "+mensaje);
                        respuesta=commentDao.deleteComment(strSplit[1]);
                        this.salida=(DELETE_COMMENT+SEPARADOR+respuesta);
                        break;      
                    
                    // MENU TAG
                     case MENU_TAG:
                         System.out.println("MS MENU_TAG "+mensaje);
                        String etiketas=tagDao.getTags2();
                        this.salida=(!etiketas.equals(""))? (MENU_TAG + SEPARADOR+OK + SEPARADOR+etiketas):(MENU_TAG + SEPARADOR );
                        break; 
                    case INSERT_TAG:////(INSERT_TAG:Tag insertada
                        System.out.println("INSERT_TAG "+mensaje);
                        respuesta=tagDao.addTag(mensaje);
                        this.salida=(INSERT_TAG+SEPARADOR+respuesta);
                        break;     
                     case UPDATE_TAG:
                         respuesta=tagDao.updateTag(mensaje);
                         this.salida=UPDATE_TAG+SEPARADOR+respuesta;
                         break; 
                     case DELETE_TAG:
                       // System.out.println("STOY DELETE_USUARIO "+mensaje);
                        respuesta=tagDao.deleteTag(strSplit[1]);
                        this.salida=(DELETE_TAG+SEPARADOR+respuesta);
                        break;     
                        
                        
                    //MENU categoria    
                    case MENU_CATEGORIA:
                         System.out.println("MS MENU_CATEGORIA "+mensaje);
                        String categorias=categoryDao.getCategories2();//id:valor;
                        this.salida=(!categorias.equals(""))? (MENU_CATEGORIA + SEPARADOR+OK + SEPARADOR+categorias):(MENU_CATEGORIA + SEPARADOR );
                        break;
                    case INSERT_CATEGORIA:////(INSERT_TAG:Tag insertada
                        System.out.println("INSERT_cat "+mensaje);
                        respuesta=categoryDao.addCategory(mensaje);
                        this.salida=(INSERT_CATEGORIA+SEPARADOR+respuesta);
                        break;     
                     case UPDATE_CATEGORIA:
                         respuesta=categoryDao.updateCategory(mensaje);
                         this.salida=UPDATE_CATEGORIA+SEPARADOR+respuesta;
                         break; 
                     case DELETE_CATEGORIA:
                       // System.out.println("STOY DELETE_USUARIO "+mensaje);
                        respuesta=categoryDao.deleteCategory(strSplit[1]);
                        this.salida=(DELETE_CATEGORIA+SEPARADOR+respuesta);
                        break;     
 
                    // solo app android
                    case REGISTRAR_ESTAFA:// REGISTRAR_ESTAFA:
                        fecha = dameFecha();//sdf.format(date);
                        boolean estafaOk=estafaDao.addEstafa(mensaje+SEPARADOR+fecha);

                        if(estafaOk)
                               this.salida=(REGISTRAR_ESTAFA_OK+SEPARADOR);
                        else
                              this.salida=(REGISTRAR_ESTAFA_NOTOK+SEPARADOR);
                        
                        break;
                    case GET_LIST_ESTAFAS:
                        System.out.println("get GET_LIST_ESTAFAS");
                        String ss=estafaDao.getLista_estafas();
                        if(!ss.equals(""))
                                this.salida=(ss);
                        else
                               this.salida=("");
                        break;
                        
                    case GET_DETALLES_ESTAFAS:
                       // System.out.println("get GET_DETALLES_ESTAFAS "+mensaje);
                        str_id=strSplit[1];
                        System.out.println("MS detalles STRING ID "+str_id);
                        String details=estafaDao.getDetail_estafas(str_id);
                        System.out.println("get GET_DETALLES_ESTAFAS "+details);
                        
                        this.salida=(details);
                        break;  
                    
                    case GET_CONTADOR_VISTAS:// 1- update +1,  2-obtengo
                        str_id=strSplit[1];
                        //System.out.println("MS detalles STRING ID "+str_id);                       
                        estafaDao.addVisita(str_id);
                        
                        String contador=estafaDao.getContadorVisitas(str_id);
                        System.out.println(" GET_CONTADOR_VISTAS "+contador);
                        this.salida=(contador);
                        break;

                    case COMENTARIO:// COMENTARIO:'blabla':id_estafa:fecha:email_usuario
                        System.out.println("stoy en COMENTARIO\n"+mensaje);
                        String comentario=strSplit[1];
                        int id_estafa=Integer.parseInt(strSplit[2]);
                        fecha=strSplit[3];
                        String nick=strSplit[4];
                        String meterBd=commentDao.addComment_estafa(id_estafa, comentario, fecha, nick);

                        this.salida=(meterBd);
                        break;
                        
                    case GET_COMMENTS_ESTAFA://GET_COMMENTS_ESTAFA+SEPARADOR+ID_ESTAFA
                        System.out.println("MS GET_COMMENTS_ESTAFA\n\t"+mensaje);
                        int id=Integer.parseInt(strSplit[1]);
                        String comments=commentDao.getComentarios_by(id);
                        System.out.println("----comments "+comments);
                        this.salida=(comments);
                        break;
                                
                        
                    case GET_CATEGORIES:
                        System.out.println("STOY GET_CATEGORIES");
                        String str_categories=categoryDao.getCategories();
                        System.out.println("GET_CATEGORIES "+str_categories);
                        this.salida=(str_categories);
                        break;
  // PENDIENTE FILTRADO TAGS                      
                     case GET_TAGS:
                        System.out.println("\tLONGi "+strSplit.length+"\t");
                        if(strSplit.length==2){//GET_TAGS:str_id
                        String tags=tagDao.getTagsBy(strSplit[1]); 
                        System.out.println("\tSTOY tags BY NAME "+strSplit[1]+
                           "\t"+tags);
                              this.salida=(tags);
                         } else{//GET_TAGS:
                             String tags=tagDao.getTags();
                           System.out.println("TAGS NORMAL  "+tags); 
                                 this.salida=(tags);
                         }
                        break;
                        
                    
 
                        
                   
                    
 
                        
                    case CONTACTO:
                        System.out.println("CONTACTO  "+mensaje);
                        respuesta=clase_compartida.enviarEmail(mensaje);
                        System.out.println("respuesta "+respuesta);
                        this.salida=(CONTACTO+SEPARADOR+respuesta);
                        break; 

                    default:
                        System.out.println("No he recibido nada del cl-->WRONG_______!! \nPendiente arreglar cuando se desconecta ");
                        //throw new AssertionError();
                }//switch
                 
                 enviarMensaje(  this.salida);  // envia la salida
                 controlador.server_vista_muestra_msg("MS enviar al  cliente--> "+  this.salida+"\n");// muestra salida en vista server
                 
            }//else
        }//while true
    }
private String respuesta="";
    private boolean sePulsoCheckBox_registro(String checkBox_esPulsado) {
        return (checkBox_esPulsado.equals("true")) ? true : false;

    }
    
    private String dameFecha(){
           SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");
          String dateNow=df.format(new Date());
      System.out.println("fecha "+dateNow);
      return dateNow;
    }


    
    
    
}

 