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

public class FRMPrestamosLibros extends JInternalFrame {

    private static final Color SENA_GREEN = new Color(46, 170, 84);
    private static final Color TEXT_DARK = new Color(28, 34, 45);
    private static final Color TEXT_SOFT = new Color(96, 105, 121);

    private PrestamoControlador controlador;
    private DefaultTableModel modeloTabla;
    private JTable tabla;
    private JTextField txtBuscar;
    private JComboBox<String> cmbEstado;
    private TableRowSorter<DefaultTableModel> sorter;

    public FRMPrestamosLibros() {
        super("Gestión de Préstamos de Libros", true, true, true, true);
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
        raiz.add(crearPanelTabla(), BorderLayout.CENTER);
        setContentPane(raiz);
        pack();
    }

    private JPanel crearEncabezado() {
        JPanel cont = new JPanel(new BorderLayout(16, 10));
        cont.setOpaque(false);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Gestión de Préstamos de Libros");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 26));
        titulo.setForeground(TEXT_DARK);

        JLabel subtitulo = new JLabel("Consulta y administra los préstamos de libros de la biblioteca.");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitulo.setForeground(TEXT_SOFT);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(subtitulo);

        JButton btnNuevo = crearBotonVerde("Nuevo Préstamo");
        btnNuevo.setPreferredSize(new Dimension(196, 42));
        btnNuevo.addActionListener(e -> new FRMNuevoPrestamoLibro(
                (javax.swing.JFrame) javax.swing.SwingUtilities.getWindowAncestor(this), this).setVisible(true));

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        acciones.setOpaque(false);
        acciones.add(btnNuevo);

        cont.add(textos, BorderLayout.WEST);
        cont.add(acciones, BorderLayout.EAST);
        return cont;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel filtroPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        filtroPanel.setOpaque(false);

        JLabel lblBuscar = new JLabel("BUSCAR PRÉSTAMO DE LIBRO");
        lblBuscar.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblBuscar.setForeground(TEXT_SOFT);

        txtBuscar = new JTextField(20);
        txtBuscar.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 220)),
                new EmptyBorder(8, 10, 8, 10)));

        JLabel lblEstado = new JLabel("ESTADO");
        lblEstado.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblEstado.setForeground(TEXT_SOFT);

        cmbEstado = new JComboBox<>(new String[]{"Todos", "pendiente", "aceptado", "rechazado", "devuelto"});
        cmbEstado.setFont(new Font("SansSerif", Font.PLAIN, 13));
        cmbEstado.setPreferredSize(new Dimension(160, 35));

        JButton btnBuscar = crearBotonVerde("Buscar");
        btnBuscar.setPreferredSize(new Dimension(120, 35));
        btnBuscar.addActionListener(e -> filtrar());

        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { filtrar(); }
        });
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

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 20));
        btnPanel.setOpaque(false);
        btnPanel.add(btnBuscar);

        filtroPanel.add(colBuscar);
        filtroPanel.add(Box.createHorizontalStrut(16));
        filtroPanel.add(colEstado);
        filtroPanel.add(Box.createHorizontalStrut(16));
        filtroPanel.add(btnPanel);

        panel.add(filtroPanel, BorderLayout.NORTH);

        String[] columnas = {"ID", "USUARIO", "LIBRO", "SOLICITUD", "ESTADO", "DEVOLUCIÓN", "ACCIONES"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int col) { return col == 6; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(52);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tabla.getTableHeader().setForeground(TEXT_SOFT);
        tabla.getTableHeader().setBackground(new Color(248, 250, 252));
        tabla.getTableHeader().setBorder(new MatteBorder(0, 0, 1, 0, new Color(223, 228, 234)));
        tabla.getTableHeader().setPreferredSize(new Dimension(0, 40));
        tabla.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabla.setSelectionBackground(new Color(240, 252, 244));
        tabla.setSelectionForeground(TEXT_DARK);

        TableColumnModel tcm = tabla.getColumnModel();
        tcm.getColumn(0).setMinWidth(0);
        tcm.getColumn(0).setMaxWidth(0);
        tcm.getColumn(0).setPreferredWidth(0);
        tcm.getColumn(1).setPreferredWidth(180);
        tcm.getColumn(2).setPreferredWidth(200);
        tcm.getColumn(3).setPreferredWidth(130);
        tcm.getColumn(4).setPreferredWidth(100);
        tcm.getColumn(5).setPreferredWidth(160);
        tcm.getColumn(6).setPreferredWidth(80);

        tabla.getColumnModel().getColumn(6).setCellRenderer(new BotonDevolverRenderer());
        tabla.getColumnModel().getColumn(6).setCellEditor(new BotonDevolverEditor());

        sorter = new TableRowSorter<>(modeloTabla);
        tabla.setRowSorter(sorter);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        new SwingWorker<List<Prestamo>, Void>() {
            @Override protected List<Prestamo> doInBackground() { return controlador.listarLibros(); }
            @Override protected void done() {
                try {
                    for (Prestamo p : get()) {
                        modeloTabla.addRow(new Object[]{
                            p.getIdPrestamo(),
                            p.getNombreCompletoUsuario(),
                            p.getNombreLibro() + "\n" + p.getGeneroLibro(),
                            formatearFecha(p.getFechaSolicitud()),
                            p.getEstado(),
                            formatearDevolucion(p),
                            "DEVOLVER"
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
        sorter.setRowFilter(filtros.isEmpty() ? RowFilter.regexFilter("(?i).*") : RowFilter.andFilter(filtros));
    }

    private String formatearFecha(java.sql.Timestamp ts) {
        if (ts == null) return "—";
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy\nHH:mm");
        return sdf.format(ts);
    }

    private String formatearDevolucion(Prestamo p) {
        if (p.getFechaDevolucion() != null) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
            return sdf.format(p.getFechaDevolucion()) + "\nEXCELENTE";
        }
        if (p.getFechaLimite() != null) {
            long diff = p.getFechaLimite().getTime() - System.currentTimeMillis();
            long dias = diff / (1000 * 60 * 60 * 24);
            if (dias < 0) return "VENCIDO HACE " + Math.abs(dias) + " DÍAS";
            return "Pendiente (" + dias + " días)";
        }
        return "—";
    }

    private JButton crearBotonVerde(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(SENA_GREEN);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private class BotonDevolverRenderer extends JButton implements TableCellRenderer {
        public BotonDevolverRenderer() {
            setFont(new Font("SansSerif", Font.BOLD, 11));
            setBackground(SENA_GREEN);
            setForeground(Color.WHITE);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(true);
        }
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean focus, int row, int col) {
            String estado = (String) modeloTabla.getValueAt(row, 4);
            if ("devuelto".equals(estado)) { setBackground(new Color(200, 200, 200)); setText("✓"); }
            else { setBackground(SENA_GREEN); setText("Devolver"); }
            return this;
        }
    }

    private class BotonDevolverEditor extends AbstractCellEditor implements TableCellEditor {
        private final JButton btn = new JButton();
        private int fila;
        public BotonDevolverEditor() {
            btn.setFont(new Font("SansSerif", Font.BOLD, 11));
            btn.setBackground(SENA_GREEN);
            btn.setForeground(Color.WHITE);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setOpaque(true);
            btn.addActionListener(e -> {
                int id = (int) modeloTabla.getValueAt(fila, 0);
                if (JOptionPane.showConfirmDialog(FRMPrestamosLibros.this, "¿Confirmar devolución?", "Devolver", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    controlador.devolverLibro(id);
                    cargarDatos();
                }
                fireEditingStopped();
            });
        }
        @Override public Component getTableCellEditorComponent(JTable t, Object v, boolean sel, int row, int col) {
            fila = row;
            String estado = (String) modeloTabla.getValueAt(row, 4);
            if ("devuelto".equals(estado)) { btn.setBackground(new Color(200, 200, 200)); btn.setText("✓"); btn.setEnabled(false); }
            else { btn.setBackground(SENA_GREEN); btn.setText("Devolver"); btn.setEnabled(true); }
            return btn;
        }
        @Override public Object getCellEditorValue() { return btn.getText(); }
    }

    public void refrescar() { cargarDatos(); }

    private static class FondoInternoPanel extends JPanel {
        private static final Image FONDO;
        static {
            Image img = null;
            try { img = new ImageIcon(FRMPrestamosLibros.class.getResource("/imagenes/fondo.jpg")).getImage(); } catch (Exception e) {}
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
