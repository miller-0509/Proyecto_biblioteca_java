package Vista;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
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
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import Modelo.Equipo;
import Modelo.EquipoDAO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JInternalFrame;
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

public class Equipos extends JInternalFrame {

    private static final Color SENA_GREEN = new Color(46, 170, 84);
    private static final Color SENA_GREEN_DARK = new Color(24, 123, 61);
    private static final Color SENA_GREEN_SOFT = new Color(240, 252, 244);
    private static final Color TEXT_DARK = new Color(28, 34, 45);
    private static final Color TEXT_SOFT = new Color(96, 105, 121);
    private static final Color BORDER = new Color(224, 229, 236);
    private static final Color SURFACE = new Color(255, 255, 255);
    private static final Color TABLE_ALT = new Color(249, 251, 253);
    private static final Color TABLE_HOVER = new Color(236, 248, 241);

    private PlaceholderTextField txtBuscar;
    private JComboBox<String> cmbEstado;
    private JComboBox<String> cmbTipo;
    private JTable tablaEquipos;
    private DefaultTableModel modeloEquipos;
    private TableRowSorter<DefaultTableModel> filtroTabla;
    private EquipoDAO equipoDAO;

    public Equipos() {
        initComponents();
        construirVista();
        equipoDAO = new EquipoDAO();
        cargarEquipos();
        aplicarFiltros();
    }

