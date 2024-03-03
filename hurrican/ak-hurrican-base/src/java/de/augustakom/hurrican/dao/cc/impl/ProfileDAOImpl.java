package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.cc.ProfileDAO;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.ProfileAuftrag;
import de.augustakom.hurrican.model.cc.ProfileParameter;
import de.augustakom.hurrican.model.cc.ProfileParameterMapper;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;


public class ProfileDAOImpl extends Hibernate4DAOImpl implements ProfileDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    /**
     * @see ProfileDAO#findProfileParameters(Long)
     */
    @Override
    public List<ProfileParameter> findProfileParameters(Long baugruppenTypId) {
        final Session session = sessionFactory.getCurrentSession();
        final List<ProfileParameter> defaults = session
                .createQuery("select p from ProfileParameter p where p.baugruppenTyp.id = :baugruppenTypId")
                .setParameter("baugruppenTypId", baugruppenTypId)
                .list();
        return defaults != null ? defaults : Collections.emptyList();
    }

    @Override
    public List<ProfileParameter> findProfileParameterDefaults(Long baugruppenTypId) {
        return sessionFactory
                .getCurrentSession()
                .createQuery("select p from ProfileParameter p where p.baugruppenTyp.id = :baugruppenTypId and p.default = true ")
                .setParameter("baugruppenTypId", baugruppenTypId)
                .list();
    }

    @Override
    public List<String> findParameterNamesOfBaugruppenTyp(HWBaugruppenTyp baugruppenTyp){
        final Session session = sessionFactory.getCurrentSession();
        final List<String> parameterNames = session
                .createQuery("select distinct p.parameterName from ProfileParameter p where p.baugruppenTyp.id = :baugruppenTypId")
                .setParameter("baugruppenTypId", baugruppenTyp.getId())
                .list();
        return parameterNames != null ? parameterNames : Collections.emptyList();
    }

    /**
     * @see ProfileDAO#findProfileAuftrag(Long, Date)
     */
    @Override
    public Optional<ProfileAuftrag> findProfileAuftrag(Long auftragId, Date date) {
        final Session session = sessionFactory.getCurrentSession();
        final List<ProfileAuftrag> paList = session
                .createQuery("select p from ProfileAuftrag p "
                        + " where p.auftragId = :auftragId and p.gueltigVon <= :dt and p.gueltigBis > :dt ")
                .setParameter("auftragId", auftragId)
                .setParameter("dt", date)
                .list();
        if (CollectionUtils.isNotEmpty(paList)) {
            return Optional.of(paList.get(0));
        }
        return Optional.empty();
    }

    /**
     * @see ProfileDAO#findParameterMapper(String)
     */
    @Override
    public ProfileParameterMapper findParameterMapper(String parameterName) {
        final Session session = sessionFactory.getCurrentSession();
        return (ProfileParameterMapper) session
                .createQuery("select p from ProfileParameterMapper p where p.parameterName = :parameterName")
                .setParameter("parameterName", parameterName)
                .uniqueResult();
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @Override
    public List<String> findParameterValuesByName(String name) {
        final Session session = sessionFactory.getCurrentSession();
        final List<String> lineSpectrumList = session
                .createQuery("select distinct p.parameterValue from ProfileParameter p where p.parameterName = :parameterName order by p.parameterValue ASC")
                .setParameter("parameterName", name)
                .list();
        return lineSpectrumList;
    }

    @Override
    public String findDefaultParameterValueByName(String name){
        final Session session = sessionFactory.getCurrentSession();
        String defaultValueOfParameter = (String) session
                .createQuery("select distinct p.parameterValue from ProfileParameter p where p.parameterName = :parameterName and p.default = true")
                .setParameter("parameterName", name)
                .uniqueResult();
        return defaultValueOfParameter;
    }

    @Override
    public List<ProfileParameter> findParametersWithoutDefault(Long baugruppenTypId) {
        final Session session = sessionFactory.getCurrentSession();
        Query query = session.createSQLQuery("SELECT {p.*} FROM T_PROFILE_PARAMETER p WHERE HW_BAUGRUPPEN_TYP_ID = :baugruppenTypId AND PARAMETER_NAME IN ( " +
                "SELECT PARAMETER_NAME FROM ( "
                + "SELECT PARAMETER_NAME, MAX(IS_DEFAULT) AS IS_DEFAULT FROM T_PROFILE_PARAMETER WHERE HW_BAUGRUPPEN_TYP_ID = :baugruppenTypId GROUP BY PARAMETER_NAME) WHERE IS_DEFAULT = 0)")
                .addEntity("p", ProfileParameter.class);
        query.setParameter("baugruppenTypId", baugruppenTypId);
        return query.list();
    }

    @Override
    public List<HWBaugruppe> findHWBaugruppenByAuftragId(final Long auftragId) {
        final String sql = "select {bg.*} "
                + " from T_AUFTRAG_TECHNIK atch "
                + " inner join T_ENDSTELLE es on es.ES_GRUPPE=atch.AT_2_ES_ID "
                + " inner join T_RANGIERUNG r on r.RANGIER_ID=es.RANGIER_ID or r.RANGIER_ID=es.RANGIER_ID_ADDITIONAL "
                + " inner join T_EQUIPMENT eq on r.EQ_IN_ID = eq.EQ_ID or r.EQ_OUT_ID = eq.EQ_ID "
                + " inner join T_HW_BAUGRUPPE bg on bg.id = eq.HW_BAUGRUPPEN_ID "
                + " where (atch.GUELTIG_VON <= :now and atch.GUELTIG_BIS > :now) "
                + " and (eq.GUELTIG_VON <= :now and eq.GUELTIG_BIS > :now) "
                + " and es.es_typ = 'B' "
                + " and atch.auftrag_id = :auftragId";
        final Session session = sessionFactory.getCurrentSession();
        final Query query = session.createSQLQuery(sql).addEntity("bg", HWBaugruppe.class);
        query.setLong("auftragId", auftragId);
        query.setTimestamp("now", new Date());
        @SuppressWarnings("unchecked")
        final List<HWBaugruppe> result = query.list();
        return result;
    }

    @Override
    public Equipment findEquipmentsByAuftragId(final Long auftragId) {
        final String sql = "select {eq.*} "
                + " from T_AUFTRAG_TECHNIK atch "
                + " inner join T_ENDSTELLE es on es.ES_GRUPPE=atch.AT_2_ES_ID "
                + " inner join T_RANGIERUNG r on r.RANGIER_ID=es.RANGIER_ID "
                + " inner join T_EQUIPMENT eq on r.EQ_IN_ID = eq.EQ_ID "
                + " where (atch.GUELTIG_VON <= :now and atch.GUELTIG_BIS > :now) "
                + " and (eq.GUELTIG_VON <= :now and eq.GUELTIG_BIS > :now) "
                + " and (r.GUELTIG_VON <= :now and r.GUELTIG_BIS > :now)"
                + " and es.es_typ = 'B' "
                + " and atch.auftrag_id = :auftragId";
        final Session session = sessionFactory.getCurrentSession();
        final Query query = session.createSQLQuery(sql).addEntity("eq", Equipment.class);
        query.setLong("auftragId", auftragId);
        query.setTimestamp("now", new Date());
        return (Equipment) query.uniqueResult();
    }
}
