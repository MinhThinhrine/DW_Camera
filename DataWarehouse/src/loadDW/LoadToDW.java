package loadDW;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoadToDW {
    private static final Logger LOGGER = Logger.getLogger(LoadToDW.class.getName());
    private Connection connection;

    public LoadToDW(Connection connection) {
        this.connection = connection;
    }

    // Tải dữ liệu vào bảng dim_product
    public void loadDimProduct() {
        String selectQuery = "SELECT camera_name, brand_id, image_url, description FROM staging_camera";
        String insertQuery = "INSERT INTO dim_product (product_id, product_name, brand_id, image_url, description) VALUES (?, ?, ?, ?, ?)";
        String maxIdQuery = "SELECT MAX(product_id) FROM dim_product";

        try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery);
             PreparedStatement maxIdStmt = connection.prepareStatement(maxIdQuery);
             PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {

            // Lấy giá trị product_id tiếp theo
            ResultSet maxIdRs = maxIdStmt.executeQuery();
            int productId = maxIdRs.next() ? maxIdRs.getInt(1) + 1 : 1;

            ResultSet rs = selectStmt.executeQuery();

            while (rs.next()) {
                insertStmt.setInt(1, productId++);
                insertStmt.setString(2, rs.getString("camera_name"));
                insertStmt.setInt(3, rs.getInt("brand_id"));
                insertStmt.setString(4, rs.getString("image_url"));
                insertStmt.setString(5, rs.getString("description"));
                insertStmt.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error loading data into dim_product", e);
        }
    }

    // Tải dữ liệu vào bảng dim_time
    public void loadDimTime() {
        String selectQuery = "SELECT DISTINCT updated_at FROM staging_camera";
        String insertQuery = "INSERT INTO dim_time (time_id, date, month, year) VALUES (?, ?, ?, ?)";

        try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery);
             PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {

            ResultSet rs = selectStmt.executeQuery();
            int timeId = 1;

            while (rs.next()) {
                Date date = rs.getDate("updated_at");
                insertStmt.setInt(1, timeId++);
                insertStmt.setDate(2, date);
                insertStmt.setInt(3, date.toLocalDate().getMonthValue());
                insertStmt.setInt(4, date.toLocalDate().getYear());
                insertStmt.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error loading data into dim_time", e);
        }
    }

    // Tải dữ liệu vào bảng dim_brand
    public void loadDimBrand() {
        String selectQuery = "SELECT DISTINCT brand_id, brand_name FROM staging_camera";
        String insertQuery = "INSERT INTO dim_brand (brand_id, brand_name) VALUES (?, ?)";

        try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery);
             PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {

            ResultSet rs = selectStmt.executeQuery();

            while (rs.next()) {
                insertStmt.setInt(1, rs.getInt("brand_id"));
                insertStmt.setString(2, rs.getString("brand_name"));
                insertStmt.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error loading data into dim_brand", e);
        }
    }

    // Tải dữ liệu vào bảng dim_price
    public void loadDimPrice() {
        String selectQuery = "SELECT sc.camera_name, b.brand_name, sc.price, sc.discount, sc.updated_at " +
                "FROM staging_camera sc " +
                "JOIN dim_brand b ON sc.brand_id = b.brand_id";
        String insertQuery = "INSERT INTO dim_price (product_id, product_name, brand_name, current_price, discount_percentage, last_updated) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery);
             PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {

            ResultSet rs = selectStmt.executeQuery();
            int productId = 1;

            while (rs.next()) {
                insertStmt.setInt(1, productId++);
                insertStmt.setString(2, rs.getString("camera_name"));
                insertStmt.setString(3, rs.getString("brand_name"));
                insertStmt.setBigDecimal(4, rs.getBigDecimal("price"));
                insertStmt.setBigDecimal(5, rs.getBigDecimal("discount"));
                insertStmt.setTimestamp(6, rs.getTimestamp("updated_at")); // Lấy thời gian từ staging_camera
                insertStmt.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error loading data into dim_price", e);
        }
    }
}
