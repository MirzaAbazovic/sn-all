/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.08.2010 11:58:31
 */
package de.augustakom.hurrican.model.cc;

import java.io.*;
import java.sql.*;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

import de.augustakom.hurrican.model.cc.Rangierung.Freigegeben;

/**
 * Hibernate UserType, um den Enum Rangierung.Freigegeben in der DB als String mit dem Enum Wert zu speichern.
 */
public class RangierungFreigegebenUserType implements UserType {

    private static final int[] SQL_TYPES = { Types.VARCHAR };

    public Object nullSafeGet(ResultSet resultSet, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
        String value = resultSet.getString(names[0]);
        return resultSet.wasNull() ? null : Freigegeben.getFreigegeben(Integer.parseInt(value));
    }

    public void nullSafeSet(PreparedStatement statement, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            statement.setNull(index, Types.VARCHAR);
        }
        else {
            statement.setString(index, ((Freigegeben) value).getFreigegebenValueAsString());
        }
    }

    public int[] sqlTypes() {
        return SQL_TYPES;
    }

    public Class returnedClass() {
        return Rangierung.Freigegeben.class;
    }

    public boolean equals(Object x, Object y) {  // NOSONAR squid:S1201 ; method is defined in hibernate UserType interface and cannot be changed
        return x == y;
    }

    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    public boolean isMutable() {
        return false;
    }

    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x == null ? 0 : x.hashCode();
    }
}