    private void construirVista() {
        setTitle("Gestión de Equipos");
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

        JLabel titulo = new JLabel("Gestión de Equipos");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 26));
        titulo.setForeground(TEXT_DARK);

        JLabel subtitulo = new JLabel("Inventario, estados y acciones rápidas de los equipos del sistema.");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitulo.setForeground(TEXT_SOFT);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(5));
        textos.add(subtitulo);

        JButton btnRegistrar = crearBotonPrincipal("Registrar Equipo", new PlusIcon(13, Color.WHITE));
        btnRegistrar.setPreferredSize(new Dimension(178, 42));
        btnRegistrar.addActionListener(evt -> {
            try {
                Equipo nuevo = new Equipo();
                nuevo.setEstado("disponible");
                nuevo.setDisponiblePrestamo(true);
                nuevo.setTiempoMaxPrestamo(7);
                mostrarDialogoEquipo(nuevo, true);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        acciones.setOpaque(false);
        acciones.add(btnRegistrar);

        encabezado.add(textos, BorderLayout.WEST);
        encabezado.add(acciones, BorderLayout.EAST);
        return encabezado;
    }

    private JPanel crearPanelAdministrativo() {
        RoundedPanel panel = new RoundedPanel(SURFACE, BORDER, 22);
        panel.setLayout(new BorderLayout(0, 18));
        panel.setBorder(new EmptyBorder(18, 18, 18, 18));

        panel.add(crearBarraFiltros(), BorderLayout.NORTH);
        panel.add(crearTablaEquipos(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearBarraFiltros() {
        JPanel barra = new JPanel(new GridBagLayout());
        barra.setOpaque(false);

        txtBuscar = new PlaceholderTextField("Nombre, serie o marca...");
        txtBuscar.setPreferredSize(new Dimension(320, 42));
        estilizarCampoTexto(txtBuscar);

        cmbEstado = crearCombo(new String[]{
            "Todos los estados",
            "Disponible",
            "En préstamo",
            "En mantenimiento"
        });

        cmbTipo = crearCombo(new String[]{
            "Todos los tipos",
            "Monitor",
            "Proyector",
            "Teclado",
            "Tablet",
            "Otros"
        });

        JButton btnBuscar = crearBotonPrincipal("Buscar", new SearchIcon(15, Color.WHITE));
        btnBuscar.setPreferredSize(new Dimension(112, 42));
        btnBuscar.addActionListener(evt -> aplicarFiltros());
        txtBuscar.addActionListener(evt -> aplicarFiltros());
        cmbEstado.addActionListener(evt -> aplicarFiltros());
        cmbTipo.addActionListener(evt -> aplicarFiltros());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.weightx = 1.0;
        barra.add(crearCampoConEtiqueta("Buscar equipo", txtBuscar), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.28;
        barra.add(crearCampoConEtiqueta("Estado", cmbEstado), gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.28;
        barra.add(crearCampoConEtiqueta("Tipo", cmbTipo), gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.0;
        gbc.insets = new Insets(21, 0, 0, 0);
        barra.add(btnBuscar, gbc);

        return barra;
    }

    private JPanel crearCampoConEtiqueta(String etiqueta, Component campo) {
        JPanel panel = new JPanel(new BorderLayout(0, 7));
        panel.setOpaque(false);

        JLabel label = new JLabel(etiqueta);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(TEXT_SOFT);

        panel.add(label, BorderLayout.NORTH);
        panel.add(campo, BorderLayout.CENTER);
        return panel;
    }

    private JScrollPane crearTablaEquipos() {
        modeloEquipos = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Tipo", "Número de Serie", "Estado", "Ubicación", "Acciones", "Marca"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };

        tablaEquipos = new JTable(modeloEquipos);
        filtroTabla = new TableRowSorter<>(modeloEquipos);
        tablaEquipos.setRowSorter(filtroTabla);

        tablaEquipos.setRowHeight(54);
        tablaEquipos.setShowVerticalLines(false);
        tablaEquipos.setShowHorizontalLines(true);
        tablaEquipos.setGridColor(new Color(235, 239, 244));
        tablaEquipos.setIntercellSpacing(new Dimension(0, 0));
        tablaEquipos.setSelectionBackground(new Color(231, 246, 236));
        tablaEquipos.setSelectionForeground(TEXT_DARK);
        tablaEquipos.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tablaEquipos.setForeground(TEXT_DARK);
        tablaEquipos.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tablaEquipos.setFillsViewportHeight(true);
        instalarHoverTabla();

        JTableHeader header = tablaEquipos.getTableHeader();
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 44));
        header.setFont(new Font("SansSerif", Font.BOLD, 12));
        header.setForeground(TEXT_SOFT);
        header.setBackground(new Color(247, 249, 251));
        header.setBorder(new MatteBorder(0, 0, 1, 0, BORDER));
        header.setReorderingAllowed(false);

        tablaEquipos.setDefaultRenderer(Object.class, new ZebraRenderer());
        tablaEquipos.getColumnModel().getColumn(4).setCellRenderer(new EstadoRenderer());
        tablaEquipos.getColumnModel().getColumn(6).setCellRenderer(new AccionesRenderer());
        tablaEquipos.getColumnModel().getColumn(6).setCellEditor(new AccionesEditor());
        tablaEquipos.removeColumn(tablaEquipos.getColumnModel().getColumn(7));
        configurarAnchosTabla();

        JScrollPane scroll = new JScrollPane(tablaEquipos);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBackground(Color.WHITE);
        return scroll;
    }

    private void configurarAnchosTabla() {
        TableColumnModel columnas = tablaEquipos.getColumnModel();
        columnas.getColumn(0).setMinWidth(52);
        columnas.getColumn(0).setPreferredWidth(58);
        columnas.getColumn(1).setPreferredWidth(190);
        columnas.getColumn(2).setPreferredWidth(120);
        columnas.getColumn(3).setPreferredWidth(160);
        columnas.getColumn(4).setPreferredWidth(140);
        columnas.getColumn(5).setPreferredWidth(150);
        columnas.getColumn(6).setMinWidth(120);
        columnas.getColumn(6).setPreferredWidth(132);
    }

    private void instalarHoverTabla() {
        tablaEquipos.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = tablaEquipos.rowAtPoint(e.getPoint());
                Object actual = tablaEquipos.getClientProperty("hoverRow");
                if (!(actual instanceof Integer) || ((Integer) actual) != row) {
                    tablaEquipos.putClientProperty("hoverRow", row);
                    tablaEquipos.repaint();
                }
            }
        });

        tablaEquipos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                tablaEquipos.putClientProperty("hoverRow", -1);
                tablaEquipos.repaint();
            }
        });
    }

    private void cargarEquipos() {
        modeloEquipos.setRowCount(0);
        try {
            java.util.List<Equipo> lista = equipoDAO.listarTodos();
            if (lista == null) {
                JOptionPane.showMessageDialog(this,
                        "Error: la consulta devolvió null.\nVerifique la conexión a la base de datos.",
                        "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
                return;
            }
            for (Equipo e : lista) {
                modeloEquipos.addRow(new Object[]{
                    String.format("%03d", e.getIdEquipo()),
                    e.getNombre(),
                    e.getTipoEquipo(),
                    e.getNumeroSerie(),
                    estadoToDisplay(e.getEstado()),
                    e.getUbicacion(),
                    "",
                    e.getMarca()
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al cargar equipos: " + ex.getMessage(),
                    "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refrescarTabla() {
        cargarEquipos();
    }

    private void aplicarFiltros() {
        if (filtroTabla == null) {
            return;
        }

        List<RowFilter<Object, Object>> filtros = new ArrayList<>();
        String busqueda = txtBuscar == null ? "" : txtBuscar.getText().trim();

        if (busqueda.length() > 0 && !txtBuscar.isShowingPlaceholder()) {
            String patron = "(?i)" + Pattern.quote(busqueda);
            filtros.add(RowFilter.regexFilter(patron, 1, 3, 7));
        }

        String estado = cmbEstado == null ? "Todos los estados" : String.valueOf(cmbEstado.getSelectedItem());
        if (!"Todos los estados".equals(estado)) {
            filtros.add(RowFilter.regexFilter("^" + Pattern.quote(estado) + "$", 4));
        }

        String tipo = cmbTipo == null ? "Todos los tipos" : String.valueOf(cmbTipo.getSelectedItem());
        if (!"Todos los tipos".equals(tipo)) {
            filtros.add(RowFilter.regexFilter("^" + Pattern.quote(tipo) + "$", 2));
        }

        if (filtros.isEmpty()) {
            filtroTabla.setRowFilter(null);
            return;
        }

        if (busqueda.length() > 0 && !txtBuscar.isShowingPlaceholder()) {
            RowFilter<Object, Object> texto = filtros.get(0);
            List<RowFilter<Object, Object>> finales = new ArrayList<>();
            finales.add(texto);
            for (int i = 1; i < filtros.size(); i++) {
                finales.add(filtros.get(i));
            }
            filtroTabla.setRowFilter(RowFilter.andFilter(finales));
        } else {
            filtroTabla.setRowFilter(RowFilter.andFilter(filtros));
        }
    }

    private String estadoToDisplay(String estado) {
        if (estado == null) return "Disponible";
        switch (estado) {
            case "disponible": return "Disponible";
            case "prestado": return "En préstamo";
            case "mantenimiento": return "En mantenimiento";
            case "dañado": return "Dañado";
            default: return estado;
        }
    }

    private String displayToEstado(String display) {
        if (display == null) return "disponible";
        switch (display) {
            case "Disponible": return "disponible";
            case "En préstamo": return "prestado";
            case "En mantenimiento": return "mantenimiento";
            case "Dañado": return "dañado";
            default: return "disponible";
        }
    }

    private void mostrarPopup(Component invoker, int row) {
        int modelRow = tablaEquipos.convertRowIndexToModel(row);
        if (modelRow < 0) return;
        JPopupMenu popup = new JPopupMenu();
        JMenuItem editar = new JMenuItem("Editar");
        editar.addActionListener(evt -> {
            mostrarDialogoEditar(modelRow);
        });
        JMenuItem eliminar = new JMenuItem("Eliminar");
        eliminar.addActionListener(evt -> {
            eliminarEquipo(modelRow);
        });
        popup.add(editar);
        popup.add(eliminar);
        popup.show(invoker, 0, invoker.getHeight());
    }

    private void mostrarDialogoVer(int modelRow) {
        try {
            Object idObj = modeloEquipos.getValueAt(modelRow, 0);
            if (idObj == null) return;
            int id = Integer.parseInt(idObj.toString());
            Equipo e = equipoDAO.buscarPorId(id);
            if (e != null) mostrarDialogoEquipo(e, false);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al cargar detalles del equipo: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarDialogoEditar(int modelRow) {
        try {
            Object idObj = modeloEquipos.getValueAt(modelRow, 0);
            if (idObj == null) return;
            int id = Integer.parseInt(idObj.toString());
            Equipo e = equipoDAO.buscarPorId(id);
            if (e != null) mostrarDialogoEquipo(e, true);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al cargar datos del equipo: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarEquipo(int modelRow) {
        try {
            Object idObj = modeloEquipos.getValueAt(modelRow, 0);
            if (idObj == null) return;
            int id = Integer.parseInt(idObj.toString());
            String nombre = String.valueOf(modeloEquipos.getValueAt(modelRow, 1));

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "¿Está seguro de eliminar el equipo \"" + nombre + "\"?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                if (equipoDAO.eliminarLogico(id)) {
                    refrescarTabla();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Error al eliminar el equipo.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al eliminar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarDialogoEquipo(Equipo equipo, boolean editable) {
        java.awt.Window padre = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(padre, editable ? "Registrar / Editar Equipo" : "Detalles del Equipo",
                java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBorder(new EmptyBorder(20, 24, 20, 24));
        content.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        Font labelFont = new Font("SansSerif", Font.BOLD, 12);
        Color labelColor = TEXT_SOFT;

        JTextField txtNombre = new JTextField(equipo.getNombre());
        txtNombre.setPreferredSize(new Dimension(380, 36));

        JComboBox<String> cmbTipoEquipo = new JComboBox<>(new String[]{"Monitor", "Proyector", "Teclado", "Tablet", "Otros"});
        if (equipo.getTipoEquipo() != null) cmbTipoEquipo.setSelectedItem(equipo.getTipoEquipo());
        cmbTipoEquipo.setPreferredSize(new Dimension(380, 36));

        JTextField txtMarca = new JTextField(equipo.getMarca());
        txtMarca.setPreferredSize(new Dimension(380, 36));

        JTextField txtSerie = new JTextField(equipo.getNumeroSerie());
        txtSerie.setPreferredSize(new Dimension(380, 36));

        JComboBox<String> cmbEstadoEquipo = new JComboBox<>(new String[]{"Disponible", "En préstamo", "En mantenimiento", "Dañado"});
        cmbEstadoEquipo.setSelectedItem(estadoToDisplay(equipo.getEstado()));
        cmbEstadoEquipo.setPreferredSize(new Dimension(380, 36));

        JTextField txtUbicacion = new JTextField(equipo.getUbicacion());
        txtUbicacion.setPreferredSize(new Dimension(380, 36));

        JCheckBox chkPrestamo = new JCheckBox("Disponible para préstamo", equipo.isDisponiblePrestamo());
        chkPrestamo.setFont(new Font("SansSerif", Font.PLAIN, 13));

        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(equipo.getTiempoMaxPrestamo(), 0, 365, 1);
        JSpinner spnTiempo = new JSpinner(spinnerModel);
        spnTiempo.setPreferredSize(new Dimension(120, 36));

        JTextArea txtDescripcion = new JTextArea(equipo.getDescripcion(), 3, 30);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        scrollDesc.setPreferredSize(new Dimension(380, 60));

        java.awt.Component[][] campos = {
            {crearLabel("Nombre", labelFont, labelColor), txtNombre},
            {crearLabel("Tipo", labelFont, labelColor), cmbTipoEquipo},
            {crearLabel("Marca", labelFont, labelColor), txtMarca},
            {crearLabel("Número de Serie", labelFont, labelColor), txtSerie},
            {crearLabel("Estado", labelFont, labelColor), cmbEstadoEquipo},
            {crearLabel("Ubicación", labelFont, labelColor), txtUbicacion},
            {crearLabel("", labelFont, labelColor), chkPrestamo},
            {crearLabel("Tiempo máx. préstamo (días)", labelFont, labelColor), spnTiempo},
            {crearLabel("Descripción", labelFont, labelColor), scrollDesc}
        };

        gbc.gridy = 0;
        for (java.awt.Component[] fila : campos) {
            gbc.gridx = 0;
            content.add(fila[0], gbc);
            gbc.gridy++;
            content.add(fila[1], gbc);
            gbc.gridy++;
        }

        if (!editable) {
            txtNombre.setEditable(false);
            cmbTipoEquipo.setEnabled(false);
            txtMarca.setEditable(false);
            txtSerie.setEditable(false);
            cmbEstadoEquipo.setEnabled(false);
            txtUbicacion.setEditable(false);
            chkPrestamo.setEnabled(false);
            spnTiempo.setEnabled(false);
            txtDescripcion.setEditable(false);
        }

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botones.setOpaque(false);

        if (editable) {
            JButton btnGuardar = crearBotonPrincipal(equipo.getIdEquipo() > 0 ? "Actualizar" : "Guardar", null);
            btnGuardar.addActionListener(evt -> {
                try {
                    equipo.setNombre(txtNombre.getText().trim());
                    equipo.setTipoEquipo(String.valueOf(cmbTipoEquipo.getSelectedItem()));
                    equipo.setMarca(txtMarca.getText().trim());
                    equipo.setNumeroSerie(txtSerie.getText().trim());
                    equipo.setEstado(displayToEstado(String.valueOf(cmbEstadoEquipo.getSelectedItem())));
                    equipo.setUbicacion(txtUbicacion.getText().trim());
                    equipo.setDisponiblePrestamo(chkPrestamo.isSelected());
                    equipo.setTiempoMaxPrestamo((Integer) spnTiempo.getValue());
                    equipo.setDescripcion(txtDescripcion.getText().trim());

                    boolean exito;
                    if (equipo.getIdEquipo() > 0) {
                        exito = equipoDAO.actualizar(equipo);
                    } else {
                        int id = equipoDAO.insertar(equipo);
                        exito = id > 0;
                    }

                    if (exito) {
                        refrescarTabla();
                        dialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(dialog,
                                "Error al guardar el equipo.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(dialog,
                            "Error al guardar: " + ex.getMessage(),
                            "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
                }
            });
            botones.add(btnGuardar);
        }

        JButton btnCerrar = new JButton(editable ? "Cancelar" : "Cerrar");
        btnCerrar.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btnCerrar.setForeground(TEXT_SOFT);
        btnCerrar.setBackground(Color.WHITE);
        btnCerrar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(10, 18, 10, 18)));
        btnCerrar.setFocusPainted(false);
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.addActionListener(evt -> dialog.dispose());
        botones.add(btnCerrar);

        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        content.add(botones, gbc);

        dialog.add(content);
        dialog.pack();
        dialog.setLocationRelativeTo(padre);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    private JLabel crearLabel(String texto, Font font, Color color) {
        JLabel label = new JLabel(texto);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    private JComboBox<String> crearCombo(String[] opciones) {
        JComboBox<String> combo = new JComboBox<>();
        combo.setModel(new DefaultComboBoxModel<>(opciones));
        combo.setPreferredSize(new Dimension(180, 42));
        combo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        combo.setForeground(TEXT_DARK);
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(0, 10, 0, 10)
        ));
        return combo;
    }

    private void estilizarCampoTexto(JTextField campo) {
        campo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        campo.setForeground(TEXT_DARK);
        campo.setCaretColor(SENA_GREEN_DARK);
        campo.setBackground(Color.WHITE);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(0, 14, 0, 14)
        ));
        if (campo instanceof PlaceholderTextField && ((PlaceholderTextField) campo).isShowingPlaceholder()) {
            campo.setForeground(TEXT_SOFT);
        }
    }

    private JButton crearBotonPrincipal(String texto, Icon icono) {
        JButton boton = new JButton(texto, icono);
        boton.setFont(new Font("SansSerif", Font.BOLD, 13));
        boton.setForeground(Color.WHITE);
        boton.setBackground(SENA_GREEN);
        boton.setOpaque(true);
        boton.setContentAreaFilled(true);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setIconTextGap(8);
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(SENA_GREEN_DARK),
                new EmptyBorder(10, 15, 10, 15)
        ));
        boton.setUI(new BasicButtonUI());
        return boton;
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        setBorder(BorderFactory.createEmptyBorder());
        setPreferredSize(new Dimension(1060, 680));
    }

    private static final class ZebraRenderer extends DefaultTableCellRenderer {

        private ZebraRenderer() {
            setBorder(new EmptyBorder(0, 12, 0, 12));
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {

            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            c.setFont(new Font("SansSerif", column == 1 ? Font.BOLD : Font.PLAIN, 13));
            setForeground(TEXT_DARK);
            setHorizontalAlignment(column == 0 ? SwingConstants.CENTER : SwingConstants.LEFT);
            setBackground(resolverFondoFila(table, row, isSelected));
            return c;
        }
    }

    private static final class EstadoRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {

            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 13));
            panel.setOpaque(true);
            panel.setBackground(resolverFondoFila(table, row, isSelected));

            String estado = String.valueOf(value);
            Color fondo = new Color(220, 252, 231);
            Color texto = new Color(21, 128, 61);

            if ("En préstamo".equalsIgnoreCase(estado) || "Solicitado".equalsIgnoreCase(estado)) {
                fondo = new Color(254, 243, 199);
                texto = new Color(146, 64, 14);
            } else if ("No disponible".equalsIgnoreCase(estado) || "En mantenimiento".equalsIgnoreCase(estado)) {
                fondo = new Color(254, 226, 226);
                texto = new Color(185, 28, 28);
            }

            BadgeLabel badge = new BadgeLabel(estado, fondo, texto);
            panel.add(badge);
            return panel;
        }
    }

    private static final class AccionesRenderer implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {
            JPanel panel = crearPanelAcciones(resolverFondoFila(table, row, isSelected));
            return panel;
        }
    }

    private static Color resolverFondoFila(JTable table, int row, boolean isSelected) {
        if (isSelected) {
            return table.getSelectionBackground();
        }
        Object hover = table.getClientProperty("hoverRow");
        if (hover instanceof Integer && ((Integer) hover) == row) {
            return TABLE_HOVER;
        }
        return row % 2 == 0 ? Color.WHITE : TABLE_ALT;
    }

    private class AccionesEditor extends AbstractCellEditor implements TableCellEditor {

        private int editingRow;

        @Override
        public Object getCellEditorValue() {
            return "";
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table,
                Object value,
                boolean isSelected,
                int row,
                int column) {
            editingRow = row;
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 9));
            panel.setOpaque(true);
            panel.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);

            JButton ver = crearBotonAccion("Ver", new EyeIcon(15, SENA_GREEN_DARK));
            ver.addActionListener(evt -> {
                mostrarDialogoVer(editingRow);
                fireEditingStopped();
            });

            JButton mas = crearBotonIcono(new DotsIcon(15, TEXT_SOFT));
            mas.addActionListener(evt -> {
                mostrarPopup(mas, editingRow);
                fireEditingStopped();
            });

            panel.add(ver);
            panel.add(mas);
            return panel;
        }
    }

    private static JPanel crearPanelAcciones(Color fondo) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 9));
        panel.setOpaque(true);
        panel.setBackground(fondo);

        JButton ver = crearBotonAccion("Ver", new EyeIcon(15, SENA_GREEN_DARK));
        JButton mas = crearBotonIcono(new DotsIcon(15, TEXT_SOFT));

        panel.add(ver);
        panel.add(mas);
        return panel;
    }

    private static JButton crearBotonAccion(String texto, Icon icono) {
        JButton boton = new JButton(texto, icono);
        boton.setFont(new Font("SansSerif", Font.BOLD, 12));
        boton.setForeground(SENA_GREEN_DARK);
        boton.setBackground(SENA_GREEN_SOFT);
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(188, 232, 201)),
                new EmptyBorder(7, 10, 7, 10)
        ));
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setUI(new BasicButtonUI());
        return boton;
    }

    private static JButton crearBotonIcono(Icon icono) {
        JButton boton = new JButton(icono);
        boton.setPreferredSize(new Dimension(34, 32));
        boton.setBackground(Color.WHITE);
        boton.setBorder(BorderFactory.createLineBorder(BORDER));
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setUI(new BasicButtonUI());
        return boton;
    }

    private static final class PlaceholderTextField extends JTextField {

        private final String placeholder;
        private boolean showingPlaceholder = true;

        private PlaceholderTextField(String placeholder) {
            this.placeholder = placeholder;
            setText(placeholder);
            setForeground(TEXT_SOFT);
            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (showingPlaceholder) {
                        setText("");
                        setForeground(TEXT_DARK);
                        showingPlaceholder = false;
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (getText().trim().isEmpty()) {
                        setText(placeholder);
                        setForeground(TEXT_SOFT);
                        showingPlaceholder = true;
                    }
                }
            });
        }

        private boolean isShowingPlaceholder() {
            return showingPlaceholder;
        }
    }

    private static final class BadgeLabel extends JLabel {

        private final Color fill;

        private BadgeLabel(String text, Color fill, Color foreground) {
            super(text, SwingConstants.CENTER);
            this.fill = fill;
            setForeground(foreground);
            setFont(new Font("SansSerif", Font.BOLD, 12));
            setBorder(new EmptyBorder(7, 12, 7, 12));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(fill);
                g2.fillRoundRect(0, 1, getWidth(), getHeight() - 2, 18, 18);
            } finally {
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }

    private static final class RoundedPanel extends JPanel {

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
                int w = getWidth();
                int h = getHeight();

                g2.setComposite(AlphaComposite.SrcOver.derive(0.08f));
                g2.setColor(Color.BLACK);
                g2.fillRoundRect(4, 6, Math.max(0, w - 8), Math.max(0, h - 8), arc, arc);

                g2.setComposite(AlphaComposite.SrcOver);
                g2.setColor(fill);
                g2.fillRoundRect(0, 0, Math.max(0, w - 1), Math.max(0, h - 1), arc, arc);
                g2.setColor(borderColor);
                g2.drawRoundRect(0, 0, Math.max(0, w - 1), Math.max(0, h - 1), arc, arc);
            } finally {
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }

    private static final class FondoInternoPanel extends JPanel {

        private final ImageIcon fondo;

        private FondoInternoPanel() {
            URL url = Equipos.class.getResource("/imagenes/fondo.jpg");
            fondo = url != null ? new ImageIcon(url) : null;
            setOpaque(true);
        }

        @Override
        public Dimension getMaximumSize() {
            return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (fondo != null && fondo.getImage() != null) {
                    int w = getWidth();
                    int h = getHeight();
                    int iw = fondo.getIconWidth();
                    int ih = fondo.getIconHeight();

                    double scale = Math.max((double) w / iw, (double) h / ih);
                    int drawW = (int) Math.round(iw * scale);
                    int drawH = (int) Math.round(ih * scale);
                    int x = (w - drawW) / 2;
                    int y = (h - drawH) / 2;
                    g2.drawImage(fondo.getImage(), x, y, drawW, drawH, this);
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

    private static final class PlusIcon implements Icon {

        private final int size;
        private final Color color;

        private PlusIcon(int size, Color color) {
            this.size = size;
            this.color = color;
        }

        @Override
        public int getIconWidth() {
            return size;
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
                g2.fillRoundRect(x + size / 2 - 1, y, 3, size, 3, 3);
                g2.fillRoundRect(x, y + size / 2 - 1, size, 3, 3, 3);
            } finally {
                g2.dispose();
            }
        }
    }

    private static final class SearchIcon implements Icon {

        private final int size;
        private final Color color;

        private SearchIcon(int size, Color color) {
            this.size = size;
            this.color = color;
        }

        @Override
        public int getIconWidth() {
            return size + 2;
        }

        @Override
        public int getIconHeight() {
            return size + 2;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.drawOval(x, y, size - 5, size - 5);
                g2.drawLine(x + size - 5, y + size - 5, x + size + 1, y + size + 1);
            } finally {
                g2.dispose();
            }
        }
    }

    private static final class EyeIcon implements Icon {

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

    private static final class DotsIcon implements Icon {

        private final int size;
        private final Color color;

        private DotsIcon(int size, Color color) {
            this.size = size;
            this.color = color;
        }

        @Override
        public int getIconWidth() {
            return size;
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
                for (int i = 0; i < 3; i++) {
                    g2.fillOval(x + 2 + (i * 5), y + size / 2 - 2, 4, 4);
                }
            } finally {
                g2.dispose();
            }
        }
    }
}
