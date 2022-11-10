/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo.objetos;

import java.net.Socket;

/**
 *
 * @author sinNombre
 */
public class Usuario {

    private String nombre;
    private String nick;
    private String email;
    private String pw;
  
    private boolean checkbox_register;
    private  Socket socket;

    public Usuario() {

    }
//       public Usuario(String nombre, String nick, String email, String pw) {
//        checkbox_register=false;
//        this.nombre=nombre;
//        this.apellidos=apellidos;
//        this.nick = nick;
//        this.email = email;
//        this.pw = pw; 
//    }


    public Usuario(String nombre, String nick, String email, String pw, Socket socket) {
        checkbox_register=false;
        this.nick = nick;
        this.pw = pw;
        this.nombre = nombre;
        this.email = email;
        this.socket=socket;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }



    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }




    //checkbox
    public boolean isCheckbox_register() {
        return checkbox_register;
    }

    public void setCheckbox_register(boolean checkbox_register) {
        this.checkbox_register = checkbox_register;
    }

    @Override
    public String toString() {
        return "Usuario{" + "nombre=" + nombre + ", nick=" + nick + 
                ", email=" + email + ", pw=" + pw + 
                ", checkbox_register=" + checkbox_register + '}';
    }

}