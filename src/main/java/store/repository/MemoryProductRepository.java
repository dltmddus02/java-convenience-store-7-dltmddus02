package store.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import store.domain.Product;

public class MemoryProductRepository implements ProductRepository {
    private Map<String, Product> productCatalog = new HashMap<>();

    @Override
    public void saveProduct(Product product) {
        if (!existsByName(product.getName())) {
            productCatalog.put(product.getName(), product);
        }
    }

    @Override
    public Optional<Product> findByName(String name) {
        return Optional.ofNullable(productCatalog.get(name));
    }

    @Override
    public Boolean existsByName(String name) {
        return productCatalog.containsKey(name);
    }
}
