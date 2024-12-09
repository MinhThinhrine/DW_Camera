package loadtoStagging;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import crawl_data.Product;
import db_control.Log;
import org.bson.Document;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import loadDW.LoadToDW;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static db_control.DbControl.handleLog;

public class load_staging {

    ///1. Hàm lấy file JSON mới nhất trong thư mục
    public static File getLatestJsonFile(String folderPath) {
        File folder = new File(folderPath);

        ///1.1  Lọc file JSON trong thư mục
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null || files.length == 0) {
            throw new RuntimeException("Không tìm thấy file JSON nào trong thư mục " + folderPath);
        }

        ///1.2 Sắp xếp file theo tên giảm dần
        List<File> sortedFiles =
                List.of(files).stream()
                        .sorted((f1, f2) -> f2.getName().compareTo(f1.getName())) // Giảm dần
                        .collect(Collectors.toList());

        ///1.3 Trả về file mới nhất
        return sortedFiles.get(0);
    }

    public static void main(String[] args) {
        try {
            // Thư mục chứa các file JSON
            String folderPath = "D:\\Intellij\\DW_Camera\\DataWarehouse\\src\\data_json";

            // Lấy file JSON mới nhất
            File latestFile = getLatestJsonFile(folderPath);
            String fileName = latestFile.getName();

            ///2. Ghi log khi đọc dữ liệu từ file JSON
            handleLog(new Log("INFO", "Bắt đầu đọc dữ liệu từ file JSON: " + fileName, "Load Staging", "READ"));

            ///3. Đọc dữ liệu từ file JSON với SummaryDeserializer
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(new TypeToken<Map<String, String>>() {}.getType(), new SummaryDeserializer())
                    .create();
            Type productListType = new TypeToken<List<Product>>() {}.getType();

            // Đọc nội dung từ file JSON
            List<Product> products = gson.fromJson(new FileReader(latestFile), productListType);

            ///4. Ghi log khi đọc file thành công
            handleLog(new Log("INFO", "Đọc dữ liệu từ file JSON thành công: " + fileName, "Load Staging", "READ_SUCCESS"));

            String mongoUrl = "mongodb+srv://root:root@darius.yjud9.mongodb.net/?retryWrites=true&w=majority&appName=loadToDW";

            ///5. Kết nối tới MongoDB
            try (MongoClient mongoClient = MongoClients.create(mongoUrl)) {
                MongoDatabase database = mongoClient.getDatabase("Staging"); // Tên database
                MongoCollection<Document> collection = database.getCollection("ProductStaging"); // Tên collection

                ///6. Xóa dữ liệu cũ trong collection
                collection.drop();
                handleLog(new Log("INFO", "Đã xóa dữ liệu cũ trong collection ProductStaging.", "Load Staging", "DELETE"));

                ///7. Chèn dữ liệu mới vào MongoDB
                for (Product product : products) {
                    Document doc = new Document()
                            .append("link", product.getLink())
                            .append("image_src", product.getImageSrc())
                            .append("title", product.getTitle())
                            .append("price", product.getPrice()) // Xóa dấu phẩy nếu có
                            .append("summary", product.getSummary()) // Giữ nguyên Map summary
                            .append("retrieval_time", product.getRetrievalTime());
                    collection.insertOne(doc);
                }

                // Ghi log khi chèn dữ liệu thành công
                handleLog(new Log("INFO", "Đã chèn " + products.size() + " sản phẩm vào collection ProductStaging.", "Load Staging", "INSERT"));
            }
            LoadToDW.run();
        } catch (Exception e) {
            // Ghi log lỗi khi xảy ra lỗi
            handleLog(new Log("ERROR", "Lỗi khi load dữ liệu: " + e.getMessage(), "Load Staging", "ERROR"));
            e.printStackTrace();
        }
    }
}
