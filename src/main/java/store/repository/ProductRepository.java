package store.repository;

import java.util.Optional;
import store.domain.Product;

public interface ProductRepository {
    Product save(Product product);

    Optional<Product> findByName(String name);

    Boolean existsByName(String name);
}
