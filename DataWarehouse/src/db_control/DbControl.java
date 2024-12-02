package db_control;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
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
        Log log = new Log("ERROR", fullMessage, context, e != null ? e.toString() : null);
        handleLog(connection, log);
        System.err.println(getCurrentTime() + " [ERROR] " + context + " - " + fullMessage);
    }

    // Ghi log vào DB
    public static void handleLog(Connection connection, Log log) {
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

    // Tiện ích lấy thời gian hiện tại
    private static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+7"));
        return "[" + sdf.format(new java.util.Date()) + "]";
    }
}
