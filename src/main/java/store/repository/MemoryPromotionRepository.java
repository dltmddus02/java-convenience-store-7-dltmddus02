package store.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import store.domain.Promotion;

public class MemoryPromotionRepository implements PromotionRepository {
    private final List<Promotion> promotions = new ArrayList<>();

    @Override
    public void savePromotion(Promotion promotion) {
        if (!existsByType(promotion.getPromotionType())) {
            promotions.add(promotion);
        }
    }

    @Override
    public Optional<Promotion> findByType(String promotionType) {
        return promotions.stream()
                .filter(promotion -> promotion.getPromotionType().equals(promotionType))
                .findFirst();
    }

    @Override
    public Boolean existsByType(String promotionType) {
        return promotions.stream()
                .anyMatch(promotion -> promotion.getPromotionType().equals(promotionType));
    }
}
