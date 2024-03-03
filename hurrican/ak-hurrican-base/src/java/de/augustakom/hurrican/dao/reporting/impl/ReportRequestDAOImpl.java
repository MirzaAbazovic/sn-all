/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.02.2007 08:44:38
 */
package de.augustakom.hurrican.dao.reporting.impl;

import java.util.*;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.DateType;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.ObjectTools;
import de.augustakom.hurrican.dao.reporting.ReportRequestDAO;
import de.augustakom.hurrican.model.reporting.Report;
import de.augustakom.hurrican.model.reporting.ReportData;
import de.augustakom.hurrican.model.reporting.ReportRequest;
import de.augustakom.hurrican.model.reporting.view.ReportRequestView;
import de.augustakom.hurrican.service.reporting.ReportService;

/**
 * Hibernate DAO-Implementierung fuer <code>ReportRequestDAO</code>
 *
 *
 */
public class ReportRequestDAOImpl extends Hibernate4DAOImpl implements ReportRequestDAO {

    @Autowired
    @Qualifier("reporting.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public List<ReportRequestView> findAllRequestsByKundeNoAndAuftragId(Long kundeNo, Long auftragId, Date filterBeginn, Date filterEnde) {
        return findReportRequests(kundeNo, auftragId, filterBeginn, filterEnde, Boolean.FALSE);
    }

    private List<ReportRequestView> findReportRequests(Long kundeNo, Long auftragId, Date filterBeginn, Date filterEnde, Boolean serienbrief) {
        List<Object> params = new ArrayList<Object>();
        List<Type> types = new ArrayList<Type>();
        StringBuilder sql = new StringBuilder("select rep.id as report_id, rep.name as report_name, rep.userw, ");
        sql.append(" rep.description, req.id, req.kunde__No, req.order__No, req.auftrag_Id, ");
        sql.append(" req.request_From, req.request_At, req.request_Finished_At, req.report_Downloaded_At, req.generated_file, ");
        sql.append(" req.error, req.buendel_No, req.archived, req.rfa, pr.name ");
        sql.append(" from t_report_request req ");
        sql.append(" left join t_report rep on req.rep_id = rep.id ");
        sql.append(" left join t_report_reason pr on req.print_reason = pr.id ");
        sql.append(" where (req.error NOT LIKE  '%Test%' or req.error IS NULL)");
        if (auftragId != null) {
            sql.append(" and req.auftrag_Id = ?");
            params.add(auftragId);
            types.add(new LongType());
        }
        if (kundeNo != null) {
            sql.append(" and req.kunde__No = ?");
            params.add(kundeNo);
            types.add(new LongType());
        }
        if ((kundeNo == null) && (auftragId == null) && ((serienbrief == null) || !serienbrief)) {
            sql.append(" and req.report_Downloaded_At is null");
        }
        if (filterBeginn != null) {
            sql.append(" and req.request_At >= ?");
            params.add(filterBeginn);
            types.add(new DateType());
        }
        if (filterEnde != null) {
            sql.append(" and req.request_At < ?");
            Date date = DateTools.changeDate(filterEnde, Calendar.DAY_OF_MONTH, 1);
            params.add(date);
            types.add(new DateType());
        }
        if ((serienbrief != null) && serienbrief) {
            sql.append(" and req.buendel_No is not null");
        }
        sql.append(" order by req.ID");

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameters(params.toArray(), types.toArray(new Type[types.size()]));
        List<Object[]> result = sqlQuery.list();
        List<ReportRequestView> retVal = new ArrayList<ReportRequestView>();
        if (result != null) {
            for (Object[] values : result) {
                int columnIndex = 0;
                ReportRequestView view = new ReportRequestView();
                view.setReportId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setReportName(ObjectTools.getStringSilent(values, columnIndex++));
                view.setReportUserw(ObjectTools.getStringSilent(values, columnIndex++));
                view.setReportDescription(ObjectTools.getStringSilent(values, columnIndex++));
                view.setRequestId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setKundeNo(ObjectTools.getLongSilent(values, columnIndex++));
                view.setOrderNoOrig(ObjectTools.getLongSilent(values, columnIndex++));
                view.setAuftragId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setRequestFrom(ObjectTools.getStringSilent(values, columnIndex++));
                view.setRequestAt(ObjectTools.getDateSilent(values, columnIndex++));
                view.setRequestFinishedAt(ObjectTools.getDateSilent(values, columnIndex++));
                view.setReportDownloadedAt(ObjectTools.getDateSilent(values, columnIndex++));
                view.setFile(ObjectTools.getStringSilent(values, columnIndex++));
                view.setError(ObjectTools.getStringSilent(values, columnIndex++));
                view.setBuendelNo(ObjectTools.getIntegerSilent(values, columnIndex++));
                view.setReportArchivedAt(ObjectTools.getDateSilent(values, columnIndex++));
                view.setReportRFA(ObjectTools.getBooleanSilent(values, columnIndex++));
                view.setPrintReason(ObjectTools.getStringSilent(values, columnIndex++));
                retVal.add(view);
            }
        }
        return (CollectionTools.isNotEmpty(retVal)) ? retVal : null;
    }

    @Override
    public List<ReportRequest> findReports2Delete(final Integer days) {
        Session session = sessionFactory.getCurrentSession();
        Date date = DateTools.changeDate(DateTools.getActualSQLDate(), Calendar.DAY_OF_MONTH, -days.intValue());
        date = DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
        Criteria criteria = session.createCriteria(ReportRequest.class);
        criteria.add(Restrictions.isNotNull("file"));
        criteria.add(Restrictions.not(Restrictions.like("file", ReportService.REPORT_ARCHIV, MatchMode.START)));
        criteria.add(Restrictions.lt("requestFinishedAt", date));

        List<ReportRequest> result = criteria.list();
        return result;
    }

