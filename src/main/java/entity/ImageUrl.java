package entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * Created by vkrissz on 2016.12.04..
 */
@Entity
public class ImageUrl {

    @Id
    private String uid;

    @OneToOne
    private ImageDescription image;

    public ImageUrl(String uid, ImageDescription image) {
        this.uid = uid;
        this.image = image;
    }

    public ImageUrl() {
    }

    public String getUid() {
        return uid;
    }

    public ImageDescription getImage() {
        return image;
    }
}
