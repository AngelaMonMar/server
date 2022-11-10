/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.daos;

import controlador.Clase_compartida;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.Conexion;
import static modelo.ProtocoloServer.PUNTO_Y_COMA;
import static modelo.ProtocoloServer.SEPARADOR;

/**
 *
 * @author sinNombre
 */
public class EstafadorDao {
     private String sql=null;
     private Statement state;
     private Connection conexion=null;
     private PreparedStatement pre_state;
     private ResultSet rs;
     
        public String getInformacionEstafador() {
     String salida="";
        this.sql="SELECT CONCAT(group_concat(IFNULL(p2.valor, \"sin_datos\")))as datos, p.id_estafador as id FROM perfil_estafador p "
                + "left join perfil_estafador_tiene_dato p2 on p.id_estafador=p2.id_estafador group by p.id_estafador"; 
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
        System.out.println("salida "+salida);
        return salida;
   }
        
      public int insertarTabla_estafador(String observacion) {//1-OK
            int id_estafador=0;
        try {
            CallableStatement callableState;
            this.conexion =  Conexion.getConnection();
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
      
    
              
              
              
  public  void insertarTabla_perfil_estafador_tieneDatos(int id, Map<String, String> mimap) {  //2-ok

        for(Map.Entry entry:mimap.entrySet()){
            try {
                String nombre=entry.getKey().toString();
                String valor=entry.getValue().toString();
                String sql="INSERT INTO PERFIL_ESTAFADOR_TIENE_DATO(ID_ESTAFADOR, ID_TIPO_DATOS, VALOR)  "
                        + " VALUES (?, "
                        + "( select id_tipo from tipo_dato where valor=?),?)";
                this.conexion =  Conexion.getConnection();
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

}
