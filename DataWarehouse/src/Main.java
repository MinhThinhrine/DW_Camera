import crawl_data.DataCrawler;
import loadDW.LoadToDW;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
//        DataCrawler dc = new DataCrawler();
//        dc.main(new String[]{"running"});
        String filePath = "D:\\Intellij\\DW_Camera\\DataWarehouse\\src/output.txt"; // Đường dẫn đến file sẽ tạo ra

        try (FileWriter writer = new FileWriter(filePath, true)) {
            writer.write("Task ran successfully!\n");
            System.out.println("File created: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
