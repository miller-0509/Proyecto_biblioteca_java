package Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO {

    private static final String SQL_LISTAR_EQUIPOS =
        "SELECT p.id_prestamo, p.id_usuario, p.id_equipo, p.fecha_solicitud, "
      + "p.fecha_devolucion_real, p.fecha_devolucion_esperada, p.estado::text, p.observaciones, "
      + "u.nombres AS usuario_nombres, u.apellidos AS usuario_apellidos, "
      + "e.nombre AS equipo_nombre, e.tipo_equipo "
      + "FROM prestamos p "
      + "LEFT JOIN usuarios u ON u.id_usuario = p.id_usuario "
      + "LEFT JOIN equipos e ON e.id_equipo = p.id_equipo "
      + "ORDER BY p.id_prestamo DESC";

    private static final String SQL_LISTAR_LIBROS =
        "SELECT p.id_prestamo_libro AS id_prestamo, p.id_usuario, p.id_libro, p.fecha_solicitud, "
      + "p.fecha_devolucion_real, p.fecha_devolucion_esperada, p.estado::text, p.observaciones, "
      + "u.nombres AS usuario_nombres, u.apellidos AS usuario_apellidos, "
      + "l.titulo AS libro_titulo, l.genero AS libro_genero "
      + "FROM prestamos_libros p "
      + "LEFT JOIN usuarios u ON u.id_usuario = p.id_usuario "
      + "LEFT JOIN libros l ON l.id_libro = p.id_libro "
      + "ORDER BY p.id_prestamo_libro DESC";

    public List<Prestamo> listarEquipos() {
        return listar(SQL_LISTAR_EQUIPOS);
    }

    public List<Prestamo> listarLibros() {
        return listar(SQL_LISTAR_LIBROS);
    }

    private List<Prestamo> listar(String sql) {
        List<Prestamo> lista = new ArrayList<>();

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar prestamos: " + ex.getMessage(), ex);
        }
        return lista;
    }

    public int insertarEquipo(Prestamo p) {
        String sql = "INSERT INTO prestamos (id_usuario, id_equipo, fecha_solicitud, "
                   + "fecha_devolucion_esperada, estado, observaciones) "
                   + "VALUES (?, ?, ?, ?, 'pendiente'::estado_prestamo, ?)";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, p.getIdUsuario());
            ps.setInt(2, p.getIdEquipo());
            ps.setTimestamp(3, p.getFechaSolicitud() != null ? p.getFechaSolicitud() : new Timestamp(System.currentTimeMillis()));
            ps.setTimestamp(4, p.getFechaLimite());
            ps.setString(5, p.getObservaciones());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al insertar prestamo de equipo: " + ex.getMessage(), ex);
        }
        return -1;
    }

    public int insertarLibro(Prestamo p) {
        String sql = "INSERT INTO prestamos_libros (id_usuario, id_libro, fecha_solicitud, "
                   + "fecha_devolucion_esperada, estado, observaciones) "
                   + "VALUES (?, ?, ?, ?, 'pendiente'::estado_prestamo_libro, ?)";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, p.getIdUsuario());
            ps.setInt(2, p.getIdLibro());
            ps.setTimestamp(3, p.getFechaSolicitud() != null ? p.getFechaSolicitud() : new Timestamp(System.currentTimeMillis()));
            ps.setTimestamp(4, p.getFechaLimite());
            ps.setString(5, p.getObservaciones());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al insertar prestamo de libro: " + ex.getMessage(), ex);
        }
        return -1;
    }

    public boolean devolverEquipo(int idPrestamo) {
        String sql = "UPDATE prestamos SET estado = 'devuelto'::estado_prestamo, "
                   + "fecha_devolucion_real = NOW() WHERE id_prestamo = ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idPrestamo);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Error al devolver prestamo de equipo: " + ex.getMessage(), ex);
        }
    }

    public boolean devolverLibro(int idPrestamo) {
        String sql = "UPDATE prestamos_libros SET estado = 'devuelto'::estado_prestamo_libro, "
                   + "fecha_devolucion_real = NOW() WHERE id_prestamo_libro = ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idPrestamo);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Error al devolver prestamo de libro: " + ex.getMessage(), ex);
        }
    }

    private Prestamo mapear(ResultSet rs) throws SQLException {
        Prestamo p = new Prestamo();
        p.setIdPrestamo(rs.getInt("id_prestamo"));
        p.setIdUsuario(rs.getInt("id_usuario"));
        p.setFechaSolicitud(rs.getTimestamp("fecha_solicitud"));
        p.setFechaDevolucion(rs.getTimestamp("fecha_devolucion_real"));
        p.setFechaLimite(rs.getTimestamp("fecha_devolucion_esperada"));
        p.setEstado(rs.getString("estado"));
        p.setObservaciones(rs.getString("observaciones"));
        p.setNombreUsuario(rs.getString("usuario_nombres"));
        p.setApellidoUsuario(rs.getString("usuario_apellidos"));

        try { p.setIdEquipo(rs.getInt("id_equipo")); } catch (SQLException e) {}
        try {
            p.setNombreEquipo(rs.getString("equipo_nombre"));
            p.setTipoEquipo(rs.getString("tipo_equipo"));
        } catch (SQLException e) {}
        try {
            p.setIdLibro(rs.getInt("id_libro"));
        } catch (SQLException e) {}
        try {
            p.setNombreLibro(rs.getString("libro_titulo"));
            p.setGeneroLibro(rs.getString("libro_genero"));
        } catch (SQLException e) {}

        return p;
    }
}
