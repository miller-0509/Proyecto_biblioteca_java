package Vista;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class FRMReportes extends JInternalFrame {

    private static final Color SENA_GREEN = new Color(46, 170, 84);
    private static final Color TEXT_DARK = new Color(28, 34, 45);
    private static final Color TEXT_SOFT = new Color(96, 105, 121);
    private static final Color CARD_BG = new Color(255, 255, 255);
    private static final Color CARD_BORDER = new Color(223, 228, 234);

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
        raiz.setLayout(new BorderLayout(0, 18));
        raiz.setBorder(new EmptyBorder(20, 22, 22, 22));
        raiz.add(crearEncabezado(), BorderLayout.NORTH);
        raiz.add(crearGrillaReportes(), BorderLayout.CENTER);
        setContentPane(raiz);
        pack();
    }

    private JPanel crearEncabezado() {
        JPanel cont = new JPanel(new BorderLayout(16, 6));
        cont.setOpaque(false);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("\uD83D\uDCCA Reportes Estratégicos");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 26));
        titulo.setForeground(TEXT_DARK);

        JLabel subtitulo = new JLabel("Bienvenido al centro de generación de reportes y estadísticas.");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitulo.setForeground(TEXT_SOFT);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(subtitulo);

        cont.add(textos, BorderLayout.WEST);
        return cont;
    }

    private JPanel crearGrillaReportes() {
        JPanel grilla = new JPanel(new GridLayout(1, 3, 20, 0));
        grilla.setOpaque(false);
        grilla.setBorder(new EmptyBorder(10, 0, 0, 0));

        grilla.add(crearTarjetaReporte(
                "\uD83D\uDCE6",
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
        JPanel tarjeta = new JPanel(new BorderLayout(0, 12)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setColor(CARD_BORDER);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 18, 18);
                g2.dispose();
            }
        };
        tarjeta.setOpaque(false);
        tarjeta.setBorder(new EmptyBorder(22, 22, 22, 22));

        JPanel encabezado = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        encabezado.setOpaque(false);

        JLabel lblIcono = new JLabel(icono);
        lblIcono.setFont(new Font("SansSerif", Font.PLAIN, 22));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 17));
        lblTitulo.setForeground(TEXT_DARK);

        encabezado.add(lblIcono);
        encabezado.add(lblTitulo);

        JLabel lblDesc = new JLabel("<html><div style='width: 220px;'>" + descripcion + "</div></html>");
        lblDesc.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblDesc.setForeground(TEXT_SOFT);

        JButton btnAcceder = new JButton("Acceder al Reporte  \u2192");
        btnAcceder.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnAcceder.setForeground(SENA_GREEN);
        btnAcceder.setBackground(Color.WHITE);
        btnAcceder.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(SENA_GREEN, 2),
                new EmptyBorder(10, 16, 10, 16)));
        btnAcceder.setFocusPainted(false);
        btnAcceder.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAcceder.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnAcceder.setPreferredSize(new Dimension(0, 42));
        btnAcceder.setAlignmentX(Component.LEFT_ALIGNMENT);
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

        tarjeta.add(encabezado, BorderLayout.NORTH);
        tarjeta.add(lblDesc, BorderLayout.CENTER);
        tarjeta.add(btnAcceder, BorderLayout.SOUTH);

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
