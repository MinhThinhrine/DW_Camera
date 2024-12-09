package crawl_data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoDatabase;
import loadtoStagging.load_staging;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import db_control.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static db_control.DbControl.handleLog;

public class DataCrawler {

    public static List<Product> fetchData(String url, MongoDatabase connection) {
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

            /// 6.1 Crawl dữ liệu - Lấy danh sách các sản phẩm
            Elements articles = doc.select(".col-tile");

            for (Element article : articles) {
                String link = article.select("a").attr("href");
                String imageSrc = article.select("a.abt-single-image img").attr("data-srcset");
                String title = article.select("a.product-title").attr("title");

                // Lấy giá và xử lý
                String priceText = article.select("span.ty-price-num").text().strip();
                String priceStr = priceText.replaceAll("[^\\d]", ""); // Chỉ giữ lại số

                /// 6.2 Check null data
                if (!imageSrc.isEmpty() && !priceStr.isEmpty()) try {
                    // Chuyển đổi chuỗi giá thành kiểu int
                    int price = Integer.parseInt(priceStr);
                    Product product = new Product(link, imageSrc, title, price, "");

                    /// 6.2 Check null data
                    if (!link.isEmpty()) {
                        // Log: "Đang crawl"
                        handleLog( new Log("INFO", "Đang crawl dữ liệu: " + title, "Crawl Process", null));

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

                        /// 6.2 Check null data
                        // Chỉ thêm sản phẩm vào danh sách nếu summary không rỗng
                        if (!summary.isEmpty()) {
                            /// 6.2.1 Lưu dữ liệu
                            products.add(product);
                            /// 6.3 Ghi log
                            handleLog(  new Log("INFO", "Crawl hoàn thành sản phẩm: " + title, "Crawl Process", null));
                        } else
                            /// 6.2.2 Không lưu dữ liệu
                        {
                            /// 6.3 Ghi log
                            handleLog(  new Log("WARNNING", "Sản phẩm không có thông tin chi tiết: " + title, "Crawl Process", "No save"));
                        }
                    }
                } catch (NumberFormatException e) {
                    String errorDetails = "Sai định dạng giá: " + priceStr;
                    DbControl.handleLogException(errorDetails, "Crawl Process", e);
                    // Gửi email thông báo lỗi
                    EmailNotifier.sendFailureNotification(errorDetails + "\n" + e.getMessage());
                } catch (IOException e) {
                    String errorDetails = "Lỗi khi cào thông tin chi tiết: " + link;
                    DbControl.handleLogException(errorDetails, "Crawl Process", e);
                    // Gửi email thông báo lỗi
                    EmailNotifier.sendFailureNotification(errorDetails + "\n" + e.getMessage());
                }

            }
        } catch (IOException e) {
            String errorDetails = "Lỗi khi cào thông tin từ " + url;
            DbControl.handleLogException(errorDetails, "Crawl Process", e);
            // Gửi email thông báo lỗi
            EmailNotifier.sendFailureNotification(errorDetails + "\n" + e.getMessage());
        }

        return products;
    }

    public static void main(String[] args) {
        /// 1. Connect database
        MongoDatabase database = DbControl.getConnection(); // Kết nối MongoDB

        /// 2. Check config
        // Kiểm tra trạng thái crawl từ bảng config
        if (!DbControl.canRunCrawl()) {

            /// Ghi log và end task nếu đã chạy
            System.out.println("Crawl đã hoàn thành hôm nay. Không chạy lại.");
            handleLog(new Log("INFO", "Crawl đã hoàn thành hôm nay. Không chạy lại.", "Check config", "CONFIG"));
            EmailNotifier.sendAlreadyRunNotification();
            return; // Thoát nếu đã crawl hôm nay
        }

        /// 3. Ghi config
        Config crawlConfig = new Config("crawl_status", "in_progress", "Quá trình crawl đang thực hiện");
        DbControl.handleConfig(crawlConfig, false);

        /// 4. Ghi log
        handleLog(new Log("INFO", "Bắt đầu quá trình crawl dữ liệu", "Crawl Process", "START"));

             /// 5. Tiến hành crawl {{
        // Gửi url
        String[] urls = {
                "https://zshop.vn/may-anh/",
                "https://zshop.vn/may-anh/page-2/",
                "https://zshop.vn/may-anh/page-3/"
        };

        List<Product> allProducts = new ArrayList<>();

        for (String url : urls) {
            // Gọi hàm fetch
            List<Product> pageProducts = fetchData(url, database);
            allProducts.addAll(pageProducts);
        }
            /// }}

        /// 7. Ghi config
        // Cập nhật trạng thái trong config sau khi crawl xong
        crawlConfig.setValue("completed");
        crawlConfig.setDescription("Quá trình crawl đã hoàn thành");
        DbControl.handleConfig(crawlConfig, true);

        /// 8. Ghi log
        // Ghi log trạng thái "Hoàn thành"
        handleLog(new Log("INFO", "Hoàn thành quá trình crawl dữ liệu", "Crawl Process", "END"));
        EmailNotifier.sendSuccessNotification();
        /// 9. Lưu file dạng json
        // Gửi dữ liệu qua staging
        saveProductsToJson(allProducts);
        load_staging.main(new String[]{"run load_staging"});

        // In kết quả ra console
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        for (Product product : allProducts) {
            System.out.println("----------------------------------------------");
            System.out.println("Retrieval Time: " + product.getRetrievalTime());
            System.out.println("Title: " + product.getTitle());
            System.out.println("Price: " + decimalFormat.format(product.getPrice()) + " đ");
            System.out.println("Summary: " + product.getSummary());
        }
    }
    public static void saveProductsToJson(List<Product> products) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create(); // Tạo đối tượng Gson
        String folderPath = "D:\\Intellij\\DW_Camera\\DataWarehouse\\src/data_json"; // Đường dẫn tới thư mục lưu trữ
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
            // Ghi log thành công
            /// Ghi log của hàm
            handleLog(new Log("INFO", "Lưu data file JSON thành công: " + fileName, "Crawl Process", "SAVED"));
        } catch (IOException e) {
            e.printStackTrace();
            // Ghi log lỗi
            DbControl.handleLogException( "Lỗi khi lưu file: " + e.getMessage(), "Crawl Process", e);
        }
    }
}

