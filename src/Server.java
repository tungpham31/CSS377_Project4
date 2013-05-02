import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * An object implementing the Remote interface. This allows it to be called through RMI.
 */
public class Server implements Store {

    /**
     * This port will be assigned to your group for use on EC2. For local testing, you can use any
     * (nonstandard) port you wish.
     */
    public final static int REGISTRY_PORT = 53824;

    /**
     * The set of all the available books along with its number of copies
     */
    private Map<String, Integer> books = new HashMap<String, Integer>();

    /**
     * The Map that map each book to its queue of waiting request. Requests in each queue will be
     * first come first serve. This database is used to track which buying request should be served
     * first when there is an inventory available
     */
    private Map<String, Queue<Long>> requestRank = new HashMap<String, Queue<Long>>();

    /**
     * A variable to keep track of the id of the lattest request
     */
    private long currentRequestId = -1;
    
    public String sayHello(){
        return "Hello";
    }

    public synchronized int buy(String bookName, int copies) throws RemoteException {
        if (copies < 0) {
            throw new RemoteException("Copies must be non-negative.");
        }

        // If there is no book like that in the books database, add it in with 0 number of copies
        // and create a new entry for it in requestRank
        if (!books.containsKey(bookName)) {
            books.put(bookName, 0);
            requestRank.put(bookName, new LinkedList<Long>());
        }

        // Get the most number of copies possible
        int available = books.get(bookName);
        int bought = Math.min(copies, available);
        books.put(bookName, available - bought);

        // If have not bought enough copies
        if (copies > bought) {
            // TODO: Wait and buy more.
            // Create an id for this waiting request
            currentRequestId++;
            long requestId = currentRequestId;
            // Add this waitingRequest to requestRank
            requestRank.get(bookName).add(Long.valueOf(requestId));

            // Define the waiting time left = 10s
            long waitingTimeLeft = 10 * 1000; // in milliseconds

            while (true) {
                long timeStartWaiting = System.currentTimeMillis(); // in millisec
                try {
                    wait(waitingTimeLeft);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // Update waiting time left right after thread continues from wait()
                waitingTimeLeft = waitingTimeLeft - System.currentTimeMillis() + timeStartWaiting;

                if (books.get(bookName) > 0 && requestRank.get(bookName).peek() == requestId) {
                    // If there is available copy of this book and this request is the first in
                    // those requesting for the book, this buyer will take those
                    available = books.get(bookName);
                    int buyMore = Math.min(copies - bought, available);
                    bought += buyMore;
                    books.put(bookName, available - buyMore);
                    
                    if (copies == bought){
                        // If this buyer has bough enough, he will remove himself from request queue and then exit
                        requestRank.get(bookName).poll(); 
                        // If there is more copies available, notify other buyer waiting for this book
                        notifyAll();
                        break;
                    }
                }
                
                if (waitingTimeLeft <= 0){
                    // If the waiting time for this thread is over then simply remove its request from request queue, then exit
                    requestRank.get(bookName).remove(Long.valueOf(requestId));
                    break;
                }
                
                // Otherwise, this thread will keep waiting for more inventory
            }
        }

        return bought;
    }

    public synchronized int sell(String bookName, int copies) throws RemoteException {
        if (copies < 0) {
            throw new RemoteException("Copies must be non-negative.");
        }

        // If there is no book like that in the books database, add it in with 0 number of copies
        // and create a new entry for it in requestRank
        if (!books.containsKey(bookName)) {
            books.put(bookName, 0);
            requestRank.put(bookName, new LinkedList<Long>());
        }

        int before = books.get(bookName);
        books.put(bookName, before + copies); //update books database with new copies of this book
        notifyAll(); // notify all the buyer waiting for this books
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
