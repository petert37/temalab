package bean;

import entity.Image;
import entity.ImageDescription;
import entity.ImageUrl;
import facade.ImageDescriptionFacade;
import facade.ImageFacade;
import facade.ImageUrlFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class OperatorBean {

    @EJB
    private ImageDescriptionFacade imageDescriptionFacade;

    @EJB
    private ImageUrlFacade imageUrlFacade;

    @EJB
    private ImageFacade imageFacade;

    public void storeImageDescription(ImageDescription image) {
        imageDescriptionFacade.create(image);
    }

    public ImageDescription loadImageDescription(long imgID) {
        return imageDescriptionFacade.find(imgID);
    }

    public void storeImage(Image image) {
        imageFacade.create(image);
    }

    public Image loadImage(long imgID) {
        return imageFacade.find(imgID);
    }

    public List<ImageDescription> findAll() {
        return imageDescriptionFacade.findAll();
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

    public ImageUrl loadImageUrlWithImage(String uid) {
        ImageUrl url = imageUrlFacade.find(uid);
        if (url != null && url.getImage() != null) {
            url.getImage().getImage();
            url.getImage().getThumbnail();
        }
        return url;
    }
}
