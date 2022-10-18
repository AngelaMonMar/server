/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author sinNombre
 */
class Estafa {
    private String fecha;
    private String titulo;
    private String category;
    private String contenido;

    public Estafa(String fecha, String category, String titulo, String contenido) {
        this.fecha = fecha;
        this.titulo = titulo;
        this.category = category;
        this.contenido=contenido;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    @Override
    public String toString() {
        return "Estafa{" +
                "fecha='" + fecha + '\'' +
                ", titulo='" + titulo + '\'' +
                ", publicado='" + category + '\'' +
                '}';
    }
}

