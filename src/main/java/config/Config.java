package config;

public class Config {
    private int partsPerRequest = 100;
    private int pixelsPerRequest = 11500;
    private int maxConnections = 100;

    public int getPartsPerRequest() {
        return partsPerRequest;
    }

    public void setPartsPerRequest(int partsPerRequest) {
        this.partsPerRequest = partsPerRequest;
    }

    public int getPixelsPerRequest() {
        return pixelsPerRequest;
    }

    public void setPixelsPerRequest(int pixelsPerRequest) {
        this.pixelsPerRequest = pixelsPerRequest;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    @Override
    public String toString() {
        return "Config{" +
                "partsPerRequest=" + partsPerRequest +
                ", pixelsPerRequest=" + pixelsPerRequest +
                ", maxConnections=" + maxConnections +
                '}';
    }
}
