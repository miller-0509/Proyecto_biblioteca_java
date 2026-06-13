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
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
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
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import Modelo.Usuario;
import Modelo.UsuarioDAO;

public class Usuarios extends JInternalFrame {

    private static final Color SENA_GREEN = new Color(46, 170, 84);
    private static final Color SENA_GREEN_DARK = new Color(24, 123, 61);
    private static final Color SENA_GREEN_SOFT = new Color(240, 252, 244);
    private static final Color BLUE = new Color(78, 117, 217);
    private static final Color BLUE_SOFT = new Color(235, 242, 255);
    private static final Color RED = new Color(215, 74, 74);
    private static final Color RED_SOFT = new Color(253, 231, 231);
    private static final Color TEXT_DARK = new Color(28, 34, 45);
    private static final Color TEXT_SOFT = new Color(96, 105, 121);
    private static final Color BORDER = new Color(224, 229, 236);
    private static final Color SURFACE = new Color(255, 255, 255);
    private static final Color TABLE_ALT = new Color(249, 251, 253);
    private static final Color TABLE_HOVER = new Color(236, 248, 241);

    private PlaceholderTextField txtBuscar;
    private JComboBox<String> cmbRol;
    private JComboBox<String> cmbEstado;
    private JTable tablaUsuarios;
    private DefaultTableModel modeloUsuarios;
    private TableRowSorter<DefaultTableModel> filtroTabla;
    private UsuarioDAO usuarioDAO;

    public Usuarios() {
        initComponents();
        construirVista();
        usuarioDAO = new UsuarioDAO();
        cargarUsuarios();
        aplicarFiltros();
    }

