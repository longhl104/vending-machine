import product.Product;

public class TransactionPair {

    private Product product;
    private int quantity;

    public TransactionPair(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        return product.getPrice() * quantity;
    }

    public void increaseQuantity(int extra) {
        quantity += extra;
    }

}
