package crawl_data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/dw"; // Thay đổi thông tin cơ sở dữ liệu của bạn
    private static final String DB_USER = "root"; // Tên người dùng DB
    private static final String DB_PASSWORD = ""; // Mật khẩu của DB

    // Hàm ghi log vào cơ sở dữ liệu
    public static void logToDatabase(String taskName, String status, String message) {
        String query = "INSERT INTO file_logs (task_name, status, message) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, taskName);
            ps.setString(2, status);
            ps.setString(3, message);

            ps.executeUpdate();
            System.out.println("Log added to database successfully.");
        } catch (SQLException e) {
            System.err.println("Error writing log to database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Hàm ghi log trạng thái "Bắt đầu crawl"
    public static void logStart(String taskName) {
        String status = "Started";
        String message = "Crawling started at " + getCurrentTimestamp();
        logToDatabase(taskName, status, message);
    }

    // Hàm ghi log trạng thái "Đang crawl"
    public static void logInProgress(String taskName) {
        String status = "In Progress";
        String message = "Crawling in progress at " + getCurrentTimestamp();
        logToDatabase(taskName, status, message);
    }

    // Hàm ghi log trạng thái "Hoàn thành"
    public static void logCompleted(String taskName) {
        String status = "Completed";
        String message = "Crawling completed at " + getCurrentTimestamp();
        logToDatabase(taskName, status, message);
    }

    // Hàm ghi log trạng thái "Lỗi"
    public static void logError(String taskName, String errorMessage) {
        String status = "Error";
        String message = "Error occurred: " + errorMessage + " at " + getCurrentTimestamp();
        logToDatabase(taskName, status, message);
    }

    // Hàm lấy thời gian hiện tại dưới dạng chuỗi
    private static String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    // Main để thử nghiệm
    public static void main(String[] args) {
        // Ví dụ về cách sử dụng
        String taskName = "Crawl Task 1";

        // Ghi log trạng thái bắt đầu
        logStart(taskName);

        // Ghi log trạng thái đang crawl
        logInProgress(taskName);

        // Giả lập một số lỗi
        try {
            // Thử một hành động có thể gây lỗi
            int result = 10 / 0;
        } catch (Exception e) {
            logError(taskName, e.getMessage());
        }

        // Ghi log trạng thái hoàn thành
        logCompleted(taskName);
    }
}

