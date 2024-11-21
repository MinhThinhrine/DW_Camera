package crawl_data;

import java.sql.Connection;
import java.sql.SQLException;
import db_control.*;

public class Log_crawl {
    // Hàm ghi log bắt đầu
    public static void logStart(String message) {
        try (Connection connection = DbControl.getConnection()) {
            // Tạo đối tượng Log
            Log log = new Log("INFO", message, "Crawl May Anh", null);
            DbControl.handleLog(connection, log); // Ghi log vào DB
            System.out.println("Log đã được lưu: " + message);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Hàm ghi log khi quá trình crawl hoàn tất
    public static void logCompleted(String message) {
        try (Connection connection = DbControl.getConnection()) {
            // Tạo đối tượng Log
            Log log = new Log("INFO", message, "Crawl May Anh", null);
            DbControl.handleLog(connection, log); // Ghi log vào DB
            System.out.println("Log đã được lưu: " + message);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Hàm ghi log lỗi
    public static void logError(String source, String errorMessage) {
        try (Connection connection = DbControl.getConnection()) {
            // Tạo đối tượng Log
            Log log = new Log("ERROR", errorMessage, source, null);
            DbControl.handleLog(connection, log); // Ghi log vào DB
            System.out.println("Log lỗi đã được lưu: " + errorMessage);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Hàm ghi log tiến trình
    public static void logInProgress(String message) {
        try (Connection connection = DbControl.getConnection()) {
            // Tạo đối tượng Log
            Log log = new Log("INFO", message, "Crawl May Anh", null);
            DbControl.handleLog(connection, log); // Ghi log vào DB
            System.out.println("Log tiến trình đã được lưu: " + message);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
