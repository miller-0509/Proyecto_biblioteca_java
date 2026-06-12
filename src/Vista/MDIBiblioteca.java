package Vista;

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
import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.OverlayLayout;
import java.awt.GridLayout;
import java.beans.PropertyVetoException;

public class MDIBiblioteca extends javax.swing.JFrame {

    private static final Color SENA_GREEN = new Color(46, 170, 84);
    private static final Color SENA_GREEN_DARK = new Color(25, 120, 60);
    private static final Color SENA_GREEN_LIGHT = new Color(240, 252, 244);
    private static final Color SENA_ORANGE = new Color(242, 170, 55);
    private static final Color SENA_RED = new Color(217, 70, 70);
    private static final Color SENA_BLUE = new Color(54, 119, 229);
    private static final Color TEXT_DARK = new Color(28, 34, 45);
    private static final Color TEXT_SOFT = new Color(96, 105, 121);
    private static final Color WHITE_ALPHA = new Color(255, 255, 255, 232);

    public MDIBiblioteca() {
        initComponents();
        configurarAparienciaBase();
        construirDashboardVisual();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
    }

    private void configurarAparienciaBase() {
        setTitle("Proyecto Biblioteca - SENA");

        menuBar.setOpaque(true);
        menuBar.setBackground(new Color(255, 255, 255, 245));
        menuBar.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, new Color(223, 228, 234)),
                new EmptyBorder(8, 14, 8, 14)
        ));

        aplicarEstiloMenuItem(fileMenu);
        aplicarEstiloMenuItem(jMenu1);
        aplicarEstiloMenuItem(jMenu2);
        aplicarEstiloMenuItem(jMenu3);
        aplicarEstiloMenuItem(jMenu4);
        aplicarEstiloMenuItem(jMenu5);

        escritorio.setOpaque(false);
        escritorio.setBorder(BorderFactory.createEmptyBorder());
    }

    private void aplicarEstiloMenuItem(JMenuItem item) {
        item.setFont(new Font("SansSerif", Font.PLAIN, 13));
        item.setForeground(TEXT_DARK);
        item.setBorder(new EmptyBorder(7, 12, 7, 12));
        item.setOpaque(true);
        item.setBackground(Color.WHITE);
        item.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        item.addActionListener(evt -> manejarAccionModulo(item.getText()));
    }

    private void construirDashboardVisual() {
        URL imgURL = getClass().getResource("/imagenes/fondo.jpg");
        ImageIcon fondoIcon = imgURL != null ? new ImageIcon(imgURL) : null;

        escritorio.removeAll();
        escritorio.setLayout(new OverlayLayout(escritorio));

        FondoPanel panelFondo = new FondoPanel(fondoIcon);
        panelFondo.setAlignmentX(0.5f);
        panelFondo.setAlignmentY(0.5f);

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setOpaque(false);
        contenedor.setAlignmentX(0.5f);
        contenedor.setAlignmentY(0.5f);
        contenedor.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        contenedor.setBorder(new EmptyBorder(18, 18, 18, 18));

        RoundedPanel shell = new RoundedPanel(new Color(255, 255, 255, 214), new Color(255, 255, 255, 110), 28);
        shell.setLayout(new BorderLayout(0, 16));
        shell.setBorder(new EmptyBorder(18, 18, 18, 18));

        shell.add(crearBarraSuperior(), BorderLayout.NORTH);
        shell.add(crearContenidoPrincipal(), BorderLayout.CENTER);

        contenedor.add(shell, BorderLayout.CENTER);

        escritorio.add(panelFondo);
        escritorio.add(contenedor);

        escritorio.revalidate();
        escritorio.repaint();
    }

    private JPanel crearBarraSuperior() {
        JPanel barra = new JPanel(new BorderLayout(16, 12));
        barra.setOpaque(false);
        barra.setBorder(new EmptyBorder(0, 4, 10, 4));

        barra.add(crearBloqueMarca(), BorderLayout.WEST);
        barra.add(crearPerfilUsuario(), BorderLayout.EAST);
        return barra;
    }

    private JPanel crearBloqueMarca() {
        JPanel marca = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        marca.setOpaque(false);

        JPanel insignia = new RoundedPanel(new Color(255, 245, 236), new Color(255, 220, 185), 18);
        insignia.setPreferredSize(new Dimension(52, 52));
        insignia.setLayout(new BorderLayout());
        JLabel textoInsignia = new JLabel("SENA", SwingConstants.CENTER);
        textoInsignia.setFont(new Font("SansSerif", Font.BOLD, 14));
        textoInsignia.setForeground(new Color(225, 101, 20));
        insignia.add(textoInsignia, BorderLayout.CENTER);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Control de elementos de almacén y préstamo");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        titulo.setForeground(TEXT_DARK);

        JLabel subtitulo = new JLabel("de equipos de biblioteca");
        subtitulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        subtitulo.setForeground(TEXT_DARK);

        textos.add(titulo);
        textos.add(subtitulo);

        marca.add(insignia);
        marca.add(textos);
        return marca;
    }

    private JPanel crearPerfilUsuario() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        panel.setOpaque(false);

        JPanel avatar = new RoundedPanel(SENA_GREEN, SENA_GREEN, 24);
        avatar.setPreferredSize(new Dimension(38, 38));
        avatar.setLayout(new BorderLayout());
        JLabel iniciales = new JLabel("SA", SwingConstants.CENTER);
        iniciales.setFont(new Font("SansSerif", Font.BOLD, 13));
        iniciales.setForeground(Color.WHITE);
        avatar.add(iniciales, BorderLayout.CENTER);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel rol = new JLabel("Super Administrador");
        rol.setFont(new Font("SansSerif", Font.BOLD, 13));
        rol.setForeground(TEXT_DARK);

        JLabel estado = new JLabel("Panel principal");
        estado.setFont(new Font("SansSerif", Font.PLAIN, 11));
        estado.setForeground(TEXT_SOFT);

        textos.add(rol);
        textos.add(estado);

        panel.add(avatar);
        panel.add(textos);
        return panel;
    }

    private JScrollPane crearContenidoPrincipal() {
        JPanel contenedor = new JPanel(new GridBagLayout());
        contenedor.setOpaque(false);
        contenedor.setBorder(new EmptyBorder(4, 4, 4, 4));

        GridBagConstraintsHelper gbc = new GridBagConstraintsHelper();

        gbc.y(0).weight(1, 0).insets(0, 0, 18, 0).fillHorizontal();
        contenedor.add(crearBannerBienvenida(), gbc.build());

        gbc.y(1).weight(1, 1).insets(0, 0, 0, 0).fillBoth();
        contenedor.add(crearGrillaTarjetas(), gbc.build());

        JScrollPane scroll = new JScrollPane(contenedor);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getHorizontalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JPanel crearBannerBienvenida() {
        RoundedPanel banner = new RoundedPanel(SENA_GREEN_LIGHT, new Color(193, 234, 203), 24);
        banner.setLayout(new BorderLayout(18, 10));
        banner.setBorder(new EmptyBorder(18, 20, 18, 20));

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel saludo = new JLabel("¡Hola, Super Administrador!");
        saludo.setFont(new Font("SansSerif", Font.BOLD, 23));
        saludo.setForeground(TEXT_DARK);

        JLabel descripcion = new JLabel(
                "<html>Bienvenido a <b>Control de elementos de almacén y préstamo de equipos de biblioteca</b> "
                + "- Tu plataforma de gestión de equipos y préstamos.</html>"
        );
        descripcion.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descripcion.setForeground(TEXT_SOFT);

        textos.add(saludo);
        textos.add(Box.createVerticalStrut(6));
        textos.add(descripcion);

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        derecha.setOpaque(false);
        JLabel chip = new JLabel("ADMINISTRADOR", SwingConstants.CENTER);
        chip.setOpaque(true);
        chip.setBackground(SENA_GREEN);
        chip.setForeground(Color.WHITE);
        chip.setFont(new Font("SansSerif", Font.BOLD, 12));
        chip.setBorder(new EmptyBorder(10, 16, 10, 16));
        derecha.add(chip);

        banner.add(textos, BorderLayout.CENTER);
        banner.add(derecha, BorderLayout.EAST);
        return banner;
    }

    private JPanel crearGrillaTarjetas() {
        JPanel raiz = new JPanel();
        raiz.setOpaque(false);
        raiz.setLayout(new BoxLayout(raiz, BoxLayout.Y_AXIS));

        JPanel filaSuperior = new JPanel(new GridLayout(1, 3, 16, 16));
        filaSuperior.setOpaque(false);
        filaSuperior.add(crearTarjetaModulo(
                "EQ",
                SENA_GREEN,
                "Catálogo de Equipos",
                "Administra el inventario completo de equipos del sistema.",
                "ADMINISTRAR",
                SENA_GREEN,
                Color.WHITE
        ));
        filaSuperior.add(crearTarjetaModulo(
                "LB",
                new Color(57, 181, 120),
                "Catálogo de Libros",
                "Administra el inventario completo de libros de la biblioteca.",
                "ADMINISTRAR",
                SENA_GREEN,
                Color.WHITE
        ));
        filaSuperior.add(crearTarjetaModulo(
                "PR",
                SENA_ORANGE,
                "Préstamos",
                "Revisa, aprueba o rechaza las solicitudes de préstamo de usuarios.",
                "ADMINISTRAR",
                new Color(248, 226, 178),
                new Color(145, 98, 10)
        ));

        JPanel filaInferior = new JPanel(new GridLayout(1, 3, 16, 16));
        filaInferior.setOpaque(false);
        filaInferior.add(crearTarjetaModulo(
                "SM",
                SENA_RED,
                "Sanciones y Multas",
                "Gestiona y condona las suspensiones aplicadas a los usuarios por retrasos.",
                "ADMINISTRAR",
                new Color(252, 225, 225),
                new Color(165, 44, 44)
        ));
        filaInferior.add(crearTarjetaModulo(
                "PDF",
                SENA_BLUE,
                "Manual de Usuario",
                "Descarga la guía detallada con los procesos, permisos y límites correspondientes a tu perfil.",
                "DESCARGAR PDF",
                SENA_GREEN,
                Color.WHITE
        ));
        filaInferior.add(crearPanelVacio());

        raiz.add(filaSuperior);
        raiz.add(Box.createVerticalStrut(16));
        raiz.add(filaInferior);
        return raiz;
    }

    private JPanel crearPanelVacio() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        return panel;
    }

    private JPanel crearTarjetaModulo(
            String etiqueta,
            Color acento,
            String titulo,
            String descripcion,
            String accion,
            Color colorBoton,
            Color textoBoton) {

        RoundedPanel tarjeta = new RoundedPanel(Color.WHITE, new Color(230, 233, 239), 24);
        tarjeta.setLayout(new BorderLayout(0, 16));
        tarjeta.setBorder(new EmptyBorder(18, 18, 18, 18));
        tarjeta.setPreferredSize(new Dimension(240, 200));

        JPanel icono = new RoundedPanel(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 24), new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 55), 18);
        icono.setPreferredSize(new Dimension(54, 54));
        icono.setLayout(new BorderLayout());

        JLabel letra = new JLabel(etiqueta, SwingConstants.CENTER);
        letra.setFont(new Font("SansSerif", Font.BOLD, 15));
        letra.setForeground(acento.darker());
        icono.add(letra, BorderLayout.CENTER);

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));

        JLabel tituloLabel = new JLabel(titulo);
        tituloLabel.setFont(new Font("SansSerif", Font.BOLD, 17));
        tituloLabel.setForeground(TEXT_DARK);

        JLabel descripcionLabel = new JLabel("<html><div style='width: 200px;'>" + descripcion + "</div></html>");
        descripcionLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        descripcionLabel.setForeground(TEXT_SOFT);

        centro.add(tituloLabel);
        centro.add(Box.createVerticalStrut(8));
        centro.add(descripcionLabel);
        centro.add(Box.createVerticalGlue());

        tarjeta.add(icono, BorderLayout.NORTH);
        tarjeta.add(centro, BorderLayout.CENTER);
        tarjeta.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                manejarAccionModulo(titulo);
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                tarjeta.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                tarjeta.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            }
        });
        return tarjeta;
    }

    private void manejarAccionModulo(String modulo) {
        if ("Inicio".equals(modulo)) {
            return;
        }

        if ("Libros".equals(modulo) || modulo.contains("Libros")) {
            abrirModuloLibros();
            return;
        }

        if ("Equipos".equals(modulo) || "Catálogo de Equipos".equals(modulo)) {
            abrirModuloEquipos();
            return;
        }

        JOptionPane.showMessageDialog(
                this,
                "El módulo \"" + modulo + "\" todavía no tiene ventana interna creada en este proyecto.",
                "Módulo en desarrollo",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void abrirModuloEquipos() {
        for (JInternalFrame frame : escritorio.getAllFrames()) {
            if (frame instanceof Equipos) {
                try {
                    frame.setIcon(false);
                    frame.setSelected(true);
                } catch (PropertyVetoException ex) {
                    // Si el sistema de ventanas lo bloquea, igual lo traemos al frente.
                }
                frame.toFront();
                return;
            }
        }

        Equipos equipos = new Equipos();
        equipos.setVisible(true);
        equipos.setClosable(true);
        equipos.setIconifiable(true);
        equipos.setMaximizable(true);
        equipos.setResizable(true);
        equipos.setSize(1080, 700);
        equipos.setLocation(30, 30);
        escritorio.add(equipos);
        try {
            equipos.setSelected(true);
        } catch (PropertyVetoException ex) {
            // No bloqueamos la apertura por un veto visual.
        }
        equipos.toFront();
    }

    private void abrirModuloLibros() {
        for (JInternalFrame frame : escritorio.getAllFrames()) {
            if (frame instanceof Libros) {
                try {
                    frame.setIcon(false);
                    frame.setSelected(true);
                } catch (PropertyVetoException ex) {
                    // Si el sistema de ventanas lo bloquea, igual lo traemos al frente.
                }
                frame.toFront();
                return;
            }
        }

        Libros libros = new Libros();
        libros.setVisible(true);
        libros.setClosable(true);
        libros.setIconifiable(true);
        libros.setMaximizable(true);
        libros.setResizable(true);
        libros.setSize(1120, 720);
        libros.setLocation(40, 40);
        escritorio.add(libros);
        try {
            libros.setSelected(true);
        } catch (PropertyVetoException ex) {
            // No bloqueamos la apertura por un veto visual.
        }
        libros.toFront();
    }

    private void mostrarModuloEnDesarrollo(String modulo) {
        JOptionPane.showMessageDialog(
                this,
                "El módulo \"" + modulo + "\" todavía no tiene ventana interna creada en este proyecto.",
                "Módulo en desarrollo",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(MDIBiblioteca.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MDIBiblioteca().setVisible(true);
            }
        });
    }

    private void initComponents() {

        escritorio = new javax.swing.JDesktopPane();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        fileMenu.setText("Equipos");
        menuBar.add(fileMenu);

        jMenu1.setText("Libros");
        menuBar.add(jMenu1);
        jMenu2.setText("Préstamos");
        menuBar.add(jMenu2);
        jMenu3.setText("Reportes");
        menuBar.add(jMenu3);
        jMenu4.setText("Sanciones");
        menuBar.add(jMenu4);
        jMenu5.setText("Usuarios");
        menuBar.add(jMenu5);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(escritorio, javax.swing.GroupLayout.DEFAULT_SIZE, 827, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(escritorio, javax.swing.GroupLayout.DEFAULT_SIZE, 441, Short.MAX_VALUE)
        );

        pack();
    }

    private javax.swing.JDesktopPane escritorio;
    private javax.swing.JMenuItem fileMenu;
    private javax.swing.JMenuItem jMenu1;
    private javax.swing.JMenuItem jMenu2;
    private javax.swing.JMenuItem jMenu3;
    private javax.swing.JMenuItem jMenu4;
    private javax.swing.JMenuItem jMenu5;
    private javax.swing.JMenuBar menuBar;

    private static final class FondoPanel extends JPanel {

        private final ImageIcon fondo;

        private FondoPanel(ImageIcon fondo) {
            this.fondo = fondo;
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
                    g2.setPaint(new GradientPaint(0, 0, new Color(242, 245, 248), 0, getHeight(), new Color(223, 230, 236)));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }

                g2.setComposite(AlphaComposite.SrcOver.derive(0.80f));
                g2.setPaint(new GradientPaint(0, 0, new Color(250, 252, 253, 205), 0, getHeight(), new Color(245, 248, 250, 185)));
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setComposite(AlphaComposite.SrcOver.derive(0.20f));
                g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 0), getWidth(), getHeight(), new Color(255, 255, 255, 100)));
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
        public Dimension getMaximumSize() {
            return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
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

    private static final class GridBagConstraintsHelper {

        private final java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();

        private GridBagConstraintsHelper() {
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gbc.anchor = java.awt.GridBagConstraints.NORTH;
            gbc.weightx = 1.0;
            gbc.weighty = 0.0;
        }

        private GridBagConstraintsHelper y(int value) {
            gbc.gridy = value;
            return this;
        }

        private GridBagConstraintsHelper weight(double x, double y) {
            gbc.weightx = x;
            gbc.weighty = y;
            return this;
        }

        private GridBagConstraintsHelper insets(int top, int left, int bottom, int right) {
            gbc.insets = new Insets(top, left, bottom, right);
            return this;
        }

        private GridBagConstraintsHelper fillHorizontal() {
            gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
            return this;
        }

        private GridBagConstraintsHelper fillBoth() {
            gbc.fill = java.awt.GridBagConstraints.BOTH;
            return this;
        }

        private java.awt.GridBagConstraints build() {
            return gbc;
        }
    }
}
