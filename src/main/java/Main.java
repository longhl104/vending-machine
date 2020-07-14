import exceptions.CancellationException;
import exceptions.TimeoutException;
import product.Product;

import java.util.*;
import java.util.concurrent.*;

public class Main {


    // static fields

    public static long timeoutSeconds = 5;

    // instance variables

    private static Main instance;
    private VendingMachine vendingMachine;
    private AdminSystem adminSystem;
  
    private HashSet<TransactionPair> selections;
    private static double cumulativePaid;


    // static methods

    /**
     * Sets the global input timeout
     *
     * @param timeout the new timeout value, in seconds
     */
    public static void setTimeout(long timeout) {
        timeoutSeconds = timeout;
    }

    /**
     * Reset interim quantities to their actual quantities for the next user. These quantities could differ if the
     * previous user selected an item but did not pay before timeout.
     *
     * @param selections User selections.
     */
    private static void resetQuantities(Collection<TransactionPair> selections) {
        for (TransactionPair tp : selections) {
            tp.getProduct().increaseQuantity(tp.getQuantity());
        }
    }

    /**
     * Main method.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {

        instance = new Main();
        instance.start();

    }


    // instance methods

    /**
     * Constructor.
     */
    public Main() {
        this.vendingMachine = new VendingMachine();
        this.adminSystem = new AdminSystem();
        this.adminSystem.setVendingMachine(vendingMachine);
        cumulativePaid = 0.0;
    }

    /**
     * Starts the application.
     */
    public void start() {

        System.out.println("==================\n\nWelcome to the Vending Machine!");

        selections = new HashSet<>();
        cumulativePaid = 0;

        while (true) {

            try {

                displayAvailable();
                run();

            } catch (TimeoutException e) {

                System.out.println("\n\n\n [!] Transaction cancelled due to user inactivity. [!]\n");
                reset();

            } catch (CancellationException e) {

                System.out.println("\n\n [!] Transaction cancelled by user. [!]\n");
                reset();

            }

        }

    }

