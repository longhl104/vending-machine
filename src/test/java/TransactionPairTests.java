import org.junit.Before;
import org.junit.Test;
import product.Category;
import product.Product;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for the TransactionPair class.
 */
public class TransactionPairTests {

    TransactionPair tp;

    /**
     *  Creating a transaction pair with 4 RedBulls.
     *  RedBull's price is 4.0.
     */
    @Before
    public void setup() {
        tp = new TransactionPair(
                new Product(1, "RedBull", 4.0, 10, Category.DRINK), 4);
    }

    /**
     * Testing if getProduct() returns the product.
     * We expect the RedBull product to be returned.
     * Test passes because getProduct() accesses the product field directly.
     */
    @Test
    public void getProductTest() {
        assertNotNull(tp.getProduct());
    }

    /**
     * Testing if getQuantity() returns the quantity of the product.
     * We expect 4 to be returned.
     * Test passes because getQuantity() accesses the quantity field directly.
     */
    @Test
    public void getQuantityTest() {
        assertEquals(4, tp.getQuantity(), 0);
    }

    /**
     * Testing if getTotalPrice() returns the total price for the product.
     * We expect 16 to be returned.
     * Test passes because getTotalPrice() multiplies quantity of the product by the price.
     */
    @Test
    public void getTotalPriceTest() {
        assertEquals(16.0, tp.getTotalPrice(), 0);
    }

    /**
     * Testing if increaseQuantity() updates the quantity of the product, provided 1.
     * We expect the quantity to be incremented by 1.
     * Test passes because increaseQuantity() adds the provided number to the quantity.
     */
    @Test
    public void increaseQuantityTest() {

        tp.increaseQuantity(1);
        assertEquals(5, tp.getQuantity(), 0);

    }
}
