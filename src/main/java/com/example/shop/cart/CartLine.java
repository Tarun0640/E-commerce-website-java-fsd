package com.example.shop.cart;

import com.example.shop.model.Product;

import java.math.BigDecimal;

/** One row of the cart page: a product, its quantity and the line total. */
public class CartLine {

    private final Product product;
    private final int quantity;

    public CartLine(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() { return product; }

    public int getQuantity() { return quantity; }

    public BigDecimal getSubtotal() {
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}
