package store.domain;

import java.util.ArrayList;
import java.util.List;

public class Product {
    private final String name;
    private final int price;
    private final List<Stock> stocks = new ArrayList<>();

    public Product(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public List<Stock> getStocks() {
        return stocks;
    }

    public void addStock(Stock stock) {
        stocks.add(stock);
    }

    public int getAllQuantityAboutProduct() {
        return stocks.stream()
                .mapToInt(Stock::getQuantity)
                .sum();
    }
}
