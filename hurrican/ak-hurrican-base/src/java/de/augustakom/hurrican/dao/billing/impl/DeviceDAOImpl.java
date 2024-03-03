/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.12.2008 09:09:44
 */
package de.augustakom.hurrican.dao.billing.impl;

import java.sql.*;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.orm.hibernate4.SessionFactoryUtils;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.dao.jdbc.ResultSetHelper;
import de.augustakom.hurrican.dao.billing.DeviceDAO;
import de.augustakom.hurrican.model.billing.Device;


/**
 * Implementierungsklasse von <code>DeviceDAO</code>.
 *
 *
 */
public class DeviceDAOImpl extends Hibernate4DAOImpl implements DeviceDAO, InitializingBean {

    @Autowired
    @Qualifier("billing.sessionFactory")
    protected SessionFactory sessionFactory;

    // Basis SQL-Query fuer die Ermittlung der Endgeraete
    private static final String baseQuery = "select d.DEV_NO, d.DEVTYPE_ID, d.SRNUM, dt.MANUFACTURER, " +
            "m.MAC, m.STATIC_IP, dt.PROVISIONING_SYSTEM, dt.TECH_NAME, dt.DEVICE_CLASS, dt.DEVICE_EXTENSION " +
            "from DEVICE d " +
            "left join MACADDR m on m.DEV_NO=d.DEV_NO " +
            "inner join DEVICETYPE dt on d.DEVTYPE_ID=dt.DEVTYPE_ID ";

