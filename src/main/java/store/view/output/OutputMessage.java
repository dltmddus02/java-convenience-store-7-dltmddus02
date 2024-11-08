package store.view.output;

public enum OutputMessage {
    WELCOME_MESSAGE("안녕하세요. W편의점입니다.\n현재 보유하고 있는 상품입니다.\n"),
    PRODUCT_DETAILS_FORMAT("- %s %,d원 %s %s\n"),
    ENTER_PRODUCT_NAME_AND_QUANTITY("구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])");

    private final String message;

    private OutputMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
