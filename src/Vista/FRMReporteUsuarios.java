package Vista;

import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.*;

import Modelo.ConexionDB;

public class FRMReporteUsuarios extends JInternalFrame {

    private static final Color SENA_GREEN = new Color(46, 170, 84);
    private static final Color SENA_GREEN_DARK = new Color(25, 120, 60);
    private static final Color SENA_RED = new Color(217, 70, 70);
    private static final Color TEXT_DARK = new Color(28, 34, 45);
    private static final Color TEXT_SOFT = new Color(96, 105, 121);

    private DefaultTableModel modeloTabla;
    private JTable tabla;
    private JTextField txtBuscar;
    private JLabel lblConteo;

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
        raiz.setLayout(new BorderLayout(0, 0));
        raiz.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel contenido = new JPanel(new BorderLayout(0, 8));
        contenido.setOpaque(false);

        JPanel superior = new JPanel(new BorderLayout(0, 8));
        superior.setOpaque(false);
        superior.add(crearHeader(), BorderLayout.NORTH);
        superior.add(crearFiltros(), BorderLayout.SOUTH);
        contenido.add(superior, BorderLayout.NORTH);
        contenido.add(crearTabla(), BorderLayout.CENTER);

        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 230));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(new Color(223, 228, 234));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(16, 24, 16, 24));
        card.add(contenido, BorderLayout.CENTER);

        raiz.add(card, BorderLayout.CENTER);
        setContentPane(raiz);
        pack();
    }

    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout(16, 8));
        header.setOpaque(false);

        JPanel tituloPanel = new JPanel();
        tituloPanel.setOpaque(false);
        tituloPanel.setLayout(new BoxLayout(tituloPanel, BoxLayout.Y_AXIS));

        JLabel icono = new JLabel("\uD83D\uDC65");
        icono.setFont(new Font("SansSerif", Font.PLAIN, 20));

        JLabel titulo = new JLabel("  Usuarios Activos");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setForeground(TEXT_DARK);

        JPanel filaTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        filaTitulo.setOpaque(false);
        filaTitulo.add(icono);
        filaTitulo.add(titulo);

        JLabel subtitulo = new JLabel("Listado de usuarios con cantidad de préstamos, ordenados de mayor a menor.");
        subtitulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        subtitulo.setForeground(SENA_GREEN);
        subtitulo.setBorder(new EmptyBorder(8, 0, 0, 0));

        tituloPanel.add(filaTitulo);
        tituloPanel.add(subtitulo);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botones.setOpaque(false);

        JButton btnVolver = new JButton("Volver");
        btnVolver.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnVolver.setForeground(TEXT_SOFT);
        btnVolver.setBackground(Color.WHITE);
        btnVolver.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 220)),
                new EmptyBorder(8, 20, 8, 20)));
        btnVolver.setFocusPainted(false);
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.addActionListener(e -> {
            try { setClosed(true); } catch (Exception ex) {}
        });

        JButton btnExcel = crearBotonExportar("Exportar Excel", SENA_GREEN, Color.WHITE);
        btnExcel.addActionListener(e -> exportarExcel());

        JButton btnPdf = crearBotonExportar("Exportar PDF", SENA_RED, Color.WHITE);
        btnPdf.addActionListener(e -> exportarPDF());

        botones.add(btnVolver);
        botones.add(btnExcel);
        botones.add(btnPdf);

        header.add(tituloPanel, BorderLayout.WEST);
        header.add(botones, BorderLayout.EAST);
        return header;
    }

    private JButton crearBotonExportar(String texto, Color bg, Color fg) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 18, 10, 18));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(bg.darker()); }
            @Override public void mouseExited(MouseEvent e) { btn.setBackground(bg); }
        });
        return btn;
    }

    private JPanel crearFiltros() {
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 8)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(250, 252, 253));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(new Color(223, 228, 234));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                g2.dispose();
            }
        };
        filtros.setOpaque(false);
        filtros.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel lblBuscar = new JLabel("BUSCAR USUARIO");
        lblBuscar.setFont(new Font("SansSerif", Font.BOLD, 10));
        lblBuscar.setForeground(TEXT_SOFT);

        txtBuscar = new JTextField(25);
        txtBuscar.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtBuscar.setPreferredSize(new Dimension(300, 36));
        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { filtrar(); }
        });

        JPanel pnlBuscar = new JPanel();
        pnlBuscar.setOpaque(false);
        pnlBuscar.setLayout(new BoxLayout(pnlBuscar, BoxLayout.Y_AXIS));
        pnlBuscar.add(lblBuscar);
        pnlBuscar.add(Box.createVerticalStrut(4));
        pnlBuscar.add(txtBuscar);
        filtros.add(pnlBuscar);

        JButton btnFiltrar = new JButton("\uD83D\uDD0D Filtrar");
        btnFiltrar.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnFiltrar.setForeground(Color.WHITE);
        btnFiltrar.setBackground(SENA_GREEN);
        btnFiltrar.setFocusPainted(false);
        btnFiltrar.setBorderPainted(false);
        btnFiltrar.setOpaque(true);
        btnFiltrar.setPreferredSize(new Dimension(140, 36));
        btnFiltrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFiltrar.setBorder(new EmptyBorder(0, 12, 0, 12));
        btnFiltrar.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btnFiltrar.setBackground(SENA_GREEN_DARK); }
            @Override public void mouseExited(MouseEvent e) { btnFiltrar.setBackground(SENA_GREEN); }
        });
        btnFiltrar.addActionListener(e -> filtrar());
        filtros.add(btnFiltrar);

        return filtros;
    }

    private JPanel crearTabla() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);

        JPanel headerTabla = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        headerTabla.setOpaque(false);
        headerTabla.add(new JLabel("\uD83D\uDC65"));
        headerTabla.add(new JLabel("Todos los Usuarios"));
        lblConteo = new JLabel("0");
        lblConteo.setOpaque(true);
        lblConteo.setBackground(new Color(230, 235, 240));
        lblConteo.setForeground(TEXT_SOFT);
        lblConteo.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblConteo.setBorder(new EmptyBorder(2, 8, 2, 8));
        headerTabla.add(lblConteo);
        panel.add(headerTabla, BorderLayout.NORTH);

        String[] cols = {"ID", "NOMBRE COMPLETO", "CORREO", "ROL", "ESTADO", "PRÉSTAMOS EQUIPOS", "PRÉSTAMOS LIBROS"};
        modeloTabla = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modeloTabla);
        configurarTabla();

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private void configurarTabla() {
        tabla.setRowHeight(42);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));
        tabla.getTableHeader().setForeground(TEXT_SOFT);
        tabla.getTableHeader().setBackground(new Color(248, 250, 252));
        tabla.getTableHeader().setPreferredSize(new Dimension(0, 40));
        tabla.getTableHeader().setBorder(new MatteBorder(0, 0, 1, 0, new Color(223, 228, 234)));
        tabla.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabla.setSelectionBackground(new Color(240, 252, 244));
        tabla.setSelectionForeground(TEXT_DARK);
        tabla.setGridColor(new Color(245, 247, 250));

        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                setFont(new Font("SansSerif", Font.PLAIN, 13));

                if (!sel && col == 4) {
                    String estado = val != null ? val.toString().toLowerCase() : "";
                    setHorizontalAlignment(CENTER);
                    if (estado.equals("activo")) {
                        setBackground(new Color(220, 252, 231));
                        setForeground(new Color(22, 163, 74));
                        setFont(new Font("SansSerif", Font.BOLD, 11));
                        setText("ACTIVO");
                    } else {
                        setBackground(sel ? t.getSelectionBackground() : t.getBackground());
                        setForeground(TEXT_DARK);
                        setHorizontalAlignment(LEFT);
                        setFont(new Font("SansSerif", Font.PLAIN, 13));
                    }
                    return c;
                }

                if (!sel) {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251));
                }
                setHorizontalAlignment(LEFT);
                return c;
            }
        });

        TableColumnModel tcm = tabla.getColumnModel();
        tcm.getColumn(0).setPreferredWidth(40);
        tcm.getColumn(1).setPreferredWidth(180);
        tcm.getColumn(2).setPreferredWidth(200);
        tcm.getColumn(3).setPreferredWidth(100);
        tcm.getColumn(4).setPreferredWidth(80);
        tcm.getColumn(5).setPreferredWidth(120);
        tcm.getColumn(6).setPreferredWidth(120);
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
            @Override protected void done() {
                try {
                    get();
                    lblConteo.setText(String.valueOf(modeloTabla.getRowCount()));
                } catch (Exception ex) { ex.printStackTrace(); }
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

    private void exportarExcel() {
        ExportUtil.exportarCSVSimple(modeloTabla, "--- USUARIOS ACTIVOS ---", this);
    }

    private void exportarPDF() {
        ExportUtil.exportarPDFSimple(modeloTabla,
                "Reporte de Usuarios Activos", "Usuarios Activos - " +
                new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date()), this);
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
