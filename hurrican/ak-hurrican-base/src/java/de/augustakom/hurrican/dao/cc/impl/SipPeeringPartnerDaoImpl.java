package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import java.util.stream.*;
import javax.annotation.*;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.dao.cc.SipPeeringPartnerDao;
import de.augustakom.hurrican.model.cc.sip.SipPeeringPartner;

/**
 * SipPeeringPartnerDaoImpl
 */
public class SipPeeringPartnerDaoImpl extends Hibernate4DAOImpl implements SipPeeringPartnerDao {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    private int compareSipPeeringPartner(SipPeeringPartner left, SipPeeringPartner right) {
        return StringTools.compare((left != null && left.getName() != null) ? left.getName().toLowerCase() : null,
                (right != null && right.getName() != null) ? right.getName().toLowerCase() : null, false);
    }

    @Override
    public List<SipPeeringPartner> findAllPeeringPartner(Boolean activeOnly) {
        SipPeeringPartner example = new SipPeeringPartner();
        example.setIsActive(activeOnly);
        List<SipPeeringPartner> result = queryByExample(example, SipPeeringPartner.class);
        return result.stream().sorted(this::compareSipPeeringPartner).collect(Collectors.toList());
    }

    @Override
    @Nullable
    public SipPeeringPartner findPeeringPartnerById(Long id) {
        return findById(id, SipPeeringPartner.class);
    }

    @Override
    @Nullable
    public SipPeeringPartner findPeeringPartnerByName(String name) {
        SipPeeringPartner example = new SipPeeringPartner();
        example.setName(name);
        List<SipPeeringPartner> queryResult = queryByExample(example, SipPeeringPartner.class);
        return (queryResult != null && queryResult.size() == 1)? queryResult.get(0) : null;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

}
