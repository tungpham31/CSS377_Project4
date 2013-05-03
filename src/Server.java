import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * An object implementing the Remote interface. This allows it to be called through RMI.
 */
public class Server implements Store {

    /**
     * This port will be assigned to your group for use on EC2. For local testing, you can use any
     * (nonstandard) port you wish.
     */
    public final static int REGISTRY_PORT = 27630;

    /**
     * The Map mapping from bookName to the according book object
     */
    private Map<String, Book> books = new HashMap<String, Book>();

    /**
     * A variable to keep track of the id of the lattest request
     */
    private long currentRequestId = -1;

    /**
     * A global lock for global database
     */
    private Lock globalLock = new ReentrantLock();

    /**
     * A variable to keep track of the execution time in server
     */
    private long totalTimeInServer = 0;

    public String sayHello() {
        return "Hello";
    }

    public int buy(String bookName, int copies) throws Exception {
        long start = System.currentTimeMillis();

        if (copies < 0) {
            throw new RemoteException("Copies must be non-negative.");
        }

        globalLock.lock(); // lock before accessing the global database - books
        Book book = books.get(bookName); // get the book with this bookName
        if (book == null) {
            // If there is no book like that in the books database, create a new one with 0 copies
            book = new Book(bookName, 0);
            books.put(bookName, book); // update books database
        }
        globalLock.unlock(); // release the lock after accessing the global database

        book.getLock().lock(); // lock before accessing this book information
        // Get the most number of copies possible to fulfill the request
        int available = book.getCopies();
        int bought = Math.min(copies, available);
        book.setCopies(available - bought);

        if (copies > bought) {
            // If have not bought enough copies, wait and buy more.
            // Create an id for this waiting request
            currentRequestId++;
            long requestId = currentRequestId;
            book.addWaitingRequest(requestId); // add the id of this request to the queue of
                                               // requests waiting for this book

            long waitingTimeLeft = 10 * 1000; // Define the waiting time left = 10s in millisecs

            while (true) {
                long timeStartWaiting = System.currentTimeMillis(); // in millisec
                try {
                    book.getCondVar().await(waitingTimeLeft, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // Update waiting time left right after thread continues from wait()
                waitingTimeLeft = waitingTimeLeft - System.currentTimeMillis() + timeStartWaiting;

                if (book.getCopies() > 0 && book.firstWaitingRequest() == requestId) {
                    // If there is available copy of this book and this request is the first in
                    // those requesting for the book, this buyer will take those
                    available = book.getCopies();
                    int buyMore = Math.min(copies - bought, available);
                    bought += buyMore;
                    book.setCopies(available - buyMore);

                    if (copies == bought) {
                        // If this buyer has bough enough, he will remove himself from request queue
                        // and then exit
                        book.removeFirstWaitingRequest();
                        // If there is more copies available, notify other buyers waiting for this
                        // book
                        if (book.getCopies() > 0)
                            book.getCondVar().signalAll();
                        break;
                    }
                }

                if (waitingTimeLeft <= 0) {
                    // If the waiting time for this thread is over then simply remove its request
                    // from request queue, then exit
                    book.removeWaitingRequest(requestId);
                    break;
                }

                // Otherwise, this thread will keep waiting for more inventory
            }
        }
        book.getLock().unlock(); // unlock this book after accessing and modifying its information

        totalTimeInServer += System.currentTimeMillis() - start;
        System.out.println("Time spent in server so far is: " + totalTimeInServer);
        return bought;
    }

    public int sell(String bookName, int copies) throws RemoteException {
        long start = System.currentTimeMillis();

        if (copies < 0) {
            throw new RemoteException("Copies must be non-negative.");
        }

        globalLock.lock(); // lock before accessing global database - books
        Book book = books.get(bookName); // get the book object with this particular name
        if (book == null) {
            // If there is no book like that in the books database, create a new one with 0 number
            // of copies
            book = new Book(bookName, 0);
            books.put(bookName, book); // update books database with the new book
        }
        globalLock.unlock(); // release global lock after accessing global database

        book.getLock().lock(); // lock this book before accessing and modifying its content
        int before = book.getCopies();
        book.setCopies(before + copies); // update number of copies of this book
        book.getCondVar().signalAll(); // signal all the buyers waiting for this book that there are
                                       // new copies
        book.getLock().unlock(); // release the lock after accessing and modifying the book's
                                 // content

        totalTimeInServer += System.currentTimeMillis() - start;
        System.out.println("Time spent in server so far is: " + totalTimeInServer);
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
