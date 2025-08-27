package com.antonio.proyecto;
import com.antonio.proyecto.category.model.CategoryDao;
import com.antonio.proyecto.db.ConnectionPool;
import com.antonio.proyecto.product.controller.ProductController;
import com.antonio.proyecto.product.exception.InvalidProductDataException;
import com.antonio.proyecto.product.interfaces.ProductRepository;
import com.antonio.proyecto.product.repository.ProductRepositoryServices;
import com.antonio.proyecto.product.service.ProductService;
import com.antonio.proyecto.product.view.ProductView;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        try{
            CategoryDao categoryDao = new CategoryDao();
            ProductRepository repositoryServices = new ProductRepositoryServices(categoryDao);
            ProductService productService = new ProductService(repositoryServices);
            ProductController controller = new ProductController(productService);
            ProductView view = new ProductView(controller);
            view.showMenu();
        }catch (SQLException | InvalidProductDataException e){
            System.out.println(e.getMessage());
        }


    }
}