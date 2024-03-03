/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.08.2004 15:48:25
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.io.*;
import java.math.*;
import java.util.*;
import org.hibernate.NonUniqueResultException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.DateType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.ObjectTools;
import de.augustakom.hurrican.dao.cc.CarrierbestellungDAO;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.CarrierVaModus;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Carrierbestellung2Endstelle;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.view.AQSView;
import de.augustakom.hurrican.model.cc.view.CuDAVorschau;
import de.mnet.wbci.model.CarrierCode;

/**
 * Hibernate DAO-Implementierung von <code>CarrierbestellungDAO</code>
 * 
 *
 */
public class CarrierbestellungDAOImpl extends Hibernate4DAOImpl implements CarrierbestellungDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public List<AQSView> findAqsLL4GeoId(Long geoId) {
        StringBuilder hql = new StringBuilder();
        hql.append("select new ").append(AQSView.class.getName()).append("(e.endstelle, c.aqs, c.ll, at.auftragId) from ");
        hql.append(Endstelle.class.getName()).append(" e, ");
        hql.append(AuftragTechnik.class.getName()).append(" at, ");
        hql.append(Carrierbestellung.class.getName()).append(" c ");
        hql.append("where c.cb2EsId=e.cb2EsId");
        hql.append(" and e.cb2EsId is not null");
        hql.append(" and at.auftragTechnik2EndstelleId=e.endstelleGruppeId");
        hql.append(" and e.geoId=? ");

