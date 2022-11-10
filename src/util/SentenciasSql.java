/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

/**
 *
 * @author sinNombre
 */
public class SentenciasSql {
    //USUARIO
     public static final String str_INSERT_USUARIO= "Insert into usuario(nombre_usuario,nick,  email, password)"
                                        + " values(?,?,?,?) ";
     public static final String str_SELECT_USUARIOS="SELECT* FROM usuario"; 
     public static final String str_SELECT_USUARIOS_ByEMAIL="SELECT * FROM usuario WHERE email=?";
     public static final String str_SELECT_IdUSUARIOS_ByNICK="SELECT cod_usuario as codigo from usuario where nick=? limit 1";
     public static final String str_SELECT_IdUSER_ByEMAIL="SELECT cod_usuario FROM usuario WHERE email=?";
     public static final String str_SELECT_USUARIOS_ByNICK_PW="SELECT* FROM usuario where nick=? and password=?";
     
     public static final String str_UPDATE_USUARIO="Update usuario set nombre_usuario=? , email=? , nick=? where cod_usuario=?";
     public static final String str_UPDATE_PW="UPDATE usuario set password=? where email=?";  
     public static final String str_DELETE_USUARIO="Delete from usuario where cod_usuario=?";
     
     //COMENTARIOS
     public static final String str_INSERT_COMMENT="INSERT into comentario_publico (id_estafa, descripcion, cod_usu,fecha) values(?,?,?,?)";
     public static final String str_SELECT_COMMENT="SELECT id_comment as ID, c.descripcion as Comment, "
                        + " u.nick as nick from estafa e join comentario_publico c " +
                            "join usuario u  " +
                            "on c.id_estafa=e.id_estafa AND " +
                            "u.cod_usuario=c.cod_usu"; 
     public static final String str_SELECT_COMMENT_ByID="SELECT fecha, descripcion,id_estafa, nick " +
                         "from comentario_publico c JOIN usuario u ON u.cod_usuario=c.cod_usu and id_estafa=?";
     public static final String str_UPDATE_COMMENT="Update comentario_publico set descripcion=? where id_comment=?";
     public static final String str_DELETE_COMMENT="DELETE from comentario_publico where id_comment=?";
     
     //ESTAFADORES
    
    //TAG
     public static final String str_INSERT_TAG="INSERT into Tags(nombre) value(?)";
     public static final String str_SELECT_TAGS="SELECT* FROM tags";
     public static final String str_SELECT_TAG_BYID="SELECT t.nombre FROM estafa_tiene_tag e join tags t ON " +
                        "e.id_tag=t.id_tag where id_estafa=?";
     public static final String str_UPDATE_TAG="UPDATE Tags set nombre=? where id_tag=?";
     public static final String str_DELETE_TAG="DELETE from Tags where id_tag=?";
     
      //CATEGORY
     public static final String str_INSERT_CATEGORY="INSERT into Categoria(nombre) value(?)";
     public static final String str_SELECT_CATEGORY="SELECT* FROM CATEGORIA";
     public static final String str_UPDATE_CATEGORY="Update Categoria set nombre=? where id_category=?";
     public static final String str_DELETE_CATEGORY="DELETE from Categoria where id_category=?";
//    public static final String str_SELECT_CATEGORY_BYID="SELECT t.nombre FROM estafa_tiene_tag e join tags t ON " +
//                        "e.id_tag=t.id_tag where id_estafa=?";
     
     
     public static final String GRACIAS_COMMENT="Gracias por su comentarío";
     public static final String ERROR_COMMENT="Error al publicar su mensaje, inténtalo mas tarde.";
}