    private DeviceDAOImplJdbc daoJdbc = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        daoJdbc = new DeviceDAOImplJdbc();
        daoJdbc.setDataSource(SessionFactoryUtils.getDataSource(getSessionFactory()));
    }

    /**
     * @see de.augustakom.hurrican.dao.billing.DeviceDAO#findDevice(java.lang.Long)
     */
    public Device findDevice(Long devNo) {
        return daoJdbc.findDevice(devNo);
    }

    /**
     * @see de.augustakom.hurrican.dao.billing.DeviceDAO#findDevices4Auftrag(java.lang.Long, java.lang.String,
     * java.lang.String)
     */
    public List<Device> findDevices4Auftrag(Long auftragNoOrig, String provisioningSystem, String deviceClass) {
        return daoJdbc.findDevices4Auftrag(auftragNoOrig, provisioningSystem, deviceClass);
    }

    public List<Device> findOrderedDevices4Auftrag(Long auftragNoOrig, String provisioningSystem, String deviceClass) {
        return daoJdbc.findOrderedDevices4Auftrag(auftragNoOrig, provisioningSystem, deviceClass);
    }


    /*
     * JDBC DAO-Implementierung
     */
    static class DeviceDAOImplJdbc extends JdbcDaoSupport {

        /**
         * @see de.augustakom.hurrican.dao.billing.DeviceDAO#findDevice(java.lang.Long)
         */
        public Device findDevice(Long devNo) {
            String sql = baseQuery;
            sql += " where d.DEV_NO=?";

            List<Device> devs = getJdbcTemplate().query(sql, new Object[] { devNo }, new DeviceRowMapper());
            if (CollectionTools.isNotEmpty(devs)) {
                return devs.get(0);
            }
            return null;
        }

        /**
         * @see de.augustakom.hurrican.dao.billing.DeviceDAO#findDevices4Auftrag(java.lang.Long, java.lang.String,
         * java.lang.String)
         */
        public List<Device> findDevices4Auftrag(Long auftragNoOrig, String provisioningSystem, String deviceClass) {
            List<Object> params = new ArrayList<>();
            params.add(auftragNoOrig);

            StringBuilder sql = new StringBuilder("select d.DEV_NO, d.DEVTYPE_ID, d.SRNUM, dt.MANUFACTURER, m.MAC, ");
            sql.append("m.STATIC_IP, dt.PROVISIONING_SYSTEM, dt.TECH_NAME, dt.DEVICE_CLASS, d2s.VALID_FROM, ");
            sql.append("d2s.VALID_TO, d2s.PURCHASE_ORDER_NO, dt.DEVICE_EXTENSION ");
            sql.append("from DEVICE d ");
            sql.append("left join MACADDR m on m.DEV_NO=d.DEV_NO ");
            sql.append("inner join DEVICETYPE dt on d.DEVTYPE_ID=dt.DEVTYPE_ID ");
            sql.append("inner join DEVICE2SERVICE d2s on d.DEV_NO=d2s.DEV_NO ");
            sql.append("where d2s.SERVICE__NO=? ");
            if (StringUtils.isNotBlank(provisioningSystem)) {
                sql.append("and dt.PROVISIONING_SYSTEM=? ");
                params.add(provisioningSystem);
            }
            if (StringUtils.isNotBlank(deviceClass)) {
                sql.append("and dt.DEVICE_CLASS=? ");
                params.add(deviceClass);
            }

            return getJdbcTemplate().query(sql.toString(), params.toArray(), new DeviceRowMapper());
        }

        public List<Device> findOrderedDevices4Auftrag(Long auftragNoOrig, String provisioningSystem, String deviceClass) {
            List<Object> params = new ArrayList<>();
            params.add(auftragNoOrig);

            StringBuilder sql = new StringBuilder("select null as DEV_NO, dt.DEVTYPE_ID, null as SRNUM, dt.MANUFACTURER, null as MAC, ");
            sql.append("null as STATIC_IP, dt.PROVISIONING_SYSTEM, dt.TECH_NAME, dt.DEVICE_CLASS, d2s.VALID_FROM, ");
            sql.append("d2s.VALID_TO, d2s.PURCHASE_ORDER_NO, dt.DEVICE_EXTENSION ");
            sql.append("from DEVICE2SERVICE d2s ");
            sql.append("inner join DEVICETYPE dt on d2s.DEVTYPE_ID=dt.DEVTYPE_ID ");
            sql.append("where d2s.SERVICE__NO=? ");
            if (StringUtils.isNotBlank(provisioningSystem)) {
                sql.append("and dt.PROVISIONING_SYSTEM=? ");
                params.add(provisioningSystem);
            }
            if (StringUtils.isNotBlank(deviceClass)) {
                sql.append("and dt.DEVICE_CLASS=? ");
                params.add(deviceClass);
            }

            return getJdbcTemplate().query(sql.toString(), params.toArray(), new DeviceRowMapper());
        }

    }

    /*
     * RowMapper fuer Device-Objekte.
     */
    static class DeviceRowMapper implements RowMapper<Device> {
        /**
         * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
         */
        public Device mapRow(ResultSet rs, int rowNum) throws SQLException {
            Device dev = new Device();
            dev.setDevNo(ResultSetHelper.getLongSilent(rs, "DEV_NO"));
            dev.setDevType(ResultSetHelper.getStringSilent(rs, "DEVTYPE_ID"));
            dev.setManufacturer(ResultSetHelper.getStringSilent(rs, "MANUFACTURER"));
            dev.setSerialNumber(ResultSetHelper.getStringSilent(rs, "SRNUM"));
            dev.setMacAddress(ResultSetHelper.getStringSilent(rs, "MAC"));
            dev.setManagementIP(ResultSetHelper.getStringSilent(rs, "STATIC_IP"));
            dev.setProvisioningSystem(ResultSetHelper.getStringSilent(rs, "PROVISIONING_SYSTEM"));
            dev.setTechName(ResultSetHelper.getStringSilent(rs, "TECH_NAME"));
            dev.setDeviceClass(ResultSetHelper.getStringSilent(rs, "DEVICE_CLASS"));
            dev.setValidFrom(ResultSetHelper.getDateSilent(rs, "VALID_FROM"));
            dev.setValidTo(ResultSetHelper.getDateSilent(rs, "VALID_TO"));
            dev.setDeviceExtension(ResultSetHelper.getStringSilent(rs, "DEVICE_EXTENSION"));
            dev.setPurchaseOrderNo(ResultSetHelper.getLongSilent(rs, "PURCHASE_ORDER_NO"));
            return dev;
        }
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


