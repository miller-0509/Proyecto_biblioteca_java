package Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT id_usuario, nombres, apellidos, correo, "
                   + "rol::text, estado::text FROM usuarios "
                   + "ORDER BY id_usuario";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setNombres(rs.getString("nombres"));
                u.setApellidos(rs.getString("apellidos"));
                u.setCorreo(rs.getString("correo"));
                u.setRol(rs.getString("rol"));
                u.setEstado(rs.getString("estado"));
                lista.add(u);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar usuarios: " + ex.getMessage(), ex);
        }
        return lista;
    }

    public Usuario buscarPorId(int id) {
        String sql = "SELECT id_usuario, nombres, apellidos, correo, "
                   + "rol::text, estado::text, email_verificado FROM usuarios "
                   + "WHERE id_usuario = ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setIdUsuario(rs.getInt("id_usuario"));
                    u.setNombres(rs.getString("nombres"));
                    u.setApellidos(rs.getString("apellidos"));
                    u.setCorreo(rs.getString("correo"));
                    u.setRol(rs.getString("rol"));
                    u.setEstado(rs.getString("estado"));
                    u.setEmailVerificado(rs.getBoolean("email_verificado"));
                    return u;
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al buscar usuario por ID: " + ex.getMessage(), ex);
        }
        return null;
    }

    public int insertar(Usuario u) {
        String sql = "INSERT INTO usuarios (nombres, apellidos, correo, password, "
                   + "rol, estado) "
                   + "VALUES (?, ?, ?, ?, ?::rol_usuario, ?::estado_usuario)";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, u.getNombres());
            ps.setString(2, u.getApellidos());
            ps.setString(3, u.getCorreo());
            ps.setString(4, u.getPassword());
            ps.setString(5, u.getRol());
            ps.setString(6, u.getEstado());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al insertar usuario: " + ex.getMessage(), ex);
        }
        return -1;
    }

    public boolean actualizar(Usuario u) {
        String sql = "UPDATE usuarios SET nombres = ?, apellidos = ?, correo = ?, "
                   + "rol = ?::rol_usuario, estado = ?::estado_usuario "
                   + (u.getPassword() != null && !u.getPassword().isEmpty()
                      ? ", password = ? " : " ")
                   + "WHERE id_usuario = ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, u.getNombres());
            ps.setString(2, u.getApellidos());
            ps.setString(3, u.getCorreo());
            ps.setString(4, u.getRol());
            ps.setString(5, u.getEstado());

            int idx = 6;
            if (u.getPassword() != null && !u.getPassword().isEmpty()) {
                ps.setString(idx++, u.getPassword());
            }
            ps.setInt(idx, u.getIdUsuario());

            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Error al actualizar usuario: " + ex.getMessage(), ex);
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM usuarios WHERE id_usuario = ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Error al eliminar usuario: " + ex.getMessage(), ex);
        }
    }
}
