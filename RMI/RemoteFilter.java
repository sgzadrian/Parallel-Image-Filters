import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class RemoteFilter extends UnicastRemoteObject implements FiltersInterface {

    public static final int GRAYSCALE = 0;
    public static final int SEPIA     = 1;
    public static final int NEGATIVE  = 2;
    public static final int BOX       = 3;
    public static final int GAUSS     = 4;
    public static final int DIFF      = 5;

	private static final long serialVersionUID = 1L;

    // Server Variables
    ArrayList<FiltersInterface> clientList = new ArrayList<>();
    byte[] image = null;
    Window window = null;

    // Client Variables
    ArrayList<Integer> filterList = new ArrayList<>();
    FiltersInterface server = null;

    /******************* Constructors *******************/

    public RemoteFilter() throws RemoteException {
        super( 0 );
    }

    /******************* Static Methods *******************/

    public static byte[] im2byte( BufferedImage image ) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
			ImageIO.write( image, "jpg", baos );
		} catch (IOException e) {
			e.printStackTrace();
		}
        return baos.toByteArray();
    }

    public static BufferedImage byte2im( byte[] image ) {
        ByteArrayInputStream bais = new ByteArrayInputStream( image );
        try {
			return ImageIO.read( bais );
		} catch (IOException e) {
			e.printStackTrace();
            return null;
		}
    }

    /******************* GUI Methods *******************/

    public Window getWindow() {
        return window;
    }

    public void setWindow(Window window) {
        this.window = window;
    }

    /******************* Remote Methods *******************/

    // Client -> Client / Server -> Server
	@Override
	public void setImage(BufferedImage image) throws RemoteException {
        this.image = RemoteFilter.im2byte( image );
	}

	@Override
	public void setImage(byte[] image) throws RemoteException {
        this.image = image;
	}

	@Override
	public void registerServer(FiltersInterface server) throws RemoteException {
        this.server = server;
	}

    /*
     * Client Only Function !
     * It runs all the assigned filters on a separated thread
     * for more info check SplitAndRun on the filter classes
     */
	@Override
	public void runFilters() throws RemoteException {
        for ( Integer filter : filterList ) {
            switch ( filter ) {
                case RemoteFilter.GRAYSCALE:
                    GrayScale.splitAndRun( RemoteFilter.byte2im( image ), server );
                    break;
                case RemoteFilter.SEPIA:
                    Sepia.splitAndRun( RemoteFilter.byte2im( image ), server );
                    break;
                case RemoteFilter.NEGATIVE:
                    Negative.splitAndRun( RemoteFilter.byte2im( image ), server );
                    break;
                case RemoteFilter.BOX:
                    LinearFilter.splitAndRun( RemoteFilter.byte2im( image ), RemoteFilter.BOX, server );
                    break;
                case RemoteFilter.GAUSS:
                    LinearFilter.splitAndRun( RemoteFilter.byte2im( image ), RemoteFilter.GAUSS, server );
                    break;
                case RemoteFilter.DIFF:
                    LinearFilter.splitAndRun( RemoteFilter.byte2im( image ), RemoteFilter.DIFF, server );
                    break;
            }
        }
    }

    // Server -> Client
	@Override
    public void clearFilters() {
        filterList.clear();
    }

	@Override
    public void addFilter( int filter ) {
        filterList.add( filter );
    }

    /*
     * Server Only function !
     * Gives each Client one or more filters to process then run
     * every Client from distance
     */
	@Override
	public void runClients() throws RemoteException {
        FiltersInterface tmpClient;
        int clientsSize = clientList.size();
        for( int i = 0; i < clientsSize; ++i ) {
            tmpClient = clientList.get( i );
            try {
                tmpClient.clearFilters();
                tmpClient.setImage( image );
            } catch (Exception e) {
                clientList.remove( i );
                clientsSize = clientList.size();
                --i;
            }
        }
        if ( clientsSize < 1 ) {
            // System.out.println( "No Clients connected :C \n" );
            window.print( "No Clients connected :C \n" );
            return;
        }
        for( int i = 0, j = 0; i < 6; ++i, ++j ) {
            if ( j >= clientsSize) {
                j = 0;
            }
            tmpClient = clientList.get( j );
            tmpClient.addFilter( i );
        }
        for (FiltersInterface client : clientList) {
            client.runFilters();
        }
	}

    // Client -> Server
	@Override
	public void registerClient(FiltersInterface client) throws RemoteException {
        clientList.add( client );
        client.registerServer( this );
        // client.setImage( image );
        window.print( "Client connected..." );
	}

	@Override
	public BufferedImage getServerImage() throws RemoteException {
        return RemoteFilter.byte2im( image );
	}

    /*
     * This function can't receive a BufferedImage cuz it sends the processed image
     * directly to the server to save it, that means the image should be Byte array
     * in order to serialize it to send
     */
	@Override
    public void saveImage( byte[] image, String filename, int filter, long time ) {
        Filter.write( image, filename, time );
        window.setFilterImage( image, filter, filename, time );
    }

}
