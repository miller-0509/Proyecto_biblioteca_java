package Modelo;

public class Usuario {

    private int idUsuario;
    private String nombres;
    private String apellidos;
    private String correo;
    private String password;
    private String rol;
    private String estado;
    private boolean emailVerificado;

    public Usuario() {
    }

    public Usuario(int idUsuario, String nombres, String apellidos, String correo,
                   String rol, String estado) {
        this.idUsuario = idUsuario;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.correo = correo;
        this.rol = rol;
        this.estado = estado;
    }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getNombreCompleto() { return nombres + " " + apellidos; }

    public String getIniciales() {
        String n = nombres != null && !nombres.isEmpty() ? nombres.substring(0, 1).toUpperCase() : "";
        String a = apellidos != null && !apellidos.isEmpty() ? apellidos.substring(0, 1).toUpperCase() : "";
        return n + a;
    }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public boolean isEmailVerificado() { return emailVerificado; }
    public void setEmailVerificado(boolean emailVerificado) { this.emailVerificado = emailVerificado; }
}
