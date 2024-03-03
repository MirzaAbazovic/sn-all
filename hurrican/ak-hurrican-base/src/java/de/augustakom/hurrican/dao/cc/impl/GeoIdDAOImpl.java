package de.augustakom.hurrican.dao.cc.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.cc.GeoIdDAO;
import de.augustakom.hurrican.model.cc.GeoIdLocation;

public class GeoIdDAOImpl extends Hibernate4DAOImpl implements GeoIdDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public <T extends GeoIdLocation> T save(T location) {
        return super.store(location);
    }

    @Override
    public <T extends GeoIdLocation> T findLocation(Class<T> clazz, long id) {
        return super.findById(id, clazz);
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
