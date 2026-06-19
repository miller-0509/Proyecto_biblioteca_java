package Modelo;

import java.sql.Timestamp;

public class Prestamo {

    private int idPrestamo;
    private int idUsuario;
    private int idEquipo;
    private int idLibro;
    private Timestamp fechaSolicitud;
    private Timestamp fechaDevolucion;
    private Timestamp fechaLimite;
    private String estado;
    private String observaciones;

    private String nombreUsuario;
    private String apellidoUsuario;
    private String nombreEquipo;
    private String tipoEquipo;
    private String nombreLibro;
    private String generoLibro;

    public Prestamo() {
    }

    public int getIdPrestamo() { return idPrestamo; }
    public void setIdPrestamo(int idPrestamo) { this.idPrestamo = idPrestamo; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public int getIdEquipo() { return idEquipo; }
    public void setIdEquipo(int idEquipo) { this.idEquipo = idEquipo; }

    public int getIdLibro() { return idLibro; }
    public void setIdLibro(int idLibro) { this.idLibro = idLibro; }

    public Timestamp getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(Timestamp fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }

    public Timestamp getFechaDevolucion() { return fechaDevolucion; }
    public void setFechaDevolucion(Timestamp fechaDevolucion) { this.fechaDevolucion = fechaDevolucion; }

    public Timestamp getFechaLimite() { return fechaLimite; }
    public void setFechaLimite(Timestamp fechaLimite) { this.fechaLimite = fechaLimite; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getApellidoUsuario() { return apellidoUsuario; }
    public void setApellidoUsuario(String apellidoUsuario) { this.apellidoUsuario = apellidoUsuario; }

    public String getNombreEquipo() { return nombreEquipo; }
    public void setNombreEquipo(String nombreEquipo) { this.nombreEquipo = nombreEquipo; }

    public String getTipoEquipo() { return tipoEquipo; }
    public void setTipoEquipo(String tipoEquipo) { this.tipoEquipo = tipoEquipo; }

    public String getNombreLibro() { return nombreLibro; }
    public void setNombreLibro(String nombreLibro) { this.nombreLibro = nombreLibro; }

    public String getGeneroLibro() { return generoLibro; }
    public void setGeneroLibro(String generoLibro) { this.generoLibro = generoLibro; }

    public String getNombreCompletoUsuario() {
        return ((nombreUsuario != null ? nombreUsuario : "") + " " + (apellidoUsuario != null ? apellidoUsuario : "")).trim();
    }
}
