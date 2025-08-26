package com.antonio.proyecto.product.persistence;


import com.antonio.proyecto.category.model.Category;
import com.antonio.proyecto.category.model.CategoryDao;
import com.antonio.proyecto.product.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductDAO {

    private final Connection connection;
    private final CategoryDao categoryDao;

    public ProductDAO(Connection connection, CategoryDao categoryDao) {
        this.connection = connection;
        this.categoryDao = categoryDao;
    }

    public Product save(Product product) throws SQLException{
        String sql = "INSERT INTO products (name, price, stock, category_id) " +
                " VALUES (?, ?, ?, ?)";

        try (
                PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ){
            statement.setString(1, product.getName());
            statement.setDouble(2, product.getPrice());
            statement.setInt(3, product.getStock());
            statement.setLong(4, product.getCategory().getId());

            int rows = statement.executeUpdate();

            if(rows > 0){
                try (ResultSet resultSet = statement.getGeneratedKeys()){
                    if(resultSet.next()){
                        product.setId(resultSet.getLong(1));
                        System.out.println("producto ingresado correctamente");
                    }
                }
            }
        }
        return product;
    }

    public boolean existsById(Long id) throws SQLException {
        if (id == null) return false;

        String sql = "SELECT Count(*) FROM products WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()){
                if (resultSet.next()){
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public void update(Product product) throws SQLException {
        String sql = "UPDATE products SET name = ?, price = ?, stock = ?, category_id = ? " +
                " WHERE id=?";

        try (
                PreparedStatement statement = connection.prepareStatement(sql)
        ){
            statement.setString(1, product.getName());
            statement.setDouble(2, product.getPrice());
            statement.setInt(3, product.getStock());
            statement.setLong(4, product.getCategory().getId());
            statement.setLong(5, product.getId());

            int rows = statement.executeUpdate();



        }
    }

    public void delete(long id) throws SQLException{
        String sql = "DELETE FROM products WHERE id = ? ";

        try (
                PreparedStatement statement = connection.prepareStatement(sql)
        ){

            statement.setLong(1, id);

            int rows = statement.executeUpdate();
            showMessage(rows, "El producto fue eliminado", "El producto no existe...");

        }
    }

    public Optional<Product> findById(long id) throws SQLException {
        String sql = "Select p.id, p.name, p.price, p.stock, p.category_id, \n" +
                " c.name, as category_name \n" +
                "FROM  products p JOIN categories c ON p.category_id = c.id where p.id = ?";
        try (
                PreparedStatement statement = connection.prepareStatement(sql)
        ){
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()){
                if (resultSet.next()){
                    return Optional.of(mapResult(resultSet));
                }
            }
        }
        return Optional.empty();
    }

    public List<Product> findAll() throws SQLException{
        String sql = "Select p.id, p.name, p.price, p.stock, p.category_id, \n" +
                " c.name, as category_name \n" +
                "FROM  products p JOIN categories c ON p.category_id = c.id";
        List<Product> products = new ArrayList<>();
        try (
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ){
            while (resultSet.next()){
                Product product = mapResult(resultSet);
                products.add(product);
            }
        }
        return products;
    }

    public List<Product> findByCategoryId(Connection connection, Long categoryId) throws SQLException{
        String sql = "SELECT p.id, p.name, p.price, p.stock, p.category_id,\n" +
                "       c.name as category_name\n" +
                "FROM products p JOIN categories c ON p.category_id = c.id Where p.category_id=?";
        List<Product> products = new ArrayList<>();
        try (
                PreparedStatement statement = connection.prepareStatement(sql);
        ){
            statement.setLong(1, categoryId);
            try (ResultSet resultSet = statement.executeQuery()){
                while (resultSet.next()){
                    products.add(mapResult(resultSet));
                }
            }
        }
        return products;
    }

    private Product mapResult(ResultSet resultSet) throws SQLException {
        Long idCat = resultSet.getLong("category_id");
        String nameCat = resultSet.getString("category_name");
        Category category = new Category(idCat,  nameCat);
        Product product = new Product(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getDouble("price"),
                resultSet.getInt("stock"),
                category
        );

        return product;
    }

    private void showMessage(int rows, String messageOK, String messageError){
        if(rows>0){
            System.out.println(messageOK);
        }else if(!messageError.isBlank()){
            System.out.println(messageError);
        }
    }
}
