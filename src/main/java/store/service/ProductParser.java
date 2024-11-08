package store.service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProductParser {
    private static final int NAME = 1;
    private static final int PRICE = 2;

    public static Map<String, Integer> parseProducts(String productNameAndQuantity) {
        String[] products = productNameAndQuantity.split(",");
        Map<String, Integer> productsMap = new HashMap<>();

        String productPattern = "[ㄱ-ㅎ가-힣a-zA-Z]+";
        String numberPattern = "[0-9]+";
        String singlePattern = String.format("\\[(%s)-(%s)]", productPattern, numberPattern);

        Pattern pattern = Pattern.compile(singlePattern);

        for (String product : products) {
            Matcher matcher = pattern.matcher(product);
            if (matcher.matches()) {
                String productName = matcher.group(NAME);
                int quantity = Integer.parseInt(matcher.group(PRICE));
                productsMap.put(productName, quantity);
            }
        }
        return productsMap;
    }
}
