package bean;

import entity.Image;
import entity.ImageUrl;
import facade.ImageUrlFacade;
import facade.ImageFacade;
import servlet.AccessImage;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class OperatorBean {

    @EJB
    private ImageFacade imageFacade;

    @EJB
    private ImageUrlFacade imageUrlFacade;

    public void storeImage(Image image) {
        imageFacade.create(image);
    }

    public Image loadImage(long imgID) {
        return imageFacade.find(imgID);
    }

    public List<Image> findAll() {
        return imageFacade.findAll();
    }

    public List<ImageUrl> findAllImageUrls() {
        return imageUrlFacade.findAll();
    }

    public void storeImageUrl(ImageUrl imageUrl) {
        imageUrlFacade.create(imageUrl);
    }

    public ImageUrl loadImageUrl(String uid) {
        return imageUrlFacade.find(uid);
    }
}
