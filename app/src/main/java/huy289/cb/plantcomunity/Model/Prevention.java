package huy289.cb.plantcomunity.Model;

public class Prevention {

    private String id;
    private String performer;
    private String imageUrl;
    private String description;
    private String date;
    private String plantId;

    public Prevention() {
    }

    public Prevention(String id, String performer, String imageUrl, String description, String date, String plantId) {
        this.id = id;
        this.performer = performer;
        this.imageUrl = imageUrl;
        this.description = description;
        this.date = date;
        this.plantId = plantId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPerformer() {
        return performer;
    }

    public void setPerformer(String performer) {
        this.performer = performer;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPlantId() {
        return plantId;
    }

    public void setPlantId(String plantId) {
        this.plantId = plantId;
    }
}
