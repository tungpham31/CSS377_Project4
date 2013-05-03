import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Store extends Remote {

    int buy(String bookName, int copies) throws RemoteException, Exception;
    int sell(String bookName, int copies) throws RemoteException;
    String sayHello() throws RemoteException;
}

