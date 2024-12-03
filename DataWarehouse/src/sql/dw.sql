/*
 Navicat Premium Data Transfer

 Source Server         : database
 Source Server Type    : MySQL
 Source Server Version : 100427
 Source Host           : localhost:3306
 Source Schema         : dw

 Target Server Type    : MySQL
 Target Server Version : 100427
 File Encoding         : 65001

 Date: 02/11/2024 23:18:11
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for dim_brand
-- ----------------------------
DROP TABLE IF EXISTS `dim_brand`;
CREATE TABLE `dim_brand`  (
  `brand_id` int NOT NULL,
  `brand_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`brand_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dim_product
-- ----------------------------
DROP TABLE IF EXISTS `dim_product`;
CREATE TABLE `dim_product`  (
  `product_id` int NOT NULL,
  `product_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `brand_int` int NULL DEFAULT NULL,
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  PRIMARY KEY (`product_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dim_time
-- ----------------------------
DROP TABLE IF EXISTS `dim_time`;
CREATE TABLE `dim_time`  (
  `time_id` int NOT NULL,
  `date` date NULL DEFAULT NULL,
  `month` int NULL DEFAULT NULL,
  `year` int NULL DEFAULT NULL,
  PRIMARY KEY (`time_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for fact_price
-- ----------------------------
DROP TABLE IF EXISTS `fact_price`;
CREATE TABLE `fact_price`  (
  `fact_id` int NOT NULL,
  `product_id` int NULL DEFAULT NULL,
  `time_id` int NULL DEFAULT NULL,
  `current_price` decimal(10, 2) NULL DEFAULT NULL,
  `original_price` decimal(10, 2) NULL DEFAULT NULL,
  `discount_percentage` decimal(5, 2) NULL DEFAULT NULL,
  PRIMARY KEY (`fact_id`) USING BTREE,
  INDEX `product_id`(`product_id`) USING BTREE,
  INDEX `time_id`(`time_id`) USING BTREE,
  CONSTRAINT `fact_price_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `dim_product` (`product_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fact_price_ibfk_2` FOREIGN KEY (`time_id`) REFERENCES `dim_time` (`time_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for file_config
-- ----------------------------
DROP TABLE IF EXISTS `file_config`;
CREATE TABLE `file_config`  (
  `config_id` int NOT NULL,
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `last_extracted` datetime(0) NULL DEFAULT NULL,
  `schedule_id` int NULL DEFAULT NULL,
  PRIMARY KEY (`config_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for file_logs
-- ----------------------------
DROP TABLE IF EXISTS `file_logs`;
CREATE TABLE `file_logs`  (
  `log_id` int NOT NULL,
  `config` int NULL DEFAULT NULL,
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `timestamp` datetime(0) NULL DEFAULT NULL,
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  PRIMARY KEY (`log_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for products
-- ----------------------------
DROP TABLE IF EXISTS `products`;
CREATE TABLE `products`  (
  `products_id` int NOT NULL,
  `product_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `brand_name` int NULL DEFAULT NULL,
  `current_price` decimal(10, 2) NULL DEFAULT NULL,
  `discount_percentage` decimal(5, 2) NULL DEFAULT NULL,
  `last_updated` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`products_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for staging_camera
-- ----------------------------
DROP TABLE IF EXISTS `staging_camera`;
CREATE TABLE `staging_camera`  (
  `camera_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `brand_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `price` decimal(10, 2) NULL DEFAULT NULL,
  `original_price` decimal(10, 2) NULL DEFAULT NULL,
  `discount_percentage` decimal(5, 2) NULL DEFAULT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
