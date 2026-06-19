package Vista;

import Controlador.EquipoControlador;
import Controlador.LibroControlador;
import Modelo.Equipo;
import Modelo.Libro;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.*;

public class FRMReporteInventario extends JInternalFrame {

    private static final Color SENA_GREEN = new Color(46, 170, 84);
    private static final Color SENA_GREEN_DARK = new Color(25, 120, 60);
    private static final Color SENA_RED = new Color(217, 70, 70);
    private static final Color TEXT_DARK = new Color(28, 34, 45);
    private static final Color TEXT_SOFT = new Color(96, 105, 121);
    private static final Color BG_LIGHT = new Color(248, 250, 252);

    private EquipoControlador equipoControlador;
    private LibroControlador libroControlador;
    private DefaultTableModel modeloEquipos;
    private DefaultTableModel modeloLibros;
    private JTable tablaEquipos;
    private JTable tablaLibros;
    private JTabbedPane tabbedPane;
    private JComboBox<String> cmbEstado;
    private JComboBox<String> cmbTipoEquipo;
    private JLabel lblConteoEquipos;
    private JLabel lblConteoLibros;
    private List<Equipo> listaEquipos;
    private List<Libro> listaLibros;

    public FRMReporteInventario() {
        super("Reporte de Inventario", true, true, true, true);
        equipoControlador = new EquipoControlador();
        libroControlador = new LibroControlador();
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

        JPanel contenido = new JPanel(new BorderLayout(0, 16));
        contenido.setOpaque(false);
        contenido.setBorder(new EmptyBorder(24, 32, 24, 32));

        contenido.add(crearHeader(), BorderLayout.NORTH);
        contenido.add(crearFiltros(), BorderLayout.CENTER);

        JPanel tablaPanel = new JPanel(new BorderLayout(0, 12));
        tablaPanel.setOpaque(false);
        tablaPanel.add(crearTabs(), BorderLayout.CENTER);
        contenido.add(tablaPanel, BorderLayout.SOUTH);

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
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
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

        JLabel icono = new JLabel("\uD83D\uDCE6");
        icono.setFont(new Font("SansSerif", Font.PLAIN, 20));

        JLabel titulo = new JLabel("  Reporte de Inventario");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setForeground(TEXT_DARK);

        JPanel filaTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        filaTitulo.setOpaque(false);
        filaTitulo.add(icono);
        filaTitulo.add(titulo);

        JLabel subtitulo = new JLabel("Inventario Institucional");
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
        JPanel filtros = new JPanel(new GridBagLayout()) {
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
        filtros.setBorder(new EmptyBorder(16, 16, 16, 16));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 8, 0, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cmbEstado = new JComboBox<>(new String[]{"Todos los estados", "disponible", "prestado", "mantenimiento", "dañado"});
        cmbEstado.setFont(new Font("SansSerif", Font.PLAIN, 13));
        cmbEstado.setPreferredSize(new Dimension(180, 38));
        gbc.gridx = 0; gbc.weightx = 1;
        filtros.add(crearCampoFiltro("ESTADO", cmbEstado), gbc);

        cmbTipoEquipo = new JComboBox<>(new String[]{"Todos los tipos"});
        cmbTipoEquipo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        cmbTipoEquipo.setPreferredSize(new Dimension(180, 38));
        gbc.gridx = 1; gbc.weightx = 1;
        filtros.add(crearCampoFiltro("TIPO DE EQUIPO", cmbTipoEquipo), gbc);

        JPanel pnlBtnFiltrar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 20));
        pnlBtnFiltrar.setOpaque(false);
        JButton btnFiltrar = new JButton("\uD83D\uDD0D Filtrar");
        btnFiltrar.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnFiltrar.setForeground(Color.WHITE);
        btnFiltrar.setBackground(SENA_GREEN);
        btnFiltrar.setFocusPainted(false);
        btnFiltrar.setBorderPainted(false);
        btnFiltrar.setOpaque(true);
        btnFiltrar.setPreferredSize(new Dimension(130, 38));
        btnFiltrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFiltrar.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btnFiltrar.setBackground(SENA_GREEN_DARK); }
            @Override public void mouseExited(MouseEvent e) { btnFiltrar.setBackground(SENA_GREEN); }
        });
        btnFiltrar.addActionListener(e -> aplicarFiltros());
        pnlBtnFiltrar.add(btnFiltrar);
        gbc.gridx = 2; gbc.weightx = 0;
        filtros.add(pnlBtnFiltrar, gbc);

        return filtros;
    }

    private JPanel crearCampoFiltro(String label, JComponent campo) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        lbl.setForeground(TEXT_SOFT);
        lbl.setBorder(new EmptyBorder(0, 2, 4, 0));
        p.add(lbl);
        p.add(campo);
        return p;
    }

    private JPanel crearTabs() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 13));

        JPanel headerEquipos = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        headerEquipos.setOpaque(false);
        headerEquipos.add(new JLabel("\uD83D\uDCE6"));
        headerEquipos.add(new JLabel("Equipos"));
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
        headerLibros.add(new JLabel("Libros"));
        lblConteoLibros = new JLabel("0");
        lblConteoLibros.setOpaque(true);
        lblConteoLibros.setBackground(new Color(230, 235, 240));
        lblConteoLibros.setForeground(TEXT_SOFT);
        lblConteoLibros.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblConteoLibros.setBorder(new EmptyBorder(2, 8, 2, 8));
        headerLibros.add(lblConteoLibros);

        tablaEquipos = new JTable(crearModeloEquipos());
        configurarTabla(tablaEquipos);
        tabbedPane.addTab("Equipos", new JScrollPane(tablaEquipos));

        tablaLibros = new JTable(crearModeloLibros());
        configurarTabla(tablaLibros);
        tabbedPane.addTab("Libros", new JScrollPane(tablaLibros));

        panel.add(tabbedPane, BorderLayout.CENTER);
        return panel;
    }

    private DefaultTableModel crearModeloEquipos() {
        String[] cols = {"REF", "NOMBRE", "TIPO", "N° SERIE", "ESTADO", "UBICACIÓN", "REGISTRO"};
        modeloEquipos = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int col) {
                if (col == 4) return String.class;
                return Object.class;
            }
        };
        return modeloEquipos;
    }

    private DefaultTableModel crearModeloLibros() {
        String[] cols = {"REF", "TÍTULO", "AUTOR", "GÉNERO", "CÓDIGO", "ESTADO", "UBICACIÓN"};
        modeloLibros = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int col) {
                if (col == 5) return String.class;
                return Object.class;
            }
        };
        return modeloLibros;
    }

    private void configurarTabla(JTable tabla) {
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
                    if (estado.equals("disponible") || estado.equals("activo")) {
                        setBackground(new Color(220, 252, 231));
                        setForeground(new Color(22, 163, 74));
                        setFont(new Font("SansSerif", Font.BOLD, 11));
                        setText(val != null ? val.toString().toUpperCase() : "");
                    } else if (estado.equals("prestado")) {
                        setBackground(new Color(255, 237, 213));
                        setForeground(new Color(194, 65, 12));
                        setFont(new Font("SansSerif", Font.BOLD, 11));
                        setText("PRESTADO");
                    } else if (estado.equals("mantenimiento")) {
                        setBackground(new Color(219, 234, 254));
                        setForeground(new Color(37, 99, 235));
                        setFont(new Font("SansSerif", Font.BOLD, 11));
                        setText("MANTENIMIENTO");
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
        tcm.getColumn(1).setPreferredWidth(200);
        tcm.getColumn(2).setPreferredWidth(100);
        tcm.getColumn(3).setPreferredWidth(120);
        tcm.getColumn(4).setPreferredWidth(110);
        tcm.getColumn(5).setPreferredWidth(120);
        tcm.getColumn(6).setPreferredWidth(80);
    }

    private void cargarDatos() {
        new SwingWorker<List<Equipo>, Void>() {
            @Override protected List<Equipo> doInBackground() { return equipoControlador.listarTodos(); }
            @Override protected void done() {
                try {
                    listaEquipos = get();
                    modeloEquipos.setRowCount(0);
                    for (Equipo e : listaEquipos) {
                        modeloEquipos.addRow(new Object[]{
                            "E-" + e.getIdEquipo(),
                            e.getNombre() + (e.getMarca() != null ? "\n" + e.getMarca() : ""),
                            e.getTipoEquipo(),
                            e.getNumeroSerie(),
                            e.getEstado(),
                            e.getUbicacion(),
                            "N/A"
                        });
                    }
                    lblConteoEquipos.setText(String.valueOf(listaEquipos.size()));
                    cargarTiposEquipo(listaEquipos);
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();

        new SwingWorker<List<Libro>, Void>() {
            @Override protected List<Libro> doInBackground() { return libroControlador.listarTodos(); }
            @Override protected void done() {
                try {
                    listaLibros = get();
                    modeloLibros.setRowCount(0);
                    for (Libro l : listaLibros) {
                        modeloLibros.addRow(new Object[]{
                            "L-" + l.getIdLibro(),
                            l.getTitulo(),
                            l.getAutor(),
                            l.getGenero(),
                            l.getCodigoUnico(),
                            l.getEstado(),
                            l.getUbicacion()
                        });
                    }
                    lblConteoLibros.setText(String.valueOf(listaLibros.size()));
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();
    }

    private void cargarTiposEquipo(List<Equipo> equipos) {
        java.util.Set<String> tipos = new java.util.TreeSet<>();
        for (Equipo e : equipos) {
            if (e.getTipoEquipo() != null && !e.getTipoEquipo().isEmpty()) {
                tipos.add(e.getTipoEquipo());
            }
        }
        cmbTipoEquipo.removeAllItems();
        cmbTipoEquipo.addItem("Todos los tipos");
        for (String t : tipos) cmbTipoEquipo.addItem(t);
    }

    private void aplicarFiltros() {
        String estado = (String) cmbEstado.getSelectedItem();
        String tipo = (String) cmbTipoEquipo.getSelectedItem();

        modeloEquipos.setRowCount(0);
        if (listaEquipos != null) {
            for (Equipo e : listaEquipos) {
                boolean okEstado = "Todos los estados".equals(estado) || e.getEstado().equalsIgnoreCase(estado);
                boolean okTipo = "Todos los tipos".equals(tipo) || (e.getTipoEquipo() != null && e.getTipoEquipo().equalsIgnoreCase(tipo));
                if (okEstado && okTipo) {
                    modeloEquipos.addRow(new Object[]{
                        "E-" + e.getIdEquipo(),
                        e.getNombre() + (e.getMarca() != null ? "\n" + e.getMarca() : ""),
                        e.getTipoEquipo(),
                        e.getNumeroSerie(),
                        e.getEstado(),
                        e.getUbicacion(),
                        "N/A"
                    });
                }
            }
            lblConteoEquipos.setText(String.valueOf(modeloEquipos.getRowCount()));
        }
    }

    private void exportarExcel() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("Reporte_Inventario.csv"));
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV (*.csv)", "csv"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(fc.getSelectedFile()))) {
                pw.println("REF,NOMBRE,TIPO,N° SERIE,ESTADO,UBICACIÓN,REGISTRO");
                for (int i = 0; i < modeloEquipos.getRowCount(); i++) {
                    StringBuilder sb = new StringBuilder();
                    for (int j = 0; j < modeloEquipos.getColumnCount(); j++) {
                        Object val = modeloEquipos.getValueAt(i, j);
                        String s = val != null ? val.toString().replace("\n", " ").replace(",", ";") : "";
                        if (j > 0) sb.append(",");
                        sb.append("\"").append(s).append("\"");
                    }
                    pw.println(sb);
                }
                pw.println();
                pw.println("--- LIBROS ---");
                pw.println("REF,TÍTULO,AUTOR,GÉNERO,CÓDIGO,ESTADO,UBICACIÓN");
                for (int i = 0; i < modeloLibros.getRowCount(); i++) {
                    StringBuilder sb = new StringBuilder();
                    for (int j = 0; j < modeloLibros.getColumnCount(); j++) {
                        Object val = modeloLibros.getValueAt(i, j);
                        String s = val != null ? val.toString().replace("\n", " ").replace(",", ";") : "";
                        if (j > 0) sb.append(",");
                        sb.append("\"").append(s).append("\"");
                    }
                    pw.println(sb);
                }
                JOptionPane.showMessageDialog(this, "Reporte exportado correctamente.\n" + fc.getSelectedFile().getAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al exportar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportarPDF() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("Reporte_Inventario.html"));
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("HTML (*.html)", "html"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(fc.getSelectedFile()))) {
                pw.println("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
                pw.println("<title>Reporte de Inventario</title>");
                pw.println("<style>");
                pw.println("body{font-family:Arial,sans-serif;margin:30px;color:#1c222d;}");
                pw.println("h1{color:#2eaa54;font-size:24px;}");
                pw.println("h2{color:#2eaa54;font-size:18px;margin-top:30px;}");
                pw.println("table{border-collapse:collapse;width:100%;margin-top:10px;}");
                pw.println("th{background:#f8fafc;color:#606979;padding:10px 12px;text-align:left;font-size:12px;border-bottom:2px solid #dfe4ea;}");
                pw.println("td{padding:8px 12px;border-bottom:1px solid #eef1f5;font-size:13px;}");
                pw.println("tr:nth-child(even){background:#f9fafb;}");
                pw.println(".badge{padding:3px 10px;border-radius:12px;font-size:11px;font-weight:bold;display:inline-block;}");
                pw.println(".disponible{background:#dcfce7;color:#16a34a;}");
                pw.println(".prestado{background:#ffedd5;color:#c2410c;}");
                pw.println(".mantenimiento{background:#dbeafe;color:#2563eb;}");
                pw.println(".header{display:flex;justify-content:space-between;align-items:center;}");
                pw.println("</style></head><body>");
                pw.println("<div class='header'><h1>\uD83D\uDCE6 Reporte de Inventario</h1><p>Fecha: " + new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date()) + "</p></div>");

                pw.println("<h2>\uD83D\uDCE6 Equipos (" + modeloEquipos.getRowCount() + ")</h2>");
                pw.println("<table><tr><th>REF</th><th>NOMBRE</th><th>TIPO</th><th>N° SERIE</th><th>ESTADO</th><th>UBICACIÓN</th><th>REGISTRO</th></tr>");
                for (int i = 0; i < modeloEquipos.getRowCount(); i++) {
                    pw.println("<tr>");
                    for (int j = 0; j < modeloEquipos.getColumnCount(); j++) {
                        Object val = modeloEquipos.getValueAt(i, j);
                        String s = val != null ? val.toString().replace("\n", " ") : "";
                        if (j == 4) {
                            String cls = s.toLowerCase().replace("á","a").replace("é","e");
                            if (cls.equals("disponible") || cls.equals("activo")) s = "<span class='badge disponible'>" + s.toUpperCase() + "</span>";
                            else if (cls.equals("prestado")) s = "<span class='badge prestado'>PRESTADO</span>";
                            else if (cls.equals("mantenimiento")) s = "<span class='badge mantenimiento'>MANTENIMIENTO</span>";
                        }
                        pw.println("<td>" + s + "</td>");
                    }
                    pw.println("</tr>");
                }
                pw.println("</table>");

                pw.println("<h2>\uD83D\uDCDA Libros (" + modeloLibros.getRowCount() + ")</h2>");
                pw.println("<table><tr><th>REF</th><th>TÍTULO</th><th>AUTOR</th><th>GÉNERO</th><th>CÓDIGO</th><th>ESTADO</th><th>UBICACIÓN</th></tr>");
                for (int i = 0; i < modeloLibros.getRowCount(); i++) {
                    pw.println("<tr>");
                    for (int j = 0; j < modeloLibros.getColumnCount(); j++) {
                        Object val = modeloLibros.getValueAt(i, j);
                        String s = val != null ? val.toString().replace("\n", " ") : "";
                        if (j == 5) {
                            String cls = s.toLowerCase().replace("á","a").replace("é","e");
                            if (cls.equals("disponible") || cls.equals("activo")) s = "<span class='badge disponible'>" + s.toUpperCase() + "</span>";
                            else if (cls.equals("prestado")) s = "<span class='badge prestado'>PRESTADO</span>";
                        }
                        pw.println("<td>" + s + "</td>");
                    }
                    pw.println("</tr>");
                }
                pw.println("</table>");
                pw.println("</body></html>");
                pw.flush();
                java.awt.Desktop.getDesktop().open(fc.getSelectedFile());
                JOptionPane.showMessageDialog(this, "Reporte generado. Use Ctrl+P en el navegador para guardar como PDF.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al exportar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static class FondoInternoPanel extends JPanel {
        private static final Image FONDO;
        static {
            Image img = null;
            try { img = new ImageIcon(FRMReporteInventario.class.getResource("/imagenes/fondo.jpg")).getImage(); } catch (Exception e) {}
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
