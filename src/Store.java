import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Store extends Remote {

    int Buy(String bookName, int copies) throws RemoteException;
    int Sell(String bookName, int copies) throws RemoteException;

}

