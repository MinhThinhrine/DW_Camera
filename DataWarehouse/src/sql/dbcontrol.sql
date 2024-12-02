-- Tạo cơ sở dữ liệu
CREATE DATABASE IF NOT EXISTS Dbcontrol;

-- Sử dụng cơ sở dữ liệu vừa tạo
USE Dbcontrol;

-- Xóa bảng Config nếu đã tồn tại
DROP TABLE IF EXISTS Config;

-- Xóa bảng Log nếu đã tồn tại
DROP TABLE IF EXISTS Log;

-- Tạo bảng Config
CREATE TABLE Config (
                        id_config INT AUTO_INCREMENT PRIMARY KEY,
                        `key` VARCHAR(100) NOT NULL,
                        value TEXT,
                        description TEXT,
                        created_at DATETIME NOT NULL,
                        updated_at DATETIME NOT NULL
);




-- Tạo bảng Log
CREATE TABLE Log (
                     id_log INT AUTO_INCREMENT PRIMARY KEY,
                     timestamp DATETIME NOT NULL,
                     level VARCHAR(50) NOT NULL,
                     message TEXT NOT NULL,
                     context TEXT,
                     stack_trace TEXT
);
