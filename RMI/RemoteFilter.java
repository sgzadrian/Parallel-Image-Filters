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
    private ArrayList<FiltersInterface> clientList = new ArrayList<>();
    private byte[] image = null;

    // Client Variables
    private ArrayList<Integer> filterList = new ArrayList<>();
    private FiltersInterface server = null;

    public RemoteFilter() throws RemoteException {
        super( 0 );
    }

    // Static Methods
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

	@Override
	public void runFilters() throws RemoteException {
        for ( Integer filter : filterList ) {
            switch ( filter ) {
                case RemoteFilter.GRAYSCALE:
                    GrayScale.splitAndRun( RemoteFilter.byte2im( image ), server );
                    break;
                case RemoteFilter.SEPIA:
                    break;
                case RemoteFilter.NEGATIVE:
                    break;
                case RemoteFilter.BOX:
                    break;
                case RemoteFilter.GAUSS:
                    break;
                case RemoteFilter.DIFF:
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

	@Override
	public void runClients() throws RemoteException {
        FiltersInterface tmpClient;
        int clientsSize = clientList.size();
        for (FiltersInterface client : clientList) {
            client.clearFilters();
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
        client.setImage( image );
	}

	@Override
	public BufferedImage getServerImage() throws RemoteException {
        return RemoteFilter.byte2im( image );
	}

	@Override
    public void saveImage( byte[] image, String filename, long time ) {
        Filter.write( image, filename, time );
    }
}
