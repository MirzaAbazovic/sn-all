package de.mnet.wbci.dao.impl;

import static de.mnet.wbci.model.AutomationTask.*;
import static de.mnet.wbci.model.CarrierCode.*;
import static de.mnet.wbci.model.WbciGeschaeftsfallStatus.*;

import java.math.*;
import java.time.*;
import java.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.model.cc.KuendigungCheck;
import de.mnet.wbci.dao.VorabstimmungIdsByBillingOrderNoCriteria;
import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.model.Antwortfrist;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.CarrierRole;
import de.mnet.wbci.model.ErledigtmeldungStornoAuf;
import de.mnet.wbci.model.ErledigtmeldungTerminverschiebung;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.OverdueAbmPvVO;
import de.mnet.wbci.model.PreAgreementVO;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.Technologie;
import de.mnet.wbci.model.UebernahmeRessourceMeldung;
import de.mnet.wbci.model.WbciEntity;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.WbciRequestStatus;

/**
 * WBCI Dao class for accessing all WBCI entities
 */
public class WbciDaoImpl extends Hibernate4DAOImpl implements WbciDao {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Autowired
    private PreAgreementVOTransformer preagreementVOTransformer;

    @Autowired
    private OverdueAbmPvVOTransformer overdueAbmPvVOTransformer;

    private static final Logger LOGGER = Logger.getLogger(WbciDaoImpl.class);

    private static final String SEQ_NEXT_VAL_PREAGREEMENT_ID_VA = "SELECT SEQ_VORABSTIMMUNGSID_VA.NEXTVAL FROM DUAL";
    private static final String SEQ_NEXT_VAL_PREAGREEMENT_ID_STORNO = "SELECT SEQ_VORABSTIMMUNGSID_STORNO.NEXTVAL FROM DUAL";
    private static final String SEQ_NEXT_VAL_PREAGREEMENT_ID_TV = "SELECT SEQ_VORABSTIMMUNGSID_TV.NEXTVAL FROM DUAL";

