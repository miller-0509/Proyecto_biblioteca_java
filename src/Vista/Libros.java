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
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
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

public class Libros extends JInternalFrame {

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
    private JComboBox<String> cmbGenero;
    private JTable tablaLibros;
    private DefaultTableModel modeloLibros;
    private TableRowSorter<DefaultTableModel> filtroTabla;

    public Libros() {
        initComponents();
        construirVista();
        cargarDatosDemo();
        aplicarFiltros();
    }

    private void construirVista() {
        setTitle("Gestión de Libros");
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

        JLabel titulo = new JLabel("Gestión de Libros");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 26));
        titulo.setForeground(TEXT_DARK);

        JLabel subtitulo = new JLabel("Consulta, registra y administra el inventario bibliográfico del sistema.");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitulo.setForeground(TEXT_SOFT);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(5));
        textos.add(subtitulo);

        JButton btnRegistrar = crearBotonPrincipal("Registrar Libro", new PlusIcon(13, Color.WHITE));
        btnRegistrar.setPreferredSize(new Dimension(172, 42));
        btnRegistrar.addActionListener(evt -> mostrarMensajeAccion("Registrar Libro"));

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
        panel.add(crearTablaLibros(), BorderLayout.CENTER);
        return panel;
    }
    private JPanel crearBarraFiltros() {
        JPanel barra = new JPanel(new GridBagLayout());
        barra.setOpaque(false);

        txtBuscar = new PlaceholderTextField("Nombre, serie o autor...");
        txtBuscar.setPreferredSize(new Dimension(330, 42));
        estilizarCampoTexto(txtBuscar);

        cmbEstado = crearCombo(new String[]{
            "Todos los estados",
            "Disponible",
            "Prestado",
            "Reservado",
            "En mantenimiento"
        });

        cmbGenero = crearCombo(new String[]{
            "Todos los géneros",
            "Novela",
            "Ciencia ficción",
            "Fábula",
            "Historia",
            "Tecnología",
            "Educación",
            "Otros"
        });

        JButton btnBuscar = crearBotonPrincipal("Buscar", new SearchIcon(15, Color.WHITE));
        btnBuscar.setPreferredSize(new Dimension(112, 42));
        btnBuscar.addActionListener(evt -> aplicarFiltros());
        txtBuscar.addActionListener(evt -> aplicarFiltros());
        cmbEstado.addActionListener(evt -> aplicarFiltros());
        cmbGenero.addActionListener(evt -> aplicarFiltros());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.weightx = 1.0;
        barra.add(crearCampoConEtiqueta("Buscar libro", txtBuscar), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.28;
        barra.add(crearCampoConEtiqueta("Estado", cmbEstado), gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.28;
        barra.add(crearCampoConEtiqueta("Género", cmbGenero), gbc);

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

    private JPanel crearTarjetaTabla() {
        RoundedPanel panel = new RoundedPanel(new Color(255, 255, 255, 244), new Color(233, 237, 243), 24);
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(0, 0, 0, 0));
        panel.add(crearTablaLibros(), BorderLayout.CENTER);
        return panel;
    }

    private JScrollPane crearTablaLibros() {
        modeloLibros = new DefaultTableModel(
                new Object[]{"ID", "Título", "Género", "Código Único", "Estado", "Ubicación", "Acciones", "Autor"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };

        tablaLibros = new JTable(modeloLibros);
        filtroTabla = new TableRowSorter<>(modeloLibros);
        tablaLibros.setRowSorter(filtroTabla);

        tablaLibros.setRowHeight(74);
        tablaLibros.setShowVerticalLines(false);
        tablaLibros.setShowHorizontalLines(true);
        tablaLibros.setGridColor(new Color(235, 239, 244));
        tablaLibros.setIntercellSpacing(new Dimension(0, 0));
        tablaLibros.setSelectionBackground(new Color(231, 246, 236));
        tablaLibros.setSelectionForeground(TEXT_DARK);
        tablaLibros.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tablaLibros.setForeground(TEXT_DARK);
        tablaLibros.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tablaLibros.setFillsViewportHeight(true);
        instalarHoverTabla();

        JTableHeader header = tablaLibros.getTableHeader();
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 44));
        header.setFont(new Font("SansSerif", Font.BOLD, 12));
        header.setForeground(TEXT_SOFT);
        header.setBackground(new Color(247, 249, 251));
        header.setBorder(new MatteBorder(0, 0, 1, 0, new Color(194, 239, 207)));
        header.setReorderingAllowed(false);

        tablaLibros.setDefaultRenderer(Object.class, new ZebraRenderer());
        tablaLibros.getColumnModel().getColumn(0).setCellRenderer(new IdRenderer());
        tablaLibros.getColumnModel().getColumn(1).setCellRenderer(new TituloRenderer());
        tablaLibros.getColumnModel().getColumn(2).setCellRenderer(new GeneroRenderer());
        tablaLibros.getColumnModel().getColumn(3).setCellRenderer(new CodigoRenderer());
        tablaLibros.getColumnModel().getColumn(4).setCellRenderer(new EstadoRenderer());
        tablaLibros.getColumnModel().getColumn(6).setCellRenderer(new AccionesRenderer());
        tablaLibros.getColumnModel().getColumn(6).setCellEditor(new AccionesEditor());
        tablaLibros.removeColumn(tablaLibros.getColumnModel().getColumn(7));
        configurarAnchosTabla();

        JScrollPane scroll = new JScrollPane(tablaLibros);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBackground(Color.WHITE);
        return scroll;
    }

    private void configurarAnchosTabla() {
        TableColumnModel columnas = tablaLibros.getColumnModel();
        columnas.getColumn(0).setMinWidth(58);
        columnas.getColumn(0).setPreferredWidth(70);
        columnas.getColumn(1).setPreferredWidth(230);
        columnas.getColumn(2).setPreferredWidth(220);
        columnas.getColumn(3).setPreferredWidth(170);
        columnas.getColumn(4).setPreferredWidth(145);
        columnas.getColumn(5).setPreferredWidth(145);
        columnas.getColumn(6).setMinWidth(128);
        columnas.getColumn(6).setPreferredWidth(142);
    }

    private void instalarHoverTabla() {
        tablaLibros.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = tablaLibros.rowAtPoint(e.getPoint());
                Object actual = tablaLibros.getClientProperty("hoverRow");
                if (!(actual instanceof Integer) || ((Integer) actual) != row) {
                    tablaLibros.putClientProperty("hoverRow", row);
                    tablaLibros.repaint();
                }
            }
        });

        tablaLibros.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                tablaLibros.putClientProperty("hoverRow", -1);
                tablaLibros.repaint();
            }
        });
    }

    private void cargarDatosDemo() {
        modeloLibros.addRow(new Object[]{"#3", "Neuromancer", "CIENCIA FICCIÓN / CYBERPUNK", "LIB-CYB-78421-XA", "Disponible", "Biblioteca", "", "William Gibson"});
        modeloLibros.addRow(new Object[]{"#2", "El principito", "FÁBULA", "ZQW5428", "Disponible", "Ambiente 208", "", "Antoine de Saint-Exupéry"});
        modeloLibros.addRow(new Object[]{"#4", "El coronel no tiene quien le escriba", "NOVELA / REALISMO MÁGICO", "XOEW83XL01", "Disponible", "Biblioteca", "", "Gabriel García Márquez"});
        modeloLibros.addRow(new Object[]{"#1", "Cien años de soledad", "NOVELA / REALISMO MÁGICO", "CVB34567", "Prestado", "Biblioteca", "", "Gabriel García Márquez"});
        modeloLibros.addRow(new Object[]{"#5", "Clean Code", "TECNOLOGÍA", "LIB-TEC-90218-CC", "Reservado", "Sala de lectura", "", "Robert C. Martin"});
        modeloLibros.addRow(new Object[]{"#6", "Historia mínima de Colombia", "HISTORIA", "LIB-HIS-11028-CO", "En mantenimiento", "Restauración", "", "Jorge Orlando Melo"});
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

        String genero = cmbGenero == null ? "Todos los géneros" : String.valueOf(cmbGenero.getSelectedItem());
        if (!"Todos los géneros".equals(genero)) {
            filtros.add(RowFilter.regexFilter("(?i)" + Pattern.quote(genero), 2));
        }

        if (filtros.isEmpty()) {
            filtroTabla.setRowFilter(null);
            return;
        }

        filtroTabla.setRowFilter(RowFilter.andFilter(filtros));
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

    private void mostrarMensajeAccion(String accion) {
        JOptionPane.showMessageDialog(
                this,
                accion + " todavía no está conectado al flujo real de datos.",
                "Módulo de libros",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        setBorder(BorderFactory.createEmptyBorder());
        setPreferredSize(new Dimension(1120, 720));
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

    private static final class IdRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {

            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 23));
            panel.setOpaque(true);
            panel.setBackground(resolverFondoFila(table, row, isSelected));
            panel.add(new BadgeLabel(String.valueOf(value), new Color(248, 250, 252), SENA_GREEN_DARK));
            return panel;
        }
    }

    private static final class TituloRenderer implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {

            int modelRow = table.convertRowIndexToModel(row);
            String autor = String.valueOf(table.getModel().getValueAt(modelRow, 7));

            JPanel panel = new JPanel();
            panel.setOpaque(true);
            panel.setBackground(resolverFondoFila(table, row, isSelected));
            panel.setBorder(new EmptyBorder(12, 12, 10, 12));
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            JLabel titulo = new JLabel(String.valueOf(value));
            titulo.setFont(new Font("SansSerif", Font.BOLD, 14));
            titulo.setForeground(TEXT_DARK);

            JLabel autorLabel = new JLabel(autor);
            autorLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
            autorLabel.setForeground(TEXT_SOFT);

            panel.add(titulo);
            panel.add(Box.createVerticalStrut(5));
            panel.add(autorLabel);
            return panel;
        }
    }

    private static final class GeneroRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {

            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 23));
            panel.setOpaque(true);
            panel.setBackground(resolverFondoFila(table, row, isSelected));
            panel.add(new BadgeLabel(String.valueOf(value), new Color(240, 253, 244), SENA_GREEN));
            return panel;
        }
    }

    private static final class CodigoRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {

            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 24));
            panel.setOpaque(true);
            panel.setBackground(resolverFondoFila(table, row, isSelected));
            panel.add(new CodeLabel(String.valueOf(value)));
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

            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 23));
            panel.setOpaque(true);
            panel.setBackground(resolverFondoFila(table, row, isSelected));

            String estado = String.valueOf(value);
            Color fondo = new Color(220, 252, 231);
            Color texto = new Color(21, 128, 61);

            if ("Prestado".equalsIgnoreCase(estado) || "Reservado".equalsIgnoreCase(estado)) {
                fondo = new Color(254, 243, 199);
                texto = new Color(146, 64, 14);
            } else if ("No disponible".equalsIgnoreCase(estado) || "En mantenimiento".equalsIgnoreCase(estado)) {
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

    private static final class AccionesEditor extends AbstractCellEditor implements TableCellEditor {

        private final JPanel panel = crearPanelAcciones(Color.WHITE);

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
            panel.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            return panel;
        }
    }

    private static JPanel crearPanelAcciones(Color fondo) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 14));
        panel.setOpaque(true);
        panel.setBackground(fondo);

        JButton ver = crearBotonIconoContorno(new EyeIcon(15, SENA_GREEN_DARK), SENA_GREEN, Color.WHITE, new Dimension(58, 42));
        ver.addActionListener(evt -> JOptionPane.showMessageDialog(
                null,
                "Vista previa del libro todavía no está conectada a datos reales.",
                "Acción de libro",
                JOptionPane.INFORMATION_MESSAGE
        ));

        JButton mas = crearBotonIconoContorno(new DotsIcon(15, TEXT_SOFT), BORDER, Color.WHITE, new Dimension(42, 42));
        JPopupMenu menu = crearMenuAcciones();
        mas.addActionListener(evt -> menu.show(mas, 0, mas.getHeight()));

        panel.add(ver);
        panel.add(mas);
        return panel;
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
        item.setForeground("Eliminar".equals(texto) ? new Color(185, 28, 28) : TEXT_DARK);
        item.setBorder(new EmptyBorder(7, 12, 7, 12));
        item.addActionListener(evt -> JOptionPane.showMessageDialog(
                null,
                texto + " del libro todavía no está conectado a datos reales.",
                "Acción de libro",
                JOptionPane.INFORMATION_MESSAGE
        ));
        return item;
    }

    private static JButton crearBotonAccion(String texto, Icon icono) {
        JButton boton = new JButton(texto, icono);
        boton.setFont(new Font("SansSerif", Font.BOLD, 12));
        boton.setForeground(SENA_GREEN_DARK);
        boton.setBackground(Color.WHITE);
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(SENA_GREEN),
                new EmptyBorder(9, 13, 9, 13)
        ));
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setUI(new BasicButtonUI());
        return boton;
    }

    private static JButton crearBotonIcono(Icon icono) {
        JButton boton = new JButton(icono);
        boton.setPreferredSize(new Dimension(36, 36));
        boton.setBackground(Color.WHITE);
        boton.setBorder(BorderFactory.createLineBorder(BORDER));
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setUI(new BasicButtonUI());
        return boton;
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

    private static final class CodeLabel extends BadgeLabel {

        private CodeLabel(String text) {
            super(text, new Color(248, 250, 252), SENA_GREEN_DARK);
            setFont(new Font("Monospaced", Font.PLAIN, 12));
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
            URL url = Libros.class.getResource("/imagenes/fondo.jpg");
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

    private static final class BookIcon implements Icon {

        private final int size;
        private final Color color;

        private BookIcon(int size, Color color) {
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
                g2.setStroke(new java.awt.BasicStroke(1.8f));

                int w = size;
                int h = size;
                int left = x + 2;
                int top = y + 2;
                int right = x + w - 2;
                int bottom = y + h - 3;
                int mid = x + w / 2;

                g2.drawRoundRect(left, top, w - 4, h - 5, 4, 4);
                g2.drawLine(mid, top + 1, mid, bottom - 1);
                g2.drawLine(left + 3, top + 5, mid - 1, top + 4);
                g2.drawLine(mid + 1, top + 4, right - 3, top + 5);
                g2.drawLine(left + 3, bottom - 4, mid - 1, bottom - 3);
                g2.drawLine(mid + 1, bottom - 3, right - 3, bottom - 4);
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

