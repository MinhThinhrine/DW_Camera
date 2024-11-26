package loadtoStagging;

import crawl_data.Product;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class Stagging {
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/datastagging";
        String user = "root";
        String password = "";
        return DriverManager.getConnection(url, user, password);
    }
    public static void loadToStaging(List<Product> products) throws SQLException {
        String insertQuery = "INSERT INTO ProductStaging (link, image_src, title, price, summary, retrieval_time) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        Connection connection = getConnection();
        try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
            for (Product product : products) {
                statement.setString(1, product.getLink());
                statement.setString(2, product.getImageSrc());
                statement.setString(3, product.getTitle());
                statement.setInt(4, product.getPrice());
                statement.setString(5, product.getSummary());
                statement.setString(6, product.getRetrievalTime());
                statement.addBatch(); // Add to batch
            }

            statement.executeBatch(); // Execute batch insert
            System.out.println("All products have been successfully loaded into the staging table.");
        } catch (SQLException e) {
            System.err.println("Failed to save products to the staging table: " + e.getMessage());
        }
    }
}
