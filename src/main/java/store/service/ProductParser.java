package store.service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProductParser {
    private static final int NAME = 1;
    private static final int PRICE = 2;

    private static final String PRODUCT_PATTERN = "[ㄱ-ㅎ가-힣a-zA-Z]+";
    private static final String NUMBER_PATTERN = "[0-9]+";
    private static final String SINGLE_PATTERN = String.format("\\[(%s)-(%s)]", PRODUCT_PATTERN, NUMBER_PATTERN);
    private static final Pattern PATTERN = Pattern.compile(SINGLE_PATTERN);

    public static Map<String, Integer> parseProducts(String productNameAndQuantity) {
        String[] products = splitProducts(productNameAndQuantity);
        return extractProductsNameAndQuantity(products);
    }

    private static String[] splitProducts(String productNameAndQuantity) {
        return productNameAndQuantity.split(",");
    }

    private static Map<String, Integer> extractProductsNameAndQuantity(String[] products) {
        Map<String, Integer> productsMap = new HashMap<>();

        for (String product : products) {
            addProductToMap(product, productsMap);
        }
        return productsMap;
    }

    private static void addProductToMap(String product, Map<String, Integer> productsMap) {
        Matcher matcher = PATTERN.matcher(product);
        if (matcher.matches()) {
            String productName = matcher.group(NAME);
            int quantity = Integer.parseInt(matcher.group(PRICE));
            productsMap.put(productName, quantity);
        }
    }

}
