package facade;

import entity.ImageDescription;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ImageDescriptionFacade extends AbstractFacade<ImageDescription> {

    @PersistenceContext
    private EntityManager em;

    public ImageDescriptionFacade() {
        super(ImageDescription.class);
    }

    @Override
    protected EntityManager em() {
        return em;
    }
}
