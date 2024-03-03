/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.11.2010 13:30:40
 */

package de.augustakom.hurrican.service.cc.impl.equipment;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HWDluBuilder;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChange;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangeDlu;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangeDluBuilder;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * TestNG Klasse fuer {@link HWBaugruppenChangeCpsReInitExecuter}
 */
@Test(groups = BaseTest.UNIT)
public class HWBaugruppenChangeCpsReInitExecuterTest extends BaseTest {

    private HWBaugruppenChangeCpsReInitExecuter cut;
    private RangierungsService rangierungsService;
    private CCAuftragService auftragService;
    private CPSService cpsService;
    private HWService hwService;

    class EQ2ADEntity {
        boolean select; //wird dieser Auftrag gezogen?
        Long statusId;
        Date kuendigung;

        EQ2ADEntity(boolean select, Long statusId, Date kuendigung) {
            this.select = select;
            this.statusId = statusId;
            this.kuendigung = kuendigung;
        }
    }

    class ADEntity {
        boolean expectWarnings;
        Long auftragId;
        boolean hasCPSTx;
        Long billingId;
        boolean select; //wird dieser Auftrag gezogen?

        ADEntity(boolean expectWarnings, Long auftragId, boolean hasCPSTx, Long billingId, boolean select) {
            this.expectWarnings = expectWarnings;
            this.auftragId = auftragId;
            this.hasCPSTx = hasCPSTx;
            this.billingId = billingId;
            this.select = select;
        }
    }

    @DataProvider
    public Object[][] dataProviderValidateValues() {
        return new Object[][] {
                // hwBgChangeDlu, hwBgChangeState, expectException
                { null, null, true },
                { buildHwBgChangeDlu(null), null, true },
                { buildHwBgChangeDlu(getDluBuilder(1L)), null, true },
                { buildHwBgChangeDlu(getDluBuilder(null)), null, true },
                { buildHwBgChangeDlu(getDluBuilder(1L)), createHwBgChangeState(getChangeState("closed")), true },
                { buildHwBgChangeDlu(getDluBuilder(1L)), createHwBgChangeState(getChangeState("planing")), true },
                { buildHwBgChangeDlu(getDluBuilder(1L)), createHwBgChangeState(getChangeState("prepared")), true },
                { buildHwBgChangeDlu(getDluBuilder(1L)), createHwBgChangeState(getChangeState("executed")), false },
                { buildHwBgChangeDlu(getDluBuilder(1L)), createHwBgChangeState(getChangeState("cancelled")), true },
        };
    }

    @DataProvider
    public Object[][] dataProviderFindAuftragsDaten4Equipments() {
        return new Object[][] {
                // Map<Long, List<EQ2ADEntity>>
                { createEQ2ADMapping(1) },
                { createEQ2ADMapping(2) },
                { createEQ2ADMapping(3) },
                { createEQ2ADMapping(4) },
                { createEQ2ADMapping(5) },
                { createEQ2ADMapping(6) },
        };
    }

    @DataProvider
    public Object[][] dataProviderGetReInitAuftragDaten() {
        return new Object[][] {
                // List<ADEntity>
                { createADList(1) },
                { createADList(2) },
                { createADList(3) },
                { createADList(4) },
                { createADList(5) },
                { createADList(6) },
                { createADList(7) },
        };
    }

    private HWDluBuilder getDluBuilder(Long withId) {
        return new HWDluBuilder().withId(withId).setPersist(false);
    }

    private HWBaugruppenChangeDlu buildHwBgChangeDlu(HWDluBuilder dluBuilder) {
        return new HWBaugruppenChangeDluBuilder()
                .withDluBuilder(dluBuilder)
                .setPersist(false)
                .build();
    }

    private HWBaugruppenChange.ChangeState getChangeState(String identifier) {
        if (StringUtils.equals(identifier, "planing")) {
            return HWBaugruppenChange.ChangeState.CHANGE_STATE_PLANNING;
        }
        else if (StringUtils.equals(identifier, "prepared")) {
            return HWBaugruppenChange.ChangeState.CHANGE_STATE_PREPARED;
        }
        else if (StringUtils.equals(identifier, "cancelled")) {
            return HWBaugruppenChange.ChangeState.CHANGE_STATE_CANCELLED;
        }
        else if (StringUtils.equals(identifier, "executed")) {
            return HWBaugruppenChange.ChangeState.CHANGE_STATE_EXECUTED;
        }
        else if (StringUtils.equals(identifier, "closed")) {
            return HWBaugruppenChange.ChangeState.CHANGE_STATE_CLOSED;
        }
        return null;
    }

    private Reference createHwBgChangeState(HWBaugruppenChange.ChangeState changeState) {
        if (changeState != null) {
            Reference hwBgChangeState = new Reference();
            hwBgChangeState.setId(changeState.refId());
            return hwBgChangeState;
        }
        return null;
    }

