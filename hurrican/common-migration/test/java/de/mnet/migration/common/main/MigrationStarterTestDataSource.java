/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.06.2010 14:20:07
 */
package de.mnet.migration.common.main;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyChar;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.logging.*;
import javax.sql.*;
import org.mockito.internal.stubbing.defaultanswers.ReturnsMoreEmptyValues;
import org.mockito.invocation.InvocationOnMock;


/**
 *
 */
public class MigrationStarterTestDataSource implements DataSource {

    private static class TestReturner extends ReturnsMoreEmptyValues {
        @Override
        public Object answer(InvocationOnMock invocation) throws Throwable {
            if (invocation.getMethod().getName().startsWith("execute")) {
                return mock(ResultSet.class);
            }
            if (invocation.getMethod().getName().startsWith("prepareStatement")) {
                return mock(PreparedStatement.class, this);
            }
            return super.answer(invocation);
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return mock(Connection.class, new TestReturner());
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        Connection mock = mock(Connection.class, new TestReturner());
        return mock;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        PrintWriter result = mock(PrintWriter.class);
        when(result.append(anyChar())).thenReturn(result);
        when(result.append(any(CharSequence.class))).thenReturn(result);
        when(result.append(any(CharSequence.class), anyInt(), anyInt())).thenReturn(result);
        when(result.format(anyString(), anyObject())).thenReturn(result);
        when(result.format(any(Locale.class), anyString(), anyObject())).thenReturn(result);
        when(result.printf(anyString(), anyObject())).thenReturn(result);
        when(result.printf(any(Locale.class), anyString(), anyObject())).thenReturn(result);
        when(result.checkError()).thenReturn(false);
        return null;
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("Method getParentLogger() not supported!");
    }
}
