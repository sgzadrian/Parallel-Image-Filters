import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class Server {

    public static void main(String args[]) throws Exception {
        String address = "localhost";
        if ( args.length > 0 && args[0].length() > 5 ) {
            address = args[0];
        }
        System.out.println( "Server started on " + address );

        try {
            LocateRegistry.createRegistry( 1099 );
            System.out.println( "RMI registry created." );
        } catch (RemoteException e) {
            System.out.println( "RMI registry already exists." );
        }

        RemoteFilter server = new RemoteFilter();
        Window w = new Window( server );
        server.setWindow( w );
        Naming.rebind( "//"+ address +"/ImgFilters", server );
    }

}
