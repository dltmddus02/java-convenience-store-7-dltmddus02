package store.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import store.domain.Promotion;
import store.domain.PromotionField;
import store.repository.PromotionRepository;

public class PromotionService {
    private static final String PRODUCT_FILE_PATH = "src/main/resources/promotions.md";
    private static final String FILE_LOAD_ERROR = "프로모션 목록을 불러오는 중 오류가 발생했습니다.";
    private final PromotionRepository promotionRepository;

    public PromotionService(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    public void loadPromotionsFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(PRODUCT_FILE_PATH))) {
            readPromotionLines(br);
        } catch (IOException e) {
            System.err.println(FILE_LOAD_ERROR + ": " + e.getMessage());
        }
    }


    void readPromotionLines(BufferedReader br) throws IOException {
        br.readLine();
        String line;
        while ((line = br.readLine()) != null) {
            processEachPromotionLine(line);
        }
    }

    private void processEachPromotionLine(String line) {
        Promotion promotion = parsePromotionByComma(line);
        savePromotion(promotion);
    }

    private Promotion parsePromotionByComma(String line) {
        List<String> values = Arrays.asList(line.split(","));

        String promotionName = values.get(PromotionField.NAME.getIndex());
        int buy = Integer.parseInt(values.get(PromotionField.BUY.getIndex()));
        int get = Integer.parseInt(values.get(PromotionField.GET.getIndex()));
        String start_date = values.get(PromotionField.START_DATE.getIndex());
        String end_date = values.get(PromotionField.END_DATE.getIndex());

        return new Promotion(promotionName, buy, get, start_date, end_date);
    }


    private void savePromotion(Promotion promotion) {
        promotionRepository.savePromotion(promotion);
    }
}
