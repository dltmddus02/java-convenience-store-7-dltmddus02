package store.domain;

public enum ProductField {
    NAME(0),
    PRICE(1),
    QUANTITY(2),
    PROMOTION_TYPE(3);

    private final int index;

    ProductField(final int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
