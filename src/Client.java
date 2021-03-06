import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client {
    private static Store store;
    private static long totalTime = 0;

    public static void main(String[] args) throws Exception {
        String host = (args.length < 1) ? null : args[0];
        try {

            // Get the registry from the specified host (defaults to localhost)
            // on the port we designed for the registry in Server.
            Registry registry = LocateRegistry.getRegistry(host, Server.REGISTRY_PORT);

            // Get an object by looking up the name it was bound to.
            store = (Store) registry.lookup("Store");

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }

        TestCase1(); // you can change this to different test case 1, 2, 3, or others
    }

    /**
     * A test using interactive console
     * @throws Exception
     */
    private static void TestCase1() throws Exception {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print("What do you want (E-Escape, S-Sell, B-Buy): ");
            while (!sc.hasNext()) {
            }
            char command = sc.next().charAt(0);
            System.out.println(command);

            if (command == 'E' || command == 'e')
                break;

            System.out.print("book name and number of copies: ");
            while (!sc.hasNext()) {
            }
            String bookName = sc.next();
            int copies = sc.nextInt();
            System.out.println("book name = " + bookName + " copies = " + copies);

            if (command == 'S' || command == 's') {
                System.out.println("Selling: " + store.sell(bookName, copies));
            }

            if (command == 'B' || command == 'b')
                System.out.println("Buying: " + store.buy(bookName, copies));
        }
    }

    /**
     * Create 50 buying request for Davinci Code, which request requires 2 copies But then there is
     * only 1 selling request with only 10 copies At last there is another selling request with 0
     * copies simply to check if the number of copies is correct
     * @throws RemoteException
     */
    private static void TestCase2() throws RemoteException {
        for (int i = 1; i <= 50; i++) {
            new Thread() {
                public void run() {
                    try {
                        System.out.println("Bought = " + store.buy("Davinci Code", 2));
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        System.out.println("Copies before = " + store.sell("Davinci Code", 10));
        System.out.println("Copies before = " + store.sell("Davinci Code", 0));
    }

    /**
     * Simply 1000 buying request waiting for server
     */
    private static void TestCase3() {
        for (int i = 1; i <= 1000; i++) {
            new Thread() {
                public void run() {
                    try {
                        System.out.println("Bought = " + store.buy("Davinci Code", 100));
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    /*
     * First, there are 20 concurrent buying request for book A, each buy 5 Then, there are 10
     * concurrent selling request for book A, each sell 5
     */
    private static void TestCase4() {
        for (int i = 1; i <= 20; i++) {
            new Thread() {
                public void run() {
                    try {
                        System.out.println("Bought = " + store.buy("A", 5));
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        for (int i = 1; i <= 10; i++) {
            new Thread() {
                public void run() {
                    try {
                        System.out.println("Copies before = " + store.sell("A", 5));
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    /*
     * First, there are 20 concurrent buying request for book A, each buy 5. Then, there are 10
     * concurrent selling request for book A, each sell 5 Then sleep 5 secs before one last selling
     * request sells 100 copies of book A
     */
    private static void TestCase5() throws RemoteException {
        for (int i = 1; i <= 20; i++) {
            new Thread() {
                public void run() {
                    try {
                        System.out.println("Bought = " + store.buy("A", 5));
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        for (int i = 1; i <= 10; i++) {
            new Thread() {
                public void run() {
                    try {
                        System.out.println("Copies before = " + store.sell("A", 5));
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        new Thread() {
            public void run() {
                try {
                    sleep(5000);
                    System.out.println("Copies before = " + store.sell("A", 100));
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * A sell request run and then a buy request
     */
    private static void TestCase6() {
        new Thread() {
            public void run() {
                try {
                    long startTime = System.currentTimeMillis();
                    store.sell("Home", 1000);
                    System.out.println("End-to-end latency of this selling request = "
                            + (System.currentTimeMillis() - startTime));
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }.start();

        new Thread() {
            public void run() {
                try {
                    long startTime = System.currentTimeMillis();
                    store.buy("Home", 999);
                    System.out.println("End-to-end latency of this buying request = "
                            + (System.currentTimeMillis() - startTime));
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * Run 10 concurrent sell requests Then 10 concurrent buy requests
     */
    private static void TestCase7() {
        for (int i = 1; i <= 10; i++) {
            new Thread() {
                public void run() {
                    try {
                        long startTime = System.currentTimeMillis();
                        store.sell("Home", 1000);
                        System.out.println("End-to-end latency for sell request is: "
                                + (System.currentTimeMillis() - startTime));
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        for (int i = 1; i <= 10; i++) {
            new Thread() {
                public void run() {
                    try {
                        long startTime = System.currentTimeMillis();
                        store.buy("Home", 999);
                        System.out.println("End-to-end latency for buy request is: "
                                + (System.currentTimeMillis() - startTime));

                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    /**
     * Run 50 concurrent sell requests Then 50 concurrent buy requests
     */
    private static void TestCase8() {

        for (int i = 1; i <= 50; i++) {
            new Thread() {
                public void run() {
                    try {
                        long startTime = System.currentTimeMillis();
                        store.sell("Home", 1000);
                        long endTime = System.currentTimeMillis();
                        System.out.println("End-to-end latency for sell request is: "
                                + (endTime - startTime));
                        totalTime += (endTime - startTime);
                        System.out.println("Total End-to-end latency so far is: " + totalTime);

                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        for (int i = 1; i <= 50; i++) {
            new Thread() {
                public void run() {
                    try {
                        long startTime = System.currentTimeMillis();
                        store.buy("Home", 999);
                        long endTime = System.currentTimeMillis();
                        System.out.println("End-to-end latency for buy request is: "
                                + (endTime - startTime));
                        totalTime += endTime - startTime;
                        System.out.println("Total End-to-end latency so far is: " + totalTime);

                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    /**
     * Run 100 concurrent sell requests Then 100 concurrent buy requests
     */
    private static void TestCase9() {

        for (int i = 1; i <= 100; i++) {
            new Thread() {
                public void run() {
                    try {
                        long startTime = System.currentTimeMillis();
                        store.sell("Home", 1000);
                        long endTime = System.currentTimeMillis();
                        System.out.println("End-to-end latency for sell request is: "
                                + (endTime - startTime));
                        totalTime += (endTime - startTime);
                        System.out.println("Total End-to-end latency so far is: " + totalTime);

                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        for (int i = 1; i <= 100; i++) {
            new Thread() {
                public void run() {
                    try {
                        long startTime = System.currentTimeMillis();
                        store.buy("Home", 999);
                        long endTime = System.currentTimeMillis();
                        System.out.println("End-to-end latency for buy request is: "
                                + (endTime - startTime));
                        totalTime += endTime - startTime;
                        System.out.println("Total End-to-end latency so far is: " + totalTime);

                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    /**
     * 10 buy requests for Davinci Code. 10 buy requests for Sherlock Homes. 5 sell requests for
     * Davinci Code only. 5s later, 10 sell requests for Sherlock Homes.
     */
    private static void TestCase10() {
        for (int i = 1; i <= 10; i++) {
            new Thread() {
                public void run() {
                    try {
                        System.out.println("Bought Davinci Code = " + store.buy("Davinci Code", 3));
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        for (int i = 1; i <= 10; i++) {
            new Thread() {
                public void run() {
                    try {
                        System.out.println("Bought Sherlock Homes = "
                                + store.buy("Sherlock Homes", 2));
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        for (int i = 1; i <= 5; i++) {
            new Thread() {
                public void run() {
                    try {
                        System.out.println("Sell Davinci Code = " + store.sell("Davinci Code", 2));
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        for (int i = 1; i <= 10; i++) {
            new Thread() {
                public void run() {
                    try {
                        sleep(5000);
                        System.out.println("Sell Sherlock Homes = "
                                + store.sell("Sherlock Homes", 1));
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }
}
