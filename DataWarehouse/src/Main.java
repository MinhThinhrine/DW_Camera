import loadDW.LoadToDW;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try (
                Connection stagingConnection = DatabaseConnection.getStagingConnection();
                Connection dwConnection = DatabaseConnection.getDWConnection()
        ) {
            // Khởi tạo đối tượng LoadToDW
            LoadToDW loader = new LoadToDW(stagingConnection, dwConnection);

            // Gọi các phương thức để tải dữ liệu
            System.out.println("Đang tải dữ liệu vào bảng dim_brand...");
            loader.loadDimBrand();
            System.out.println("Hoàn thành tải dữ liệu vào dim_brand.");

            System.out.println("Đang tải dữ liệu vào bảng dim_product...");
            loader.loadDimProduct();
            System.out.println("Hoàn thành tải dữ liệu vào dim_product.");

            System.out.println("Đang tải dữ liệu vào bảng dim_price...");
            loader.loadDimPrice();
            System.out.println("Hoàn thành tải dữ liệu vào dim_price.");

        } catch (SQLException e) {
            System.err.println("Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
        }
    }
}
