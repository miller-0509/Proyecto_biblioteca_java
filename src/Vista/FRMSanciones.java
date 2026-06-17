package Vista;

import Controlador.ControladorSancion;
import Modelo.Sancion;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingWorker;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
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
    private static final Color SENA_GREEN_SOFT = new Color(240, 252, 244);
    private static final Color SENA_ORANGE = new Color(242, 170, 55);
    private static final Color SENA_RED = new Color(215, 74, 74);
    private static final Color SENA_RED_SOFT = new Color(253, 231, 231);
    private static final Color TEXT_DARK = new Color(28, 34, 45);
    private static final Color TEXT_SOFT = new Color(96, 105, 121);
    private static final Color BORDER = new Color(224, 229, 236);
    private static final Color SURFACE = new Color(255, 255, 255);
    private static final Color SURFACE_SOFT = new Color(255, 255, 255, 225);
    private static final Color TABLE_ALT = new Color(249, 251, 253);
    private static final DateTimeFormatter FECHA_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter HORA_FMT = DateTimeFormatter.ofPattern("HH:mm");

    private PlaceholderTextField txtBuscar;
    private JComboBox<String> cmbEstado;
    private JTable tablaSanciones;
    private DefaultTableModel modeloSanciones;
    private TableRowSorter<DefaultTableModel> sorter;
    private ControladorSancion controlador;

    public FRMSanciones() {
        initComponents();
        controlador = new ControladorSancion();
        construirVista();
        cargarTabla();
    }

    private void construirVista() {
        setTitle("Gestión de Sanciones");
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);

        JPanel raiz = new FondoInternoPanel();
        raiz.setLayout(new BorderLayout(0, 18));
        raiz.setBorder(new EmptyBorder(20, 22, 22, 22));
        raiz.add(crearEncabezado(), BorderLayout.NORTH);
        raiz.add(crearPanelPrincipal(), BorderLayout.CENTER);
        setContentPane(raiz);
        pack();
    }

    private JPanel crearEncabezado() {
        JPanel cont = new JPanel(new BorderLayout(16, 10));
        cont.setOpaque(false);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Gestión de Sanciones");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 26));
        titulo.setForeground(TEXT_DARK);

        JLabel subtitulo = new JLabel("Consulta, registra y administra las multas almacenadas en PostgreSQL.");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitulo.setForeground(TEXT_SOFT);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(subtitulo);

        JButton btnNuevo = crearBotonPrincipal("Registrar Sanción", SENA_GREEN, Color.WHITE);
        btnNuevo.setPreferredSize(new Dimension(196, 42));
        btnNuevo.addActionListener(this::abrirFormularioNuevo);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        acciones.setOpaque(false);
        acciones.add(btnNuevo);

        cont.add(textos, BorderLayout.WEST);
        cont.add(acciones, BorderLayout.EAST);
        return cont;
    }

    private JPanel crearPanelPrincipal() {
        RoundedPanel panel = new RoundedPanel(SURFACE, BORDER, 22);
        panel.setLayout(new BorderLayout(0, 16));
        panel.setBorder(new EmptyBorder(18, 18, 18, 18));
        panel.add(crearBarraSuperior(), BorderLayout.NORTH);
        panel.add(crearTabla(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearBarraSuperior() {
        JPanel barra = new JPanel(new BorderLayout(14, 12));
        barra.setOpaque(false);

        txtBuscar = new PlaceholderTextField("Usuario, correo o recurso...");
        txtBuscar.setPreferredSize(new Dimension(320, 42));
        estilizarCampoTexto(txtBuscar);
        txtBuscar.addActionListener(evt -> buscar());

        cmbEstado = new JComboBox<>(new String[]{
            "Todos los estados",
            "Acumulando",
            "Activa (Suspendido)",
            "Cumplida",
            "Condonada"
        });
        cmbEstado.setPreferredSize(new Dimension(220, 42));
        estilizarCombo(cmbEstado);
        cmbEstado.addActionListener(evt -> buscar());

        JButton btnBuscar = crearBotonPrincipal("Buscar", SENA_GREEN, Color.WHITE);
        btnBuscar.setPreferredSize(new Dimension(110, 42));
        btnBuscar.addActionListener(evt -> buscar());

        JPanel izquierda = new JPanel(new BorderLayout(0, 6));
        izquierda.setOpaque(false);
        JLabel lbl = new JLabel("Buscar");
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setForeground(TEXT_DARK);
        izquierda.add(lbl, BorderLayout.NORTH);
        JPanel filaFiltros = new JPanel(new BorderLayout(10, 0));
        filaFiltros.setOpaque(false);
        filaFiltros.add(txtBuscar, BorderLayout.CENTER);
        filaFiltros.add(cmbEstado, BorderLayout.EAST);
        izquierda.add(filaFiltros, BorderLayout.CENTER);

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        derecha.setOpaque(false);
        derecha.add(btnBuscar);

        barra.add(izquierda, BorderLayout.CENTER);
        barra.add(derecha, BorderLayout.EAST);
        return barra;
    }

    private JScrollPane crearTabla() {
        modeloSanciones = new DefaultTableModel(
                new Object[]{"ID", "Fecha", "Usuario", "Recurso", "Dias retraso", "Suspension", "Estado", "Acciones"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7;
            }
        };

        tablaSanciones = new JTable(modeloSanciones);
        tablaSanciones.setRowHeight(68);
        tablaSanciones.setShowGrid(false);
        tablaSanciones.setIntercellSpacing(new Dimension(0, 0));
        tablaSanciones.setFillsViewportHeight(true);
        tablaSanciones.setSelectionBackground(SENA_GREEN_SOFT);
        tablaSanciones.setSelectionForeground(TEXT_DARK);

        JTableHeader header = tablaSanciones.getTableHeader();
        header.setReorderingAllowed(false);
        header.setFont(new Font("SansSerif", Font.BOLD, 12));
        header.setForeground(new Color(94, 104, 118));
        header.setBackground(Color.WHITE);
        header.setBorder(new MatteBorder(0, 0, 1, 0, BORDER));

        tablaSanciones.setDefaultRenderer(Object.class, new CeldaRenderer());
        tablaSanciones.getColumnModel().getColumn(1).setCellRenderer(new FechaRenderer());
        tablaSanciones.getColumnModel().getColumn(2).setCellRenderer(new UsuarioRenderer());
        tablaSanciones.getColumnModel().getColumn(3).setCellRenderer(new RecursoRenderer());
        tablaSanciones.getColumnModel().getColumn(6).setCellRenderer(new EstadoRenderer());
        tablaSanciones.getColumnModel().getColumn(7).setCellRenderer(new AccionesRenderer());
        tablaSanciones.getColumnModel().getColumn(7).setCellEditor(new AccionesEditor());

        sorter = new TableRowSorter<>(modeloSanciones);
        tablaSanciones.setRowSorter(sorter);

        ajustarAnchos();

        JScrollPane scroll = new JScrollPane(tablaSanciones);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(SURFACE);
        return scroll;
    }

    private void ajustarAnchos() {
        TableColumnModel cols = tablaSanciones.getColumnModel();
        cols.getColumn(0).setMinWidth(0);
        cols.getColumn(0).setMaxWidth(0);
        cols.getColumn(0).setPreferredWidth(0);
        cols.getColumn(1).setPreferredWidth(150);
        cols.getColumn(2).setPreferredWidth(260);
        cols.getColumn(3).setPreferredWidth(220);
        cols.getColumn(4).setPreferredWidth(95);
        cols.getColumn(5).setPreferredWidth(120);
        cols.getColumn(6).setPreferredWidth(120);
        cols.getColumn(7).setPreferredWidth(110);
    }

    private void cargarTabla() {
        new SwingWorker<List<Sancion>, Void>() {
            @Override
            protected List<Sancion> doInBackground() throws Exception {
                return controlador.listarTodas();
            }

            @Override
            protected void done() {
                try {
                    List<Sancion> lista = get();
                    modeloSanciones.setRowCount(0);
                    for (Sancion s : lista) {
                        modeloSanciones.addRow(new Object[]{
                            s.getIdSancion(),
                            s.getFechaSancion(),
                            mostrarUsuario(s),
                            mostrarRecurso(s),
                            s.getDiasRetraso(),
                            s.getDiasSuspension(),
                            mostrarEstado(s),
                            "Acciones"
                        });
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    modeloSanciones.setRowCount(0);
                }
            }
        }.execute();
    }

    private void buscar() {
        try {
            String texto = txtBuscar != null ? txtBuscar.getText() : "";
            String estado = cmbEstado != null ? String.valueOf(cmbEstado.getSelectedItem()) : "Todos los estados";
            List<Sancion> lista = controlador.buscar(texto, estado);
            modeloSanciones.setRowCount(0);
            for (Sancion s : lista) {
                modeloSanciones.addRow(new Object[]{
                    s.getIdSancion(),
                    s.getFechaSancion(),
                    mostrarUsuario(s),
                    mostrarRecurso(s),
                    s.getDiasRetraso(),
                    s.getDiasSuspension(),
                    mostrarEstado(s),
                    "Acciones"
                });
            }
        } catch (Exception ex) {
            System.out.println("[FRMSanciones] Error en busqueda: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void estilizarCombo(JComboBox<String> combo) {
        combo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        combo.setBackground(Color.WHITE);
        combo.setForeground(TEXT_DARK);
        combo.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 1, 1, 1, BORDER),
                new EmptyBorder(6, 12, 6, 12)
        ));
    }

    private void abrirFormularioNuevo(ActionEvent evt) {
        abrirDialogo(null);
    }

    private void abrirDialogo(Sancion actual) {
        JDialog dlg = new JDialog();
        dlg.setModal(true);
        dlg.setTitle(actual == null ? "Registrar Sancion" : "Modificar Sancion");
        dlg.setSize(620, 620);
        dlg.setLocationRelativeTo(this);

        JPanel raiz = new JPanel(new BorderLayout(0, 14));
        raiz.setBorder(new EmptyBorder(18, 18, 18, 18));
        raiz.setBackground(Color.WHITE);

        JPanel campos = new JPanel(new GridBagLayout());
        campos.setOpaque(false);

        JTextField txtId = new JTextField(actual != null ? String.valueOf(actual.getIdSancion()) : "");
        JTextField txtFecha = new JTextField(actual != null && actual.getFechaSancion() != null ? actual.getFechaSancion().toString() : LocalDate.now().toString());
        JTextField txtUsuario = new JTextField(actual != null && actual.getIdUsuario() != null ? String.valueOf(actual.getIdUsuario()) : "");
        JTextField txtCorreo = new JTextField(actual != null ? value(actual.getCorreoUsuario()) : "");
        JTextField txtRecurso = new JTextField(actual != null ? value(actual.getNombreRecurso()) : "");
        JTextField txtRetraso = new JTextField(actual != null ? String.valueOf(actual.getDiasRetraso()) : "");
        JTextField txtSuspension = new JTextField(actual != null ? String.valueOf(actual.getDiasSuspension()) : "");
        JTextField txtEstado = new JTextField(actual != null ? value(actual.getEstado()) : "activa");
        JTextArea txtDetalle = new JTextArea(actual != null ? value(actual.getDetalle()) : "");
        txtDetalle.setLineWrap(true);
        txtDetalle.setWrapStyleWord(true);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        campos.add(campo("ID", txtId, true), gbc);
        gbc.gridy++;
        campos.add(campo("Fecha (yyyy-MM-dd)", txtFecha, false), gbc);
        gbc.gridy++;
        campos.add(campo("Usuario", txtUsuario, false), gbc);
        gbc.gridy++;
        campos.add(campo("Correo", txtCorreo, false), gbc);
        gbc.gridy++;
        campos.add(campo("Recurso", txtRecurso, false), gbc);
        gbc.gridy++;
        campos.add(campo("Dias retraso", txtRetraso, false), gbc);
        gbc.gridy++;
        campos.add(campo("Suspension", txtSuspension, false), gbc);
        gbc.gridy++;
        campos.add(campo("Estado", txtEstado, false), gbc);
        gbc.gridy++;
        campos.add(campoArea("Detalle", txtDetalle), gbc);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botones.setOpaque(false);
        JButton btnGuardar = crearBotonPrincipal("Guardar", SENA_GREEN, Color.WHITE);
        JButton btnCancelar = crearBotonSecundario("Cancelar");
        btnCancelar.addActionListener(e -> dlg.dispose());
        btnGuardar.addActionListener(e -> {
            try {
                if (txtFecha.getText().trim().isEmpty() || txtUsuario.getText().trim().isEmpty()
                        || txtRecurso.getText().trim().isEmpty() || txtRetraso.getText().trim().isEmpty()
                        || txtSuspension.getText().trim().isEmpty() || txtEstado.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dlg, "Completa los campos obligatorios.", "Validacion", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Sancion s = new Sancion();
                if (!txtId.getText().trim().isEmpty()) {
                    s.setIdSancion(Integer.parseInt(txtId.getText().trim()));
                }
                s.setFechaSancion(Date.valueOf(LocalDate.parse(txtFecha.getText().trim())));
                s.setIdUsuario(Integer.valueOf(txtUsuario.getText().trim()));
                s.setCorreoUsuario(txtCorreo.getText().trim());
                s.setNombreRecurso(txtRecurso.getText().trim());
                s.setDiasRetraso(Integer.parseInt(txtRetraso.getText().trim()));
                s.setDiasSuspension(Integer.parseInt(txtSuspension.getText().trim()));
                s.setEstado(txtEstado.getText().trim());
                s.setDetalle(txtDetalle.getText().trim());
                s.setCondonada("condonada".equalsIgnoreCase(txtEstado.getText().trim()));
                controlador.guardar(s);
                cargarTabla();
                dlg.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "No se pudo guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        botones.add(btnCancelar);
        botones.add(btnGuardar);

        raiz.add(campos, BorderLayout.CENTER);
        raiz.add(botones, BorderLayout.SOUTH);
        dlg.setContentPane(raiz);
        dlg.setVisible(true);
    }

    private JPanel campo(String titulo, JTextField field, boolean soloLectura) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        JLabel lbl = new JLabel(titulo);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setForeground(TEXT_DARK);
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(new MatteBorder(1, 1, 1, 1, BORDER), new EmptyBorder(8, 10, 8, 10)));
        field.setEditable(!soloLectura);
        p.add(lbl, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private JPanel campoArea(String titulo, JTextArea area) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        JLabel lbl = new JLabel(titulo);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setForeground(TEXT_DARK);
        area.setFont(new Font("SansSerif", Font.PLAIN, 13));
        area.setBorder(BorderFactory.createCompoundBorder(new MatteBorder(1, 1, 1, 1, BORDER), new EmptyBorder(8, 10, 8, 10)));
        JScrollPane sp = new JScrollPane(area);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setPreferredSize(new Dimension(10, 90));
        p.add(lbl, BorderLayout.NORTH);
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private String mostrarUsuario(Sancion s) {
        String nombre = value(s.getCorreoUsuario());
        if (s.getIdUsuario() != null) {
            nombre = "ID " + s.getIdUsuario() + "<br>" + nombre;
        }
        return "<html><div style='line-height:1.2;'><b>" + nombre + "</b></div></html>";
    }

    private String mostrarRecurso(Sancion s) {
        String tipo = value(s.getTipoRecurso()).toUpperCase();
        String recurso = value(s.getNombreRecurso());
        if (recurso.isEmpty()) {
            recurso = value(s.getDetalle());
        }
        return "<html>"
                + "<div style='line-height:1.15;'>"
                + "<span style='background-color:#1faa55;color:white;padding:4px 10px;border-radius:12px;font-weight:bold;'>"
                + tipo
                + "</span><br>"
                + "<span style='color:#111827;font-size:12px;'>" + recurso + "</span>"
                + "</div></html>";
    }

    private String mostrarEstado(Sancion s) {
        return s.getEstado() != null ? s.getEstado() : "";
    }

    private String value(String s) {
        return s != null ? s : "";
    }

    private JButton crearBotonPrincipal(String texto, Color fondo, Color textoColor) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("SansSerif", Font.BOLD, 13));
        boton.setBackground(fondo);
        boton.setForeground(textoColor);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        boton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        boton.setUI(new BasicButtonUI());
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(fondo.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(fondo);
            }
        });
        return boton;
    }

    private JButton crearBotonSecundario(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("SansSerif", Font.BOLD, 13));
        boton.setBackground(new Color(245, 248, 250));
        boton.setForeground(TEXT_DARK);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createCompoundBorder(new MatteBorder(1, 1, 1, 1, BORDER), new EmptyBorder(10, 16, 10, 16)));
        return boton;
    }

    private void estilizarCampoTexto(JTextField campo) {
        campo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        campo.setBorder(BorderFactory.createCompoundBorder(new MatteBorder(1, 1, 1, 1, BORDER), new EmptyBorder(10, 12, 10, 12)));
        campo.setBackground(Color.WHITE);
    }

    private static final class CeldaRenderer extends DefaultTableCellRenderer {
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

    private static final class FechaRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setHorizontalAlignment(SwingConstants.LEFT);
            lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
            if (value instanceof java.util.Date) {
                LocalDateTime ldt;
                if (value instanceof Date) {
                    ldt = ((Date) value).toLocalDate().atStartOfDay();
                } else if (value instanceof java.sql.Timestamp) {
                    ldt = ((java.sql.Timestamp) value).toLocalDateTime();
                } else if (value instanceof Time) {
                    ldt = LocalDate.now().atTime(((Time) value).toLocalTime());
                } else {
                    ldt = LocalDate.now().atStartOfDay();
                }
                lbl.setText("<html><div style='line-height:1.1;'>"
                        + "<div style='color:#111827; font-weight:600;'>" + ldt.toLocalDate().format(FECHA_FMT) + "</div>"
                        + "<div style='color:#6b7280; font-size:11px;'>" + ldt.toLocalTime().format(HORA_FMT) + "</div>"
                        + "</div></html>");
            }
            return lbl;
        }
    }

    private static final class UsuarioRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setText(String.valueOf(value));
            lbl.setHorizontalAlignment(SwingConstants.LEFT);
            lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
            return lbl;
        }
    }

    private static final class RecursoRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setText(String.valueOf(value));
            lbl.setHorizontalAlignment(SwingConstants.LEFT);
            return lbl;
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
                lbl.setBackground(new Color(240, 242, 246));
                lbl.setForeground(new Color(90, 99, 112));
            } else {
                lbl.setBackground(SENA_RED_SOFT);
                lbl.setForeground(SENA_RED);
            }
            lbl.setOpaque(true);
            lbl.setBorder(new EmptyBorder(8, 12, 8, 12));
            return lbl;
        }
    }

    private final class AccionesRenderer extends JPanel implements TableCellRenderer {
        private AccionesRenderer() {
            setOpaque(false);
            setLayout(new FlowLayout(FlowLayout.CENTER, 6, 4));
            JButton btnVer = new JButton(" Ver");
            btnVer.setPreferredSize(new Dimension(78, 40));
            btnVer.setFocusPainted(false);
            btnVer.setBackground(Color.WHITE);
            btnVer.setForeground(new Color(95, 105, 121));
            btnVer.setBorder(BorderFactory.createCompoundBorder(
                    new MatteBorder(1, 1, 1, 1, new Color(209, 216, 224)),
                    new EmptyBorder(10, 10, 10, 10)
            ));
            btnVer.setIcon(new EyeIcon(14, new Color(95, 105, 121)));
            add(btnVer);
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
            JButton btnEditar = new JButton("Editar");
            JButton btnEliminar = new JButton("Eliminar");
            btnVer.setFocusPainted(false);
            btnEditar.setFocusPainted(false);
            btnEliminar.setFocusPainted(false);
            btnVer.setPreferredSize(new Dimension(78, 40));
            btnVer.setBackground(Color.WHITE);
            btnVer.setForeground(new Color(95, 105, 121));
            btnVer.setBorder(BorderFactory.createCompoundBorder(
                    new MatteBorder(1, 1, 1, 1, new Color(209, 216, 224)),
                    new EmptyBorder(10, 10, 10, 10)
            ));
            btnVer.setIcon(new EyeIcon(14, new Color(95, 105, 121)));
            btnVer.setHorizontalTextPosition(SwingConstants.RIGHT);
            btnEditar.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
            btnEliminar.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
            btnVer.addActionListener(e -> {
                fireEditingStopped();
                verSeleccionada();
            });
            btnEditar.addActionListener(e -> {
                fireEditingStopped();
                editarSeleccionada();
            });
            btnEliminar.addActionListener(e -> {
                fireEditingStopped();
                eliminarSeleccionada();
            });
            panel.add(btnVer);
            panel.add(btnEditar);
            panel.add(btnEliminar);
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

    private void verSeleccionada() {
        int row = tablaSanciones.getSelectedRow();
        if (row < 0) {
            return;
        }
        int model = tablaSanciones.convertRowIndexToModel(row);
        JOptionPane.showMessageDialog(this,
                "Fecha: " + modeloSanciones.getValueAt(model, 1) + "\n"
                + "Usuario: " + modeloSanciones.getValueAt(model, 2) + "\n"
                + "Recurso: " + modeloSanciones.getValueAt(model, 3) + "\n"
                + "Dias retraso: " + modeloSanciones.getValueAt(model, 4) + "\n"
                + "Suspension: " + modeloSanciones.getValueAt(model, 5) + "\n"
                + "Estado: " + modeloSanciones.getValueAt(model, 6),
                "Detalle", JOptionPane.INFORMATION_MESSAGE);
    }

    private void editarSeleccionada() {
        int row = tablaSanciones.getSelectedRow();
        if (row < 0) {
            return;
        }
        int model = tablaSanciones.convertRowIndexToModel(row);
        Sancion s = new Sancion();
        s.setIdSancion(Integer.parseInt(String.valueOf(modeloSanciones.getValueAt(model, 0))));
        s.setFechaSancion(Date.valueOf(LocalDate.parse(String.valueOf(modeloSanciones.getValueAt(model, 1)))));
        abrirDialogo(s);
    }

    private void eliminarSeleccionada() {
        int row = tablaSanciones.getSelectedRow();
        if (row < 0) {
            return;
        }
        int model = tablaSanciones.convertRowIndexToModel(row);
        int id = Integer.parseInt(String.valueOf(modeloSanciones.getValueAt(model, 0)));
        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar la sancion seleccionada?", "Eliminar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            controlador.eliminar(id);
            cargarTabla();
        }
    }

    private static class FondoInternoPanel extends JPanel {

        private static final ImageIcon FONDO;

        static {
            java.net.URL url = FRMSanciones.class.getResource("/imagenes/fondo.jpg");
            FONDO = url != null ? new ImageIcon(url) : null;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (FONDO != null && FONDO.getImage() != null) {
                    int w = getWidth();
                    int h = getHeight();
                    int iw = FONDO.getIconWidth();
                    int ih = FONDO.getIconHeight();
                    double scale = Math.max((double) w / iw, (double) h / ih);
                    int drawW = (int) Math.round(iw * scale);
                    int drawH = (int) Math.round(ih * scale);
                    int x = (w - drawW) / 2;
                    int y = (h - drawH) / 2;
                    g2.drawImage(FONDO.getImage(), x, y, drawW, drawH, this);
                } else {
                    g2.setPaint(new GradientPaint(0, 0, new Color(248, 250, 252), 0, getHeight(), new Color(235, 243, 238)));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
                g2.setComposite(AlphaComposite.SrcOver.derive(0.72f));
                g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 235), 0, getHeight(), new Color(248, 248, 248, 224)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            } finally {
                g2.dispose();
            }
        }
    }

    private static final class EyeIcon implements javax.swing.Icon {
        private final int size;
        private final Color color;

        private EyeIcon(int size, Color color) {
            this.size = size;
            this.color = color;
        }

        @Override
        public int getIconWidth() {
            return size + 2;
        }

        @Override
        public int getIconHeight() {
            return size;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.drawArc(x, y + 2, size, size - 5, 0, 180);
                g2.drawArc(x, y - 1, size, size - 5, 180, 180);
                g2.fillOval(x + size / 2 - 2, y + size / 2 - 2, 4, 4);
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
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getText().isEmpty() && !isFocusOwner()) {
                Graphics2D g2 = (Graphics2D) g.create();
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
        setPreferredSize(new Dimension(1120, 720));
    }
}
