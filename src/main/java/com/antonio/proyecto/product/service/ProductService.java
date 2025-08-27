package com.antonio.proyecto.product.service;

import com.antonio.proyecto.category.model.Category;
import com.antonio.proyecto.db.ConnectionPool;
import com.antonio.proyecto.product.exception.InvalidProductDataException;
import com.antonio.proyecto.product.exception.ProductNotFoundException;
import com.antonio.proyecto.product.interfaces.ProductRepository;
import com.antonio.proyecto.product.model.Product;
import com.antonio.proyecto.product.model.ProductCategory;
import com.antonio.proyecto.product.repository.ProductRepositoryServices;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() throws InvalidProductDataException {
        return productRepository.findAll();
    }

    public List<Product> getAllProductsByCategory(ProductCategory category){
        return productRepository.findByCategory(category);
    }

    public Optional<Product> getProductById(Long id){
        return productRepository.findById(id);
    }

    public Optional<Product> getProductByIdDB(Long id) throws SQLException {
        try(Connection connection = ConnectionPool.getConnection()){
            return productRepository.findByIdDB(connection, id);
        }
    }

    public void saveProduct(Product product) throws InvalidProductDataException, ProductNotFoundException, SQLException {
        ProductValidator.validate(product);
        Connection connection = null;
        try{
            connection = ConnectionPool.getConnection();
            connection.setAutoCommit(false);

            Optional<Category> optionalCategory = ((ProductRepositoryServices)productRepository)
                    .getCategoryDao().findCategoryByName(connection, product.getName());

            if(optionalCategory.isPresent()){
                if(!productRepository.existsById(product.getId())) {
                    product.setCategory(optionalCategory.get());
                }else {
                    throw new ProductNotFoundException("El producto con el ID: " + product.getId() + " que desea ya existe.");
                }
            }else{
                Optional<Category> optionalNewCategory = ((ProductRepositoryServices)productRepository)
                        .getCategoryDao().save(connection, product.getCategory());
                optionalNewCategory.ifPresent(product::setCategory);
            }
            productRepository.save(connection, product);
            connection.commit();
            System.out.println("El producto ha sido agregado.");
        }catch (SQLException | InvalidProductDataException | ProductNotFoundException e){
            if(connection!=null){
                connection.rollback();
            }
            throw e;
        }finally {
            if(connection!=null){
                try{
                    connection.setAutoCommit(true);
                    connection.close();
                }catch (SQLException e){
                    System.out.println("Error al cerrar la conexión...");
                }
            }
        }

    }

    public void deleteProduct(Long id) throws ProductNotFoundException, SQLException {

        Connection connection = null;
        try{
            connection = ConnectionPool.getConnection();
            connection.setAutoCommit(false);

            Optional<Product> optionalProduct = productRepository.findById(id);
            if(optionalProduct.isPresent()) {
                productRepository.delete(connection, id);
                connection.commit();
                System.out.println("El producto con ID: " + id + " ha sido eliminado.");
            }else {
                throw new ProductNotFoundException("El producto con el ID: " + id + " que desea eliminar no existe.");
            }
        }catch (SQLException | ProductNotFoundException e){
            if(connection!=null){
                connection.rollback();
            }
            throw e;
        }finally {
            if(connection!=null){
                try{
                    connection.setAutoCommit(true);
                    connection.close();
                }catch (SQLException e){
                    System.out.println("Error al cerrar la conexión...");
                }
            }
        }
    }

    public void updateProduct(Product product) throws ProductNotFoundException, SQLException {

        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection(); // ¡CAMBIO AQUÍ!
            connection.setAutoCommit(false);

            Optional<Product> optionalProduct = ((ProductRepositoryServices)productRepository)
                    .getDao().findById(connection, product.getId());
            if(optionalProduct.isPresent()) {
                Optional<Category> category = ((ProductRepositoryServices)productRepository).getCategoryDao()
                        .findCategoryByName(connection, product.getCategory().getName());
                if(category.isPresent()){
                    product.setCategory(category.get());
                    System.out.println("El producto con ID: " + product.getId() + " ha sido actualizado.");
                }else{
                    Optional<Category> newCategory = ((ProductRepositoryServices)productRepository)
                            .getCategoryDao().save(connection, product.getCategory());
                    newCategory.ifPresent(product::setCategory);
                }
                productRepository.update(connection, Optional.of(product));
                connection.commit();
            }else {
                throw new ProductNotFoundException("El producto con el ID: " + product.getId() + "que desea eliminar no existe.");
            }
        }catch (SQLException | ProductNotFoundException e){
            if(connection!=null){
                connection.rollback();
            }
            throw e;
        }finally {
            if(connection!=null){
                try{
                    connection.setAutoCommit(true);
                    connection.close();
                }catch (SQLException e){
                    System.out.println("Error al cerrar la conexión...");
                }
            }
        }
    }
}