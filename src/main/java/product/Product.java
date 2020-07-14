package product;

public class Product {
    private final int id;
    private String name;
    private double price;
    private int quantity;
    private Category category;

    public Product(int id, String name, double price, int quantity, Category category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
    }

    public int getId() { return id; }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public Category getCategory() {
        return category;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void increaseQuantity(int quantity) {this.quantity += quantity;}

    public void reduceQuantity(int quantity) {
        this.quantity -= quantity;
    }

    public void restock() {this.quantity = 10;}

}
