package Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO {

    public List<Libro> listarTodos() {
        List<Libro> lista = new ArrayList<>();
        String sql = "SELECT id_libro, titulo, autor, genero, codigo_unico, "
                   + "estado::text, ubicacion FROM libros WHERE eliminado = false "
                   + "ORDER BY id_libro";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Libro l = new Libro();
                l.setIdLibro(rs.getInt("id_libro"));
                l.setTitulo(rs.getString("titulo"));
                l.setAutor(rs.getString("autor"));
                l.setGenero(rs.getString("genero"));
                l.setCodigoUnico(rs.getString("codigo_unico"));
                l.setEstado(rs.getString("estado"));
                l.setUbicacion(rs.getString("ubicacion"));
                lista.add(l);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar libros: " + ex.getMessage(), ex);
        }
        return lista;
    }

    public Libro buscarPorId(int id) {
        String sql = "SELECT id_libro, titulo, autor, genero, codigo_unico, "
                   + "estado::text, ubicacion, disponible_prestamo, "
                   + "tiempo_max_prestamo, descripcion FROM libros "
                   + "WHERE id_libro = ? AND eliminado = false";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Libro l = new Libro();
                    l.setIdLibro(rs.getInt("id_libro"));
                    l.setTitulo(rs.getString("titulo"));
                    l.setAutor(rs.getString("autor"));
                    l.setGenero(rs.getString("genero"));
                    l.setCodigoUnico(rs.getString("codigo_unico"));
                    l.setEstado(rs.getString("estado"));
                    l.setUbicacion(rs.getString("ubicacion"));
                    l.setDisponiblePrestamo(rs.getBoolean("disponible_prestamo"));
                    l.setTiempoMaxPrestamo(rs.getInt("tiempo_max_prestamo"));
                    l.setDescripcion(rs.getString("descripcion"));
                    return l;
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al buscar libro por ID: " + ex.getMessage(), ex);
        }
        return null;
    }

    public int insertar(Libro l) {
        String sql = "INSERT INTO libros (titulo, autor, genero, codigo_unico, "
                   + "estado, ubicacion, disponible_prestamo, "
                   + "tiempo_max_prestamo, descripcion) "
                   + "VALUES (?, ?, ?, ?, ?::estado_libro, ?, ?, ?, ?)";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, l.getTitulo());
            ps.setString(2, l.getAutor());
            ps.setString(3, l.getGenero());
            ps.setString(4, l.getCodigoUnico());
            ps.setString(5, l.getEstado());
            ps.setString(6, l.getUbicacion());
            ps.setBoolean(7, l.isDisponiblePrestamo());
            ps.setInt(8, l.getTiempoMaxPrestamo());
            ps.setString(9, l.getDescripcion());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al insertar libro: " + ex.getMessage(), ex);
        }
        return -1;
    }

    public boolean actualizar(Libro l) {
        String sql = "UPDATE libros SET titulo = ?, autor = ?, genero = ?, "
                   + "codigo_unico = ?, estado = ?::estado_libro, "
                   + "ubicacion = ?, disponible_prestamo = ?, "
                   + "tiempo_max_prestamo = ?, descripcion = ? "
                   + "WHERE id_libro = ? AND eliminado = false";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, l.getTitulo());
            ps.setString(2, l.getAutor());
            ps.setString(3, l.getGenero());
            ps.setString(4, l.getCodigoUnico());
            ps.setString(5, l.getEstado());
            ps.setString(6, l.getUbicacion());
            ps.setBoolean(7, l.isDisponiblePrestamo());
            ps.setInt(8, l.getTiempoMaxPrestamo());
            ps.setString(9, l.getDescripcion());
            ps.setInt(10, l.getIdLibro());

            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Error al actualizar libro: " + ex.getMessage(), ex);
        }
    }

    public boolean eliminarLogico(int id) {
        String sql = "UPDATE libros SET eliminado = true WHERE id_libro = ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Error al eliminar libro: " + ex.getMessage(), ex);
        }
    }
}
