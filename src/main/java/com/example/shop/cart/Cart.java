package com.example.shop.cart;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Shopping cart that lives in the user's HTTP session.
 * Stores productId -> quantity.
 */
@Component
@SessionScope
public class Cart {

    private final Map<Long, Integer> items = new LinkedHashMap<>();

    public void add(Long productId, int quantity) {
        items.merge(productId, quantity, Integer::sum);
    }

    public void set(Long productId, int quantity) {
        if (quantity <= 0) {
            items.remove(productId);
        } else {
            items.put(productId, quantity);
        }
    }

    public void remove(Long productId) {
        items.remove(productId);
    }

    public void clear() {
        items.clear();
    }

    public int getQuantity(Long productId) {
        return items.getOrDefault(productId, 0);
    }

    public int count() {
        return items.values().stream().mapToInt(Integer::intValue).sum();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public Map<Long, Integer> getItems() {
        return items;
    }
}
