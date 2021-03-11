package huy289.cb.plantcomunity.Model;

public class Post {

    private String postId;
    private String imageUrl;
    private String publisher;
    private String description;

    public Post() {
    }

    public Post(String postId, String imageUrl, String publisher, String description) {
        this.postId = postId;
        this.imageUrl = imageUrl;
        this.publisher = publisher;
        this.description = description;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
