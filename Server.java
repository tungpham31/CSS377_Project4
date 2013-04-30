import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * An object implementing the Remote interface. This allows it to be
 * called through RMI.
 */
public class Server implements Store {

    /**
     * This port will be assigned to your group for use on EC2. For local testing, you can use any (nonstandard) port you wish.
     */
    public final static int REGISTRY_PORT = 53824;

    public int Buy(String bookName, int copies) throws RemoteException {
        throw new Exception("Not implemented!");
    }

    public int Sell(String bookName, int copies) throws RemoteException {
        throw new Exception("Not implemented!");
    }

    public static void main(String args[]) {

        try {
            // create the RMI registry on the local machine
            Registry registry = LocateRegistry.createRegistry(REGISTRY_PORT);

            // create an object we're going to call methods on remotely
            Server obj = new Server();

            // export the object in the registry so it can be retrieved by client,
            // casting to the Remote interface
            Store stub = (Store) UnicastRemoteObject.exportObject(obj, 0);

            // bind the remote object's stub in the registry
            registry.bind("Store", stub);

            System.err.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }

    }

}