    /**
     * Cleanup after transaction for next user.
     */
    private void reset() {

        // reset selections
        resetQuantities(selections);
        selections.clear();
        cumulativePaid = 0;

        // restart input method
        instance.start();

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
     * Manages user input.
     *
     * @param userInput The user input.
     */
    public void handleInput(String userInput) throws TimeoutException, CancellationException {

        String[] inputArray = userInput.split(" ");
        String command = inputArray[0];
        String[] arguments = Arrays.copyOfRange(inputArray, 1, inputArray.length);

        // handle user commands

        if (command.equalsIgnoreCase("HELP")) {

            if (inputArray.length > 1) {
                System.out.println("\nInvalid input. Type HELP for instructions.");
                return;
            }

            displayHelp();
            return;

        } else if (command.equalsIgnoreCase("CANCEL")) {

            if (inputArray.length > 1) {
                System.out.println("\nInvalid input. Type HELP for instructions.");
                return;
            }

            throw new CancellationException();

        } else if (command.equalsIgnoreCase("ADMIN")) {

            if (arguments.length != 1) {
                System.out.println("\nInvalid input. Type HELP for instructions.");
                return;
            }

            handleAdminEntry(arguments);
            return;

        } else if (command.equalsIgnoreCase("FILL")) {

            if (arguments.length != 2) {
                System.out.println("\nInvalid input. Type HELP for instructions.");
                return;
            }

            String productId = arguments[0];
            String adminId = arguments[1];

            if (!adminSystem.availableAdminId(adminId)) {
                System.out.printf("\nAdmin id \"%s\" does not exist in the system!\n", adminId);
                return;
            }

            System.out.println("\nAdmin identity authenticated. Refilling...");
            adminSystem.fill(productId);
            return;


        } else if (command.equalsIgnoreCase("QUIT")) {

            if (inputArray.length > 1) {
                System.out.println("\nInvalid input. Type HELP for instructions.");
                return;
            }

            System.out.println("\nExiting system. Have a nice day! :)");
            System.exit(0);

        }

        // perform transaction
        // selection -> payment -> retrieval

        // select product

        if (!command.equalsIgnoreCase("end")) {

            Product selection = select(userInput);
            if (selection == null) {
                return;
            }

            TransactionPair newPair = selectMultiple(selection);
            Optional<TransactionPair> optional = selections.stream().filter(s -> s.getProduct().equals(newPair.getProduct())).findFirst();

            if (optional.isPresent()) {
                optional.get().increaseQuantity(newPair.getQuantity());
            } else {
                selections.add(newPair);
            }

            System.out.println("\nYou have selected:");
            System.out.print(vendingMachine.displaySelections(selections));

            return;

        }

        // ensure user has selected something
        if (selections.isEmpty()) {
            System.out.println("\nNo items have been selected for purchase. Please try again.");
            return;
        }

        // below will run only when user inputs "end"
        double grandTotal = vendingMachine.grandTotal(selections);
        System.out.println(String.format("\nGrand total is $%.2f - Please insert money:\n", grandTotal));

        // process payment

        while (true) {

            String input = awaitInput();
            if (input == null) {
                throw new TimeoutException();
            }

            if (payment(grandTotal, input)) {
                break;
            }

        }

        // receive products (there has to be a better way to do this instead of doubly withdrawing every item)
        resetQuantities(selections);
        receiveProducts(selections);

        System.out.println("Thank you for your purchase!\n");

        // transaction finished - reset for next customer
        instance.start();

    }

    /**
     * Attempts to fetch the Product from the data store.
     *
     * @param input The name or ID of the desired Product.
     * @return the Product if available, otherwise null.
     */
    public Product select(String input) {

        Product selection = vendingMachine.getProduct(input);

        if (selection == null) {
            System.out.println("\nInvalid selection.");
        } else if (selection.getQuantity() < 1) {
            System.out.printf("%s is out of stock.\n", selection.getName());
            return null;
        }

        return selection;

    }

    /**
     * Allow user to select multiple of the same product.
     *
     * @param selection The Product selected.
     * @return A TransactionPair of the Product and its selected quantity.
     * @throws TimeoutException Exception thrown after 30 seconds of inactivity.
     * @throws CancellationException Exception thrown when user cancels transaction.
     */
    private TransactionPair selectMultiple(Product selection) throws TimeoutException, CancellationException {

        System.out.println(String.format("\nYou have selected %s. There are %d item(s) in stock. How many would you like " +
                "to purchase (Type a number)?\n", selection.getName(), selection.getQuantity()));

        // determine qty of item
        int quantity = 1;

        boolean quantitySuccess = false;
        while (!quantitySuccess) {

            String quantityInput = awaitInput();
            if (quantityInput == null) {
                throw new TimeoutException();
            } else if (quantityInput.equalsIgnoreCase("CANCEL")) {
                throw new CancellationException();
            }

            try {

                quantity = Integer.parseInt(quantityInput);
                if (quantity <= 0) {

                    System.out.println("\nInvalid input. Please enter a positive, non-zero number.\n");
                    continue;
                }
                if (quantity > selection.getQuantity()) {

                    System.out.println("\nNot enough stock. Please enter a smaller number.\n");
                    continue;
                }
                quantitySuccess = true;

            } catch (NumberFormatException e) {

                System.out.println("\nInvalid input. Please enter a numerical value.\n");
            }

        }

        TransactionPair tp = new TransactionPair(selection, quantity);
        vendingMachine.dispenseItems(Collections.singletonList(tp));

        return tp;

    }

    /**
     * Handles user payment.
     *
     * @param grandTotal The total price to pay for all products.
     * @return Whether or not the user successfully paid for the transaction.
     */

    public boolean payment(double grandTotal, String input) throws CancellationException {

        // try to parse numerical amount
        double insert;
        try {
            insert = Double.parseDouble(input);

        } catch (NumberFormatException e) {

            // cancellation by user
            if (input.equalsIgnoreCase("CANCEL")) {

                System.out.println();
                dispenseChange(cumulativePaid, 0);

                throw new CancellationException();
            }
            System.out.println("\nInvalid input. Please insert money:\n");
            return false;
        }

        // ensuring inserted money is accepted
        if (!acceptMoney(insert)) {
            return false;
        } else {
            cumulativePaid += insert;
        }

        // then ensure amount is sufficient for purchase
        if (cumulativePaid < grandTotal) {
            System.out.println(String.format("\nInsufficient funds. You have paid $%.2f so far. Owing $%.2f.", cumulativePaid, grandTotal - cumulativePaid));
            System.out.println("Please insert more money or type 'CANCEL' to cancel transaction:\n");
            return false;
        }

        System.out.println("\nPayment successful.");
        dispenseChange(cumulativePaid, grandTotal);
        return true;

    }

    /**
     * Gives the Product to the user after successful payment.
     *
     * @param purchased The list of products that has been purchased.
     */
    public void receiveProducts(Collection<TransactionPair> purchased) {
        System.out.println("\nYou have purchased:");
        System.out.println(vendingMachine.displaySelections(purchased));
        vendingMachine.dispenseItems(purchased);
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
            return future.get(timeoutSeconds, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | java.util.concurrent.TimeoutException e) {
            return null;
        }

    }

    /**
     * Dispenses change to the user.
     *
     * @param paid The amount the user has paid.
     * @param total The total price of the purchase.
     */
    private void dispenseChange(double paid, double total) {

        double change = paid - total;

        if (change != 0) {
            System.out.println(String.format("Please don't forget to take your change: $%.2f", change));
        }

    }


    /**
     * Displays the user help menu.
     */
    private void displayHelp() {

        List<String> help = List.of(
                "\n[product id] - Select a product.",
                "[product name] - Select a product.",
                "HELP - Display this help dialog.");
        help.forEach(System.out::println);

    }

    /**
     * Displays available products to the user.
     */
    private void displayAvailable() {

        List<String> welcome = List.of(
                vendingMachine.displayProducts(false),
                "Please select a product. Type 'END' to proceed to payment. Type 'CANCEL' to cancel transaction. Type 'HELP' for instructions.\n");
        welcome.forEach(System.out::println);

    }

    /**
     * Checks if inserted money is accepted by the machine.
     *
     * @param paid inserted coin/note.
     * @return weather the coin/note is accepted.
     */
    private boolean acceptMoney(double paid) {

        double[] validValue = {0.1, 0.2, 0.5, 1.0, 2.0, 5.0, 10.0, 20.0};

        for (double v : validValue) {

            if (paid == v) {
                return true;
            }
        }

        System.out.println("\nInvalid input.\n" +
                "\nThe Vending Machine accepts:\n" +
                "$0.10  $0.20  $0.50  $1.00  $2.00  $5.00  $10.00  $20.00\n");

        return false;


    }

    /**
     * Entry to the admin system.
     *
     * @param arguments
     */
    private void handleAdminEntry(String[] arguments) {

        if (!adminSystem.availableAdminId(arguments[0])) {
            System.out.printf("\nAdmin id \"%s\" does not exist in the system!\n", arguments[0]);
            return;
        }

        System.out.printf("\nWelcome Admin \"%s\" to the admin system!\n", arguments[0]);
        adminSystem.adminOperations();

    }

}

