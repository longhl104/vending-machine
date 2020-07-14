import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 *  Tests for the VendingMachine class.
 */
public class VendingMachineTests {

    private VendingMachine vm;
    private ArrayList<TransactionPair> tp;

    /**
     * Creating a default VM containing chips with name "Original", id 0 and quantity 2.
     */
    @Before
    public void setup() {
        vm = new VendingMachine();
        tp = new ArrayList<>();
    }

    /**
     * Testing if getProduct() accepts correct string ignoring the case.
     * We expect the method to return the name of the product as a String.
     * Tests pass because getProduct() uses .equalsIgnoreCase() when searching for the product name.
     */
    @Test
    public void getProduct_validNameTest() {

        assertEquals("Original", vm.getProduct("original").getName());
        assertEquals("Original", vm.getProduct("Original").getName());
        assertEquals("Original", vm.getProduct("ORIGINAL").getName());
        assertEquals("Original", vm.getProduct("OrIgInAl").getName());

    }

    /**
     * Testing if getProduct() does not accept strings that are not the product name or
     * contain invalid symbols.
     * We expect getProduct() to return null if the input is invalid.
     * Tests pass because none of these Strings match any of the product names.
     */
    @Test
    public void getProduct_invalidNameTest() {

        assertNull(vm.getProduct("chip"));
        assertNull(vm.getProduct("CHIP"));
        assertNull(vm.getProduct("origina"));
        assertNull(vm.getProduct("original "));
        assertNull(vm.getProduct("$%^&£$"));
        assertNull(vm.getProduct(" original"));
        assertNull(vm.getProduct("Original 1"));
        assertNull(vm.getProduct("original chip"));
        assertNull(vm.getProduct("chip original"));
        assertNull(vm.getProduct("i want to die"));

    }

    /**
     * Testing if getProduct() returns correct product, provided a valid id.
     * We expect a product with the name "Original".
     * Test passes because getProduct() tries to convert the input into an int
     * and then it returns the Product with this id.
     */
    @Test
    public void getProduct_validIdTest() {

        assertEquals("Original", vm.getProduct("0").getName());

    }

    /**
     * Testing if getProduct() returns null product, provided an invalid id.
     * We expect a null.
     * Test passes because getProduct() tries to convert the input into an int
     * and then it returns null because no Product with that id was found.
     */
    @Test
    public void getProduct_invalidIdTest() {

        assertNull(vm.getProduct("100"));

    }

    /**
     * Testing if getProduct() returns null product, provided an invalid id.
     * We expect a null.
     * Test passes because getProduct() tries to convert the input into an int
     * and then it returns null because no Product with that id was found.
     */
    @Test
    public void getProduct_invalidIdDoubleTest() {

        assertNull(vm.getProduct("0.0"));

    }

    /**
     * Testing if getProduct() returns null product, provided an invalid id.
     * We expect a null.
     * Test passes because getProduct() can not convert the input into an int
     * and then it returns null.
     */
    @Test
    public void getProduct_invalidIdNotIntTest() {

        assertNull(vm.getProduct("abc"));

    }

    /**
     * Testing if the default VM's displayProducts() returns the correct representation of stock.
     * We expect a String "\nAvailable selections:\n[ID 0] Original - $5.00\n";
     * Test passes because the default VM has only one product in stock with a quantity of more than 0,
     * which is then appended to the available string within the loop.
     */
    @Test
    public void displayProducts_validTest() {

        String correctOut =
                "\nAvailable selections:\n" +
                        "[ID 0] Original - $5.00 (2 item(s) in stock)\n" +
                        "[ID 1] Chicken - $3.50 (10 item(s) in stock)\n" +
                        "[ID 2] BBQ - $3.50 (10 item(s) in stock)\n" +
                        "[ID 3] Sweet Chillies - $3.50 (10 item(s) in stock)\n" +
                        "[ID 4] Sour Worms - $3.00 (10 item(s) in stock)\n" +
                        "[ID 5] Jellybeans - $3.00 (10 item(s) in stock)\n" +
                        "[ID 6] Little Bears - $3.00 (10 item(s) in stock)\n" +
                        "[ID 7] Part Mix - $3.50 (10 item(s) in stock)\n" +
                        "[ID 8] Water - $2.50 (10 item(s) in stock)\n" +
                        "[ID 9] Soft Drink - $3.00 (10 item(s) in stock)\n" +
                        "[ID 10] Juice - $3.50 (10 item(s) in stock)\n" +
                        "[ID 11] M&M - $1.00 (10 item(s) in stock)\n" +
                        "[ID 12] Bounty - $1.00 (10 item(s) in stock)\n" +
                        "[ID 13] Mars - $1.00 (10 item(s) in stock)\n" +
                        "[ID 14] Sneakers - $1.00 (10 item(s) in stock)\n";

        assertEquals(correctOut, vm.displayProducts(false));

    }

