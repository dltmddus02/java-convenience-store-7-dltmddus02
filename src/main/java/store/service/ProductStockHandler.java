package store.service;

import camp.nextstep.edu.missionutils.DateTimes;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import store.domain.Product;
import store.domain.Receipt;
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
    private Receipt receipt;
    private int freeQuantity = 0;
    private Map<String, Integer> finalChoosing = new HashMap<>();

    public ProductStockHandler(ProductRepository productRepository, ProductService productService, Receipt receipt) {
        this.productRepository = productRepository;
        this.productService = productService;
        this.receipt = receipt;
    }

    public void updateReceipt(Receipt receipt) {
        this.receipt = receipt;
    }

    public void deductStock(String productName, int requestedQuantity) {
        freeQuantity = 0;
        validateExistingProduct(productName);

        int neededQuantity = requestedQuantity;
        Product product = productRepository.findByName(productName).get();
        validateSufficientStock(product, requestedQuantity);

        receipt.addPurchasedItem(productName, requestedQuantity, product.getPrice()); // 구매한 상품 이름과 개수 (영수증)

        for (Stock stock : product.getStocks()) { // 프로모션 2개 돌면서 확인한다.
            if (isPromotionOngoing(stock)) { // 프로모션 기간이라면?
                neededQuantity = validateRemainingPromotionStock(stock, product.getName(), requestedQuantity);
            } // neededQuantity에 최종적으로 구매할 물건 개수 들어간다. 0개일 수도 있다.

            int quantity = stock.getQuantity();
            if (quantity >= neededQuantity) { // 해당 프로모션의 재고가 내가 구매할 물건보다 많으면 구매 가능함
                int newQuantity = quantity - neededQuantity; // 내가 구매하고 남은 재고 newQuantity
                stock.setQuantity(newQuantity);
                updatePromotionStockInFile(product.getName(), stock.getPromotionType(), newQuantity);
                break;
            }
            neededQuantity -= quantity; // 해당 프로모션의 재고 부족하면 일단 그 프로모션거는 다 쓰고 남은거 다음 프로모션으로 돌리기
            stock.setQuantity(0);
            updatePromotionStockInFile(product.getName(), stock.getPromotionType(), 0);
        }
        receipt.setFreeQuantityForItem(productName, freeQuantity); // 무료로 증정하는 상품과 개수 (영수증)
//        receipt.addEventDiscount(freeQuantity * product.getPrice()); // ?
//        finalChoosing.put(productName, requestedQuantity);
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
                freeQuantity++; // 무료로 증정되는 개수 추가
//                receipt.addEventDiscount(productRepository.findByName(name).get().getPrice());
            }

            if (isFreeProductOffer(stock, remainingQuantity, requestedQuantity)) {
                return returnQuantity + calculateFreeProduct(stock, name);
            }
            if (isPromotionDiscountNotApplied(remainingQuantity, requestedQuantity)) {
                returnQuantity = calculatePromotionDiscountNotApplied(returnQuantity, requestedQuantity, name);
            }
            return returnQuantity;
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
        String input;
        try {
            input = InputView.inputYesOrNo();
        } catch (InputException e) {
            System.out.println(e.getMessage());
            return calculateFreeProduct(stock, name); // 예외 발생 시 다시 호출하여 입력 재시도
        }
        if (input.equals("Y")) {
            freeQuantity++; // 하나 무료로 더 가져와야되니까 증정 추가
            receipt.addQuantityForItem(name, getQuantity); // 하나 무료로 더 가져와야 하니까 최종 구매하는 개수 하나더 추가 (영수증)
//            receipt.addEventDiscount(productRepository.findByName(name).get().getPrice());
            return getQuantity;
        }
        return 0;

    }

    private boolean isPromotionDiscountNotApplied(int remainingQuantity, int requestedQuantity) {
        return remainingQuantity < requestedQuantity;
    }


    private int calculatePromotionDiscountNotApplied(int returnQuantity, int requestedQuantity, String name) {
//        if (remainingQuantity < requestedQuantity) {
        System.out.printf(PROMOTION_DISCOUNT_NOT_APPLIED, name, requestedQuantity);
        String input;
        try {
            input = InputView.inputYesOrNo();
        } catch (InputException e) {
            System.out.println(e.getMessage());
            return calculatePromotionDiscountNotApplied(returnQuantity, requestedQuantity, name);
        }
        if (input.equals("N")) {
            receipt.setQuantityForItem(name, 0);
            receipt.removePurchasedItem(name);
            finalChoosing.remove(name);
            return 0;
        }
        return returnQuantity;
//        }
//        return requestedQuantity;
    }

    private void updatePromotionStockInFile(String productName, String promotionType, int newQuantity) {
        productService.updateProductStock(productName, promotionType, newQuantity);
    }
}