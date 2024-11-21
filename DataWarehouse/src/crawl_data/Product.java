package crawl_data;

class Product {
    private String link;
    private String imageSrc;
    private String title;
    private int price; // Giá được lưu dưới dạng số nguyên
    private String summary;
    private String retrievalTime;

    public Product(String link, String imageSrc, String title, int price, String summary) {
        this.link = link;
        this.imageSrc = imageSrc;
        this.title = title;
        this.price = price;
        this.summary = summary;
    }

    // Getter và Setter
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImageSrc() {
        return imageSrc;
    }

    public void setImageSrc(String imageSrc) {
        this.imageSrc = imageSrc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getRetrievalTime() {
        return retrievalTime;
    }

    public void setRetrievalTime(String retrievalTime) {
        this.retrievalTime = retrievalTime;
    }
}