    @Override
    public List<Long> findReportData2Delete(final Integer days) {
        if (days == null) {
            return null;
        }
        final StringBuilder hql = new StringBuilder("select distinct req.id from ");
        hql.append(ReportRequest.class.getName()).append(" req, ");
        hql.append(ReportData.class.getName()).append(" data ");
        hql.append(" where req.id = data.requestId");
        hql.append(" and req.requestAt < :Date");

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(hql.toString());

        Date date = DateTools.changeDate(DateTools.getActualSQLDate(), Calendar.DAY_OF_MONTH, -days.intValue());
        date = DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
        q.setDate("Date", date);

        List result = q.list();
        if (CollectionTools.isNotEmpty(result)) {
            return result;
        }
        return null;
    }

    @Override
    public Integer findNewBuendelNo() {
        final StringBuilder hql = new StringBuilder("select max(req.buendelNo) from ");
        hql.append(ReportRequest.class.getName()).append(" req ");

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(hql.toString());

        @SuppressWarnings("unchecked")
        List<Object> result = q.list();
        if (CollectionTools.isNotEmpty(result) && (result.size() == 1)) {
            if ((result.get(0) != null) && (result.get(0) instanceof Integer)) {
                return ((Integer) result.get(0)) + 1;
            }
            else if (result.get(0) == null) {
                return 1;
            }
        }
        return null;
    }

    @Override
    public List<ReportRequest> findReportsNotReady4BuendelNo(final Integer buendelNo) {
        final StringBuilder hql = new StringBuilder("from " + ReportRequest.class.getName() + " req ");
        hql.append(" where req.buendelNo = :BUENDELNO");
        hql.append(" and req.requestFinishedAt IS NULL");

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(hql.toString());
        q.setInteger("BUENDELNO", buendelNo);

        return q.list();
    }

    @Override
    public List<ReportRequestView> findAllSerienbriefe() {
        return findReportRequests(null, null, null, null, Boolean.TRUE);
    }

    @Override
    public List<ReportRequest> findAllRequests4Reportgruppe(final Report rep, final Long kundeNo, final Long auftragId) {
        if ((rep == null) || (kundeNo == null)) {
            return null;
        }

        final StringBuilder hql = new StringBuilder("select req.id, req.kundeNo, req.orderNoOrig, req.auftragId, ");
        hql.append(" req.requestFrom, req.requestAt, req.requestFinishedAt, req.reportDownloadedAt, req.file, ");
        hql.append(" req.error, req.buendelNo, req.rfa, req.reportArchivedAt, req.reportMovedToArchive, ");
        hql.append(" req.requestType, req.printReasonId from ");
        hql.append(ReportRequest.class.getName()).append(" req, ");
        hql.append(Report.class.getName()).append(" rep ");
        hql.append(" where rep.id = req.repId");
        if (rep.getReportGruppeId() != null) {
            hql.append(" and rep.reportGruppeId = :gruppeId");
        }
        else {
            hql.append(" and req.repId = :repId");
        }
        hql.append(" and req.kundeNo = :kundeNo");
        if (auftragId != null) {
            hql.append(" and req.auftragId = :auftragId");
        }
        hql.append(" order by req.requestAt desc");

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(hql.toString());
        q.setLong("kundeNo", kundeNo);
        if (rep.getReportGruppeId() != null) {
            q.setLong("gruppeId", rep.getReportGruppeId());
        }
        else {
            q.setLong("repId", rep.getId());
        }
        if (auftragId != null) {
            q.setLong("auftragId", auftragId);
        }

        @SuppressWarnings("unchecked")
        List<Object[]> result = q.list();
        if (CollectionTools.isNotEmpty(result)) {
            List<ReportRequest> retVal = new ArrayList<ReportRequest>();
            for (int i = 0; i < result.size(); i++) {
                if (result.get(i) != null) {
                    Object[] values = result.get(i);
                    ReportRequest request = new ReportRequest();
                    request.setId(ObjectTools.getLongSilent(values, 0));
                    request.setKundeNo(ObjectTools.getLongSilent(values, 1));
                    request.setOrderNoOrig(ObjectTools.getLongSilent(values, 2));
                    request.setAuftragId(ObjectTools.getLongSilent(values, 3));
                    request.setRequestFrom(ObjectTools.getStringSilent(values, 4));
                    request.setRequestAt(ObjectTools.getDateSilent(values, 5));
                    request.setRequestFinishedAt(ObjectTools.getDateSilent(values, 6));
                    request.setReportDownloadedAt(ObjectTools.getDateSilent(values, 7));
                    request.setFile(ObjectTools.getStringSilent(values, 8));
                    request.setError(ObjectTools.getStringSilent(values, 9));
                    request.setBuendelNo(ObjectTools.getIntegerSilent(values, 10));
                    request.setRfa(ObjectTools.getBooleanSilent(values, 11));
                    request.setReportArchivedAt(ObjectTools.getDateSilent(values, 12));
                    request.setReportMovedToArchive(ObjectTools.getDateSilent(values, 13));
                    request.setRequestType(ObjectTools.getStringSilent(values, 14));
                    request.setPrintReasonId(ObjectTools.getLongSilent(values, 15));
                    retVal.add(request);
                }
            }
            return retVal;
        }
        return null;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}

