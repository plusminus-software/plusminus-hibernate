package software.plusminus.hibernate;

import java.util.Collections;
import java.util.Map;

public interface HibernateFilter {

    String filterName();

    default Map<String, Object> parameters() {
        return Collections.emptyMap();
    }

}