    /**
     * Testing if an empty VM's displayProducts() returns the correct representation of stock.
     * We expect a String "\nAvailable selections:\n";
     * Test passes because the displayProducts() skips a product if it's quantity is 0.
     */
    @Test
    public void displayProducts_zeroQuantityTest() {

        tp.add(new TransactionPair(vm.getProduct("original"), 2));
        tp.add(new TransactionPair(vm.getProduct("1"), 10));
        tp.add(new TransactionPair(vm.getProduct("2"), 10));
        tp.add(new TransactionPair(vm.getProduct("3"), 10));
        tp.add(new TransactionPair(vm.getProduct("4"), 10));
        tp.add(new TransactionPair(vm.getProduct("5"), 10));
        tp.add(new TransactionPair(vm.getProduct("6"), 10));
        tp.add(new TransactionPair(vm.getProduct("7"), 10));
        tp.add(new TransactionPair(vm.getProduct("8"), 10));
        tp.add(new TransactionPair(vm.getProduct("9"), 10));
        tp.add(new TransactionPair(vm.getProduct("10"), 10));
        tp.add(new TransactionPair(vm.getProduct("11"), 10));
        tp.add(new TransactionPair(vm.getProduct("12"), 10));
        tp.add(new TransactionPair(vm.getProduct("13"), 10));
        tp.add(new TransactionPair(vm.getProduct("14"), 10));

        vm.dispenseItems(tp);

        String correctOut = "\nAvailable selections:\n" + "(no items available)\n";

        assertEquals(correctOut, vm.displayProducts(false));

    }

    /**
     * Testing if dispenseItems() reduces the quantity of the item in the machine, providing 1 as quantity.
     * We expect the item's quantity to be reduced by one each time.
     *
     * Test passes because the method calls product's reduce quantity,
     * which reduces the quantity of the product by the given amount.
     */
    @Test
    public void dispenseItems_SingleTest() {

        tp.add(new TransactionPair(vm.getProduct("original"), 1));

        vm.dispenseItems(tp);
        assertEquals(1, vm.getProduct("original").getQuantity(), 0);

        vm.dispenseItems(tp);
        assertEquals(0, vm.getProduct("original").getQuantity(), 0);

    }

    /**
     * Testing if dispenseItems() reduces the quantity of the item in the machine, providing 2 as quantity.
     * We expect the item's quantity to be reduced to 0.
     *
     * Test passes because the method calls product's reduce quantity,
     * which reduces the quantity of the product by the given amount.
     */
    @Test
    public void dispenseItems_MultipleTest() {

        tp.add(new TransactionPair(vm.getProduct("original"), 2));

        vm.dispenseItems(tp);
        assertEquals(0, vm.getProduct("original").getQuantity(), 0);

    }

    /**
     * Testing if dispenseItems() reduces the quantity of the item in the machine, providing 0 as quantity.
     * We expect the item's quantity not to be reduced.
     *
     * Test passes because the method calls product's reduce quantity,
     * which reduces the quantity of the product by the given amount.
     */
    @Test
    public void dispenseItems_ZeroTest() {

        tp.add(new TransactionPair(vm.getProduct("original"), 0));

        vm.dispenseItems(tp);
        assertEquals(2, vm.getProduct("original").getQuantity(), 0);

    }

    /**
     * Testing if displaySelections() returns a correct string of selections, provided no transaction pairs.
     * We expect an empty string.
     * Test passes because displaySelections() loops over the transaction pairs, since the are no transaction pairs
     * an empty StringBuilder is returned.
     */
    @Test
    public void displaySelections_emptyTest() {

        assertEquals( "", vm.displaySelections(tp).toString());

    }

    /**
     * Testing if displaySelections() returns a correct string of selections.
     * We expect a String "[ID 0] Original - quantity 1 @ $5.00 each = total $5.00\n".
     * Test passes because displaySelections() loops over the transaction pairs,
     * and returns a StringBuilder based on the transaction pair.
     */
    @Test
    public void displaySelections_singleTest() {

        String correctOut = "[ID 0] Original - quantity 1 @ $5.00 each = total $5.00\n";

        tp.add(new TransactionPair(vm.getProduct("original"), 1));

        assertEquals( correctOut, vm.displaySelections(tp).toString());

    }

    /**
     * Testing if displaySelections() returns a correct string of selections.
     * We expect a String "[ID 0] Original - quantity 1 @ $5.00 each = total $5.00\n
     *                     [ID 0] Original - quantity 1 @ $5.00 each = total $5.00\n".
     * Test passes because displaySelections() loops over the transaction pairs,
     * and the method does not group the transaction pairs.
     */
    @Test
    public void displaySelections_doubleTest() {

        String correctOut = "[ID 0] Original - quantity 1 @ $5.00 each = total $5.00\n"
                + "[ID 0] Original - quantity 1 @ $5.00 each = total $5.00\n";

        tp.add(new TransactionPair(vm.getProduct("original"), 1));
        tp.add(new TransactionPair(vm.getProduct("original"), 1));

        assertEquals( correctOut, vm.displaySelections(tp).toString());

    }

    /**
     * Testing if grandTotal() calculates the correct total.
     * We expect the total to be $0, since we provide an empty list.
     * Test passes because grandTotal() loops over the list of transaction pairs and adds up all the prices,
     * since the list is empty, the total is 0.
     */
    @Test
    public void grandTotal_emptyTest() {
        assertEquals(0, vm.grandTotal(tp), 0);

    }

    /**
     * Testing if grandTotal() calculates the correct total.
     * We expect the total to be $10, since the are two chips in the list, $5 each.
     * Test passes because grandTotal() loops over the list of transaction pairs and adds up all the prices.
     */
    @Test
    public void grandTotal_validTest() {

        tp.add(new TransactionPair(vm.getProduct("original"), 1));
        tp.add(new TransactionPair(vm.getProduct("original"), 1));

        assertEquals(10, vm.grandTotal(tp), 0);
    }

}
