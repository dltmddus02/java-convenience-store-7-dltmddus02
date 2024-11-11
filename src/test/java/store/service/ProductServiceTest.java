package store.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.BufferedReader;
import java.io.StringReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import store.repository.MemoryProductRepository;
import store.repository.MemoryPromotionRepository;
import store.repository.ProductRepository;
import store.repository.PromotionRepository;

class ProductServiceTest {
    ProductRepository productRepository;
    PromotionRepository promotionRepository;
    ProductService productService;

    @BeforeEach
    void setUp() {
        productRepository = new MemoryProductRepository();
        promotionRepository = new MemoryPromotionRepository();
        productService = new ProductService(productRepository, promotionRepository);
    }

    @Test
    @DisplayName("파일을 잘 읽어오는지 테스트")
    void loadProductsFromFileTest() {
        // given
        String mockFile = "name,price,quantity,promotion\n"
                + "콜라,1000,10,탄산2+1\n"
                + "콜라,1000,10,null\n"
                + "사이다,1000,8,탄산2+1";
        BufferedReader reader = new BufferedReader(new StringReader(mockFile));

        // when & then
        assertDoesNotThrow(() -> productService.readProductLines(reader));
    }

}
