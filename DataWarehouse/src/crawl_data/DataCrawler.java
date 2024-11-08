package crawl_data;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataCrawler {

    public static List<Map<String, Object>> fetchData(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();

        List<Map<String, Object>> data = new ArrayList<>();

        // Lấy thời gian hiện tại theo giờ UTC+7
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+7"));
        String formattedTime = sdf.format(new Date());

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
            String price = article.select("span.ty-price-num").text().strip();

            if (!imageSrc.isEmpty() && !price.isEmpty()) {
                Map<String, Object> summary = new HashMap<>();

                if (!link.isEmpty()) {
                    // Ghi log: "Đang crawl"
                    Log.logInProgress("Crawl May Anh");

                    // Lấy chi tiết sản phẩm
                    Document detailDoc = Jsoup.connect(link).get();
                    Elements detailItems = detailDoc.select(".list_OMS5rN7R1Z li");

                    for (int i = 0; i < detailItems.size() && i < propertyNames.length; i++) {
                        String text = detailItems.get(i).text().strip();
                        summary.put(propertyNames[i], text);
                    }

                    // Ghi log: "Crawl hoàn thành"
                    Log.logCompleted("Crawl May Anh");
                }

                // Chỉ thêm sản phẩm nếu có thông tin về giá và summary
                if (!summary.isEmpty()) {
                    Map<String, Object> product = new HashMap<>();
                    product.put("link", link);
                    product.put("img_src", imageSrc);
                    product.put("title", title);
                    product.put("price", price);
                    product.put("summary", summary);
                    product.put("retrieval_time", formattedTime);

                    data.add(product);
                }
            }
        }

        return data;
    }

    public static void main(String[] args) {
        // Ghi log trạng thái "Bắt đầu crawl"
        Log.logStart("Crawl May Anh");

        String[] urls = {
                "https://zshop.vn/may-anh/",
                "https://zshop.vn/may-anh/page-2/",
                "https://zshop.vn/may-anh/page-3/"
        };

        List<Map<String, Object>> allData = new ArrayList<>();

        for (String url : urls) {
            try {
                List<Map<String, Object>> pageData = fetchData(url);
                allData.addAll(pageData);
            } catch (IOException e) {
                Log.logError("Crawl May Anh", "Error while fetching data from " + url + ": " + e.getMessage());
            }
        }

        // Ghi log trạng thái "Hoàn thành"
        Log.logCompleted("Crawl May Anh");

        // In kết quả ra console hoặc có thể lưu vào file nếu cần
        System.out.println(allData);
    }
}
