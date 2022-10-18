
-- Inserta en la tabla perfil_estafador-- obtengo el ID_estafador
        DELIMITER //

DROP PROCEDURE IF EXISTS insertPerfilEstafador //

CREATE PROCEDURE insertPerfilEstafador (in xx varchar(40) , out salida int )
   BEGIN
     insert into perfil_estafador(observacion) VALUES(xx);
     set salida=LAST_INSERT_ID();
END //

DELIMITER ;

call insertPerfilEstafador("algo", @id);
select @id;
        
----- FIN INSERTAR PERFIL ESTAFADOR  

----- INSERTAR EN POST ESTAFA
        DELIMITER //

DROP PROCEDURE IF EXISTS insertarEstafa //

CREATE PROCEDURE insertarEstafa (in titulox varchar(20) ,
                                 in comment varchar(500),
                                 in fechax varchar(20),
                                 in cod_usux int,
                                 in categoryx varchar(20),
                                 in cod_estafador int,
                                 out id_post int )
   BEGIN
   INSERT INTO ESTAFA(titulo,descripcion, fecha,cod_usu, id_category,id_estafador)
    VALUES(titulox,comment, fechax,cod_usux,
          (Select id_category from categoria where nombre=categoryx),
         cod_estafador);
     set id_post=LAST_INSERT_ID();
END //

DELIMITER ;
call insertarEstafa("T1","miCom","2020-01-01",6,"otros",21, @id);
select @id;



------------------FIN PUBLICAR ESTAFA


-----------------iNSERTAR EN TABLA PERFIL_ESTAFADOR_TIENE_DATO----------
INSERT INTO PERFIL_ESTAFADOR_TIENE_DATO(ID_ESTAFADOR, ID_TIPO_DATOS, VALOR) 
VALUES (21, ( select id_tipo from tipo_dato where valor='nombre'), 'pepe perez')
  
for(Map.Entry entry:mimap.entrySet()){
            try {
                String nombre=entry.getKey().toString();
                String valor=entry.getValue().toString();
                String sql="INSERT INTO PERFIL_ESTAFADOR_TIENE_DATO(ID_ESTAFADOR, ID_TIPO_DATOS, VALOR)  "
                        + " VALUES (?, "
                        + "( select id_tipo from tipo_dato where valor=?),?)";
                pre_state= (PreparedStatement)c.prepareStatement(sql);
                pre_state.setInt(1, 22);
                pre_state.setString (2, nombre);
                pre_state.setString (3, valor);
                if(!pre_state.execute()){
                    System.out.println("Done ");
                }
                
                pre_state.close();
            } catch (SQLException ex) {
                System.out.println("SQLException "+ex.getMessage());
            }
             
         } 
//---------------FIN INSERTAR EN ESTAFDOR TIENE DATOS

//-------------INSERTAR TAGS
INSERT INTO estafa_tiene_tag(ID_TAG, id_estafa) 
        VALUES 
((SELECT ID_TAG FROM TAGS WHERE NOMBRE='jefe'), 1)  


--- obtengo el codigo del tipo de dato-- identificado internamemte
--- y el valor de tipo dato pe 1(==nombre)  pepito
select d.valOr, d.id_tipo_datos from estafa e inner join perfil_estafador p  join perfil_estafador_tiene_dato d on
p.id_estafador=e.id_estafador AND
p.id_estafador=d.id_estafador


-----------getDetail_estafas // valor y tipo de dato filtrado x id pasado x PM

select t.valor as valor , t.id_tipo as clave from estafa e join perfil_estafador p join perfil_estafador_tiene_dato pp join tipo_dato t 
on e.id_estafador=p.id_estafador AND 
p.id_estafador=pp.id_estafador AND 
t.id_tipo=pp.id_tipo_datos and id_estafa=26


-- select  DISTINCT(d.id_tipo_datos), d.valOr, e.id_estafa
-- from tags tag join estafa_tiene_tag ett join
-- estafa e inner join perfil_estafador p 
-- join perfil_estafador_tiene_dato d on
-- p.id_estafador=e.id_estafador AND
-- p.id_estafador=d.id_estafador AND
-- e.id_estafa=ett.id_estafa AND
-- tag.id_tag=ett.id_tag where e.id_estafa=24


--------getComentarios
SELECT fecha, descripcion,id_estafa, nick 
  from comentario_publico c JOIN usuario u ON u.cod_usuario=c.cod_usu and id_estafa=14


-- Orden insertar bd
--1-usuario y usuario_tiene_rol
--2- perfil_estafador
insert into perfil_estafador (`observacion`) VALUES ("EStA LOCO")
--3-perfil_tiene_dato
insert into `perfil_estafador_tiene_dato` (`id_estafador`, `id_tipo_datos`,`valor`)
VALUES
(53, 1, "Pepe perez");
--4 estafa
Insert into `estafa` (`titulo`,`descripcion`,`fecha`,`cod_usu`,`id_category`,`id_estafador`)
VALUES
("La ETT se quedo con medio sueldo",
"harto trabajar para no cobrar nada", "1999/01/01",6, 1, 53);

--5 estafa tiene TAG
Insert into `estafa_tiene_tag`(`id_tag`, `id_estafa`) VALUES(17, 25)



---TagsById Detalles fragment
SELECT t.nombre FROM estafa_tiene_tag e join tags t ON
e.id_tag=t.id_tag where id_estafa=28


//----------------------Action2
select p.id_estafador as id, tipo_dato.valor as clave, p2.valor as valor , t2.nombre as tag from perfil_estafador p 
join perfil_estafador_tiene_dato p2 join tipo_dato as tipo_dato join estafa e join estafa_tiene_tag t join tags t2 
on e.id_estafador=p.id_estafador AND p.id_estafador=p2.id_estafador AND
 t.id_estafa=e.id_estafa AND t2.id_tag=t.id_tag AND tipo_dato.id_tipo=p2.id_tipo_datos

//datos estafador
SELECT CONCAT(group_concat(p2.valor),".") as datos, p2.id_estafador as id
FROM perfil_estafador p join perfil_estafador_tiene_dato p2
on p.id_estafador=p2.id_estafador
group by  p.id_estafador

-- id estafa , nick , comentario
SELECT e.id_estafa as ID, c.descripcion as Comment, u.nick as nick from estafa e join comentario_publico c
join usuario u 
on c.id_estafa=e.id_estafa AND
u.cod_usuario=c.cod_usu

--genera Jar netbeans
--https://www.youtube.com/watch?v=TK-cNN2lwS0

--apk android
--https://www.youtube.com/watch?v=Qf3vIAP2eF0

-- GENERA .EXE netbeans
https://www.youtube.com/watch?v=i4NKbt9a53U