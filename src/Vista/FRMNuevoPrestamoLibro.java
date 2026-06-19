package Vista;

import Controlador.PrestamoControlador;
import Controlador.LibroControlador;
import Controlador.UsuarioControlador;
import Modelo.Libro;
import Modelo.Prestamo;
import Modelo.Usuario;
import java.awt.*;
import java.awt.event.*;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class FRMNuevoPrestamoLibro extends JDialog {

    private static final Color SENA_GREEN = new Color(46, 170, 84);
    private static final Color TEXT_DARK = new Color(28, 34, 45);
    private static final Color TEXT_SOFT = new Color(96, 105, 121);

    private PrestamoControlador prestamoControlador;
    private LibroControlador libroControlador;
    private UsuarioControlador usuarioControlador;

    private JComboBox<Usuario> cmbUsuario;
    private JComboBox<Libro> cmbLibro;
    private JSpinner spnFechaLimite;
    private JTextArea txtObservaciones;
    private FRMPrestamosLibros frmPadre;

    public FRMNuevoPrestamoLibro(Frame owner, FRMPrestamosLibros padre) {
        super(owner, "Solicitar Préstamo de Libro", true);
        this.frmPadre = padre;
        prestamoControlador = new PrestamoControlador();
        libroControlador = new LibroControlador();
        usuarioControlador = new UsuarioControlador();
        construirVista();
        cargarCombos();
    }

    private void construirVista() {
        setSize(520, 520);
        setLocationRelativeTo(getOwner());
        setResizable(false);

        JPanel contenido = new JPanel(new BorderLayout(0, 16));
        contenido.setBorder(new EmptyBorder(22, 24, 22, 24));
        contenido.setBackground(Color.WHITE);

        JPanel encabezado = new JPanel();
        encabezado.setOpaque(false);
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));
        encabezado.add(Box.createVerticalStrut(4));

        JLabel titulo = new JLabel("Nuevo Préstamo de Libro");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setForeground(TEXT_DARK);
        titulo.setAlignmentX(0);

        JLabel sub = new JLabel("Seleccione el usuario y el libro a prestar.");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setForeground(TEXT_SOFT);
        sub.setAlignmentX(0);

        encabezado.add(titulo);
        encabezado.add(Box.createVerticalStrut(4));
        encabezado.add(sub);

        JPanel formulario = new JPanel();
        formulario.setOpaque(false);
        formulario.setLayout(new BoxLayout(formulario, BoxLayout.Y_AXIS));

        cmbUsuario = new JComboBox<>();
        cmbUsuario.setFont(new Font("SansSerif", Font.PLAIN, 13));
        cmbUsuario.setPreferredSize(new Dimension(400, 38));
        cmbUsuario.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object v, int idx, boolean sel, boolean foc) {
                super.getListCellRendererComponent(l, v, idx, sel, foc);
                if (v instanceof Usuario) {
                    Usuario u = (Usuario) v;
                    setText(u.getNombreCompleto() + " — " + u.getCorreo());
                }
                return this;
            }
        });

        cmbLibro = new JComboBox<>();
        cmbLibro.setFont(new Font("SansSerif", Font.PLAIN, 13));
        cmbLibro.setPreferredSize(new Dimension(400, 38));
        cmbLibro.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object v, int idx, boolean sel, boolean foc) {
                super.getListCellRendererComponent(l, v, idx, sel, foc);
                if (v instanceof Libro) {
                    Libro lb = (Libro) v;
                    setText(lb.getTitulo() + " — " + lb.getGenero());
                }
                return this;
            }
        });

        Date modelo = new Date();
        spnFechaLimite = new JSpinner(new SpinnerDateModel(modelo, null, null, Calendar.DAY_OF_MONTH));
        spnFechaLimite.setFont(new Font("SansSerif", Font.PLAIN, 13));
        spnFechaLimite.setPreferredSize(new Dimension(400, 38));
        JSpinner.DateEditor editorFecha = new JSpinner.DateEditor(spnFechaLimite, "dd/MM/yyyy");
        spnFechaLimite.setEditor(editorFecha);

        txtObservaciones = new JTextArea(3, 30);
        txtObservaciones.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);
        txtObservaciones.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 220)),
                new EmptyBorder(8, 10, 8, 10)));

        formulario.add(crearCampo("Usuario", cmbUsuario));
        formulario.add(Box.createVerticalStrut(12));
        formulario.add(crearCampo("Libro", cmbLibro));
        formulario.add(Box.createVerticalStrut(12));
        formulario.add(crearCampo("Fecha límite", spnFechaLimite));
        formulario.add(Box.createVerticalStrut(12));
        formulario.add(crearCampo("Observaciones", new JScrollPane(txtObservaciones)));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnCancelar.setForeground(TEXT_SOFT);
        btnCancelar.setBackground(Color.WHITE);
        btnCancelar.setPreferredSize(new Dimension(110, 38));
        btnCancelar.addActionListener(e -> dispose());

        JButton btnGuardar = new JButton("Registrar");
        btnGuardar.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setBackground(SENA_GREEN);
        btnGuardar.setPreferredSize(new Dimension(120, 38));
        btnGuardar.addActionListener(e -> guardar());

        btnPanel.add(btnCancelar);
        btnPanel.add(btnGuardar);

        contenido.add(encabezado, BorderLayout.NORTH);
        contenido.add(formulario, BorderLayout.CENTER);
        contenido.add(btnPanel, BorderLayout.SOUTH);

        setContentPane(contenido);
    }

    private void cargarCombos() {
        new SwingWorker<List<Usuario>, Void>() {
            @Override protected List<Usuario> doInBackground() { return usuarioControlador.listarTodos(); }
            @Override protected void done() {
                try {
                    for (Usuario u : get()) cmbUsuario.addItem(u);
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();

        new SwingWorker<List<Libro>, Void>() {
            @Override protected List<Libro> doInBackground() { return libroControlador.listarTodos(); }
            @Override protected void done() {
                try {
                    for (Libro lb : get()) cmbLibro.addItem(lb);
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();
    }

    private void guardar() {
        Usuario u = (Usuario) cmbUsuario.getSelectedItem();
        Libro lb = (Libro) cmbLibro.getSelectedItem();
        Date fecha = (Date) spnFechaLimite.getValue();
        String obs = txtObservaciones.getText().trim();

        if (u == null) { JOptionPane.showMessageDialog(this, "Seleccione un usuario."); return; }
        if (lb == null) { JOptionPane.showMessageDialog(this, "Seleccione un libro."); return; }

        Prestamo p = new Prestamo();
        p.setIdUsuario(u.getIdUsuario());
        p.setIdLibro(lb.getIdLibro());
        p.setFechaSolicitud(new Timestamp(System.currentTimeMillis()));
        p.setFechaLimite(new Timestamp(fecha.getTime()));
        p.setObservaciones(obs);

        int id = prestamoControlador.crearLibro(p);
        if (id > 0) {
            JOptionPane.showMessageDialog(this, "Préstamo registrado exitosamente.");
            frmPadre.refrescar();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar el préstamo.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel crearCampo(String lbl, JComponent campo) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setAlignmentX(0);
        JLabel label = new JLabel(lbl);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(TEXT_SOFT);
        label.setAlignmentX(0);
        p.add(label);
        p.add(Box.createVerticalStrut(6));
        campo.setAlignmentX(0);
        if (campo instanceof JTextField || campo instanceof JComboBox || campo instanceof JSpinner) {
            campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        }
        p.add(campo);
        return p;
    }
}
