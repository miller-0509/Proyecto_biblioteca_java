package Vista;

import Controlador.PrestamoControlador;
import Modelo.Prestamo;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.*;

public class FRMReportePrestamos extends JInternalFrame {

    private static final Color SENA_GREEN = new Color(46, 170, 84);
    private static final Color SENA_GREEN_DARK = new Color(25, 120, 60);
    private static final Color SENA_RED = new Color(217, 70, 70);
    private static final Color TEXT_DARK = new Color(28, 34, 45);
    private static final Color TEXT_SOFT = new Color(96, 105, 121);

    private PrestamoControlador controlador;
    private DefaultTableModel modeloEquipos;
    private DefaultTableModel modeloLibros;
    private JTable tablaEquipos;
    private JTable tablaLibros;
    private JTextField txtBuscar;
    private JComboBox<String> cmbEstado;
    private JTabbedPane tabbedPane;
    private JLabel lblConteoEquipos;
    private JLabel lblConteoLibros;

    public FRMReportePrestamos() {
        super("Reporte de Préstamos Realizados", true, true, true, true);
        controlador = new PrestamoControlador();
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
        contenido.add(crearTabs(), BorderLayout.CENTER);

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

        JLabel icono = new JLabel("\u21C4");
        icono.setFont(new Font("SansSerif", Font.PLAIN, 20));

        JLabel titulo = new JLabel("  Préstamos Realizados");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setForeground(TEXT_DARK);

        JPanel filaTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        filaTitulo.setOpaque(false);
        filaTitulo.add(icono);
        filaTitulo.add(titulo);

        JLabel subtitulo = new JLabel("Historial completo de préstamos de equipos y libros.");
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

        JLabel lblBuscar = new JLabel("BUSCAR");
        lblBuscar.setFont(new Font("SansSerif", Font.BOLD, 10));
        lblBuscar.setForeground(TEXT_SOFT);

        txtBuscar = new JTextField(18);
        txtBuscar.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtBuscar.setPreferredSize(new Dimension(200, 36));
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

        JLabel lblEstado = new JLabel("ESTADO");
        lblEstado.setFont(new Font("SansSerif", Font.BOLD, 10));
        lblEstado.setForeground(TEXT_SOFT);

        cmbEstado = new JComboBox<>(new String[]{"Todos", "pendiente", "aceptado", "rechazado", "devuelto"});
        cmbEstado.setFont(new Font("SansSerif", Font.PLAIN, 13));
        cmbEstado.setPreferredSize(new Dimension(160, 36));
        cmbEstado.addActionListener(e -> filtrar());

        JPanel pnlEstado = new JPanel();
        pnlEstado.setOpaque(false);
        pnlEstado.setLayout(new BoxLayout(pnlEstado, BoxLayout.Y_AXIS));
        pnlEstado.add(lblEstado);
        pnlEstado.add(Box.createVerticalStrut(4));
        pnlEstado.add(cmbEstado);
        filtros.add(pnlEstado);

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

    private JPanel crearTabs() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 13));

        JPanel headerEquipos = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        headerEquipos.setOpaque(false);
        headerEquipos.add(new JLabel("\uD83D\uDCE6"));
        headerEquipos.add(new JLabel("Préstamos de Equipos"));
        lblConteoEquipos = new JLabel("0");
        lblConteoEquipos.setOpaque(true);
        lblConteoEquipos.setBackground(new Color(230, 235, 240));
        lblConteoEquipos.setForeground(TEXT_SOFT);
        lblConteoEquipos.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblConteoEquipos.setBorder(new EmptyBorder(2, 8, 2, 8));
        headerEquipos.add(lblConteoEquipos);

        JPanel headerLibros = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        headerLibros.setOpaque(false);
        headerLibros.add(new JLabel("\uD83D\uDCDA"));
        headerLibros.add(new JLabel("Préstamos de Libros"));
        lblConteoLibros = new JLabel("0");
        lblConteoLibros.setOpaque(true);
        lblConteoLibros.setBackground(new Color(230, 235, 240));
        lblConteoLibros.setForeground(TEXT_SOFT);
        lblConteoLibros.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblConteoLibros.setBorder(new EmptyBorder(2, 8, 2, 8));
        headerLibros.add(lblConteoLibros);

        String[] colsEq = {"ID", "USUARIO", "EQUIPO", "SOLICITUD", "ESTADO", "DEVOLUCIÓN"};
        modeloEquipos = new DefaultTableModel(colsEq, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaEquipos = new JTable(modeloEquipos);
        configurarTabla(tablaEquipos, 4);
        tabbedPane.addTab("Préstamos de Equipos", new JScrollPane(tablaEquipos));

        String[] colsLb = {"ID", "USUARIO", "LIBRO", "SOLICITUD", "ESTADO", "DEVOLUCIÓN"};
        modeloLibros = new DefaultTableModel(colsLb, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaLibros = new JTable(modeloLibros);
        configurarTabla(tablaLibros, 4);
        tabbedPane.addTab("Préstamos de Libros", new JScrollPane(tablaLibros));

        panel.add(tabbedPane, BorderLayout.CENTER);
        return panel;
    }

    private void configurarTabla(JTable tabla, int colEstado) {
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

                if (!sel && col == colEstado) {
                    String estado = val != null ? val.toString().toLowerCase() : "";
                    setHorizontalAlignment(CENTER);
                    if (estado.equals("devuelto") || estado.equals("aceptado")) {
                        setBackground(new Color(220, 252, 231));
                        setForeground(new Color(22, 163, 74));
                        setFont(new Font("SansSerif", Font.BOLD, 11));
                        setText(val != null ? val.toString().toUpperCase() : "");
                    } else if (estado.equals("pendiente")) {
                        setBackground(new Color(255, 237, 213));
                        setForeground(new Color(194, 65, 12));
                        setFont(new Font("SansSerif", Font.BOLD, 11));
                        setText("PENDIENTE");
                    } else if (estado.equals("rechazado")) {
                        setBackground(new Color(254, 226, 226));
                        setForeground(new Color(220, 38, 38));
                        setFont(new Font("SansSerif", Font.BOLD, 11));
                        setText("RECHAZADO");
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
        tcm.getColumn(0).setPreferredWidth(50);
        tcm.getColumn(1).setPreferredWidth(180);
        tcm.getColumn(2).setPreferredWidth(200);
        tcm.getColumn(3).setPreferredWidth(120);
        tcm.getColumn(4).setPreferredWidth(110);
        tcm.getColumn(5).setPreferredWidth(120);
    }

    private void cargarDatos() {
        new SwingWorker<List<Prestamo>, Void>() {
            @Override protected List<Prestamo> doInBackground() { return controlador.listarEquipos(); }
            @Override protected void done() {
                try {
                    for (Prestamo p : get()) {
                        modeloEquipos.addRow(new Object[]{
                            p.getIdPrestamo(), p.getNombreCompletoUsuario(),
                            p.getNombreEquipo() + " - " + p.getTipoEquipo(),
                            formatearFecha(p.getFechaSolicitud()), p.getEstado(),
                            formatearFecha(p.getFechaDevolucion())
                        });
                    }
                    lblConteoEquipos.setText(String.valueOf(modeloEquipos.getRowCount()));
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();

        new SwingWorker<List<Prestamo>, Void>() {
            @Override protected List<Prestamo> doInBackground() { return controlador.listarLibros(); }
            @Override protected void done() {
                try {
                    for (Prestamo p : get()) {
                        modeloLibros.addRow(new Object[]{
                            p.getIdPrestamo(), p.getNombreCompletoUsuario(),
                            p.getNombreLibro() + " - " + p.getGeneroLibro(),
                            formatearFecha(p.getFechaSolicitud()), p.getEstado(),
                            formatearFecha(p.getFechaDevolucion())
                        });
                    }
                    lblConteoLibros.setText(String.valueOf(modeloLibros.getRowCount()));
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();
    }

    private void filtrar() {
        String texto = txtBuscar.getText().trim();
        String estado = (String) cmbEstado.getSelectedItem();

        java.util.List<RowFilter<DefaultTableModel, Object>> filtros = new java.util.ArrayList<>();
        if (!texto.isEmpty()) {
            filtros.add(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(texto), 1, 2));
        }
        if (!"Todos".equals(estado)) {
            filtros.add(RowFilter.regexFilter("(?i)^" + java.util.regex.Pattern.quote(estado) + "$", 4));
        }
        RowFilter<DefaultTableModel, Object> rf = filtros.isEmpty() ? RowFilter.regexFilter(".*") : RowFilter.andFilter(filtros);

        TableRowSorter<DefaultTableModel> sorterE = new TableRowSorter<>(modeloEquipos);
        sorterE.setRowFilter(rf);
        tablaEquipos.setRowSorter(sorterE);

        TableRowSorter<DefaultTableModel> sorterL = new TableRowSorter<>(modeloLibros);
        sorterL.setRowFilter(rf);
        tablaLibros.setRowSorter(sorterL);
    }

    private void exportarExcel() {
        ExportUtil.exportarCSV(tablaEquipos, tablaLibros, modeloEquipos, modeloLibros,
                "--- PRÉSTAMOS DE EQUIPOS ---", "--- PRÉSTAMOS DE LIBROS ---", this);
    }

    private void exportarPDF() {
        ExportUtil.exportarPDF(modeloEquipos, modeloLibros,
                "Reporte de Préstamos", "Historial de Préstamos - " +
                new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date()), this);
    }

    private String formatearFecha(java.sql.Timestamp ts) {
        if (ts == null) return "—";
        return new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(ts);
    }

    private static class FondoInternoPanel extends JPanel {
        private static final Image FONDO;
        static {
            Image img = null;
            try { img = new ImageIcon(FRMReportePrestamos.class.getResource("/imagenes/fondo.jpg")).getImage(); } catch (Exception e) {}
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
