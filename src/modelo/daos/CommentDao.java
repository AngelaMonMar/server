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
import static modelo.ProtocoloServer.COMENTARIO;
import static modelo.ProtocoloServer.NOT_OK;
import static modelo.ProtocoloServer.OK;
import static modelo.ProtocoloServer.PUNTO_Y_COMA;
import static modelo.ProtocoloServer.SEPARADOR;
import static util.SentenciasSql.*;


/**
 *
 * @author sinNombre
 */
public class CommentDao {
        private String sql=null;
        private Statement state;
        private Connection conexion=null;
        private PreparedStatement pre_state;
        private ResultSet rs;
        UsuarioDao usuarioDao=new UsuarioDao();
    
        
      public String getComentarios() {
        String salida="";
        this.sql=str_SELECT_COMMENT;
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
            System.out.println(" SQLException - getComentarios "+ex.getMessage());
        }
        return salida;
    }
     
   
      //INSERT into comentario_publico (id_estafa, descripcion, cod_usu,fecha) values(24, 
                //"stoy hasta el coño", 1, "2021/01/01")
    public String addComment_estafa(int id_estafa, String comentario, String fecha, String nick) {
        String salida=COMENTARIO+SEPARADOR;
        // obtener id_usuario con nick
        int id_usuario=usuarioDao.dameId_usuario_conNick(nick);
        
        String sql=str_INSERT_COMMENT;//"INSERT into comentario_publico (id_estafa, descripcion, cod_usu,fecha) values(?,?,?,?)";
         try {
            this.conexion =  Conexion.getConnection();
            this.pre_state= conexion.prepareStatement(sql);

            this.pre_state.setInt(1, id_estafa);
            this.pre_state.setString(2, comentario);
            this.pre_state.setInt(3, id_usuario);
            this.pre_state.setString(4, fecha.toString());

            if(!pre_state.execute())
                    salida+=OK+SEPARADOR+GRACIAS_COMMENT;
             else  
                   salida+=NOT_OK+SEPARADOR+ERROR_COMMENT;//"Error al publicar su mensaje, inténtalo mas tarde.";
            
           
        } catch (Exception e) {
        }
        return salida;
       }
    
   
    public String getComentarios_by(int id) {
            String salida="";
            String sql=str_SELECT_COMMENT_ByID;
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

     public String updateComment(String mensaje) {
        String [] vstr=mensaje.split(SEPARADOR);
        String salida=NOT_OK+SEPARADOR+"No se ha actualizado";
        int commentId=Integer.parseInt(vstr[1]);
        String newValor=vstr[2];
          
        String sql=str_UPDATE_COMMENT;//"Update comentario_publico set descripcion=? where id_comment=?";//str_UPDATE_TAG;
         try {
            this.conexion =  Conexion.getConnection();
            this.pre_state= conexion.prepareStatement(sql);
            this.pre_state.setString(1, newValor);
            this.pre_state.setInt(2, commentId);

            if(!pre_state.execute()){
                    salida=OK+SEPARADOR+"Comentario con codigo "+commentId+" actualizada con exito.";
                }
            pre_state.close();
        } catch (Exception e) {
        }
        return salida;  
    }

    public String deleteComment(String id) {
            String salida=NOT_OK+SEPARADOR+"No se ha eliminado";
            int idComment=Integer.parseInt(id);
            String sql=str_DELETE_COMMENT;//"DELETE from comentario_publico where id_comment=?";
         try {
            this.conexion =  Conexion.getConnection();
            this.pre_state= conexion.prepareStatement(sql);

            this.pre_state.setInt(1, idComment);

            if(!pre_state.execute()){
                    salida=OK+SEPARADOR+"Comentario con codigo "+idComment+" Eliminada con exito.";
                }
            pre_state.close();
        } catch (Exception e) {
        }
        return salida;  
    }
}
