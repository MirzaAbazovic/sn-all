/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.06.2010
 */
package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.RufnummerBuilder;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.service.cc.impl.command.CommandBaseTest;

@Test(groups = { BaseTest.UNIT })
public class CheckDNCountCommandTest extends CommandBaseTest {

    public void testExecuteWithRufnummerWithoutProdukt() throws Exception {
        CheckDNCountCommand cmd = spy(new CheckDNCountCommand());
        doReturn(null).when(cmd).getProdukt();

        assertCommandResult(cmd.execute(), ServiceCommandResult.CHECK_STATUS_INVALID,
                "Produkt-Konfiguration ist nicht vorhanden!", cmd.getClass());
    }

    public void testExecuteWithRufnummerWithTooManyNumbers() throws Exception {
        List<Rufnummer> rufnummern = new ArrayList<Rufnummer>();
        rufnummern.add((new RufnummerBuilder()).withOeNoOrig(11L).setPersist(false).build());
        rufnummern.add((new RufnummerBuilder()).withOeNoOrig(11L).setPersist(false).build());

        CheckDNCountCommand cmd = spy(new CheckDNCountCommand());
        doReturn((new ProduktBuilder()).withMaxDnCount(1).withDnTyp(11L).setPersist(false).build()).when(cmd).getProdukt();
        doReturn(rufnummern).when(cmd).getRufnummern();

        assertCommandResult(cmd.execute(), ServiceCommandResult.CHECK_STATUS_INVALID,
                "Dem Auftrag sind zu viele Rufnummern vom Typ 11 zugeordnet!", cmd.getClass());
    }

    public void testExecuteWithRufnummerWithTooLessNumbers() throws Exception {
        ArrayList<Rufnummer> rufnummern = new ArrayList<Rufnummer>();
        rufnummern.add((new RufnummerBuilder()).withMainNumber(true).withOeNoOrig(91L).setPersist(false).build());

        CheckDNCountCommand cmd = spy(new CheckDNCountCommand());
        doReturn((new ProduktBuilder()).withMinDnCount(3).withMaxDnCount(10).withDnTyp(91L).setPersist(false).build())
                .when(cmd).getProdukt();
        doReturn(rufnummern).when(cmd).getRufnummern();

        assertCommandResult(cmd.execute(), ServiceCommandResult.CHECK_STATUS_OK, null, cmd.getClass());
        assertNotNull(cmd.getWarnings());
        assertTrue(cmd.getWarnings().isNotEmpty());
        assertNotNull(cmd.getWarnings().getWarningsAsText());
        assertTrue(cmd.getWarnings().getWarningsAsText().startsWith(
                "Dem Auftrag sind zu wenige Rufnummern vom Typ 91 zugeordnet! [Source: "));
    }

    public void testExecuteWithRufnummerWithoutMainNumber() throws Exception {
        ArrayList<Rufnummer> rufnummern = new ArrayList<Rufnummer>();
        rufnummern.add((new RufnummerBuilder()).withOeNoOrig(7L).setPersist(false).build());

        CheckDNCountCommand cmd = spy(new CheckDNCountCommand());
        doReturn((new ProduktBuilder()).withMinDnCount(1).withMaxDnCount(1).withDNBlock(false)
                .withDnTyp(7L).setPersist(false).build())
                .when(cmd).getProdukt();
        doReturn(rufnummern).when(cmd).getRufnummern();

        assertCommandResult(cmd.execute(), ServiceCommandResult.CHECK_STATUS_INVALID,
                "Dem Auftrag ist keine Hauptrufnummer zugeordnet. Bitte in Taifun nachholen.", cmd.getClass());
    }

    public void testExecuteWithRufnummerWithTooManyMainNumber() throws Exception {
        List<Rufnummer> rufnummern = new ArrayList<Rufnummer>();
        rufnummern.add((new RufnummerBuilder()).withMainNumber(true).withOeNoOrig(27L).setPersist(false).build());
        rufnummern.add((new RufnummerBuilder()).withMainNumber(true).withOeNoOrig(27L).setPersist(false).build());

        CheckDNCountCommand cmd = spy(new CheckDNCountCommand());
        doReturn((new ProduktBuilder()).withMinDnCount(1).withMaxDnCount(10).withDNBlock(false)
                .withDnTyp(27L).setPersist(false).build())
                .when(cmd).getProdukt();
        doReturn(rufnummern).when(cmd).getRufnummern();

        assertCommandResult(cmd.execute(), ServiceCommandResult.CHECK_STATUS_INVALID,
                "Diesem Auftrag sind mehrere Hauptrufnummern zugeordnet. Bitte in Taifun korrigieren.", cmd.getClass());
    }
}
