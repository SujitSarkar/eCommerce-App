package sarkar.kinboami.model;

public class CartList {

    private String pid;
    private String name;
    private String price;
    private String date;
    private String time;
    private String quantity;
    private String discount;
    private String image;

    public CartList(){}

    public CartList(String pid, String name, String price, String date, String time, String quantity, String discount,String image) {
        this.pid = pid;
        this.name = name;
        this.price = price;
        this.date = date;
        this.time = time;
        this.quantity = quantity;
        this.discount = discount;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
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

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }
}
