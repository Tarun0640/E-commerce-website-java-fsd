package com.example.shop.service;

import com.example.shop.model.CustomerOrder;
import com.example.shop.model.OrderItem;
import com.example.shop.model.Product;
import com.example.shop.repository.OrderRepository;
import com.example.shop.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class OrderService {

    private final ProductRepository products;
    private final OrderRepository orders;

    public OrderService(ProductRepository products, OrderRepository orders) {
        this.products = products;
        this.orders = orders;
    }

    /**
     * Creates an order from the cart contents and reduces stock.
     * Throws IllegalStateException if something is out of stock.
     */
    @Transactional
    public CustomerOrder placeOrder(String name, String email, String address, Map<Long, Integer> cartItems) {
        CustomerOrder order = new CustomerOrder();
        order.setCustomerName(name);
        order.setEmail(email);
        order.setAddress(address);

        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<Long, Integer> entry : cartItems.entrySet()) {
            Product product = products.findById(entry.getKey())
                    .orElseThrow(() -> new IllegalStateException("A product in your cart no longer exists."));
            int qty = entry.getValue();

            if (qty > product.getStock()) {
                throw new IllegalStateException("Not enough stock for \"" + product.getName()
                        + "\" (only " + product.getStock() + " left).");
            }

            product.setStock(product.getStock() - qty);
            products.save(product);

            OrderItem item = new OrderItem(product.getName(), product.getPrice(), qty);
            order.addItem(item);
            total = total.add(item.getSubtotal());
        }

        order.setTotal(total);
        return orders.save(order);
    }
}
