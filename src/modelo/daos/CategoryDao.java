/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import modelo.Conexion;
import static modelo.ProtocoloServer.NOT_OK;
import static modelo.ProtocoloServer.OK;
import static modelo.ProtocoloServer.PUNTO_Y_COMA;
import static modelo.ProtocoloServer.SEPARADOR;

import static util.SentenciasSql.*;


/**
 *
 * @author sinNombre
 */
public class CategoryDao {
     private String sql=null;
     private Statement state;
     private Connection conexion=null;
     private PreparedStatement pre_state;
     private ResultSet rs;   
     
     
    public String getCategories() {
       String salida="";
       this.sql=str_SELECT_CATEGORY; 
       try {
          this.conexion =  Conexion.getConnection();
          this.state=this.conexion.createStatement();
          ResultSet rs=this.state.executeQuery(this.sql);
          while(rs.next()){
            salida+=rs.getString("nombre")+SEPARADOR;
          }
           System.out.println("salida"+salida);
            
        } catch (SQLException ex) {
            System.out.println(" SQLException - getCategories "+ex.getMessage());
        }
        return salida;
    }
    
    
    public String getCategories2() {
       String salida="";
       this.sql=str_SELECT_CATEGORY; 
       try {
            this.conexion =  Conexion.getConnection();
            this.state=this.conexion.createStatement();
            ResultSet rs=this.state.executeQuery(this.sql);
            while(rs.next()){
              salida+=rs.getInt("id_category")+SEPARADOR;
              salida+=rs.getString("nombre")+PUNTO_Y_COMA;
            }
            
           System.out.println("salida"+salida);
            
        } catch (SQLException ex) {
            System.out.println(" SQLException - getCategories "+ex.getMessage());
        }
        return salida;
    }

    public String addCategory(String mensaje) {//ADDTAG:tag
         String [] vstr=mensaje.split(SEPARADOR);
         String salida=NOT_OK+SEPARADOR+"Categoria NO actualizada.";

         String sql=str_INSERT_CATEGORY;
         try {
            this.conexion =  Conexion.getConnection();
            this.pre_state= conexion.prepareStatement(sql);
            this.pre_state.setString(1, vstr[1]);

            if(!pre_state.execute()){
                    salida=OK+SEPARADOR+"Categoria insertada con exito.";
                }
        } catch (Exception e) {
        }
        return salida;
    }
    
    public String updateCategory(String mensaje) {//UPDATE:id:valor;
        String [] vstr=mensaje.split(SEPARADOR);
        String salida=NOT_OK+SEPARADOR+"No se ha actualizado";
        int categoryId=Integer.parseInt(vstr[1]);
        String newValor=vstr[2];
          
        String sql=str_UPDATE_CATEGORY;//str_UPDATE_TAG;
         try {
            this.conexion =  Conexion.getConnection();
            this.pre_state= conexion.prepareStatement(sql);
            this.pre_state.setString(1, newValor);
            this.pre_state.setInt(2, categoryId);

            if(!pre_state.execute()){
                    salida=OK+SEPARADOR+"Categoria con codigo "+categoryId+" actualizada con exito.";
                }
            pre_state.close();
        } catch (Exception e) {
        }
        return salida;  
    }

    public String deleteCategory(String id) {
            String salida=NOT_OK+SEPARADOR+"No se ha eliminado";
            int categoryId=Integer.parseInt(id);
            String sql=str_DELETE_CATEGORY;
         try {
            this.conexion =  Conexion.getConnection();
            this.pre_state= conexion.prepareStatement(sql);

            this.pre_state.setInt(1, categoryId);

            if(!pre_state.execute()){
                    salida=OK+SEPARADOR+"Categoria con codigo "+categoryId+" eliminada con exito.";
                }
            pre_state.close();
        } catch (Exception e) {
        }
        return salida;   
    }
    
}
