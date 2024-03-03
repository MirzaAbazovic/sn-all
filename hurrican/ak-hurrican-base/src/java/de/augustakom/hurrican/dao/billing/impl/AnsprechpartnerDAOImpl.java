/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.06.2004 11:09:09
 */
package de.augustakom.hurrican.dao.billing.impl;

import java.io.*;
import java.sql.*;
import java.util.*;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.orm.hibernate4.SessionFactoryUtils;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.dao.jdbc.ResultSetHelper;
import de.augustakom.hurrican.dao.billing.AnsprechpartnerDAO;
import de.augustakom.hurrican.model.billing.Ansprechpartner;


/**
 * Hibernate DAO-Implementierung zur Verwaltung von Objekten des Typs <code>Ansprechpartner</code>
 *
 *
 */
public class AnsprechpartnerDAOImpl extends Hibernate4DAOImpl implements AnsprechpartnerDAO, InitializingBean {

    @Autowired
    @Qualifier("billing.sessionFactory")
    protected SessionFactory sessionFactory;

    private AnsprechpartnerDAOImplJdbc daoJdbc = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        daoJdbc = new AnsprechpartnerDAOImplJdbc();
        daoJdbc.setDataSource(SessionFactoryUtils.getDataSource(getSessionFactory()));
    }

    /**
     * @see de.augustakom.common.tools.dao.hibernate.Hibernate4FindDAOImpl#findById(java.io.Serializable,
     * java.lang.Class)
     */
    @Override
    public Object findById(Serializable id, Class type) {
        return daoJdbc.findById(id, type);
    }

    /**
     * @see de.augustakom.hurrican.dao.billing.AnsprechpartnerDAO#findByKundenNo(java.lang.Integer)
     */
    public List<Ansprechpartner> findByKundenNo(Long kundeNo) {
        if (kundeNo == null) {
            return null;
        }

        return daoJdbc.findByKundenNo(kundeNo);
    }

    /**
     * @see de.augustakom.hurrican.dao.billing.AnprechpartnerDAO#findByKundeNos(java.util.List)
     */
    public List<Ansprechpartner> findByKundeNos(List<Long> kundeNos) {
        List<Ansprechpartner> result = new ArrayList<Ansprechpartner>();
        for (Long kNo : kundeNos) {
            result.addAll(findByKundenNo(kNo));
        }

        return result;
    }

    /* JDBC DAO-Implementierung */
    static class AnsprechpartnerDAOImplJdbc extends JdbcDaoSupport {

        /**
         * @see de.augustakom.common.tools.dao.hibernate.Hibernate4FindDAOImpl#findById(java.io.Serializable,
         * java.lang.Class)
         */
        public Ansprechpartner findById(Serializable id, Class<Ansprechpartner> type) {
            StringBuilder sql = new StringBuilder("select p2c.CUST_NO, p.PERSON_NO, p.TITLE, p.NAME, p.FIRSTNAME, ");
            sql.append("p.PHONE_BUSINESS, p.FAX, p.MOBILE, p.EMAIL, p.NOTES, type.NAME as TYPENAME, adr.STREET, ");
            sql.append("adr.HOUSE_NUM || adr.HOUSE_NUM_ADD as HNUM, adr.PO_BOX, adr.ZIP_CODE, adr.CITY ");
            sql.append("from PERSON2CUSTOMER p2c ");
            sql.append("INNER JOIN PERSON p on p2c.PERSON_NO=p.PERSON_NO ");
            sql.append("LEFT JOIN ADDRESS adr on p.COMPANY_ADDR_NO=adr.ADDR_NO ");
            sql.append("LEFT JOIN P2C_TYPE type on p2c.TYPE_NO=type.TYPE_NO ");
            sql.append("where p2c.PERSON_NO in (?)");

            List<Ansprechpartner> result = getJdbcTemplate().query(
                    sql.toString(), new Object[] { id }, new AnsprechpartnerRowMapper());

            if (result != null) {
                if (result.size() == 1) {
                    return result.get(0);
                }
                else if (result.size() > 1) {
                    throw new IncorrectResultSizeDataAccessException(
                            "Zu viele Ansprechpartner zur ID gefunden!", 1, result.size());
                }
            }

            return null;
        }


        /**
         * @see de.augustakom.hurrican.dao.billing.AnsprechpartnerDAO#findByKundenNo(java.lang.Integer)
         */
        public List<Ansprechpartner> findByKundenNo(Long kundeNo) {
            StringBuilder sql = new StringBuilder("select p2c.CUST_NO, p.PERSON_NO, p.TITLE, p.NAME, p.FIRSTNAME, ");
            sql.append("p.PHONE_BUSINESS, p.FAX, p.MOBILE, p.EMAIL, p.NOTES, type.NAME as TYPENAME, adr.STREET, ");
            sql.append("adr.HOUSE_NUM || adr.HOUSE_NUM_ADD as HNUM, adr.PO_BOX, adr.ZIP_CODE, adr.CITY ");
            sql.append("from PERSON2CUSTOMER p2c ");
            sql.append("INNER JOIN PERSON p on p2c.PERSON_NO=p.PERSON_NO ");
            sql.append("LEFT JOIN ADDRESS adr on p.COMPANY_ADDR_NO=adr.ADDR_NO ");
            sql.append("LEFT JOIN P2C_TYPE type on p2c.TYPE_NO=type.TYPE_NO ");
            sql.append("where p2c.CUST_NO in (?)");

            return getJdbcTemplate().query(sql.toString(), new Object[] { kundeNo }, new AnsprechpartnerRowMapper());
        }
    }

    /* Row-Mapper fuer Ansprechpartner-Objekte. */
    static class AnsprechpartnerRowMapper implements RowMapper<Ansprechpartner> {
        /**
         * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
         */
        public Ansprechpartner mapRow(ResultSet rs, int row) throws SQLException {
            Ansprechpartner ansp = new Ansprechpartner();
            ansp.setAnsprechpartnerNo(ResultSetHelper.getLongSilent(rs, "PERSON_NO"));
            ansp.setKundeNo(ResultSetHelper.getLongSilent(rs, "CUST_NO"));
            ansp.setTyp(ResultSetHelper.getStringSilent(rs, "TYPENAME"));
            ansp.setTitel(ResultSetHelper.getStringSilent(rs, "TITLE"));
            ansp.setName(ResultSetHelper.getStringSilent(rs, "NAME"));
            ansp.setVorname(ResultSetHelper.getStringSilent(rs, "FIRSTNAME"));
            ansp.setStrasse(ResultSetHelper.getStringSilent(rs, "STREET"));
            ansp.setNummer(ResultSetHelper.getStringSilent(rs, "HNUM"));
            ansp.setPostfach(ResultSetHelper.getStringSilent(rs, "PO_BOX"));
            ansp.setPlz(ResultSetHelper.getStringSilent(rs, "ZIP_CODE"));
            ansp.setOrt(ResultSetHelper.getStringSilent(rs, "CITY"));
            ansp.setRufnummer(ResultSetHelper.getStringSilent(rs, "PHONE_BUSINESS"));
            ansp.setFax(ResultSetHelper.getStringSilent(rs, "FAX"));
            ansp.setRnMobile(ResultSetHelper.getStringSilent(rs, "MOBILE"));
            ansp.setEmail(ResultSetHelper.getStringSilent(rs, "EMAIL"));
            ansp.setInfo(ResultSetHelper.getStringSilent(rs, "NOTES"));
            return ansp;
        }
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