    private Map<Long, List<EQ2ADEntity>> createEQ2ADMapping(Integer variante) {
        Map<Long, List<EQ2ADEntity>> adMapping = new HashMap<>();
        switch (variante) {
            case 1:
                return null;
            case 2:
                adMapping.put(1L, null);
                break;
            case 3:
                List<EQ2ADEntity> adList31 = new ArrayList<>();
                adList31.add(new EQ2ADEntity(true, 6000L, null));
                adMapping.put(1L, adList31); // in Betrieb
                break;
            case 4:
                List<EQ2ADEntity> adList41 = new ArrayList<>();
                adList41.add(new EQ2ADEntity(true, 6000L, null));
                adMapping.put(1L, adList41); // in Betrieb
                List<EQ2ADEntity> adList42 = new ArrayList<>();
                adList42.add(new EQ2ADEntity(false, 9100L, DateTools.changeDate(new Date(), Calendar.DAY_OF_MONTH, -1)));
                adMapping.put(2L, adList42); // zu gestern gekündigt
                break;
            case 5:
                List<EQ2ADEntity> adList51 = new ArrayList<>();
                adList51.add(new EQ2ADEntity(true, 6000L, null));
                adMapping.put(1L, adList51); // in Betrieb
                List<EQ2ADEntity> adList52 = new ArrayList<>();
                adList52.add(new EQ2ADEntity(false, 9800L, DateTools.changeDate(new Date(), Calendar.DAY_OF_MONTH, -1)));
                adList52.add(new EQ2ADEntity(false, 3400L, null));
                adList52.add(new EQ2ADEntity(true, 6000L, null));
                adMapping.put(2L, adList52); // zu gestern gekündigt + storniert + in Betrieb
                break;
            case 6:
                List<EQ2ADEntity> adList61 = new ArrayList<>();
                adList61.add(new EQ2ADEntity(false, 1150L, null));
                adList61.add(new EQ2ADEntity(false, 1100L, null));
                adMapping.put(1L, adList61); // storno + aus Taifun übernommen
                adMapping.put(2L, null); // kein Auftrag
                List<EQ2ADEntity> adList63 = new ArrayList<>();
                adList63.add(new EQ2ADEntity(true, 9105L, DateTools.changeDate(new Date(), Calendar.DAY_OF_MONTH, 1)));
                adMapping.put(3L, adList63); // zu morgen Kündigung in Umlauf
                List<EQ2ADEntity> adList64 = new ArrayList<>();
                adList64.add(new EQ2ADEntity(true, 6000L, null));
                adMapping.put(4L, adList64); // in Betrieb
                break;
        }
        return adMapping;
    }

    private List<ADEntity> createADList(Integer variante) {
        List<ADEntity> adList = new ArrayList<>();
        switch (variante) {
            case 1:
                return null;
            case 2:
                // Auftrag ohne Billing ID
                adList.add(new ADEntity(true, 1L, false, null, false));
                break;
            case 3:
                // Auftrag ohne CPS-Tx
                adList.add(new ADEntity(false, 1L, false, 1L, false));
                break;
            case 4:
                // Auftrag mit CPS-Tx
                adList.add(new ADEntity(false, 1L, true, 1L, true));
                break;
            case 5:
                // Auftrag mit CPS-Tx
                adList.add(new ADEntity(false, 1L, true, 1L, true));
                // Auftrag mit CPS-Tx und gleicher Billing ID
                adList.add(new ADEntity(false, 2L, true, 1L, false));
                break;
            case 6:
                // Auftrag mit CPS-Tx
                adList.add(new ADEntity(false, 1L, true, 1L, true));
                // Auftrag mit CPS-Tx und anderer Billing ID
                adList.add(new ADEntity(false, 2L, true, 2L, true));
                break;
            case 7:
                // Auftrag mit CPS-Tx
                adList.add(new ADEntity(false, 1L, true, 1L, true));
                // Auftrag ohne CPS-Tx und gleicher Billing ID
                adList.add(new ADEntity(false, 2L, false, 1L, false));
                // Auftrag mit CPS-Tx und gleicher Billing ID
                adList.add(new ADEntity(false, 3L, true, 1L, false));
                // Auftrag mit CPS-Tx und anderer Billing ID
                adList.add(new ADEntity(false, 4L, true, 2L, true));
                // Auftrag ohne CPS-Tx und anderer Billing ID
                adList.add(new ADEntity(false, 5L, false, 3L, false));
                // Auftrag ohne Billing ID
                adList.add(new ADEntity(true, 6L, false, null, false));
                break;
        }
        return adList;
    }

