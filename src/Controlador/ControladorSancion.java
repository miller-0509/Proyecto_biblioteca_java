package Controlador;

import Modelo.ModeloSancion;
import Modelo.Sancion;
import java.sql.Date;
import java.util.List;

public class ControladorSancion {

    private final ModeloSancion modeloSancion;

    public ControladorSancion() {
        this.modeloSancion = new ModeloSancion();
    }

    public List<Sancion> listarTodas() {
        return modeloSancion.listarTodas();
    }

    public List<Sancion> buscar(String texto, String estado) {
        return modeloSancion.buscar(texto, estado);
    }

    public Sancion buscarPorId(int idSancion) {
        return modeloSancion.buscarPorId(idSancion);
    }

    public int guardar(Sancion sancion) {
        if (sancion.getIdSancion() > 0) {
            return modeloSancion.actualizar(sancion) ? sancion.getIdSancion() : -1;
        }
        return modeloSancion.insertar(sancion);
    }

    public boolean condonar(int idSancion) {
        return modeloSancion.condonar(idSancion);
    }

    public boolean eliminar(int idSancion) {
        return modeloSancion.eliminar(idSancion);
    }

    public Sancion registrarAutomaticamente(
            Integer idPrestamo,
            Integer idUsuario,
            String correoUsuario,
            String tipoRecurso,
            String nombreRecurso,
            Date fechaLimite,
            Date fechaDevolucion) {
        return modeloSancion.registrarAutomaticamente(
                idPrestamo,
                idUsuario,
                correoUsuario,
                tipoRecurso,
                nombreRecurso,
                fechaLimite,
                fechaDevolucion
        );
    }
}
