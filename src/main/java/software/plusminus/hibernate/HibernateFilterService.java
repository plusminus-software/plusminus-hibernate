package software.plusminus.hibernate;

import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
public class HibernateFilterService {
    
    @Autowired
    private List<HibernateFilter> filters;
    @PersistenceContext
    private EntityManager entityManager;
    
    public void enableFilters() {
        enableFilters(getSession());
    }
    
    public void enableFilters(Session session) {
        filters.forEach(f -> {
            Filter filter = session.enableFilter(f.filterName());
            f.parameters().forEach(filter::setParameter);
        });
    }
    
    public void disableFilters() {
        disableFilters(getSession());
    }
    
    public void disableFilters(Session session) {
        filters.forEach(f -> session.disableFilter(f.filterName()));
    }
    
    private Session getSession() {
        return entityManager.unwrap(Session.class);
    }
    
}
