package store.repository;

import java.util.Optional;
import store.domain.Promotion;

public interface PromotionRepository {
    void savePromotion(Promotion promotion);

    Optional<Promotion> findByType(String name);

    Boolean existsByType(String name);
}
