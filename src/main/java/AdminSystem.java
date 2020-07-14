import exceptions.CancellationException;
import exceptions.TimeoutException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

public class AdminSystem {

    /**
     * Store admin IDs in a set of string
     */
    private Set<String> adminIDs;
    private VendingMachine vendingMachine;

    /**
     * Constructor.
     */
    public AdminSystem() {
        adminIDs = new HashSet<>();
        adminIDs.add("admin");
    }

    /**
     * Add an admin ID to the system.
     *
     * @param id new admin ID
     * @return true if the admin ID currently doesn't exist.
     * false otherwise.
     */
    public boolean addAdminId(String id) {

        if (adminIDs.add(id)) {
            System.out.printf("\nAdmin id \"%s\" has been successfully added to the system!\n", id);
            return true;
        } else {
            System.out.printf("\nAdmin id \"%s\" has been already stored in the system!\n", id);
            return false;
        }

    }

    /**
     * Remove an admin Id from the system.
     *
     * @param id Unwanted admin Id
     */
    public boolean removeAdminId(String id) {

        if (adminIDs.remove(id)) {
            System.out.printf("\nAdmin id \"%s\" has been successfully removed from the system!\n", id);
            return true;
        } else {
            System.out.printf("\nAdmin id \"%s\" does not exist in the system!\n", id);
            return false;
        }

    }

    /**
     * Display all products.
     */
    public void displayProducts() {
        System.out.println(vendingMachine.displayProducts(true));
    }

    /**
     * @return the set of admin IDs
     */
    public Set<String> getAdminIDs() {
        return adminIDs;
    }

    public boolean availableAdminId(String id) {
        return adminIDs.contains(id);
    }

    public void adminOperations() {

        while (true) {

            try {
                run();
            } catch (TimeoutException e) {

                System.out.println("\n\n\n [!] Transaction cancelled due to user inactivity. [!]\n");
                return;

            } catch (CancellationException e) {

                System.out.println("\n\nYou are exiting admin mode\n===========================\n");
                return;

            }

        }

    }

    /**
     * Driver function, attempts to read and interpret user input.
     */
    public void run() throws TimeoutException, CancellationException {

        String input = awaitInput();
        if (input == null) {
            throw new TimeoutException();
        }

        handleInput(input);

    }

    /**
     * Handle admin input.
     * @param userInput The user input.
     * @throws TimeoutException Exception after 30 seconds of inactivity.
     * @throws CancellationException Exception when admin types "END" to exit admin mode.
     */
    public void handleInput(String userInput) throws TimeoutException, CancellationException {
        String[] inputArray = userInput.split(" ");
        String command = inputArray[0];
        String[] arguments = Arrays.copyOfRange(inputArray, 1, inputArray.length);

        if (command.equalsIgnoreCase("AVAILABLE") && arguments.length == 0) {
            displayProducts();

        } else if (command.equalsIgnoreCase("ADDADMIN") && arguments.length == 1) {
            addAdminId(arguments[0]);

        } else if (command.equalsIgnoreCase("REMOVEADMIN") && arguments.length == 1) {
            removeAdminId(arguments[0]);

        } else if (command.equalsIgnoreCase("FILL") && arguments.length == 1) {
            fill(arguments[0]);

        } else if (command.equalsIgnoreCase("END")) {
            throw new CancellationException();
        }

    }

    /**
     * Restocks the specified product.
     *
     * @param product The name or ID of the product.
     */
    public void fill (String product) {
        boolean restocked = vendingMachine.fill(product);
        if (restocked) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            System.out.println(String.format("Product %s successfully restocked at %s\n", product, dtf.format(now)));
        } else {
            System.out.println(String.format("%s is not a valid product or product ID. Restock failed.\n", product));
          
        }
    }

    /**
     * Internal method for input retrieval.
     *
     * @return a String if the user enters any input, otherwise null.
     */
    private String awaitInput() {

        System.out.print("> ");
        Scanner scanner = new Scanner(System.in);

        ExecutorService executor = Executors.newCachedThreadPool();
        Callable<String> task = scanner::nextLine;

        Future<String> future = executor.submit(task);

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }

    }

    public void setVendingMachine(VendingMachine vendingMachine) {
        this.vendingMachine = vendingMachine;
    }

}
