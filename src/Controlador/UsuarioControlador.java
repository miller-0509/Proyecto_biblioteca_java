package Controlador;

import Modelo.Usuario;
import Modelo.UsuarioDAO;
import java.util.List;

public class UsuarioControlador {

    private final UsuarioDAO usuarioDAO;

    public UsuarioControlador() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public List<Usuario> listarTodos() {
        return usuarioDAO.listarTodos();
    }

    public Usuario buscarPorId(int id) {
        return usuarioDAO.buscarPorId(id);
    }

    public int guardar(Usuario u) {
        if (u.getIdUsuario() > 0) {
            return usuarioDAO.actualizar(u) ? u.getIdUsuario() : -1;
        } else {
            return usuarioDAO.insertar(u);
        }
    }

    public boolean eliminar(int id) {
        return usuarioDAO.eliminar(id);
    }
}
