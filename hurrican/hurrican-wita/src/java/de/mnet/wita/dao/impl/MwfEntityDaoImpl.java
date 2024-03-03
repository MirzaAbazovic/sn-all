/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.05.2011 08:41:29
 */
package de.mnet.wita.dao.impl;

import static de.mnet.wita.message.MessageWithSentToBsiFlag.SENT_TO_BSI_FIELD;
import static de.mnet.wita.message.MnetWitaRequest.*;
import static de.mnet.wita.message.MwfEntity.ID_FIELD;
import static de.mnet.wita.message.common.BsiProtokollEintragSent.*;
import static org.hibernate.criterion.Restrictions.*;

import java.time.*;
import java.util.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.mnet.wita.dao.MwfEntityDao;
import de.mnet.wita.exceptions.AuftragNotFoundException;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.message.MwfEntity;
import de.mnet.wita.message.Storno;
import de.mnet.wita.message.TerminVerschiebung;
import de.mnet.wita.message.common.Anlage;
import de.mnet.wita.message.common.EmailStatus;
import de.mnet.wita.message.common.SmsStatus;
import de.mnet.wita.message.meldung.AbbruchMeldungPv;
import de.mnet.wita.message.meldung.AnkuendigungsMeldungPv;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldung;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldungPv;
import de.mnet.wita.message.meldung.ErledigtMeldungPv;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.message.meldung.RueckMeldungPv;
import de.mnet.wita.message.meldung.TerminAnforderungsMeldung;
import de.mnet.wita.message.meldung.VerzoegerungsMeldungPv;

/**
 * Hibernate Implementierung von {@link MwfEntityDao}.
 */
public class MwfEntityDaoImpl extends Hibernate4DAOImpl implements MwfEntityDao {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public Auftrag getAuftragOfCbVorgang(Long cbVorgangId) {
        return Iterables.getOnlyElement(getRequestOfCbVorgang(cbVorgangId, Auftrag.class));
    }

    @Override
    public List<Storno> getStornosOfCbVorgang(Long cbVorgangId) {
        return getRequestOfCbVorgang(cbVorgangId, Storno.class);
    }

    @Override
    public List<TerminVerschiebung> getTerminverschiebungenOfCbVorgang(Long cbVorgangId) {
        return getRequestOfCbVorgang(cbVorgangId, TerminVerschiebung.class);
    }

    private <T extends MnetWitaRequest> List<T> getRequestOfCbVorgang(Long cbVorgangId, Class<T> clazz) {
        Criteria criteria = createCriteria(cbVorgangId, clazz);

        @SuppressWarnings("unchecked")
        List<T> requests = criteria.list();

        if (CollectionUtils.isEmpty(requests)) {
            throw new AuftragNotFoundException(String.format("Keine %s für cbVorgangId=%s gefunden",
                    clazz.getSimpleName(), cbVorgangId));
        }
        return requests;
    }

    private <T> Criteria createCriteria(final Long cbVorgangId, final Class<T> clazz) {
        Preconditions.checkNotNull(cbVorgangId);

        Session session = sessionFactory.getCurrentSession();
        Criteria createCriteria = session.createCriteria(clazz);
        return createCriteria.add(Restrictions.eq("cbVorgangId", cbVorgangId));
    }

    @Override
    public AnkuendigungsMeldungPv getLastAkmPv(final String vertragsNummer) {
        Preconditions.checkNotNull(vertragsNummer, "vertragsNummer ist null");

        Session session = sessionFactory.getCurrentSession();
        Criteria createCriteria = session.createCriteria(AnkuendigungsMeldungPv.class);
        Criteria criteria = createCriteria.add(Restrictions.eq(Meldung.VERTRAGS_NUMMER_FIELD, vertragsNummer))
                .addOrder(Order.desc(ID_FIELD));
        @SuppressWarnings("unchecked")
        List<AnkuendigungsMeldungPv> result = criteria.list();
        return Iterables.getFirst(result, null);
    }

    @Override
    public TerminAnforderungsMeldung getLastTam(final String externeAuftragsnummer) {
        Preconditions.checkNotNull(externeAuftragsnummer);

        Session session = sessionFactory.getCurrentSession();
        Criteria createCriteria = session.createCriteria(TerminAnforderungsMeldung.class);
        Criteria criteria = createCriteria
                .add(Restrictions.eq(Meldung.EXTERNE_AUFTRAGSNR_FIELD, externeAuftragsnummer)).addOrder(
                        Order.desc(ID_FIELD));
        @SuppressWarnings("unchecked")
        List<TerminAnforderungsMeldung> result = criteria.list();

        return Iterables.getFirst(result, null);
    }