    @BeforeMethod
    public void setUp() {
        cut = new HWBaugruppenChangeCpsReInitExecuter();

        rangierungsService = mock(RangierungsService.class);
        cut.setRangierungsService(rangierungsService);

        auftragService = mock(CCAuftragService.class);
        cut.setAuftragService(auftragService);

        cpsService = mock(CPSService.class);
        cut.setCpsService(cpsService);

        hwService = mock(HWService.class);
        cut.setHwService(hwService);
    }

    /**
     * Test für validateValues() und (implizit) für isCpsReInitActionAllowed()
     */
    @Test(dataProvider = "dataProviderValidateValues")
    public void testValidateValues(HWBaugruppenChangeDlu hwBgChangeDlu, Reference hwBgChangeState,
            boolean expectException) {
        cut.setHwBgChangeDlu(hwBgChangeDlu);
        cut.setHwBgChangeState(hwBgChangeState);

        try {
            cut.validateValues();
            assertEquals(expectException, false, "StoreException hätte geworfen werden müssen!");
        }
        catch (StoreException e) {
            assertEquals(expectException, true, "Unerwartete StoreException gefangen!");
        }
    }


    @Test(dataProvider = "dataProviderFindAuftragsDaten4Equipments")
    public void testFindAuftragsDaten4Equipments(Map<Long, List<EQ2ADEntity>> adMapping) throws FindException {
        int expectedResultSize = 0;
        List<Equipment> dluEquipments = null;

        if ((adMapping != null) && (!adMapping.isEmpty())) {
            dluEquipments = new ArrayList<>();
            Set<Long> eqKeys = adMapping.keySet();
            for (Long eqId : eqKeys) {
                // Equipment
                Equipment equipment = new Equipment();
                equipment.setId(eqId);
                dluEquipments.add(equipment);

                // AuftragDaten
                List<EQ2ADEntity> adEntities = adMapping.get(eqId);
                if (CollectionTools.isNotEmpty(adEntities)) {
                    List<AuftragDaten> adList = new ArrayList<>();
                    for (EQ2ADEntity adEntity : adEntities) {
                        AuftragDaten auftragDaten = new AuftragDaten();
                        auftragDaten.setStatusId(adEntity.statusId);
                        auftragDaten.setKuendigung(adEntity.kuendigung);
                        adList.add(auftragDaten);
                        if (adEntity.select) {
                            expectedResultSize++;
                        }
                    }
                    when(auftragService.findAuftragDatenByEquipment(eqId)).thenReturn(adList);
                }
                else {
                    when(auftragService.findAuftragDatenByEquipment(eqId)).thenReturn(null);
                }
            }
        }

        List<AuftragDaten> dluAuftragDaten = cut.findAuftragsDaten4Equipments(dluEquipments);
        int resultSize = (dluAuftragDaten != null) ? dluAuftragDaten.size() : 0;
        assertEquals(resultSize, expectedResultSize, "Die Port AuftragDaten weichen von erwartetem Ergebnis ab!");
    }

    @Test(dataProvider = "dataProviderGetReInitAuftragDaten")
    public void testGetReInitAuftragDaten(List<ADEntity> adList) throws FindException {
        int expectedResultSize = 0;
        boolean expectWarnings = false;
        List<AuftragDaten> dluAuftragDaten = null;

        if (CollectionTools.isNotEmpty(adList)) {
            dluAuftragDaten = new ArrayList<>();
            for (ADEntity adEntity : adList) {
                AuftragDaten auftragDaten = new AuftragDaten();
                auftragDaten.setAuftragNoOrig(adEntity.billingId);
                auftragDaten.setAuftragId(adEntity.auftragId);
                dluAuftragDaten.add(auftragDaten);

                if (adEntity.hasCPSTx) {
                    List<CPSTransaction> cpsTransactions = new ArrayList<>();
                    CPSTransaction cpsTx = new CPSTransaction();
                    cpsTransactions.add(cpsTx);
                    when(cpsService.findSuccessfulCPSTransaction4TechOrder(adEntity.auftragId)).thenReturn(cpsTransactions);
                }
                else {
                    when(cpsService.findSuccessfulCPSTransaction4TechOrder(adEntity.auftragId)).thenReturn(null);
                }

                if (adEntity.select) {
                    expectedResultSize++;
                }

                if (adEntity.expectWarnings) {
                    expectWarnings = true;
                }
            }
        }

        List<AuftragDaten> reInitAuftragDaten = cut.getReInitAuftragDaten(dluAuftragDaten);
        int resultSize = (reInitAuftragDaten != null) ? reInitAuftragDaten.size() : 0;
        assertEquals(resultSize, expectedResultSize, "ReInit AuftragDaten weichen von erwartetem Ergebnis ab!");
        assertEquals(StringUtils.length(cut.getWarnings()) > 0, expectWarnings,
                "Warnungen entsprechen nicht dem erwarteten Ergebnis!");
    }

}
