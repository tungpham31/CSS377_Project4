import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        String host = (args.length < 1) ? null : args[0];
        try {

            // Get the registry from the specified host (defaults to localhost)
            // on the port we designed for the registry in Server.
            Registry registry = LocateRegistry.getRegistry(host, Server.REGISTRY_PORT);

            // Get an object by looking up the name it was bound to.
            Store store = (Store) registry.lookup("Store");

            Scanner sc = new Scanner(System.in);
            while (true) {
                System.out.print("What do you want (E-Escape, S-Sell, B-Buy): ");
                while (!sc.hasNext()){}
                char command = sc.next().charAt(0);
                System.out.println(command);

                if (command == 'E' || command == 'e')
                    break;

                System.out.print("book name and number of copies: ");
                while (!sc.hasNext()){}
                String bookName = sc.next();
                int copies = sc.nextInt();
                System.out.println("book name = " + bookName + " copies = " + copies);

                if (command == 'S' || command == 's') {
                    System.out.println(store.sell(bookName, copies));
                }

                if (command == 'B' || command == 'b')
                    System.out.println(store.buy(bookName, copies));
            }

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

}
