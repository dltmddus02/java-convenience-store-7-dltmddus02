package store.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import store.domain.Product;
import store.domain.ProductField;
import store.domain.Promotion;
import store.repository.ProductRepository;

public class ProductService {
    private static final String PRODUCT_FILE_PATH = "resources/products.md";
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    private void loadProductsFromFile() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(PRODUCT_FILE_PATH));

        br.readLine();

        String line;
        while ((line = br.readLine()) != null) {
            processEachProductLine(line);
        }
        br.close();
    }

    private void processEachProductLine(String line) {
        Product product = parseProductByComma(line);
        saveOrUpdateProduct(product);
    }

    private Product parseProductByComma(String line) {
        List<String> values = Arrays.asList(line.split(","));

        String productName = values.get(ProductField.NAME.getIndex());
        int price = Integer.parseInt(values.get(ProductField.PRICE.getIndex()));
        int quantity = Integer.parseInt(values.get(ProductField.QUANTITY.getIndex()));
        String promotionType = values.get(ProductField.PROMOTION_TYPE.getIndex());

        Product product = getOrCreateProduct(productName, price);
        addPromotionToProduct(product, quantity, promotionType);
        return product;
    }

    private Product getOrCreateProduct(String productName, int price) {
        return productRepository.findByName(productName)
                .orElseGet(() -> new Product(productName, price));
    }

    private void addPromotionToProduct(Product product, int quantity, String promotionType) {
        Promotion promotion = new Promotion(quantity, promotionType);
        product.addPromotion(promotion);
    }

    private void saveOrUpdateProduct(Product product) {
        if (productRepository.existsByName(product.getName())) {
            return;
        }
        productRepository.save(product);
    }
}
