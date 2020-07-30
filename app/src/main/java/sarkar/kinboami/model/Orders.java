package sarkar.kinboami.model;

public class Orders {
    private String name;
    private String phone;
    private String totalAmount;
    private String city;
    private String district;
    private String region;
    private String house;
    private String postal;
    private String date;
    private String time;
    private String state;

    public Orders(){}

    public Orders(String name, String phone, String totalAmount, String city, String district, String region, String house, String postal, String date, String time, String state) {
        this.name = name;
        this.phone = phone;
        this.totalAmount = totalAmount;
        this.city = city;
        this.district = district;
        this.region = region;
        this.house = house;
        this.postal = postal;
        this.date = date;
        this.time = time;
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getPostal() {
        return postal;
    }

    public void setPostal(String postal) {
        this.postal = postal;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
