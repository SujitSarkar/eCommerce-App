package sarkar.kinboami.model;

public class ProductDetails {
    private String pid;
    private String name;
    private String description;
    private String price;
    private String category;
    private String date;
    private String time;
    private String image;

    public ProductDetails(){}

    public ProductDetails(String pid, String name, String description, String price, String category, String date, String time, String image) {
        this.pid = pid;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.date = date;
        this.time = time;
        this.image = image;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
