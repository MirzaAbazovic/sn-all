/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.01.2012 14:24:07
 */
package de.augustakom.hurrican.validation.cc;

import static de.augustakom.hurrican.validation.cc.DomainValidatorTest.TestDomain.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.messages.AKWarning;
import de.augustakom.hurrican.model.cc.AuftragMVSEnterprise;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.MVSService;
import de.augustakom.hurrican.validation.cc.DomainValidatorTest.TestDomain;


/**
 * Modultest fuer {@link SubdomainValidator}.
 *
 *
 * @since Release 11
 */
@Test(groups = BaseTest.UNIT)
public class SubdomainValidatorTest extends BaseTest {

    private SubdomainValidator cut;

    @Mock
    private MVSService mvsServiceMock;

    @BeforeMethod
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @DataProvider(name = "dataProviderValidate")
    protected Object[][] dataProviderValidate() {
        final String domainGreaterThan150Characters = TestDomain.bigString(150, 'o');

        return new Object[][] {
                // Subdomains dessen Pattern nicht stimmt.
                { domain("österreich").shouldFail(true) },
                { domain("$%§!").shouldFail(true) },
                { domain("dd,asdf").shouldFail(true) },
                { domain("Mit Leerzeichen").shouldFail(true) },
                { domain("i.am.from.m-net").shouldFail(true) },
                { domain("master.universe").shouldFail(true) },
                { domain("m-net.de").shouldFail(true) },
                // gute Subdomains
                { domain("ich-liebe-deutsche-land").shouldFail(false) },
                { domain("m-net").shouldFail(false) },
                // Domains, die es bereits gibt. (laut Service)
                { domain("telekom").isAlreadyUsed().shouldFail(true) },
                // Domain ist leer
                { domain("").shouldFail(true) },
                // Domain ist länger als die erlaubten 150 Zeichen
                { domain(domainGreaterThan150Characters).shouldFail(true) },
        };
    }

    @Test(dataProvider = "dataProviderValidate")
    public void validate(TestDomain testObject) throws FindException {
        cut = new SubdomainValidator(mvsServiceMock);
        when(
                mvsServiceMock.isSubdomainAlreadyUsedInEnterpriseDomain(Mockito.any(AuftragMVSEnterprise.class),
                        Mockito.eq(testObject.domain))
        ).thenReturn(testObject.isAlreadyUsed);
        AKWarning result = cut.validate(testObject.domain, new AuftragMVSEnterprise());
        assertEquals(result != null, testObject.shouldFail);
    }

}
