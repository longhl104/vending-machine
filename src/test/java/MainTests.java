
import exceptions.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.contrib.java.lang.system.TextFromStandardInputStream;
import product.Category;
import product.Product;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.junit.contrib.java.lang.system.TextFromStandardInputStream.emptyStandardInputStream;

public class MainTests {

    private Main instance;
    private final InputStream stdin = System.in;

    @Rule public final TextFromStandardInputStream systemIn = emptyStandardInputStream();
    @Rule public final SystemOutRule systemOut = new SystemOutRule().enableLog();
    @Rule public final ExpectedSystemExit exit = ExpectedSystemExit.none();



    /**
     * Creating an instance of the main class, and setting the default timeout value.
     */
    @Before
    public void setup() {
        this.instance = new Main();
        Main.setTimeout(30);
    }

    /**
     * Testing if select() returns the product, provided correct name in lower case.
     * We expect a product with the name "Original".
     * Test passes because "original" is a valid input and when passed to the Vending Machine's getProduct()
     * it uses equalsIgnoreCase() to find the product.
     */
    @Test
    public void select_validLowercaseTest() {

        assertNotNull(instance.select("original"));
        assertEquals("Original", instance.select("original").getName());

    }

    /**
     * Testing if select() returns the product, provided correct name in upper case.
     * We expect a product with the name "Original".
     * Test passes because "original" is a valid input and when passed to the Vending Machine's getProduct()
     * it uses equalsIgnoreCase() to find the product.
     */
    @Test
    public void select_validUppercaseTest() {

        assertNotNull(instance.select("ORIGINAL"));
        assertEquals("Original", instance.select("original").getName());

    }

    /**
     * Testing if select() returns the product, provided correct name in mixed case.
     * We expect a product with the name "Original".
     * Test passes because "original" is a valid input and when passed to the Vending Machine's getProduct()
     * it uses equalsIgnoreCase() to find the product.
     */
    @Test
    public void select_validMixedcaseTest() {

        assertNotNull(instance.select("OrIgInAl"));
        assertEquals("Original", instance.select("original").getName());

    }

    /**
     * Testing if select() returns null, provided invalid name.
     * We expect a null returned.
     * Test passes because "nonexistent-product" is an invalid input and when passed to the Vending Machine's getProduct()
     * it doesn't match any of te existing products and returns false, resulting in null returned from select();
     */
    @Test
    public void select_invalidTest() {

        assertNull(instance.select("nonexistent-product"));

    }

    /**
     * Testing if select() returns null, if the product is out of stock.
     * We expect a null returned and "Original is out of stock." message displayed.
     * Test passes because we have removed the two chips from the machine and select() then checked if quantity
     * of the product is 0 and returned null and hence, the message is printed.
     */
    @Test
    public void select_outOfStockTest() {

        String correctOut = "Original is out of stock.";

        // remove the two bags of chips from the machine.
        ArrayList<TransactionPair> tp = new ArrayList<>();
        tp.add(new TransactionPair(instance.select("original"), 2));
        instance.receiveProducts(tp);

        assertNull(instance.select("original"));

        // only interested in the last thing printed
        String[] log = systemOut.getLog().split("\n");

        assertEquals(correctOut, normalize(log[log.length - 1]));

    }

    /**
     * Testing if payment() is successful, provided amount >= product's price.
     * We expect true returned.
     * Test passes because because the string provided could be converted to double.
     * The double was exactly 4.0, hence payment() returned true.
     */
    @Test
    public void payment_validCorrectTest() {

        try {
            assertTrue(instance.payment(5, "5.0"));
        } catch (CancellationException e) {
            fail();
        }

    }

    /**
     * Testing if payment() is successful, provided amount >= product's price.
     * We expect true returned.
     * Test passes because because the string provided could be converted to double.
     * The double was grater then 4.0, hence payment() returned true.
     */
    @Test
    public void payment_validMoreTest() {

        try {
            assertTrue(instance.payment(4, "5.0"));
        } catch (CancellationException e) {
            fail();
        }

    }

    /**
     * Testing if payment() is unsuccessful, provided amount < product's price.
     * We expect false returned.
     * Test passes because because the string provided could be converted to double.
     * The double was less than 4.0, hence payment() returned false.
     */
    @Test
    public void payment_invalidLowerTest() {

        try {
            assertFalse(instance.payment(4, "2"));
        } catch (CancellationException e) {
            fail();
        }

    }

