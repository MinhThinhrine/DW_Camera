package org.example;

import java.util.Map;

public class Product {
    private String link;
    private String img_src;
    private String title;
    private String price;
    private Map<String, String> summary; // Dùng Map để lưu key-value của "summary"
    private String retrieval_time;

    // Getters và setters (nếu cần thiết)
    public String getLink() {
        return link;
    }

    public String getImgSrc() {
        return img_src;
    }

    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }

    public Map<String, String> getSummary() {
        return summary;
    }

    public String getRetrievalTime() {
        return retrieval_time;
    }
}
