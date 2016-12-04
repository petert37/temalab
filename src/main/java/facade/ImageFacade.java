package facade;

import entity.Image;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ImageFacade extends AbstractFacade<Image> {

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
