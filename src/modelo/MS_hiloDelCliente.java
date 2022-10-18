/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import controlador.Clase_compartida;
import controlador.Clase_compartidaChatPublico;
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
    
    private Clase_compartidaChatPublico clasCompartidaChat=new Clase_compartidaChatPublico();

    private Clase_compartida clase_compartida;
    private Usuario usuario;

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
        try {
            String mensaje = br.readLine();
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
      
        
        while(true){
             String mensaje = recibirMensaje();
             if(mensaje!=null || mensaje.length()!=0){
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

                        } else if (strSplit.length == 4) {// es la primera vez q se loguea LOGIN:NICK:PW:CHECKBOX
                            String nick = strSplit[1];
                            String pw = strSplit[2];
                             System.out.println("\t"+nick+"\t"+pw);
                            boolean checkbox_isChecked = sePulsoCheckBox_registro(strSplit[3]);
                           // controlador.server_vista_muestra_msg("El cliente dice: " + nick + "\t pw " + pw + "\t" + checkbox_isChecked);


                            if (clase_compartida.comprobarLogin2(nick, pw)) {// comprueba si existe registro

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

                    case REGISTER_FORM:
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
                            boolean isRegisterOk=clase_compartida.insertar_newUsuario(usuario, this.getSocket());
                            
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
                        String s = clase_compartida.cambiarPwCliente(email, newpw);
                        System.out.println("MS- string CHANGE_PW "+s);
                        this.salida=(s);
                        break;

                    // abre VistaJFrame_olvideContraseña-- android abre frag recuperarPW
                    case RECUPERAR_PW:
                        Usuario usuario = null;
                        email = strSplit[1];
                        try {
                            //usuario = clase_compartida.getUsuario_x_email(email);
                            boolean emailExiste= clase_compartida.existeEmail_usuario(email);
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
                    case ACTION1:
                        String usuarios="";
                        usuarios=clase_compartida.getUsuario();
                        this.salida=(!usuarios.equals(""))? (ACTION1 + SEPARADOR +OK+SEPARADOR+ usuarios):(ACTION1 + SEPARADOR + usuarios);
                        break;

                     case ACTION2:
                        String estafadores="";
                        estafadores=clase_compartida.getInformacionEstafador();
                        this.salida=(!estafadores.equals(""))? (ACTION2 + SEPARADOR+OK+SEPARADOR + estafadores):(ACTION2 + SEPARADOR +NOT_OK);
                        break;  
                    case ACTION3:
                        String comentario_estafas="";
                        comentario_estafas=clase_compartida.getComentarios();
                        this.salida=(!comentario_estafas.equals(""))? (ACTION3 + SEPARADOR +OK+SEPARADOR+ comentario_estafas):(ACTION3 + SEPARADOR );
                        break;
                     case ACTION4:
                        String etiketas=clase_compartida.getTagsAction4();
                        this.salida=(!etiketas.equals(""))? (ACTION4 + SEPARADOR+OK + SEPARADOR+etiketas):(ACTION4 + SEPARADOR );
                        break;    
                     case ACTION5:
                        this.salida=(ACTION5 + SEPARADOR + " " );
                        break; 
                    // solo app android
                    case REGISTRAR_ESTAFA:// REGISTRAR_ESTAFA:
                        //System.out.println("MS stoy en estafa\n"+mensaje);
                        Date date=new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                        fecha = dameFecha();//sdf.format(date);
                        boolean estafaOk=clase_compartida.addEstafa(mensaje+SEPARADOR+fecha);

                        if(estafaOk)
                               this.salida=(REGISTRAR_ESTAFA_OK+SEPARADOR);
                        else
                              this.salida=(REGISTRAR_ESTAFA_NOTOK+SEPARADOR);
                        
                        break;
                    case GET_LIST_ESTAFAS:
                        System.out.println("get estafas");
                        String ss=clase_compartida.getLista_estafas();
                        if(!ss.equals(""))
                                this.salida=(ss);
                        else
                               this.salida=("");
                        break;
                        
                    case GET_DETALLES_ESTAFAS:
                       // System.out.println("get GET_DETALLES_ESTAFAS "+mensaje);
                        String str_id=strSplit[1];
                        System.out.println("MS detalles STRING ID "+str_id);
                        String details=clase_compartida.getDetail_estafas(str_id);
                        System.out.println("get GET_DETALLES_ESTAFAS "+details);
                        
                        this.salida=(details);
                        break;    

                    case COMENTARIO:// COMENTARIO:'blabla':id_estafa:fecha:email_usuario
                        System.out.println("stoy en COMENTARIO\n"+mensaje);
                        String comentario=strSplit[1];
                        int id_estafa=Integer.parseInt(strSplit[2]);
                        fecha=strSplit[3];
                        String nick=strSplit[4];
                        String meterBd=clase_compartida.addComment_estafa(id_estafa, comentario, fecha, nick);

                        this.salida=(meterBd);
                        break;
                        
                    case GET_COMMENTS_ESTAFA://GET_COMMENTS_ESTAFA+SEPARADOR+ID_ESTAFA
                        System.out.println("MS GET_COMMENTS_ESTAFA\n\t"+mensaje);
                        int id=Integer.parseInt(strSplit[1]);
                        String comments=clase_compartida.getComentarios_by(id);
                        System.out.println("----comments "+comments);
                        this.salida=(comments);
                        break;
                                
                        
                    case GET_CATEGORIES:
                        System.out.println("STOY CATEGORIA");
                        String str_categories=clase_compartida.getCategories();
                        this.salida=(str_categories);
                        break;
  // PENDIENTE FILTRADO TAGS                      
                    case GET_TAGS:
                        System.out.println("\tLONGi "+strSplit.length+"\t");
                        if(strSplit.length==2){//GET_TAGS:str_id
                           String tags=clase_compartida.getTagsBy(strSplit[1]); 
                           System.out.println("\tSTOY tags BY NAME "+strSplit[1]+
                           "\t"+tags);
                              this.salida=(tags);
                         } else{//GET_TAGS:
                             String tags=clase_compartida.getTags();
                           System.out.println("TAGS NORMAL  "+tags); 
                                 this.salida=(tags);
                         }
                        break;
                        
                    case DELETE:
                       // System.out.println("STOY DELETE "+mensaje);
                        respuesta=clase_compartida.deleteBy(strSplit[1]);
                        this.salida=(DELETE+SEPARADOR+respuesta);
                        break; 
                        
                    case UPDATE:
                       // System.out.println("UPDATE "+mensaje);
                        respuesta=clase_compartida.updateBy(mensaje);
                        this.salida=(DELETE+SEPARADOR+respuesta);
                        break; 
                        
                    case INSERT:
                        //System.out.println("INSERT "+mensaje);
                        respuesta=clase_compartida.updateBy(mensaje);
                        this.salida=(DELETE+SEPARADOR+respuesta);
                        break; 
                        
                    case ADDTAG:
                        //System.out.println("ADDTAG "+mensaje);
                        respuesta=clase_compartida.addTag(mensaje);
                        this.salida=(ADDTAG+SEPARADOR+respuesta);
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
      Date date=new Date();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
      String fecha = sdf.format(date); 
      return fecha;
    }
}

 