package store.view.output;

public enum OutputMessage {
    WELCOME_MESSAGE("안녕하세요. W편의점입니다.\n현재 보유하고 있는 상품입니다.\n"),
    PRODUCT_DETAILS_FORMAT("- %s %,d원 %s %s\n"),
    ENTER_PRODUCT_NAME_AND_QUANTITY("구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])"),
    PROMOTION_DISCOUNT_NOT_APPLIED("현재 %s %d개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)\n"),
    PROMOTION_FREE_PRODUCT_OFFER("현재 %s은(는) %d개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)\n"),
    MEMBERSHIP_DISCOUNT("멤버십 할인을 받으시겠습니까? (Y/N)"),
    CONTINUE_SHOPPING("감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)");


    private final String message;

    OutputMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
