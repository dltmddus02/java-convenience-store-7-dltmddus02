package store.service;

import store.domain.Receipt;

public class ReceiptService {
    private static final String HEADER = "============== W 편의점 ================\n상품명\t수량\t금액";
    private static final String FREE_PRODUCT_HEADER = "============= 증정 =============";
    private static final String FOOTER = "====================================";
    private static final String TOTAL_AMOUNT_LABEL = "총구매액";
    private static final String EVENT_DISCOUNT_LABEL = "행사할인";
    private static final String MEMBERSHIP_DISCOUNT_LABEL = "멤버십할인";
    private static final String FINAL_AMOUNT_LABEL = "내실돈";

    public ReceiptService(Receipt receipt) {
        receipt.setBeforeAmount();
        receipt.setEventDiscount();
        receipt.setMembershipDiscount();
        receipt.setFinalAmount();
    }

    public void printReceipt(Receipt receipt) {
        System.out.println(HEADER);
        printProducts(receipt);
        System.out.println(FREE_PRODUCT_HEADER);
        printFreeProducts(receipt);
        System.out.println(FOOTER);
        printAllMoney(receipt);
    }

    private void printProducts(Receipt receipt) {
        receipt.getPurchasedItems().stream()
                .filter(item -> item.getQuantity() > 0)
                .forEach(item -> System.out.println(
                        item.getProductName() + "\t" + (item.getQuantity()) + "\t"
                                + item.getAmount() * (item.getQuantity())));
    }

    private void printFreeProducts(Receipt receipt) {
        receipt.getPurchasedItems().stream()
                .filter(item -> item.getQuantity() > 0)
                .filter(item -> item.getFreeQuantity() > 0)
                .forEach(item -> System.out.println(
                        item.getProductName() + "\t" + (item.getFreeQuantity()) + "\t"
                                + item.getAmount() * (item.getFreeQuantity())));
    }

    private void printAllMoney(Receipt receipt) {
        System.out.println(
                TOTAL_AMOUNT_LABEL + "\t" + receipt.getTotalQuantity() + "\t" + formatWithCommas(
                        receipt.getBeforeAmount()));
        System.out.println(EVENT_DISCOUNT_LABEL + "\t\t-" + formatWithCommas(receipt.getEventDiscount()));
        System.out.println(MEMBERSHIP_DISCOUNT_LABEL + "\t\t-" + formatWithCommas(receipt.getMembershipDiscount()));
        System.out.println(FINAL_AMOUNT_LABEL + "\t\t" + formatWithCommas(
                (receipt.getFinalAmount())));

    }

    private String formatWithCommas(int value) {
        return String.format("%,d", value);
    }
}
