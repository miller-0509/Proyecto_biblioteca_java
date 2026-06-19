package Controlador;

import Modelo.Prestamo;
import Modelo.PrestamoDAO;
import java.util.List;

public class PrestamoControlador {

    private final PrestamoDAO prestamoDAO;

    public PrestamoControlador() {
        this.prestamoDAO = new PrestamoDAO();
    }

    public List<Prestamo> listarEquipos() {
        return prestamoDAO.listarEquipos();
    }

    public List<Prestamo> listarLibros() {
        return prestamoDAO.listarLibros();
    }

    public int crearEquipo(Prestamo p) {
        return prestamoDAO.insertarEquipo(p);
    }

    public int crearLibro(Prestamo p) {
        return prestamoDAO.insertarLibro(p);
    }

    public boolean devolverEquipo(int idPrestamo) {
        return prestamoDAO.devolverEquipo(idPrestamo);
    }

    public boolean devolverLibro(int idPrestamo) {
        return prestamoDAO.devolverLibro(idPrestamo);
    }
}
