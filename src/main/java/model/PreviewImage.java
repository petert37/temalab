package model;

public class PreviewImage {

    private long id;
    private byte[] preview;
    private String link;

    public PreviewImage(long id, byte[] preview, String link) {
        this.id = id;
        this.preview = preview;
        this.link = link;
    }
}
