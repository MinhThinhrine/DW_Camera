package db_control;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public class Log {

    // Thông tin kết nối
    private static final String URL = "jdbc:mysql://localhost:3306/dw"; // Đổi tên cơ sở dữ liệu nếu cần
    private static final String USER = "root"; // Đổi tên người dùng nếu cần
    private static final String PASSWORD = ""; // Đổi mật khẩu nếu cần

    // Phương thức ghi log
    public void writeLog(String config, String status, String errorMessage) {
        String sql = "INSERT INTO file_logs (config, status, timestamp, error_message) VALUES (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            // Thiết lập giá trị cho các tham số
            preparedStatement.setString(1, config);
            preparedStatement.setString(2, status);
            preparedStatement.setTimestamp(3, new java.sql.Timestamp(new Date().getTime()));
            preparedStatement.setString(4, errorMessage);

            // Thực hiện truy vấn
            preparedStatement.executeUpdate();
            System.out.println("Log đã được ghi thành công!");

        } catch (SQLException e) {
            System.out.println("Lỗi khi ghi log: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Log logger = new Log();
        // Ghi log mẫu
        logger.writeLog("Sample Config", "Success", null); // Ghi log thành công
        // logger.writeLog("Sample Config", "Error", "Lỗi khi thu thập dữ liệu."); // Ghi log lỗi
    }
}