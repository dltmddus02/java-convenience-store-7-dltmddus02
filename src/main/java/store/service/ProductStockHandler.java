package store.service;

import camp.nextstep.edu.missionutils.DateTimes;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import store.domain.Product;
import store.domain.Stock;
import store.repository.ProductRepository;
import store.view.input.InputView;
import store.view.input.exception.InputErrorMessage;
import store.view.input.exception.InputException;

public class ProductStockHandler {
    private static final String PROMOTION_DISCOUNT_NOT_APPLIED = "현재 %s %d개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)";
    private static final String PROMOTION_FREE_PRODUCT_OFFER = "현재 %s은(는) %d개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)";

    private final ProductRepository productRepository;
    private final ProductService productService;

    public ProductStockHandler(ProductRepository productRepository, ProductService productService) {
        this.productRepository = productRepository;
        this.productService = productService;
    }

    public void deductStock(String productName, int requestedQuantity) {
        validateExistingProduct(productName);

        int neededQuantity = requestedQuantity;
        Product product = productRepository.findByName(productName).get();
        validateSufficientStock(product, requestedQuantity);

        for (Stock stock : product.getStocks()) {
            if (isPromotionOngoing(stock)) {
                neededQuantity = validateRemainingPromotionStock(stock, product.getName(), requestedQuantity);
            }

            int quantity = stock.getQuantity();
            if (quantity >= neededQuantity) {
                int newQuantity = quantity - neededQuantity;
                stock.setQuantity(newQuantity);
                updatePromotionStockInFile(product.getName(), stock.getPromotionType(), newQuantity);
                break;
            }
            neededQuantity -= quantity;
            stock.setQuantity(0);
            updatePromotionStockInFile(product.getName(), stock.getPromotionType(), 0);
        }
    }

    private void validateExistingProduct(String name) {
        if (productRepository.findByName(name).isEmpty()) {
            throw new InputException(InputErrorMessage.NON_EXISTENT_PRODUCT);
        }
    }

    private static void validateSufficientStock(Product product, int requestedQuantity) {
        int existingQuantity = product.getAllQuantityAboutProduct();
        if (existingQuantity < requestedQuantity) {
            throw new InputException(InputErrorMessage.STOCK_QUANTITY_NOT_FOUND);
        }
    }

    private boolean isPromotionOngoing(Stock stock) {
        if (stock.getPromotion() == null) {
            return false;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate startDate = LocalDate.parse(stock.getPromotion().getStartDate(), formatter);
        LocalDate endDate = LocalDate.parse(stock.getPromotion().getEndDate(), formatter);
        LocalDate today = DateTimes.now().toLocalDate();

        return (today.isEqual(startDate) || today.isAfter(startDate)) &&
                (today.isEqual(endDate) || today.isBefore(endDate));

    }

    private int validateRemainingPromotionStock(Stock stock, String name, int requestedQuantity) {
        int remainingQuantity = stock.getQuantity();
        int returnQuantity = requestedQuantity;
        int buyQuantity = stock.getPromotion().getBuy();
        int getQuantity = stock.getPromotion().getGet();

        int totalQuantityUnit = buyQuantity + getQuantity;

        try {
            while (requestedQuantity >= totalQuantityUnit && remainingQuantity >= totalQuantityUnit) {
                requestedQuantity -= totalQuantityUnit;
                remainingQuantity -= totalQuantityUnit;
            }

            if (isFreeProductOffer(stock, remainingQuantity, requestedQuantity)) {
                return returnQuantity + calculateFreeProduct(stock, name);
            }
            return calculatePromotionDiscountNotApplied(remainingQuantity, requestedQuantity, name);
        } catch (InputException e) {
            System.out.println(e.getMessage());
        }
        return returnQuantity;
    }


    private boolean isFreeProductOffer(Stock stock, int remainingQuantity, int requestedQuantity) {
        return remainingQuantity > requestedQuantity && requestedQuantity == stock.getPromotion().getBuy();
    }

    private int calculateFreeProduct(Stock stock, String name) {
        int getQuantity = stock.getPromotion().getGet();

        System.out.printf(PROMOTION_FREE_PRODUCT_OFFER, name, getQuantity);
        String input = InputView.inputYesOrNo();
        if (input.equals("Y")) {
            return getQuantity;
        }
        return 0;

    }

    private int calculatePromotionDiscountNotApplied(int remainingQuantity, int requestedQuantity, String name) {
        if (remainingQuantity < requestedQuantity) {
            System.out.printf(PROMOTION_DISCOUNT_NOT_APPLIED, name, requestedQuantity);
            String input = InputView.inputYesOrNo();
            if (input.equals("N")) {
                return 0;
            }
        }
        return requestedQuantity;
    }

    private void updatePromotionStockInFile(String productName, String promotionType, int newQuantity) {
        productService.updateProductStock(productName, promotionType, newQuantity);
    }
}
