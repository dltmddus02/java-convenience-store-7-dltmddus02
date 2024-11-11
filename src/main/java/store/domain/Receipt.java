package store.domain;

import java.util.ArrayList;
import java.util.List;
import store.dto.ReceiptItem;

public class Receipt {
    private List<ReceiptItem> purchasedItems;
    private List<ReceiptItem> promotionItems;
    private int beforeAmount;
    private int eventDiscount;
    private int membershipDiscount;
    private int finalAmount;

    public Receipt() {
        this.purchasedItems = new ArrayList<>();
        this.promotionItems = new ArrayList<>();
        this.beforeAmount = 0;
        this.eventDiscount = 0;
        this.membershipDiscount = 0;
        this.finalAmount = 0;
    }

    public List<ReceiptItem> getPurchasedItems() {
        return purchasedItems;
    }

    public void addPurchasedItem(String productName, int quantity, int amount) {
        if (purchasedItems == null) {
            purchasedItems = new ArrayList<>();
        }
        boolean isDuplicate = purchasedItems.stream()
                .anyMatch(item -> item.getProductName().equals(productName));
        if (isDuplicate) {
            return;
        }
        ReceiptItem item = new ReceiptItem(productName, quantity, amount);
        purchasedItems.add(item);
        calculateTotalAmount();
    }

    public void setQuantityForItem(String productName, int newQuantity) {
        purchasedItems.stream()
                .filter(item -> item.getProductName().equals(productName))
                .findFirst()
                .ifPresent(item -> item.setQuantity(newQuantity));
        calculateTotalAmount();
    }

    public void addQuantityForItem(String productName, int additionalQuantity) {
        purchasedItems.stream()
                .filter(item -> item.getProductName().equals(productName))
                .findFirst()
                .ifPresent(item -> item.setQuantity(item.getQuantity() + additionalQuantity));
        calculateTotalAmount();
    }

    public void setFreeQuantityForItem(String productName, int freeQuantity) {
        purchasedItems.stream()
                .filter(item -> item.getProductName().equals(productName))
                .findFirst()
                .ifPresent(item -> item.setFreeQuantity(freeQuantity));
        calculateTotalAmount();
    }

    public void removePurchasedItem(String productName) {
        purchasedItems.removeIf(item -> item.getProductName().equals(productName));
        calculateTotalAmount();
    }

    public void calculateEventDiscount() {
        eventDiscount = purchasedItems.stream()
                .mapToInt(item -> item.getFreeQuantity() * item.getAmount())
                .sum();
        applyDiscounts();
    }

    public void setMembershipDiscount() {
        int discountAmount = 0;

        discountAmount = purchasedItems.stream()
                .filter(item -> item.getFreeQuantity() == 0)
                .mapToInt(item -> item.getAmount() * item.getQuantity())
                .sum();

        membershipDiscount = (int) (discountAmount * 0.3);
        if (membershipDiscount >= 8000) {
            membershipDiscount = 8000;
        }
        applyDiscounts();
    }

    public int getMembershipDiscount() {
        return membershipDiscount;
    }


    public void addMembershipDiscount(int discountAmount) {
        membershipDiscount += discountAmount;
        applyDiscounts();
    }

    private void calculateTotalAmount() {
        finalAmount = purchasedItems.stream()
                .mapToInt(item -> item.getAmount() * (item.getQuantity()))
                .sum();
    }

    private void applyDiscounts() {
        finalAmount = beforeAmount - eventDiscount - membershipDiscount;
    }

    public int getBeforeAmount() {
        calculateTotalAmount();
        return beforeAmount;
    }

    public int getEventDiscount() {
        calculateEventDiscount();
        return eventDiscount;
    }


    public int getTotalQuantity() {
        return purchasedItems.stream()
                .mapToInt(ReceiptItem::getQuantity)
                .sum();
    }

    public int getTotalFreeQuantity() {
        return purchasedItems.stream()
                .mapToInt(ReceiptItem::getFreeQuantity)
                .sum();
    }


    public int getTotalPurchaseAmount() {
        return purchasedItems.stream()
                .mapToInt(item -> item.getAmount() * item.getQuantity())
                .sum();
    }

}
