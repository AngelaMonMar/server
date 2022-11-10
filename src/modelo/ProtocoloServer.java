/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

/**
 *
 * @author sinNombre
 */
public class ProtocoloServer {
     public final static String IP = "IP";
   public final static String SEPARADOR = ":";
    public static final String SEPARADOR_checkboxs="?";// ?nombreCheckBos:
    public static final String SEPARADOR_dats="¿";
    public static final String PUNTO_Y_COMA=";";
    public final static String SIN_DATOS = "SIN_DATOS";
    
    public final static String OK = "OK"; // AUXILIARES
    public final static String NOT_OK = "NOT_OK";
       
       
       
    
     public static final String NOMBRE = "NOMBRE";
    public final static String EMAIL = "EMAIL";
    public static final String URL="URL";// ?nombreCheckBos:
    public static final String TELEFONO="TELEFONO";
    
    
    //ESTADOS --- 
    public final static String ESTADO_PETICION="ESTADO_PETICION";// estado cuando interactua con el cliente al hacer peticion
    public final static String ESTADO_CHAT_PUBLIC="ESTADO_CHAT_PUBLIC";
    public final static String ESTADO_CHAT_PRIVADO="ESTADO_CHAT_PRIVADO";
    
    
    // vista login 
    final static String ENVIAR = "ENVIAR";
    final static String LOGIN = "LOGIN"; // LOGIN: nombreUsuario : PW  caso q se erroneo LOGIN:SIN_DATOS
    final static String LOGIN_EXISTE = "LOGIN_EXISTE";
    final static String REGISTER = "REGISTER";
    
    final static String LOGIN_OK = "LOGIN_OK";
    final static String LOGIN_NOT_OK = "LOGIN_NOT_OK";
    final static String RECORDAR_DATOS_LOGIN = "RECORDAR_DATOS_LOGIN";
    
    final static String ESTADO_CHECKBOX = "ESTADO_CHECKBOX"; //ESTADO_CHECKBOX+SEPARADOR+remoteIp+SEPARADOR+CHECKBOX_REGISTRO_FALSE
    final static String CHECKBOX_REGISTRO_FALSE = "CHECKBOX_REGISTRO_FALSE";
    final static String CHECKBOX_REGISTRO_TRUE = "CHECKBOX_REGISTRO_TRUE";
    public final static String VOLVER="VOLVER";
    
    // VISTA RECUPERAR PW
    final static String RECUPERAR_PW="RECUPERAR_PW";
    public final static String PW_CAMBIADO_OK="PW_CAMBIADO_OK";//PW_CAMBIADO_OK:str
    public final static String PW_CAMBIADO_NOTOK="PW_CAMBIADO_NOTOK";//PW_CAMBIADO_NOTOK:str
    final static String VOLVER_VISTA_LOGIN="VOLVER_VISTA_LOGIN";
    
      //vista cambiar la contraseña
    public static final String CHANGE_PW="CHANGE_PW";//

      // __Server4App.vista Register
    final static String REGISTER_FORM_NOT_OK = "REGISTER_FORM_NOT_OK";
    public final static String REGISTER_FORM="REGISTER_FORM";
    public static final String REGISTER_FORM_OK="REGISTER_FORM_OK";

    // registro de estafa- solo app
    public static final String REGISTRAR_ESTAFA="REGISTRAR_ESTAFA";//REGISTRAR_ESTAFA: titulo: comentario: fecha : resto de datos
    public final static String REGISTRAR_ESTAFA_OK="REGISTRAR_ESTAFA_OK";
    public final static String REGISTRAR_ESTAFA_NOTOK="REGISTRAR_ESTAFA_NOTOK";

    //vista detalles
    public static final String COMENTARIO="COMENTARIO"; //  COMENTARIO +SEPARADOR+comment+SEPARADOR+Integer.toString(id);
  
    //menu opciones escritorio
    public static final String MENU_USUARIO="MENU_USUARIO";
    public static final String MENU_ESTAFADOR="MENU_ESTAFADOR";
    public static final String MENU_COMENTARIO="MENU_COMENTARIO";
    public static final String MENU_TAG="MENU_TAG";
    public static final String MENU_CATEGORIA="MENU_CATEGORIA"; 
   
    
       //usuario
    public static final String INSERT_USUARIO="INSERT_USUARIO";
    public static final String DELETE_USUARIO="DELETE_USUARIO";
    public static final String UPDATE_USUARIO="UPDATE_USUARIO";
    
        //COMENTARIO
    public static final String DELETE_COMMENT="DELETE_COMMENT";
    public static final String INSERT_COMMENT="INSERT_COMMENT";
    public static final String UPDATE_COMMENT="UPDATE_COMMENT";
  
       // TAG
    public static final String INSERT_TAG="INSERT_TAG";
    public static final String DELETE_TAG="DELETE_TAG";
    public static final String UPDATE_TAG="UPDATE_TAG";
      // CATEGORIA
    public static final String INSERT_CATEGORIA="INSERT_CATEGORIA";
    public static final String DELETE_CATEGORIA="DELETE_CATEGORIA";
    public static final String UPDATE_CATEGORIA="UPDATE_CATEGORIA";
     
     
 public static final String CONTACTO="CONTACTO";//CONTACTO
      
    public static final String GET_CATEGORIES="GET_CATEGORIES"; //GET_CATEGORIES+SEPARADOR
    public static final String GET_TAGS="GET_TAGS";//GET_TAGS+SEPARADOR 
    public final static String GET_LIST_ESTAFAS = "GET_LIST_ESTAFAS";//GET_ESTAFAS+SEPARADOR
    public final static String GET_DETALLES_ESTAFAS= "GET_DETALLES_ESTAFAS";//GET_DETALLES_ESTAFAS+SEPARADOR+ID_estafa
    public final static String  GET_COMMENTS_ESTAFA="GET_COMMENTS_ESTAFA";////GET_COMMENTS_ESTAFA+SEPARADOR+ID_ESTAFA
   public final static String GET_CONTADOR_VISTAS="GET_CONTADOR_VISTAS";
   

  
   public static final String ROL_ADMIN="ROL_ADMIN";
   public static final String ROL_ANDROID="ROL_ANDROID";
}   



