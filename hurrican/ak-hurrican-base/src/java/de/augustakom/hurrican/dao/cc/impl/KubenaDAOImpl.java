/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2005 08:30:44
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.lang.ObjectTools;
import de.augustakom.hurrican.dao.cc.KubenaDAO;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.kubena.Kubena;
import de.augustakom.hurrican.model.cc.kubena.KubenaHVT;
import de.augustakom.hurrican.model.cc.kubena.KubenaProdukt;
import de.augustakom.hurrican.model.cc.kubena.KubenaResultView;
import de.augustakom.hurrican.model.cc.kubena.KubenaVbz;


/**
 * Hibernate DAO-Implementierung von <code>KubenaDAO</code>.
 *
 *
 */
public class KubenaDAOImpl extends Hibernate4DAOImpl implements KubenaDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public void deleteKubenaHVT(final Long kubenaId, final Long hvtIdStd) {
        StringBuilder hql = new StringBuilder("delete from ");
        hql.append(KubenaHVT.class.getName());
        hql.append(" h where h.kubenaId=:kubenaId and h.hvtIdStandort=:hvtStandortId");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setLong("kubenaId", kubenaId);
        query.setLong("hvtStandortId", hvtIdStd);
        query.executeUpdate();
    }

    @Override
    public void deleteKubenaProdukt(final Long kubenaId, final Long prodId) {
        StringBuilder hql = new StringBuilder("delete from ");
        hql.append(KubenaProdukt.class.getName());
        hql.append(" h where h.kubenaId=:kubenaId and h.prodId=:prodId");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setLong("kubenaId", kubenaId);
        query.setLong("prodId", prodId);
        query.executeUpdate();
    }

    @Override
    public List<KubenaResultView> queryKubenaHVT(Long kubenaId) {
        StringBuilder hql = new StringBuilder("select a.id, a.kundeNo, es.endstelle, ");
        hql.append("es.name, es.ort, es.plz, es.endstelleTyp, ");
        hql.append("t.vbz, p.anschlussart from ");
        hql.append(Kubena.class.getName()).append(" k, ");
        hql.append(KubenaHVT.class.getName()).append(" kh, ");
        hql.append(HVTStandort.class.getName()).append(" h, ");
        hql.append(Endstelle.class.getName()).append(" es, ");
        hql.append(AuftragTechnik.class.getName()).append(" at, ");
        hql.append(VerbindungsBezeichnung.class.getName()).append(" t, ");
        hql.append(Auftrag.class.getName()).append(" a, ");
        hql.append(AuftragDaten.class.getName()).append(" ad, ");
        hql.append(Produkt.class.getName()).append(" p ");
        hql.append("where k.id= :kubenaId and kh.kubenaId=k.id and kh.hvtIdStandort=h.id ");
        hql.append("and es.hvtIdStandort=h.id and es.endstelleGruppeId=at.auftragTechnik2EndstelleId ");
        hql.append("and at.vbzId=t.id and at.auftragId=a.id and ad.auftragId=a.id and ad.prodId=p.id ");
        hql.append("and ad.inbetriebnahme<k.schaltzeitVon ");
        hql.append("and (ad.kuendigung is null or ad.kuendigung>k.schaltzeitBis) ");
        hql.append("and at.gueltigVon<= :now and at.gueltigBis> :now ");
        hql.append("and ad.gueltigVon<= :now and ad.gueltigBis> :now ");
        hql.append("and ad.statusId<> :konsolidiert");

        return queryKubena(kubenaId, hql.toString());
    }

    @Override
    public List<KubenaResultView> queryKubenaProd(Long kubenaId) {
        StringBuilder hql = new StringBuilder("select a.id, a.kundeNo, es.endstelle, ");
        hql.append("es.name, es.ort, es.plz, es.endstelleTyp, ");
        hql.append("t.vbz, p.anschlussart from ");
        hql.append(Kubena.class.getName()).append(" k, ");
        hql.append(KubenaHVT.class.getName()).append(" kh, ");
        hql.append(KubenaProdukt.class.getName()).append(" kp, ");
        hql.append(HVTStandort.class.getName()).append(" h, ");
        hql.append(Endstelle.class.getName()).append(" es, ");
        hql.append(AuftragTechnik.class.getName()).append(" at, ");
        hql.append(VerbindungsBezeichnung.class.getName()).append(" t, ");
        hql.append(Auftrag.class.getName()).append(" a, ");
        hql.append(AuftragDaten.class.getName()).append(" ad, ");
        hql.append(Produkt.class.getName()).append(" p ");
        hql.append("where k.id= :kubenaId and kh.kubenaId=k.id and kh.hvtIdStandort=h.id ");
        hql.append("and kp.kubenaId=k.id and kp.prodId=p.id ");
        hql.append("and es.hvtIdStandort=h.id and es.endstelleGruppeId=at.auftragTechnik2EndstelleId ");
        hql.append("and at.vbzId=t.id and at.auftragId=a.id and ad.auftragId=a.id and ad.prodId=p.id ");
        hql.append("and ad.inbetriebnahme<k.schaltzeitVon ");
        hql.append("and (ad.kuendigung is null or ad.kuendigung>k.schaltzeitBis) ");
        hql.append("and at.gueltigVon<= :now and at.gueltigBis> :now ");
        hql.append("and ad.gueltigVon<= :now and ad.gueltigBis> :now ");
        hql.append("and ad.statusId<> :konsolidiert");

        return queryKubena(kubenaId, hql.toString());
    }

    @Override
    public List<KubenaResultView> queryKubenaVbz(Long kubenaId) {
        StringBuilder hql = new StringBuilder("select a.id, a.kundeNo, es.endstelle, ");
        hql.append("es.name, es.ort, es.plz, es.endstelleTyp, ");
        hql.append("t.vbz, p.anschlussart from ");
        hql.append(Kubena.class.getName()).append(" k, ");
        hql.append(KubenaVbz.class.getName()).append(" kt, ");
        hql.append(Endstelle.class.getName()).append(" es, ");
        hql.append(AuftragTechnik.class.getName()).append(" at, ");
        hql.append(VerbindungsBezeichnung.class.getName()).append(" t, ");
        hql.append(Auftrag.class.getName()).append(" a, ");
        hql.append(AuftragDaten.class.getName()).append(" ad, ");
        hql.append(Produkt.class.getName()).append(" p ");
        hql.append("where k.id= :kubenaId and kt.kubenaId=k.id ");
        hql.append("and kt.vbz=t.vbz and t.id=at.vbzId ");
        hql.append("and es.endstelleGruppeId=at.auftragTechnik2EndstelleId ");
        hql.append("and at.auftragId=a.id and ad.auftragId=a.id ");
        hql.append("and ad.inbetriebnahme<k.schaltzeitVon and ad.prodId=p.id ");
        hql.append("and (ad.kuendigung is null or ad.kuendigung>k.schaltzeitBis) ");
        hql.append("and at.gueltigVon<= :now and at.gueltigBis> :now ");
        hql.append("and ad.gueltigVon<= :now and ad.gueltigBis> :now ");
        hql.append("and ad.statusId<> :konsolidiert");

        return queryKubena(kubenaId, hql.toString());
    }

    /**
     * Ermittelt die Daten fuer eine best. Kubena. <br> Der String <code>hql</code> enthaelt den Hibernate-Query-String,
     * der fuer die Suche verwendet werden soll. <br> Als Select-Felder MUESSEN folgende Felder (Reihenfolge beachten!)
     * vorhanden sein: <ul> <li>Auftrags-ID <li>Kundennummer <li>Endstelle <li>Endstelle Name <li>Endstelle Ort
     * <li>Endstelle PLZ <li>Endstelle-Typ <li>VerbindungsBezeichnung <li>Produkt </ul> Fuer die Where-Clause werden
     * folgende Parameter (mit Namen) erwartet: <ul> <li>kubenaId - die ID der auszufuehrenden Kubena <li>now -
     * aktuelles Datum </ul>
     *
     * @param kubenaId
     * @param hql
     * @return
     */
    protected List<KubenaResultView> queryKubena(final Long kubenaId, final String hql) {
        Session session = sessionFactory.getCurrentSession();
        Date now = new Date();
        Query q = session.createQuery(hql);
        q.setLong("kubenaId", kubenaId);
        q.setDate("now", now);
        q.setLong("konsolidiert", AuftragStatus.KONSOLIDIERT);

        @SuppressWarnings("unchecked")
        List<Object[]> result = q.list();
        if (result != null) {
            Map<Long, KubenaResultView> tmp = new HashMap<>();
            for (Object[] values : result) {
                Long aId = ObjectTools.getLongSilent(values, 0);
                String esTyp = ObjectTools.getStringSilent(values, 6);
                if (!tmp.containsKey(aId)) {
                    KubenaResultView view = new KubenaResultView();
                    view.setAuftragId(ObjectTools.getLongSilent(values, 0));
                    view.setKundNoOrig(ObjectTools.getLongSilent(values, 1));
                    view.setEndstelle(ObjectTools.getStringSilent(values, 2), esTyp);
                    view.setEndstelleName(ObjectTools.getStringSilent(values, 3), esTyp);
                    view.setEndstelleOrt(ObjectTools.getStringSilent(values, 4), esTyp);
                    view.setEndstellePLZ(ObjectTools.getStringSilent(values, 5), esTyp);
                    view.setVbz(ObjectTools.getStringSilent(values, 7));
                    view.setProdukt(ObjectTools.getStringSilent(values, 8));
                    tmp.put(aId, view);
                }
                else {
                    KubenaResultView view = tmp.get(aId);
                    view.setEndstelle(ObjectTools.getStringSilent(values, 2), esTyp);
                    view.setEndstelleName(ObjectTools.getStringSilent(values, 3), esTyp);
                    view.setEndstelleOrt(ObjectTools.getStringSilent(values, 4), esTyp);
                    view.setEndstellePLZ(ObjectTools.getStringSilent(values, 5), esTyp);
                }
            }

            return new ArrayList<>(tmp.values());
        }
        return null;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}

