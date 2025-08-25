package com.antonio.proyecto;
import com.antonio.proyecto.product.controller.ProductController;
import com.antonio.proyecto.product.interfaces.ProductRepository;
import com.antonio.proyecto.product.repository.ProductRepositoryServices;
import com.antonio.proyecto.product.service.ProductService;
import com.antonio.proyecto.product.view.ProductView;

public class Main {
    public static void main(String[] args) {

        ProductRepository repositoryServices = new ProductRepositoryServices();
        ProductService productService = new ProductService(repositoryServices);
        ProductController controller = new ProductController(productService);
        ProductView view = new ProductView(controller);
        view.showMenu();

    }
}