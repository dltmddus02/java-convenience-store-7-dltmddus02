package store.domain;

import store.repository.PromotionRepository;

public class Stock {
    private int quantity;
    private final String promotionType;
    private final Promotion promotion;
    private final PromotionRepository promotionRepository;


    public Stock(int quantity, String promotionType, PromotionRepository promotionRepository) {
        this.quantity = quantity;
        this.promotionType = promotionType;
        this.promotionRepository = promotionRepository;
        this.promotion = findPromotionByType(promotionType);
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

    public Promotion getPromotion() {
        return promotion;
    }

    private Promotion findPromotionByType(String promotionType) {
        return promotionRepository.findByType(promotionType).orElse(null);
    }
}
