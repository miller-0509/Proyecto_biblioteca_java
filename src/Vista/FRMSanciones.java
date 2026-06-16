package Vista;

import Controlador.ControladorSancion;
import Modelo.Sancion;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

public class FRMSanciones extends JInternalFrame {

    private static final Color SENA_GREEN = new Color(46, 170, 84);
    private static final Color SENA_GREEN_DARK = new Color(24, 123, 61);
    private static final Color SENA_RED = new Color(215, 74, 74);
    private static final Color SENA_RED_SOFT = new Color(253, 231, 231);
    private static final Color SENA_GREEN_SOFT = new Color(240, 252, 244);
    private static final Color TEXT_DARK = new Color(28, 34, 45);
    private static final Color TEXT_SOFT = new Color(96, 105, 121);
    private static final Color BORDER = new Color(224, 229, 236);
    private static final Color SURFACE = new Color(255, 255, 255);
    private static final Color TABLE_ALT = new Color(249, 251, 253);

    private PlaceholderTextField txtBuscar;
    private JComboBox<String> cmbEstado;
    private JTable tablaSanciones;
    private DefaultTableModel modeloSanciones;
    private TableRowSorter<DefaultTableModel> filtroTabla;
    private ControladorSancion controladorSancion;

    public FRMSanciones() {
        initComponents();
        construirVista();
        controladorSancion = new ControladorSancion();
        try {
            cargarSanciones();
        } catch (Exception ex) {
            System.out.println("[FRMSanciones] Error cargando sanciones en constructor: " + ex.getMessage());
            ex.printStackTrace();
        }
        aplicarFiltros();
    }

    private void construirVista() {
        setTitle("Gestion de Sanciones");
        setClosable(true);
        setMaximizable(true);
        setIconifiable(true);
        setResizable(true);

        JPanel raiz = new FondoInternoPanel();
        raiz.setLayout(new BorderLayout(0, 18));
        raiz.setBorder(new EmptyBorder(20, 22, 22, 22));
        raiz.add(crearEncabezado(), BorderLayout.NORTH);
        raiz.add(crearPanelAdministrativo(), BorderLayout.CENTER);
        setContentPane(raiz);
        pack();
    }

    private JPanel crearEncabezado() {
        JPanel encabezado = new JPanel(new BorderLayout(18, 12));
        encabezado.setOpaque(false);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Gestion de Sanciones");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 26));
        titulo.setForeground(TEXT_DARK);

        JLabel subtitulo = new JLabel("Seguimiento de suspensiones generadas por devoluciones fuera de fecha.");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitulo.setForeground(TEXT_SOFT);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(5));
        textos.add(subtitulo);

        JButton btnActualizar = crearBotonPrincipal("Actualizar");
        btnActualizar.setPreferredSize(new Dimension(136, 42));
        btnActualizar.addActionListener(evt -> cargarSanciones());

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        acciones.setOpaque(false);
        acciones.add(btnActualizar);

