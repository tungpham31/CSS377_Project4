import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * An example remote interface for RMI. RMI interfaces must extend the base Remote interface.
 */
public interface Hello extends Remote {

  String sayHello() throws RemoteException;

}

