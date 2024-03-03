/**
 *
 */
package de.augustakom.hurrican.tools.hibernate;

import java.io.*;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import de.augustakom.common.tools.net.IPAddressConverter;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.service.utils.HistoryHelper;

/**
 * Interceptor für IPAddress. Legt Sequenz und 'gueltig' Daten automatisch an, wenn noch nicht erfolgt und baut die
 * binaere Repraesentation.
 */
public class IPAddressInterceptor extends EmptyInterceptor {

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        boolean modified = false;
        if (entity instanceof IPAddress) {
            IPAddress ipAddress = (IPAddress) entity;
            for (int i = 0; i < propertyNames.length; i++) {
                if (IPAddress.GUELTIG_VON.equals(propertyNames[i]) && (state[i] == null)) {
                    state[i] = HistoryHelper.checkGueltigVon((Date) state[i]);
                    modified = true;
                }
                else if (IPAddress.GUELTIG_BIS.equals(propertyNames[i]) && (state[i] == null)) {
                    state[i] = HistoryHelper.checkGueltigBis((Date) state[i]);
                    modified = true;
                }
                else if (IPAddress.BINARY_REPRESENTATION.equals(propertyNames[i])) {
                    state[i] = IPAddressConverter.parseIPAddress(ipAddress.getAbsoluteAddress(),
                            !ipAddress.isPrefixAddress());
                    modified = true;
                }
            }
        }
        return modified;
    }

    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState,
            String[] propertyNames, Type[] types) {
        boolean modified = false;
        if (entity instanceof IPAddress) {
            IPAddress ipAddress = (IPAddress) entity;
            for (int i = 0; i < propertyNames.length; i++) {
                if (IPAddress.BINARY_REPRESENTATION.equals(propertyNames[i])) {
                    String binaryRepresentation = IPAddressConverter.parseIPAddress(ipAddress.getAbsoluteAddress(),
                            !ipAddress.isPrefixAddress());
                    if (!StringUtils.equals(binaryRepresentation, (String) currentState[i])) {
                        currentState[i] = binaryRepresentation;
                        modified = true;
                    }
                }
            }
        }
        return modified;
    }

}
