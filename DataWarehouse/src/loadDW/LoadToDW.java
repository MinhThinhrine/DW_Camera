package loadDW;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoadToDW {
    private static final Logger LOGGER = Logger.getLogger(LoadToDW.class.getName());
    private Connection stagingConnection; // Kết nối đến datastagin
    private Connection dwConnection;      // Kết nối đến datawarehouse

    public LoadToDW(Connection stagingConnection, Connection dwConnection) {
        this.stagingConnection = stagingConnection;
        this.dwConnection = dwConnection;
    }

    // Tải dữ liệu từ datastagin vào bảng dim_product trong datawarehouse
    public void loadDimProduct() {
        String selectQuery = "SELECT camera_name, brand_id, image_url, description FROM staging_camera";
        String insertQuery = "INSERT INTO dim_product (product_id, product_name, brand_id, image_url, description) VALUES (?, ?, ?, ?, ?)";
        String maxIdQuery = "SELECT MAX(product_id) FROM dim_product";

        try (PreparedStatement selectStmt = stagingConnection.prepareStatement(selectQuery); // Lấy từ datastagin
             PreparedStatement maxIdStmt = dwConnection.prepareStatement(maxIdQuery);       // Xác định ID từ datawarehouse
             PreparedStatement insertStmt = dwConnection.prepareStatement(insertQuery)) {  // Ghi vào datawarehouse

            // Lấy giá trị product_id tiếp theo
            ResultSet maxIdRs = maxIdStmt.executeQuery();
            int productId = maxIdRs.next() ? maxIdRs.getInt(1) + 1 : 1;

            ResultSet rs = selectStmt.executeQuery(); // Lấy dữ liệu từ staging_camera

            while (rs.next()) {
                insertStmt.setInt(1, productId++);
                insertStmt.setString(2, rs.getString("camera_name"));
                insertStmt.setInt(3, rs.getInt("brand_id"));
                insertStmt.setString(4, rs.getString("image_url"));
                insertStmt.setString(5, rs.getString("description"));
                insertStmt.executeUpdate(); // Chèn vào dim_product
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tải dữ liệu vào dim_product", e);
        }
    }

    // Tải dữ liệu từ datastagin vào bảng dim_brand trong datawarehouse
    public void loadDimBrand() {
        String selectQuery = "SELECT DISTINCT brand_id, brand_name FROM staging_camera";
        String insertQuery = "INSERT INTO dim_brand (brand_id, brand_name) VALUES (?, ?)";

        try (PreparedStatement selectStmt = stagingConnection.prepareStatement(selectQuery); // Lấy từ datastagin
             PreparedStatement insertStmt = dwConnection.prepareStatement(insertQuery)) {   // Ghi vào datawarehouse

            ResultSet rs = selectStmt.executeQuery(); // Lấy dữ liệu từ staging_camera

            while (rs.next()) {
                insertStmt.setInt(1, rs.getInt("brand_id"));
                insertStmt.setString(2, rs.getString("brand_name"));
                insertStmt.executeUpdate(); // Chèn vào dim_brand
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tải dữ liệu vào dim_brand", e);
        }
    }
    // Tải dữ liệu từ datastagin vào bảng dim_price trong datawarehouse
    public void loadDimPrice() {
        String selectQuery = "SELECT sc.camera_name, sc.brand_name, sc.price, sc.discount_percentage, sc.updated_at " +
                "FROM staging_camera sc " +
                "LEFT JOIN dim_brand b ON sc.brand_name = b.brand_name";

        String insertQuery = "INSERT INTO dim_price (price_id, product_name, brand_name, current_price, discount_percentage, last_updated) VALUES (?, ?, ?, ?, ?, ?)";
        String maxIdQuery = "SELECT MAX(price_id) FROM dim_price";

        try (PreparedStatement selectStmt = stagingConnection.prepareStatement(selectQuery); // Lấy từ datastagin
             PreparedStatement maxIdStmt = dwConnection.prepareStatement(maxIdQuery);       // Xác định ID từ datawarehouse
             PreparedStatement insertStmt = dwConnection.prepareStatement(insertQuery)) {  // Ghi vào datawarehouse

            // Lấy giá trị price_id tiếp theo
            ResultSet maxIdRs = maxIdStmt.executeQuery();
            int priceId = maxIdRs.next() ? maxIdRs.getInt(1) + 1 : 1;

            ResultSet rs = selectStmt.executeQuery(); // Lấy dữ liệu từ staging_camera

            while (rs.next()) {
                insertStmt.setInt(1, priceId++);
                insertStmt.setString(2, rs.getString("camera_name"));
                insertStmt.setString(3, rs.getString("brand_name"));
                insertStmt.setBigDecimal(4, rs.getBigDecimal("price"));
                insertStmt.setBigDecimal(5, rs.getBigDecimal("discount"));
                insertStmt.setTimestamp(6, rs.getTimestamp("updated_at")); // Lấy thời gian cập nhật
                insertStmt.executeUpdate(); // Chèn vào dim_price
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tải dữ liệu vào dim_price", e);
        }
    }

}
