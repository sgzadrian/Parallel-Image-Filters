import java.rmi.Remote;
import java.rmi.RemoteException;

import java.awt.image.BufferedImage;

public interface FiltersInterface extends Remote {

    // Client -> Client / Server -> Server
    public void setImage( BufferedImage image ) throws RemoteException;
    public void setImage( byte[] image ) throws RemoteException;
    public void registerServer( FiltersInterface server ) throws RemoteException;
    public void runFilters() throws RemoteException;

    // Server -> Client
    public void clearFilters() throws RemoteException;
    public void addFilter( int filter ) throws RemoteException;
    public void runClients() throws RemoteException;

    // Client -> Server
    public void registerClient( FiltersInterface client ) throws RemoteException;
    public BufferedImage getServerImage() throws RemoteException;
    public void saveImage( byte[] image, String filename, int filter, long time ) throws RemoteException;
}
