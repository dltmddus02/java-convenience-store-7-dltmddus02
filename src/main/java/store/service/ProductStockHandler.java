package store.service;

import static store.view.input.exception.InputErrorMessage.STOCK_QUANTITY_NOT_FOUND;

import camp.nextstep.edu.missionutils.DateTimes;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import store.domain.Product;
import store.domain.Receipt;
import store.domain.Stock;
import store.repository.ProductRepository;
import store.view.input.InputView;
import store.view.input.exception.InputErrorMessage;
import store.view.input.exception.InputException;

public class ProductStockHandler {
    private final ProductRepository productRepository;
    private final ProductService productService;
    private Receipt receipt;
    private int freeQuantity = 0;

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
        Product product = validateProductAndStock(productName, requestedQuantity);

        receipt.addPurchasedItem(productName, requestedQuantity, product.getPrice());

        processStockReduction(product, requestedQuantity);
        receipt.setFreeQuantityForItem(productName, freeQuantity);
    }

    private Product validateProductAndStock(String productName, int requestedQuantity) {
        Product product = productRepository.findByName(productName)
                .orElseThrow(() -> new InputException(InputErrorMessage.NON_EXISTENT_PRODUCT));

        if (product.getAllQuantityAboutProduct() < requestedQuantity) {
            throw new InputException(STOCK_QUANTITY_NOT_FOUND);
        }
        return product;
    }

    private void processStockReduction(Product product, int requestedQuantity) {
        int neededQuantity = requestedQuantity;

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

        int unitQuantity = buyQuantity + getQuantity;

        try {
            while (requestedQuantity >= unitQuantity && remainingQuantity >= unitQuantity) {
                requestedQuantity -= unitQuantity;
                remainingQuantity -= unitQuantity;
                freeQuantity++;
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

        if (InputView.askFreeProductOffer(name, getQuantity)) {
            freeQuantity++;
            receipt.addQuantityForItem(name, getQuantity);
            return getQuantity;
        }
        return 0;
    }

    private boolean isPromotionDiscountNotApplied(int remainingQuantity, int requestedQuantity) {
        return remainingQuantity <= requestedQuantity;
    }


    private int calculatePromotionDiscountNotApplied(int returnQuantity, int requestedQuantity, String name) {
        if (!InputView.askDiscountNotApplied(name, requestedQuantity)) {
            receipt.setQuantityForItem(name, 0);
            receipt.removePurchasedItem(name);
            return 0;
        }
        return returnQuantity;
    }

    private void updatePromotionStockInFile(String productName, String promotionType, int newQuantity) {
        productService.updateProductStock(productName, promotionType, newQuantity);
    }
}