    private final SqlHelper sqlHelper = new SqlHelper();

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getNextSequenceValue(RequestTyp requestTyp) {
        String sequenceSql;
        switch (requestTyp) {
            case VA:
                sequenceSql = SEQ_NEXT_VAL_PREAGREEMENT_ID_VA;
                break;
            case STR_AEN_ABG:
            case STR_AEN_AUF:
            case STR_AUFH_AUF:
            case STR_AUFH_ABG:
                sequenceSql = SEQ_NEXT_VAL_PREAGREEMENT_ID_STORNO;
                break;
            case TV:
                sequenceSql = SEQ_NEXT_VAL_PREAGREEMENT_ID_TV;
                break;
            default:
                throw new IllegalArgumentException(String.format("Unsupported request type '%s'.", requestTyp));
        }

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sequenceSql);
        return ((BigDecimal) sqlQuery.uniqueResult()).longValue();
    }

    @Override
    public <T extends WbciRequest> List<T> findWbciRequestByType(String vorabstimmungsId, Class<T> requestTyp) {
        return findWbciRequestByType(vorabstimmungsId, requestTyp, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends WbciRequest> List<T> findWbciRequestByType(String vorabstimmungsId, Class<T> requestTyp, LockMode lockMode) {
        DetachedCriteria criteria = DetachedCriteria.forClass(requestTyp);
        DetachedCriteria gfCriteria = criteria.createCriteria(WbciRequest.WBCI_GESCHAEFTSFALL);
        gfCriteria.add(Restrictions.eq(WbciGeschaeftsfall.VORABSTIMMUNGS_ID, vorabstimmungsId));
        if (lockMode != null) {
            criteria.setLockMode(lockMode);
        }
        return (List<T>) executeForCriteria(criteria);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends WbciRequest> T findLastWbciRequestByType(String vorabstimmungsId, Class<T> requestTyp) {
        DetachedCriteria criteria = DetachedCriteria.forClass(requestTyp);
        DetachedCriteria gfCriteria = criteria.createCriteria(WbciRequest.WBCI_GESCHAEFTSFALL);
        gfCriteria.add(Restrictions.eq(WbciGeschaeftsfall.VORABSTIMMUNGS_ID, vorabstimmungsId));
        criteria.addOrder(Order.desc(WbciRequest.CREATION_DATE));
        final List<T> list = (List<T>) executeForCriteria(criteria);
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<WbciRequest> findWbciRequestByChangeId(String vorabstimmungsId, String changeId) {
        DetachedCriteria criteria = DetachedCriteria.forClass(WbciRequest.class);
        DetachedCriteria gfCriteria = criteria.createCriteria(WbciRequest.WBCI_GESCHAEFTSFALL);
        gfCriteria.add(Restrictions.eq(WbciGeschaeftsfall.VORABSTIMMUNGS_ID, vorabstimmungsId));
        criteria.add(Restrictions.eq(WbciRequest.CHANGE_ID, changeId));
        criteria.addOrder(Order.desc(WbciRequest.CREATION_DATE));
        return (List<WbciRequest>) executeForCriteria(criteria);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends WbciRequest> T findWbciRequestByChangeId(String vorabstimmungsId, String changeId, Class<T> requestTyp) {
        DetachedCriteria criteria = DetachedCriteria.forClass(requestTyp);
        DetachedCriteria gfCriteria = criteria.createCriteria(WbciRequest.WBCI_GESCHAEFTSFALL);
        gfCriteria.add(Restrictions.eq(WbciGeschaeftsfall.VORABSTIMMUNGS_ID, vorabstimmungsId));
        criteria.add(Restrictions.eq(WbciRequest.CHANGE_ID, changeId));
        return (T) executeUniqueResultForCriteria(criteria);
    }

    @Override
    public WbciGeschaeftsfall findWbciGeschaeftsfall(String vorabstimmungsId) {
        DetachedCriteria criteria = DetachedCriteria.forClass(WbciGeschaeftsfall.class);
        criteria.add(Restrictions.eq(WbciGeschaeftsfall.VORABSTIMMUNGS_ID, vorabstimmungsId));
        return (WbciGeschaeftsfall) executeUniqueResultForCriteria(criteria);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Meldung> findMeldungenForVaId(String vorabstimmungsId) {
        DetachedCriteria criteria = DetachedCriteria.forClass(Meldung.class);
        DetachedCriteria gfCriteria = criteria.createCriteria(Meldung.WBCI_GESCHAEFTSFALL);
        gfCriteria.add(Restrictions.eq(WbciGeschaeftsfall.VORABSTIMMUNGS_ID, vorabstimmungsId));
        criteria.addOrder(Order.desc(Meldung.ID_FIELD));
        return (List<Meldung>) executeForCriteria(criteria);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <M extends Meldung> List<M> findMeldungenForVaIdAndMeldungClass(String vorabstimmungsId, Class<M> classDef) {
        DetachedCriteria criteria = DetachedCriteria.forClass(classDef);
        DetachedCriteria gfCriteria = criteria.createCriteria(Meldung.WBCI_GESCHAEFTSFALL);
        gfCriteria.add(Restrictions.eq(WbciGeschaeftsfall.VORABSTIMMUNGS_ID, vorabstimmungsId));
        criteria.addOrder(Order.desc(Meldung.ID_FIELD));
        return (List<M>) executeForCriteria(criteria);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Antwortfrist findAntwortfrist(RequestTyp typ, WbciRequestStatus wbciRequestStatus) {
        DetachedCriteria criteria = DetachedCriteria.forClass(Antwortfrist.class);
        criteria.add(Restrictions.eq(Antwortfrist.TYP, typ));
        criteria.add(Restrictions.eq(Antwortfrist.STATUS, wbciRequestStatus));
        return (Antwortfrist) executeUniqueResultForCriteria(criteria);
    }

    private List<?> executeForCriteria(final DetachedCriteria criteria) {
        return criteria.getExecutableCriteria(sessionFactory.getCurrentSession()).list();
    }

    private Object executeUniqueResultForCriteria(final DetachedCriteria criteria) {
        return criteria.getExecutableCriteria(sessionFactory.getCurrentSession()).uniqueResult();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> findVorabstimmungIdsByBillingOrderNoOrig(VorabstimmungIdsByBillingOrderNoCriteria criteria) {
        DetachedCriteria vaCriteria = DetachedCriteria.forClass(criteria.getWbciRequestClassType(), "request");
        vaCriteria.createAlias("request." + WbciRequest.WBCI_GESCHAEFTSFALL, "gf");
        if (!criteria.getMatchingRequestStatuses().isEmpty()) {
            vaCriteria.add(Restrictions.in(WbciRequest.REQUEST_STATUS, criteria.getMatchingRequestStatuses()));
        }
        if (!criteria.getMatchingGeschaeftsfallStatuses().isEmpty()) {
            vaCriteria.add(Restrictions.in("gf." + WbciGeschaeftsfall.STATUS, criteria.getMatchingGeschaeftsfallStatuses()));
        }
        vaCriteria.add(Restrictions.eq("gf." + WbciGeschaeftsfall.BILLING_ORDER_NO_ORIG, criteria.getMatchingBillingOrderNoOrig()));
        vaCriteria.setProjection(Projections.projectionList()
                .add(Projections.property("gf." + WbciGeschaeftsfall.VORABSTIMMUNGS_ID))
        );
        return (List<String>) executeForCriteria(vaCriteria);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Long> findScheduledWbciRequestIds() {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(
                "select r.id from de.mnet.wbci.model.WbciRequest r where r.sendAfter <= current_date() " +
                        "and r.processedAt is null " +
                        "order by r.sendAfter asc"
        );
        return query.list();
    }

    @Override
    public <T extends WbciEntity> void delete(T entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }

    @Override
    public KuendigungCheck findKuendigungCheckForOeNoOrig(Long oeNoOrig) {
        List<KuendigungCheck> result = find("from KuendigungCheck kc where kc.oeNoOrig = ?", oeNoOrig);
        return (result != null && result.size() == 1) ? result.get(0) : null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<WbciGeschaeftsfall> findElapsedPreagreements(final int maxResults) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(
                "select gf from de.mnet.wbci.model.WbciGeschaeftsfall gf " +
                        " where " +
                        " gf.status in (de.mnet.wbci.model.WbciGeschaeftsfallStatus.ACTIVE, de.mnet.wbci.model.WbciGeschaeftsfallStatus.PASSIVE) " +
                        " and gf.klaerfall != true " +
                        " and gf.wechseltermin <= current_date() - 3 " + // with wechseltermin 3 days ago or older
                        " and not exists(select gf2.id from WbciGeschaeftsfall gf2 " +
                        "                inner join gf2.automationTasks automationTask " +
                        "                where automationTask.status != :status" +
                        "                      and automationTask.automatable = true" +
                        "                      and gf2.id = gf.id)"
        );
        query.setParameter("status", AutomationStatus.COMPLETED);
        if (maxResults > 0) {
            query.setMaxResults(maxResults);
        }
        return query.list();
    }

    @Override
    public boolean hasFaultyAutomationTasks(final String vorabstimmungsId) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(
                "select gf from WbciGeschaeftsfall gf " +
                        "       inner join gf.automationTasks automationTask " +
                        "       where automationTask.status != :status" +
                        "           and automationTask.automatable = true" +
                        "           and gf.vorabstimmungsId = :vorabstimmungsId"
        );
        query.setParameter("status", AutomationStatus.COMPLETED);
        query.setParameter("vorabstimmungsId", vorabstimmungsId);
        return !CollectionUtils.isEmpty(query.list());
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<PreAgreementVO> findMostRecentPreagreements(final CarrierRole mnetCarrierRole) {
        return findMostRecentPreagreements(mnetCarrierRole, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<PreAgreementVO> findMostRecentPreagreements(CarrierRole mnetCarrierRole, String singlePreAgreementIdToLoad) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createSQLQuery(sqlHelper.getMostRecentPreagreementsQuery(mnetCarrierRole, singlePreAgreementIdToLoad));
        query.setResultTransformer(preagreementVOTransformer);
        return query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<OverdueAbmPvVO> findPreagreementsWithOverdueAbmPv(final LocalDate withWechselTerminBefore) {
        Session session = sessionFactory.getCurrentSession();
        String nativeSql = sqlHelper.findPreagreementsWithOverdueAbmPvQuery();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Execute the native SQL:\n" + nativeSql);
        }
        Query query = session.createSQLQuery(nativeSql);
        query.setDate(SqlHelper.WECHSELTERMIN_PLACEHOLDER, Date.from(withWechselTerminBefore.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        query.setResultTransformer(overdueAbmPvVOTransformer);
        return query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<WbciGeschaeftsfall> findActiveGfByOrderNoOrig(Long billingOrderNoOrig,
            boolean interpretNewVaStatusesAsActive) {

        List<WbciGeschaeftsfallStatus> nonActiveStatuses = new ArrayList<>();
        nonActiveStatuses.add(COMPLETE);

        if (!interpretNewVaStatusesAsActive) {
            // a Vorabstimmung is not deemed non-active when in state NEW_VA
            nonActiveStatuses.add(WbciGeschaeftsfallStatus.NEW_VA);
            nonActiveStatuses.add(WbciGeschaeftsfallStatus.NEW_VA_EXPIRED);
        }

        DetachedCriteria criteria = DetachedCriteria.forClass(WbciGeschaeftsfall.class);
        criteria.add(Restrictions.eq(WbciGeschaeftsfall.BILLING_ORDER_NO_ORIG, billingOrderNoOrig));
        criteria.add(Restrictions.not(Restrictions.in(WbciGeschaeftsfall.STATUS, nonActiveStatuses)));
        return (List<WbciGeschaeftsfall>) executeForCriteria(criteria);
    }


    @SuppressWarnings("unchecked")
    @Override
    public List<WbciGeschaeftsfall> findCompleteGfByOrderNoOrig(Long billingOrderNoOrig) {
        DetachedCriteria criteria = DetachedCriteria.forClass(WbciGeschaeftsfall.class);
        criteria.add(Restrictions.eq(WbciGeschaeftsfall.BILLING_ORDER_NO_ORIG, billingOrderNoOrig));
        criteria.add(Restrictions.eq(WbciGeschaeftsfall.STATUS, COMPLETE));
        return (List<WbciGeschaeftsfall>) executeForCriteria(criteria);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<WbciGeschaeftsfall> findGfByOrderNoOrig(final Long billingOrderNoOrig) {
        Session session = sessionFactory.getCurrentSession();
        String queryStr = "select gf from de.mnet.wbci.model.WbciGeschaeftsfall gf where " +
                " gf." + WbciGeschaeftsfall.BILLING_ORDER_NO_ORIG + " = %s " +
                " or %s in elements (gf." + WbciGeschaeftsfall.NON_BILLING_RELEVANT_ORDER_NO_ORIGS + ")";
        Query query = session.createQuery(String.format(queryStr, billingOrderNoOrig, billingOrderNoOrig));
        return query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends WbciEntity> T byIdWithLockMode(Long id, LockMode lockMode, Class<T> clazz) {
        DetachedCriteria criteria = DetachedCriteria.forClass(clazz);
        criteria.add(Restrictions.eq("id", id));
        criteria.setLockMode(lockMode);
        return (T) executeUniqueResultForCriteria(criteria);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<UebernahmeRessourceMeldung> findAutomatableAkmTRsForWitaProcesing(final List<Technologie> mnetTechnologies) {
        Session session = sessionFactory.getCurrentSession();
        String nativeSql = SqlHelper.findAutomateableAkmTRsForWitaProcessingSQL(mnetTechnologies);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Execute the native SQL:\n" + nativeSql);
        }

        Query query = session.createSQLQuery(nativeSql)
                .addEntity(UebernahmeRessourceMeldung.class);
        return query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<UebernahmeRessourceMeldung> findAutomatableIncomingAkmTRsForWitaProcesing() {
        Session session = sessionFactory.getCurrentSession();
        String nativeSql = SqlHelper.findAutomateableIncomingAkmTRsForWitaProcessingSQL();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Execute the native SQL:\n" + nativeSql);
        }

        Query query = session.createSQLQuery(nativeSql)
                .addEntity(UebernahmeRessourceMeldung.class);
        return query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ErledigtmeldungTerminverschiebung> findAutomateableTvErlmsForWitaProcessing(final List<Technologie> mnetTechnologies) {
        Session session = sessionFactory.getCurrentSession();
        String nativeSql = SqlHelper.findAutomateableTvErlmsForWitaProcessingSQL(mnetTechnologies);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Execute the native SQL:\n" + nativeSql);
        }

        Query query = session.createSQLQuery(nativeSql)
                .addEntity(ErledigtmeldungTerminverschiebung.class);
        return query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ErledigtmeldungStornoAuf> findAutomateableStrAufhErlmsForWitaProcessing(final List<Technologie> mnetTechnologies) {
        Session session = sessionFactory.getCurrentSession();
        String nativeSql = SqlHelper.findAutomateableStrAufhErlmsForWitaProcessingSQL(mnetTechnologies);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Execute the native SQL:\n" + nativeSql);
        }

        Query query = session.createSQLQuery(nativeSql)
                .addEntity(ErledigtmeldungStornoAuf.class);
        return query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ErledigtmeldungStornoAuf> findAutomateableStrAufhErlmsDonatingProcessing() {
        Session session = sessionFactory.getCurrentSession();
        String nativeSql = SqlHelper.findAutomateableStrAufhErlmsDonatingProcessingSQL();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Execute the native SQL:\n" + nativeSql);
        }

        Query query = session.createSQLQuery(nativeSql)
                .addEntity(ErledigtmeldungStornoAuf.class);
        return query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<UebernahmeRessourceMeldung> findOverdueAkmTrsNearToWechseltermin(final int workingDaysBeforeWechseltermin, final CarrierCode aufnehmenderEKP, final List<Technologie> mnetTechnologies) {
        final WbciRequestStatus requestStatus = MNET.equals(aufnehmenderEKP) ? WbciRequestStatus.AKM_TR_VERSENDET : WbciRequestStatus.AKM_TR_EMPFANGEN;

        Session session = sessionFactory.getCurrentSession();
        String nativeSql = SqlHelper.findOverdueAkmTRsSQL(requestStatus, mnetTechnologies);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Execute the native SQL:\n" + nativeSql);
        }

        Query query = session.createSQLQuery(nativeSql)
                .addEntity(UebernahmeRessourceMeldung.class)
                .setDate(SqlHelper.WECHSELTERMIN_PLACEHOLDER, Date.from(DateCalculationHelper.getDateInWorkingDaysFromNow(workingDaysBeforeWechseltermin).atZone(ZoneId.systemDefault()).toInstant()));
        return query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<WbciGeschaeftsfall> findPreagreementsWithStatusAndWechselTerminBefore(WbciGeschaeftsfallStatus status, LocalDate wechselTerminBefore) {
        DetachedCriteria criteria = DetachedCriteria.forClass(WbciGeschaeftsfall.class);
        criteria.add(Restrictions.eq(WbciGeschaeftsfall.STATUS, status));
        criteria.add(Restrictions.lt(WbciGeschaeftsfall.WECHSELTERMIN, wechselTerminBefore));

        return (List<WbciGeschaeftsfall>) executeForCriteria(criteria);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> findPreagreementsWithAutomatableRuemVa() {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createSQLQuery(sqlHelper.findPreagreementsWithAutomatableRuemVa());
        return query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<WbciGeschaeftsfall> findAutomateableOutgoingRuemVaForKuendigung() {
        Session session = sessionFactory.getCurrentSession();
        String nativeSql = SqlHelper.findAutomateableOutgoingRuemVaForKuendigungSQL();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Execute the native SQL:\n" + nativeSql);
        }

        Query query = session.createSQLQuery(nativeSql).addEntity(WbciGeschaeftsfall.class);
        return query.list();
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
