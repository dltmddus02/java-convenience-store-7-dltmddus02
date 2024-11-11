package store.service;

import java.util.List;
import store.domain.Product;
import store.domain.Receipt;

public class ReceiptService {
    public Receipt createReceipt(List<Product> products, int eventDiscount, int membershipDiscount) {
        Receipt receipt = new Receipt();

        return receipt;
    }

    public void printReceipt(Receipt receipt) {
        System.out.println("==============W 편의점================\n상품명\t수량\t금액");
        printProducts(receipt);
        System.out.println("=============증\t정===============");
        printFreeProducts(receipt);
        System.out.println("====================================");
        printAllMoney(receipt);
    }

    private void printProducts(Receipt receipt) {
        receipt.getPurchasedItems().stream()
                .filter(item -> item.getQuantity() > 0) // 개수가 0이 아닌 항목만 필터링
                .forEach(item -> System.out.println(
                        item.getProductName() + "\t" + (item.getQuantity()) + "\t"
                                + item.getAmount() * (item.getQuantity())));
    }

    private void printFreeProducts(Receipt receipt) {
        receipt.getPurchasedItems().stream()
                .filter(item -> item.getQuantity() > 0) // 개수가 0이 아닌 항목만 필터링
                .filter(item -> item.getFreeQuantity() > 0) // 개수가 0이 아닌 항목만 필터링
                .forEach(item -> System.out.println(
                        item.getProductName() + "\t" + (item.getFreeQuantity()) + "\t"
                                + item.getAmount() * (item.getFreeQuantity())));
    }

    private void printAllMoney(Receipt receipt) {
        int totalQuantity = receipt.getTotalQuantity();
        int totalFreeQuantity = receipt.getTotalFreeQuantity();

        System.out.println("총구매액" + "\t" + totalQuantity + "\t" + formatWithCommas(receipt.getTotalPurchaseAmount()));
        System.out.println("행사할인" + "\t\t-" + formatWithCommas(receipt.getEventDiscount()));
        System.out.println("멤버십할인" + "\t\t-" + formatWithCommas(receipt.getMembershipDiscount()));
        System.out.println(
                "내실돈" + "\t\t" + formatWithCommas((receipt.getTotalPurchaseAmount() - receipt.getEventDiscount())));

    }

    private String formatWithCommas(int value) {
        return String.format("%,d", value);
    }
}