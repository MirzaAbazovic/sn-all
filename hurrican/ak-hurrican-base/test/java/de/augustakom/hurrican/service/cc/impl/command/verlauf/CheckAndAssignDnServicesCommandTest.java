/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.10.2009 18:48:43
 */
package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import java.util.*;
import com.google.common.collect.ImmutableList;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.RufnummerBuilder;
import de.augustakom.hurrican.model.cc.Leistung4DnBuilder;
import de.augustakom.hurrican.model.cc.LeistungsbuendelBuilder;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.dn.Leistung4Dn;
import de.augustakom.hurrican.model.cc.dn.Leistungsbuendel;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCRufnummernService;


/**
 * TestNG Klasse fuer {@link CheckAndAssignDnServicesCommand}
 *
 *
 */
@Test(groups = { BaseTest.UNIT })
public class CheckAndAssignDnServicesCommandTest extends BaseTest {

    @InjectMocks
    private CheckAndAssignDnServicesCommand cut;

    @Mock
    private CCRufnummernService ccRufnummernService;

    @BeforeMethod
    public void setUp() throws ServiceNotFoundException {
        cut = new CheckAndAssignDnServicesCommand();
        MockitoAnnotations.initMocks(this);
    }

    @Test(expectedExceptions = StoreException.class, expectedExceptionsMessageRegExp = "DN-Leistungsbuendel konnte nicht ermittelt werden!")
    public void testVerifyDNPresenceWithStoreExceptionBecauseOfNonExistentLeistungsbuendel() throws Exception {
        setProdukt();

        final List<Leistung4Dn> leistungen4DNs = ImmutableList.of(new Leistung4DnBuilder().withRandomId().build());
        when(ccRufnummernService.findMissingDnLeistungen(any(Long.class))).thenReturn(leistungen4DNs);
        List<Leistung4Dn> leistungen4DNEmpty = new LinkedList<Leistung4Dn>();
        when(ccRufnummernService.findDNLeistungenWithoutParameter(leistungen4DNs)).thenReturn(leistungen4DNEmpty);
        when(ccRufnummernService.findDNLeistungenWithParameter(leistungen4DNs)).thenReturn(leistungen4DNEmpty);
        when(ccRufnummernService.findLeistungsbuendel4Auftrag(any(Long.class))).thenReturn(null);

        cut.verifyDNPresence();
    }

    @Test(expectedExceptions = FindException.class,
            expectedExceptionsMessageRegExp = "Folgende Rufnummernleistungen wurden in Taifun aber nicht in Hurrican konfiguriert und benötigen einen Parameter: \ntest-leistung")
    public void testVerifyDNPresenceExpectExceptionBecauseOfMissingDnServiceNeedsParameter() throws Exception {
        setProdukt();

        final List<Leistung4Dn> leistungen4DNs = ImmutableList.of(new Leistung4DnBuilder().withRandomId().withLeistung("test-leistung").build());
        when(ccRufnummernService.findMissingDnLeistungen(any(Long.class))).thenReturn(leistungen4DNs);
        List<Leistung4Dn> leistungen4DNEmpty = new LinkedList<Leistung4Dn>();
        when(ccRufnummernService.findDNLeistungenWithoutParameter(leistungen4DNs)).thenReturn(leistungen4DNEmpty);
        when(ccRufnummernService.findDNLeistungenWithParameter(leistungen4DNs)).thenReturn(leistungen4DNs);
        Leistungsbuendel leistungsbuendel = new LeistungsbuendelBuilder().withRandomId().setPersist(false).build();
        when(ccRufnummernService.findLeistungsbuendel4Auftrag(any(Long.class))).thenReturn(leistungsbuendel);

        cut.verifyDNPresence();
    }

    public void testVerifyDNPresence() throws Exception {
        setProdukt();
        setRufnummern();
        setRealTermin();

        final List<Leistung4Dn> leistungen4DNs = ImmutableList.of(new Leistung4DnBuilder().withRandomId().build());
        when(ccRufnummernService.findMissingDnLeistungen(any(Long.class))).thenReturn(leistungen4DNs);
        when(ccRufnummernService.findDNLeistungenWithoutParameter(leistungen4DNs)).thenReturn(leistungen4DNs);
        List<Leistung4Dn> leistungen4DNEmpty = new LinkedList<Leistung4Dn>();
        when(ccRufnummernService.findDNLeistungenWithParameter(leistungen4DNs)).thenReturn(leistungen4DNEmpty);
        Leistungsbuendel leistungsbuendel = new LeistungsbuendelBuilder().withRandomId().setPersist(false).build();
        when(ccRufnummernService.findLeistungsbuendel4Auftrag(any(Long.class))).thenReturn(leistungsbuendel);

        cut.verifyDNPresence();

        // @formatter:off
        verify(ccRufnummernService).saveLeistung2DN(
                any(Rufnummer.class),
                any(Long.class),
                any(String.class),
                eq(false),
                any(Long.class),
                any(Date.class),
                any(Long.class),
                any(Long.class));
        // @formatter:on
    }

    /**
     * Setzt das realTermin Feld in der Kommand Klasse.
     */
    private void setRealTermin() {
        cut.realTermin = new Date();
    }

    /**
     * Setzt das rufnummern Feld in der Kommand Klasse.
     */
    private void setRufnummern() {
        final List<Rufnummer> rufnummern = ImmutableList.of(new RufnummerBuilder().setPersist(false).build());
        cut.rufnummern = rufnummern;
    }

    /**
     * Setzt das produkt Feld in der Kommand Klasse.
     */
    private void setProdukt() {
        final Produkt produkt = mock(Produkt.class);
        when(produkt.isDnAllowed()).thenReturn(true);
        cut.produkt = produkt;
    }
}


