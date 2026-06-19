package Vista;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

public class FRMReportes extends JInternalFrame {

    private static final Color SENA_GREEN = new Color(46, 170, 84);
    private static final Color SENA_GREEN_LIGHT = new Color(232, 250, 237);
    private static final Color TEXT_DARK = new Color(30, 35, 48);
    private static final Color TEXT_SOFT = new Color(100, 110, 128);
    private static final Color CARD_BORDER = new Color(220, 226, 234);
    private static final Color CARD_BG = new Color(252, 253, 255);

    public FRMReportes() {
        super("Reportes Estratégicos", true, true, true, true);
        construirVista();
    }

    private void construirVista() {
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);

        JPanel raiz = new FondoInternoPanel();
        raiz.setLayout(new BorderLayout());
        raiz.setBorder(new EmptyBorder(16, 24, 16, 24));

        JPanel cardPrincipal = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 235));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
                g2.setColor(new Color(210, 218, 228));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 22, 22);
                g2.dispose();
            }
        };
        cardPrincipal.setOpaque(false);
        cardPrincipal.setLayout(new BorderLayout(0, 0));

        cardPrincipal.add(crearBarraSuperior(), BorderLayout.NORTH);
        cardPrincipal.add(crearContenidoCentral(), BorderLayout.CENTER);

        raiz.add(cardPrincipal, BorderLayout.CENTER);
        setContentPane(raiz);
        pack();
    }

    private JPanel crearBarraSuperior() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 14));
        barra.setOpaque(false);
        barra.setBorder(new MatteBorder(0, 0, 1, 0, new Color(225, 230, 238)));

        JLabel icono = new JLabel("\u2B22");
        icono.setFont(new Font("SansSerif", Font.PLAIN, 18));
        icono.setForeground(SENA_GREEN);

        JLabel lbl = new JLabel("Centro de Reportes Institucionales");
        lbl.setFont(new Font("SansSerif", Font.BOLD, 15));
        lbl.setForeground(TEXT_DARK);

        barra.add(icono);
        barra.add(lbl);
        return barra;
    }

    private JPanel crearContenidoCentral() {
        JPanel central = new JPanel(new BorderLayout(0, 24));
        central.setOpaque(false);
        central.setBorder(new EmptyBorder(28, 36, 30, 36));

        central.add(crearEncabezado(), BorderLayout.NORTH);
        central.add(crearGrillaReportes(), BorderLayout.CENTER);

        return central;
    }

    private JPanel crearEncabezado() {
        JPanel cont = new JPanel();
        cont.setOpaque(false);
        cont.setLayout(new BoxLayout(cont, BoxLayout.Y_AXIS));

        JPanel filaTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filaTitulo.setOpaque(false);
        filaTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel icono = new JLabel("\uD83D\uDCC8");
        icono.setFont(new Font("SansSerif", Font.PLAIN, 22));
        icono.setForeground(SENA_GREEN);

        JLabel titulo = new JLabel("Reportes Estratégicos");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 26));
        titulo.setForeground(SENA_GREEN);

        filaTitulo.add(icono);
        filaTitulo.add(titulo);

        JLabel subtitulo = new JLabel("Bienvenido al centro de generación de reportes y estadísticas.");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitulo.setForeground(TEXT_SOFT);
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        cont.add(filaTitulo);
        cont.add(Box.createVerticalStrut(6));
        cont.add(subtitulo);
        return cont;
    }

    private JPanel crearGrillaReportes() {
        JPanel grilla = new JPanel(new GridLayout(1, 3, 20, 0));
        grilla.setOpaque(false);

        grilla.add(crearTarjetaReporte(
                "\u2B22",
                "Inventario General",
                "Consulta y exporta el estado actual del inventario (libros y/o equipos) con filtros detallados.",
                e -> abrirReporteInventario()
        ));
        grilla.add(crearTarjetaReporte(
                "\u21C4",
                "Préstamos Realizados",
                "Revisa el historial global de préstamos, devoluciones pendientes y vencidas por estado y fecha.",
                e -> abrirReportePrestamos()
        ));
        grilla.add(crearTarjetaReporte(
                "\uD83D\uDC65",
                "Usuarios Activos",
                "Listado de usuarios activos y su cantidad de préstamos en curso ordenados de mayor a menor.",
                e -> abrirReporteUsuarios()
        ));

        return grilla;
    }

    private JPanel crearTarjetaReporte(String icono, String titulo, String descripcion, ActionListener accion) {
        JPanel tarjeta = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(CARD_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                g2.dispose();
            }
        };
        tarjeta.setOpaque(false);
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel encabezado = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        encabezado.setOpaque(false);
        encabezado.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        encabezado.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblIcono = new JLabel(icono);
        lblIcono.setFont(new Font("SansSerif", Font.PLAIN, 18));
        lblIcono.setForeground(SENA_GREEN);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTitulo.setForeground(TEXT_DARK);

        encabezado.add(lblIcono);
        encabezado.add(lblTitulo);

        JLabel lblDesc = new JLabel("<html><div style='width: 220px; line-height: 1.5;'>" + descripcion + "</div></html>");
        lblDesc.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblDesc.setForeground(TEXT_SOFT);
        lblDesc.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnAcceder = new JButton("Acceder al Reporte  \u2192");
        btnAcceder.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnAcceder.setForeground(SENA_GREEN);
        btnAcceder.setBackground(Color.WHITE);
        btnAcceder.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(SENA_GREEN, 2),
                new EmptyBorder(12, 16, 12, 16)));
        btnAcceder.setFocusPainted(false);
        btnAcceder.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAcceder.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnAcceder.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnAcceder.setPreferredSize(new Dimension(0, 44));
        btnAcceder.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                btnAcceder.setBackground(SENA_GREEN);
                btnAcceder.setForeground(Color.WHITE);
            }
            @Override public void mouseExited(MouseEvent e) {
                btnAcceder.setBackground(Color.WHITE);
                btnAcceder.setForeground(SENA_GREEN);
            }
        });
        btnAcceder.addActionListener(accion);

        tarjeta.add(encabezado);
        tarjeta.add(Box.createVerticalStrut(20));
        tarjeta.add(lblDesc);
        tarjeta.add(Box.createVerticalGlue());
        tarjeta.add(Box.createVerticalStrut(16));
        tarjeta.add(btnAcceder);

        return tarjeta;
    }

    private void abrirReporteInventario() {
        for (JInternalFrame frame : getDesktopPane().getAllFrames()) {
            if (frame instanceof FRMReporteInventario) {
                try { frame.setIcon(false); frame.setSelected(true); } catch (Exception ex) {}
                frame.toFront();
                return;
            }
        }
        try {
            FRMReporteInventario f = new FRMReporteInventario();
            f.setVisible(true);
            getDesktopPane().add(f);
            f.setSelected(true);
            f.setMaximum(true);
            f.toFront();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void abrirReportePrestamos() {
        for (JInternalFrame frame : getDesktopPane().getAllFrames()) {
            if (frame instanceof FRMReportePrestamos) {
                try { frame.setIcon(false); frame.setSelected(true); } catch (Exception ex) {}
                frame.toFront();
                return;
            }
        }
        try {
            FRMReportePrestamos f = new FRMReportePrestamos();
            f.setVisible(true);
            getDesktopPane().add(f);
            f.setSelected(true);
            f.setMaximum(true);
            f.toFront();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void abrirReporteUsuarios() {
        for (JInternalFrame frame : getDesktopPane().getAllFrames()) {
            if (frame instanceof FRMReporteUsuarios) {
                try { frame.setIcon(false); frame.setSelected(true); } catch (Exception ex) {}
                frame.toFront();
                return;
            }
        }
        try {
            FRMReporteUsuarios f = new FRMReporteUsuarios();
            f.setVisible(true);
            getDesktopPane().add(f);
            f.setSelected(true);
            f.setMaximum(true);
            f.toFront();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private static class FondoInternoPanel extends JPanel {
        private static final Image FONDO;
        static {
            Image img = null;
            try { img = new ImageIcon(FRMReportes.class.getResource("/imagenes/fondo.jpg")).getImage(); } catch (Exception e) {}
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
                g2.setPaint(new GradientPaint(0, 0, new Color(245, 248, 250), 0, getHeight(), new Color(230, 240, 235)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
            g2.setComposite(AlphaComposite.SrcOver.derive(0.55f));
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }
}
