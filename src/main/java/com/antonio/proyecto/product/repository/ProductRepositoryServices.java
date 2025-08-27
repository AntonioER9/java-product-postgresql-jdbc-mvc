package com.antonio.proyecto.product.repository;

import com.antonio.proyecto.category.model.CategoryDao;
import com.antonio.proyecto.db.ConnectionPool;
import com.antonio.proyecto.product.exception.InvalidProductDataException;
import com.antonio.proyecto.product.exception.ProductNotFoundException;
import com.antonio.proyecto.product.interfaces.ProductRepository;
import com.antonio.proyecto.product.model.Product;
import com.antonio.proyecto.product.model.ProductCategory;
import com.antonio.proyecto.product.persistence.ProductDAO;
import lombok.Getter;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
public class ProductRepositoryServices implements ProductRepository {
    private final List<Product> products;
    private final ProductDAO dao;
    private final CategoryDao categoryDao;

    public ProductRepositoryServices(CategoryDao categoryDao) throws SQLException, InvalidProductDataException {
        dao = new ProductDAO(categoryDao);
        this.categoryDao = categoryDao;
        try(Connection connection = ConnectionPool.getConnection()){
            products = dao.findAll(connection);
        }catch (SQLException e){
            throw new InvalidProductDataException("Error al inicializar la lista " + e.getMessage());
        }
    }

    @Override
    public List<Product> findAll() throws InvalidProductDataException {
        if(products.isEmpty()){
            throw new InvalidProductDataException("La lista esta vacía");
        }
        return new ArrayList<>(products);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return products.stream()
                .filter(product -> product.getId().equals(id))
                .findFirst();
    }

    public Optional<Product> findByIdDB(Connection connection, Long id) throws SQLException {
        return dao.findById(connection, id);
    }

    @Override
    public Product save(Connection connection, Product product) throws SQLException {
        Product newProduct = dao.save(connection, product);
        products.add(newProduct);
        return newProduct;
    }

    @Override
    public void delete(Connection connection, Long id) throws SQLException {
        products.removeIf(product -> product.getId().equals(id));
        dao.delete(connection, id);
    }

    @Override
    public List<Product> findByCategory(ProductCategory category) {
        return products.stream()
                .filter(product -> product.getCategory().equals(category))
                .toList();
    }

    @Override
    public void update(Connection connection, Optional<Product> existingProductOptional) throws ProductNotFoundException, SQLException {
        if(existingProductOptional.isPresent()){
            Long idToUpdate = existingProductOptional.get().getId();
            int index = findIndexById(idToUpdate);
            if (index != -1) {
                products.set(index, existingProductOptional.get());
                dao.update(connection, existingProductOptional.get());
            } else {
                throw new ProductNotFoundException("El producto que quiere actualizar no existe.");
            }
        } else {
            throw new ProductNotFoundException("El producto que quiere actualizar no existe.");
        }
    }

    @Override
    public boolean existsById(Long id) {
        return products.stream().anyMatch(product -> product.getId().equals(id));
    }

    public int findIndexById(Long id){
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }
}