    @Override
    public AuftragsBestaetigungsMeldung getLastAbm(final String externeAuftragsnummer) {
        Preconditions.checkNotNull(externeAuftragsnummer);

        Session session = sessionFactory.getCurrentSession();
        Criteria createCriteria = session.createCriteria(AuftragsBestaetigungsMeldung.class);
        Criteria criteria = createCriteria.add(Restrictions.eq("externeAuftragsnummer", externeAuftragsnummer))
                .addOrder(Order.desc(MwfEntity.ID_FIELD));
        @SuppressWarnings("unchecked")
        List<AuftragsBestaetigungsMeldung> result = criteria.list();

        return Iterables.getFirst(result, null);
    }

    @Override
    public AuftragsBestaetigungsMeldungPv getLastAbmPv(final String vertragsNummer) {
        Preconditions.checkNotNull(vertragsNummer);

        Session session = sessionFactory.getCurrentSession();
        Criteria createCriteria = session.createCriteria(AuftragsBestaetigungsMeldungPv.class);
        Criteria criteria = createCriteria.add(Restrictions.eq(Meldung.VERTRAGS_NUMMER_FIELD, vertragsNummer))
                .addOrder(Order.desc(MwfEntity.ID_FIELD));
        @SuppressWarnings("unchecked")
        List<AuftragsBestaetigungsMeldungPv> result = criteria.list();
        return Iterables.getFirst(result, null);
    }

    @Override
    public VerzoegerungsMeldungPv getLastVzmPv(final String vertragsNummer) {
        Preconditions.checkNotNull(vertragsNummer);

        Session session = sessionFactory.getCurrentSession();
        Criteria createCriteria = session.createCriteria(VerzoegerungsMeldungPv.class);
        Criteria criteria = createCriteria.add(Restrictions.eq(Meldung.VERTRAGS_NUMMER_FIELD, vertragsNummer))
                .addOrder(Order.desc(MwfEntity.ID_FIELD));
        @SuppressWarnings("unchecked")
        List<VerzoegerungsMeldungPv> result = criteria.list();
        return Iterables.getFirst(result, null);
    }


    @Override
    public AbbruchMeldungPv getLastAbbmPv(final String vertragsNummer) {
        Preconditions.checkNotNull(vertragsNummer);

        Session session = sessionFactory.getCurrentSession();
        Criteria createCriteria = session.createCriteria(AbbruchMeldungPv.class);
        Criteria criteria = createCriteria.add(Restrictions.eq(Meldung.VERTRAGS_NUMMER_FIELD, vertragsNummer))
                .addOrder(Order.desc(MwfEntity.ID_FIELD));
        @SuppressWarnings("unchecked")
        List<AbbruchMeldungPv> result = criteria.list();
        return Iterables.getFirst(result, null);
    }

    @Override
    public List<Long> findUnsentRequests(GeschaeftsfallTyp geschaeftsfallTyp, int maxResults) {
        String hql = "select mwr." + MnetWitaRequest.ID_FIELD + " from " + MnetWitaRequest.class.getName() + " mwr" + " where mwr."
                + MnetWitaRequest.SENT_AT + " is null and " + MnetWitaRequest.REQUEST_STORNIERT + "!= true and "
                + "mwr.geschaeftsfall.class = " + geschaeftsfallTyp.getClazz().getName() + " and ("
                + MnetWitaRequest.EARLIEST_SEND_DATE + " is null or " + MnetWitaRequest.EARLIEST_SEND_DATE + " <= current_date) "
                + "order by mwr.geschaeftsfall.kundenwunschtermin.datum asc";

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql);

        if (maxResults > 0) {
            query.setMaxResults(maxResults);
        }

