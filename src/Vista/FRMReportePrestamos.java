package Vista;

import Controlador.PrestamoControlador;
import Modelo.Prestamo;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;

public class FRMReportePrestamos extends JInternalFrame {

    private static final Color SENA_GREEN = new Color(46, 170, 84);
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

        JLabel titulo = new JLabel("Préstamos Realizados");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 26));
        titulo.setForeground(TEXT_DARK);

        JLabel subtitulo = new JLabel("Historial completo de préstamos de equipos y libros.");
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

        JLabel lblBuscar = new JLabel("BUSCAR");
        lblBuscar.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblBuscar.setForeground(TEXT_SOFT);

        txtBuscar = new JTextField(20);
        txtBuscar.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 220)),
                new EmptyBorder(8, 10, 8, 10)));
        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { filtrar(); }
        });

        JLabel lblEstado = new JLabel("ESTADO");
        lblEstado.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblEstado.setForeground(TEXT_SOFT);

        cmbEstado = new JComboBox<>(new String[]{"Todos", "pendiente", "aceptado", "rechazado", "devuelto"});
        cmbEstado.setFont(new Font("SansSerif", Font.PLAIN, 13));
        cmbEstado.setPreferredSize(new Dimension(160, 35));
        cmbEstado.addActionListener(e -> filtrar());

        JPanel colBuscar = new JPanel();
        colBuscar.setOpaque(false);
        colBuscar.setLayout(new BoxLayout(colBuscar, BoxLayout.Y_AXIS));
        colBuscar.add(lblBuscar);
        colBuscar.add(Box.createVerticalStrut(4));
        colBuscar.add(txtBuscar);

        JPanel colEstado = new JPanel();
        colEstado.setOpaque(false);
        colEstado.setLayout(new BoxLayout(colEstado, BoxLayout.Y_AXIS));
        colEstado.add(lblEstado);
        colEstado.add(Box.createVerticalStrut(4));
        colEstado.add(cmbEstado);

        filtroPanel.add(colBuscar);
        filtroPanel.add(Box.createHorizontalStrut(16));
        filtroPanel.add(colEstado);

        panel.add(filtroPanel, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 13));

        String[] colsEq = {"ID", "USUARIO", "EQUIPO", "SOLICITUD", "ESTADO", "DEVOLUCIÓN"};
        modeloEquipos = new DefaultTableModel(colsEq, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaEquipos = new JTable(modeloEquipos);
        configurarTabla(tablaEquipos);
        tabbedPane.addTab("Préstamos de Equipos", new JScrollPane(tablaEquipos));

        String[] colsLb = {"ID", "USUARIO", "LIBRO", "SOLICITUD", "ESTADO", "DEVOLUCIÓN"};
        modeloLibros = new DefaultTableModel(colsLb, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaLibros = new JTable(modeloLibros);
        configurarTabla(tablaLibros);
        tabbedPane.addTab("Préstamos de Libros", new JScrollPane(tablaLibros));

        panel.add(tabbedPane, BorderLayout.CENTER);
        return panel;
    }

    private void configurarTabla(JTable tabla) {
        tabla.setRowHeight(40);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tabla.getTableHeader().setForeground(TEXT_SOFT);
        tabla.getTableHeader().setBackground(new Color(248, 255, 252));
        tabla.getTableHeader().setPreferredSize(new Dimension(0, 38));
        tabla.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabla.setSelectionBackground(new Color(240, 252, 244));
        tabla.setSelectionForeground(TEXT_DARK);
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