    /**
     * Testing if payment() is unsuccessful, provided an invalid coin.
     * We expect false returned.
     * Test passes because because the string provided could be converted to double.
     * The double was 3.0, which is not a valid coin, hence payment() returned false.
     */
    @Test
    public void payment_invalidCoinTest() {

        try {
            assertFalse(instance.payment(3, "3"));
        } catch (CancellationException e) {
            fail();
        }

    }

    /**
     * Testing if payment() is unsuccessful, provided amount < product's price.
     * We expect false returned.
     * Test passes because because the string provided could not be converted to double.
     * Hence payment() returned false.
     */
    @Test
    public void payment_invalidNotDoubleTest() {

        try {
            assertFalse(instance.payment(4, "i have no money"));
        } catch (CancellationException e) {
            fail();
        }

    }

    /**
     * Testing if payment() is successful when attempting to buy multiples of an item with a non-numerical input.
     * We expect false returned.
     * Test passes because because the string provided could not be converted to double.
     * Hence payment() returned false.
     */
    @Test
    public void payment_invalidNotDoubleMultipleTest() {

        try {
            assertFalse(instance.payment(4, "give me the snacks"));
        } catch (CancellationException e) {
            fail();
        }

    }

    /**
     * Testing if payment is cancelled if user provides "cancel".
     * We expect false returned.
     * Test passes because because the string provided could not be converted to double and matched to "CANCEL".
     * Hence payment was cancelled and the method returned false.
     */
    @Test (expected = CancellationException.class)
    public void payment_cancelTest() throws CancellationException {

            assertFalse(instance.payment(4, "cancel"));

    }

    /**
     * Testing if receiveProduct() prints the correct information when dispensing one item.
     * We except a String output "\nYou have received 1 Original.\n"
     * Test passes because the method simply prints a String, formatted with the name
     * of the product passed and the quantity passed.
     */
    @Test
    public void receiveProduct_displaySingularTest() {

        String correctOut = "\nYou have purchased:\n" +
                "[ID 0] Original - quantity 1 @ $5.00 each = total $5.00\n\n";

        TransactionPair tp = new TransactionPair(new Product(0, "Original", 5.00, 1, Category.CHIPS), 1);
        instance.receiveProducts(Collections.singletonList(tp));
        assertEquals(correctOut, normalize(systemOut.getLog()));

    }

    /**
     * Testing if receiveProduct() prints the correct information when dispensing more than one item.
     * We except a String output ""\nYou have received 8 Original.\n""
     * Test passes because the method simply prints a String, formatted with the name
     * of the product passed and the quantity passed.
     */
    @Test
    public void receiveProduct_displayMultipleTest() {

        String correctOut = "\nYou have purchased:\n" +
                "[ID 0] Original - quantity 8 @ $5.00 each = total $40.00\n\n";

        TransactionPair tp = new TransactionPair(new Product(0, "Original", 5.00, 1, Category.CHIPS), 8);
        instance.receiveProducts(Collections.singletonList(tp));
        assertEquals(correctOut, normalize(systemOut.getLog()));

    }

    /**
     * Testing if receiveProduct() calls the correct methods to reduce the quantity of purchased items by 0.
     * We expect that the quantity of the item will not decrease.
     * Test passes because recieveProduct() calls VendingMachine#dispenseItems, which in turn calls
     * Product#reduceQuantity with the value passed as quantity, which then subtracts the value from the product quantity.
     * As a value - 0 is the value itself, the quantity does not change.
     */
    @Test
    public void receiveProduct_dispenseNoneTest() {

        Product selection = new Product(0, "Original", 5.00, 10, Category.CHIPS);

        assertEquals(10, selection.getQuantity());
        TransactionPair tp = new TransactionPair(selection, 0);
        instance.receiveProducts(Collections.singletonList(tp));
        assertEquals(10, selection.getQuantity());

    }

    /**
     * Testing if receiveProduct() calls the correct methods to reduce the quantity of purchased items by 1.
     * We expect that the quantity of the item will decrease by 1.
     * Test passes because recieveProduct() calls VendingMachine#dispenseItems, which in turn calls
     * Product#reduceQuantity with the value passed as quantity, which then subtracts the value from the product quantity.
     */
    @Test
    public void receiveProduct_dispenseSingularTest() {

        Product selection = new Product(0, "Original", 5.00, 10, Category.CHIPS);

        assertEquals(10, selection.getQuantity());
        TransactionPair tp = new TransactionPair(selection, 1);
        instance.receiveProducts(Collections.singletonList(tp));
        assertEquals(9, selection.getQuantity());

    }

