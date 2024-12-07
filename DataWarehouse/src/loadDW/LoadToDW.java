package loadDW;

import com.mongodb.client.MongoClients;
import com.mongodb.client.model.UpdateOptions;
import db_control.Log;
import org.bson.Document;

import java.time.Instant;
import java.util.ArrayList;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.nin;
import static db_control.DbControl.handleLog;

public class LoadToDW {
    private static final String CONNECTION_URI = "mongodb+srv://root:root@darius.yjud9.mongodb.net/?retryWrites=true&w=majority&appName=darius";
    private static final String STAGING_DATABASE = "Staging";
    private static final String STAGING_PRODUCT = "productStagging";
    private static final String DATA_WAREHOUSE_DATABASE = "DataWarehouse";
    private static final String DATA_WAREHOUSE_DIM_PRODUCT = "dimProduct";

    public static void run() {
        try (
            // Step 1: Connect to MongoDB server
            var client = MongoClients.create(CONNECTION_URI)
        ) {
            // Step 2: Connect to Stating database
            var stagingDb = client.getDatabase(STAGING_DATABASE);
            // Step 3: Connect to Data Warehouse database
            var dataWarehouseDb = client.getDatabase(DATA_WAREHOUSE_DATABASE);
            // Step 4: Connect to `productStaging` collection in Stating database
            var productStaging = stagingDb.getCollection(STAGING_PRODUCT);
            // Step 5: Connect to `dimProduct` collection in Data Warehouse database
            var dimProduct = dataWarehouseDb.getCollection(DATA_WAREHOUSE_DIM_PRODUCT);

            handleLog(new Log("INFO", "Kết nối thành công MongoDB.", "Data warehouse", null));

            handleLog(new Log("INFO", "Tiến hành thêm dữ liệu vào data warehouse.", "Data warehouse", null));

            // Step 6: Upsert data from `productStaging` into `dimProduct`
            // If a document with the given filter doesn't exist,
            // it will insert a new one. If it exists, it updates it.
            for (var doc : productStaging.find()) {
                var title = doc.getString("title");
                doc.put("saved_time", Instant.now());
                dimProduct.updateOne(
                        eq("title", title),
                        new Document("$set", doc),
                        new UpdateOptions().upsert(true)
                );
            }

            handleLog(new Log("INFO", "Hoàn thành thêm dữ liệu vào data warehouse.", "Data warehouse", null));

            handleLog(new Log("INFO", "Tiến hành xóa dữ liệu trong data warehouse.", "Data warehouse", null));

            // Step 7: Remove documents from `dimProduct` that are no longer in `productStaging`
            var stagingTitles = new ArrayList<>();
            for (var doc : productStaging.find()) {
                stagingTitles.add(doc.getString("title"));
            }
            // Step 7 (cont): Delete from `dimProduct` where `title` not in `productStaging`
            dimProduct.deleteMany(nin("title", stagingTitles));

            handleLog(new Log("INFO", "Hoàn thành xóa dữ liệu trong data warehouse.", "Data warehouse", null));
        }
    }

    public static void main(String[] args) {
        LoadToDW.run();
    }
}
