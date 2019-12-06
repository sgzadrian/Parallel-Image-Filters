import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.rmi.*;
import java.rmi.server.*;
import java.util.ArrayList;

import javax.swing.JTextArea;

public class Chat extends UnicastRemoteObject implements ChatInterface  {

	private static final long serialVersionUID = 1L;

	public String name;
	public String address;

    private ArrayList<ChatInterface> clientList = new ArrayList<ChatInterface>();

    private ChatInterface server = null;

    private JTextArea output = null;

    public Chat( String name ) throws RemoteException {
        super( 0 );
        this.name = name;
    }

    public Chat( String name, String address ) throws RemoteException {
        super( 0 );
        this.name = name;
        this.address = address;
    }

    public void setOutput( JTextArea output ) {
        this.output = output;
    }

	@Override
    public String getName() throws RemoteException {
        return this.name;
    }

	@Override
    public void setClient( ChatInterface client ) {
        clientList.add( client );
    }

	@Override
    public void send( String msg ) throws RemoteException {
        if ( output == null ) {
            System.out.println( msg );
        } else {
            output.append( msg );
        }
    }

	@Override
    public void send( String msg, String name ) throws RemoteException {
        for( int i = 0; i < clientList.size(); i++ ) {
            ChatInterface client = clientList.get( i );
            if ( client.getName().equals( name ) ) {
                client.send( msg );
            }
        }
    }

	@Override
	public String getClients() throws RemoteException {
        String clients = "";
        for( int i = 0; i < clientList.size(); i++ ) {
            ChatInterface client = clientList.get( i );
            String name = client.getName();
            clients += "["+ name +"] ";
        }
        return clients;
	}

    public static String getAddress() throws UnknownHostException {
        return Inet4Address.getLocalHost().getHostAddress();
    }

	@Override
	public void removeClient( String removeName ) throws RemoteException {
        for( int i = 0; i < clientList.size(); i++ ) {
            ChatInterface client = clientList.get( i );
            String name = client.getName();
            if ( name.equals( removeName ) ) {
                clientList.remove( i );
                return;
            }
        }
	}
}