    private void construirVista() {
        setTitle("Gestión de Usuarios");
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

        JLabel titulo = new JLabel("Gestión de Usuarios");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 26));
        titulo.setForeground(TEXT_DARK);

        JLabel subtitulo = new JLabel("Administración de aprendices, instructores y administradores.");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitulo.setForeground(TEXT_SOFT);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(5));
        textos.add(subtitulo);

        JButton btnNuevo = crearBotonPrincipal("Nuevo Usuario", new UserPlusIcon(13, Color.WHITE));
        btnNuevo.setPreferredSize(new Dimension(176, 42));
        btnNuevo.addActionListener(evt -> {
            try {
                Usuario nuevo = new Usuario();
                nuevo.setEstado("activo");
                mostrarDialogoUsuario(nuevo, true);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        acciones.setOpaque(false);
        acciones.add(btnNuevo);

        encabezado.add(textos, BorderLayout.WEST);
        encabezado.add(acciones, BorderLayout.EAST);
        return encabezado;
    }

    private JPanel crearPanelAdministrativo() {
        RoundedPanel panel = new RoundedPanel(SURFACE, BORDER, 22);
        panel.setLayout(new BorderLayout(0, 18));
        panel.setBorder(new EmptyBorder(18, 18, 18, 18));

        panel.add(crearBarraFiltros(), BorderLayout.NORTH);
        panel.add(crearTablaUsuarios(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearBarraFiltros() {
        JPanel barra = new JPanel(new GridBagLayout());
        barra.setOpaque(false);

        txtBuscar = new PlaceholderTextField("Nombre o correo...");
        txtBuscar.setPreferredSize(new Dimension(320, 42));
        estilizarCampoTexto(txtBuscar);

        cmbRol = crearCombo(new String[]{
            "Todos los roles",
            "Administrador",
            "Bibliotecario",
            "Almacenista",
            "Aprendiz",
            "Instructor"
        });

        cmbEstado = crearCombo(new String[]{
            "Todos los estados",
            "Activo",
            "Inactivo"
        });

        JButton btnBuscar = crearBotonPrincipal("Buscar", new SearchIcon(15, Color.WHITE));
        btnBuscar.setPreferredSize(new Dimension(112, 42));
        btnBuscar.addActionListener(evt -> aplicarFiltros());
        txtBuscar.addActionListener(evt -> aplicarFiltros());
        cmbRol.addActionListener(evt -> aplicarFiltros());
        cmbEstado.addActionListener(evt -> aplicarFiltros());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.weightx = 1.0;
        barra.add(crearCampoConEtiqueta("Buscar usuario", txtBuscar), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.28;
        barra.add(crearCampoConEtiqueta("Rol", cmbRol), gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.28;
        barra.add(crearCampoConEtiqueta("Estado", cmbEstado), gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.0;
        gbc.insets = new Insets(21, 0, 0, 0);
        barra.add(btnBuscar, gbc);

        return barra;
    }

    private JPanel crearCampoConEtiqueta(String etiqueta, Component campo) {
        JPanel panel = new JPanel(new BorderLayout(0, 7));
        panel.setOpaque(false);

        JLabel label = new JLabel(etiqueta.toUpperCase());
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(TEXT_SOFT);

        panel.add(label, BorderLayout.NORTH);
        panel.add(campo, BorderLayout.CENTER);
        return panel;
    }

    private JScrollPane crearTablaUsuarios() {
        modeloUsuarios = new DefaultTableModel(
                new Object[]{"Avatar", "Nombre Completo", "Correo", "Rol", "Estado", "Acciones", "ID"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };

        tablaUsuarios = new JTable(modeloUsuarios);
        filtroTabla = new TableRowSorter<>(modeloUsuarios);
        tablaUsuarios.setRowSorter(filtroTabla);

        tablaUsuarios.setRowHeight(68);
        tablaUsuarios.setShowVerticalLines(false);
        tablaUsuarios.setShowHorizontalLines(true);
        tablaUsuarios.setGridColor(new Color(235, 239, 244));
        tablaUsuarios.setIntercellSpacing(new Dimension(0, 0));
        tablaUsuarios.setSelectionBackground(new Color(231, 246, 236));
        tablaUsuarios.setSelectionForeground(TEXT_DARK);
        tablaUsuarios.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tablaUsuarios.setForeground(TEXT_DARK);
        tablaUsuarios.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tablaUsuarios.setFillsViewportHeight(true);
        instalarHoverTabla();

        JTableHeader header = tablaUsuarios.getTableHeader();
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 44));
        header.setFont(new Font("SansSerif", Font.BOLD, 12));
        header.setForeground(TEXT_SOFT);
        header.setBackground(new Color(247, 249, 251));
        header.setBorder(new MatteBorder(0, 0, 1, 0, BORDER));
        header.setReorderingAllowed(false);

        tablaUsuarios.setDefaultRenderer(Object.class, new ZebraRenderer());
        tablaUsuarios.getColumnModel().getColumn(0).setCellRenderer(new AvatarRenderer());
        tablaUsuarios.getColumnModel().getColumn(1).setCellRenderer(new NombreRenderer());
        tablaUsuarios.getColumnModel().getColumn(3).setCellRenderer(new RolRenderer());
        tablaUsuarios.getColumnModel().getColumn(4).setCellRenderer(new EstadoRenderer());
        tablaUsuarios.getColumnModel().getColumn(5).setCellRenderer(new AccionesRenderer());
        tablaUsuarios.getColumnModel().getColumn(5).setCellEditor(new AccionesEditor());
        tablaUsuarios.removeColumn(tablaUsuarios.getColumnModel().getColumn(6));
        configurarAnchosTabla();

        JScrollPane scroll = new JScrollPane(tablaUsuarios);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBackground(Color.WHITE);
        return scroll;
    }

    private void configurarAnchosTabla() {
        TableColumnModel columnas = tablaUsuarios.getColumnModel();
        columnas.getColumn(0).setMinWidth(56);
        columnas.getColumn(0).setPreferredWidth(62);
        columnas.getColumn(1).setPreferredWidth(240);
        columnas.getColumn(2).setPreferredWidth(240);
        columnas.getColumn(3).setPreferredWidth(140);
        columnas.getColumn(4).setPreferredWidth(120);
        columnas.getColumn(5).setMinWidth(124);
        columnas.getColumn(5).setPreferredWidth(138);
    }

    private void instalarHoverTabla() {
        tablaUsuarios.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = tablaUsuarios.rowAtPoint(e.getPoint());
                Object actual = tablaUsuarios.getClientProperty("hoverRow");
                if (!(actual instanceof Integer) || ((Integer) actual) != row) {
                    tablaUsuarios.putClientProperty("hoverRow", row);
                    tablaUsuarios.repaint();
                }
            }
        });

        tablaUsuarios.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                tablaUsuarios.putClientProperty("hoverRow", -1);
                tablaUsuarios.repaint();
            }
        });
    }

    private void cargarUsuarios() {
        modeloUsuarios.setRowCount(0);
        try {
            java.util.List<Usuario> lista = usuarioDAO.listarTodos();
            if (lista == null) return;
            for (Usuario u : lista) {
                modeloUsuarios.addRow(new Object[]{
                    u.getIniciales(),
                    u.getNombreCompleto(),
                    u.getCorreo(),
                    rolToDisplay(u.getRol()),
                    estadoToDisplay(u.getEstado()),
                    "",
                    u.getIdUsuario()
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al cargar usuarios: " + ex.getMessage(),
                    "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refrescarTabla() {
        cargarUsuarios();
    }

    private void aplicarFiltros() {
        if (filtroTabla == null) {
            return;
        }

        List<RowFilter<Object, Object>> filtros = new ArrayList<>();
        String busqueda = txtBuscar == null ? "" : txtBuscar.getText().trim();

        if (busqueda.length() > 0 && !txtBuscar.isShowingPlaceholder()) {
            String patron = "(?i)" + Pattern.quote(busqueda);
            filtros.add(RowFilter.regexFilter(patron, 1, 2));
        }

        String rol = cmbRol == null ? "Todos los roles" : String.valueOf(cmbRol.getSelectedItem());
        if (!"Todos los roles".equals(rol)) {
            filtros.add(RowFilter.regexFilter("(?i)^" + Pattern.quote(rol) + "$", 3));
        }

        String estado = cmbEstado == null ? "Todos los estados" : String.valueOf(cmbEstado.getSelectedItem());
        if (!"Todos los estados".equals(estado)) {
            filtros.add(RowFilter.regexFilter("(?i)^" + Pattern.quote(estado) + "$", 4));
        }

        if (filtros.isEmpty()) {
            filtroTabla.setRowFilter(null);
            return;
        }

        filtroTabla.setRowFilter(RowFilter.andFilter(filtros));
    }

    private String estadoToDisplay(String estado) {
        if (estado == null) return "Activo";
        switch (estado) {
            case "activo": return "Activo";
            case "inactivo": return "Inactivo";
            case "bloqueado": return "Bloqueado";
            default: return estado;
        }
    }

    private String displayToEstado(String display) {
        if (display == null) return "activo";
        switch (display) {
            case "Activo": return "activo";
            case "Inactivo": return "inactivo";
            case "Bloqueado": return "bloqueado";
            default: return "activo";
        }
    }

    private String rolToDisplay(String rol) {
        if (rol == null) return "Aprendiz";
        switch (rol) {
            case "administrador": return "Administrador";
            case "bibliotecario": return "Bibliotecario";
            case "almacenista": return "Almacenista";
            case "aprendiz": return "Aprendiz";
            case "instructor": return "Instructor";
            default: return rol;
        }
    }

    private String displayToRol(String display) {
        if (display == null) return "aprendiz";
        switch (display) {
            case "Administrador": return "administrador";
            case "Bibliotecario": return "bibliotecario";
            case "Almacenista": return "almacenista";
            case "Aprendiz": return "aprendiz";
            case "Instructor": return "instructor";
            default: return "aprendiz";
        }
    }

    private void mostrarPopup(Component invoker, int row) {
        int modelRow = tablaUsuarios.convertRowIndexToModel(row);
        if (modelRow < 0) return;
        JPopupMenu popup = new JPopupMenu();
        popup.setBorder(BorderFactory.createLineBorder(BORDER));
        popup.add(crearItemMenuAccion("Ver", modelRow));
        popup.add(crearItemMenuAccion("Editar", modelRow));
        popup.add(crearItemMenuAccion("Eliminar", modelRow));
        popup.show(invoker, 0, invoker.getHeight());
    }

    private JMenuItem crearItemMenuAccion(String texto, int modelRow) {
        JMenuItem item = new JMenuItem(texto);
        item.setFont(new Font("SansSerif", Font.PLAIN, 13));
        item.setForeground("Eliminar".equals(texto) ? RED : TEXT_DARK);
        item.setBorder(new EmptyBorder(7, 12, 7, 12));
        item.addActionListener(evt -> {
            if ("Ver".equals(texto)) mostrarDialogoVer(modelRow);
            else if ("Editar".equals(texto)) mostrarDialogoEditar(modelRow);
            else if ("Eliminar".equals(texto)) eliminarUsuario(modelRow);
        });
        return item;
    }

    private int getIdFromModelRow(int modelRow) {
        // ID is stored in column 6 (hidden), but the column is removed from view.
        // We store it as a client property keyed by row, or read from model directly
        // Since the column is removed from the table view but still in the model:
        try {
            Object val = modeloUsuarios.getValueAt(modelRow, 6);
            return Integer.parseInt(val.toString());
        } catch (Exception e) {
            return -1;
        }
    }

    private void mostrarDialogoVer(int modelRow) {
        try {
            int id = getIdFromModelRow(modelRow);
            if (id < 0) return;
            Usuario u = usuarioDAO.buscarPorId(id);
            if (u != null) mostrarDialogoUsuario(u, false);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al cargar detalles del usuario: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarDialogoEditar(int modelRow) {
        try {
            int id = getIdFromModelRow(modelRow);
            if (id < 0) return;
            Usuario u = usuarioDAO.buscarPorId(id);
            if (u != null) mostrarDialogoUsuario(u, true);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al cargar datos del usuario: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarUsuario(int modelRow) {
        try {
            int id = getIdFromModelRow(modelRow);
            if (id < 0) return;
            String nombre = String.valueOf(modeloUsuarios.getValueAt(modelRow, 1));

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "¿Está seguro de eliminar el usuario \"" + nombre + "\"?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                if (usuarioDAO.eliminar(id)) {
                    refrescarTabla();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Error al eliminar el usuario.",
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

    private void mostrarDialogoUsuario(Usuario usuario, boolean editable) {
        java.awt.Window padre = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(padre, editable ? "Registrar / Editar Usuario" : "Detalles del Usuario",
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

        JTextField txtNombres = new JTextField(usuario.getNombres());
        txtNombres.setPreferredSize(new Dimension(380, 36));

        JTextField txtApellidos = new JTextField(usuario.getApellidos());
        txtApellidos.setPreferredSize(new Dimension(380, 36));

        JTextField txtCorreo = new JTextField(usuario.getCorreo());
        txtCorreo.setPreferredSize(new Dimension(380, 36));

        JComboBox<String> cmbRolUsuario = new JComboBox<>(new String[]{"Administrador", "Bibliotecario", "Almacenista", "Aprendiz", "Instructor"});
        cmbRolUsuario.setSelectedItem(rolToDisplay(usuario.getRol()));
        cmbRolUsuario.setPreferredSize(new Dimension(380, 36));

        JComboBox<String> cmbEstadoUsuario = new JComboBox<>(new String[]{"Activo", "Inactivo", "Bloqueado"});
        cmbEstadoUsuario.setSelectedItem(estadoToDisplay(usuario.getEstado()));
        cmbEstadoUsuario.setPreferredSize(new Dimension(380, 36));

        JPasswordField txtPassword = new JPasswordField();
        txtPassword.setPreferredSize(new Dimension(380, 36));

        java.awt.Component[][] campos = {
            {crearLabel("Nombres", labelFont, labelColor), txtNombres},
            {crearLabel("Apellidos", labelFont, labelColor), txtApellidos},
            {crearLabel("Correo", labelFont, labelColor), txtCorreo},
            {crearLabel("Rol", labelFont, labelColor), cmbRolUsuario},
            {crearLabel("Estado", labelFont, labelColor), cmbEstadoUsuario},
            {crearLabel(usuario.getIdUsuario() > 0 ? "Contraseña (dejar vacío para no cambiar)" : "Contraseña", labelFont, labelColor), txtPassword}
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
            txtNombres.setEditable(false);
            txtApellidos.setEditable(false);
            txtCorreo.setEditable(false);
            cmbRolUsuario.setEnabled(false);
            cmbEstadoUsuario.setEnabled(false);
            txtPassword.setVisible(false);
        }

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botones.setOpaque(false);

        if (editable) {
            JButton btnGuardar = crearBotonPrincipal(usuario.getIdUsuario() > 0 ? "Actualizar" : "Guardar", null);
            btnGuardar.addActionListener(evt -> {
                try {
                    usuario.setNombres(txtNombres.getText().trim());
                    usuario.setApellidos(txtApellidos.getText().trim());
                    usuario.setCorreo(txtCorreo.getText().trim());
                    usuario.setRol(displayToRol(String.valueOf(cmbRolUsuario.getSelectedItem())));
                    usuario.setEstado(displayToEstado(String.valueOf(cmbEstadoUsuario.getSelectedItem())));
                    String pass = new String(txtPassword.getPassword());
                    if (!pass.isEmpty()) {
                        usuario.setPassword(pass);
                    }

                    boolean exito;
                    if (usuario.getIdUsuario() > 0) {
                        exito = usuarioDAO.actualizar(usuario);
                    } else {
                        if (pass.isEmpty()) {
                            JOptionPane.showMessageDialog(dialog,
                                    "La contraseña es obligatoria para nuevos usuarios.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        int id = usuarioDAO.insertar(usuario);
                        exito = id > 0;
                    }

                    if (exito) {
                        refrescarTabla();
                        dialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(dialog,
                                "Error al guardar el usuario.",
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
        combo.setPreferredSize(new Dimension(190, 42));
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

    private void mostrarMensajeAccion(String accion) {
        JOptionPane.showMessageDialog(
                this,
                accion + " todavía no está conectado al flujo real de datos.",
                "Módulo de usuarios",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        setBorder(BorderFactory.createEmptyBorder());
        setPreferredSize(new Dimension(1120, 720));
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
            c.setFont(new Font("SansSerif", Font.PLAIN, 13));
            setForeground(TEXT_DARK);
            setHorizontalAlignment(column == 0 ? SwingConstants.CENTER : SwingConstants.LEFT);
            setBackground(resolverFondoFila(table, row, isSelected));
            return c;
        }
    }

    private static final class AvatarRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {

            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 13));
            panel.setOpaque(true);
            panel.setBackground(resolverFondoFila(table, row, isSelected));
            panel.add(new AvatarBadge(String.valueOf(value)));
            return panel;
        }
    }

    private static final class NombreRenderer implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {

            int modelRow = table.convertRowIndexToModel(row);
            String id = String.valueOf(table.getModel().getValueAt(modelRow, 0));

            JPanel panel = new JPanel();
            panel.setOpaque(true);
            panel.setBackground(resolverFondoFila(table, row, isSelected));
            panel.setBorder(new EmptyBorder(12, 12, 10, 12));
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            JLabel nombre = new JLabel(String.valueOf(value));
            nombre.setFont(new Font("SansSerif", Font.BOLD, 14));
            nombre.setForeground(TEXT_DARK);

            JLabel idLabel = new JLabel("ID: " + id);
            idLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
            idLabel.setForeground(TEXT_SOFT);

            panel.add(nombre);
            panel.add(Box.createVerticalStrut(5));
            panel.add(idLabel);
            return panel;
        }
    }

    private static final class RolRenderer extends DefaultTableCellRenderer {

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

            String rol = String.valueOf(value);
            Color fondo = new Color(224, 231, 255);
            Color texto = BLUE;

            if ("Administrador".equalsIgnoreCase(rol)) {
                fondo = new Color(253, 226, 226);
                texto = RED;
            } else if ("Aprendiz".equalsIgnoreCase(rol)) {
                fondo = new Color(232, 240, 255);
                texto = BLUE;
            }

            panel.add(new BadgeLabel(rol.toUpperCase(), fondo, texto));
            return panel;
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

            if ("Inactivo".equalsIgnoreCase(estado)) {
                fondo = new Color(254, 226, 226);
                texto = new Color(185, 28, 28);
            }

            panel.add(new BadgeLabel(estado.toUpperCase(), fondo, texto));
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
            return crearPanelAcciones(resolverFondoFila(table, row, isSelected));
        }
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
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 14));
            panel.setOpaque(true);
            panel.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);

            JButton editar = crearBotonIconoContorno(new EditIcon(15, SENA_GREEN_DARK), SENA_GREEN, Color.WHITE, new Dimension(58, 42));
            editar.addActionListener(evt -> {
                mostrarDialogoEditar(editingRow);
                fireEditingStopped();
            });

            JButton mas = crearBotonIconoContorno(new DotsIcon(15, TEXT_SOFT), BORDER, Color.WHITE, new Dimension(42, 42));
            mas.addActionListener(evt -> {
                mostrarPopup(mas, editingRow);
                fireEditingStopped();
            });

            panel.add(editar);
            panel.add(mas);
            return panel;
        }
    }

    private static JPanel crearPanelAcciones(Color fondo) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 14));
        panel.setOpaque(true);
        panel.setBackground(fondo);

        JButton ver = crearBotonIconoContorno(new EditIcon(15, SENA_GREEN_DARK), SENA_GREEN, Color.WHITE, new Dimension(58, 42));
        ver.addActionListener(evt -> JOptionPane.showMessageDialog(
                null,
                "Edición de usuario todavía no está conectada a datos reales.",
                "Acción de usuario",
                JOptionPane.INFORMATION_MESSAGE
        ));

        JButton mas = crearBotonIconoContorno(new DotsIcon(15, TEXT_SOFT), BORDER, Color.WHITE, new Dimension(42, 42));
        JPopupMenu menu = crearMenuAcciones();
        mas.addActionListener(evt -> menu.show(mas, 0, mas.getHeight()));

        panel.add(ver);
        panel.add(mas);
        return panel;
    }

    private static JButton crearBotonIconoContorno(Icon icono, Color borde, Color fondo, Dimension dimension) {
        JButton boton = new JButton(icono);
        boton.setPreferredSize(dimension);
        boton.setBackground(fondo);
        boton.setOpaque(true);
        boton.setContentAreaFilled(true);
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borde),
                new EmptyBorder(8, 8, 8, 8)
        ));
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setUI(new BasicButtonUI());
        return boton;
    }

    private static JPopupMenu crearMenuAcciones() {
        JPopupMenu menu = new JPopupMenu();
        menu.setBorder(BorderFactory.createLineBorder(BORDER));
        menu.add(crearItemMenu("Ver"));
        menu.add(crearItemMenu("Editar"));
        menu.add(crearItemMenu("Eliminar"));
        return menu;
    }

    private static JMenuItem crearItemMenu(String texto) {
        JMenuItem item = new JMenuItem(texto);
        item.setFont(new Font("SansSerif", Font.PLAIN, 13));
        item.setForeground("Eliminar".equals(texto) ? RED : TEXT_DARK);
        item.setBorder(new EmptyBorder(7, 12, 7, 12));
        item.addActionListener(evt -> JOptionPane.showMessageDialog(
                null,
                texto + " de usuario todavía no está conectado a datos reales.",
                "Acción de usuario",
                JOptionPane.INFORMATION_MESSAGE
        ));
        return item;
    }

    private static final class PlaceholderTextField extends JTextField {

        private boolean showingPlaceholder = true;

        private PlaceholderTextField(String placeholder) {
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

    private static class BadgeLabel extends JLabel {

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

    private static final class AvatarBadge extends JLabel {

        private AvatarBadge(String text) {
            super(text, SwingConstants.CENTER);
            setOpaque(false);
            setForeground(SENA_GREEN);
            setFont(new Font("SansSerif", Font.BOLD, 12));
            setBorder(new EmptyBorder(7, 12, 7, 12));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(240, 252, 244));
                g2.fillOval(0, 0, getWidth(), getHeight());
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

                g2.setComposite(AlphaComposite.SrcOver.derive(0.10f));
                g2.setColor(Color.BLACK);
                g2.fillRoundRect(4, 6, Math.max(0, w - 8), Math.max(0, h - 8), arc, arc);

                g2.setComposite(AlphaComposite.SrcOver);
                g2.setColor(fill);
                g2.fillRoundRect(0, 0, Math.max(0, w - 1), Math.max(0, h - 1), arc, arc);

                if (borderColor != null) {
                    g2.setColor(borderColor);
                    g2.drawRoundRect(0, 0, Math.max(0, w - 1), Math.max(0, h - 1), arc, arc);
                }
            } finally {
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }

    private static final class FondoInternoPanel extends JPanel {

        private final ImageIcon fondo;

        private FondoInternoPanel() {
            URL url = Usuarios.class.getResource("/imagenes/fondo.jpg");
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

    private static final class UserPlusIcon implements Icon {

        private final int size;
        private final Color color;

        private UserPlusIcon(int size, Color color) {
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
                g2.drawOval(x, y + 1, 5, 5);
                g2.drawArc(x - 1, y + 7, 8, 8, 0, 180);
                g2.fillRoundRect(x + size - 2, y + 4, 3, size - 2, 2, 2);
                g2.fillRoundRect(x + size - 5, y + size / 2 + 1, 9, 3, 2, 2);
            } finally {
                g2.dispose();
            }
        }
    }

    private static final class EditIcon implements Icon {

        private final int size;
        private final Color color;

        private EditIcon(int size, Color color) {
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
                g2.drawLine(x + 2, y + size, x + size - 2, y + 2);
                g2.fillPolygon(
                        new int[]{x + size - 1, x + size + 2, x + size - 2},
                        new int[]{y + 1, y + 4, y + 6},
                        3
                );
                g2.fillRect(x + 1, y + size - 2, 4, 2);
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
