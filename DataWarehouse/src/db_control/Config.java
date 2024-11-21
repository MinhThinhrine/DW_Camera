package db_control;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class Config {
    private int idConfig;
    private String key;
    private String value;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor
    public Config(String key, String value, String description) {
        this.key = key;
        this.value = value;
        this.description = description;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Function lưu Config vào DB
    public void saveToDb(Connection connection) throws SQLException {
        // Câu lệnh SQL INSERT để thêm bản ghi mới vào bảng
        String sql = "INSERT INTO Config (`key`, `value`, `description`, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, this.key);           // Set giá trị của key
            preparedStatement.setString(2, this.value);         // Set giá trị của value
            preparedStatement.setString(3, this.description);   // Set giá trị của description
            preparedStatement.setObject(4, this.createdAt);     // Set giá trị của created_at
            preparedStatement.setObject(5, this.updatedAt);     // Set giá trị của updated_at
            preparedStatement.executeUpdate();                   // Thực thi câu lệnh INSERT
        }
    }



    // Function cập nhật Config
    public void updateToDb(Connection connection) throws SQLException {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Key không được để trống.");
        }

        // Cập nhật lại thời gian 'updated_at' mỗi lần lưu mới.
        this.updatedAt = LocalDateTime.now();
        this.createdAt = LocalDateTime.now(); // Cập nhật lại thời gian tạo khi thêm mới bản ghi.

        String sql = "INSERT INTO Config (`key`, `value`, `description`, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, this.key);           // Set giá trị của key
            preparedStatement.setString(2, this.value);         // Set giá trị của value
            preparedStatement.setString(3, this.description);   // Set giá trị của description
            preparedStatement.setObject(4, this.createdAt);     // Set giá trị của created_at
            preparedStatement.setObject(5, this.updatedAt);     // Set giá trị của updated_at

            // Thực thi câu lệnh INSERT
            preparedStatement.executeUpdate();
        }
    }

    public int getIdConfig() {
        return idConfig;
    }

    public void setIdConfig(int idConfig) {
        this.idConfig = idConfig;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
