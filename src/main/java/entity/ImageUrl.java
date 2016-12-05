package entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 * Created by vkrissz on 2016.12.04..
 */
@Entity
public class ImageUrl {

    @Id
    private String uid;

    @OneToOne
    private Image image;

    public ImageUrl(String uid, Image image) {
        this.uid = uid;
        this.image = image;
    }

    public ImageUrl() {
    }

    public String getUid() {
        return uid;
    }

    public Image getImage() {
        return image;
    }
}
