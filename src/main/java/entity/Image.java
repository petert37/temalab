package entity;

import javax.persistence.*;

@Entity
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(columnDefinition="TEXT")
    private String world;

    @Lob
    private byte[] png;

    @Lob
    private byte[] preview;

    public long getId() {
        return id;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String link) {
        this.world = link;
    }

    public byte[] getPng() {
        return png;
    }

    public void setPng(byte[] png) {
        this.png = png;
    }

    public byte[] getPreview() {
        return preview;
    }

    public void setPreview(byte[] preview) {
        this.preview = preview;
    }
}
