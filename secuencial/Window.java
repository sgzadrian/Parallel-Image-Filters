import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Window extends JFrame {

    private static final long serialVersionUID = 1L;

    JPanel mainPanel = new JPanel();
    ImagePane originalImg = new ImagePane();

    JButton fileBtn = new JButton( "Select an Image" );
    JFileChooser chooser = new JFileChooser();
    BufferedImage file;

    ImagePane grayImg = new ImagePane();
    ImagePane sepiaImg = new ImagePane();
    ImagePane negativeImg = new ImagePane();
    ImagePane boxImg = new ImagePane();
    ImagePane gaussImg = new ImagePane();
    ImagePane diffImg = new ImagePane();

    Window() {
        super( "Image filters" );
        setMinimumSize( new Dimension( 800, 600 ) );
        setResizable( true );
        setDefaultCloseOperation( EXIT_ON_CLOSE );
        setContentPane( mainPanel );
        setVisible( true );
        setElements();
        pack();
    }

    public void setElements() {
        GridLayout base = new GridLayout( 3, 1 );
        JPanel top = new JPanel( new GridLayout( 1, 2 ) );
        JPanel middle = new JPanel( new GridLayout( 1, 3 ) );
        JPanel bottom = new JPanel( new GridLayout( 1, 3 ) );
        // Set panels
        mainPanel.setLayout( base );
        mainPanel.add( top );
        mainPanel.add( middle );
        mainPanel.add( bottom );
        // File Chooser
        FileNameExtensionFilter filter = new FileNameExtensionFilter( "JPG & GIF Images", "jpg", "gif", "jpeg" );
        chooser.setFileFilter( filter );
        chooser.setDialogTitle( "Select an image" );
        // Choose a file button
        fileBtn.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e ) {
                chooseFile();
            }
        });
        top.add( fileBtn );
        top.add( originalImg );

        middle.add( grayImg );
        middle.add( sepiaImg );
        middle.add( negativeImg );

        bottom.add( boxImg );
        bottom.add( diffImg );
        bottom.add( gaussImg );
    }

    public void chooseFile() {
        int userSelection = chooser.showSaveDialog( this );
        if ( userSelection == JFileChooser.APPROVE_OPTION ) {
            try {
				file = ImageIO.read( chooser.getSelectedFile() );
                originalImg.setBack( file );
                applyFilters();
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }
    }

    public void applyFilters() {
        new GrayScale( file, grayImg );
        new Sepia( file, sepiaImg );
        new Negative( file, negativeImg );
        LinearFilter box = new LinearFilter( file, boxImg );
        box.box( 5 );
        LinearFilter gauss = new LinearFilter( file, gaussImg );
        gauss.gauss();
        LinearFilter diff = new LinearFilter( file, diffImg );
        diff.diff();
    }

}
