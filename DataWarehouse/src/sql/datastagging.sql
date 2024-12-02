-- Create database
CREATE DATABASE IF NOT EXISTS datastagging;

-- Use the database
USE datastagging;

-- Create staging table
CREATE TABLE IF NOT EXISTS ProductStaging (
                                              id INT AUTO_INCREMENT PRIMARY KEY,
                                              link TEXT NOT NULL,
                                              image_src TEXT NOT NULL,
                                              title VARCHAR(255) NOT NULL,
    price INT NOT NULL,
    summary TEXT,
    retrieval_time DATETIME NOT NULL,
    saved_time DATETIME DEFAULT CURRENT_TIMESTAMP -- Time of saving to DB
    );
