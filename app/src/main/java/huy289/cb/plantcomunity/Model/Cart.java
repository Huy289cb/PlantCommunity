package huy289.cb.plantcomunity.Model;

public class Cart {

    private String plantId;
    private String quantity;
    private String price;

    public Cart() {
    }

    public Cart(String plantId, String quantity, String price) {
        this.plantId = plantId;
        this.quantity = quantity;
        this.price = price;
    }

    public String getPlantId() {
        return plantId;
    }

    public void setPlantId(String plantId) {
        this.plantId = plantId;
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
}
