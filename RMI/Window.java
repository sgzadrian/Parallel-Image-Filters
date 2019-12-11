import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.rmi.RemoteException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Window extends JFrame {

    private static final long serialVersionUID = 1L;

    JPanel mainPanel = new JPanel();
    ImagePane originalImg = new ImagePane();

    JButton fileBtn = new JButton( "Select an Image" );
    JButton startBtn = new JButton( "Apply Filters" );
    JFileChooser chooser = new JFileChooser();
    JTextArea msgArea = new JTextArea( 5, 20 );
    BufferedImage file;

    ImagePane grayImg = new ImagePane();
    ImagePane sepiaImg = new ImagePane();
    ImagePane negativeImg = new ImagePane();
    ImagePane boxImg = new ImagePane();
    ImagePane gaussImg = new ImagePane();
    ImagePane diffImg = new ImagePane();

    FiltersInterface server = null;

    Window() {
        super( "RMI Image filters" );
        setMinimumSize( new Dimension( 800, 600 ) );
        setResizable( true );
        setDefaultCloseOperation( EXIT_ON_CLOSE );
        setContentPane( mainPanel );
        setVisible( true );
        setElements();
        pack();
    }

    Window( FiltersInterface server ) {
        this();
        this.server = server;
    }

    public void setElements() {
        // Set base layouts
        GridLayout base = new GridLayout( 3, 1 );
        JPanel top = new JPanel( new GridLayout( 1, 2 ) );
        JPanel middle = new JPanel( new GridLayout( 1, 3 ) );
        JPanel bottom = new JPanel( new GridLayout( 1, 3 ) );
        // Set panels
        top.setBorder( new EmptyBorder( 6, 6, 6, 6 ) );
        middle.setBorder( new EmptyBorder( 6, 6, 6, 6 ) );
        bottom.setBorder( new EmptyBorder( 6, 6, 6, 6 ) );
        mainPanel.setLayout( base );
        mainPanel.add( top );
        mainPanel.add( middle );
        mainPanel.add( bottom );
        // File Chooser
        FileNameExtensionFilter filter = new FileNameExtensionFilter( "JPG & GIF Images", "jpg", "gif", "jpeg" );
        chooser.setFileFilter( filter );
        chooser.setDialogTitle( "Select an image" );

        // Top Area
        JPanel topInner = new JPanel( new GridLayout( 5, 1 ) );
        topInner.setBorder( new EmptyBorder( 0, 20, 0, 20 ) );
        top.add( topInner );
        // Inner Buttons
        fileBtn.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e ) {
                chooseFile();
            }
        });
        startBtn.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e ) {
                applyFilters();
            }
        });
        topInner.add( new JPanel() );
        topInner.add( fileBtn );
        topInner.add( new JPanel() );
        topInner.add( startBtn );
        topInner.add( new JPanel() );
        // top.add( fileBtn );
        // top.add( originalImg );
        msgArea.setEditable( false );
        JScrollPane scrollPane = new JScrollPane( msgArea );
        top.add( scrollPane );

        // Set image panels
        grayImg.setBorder( new EmptyBorder( 0, 6, 0, 6 ) );
        sepiaImg.setBorder( new EmptyBorder( 0, 6, 0, 6 ) );
        negativeImg.setBorder( new EmptyBorder( 0, 6, 0, 6 ) );
        boxImg.setBorder( new EmptyBorder( 0, 6, 0, 6 ) );
        diffImg.setBorder( new EmptyBorder( 0, 6, 0, 6 ) );
        gaussImg.setBorder( new EmptyBorder( 0, 6, 0, 6 ) );

        middle.add( grayImg );
        middle.add( sepiaImg );
        middle.add( negativeImg );

        bottom.add( boxImg );
        bottom.add( diffImg );
        bottom.add( gaussImg );
    }

    public void print( String msg ) {
        msg = msg.trim() + "\n";
        msgArea.append( msg );
    }

    public void chooseFile() {
        int userSelection = chooser.showSaveDialog( this );
        if ( userSelection == JFileChooser.APPROVE_OPTION ) {
            try {
                file = ImageIO.read( chooser.getSelectedFile() );
                String filename = chooser.getName( chooser.getSelectedFile() );
                fileBtn.setText( filename );
                // originalImg.setBack( file );
                // applyFilters();
                server.setImage( file );
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }
    }

    public void applyFilters() {
        try {
            print( "\n" );
			server.runClients();
		} catch (RemoteException e) {
            print( "\n!!! Error trying to run the filters !!!\n" );
		}
    }

    public void setFilterImage( byte[] imageData, int filter, String filename, long time ) {
        BufferedImage image = RemoteFilter.byte2im( imageData );
        switch ( filter ) {
            case RemoteFilter.GRAYSCALE:
                grayImg.setBack( image );
                break;
            case RemoteFilter.SEPIA:
                sepiaImg.setBack( image );
                break;
            case RemoteFilter.NEGATIVE:
                negativeImg.setBack( image );
                break;
            case RemoteFilter.BOX:
                boxImg.setBack( image );
                break;
            case RemoteFilter.GAUSS:
                gaussImg.setBack( image );
                break;
            case RemoteFilter.DIFF:
                diffImg.setBack( image );
                break;
        }
        print( filename +" finished in "+ time +"ms " );
    }

}
