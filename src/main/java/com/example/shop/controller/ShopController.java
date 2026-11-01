package com.example.shop.controller;

import com.example.shop.cart.Cart;
import com.example.shop.cart.CartLine;
import com.example.shop.model.CustomerOrder;
import com.example.shop.model.Product;
import com.example.shop.repository.OrderRepository;
import com.example.shop.repository.ProductRepository;
import com.example.shop.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class ShopController {

    private final ProductRepository products;
    private final OrderRepository orders;
    private final OrderService orderService;
    private final Cart cart;

    public ShopController(ProductRepository products, OrderRepository orders,
                          OrderService orderService, Cart cart) {
        this.products = products;
        this.orders = orders;
        this.orderService = orderService;
        this.cart = cart;
    }

    /** Available on every page so the header can show the cart count. */
    @ModelAttribute("cartCount")
    public int cartCount() {
        return cart.count();
    }

    // ---------- Storefront ----------

    @GetMapping("/")
    public String home(@RequestParam(required = false) String q, Model model) {
        List<Product> list = (q == null || q.isBlank())
                ? products.findAll()
                : products.findByNameContainingIgnoreCase(q.trim());
        model.addAttribute("products", list);
        model.addAttribute("q", q);
        return "index";
    }

    @GetMapping("/product/{id}")
    public String product(@PathVariable Long id, Model model) {
        Product product = products.findById(id).orElse(null);
        if (product == null) {
            return "redirect:/";
        }
        model.addAttribute("product", product);
        return "product";
    }

    // ---------- Cart ----------

    @PostMapping("/cart/add/{id}")
    public String addToCart(@PathVariable Long id,
                            @RequestParam(defaultValue = "1") int qty,
                            RedirectAttributes redirect) {
        Product product = products.findById(id).orElse(null);
        if (product == null) {
            return "redirect:/";
        }
        int wanted = cart.getQuantity(id) + Math.max(qty, 1);
        if (wanted > product.getStock()) {
            redirect.addFlashAttribute("error",
                    "Sorry, only " + product.getStock() + " of \"" + product.getName() + "\" in stock.");
            return "redirect:/";
        }
        cart.add(id, Math.max(qty, 1));
        redirect.addFlashAttribute("message", "Added \"" + product.getName() + "\" to your cart.");
        return "redirect:/";
    }

    @GetMapping("/cart")
    public String viewCart(Model model) {
        List<CartLine> lines = cartLines();
        model.addAttribute("lines", lines);
        model.addAttribute("total", total(lines));
        return "cart";
    }

    @PostMapping("/cart/update/{id}")
    public String updateCart(@PathVariable Long id, @RequestParam int qty, RedirectAttributes redirect) {
        Product product = products.findById(id).orElse(null);
        if (product != null && qty > product.getStock()) {
            redirect.addFlashAttribute("error",
                    "Only " + product.getStock() + " of \"" + product.getName() + "\" in stock.");
            return "redirect:/cart";
        }
        cart.set(id, qty);
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove/{id}")
    public String removeFromCart(@PathVariable Long id) {
        cart.remove(id);
        return "redirect:/cart";
    }

    // ---------- Checkout ----------

    @GetMapping("/checkout")
    public String checkoutForm(Model model) {
        if (cart.isEmpty()) {
            return "redirect:/cart";
        }
        List<CartLine> lines = cartLines();
        model.addAttribute("lines", lines);
        model.addAttribute("total", total(lines));
        return "checkout";
    }

    @PostMapping("/checkout")
    public String placeOrder(@RequestParam String name,
                             @RequestParam String email,
                             @RequestParam String address,
                             Model model) {
        if (cart.isEmpty()) {
            return "redirect:/cart";
        }
        List<CartLine> lines = cartLines();

        if (name.isBlank() || email.isBlank() || address.isBlank()) {
            model.addAttribute("lines", lines);
            model.addAttribute("total", total(lines));
            model.addAttribute("error", "Please fill in all the fields.");
            return "checkout";
        }

        try {
            CustomerOrder order = orderService.placeOrder(name.trim(), email.trim(), address.trim(),
                    new java.util.LinkedHashMap<>(cart.getItems()));
            cart.clear();
            return "redirect:/order/" + order.getId();
        } catch (IllegalStateException e) {
            model.addAttribute("lines", lines);
            model.addAttribute("total", total(lines));
            model.addAttribute("error", e.getMessage());
            return "checkout";
        }
    }

    @GetMapping("/order/{id}")
    public String orderConfirmation(@PathVariable Long id, Model model) {
        CustomerOrder order = orders.findById(id).orElse(null);
        if (order == null) {
            return "redirect:/";
        }
        model.addAttribute("order", order);
        return "order";
    }

    // ---------- Simple admin (no login, local use only) ----------

    @GetMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("products", products.findAll());
        model.addAttribute("orders", orders.findAll());
        return "admin";
    }

    @PostMapping("/admin/products")
    public String addProduct(@RequestParam String name,
                             @RequestParam(defaultValue = "") String description,
                             @RequestParam BigDecimal price,
                             @RequestParam(defaultValue = "") String imageUrl,
                             @RequestParam(defaultValue = "10") int stock) {
        String image = imageUrl.isBlank()
                ? "https://picsum.photos/seed/" + name.replaceAll("\\s+", "") + "/400/300"
                : imageUrl;
        products.save(new Product(name, description, price, image, stock));
        return "redirect:/admin";
    }

    @PostMapping("/admin/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id) {
        cart.remove(id);
        products.deleteById(id);
        return "redirect:/admin";
    }

    // ---------- helpers ----------

    private List<CartLine> cartLines() {
        List<CartLine> lines = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : new ArrayList<>(cart.getItems().entrySet())) {
            Product product = products.findById(entry.getKey()).orElse(null);
            if (product == null) {
                cart.remove(entry.getKey());
            } else {
                lines.add(new CartLine(product, entry.getValue()));
            }
        }
        return lines;
    }

    private BigDecimal total(List<CartLine> lines) {
        return lines.stream().map(CartLine::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
