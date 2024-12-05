package crawl_data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import loadtoStagging.Stagging;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import db_control.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static db_control.DbControl.canRunCrawl;
import static db_control.DbControl.handleLog;

public class DataCrawler {

    public static List<Product> fetchData(String url, Connection connection) {
        List<Product> products = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(url).get();

            // Lấy thời gian hiện tại theo giờ UTC+7
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+7"));
            String formattedTime = sdf.format(new java.util.Date());

            // Các tên thuộc tính để lấy thông tin chi tiết
            String[] propertyNames = {
                    "Image Sensor", "Image Processor", "Video Capability", "Focusing System",
                    "Viewfinder", "LCD Screen", "Continuous Shooting Speed", "Creative Mode",
                    "Connectivity", "Memory Card Slot", "Kit Lens", "Combo Lens", "Weight"
            };

            // Lấy danh sách các sản phẩm
            Elements articles = doc.select(".col-tile");

            for (Element article : articles) {
                String link = article.select("a").attr("href");
                String imageSrc = article.select("a.abt-single-image img").attr("data-srcset");
                String title = article.select("a.product-title").attr("title");

                // Lấy giá và xử lý
                String priceText = article.select("span.ty-price-num").text().strip();
                String priceStr = priceText.replaceAll("[^\\d]", ""); // Chỉ giữ lại số

                if (!imageSrc.isEmpty() && !priceStr.isEmpty()) try {
                    // Chuyển đổi chuỗi giá thành kiểu int
                    int price = Integer.parseInt(priceStr);

                    Product product = new Product(link, imageSrc, title, price, "");

                    if (!link.isEmpty()) {
                        // Log: "Đang crawl"
                        handleLog(connection, new Log("INFO", "Đang crawl dữ liệu: " + title, "Crawl Process", null));

                        // Lấy chi tiết sản phẩm
                        Document detailDoc = Jsoup.connect(link).get();
                        Elements detailItems = detailDoc.select(".list_OMS5rN7R1Z li");

                        StringBuilder summaryBuilder = new StringBuilder();

                        for (int i = 0; i < detailItems.size() && i < propertyNames.length; i++) {
                            String text = detailItems.get(i).text().strip();
                            summaryBuilder.append(propertyNames[i]).append(": ").append(text).append("\n");
                        }

                        String summary = summaryBuilder.toString().trim();
                        product.setSummary(summary);
                        product.setRetrievalTime(formattedTime); // Ghi lại thời gian lấy dữ liệu

                        // Log: "Crawl hoàn thành"
                        handleLog(connection, new Log("INFO", "Crawl hoàn thành sản phẩm: " + title, "Crawl Process", null));

                        // Chỉ thêm sản phẩm vào danh sách nếu summary không rỗng
                        if (!summary.isEmpty()) {
                            products.add(product);
                        } else {
                            handleLog(connection, new Log("WARNNING", "Sản phẩm không có thông tin chi tiết: " + title, "Crawl Process", "No save"));
                        }
                    }
                } catch (NumberFormatException e) {
                    DbControl.handleLogException(connection, "Sai định dạng giá: " + priceStr, "Crawl Process", e);
                } catch (IOException e) {
                    DbControl.handleLogException(connection, "Lỗi khi cào thông tin chi tiết: " + link, "Crawl Process", e);
                }
            }
        } catch (IOException e) {
            DbControl.handleLogException(connection, "Lỗi khi cào thông tin từ " + url, "Crawl Process", e);
        }
        return products;
    }

    public static void main(String[] args) {
        try (Connection connection = DbControl.getConnection()) {
            // Kiểm tra trạng thái crawl từ bảng config
            if (!DbControl.canRunCrawl(connection)) {
                System.out.println("Crawl đã hoàn thành hôm nay. Không chạy lại.");
                handleLog(connection, new Log("INFO", "Crawl đã hoàn thành hôm nay. Không chạy lại.", "Check config", "CONFIG"));

                return; // Thoát nếu đã crawl hôm nay
            }

            // Cập nhật trạng thái trong config (crawl_status)
            Config crawlConfig = new Config("crawl_status", "in_progress", "Quá trình crawl đang thực hiện");
            DbControl.handleConfig(connection, crawlConfig, false);

            // Ghi log trạng thái "Bắt đầu crawl"
            handleLog(connection, new Log("INFO", "Bắt đầu quá trình crawl dữ liệu", "Crawl Process", "START"));

            String[] urls = {
                    "https://zshop.vn/may-anh/",
                    "https://zshop.vn/may-anh/page-2/",
                    "https://zshop.vn/may-anh/page-3/"
            };

            List<Product> allProducts = new ArrayList<>();

            for (String url : urls) {
                List<Product> pageProducts = fetchData(url, connection);
                allProducts.addAll(pageProducts);
            }

            // Cập nhật trạng thái trong config sau khi crawl xong
            crawlConfig.setValue("completed");
            crawlConfig.setDescription("Quá trình crawl đã hoàn thành");
            DbControl.handleConfig(connection, crawlConfig, true);

            // Ghi log trạng thái "Hoàn thành"
            handleLog(connection, new Log("INFO", "Hoàn thành quá trình crawl dữ liệu", "Crawl Process", "END"));

            // Gửi dữ liệu qua staging
            saveProductsToJson(allProducts);
            // In kết quả ra console
            DecimalFormat decimalFormat = new DecimalFormat("#,###");
            for (Product product : allProducts) {
                System.out.println("----------------------------------------------");
                System.out.println("Retrieval Time: " + product.getRetrievalTime());
                System.out.println("Title: " + product.getTitle());
                System.out.println("Price: " + decimalFormat.format(product.getPrice()) + " đ");
                System.out.println("Summary: " + product.getSummary());
            }
        } catch (SQLException e) {
            DbControl.handleLogException(null, "SQL error during the crawl process", "Crawl Process", e);
        }
    }

    public static void saveProductsToJson(List<Product> products) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create(); // Tạo đối tượng Gson
        String folderPath = "src/data_json"; // Đường dẫn tới thư mục lưu trữ
        String fileName = "data_" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".json"; // Tạo tên file

        // Thay thế dấu , bằng dấu ; trong các thuộc tính
        for (Product product : products) {
            product.setSummary(product.getSummary().replace(",", ";")); // Thay thế dấu ,
            product.setTitle(product.getTitle().replace(",", ";")); // Thay thế dấu , trong title nếu cần
        }

        // Tạo thư mục nếu không tồn tại
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File jsonFile = new File(folderPath, fileName); // Tạo file JSON

        try (FileWriter writer = new FileWriter(jsonFile)) {
            gson.toJson(products, writer); // Chuyển đổi danh sách sản phẩm thành JSON và ghi vào file
        } catch (IOException e) {
            e.printStackTrace();
            // Ghi log lỗi
            DbControl.handleLogException(null, "Lỗi khi lưu file: " + e.getMessage(), "Crawl Process", e);
            return; // Dừng thực hiện nếu có lỗi
        }

// Ghi log thành công
        try (Connection connection = DbControl.getConnection()) {
            handleLog(connection, new Log("INFO", "Lưu data file json", "Crawl Process", "SAVED"));
        } catch (SQLException e) {
            DbControl.handleLogException(null, "SQL error during the crawl process", "Crawl Process", e);
        }
    }
}
