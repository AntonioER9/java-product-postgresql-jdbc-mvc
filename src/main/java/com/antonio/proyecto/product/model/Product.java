package com.antonio.proyecto.product.model;

import com.antonio.proyecto.category.model.Category;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Product {
    private Long id;
    private String name;
    private double price;
    private int stock;
    private Category category;

    public Product(String name, double price, int stock, Category category) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.category = category;
    }
}