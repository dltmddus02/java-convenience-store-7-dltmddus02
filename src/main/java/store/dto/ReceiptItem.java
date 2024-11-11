package store.dto;

public class ReceiptItem {
    private final String productName;
    private int quantity;
    private int freeQuantity;
    private final int amount;

    public ReceiptItem(String productName, int quantity, int amount) {
        this.productName = productName;
        this.quantity = quantity;
        this.amount = amount;

    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getFreeQuantity() {
        return freeQuantity;
    }

    public void setFreeQuantity(int freeQuantity) {
        this.freeQuantity = freeQuantity;
    }


    public int getAmount() {
        return amount;
    }
}
