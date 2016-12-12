package facade;

import entity.ImageUrl;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

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

    public List<ImageUrl> findAllDone() {
        return em.createQuery("select url from ImageUrl url JOIN url.image imd JOIN imd.image img where img.data is not null").getResultList();
//        return em.createNativeQuery("select uid from ImageUrl, ImageDescription, Image where ImageUrl.image_id = ImageDescription.id and ImageDescription.image_id = Image.id and Image.data is not null", ImageUrl.class).getResultList();
    }
}