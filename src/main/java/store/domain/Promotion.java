package store.domain;

public class Promotion {
    private int quantity;
    private final String promotionType;

    public Promotion(int quantity, String promotionType) {
        this.quantity = quantity;
        this.promotionType = promotionType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int newQuantity) {
        quantity = newQuantity;
    }

    public String getPromotionType() {
        return promotionType;
    }
}
