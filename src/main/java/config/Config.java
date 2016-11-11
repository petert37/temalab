package config;

public class Config {

    private String imageUrl;
    private String imagePartUrl;
    private int maxConnections;
    private int imageDivision;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImagePartUrl() {
        return imagePartUrl;
    }

    public void setImagePartUrl(String imagePartUrl) {
        this.imagePartUrl = imagePartUrl;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public int getImageDivision() {
        return imageDivision;
    }

    public void setImageDivision(int imageDivision) {
        this.imageDivision = imageDivision;
    }

    @Override
    public String toString() {
        return "Config{" +
                "imageUrl='" + imageUrl + '\'' +
                ", imagePartUrl='" + imagePartUrl + '\'' +
                ", maxConnections=" + maxConnections +
                ", imageDivision=" + imageDivision +
                '}';
    }
}
