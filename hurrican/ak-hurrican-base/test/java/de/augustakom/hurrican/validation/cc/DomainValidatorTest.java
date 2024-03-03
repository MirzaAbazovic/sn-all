/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.01.2012 15:30:28
 */
package de.augustakom.hurrican.validation.cc;

import static de.augustakom.hurrican.validation.cc.DomainValidatorTest.TestDomain.*;
import static org.testng.Assert.*;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.messages.AKWarning;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.MVSService;
import de.augustakom.hurrican.validation.cc.DomainValid.DomainValidator;

/**
 * Modultests fuer {@link DomainValidator}.
 *
 *
 * @since Release 11
 */
@Test(groups = BaseTest.UNIT)
public class DomainValidatorTest extends BaseTest {

    private DomainValidator cut;

    @Mock
    private MVSService mvsServiceMock;

    @BeforeMethod
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    static class TestDomain {
        String domain;
        boolean shouldFail = true;
        boolean isAlreadyUsed = false;

        static TestDomain domain(String domain) {
            TestDomain instance = new TestDomain();
            instance.domain = domain;
            return instance;
        }

        TestDomain shouldFail(boolean shouldFail) {
            this.shouldFail = shouldFail;
            return this;
        }

        TestDomain isAlreadyUsed() {
            this.isAlreadyUsed = true;
            return this;
        }

        static String bigString(int size, char character) {
            final StringBuilder builder = new StringBuilder(size);
            int i = 0;
            while (i <= size) {
                builder.append(character);
                i++;
            }
            return builder.toString();
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return String.format("TestDomain [domain=%s, shouldFail=%s, isAlreadyUsed=%s]", domain, shouldFail,
                    isAlreadyUsed);
        }

    } // end

    @DataProvider(name = "dataProviderValidate")
    protected Object[][] dataProviderValidate() {

        final String domainGreaterThan150Characters = TestDomain.bigString(150, 'o');

        return new Object[][] {
                // Domains dessen Pattern nicht stimmt.
                { domain("österreich.at").shouldFail(true) }, { domain("$%§!.de").shouldFail(true) },
                { domain("dd,asdf.de").shouldFail(true) }, { domain("Mit Leerzeichen.com").shouldFail(true) },
                // gute Domains
                { domain("mnet.de").shouldFail(false) }, { domain("m-net.de").shouldFail(false) },
                { domain("master.universe").shouldFail(false) }, { domain("i.am.from.m-net.de").shouldFail(false) },
                // Domains, die es bereits gibt. (laut Service)
                { domain("telekom.de").isAlreadyUsed().shouldFail(true) },
                // Domain ist leer
                { domain("").shouldFail(true) },
                // Domain ist länger als die erlaubten 150 Zeichen
                { domain(domainGreaterThan150Characters + ".de").shouldFail(true) }, };
    }

    @Test(dataProvider = "dataProviderValidate")
    public void validate(TestDomain testDomain) throws FindException {
        cut = new DomainValidator(mvsServiceMock);
        Mockito.when(mvsServiceMock.isDomainAlreadyUsed(Mockito.eq(testDomain.domain))).thenReturn(
                testDomain.isAlreadyUsed);
        AKWarning result = cut.validate(testDomain.domain);
        assertEquals(result != null, testDomain.shouldFail);
    }

} // end
