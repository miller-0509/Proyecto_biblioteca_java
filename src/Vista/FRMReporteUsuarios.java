package Vista;

import Controlador.UsuarioControlador;
import Modelo.Usuario;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;

import Modelo.ConexionDB;

public class FRMReporteUsuarios extends JInternalFrame {

    private static final Color SENA_GREEN = new Color(46, 170, 84);
    private static final Color TEXT_DARK = new Color(28, 34, 45);
    private static final Color TEXT_SOFT = new Color(96, 105, 121);

    private DefaultTableModel modeloTabla;
    private JTable tabla;
    private JTextField txtBuscar;

    public FRMReporteUsuarios() {
        super("Reporte de Usuarios Activos", true, true, true, true);
        construirVista();
        cargarDatos();
    }

    private void construirVista() {
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);

        JPanel raiz = new FondoInternoPanel();
        raiz.setLayout(new BorderLayout(0, 18));
        raiz.setBorder(new EmptyBorder(20, 22, 22, 22));
        raiz.add(crearEncabezado(), BorderLayout.NORTH);
        raiz.add(crearContenido(), BorderLayout.CENTER);
        setContentPane(raiz);
        pack();
    }

    private JPanel crearEncabezado() {
        JPanel cont = new JPanel(new BorderLayout(16, 10));
        cont.setOpaque(false);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Usuarios Activos");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 26));
        titulo.setForeground(TEXT_DARK);

        JLabel subtitulo = new JLabel("Listado de usuarios con cantidad de préstamos, ordenados de mayor a menor.");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitulo.setForeground(TEXT_SOFT);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(subtitulo);

        cont.add(textos, BorderLayout.WEST);
        return cont;
    }

    private JPanel crearContenido() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);

        JPanel filtroPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        filtroPanel.setOpaque(false);

        JLabel lblBuscar = new JLabel("BUSCAR USUARIO");
        lblBuscar.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblBuscar.setForeground(TEXT_SOFT);

        txtBuscar = new JTextField(25);
        txtBuscar.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 220)),
                new EmptyBorder(8, 10, 8, 10)));
        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { filtrar(); }
        });

        JPanel colBuscar = new JPanel();
        colBuscar.setOpaque(false);
        colBuscar.setLayout(new BoxLayout(colBuscar, BoxLayout.Y_AXIS));
        colBuscar.add(lblBuscar);
        colBuscar.add(Box.createVerticalStrut(4));
        colBuscar.add(txtBuscar);

        filtroPanel.add(colBuscar);
        panel.add(filtroPanel, BorderLayout.NORTH);

        String[] cols = {"ID", "NOMBRE COMPLETO", "CORREO", "ROL", "ESTADO", "PRÉSTAMOS EQUIPOS", "PRÉSTAMOS LIBROS"};
        modeloTabla = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(36);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tabla.getTableHeader().setForeground(TEXT_SOFT);
        tabla.getTableHeader().setBackground(new Color(248, 250, 252));
        tabla.getTableHeader().setPreferredSize(new Dimension(0, 40));
        tabla.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabla.setSelectionBackground(new Color(240, 252, 244));
        tabla.setSelectionForeground(TEXT_DARK);

        TableColumnModel tcm = tabla.getColumnModel();
        tcm.getColumn(0).setPreferredWidth(40);
        tcm.getColumn(1).setPreferredWidth(180);
        tcm.getColumn(2).setPreferredWidth(200);
        tcm.getColumn(3).setPreferredWidth(100);
        tcm.getColumn(4).setPreferredWidth(80);
        tcm.getColumn(5).setPreferredWidth(120);
        tcm.getColumn(6).setPreferredWidth(120);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private void cargarDatos() {
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() {
                String sql =
                    "SELECT u.id_usuario, u.nombres, u.apellidos, u.correo, u.rol, u.estado, "
                  + "(SELECT COUNT(*) FROM prestamos WHERE id_usuario = u.id_usuario) AS total_equipos, "
                  + "(SELECT COUNT(*) FROM prestamos_libros WHERE id_usuario = u.id_usuario) AS total_libros "
                  + "FROM usuarios u "
                  + "WHERE u.estado = 'activo' "
                  + "ORDER BY (SELECT COUNT(*) FROM prestamos WHERE id_usuario = u.id_usuario) "
                  + "+ (SELECT COUNT(*) FROM prestamos_libros WHERE id_usuario = u.id_usuario) DESC";

                try (Connection con = ConexionDB.conectar();
                     PreparedStatement ps = con.prepareStatement(sql);
                     ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        modeloTabla.addRow(new Object[]{
                            rs.getInt("id_usuario"),
                            rs.getString("nombres") + " " + rs.getString("apellidos"),
                            rs.getString("correo"),
                            rs.getString("rol"),
                            rs.getString("estado"),
                            rs.getInt("total_equipos"),
                            rs.getInt("total_libros")
                        });
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
                return null;
            }
        }.execute();
    }

    private void filtrar() {
        String texto = txtBuscar.getText().trim();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modeloTabla);
        if (!texto.isEmpty()) {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(texto)));
        }
        tabla.setRowSorter(sorter);
    }

    private static class FondoInternoPanel extends JPanel {
        private static final Image FONDO;
        static {
            Image img = null;
            try { img = new ImageIcon(FRMReporteUsuarios.class.getResource("/imagenes/fondo.jpg")).getImage(); } catch (Exception e) {}
            FONDO = img;
        }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (FONDO != null) {
                int w = getWidth(), h = getHeight();
                double s = Math.max((double) w / FONDO.getWidth(null), (double) h / FONDO.getHeight(null));
                g2.drawImage(FONDO, (w - (int)(FONDO.getWidth(null)*s))/2, (h - (int)(FONDO.getHeight(null)*s))/2, (int)(FONDO.getWidth(null)*s), (int)(FONDO.getHeight(null)*s), null);
            } else {
                g2.setPaint(new GradientPaint(0, 0, new Color(248, 250, 252), 0, getHeight(), new Color(235, 243, 238)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
            g2.setComposite(AlphaComposite.SrcOver.derive(0.72f));
            g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 235), 0, getHeight(), new Color(248, 248, 248, 224)));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }
}
