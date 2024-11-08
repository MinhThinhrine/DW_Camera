-- Thêm dữ liệu vào bảng dim_brand
INSERT INTO dim_brand (brand_id, brand_name) VALUES
(1, 'TTArtisan'),
(2, 'Canon'),
(3, 'Sony');

-- Thêm dữ liệu vào bảng dim_product
INSERT INTO dim_product (product_id, product_name, brand_int, image_url, description) VALUES
(1, 'TTArtisan AF 27mm f/2.8 for Fujifilm (Chính hãng)', 1, 'https://zshop.vn/images/thumbnails/460/460/detailed/171/AF2728-X-B__1_.jpg', '- Ngàm Fujifilm X định dạng APS-C, Tiêu cự 40.5mm (tương đương full frame), Khẩu độ f/2.8 đến f/16, Động cơ AF bước STM, Khoảng cách lấy nét gần nhất 35cm, 7 lá khẩu, Kích cỡ kính lọc: 39mm (phía trước)'),
(2, 'Canon EOS R100 Kit 18-45mm + RF-S 18-150mm', 2, 'https://zshop.vn/images/thumbnails/460/460/detailed/171/CANON_R100_RFS1850_1.jpg', '- Cảm biến ảnh APS-C CMOS 24.1MP, - Chip xử lý hình ảnh DIGIC 8, - Quay video 4K 24p có crop, Full HD 60p, - Lấy nét Dual Pixel CMOS AF với 143 vùng AF, - EVF OLED 2.36M điểm, - Màn hình LCD 3" 1.04M điểm, - Chụp liên tiếp màn trập điện 6.5 fps, - Chế độ Creative Assist, - Wi-Fi & Bluetooth, 1 khe thẻ SD, - Trọng lượng:309g (body only), - Ống kính kit Canon RF-S 18-45mm f/4.5-6.3 IS STM, - Ống kính combo Canon RF-S 18-150mm f/3.5-6.3 IS STM'),
(3, 'Canon EOS R100 Kit 18-45mm + Sigma 18-50mm f/2.8', 2, 'https://zshop.vn/images/thumbnails/460/460/detailed/171/CANON_R100_S1850_1.jpg', '- Cảm biến ảnh APS-C CMOS 24.1MP, - Chip xử lý hình ảnh DIGIC 8, - Quay video 4K 24p có crop, Full HD 60p, - Lấy nét Dual Pixel CMOS AF với 143 vùng AF, - EVF OLED 2.36M điểm, - Màn hình LCD 3" 1.04M điểm, - Chụp liên tiếp màn trập điện 6.5 fps, - Chế độ Creative Assist, - Wi-Fi & Bluetooth, 1 khe thẻ SD, - Trọng lượng:309g (body only), - Ống kính kit Canon RF-S 18-45mm f/4.5-6.3 IS STM, - Ống kính combo Sigma 18-50mm f/2.8 DC DN (C) Canon RF-S'),
(4, 'Sony ZV-1 II Vlog On The Go Edition (Chính hãng)', 3, 'https://zshop.vn/images/thumbnails/460/460/detailed/170/SONY_ZV1_II__VL.jpg', '- Dành cho vlogger và người sáng tạo nội dung, - Cảm biến Exmor RS BSI CMOS 1" 20.1MP, - Ống kính góc rộng tương đương 18-50mm f/1.8-4, - Quay video 4K30p UHD với HLG & S-Log3/2, - Màn hình cảm ứng LCD 3", xoay lật, - Điều khiển phơi sáng & lấy nét cảm ứng trực quan, - Lấy nét Real-Time Tracking & Eye AF, - Cần gạt bokeh, Face Priority AE, - Thiết lập Cinematic Vlog, chế độ quay S&Q, - Chế độ giới thiệu sản phẩm Product Showcase'),
(5, 'Canon RF 28-70mm f/2.8 IS STM (Chính hãng)', 2, 'https://zshop.vn/images/thumbnails/460/460/detailed/170/canon_6535c002_rf_28_70mm_f_2_8_is_PO.jpg', '- Ngàm RF full frame, - Khẩu độ f/2.8 đến f/22, - Gồm 2 thấu kính UD và 2 thấu kính phi cầu, - Lớp phủ Super Spectra Coating (SSC), - Động cơ lấy nét tự động bước STM, - Ổn định hình ảnh quang học 5.5 stop, - Khoảng cách lấy nét gần nhất24cm, - Vòng điều khiển có thể tùy chỉnh, - Cấu trúc kháng bụi và ẩm, - 9 lá khẩu tròn, - Kích cỡ kính lọc: 67mm(phía trước), - Trọng lượng:495g');

-- Thêm dữ liệu vào bảng dim_time
INSERT INTO dim_time (time_id, date, month, year) VALUES
(1, '2024-10-15', 10, 2024);

-- Thêm dữ liệu vào bảng fact_price
INSERT INTO fact_price (fact_id, product_id, time_id, current_price, original_price, discount_percentage) VALUES
(1, 1, 1, 3990000.00, NULL, NULL),
(2, 2, 1, 28980000.00, NULL, NULL),
(3, 3, 1, 30180000.00, NULL, NULL),
(4, 4, 1, 20980000.00, NULL, NULL),
(5, 5, 1, 29990000.00, NULL, NULL);

-- Thêm dữ liệu vào bảng file_config
INSERT INTO file_config (config_id, file_name, last_extracted, schedule_id) VALUES
(1, 'camera_data_extraction', '2024-10-15 08:55:15', NULL);

-- Thêm dữ liệu vào bảng file_logs
INSERT INTO file_logs (log_id, config, status, timestamp, error_message) VALUES
(1, 1, 'Success', '2024-10-15 08:55:15', NULL);

