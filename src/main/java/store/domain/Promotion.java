package store.domain;

public class Promotion {
    private final int quantity;
    private final String promotionType;

    public Promotion(int quantity, String promotionType) {
        this.quantity = quantity;
        this.promotionType = promotionType;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getPromotionType() {
        return promotionType;
    }
}
