import product.Category;
import product.Product;

import java.util.*;

public class VendingMachine {

    private Set<Product> stock;

    /**
     * Constructor. Adds stock to vending machine.
     */
    public VendingMachine() {
        // Sort Products by increasing ID to group related Products together.
        Comparator<Product> byId = Comparator.comparing(Product::getId);
        stock = new TreeSet<>(byId);

        stock.add(new Product(0, "Original", 5, 2, Category.CHIPS));
        stock.add(new Product(1, "Chicken", 3.50, 10, Category.CHIPS));
        stock.add(new Product(2, "BBQ", 3.50, 10, Category.CHIPS));
        stock.add(new Product(3, "Sweet Chillies", 3.50, 10, Category.CHIPS));

        stock.add(new Product(4, "Sour Worms", 3, 10, Category.LOLLIES));
        stock.add(new Product(5, "Jellybeans", 3, 10, Category.LOLLIES));
        stock.add(new Product(6, "Little Bears", 3, 10, Category.LOLLIES));
        stock.add(new Product(7, "Part Mix", 3.50, 10, Category.LOLLIES));

        stock.add(new Product(8, "Water", 2.50, 10, Category.DRINK));
        stock.add(new Product(9, "Soft Drink", 3, 10, Category.DRINK));
        stock.add(new Product(10, "Juice", 3.50, 10, Category.DRINK));

        stock.add(new Product(11, "M&M", 1, 10, Category.CHOCOLATE));
        stock.add(new Product(12, "Bounty", 1, 10, Category.CHOCOLATE));
        stock.add(new Product(13, "Mars", 1, 10, Category.CHOCOLATE));
        // Client requirements state 'Sneakers', development team is happy to change this to 'Snickers' if this was
        // a typo.
        stock.add(new Product(14, "Sneakers", 1, 10, Category.CHOCOLATE));
    }

    /**
     * Returns the requested Product if it is in stock.
     *
     * @param input The name or ID of the requested Product.
     * @return The Product if found in stock, null if not.
     */
    public Product getProduct(String input) {
        // If User inputted an integer, assume they are selecting by ID.
        try {
            int id = Integer.parseInt(input);
            for (Product p : stock) {
                if (p.getId() == id) {
                    return p;
                }
            }
        } catch (NumberFormatException e) {
            for (Product p : stock) {
                if (p.getName().equalsIgnoreCase(input)) {
                    return p;
                }
            }
        }
        return null;
    }

    /**
     * Constructs a string representation of all available products.
     *
     * @return A String representation of available products.
     * @param isAdmin Whether or not the user is an admin. If they are an admin, show all products.
     */
    public String displayProducts(boolean isAdmin) {

        StringBuilder display;
        if (isAdmin) {
            display = new StringBuilder("\nProducts:\n");
        } else {
            display = new StringBuilder("\nAvailable selections:\n");
        }

        for (Product p : stock) {

            if (! isAdmin && p.getQuantity() <= 0) {
                continue;
            }

            display.append(String.format("[ID %d] %s - $%.2f (%d item(s) in stock)\n",
                    p.getId(), p.getName(), p.getPrice(), p.getQuantity()));

        }

        if (stock.stream().allMatch(p -> p.getQuantity() <= 0)) {
            display.append("(no items available)\n");
        }

        return display.toString();

    }

    /**
     * Dispenses the selected products in the specified quantities to the purchaser.
     *
     * @param selections The products and amounts selected.
     */
    public void dispenseItems(Collection<TransactionPair> selections) {

        // more logic could be implemented here - negative or zero checks
        // to prevent product from taking a negative quantity

        for (TransactionPair tp : selections) {
            tp.getProduct().reduceQuantity(tp.getQuantity());
        }

    }

    /**
     * Displays all selections to console.
     *
     * @param selections The Products selected and their quantities.
     */
    public StringBuilder displaySelections(Collection<TransactionPair> selections) {

        StringBuilder display = new StringBuilder();

        for (TransactionPair tp : selections) {
            display.append(String.format("[ID %d] %s - quantity %d @ $%.2f each = total $%.2f\n",
                    tp.getProduct().getId(),
                    tp.getProduct().getName(),
                    tp.getQuantity(),
                    tp.getProduct().getPrice(),
                    tp.getTotalPrice()));
        }
        return display;

    }

    /**
     * Calculates the total price of selections.
     *
     * @param selections The Products and quantities selected.
     * @return The total price.
     */
    public double grandTotal(Collection<TransactionPair> selections) {

        double total = 0;

        for (TransactionPair tp : selections) {
            total += tp.getTotalPrice();
        }

        return total;

    }

    /**
     * Admin function: restock the product in the vending machine.
     *
     * @param product The product to restock.
     * @return Whether or not the product was restocked.
     */
    public boolean fill(String product) {
        // If Admin inputted an integer, assume they are selecting by ID.
        try {
            int id = Integer.parseInt(product);
            for (Product p : stock) {
                if (p.getId() == id) {
                    p.restock();
                    return true;
                }
            }
        } catch (NumberFormatException e) {
            for (Product p : stock) {
                if (p.getName().equalsIgnoreCase(product)) {
                    p.restock();
                    return true;
                }
            }
        }
        return false;
    }
}
