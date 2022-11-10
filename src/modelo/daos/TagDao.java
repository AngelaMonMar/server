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
public class TagDao {
     private String sql=null;
     private Statement state;
     private Connection conexion=null;
     private PreparedStatement pre_state;
     private ResultSet rs;   
     
     
    // get Tags para rellenar el spinner
    public String getTags() {
        String salida="";
        this.sql=str_SELECT_TAGS; 
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
           
            this.sql=str_SELECT_TAG_BYID; 
            try {
               this.conexion =  Conexion.getConnection();
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

  // para el cliente de escritorio id + nombre
    public String getTags2() {
    String salida="";
        this.sql=str_SELECT_TAGS;//"SELECT* FROM tags"; 
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
    
    
        public String addTag(String mensaje) {//ADDTAG:tag
         String [] vstr=mensaje.split(SEPARADOR);
         String salida=NOT_OK+SEPARADOR+"Tag NO actualizada.";

         String sql=str_INSERT_TAG;//"Insert into Tags(nombre) value(?)";
         try {
            this.conexion =  Conexion.getConnection();
            this.pre_state= conexion.prepareStatement(sql);
            this.pre_state.setString(1, vstr[1]);

            if(!pre_state.execute()){
                    salida=OK+SEPARADOR+"Tag insertada con exito.";
                }
        } catch (Exception e) {
        }
        return salida;
    }
        
        
       public String updateTag(String mensaje) {//UPDATE:id:valor;
        String [] vstr=mensaje.split(SEPARADOR);
        String salida=NOT_OK+SEPARADOR+"No se ha actualizado";
        int tagId=Integer.parseInt(vstr[1]);
        String newValor=vstr[2];
          
        String sql=str_UPDATE_TAG;//"Update Tags set nombre=? where id_tag=?";//str_UPDATE_TAG;
         try {
            this.conexion =  Conexion.getConnection();
            this.pre_state= conexion.prepareStatement(sql);
            this.pre_state.setString(1, newValor);
            this.pre_state.setInt(2, tagId);

            if(!pre_state.execute()){
                    salida=OK+SEPARADOR+"Tag con codigo "+tagId+" actualizada con exito.";
                }
            pre_state.close();
        } catch (Exception e) {
        }
        return salida;  
    }

    public String deleteTag(String id) {
            String salida=NOT_OK+SEPARADOR+"No se ha eliminado";
            int idTag=Integer.parseInt(id);
            String sql=str_DELETE_TAG;//"DELETE from Tags where id_tag=?";
         try {
            this.conexion =  Conexion.getConnection();
            this.pre_state= conexion.prepareStatement(sql);

            this.pre_state.setInt(1, idTag);

            if(!pre_state.execute()){
                    salida=OK+SEPARADOR+"Tag con codigo "+idTag+" Eliminada con exito.";
                }
            pre_state.close();
        } catch (Exception e) {
        }
        return salida;   
    }

}