    /**
     * Testing if receiveProduct() calls the correct methods to reduce the quantity of purchased items by more than 1.
     * We expect that the quantity of the item will decrease by 6.
     * Test passes because recieveProduct() calls VendingMachine#dispenseItems, which in turn calls
     * Product#reduceQuantity with the value passed as quantity, which then subtracts the value from the product quantity.
     */
    @Test
    public void receiveProduct_dispenseMultipleTest() {

        Product selection = new Product(0, "Original", 5.00, 10, Category.CHIPS);

        assertEquals(10, selection.getQuantity());
        TransactionPair tp = new TransactionPair(selection, 6);
        instance.receiveProducts(Collections.singletonList(tp));
        assertEquals(4, selection.getQuantity());

    }

    /**
     * Testing if dispenseChange() prints out a correct message, provided a transaction pair and input.
     * We expect the String "\nPayment successful.\nPlease don't forget to take your change: $2.00\n" to be printed out.
     * Test passes because dispenseChange() calculates the change subtracting total from paid amount,
     * we provided $10 as an input, the total was $8, hence the change was $2.
     */
    @Test
    public void dispenseChange_validTest() {

        String correctOut = "\nPayment successful.\nPlease don't forget to take your change: $2.00\n";

        try {
            instance.payment(8, "10");
        } catch (CancellationException e) {
            fail();
        }

        assertEquals(correctOut, normalize(systemOut.getLog()));

    }

    /**
     * Testing if dispenseChange() does not indicate change, provided a transaction pair and exact amount.
     * We expect the String "\nPayment successful.\n"
     * Test passes because dispenseChange() calculates the change subtracting total from paid amount,
     * we provided $8 as an input, the total was $8, hence the machine did not dispense any change.
     */
    @Test
    public void dispenseChange_noChangeTest() {

        String correctOut = "\nPayment successful.\n";

        try {
            instance.payment(2, "2");
        } catch (CancellationException e) {
            fail();
        }

        assertEquals(correctOut, normalize(systemOut.getLog()));

    }

    /**
     * Testing if handleInput() prints the correct message when specifying the HELP command.
     * We expect a String output "\n[product id] - Select a product.\nHELP - Display this help dialog."
     * <p>
     * Test passes because "HELP" is a valid command. The method first uses String.toUpperCase() on the input,
     * then attempts to match valid commands before passing all other input to the transaction method.
     * As "HELP" is a command, the method will print the help dialog instead of attempting to find a product.
     */
    @Test
    public void handleInput_helpTest() {

        String correctOut =
                "\n[product id] - Select a product.\n" +
                        "[product name] - Select a product.\n" +
                        "HELP - Display this help dialog.\n";

        try {
            instance.handleInput("HELP");
        } catch (exceptions.TimeoutException | CancellationException ignored) {
        }

        assertEquals(correctOut, normalize(systemOut.getLog()));

    }

    /**
     * Testing if handleInput() prints the correct message when given a multi-word input.
     * We expect a String output "\n[product id] - Select a product.\nHELP - Display this help dialog."
     * <p>
     * Test passes because "help me please" has a length greater than 1. The method will first ensure that
     * the length of the input is less than one before attempting to match commands or parse products.
     * As "help me please" is an invalid input, the method will print the warning instead of the HELP dialog.
     */
    @Test
    public void handleInput_multiWordHelpTest() {

        String correctOut = "\nInvalid input. Type HELP for instructions.\n";

        try {
            instance.handleInput("help me please");
        } catch (TimeoutException | CancellationException ignored) {
        }

        assertEquals(correctOut, normalize(systemOut.getLog()));

    }

    /**
     * Testing if handleInput() will correctly time out if no input is provided at product select.
     * We expect an exception TimeoutException to be thrown.
     * <p>
     * Test passes because the internal method awaitInput() will throw a TimeoutException if a certain
     * number of seconds elapses after being called, and no input is provided by the user. As we intentionally
     * provide no input to the program here, the awaitInput method will time out.
     * <p>
     * In this test, timeout is set to 1 second to ensure testing the feature does not have a significant impact
     * on test execution time.
     */
    @Test
    public void handleInput_timeoutAtSelectTest() {

        Main.setTimeout(1);

        try {
            instance.run();
            fail("TimeoutException not thrown.");
        } catch (TimeoutException e) {
            // if execution reaches this stage, test passes.
        } catch (CancellationException e) {
            // if execution reaches this stage, something's wrong.
            fail();
        }

    }