        @SuppressWarnings("unchecked")
        List<AQSView> queryResult = find(hql.toString(), geoId);
        return queryResult;
    }

    @Override
    public Long createNewMappingId() {
        Carrierbestellung2Endstelle c2e = new Carrierbestellung2Endstelle();
        sessionFactory.getCurrentSession().save(c2e);
        return c2e.getId();
    }

    @Override
    public List<Carrierbestellung> findByEndstelle(Long esId) {
        StringBuilder hql = new StringBuilder();
        hql.append("select c.id from ");
        hql.append(Carrierbestellung.class.getName()).append(" c, ");
        hql.append(Endstelle.class.getName()).append(" e ");
        hql.append("where e.id=? and e.cb2EsId=c.cb2EsId ");
        hql.append("order by c.id desc");

        @SuppressWarnings("unchecked")
        List<Long> result = find(hql.toString(), esId);
        if (result != null) {
            List<Carrierbestellung> retVal = new ArrayList<>();
            for (Long id : result) {
                Carrierbestellung cb = (Carrierbestellung) sessionFactory.getCurrentSession().get(Carrierbestellung.class, id);
                retVal.add(cb);
            }

            return retVal;
        }

        return null;
    }

    @Override
    public Carrierbestellung findLastByEndstelle(final Long esId) {
        final StringBuilder hql = new StringBuilder();
        hql.append("select c.id from ");
        hql.append(Carrierbestellung.class.getName()).append(" c, ");
        hql.append(Endstelle.class.getName()).append(" e ");
        hql.append("where e.id= :eId and e.cb2EsId=c.cb2EsId ");
        hql.append("order by c.id desc");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setLong("eId", esId);
        query.setMaxResults(1);

      @SuppressWarnings("unchecked")
        List<Long> result = query.list();
        Long cbId = ((result != null) && (result.size() == 1)) ? result.get(0) : null;
        if (cbId != null) {
            return findById(cbId, Carrierbestellung.class);
        }

        return null;
    }

    @Override
    public List<Carrierbestellung> findByVertragsnummer(String vertragsnummer) {
        StringBuilder hql = new StringBuilder("from ");
        hql.append(Carrierbestellung.class.getName());
        hql.append(" c where c.vtrNr=?");

        @SuppressWarnings("unchecked")
        List<Carrierbestellung> result = find(hql.toString(), vertragsnummer);
        return result;
    }

    @Override
    public List<Carrier> findCarrier() {
        StringBuilder hql = new StringBuilder("from ");
        hql.append(Carrier.class.getName());
        hql.append(" c order by c.orderNo, c.name");

        @SuppressWarnings("unchecked")
        List<Carrier> result = find(hql.toString());
        return result;
    }

    @Override
    public List<Carrier> findCarrierByVaModus(CarrierVaModus modus) {
        StringBuilder hql = new StringBuilder("from ");
        hql.append(Carrier.class.getName());
        hql.append(" c where c.vorabstimmungsModus=? order by c.orderNo, c.name");

        @SuppressWarnings("unchecked")
        List<Carrier> result = find(hql.toString(), modus);
        return result;
    }

    @Override
    public List<Carrier> findCarrierForAnbieterwechsel() {
        StringBuilder hql = new StringBuilder("from ");
        hql.append(Carrier.class.getName());
        hql.append(" c where portierungskennung is not null order by c.orderNo, c.name");

        @SuppressWarnings("unchecked")
        List<Carrier> result = find(hql.toString());
        return result;
    }

    @Override
    public List<Carrier> findWbciAwareCarrier() {
        final StringBuilder hql = new StringBuilder("from ");
        hql.append(Carrier.class.getName());
        hql.append(" c where portierungskennung is not null");
        hql.append(" and c.vorabstimmungsModus in (:vorabstimmungsModus) ");
        hql.append(" order by c.orderNo, c.name");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setParameterList("vorabstimmungsModus", Arrays.asList(CarrierVaModus.WBCI, CarrierVaModus.PORTAL));

        @SuppressWarnings("unchecked")
        List<Carrier> result = query.list();
        return result;
    }

    @Override
    public Carrier findCarrierByCarrierCode(final CarrierCode carrierCode) {
        final StringBuilder hql = new StringBuilder("from ");
        hql.append(Carrier.class.getName());
        hql.append(" c where ituCarrierCode is not null");
        hql.append(" and c.ituCarrierCode = (:ituCarrierCode) ");
        hql.append(" order by c.orderNo, c.name");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setParameter("ituCarrierCode", carrierCode.getITUCarrierCode());

        Carrier carrier = (Carrier) query.uniqueResult();

        if (carrier == null) {
            throw new EmptyResultDataAccessException(String.format(
                    "Es wurde kein Carrier mit ITU-CarrierCode '%s' gefunden", carrierCode), 1);
        }
        return carrier;
    }

    @Override
    public Carrier findCarrierByPortierungskennung(final String portierungskennung) {
        final StringBuilder hql = new StringBuilder("from ")
                .append(Carrier.class.getName())
                .append(" c where c.portierungskennung = (:portierungskennung) ");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setParameter("portierungskennung", portierungskennung);

        Carrier carrier = (Carrier) query.uniqueResult();

        if (carrier == null) {
            throw new EmptyResultDataAccessException(
                    String.format("Es wurde kein Carrier mit Portierungskennung '%s' gefunden",
                            portierungskennung), 1);
        }

        return carrier;
    }

    @Override
    public Carrier find4HVT(Long hvtId) {
        StringBuilder hql = new StringBuilder("select h.carrierId from ");
        hql.append(HVTStandort.class.getName()).append(" h where h.id=?");

        @SuppressWarnings("unchecked")
        List<Long> result = find(hql.toString(), hvtId);
        if ((result != null) && (result.size() == 1) && (result.get(0) != null)) {
            return findById(result.get(0), Carrier.class);
        }
        return null;
    }

    @Override
    public List<CuDAVorschau> createCuDAVorschau(Date vorgabeSCV) {
        StringBuilder sql = new StringBuilder()
                .append("select ad.VORGABE_SCV, c.TEXT, count(ad.VORGABE_SCV) as ANZAHL_CUDA ")
                .append("from T_AUFTRAG_DATEN ad ")
                .append("inner join T_AUFTRAG a on ad.AUFTRAG_ID=a.ID ")
                .append("inner join T_AUFTRAG_TECHNIK at on at.AUFTRAG_ID=a.ID ")
                .append("inner join T_AUFTRAG_TECHNIK_2_ENDSTELLE a2e on at.AT_2_ES_ID=a2e.ID ")
                .append("inner join T_ENDSTELLE e on a2e.ID=e.ES_GRUPPE ")
                .append("inner join T_CB_2_ES c2e on e.CB_2_ES_ID=c2e.ID ")
                .append("inner join T_CARRIERBESTELLUNG cb on c2e.ID=cb.CB_2_ES_ID ")
                .append("inner join T_CARRIER c on cb.CARRIER_id=c.ID ")
                .append("where cb.BESTELLT_AM is not null ")
                .append("       and ad.GUELTIG_VON<=? and ad.GUELTIG_BIS>? ")
                .append("       and at.GUELTIG_VON<=? and at.GUELTIG_BIS>? ")
                .append("       and ad.STATUS_ID not in (?,?,?,?) ")
                .append("group by ad.VORGABE_SCV, ad.KUENDIGUNG, c.TEXT ")
                .append("having (ad.VORGABE_SCV is not null and ad.VORGABE_SCV>?) ")
                .append("       and c.TEXT=? and ad.KUENDIGUNG is null ")
                .append("order by ad.VORGABE_SCV");

        Date now = DateTools.getActualSQLDate();
        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameters(
                new Object[] { now, now, now, now, AuftragStatus.ABSAGE, AuftragStatus.STORNO,
                        AuftragStatus.AUFTRAG_GEKUENDIGT, AuftragStatus.KONSOLIDIERT, vorgabeSCV, Carrier.CARRIER_DTAG},
                new Type[] {new DateType(), new DateType(), new DateType(), new DateType(), new LongType(),
                        new LongType(), new LongType(), new LongType(), new DateType(), new StringType()}
        );
        List<Object[]> result = sqlQuery.list();
        if (result != null) {
            List<CuDAVorschau> retVal = new ArrayList<>();
            for (Object[] values : result) {
                CuDAVorschau view = new CuDAVorschau();
                view.setVorgabeSCV(ObjectTools.getDateSilent(values, 0));
                view.setAnzahlCuDA(ObjectTools.getIntegerSilent(values, 2));
                retVal.add(view);
            }
            return retVal;
        }

        return null;
    }

    @Override
    public List<Carrierbestellung> findCBs4Carrier(final Long carrierId, final int maxResults, final int beginWith) {
        final StringBuilder hql = new StringBuilder("from ");
        hql.append(Carrierbestellung.class.getName()).append(" cb where cb.carrier= :carrierId");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setLong("carrierId", carrierId);
        query.setFirstResult(beginWith);
        if (maxResults > 0) {
            query.setMaxResults(maxResults);
        }

        @SuppressWarnings("unchecked")
        List<Carrierbestellung> result = query.list();
        return result;
    }

    @Override
    public List<Long> findAuftragIds4CB(Long cbId) {
        StringBuilder sql = new StringBuilder("select at.AUFTRAG_ID from T_CARRIERBESTELLUNG cb ");
        sql.append("inner join T_ENDSTELLE es on es.CB_2_ES_ID=cb.CB_2_ES_ID ");
        sql.append("inner join T_AUFTRAG_TECHNIK at on es.ES_GRUPPE=at.AT_2_ES_ID ");
        sql.append("where cb.CB_ID=? and at.GUELTIG_VON<=? and at.GUELTIG_BIS>? ");

        Date now = DateTools.getActualSQLDate();
        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameters(new Object[] { cbId, now, now}, new Type[] {new LongType(), new DateType(), new DateType()});
        List<BigDecimal> result = sqlQuery.list();
        if (result != null) {
            List<Long> retVal = new ArrayList<>();
            for (BigDecimal value : result) {
                retVal.add(value.longValue());
            }
            return retVal;
        }
        return null;
    }

    @Override
    public Long findHvtStdId4Cb(Long cbId) {
        flushSession();
        StringBuilder sql = new StringBuilder("select distinct(HVT_ID_STANDORT) from ");
        sql.append(" t_carrierbestellung cb");
        sql.append(" inner join t_endstelle e on cb.CB_2_ES_ID=e.CB_2_ES_ID");
        sql.append(" where cb.CB_ID=?");

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setLong(0, cbId);
        return ((BigDecimal) sqlQuery.uniqueResult()).longValue();
    }

    @Override
    public void deleteById(Serializable id) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("delete from " + Carrierbestellung.class.getName() + " cb where cb.id=?");
        query.setParameter(0, id);
        query.executeUpdate();
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
