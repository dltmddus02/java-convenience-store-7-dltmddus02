package store.view.input;

import static store.view.output.OutputMessage.CONTINUE_SHOPPING;
import static store.view.output.OutputMessage.MEMBERSHIP_DISCOUNT;
import static store.view.output.OutputMessage.PROMOTION_DISCOUNT_NOT_APPLIED;
import static store.view.output.OutputMessage.PROMOTION_FREE_PRODUCT_OFFER;

import camp.nextstep.edu.missionutils.Console;

public class InputView {
    public static String inputProductNameAndQuantity() {
        String ProductNameAndQuantity = Console.readLine();
        InputValidator.validate(ProductNameAndQuantity);
        return ProductNameAndQuantity;
    }

    public static String inputYesOrNo() {
        String YesOrNo = Console.readLine();
        InputValidator.validateYesOrNo(YesOrNo);
        return YesOrNo;
    }

    public static boolean askFreeProductOffer(String productName, int freeQuantity) {
        System.out.printf(PROMOTION_FREE_PRODUCT_OFFER.getMessage(), productName, freeQuantity);
        return askYesOrNo();
    }

    public static boolean askDiscountNotApplied(String productName, int requestedQuantity) {
        System.out.printf(PROMOTION_DISCOUNT_NOT_APPLIED.getMessage(), productName, requestedQuantity);
        return askYesOrNo();
    }

    public static boolean askMembershipDiscount() {
        System.out.println(MEMBERSHIP_DISCOUNT.getMessage());
        return askYesOrNo();
    }

    public static boolean askToContinueShopping() {
        System.out.println(CONTINUE_SHOPPING.getMessage());
        return askYesOrNo();
    }

    private static boolean askYesOrNo() {
        String input;
        try {
            input = inputYesOrNo();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return askYesOrNo();
        }
        return input.equalsIgnoreCase("Y");
    }

}
