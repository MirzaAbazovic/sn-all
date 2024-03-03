/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.09.2011 11:11:13
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.mockito.Mockito.*;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.common.BaseTest;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.ExtServiceProvider;
import de.augustakom.hurrican.model.cc.Mail;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.ExtServiceProviderService;
import de.augustakom.hurrican.service.cc.PhysikService;

/**
 *
 */
@Test(groups = { "unit" })
public class ExtServiceProviderServiceImplUnitTest extends BaseTest {

    ExtServiceProviderServiceImpl sut;

    BAService baService = null;
    CCAuftragService cas = null;
    KundenService ks = null;
    ExtServiceProviderService es = null;
    PhysikService phys = null;

    private AKUser currentUser;
    private Niederlassung niederlassung;

    @BeforeMethod
    public void setUp() {
        currentUser = new AKUser();

        sut = new ExtServiceProviderServiceImpl() {
            @Override
            public AKUser getAKUserBySessionId(Long sessionId) throws AKAuthenticationException {
                return currentUser;
            }

            ;

            @Override
            void sendEmail(Mail mail, Long sessionId) throws IllegalArgumentException, ServiceNotFoundException {
                // tut so, als wenn es eine Email versendet. :)
            }

            ;

            @Override
            Niederlassung getNiederlassung(Long niederlassungsId) throws FindException, ServiceNotFoundException {
                return niederlassung;
            }

            ;

            @Override
            public BAService getBaService() throws ServiceNotFoundException {
                return baService;
            }

            ;
        };

        baService = mock(BAService.class);
        cas = mock(CCAuftragService.class);
        ks = mock(KundenService.class);
        es = mock(ExtServiceProviderService.class);
        phys = mock(PhysikService.class);

        sut.setCCAuftragService(cas);
        sut.setKundenService(ks);
        sut.setExtServiceProviderService(es);
        sut.setPhysikService(phys);

    }

    /**
     * Testet die Methode {@link ExtServiceProviderServiceImpl#sendAuftragEmail(Long, String, byte[], Long)}. Die
     * AuftragsId ist null, was zu einer {@link FindException} führt.
     *
     * @throws FindException
     */
    @Test(expectedExceptions = FindException.class)
    public void testSendEmail_VerlaufIdIsNull() throws FindException {
        final Long verlaufId = null;
        final Long sessionId = Long.valueOf(2);
        sut.sendAuftragEmail(verlaufId, Collections.<Pair<byte[], String>>emptyList(), sessionId);
    }

    public void testSendEmail_FindProperAuftrag() throws FindException {
        final Long verlaufId = Long.valueOf(2);
        final Long sessionId = Long.valueOf(2);

        final Long auftragId = Long.valueOf(1465);
        final Verlauf verlauf = new Verlauf();
        verlauf.setAuftragId(auftragId);
        Set<Long> subAuftragsIds = new java.util.HashSet<Long>();
        subAuftragsIds.add(123L);
        subAuftragsIds.add(789L);
        verlauf.setSubAuftragsIds(subAuftragsIds);

        Mockito.when(baService.findVerlauf(Mockito.anyLong())).thenReturn(verlauf);

        VerlaufAbteilung verlaufAbteilung = new VerlaufAbteilung();
        verlaufAbteilung.setExtServiceProviderId(Long.valueOf(1492));
        verlaufAbteilung.setRealisierungsdatum(new Date());

        Mockito.when(baService.findVerlaufAbteilung(verlaufId, Abteilung.EXTERN)).thenReturn(verlaufAbteilung);

        final String emailAddr = "benjamin.koppe@m-net.de";

        ExtServiceProvider extServiceProvider = new ExtServiceProvider();
        extServiceProvider.setEmail(emailAddr);
        Mockito.when(es.findById(Mockito.anyLong())).thenReturn(extServiceProvider);

        Auftrag auftrag = new Auftrag();
        auftrag.setAuftragId(Long.valueOf(789));
        Mockito.when(cas.findAuftragById(Mockito.anyLong())).thenReturn(auftrag);

        Kunde kunde = new Kunde();
        kunde.setKundeNo(Long.valueOf(77));
        kunde.setName("Test AG");
        Mockito.when(ks.findKunde(Mockito.anyLong())).thenReturn(kunde);

        currentUser.setNiederlassungId(Long.valueOf(1234));
        niederlassung = new Niederlassung();
        niederlassung.setDispoTeampostfach(emailAddr);

        VerbindungsBezeichnung vb0 = new VerbindungsBezeichnung();
        vb0.setVbz("Hauptleitung");
        VerbindungsBezeichnung vb1 = new VerbindungsBezeichnung();
        vb1.setVbz("Leitung1");
        VerbindungsBezeichnung vb3 = new VerbindungsBezeichnung();
        vb3.setVbz("Leitung1");

        Mockito.when(phys.findVerbindungsBezeichnungByAuftragId(1465L)).thenReturn(vb0);
        Mockito.when(phys.findVerbindungsBezeichnungByAuftragId(123L)).thenReturn(vb1);
        Mockito.when(phys.findVerbindungsBezeichnungByAuftragId(789L)).thenReturn(vb3);

        sut.sendAuftragEmail(verlaufId, Collections.<Pair<byte[], String>>emptyList(), sessionId);
    }

