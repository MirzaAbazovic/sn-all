/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.07.2007 09:47:33
 */
package de.augustakom.hurrican.dao.exmodules.tal.impl;

import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.orm.hibernate4.SessionFactoryUtils;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.exmodules.tal.TALBestellungDAO;
import de.augustakom.hurrican.model.exmodules.tal.TALBestellung;
import de.augustakom.hurrican.model.exmodules.tal.TALSegment;
import de.augustakom.hurrican.model.exmodules.tal.TALVorfall;


/**
 * Hibernate DAO-Implementierung von <code>TALBestellungDAO</code>
 *
 *
 */
public class TALBestellungDAOImpl extends Hibernate4DAOImpl implements TALBestellungDAO, InitializingBean {

    @Autowired
    @Qualifier("tal.sessionFactory")
    protected SessionFactory sessionFactory;

    private TALBestellungDAOImplJdbc daoJdbc = null;

    private String segmentName = null;

    private List<String> b001 = null;
    private List<String> b002 = null;
    private List<String> b003 = null;
    private List<String> b004 = null;
    private List<String> b005 = null;
    private List<String> b006 = null;
    private List<String> b007 = null;
    private List<String> b008 = null;
    private List<String> b009 = null;
    private List<String> b010 = null;
    private List<String> b011 = null;
    private List<String> b012 = null;
    private List<String> b013 = null;
    private List<String> b014 = null;
    private List<String> b015 = null;
    private List<String> b016 = null;
    private List<String> b017 = null;
    private List<String> b018 = null;
    private List<String> b019 = null;
    private List<String> b020 = null;
    private List<String> b021 = null;
    private List<String> b022 = null;
    private List<String> b023 = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        daoJdbc = new TALBestellungDAOImplJdbc();
        daoJdbc.setDataSource(SessionFactoryUtils.getDataSource(getSessionFactory()));
    }

    @Override
    public List<TALBestellung> findTALBestellungenByFirstId(final Long tbsFirstId) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(TALBestellung.class);
        criteria.add(Restrictions.eq("firstId", tbsFirstId));
        criteria.addOrder(Order.asc("id"));

        return criteria.list();
    }

    @Override
    public List<TALVorfall> findTALVorfaelleByIds(final List<Long> ids) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(TALVorfall.class);
        criteria.add(Restrictions.in("id", ids));
        criteria.addOrder(Order.asc("id"));

        return criteria.list();
    }

    @Override
    public List<TALSegment> findTALSegment(String talIdentifier, Number tbsId) {
        if (StringUtils.isBlank(talIdentifier)) {
            throw new DataIntegrityViolationException("Segment-Name ist nicht definiert!");
        }

        List<TALSegment> talSegment = daoJdbc.findTALSegment(talIdentifier, tbsId);
        return talSegment;
    }

    /* JDBC-Implementierung */
    class TALBestellungDAOImplJdbc extends JdbcDaoSupport {

        /**
         * @see de.augustakom.hurrican.dao.exmodules.tal.TALBestellungDAO#stornoTALBestellung(java.lang.Long)
         */
        public void stornoTALBestellung(Long tbsId) {
            getJdbcTemplate().update(
                    "insert into VINS_TTALBESTELLUNG_SNDSTO (TBS_ID) values (?)",
                    new Object[] { tbsId });
        }

        /**
         * Die Methode speichert die einzelnen TAL-Segmente in der jeweiligen Segment-Tabelle. Die Unterscheidung wird
         * anhand des Segment-Namens getroffen. <br> Wichtig: aktuell sind nur Insert-Operationen moeglich!
         *
         * @see de.augustakom.hurrican.dao.exmodules.tal.TALBestellungDAO#saveTALSegment(de.augustakom.hurrican.model.exmodules.tal.TALSegment)
         */
        public void saveTALSegment(TALSegment toSave) {
            String segName = toSave.getSegmentName();
            List<Object> values = toSave.getValues();
            int count = 1;
            List<Object> params = new ArrayList<Object>();

            if (toSave.getId() == null) {
                // Insert
                params.add(toSave.getTalBestellungId());
                StringBuilder insSql = new StringBuilder("insert into TTALBESTELLUNG_").append(segName);
                insSql.append(" (").append(segName).append("_TBS_ID, ");
                for (Object obj : values) {
                    if (count > 1) { insSql.append(", "); }
                    count++;
                    insSql.append(segName).append("_").append(count);
                    params.add(obj);
                }
                insSql.append(") values (");
                for (int i = 0; i < params.size(); i++) {
                    if (i > 0) { insSql.append(","); }
                    insSql.append("?");
                }
                insSql.append(")");

                getJdbcTemplate().update(insSql.toString(), params.toArray());
            }
            else {
                // Update
                throw new InvalidDataAccessResourceUsageException(
                        "Update von TALSegment-Objekten nicht implementiert!");
            }
        }

        /**
         * @see de.augustakom.hurrican.dao.exmodules.tal.TALBestellungDAO#findTALSegment(String identifier, Number
         * tbsId)
         */
        public List<TALSegment> findTALSegment(String identifier, Number tbsId) {
            segmentName = identifier;

            final String TTALBESTELLUNG = "TTALBESTELLUNG_";

            if (StringUtils.isBlank(identifier)) {
                throw new DataIntegrityViolationException("Segment-Name ist nicht definiert!");
            }

            StringBuilder selectBuffer = new StringBuilder();
            selectBuffer.append("SELECT * FROM ");
            selectBuffer.append(TTALBESTELLUNG);
            selectBuffer.append(identifier);
            selectBuffer.append(" WHERE ");
            selectBuffer.append(TTALBESTELLUNG);
            selectBuffer.append(identifier);
            selectBuffer.append(".");
            selectBuffer.append(identifier);
            selectBuffer.append("_tbs_id = ?");

            List<TALSegment> result = getJdbcTemplate().query(selectBuffer.toString(), new Object[] { tbsId }, new TALSegmentMapper());
            return result;
        }
    }

    /**
     * Klasse für das Mapping eines TAL-Segments
     *
     *
     */
    private final class TALSegmentMapper implements RowMapper<TALSegment> {

        /**
         * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
         */
        @Override
        public TALSegment mapRow(ResultSet rs, int rowNum) throws SQLException {
            TALSegment talSegment = new TALSegment();
            talSegment.setSegmentName(segmentName);

            try {
                Method method = this.getClass().getDeclaringClass().getMethod("get" + segmentName, (Class<?>[]) null);
                if (null != method) {
                    // Felder des entsprechenden TAL-Segments aus Konfiguration ermitteln
                    List<String> fieldDefinition = (List<String>) method.invoke(get(), new Object[] { });
                    if (CollectionTools.isNotEmpty(fieldDefinition)) {
                        for (String fieldDef : fieldDefinition) {
                            if (StringUtils.isNotBlank(fieldDef)) {
                                talSegment.addValue(rs.getObject(getFieldName(talSegment, fieldDef).toString()));
                            }
                        }
                    }
                }
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
            return talSegment;
        }

        /*
         * Liefert den Feldnamen, Name des Segments_n zurück, z.B. B001_4
         */
        private StringBuilder getFieldName(TALSegment talSegment, String fieldDef) {
            StringBuilder fieldBuffer = new StringBuilder();
            fieldBuffer.append(talSegment.getSegmentName());
            fieldBuffer.append("_");
            fieldBuffer.append(fieldDef);
            return fieldBuffer;
        }
    }

    /**
     * @return the b001
     */
    public List<String> getB001() {
        return b001;
    }

    /**
     * @param b001 the b001 to set
     */
    public void setB001(List<String> b001) {
        this.b001 = b001;
    }

    /**
     * @return the b002
     */
    public List<String> getB002() {
        return b002;
    }

    /**
     * @param b002 the b002 to set
     */
    public void setB002(List<String> b002) {
        this.b002 = b002;
    }

    /**
     * @return the b003
     */
    public List<String> getB003() {
        return b003;
    }

    /**
     * @param b003 the b003 to set
     */
    public void setB003(List<String> b003) {
        this.b003 = b003;
    }

    /**
     * @return the b004
     */
    public List<String> getB004() {
        return b004;
    }

    /**
     * @param b004 the b004 to set
     */
    public void setB004(List<String> b004) {
        this.b004 = b004;
    }

    /**
     * @return the b005
     */
    public List<String> getB005() {
        return b005;
    }

    /**
     * @param b005 the b005 to set
     */
    public void setB005(List<String> b005) {
        this.b005 = b005;
    }

    /**
     * @return the b006
     */
    public List<String> getB006() {
        return b006;
    }

    /**
     * @param b006 the b006 to set
     */
    public void setB006(List<String> b006) {
        this.b006 = b006;
    }

    /**
     * @return the b007
     */
    public List<String> getB007() {
        return b007;
    }

    /**
     * @param b007 the b007 to set
     */
    public void setB007(List<String> b007) {
        this.b007 = b007;
    }

    /**
     * @return the b008
     */
    public List<String> getB008() {
        return b008;
    }

    /**
     * @param b008 the b008 to set
     */
    public void setB008(List<String> b008) {
        this.b008 = b008;
    }

    /**
     * @return the b009
     */
    public List<String> getB009() {
        return b009;
    }

    /**
     * @param b009 the b009 to set
     */
    public void setB009(List<String> b009) {
        this.b009 = b009;
    }

    /**
     * @return the b010
     */
    public List<String> getB010() {
        return b010;
    }

    /**
     * @param b010 the b010 to set
     */
    public void setB010(List<String> b010) {
        this.b010 = b010;
    }

    /**
     * @return the b011
     */
    public List<String> getB011() {
        return b011;
    }

    /**
     * @param b011 the b011 to set
     */
    public void setB011(List<String> b011) {
        this.b011 = b011;
    }

    /**
     * @return the b012
     */
    public List<String> getB012() {
        return b012;
    }

    /**
     * @param b012 the b012 to set
     */
    public void setB012(List<String> b012) {
        this.b012 = b012;
    }

    /**
     * @return the b013
     */
    public List<String> getB013() {
        return b013;
    }

    /**
     * @param b013 the b013 to set
     */
    public void setB013(List<String> b013) {
        this.b013 = b013;
    }

    /**
     * @return the b014
     */
    public List<String> getB014() {
        return b014;
    }

    /**
     * @param b014 the b014 to set
     */
    public void setB014(List<String> b014) {
        this.b014 = b014;
    }

    /**
     * @return the b015
     */
    public List<String> getB015() {
        return b015;
    }

    /**
     * @param b015 the b015 to set
     */
    public void setB015(List<String> b015) {
        this.b015 = b015;
    }

    /**
     * @return the b016
     */
    public List<String> getB016() {
        return b016;
    }

    /**
     * @param b016 the b016 to set
     */
    public void setB016(List<String> b016) {
        this.b016 = b016;
    }

    /**
     * @return the b017
     */
    public List<String> getB017() {
        return b017;
    }

    /**
     * @param b017 the b017 to set
     */
    public void setB017(List<String> b017) {
        this.b017 = b017;
    }

    /**
     * @return the b018
     */
    public List<String> getB018() {
        return b018;
    }

    /**
     * @param b018 the b018 to set
     */
    public void setB018(List<String> b018) {
        this.b018 = b018;
    }

    /**
     * @return the b019
     */
    public List<String> getB019() {
        return b019;
    }

    /**
     * @param b019 the b019 to set
     */
    public void setB019(List<String> b019) {
        this.b019 = b019;
    }

    /**
     * @return the b020
     */
    public List<String> getB020() {
        return b020;
    }

    /**
     * @param b020 the b020 to set
     */
    public void setB020(List<String> b020) {
        this.b020 = b020;
    }

    /**
     * @return the b021
     */
    public List<String> getB021() {
        return b021;
    }

    /**
     * @param b021 the b021 to set
     */
    public void setB021(List<String> b021) {
        this.b021 = b021;
    }

    /**
     * @return the b022
     */
    public List<String> getB022() {
        return b022;
    }

    /**
     * @param b022 the b022 to set
     */
    public void setB022(List<String> b022) {
        this.b022 = b022;
    }

    /**
     * @return the b023
     */
    public List<String> getB023() {
        return b023;
    }

    /**
     * @param b023 the b023 to set
     */
    public void setB023(List<String> b023) {
        this.b023 = b023;
    }

    /**
     * @return
     */
    protected TALBestellungDAOImpl get() {
        return this;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


