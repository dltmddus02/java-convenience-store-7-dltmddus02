package store.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import store.domain.Product;
import store.domain.ProductField;
import store.domain.Stock;
import store.repository.ProductRepository;
import store.repository.PromotionRepository;
import store.view.output.OutputView;

public class ProductService {
    private static final String PRODUCT_FILE_PATH = "src/main/resources/products_backup.md";
    private static final String FILE_LOAD_ERROR = "[ERROR] 상품 목록을 불러오는 중 오류가 발생했습니다.";
    private static final String FILE_WRITE_ERROR = "[ERROR] 파일을 작성하는 중 오류가 발생했습니다.";
    private final ProductRepository productRepository;
    private final PromotionRepository promotionRepository;

    public ProductService(ProductRepository productRepository, PromotionRepository promotionRepository) {
        this.productRepository = productRepository;
        this.promotionRepository = promotionRepository;
    }

    public void loadProductsFromFile() {
        addNullPromotionProducts();
        try (BufferedReader br = new BufferedReader(new FileReader(PRODUCT_FILE_PATH))) {
            readProductLines(br);
        } catch (IOException e) {
            System.err.println(FILE_LOAD_ERROR + ": " + e.getMessage());
        }
    }

    void readProductLines(BufferedReader br) throws IOException {
        br.readLine();
        String line;
        while ((line = br.readLine()) != null) {
            processEachProductLine(line);
        }
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

        OutputView.printProductList(productName, price, quantity, promotionType);

        Product product = getOrCreateProduct(productName, price);
        addStockToProduct(product, quantity, promotionType);
        return product;
    }

    private Product getOrCreateProduct(String productName, int price) {
        return productRepository.findByName(productName)
                .orElseGet(() -> new Product(productName, price));
    }

    private void addStockToProduct(Product product, int quantity, String promotionType) {
        Stock stock = new Stock(quantity, promotionType, promotionRepository);
        product.addStock(stock);
    }

    private void saveOrUpdateProduct(Product product) {
        if (!productRepository.existsByName(product.getName())) {
            productRepository.saveProduct(product);
        }
    }

    public void updateProductStock(String productName, String promotionType, int newQuantity) {
        try {
            List<String> lines = readAllLines();
            List<String> updatedLines = lines.stream()
                    .map(line -> updateStockInLine(line, productName, promotionType, newQuantity))
                    .toList();
            writeUpdatedLinesToFile(updatedLines);
        } catch (IOException e) {
            System.err.println(FILE_LOAD_ERROR + ": " + e.getMessage());
        }
    }

    private List<String> readAllLines() throws IOException {
        return Files.readAllLines(Paths.get(PRODUCT_FILE_PATH));
    }

    private String updateStockInLine(String line, String productName, String promotionType, int newQuantity) {
        String[] values = line.split(",");
        if (isMatchingProduct(values, productName, promotionType)) {
            return createUpdatedLine(values, newQuantity);
        }
        return line;
    }

    private boolean isMatchingProduct(String[] values, String productName, String promotionType) {
        return values[ProductField.NAME.getIndex()].equals(productName) &&
                values[ProductField.PROMOTION_TYPE.getIndex()].equals(promotionType);
    }

    private String createUpdatedLine(String[] values, int updatedQuantity) {
        values[ProductField.QUANTITY.getIndex()] = String.valueOf(updatedQuantity);
        return String.join(",", values);
    }

    private void writeUpdatedLinesToFile(List<String> updatedLines) throws IOException {
        try (FileWriter writer = new FileWriter(PRODUCT_FILE_PATH)) {
            for (String updatedLine : updatedLines) {
                writer.write(updatedLine + System.lineSeparator());
            }
        }
    }

    public void addNullPromotionProducts() {
        try {
            List<String> lines = readAllLines();
            List<String> productLines = lines.subList(1, lines.size());
            Set<String> productsWithoutPromotion = findProductsWithoutPromotion(productLines);

            List<String> newLines = createNewLines(productLines, productsWithoutPromotion);

            if (!newLines.isEmpty()) {
                lines.addAll(newLines);
                addLinesToFile(lines);
            }
        } catch (IOException e) {
            System.err.println(FILE_LOAD_ERROR);
        }
    }

    private Set<String> findProductsWithoutPromotion(List<String> productLines) {
        Set<String> productsWithoutPromotion = new HashSet<>();
        for (String line : productLines) {
            String[] parts = line.split(",");
            if ("null".equals(parts[ProductField.QUANTITY.getIndex()])) {
                productsWithoutPromotion.add(parts[ProductField.NAME.getIndex()]);
            }
        }
        return productsWithoutPromotion;
    }

    private List<String> createNewLines(List<String> productLines, Set<String> productsWithoutPromotion) {
        List<String> linesToAdd = new ArrayList<>();
        for (String line : productLines) {
            String[] parts = line.split(",");
            if (!"null".equals(parts[ProductField.QUANTITY.getIndex()]) && !productsWithoutPromotion.contains(
                    parts[0])) {
                linesToAdd.add(
                        parts[ProductField.NAME.getIndex()] + "," + parts[ProductField.PRICE.getIndex()] + ",0,null");
                productsWithoutPromotion.add(parts[ProductField.NAME.getIndex()]);
            }
        }
        return linesToAdd;
    }

    private void addLinesToFile(List<String> lines) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PRODUCT_FILE_PATH))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println(FILE_WRITE_ERROR);
        }
    }

}
