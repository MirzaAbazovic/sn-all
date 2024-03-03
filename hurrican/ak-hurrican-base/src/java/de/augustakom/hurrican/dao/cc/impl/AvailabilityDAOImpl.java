/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.12.2010 11:33:14
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.lang.ObjectTools;
import de.augustakom.common.tools.lang.WildcardTools;
import de.augustakom.hurrican.dao.cc.AvailabilityDAO;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.GeoId2TechLocation;
import de.augustakom.hurrican.model.cc.GeoId2TechLocationView;
import de.augustakom.hurrican.model.cc.GeoIdClarification;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Produkt2TechLocationType;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.query.GeoIdClarificationQuery;
import de.augustakom.hurrican.model.cc.query.GeoIdQuery;
import de.augustakom.hurrican.model.cc.query.GeoIdSearchQuery;
import de.augustakom.hurrican.model.cc.view.GeoIdClarificationView;

/**
 * Hibernate DAO Implementierung von {@link AvailabilityDAO}
 */
public class AvailabilityDAOImpl extends Hibernate4DAOImpl implements AvailabilityDAO, FindDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public List<GeoId> findByQuery(GeoIdQuery query) {
        List<Object> params = new ArrayList<>();
        StringBuilder hql = new StringBuilder();
        hql.append("SELECT geo FROM ");
        hql.append(GeoId.class.getName()).append(" geo ");
        hql.append(" where 1=1 ");
        if (StringUtils.isNotBlank(query.getStreet())) {
            hql.append("and lower(geo." + GeoId.STREET + ") like ? ");
            params.add(WildcardTools.replaceWildcards(query.getStreet()).toLowerCase());
        }
        if (StringUtils.isNotBlank(query.getHouseNum())) {
            hql.append("and lower(geo." + GeoId.HOUSENUM + ") like ? ");
            params.add(WildcardTools.replaceWildcards(query.getHouseNum()).toLowerCase());
        }
        if (StringUtils.isNotBlank(query.getHouseNumExt())) {
            hql.append("and lower(geo." + GeoId.HOUSENUM_EXT + ") like ? ");
            params.add(WildcardTools.replaceWildcards(query.getHouseNumExt()).toLowerCase());
        }
        if (StringUtils.isNotBlank(query.getZipCode())) {
            hql.append("and lower(geo." + GeoId.ZIPCODE + ") like ? ");
            params.add(WildcardTools.replaceWildcards(query.getZipCode()).toLowerCase());
        }
        if (StringUtils.isNotBlank(query.getCity())) {
            hql.append("and lower(geo." + GeoId.CITY + ") like ? ");
            params.add(WildcardTools.replaceWildcards(query.getCity()).toLowerCase());
        }
        if (StringUtils.isNotBlank(query.getDistrict())) {
            hql.append("and lower(geo." + GeoId.DISTRICT + ") like ? ");
            params.add(WildcardTools.replaceWildcards(query.getDistrict()).toLowerCase());
        }

        if (null != query.getServiceable()) {
            hql.append("and " + GeoId.SERVICEABLE + " = ? ");
            params.add(query.getServiceable());
        }

        hql.append("and " + GeoId.REPLACED_BY + " is null ");

        hql.append("order by geo." + GeoId.STREET + ", geo." + GeoId.HOUSENUM + ", geo." + GeoId.HOUSENUM_EXT
                + ", geo." + GeoId.CITY);

