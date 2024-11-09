package store.domain;

public class Promotion {
    private final String promotionType;
    private int buy;
    private int get;
    private String startDate;
    private String endDate;

    public Promotion(String promotionType, int buy, int get, String startDate, String endDate) {
        this.promotionType = promotionType;
        this.buy = buy;
        this.get = get;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getPromotionType() {
        return promotionType;
    }

    public int getBuy() {
        return buy;
    }

    public int getGet() {
        return get;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }
}
