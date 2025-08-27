package com.antonio.proyecto.category.model;

import com.antonio.proyecto.category.model.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoryDao {
    private final Connection connection;

    public CategoryDao(Connection connection) {
        this.connection = connection;
    }

    public Optional<Category> save(Category category){
        String sql = "INSERT INTO categories (name) " +
                "VALUES (?)";

        try (
                PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)
        ){
            statement.setString(1, category.getName());

            int rows = statement.executeUpdate();

            if(rows>0){
                try (ResultSet resultSet = statement.getGeneratedKeys()){
                    if(resultSet.next()){
                        long id = resultSet.getLong(1);
                        category.setId(id);
                        return Optional.of(category);
                    }
                }
                System.out.println("La categoria fue creada correctamente");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return Optional.empty();
    }

    public void update(Category category) throws SQLException {
        String sql = "UPDATE categories SET name = ? " +
                " WHERE id=?";

        try (
                PreparedStatement statement = connection.prepareStatement(sql)
        ){
            statement.setString(1, category.getName());
            statement.setLong(5, category.getId());

            int rows = statement.executeUpdate();
            showMessage(rows, "El categoria fue actualizado", "El categoria no existe...");

        }
    }

    public void delete(long id) throws SQLException{
        String sql = "DELETE FROM categories WHERE id = ? ";

        try (
                PreparedStatement statement = connection.prepareStatement(sql)
        ){

            statement.setLong(1, id);

            int rows = statement.executeUpdate();
            showMessage(rows, "La categoria fue eliminado", "La categoria no existe...");

        }
    }


    public List<Category> findAll() throws SQLException{
        String sql = "Select * From categories";
        List<Category> categories = new ArrayList<>();
        try (
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ){
            while (resultSet.next()){
                Category category = mapResult(resultSet);
                categories.add(category);
            }
        }
        return categories;
    }

    public Optional<Category> findCategoryByName(String categoryName) throws SQLException{
        String sql = "Select * From categories Where name = ?";
        List<Category> categories = new ArrayList<>();
        try (
                PreparedStatement statement = connection.prepareStatement(sql);
        ){
            statement.setString(1, categoryName);
            try (ResultSet resultSet = statement.executeQuery()){
                if (resultSet.next()){
                    return Optional.of(mapResult(resultSet));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Category> findById(Long id) throws SQLException{
        String sql = "Select * From categories Where id = ?";
        List<Category> categories = new ArrayList<>();
        try (
                PreparedStatement statement = connection.prepareStatement(sql);
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

    private Category mapResult(ResultSet resultSet) throws SQLException {
        Category category = new Category(
                resultSet.getLong("id"),
                resultSet.getString("name")
        );

        return category;
    }

    private void showMessage(int rows, String messageOK, String messageError){
        if(rows>0){
            System.out.println(messageOK);
        }else if(!messageError.isBlank()){
            System.out.println(messageError);
        }
    }
}
