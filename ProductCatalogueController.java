package com.noch.noch.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ProductCatalogueController {

    @FXML private TextField searchField;
    private java.util.List<String[]> dbProductList = null;
    @FXML private FlowPane  productGrid;
    @FXML private Label     emptyLabel;
    @FXML private Button    viewOrderBtn;

    // [id, name, category, price, stock, imageFile]
    private final String[][] PRODUCTS = {
        {"PROD-001","Classic White Tee",          "T-Shirts",   "19.99", "120","white_tee.jpg"},
        {"PROD-002","Slim Fit Jeans",              "Trousers",   "49.99", "85", "trousers.jpg"},
        {"PROD-003","Floral Summer Dress",         "Dresses",    "54.99", "40", "dress.jpg"},
        {"PROD-004","Leather Jacket",              "Jackets",    "129.99","20", "jacket.jpg"},
        {"PROD-005","Wool Overcoat",               "Coats",      "129.99","15", "coats.jpg"},
        {"PROD-006","Striped Polo Shirt",          "Shirts",     "34.99", "60", "shirt.jpg"},
        {"PROD-007","Chino Trousers",              "Trousers",   "49.99", "70", "trousers.jpg"},
        {"PROD-008","Hijab Set",                   "Accessories","29.99", "50", ""},
        {"PROD-009","Oxford Button Shirt",         "Shirts",     "44.99", "55", "shirt.jpg"},
        {"PROD-010","Knit Cardigan",               "Knitwear",   "39.99", "45", "knitwear_women.jpg"},
        {"PROD-011","Graphic Hoodie",              "Hoodies",    "44.99", "90", "hoodies.jpg"},
        {"PROD-012","Silk Blouse",                 "Tops",       "59.99", "30", ""},
        {"PROD-013","Tailored Blazer",             "Suits",      "149.99","12", "suits.jpg"},
        {"PROD-014","High-Waist Joggers",          "Activewear", "34.99", "100",""},
        {"PROD-015","Linen Shirt",                 "Shirts",     "39.99", "65", "shirt.jpg"},
        {"PROD-016","Flared Midi Skirt",           "Skirts",     "44.99", "35", "skirts.jpg"},
        {"PROD-017","Puffer Jacket",               "Jackets",    "89.99", "25", "jacket.jpg"},
        {"PROD-018","Cropped Sweatshirt",          "Hoodies",    "29.99", "0",  "hoodies.jpg"},
        {"PROD-019","Formal Trousers",             "Suits",      "69.99", "40", "suits.jpg"},
        {"PROD-020","Printed Maxi Dress",          "Dresses",    "64.99", "28", "dress.jpg"},
        {"PROD-021","Women's Satin Evening Dress", "Dresses",    "142.00","25", "satin_dress.jpg"}
    };

    @FXML
    public void initialize() {
        // ── Load products from database ───────────────────────
        java.util.List<String[]> dbProducts = ProductDAO.getInstance().getAllProducts();
        if (!dbProducts.isEmpty()) {
            // Convert price to always show 2 decimal places
            for (String[] p : dbProducts) {
                try {
                    double price = Double.parseDouble(p[3]);
                    p[3] = String.format("%.2f", price);
                } catch (Exception ignored) {}
            }
            dbProductList = dbProducts;
        }
        updateBagButton();
        renderProducts("");
    }

    @FXML private void applySearch() {
        renderProducts(searchField.getText().toLowerCase());
    }

    private void renderProducts(String search) {
        productGrid.getChildren().clear();
        List<String[]> filtered = new ArrayList<>();
        String[][] source = (dbProductList != null)
            ? dbProductList.toArray(new String[0][])
            : PRODUCTS;
        for (String[] p : source) {
            if (p[1].toLowerCase().contains(search) || p[2].toLowerCase().contains(search))
                filtered.add(p);
        }
        if (filtered.isEmpty()) {
            emptyLabel.setVisible(true);
            emptyLabel.setManaged(true);
        } else {
            emptyLabel.setVisible(false);
            emptyLabel.setManaged(false);
            for (String[] p : filtered)
                productGrid.getChildren().add(buildProductCard(p));
        }
    }

    private VBox buildProductCard(String[] product) {
        boolean inStock = Integer.parseInt(product[4]) > 0;
        int cartQty = 0;
        if (Session.cart.containsKey(product[0]))
            cartQty = Integer.parseInt(Session.cart.get(product[0])[5]);

        VBox card = new VBox(0);
        card.setPrefWidth(220);
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #e8e8e8;" +
            "-fx-border-width: 1;" +
            (inStock ? "" : "-fx-opacity:0.55;")
        );

        // ── Image Box ──────────────────────────────────────────
        StackPane imgBox = new StackPane();
        imgBox.setPrefHeight(260);
        imgBox.setPrefWidth(220);

        String imageFile = product[5];
        if (imageFile != null && !imageFile.isEmpty()) {
            loadImage(imgBox, imageFile, product[2]);
        } else {
            buildPlaceholder(imgBox, product[2]);
        }

        // Sold out overlay
        if (!inStock) {
            Label soldOut = new Label("SOLD OUT");
            soldOut.setStyle(
                "-fx-background-color:rgba(0,0,0,0.65);" +
                "-fx-text-fill:white;-fx-font-size:11;" +
                "-fx-font-weight:bold;-fx-font-family:Arial;" +
                "-fx-letter-spacing:2;-fx-padding:6 14;"
            );
            imgBox.getChildren().add(soldOut);
        }

        // In bag badge
        if (cartQty > 0) {
            Label cartBadge = new Label("IN BAG: " + cartQty);
            cartBadge.setStyle(
                "-fx-background-color:#000000;-fx-text-fill:white;" +
                "-fx-font-size:10;-fx-font-weight:bold;" +
                "-fx-font-family:Arial;-fx-letter-spacing:1;-fx-padding:4 10;"
            );
            StackPane.setAlignment(cartBadge, javafx.geometry.Pos.TOP_LEFT);
            StackPane.setMargin(cartBadge, new Insets(10,0,0,10));
            imgBox.getChildren().add(cartBadge);
        }

        // ── Product Info ───────────────────────────────────────
        VBox info = new VBox(4);
        info.setStyle("-fx-padding:14 16 6 16;-fx-background-color:white;");

        Label catLabel = new Label(product[2].toUpperCase());
        catLabel.setStyle("-fx-font-size:10;-fx-text-fill:#888888;-fx-font-family:Arial;-fx-letter-spacing:1;");

        Label nameLabel = new Label(product[1]);
        nameLabel.setWrapText(true);
        nameLabel.setStyle("-fx-font-size:14;-fx-font-weight:bold;-fx-text-fill:#000000;-fx-font-family:Arial;");

        Label stars = new Label("* * * * *");
        stars.setStyle("-fx-font-size:10;-fx-text-fill:#cccccc;-fx-font-family:Arial;");

        Label priceLabel = new Label("£" + product[3]);
        priceLabel.setStyle("-fx-font-size:16;-fx-font-weight:bold;-fx-text-fill:#000000;-fx-font-family:Georgia;");

        Label stockLabel = new Label(inStock ? "In Stock: " + product[4] : "Out of Stock");
        stockLabel.setStyle(
            "-fx-font-size:10;-fx-text-fill:" +
            (inStock ? "#22c55e" : "#cc0000") + ";-fx-font-family:Arial;"
        );

        info.getChildren().addAll(catLabel, nameLabel, stars, priceLabel, stockLabel);

        // ── Add Button ─────────────────────────────────────────
        String btnText = !inStock ? "OUT OF STOCK" : cartQty > 0 ? "ADD MORE" : "ADD TO ORDER";
        Button addBtn = new Button(btnText);
        addBtn.setMaxWidth(Double.MAX_VALUE);
        addBtn.setDisable(!inStock);
        VBox.setMargin(addBtn, new Insets(8,16,16,16));
        addBtn.setStyle(
            "-fx-background-color:" + (inStock ? "#000000" : "#e0e0e0") + ";" +
            "-fx-text-fill:" + (inStock ? "white" : "#888888") + ";" +
            "-fx-background-radius:0;-fx-padding:11 0;" +
            "-fx-font-size:10;-fx-font-weight:bold;" +
            "-fx-font-family:Arial;-fx-letter-spacing:2;" +
            "-fx-cursor:" + (inStock ? "hand" : "default") + ";"
        );
        addBtn.setOnAction(e -> {
            Session.addToCart(product);
            updateBagButton();
            renderProducts(searchField.getText().toLowerCase());
        });

        card.getChildren().addAll(imgBox, info, addBtn);
        return card;
    }

    private void loadImage(StackPane imgBox, String imageFile, String category) {
        try {
            // Get the URL of the image file
            java.net.URL url = getClass().getResource("/images/" + imageFile);

            if (url == null) {
                System.err.println("Image not found: /images/" + imageFile);
                buildPlaceholder(imgBox, category);
                return;
            }

            // Load using URL string — handles all formats (jpg, png, webp, gif)
            Image img = new Image(url.toExternalForm(), 220, 260, false, true);

            if (img.isError()) {
                System.err.println("Image error: " + imageFile + " -> " + img.getException());
                buildPlaceholder(imgBox, category);
                return;
            }

            ImageView iv = new ImageView(img);
            iv.setFitWidth(220);
            iv.setFitHeight(260);
            iv.setPreserveRatio(false);
            iv.setSmooth(true);
            imgBox.setStyle("-fx-background-color:#f0f0f0;");
            imgBox.getChildren().add(iv);

        } catch (Exception e) {
            System.err.println("Exception loading " + imageFile + ": " + e.getMessage());
            buildPlaceholder(imgBox, category);
        }
    }

    private void buildPlaceholder(StackPane imgBox, String category) {
        String bg = switch (category) {
            case "T-Shirts"    -> "#f5e6d3";
            case "Trousers"    -> "#e8e0f5";
            case "Dresses"     -> "#fce4ec";
            case "Jackets"     -> "#e0e8f5";
            case "Coats"       -> "#e8f0e0";
            case "Shirts"      -> "#fff8e1";
            case "Accessories" -> "#fce4ec";
            case "Knitwear"    -> "#f3e5f5";
            case "Hoodies"     -> "#e0f2f1";
            case "Tops"        -> "#fff3e0";
            case "Suits"       -> "#e8eaf6";
            case "Activewear"  -> "#e0f7fa";
            case "Skirts"      -> "#fce4ec";
            default            -> "#f5f5f5";
        };
        imgBox.setStyle("-fx-background-color:" + bg + ";");

        // Text only — no emojis to avoid rendering issues
        Label lbl = new Label(category);
        lbl.setStyle(
            "-fx-font-size:13;-fx-font-weight:bold;" +
            "-fx-text-fill:#bbbbbb;-fx-font-family:Arial;" +
            "-fx-letter-spacing:2;"
        );
        imgBox.getChildren().add(lbl);
    }

    private void updateBagButton() {
        int total = Session.cartTotal();
        viewOrderBtn.setDisable(total == 0);
        viewOrderBtn.setText(total == 0
            ? "VIEW BAG (0 ITEMS)"
            : "VIEW BAG (" + total + " ITEM" + (total != 1 ? "S" : "") + ")"
        );
    }

    @FXML private void proceedToOrderBuilder() {
        if (Session.cart.isEmpty()) return;
        navigateTo("/fxml/OrderBuilder.fxml");
    }

    private void navigateTo(String fxmlPath) {
        try {
            StackPane contentArea = (StackPane) productGrid
                .getScene().getRoot().lookup("#contentArea");
            if (contentArea != null) {
                Parent content = FXMLLoader.load(getClass().getResource(fxmlPath));
                contentArea.getChildren().setAll(content);
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}