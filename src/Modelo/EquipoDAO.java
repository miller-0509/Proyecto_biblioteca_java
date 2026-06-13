package Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EquipoDAO {

    public List<Equipo> listarTodos() {
        List<Equipo> lista = new ArrayList<>();
        String sql = "SELECT id_equipo, nombre, tipo_equipo, marca, numero_serie, "
                   + "estado::text, ubicacion FROM equipos WHERE eliminado = false "
                   + "ORDER BY id_equipo";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Equipo e = new Equipo();
                e.setIdEquipo(rs.getInt("id_equipo"));
                e.setNombre(rs.getString("nombre"));
                e.setTipoEquipo(rs.getString("tipo_equipo"));
                e.setMarca(rs.getString("marca"));
                e.setNumeroSerie(rs.getString("numero_serie"));
                e.setEstado(rs.getString("estado"));
                e.setUbicacion(rs.getString("ubicacion"));
                lista.add(e);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar equipos: " + ex.getMessage(), ex);
        }
        return lista;
    }

    public Equipo buscarPorId(int id) {
        String sql = "SELECT id_equipo, nombre, tipo_equipo, marca, modelo, "
                   + "numero_serie, estado::text, ubicacion, disponible_prestamo, "
                   + "tiempo_max_prestamo, descripcion FROM equipos "
                   + "WHERE id_equipo = ? AND eliminado = false";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Equipo e = new Equipo();
                    e.setIdEquipo(rs.getInt("id_equipo"));
                    e.setNombre(rs.getString("nombre"));
                    e.setTipoEquipo(rs.getString("tipo_equipo"));
                    e.setMarca(rs.getString("marca"));
                    e.setModelo(rs.getString("modelo"));
                    e.setNumeroSerie(rs.getString("numero_serie"));
                    e.setEstado(rs.getString("estado"));
                    e.setUbicacion(rs.getString("ubicacion"));
                    e.setDisponiblePrestamo(rs.getBoolean("disponible_prestamo"));
                    e.setTiempoMaxPrestamo(rs.getInt("tiempo_max_prestamo"));
                    e.setDescripcion(rs.getString("descripcion"));
                    return e;
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al buscar equipo por ID: " + ex.getMessage(), ex);
        }
        return null;
    }

    public int insertar(Equipo e) {
        String sql = "INSERT INTO equipos (nombre, tipo_equipo, marca, modelo, "
                   + "numero_serie, estado, ubicacion, disponible_prestamo, "
                   + "tiempo_max_prestamo, descripcion) "
                   + "VALUES (?, ?, ?, ?, ?, ?::estado_equipo, ?, ?, ?, ?)";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, e.getNombre());
            ps.setString(2, e.getTipoEquipo());
            ps.setString(3, e.getMarca());
            ps.setString(4, e.getModelo());
            ps.setString(5, e.getNumeroSerie());
            ps.setString(6, e.getEstado());
            ps.setString(7, e.getUbicacion());
            ps.setBoolean(8, e.isDisponiblePrestamo());
            ps.setInt(9, e.getTiempoMaxPrestamo());
            ps.setString(10, e.getDescripcion());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al insertar equipo: " + ex.getMessage(), ex);
        }
        return -1;
    }

    public boolean actualizar(Equipo e) {
        String sql = "UPDATE equipos SET nombre = ?, tipo_equipo = ?, marca = ?, "
                   + "modelo = ?, numero_serie = ?, estado = ?::estado_equipo, "
                   + "ubicacion = ?, disponible_prestamo = ?, "
                   + "tiempo_max_prestamo = ?, descripcion = ? "
                   + "WHERE id_equipo = ? AND eliminado = false";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, e.getNombre());
            ps.setString(2, e.getTipoEquipo());
            ps.setString(3, e.getMarca());
            ps.setString(4, e.getModelo());
            ps.setString(5, e.getNumeroSerie());
            ps.setString(6, e.getEstado());
            ps.setString(7, e.getUbicacion());
            ps.setBoolean(8, e.isDisponiblePrestamo());
            ps.setInt(9, e.getTiempoMaxPrestamo());
            ps.setString(10, e.getDescripcion());
            ps.setInt(11, e.getIdEquipo());

            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Error al actualizar equipo: " + ex.getMessage(), ex);
        }
    }

    public boolean eliminarLogico(int id) {
        String sql = "UPDATE equipos SET eliminado = true WHERE id_equipo = ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Error al eliminar equipo: " + ex.getMessage(), ex);
        }
    }
}
