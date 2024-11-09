package store.domain;

import java.util.Map;

public class Stock {
    private int quantity;
    private final String promotionType;
//    private final Promotion promotionDetail;

    public Stock(int quantity, String promotionType) {
        this.quantity = quantity;
        this.promotionType = promotionType;
//        this.promotionDetail = getPromotionDetailByType(promotionType);
    }

    private static Map<String, Promotion> promotionDetailMap;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int newQuantity) {
        quantity = newQuantity;
    }

    public String getPromotionType() {
        return promotionType;
    }

//    private Promotion getPromotionDetailByType(String promotionType) {
//        return promotionDetailMap.get(promotionType);
//    }
//
//    public static void setPromotionDetailMap(Map<String, Promotion> detailsMap) {
//        promotionDetailMap = detailsMap;
//    }
}
