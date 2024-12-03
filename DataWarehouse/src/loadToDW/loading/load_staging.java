package org.example;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.List;
import java.util.Map;

public class load_staging {
    public static void main(String[] args) {
        String url = args[0];
        String user = args[1];
        String password = args[2];

        try {
            // Đường dẫn file JSON chứa mảng các sản phẩm
            String jsonFilePath =  args[3];
            // Tạo Gson instance
            Gson gson = new Gson();
            // Đọc file JSON bằng UTF-8
            Reader reader = new InputStreamReader(new FileInputStream(jsonFilePath), "UTF-8");

            Type productListType = new TypeToken<List<Product>>() {}.getType();
            List<Product> products = gson.fromJson(reader, productListType);

            for (Product product : products) {

                String insertQuery = "INSERT INTO ProductStaging (link, image_src, title, price, summary, retrieval_time) VALUES (?, ?, ?, ?, ?, ?)";
                String checkQuery = "SELECT COUNT(*) FROM ProductStaging WHERE link = ? and image_src = ? and title = ? and price = ? and summary = ? and retrieval_time = ?";
                // Kết nối và thực hiện truy vấn
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection connection = DriverManager.getConnection(url, user, password);

                    // Kiểm tra xem brandName đã tồn tại chưa
                    try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
                        checkStatement.setString(1, product.getLink());
                        checkStatement.setString(2, product.getImgSrc());
                        checkStatement.setString(3, product.getTitle());
                        checkStatement.setString(4, product.getPrice().replace(",", ""));
                        StringBuilder des = new StringBuilder();
                        product.getSummary().forEach((key, value) -> {
                            des.append(" - ").append(key).append(": ").append(value).append("\n");
                        });
                        checkStatement.setString(5, String.valueOf(des));
                        checkStatement.setString(6, product.getRetrievalTime());

                        ResultSet resultSet = checkStatement.executeQuery();
                        resultSet.next();
                        int count = resultSet.getInt(1);

                        if (count == 0) {
                            // Nếu không tồn tại, chèn brandName vào bảng
                            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                                insertStatement.setString(1, product.getLink());
                                insertStatement.setString(2,  product.getImgSrc());
                                insertStatement.setString(3, product.getTitle());
                                insertStatement.setString(4, product.getPrice().replace(",", ""));
                                StringBuilder des1 = new StringBuilder();
                                product.getSummary().forEach((key, value) -> {
                                    des1.append(" - ").append(key).append(": ").append(value).append("\n");
                                });
                                insertStatement.setString(5, String.valueOf(des1));
                                insertStatement.setString(6, product.getRetrievalTime());
                                int rowsAffected = insertStatement.executeUpdate();
                                System.out.println("Product đã thêm : " + rowsAffected);
                            }
                        } else {
                            System.out.println("Product đã tồn tại trong data_staging");
                        }
                    }

                } catch (ClassNotFoundException e) {
                    System.out.println("MySQL JDBC driver not found.");
                    e.printStackTrace();
                } catch (SQLException e) {
                    System.out.println("Connection failed.");
                    e.printStackTrace();
                }

            }

            // Đóng reader
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
