package store.view.output;

import static store.view.output.OutputMessage.ENTER_PRODUCT_NAME_AND_QUANTITY;
import static store.view.output.OutputMessage.PRODUCT_DETAILS_FORMAT;
import static store.view.output.OutputMessage.WELCOME_MESSAGE;

public class OutputView {
    public static void printWelcomeMessage() {
        System.out.println(WELCOME_MESSAGE.getMessage());
    }

    public static void printProductList(String name, int price, int quantity, String promotion) {
        String outputFormatQuantity = formatQuantityForDisplay(quantity);
        String outputFormatPromotion = formatPromotionForDisplay(promotion);

        System.out.printf(PRODUCT_DETAILS_FORMAT.getMessage(), name, price, outputFormatQuantity,
                outputFormatPromotion);
    }

    private static String formatQuantityForDisplay(int quantity) {
        if (quantity == 0) {
            return "재고 없음";
        }
        return quantity + "개";
    }

    private static String formatPromotionForDisplay(String promotionType) {
        if (promotionType.equals("null")) {
            return "";
        }
        return promotionType;
    }

    public static void printProductNameAndQuantity() {
        System.out.println(ENTER_PRODUCT_NAME_AND_QUANTITY.getMessage());
    }
}
