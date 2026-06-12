package Vista;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class MDIBiblioteca extends javax.swing.JFrame {

    public MDIBiblioteca() {
        initComponents();
        reorganizarLayout();
        setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
    }

    private void reorganizarLayout() {
        JLabel lblFondo = new JLabel() {
            @Override
            public void paintComponent(java.awt.Graphics g) {
                if (getIcon() != null) {
                    g.drawImage(((ImageIcon) getIcon()).getImage(), 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        java.net.URL imgURL = getClass().getResource("/imagenes/fondo.jpg");
        if (imgURL != null) {
            lblFondo.setIcon(new ImageIcon(imgURL));
        }

        javax.swing.JPanel centerPanel = new javax.swing.JPanel(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = java.awt.GridBagConstraints.BOTH;
        centerPanel.add(lblFondo, gbc);
        escritorio.setOpaque(false);
        centerPanel.add(escritorio, gbc);

        java.awt.Container content = getContentPane();
        content.removeAll();
        content.setLayout(new java.awt.BorderLayout());
        content.add(centerPanel, java.awt.BorderLayout.CENTER);
    }

    private void initComponents() {
        escritorio = new javax.swing.JDesktopPane();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        openMenuItem = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        jMenu4 = new javax.swing.JMenu();
        jMenu5 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        fileMenu.setMnemonic('f');
        fileMenu.setText("Equipos");
        openMenuItem.setMnemonic('o');
        openMenuItem.setText("Abrir");
        openMenuItem.addActionListener(this::openMenuItemActionPerformed);
        fileMenu.add(openMenuItem);
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

    private void openMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
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

    private javax.swing.JDesktopPane escritorio;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem openMenuItem;
}
