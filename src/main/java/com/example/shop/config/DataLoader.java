package com.example.shop.config;

import com.example.shop.model.Product;
import com.example.shop.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

/** Adds a few sample products every time the app starts (the DB is in memory). */
@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadProducts(ProductRepository repo) {
        return args -> {
            repo.save(new Product("Wireless Headphones",
                    "Over-ear Bluetooth headphones with 30-hour battery life.",
                    new BigDecimal("2499.00"), "https://picsum.photos/seed/headphones/400/300", 25));
            repo.save(new Product("Smart Watch",
                    "Fitness tracker with heart-rate monitor and sleep tracking.",
                    new BigDecimal("3999.00"), "https://picsum.photos/seed/watch/400/300", 15));
            repo.save(new Product("Backpack",
                    "Water-resistant 25L backpack with laptop sleeve.",
                    new BigDecimal("1299.00"), "https://picsum.photos/seed/backpack/400/300", 40));
            repo.save(new Product("Coffee Mug",
                    "Insulated stainless-steel mug that keeps drinks hot for 6 hours.",
                    new BigDecimal("499.00"), "https://picsum.photos/seed/mug/400/300", 100));
            repo.save(new Product("Desk Lamp",
                    "LED desk lamp with adjustable brightness and USB charging port.",
                    new BigDecimal("899.00"), "https://picsum.photos/seed/lamp/400/300", 30));
            repo.save(new Product("Running Shoes",
                    "Lightweight breathable running shoes for everyday use.",
                    new BigDecimal("2199.00"), "https://picsum.photos/seed/shoes/400/300", 20));
        };
    }
}
