import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatInterface extends Remote {

    public String getName() throws RemoteException;
	public void send( String msg ) throws RemoteException;
	public void send( String msg, String name ) throws RemoteException;
	public void setClient( ChatInterface client )throws RemoteException;
	public void removeClient( String name )throws RemoteException;
	public String getClients() throws RemoteException;
}
