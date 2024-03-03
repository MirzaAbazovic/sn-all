/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.08.2004 15:58:54
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.time.*;
import java.util.*;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.ObjectTools;
import de.augustakom.hurrican.dao.cc.CCAuftragDAO;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.mnet.common.tools.DateConverterUtils;


/**
 * Hibernate DAO-Implementierung, um Objekte des Typs <code>Auftrag</code> zu verwalten.
 *
 *
 */
public class CCAuftragDAOImpl extends Hibernate4DAOImpl implements CCAuftragDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public void save(Auftrag toSave) {
        store(toSave);
    }

    @Override
    public List<Auftrag> findAuftraege4Export(final Date minRealDate, final Date maxRealDate) {
        final StringBuilder hql = new StringBuilder("select distinct ad.auftragNoOrig, a.id, a.kundeNo, ad.statusId from ");
        hql.append(Produkt.class.getName()).append(" p, ");
        hql.append(AuftragDaten.class.getName()).append(" ad, ");
        hql.append(Auftrag.class.getName()).append(" a ");
        if (minRealDate != null) {
            hql.append(", ");
            hql.append(Verlauf.class.getName()).append(" v ");
        }
        hql.append(" where p.id=ad.prodId and ad.auftragId=a.id ");
        hql.append(" and p.exportKdpM= :true and ad.gueltigBis> :now ");
        // Kein Full-Export, dann beachte Verlauf und Realisierungtermin
        if (minRealDate != null) {
            // nur Auftraege mit Realisierungstermin zwischen minDate und maxDate
            hql.append(" and v.auftragId = ad.auftragId ");
            hql.append(" and v.verlaufStatusId not in (:vstorno, :vstornoKuend) ");
            hql.append(" and v.realisierungstermin>= :minDate and v.realisierungstermin<= :maxDate ");
            hql.append(" group by ad.auftragNoOrig, a.id, a.kundeNo, ad.statusId ");
            hql.append(" order by a.id desc ");
        }
        else {
            // Der Verlauf fuer die techn. Realisierung kann erst bei der Uebergabe in den Returnwert
            // abgefragt werden da die Altprodukte u.U. keinen Verlauf besitzen.
            hql.append(" and ad.statusId>= :technRealisierung and ad.statusId < :kons");
            hql.append(" and (ad.kuendigung is null or ad.kuendigung > :maxKuendDate) ");
            hql.append(" group by ad.auftragNoOrig, a.id, a.kundeNo, ad.statusId ");
            hql.append(" order by a.id desc ");
        }

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setBoolean("true", true);
        query.setDate("now", new Date());
        if (minRealDate != null) {
            query.setLong("vstorno", VerlaufStatus.VERLAUF_STORNIERT);
            query.setLong("vstornoKuend", VerlaufStatus.KUENDIGUNG_VERLAUF_STORNIERT);
            query.setDate("minDate", minRealDate);
            query.setDate("maxDate", maxRealDate);
        }
        else {
            query.setLong("technRealisierung", AuftragStatus.TECHNISCHE_REALISIERUNG);
            query.setLong("kons", AuftragStatus.KONSOLIDIERT);
            query.setDate("maxKuendDate", new Date());
        }

        List<Object[]> result = query.list();

        List<Auftrag> retVal = new ArrayList<>();
        for (Object[] value : result) {
            //Abfrage auf Verlauf
            if (ObjectTools.getLongSilent(value, 1).equals(AuftragStatus.TECHNISCHE_REALISIERUNG)
                    && (minRealDate == null)) {
                StringBuilder hqlVerlauf = new StringBuilder("select ");
                hqlVerlauf.append(Verlauf.class.getName()).append(" v ");
                hqlVerlauf.append(" where v.auftragId = :auftragId ");
                hqlVerlauf.append(" and v.verlaufStatusId not in (:vstorno, :vstornoKuend) ");
                hqlVerlauf.append(" and v.realisierungstermin<= :maxDate ");
                hqlVerlauf.append(" and v.akt :true");
                Query queryVerlauf = session.createQuery(hql.toString());
                queryVerlauf.setLong("auftragId", ObjectTools.getLongSilent(value, 1));
                queryVerlauf.setLong("vstorno", VerlaufStatus.VERLAUF_STORNIERT);
                queryVerlauf.setLong("vstornoKuend", VerlaufStatus.KUENDIGUNG_VERLAUF_STORNIERT);
                queryVerlauf.setDate("maxDate", maxRealDate);
                queryVerlauf.setBoolean("true", true);

                List<Object[]> resultVerlauf = queryVerlauf.list();
                if (resultVerlauf.isEmpty()) {
                    continue;
                }
            }

            Auftrag auftrag = new Auftrag();
            auftrag.setAuftragId(ObjectTools.getLongSilent(value, 1));
            auftrag.setKundeNo(ObjectTools.getLongSilent(value, 2));
            retVal.add(auftrag);
        }
        return retVal;
    }

    @Override
    public Long getMaxAuftragId() {
        StringBuilder hql = new StringBuilder("select max(a.id) from ");
        hql.append(Auftrag.class.getName()).append(" a");

        List result = find(hql.toString());
        return ((result != null) && (result.size() == 1)) ? (Long) result.get(0) : null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Auftrag> findActiveOrdersByLineId(final String lineId, final LocalDate when) {
        final StringBuilder hql = new StringBuilder("select distinct a from ");
        hql.append(Auftrag.class.getName()).append(" a, ");
        hql.append(AuftragDaten.class.getName()).append(" ad, ");
        hql.append(AuftragTechnik.class.getName()).append(" atech, ");
        hql.append(VerbindungsBezeichnung.class.getName()).append(" vbz ");
        hql.append(" where vbz." + VerbindungsBezeichnung.VBZ + "= :lineId ");
        hql.append(" and atech.vbzId=vbz.id and atech.auftragId=a.id and ad.auftragId=a.id ");
        hql.append(" and atech.gueltigBis > :now and ad.gueltigBis > :now ");
        hql.append(" and (( ad.statusId = :inBetrieb ) ");
        hql.append(" or ( ad.statusId = :gekuendigt and ad.kuendigung > :when ))");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setString("lineId", lineId);
        query.setDate("now", new Date());
        query.setDate("when", DateConverterUtils.asDate(when));
        query.setLong("inBetrieb", AuftragStatus.IN_BETRIEB);
        query.setLong("gekuendigt", AuftragStatus.AUFTRAG_GEKUENDIGT);

        return query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Auftrag> findActiveOrdersByLineIdAndAuftragStatus(final String lineId, final Long auftragStatus) {
        final StringBuilder hql = new StringBuilder("select distinct a from ");
        hql.append(Auftrag.class.getName()).append(" a, ");
        hql.append(AuftragDaten.class.getName()).append(" ad, ");
        hql.append(AuftragTechnik.class.getName()).append(" atech, ");
        hql.append(VerbindungsBezeichnung.class.getName()).append(" vbz ");
        hql.append(" where vbz." + VerbindungsBezeichnung.VBZ + "= :lineId ");
        hql.append(" and atech.vbzId=vbz.id and atech.auftragId=a.id and ad.auftragId=a.id ");
        hql.append(" and atech.gueltigBis > :now and ad.gueltigBis > :now ");
        hql.append(" and ( ad.statusId = :auftragStatus ) ");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setString("lineId", lineId);
        query.setDate("now", new Date());
        query.setLong("auftragStatus", auftragStatus);

        return query.list();
    }

    /**
     * Ermittelt Auftragsdaten zu der angegebenen LineId zwecks Portfreigabe.
     *
     * @param lineId  die LineID der zur ermittelnde Aufragdaten
     * @return Eine Liste mit Auftragsdaten zur angegebene LineId
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<AuftragDaten> findAuftragDatenByLineIdAndStatus(final String lineId, final Long... auftragStatus) {
        if (auftragStatus == null || auftragStatus.length == 0) {
            return Collections.emptyList();
        }
        final StringBuilder hql = new StringBuilder("select distinct ad from ");
        hql.append(AuftragDaten.class.getName()).append(" ad, ");
        hql.append(AuftragTechnik.class.getName()).append(" atech, ");
        hql.append(VerbindungsBezeichnung.class.getName()).append(" vbz ");
        hql.append(" where vbz." + VerbindungsBezeichnung.VBZ + "= :lineId ");
        hql.append(" and atech.vbzId=vbz.id and atech.auftragId=ad.auftragId ");
        hql.append(" and atech.gueltigBis = :endDate and ad.gueltigBis = :endDate ");
        hql.append(" and ad.statusId in ( :auftragStatus ))");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setString("lineId", lineId);
        query.setDate("endDate", DateTools.getHurricanEndDate());
        query.setParameterList("auftragStatus", auftragStatus);

        return query.list();
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


