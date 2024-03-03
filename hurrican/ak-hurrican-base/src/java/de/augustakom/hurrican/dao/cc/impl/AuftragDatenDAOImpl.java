/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.08.2004 13:43:12
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.io.*;
import java.util.*;
import javax.annotation.*;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.dao.cc.AuftragDatenDAO;
import de.augustakom.hurrican.model.cc.Ansprechpartner;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Rangierung;


/**
 * Hibernate DAO-Implementierung fuer Objekte des Typs AuftragDaten.
 *
 *
 */
public class AuftragDatenDAOImpl extends HurricanHibernateDaoImpl implements AuftragDatenDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public AuftragDaten update4History(AuftragDaten modelToHistorize, Serializable id, Date gueltigBis) {
        AuftragDaten emptyNewObject = new AuftragDaten();
        return update4History(modelToHistorize, emptyNewObject, id, gueltigBis);
    }

    @Override
    public AuftragDaten findByAuftragId(final Long auftragId) {
        final StringBuilder hql = new StringBuilder();
        hql.append("from ");
        hql.append(AuftragDaten.class.getName());
        hql.append(" ad where ad.auftragId= :aId and ad.gueltigVon<= :now  and ad.gueltigBis> :now");

        Session session = sessionFactory.getCurrentSession();
        Date now = new Date();
        Query q = session.createQuery(hql.toString());
        q.setLong("aId", auftragId);
        q.setDate("now", now);

        @SuppressWarnings("unchecked")
        List<AuftragDaten> result = q.list();
        return ((result != null) && (!result.isEmpty())) ? result.get(result.size() - 1) : null;
    }

    @Override
    public List<AuftragDaten> findByKundeNo(Long kundeNo) {
        StringBuilder hql = new StringBuilder("select ad.id from ");
        hql.append(Auftrag.class.getName()).append(" a, ");
        hql.append(AuftragDaten.class.getName()).append(" ad ");
        hql.append(" where a.id=ad.auftragId and a.kundeNo=? ");
        hql.append(" and ad.gueltigVon<=? and ad.gueltigBis>? ");

        Date now = new Date();
        @SuppressWarnings("unchecked")
        List<Long> auftragIds = find(hql.toString(), new Object[] { kundeNo, now, now });

        if (auftragIds != null) {
            List<AuftragDaten> result = new ArrayList<>();
            for (Long aId : auftragIds) {
                result.add((AuftragDaten) sessionFactory.getCurrentSession().get(AuftragDaten.class, aId));
            }
            return result;
        }

        return null;
    }

    @Override
    public List<AuftragDaten> findByBuendelNr(final Integer buendelNr, final String buendelNrHerkunft) {
        final StringBuilder hql = new StringBuilder();
        hql.append("from ");
        hql.append(AuftragDaten.class.getName());
        hql.append(" ad where ad.buendelNr= :bNr and ad.buendelNrHerkunft= :bNrH ");
        hql.append(" and ad.gueltigVon<= :now  and ad.gueltigBis> :now");

        Session session = sessionFactory.getCurrentSession();
        Date now = new Date();
        Query q = session.createQuery(hql.toString());
        q.setInteger("bNr", buendelNr);
        q.setString("bNrH", buendelNrHerkunft);
        q.setDate("now", now);

        @SuppressWarnings("unchecked")
        List<AuftragDaten> result = q.list();
        return result;
    }

    @Override
    public List<AuftragDaten> findByOrderNoOrig(final Long orderNoOrig, final boolean ignoreStatus) {
        final StringBuilder hql = new StringBuilder();
        hql.append("from ");
        hql.append(AuftragDaten.class.getName());
        hql.append(" ad where ad.auftragNoOrig= :oNo ");
        hql.append(" and ad.gueltigVon<= :now and ad.gueltigBis> :now");
        if (!ignoreStatus) {
            hql.append(" and ad.statusId not in (:storno, :absage, :konsol) ");
        }
        hql.append(" order by ad.auftragId desc");

        Session session = sessionFactory.getCurrentSession();
        Date now = new Date();
        Query q = session.createQuery(hql.toString());
        q.setLong("oNo", orderNoOrig);
        q.setDate("now", now);
        if (!ignoreStatus) {
            q.setLong("storno", AuftragStatus.STORNO);
            q.setLong("absage", AuftragStatus.ABSAGE);
            q.setLong("konsol", AuftragStatus.KONSOLIDIERT);
        }

        @SuppressWarnings("unchecked")
        List<AuftragDaten> result = q.list();
        return result;
    }

    @Override
    public AuftragDaten findByEndstelleId(final Long endstelleId) {
        final StringBuilder hql = new StringBuilder("select ad.id from ");
        hql.append(AuftragDaten.class.getName()).append(" ad, ");
        hql.append(AuftragTechnik.class.getName()).append(" at, ");
        hql.append(Endstelle.class.getName()).append(" e ");
        hql.append(" where e.id= :esId and e.endstelleGruppeId=at.auftragTechnik2EndstelleId ");
        hql.append(" and at.auftragId=ad.auftragId and ad.statusId< :konsolidiert ");
        hql.append(" and at.gueltigVon<= :now and at.gueltigBis> :now ");
        hql.append(" and ad.gueltigVon<= :now and ad.gueltigBis> :now ");

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(hql.toString());
        q.setLong("esId", endstelleId);
        q.setLong("konsolidiert", AuftragStatus.KONSOLIDIERT);
        q.setDate("now", new Date());

        @SuppressWarnings("unchecked")
        List<Long> result = q.list();
        if ((result != null) && (result.size() == 1)) {
            Long aId = result.get(0);
            return (AuftragDaten) sessionFactory.getCurrentSession().get(AuftragDaten.class, aId);
        }

        return null;
    }

    @Override
    public List<AuftragDaten> findByGeoIdProduktIds(final Long geoId, final Long ... produktIds) {
        Session session = sessionFactory.getCurrentSession();
        StringBuilder hql = new StringBuilder("select ad from ");
        hql.append(AuftragDaten.class.getName()).append(" ad, ");
        hql.append(AuftragTechnik.class.getName()).append(" at, ");
        hql.append(Endstelle.class.getName()).append(" e ");
        hql.append(" where e.geoId= :geoId and e.endstelleGruppeId=at.auftragTechnik2EndstelleId ");
        hql.append(" and at.auftragId=ad.auftragId and ad.statusId < :konsolidiert ");
        if(produktIds.length > 0) {
            hql.append(" and ad.prodId in (:produktIds)");
        }
        hql.append(" and at.gueltigVon<= :now and at.gueltigBis> :now ");
        hql.append(" and ad.gueltigVon<= :now and ad.gueltigBis> :now ");

        Query query = session.createQuery(hql.toString());
        query.setLong("geoId", geoId);
        query.setLong("konsolidiert", AuftragStatus.KONSOLIDIERT);
        if(produktIds.length > 0) {
            query.setParameterList("produktIds", produktIds);
        }
        query.setTimestamp("now", new Date());
        @SuppressWarnings("unchecked")
        List<AuftragDaten> result = query.list();
        return result;
    }

    @Override
    public List<AuftragDaten> findAuftragDatenByEquipment(final Long equipmentId) {
        final String sql = "select {ad.*} " +
                " from T_AUFTRAG_DATEN {ad} " +
                " join T_AUFTRAG_TECHNIK at on {ad}.AUFTRAG_ID = at.AUFTRAG_ID " +
                " join T_ENDSTELLE es on es.ES_GRUPPE=at.AT_2_ES_ID " +
                " join T_RANGIERUNG r on r.RANGIER_ID=es.RANGIER_ID or r.RANGIER_ID=es.RANGIER_ID_ADDITIONAL " +
                " where (r.EQ_IN_ID=:id or r.EQ_OUT_ID=:id) " +
                // Rangierungshistorisierung darf nicht beachtet werden da sie fuer die Zukunft gemacht wird!
                //" and (r.GUELTIG_VON <= :now and r.GUELTIG_BIS > :now) " +
                " and (at.GUELTIG_VON <= :now and at.GUELTIG_BIS > :now) " +
                " and ({ad}.GUELTIG_VON <= :now and {ad}.GUELTIG_BIS > :now) ";

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createSQLQuery(sql).addEntity("ad", AuftragDaten.class);
        query.setLong("id", equipmentId);
        query.setTimestamp("now", new Date());
        @SuppressWarnings("unchecked")
        List<AuftragDaten> result = query.list();
        return result;
    }

    @Override
    public List<AuftragDaten> findAuftragDatenByEquipment(final String switchAK, final String hwEQN, final Date gueltig) {
        final String sql = "select {ad.*} " +
                " from T_AUFTRAG_DATEN {ad} " +
                " join T_AUFTRAG_TECHNIK at on {ad}.AUFTRAG_ID = at.AUFTRAG_ID " +
                " join T_ENDSTELLE es on es.ES_GRUPPE=at.AT_2_ES_ID " +
                " join T_RANGIERUNG r on r.RANGIER_ID=es.RANGIER_ID or r.RANGIER_ID=es.RANGIER_ID_ADDITIONAL " +
                " join T_EQUIPMENT eq on eq.EQ_ID = r.EQ_IN_ID or eq.EQ_ID = r.EQ_OUT_ID " +
                " join T_HW_SWITCH sw on eq.SWITCH = sw.ID " +
                " where sw.NAME = :switch and eq.HW_EQN = :hwEqn " +
                // Rangierungshistorisierung darf nicht beachtet werden da sie fuer die Zukunft gemacht wird!
                // " and (r.GUELTIG_VON <= :now and r.GUELTIG_BIS > :now) " +
                " and (at.GUELTIG_VON <= :gueltig and at.GUELTIG_BIS > :gueltig) " +
                " and ({ad}.GUELTIG_VON <= :gueltig and {ad}.GUELTIG_BIS > :gueltig) ";

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createSQLQuery(sql).addEntity("ad", AuftragDaten.class);
        query.setString("switch", switchAK);
        query.setString("hwEqn", hwEQN);
        query.setTimestamp("gueltig", gueltig);
        @SuppressWarnings("unchecked")
        List<AuftragDaten> result = query.list();
        return result;
    }

    @Override
    public List<AuftragDaten> findAuftragDatenByRackAndEqn(final String rackBezeichnung, final String hwEQN, final Date gueltig) {
        final String sql = "select {ad.*} " +
                " from T_AUFTRAG_DATEN {ad} " +
                " join T_AUFTRAG_TECHNIK at on {ad}.AUFTRAG_ID = at.AUFTRAG_ID " +
                " join T_ENDSTELLE es on es.ES_GRUPPE=at.AT_2_ES_ID " +
                " join T_RANGIERUNG r on r.RANGIER_ID=es.RANGIER_ID or r.RANGIER_ID=es.RANGIER_ID_ADDITIONAL " +
                " join T_EQUIPMENT eq on eq.EQ_ID = r.EQ_IN_ID " +
                " join T_HW_BAUGRUPPE bg on eq.HW_BAUGRUPPEN_ID = bg.ID " +
                " join T_HW_RACK ra on bg.RACK_ID = ra.ID " +
                " where ra.GERAETEBEZ = :geraetBez and eq.HW_EQN = :hwEqn " +
                // Rangierungshistorisierung darf nicht beachtet werden da sie fuer die Zukunft gemacht wird!
                // " and (r.GUELTIG_VON <= :now and r.GUELTIG_BIS > :now) " +
                " and (at.GUELTIG_VON <= :gueltig and at.GUELTIG_BIS > :gueltig) " +
                " and ({ad}.GUELTIG_VON <= :gueltig and {ad}.GUELTIG_BIS > :gueltig) ";

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createSQLQuery(sql).addEntity("ad", AuftragDaten.class);
        query.setString("geraetBez", rackBezeichnung);
        query.setString("hwEqn", hwEQN);
        query.setTimestamp("gueltig", gueltig);
        @SuppressWarnings("unchecked")
        List<AuftragDaten> result = query.list();
        return result;
    }

    @Override
    public List<AuftragDaten> findAuftragDatenByBaugruppe(final Long baugruppeId) {
        final String sql = "select {ad.*} " +
                " from T_AUFTRAG_DATEN {ad} " +
                " join T_AUFTRAG_TECHNIK at on {ad}.AUFTRAG_ID = at.AUFTRAG_ID " +
                " join T_ENDSTELLE es on es.ES_GRUPPE=at.AT_2_ES_ID " +
                " join T_RANGIERUNG r on r.RANGIER_ID=es.RANGIER_ID or r.RANGIER_ID=es.RANGIER_ID_ADDITIONAL " +
                " join T_EQUIPMENT eq on eq.EQ_ID = r.EQ_IN_ID " +
                " join T_HW_BAUGRUPPE bg on eq.HW_BAUGRUPPEN_ID = bg.ID " +
                " where bg.ID=:id " +
                // Rangierungshistorisierung darf nicht beachtet werden da sie fuer die Zukunft gemacht wird!
                //" and (r.GUELTIG_VON <= SYSDATE and r.GUELTIG_BIS > SYSDATE) " +
                " and (at.GUELTIG_VON <= SYSDATE and at.GUELTIG_BIS > SYSDATE) " +
                " and ({ad}.GUELTIG_VON <= SYSDATE and {ad}.GUELTIG_BIS > SYSDATE) ";

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createSQLQuery(sql).addEntity("ad", AuftragDaten.class);
        query.setLong("id", baugruppeId);
        @SuppressWarnings("unchecked")
        List<AuftragDaten> result = query.list();
        return result;
    }


    @Override
    public AuftragDaten findParent4Buendel(final Long kundeNo, final Integer buendelNr, final String buendelHerkunft) {
        final StringBuilder hql = new StringBuilder();
        hql.append("select ad.id from ");
        hql.append(AuftragDaten.class.getName()).append(" ad, ");
        hql.append(Auftrag.class.getName()).append(" a, ");
        hql.append(Produkt.class.getName()).append(" p ");
        hql.append(" where ad.buendelNr= :bNr and ad.buendelNrHerkunft= :bHerk ");
        hql.append(" and ad.auftragId=a.id and a.kundeNo= :kNo ");
        hql.append(" and p.id=ad.prodId and p.isParent= :isP ");
        hql.append(" and ad.statusId not in (:storno, :absage) and ad.statusId< :gekuendigt ");
        hql.append(" and ad.gueltigVon<= :now  and ad.gueltigBis> :now");

        Session session = sessionFactory.getCurrentSession();
        Date now = new Date();
        Query q = session.createQuery(hql.toString());
        q.setInteger("bNr", buendelNr);
        q.setString("bHerk", buendelHerkunft);
        q.setLong("kNo", kundeNo);
        q.setBoolean("isP", Boolean.TRUE);
        q.setLong("storno", AuftragStatus.STORNO);
        q.setLong("absage", AuftragStatus.ABSAGE);
        q.setLong("gekuendigt", AuftragStatus.AUFTRAG_GEKUENDIGT);
        q.setDate("now", now);

        @SuppressWarnings("unchecked")
        List<Long> result = q.list();
        if ((result != null) && (result.size() == 1)) {
            Long aId = result.get(0);
            return (AuftragDaten) sessionFactory.getCurrentSession().get(AuftragDaten.class, aId);
        }

        return null;
    }

    @Override
    public List<AuftragDaten> find4Sperre(final Long kundeNo) {
        final StringBuilder hql = new StringBuilder();
        hql.append("select ad.id from ");
        hql.append(Auftrag.class.getName()).append(" a, ");
        hql.append(AuftragDaten.class.getName());
        hql.append(" ad where a.id=ad.auftragId and ");
        hql.append(" ad.gueltigVon<= :now and ad.gueltigBis> :now and ");
        hql.append(" a.kundeNo= :kNo and ad.statusId> :status and ad.statusId< :statusKons ");
        hql.append(" and (ad.kuendigung is null or ad.kuendigung>= :now)");

        Session session = sessionFactory.getCurrentSession();
        Date now = new Date();
        Query q = session.createQuery(hql.toString());
        q.setDate("now", now);
        q.setLong("kNo", kundeNo);
        q.setLong("status", AuftragStatus.TECHNISCHE_REALISIERUNG);
        q.setLong("statusKons", AuftragStatus.KONSOLIDIERT);

        @SuppressWarnings("unchecked")
        List<Long> result = q.list();
        if (result != null) {
            List<AuftragDaten> retVal = new ArrayList<>(result.size());
            for (Long aId : result) {
                // AuftragDaten werden ueber load-Methode geladen - erspart sehr viel Codier-Aufwand.
                // Da bei den Sperren nicht sehr viele Auftraege abgefragt werden ist diese
                // Vorgehensweise akzeptabel.
                AuftragDaten ad = (AuftragDaten) sessionFactory.getCurrentSession().get(AuftragDaten.class, aId);
                retVal.add(ad);
            }

            return retVal;
        }

        return null;
    }

    @Override
    public Long findProduktId4Auftrag(final Long auftragId) {
        final StringBuilder hql = new StringBuilder();
        hql.append("select ad.prodId from ");
        hql.append(AuftragDaten.class.getName());
        hql.append(" ad where ad.auftragId= :aId and ");
        hql.append(" ad.gueltigVon<= :now and ad.gueltigBis> :now ");

        Session session = sessionFactory.getCurrentSession();
        Date now = new Date();
        Query q = session.createQuery(hql.toString());
        q.setDate("now", now);
        q.setLong("aId", auftragId);

        @SuppressWarnings("unchecked")
        List<Long> result = q.list();
        return ((result != null) && (result.size() == 1)) ? result.get(0) : null;
    }

    @Override
    public List<Long> findAuftragIds(Long kundeNo, Long prodId) {
        StringBuilder hql = new StringBuilder("select distinct a.id from ");
        hql.append(Auftrag.class.getName()).append(" a, ");
        hql.append(AuftragDaten.class.getName()).append(" ad ");
        hql.append(" where a.id=ad.auftragId and a.kundeNo=? ");

        if (prodId != null) {
            hql.append(" and ad.prodId=?");
            @SuppressWarnings("unchecked")
            List<Long> result = find(hql.toString(), new Object[] { kundeNo, prodId });
            return result;
        }
        @SuppressWarnings("unchecked")
        List<Long> result = find(hql.toString(), new Object[] { kundeNo });
        return result;
    }

    @Override
    public List<Long> findAuftragIdsInProduktGruppe(Long kundeNo, List<Long> produktGruppen) {
        List<Object> params = new ArrayList<>();
        params.add(kundeNo);
        params.add(AuftragStatus.STORNO);
        params.add(AuftragStatus.ABSAGE);
        params.add(AuftragStatus.KUENDIGUNG);

        StringBuilder hql = new StringBuilder("select distinct a.id from ");
        hql.append(Auftrag.class.getName()).append(" a, ");
        hql.append(AuftragDaten.class.getName()).append(" ad, ");
        hql.append(Produkt.class.getName()).append(" p ");
        hql.append(" where a.id=ad.auftragId and a.kundeNo=? ");
        hql.append(" and ad.prodId=p.id ");
        hql.append(" and ad.statusId not in (?,?) and ad.statusId<? and p.produktGruppeId in (");
        for (int i = 0; i < produktGruppen.size(); i++) {
            if (i > 0) { hql.append(","); }
            hql.append("?");
            params.add(produktGruppen.get(i));
        }
        hql.append(")");

        @SuppressWarnings("unchecked")
        List<Long> result = find(hql.toString(), params.toArray());
        return result;
    }

    @Override
    @Nonnull
    public List<AuftragDaten> findAktiveAuftragDatenByBaugruppe(@Nonnull Long baugruppeId) {
        DetachedCriteria equipmentCriteria = DetachedCriteria.forClass(Equipment.class)
                .add(Property.forName(Equipment.HW_BAUGRUPPEN_ID).eq(baugruppeId))
                .setProjection(Projections.property(Equipment.ID));

        DetachedCriteria rangierungCriteria = DetachedCriteria.forClass(Rangierung.class)
                .add(Property.forName(Rangierung.EQ_IN_ID).in(equipmentCriteria))
                .add(Property.forName(Rangierung.GUELTIG_BIS).eq(DateTools.getHurricanEndDate()))
                .add(Restrictions.isNotNull(Rangierung.ES_ID))
                .setProjection(Projections.property(Rangierung.ID));

        DetachedCriteria endstelleCriteria = DetachedCriteria.forClass(Endstelle.class)
                .add(Property.forName(Endstelle.RANGIER_ID).in(rangierungCriteria))
                .setProjection(Projections.property(Endstelle.ENDSTELLE_GRUPPE_ID));

        DetachedCriteria auftragTechnikCriteria = DetachedCriteria.forClass(AuftragTechnik.class)
                .add(Property.forName(AuftragTechnik.GUELTIG_BIS).eq(DateTools.getHurricanEndDate()))
                .add(Property.forName("auftragTechnik2EndstelleId").in(endstelleCriteria))
                .setProjection(Projections.property("auftragId"));

        DetachedCriteria auftragDatenCriteria = DetachedCriteria.forClass(AuftragDaten.class)
                .add(Property.forName(AuftragDaten.GUELTIG_BIS).eq(DateTools.getHurricanEndDate()))
                .add(Property.forName("statusId").lt(AuftragStatus.AUFTRAG_GEKUENDIGT))
                .add(Property.forName("statusId").ge(AuftragStatus.TECHNISCHE_REALISIERUNG))
                .add(Property.forName("auftragId").in(auftragTechnikCriteria))
                .addOrder(Order.desc("statusId"));

        @SuppressWarnings("unchecked")
        List<AuftragDaten> auftragDatens = (List<AuftragDaten>) auftragDatenCriteria.getExecutableCriteria(sessionFactory.getCurrentSession()).list();
        return auftragDatens;
    }

    @Override
    @Nonnull
    public List<AuftragDaten> findAktiveAuftragDatenByOrtsteilAndProduktGroup(final @Nonnull String ortsteil, final @Nonnull String produktGrouppe) {
        final StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("  ad.* ");
        sql.append("FROM t_auftrag_daten ad ");
        sql.append("  JOIN t_produkt p ON ad.PROD_ID = p.PROD_ID ");
        sql.append("  JOIN t_produktgruppe pg ON p.PRODUKTGRUPPE_ID = pg.ID ");
        sql.append("  JOIN t_auftrag_technik at ON ad.AUFTRAG_ID = at.AUFTRAG_ID ");
        sql.append("  JOIN t_endstelle es ON at.AT_2_ES_ID = es.ES_GRUPPE ");
        sql.append("  JOIN t_hvt_standort hvts ON ES.HVT_ID_STANDORT = hvts.HVT_ID_STANDORT ");
        sql.append("  JOIN t_hvt_gruppe hvtg ON hvts.HVT_GRUPPE_ID = hvtg.HVT_GRUPPE_ID ");
        sql.append("WHERE ");
        sql.append("  hvtg.ORTSTEIL = :ortsteil ");
        sql.append("  AND pg.PRODUKTGRUPPE = :produktGrouppe ");
        sql.append("  AND ad.GUELTIG_VON <= :adValidFrom ");
        sql.append("  AND ad.GUELTIG_BIS > :adValidTo ");
        sql.append("  AND at.GUELTIG_VON <= :atValidFrom ");
        sql.append("  AND at.GUELTIG_BIS > :atValidTo ");

        Session session = sessionFactory.getCurrentSession();
        java.sql.Date now = DateTools.getActualSQLDate();
        SQLQuery query = session.createSQLQuery(sql.toString()).addEntity(AuftragDaten.class);
        query.setString("ortsteil", ortsteil);
        query.setString("produktGrouppe", produktGrouppe);
        query.setDate("adValidFrom", now);
        query.setDate("adValidTo", now);
        query.setDate("atValidFrom", now);
        query.setDate("atValidTo", now);

        @SuppressWarnings("unchecked")
        List<AuftragDaten> result = query.list();
        return AuftragDaten.RETURN_ACTIVE_AUFTRAG_DATEN.apply(result);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<AuftragDaten> findAuftragDatenByAuftragNoOrigAndBuendelNo(Long auftragNoOrig, Integer buendelNo) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(AuftragDaten.class);
        if (auftragNoOrig != null) {
            criteria.add(Restrictions.eq("auftragNoOrig", auftragNoOrig));
        }
        if (buendelNo != null) {
            criteria.add(Restrictions.eq("buendelNr", buendelNo));
        }
        return criteria.list();
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


