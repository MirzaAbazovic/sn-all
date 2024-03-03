/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.08.2011 12:10:24
 */
package de.augustakom.hurrican.gui.tools.tal;

import static de.augustakom.hurrican.gui.tools.tal.UnfinishedCarrierBestellungPanel.*;
import static de.mnet.wita.message.meldung.position.AenderungsKennzeichen.STORNO;
import static org.testng.Assert.*;

import java.util.*;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.gui.swing.table.AKReferenceAwareTableModel;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.cc.tal.CBVorgangNiederlassung;
import de.mnet.wita.message.meldung.position.AenderungsKennzeichen;
import de.mnet.wita.model.WitaCBVorgang;

@Test(groups = BaseTest.UNIT)
public class UnfinishedCarrierBestellungPanelTest extends BaseTest {
    private UnfinishedCarrierBestellungPanel sut = new UnfinishedCarrierBestellungPanel(false);

    public void testWitaCbVorgangTableModel() {
        WitaCBVorgang witaCBVorgang = new WitaCBVorgang();
        witaCBVorgang.setAenderungsKennzeichen(STORNO);
        AKReferenceAwareTableModel<CBVorgangNiederlassung> tableMdl = createWitaTableModelWithObject(witaCBVorgang);

        AenderungsKennzeichen aenderungsKennzeichen = getColumnContent(tableMdl, AENDERUNGSKENNZEICHEN_COLUMN_NAME);
        assertEquals(aenderungsKennzeichen, STORNO);
    }

    @DataProvider
    public Object[][] tableModels() {
        WitaCBVorgang witaCBVorgang1 = new WitaCBVorgang();
        WitaCBVorgang witaCBVorgang2 = new WitaCBVorgang();
        CBVorgang cbVorgang1 = new CBVorgang();
        CBVorgang cbVorgang2 = new CBVorgang();
        return new Object[][] {
                { sut.createCBVorgangTableModel(), witaCBVorgang1 },
                { sut.createCBVorgangTableModel(), cbVorgang1 },
                { sut.createCBVorgangTableModel(), witaCBVorgang2 },
                { sut.createCBVorgangTableModel(), cbVorgang2 },
        };
    }

    @Test(dataProvider = "tableModels")
    public void rueckTypReferenceShouldWork(AKReferenceAwareTableModel<CBVorgangNiederlassung> tableMdl,
            CBVorgang witaCBVorgang) {
        witaCBVorgang.setReturnOk(true);
        addCbVorgang(tableMdl, witaCBVorgang);

        sut.addRueckmeldungTypReference(tableMdl, ImmutableMap.of(true, "positive"));

        String rueckMeldung = getColumnContent(tableMdl, RUECKMELDUNG_COLUMN_NAME);
        assertEquals(rueckMeldung, "positive");
    }

    @Test(dataProvider = "tableModels")
    public void statusReferenceShouldWork(AKReferenceAwareTableModel<CBVorgangNiederlassung> tableMdl,
            CBVorgang witaCBVorgang) {
        witaCBVorgang.setStatus(CBVorgang.STATUS_STORNO);
        addCbVorgang(tableMdl, witaCBVorgang);

        Reference ref = new Reference();
        ref.setStrValue("storno");
        ref.setId(CBVorgang.STATUS_STORNO);
        sut.addStatusReference(tableMdl, ImmutableMap.of(CBVorgang.STATUS_STORNO, ref));

        String rueckMeldung = getColumnContent(tableMdl, STATUS_COLUMN_NAME);
        assertEquals(rueckMeldung, ref.getStrValue());
    }

    @Test(dataProvider = "tableModels")
    public void typReferenceShouldWork(AKReferenceAwareTableModel<CBVorgangNiederlassung> tableMdl,
            CBVorgang witaCBVorgang) {
        witaCBVorgang.setTyp(CBVorgang.TYP_NEU);
        addCbVorgang(tableMdl, witaCBVorgang);

        Reference ref = new Reference();
        ref.setStrValue("neubst");
        sut.addTypReference(tableMdl, ImmutableMap.of(CBVorgang.TYP_NEU, ref));

        String rueckMeldung = getColumnContent(tableMdl, TYP_COLUMN_NAME);
        assertEquals(rueckMeldung, ref.getStrValue());
    }

    @Test(dataProvider = "tableModels")
    public void carrierReferenceShouldWork(AKReferenceAwareTableModel<CBVorgangNiederlassung> tableMdl,
            CBVorgang witaCBVorgang) {
        witaCBVorgang.setCarrierId(Carrier.ID_DTAG);
        addCbVorgang(tableMdl, witaCBVorgang);

        Carrier carrier = new Carrier();
        carrier.setName("lila");
        sut.addCarrierReference(tableMdl, ImmutableMap.of(Carrier.ID_DTAG, carrier));

        String carrierName = getColumnContent(tableMdl, CARRIER_COLUMN_NAME);
        assertEquals(carrierName, carrier.getName());
    }

    public void prioShouldBeFirstColumn() {
        WitaCBVorgang witaCBVorgang = new WitaCBVorgang();
        witaCBVorgang.setReturnRealDate(new Date());
        witaCBVorgang.setReturnOk(false);
        CBVorgangNiederlassung cbVorgangNiederlassung = new CBVorgangNiederlassung(witaCBVorgang, "TestNiederlassung");
        AKReferenceAwareTableModel<CBVorgangNiederlassung> tableMdl = sut.createCBVorgangTableModel();
        addCbVorgang(tableMdl, cbVorgangNiederlassung.getCbVorgang());

        assertEquals(tableMdl.getColumnName(0), "Prio");
        Boolean prio = getColumnContent(tableMdl, "Prio");
        assertEquals(prio, witaCBVorgang.getPrio());
    }

    private <T> T getColumnContent(AKReferenceAwareTableModel<CBVorgangNiederlassung> tableMdl, String columnName) {
        int typColumn = tableMdl.findColumn(columnName);
        assertNotNull(typColumn);
        @SuppressWarnings("unchecked")
        T rueckMeldung = (T) tableMdl.getValueAt(0,
                typColumn);
        return rueckMeldung;
    }

    private AKReferenceAwareTableModel<CBVorgangNiederlassung> createWitaTableModelWithObject(
            CBVorgang cbVorgang) {
        AKReferenceAwareTableModel<CBVorgangNiederlassung> tableMdl = sut.createCBVorgangTableModel();
        addCbVorgang(tableMdl, cbVorgang);
        return tableMdl;
    }

    private void addCbVorgang(AKReferenceAwareTableModel<CBVorgangNiederlassung> tableMdl, CBVorgang witaCBVorgang) {
        tableMdl.addObject(new CBVorgangNiederlassung(witaCBVorgang, "Augsburg"));
    }

}


