package bean;

import entity.Image;
import facade.ImageFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class OperatorBean {

    @EJB
    private ImageFacade imageFacade;

    public void storeImage(Image image) {
        imageFacade.create(image);
    }

    public Image loadImage(long imgID) {
        return imageFacade.find(imgID);
    }

    public List<Image> findAll() {
        return imageFacade.findAll();
    }
}
