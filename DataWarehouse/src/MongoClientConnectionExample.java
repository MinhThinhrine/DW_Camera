
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoClientConnectionExample {
    public static void main(String[] args) {
        String connectionString = "mongodb+srv://root:root@darius.yjud9.mongodb.net/?retryWrites=true&w=majority&appName=darius";


        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .build();

        // Create a new client and connect to the server
        try (MongoClient mongoClient = MongoClients.create(settings)) {
            try {
                // Gửi ping để xác nhận kết nối thành công
                MongoDatabase database = mongoClient.getDatabase("admin");
                database.runCommand(new Document("ping", 1));
                System.out.println("Pinged your deployment. You successfully connected to MongoDB!");

                // Kết nối đến collection log
                MongoDatabase logDatabase = mongoClient.getDatabase("Dbcontrol");
                MongoCollection<Document> logCollection = logDatabase.getCollection("log");

                // Tạo document mới để ghi vào collection
                Document logDocument = new Document("field", "value")
                        .append("timestamp", System.currentTimeMillis());

                // Chèn document vào collection
                logCollection.insertOne(logDocument);
                System.out.println("Document inserted into 'log' collection.");

            } catch (MongoException e) {
                e.printStackTrace();
            }
        }
    }
}
