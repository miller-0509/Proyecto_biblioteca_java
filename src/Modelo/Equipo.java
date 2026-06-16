package Modelo;

public class Equipo {

    private int idEquipo;
    private String nombre;
    private String tipoEquipo;
    private String marca;
    private String modelo;
    private String numeroSerie;
    private String estado;
    private String ubicacion;
    private boolean disponiblePrestamo;
    private int tiempoMaxPrestamo;
    private String descripcion;
    private boolean eliminado;

    public Equipo() {
    }

    public Equipo(int idEquipo, String nombre, String tipoEquipo, String marca,
                  String numeroSerie, String estado, String ubicacion) {
        this.idEquipo = idEquipo;
        this.nombre = nombre;
        this.tipoEquipo = tipoEquipo;
        this.marca = marca;
        this.numeroSerie = numeroSerie;
        this.estado = estado;
        this.ubicacion = ubicacion;
    }

    public int getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(int idEquipo) {
        this.idEquipo = idEquipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipoEquipo() {
        return tipoEquipo;
    }

    public void setTipoEquipo(String tipoEquipo) {
        this.tipoEquipo = tipoEquipo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getNumeroSerie() {
        return numeroSerie;
    }

    public void setNumeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public boolean isDisponiblePrestamo() {
        return disponiblePrestamo;
    }

    public void setDisponiblePrestamo(boolean disponiblePrestamo) {
        this.disponiblePrestamo = disponiblePrestamo;
    }

    public int getTiempoMaxPrestamo() {
        return tiempoMaxPrestamo;
    }

    public void setTiempoMaxPrestamo(int tiempoMaxPrestamo) {
        this.tiempoMaxPrestamo = tiempoMaxPrestamo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }
}
