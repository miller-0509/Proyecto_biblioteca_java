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
            "SELECT m.*, u.nombres AS usuario_nombres, u.apellidos AS usuario_apellidos, u.correo AS usuario_correo "
          + "FROM multas m LEFT JOIN usuarios u ON u.id_usuario = m.id_usuario ";

    public List<Sancion> listarTodas() {
        List<Sancion> lista = new ArrayList<>();
        String sql = SQL_BASE + "ORDER BY m.id_multa";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement psSchema = con.prepareStatement(
                     "SELECT table_schema, table_name FROM information_schema.tables WHERE table_name = 'multas'");
             ResultSet rsSchema = psSchema.executeQuery();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) AS total FROM multas");
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
                    "SELECT estado, COUNT(*) AS total FROM multas GROUP BY estado ORDER BY estado");
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
        List<Sancion> base = listarTodas();
        List<Sancion> lista = new ArrayList<>();
        String textoNorm = texto != null ? texto.trim().toLowerCase() : "";
        String estadoNorm = estado != null ? estado.trim().toLowerCase() : "todos";
        boolean filtraTexto = !textoNorm.isEmpty();
        boolean filtraEstado = !estadoNorm.isEmpty() && !"todos".equals(estadoNorm);

        for (Sancion s : base) {
            boolean coincideTexto = true;
            boolean coincideEstado = true;

            if (filtraTexto) {
                String combinado = (String.valueOf(s.getIdSancion()) + " "
                        + valueOrEmpty(s.getCorreoUsuario()) + " "
                        + valueOrEmpty(s.getDetalle()) + " "
                        + valueOrEmpty(s.getTipoRecurso()) + " "
                        + valueOrEmpty(s.getNombreRecurso()) + " "
                        + valueOrEmpty(s.getEstado())).toLowerCase();
                coincideTexto = combinado.contains(textoNorm);
            }

            if (filtraEstado) {
                coincideEstado = valueOrEmpty(s.getEstado()).toLowerCase().equals(estadoNorm);
            }

            if (coincideTexto && coincideEstado) {
                lista.add(s);
            }
        }

        System.out.println("[ModeloSancion] buscar: registros filtrados = " + lista.size());
        return lista;
    }

    public Sancion buscarPorId(int idSancion) {
        String sql = "SELECT m.*, u.nombres AS usuario_nombres, u.apellidos AS usuario_apellidos, u.correo AS usuario_correo "
                   + "FROM multas m LEFT JOIN usuarios u ON u.id_usuario = m.id_usuario WHERE m.id_multa = ?";

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
        String sql = "INSERT INTO multas (id_prestamo, id_usuario, fecha_sancion, correo_usuario, "
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
        String sql = "UPDATE multas SET id_prestamo = ?, id_usuario = ?, fecha_sancion = ?, correo_usuario = ?, "
                   + "tipo_recurso = ?, nombre_recurso = ?, dias_retraso = ?, dias_suspension = ?, estado = ?::text, "
                   + "detalle = ?, fecha_inicio_suspension = ?, fecha_fin_suspension = ?, condonada = ? "
                   + "WHERE id_multa = ?";

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
        String sql = "UPDATE multas SET estado = 'condonada', condonada = true WHERE id_multa = ?";
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

    public boolean eliminar(int idSancion) {
        String sql = "DELETE FROM multas WHERE id_multa = ?";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idSancion);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println("[ModeloSancion] eliminar fallo: " + ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException("Error al eliminar sancion: " + ex.getMessage(), ex);
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
        Integer idSancion = getIntFlexible(rs, "id_multa", "id_sancion");
        s.setIdSancion(idSancion != null ? idSancion : 0);
        Integer idPrestamo = getIntFlexible(rs, "id_prestamo");
        s.setIdPrestamo(idPrestamo);
        Integer idUsuario = getIntFlexible(rs, "id_usuario", "id_usuario_sancion");
        s.setIdUsuario(idUsuario);
        s.setFechaSancion(getDateFlexible(rs, "fecha_sancion", "fecha_creacion", "fecha_registro", "fecha", "created_at", "updated_at"));
        if (s.getFechaSancion() == null) {
            s.setFechaSancion(getFirstDateColumn(rs));
        }
        s.setCorreoUsuario(getStringFlexible(rs, "usuario_correo", "correo_usuario", "correo"));
        s.setTipoRecurso(getStringFlexible(rs, "tipo_recurso", "recurso_tipo", "tipo"));
        s.setNombreRecurso(getStringFlexible(rs, "nombre_recurso", "recurso_nombre", "nombre"));
        s.setDiasRetraso(getIntFlexible(rs, "dias_retraso", "retraso_dias"));
        s.setDiasSuspension(getIntFlexible(rs, "dias_suspension", "suspension_dias"));
        s.setEstado(getStringFlexible(rs, "estado", "estado_multa"));
        s.setDetalle(getStringFlexible(rs, "detalle", "observacion", "descripcion"));
        s.setFechaInicioSuspension(getDateFlexible(rs, "fecha_inicio_suspension", "fecha_inicio"));
        s.setFechaFinSuspension(getDateFlexible(rs, "fecha_fin_suspension", "fecha_fin"));
        Object cond = getObjectFlexible(rs, "condonada", "es_condonada");
        s.setCondonada(cond != null ? Boolean.valueOf(String.valueOf(cond)) : null);
        return s;
    }

    private String getStringFlexible(ResultSet rs, String... names) throws SQLException {
        for (String name : names) {
            try {
                return rs.getString(name);
            } catch (SQLException ex) {
                // probar siguiente nombre
            }
        }
        return null;
    }

    private Integer getIntFlexible(ResultSet rs, String... names) throws SQLException {
        for (String name : names) {
            try {
                int value = rs.getInt(name);
                if (!rs.wasNull()) {
                    return Integer.valueOf(value);
                }
            } catch (SQLException ex) {
                // probar siguiente nombre
            }
        }
        return null;
    }

    private Date getDateFlexible(ResultSet rs, String... names) throws SQLException {
        for (String name : names) {
            try {
                Date value = rs.getDate(name);
                if (value != null) {
                    return value;
                }
            } catch (SQLException ex) {
                // probar siguiente nombre
            }
        }
        return null;
    }

    private Date getFirstDateColumn(ResultSet rs) throws SQLException {
        int cols = rs.getMetaData().getColumnCount();
        for (int i = 1; i <= cols; i++) {
            try {
                String label = rs.getMetaData().getColumnLabel(i).toLowerCase();
                if (label.contains("fecha") || label.contains("date") || label.contains("time")) {
                    Date d = rs.getDate(i);
                    if (d != null) {
                        System.out.println("[ModeloSancion] Fecha detectada en columna: " + label + " = " + d);
                        return d;
                    }
                }
            } catch (SQLException ex) {
                // seguir buscando
            }
        }
        return null;
    }

    private Object getObjectFlexible(ResultSet rs, String... names) throws SQLException {
        for (String name : names) {
            try {
                return rs.getObject(name);
            } catch (SQLException ex) {
                // probar siguiente nombre
            }
        }
        return null;
    }

    private String valueOrEmpty(String value) {
        return value != null ? value : "";
    }
}