        return query.list();
    }

    @Override
    public List<Meldung<?>> findAllMeldungen(final String externeAuftragsnummer) {
        Preconditions.checkNotNull(externeAuftragsnummer);

        Session session = sessionFactory.getCurrentSession();
        Criteria createCriteria = session.createCriteria(Meldung.class);
        Criteria criteria = createCriteria
                .add(Restrictions.eq(Meldung.EXTERNE_AUFTRAGSNR_FIELD, externeAuftragsnummer)).addOrder(
                        Order.desc(MwfEntity.ID_FIELD));
        @SuppressWarnings("unchecked")
        List<Meldung<?>> result = criteria.list();
        return result;
    }

    @Override
    public Pair<String, String> findLastMeldungsTyp(String externeAuftragsnummer, String vertragsnummer) {
        Preconditions.checkNotNull(externeAuftragsnummer);
        Preconditions.checkNotNull(vertragsnummer);

        Session session = sessionFactory.getCurrentSession();
        SQLQuery query = session.createSQLQuery("SELECT M.MELDUNGSTYP, AP.ZUSTIMMUNG_PROVIDER_WECHSEL "
                + "FROM T_MWF_MELDUNG M "
                + "LEFT OUTER JOIN T_MWF_ABGEBENDERPROVIDER AP ON M.ABGEBENDER_PROVIDER_ID = AP.ID "
                + "WHERE EXT_AUFTRAGS_NR = :extAuftragsNr "
                + "AND VERTRAGS_NUMMER = :vertragsNummer "
                + "AND MELDUNGSTYP IN ('AKM-PV', 'RUEM-PV', 'VZM-PV', 'ABM-PV', 'ABBM-PV') "
                + "ORDER BY VERSAND_ZEITSTEMPEL DESC");
        query.setParameter("extAuftragsNr", externeAuftragsnummer);
        query.setParameter("vertragsNummer", vertragsnummer);
        final List resultList = query.list();

        if (resultList.size() == 0) {
            return null;
        }

        // Letzten Meldungstyp ermitteln
        String lastMeldungsTyp = (String) ((Object[]) resultList.get(0))[0];

        // PV Zustimmung ermitteln
        String zustimmungPvWechsel = null;
        for (Object result : resultList) {
            Object[] s = (Object[]) result;
            if ("RUEM-PV".equals(s[0])) {
                zustimmungPvWechsel = (String) s[1];
                break;
            }
        }
        return new Pair<>(lastMeldungsTyp, zustimmungPvWechsel);
    }

    @Override
    public List<MnetWitaRequest> findAllRequests(String externeAuftragsnummer) {
        return findAllRequests(externeAuftragsnummer, MnetWitaRequest.class);
    }

    @Override
    public List<Meldung<?>> findMeldungenToBeSentToBsi() {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(Meldung.class);
        criteria.add(
                or(eq(SENT_TO_BSI_FIELD, NOT_SENT_TO_BSI),
                        or(eq(SENT_TO_BSI_FIELD, ERROR_SEND_TO_BSI), isNull(SENT_TO_BSI_FIELD)))
        )
                .addOrder(Order.desc(MwfEntity.ID_FIELD)).setMaxResults(1000);
        @SuppressWarnings("unchecked")
        List<Meldung<?>> result = criteria.list();
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Meldung<?>> findPvMeldungenNotToBeSendToBsi() {
        // @formatter:off
        String hql = "select m from " + Meldung.class.getName() + " m " + "where m." + SENT_TO_BSI_FIELD
                + " = '" + ERROR_SEND_TO_BSI + "' and m.class in ("
                + AnkuendigungsMeldungPv.class.getName() + ","
                + RueckMeldungPv.class.getName() + ","
                + AuftragsBestaetigungsMeldungPv.class.getName() + ","
                + AbbruchMeldungPv.class.getName() + ","
                + ErledigtMeldungPv.class.getName()
                + ") order by " + Meldung.EXTERNE_AUFTRAGSNR_FIELD + "," + MwfEntity.ID_FIELD;
        // @formatter:on
        return find(hql);
    }

    @Override
    public List<MnetWitaRequest> findRequestsToBeSentToBsi() {
        Session session = sessionFactory.getCurrentSession();
        Criteria createCriteria = session.createCriteria(MnetWitaRequest.class);
        // @formatter:off
        Criteria criteria = createCriteria
                .add(or(eq(SENT_TO_BSI_FIELD, NOT_SENT_TO_BSI),
                        or(eq(SENT_TO_BSI_FIELD, ERROR_SEND_TO_BSI), isNull(SENT_TO_BSI_FIELD))))
                .add(isNotNull(SENT_AT))
                .addOrder(Order.desc(MwfEntity.ID_FIELD))
                .setMaxResults(1000);
        // @formatter:on
        @SuppressWarnings("unchecked")
        List<MnetWitaRequest> result = criteria.list();
        return result;
    }

    @Override
    public List<MnetWitaRequest> findDelayedRequestsToBeSentToBsi(final LocalDateTime maxCreationDate, final int maxCountOfRequests) {
        Session session = sessionFactory.getCurrentSession();
        Criteria createCriteria = session.createCriteria(MnetWitaRequest.class);
        // @formatter:off
        Criteria criteria = createCriteria
                .add(isNull(DELAY_SENT_TO_BSI_FIELD))
                .add(isNull(SENT_TO_BSI_FIELD))
                .add(isNull(SENT_AT))
                .add(eq(REQUEST_STORNIERT, Boolean.FALSE))
                .add(Restrictions.lt(MWF_CREATION_DATE, maxCreationDate != null ? Date.from(maxCreationDate.atZone(ZoneId.systemDefault()).toInstant()) : null ))
                .addOrder(Order.desc(MwfEntity.ID_FIELD))
                .setMaxResults(maxCountOfRequests);
        // @formatter:on
        @SuppressWarnings("unchecked")
        List<MnetWitaRequest> result = criteria.list();
        return result;
    }

    @Override
    public <T extends MnetWitaRequest> List<T> findAllRequests(final String externeAuftragsnummer, final Class<T> requestClass) {
        Preconditions.checkNotNull(externeAuftragsnummer);
        Session session = sessionFactory.getCurrentSession();
        Criteria createCriteria = session.createCriteria(requestClass);
        // @formatter:off
        Criteria criteria = createCriteria.add(
                Restrictions.eq(MnetWitaRequest.EXTERNE_AUFTRAGSNR_FIELD, externeAuftragsnummer))
                .addOrder(Order.desc(MwfEntity.ID_FIELD));
        // @formatter:on
        @SuppressWarnings("unchecked")
        List<T> requests = criteria.list();
        return requests;
    }

    @Override
    public List<Auftrag> getAuftragRequestsForAuftragId(Long auftragId) {
        String hql = "select a from " + Auftrag.class.getName() + " a, " + CBVorgang.class.getName() + " cbv "
                + "where a.cbVorgangId = cbv.id" + "  and cbv.auftragId = :auftragId";

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql);
        query.setParameter("auftragId", auftragId);

        return query.list();
    }

    @Override
    public MnetWitaRequest findRequest4Anlage(Anlage anlage) {
        String hql = "select request from " + MnetWitaRequest.class.getName() + " request "
                + "inner join request.geschaeftsfall.anlagen as anlage where anlage.id = :anlageId";

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql);
        query.setParameter("anlageId", anlage.getId());

        List<MnetWitaRequest> requests = query.list();
        return Iterables.getOnlyElement(requests, null);
    }

    @Override
    public Meldung<?> findMeldung4Anlage(Anlage anlage) {
        String hql = "select meldung from Meldung meldung inner join meldung.anlagen as anlage where anlage.id = :anlageId";

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql);
        query.setParameter("anlageId", anlage.getId());

        List<Meldung<?>> meldungen = query.list();
        return Iterables.getOnlyElement(meldungen, null);
    }

    @Override
    public List<Anlage> findUnArchivedAnlagen() {
        Session session = sessionFactory.getCurrentSession();
        String hql = "select anlage from Meldung meldung inner join meldung.anlagen as anlage where "
                + "anlage." + Anlage.ARCHIVE_SCHLUESSEL + " is null " + " and anlage."
                + Anlage.ARCHIVING_CANCEL_REASON + " is null";
        return session.createQuery(hql).list();
    }

    @Override
    public MnetWitaRequest findUnsentRequest(final Long witaCbVorgangId) {
        Session session = sessionFactory.getCurrentSession();
        Criteria createCriteria = session.createCriteria(MnetWitaRequest.class);
        Criteria criteria = createCriteria.add(Restrictions.eq(MnetWitaRequest.CB_VORGANG_ID, witaCbVorgangId))
                .add(Restrictions.isNull(MnetWitaRequest.SENT_AT))
                .add(Restrictions.eq(MnetWitaRequest.REQUEST_STORNIERT, false));
        @SuppressWarnings("unchecked")
        List<MnetWitaRequest> result = criteria.list();
        return Iterables.getFirst(result, null);
    }

    @Override
    public List<Meldung<?>> findMeldungenForSmsVersand() {
        Session session = sessionFactory.getCurrentSession();
        Criteria createCriteria = session.createCriteria(Meldung.class);
        Criteria criteria = createCriteria
                .add(Restrictions.eq(Meldung.SMS_STATUS_FIELD, SmsStatus.OFFEN));
        @SuppressWarnings("unchecked")
        List<Meldung<?>> result = criteria.list();
        return result;
    }

    @Override
    public List<Meldung<?>> findMessagesForEmailing() {
        Session session = sessionFactory.getCurrentSession();
        Criteria createCriteria = session.createCriteria(Meldung.class);
        Criteria criteria = createCriteria
                .add(Restrictions.eq(Meldung.EMAIL_STATUS_FIELD, EmailStatus.OFFEN));
        @SuppressWarnings("unchecked")
        List<Meldung<?>> result = criteria.list();
        return result;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
