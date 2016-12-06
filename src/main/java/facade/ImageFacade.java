package facade;

import entity.Image;
import entity.ImageDescription;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by vkrissz on 2016.12.06..
 */
@Stateless
public class ImageFacade  extends AbstractFacade<Image> {

    @PersistenceContext
    private EntityManager em;

    public ImageFacade() {
        super(Image.class);
    }

    @Override
    protected EntityManager em() {
        return em;
    }
}
