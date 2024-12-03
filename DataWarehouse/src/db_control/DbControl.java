package db_control;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.TimeZone;

public class DbControl {

    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/Dbcontrol";
        String user = "root";
        String password = "";
        return DriverManager.getConnection(url, user, password);
    }

    // Xử lý ngoại lệ khi lưu Log
    public static void handleLogException(Connection connection, String message, String context, Exception e) {
        String fullMessage = message + (e != null ? ": " + e.getMessage() : "");

        // Create a log entry with the exception details
        Log log = new Log("ERROR", fullMessage, context, "ERROR");

        // Attempt to save the log to the database
        handleLog(connection, log);

        // Print the error message to the console
        System.err.println(getCurrentTime() + " [ERROR] " + context + " - " + fullMessage);
    }

    // Ghi log vào DB
    public static void handleLog(Connection connection, Log log) {
        if (connection == null) {
            System.err.println(getCurrentTime() + " [ERROR] Connection is null. Cannot save log.");
            return; // Prevent NullPointerException
        }
        if (log == null) {
            System.err.println(getCurrentTime() + " [ERROR] Log object is null. Cannot save log.");
            return; // Prevent NullPointerException
        }

        try {
            log.saveToDb(connection);
            System.out.println(getCurrentTime() + " [INFO] Log saved: " + log.getMessage());
        } catch (SQLException e) {
            System.err.println(getCurrentTime() + " [ERROR] Failed to save log: " + e.getMessage());
        }
    }

    // Xử lý ngoại lệ khi lưu hoặc cập nhật Config
    public static void handleConfigException(Connection connection, Config config, boolean isUpdate, Exception e) {
        String action = isUpdate ? "update" : "save";
        String fullMessage = "Failed to " + action + " Config [" + config.getKey() + "]"
                + (e != null ? ": " + e.getMessage() : "");
        Log log = new Log("ERROR", fullMessage, "Config Process", e != null ? e.toString() : null);
        handleLog(connection, log);
        System.err.println(getCurrentTime() + " [ERROR] Config Process - " + fullMessage);
    }

    // Lưu hoặc cập nhật Config
    public static void handleConfig(Connection connection, Config config, boolean isUpdate) {
        try {
            if (isUpdate) {
                config.updateToDb(connection);
                System.out.println(getCurrentTime() + " [INFO] Config updated: " + config.getKey());
            } else {
                config.saveToDb(connection);
                System.out.println(getCurrentTime() + " [INFO] Config saved: " + config.getKey());
            }
        } catch (SQLException e) {
            handleConfigException(connection, config, isUpdate, e);
        }
    }
    public static boolean canRunCrawl(Connection connection) throws SQLException {
        LocalDate today = LocalDate.now();
        String todayStr = today.toString(); // Định dạng thành yyyy-MM-dd

        String query = "SELECT description, created_at FROM config " +
                "WHERE `key` = 'crawl_status' AND DATE(created_at) = ? " +
                "ORDER BY created_at DESC LIMIT 1";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, todayStr);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String description = rs.getString("description");
                LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();

                // Kiểm tra xem quá trình crawl đã hoàn thành hay chưa
                if ("Quá trình crawl đã hoàn thành".toLowerCase().equalsIgnoreCase(description.toLowerCase())) {
                    return false; // Không cần crawl lại
                } else
                    return true; // Có thể xử lý crawl đang chạy

            }
        }

        // Nếu không tìm thấy mục nào cho hôm nay hoặc nó chưa hoàn thành, cho phép crawl chạy
        return true;
    }
    // Tiện ích lấy thời gian hiện tại
    private static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+7"));
        return "[" + sdf.format(new java.util.Date()) + "]";
    }
}