-- Thêm dữ liệu vào bảng products
INSERT INTO products (products_id, product_name, brand_name, current_price, discount_percentage, last_updated) VALUES
(1, 'TTArtisan AF 27mm f/2.8 for Fujifilm (Chính hãng)', 'TTArtisan', 3990000.00, NULL, '2024-10-15 08:55:15'),
(2, 'Canon EOS R100 Kit 18-45mm + RF-S 18-150mm', 'Canon', 28980000.00, NULL, '2024-10-15 08:55:15'),
(3, 'Canon EOS R100 Kit 18-45mm + Sigma 18-50mm f/2.8', 'Canon', 30180000.00, NULL, '2024-10-15 08:55:15'),
(4, 'Sony ZV-1 II Vlog On The Go Edition (Chính hãng)', 'Sony', 20980000.00, NULL, '2024-10-15 08:55:15'),
(5, 'Canon RF 28-70mm f/2.8 IS STM (Chính hãng)', 'Canon', 29990000.00, NULL, '2024-10-15 08:55:15');

-- Thêm dữ liệu vào bảng staging_camera
INSERT INTO staging_camera (camera_name, brand_name, image_url, price, original_price, discount_percentage, description) VALUES
('TTArtisan AF 27mm f/2.8 for Fujifilm', 'TTArtisan', 'https://zshop.vn/images/thumbnails/460/460/detailed/171/AF2728-X-B__1_.jpg', 3990000.00, NULL, NULL, '- Ngàm Fujifilm X định dạng APS-C, Tiêu cự 40.5mm (tương đương full frame), Khẩu độ f/2.8 đến f/16, Động cơ AF bước STM, Khoảng cách lấy nét gần nhất 35cm, 7 lá khẩu, Kích cỡ kính lọc: 39mm (phía trước)'),
('Canon EOS R100 Kit 18-45mm + RF-S 18-150mm', 'Canon', 'https://zshop.vn/images/thumbnails/460/460/detailed/171/CANON_R100_RFS1850_1.jpg', 28980000.00, NULL, NULL, '- Cảm biến ảnh APS-C CMOS 24.1MP, - Chip xử lý hình ảnh DIGIC 8, - Quay video 4K 24p có crop, Full HD 60p, - Lấy nét Dual Pixel CMOS AF với 143 vùng AF, - EVF OLED 2.36M điểm, - Màn hình LCD 3" 1.04M điểm, - Chụp liên tiếp màn trập điện 6.5 fps, - Chế độ Creative Assist, - Wi-Fi & Bluetooth, 1 khe thẻ SD, - Trọng lượng:309g (body only), - Ống kính kit Canon RF-S 18-45mm f/4.5-6.3 IS STM, - Ống kính combo Canon RF-S 18-150mm f/3.5-6.3 IS STM'),
('Canon EOS R100 Kit 18-45mm + Sigma 18-50mm f/2.8', 'Canon', 'https://zshop.vn/images/thumbnails/460/460/detailed/171/CANON_R100_S1850_1.jpg', 30180000.00, NULL, NULL, '- Cảm biến ảnh APS-C CMOS 24.1MP, - Chip xử lý hình ảnh DIGIC 8, - Quay video 4K 24p có crop, Full HD 60p, - Lấy nét Dual Pixel CMOS AF với 143 vùng AF, - EVF OLED 2.36M điểm, - Màn hình LCD 3" 1.04M điểm, - Chụp liên tiếp màn trập điện 6.5 fps, - Chế độ Creative Assist, - Wi-Fi & Bluetooth, 1 khe thẻ SD, - Trọng lượng:309g (body only), - Ống kính kit Canon RF-S 18-45mm f/4.5-6.3 IS STM, - Ống kính combo Sigma 18-50mm f/2.8 DC DN (C) Canon RF-S'),
('Sony ZV-1 II Vlog On The Go Edition (Chính hãng)', 'Sony', 'https://zshop.vn/images/thumbnails/460/460/detailed/170/SONY_ZV1_II__VL.jpg', 20980000.00, NULL, NULL, '- Dành cho vlogger và người sáng tạo nội dung, - Cảm biến Exmor RS BSI CMOS 1" 20.1MP, - Ống kính góc rộng tương đương 18-50mm f/1.8-4, - Quay video 4K30p UHD với HLG & S-Log3/2, - Màn hình cảm ứng LCD 3", xoay lật, - Điều khiển phơi sáng & lấy nét cảm ứng trực quan, - Lấy nét Real-Time Tracking & Eye AF, - Cần gạt bokeh, Face Priority AE, - Thiết lập Cinematic Vlog, chế độ quay S&Q, - Chế độ giới thiệu sản phẩm Product Showcase'),
('Canon RF 28-70mm f/2.8 IS STM (Chính hãng)', 'Canon', 'https://zshop.vn/images/thumbnails/460/460/detailed/170/canon_6535c002_rf_28_70mm_f_2_8_is_PO.jpg', 29990000.00, NULL, NULL, '- Ngàm RF full frame, - Khẩu độ f/2.8 đến f/22, - Gồm 2 thấu kính UD và 2 thấu kính phi cầu, - Lớp phủ Super Spectra Coating (SSC), - Động cơ lấy nét tự động bước STM, - Ổn định hình ảnh quang học 5.5 stop, - Khoảng cách lấy nét gần nhất24cm, - Vòng điều khiển có thể tùy chỉnh, - Cấu trúc kháng bụi và ẩm, - 9 lá khẩu tròn, - Kích cỡ kính lọc: 67mm(phía trước), - Trọng lượng:495g');