        encabezado.add(textos, BorderLayout.WEST);
        encabezado.add(acciones, BorderLayout.EAST);
        return encabezado;
    }

    private JPanel crearPanelAdministrativo() {
        RoundedPanel panel = new RoundedPanel(SURFACE, BORDER, 22);
        panel.setLayout(new BorderLayout(0, 18));
        panel.setBorder(new EmptyBorder(18, 18, 18, 18));
        panel.add(crearBarraFiltros(), BorderLayout.NORTH);
        panel.add(crearTablaSanciones(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearBarraFiltros() {
        JPanel barra = new JPanel(new GridBagLayout());
        barra.setOpaque(false);

        txtBuscar = new PlaceholderTextField("Usuario, correo o recurso...");
        txtBuscar.setPreferredSize(new Dimension(320, 42));
        estilizarCampoTexto(txtBuscar);
        txtBuscar.addActionListener(evt -> cargarSanciones());

        cmbEstado = new JComboBox<>(new String[]{"Todos", "activa", "condonada"});
        cmbEstado.setPreferredSize(new Dimension(180, 42));
        cmbEstado.setSelectedIndex(0);

        JButton btnBuscar = crearBotonPrincipal("Buscar");
        btnBuscar.setPreferredSize(new Dimension(112, 42));
        btnBuscar.addActionListener(evt -> cargarSanciones());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.weightx = 1.0;
        barra.add(crearCampoConEtiqueta("Buscar sancion", txtBuscar), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.28;
        barra.add(crearCampoConEtiqueta("Estado", cmbEstado), gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.0;
        gbc.insets = new Insets(21, 0, 0, 0);
        barra.add(btnBuscar, gbc);

        return barra;
    }

    private JScrollPane crearTablaSanciones() {
        modeloSanciones = new DefaultTableModel(
                new Object[]{"ID", "Fecha", "Usuario", "Correo", "Tipo recurso", "Nombre recurso", "Dias retraso", "Suspension", "Estado", "Acciones"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 9;
            }
        };

        tablaSanciones = new JTable(modeloSanciones);
        tablaSanciones.setRowHeight(46);
        tablaSanciones.setShowGrid(false);
        tablaSanciones.setIntercellSpacing(new Dimension(0, 0));
        tablaSanciones.setSelectionBackground(SENA_GREEN_SOFT);
        tablaSanciones.setSelectionForeground(TEXT_DARK);
        tablaSanciones.setFillsViewportHeight(true);
        tablaSanciones.setAutoCreateRowSorter(false);

        JTableHeader header = tablaSanciones.getTableHeader();
        header.setReorderingAllowed(false);
        header.setFont(new Font("SansSerif", Font.BOLD, 12));
        header.setForeground(TEXT_DARK);
        header.setBackground(new Color(245, 248, 250));
        header.setBorder(new MatteBorder(0, 0, 1, 0, BORDER));

        tablaSanciones.setDefaultRenderer(Object.class, new CeldaGeneralRenderer());
        tablaSanciones.getColumnModel().getColumn(8).setCellRenderer(new EstadoRenderer());
        tablaSanciones.getColumnModel().getColumn(9).setCellRenderer(new AccionesRenderer());
        tablaSanciones.getColumnModel().getColumn(9).setCellEditor(new AccionesEditor());

        filtroTabla = new TableRowSorter<>(modeloSanciones);
        tablaSanciones.setRowSorter(filtroTabla);

        ajustarAnchosTabla();

        JScrollPane scroll = new JScrollPane(tablaSanciones);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(SURFACE);
        return scroll;
    }

    private void ajustarAnchosTabla() {
        TableColumnModel cols = tablaSanciones.getColumnModel();
        cols.getColumn(0).setMinWidth(0);
        cols.getColumn(0).setMaxWidth(0);
        cols.getColumn(0).setPreferredWidth(0);
        cols.getColumn(1).setPreferredWidth(110);
        cols.getColumn(2).setPreferredWidth(180);
        cols.getColumn(3).setPreferredWidth(210);
        cols.getColumn(4).setPreferredWidth(120);
        cols.getColumn(5).setPreferredWidth(220);
        cols.getColumn(6).setPreferredWidth(90);
        cols.getColumn(7).setPreferredWidth(90);
        cols.getColumn(8).setPreferredWidth(100);
        cols.getColumn(9).setPreferredWidth(140);
    }

    private void cargarSanciones() {
        System.out.println("[FRMSanciones] Iniciando carga de sanciones...");
        try {
            List<Sancion> sanciones = controladorSancion.listarTodas();
            System.out.println("[FRMSanciones] Registros encontrados: " + sanciones.size());
            modeloSanciones.setRowCount(0);
            for (Sancion s : sanciones) {
                modeloSanciones.addRow(new Object[]{
                    s.getIdSancion(),
                    s.getFechaSancion(),
                    s.getIdUsuario() != null ? s.getIdUsuario() : "",
                    s.getCorreoUsuario(),
                    s.getTipoRecurso(),
                    s.getNombreRecurso(),
                    s.getDiasRetraso(),
                    s.getDiasSuspension(),
                    s.getEstado(),
                    "Ver detalle"
                });
            }
            System.out.println("[FRMSanciones] Filas cargadas en JTable: " + modeloSanciones.getRowCount());
        } catch (Exception ex) {
            System.out.println("[FRMSanciones] Error en consulta de sanciones: " + ex.getMessage());
            ex.printStackTrace();
            modeloSanciones.setRowCount(0);
        }
    }

    private void aplicarFiltros() {
        if (filtroTabla == null) {
            return;
        }
        filtroTabla.setRowFilter(null);
    }

    private void verDetalleSeleccionado() {
        int filaVista = tablaSanciones.getSelectedRow();
        if (filaVista < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona una sancion primero.", "Informacion", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int filaModelo = tablaSanciones.convertRowIndexToModel(filaVista);
        String recurso = String.valueOf(modeloSanciones.getValueAt(filaModelo, 5));
        String estado = String.valueOf(modeloSanciones.getValueAt(filaModelo, 8));
        String diasRetraso = String.valueOf(modeloSanciones.getValueAt(filaModelo, 6));
        String diasSuspension = String.valueOf(modeloSanciones.getValueAt(filaModelo, 7));
        JOptionPane.showMessageDialog(this,
                "Recurso: " + recurso + "\nEstado: " + estado + "\nDias retraso: " + diasRetraso + "\nSuspension: " + diasSuspension,
                "Detalle de sancion",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void condonarSeleccionada() {
        int filaVista = tablaSanciones.getSelectedRow();
        if (filaVista < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona una sancion primero.", "Informacion", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Deseas condonar la sancion seleccionada?", "Condonar sancion", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        int filaModelo = tablaSanciones.convertRowIndexToModel(filaVista);
        int idSancion = Integer.parseInt(String.valueOf(modeloSanciones.getValueAt(filaModelo, 0)));
        if (controladorSancion.condonar(idSancion)) {
            cargarSanciones();
            JOptionPane.showMessageDialog(this, "Sancion condonada correctamente.", "Exito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo condonar la sancion.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton crearBotonPrincipal(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("SansSerif", Font.BOLD, 13));
        boton.setBackground(SENA_GREEN);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        boton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        boton.setUI(new BasicButtonUI());
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(SENA_GREEN_DARK);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(SENA_GREEN);
            }
        });
        return boton;
    }

    private JPanel crearCampoConEtiqueta(String titulo, Component campo) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel lbl = new JLabel(titulo);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setForeground(TEXT_DARK);

        panel.add(lbl);
        panel.add(Box.createVerticalStrut(6));
        panel.add(campo);
        return panel;
    }

    private void estilizarCampoTexto(JTextField campo) {
        campo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        campo.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 1, 1, 1, BORDER),
                new EmptyBorder(10, 12, 10, 12)
        ));
        campo.setBackground(Color.WHITE);
    }

    private static final class CeldaGeneralRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            c.setFont(new Font("SansSerif", Font.PLAIN, 12));
            if (!isSelected) {
                c.setBackground(row % 2 == 0 ? SURFACE : TABLE_ALT);
            }
            setBorder(new EmptyBorder(0, 10, 0, 10));
            return c;
        }
    }

    private static final class EstadoRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
            String estado = String.valueOf(value);
            if ("condonada".equalsIgnoreCase(estado)) {
                lbl.setBackground(SENA_GREEN_SOFT);
                lbl.setForeground(SENA_GREEN_DARK);
            } else {
                lbl.setBackground(SENA_RED_SOFT);
                lbl.setForeground(SENA_RED);
            }
            lbl.setOpaque(true);
            lbl.setBorder(new EmptyBorder(6, 10, 6, 10));
            return lbl;
        }
    }

    private final class AccionesRenderer extends JPanel implements TableCellRenderer {
        private AccionesRenderer() {
            setOpaque(false);
            setLayout(new FlowLayout(FlowLayout.CENTER, 6, 4));
            JButton btnVer = new JButton("Ver");
            JButton btnCondonar = new JButton("Condonar");
            btnVer.setFocusPainted(false);
            btnCondonar.setFocusPainted(false);
            btnVer.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
            btnCondonar.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
            add(btnVer);
            add(btnCondonar);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    private final class AccionesEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));

        private AccionesEditor() {
            panel.setOpaque(false);
            JButton btnVer = new JButton("Ver");
            JButton btnCondonar = new JButton("Condonar");
            btnVer.setFocusPainted(false);
            btnCondonar.setFocusPainted(false);
            btnVer.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
            btnCondonar.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
            btnVer.addActionListener(evt -> {
                fireEditingStopped();
                verDetalleSeleccionado();
            });
            btnCondonar.addActionListener(evt -> {
                fireEditingStopped();
                condonarSeleccionada();
            });
            panel.add(btnVer);
            panel.add(btnCondonar);
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            return panel;
        }
    }

    private static class FondoInternoPanel extends JPanel {
        @Override
        protected void paintComponent(java.awt.Graphics g) {
            super.paintComponent(g);
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
            try {
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new java.awt.GradientPaint(0, 0, new Color(246, 248, 250), 0, getHeight(), new Color(236, 241, 244)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            } finally {
                g2.dispose();
            }
        }
    }

    private static class RoundedPanel extends JPanel {
        private final Color fill;
        private final Color borderColor;
        private final int arc;

        private RoundedPanel(Color fill, Color borderColor, int arc) {
            this.fill = fill;
            this.borderColor = borderColor;
            this.arc = arc;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(java.awt.Graphics g) {
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
            try {
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(fill);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
                g2.setColor(borderColor);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            } finally {
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }

    private static class PlaceholderTextField extends JTextField {
        private final String placeholder;

        private PlaceholderTextField(String placeholder) {
            this.placeholder = placeholder;
        }

        @Override
        protected void paintComponent(java.awt.Graphics g) {
            super.paintComponent(g);
            if (getText().isEmpty()) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                try {
                    g2.setColor(new Color(147, 156, 170));
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    g2.drawString(placeholder, 12, getHeight() / 2 + 5);
                } finally {
                    g2.dispose();
                }
            }
        }
    }

    private void initComponents() {
        setPreferredSize(new Dimension(1100, 700));
    }
}
