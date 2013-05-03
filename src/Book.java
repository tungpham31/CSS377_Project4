import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Book {
    private String bookName; // Name of the book
    private int copies; // The current number of copies that this book has
    private Queue<Long> waitingRequests; // A queue of the buying request waiting for this book. First in first out
    private Lock lock; // The lock associated with this book
    private Condition condVar; // The condition variable associated with this book
    
    public Book(){
        lock = new ReentrantLock();
        condVar = lock.newCondition();
    }
    
    public Book(String bookName, int copies){
        this.bookName = bookName;
        this.copies = copies;
        this.waitingRequests = new LinkedList<Long>();
        this.lock = new ReentrantLock();
        this.condVar = lock.newCondition();
    }
    
    // Get method for bookName
    public String getBookName(){
        return bookName;
    }
    
    // Set method for copies
    public void setCopies(int copies){
        this.copies = copies;
    }
    
    // Get method for copies 
    public int getCopies(){
        return copies;
    }
    
    // Get method for waitingRequests
    public Queue<Long> getWaitingRequests(){
        return waitingRequests;
    }
    
    // Add a new waiting request id to the queue
    public void addWaitingRequest(long requestId){
        waitingRequests.add(Long.valueOf(requestId));
    }
    
    // Get the id of the first waiting request in queue
    public long firstWaitingRequest(){
        return waitingRequests.peek().longValue();
    }
    
    // Remove from the queue the first waiting request of this book
    public long removeFirstWaitingRequest() throws Exception{
        if (waitingRequests.size() == 0){
            throw new Exception("Queue of waiting requests is empty");
        }
        
        Long first = waitingRequests.poll();
        return first.longValue();
    }
    
    // Remove a specific waiting request with a specific request id
    public boolean removeWaitingRequest(long requestId) throws Exception{
        if (waitingRequests.size() == 0){
            throw new Exception("Queue of waiting requests is empty");
        }
        
        return waitingRequests.remove(Long.valueOf(requestId));
    }
    
    // Get method for lock
    public Lock getLock(){
        return lock;
    }
    
    // Get method for condition variable
    public Condition getCondVar(){
        return condVar;
    }
}
