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
        receiptService = new ReceiptService();

        // 프로젝트 초기 실행 시 products.md를 products_backup.md로 복사
        copyBackupFile();
    }

    private void copyBackupFile() {
        try {
            Files.copy(Path.of(PRODUCT_FILE_PATH), Path.of(BACKUP_FILE_PATH), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("초기 상품 목록을 불러왔습니다.");
        } catch (IOException e) {
            System.err.println("파일 복사 중 오류가 발생했습니다: " + e.getMessage());
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
                    if (askMembershipDiscount()) {
                        receipt.setMembershipDiscount();
                    }
                    calculateTotalMoney();

                    if (!askToContinueShopping()) {
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

    private boolean askMembershipDiscount() {
        System.out.println("멤버십 할인을 받으시겠습니까? (Y/N)");

        String membershipDiscount;
        try {
            membershipDiscount = InputView.inputYesOrNo();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return askToContinueShopping();
        }
        return membershipDiscount.equals("Y");
    }

    private void calculateTotalMoney() {
        receiptService.printReceipt(receipt);
    }

    private boolean askToContinueShopping() {
        System.out.println("감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)");

        String continueShopping;
        try {
            continueShopping = InputView.inputYesOrNo();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return askToContinueShopping();
        }
        return continueShopping.equals("Y");
    }
}