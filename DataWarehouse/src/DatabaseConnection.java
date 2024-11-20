import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    // Thông tin kết nối
    private static final String URL = "jdbc:mysql://localhost:3306/dw"; // Tên cơ sở dữ liệu
    private static final String USER = "root"; // Tên người dùng
    private static final String PASSWORD = ""; // Mật khẩu
    // Thông tin kết nối cho datastagin
    private static final String STAGING_URL = "jdbc:mysql://localhost:3306/datastaging";
    private static final String STAGING_USER = "root";
    private static final String STAGING_PASSWORD = "";

    // Thông tin kết nối cho datawarehouse
    private static final String DW_URL = "jdbc:mysql://localhost:3306/datawarehouse";
    private static final String DW_USER = "root";
    private static final String DW_PASSWORD = "";

    // Phương thức kết nối đến datastagin
    public static Connection getStagingConnection() throws SQLException {
        return DriverManager.getConnection(STAGING_URL, STAGING_USER, STAGING_PASSWORD);
    }

    // Phương thức kết nối đến datawarehouse
    public static Connection getDWConnection() throws SQLException {
        return DriverManager.getConnection(DW_URL, DW_USER, DW_PASSWORD);
    }

    public static void main(String[] args) {

        // Gọi hàm để kết nối và lấy dữ liệu từ bảng
        fetchDataFromTable("products"); // Thay đổi tên bảng nếu cần
    }

    // Hàm để kết nối và lấy dữ liệu từ bảng
    private static void fetchDataFromTable(String tableName) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            // Tải driver JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Kết nối đến cơ sở dữ liệu
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Kết nối đến dw thành công!");

            // Tạo Statement để thực hiện truy vấn
            statement = connection.createStatement();
            String sql = "SELECT * FROM " + tableName; // Truy vấn dữ liệu từ bảng
            resultSet = statement.executeQuery(sql);

            // Xử lý kết quả truy vấn
            while (resultSet.next()) {
                // Giả sử bảng có cột 'id' và 'name'
                int id = resultSet.getInt("products_id"); // Thay đổi tên cột nếu cần
                String name = resultSet.getString("product_name"); // Thay đổi tên cột nếu cần
                System.out.println("ID: " + id + ", Tên: " + name);
            }

        } catch (SQLException e) {
            System.out.println("Kết nối thất bại với dw: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Driver không tìm thấy: " + e.getMessage());
        } finally {
            // Đóng kết nối
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Test kết nối với cả ba cơ sở dữ liệu
        try (Connection stagingConnection = getStagingConnection();
             Connection dwConnection = getDWConnection()) {

            // Kiểm tra kết nối đến staging database
            if (stagingConnection != null) {
                System.out.println("Kết nối đến datastagin thành công!");
                try (Statement stagingStatement = stagingConnection.createStatement()) {
                    ResultSet stagingResult = stagingStatement.executeQuery("SELECT 1");
                    if (stagingResult.next()) {
                        System.out.println("Kết nối đến datastagin hoạt động bình thường.");
                    }
                }
            }

            // Kiểm tra kết nối đến datawarehouse database
            if (dwConnection != null) {
                System.out.println("Kết nối đến datawarehouse thành công!");
                try (Statement dwStatement = dwConnection.createStatement()) {
                    ResultSet dwResult = dwStatement.executeQuery("SELECT 1");
                    if (dwResult.next()) {
                        System.out.println("Kết nối đến datawarehouse hoạt động bình thường.");
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Lỗi kết nối: " + e.getMessage());
        }
    }
}
