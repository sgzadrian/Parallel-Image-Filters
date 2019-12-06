import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class Server {

    public static void main(String args[]) throws Exception {
        System.out.println( "Server started" );
        String address = "localhost";
        if ( args[0].length() > 5 ) {
            address = args[0];
        }

        try {
            LocateRegistry.createRegistry( 1099 );
            System.out.println( "RMI registry created." );
        } catch (RemoteException e) {
            // Do nothing, error means registry already exists
            System.out.println( "RMI registry already exists." );
        }

        //Instantiate RmiServer
        Chat server = new Chat( "server" );
        // Bind this object instance to the "Chat"
        Naming.rebind( "//"+ address +"/Chat", server );
    }
}
