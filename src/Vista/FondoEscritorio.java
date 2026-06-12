package Vista;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JDesktopPane;

public class FondoEscritorio extends JDesktopPane {

    private Image imagen;

    public void setImagenFondo(Image imagen) {
        this.imagen = imagen;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imagen != null) {
            g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
