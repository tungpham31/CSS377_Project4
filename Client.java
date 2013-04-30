import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

  public static void main(String[] args) {
    String host = (args.length < 1) ? null : args[0];
    try {

      // Get the registry from the specified host (defaults to localhost)
      // on the port we designed for the registry in Server.
      Registry registry = LocateRegistry.getRegistry(host, Server.REGISTRY_PORT);

      // Get an object by looking up the name it was bound to.
      Hello stub = (Hello) registry.lookup("Hello");

      // Now we can make remote method calls on the object.
      String response = stub.sayHello();

      System.out.println("response: " + response);
    } catch (Exception e) {
      System.err.println("Client exception: " + e.toString());
      e.printStackTrace();
    }
  }

}
