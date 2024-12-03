package loadDW;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoadToDW {

    private static final Logger LOGGER = Logger.getLogger(LoadToDW.class.getName());

    private Connection stagingConnection;
    private Connection dwConnection;

    public LoadToDW(Connection stagingConnection, Connection dwConnection) {
        this.stagingConnection = stagingConnection;
        this.dwConnection = dwConnection;
    }

    // Tải và cập nhật dữ liệu vào bảng dim_product trong datawarehouse
    public void loadDimProduct() {
        String selectQuery = "SELECT camera_name, brand_name, image_url, description FROM staging_camera";
        String insertQuery = "INSERT INTO dim_product (product_id, product_name, brand_id, brand_name, image_url, description) VALUES (?, ?, ?, ?, ?, ?)";
        String updateQuery = "UPDATE dim_product SET product_name = ?, brand_id = ?, brand_name = ?, image_url = ?, description = ? WHERE product_id = ?";
        String maxIdQuery = "SELECT MAX(product_id) FROM dim_product";
        String checkExistQuery = "SELECT COUNT(*) FROM dim_product WHERE product_name = ? AND brand_id = ?";

        try (PreparedStatement selectStmt = stagingConnection.prepareStatement(selectQuery);
             PreparedStatement maxIdStmt = dwConnection.prepareStatement(maxIdQuery);
             PreparedStatement checkExistStmt = dwConnection.prepareStatement(checkExistQuery);
             PreparedStatement insertStmt = dwConnection.prepareStatement(insertQuery);
             PreparedStatement updateStmt = dwConnection.prepareStatement(updateQuery)) {

            // Lấy giá trị product_id tiếp theo
            ResultSet maxIdRs = maxIdStmt.executeQuery();
            int productId = maxIdRs.next() ? maxIdRs.getInt(1) + 1 : 1;

            ResultSet rs = selectStmt.executeQuery(); // Lấy dữ liệu từ staging_camera

            int insertedCount = 0; // Số bản ghi đã chèn
            int updatedCount = 0;  // Số bản ghi đã cập nhật

            while (rs.next()) {
                String cameraName = rs.getString("camera_name");
                String brandName = rs.getString("brand_name");

                // Kiểm tra dữ liệu hợp lệ
                if (cameraName == null || cameraName.trim().isEmpty() || brandName == null || brandName.trim().isEmpty()) {
                    LOGGER.warning("Dữ liệu không hợp lệ: " + cameraName + " - " + brandName);
                    continue; // Bỏ qua bản ghi không hợp lệ
                }

                // Lấy brand_id từ dim_brand
                String getBrandIdQuery = "SELECT brand_id FROM dim_brand WHERE brand_name = ?";
                try (PreparedStatement brandStmt = dwConnection.prepareStatement(getBrandIdQuery)) {
                    brandStmt.setString(1, brandName);
                    ResultSet brandRs = brandStmt.executeQuery();
                    if (brandRs.next()) {
                        int brandId = brandRs.getInt("brand_id");

                        // Kiểm tra xem sản phẩm đã có trong datawarehouse chưa
                        checkExistStmt.setString(1, cameraName);
                        checkExistStmt.setInt(2, brandId); // Kiểm tra với brand_id
                        ResultSet existRs = checkExistStmt.executeQuery();
                        existRs.next();

                        if (existRs.getInt(1) == 0) { // Nếu chưa có, insert vào datawarehouse
                            insertStmt.setInt(1, productId++);
                            insertStmt.setString(2, cameraName);
                            insertStmt.setInt(3, brandId);
                            insertStmt.setString(4, brandName);
                            insertStmt.setString(5, rs.getString("image_url"));
                            insertStmt.setString(6, rs.getString("description"));
                            insertStmt.addBatch(); // Thêm vào batch

                            insertedCount++;
                        } else { // Nếu đã có, cập nhật
                            updateStmt.setString(1, cameraName);
                            updateStmt.setInt(2, brandId);
                            updateStmt.setString(3, brandName);
                            updateStmt.setString(4, rs.getString("image_url"));
                            updateStmt.setString(5, rs.getString("description"));
                            updateStmt.setInt(6, productId); // Cập nhật theo product_id
                            updateStmt.addBatch(); // Thêm vào batch

                            updatedCount++;
                        }
                    }
                }
            }

            // Thực thi batch
            insertStmt.executeBatch();
            updateStmt.executeBatch();

            LOGGER.info("Đã chèn " + insertedCount + " bản ghi vào dim_product.");
            LOGGER.info("Đã cập nhật " + updatedCount + " bản ghi trong dim_product.");

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tải hoặc cập nhật dữ liệu vào dim_product", e);
        }
    }

    public void loadDimBrand() {
        String selectQuery = "SELECT DISTINCT brand_name FROM brands";
        String insertQuery = "INSERT INTO dim_brand (brand_name) VALUES (?)";
        String updateQuery = "UPDATE dim_brand SET brand_name = ? WHERE brand_id = ?";
        String checkExistQuery = "SELECT brand_id FROM dim_brand WHERE brand_name = ?";

        try (PreparedStatement selectStmt = stagingConnection.prepareStatement(selectQuery);
             PreparedStatement checkExistStmt = dwConnection.prepareStatement(checkExistQuery);
             PreparedStatement insertStmt = dwConnection.prepareStatement(insertQuery);
             PreparedStatement updateStmt = dwConnection.prepareStatement(updateQuery)) {

            ResultSet rs = selectStmt.executeQuery();
            int insertedCount = 0; // Số bản ghi đã chèn
            int updatedCount = 0;  // Số bản ghi đã cập nhật

            while (rs.next()) {
                String brandName = rs.getString("brand_name");

                // Kiểm tra dữ liệu hợp lệ
                if (brandName == null || brandName.trim().isEmpty()) {
                    LOGGER.warning("Dữ liệu không hợp lệ: " + brandName);
                    continue; // Bỏ qua bản ghi không hợp lệ
                }

                // Kiểm tra xem brand_name đã có trong dim_brand chưa
                checkExistStmt.setString(1, brandName);
                ResultSet existRs = checkExistStmt.executeQuery();
                if (existRs.next()) {
                    int brandId = existRs.getInt("brand_id");

                    // Nếu đã có, cập nhật thông tin brand
                    updateStmt.setString(1, brandName);
                    updateStmt.setInt(2, brandId);
                    updateStmt.addBatch(); // Thêm vào batch

                    updatedCount++;
                } else {
                    // Nếu chưa có, thêm brand mới vào dim_brand
                    String getMaxIdQuery = "SELECT MAX(brand_id) FROM dim_brand";
                    PreparedStatement maxIdStmt = dwConnection.prepareStatement(getMaxIdQuery);
                    ResultSet maxIdRs = maxIdStmt.executeQuery();
                    int brandId = maxIdRs.next() ? maxIdRs.getInt(1) + 1 : 1;

                    insertStmt.setString(1, brandName);
                    insertStmt.addBatch(); // Thêm vào batch

                    insertedCount++;
                }
            }

            // Thực thi batch
            insertStmt.executeBatch();
            updateStmt.executeBatch();

            LOGGER.info("Đã chèn " + insertedCount + " bản ghi vào dim_brand.");
            LOGGER.info("Đã cập nhật " + updatedCount + " bản ghi trong dim_brand.");

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tải hoặc cập nhật dữ liệu vào dim_brand", e);
        }
    }

    public void loadDimPrice() {
        String selectQuery = "SELECT sc.camera_name, sc.brand_name, sc.price, sc.discount_percentage, sc.description " +
                "FROM staging_camera sc " +
                "LEFT JOIN datawarehouse.dim_brand b ON sc.brand_name = b.brand_name";
        String insertQuery = "INSERT INTO dim_price (product_name, brand_id, brand_name, current_price, discount_percentage) VALUES (?, ?, ?, ?, ?)";
        String updateQuery = "UPDATE dim_price SET current_price = ?, discount_percentage = ?, brand_id = ? WHERE price_id = ?";
        String checkExistQuery = "SELECT price_id, brand_id FROM dim_price WHERE product_name = ? AND brand_id = ?";

        try (PreparedStatement selectStmt = stagingConnection.prepareStatement(selectQuery);
             PreparedStatement checkExistStmt = dwConnection.prepareStatement(checkExistQuery);
             PreparedStatement insertStmt = dwConnection.prepareStatement(insertQuery);
             PreparedStatement updateStmt = dwConnection.prepareStatement(updateQuery)) {

            ResultSet rs = selectStmt.executeQuery();
            int insertedCount = 0; // Số bản ghi đã chèn
            int updatedCount = 0;  // Số bản ghi đã cập nhật

            while (rs.next()) {
                String cameraName = rs.getString("camera_name");
                String brandName = rs.getString("brand_name");

                // Kiểm tra dữ liệu hợp lệ
                if (cameraName == null || cameraName.trim().isEmpty() || brandName == null || brandName.trim().isEmpty()) {
                    LOGGER.warning("Dữ liệu không hợp lệ: " + cameraName + " - " + brandName);
                    continue; // Bỏ qua bản ghi không hợp lệ
                }

                // Lấy discount_percentage (nếu có)
                Integer discountPercentage = rs.getInt("discount_percentage");
                if (rs.wasNull()) {
                    discountPercentage = 0; // Nếu discount_percentage là NULL, gán giá trị mặc định là 0
                }

                // Lấy brand_id từ dim_brand
                String getBrandIdQuery = "SELECT brand_id FROM dim_brand WHERE brand_name = ?";
                try (PreparedStatement brandStmt = dwConnection.prepareStatement(getBrandIdQuery)) {
                    brandStmt.setString(1, brandName);
                    ResultSet brandRs = brandStmt.executeQuery();
                    if (brandRs.next()) {
                        int brandId = brandRs.getInt("brand_id");

                        // Kiểm tra xem sản phẩm đã có trong dim_price chưa
                        checkExistStmt.setString(1, cameraName);
                        checkExistStmt.setInt(2, brandId);
                        ResultSet existRs = checkExistStmt.executeQuery();
                        if (existRs.next()) {
                            // Cập nhật giá trị discount và price
                            updateStmt.setBigDecimal(1, rs.getBigDecimal("price"));
                            updateStmt.setInt(2, discountPercentage);  // Đảm bảo tham số discount_percentage luôn có giá trị
                            updateStmt.setInt(3, brandId);  // Cập nhật brand_id
                            updateStmt.setInt(4, existRs.getInt("price_id"));
                            updateStmt.addBatch(); // Thêm vào batch

                            updatedCount++;
                        } else {
                            // Thêm mới giá và discount vào dim_price
                            String getMaxIdQuery = "SELECT MAX(price_id) FROM dim_price";
                            PreparedStatement maxIdStmt = dwConnection.prepareStatement(getMaxIdQuery);
                            ResultSet maxIdRs = maxIdStmt.executeQuery();
//                            int priceId = maxIdRs.next() ? maxIdRs.getInt(1) + 1 : 1;
//
//                            insertStmt.setInt(1, priceId);
                            insertStmt.setString(1, cameraName);
                            insertStmt.setInt(2, brandId);  // Thêm brand_id vào insert
                            insertStmt.setString(3, brandName);
                            insertStmt.setBigDecimal(4, rs.getBigDecimal("price"));
                            insertStmt.setInt(5, discountPercentage);  // Đảm bảo tham số discount_percentage luôn có giá trị
                            insertStmt.addBatch(); // Thêm vào batch

                            insertedCount++;
                        }
                    } else {
                        LOGGER.warning("Không tìm thấy brand_id cho brand_name: " + brandName);
                    }
                }
            }

            // Thực thi batch
            insertStmt.executeBatch();
            updateStmt.executeBatch();

            LOGGER.info("Đã chèn " + insertedCount + " bản ghi vào dim_price.");
            LOGGER.info("Đã cập nhật " + updatedCount + " bản ghi trong dim_price.");

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tải hoặc cập nhật dữ liệu vào dim_price", e);
        }
    }
}