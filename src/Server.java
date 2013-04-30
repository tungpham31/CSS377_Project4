import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

/**
 * An object implementing the Remote interface. This allows it to be
 * called through RMI.
 */
public class Server implements Store {

    /**
     * This port will be assigned to your group for use on EC2. For local testing, you can use any (nonstandard) port you wish.
     */
    public final static int REGISTRY_PORT = 53824;

    private Map<String, Integer> books = new HashMap<String, Integer>();

    public int Buy(String bookName, int copies) throws RemoteException {
        if (copies < 0) { throw new RemoteException("Copies must be non-negative."); }

        if (!books.containsKey(bookName)) {
            books.put(bookName, 0);
        }

        int available = books.get(bookName);
        int bought = Math.min(copies, available);
        books.put(bookName, available - bought);

        if (copies > bought) {
            // TODO: Wait and buy more.
        }

        return bought;
    }

    public int Sell(String bookName, int copies) throws RemoteException {
        if (copies < 0) { throw new RemoteException("Copies must be non-negative."); }

        if (!books.containsKey(bookName)) {
            books.put(bookName, 0);
        }

        int before = books.get(bookName);
        books.put(bookName, before + copies);
        return before;
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
