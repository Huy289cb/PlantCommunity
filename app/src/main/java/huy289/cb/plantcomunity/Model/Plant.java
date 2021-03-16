package huy289.cb.plantcomunity.Model;

import java.util.List;

public class Plant {

    private String id;
    private String imageUrl;
    private String name;
    private String age;
    private String quantity;
    private String price;
    private String address;
    private String publisher;
    private String sowingDate;
    private String plantDate;

    public Plant() {
    }

    public Plant(String id, String imageUrl, String name, String age, String quantity, String price, String address, String publisher, String sowingDate, String plantDate) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.name = name;
        this.age = age;
        this.quantity = quantity;
        this.price = price;
        this.address = address;
        this.publisher = publisher;
        this.sowingDate = sowingDate;
        this.plantDate = plantDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getSowingDate() {
        return sowingDate;
    }

    public void setSowingDate(String sowingDate) {
        this.sowingDate = sowingDate;
    }

    public String getPlantDate() {
        return plantDate;
    }

    public void setPlantDate(String plantDate) {
        this.plantDate = plantDate;
    }
}
