import org.junit.Before;
import org.junit.Test;
import product.Category;
import product.Product;

import static org.junit.Assert.assertEquals;

/**
 *  Tests for the Product class.
 */
public class ProductTests {

    Product product;

    /**
     * Creating a Product object with id: 1, name: "chips", price: 4.0, quantity: 1 and category: CHIPS
     */
    @Before
    public void setup() {
        product = new Product(1, "chips", 4.0,1, Category.CHIPS);
    }

    /**
     * Testing if getName() returns product's name.
     * We expect a String "chips".
     * Test passes because getName() accesses the name field directly.
     */
    @Test
    public void getNameTest() {
        assertEquals("chips", product.getName());
    }
    /**
     * Testing if getPrice() returns product's price.
     * We expect a double 4.0.
     * Test passes because getPrice() accesses the price field directly.
     */
    @Test
    public void getPriceTest() {
        assertEquals(4.0, product.getPrice(), 0);
    }

    /**
     * Testing if getQuantity() returns product's quantity.
     * We expect an int 1.
     * Test passes because getQuantity() accesses the quantity field directly.
     */
    @Test
    public void getQuantityTest() {
        assertEquals(1, product.getQuantity());
    }

    /**
     * Testing if setPrice() sets a correct product price.
     * We expect the method to set a new price for the product.
     * Tests pass because setPrice() accesses the price field directly and sets new value.
     */
    @Test
    public void setPriceTest() {
        product.setPrice(4.5);
        assertEquals(4.5, product.getPrice(), 0);
    }

    /**
     * Testing if getCategory() returns product's category.
     * We expect Category.CHIPS.
     * Test passes because getCategory() accesses the category field directly.
     */
    @Test
    public void getCategoryTest() {
        assertEquals(Category.CHIPS, product.getCategory());
    }

    /**
     * Testing if getId() returns product's id.
     * We expect 1.
     * Test passes because getId() accesses the id field directly.
     */
    @Test
    public void getIdTest() {
        assertEquals(1, product.getId());
    }

    /**
     * Testing if reduceQuantity updates the product's quantity
     * We expect that the quantity will decrement by 1.
     * Test passes reduceQuantity modifies the quantity field directly.
     */
    @Test
    public void reduceQuantityTest() {

        assertEquals(1, product.getQuantity());

        product.reduceQuantity(1);
        assertEquals(0, product.getQuantity());

    }

    /**
     * Testing if increaseQuantity updates the product's quantity
     * We expect that the quantity will incremented by 1.
     * Test passes reduceQuantity modifies the quantity field directly.
     */
    @Test
    public void increaseQuantityTest() {

        assertEquals(1, product.getQuantity());

        product.increaseQuantity(1);
        assertEquals(2, product.getQuantity());

    }
}
