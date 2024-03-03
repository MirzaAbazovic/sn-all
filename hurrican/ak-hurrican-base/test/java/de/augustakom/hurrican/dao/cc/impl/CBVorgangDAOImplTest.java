/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.09.2011 13:45:37
 */
/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.06.2007 11:17:12
 */
package de.augustakom.hurrican.dao.cc.impl;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.math.*;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

@Test(groups = BaseTest.UNIT)
public class CBVorgangDAOImplTest extends BaseTest {

    @InjectMocks
    private CBVorgangDAOImpl underTest;

    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @BeforeMethod
    public void setup() {
        underTest = new CBVorgangDAOImpl();
        MockitoAnnotations.initMocks(this);

        when(sessionFactory.getCurrentSession()).thenReturn(session);
    }

    @DataProvider
    public Object[][] dataProviderGetNextCarrierRefNr() {
        // @formatter:off
        return new Object[][] {
                { null,            new BigDecimal("1"), "0000000001" },
                { "",              new BigDecimal("1"), "0000000001" },
                { "",            new BigDecimal("100"), "0000000100" },
                { "",     new BigDecimal("1234567890"), "1234567890" },
                { "TEST",        new BigDecimal("100"), "TEST000100" },
                { "XYZ",  new BigDecimal("1234567890"), "XYZ4567890" },
                { "TEST", new BigDecimal("1234567890"), "TEST567890" },
                { "H_"  ,        new BigDecimal("123"), "H_00000123" },
                { "H_"  ,   new BigDecimal("12345678"), "H_12345678" },
                { "H_"  ,  new BigDecimal("999999999"), "H_99999999" },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderGetNextCarrierRefNr")
    public void testGetNextCarrierRefNr(String carrierRefNrPrefix, BigDecimal nextSequenceValue, String expectedResult) {
        underTest.carrierRefNrPrefix = carrierRefNrPrefix;
        SQLQuery sqlQueryMock = Mockito.mock(SQLQuery.class);
        when(session.createSQLQuery(anyString())).thenReturn(sqlQueryMock);
        when(sqlQueryMock.uniqueResult()).thenReturn(nextSequenceValue);

        assertEquals(underTest.getNextCarrierRefNr(), expectedResult);
    }

}
