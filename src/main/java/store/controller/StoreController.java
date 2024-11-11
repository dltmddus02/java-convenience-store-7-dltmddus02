package store.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import store.domain.Receipt;
import store.repository.MemoryProductRepository;
import store.repository.MemoryPromotionRepository;
import store.repository.ProductRepository;
import store.repository.PromotionRepository;
import store.service.ProductParser;
import store.service.ProductService;
import store.service.ProductStockHandler;
import store.service.PromotionService;
import store.service.ReceiptService;
import store.view.input.InputView;
import store.view.input.exception.InputException;
import store.view.output.OutputView;

public class StoreController {
    private static final String PRODUCT_FILE_PATH = "src/main/resources/products.md";
    private static final String BACKUP_FILE_PATH = "src/main/resources/products_backup.md";

    private final ProductService productService;
    private final ProductStockHandler productStockHandler;
    private final PromotionService promotionService;
    private Receipt receipt = new Receipt();
    private ReceiptService receiptService;

    public StoreController() {
        ProductRepository productRepository = new MemoryProductRepository();
        PromotionRepository promotionRepository = new MemoryPromotionRepository();
        productService = new ProductService(productRepository, promotionRepository);
        productStockHandler = new ProductStockHandler(productRepository, productService, receipt);
        promotionService = new PromotionService(promotionRepository);

        copyBackupFile();
    }

    private void copyBackupFile() {
        try {
            Files.copy(Path.of(PRODUCT_FILE_PATH), Path.of(BACKUP_FILE_PATH), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void storeServiceRun() {
        OutputView.printWelcomeMessage();
        promotionService.loadPromotionsFromFile();
        showProductsList();

        while (true) {
            try {
                System.out.println();
                Map<String, Integer> productsMap = chooseProductAndQuantity();
                if (updateStockOnFile(productsMap)) {
                    if (InputView.askMembershipDiscount()) {
                        receipt.setMembershipDiscount();
                    }
                    receiptService = new ReceiptService(receipt);
                    calculateTotalMoney();

                    if (!InputView.askToContinueShopping()) {
                        break;
                    }
                    receipt = new Receipt();
                    productStockHandler.updateReceipt(receipt);

                    OutputView.printWelcomeMessage();
                    promotionService.loadPromotionsFromFile();
                    showProductsList();
                }
            } catch (InputException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void showProductsList() {
        productService.loadProductsFromFile();
    }

    private Map<String, Integer> chooseProductAndQuantity() {
        while (true) {
            try {
                OutputView.printProductNameAndQuantity();
                String productNameAndQuantity = InputView.inputProductNameAndQuantity();
                return ProductParser.parseProducts(productNameAndQuantity);
            } catch (InputException e) {
                System.out.println(e.getMessage());
                System.out.println();
            }
        }
    }

    private boolean updateStockOnFile(Map<String, Integer> productsMap) throws InputException {
        boolean isStockDeducted = false;
        for (Map.Entry<String, Integer> entry : productsMap.entrySet()) {
            try {
                isStockDeducted = false;
                productStockHandler.deductStock(entry.getKey(), entry.getValue());
                isStockDeducted = true;
            } catch (InputException e) {
                System.out.println(e.getMessage());
            }
        }
        return isStockDeducted;
    }

    private void calculateTotalMoney() {
        receiptService.printReceipt(receipt);
    }

}
