package db_control;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbControl {

    // Phương thức tạo kết nối
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/Dbcontrol";
        String user = "root";
        String password = "";
        return DriverManager.getConnection(url, user, password);
    }

    // Phương thức để xử lý lưu Config khi bắt đầu hoặc cập nhật trạng thái
    public static void handleConfig(Connection connection, Config config, boolean isUpdate) {
        try {
            if (isUpdate) {
                config.updateToDb(connection); // Cập nhật Config
                System.out.println("Config đã được cập nhật: " + config.getKey());
            } else {
                config.saveToDb(connection); // Lưu mới Config
                System.out.println("Config đã được lưu: " + config.getKey());
            }
        } catch (SQLException e) {
            Log log = new Log(
                    "ERROR",
                    isUpdate ? "Lỗi khi cập nhật Config" : "Lỗi khi lưu Config vào DB",
                    "Config Process",
                    e.getMessage()
            );
            handleLog(connection, log); // Ghi log lỗi
        }
    }

    // Phương thức để xử lý lưu Log
    public static void handleLog(Connection connection, Log log) {
        try {
            log.saveToDb(connection); // Lưu Log vào DB
            System.out.println("Log đã được lưu: " + log.getMessage());
        } catch (SQLException e) {
            System.err.println("Không thể lưu Log: " + e.getMessage());
        }
    }

    // Phương thức để xử lý ngoại lệ từ quá trình crawl
    public static void handleCrawlError(Connection connection, String message, String context) {
        Log log = new Log("WARNING", message, context, null);
        handleLog(connection, log); // Lưu log cảnh báo
    }

    // Phương thức xử lý khi crawl thành công hoặc gặp lỗi nghiêm trọng
    public static void handleFinalCrawlStatus(Connection connection, Config config, String status, String description) {
        try {
            config.setValue(status); // Cập nhật trạng thái
            config.setDescription(description);
            config.updateToDb(connection);
            System.out.println("Trạng thái crawl đã được cập nhật: " + status);
        } catch (SQLException e) {
            Log log = new Log(
                    "ERROR",
                    "Lỗi khi cập nhật trạng thái crawl",
                    "Final Crawl Status Update",
                    e.getMessage()
            );
            handleLog(connection, log); // Ghi log lỗi
        }
    }
    public static void main(String[] args) {
        try (Connection connection = DbControl.getConnection()) {
            System.out.println("Kết nối thành công!");

            // Ghi log thông báo kết nối thành công
            Log log = new Log("INFO", "Kết nối đến cơ sở dữ liệu thành công.", "DB Connection", null);
            log.saveToDb(connection); // Lưu log vào cơ sở dữ liệu

            // Tạo một đối tượng Config
            Config config = new Config("test_key", "test_value", "Đây là dữ liệu test");

            // Lưu Config vào DB
            config.saveToDb(connection);
            System.out.println("Dữ liệu đã được thêm vào bảng Config.");

            // Ghi log thông báo dữ liệu đã được thêm thành công vào bảng Config
            log = new Log("INFO", "Dữ liệu đã được thêm vào bảng Config.", "Config Insert", null);
            log.saveToDb(connection); // Lưu log vào cơ sở dữ liệu

        } catch (SQLException e) {
            e.printStackTrace();

            // Ghi log lỗi khi gặp ngoại lệ
            Log log = new Log("ERROR", "Lỗi khi kết nối hoặc lưu dữ liệu: " + e.getMessage(), "Database Error", e.toString());
            try (Connection connection = DbControl.getConnection()) {
                log.saveToDb(connection); // Lưu log lỗi vào cơ sở dữ liệu
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }


}
