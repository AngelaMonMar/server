/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vista;

import controlador.ControladorServidor;

/**
 *
 * @author sinNombre
 */
public interface IVista {
//    final String ENVIAR = "ENVIAR";
//    final String LOGIN = "LOGIN";
//    
//    final String LOGIN_OK = "LOGIN_OK";
//    final String LOGIN_NOT_OK = "LOGIN_NOT_OK";
//    final String SEPARADOR = "-";
    
  //  public void habilitarEnviar();
  //  public void deshabilitarEnviar();
    public void mostrarRespuesta(String mensaje);
  //  public void borrarTextoAEnviar();
    public void setControlador(ControladorServidor controlador);
    public void hacerVisible();
   // public void inicializar();
   // public String getMensajeAEnviar();
}
