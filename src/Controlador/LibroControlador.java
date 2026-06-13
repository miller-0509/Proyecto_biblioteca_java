package Controlador;

import Modelo.Libro;
import Modelo.LibroDAO;
import java.util.List;

public class LibroControlador {

    private final LibroDAO libroDAO;

    public LibroControlador() {
        this.libroDAO = new LibroDAO();
    }

    public List<Libro> listarTodos() {
        return libroDAO.listarTodos();
    }

    public Libro buscarPorId(int id) {
        return libroDAO.buscarPorId(id);
    }

    public int guardar(Libro l) {
        if (l.getIdLibro() > 0) {
            return libroDAO.actualizar(l) ? l.getIdLibro() : -1;
        } else {
            return libroDAO.insertar(l);
        }
    }

    public boolean eliminar(int id) {
        return libroDAO.eliminarLogico(id);
    }
}
