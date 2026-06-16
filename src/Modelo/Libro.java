package Modelo;

public class Libro {

    private int idLibro;
    private String titulo;
    private String autor;
    private String genero;
    private String codigoUnico;
    private String estado;
    private String ubicacion;
    private boolean disponiblePrestamo;
    private int tiempoMaxPrestamo;
    private String descripcion;
    private boolean eliminado;

    public Libro() {
    }

    public Libro(int idLibro, String titulo, String autor, String genero,
                 String codigoUnico, String estado, String ubicacion) {
        this.idLibro = idLibro;
        this.titulo = titulo;
        this.autor = autor;
        this.genero = genero;
        this.codigoUnico = codigoUnico;
        this.estado = estado;
        this.ubicacion = ubicacion;
    }

    public int getIdLibro() { return idLibro; }
    public void setIdLibro(int idLibro) { this.idLibro = idLibro; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getCodigoUnico() { return codigoUnico; }
    public void setCodigoUnico(String codigoUnico) { this.codigoUnico = codigoUnico; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public boolean isDisponiblePrestamo() { return disponiblePrestamo; }
    public void setDisponiblePrestamo(boolean disponiblePrestamo) { this.disponiblePrestamo = disponiblePrestamo; }

    public int getTiempoMaxPrestamo() { return tiempoMaxPrestamo; }
    public void setTiempoMaxPrestamo(int tiempoMaxPrestamo) { this.tiempoMaxPrestamo = tiempoMaxPrestamo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public boolean isEliminado() { return eliminado; }
    public void setEliminado(boolean eliminado) { this.eliminado = eliminado; }
}
