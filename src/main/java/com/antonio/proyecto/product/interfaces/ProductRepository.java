package com.antonio.proyecto.product.interfaces;

import com.antonio.proyecto.product.exception.InvalidProductDataException;
import com.antonio.proyecto.product.exception.ProductNotFoundException;
import com.antonio.proyecto.product.model.Product;
import com.antonio.proyecto.product.model.ProductCategory;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    List<Product> findAll() throws InvalidProductDataException;
    Optional<Product> findById(Long id);
    void save(Product product) throws InvalidProductDataException;
    void delete(Long id);
    List<Product> findByCategory(ProductCategory category);
    void update(Optional<Product> product) throws ProductNotFoundException;
    boolean existsById(Long id);
}
