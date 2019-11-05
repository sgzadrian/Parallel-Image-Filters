import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

public class ImagePane extends JPanel {
    private static final long serialVersionUID = -7101677868629339421L;

    Image back = null;

    ImagePane() {
        super();
    }

    @Override
    protected void paintComponent( Graphics g ) {
        super.paintComponent( g );
        if ( back != null ) {
            int h = getHeight();
            int w = ( back.getWidth( null ) * h ) / back.getHeight( null );
            int posX = getWidth() / 2 - w / 2;
            Image tmp = back.getScaledInstance( w, h, Image.SCALE_DEFAULT );
            g.drawImage( tmp, posX, 0, this );
        }
    }

    public void setBack( BufferedImage back ) {
        this.back = back;
        repaint();
    }
}
