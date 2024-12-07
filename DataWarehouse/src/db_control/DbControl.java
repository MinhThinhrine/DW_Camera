package db_control;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.TimeZone;

public class DbControl {

    private static final String CONNECTION_STRING = "mongodb+srv://root:root@my-cluster.j9mcl.mongodb.net/?retryWrites=true&w=majority&appName=my-cluster";
    private static final String DATABASE_NAME = "Dbcontrol";
    private static final String CONFIG_COLLECTION_NAME = "Config";
    private static final String LOG_COLLECTION_NAME = "Log";
    private static final Logger logger =  LoggerFactory.getLogger(DbControl.class);

    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "warn"); // Chỉ ghi log ở mức WARN trở lên
    }
    public static MongoDatabase getConnection() {
        MongoClient mongoClient = MongoClients.create(CONNECTION_STRING);
        return mongoClient.getDatabase(DATABASE_NAME);
    }

    // Xử lý ngoại lệ khi lưu Log
    public static void handleLogException(String message, String context, Exception e) {
        String fullMessage = message + (e != null ? ": " + e.getMessage() : "");

        // Tạo một log entry với chi tiết ngoại lệ
        Log log = new Log("ERROR", fullMessage, context, e != null ? e.toString() : null);

        // Attempt to save the log to the database
        handleLog(log);

        // In thông báo lỗi ra console
        System.err.println(getCurrentTime() + " [ERROR] " + context + " - " + fullMessage);
    }

    // Ghi log vào MongoDB
    public static void handleLog(Log log) {
        if (log == null) {
            System.err.println(getCurrentTime() + " [ERROR] Log object is null. Cannot save log.");
            return; // Prevent NullPointerException
        }

        try (MongoClient mongoClient = MongoClients.create(CONNECTION_STRING)) {
            MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
            MongoCollection<Document> logCollection = database.getCollection(LOG_COLLECTION_NAME);

            // Tạo document từ log
            Document logDocument = new Document("timestamp", LocalDateTime.now())
                    .append("level", log.getLevel())
                    .append("message", log.getMessage())
                    .append("context", log.getContext())
                    .append("stack_trace", log.getStackTrace());

            // Chèn document vào collection
            logCollection.insertOne(logDocument);
            System.out.println(getCurrentTime() + " [INFO] Log saved: " + log.getMessage());
        } catch (Exception e) {
            System.err.println(getCurrentTime() + " [ERROR] Failed to save log: " + e.getMessage());
        }
    }

    // Xử lý ngoại lệ khi lưu hoặc cập nhật Config
    public static void handleConfigException(Config config, boolean isUpdate, Exception e) {
        String action = isUpdate ? "update" : "save";
        String fullMessage = "Failed to " + action + " Config [" + config.getKey() + "]"
                + (e != null ? ": " + e.getMessage() : "");
        Log log = new Log("ERROR", fullMessage, "Config Process", e != null ? e.toString() : null);
        handleLog(log);
        System.err.println(getCurrentTime() + " [ERROR] Config Process - " + fullMessage);
    }

    // Lưu hoặc cập nhật Config
    public static void handleConfig(Config config, boolean isUpdate) {
        try (MongoClient mongoClient = MongoClients.create(CONNECTION_STRING)) {
            MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
            MongoCollection<Document> configCollection = database.getCollection(CONFIG_COLLECTION_NAME);

            Document configDocument = new Document("key", config.getKey())
                    .append("value", config.getValue())
                    .append("description", config.getDescription())
                    .append("created_at", LocalDateTime.now())
                    .append("updated_at", LocalDateTime.now());

            if (isUpdate) {
                // Cập nhật document
                configCollection.replaceOne(new Document("key", config.getKey()), configDocument);
                System.out.println(getCurrentTime() + " [INFO] Config updated: " + config.getKey());
            } else {
                // Lưu document mới
                configCollection.insertOne(configDocument);
                System.out.println(getCurrentTime() + " [INFO] Config saved: " + config.getKey());
            }
        } catch (Exception e) {
            handleConfigException(config, isUpdate, e);
        }
    }

    public static boolean canRunCrawl() {
        LocalDate today = LocalDate.now();
        String todayStr = today.toString(); // Định dạng thành yyyy-MM-dd

        try (MongoClient mongoClient = MongoClients.create(CONNECTION_STRING)) {
            MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
            MongoCollection<Document> configCollection = database.getCollection(CONFIG_COLLECTION_NAME);

            Document query = new Document("key", "crawl_status")
                    .append("created_at", new Document("$gte", today.atStartOfDay()).append("$lt", today.plusDays(1).atStartOfDay()));

            Document result = configCollection.find(query).first();

            if (result != null) {
                String description = result.getString("description");

                // Kiểm tra xem quá trình crawl đã hoàn thành hay chưa
                return !("Quá trình crawl đã hoàn thành".equalsIgnoreCase(description));
            }
        } catch (Exception e) {
            System.err.println("Error while checking crawl status: " + e.getMessage());
        }

        // Nếu không tìm thấy mục nào cho hôm nay hoặc nó chưa hoàn thành, cho phép crawl chạy
        return true;
    }

    // Tiện ích lấy thời gian hiện tại
    private static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+7"));
        return "[" + sdf.format(new java.util.Date()) + "]";
    }
    public static void main(String[] args) {
        // Kiểm tra kết nối
        try (MongoClient mongoClient = MongoClients.create(CONNECTION_STRING)) {
            mongoClient.getDatabase(DATABASE_NAME).runCommand(new Document("ping", 1));
            System.out.println("Kết nối với MongoDB thành công!");
        } catch (Exception e) {
            System.err.println("Lỗi khi kết nối đến MongoDB: " + e.getMessage());
            return;
        }

        // Ghi log
        Log log = new Log("INFO", "This is a test log message.", "Main Method", null);
        handleLog(log);

        // Lưu config
        Config config = new Config("sample_key", "sample_value", "This is a sample config.");
        handleConfig(config, false); // false indicates it's a new save

        // Cập nhật config
        config.setValue("updated_value");
        handleConfig(config, true); // true indicates it's an update
    }
}