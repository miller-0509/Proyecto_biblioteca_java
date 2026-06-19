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
import java.awt.Image;
import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
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
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

    private java.util.List<JInternalFrame> navHistory = new java.util.ArrayList<>();
    private int navIndex = -1;
    private boolean navInternalUpdate = false;
    private JButton btnNavBack;
    private JButton btnNavForward;

    public MDIBiblioteca() {
        initComponents();
        configurarAparienciaBase();
        construirDashboardVisual();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        initNavigation();
    }

    private void initNavigation() {
        escritorio.addContainerListener(new java.awt.event.ContainerAdapter() {
            @Override
            public void componentAdded(java.awt.event.ContainerEvent e) {
                if (e.getChild() instanceof JInternalFrame) {
                    JInternalFrame frame = (JInternalFrame) e.getChild();
                    frame.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
                        @Override
                        public void internalFrameActivated(javax.swing.event.InternalFrameEvent ev) {
                            if (!navInternalUpdate) {
                                navHistory.remove(frame);
                                navHistory.add(frame);
                                navIndex = navHistory.size() - 1;
                                actualizarBotonesNavegacion();
                            }
                        }
                        @Override
                        public void internalFrameClosed(javax.swing.event.InternalFrameEvent ev) {
                            navHistory.remove(frame);
                            if (navIndex >= navHistory.size()) navIndex = navHistory.size() - 1;
                            actualizarBotonesNavegacion();
                        }
                    });
                    navHistory.remove(frame);
                    navHistory.add(frame);
                    navIndex = navHistory.size() - 1;
                    actualizarBotonesNavegacion();
                }
            }
        });
    }

    private void navegarAtras() {
        if (navIndex > 0) {
            navInternalUpdate = true;
            navIndex--;
            JInternalFrame frame = navHistory.get(navIndex);
            try { frame.setIcon(false); frame.setSelected(true); frame.toFront(); } catch (Exception ex) {}
            navInternalUpdate = false;
            actualizarBotonesNavegacion();
        }
    }

    private void navegarAdelante() {
        if (navIndex < navHistory.size() - 1) {
            navInternalUpdate = true;
            navIndex++;
            JInternalFrame frame = navHistory.get(navIndex);
            try { frame.setIcon(false); frame.setSelected(true); frame.toFront(); } catch (Exception ex) {}
            navInternalUpdate = false;
            actualizarBotonesNavegacion();
        }
    }

    private void actualizarBotonesNavegacion() {
        if (btnNavBack != null) btnNavBack.setEnabled(navIndex > 0);
        if (btnNavForward != null) btnNavForward.setEnabled(navIndex < navHistory.size() - 1);
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
        aplicarEstiloMenuItem(jMenu3);
        aplicarEstiloMenuItem(jMenu4);
        aplicarEstiloMenuItem(jMenu5);

        jMenu2.setFont(new Font("SansSerif", Font.PLAIN, 13));
        jMenu2.setForeground(TEXT_DARK);
        jMenu2.setBorder(new EmptyBorder(7, 12, 7, 12));
        jMenu2.setOpaque(true);
        jMenu2.setBackground(Color.WHITE);
        jMenu2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jMenu2.setIcon(null);

        estiloMenuItemSimple(mnuVerEquiposPrestados);
        estiloMenuItemSimple(mnuVerLibrosPrestados);
        estiloMenuItemSimple(mnuSolicitarEquipo);
        estiloMenuItemSimple(mnuSolicitarLibro);

        mnuSolicitarEquipo.setIcon(new javax.swing.Icon() {
            private final Color c = SENA_GREEN;
            @Override public void paintIcon(java.awt.Component comp, java.awt.Graphics g, int x, int y) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(c);
                g2.fillOval(x, y, 16, 16);
                g2.setColor(Color.WHITE);
                g2.setStroke(new java.awt.BasicStroke(2));
                g2.drawLine(x + 4, y + 8, x + 12, y + 8);
                g2.drawLine(x + 8, y + 4, x + 8, y + 12);
                g2.dispose();
            }
            @Override public int getIconWidth() { return 16; }
            @Override public int getIconHeight() { return 16; }
        });
        mnuSolicitarLibro.setIcon(new javax.swing.Icon() {
            private final Color c = new Color(57, 181, 120);
            @Override public void paintIcon(java.awt.Component comp, java.awt.Graphics g, int x, int y) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(c);
                g2.fillOval(x, y, 16, 16);
                g2.setColor(Color.WHITE);
                g2.setStroke(new java.awt.BasicStroke(2));
                g2.drawLine(x + 4, y + 8, x + 12, y + 8);
                g2.drawLine(x + 8, y + 4, x + 8, y + 12);
                g2.dispose();
            }
            @Override public int getIconWidth() { return 16; }
            @Override public int getIconHeight() { return 16; }
        });

        mnuVerEquiposPrestados.setIcon(new javax.swing.Icon() {
            @Override public void paintIcon(java.awt.Component comp, java.awt.Graphics g, int x, int y) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(TEXT_SOFT);
                g2.setStroke(new java.awt.BasicStroke(1.5f));
                int w = 14, h = 10;
                g2.drawRoundRect(x + 1, y + 3, w, h, 2, 2);
                g2.drawLine(x + 3, y + 1, x + 12, y + 1);
                g2.dispose();
            }
            @Override public int getIconWidth() { return 16; }
            @Override public int getIconHeight() { return 16; }
        });
        mnuVerLibrosPrestados.setIcon(new javax.swing.Icon() {
            @Override public void paintIcon(java.awt.Component comp, java.awt.Graphics g, int x, int y) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(TEXT_SOFT);
                g2.setStroke(new java.awt.BasicStroke(1.5f));
                g2.drawRoundRect(x + 2, y + 2, 12, 12, 2, 2);
                g2.drawLine(x + 5, y + 5, x + 11, y + 5);
                g2.drawLine(x + 5, y + 8, x + 11, y + 8);
                g2.dispose();
            }
            @Override public int getIconWidth() { return 16; }
            @Override public int getIconHeight() { return 16; }
        });

        agregarFlechasNavegacion();

        escritorio.setOpaque(false);
        escritorio.setBorder(BorderFactory.createEmptyBorder());
    }

    private void agregarFlechasNavegacion() {
        Color navBg = new Color(240, 243, 247);
        Color navBorder = new Color(220, 225, 232);
        Color navIcon = new Color(96, 105, 121);
        Color navDisabled = new Color(180, 185, 195);

        btnNavBack = new JButton("<") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isEnabled() ? navBg : new Color(248, 249, 251));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(isEnabled() ? navBorder : new Color(235, 237, 240));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.setColor(isEnabled() ? navIcon : navDisabled);
                g2.setFont(new Font("SansSerif", Font.BOLD, 14));
                java.awt.FontMetrics fm = g2.getFontMetrics();
                String txt = "<";
                g2.drawString(txt, (getWidth() - fm.stringWidth(txt)) / 2, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btnNavBack.setPreferredSize(new Dimension(30, 30));
        btnNavBack.setMinimumSize(new Dimension(30, 30));
        btnNavBack.setMaximumSize(new Dimension(30, 30));
        btnNavBack.setContentAreaFilled(false);
        btnNavBack.setBorderPainted(false);
        btnNavBack.setFocusPainted(false);
        btnNavBack.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNavBack.setEnabled(false);
        btnNavBack.addActionListener(e -> navegarAtras());

        btnNavForward = new JButton(">") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isEnabled() ? navBg : new Color(248, 249, 251));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(isEnabled() ? navBorder : new Color(235, 237, 240));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.setColor(isEnabled() ? navIcon : navDisabled);
                g2.setFont(new Font("SansSerif", Font.BOLD, 14));
                java.awt.FontMetrics fm = g2.getFontMetrics();
                String txt = ">";
                g2.drawString(txt, (getWidth() - fm.stringWidth(txt)) / 2, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btnNavForward.setPreferredSize(new Dimension(30, 30));
        btnNavForward.setMinimumSize(new Dimension(30, 30));
        btnNavForward.setMaximumSize(new Dimension(30, 30));
        btnNavForward.setContentAreaFilled(false);
        btnNavForward.setBorderPainted(false);
        btnNavForward.setFocusPainted(false);
        btnNavForward.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNavForward.setEnabled(false);
        btnNavForward.addActionListener(e -> navegarAdelante());

        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(btnNavBack);
        menuBar.add(Box.createHorizontalStrut(2));
        menuBar.add(btnNavForward);
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

    private void estiloMenuItemSimple(JMenuItem item) {
        item.setFont(new Font("SansSerif", Font.PLAIN, 13));
        item.setForeground(TEXT_DARK);
        item.setBorder(new EmptyBorder(7, 12, 7, 12));
        item.setOpaque(true);
        item.setBackground(Color.WHITE);
        item.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }

    private void construirDashboardVisual() {
        URL imgURL = getClass().getResource("/imagenes/fondo.jpg");
        ImageIcon fondoIcon = imgURL != null ? new ImageIcon(imgURL) : null;

        escritorio.removeAll();
        escritorio.setLayout(null);

        JPanel dashboardPanel = new JPanel(new BorderLayout()) {
            private final ImageIcon bg = fondoIcon;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                if (bg != null && bg.getImage() != null) {
                    int iw = bg.getIconWidth(), ih = bg.getIconHeight();
                    double scale = Math.max((double) w / iw, (double) h / ih);
                    int drawW = (int) Math.round(iw * scale);
                    int drawH = (int) Math.round(ih * scale);
                    g2.drawImage(bg.getImage(), (w - drawW) / 2, (h - drawH) / 2, drawW, drawH, this);
                } else {
                    g2.setPaint(new GradientPaint(0, 0, new Color(242, 245, 248), 0, h, new Color(223, 230, 236)));
                    g2.fillRect(0, 0, w, h);
                }
                g2.setComposite(AlphaComposite.SrcOver.derive(0.80f));
                g2.setPaint(new GradientPaint(0, 0, new Color(250, 252, 253, 205), 0, h, new Color(245, 248, 250, 185)));
                g2.fillRect(0, 0, w, h);
                g2.dispose();
            }
        };
        dashboardPanel.setOpaque(false);
        dashboardPanel.setBounds(0, 0, escritorio.getWidth(), escritorio.getHeight());

        RoundedPanel shell = new RoundedPanel(new Color(255, 255, 255, 214), new Color(255, 255, 255, 110), 28);
        shell.setLayout(new BorderLayout(0, 16));
        shell.setBorder(new EmptyBorder(18, 18, 18, 18));

        shell.add(crearBarraSuperior(), BorderLayout.NORTH);
        shell.add(crearContenidoPrincipal(), BorderLayout.CENTER);

        dashboardPanel.add(shell, BorderLayout.CENTER);

        escritorio.add(dashboardPanel);
        escritorio.setLayer(dashboardPanel, 0);

        escritorio.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                dashboardPanel.setBounds(0, 0, escritorio.getWidth(), escritorio.getHeight());
            }
        });

        SwingUtilities.invokeLater(() -> {
            dashboardPanel.setBounds(0, 0, escritorio.getWidth(), escritorio.getHeight());
            dashboardPanel.revalidate();
            dashboardPanel.repaint();
        });
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

        URL logoURL = getClass().getResource("/imagenes/logo_sena_verde.png");
        JComponent logoComponente;
        if (logoURL != null) {
            ImageIcon iconoOriginal = new ImageIcon(logoURL);
            int ancho = 140;
            int alto = (int) Math.round((double) iconoOriginal.getIconHeight() / iconoOriginal.getIconWidth() * ancho);
            Image escalada = iconoOriginal.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
            logoComponente = new JLabel(new ImageIcon(escalada));
        } else {
            JLabel fallback = new JLabel("SENA", SwingConstants.CENTER);
            fallback.setFont(new Font("SansSerif", Font.BOLD, 16));
            fallback.setForeground(SENA_GREEN);
            fallback.setOpaque(true);
            fallback.setBackground(SENA_GREEN_LIGHT);
            fallback.setBorder(new EmptyBorder(10, 18, 10, 18));
            logoComponente = fallback;
        }

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

        marca.add(logoComponente);
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

        JPanel filaInferior = new JPanel(new GridLayout(1, 2, 16, 16));
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
                "US",
                SENA_BLUE,
                "Usuarios",
                "Administra los usuarios registrados en el sistema de biblioteca.",
                "ADMINISTRAR",
                SENA_GREEN,
                Color.WHITE
        ));

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
        tarjeta.setLayout(new BorderLayout(0, 12));
        tarjeta.setBorder(new EmptyBorder(18, 18, 18, 18));
        tarjeta.setPreferredSize(new Dimension(240, 220));

        JPanel icono = new RoundedPanel(
                new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 24),
                new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 55), 18);
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

        JButton boton = new JButton(accion);
        boton.setFont(new Font("SansSerif", Font.BOLD, 12));
        boton.setForeground(textoBoton);
        boton.setBackground(colorBoton);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setOpaque(true);
        boton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        boton.setMinimumSize(new Dimension(0, 36));
        boton.setPreferredSize(new Dimension(0, 36));
        boton.setAlignmentX(Component.LEFT_ALIGNMENT);
        boton.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(colorBoton);
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 12, 12);
                super.paint(g2, c);
                g2.dispose();
            }
        });
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(colorBoton.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(colorBoton);
            }
        });
        boton.addActionListener(e -> manejarAccionModulo(titulo));

        centro.add(tituloLabel);
        centro.add(Box.createVerticalStrut(8));
        centro.add(descripcionLabel);
        centro.add(Box.createVerticalGlue());
        centro.add(Box.createVerticalStrut(8));
        centro.add(boton);

        tarjeta.add(icono, BorderLayout.NORTH);
        tarjeta.add(centro, BorderLayout.CENTER);

        tarjeta.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getSource() == tarjeta) {
                    manejarAccionModulo(titulo);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                tarjeta.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                tarjeta.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            }
        });
        return tarjeta;
    }

    private void manejarAccionModulo(String modulo) {
        if ("Inicio".equals(modulo)) {
            return;
        }

        if ("Ver Equipos Prestados".equals(modulo) || "Ver Todos Préstamos de Equipos".equals(modulo)) {
            abrirModuloPrestamosEquipos();
            return;
        }

        if ("Ver Libros Prestados".equals(modulo) || "Ver Todos Préstamos de Libros".equals(modulo)) {
            abrirModuloPrestamosLibros();
            return;
        }

        if ("Solicitar Equipo".equals(modulo) || "Solicitar Préstamo de Equipo".equals(modulo)) {
            abrirFormularioNuevoEquipo();
            return;
        }

        if ("Solicitar Libro".equals(modulo) || "Solicitar Préstamo de Libro".equals(modulo)) {
            abrirFormularioNuevoLibro();
            return;
        }

        if ("Libros".equals(modulo) || "Catálogo de Libros".equals(modulo)) {
            abrirModuloLibros();
            return;
        }

        if ("Equipos".equals(modulo) || "Catálogo de Equipos".equals(modulo)) {
            abrirModuloEquipos();
            return;
        }

        if ("Usuarios".equals(modulo)) {
            abrirModuloUsuarios();
            return;
        }

        if ("Sanciones".equals(modulo) || modulo.contains("Sanciones")) {
            abrirModuloSanciones();
            return;
        }

        if ("Préstamos".equals(modulo)) {
            abrirModuloPrestamosEquipos();
            return;
        }

        if ("Reportes".equals(modulo)) {
            abrirModuloReportes();
            return;
        }
    }

    private void abrirModuloEquipos() {
        for (JInternalFrame frame : escritorio.getAllFrames()) {
            if (frame instanceof Equipos) {
                try {
                    frame.setIcon(false);
                    frame.setMaximum(true);
                    frame.setSelected(true);
                } catch (PropertyVetoException ex) {
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
        escritorio.add(equipos);
        try {
            equipos.setSelected(true);
            equipos.setMaximum(true);
        } catch (PropertyVetoException ex) {
        }
        equipos.toFront();
    }

    private void abrirModuloLibros() {
        for (JInternalFrame frame : escritorio.getAllFrames()) {
            if (frame instanceof Libros) {
                try {
                    frame.setIcon(false);
                    frame.setMaximum(true);
                    frame.setSelected(true);
                } catch (PropertyVetoException ex) {
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
        escritorio.add(libros);
        try {
            libros.setSelected(true);
            libros.setMaximum(true);
        } catch (PropertyVetoException ex) {
        }
        libros.toFront();
    }

    private void abrirModuloUsuarios() {
        for (JInternalFrame frame : escritorio.getAllFrames()) {
            if (frame instanceof Usuarios) {
                try {
                    frame.setIcon(false);
                    frame.setMaximum(true);
                    frame.setSelected(true);
                } catch (PropertyVetoException ex) {
                }
                frame.toFront();
                return;
            }
        }

        Usuarios usuarios = new Usuarios();
        usuarios.setVisible(true);
        usuarios.setClosable(true);
        usuarios.setIconifiable(true);
        usuarios.setMaximizable(true);
        usuarios.setResizable(true);
        escritorio.add(usuarios);
        try {
            usuarios.setSelected(true);
            usuarios.setMaximum(true);
        } catch (PropertyVetoException ex) {
        }
        usuarios.toFront();
    }

    private void abrirModuloSanciones() {
        System.out.println("[MDIBiblioteca] Abriendo modulo Sanciones...");
        for (JInternalFrame frame : escritorio.getAllFrames()) {
            if (frame instanceof FRMSanciones) {
                try {
                    frame.setIcon(false);
                    frame.setMaximum(true);
                    frame.setSelected(true);
                } catch (PropertyVetoException ex) {
                }
                frame.toFront();
                return;
            }
        }

        try {
            FRMSanciones sanciones = new FRMSanciones();
            sanciones.setVisible(true);
            sanciones.setClosable(true);
            sanciones.setIconifiable(true);
            sanciones.setMaximizable(true);
            sanciones.setResizable(true);
            escritorio.add(sanciones);
            sanciones.setSelected(true);
            sanciones.setMaximum(true);
            sanciones.toFront();
            System.out.println("[MDIBiblioteca] Modulo Sanciones abierto correctamente.");
        } catch (PropertyVetoException ex) {
        } catch (Exception ex) {
            System.out.println("[MDIBiblioteca] Error abriendo modulo Sanciones: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void abrirModuloPrestamos() {
        for (JInternalFrame frame : escritorio.getAllFrames()) {
            if (frame instanceof FRMPrestamosEquipos) {
                try {
                    frame.setIcon(false);
                    frame.setMaximum(true);
                    frame.setSelected(true);
                } catch (PropertyVetoException ex) {
                }
                frame.toFront();
                return;
            }
        }

        try {
            FRMPrestamosEquipos prestamos = new FRMPrestamosEquipos();
            prestamos.setVisible(true);
            prestamos.setClosable(true);
            prestamos.setIconifiable(true);
            prestamos.setMaximizable(true);
            prestamos.setResizable(true);
            escritorio.add(prestamos);
            prestamos.setSelected(true);
            prestamos.setMaximum(true);
            prestamos.toFront();
        } catch (PropertyVetoException ex) {
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void abrirModuloPrestamosEquipos() {
        for (JInternalFrame frame : escritorio.getAllFrames()) {
            if (frame instanceof FRMPrestamosEquipos) {
                try {
                    frame.setIcon(false);
                    frame.setMaximum(true);
                    frame.setSelected(true);
                } catch (PropertyVetoException ex) {
                }
                frame.toFront();
                return;
            }
        }

        try {
            FRMPrestamosEquipos prestamos = new FRMPrestamosEquipos();
            prestamos.setVisible(true);
            prestamos.setClosable(true);
            prestamos.setIconifiable(true);
            prestamos.setMaximizable(true);
            prestamos.setResizable(true);
            escritorio.add(prestamos);
            prestamos.setSelected(true);
            prestamos.setMaximum(true);
            prestamos.toFront();
        } catch (PropertyVetoException ex) {
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void abrirModuloPrestamosLibros() {
        for (JInternalFrame frame : escritorio.getAllFrames()) {
            if (frame instanceof FRMPrestamosLibros) {
                try {
                    frame.setIcon(false);
                    frame.setMaximum(true);
                    frame.setSelected(true);
                } catch (PropertyVetoException ex) {
                }
                frame.toFront();
                return;
            }
        }

        try {
            FRMPrestamosLibros prestamos = new FRMPrestamosLibros();
            prestamos.setVisible(true);
            prestamos.setClosable(true);
            prestamos.setIconifiable(true);
            prestamos.setMaximizable(true);
            prestamos.setResizable(true);
            escritorio.add(prestamos);
            prestamos.setSelected(true);
            prestamos.setMaximum(true);
            prestamos.toFront();
        } catch (PropertyVetoException ex) {
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void abrirFormularioNuevoEquipo() {
        new FRMNuevoPrestamoEquipo(this, null).setVisible(true);
    }

    private void abrirFormularioNuevoLibro() {
        new FRMNuevoPrestamoLibro(this, null).setVisible(true);
    }

    private void abrirModuloReportes() {
        for (JInternalFrame frame : escritorio.getAllFrames()) {
            if (frame instanceof FRMReportes) {
                try { frame.setIcon(false); frame.setSelected(true); } catch (PropertyVetoException ex) {}
                frame.toFront();
                return;
            }
        }
        try {
            FRMReportes reportes = new FRMReportes();
            reportes.setVisible(true);
            reportes.setClosable(true);
            reportes.setIconifiable(true);
            reportes.setMaximizable(true);
            reportes.setResizable(true);
            escritorio.add(reportes);
            reportes.setSelected(true);
            reportes.setMaximum(true);
            reportes.toFront();
        } catch (PropertyVetoException ex) {
        } catch (Exception ex) { ex.printStackTrace(); }
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
        jMenu2 = new javax.swing.JMenu();
        mnuVerEquiposPrestados = new javax.swing.JMenuItem();
        mnuVerLibrosPrestados = new javax.swing.JMenuItem();
        mnuSolicitarEquipo = new javax.swing.JMenuItem();
        mnuSolicitarLibro = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        fileMenu.setText("Equipos");
        menuBar.add(fileMenu);

        jMenu1.setText("Libros");
        menuBar.add(jMenu1);

        jMenu2.setText("Préstamos");
        jMenu2.setName("jMenu2");

        mnuVerEquiposPrestados.setText("Ver Todos Préstamos de Equipos");
        mnuVerEquiposPrestados.addActionListener(evt -> manejarAccionModulo("Ver Equipos Prestados"));
        jMenu2.add(mnuVerEquiposPrestados);

        mnuVerLibrosPrestados.setText("Ver Todos Préstamos de Libros");
        mnuVerLibrosPrestados.addActionListener(evt -> manejarAccionModulo("Ver Libros Prestados"));
        jMenu2.add(mnuVerLibrosPrestados);

        jMenu2.add(new javax.swing.JSeparator());

        mnuSolicitarEquipo.setText("Solicitar Préstamo de Equipo");
        mnuSolicitarEquipo.addActionListener(evt -> manejarAccionModulo("Solicitar Equipo"));
        jMenu2.add(mnuSolicitarEquipo);

        mnuSolicitarLibro.setText("Solicitar Préstamo de Libro");
        mnuSolicitarLibro.addActionListener(evt -> manejarAccionModulo("Solicitar Libro"));
        jMenu2.add(mnuSolicitarLibro);

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
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuItem mnuVerEquiposPrestados;
    private javax.swing.JMenuItem mnuVerLibrosPrestados;
    private javax.swing.JMenuItem mnuSolicitarEquipo;
    private javax.swing.JMenuItem mnuSolicitarLibro;
    private javax.swing.JMenuItem jMenu3;
    private javax.swing.JMenuItem jMenu4;
    private javax.swing.JMenuItem jMenu5;
    private javax.swing.JMenuBar menuBar;

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
