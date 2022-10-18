/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import modelo.MS_hiloDelCliente;
import vista.IVista;

/**
 *
 * @author sinNombre
 */
public class ControladorServidor implements ActionListener {
    IVista vista;
    MS_hiloDelCliente modelo;

    public ControladorServidor(IVista vista, MS_hiloDelCliente modelo) {
        this.vista = vista;
        this.modelo = modelo;
    }
    
    public void arrancar(){
        vista.hacerVisible();      
       // modelo.abrirPuerto();
        vista.mostrarRespuesta("abriendo el puerto..."+modelo.getPUERTO());   
        modelo.acceptSocket();
        vista.mostrarRespuesta("Conectando cliente..."+modelo.getSocket()+"\n");
        modelo.crearFlujos();
        modelo.start(); 
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
    
    public void server_vista_muestra_msg(String mensaje){
        vista.mostrarRespuesta(mensaje);
    }
}
