package store.domain;

import java.util.ArrayList;
import java.util.List;

public class Product {
    private final String name;
    private final int price;
    private final List<Promotion> promotions = new ArrayList<>();

    public Product(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public List<Promotion> getPromotions() {
        return promotions;
    }

    public void addPromotion(Promotion promotion) {
        promotions.add(promotion);
    }

    public int getAllQuantityAboutProduct() {
        return promotions.stream()
                .mapToInt(Promotion::getQuantity)
                .sum();
    }
}