    /**
     * Testing if handleInput() will correctly time out if no input is provided at quantity selection.
     * We expect an exception TimeoutException to be thrown.
     * <p>
     * Test passes because the internal method awaitInput() will throw a TimeoutException if a certain
     * number of seconds elapses after being called, and no input is provided by the user. As we first
     * provide input to the program, we move to the quantity stage. However, as no input is provided once
     * the quantity stage is reached, the awaitInput method will time out.
     * <p>
     * In this test, timeout is set to 1 second to ensure testing the feature does not have a significant impact
     * on test execution time.
     */
    @Test
    public void handleInput_timeoutAtQuantityTest() {

        Main.setTimeout(1);
        supplyInput("ORIGINAL");

        try {
            instance.run();
            fail("TimeoutException not thrown.");
        } catch (TimeoutException e) {
            // if execution reaches this stage, test passes.
        } catch (CancellationException e) {
            // execution should not each this stage.
            fail();
        } finally {
            System.setIn(stdin);
        }

    }

    /**
     * Testing if handleInput() will correctly time out if no input is provided at payment.
     * We expect an exception TimeoutException to be thrown.
     * <p>
     * Test passes because the internal method awaitInput() will throw a TimeoutException if a certain
     * number of seconds elapses after being called, and no input is provided by the user. As we first
     * provide input to the program, we move to the quantity stage. Then, we provide input to move to the
     * payment stage. However, as no input is provided once the payment stage is reached, the awaitInput
     * method will time out.
     * <p>
     * In this test, timeout is set to 1 second to ensure testing the feature does not have a significant impact
     * on test execution time.
     */
    @Test
    public void handleInput_timeoutAtPaymentTest() {

        Main.setTimeout(1);
        supplyInput("ORIGINAL", "1");

        try {
            instance.run();
            fail("TimeoutException not thrown.");
        } catch (TimeoutException e) {
            // if execution reaches this stage, test passes.
        } catch (CancellationException e) {
            // execution should not each this stage.
            fail();
        } finally {
            System.setIn(stdin);
        }


    }

    /**
     * Testing if handleInput() will cancel the transaction.
     * We expect an exception CancellationException to be thrown.
     * <p>
     * Test passes because the internal method handleInput() will throw a CancellationException if user inputs "CANCEL".
     */
    @Test
    public void handleInput_cancelAtSelectionTest() {

        supplyInput("CANCEL", "0", "CANCEL");

        // at selection
        try {
            instance.run();
            fail("CancellationException not thrown.");
        } catch (CancellationException e) {
            // if execution reaches this stage, test passes.
        } catch (TimeoutException e) {
            // execution should not each this stage.
            fail();
        } finally {
            System.setIn(stdin);
        }

    }

    @Test
    public void handleInput_fillFromUserInputSuccessTest() {
        exit.expectSystemExit();

        systemIn.provideLines("FILL original admin", "QUIT");
        Main.main(null);

        assertTrue(systemOut.getLog().contains("Admin identity authenticated. Refilling..."));
        assertTrue(systemOut.getLog().contains("Product original successfully restocked"));

    }

    @Test
    public void handleInput_fillFromUserInputInvalidAdminIdTest() {
        exit.expectSystemExit();

        systemIn.provideLines("FILL original bob", "QUIT");
        Main.main(null);

        assertTrue(systemOut.getLog().contains("Admin id \"bob\" does not exist in the system!"));

    }

    @Test
    public void handleInput_fillFromUserInputInvalidProductIdTest() {
        exit.expectSystemExit();

        systemIn.provideLines("FILL fakeproduct admin", "QUIT");
        Main.main(null);

        assertTrue(systemOut.getLog().contains("Admin identity authenticated. Refilling..."));
        assertTrue(systemOut.getLog().contains("fakeproduct is not a valid product or product ID. Restock failed."));

    }

    /**
     * Removes carriage return characters (\r) from a String.
     * Used to remove unwanted invisible characters when testing console output.
     *
     * @param toNormalize the String object to normalize.
     * @return a new String object with all \r characters removed.
     */
    private String normalize(String toNormalize) {
        return toNormalize.replaceAll("\r", "");
    }

    /**
     * Sets input stream contents for testing purposes.
     *
     * @param arguments the String inputs to provide to System input stream.
     */
    private void supplyInput(String... arguments) {

        StringBuilder builder = new StringBuilder();

        for (String arg : arguments) {
            builder.append(arg);
            builder.append("\n");
        }

        System.setIn(new ByteArrayInputStream(builder.toString().getBytes()));

    }

}
