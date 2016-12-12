package entity;

import javax.persistence.*;

@Entity
public class ImageDescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition="TEXT")
    private String world;

    @OneToOne(fetch = FetchType.LAZY)
    private Image image;

    @OneToOne(fetch = FetchType.LAZY)
    private Image thumbnail;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String link) {
        this.world = link;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Image getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Image thumbnail) {
        this.thumbnail = thumbnail;
    }
}
