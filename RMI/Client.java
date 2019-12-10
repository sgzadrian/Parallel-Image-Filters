import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Client {

    private FiltersInterface server = null;
    private RemoteFilter client = null;

    private String serverAddress = "localhost";

    Client( String address ) {
        serverAddress = address;
        try {
            connect();
        } catch ( Exception e ) {
            System.out.println( "Cant connect to server" );
            e.printStackTrace();
        }
    }

    public void connect() throws MalformedURLException, RemoteException, NotBoundException {
        client = new RemoteFilter();
        server = ( FiltersInterface ) Naming.lookup( "//"+ serverAddress +"/ImgFilters" );
        server.registerClient( client );
        System.out.println( "Connected\n" );
    }

    public static void main(String args[]) throws Exception {
        String address = "localhost";
        if ( address.length() > 7 ) {
            address = args[ 0 ];
        }
        new Client( address );
    }

}