        @SuppressWarnings("unchecked")
        List<GeoId> result = find(hql.toString(), params.toArray());
        return result;
    }

    @Override
    public List<GeoId> findBySearchQuery(GeoIdSearchQuery query) {
        List<Object> params = new ArrayList<>();
        boolean searchByOnkzAsb = StringUtils.isNotBlank(query.getOnkz()) && query.getAsb() != null;

        StringBuilder hql = new StringBuilder();
        hql.append("SELECT geo FROM ");
        hql.append(GeoId.class.getName()).append(" geo ");
        if (searchByOnkzAsb) {
            hql.append(", ");
            hql.append(GeoId2TechLocation.class.getName()).append(" geo2tl, ");
            hql.append(HVTStandort.class.getName()).append(" hvtso, ");
            hql.append(HVTGruppe.class.getName()).append(" hvtgr ");
            hql.append("where geo.id = geo2tl.geoId ");
            hql.append("and geo2tl.hvtIdStandort = hvtso.id ");
            hql.append("and hvtso.hvtGruppeId = hvtgr.id ");
        }
        else {
            hql.append(" where 1=1 ");
        }

        if (query.getId() != null) {
            hql.append("and geo.id = ? ");
            params.add(query.getId());
        }
        if (StringUtils.isNotBlank(query.getStreet())) {
            hql.append("and lower(geo." + GeoId.STREET + ") like ? ");
            params.add(WildcardTools.replaceWildcards(query.getStreet()).toLowerCase());
        }
        if (StringUtils.isNotBlank(query.getHouseNum())) {
            hql.append("and lower(geo." + GeoId.HOUSENUM + ") like ? ");
            params.add(WildcardTools.replaceWildcards(query.getHouseNum()).toLowerCase());
        }
        if (StringUtils.isNotBlank(query.getZipCode())) {
            hql.append("and lower(geo." + GeoId.ZIPCODE + ") like ? ");
            params.add(WildcardTools.replaceWildcards(query.getZipCode()).toLowerCase());
        }
        if (StringUtils.isNotBlank(query.getCity())) {
            hql.append("and lower(geo." + GeoId.CITY + ") like ? ");
            params.add(WildcardTools.replaceWildcards(query.getCity()).toLowerCase());
        }
        if (StringUtils.isNotBlank(query.getDistrict())) {
            hql.append("and lower(geo." + GeoId.DISTRICT + ") like ? ");
            params.add(WildcardTools.replaceWildcards(query.getDistrict()).toLowerCase());
        }
        if (searchByOnkzAsb) {
            hql.append("and lower(hvtgr.onkz) like ? ");
            params.add(WildcardTools.replaceWildcards(query.getOnkz()).toLowerCase());
            hql.append("and hvtso.asb = ? ");
            params.add(query.getAsb());
            if (query.getKvz() != null) {
                hql.append("and geo.kvz = ? ");
                params.add(query.getKvz());
            }
        }
        if (null != query.getServiceable()) {
            hql.append("and geo." + GeoId.SERVICEABLE + " = ? ");
            params.add(query.getServiceable());
        }

        hql.append("and geo." + GeoId.REPLACED_BY + " is null ");

        hql.append("order by geo.id");

        @SuppressWarnings("unchecked")
        List<GeoId> result = find(hql.toString(), params.toArray());
        return result;
    }

    @Override
    public List<GeoIdClarification> findGeoIdClarificationsByStatusId(final List<Long> statusIds, final List<Long> geoIds) {
        final StringBuilder hql = new StringBuilder();
        hql.append("from ");
        hql.append(GeoIdClarification.class.getName()).append(" clf");
        hql.append(" where clf.status.id in (:statusIds)");
        if (CollectionUtils.isNotEmpty(geoIds)) {
            hql.append(" and clf.geoId in (:geoIds)");
        }
        hql.append(" order by clf.geoId, clf.id asc");

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(hql.toString());
        q.setParameterList("statusIds", statusIds);
        if (CollectionUtils.isNotEmpty(geoIds)) {
            q.setParameterList("geoIds", geoIds);
        }
        q.setCacheable(false);

        @SuppressWarnings("unchecked")
        List<GeoIdClarification> result = q.list();
        return result;
    }

    @Override
    public List<GeoIdClarificationView> findGeoIdClarificationViewsByStatusId(final List<Long> statusIds) {
        final StringBuilder hql = new StringBuilder();
        hql.append("select clf.id, clf.geoId, clf.status, clf.type, clf.info, ");
        hql.append("geo." + GeoId.STREET + ", geo." + GeoId.HOUSENUM + ", geo." + GeoId.HOUSENUM_EXT + ", geo."
                + GeoId.ZIPCODE + ", geo." + GeoId.CITY);
        hql.append(" from ");
        hql.append(GeoIdClarification.class.getName()).append(" clf, ");
        hql.append(GeoId.class.getName()).append(" geo");
        hql.append(" where clf.status.id in (:statusIds)");
        hql.append(" and clf.geoId=geo.id");
        hql.append(" order by clf.geoId, clf.id asc");

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(hql.toString());
        q.setParameterList("statusIds", statusIds);
        q.setCacheable(false);

        @SuppressWarnings("unchecked")
        List<Object[]> result = q.list();
        if (result != null) {
            List<GeoIdClarificationView> retVal = new ArrayList<>();
            for (Object[] values : result) {
                GeoIdClarificationView view = new GeoIdClarificationView();
                view.setId(ObjectTools.getLongSilent(values, 0));
                view.setGeoId(ObjectTools.getLongSilent(values, 1));
                view.setStatus((Reference) ObjectTools.getObjectAtIndex(values, 2, null));
                view.setType((Reference) ObjectTools.getObjectAtIndex(values, 3, null));
                view.setInfo(ObjectTools.getStringSilent(values, 4));
                view.setStreet(ObjectTools.getStringSilent(values, 5));
                view.setHouseNo(ObjectTools.getStringSilent(values, 6));
                view.setHouseNoExt(ObjectTools.getStringSilent(values, 7));
                view.setZipCode(ObjectTools.getStringSilent(values, 8));
                view.setCity(ObjectTools.getStringSilent(values, 9));
                retVal.add(view);
            }
            return retVal;
        }
        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<GeoIdClarification> findGeoIdClarificationByGeoId(Long geoId) {
        Criterion geoIdEquals = Restrictions.eq("geoId", geoId);
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(GeoIdClarification.class);
        criteria.add(geoIdEquals);
        return criteria.list();
    }

    @Override
    public List<GeoIdClarificationView> findGeoIdClarificationViewsByQuery(final GeoIdClarificationQuery query) {
        final StringBuilder hql = new StringBuilder();

        hql.append("select clf.id, clf.geoId, clf.status, clf.type, clf.info, ");
        hql.append("geo." + GeoId.STREET + ", geo." + GeoId.HOUSENUM + ", geo." + GeoId.HOUSENUM_EXT + ", geo."
                + GeoId.ZIPCODE + ", geo." + GeoId.CITY);
        hql.append(" from ");
        hql.append(GeoIdClarification.class.getName()).append(" clf, ");
        hql.append(GeoId.class.getName()).append(" geo");
        hql.append(" where clf.geoId=geo.id");
        if (query.getGeoId() != null) {
            hql.append(" and clf.geoId=:geoId");
        }
        if (CollectionTools.isNotEmpty(query.getStatusList())) {
            hql.append(" and clf.status.id in (:statusIds)");
        }
        if (CollectionTools.isNotEmpty(query.getTypeList())) {
            hql.append(" and clf.type.id in (:typIds)");
        }
        if ((query.getFrom() != null) && (query.getTo() != null)) {
            hql.append(" and clf.dateW>=:from and clf.dateW<:to");
            // 'Von' ist auf Mitternacht abgeschnitten
            // 'Bis' ist auf Mitternacht des nächsten gewählten Tages abgeschnitten
            // Wenn Benutzer 'von' und 'bis' für den selben Tag auswählt ist das ResultSet
            // alles innerhalb des ausgewählten Tages.
        }
        hql.append(" order by clf.geoId, clf.id asc");

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(hql.toString());
        if (query.getGeoId() != null) {
            q.setParameter("geoId", query.getGeoId());
        }
        if (CollectionTools.isNotEmpty(query.getStatusList())) {
            @SuppressWarnings("unchecked")
            List<Long> idList = (List<Long>) CollectionUtils.collect(query.getStatusList(),
                    new BeanToPropertyValueTransformer("id"));
            q.setParameterList("statusIds", idList);
        }
        if (CollectionTools.isNotEmpty(query.getTypeList())) {
            @SuppressWarnings("unchecked")
            List<Long> idList = (List<Long>) CollectionUtils.collect(query.getTypeList(),
                    new BeanToPropertyValueTransformer("id"));
            q.setParameterList("typIds", idList);
        }
        if ((query.getFrom() != null) && (query.getTo() != null)) {
            q.setParameter("from", query.getFrom());
            q.setParameter("to", query.getTo());
        }
        q.setCacheable(false);

        @SuppressWarnings("unchecked")
        List<Object[]> result = q.list();
        if (result != null) {
            List<GeoIdClarificationView> retVal = new ArrayList<>();
            for (Object[] values : result) {
                GeoIdClarificationView view = new GeoIdClarificationView();
                view.setId(ObjectTools.getLongSilent(values, 0));
                view.setGeoId(ObjectTools.getLongSilent(values, 1));
                view.setStatus((Reference) ObjectTools.getObjectAtIndex(values, 2, null));
                view.setType((Reference) ObjectTools.getObjectAtIndex(values, 3, null));
                view.setInfo(ObjectTools.getStringSilent(values, 4));
                view.setStreet(ObjectTools.getStringSilent(values, 5));
                view.setHouseNo(ObjectTools.getStringSilent(values, 6));
                view.setHouseNoExt(ObjectTools.getStringSilent(values, 7));
                view.setZipCode(ObjectTools.getStringSilent(values, 8));
                view.setCity(ObjectTools.getStringSilent(values, 9));
                retVal.add(view);
            }
            return retVal;
        }
        return Collections.emptyList();
    }

    @Override
    public List<GeoId2TechLocation> findPossibleGeoId2TechLocations(GeoId geoId, Long prodId) {
        StringBuilder hql = new StringBuilder();
        hql.append("select g2t from ");
        hql.append(GeoId2TechLocation.class.getName()).append(" g2t, ");
        hql.append(HVTStandort.class.getName()).append(" hvt, ");
        hql.append(Reference.class.getName()).append(" ref, ");
        hql.append(Produkt2TechLocationType.class.getName()).append(" p2t ");
        hql.append(" where g2t.hvtIdStandort=hvt.id and hvt.standortTypRefId=ref.id ");
        hql.append(" and ref.id=p2t.techLocationTypeRefId ");
        hql.append(" and p2t.produktId=? and g2t.geoId=? ");
        hql.append(" order by p2t.priority asc");

        @SuppressWarnings("unchecked")
        List<GeoId2TechLocation> result = find(hql.toString(), prodId, geoId.getId());
        return result;
    }

    @Override
    public List<GeoId2TechLocationView> findGeoId2TechLocationViews(Long geoId) {
        StringBuilder sql = new StringBuilder()
                .append("SELECT g2t.ID, g2t.GEO_ID, g2t.HVT_ID_STANDORT, g2t.TAL_LENGTH, g2t.TAL_LENGTH_TRUSTED, ")
                .append("   g2t.KVZ_NUMBER, g2t.MAX_BANDWIDTH_ADSL, g2t.MAX_BANDWIDTH_SDSL, g2t.MAX_BANDWIDTH_VDSL, ")
                .append("   g2t.VDSL_AN_HVT_AVAILABLE_SINCE, hvtgr.ORTSTEIL, ref.STR_VALUE, ")
                .append("   hvtgr.ONKZ, hvtso.ASB, sw.NAME, ref.ID as refId ")
                .append("FROM T_GEO_ID_2_TECH_LOCATION g2t ")
                .append("   inner join T_HVT_STANDORT hvtso on hvtso.HVT_ID_STANDORT = g2t.HVT_ID_STANDORT ")
                .append("   inner join T_HVT_GRUPPE hvtgr on hvtso.HVT_GRUPPE_ID = hvtgr.HVT_GRUPPE_ID ")
                .append("   left outer join T_HW_SWITCH sw on sw.ID = hvtgr.SWITCH ")
                .append("   inner join T_REFERENCE ref on ref.ID = hvtso.STANDORT_TYP_REF_ID ")
                .append("where g2t.GEO_ID = ? ")
                .append("order by g2t.ID");

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setParameter(0, geoId);
        @SuppressWarnings("unchecked")
        List<Object[]> result = sqlQuery.list();
        if (result != null) {
            List<GeoId2TechLocationView> retVal = new ArrayList<>();
            for (Object[] values : result) {
                GeoId2TechLocationView view = new GeoId2TechLocationView();
                view.setId(ObjectTools.getLongSilent(values, 0));
                view.setGeoId(ObjectTools.getLongSilent(values, 1));
                view.setHvtIdStandort(ObjectTools.getLongSilent(values, 2));
                view.setTalLength(ObjectTools.getLongSilent(values, 3));
                view.setTalLengthTrusted(ObjectTools.getBooleanSilent(values, 4));
                view.setKvzNumber(ObjectTools.getStringSilent(values, 5));
                view.setMaxBandwidthAdsl(ObjectTools.getLongSilent(values, 6));
                view.setMaxBandwidthSdsl(ObjectTools.getLongSilent(values, 7));
                view.setMaxBandwidthVdsl(ObjectTools.getLongSilent(values, 8));
                view.setVdslAnHvtAvailableSince(ObjectTools.getDateSilent(values, 9));
                view.setStandort(ObjectTools.getStringSilent(values, 10));
                view.setStandortTyp(ObjectTools.getStringSilent(values, 11));
                view.setOnkz(ObjectTools.getStringSilent(values, 12));
                view.setAsb(ObjectTools.getIntegerSilent(values, 13));
                view.setSwitchKennung(ObjectTools.getStringSilent(values, 14));
                view.setStandortTypRefId(ObjectTools.getLongSilent(values, 15));
                retVal.add(view);
            }

            return retVal;
        }
        return Collections.emptyList();
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
