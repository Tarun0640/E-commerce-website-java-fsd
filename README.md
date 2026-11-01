# Simple Shop (Spring Boot E-Commerce)

A small e-commerce app you can run on your laptop in a minute.
No database install, no login setup, no front-end build.

**Stack:** Java 17, Spring Boot 3, Spring MVC + Thymeleaf, Spring Data JPA, H2 in-memory database.

## Features

- Product listing with search
- Product detail page
- Shopping cart (kept in your browser session): add, change quantity, remove
- Checkout with name, email and address (cash on delivery, no real payment)
- Order confirmation page and stock reduction
- Simple admin page at `/admin`: add or delete products, see orders (no login, for local use only)
- 6 sample products loaded at startup

## What you need

1. **JDK 17 or newer** (check with `java -version`)
2. **Maven** (check with `mvn -version`). Install from https://maven.apache.org/download.cgi if you don't have it.
   Or just open the folder in IntelliJ IDEA / VS Code / Eclipse, which bring their own Maven.

## Run it

Open a terminal in this folder (the one with `pom.xml`) and run:

```
mvn spring-boot:run
```

Then open **http://localhost:8080** in your browser.

To stop the app press `Ctrl + C` in the terminal.

### Run from an IDE instead

Open the folder as a Maven project and run the `main` method in
`src/main/java/com/example/shop/ShopApplication.java`.

## Pages

| URL | What it does |
|-----|--------------|
| `/` | Product list (add `?q=watch` to search) |
| `/product/{id}` | Product details |
| `/cart` | Your cart |
| `/checkout` | Enter delivery details and place the order |
| `/order/{id}` | Order confirmation |
| `/admin` | Manage products and view orders |
| `/h2-console` | Browse the database (JDBC URL `jdbc:h2:mem:shopdb`, user `sa`, empty password) |

## Good to know

- The database is **in memory**: all products you add and all orders are reset every time you restart the app.
  The 6 sample products come back from `config/DataLoader.java`.
  To keep data between restarts, change the datasource URL in `application.properties` to
  `jdbc:h2:file:./data/shopdb` and set `spring.jpa.hibernate.ddl-auto=update`
  (and remove the sample-data loader or guard it with `if (repo.count() == 0)`).
- Product images come from picsum.photos, so they need an internet connection. Everything else works offline.
- If port 8080 is busy, change `server.port` in `src/main/resources/application.properties`.
- The admin page has no password. Don't expose this app to the internet as-is.

## Project layout

```
src/main/java/com/example/shop/
  ShopApplication.java        app entry point
  model/                      Product, CustomerOrder, OrderItem (JPA entities)
  repository/                 Spring Data repositories
  cart/                       session-scoped Cart and CartLine
  service/OrderService.java   places an order and reduces stock
  controller/ShopController  all web pages
  config/DataLoader.java      sample products
src/main/resources/
  templates/                  Thymeleaf pages
  static/style.css            styling
  application.properties      settings
```
