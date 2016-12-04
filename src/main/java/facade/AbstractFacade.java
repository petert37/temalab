package facade;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public abstract class AbstractFacade<T> {
    private Class<T> entityClass;

    AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract EntityManager em();

    public void create(T entity) {
        em().persist(entity);
    }

    public void edit(T entity) {
        em().merge(entity);
    }

    public void remove(T entity) {
        em().remove(em().merge(entity));
    }

    public T find(Object id) {
        return em().find(entityClass, id);
    }

    public List<T> findAll() {
        CriteriaQuery<T> cq = em().getCriteriaBuilder()
                .createQuery(entityClass);
        cq.select(cq.from(entityClass));
        return em().createQuery(cq).getResultList();
    }

    public List<T> findRange(int[] range) {
        CriteriaQuery<T> cq = em().getCriteriaBuilder()
                .createQuery(entityClass);
        cq.select(cq.from(entityClass));
        TypedQuery<T> q = em().createQuery(cq);
        q.setMaxResults(range[1] - range[0]);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    public int count() {
        CriteriaQuery<Long> cq = em().getCriteriaBuilder()
                .createQuery(Long.class);
        Root<T> rt = cq.from(entityClass);
        cq.select(em().getCriteriaBuilder().count(rt));
        Query q = em().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }
}
