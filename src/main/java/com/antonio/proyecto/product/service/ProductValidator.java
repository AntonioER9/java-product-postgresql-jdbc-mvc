package com.antonio.proyecto.product.service;

import com.antonio.proyecto.product.exception.InvalidProductDataException;
import com.antonio.proyecto.product.model.Product;

public class ProductValidator {

    public static void validate(Product product) throws InvalidProductDataException {
        if (product.getPrice() <= 0) {
            throw new InvalidProductDataException("El precio debe ser mayor a 0");
        }

        if (product.getStock() < 0) {
            throw new InvalidProductDataException("El stock no puede ser negativo");
        }
    }
}

