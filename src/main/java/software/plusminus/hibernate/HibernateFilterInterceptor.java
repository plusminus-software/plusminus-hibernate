package software.plusminus.hibernate;

import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("PMD.CloseResource")
@ConditionalOnBean(HibernateFilter.class)
@Component
public class HibernateFilterInterceptor implements HandlerInterceptor, WebMvcConfigurer {

    private  static final String ATTRIBUTE_NAME = "hibernateSession";
    
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private List<HibernateFilter> filters;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this).order(Ordered.LOWEST_PRECEDENCE);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Session session = entityManager.unwrap(Session.class);
        filters.forEach(f -> {
            Filter filter = session.enableFilter(f.filterName());
            f.parameters().forEach(filter::setParameter);
        });
        request.setAttribute(ATTRIBUTE_NAME, session);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) {
        Session session = (Session) request.getAttribute(ATTRIBUTE_NAME);
        if (session != null) {
            filters.forEach(f -> session.disableFilter(f.filterName()));
            request.removeAttribute(ATTRIBUTE_NAME);
        }
    }
}