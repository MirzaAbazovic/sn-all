/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.09.2011 11:57:12
 */
package de.augustakom.hurrican.dao.cc.impl;

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
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.ObjectTools;
import de.augustakom.common.tools.lang.SqlBuilderTools;
import de.augustakom.hurrican.dao.cc.IPAddressDAO;
import de.augustakom.hurrican.model.cc.AddressTypeEnum;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.IPPurpose;
import de.augustakom.hurrican.model.cc.view.IPAddressSearchView;

/**
 * Hibernate DAO-Implementierung von <code>IPAddressDAO</code>
 *
 *
 * @since 06.09.2011
 */
@Repository
public class IPAddressDAOImpl extends Hibernate4DAOImpl implements IPAddressDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public List<IPAddress> findV6PrefixesByBillingOrderNumber(final Long billingOrderNumber) {
        final StringBuilder hql = new StringBuilder();
        hql.append("from ");
        hql.append(IPAddress.class.getName());
        hql.append(" addr where addr.billingOrderNo= :bonId ");
        hql.append(" and addr.gueltigVon<= :now and addr.gueltigBis> :now ");
        hql.append(" and addr.ipType in (:addressTypes) ");

        Session session = sessionFactory.getCurrentSession();
        Date now = new Date();
        Query q = session.createQuery(hql.toString());
        q.setLong("bonId", billingOrderNumber);
        q.setDate("now", now);
        q.setParameterList("addressTypes", new Object[] { AddressTypeEnum.IPV6_prefix });

