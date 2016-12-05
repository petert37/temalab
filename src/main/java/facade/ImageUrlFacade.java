package facade;

import entity.Image;
import entity.ImageUrl;
import servlet.AccessImage;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by vkrissz on 2016.12.04..
 */
@Stateless
public class ImageUrlFacade extends AbstractFacade<ImageUrl> {

    @PersistenceContext
    private EntityManager em;

    public ImageUrlFacade() {
        super(ImageUrl.class);
    }

    @Override
    protected EntityManager em() {
        return em;
    }
}