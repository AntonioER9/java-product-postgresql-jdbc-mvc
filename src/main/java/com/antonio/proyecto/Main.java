package com.antonio.proyecto;
import com.antonio.proyecto.category.model.CategoryDao;
import com.antonio.proyecto.db.ConnectionPool;
import com.antonio.proyecto.product.controller.ProductController;
import com.antonio.proyecto.product.interfaces.ProductRepository;
import com.antonio.proyecto.product.repository.ProductRepositoryServices;
import com.antonio.proyecto.product.service.ProductService;
import com.antonio.proyecto.product.view.ProductView;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {

        try(Connection connection = ConnectionPool.getConnection()){
            System.out.println("Conexión exitosa...");
            CategoryDao categoryDao = new CategoryDao(connection);
            ProductRepository repositoryServices = new ProductRepositoryServices(connection, categoryDao);
            ProductService productService = new ProductService(repositoryServices);
            ProductController controller = new ProductController(productService);
            ProductView view = new ProductView(controller);
            view.showMenu();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionPool.closePool();
        }



    }
}