    public void testGetAllVerbindungsbezeichnungen() throws FindException {
        final String textVB0 = "yHauptleitung";
        final String textVB1 = "xLeitung1";
        final String textVB2 = "Leitung2";
        final String textVB3 = "";
        final Long auftragId = Long.valueOf(159987);
        final Verlauf verlauf = new Verlauf();

        verlauf.setAuftragId(auftragId);
        Set<Long> subAuftragsIds = new java.util.HashSet<Long>();
        subAuftragsIds.add(12L);
        subAuftragsIds.add(34L);
        subAuftragsIds.add(56L);
        subAuftragsIds.add(78L);
        verlauf.setSubAuftragsIds(subAuftragsIds);

        VerbindungsBezeichnung vb0 = new VerbindungsBezeichnung();
        vb0.setVbz(textVB0);
        VerbindungsBezeichnung vb1 = new VerbindungsBezeichnung();
        vb1.setVbz(textVB1);
        VerbindungsBezeichnung vb2 = new VerbindungsBezeichnung();
        vb2.setVbz(textVB2);
        VerbindungsBezeichnung vb3 = new VerbindungsBezeichnung();
        vb3.setVbz(textVB3);

        Mockito.when(phys.findVerbindungsBezeichnungByAuftragId(auftragId)).thenReturn(vb0);
        Mockito.when(phys.findVerbindungsBezeichnungByAuftragId(12L)).thenReturn(vb1);
        Mockito.when(phys.findVerbindungsBezeichnungByAuftragId(34L)).thenReturn(vb2);
        Mockito.when(phys.findVerbindungsBezeichnungByAuftragId(56L)).thenReturn(vb3);

        Set<String> result = sut.getAllVerbindungsbezeichnungen(verlauf);
        Assert.assertNotNull(result, "VBZs nicht ermittelt!");
        Assert.assertEquals(result.size(), 3, "Erwartungswert stimmt mit Anzahl VBZs nicht überein!");
        Assert.assertTrue(result.contains(textVB0), "Erwarteter Text der VBZ fehlt!");
        Assert.assertTrue(result.contains(textVB1), "Erwarteter Text der VBZ fehlt!");
        Assert.assertTrue(result.contains(textVB2), "Erwarteter Text der VBZ fehlt!");
    }

    public void testGenerateStringOfVbz() throws FindException {
        final Set<String> vbz = new HashSet<String>();
        final String seperator = ", ";

        vbz.add("Master");
        vbz.add("Leitung 1");
        vbz.add("Leitung 2");
        vbz.add("xav 3");
        vbz.add("");
        vbz.add(null);

        String result = sut.generateStringOfVbz(vbz, seperator);
        Assert.assertNotNull(result, "Zeichenkette darf nicht null sein!");
        Assert.assertTrue(StringUtils.isNotBlank(result), "Zeichenkette darf nicht leer sein!");
        String[] parts = result.split(", ");
        Assert.assertNotNull(parts, "Split Operation fehlgeschlagen!");
        Assert.assertTrue(parts.length == 4, "Anzahl 'parts' nicht wie erwartet!");
    }

}
