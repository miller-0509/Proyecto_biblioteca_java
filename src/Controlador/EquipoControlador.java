package Controlador;

import Modelo.Equipo;
import Modelo.EquipoDAO;
import java.util.List;

public class EquipoControlador {

    private final EquipoDAO equipoDAO;

    public EquipoControlador() {
        this.equipoDAO = new EquipoDAO();
    }

    public List<Equipo> listarTodos() {
        return equipoDAO.listarTodos();
    }

    public Equipo buscarPorId(int id) {
        return equipoDAO.buscarPorId(id);
    }

    public int guardar(Equipo e) {
        if (e.getIdEquipo() > 0) {
            return equipoDAO.actualizar(e) ? e.getIdEquipo() : -1;
        } else {
            return equipoDAO.insertar(e);
        }
    }

    public boolean eliminar(int id) {
        return equipoDAO.eliminarLogico(id);
    }
}