        @SuppressWarnings("unchecked")
        List<IPAddress> result = q.list();
        if (result == null) {
            return Collections.emptyList();
        }
        return result;
    }

    @Override
    public List<IPAddress> findAssignedIPs4BillingOrder(final Long billingOrderNo) {
        final StringBuilder hql = new StringBuilder();
        hql.append("from ");
        hql.append(IPAddress.class.getName());
        hql.append(" addr where addr.billingOrderNo= :bonId ");
        hql.append(" and addr.gueltigVon<= :now and addr.gueltigBis> :now ");
        hql.append(" and addr.netId is not null ");

        Session session = sessionFactory.getCurrentSession();
        Date now = new Date();
        Query q = session.createQuery(hql.toString());
        q.setLong("bonId", billingOrderNo);
        q.setDate("now", now);

        @SuppressWarnings("unchecked")
        List<IPAddress> result = q.list();
        if (result == null) {
            return Collections.emptyList();
        }
        return result;
    }

    @Override
    public List<IPAddress> findAllAssignedIPs4BillingOrder(final Long billingOrderNo) {
        final StringBuilder hql = new StringBuilder();
        hql.append("from ");
        hql.append(IPAddress.class.getName());
        hql.append(" addr where addr.billingOrderNo= :bonId ");
        hql.append(" and addr.netId is not null ");

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(hql.toString());
        q.setLong("bonId", billingOrderNo);

        @SuppressWarnings("unchecked")
        List<IPAddress> result = q.list();
        if (result == null) {
            return Collections.emptyList();
        }
        return result;
    }

    @Override
    public List<IPAddress> findAssignedIPsOnly4BillingOrder(final Long billingOrderNo) {
        final StringBuilder hql = new StringBuilder();
        hql.append("from ");
        hql.append(IPAddress.class.getName());
        hql.append(" addr where addr.billingOrderNo= :bonId ");
        hql.append(" and addr.gueltigVon<= :now and addr.gueltigBis> :now ");
        hql.append(" and addr.ipType not in (:addressTypes) ");
        hql.append(" and addr.netId is not null ");

        Session session = sessionFactory.getCurrentSession();
        Date now = new Date();
        Query q = session.createQuery(hql.toString());
        q.setLong("bonId", billingOrderNo);
        q.setDate("now", now);
        q.setParameterList("addressTypes", new Object[] { AddressTypeEnum.IPV4_prefix, AddressTypeEnum.IPV6_prefix });

        @SuppressWarnings("unchecked")
        List<IPAddress> result = q.list();
        if (result == null) {
            return Collections.emptyList();
        }
        return result;
    }

    @Override
    public List<IPAddress> findAssignedNetsOnly4BillingOrder(final Long billingOrderNo) {
        final StringBuilder hql = new StringBuilder();
        hql.append("from ");
        hql.append(IPAddress.class.getName());
        hql.append(" addr where addr.billingOrderNo= :bonId ");
        hql.append(" and addr.gueltigVon<= :now and addr.gueltigBis> :now ");
        hql.append(" and addr.ipType in (:addressTypes) ");
        hql.append(" and addr.netId is not null ");

        Session session = sessionFactory.getCurrentSession();
        Date now = new Date();
        Query q = session.createQuery(hql.toString());
        q.setLong("bonId", billingOrderNo);
        q.setDate("now", now);
        q.setParameterList("addressTypes", new Object[] { AddressTypeEnum.IPV4_prefix, AddressTypeEnum.IPV6_prefix });

        @SuppressWarnings("unchecked")
        List<IPAddress> result = q.list();
        if (result == null) {
            return Collections.emptyList();
        }
        return result;
    }

    @Override
    public List<IPAddress> findAssignedIPs4NetId(final Long netId, final Date dateActive) {
        final StringBuilder hql = new StringBuilder();
        hql.append("from ");
        hql.append(IPAddress.class.getName());
        hql.append(" addr where addr.netId= :netId ");
        hql.append(" and addr.gueltigVon<= :dateActive and addr.gueltigBis> :dateActive ");

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(hql.toString());
        q.setLong("netId", netId);
        q.setDate("dateActive", dateActive);

        @SuppressWarnings("unchecked")
        List<IPAddress> result = q.list();
        if (result == null) {
            return Collections.emptyList();
        }
        return result;
    }

    @Override
    public List<Long> findActiveIPs4OtherOrders(Long netId, Long billingOrderNo) {
        DetachedCriteria criteria = DetachedCriteria.forClass(IPAddress.class);
        //@formatter:off
        criteria.add(Restrictions.eq("netId", netId))
            .add(Restrictions.gt("gueltigBis", new Date()))
            .add(Restrictions.ne("billingOrderNo", billingOrderNo))
            .setProjection(Projections.projectionList()
                    .add(Projections.property("billingOrderNo")));
        //@formatter:on
        @SuppressWarnings("unchecked")
        List<Long> result = criteria.getExecutableCriteria(sessionFactory.getCurrentSession()).list();
        return result;
    }

    @Override
    public int findIpToOrdersAssignmentCount(final Long netId) {
        Session session = sessionFactory.getCurrentSession();
        //@formatter:off
        Criteria crit = session.createCriteria(IPAddress.class)
                .add(Restrictions.eq("netId", netId))
                .add(Restrictions.gt("gueltigBis", new Date()))
                .setProjection(Projections.rowCount());
        //@formatter:on
        return ((Long) crit.uniqueResult()).intValue();
    }

    @Override
    public List<IPAddress> findNonReleasedIPs4NetId(final Long netId) {
        Session session = sessionFactory.getCurrentSession();
        //@formatter:off
        Criteria crit = session.createCriteria(IPAddress.class)
            .add(Restrictions.eq("netId", netId))
            .add(Restrictions.isNull("freigegeben"));
        //@formatter:on
        return crit.list();
    }

    @Override
    public List<IPAddress> findIPs4NetId(final Long netId) {
        Session session = sessionFactory.getCurrentSession();
        //@formatter:off
        Criteria crit = session.createCriteria(IPAddress.class)
                .add(Restrictions.eq("netId", netId));
        //@formatter:on
        return crit.list();
    }

    @Override
    public List<IPAddressSearchView> filterIPsByBinaryRepresentation(List<String> addressTypes,
            String binaryRepresentation, boolean onlyActive, Integer limit) {
        Date now = DateTools.getActualSQLDate();

        List<Object> params = new ArrayList<Object>();
        List<Type> types = new ArrayList<Type>();
        params.addAll(Arrays.asList(new Object[] { binaryRepresentation, binaryRepresentation, now, now, now, now, limit }));
        types.addAll(Arrays.asList(new Type[] { new StringType(), new StringType(), new DateType(), new DateType(), new DateType(), new DateType(), new IntegerType()}));
        params.addAll(addressTypes);

        for (int i = 0; i < addressTypes.size(); i++) {
            types.add(new StringType());
        }

        StringBuilder sql = new StringBuilder("SELECT ipaddr.BILLING_ORDER_NO, ipaddr.ID, tdn.TDN, ad.AUFTRAG_ID, ");
        sql.append(" ref.STR_VALUE as PURPOSE, ipaddr.GUELTIG_VON as IP_GUELTIG_VON, ipaddr.GUELTIG_BIS as IP_GUELTIG_BIS, ");
        sql.append(" ipaddr.ADDRESS_TYPE, a.KUNDE__NO ");
        sql.append(" FROM T_IP_ADDRESS ipaddr ");
        sql.append(" LEFT JOIN T_AUFTRAG_DATEN ad ON ipaddr.BILLING_ORDER_NO=ad.PRODAK_ORDER__NO ");
        sql.append(" LEFT JOIN T_AUFTRAG_TECHNIK atech ON ad.AUFTRAG_ID=atech.AUFTRAG_ID ");
        sql.append(" LEFT JOIN T_AUFTRAG a ON ad.AUFTRAG_ID=a.ID ");
        sql.append(" LEFT JOIN T_TDN tdn ON atech.TDN_ID=tdn.ID ");
        sql.append(" LEFT JOIN T_REFERENCE ref ON ipaddr.PURPOSE=ref.ID ");
        sql.append(" WHERE ");
        sql.append(" ( ? LIKE ipaddr.BINARY_REPRESENTATION || '%' ");       // = BinarySearch like BINARY_REPRESENTATION%"
        sql.append("   OR ipaddr.BINARY_REPRESENTATION LIKE ? || '%' ) ");  // = BINARY_REPRESENTATION like BinarySearch%
        sql.append(" AND ipaddr.BINARY_REPRESENTATION IS NOT NULL ");
        sql.append(" AND ad.GUELTIG_VON<=? AND ad.GUELTIG_BIS>? ");
        sql.append(" AND atech.GUELTIG_VON<=? AND atech.GUELTIG_BIS>? ");
        sql.append(" AND ROWNUM <= ? ");
        sql.append(" AND ipaddr.ADDRESS_TYPE ");
        SqlBuilderTools.addIn(sql, addressTypes);
        if (onlyActive) {
            sql.append(" AND ipaddr.GUELTIG_VON<=? AND ipaddr.GUELTIG_BIS>? ");
            params.add(now);
            types.add(new DateType());
            params.add(now);
            types.add(new DateType());
        }
        sql.append(" ORDER BY ipaddr.BILLING_ORDER_NO ASC, atech.AUFTRAG_ID ASC");

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameters(params.toArray(), types.toArray(new Type[types.size()]));
        List<Object[]> result = sqlQuery.list();
        if (result != null) {
            List<IPAddressSearchView> retVal = new ArrayList<IPAddressSearchView>();
            for (Object[] values : result) {
                int columnIndex = 0;
                IPAddressSearchView view = new IPAddressSearchView();
                view.setBillingOrderNo(ObjectTools.getLongSilent(values, columnIndex++));
                Long ipAddressId = ObjectTools.getLongSilent(values, columnIndex++);
                view.setIpAddress((ipAddressId != null) ? findById(ipAddressId, IPAddress.class) : null);
                view.setTdn(ObjectTools.getStringSilent(values, columnIndex++));
                view.setAuftragId(ObjectTools.getLongSilent(values, columnIndex++));
                view.setPurpose(ObjectTools.getStringSilent(values, columnIndex++));
                view.setGueltigVon(ObjectTools.getDateSilent(values, columnIndex++));
                view.setGueltigBis(ObjectTools.getDateSilent(values, columnIndex++));
                view.setIpType(ObjectTools.getStringSilent(values, columnIndex++));
                view.setKundeNo(ObjectTools.getLongSilent(values, columnIndex++));
                retVal.add(view);
            }
            return retVal;
        }

        return null;
    }

    @Override
    public Map<Long, Set<IPAddress>> findAllActiveAndAssignedIPs() {
        Session session = sessionFactory.getCurrentSession();
        //@formatter:off
        Criteria crit = session.createCriteria(IPAddress.class)
            .add(Restrictions.isNotNull("netId"))
            .add(Restrictions.isNull("freigegeben"))
            .addOrder(Order.asc("netId"));
        //@formatter:on
        List<IPAddress> ips = crit.list();
        Map<Long, Set<IPAddress>> result = new HashMap<Long, Set<IPAddress>>();
        if (CollectionTools.isEmpty(ips)) {
            return result;
        }
        for (IPAddress ipAddress : ips) {
            Set<IPAddress> netIdEntities = result.get(ipAddress.getNetId());
            if (netIdEntities == null) {
                netIdEntities = new HashSet<IPAddress>();
                result.put(ipAddress.getNetId(), netIdEntities);
            }
            netIdEntities.add(ipAddress);
        }
        return result;
    }

    @Override
    public List<IPAddress> findRelativeIPs4Prefix(final IPAddress prefix) {
        final StringBuilder hql = new StringBuilder();
        hql.append("from ");
        hql.append(IPAddress.class.getName());
        hql.append(" ipAddr where ipAddr.prefixRef.id = :id ");
        hql.append(" and ipAddr.billingOrderNo = :billingOrderNo ");

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(hql.toString());
        q.setLong("id", prefix.getId());
        q.setLong("billingOrderNo", prefix.getBillingOrderNo());

        @SuppressWarnings("unchecked")
        List<IPAddress> resultList = q.list();
        return resultList;
    }

    @Override
    public IPAddress findDHCPv6PDPrefix(Long billingOrderNo) {
        Session session = sessionFactory.getCurrentSession();
        //@formatter:off
        Criteria crit = session.createCriteria(IPAddress.class)
                .add(Restrictions.eq("billingOrderNo", billingOrderNo))
                .add(Restrictions.eq("purpose.id", IPPurpose.DhcpV6Pd.getId()))
                .add(Restrictions.eq("ipType", AddressTypeEnum.IPV6_prefix))
                .add(Restrictions.isNotNull("netId"))
                .add(Restrictions.isNull("freigegeben"))
                .add(Restrictions.gt("gueltigBis", new Date()));
        //@formatter:on
        return (IPAddress) crit.uniqueResult();
    }

    @Override
    @Nonnull
    public List<IPAddress> findV4AddressesByBillingOrderNumber(@Nonnull Long billingOrderNumber) {
        //@formatter:off
        DetachedCriteria crit = DetachedCriteria.forClass(IPAddress.class)
                .add(Restrictions.eq("billingOrderNo", billingOrderNumber))
                .add(Restrictions.in("ipType", new Object[] { AddressTypeEnum.IPV4, AddressTypeEnum.IPV4_with_prefixlength }))
                .add(Restrictions.isNotNull("netId"))
                .add(Restrictions.isNull("freigegeben"))
                .add(Restrictions.gt("gueltigBis", new Date()));
        //@formatter:on
        @SuppressWarnings("unchecked")
        List<IPAddress> resultList = crit.getExecutableCriteria(sessionFactory.getCurrentSession()).list();
        return resultList;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
} // end

