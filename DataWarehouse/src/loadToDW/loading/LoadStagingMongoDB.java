package loadToDW.loading;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bson.Document;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

public class LoadStagingMongoDB {
    public static void main(String[] args) {
        try {
            // Đọc file JSON từ thư mục resources
            InputStream inputStream = LoadStagingMongoDB.class.getClassLoader().getResourceAsStream("data_json/data_2024-12-05.json");
            if (inputStream == null) {
                throw new RuntimeException("Không tìm thấy file JSON trong thư mục resources/data_json.");
            }

            // Đọc dữ liệu từ file JSON
            Gson gson = new Gson();
            Type productListType = new TypeToken<List<Product>>() {}.getType();
            List<Product> products = gson.fromJson(new InputStreamReader(inputStream, "UTF-8"), productListType);

            // Kết nối tới MongoDB
            try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) { // URL MongoDB
                MongoDatabase database = mongoClient.getDatabase("DataWarehouse"); // Tên database
                MongoCollection<Document> collection = database.getCollection("ProductStaging"); // Tên collection

                // Xóa dữ liệu cũ trong collection
                collection.drop();
                System.out.println("Đã xoá dữ liệu cũ trong collection ProductStaging.");

                // Chèn dữ liệu mới vào MongoDB
                for (Product product : products) {
                    Document doc = new Document()
                            .append("link", product.getLink())
                            .append("image_src", product.getImgSrc())
                            .append("title", product.getTitle())
                            .append("price", product.getPrice().replace(",", "")) // Xóa dấu phẩy nếu có
                            .append("summary", product.getSummary()) // Giữ nguyên Map summary
                            .append("retrieval_time", product.getRetrievalTime());
                    collection.insertOne(doc);
                }

                System.out.println("Đã chèn " + products.size() + " sản phẩm vào collection ProductStaging.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
