package Modelo;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class ModeloSancion {

    private static final String SQL_BASE =
            "SELECT * FROM sanciones ";

    public List<Sancion> listarTodas() {
        List<Sancion> lista = new ArrayList<>();
        String sql = SQL_BASE + "ORDER BY id_sancion";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement psSchema = con.prepareStatement(
                     "SELECT table_schema, table_name FROM information_schema.tables WHERE table_name = 'sanciones'");
             ResultSet rsSchema = psSchema.executeQuery();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) AS total FROM sanciones");
             ResultSet rsCount = ps.executeQuery()) {
            while (rsSchema.next()) {
                System.out.println("[ModeloSancion] Tabla encontrada: "
                        + rsSchema.getString("table_schema") + "." + rsSchema.getString("table_name"));
            }
            int total = 0;
            if (rsCount.next()) {
                total = rsCount.getInt("total");
            }
            System.out.println("[ModeloSancion] COUNT sanciones = " + total);

            try (PreparedStatement psEstado = con.prepareStatement(
                    "SELECT estado, COUNT(*) AS total FROM sanciones GROUP BY estado ORDER BY estado");
                 ResultSet rsEstado = psEstado.executeQuery()) {
                System.out.println("[ModeloSancion] Conteo por estado:");
                while (rsEstado.next()) {
                    System.out.println("  estado=" + rsEstado.getString("estado")
                            + " total=" + rsEstado.getInt("total"));
                }
            }
        } catch (SQLException ex) {
            System.out.println("[ModeloSancion] COUNT/schema fallo: " + ex.getMessage());
            ex.printStackTrace();
        }

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("[ModeloSancion] SQL listarTodas = " + sql);
            System.out.println("[ModeloSancion] Columnas devueltas:");
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                System.out.println("  - " + rs.getMetaData().getColumnLabel(i));
            }
            while (rs.next()) {
                Sancion sancion = mapearSancion(rs);
                System.out.println("[ModeloSancion] Sancion encontrada: " + sancion.getIdSancion());
                lista.add(sancion);
            }
            System.out.println("[ModeloSancion] Cantidad: " + lista.size());
        } catch (SQLException ex) {
            System.out.println("[ModeloSancion] listarTodas fallo: " + ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException("Error al listar sanciones: " + ex.getMessage(), ex);
        }
        return lista;
    }

    public List<Sancion> buscar(String texto, String estado) {
        List<Sancion> lista = new ArrayList<>();
        String sql = SQL_BASE + "ORDER BY id_sancion";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("[ModeloSancion] SQL buscar = " + sql);
            while (rs.next()) {
                Sancion sancion = mapearSancion(rs);
                System.out.println("[ModeloSancion] Sancion encontrada: " + sancion.getIdSancion());
                lista.add(sancion);
            }
            System.out.println("[ModeloSancion] Cantidad: " + lista.size());
        } catch (SQLException ex) {
            System.out.println("[ModeloSancion] buscar fallo: " + ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException("Error al buscar sanciones: " + ex.getMessage(), ex);
        }
        return lista;
    }

    public Sancion buscarPorId(int idSancion) {
        String sql = SQL_BASE + "WHERE id_sancion = ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idSancion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearSancion(rs);
                }
            }
        } catch (SQLException ex) {
            System.out.println("[ModeloSancion] buscarPorId fallo: " + ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException("Error al buscar sancion: " + ex.getMessage(), ex);
        }
        return null;
    }

    public int insertar(Sancion s) {
        String sql = "INSERT INTO sanciones (id_prestamo, id_usuario, fecha_sancion, correo_usuario, "
                   + "tipo_recurso, nombre_recurso, dias_retraso, dias_suspension, estado, detalle, "
                   + "fecha_inicio_suspension, fecha_fin_suspension, condonada) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?::text, ?, ?, ?, ?)";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setObject(1, s.getIdPrestamo());
            ps.setObject(2, s.getIdUsuario());
            ps.setDate(3, s.getFechaSancion() != null ? s.getFechaSancion() : Date.valueOf(LocalDate.now()));
            ps.setString(4, s.getCorreoUsuario());
            ps.setString(5, s.getTipoRecurso());
            ps.setString(6, s.getNombreRecurso());
            ps.setInt(7, s.getDiasRetraso());
            ps.setInt(8, s.getDiasSuspension());
            ps.setString(9, s.getEstado() != null ? s.getEstado() : "activa");
            ps.setString(10, s.getDetalle());
            ps.setDate(11, s.getFechaInicioSuspension());
            ps.setDate(12, s.getFechaFinSuspension());
            ps.setObject(13, s.getCondonada());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            System.out.println("[ModeloSancion] insertar fallo: " + ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException("Error al insertar sancion: " + ex.getMessage(), ex);
        }
        return -1;
    }

    public boolean actualizar(Sancion s) {
        String sql = "UPDATE sanciones SET id_prestamo = ?, id_usuario = ?, fecha_sancion = ?, correo_usuario = ?, "
                   + "tipo_recurso = ?, nombre_recurso = ?, dias_retraso = ?, dias_suspension = ?, estado = ?::text, "
                   + "detalle = ?, fecha_inicio_suspension = ?, fecha_fin_suspension = ?, condonada = ? "
                   + "WHERE id_sancion = ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, s.getIdPrestamo());
            ps.setObject(2, s.getIdUsuario());
            ps.setDate(3, s.getFechaSancion());
            ps.setString(4, s.getCorreoUsuario());
            ps.setString(5, s.getTipoRecurso());
            ps.setString(6, s.getNombreRecurso());
            ps.setInt(7, s.getDiasRetraso());
            ps.setInt(8, s.getDiasSuspension());
            ps.setString(9, s.getEstado());
            ps.setString(10, s.getDetalle());
            ps.setDate(11, s.getFechaInicioSuspension());
            ps.setDate(12, s.getFechaFinSuspension());
            ps.setObject(13, s.getCondonada());
            ps.setInt(14, s.getIdSancion());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println("[ModeloSancion] actualizar fallo: " + ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException("Error al actualizar sancion: " + ex.getMessage(), ex);
        }
    }

    public boolean condonar(int idSancion) {
        String sql = "UPDATE sanciones SET estado = 'condonada', condonada = true WHERE id_sancion = ?";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idSancion);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println("[ModeloSancion] condonar fallo: " + ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException("Error al condonar sancion: " + ex.getMessage(), ex);
        }
    }

    public Sancion registrarAutomaticamente(
            Integer idPrestamo,
            Integer idUsuario,
            String correoUsuario,
            String tipoRecurso,
            String nombreRecurso,
            Date fechaLimite,
            Date fechaDevolucion) {

        if (fechaLimite == null || fechaDevolucion == null) {
            return null;
        }

        long diasRetraso = ChronoUnit.DAYS.between(fechaLimite.toLocalDate(), fechaDevolucion.toLocalDate());
        if (diasRetraso <= 0) {
            return null;
        }

        Sancion s = new Sancion();
        s.setIdPrestamo(idPrestamo);
        s.setIdUsuario(idUsuario);
        s.setFechaSancion(fechaDevolucion);
        s.setCorreoUsuario(correoUsuario);
        s.setTipoRecurso(tipoRecurso);
        s.setNombreRecurso(nombreRecurso);
        s.setDiasRetraso((int) diasRetraso);
        s.setDiasSuspension((int) diasRetraso);
        s.setEstado("activa");
        s.setDetalle("Retraso de " + diasRetraso + " dia(s) en la devolucion.");
        s.setFechaInicioSuspension(fechaDevolucion);
        s.setFechaFinSuspension(Date.valueOf(fechaDevolucion.toLocalDate().plusDays(diasRetraso)));
        s.setCondonada(false);
        s.setIdSancion(insertar(s));
        return s;
    }

    private Sancion mapearSancion(ResultSet rs) throws SQLException {
        Sancion s = new Sancion();
        s.setIdSancion(rs.getInt("id_sancion"));
        int idPrestamo = rs.getInt("id_prestamo");
        s.setIdPrestamo(rs.wasNull() ? null : idPrestamo);
        int idUsuario = rs.getInt("id_usuario");
        s.setIdUsuario(rs.wasNull() ? null : idUsuario);
        s.setFechaSancion(rs.getDate("fecha_sancion"));
        s.setCorreoUsuario(rs.getString("correo_usuario"));
        s.setTipoRecurso(rs.getString("tipo_recurso"));
        s.setNombreRecurso(rs.getString("nombre_recurso"));
        s.setDiasRetraso(rs.getInt("dias_retraso"));
        s.setDiasSuspension(rs.getInt("dias_suspension"));
        s.setEstado(rs.getString("estado"));
        s.setDetalle(rs.getString("detalle"));
        s.setFechaInicioSuspension(rs.getDate("fecha_inicio_suspension"));
        s.setFechaFinSuspension(rs.getDate("fecha_fin_suspension"));
        Object cond = rs.getObject("condonada");
        s.setCondonada(cond != null ? rs.getBoolean("condonada") : null);
        return s;
    }
}
