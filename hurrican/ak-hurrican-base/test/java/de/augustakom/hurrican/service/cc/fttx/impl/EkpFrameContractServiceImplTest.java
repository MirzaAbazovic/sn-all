/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.04.2012 06:59:56
 */
package de.augustakom.hurrican.service.cc.fttx.impl;

import static de.augustakom.common.BaseTest.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.dao.cc.fttx.A10NspPortDao;
import de.augustakom.hurrican.dao.cc.fttx.EkpFrameContractDAO;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragAktion;
import de.augustakom.hurrican.model.cc.AuftragAktionBuilder;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.HWOltBuilder;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnungBuilder;
import de.augustakom.hurrican.model.cc.fttx.A10Nsp;
import de.augustakom.hurrican.model.cc.fttx.A10NspBuilder;
import de.augustakom.hurrican.model.cc.fttx.A10NspPort;
import de.augustakom.hurrican.model.cc.fttx.A10NspPortBuilder;
import de.augustakom.hurrican.model.cc.fttx.Auftrag2EkpFrameContract;
import de.augustakom.hurrican.model.cc.fttx.Auftrag2EkpFrameContractBuilder;
import de.augustakom.hurrican.model.cc.fttx.EkpFrameContract;
import de.augustakom.hurrican.model.cc.fttx.EkpFrameContractBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWOlt;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.PhysikService;

@Test(groups = { UNIT })
public class EkpFrameContractServiceImplTest extends BaseTest {

    @InjectMocks
    @Spy
    private EkpFrameContractServiceImpl cut;

    @Mock
    private EkpFrameContractDAO ekpFrameContractDao;
    @Mock
    private A10NspPortDao a10NspPortDao;
    @Mock
    private PhysikService physikService;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @DataProvider
    public Object[][] findA10NspPortDataProvider() {
        HWOlt oltOnlyOne = new HWOltBuilder().withRandomId().setPersist(false).build();
        Set<HWOlt> oltOnlyOneSet = Sets.newHashSet();
        oltOnlyOneSet.add(oltOnlyOne);

        HWOlt oltFirst = new HWOltBuilder().withRandomId().setPersist(false).build();
        HWOlt oltSecond = new HWOltBuilder().withRandomId().setPersist(false).build();
        Set<HWOlt> oltMultipleSet = Sets.newHashSet();
        oltMultipleSet.add(oltFirst);
        oltMultipleSet.add(oltSecond);

        A10NspPort defaultA10Nsp = new A10NspPortBuilder().withRandomId().setPersist(false).build();
        A10NspPort oltOnlyOneA10Nsp = new A10NspPortBuilder().withRandomId().withOlts(oltOnlyOneSet).setPersist(false).build();
        A10NspPort oltSecondA10Nsp = new A10NspPortBuilder().withRandomId().withOlts(oltMultipleSet).setPersist(false).build();

        Map<A10NspPort, Boolean> a10NspPortMap = new HashMap<A10NspPort, Boolean>();
        a10NspPortMap.put(defaultA10Nsp, Boolean.TRUE);
        a10NspPortMap.put(oltOnlyOneA10Nsp, Boolean.FALSE);
        a10NspPortMap.put(oltSecondA10Nsp, Boolean.FALSE);

        EkpFrameContract ekpFrameContract = new EkpFrameContractBuilder().setPersist(false).build();
        ekpFrameContract.getA10NspPortsOfEkp().putAll(a10NspPortMap);

        // @formatter:off
        return new Object[][] {
                { ekpFrameContract, Long.valueOf(0), defaultA10Nsp },    // nur Default A10NspPort zugeordnet
                { ekpFrameContract, oltOnlyOne.getId(), oltOnlyOneA10Nsp }, // A10NspPort zur OLT erwartet
                { ekpFrameContract, oltSecond.getId(), oltSecondA10Nsp },   // A10NspPort zur OLT erwartet; allerdings mehrere OLTs hinterlegt
            };
        // @formatter:on
    }

    @Test(dataProvider = "findA10NspPortDataProvider")
    public void findA10NspPort(EkpFrameContract ekpFrameContract, Long oltId, A10NspPort expectedA10Nsp) {
        A10NspPort result = cut.findA10NspPort(ekpFrameContract, oltId);
        assertNotNull(result);
        assertThat(result.getId(), equalTo(expectedA10Nsp.getId()));
    }

    public void checkA10NspPortAssignableToEkpNoDuplicate() {
        HWOlt oltAssignedToEkp = new HWOltBuilder().withRandomId().build();
        HWOlt oltNotAssignedToEkp = new HWOltBuilder().withRandomId().build();

        A10NspPort a10NspPortToCheck = new A10NspPortBuilder()
                .withOlts(ImmutableSet.of(oltNotAssignedToEkp))
                .withVbzBuilder(new VerbindungsBezeichnungBuilder())
                .withRandomId()
                .build();
        A10NspPort a10NspPortAssigned = new A10NspPortBuilder()
                .withOlts(ImmutableSet.of(oltAssignedToEkp))
                .withVbzBuilder(new VerbindungsBezeichnungBuilder())
                .withRandomId()
                .build();
        EkpFrameContract ekpFc = new EkpFrameContractBuilder()
                .addA10NspPort(a10NspPortAssigned, false)
                .build();

        AKWarnings warningsResult = cut.checkA10NspPortAssignableToEkp(ekpFc, a10NspPortToCheck);

        assertThat(warningsResult.isEmpty(), equalTo(true));
    }

    public void checkA10NspPortAssignableToEkpWithDuplicate() {
        HWOlt oltAssignedToEkp1 = new HWOltBuilder().withRandomId().build();
        HWOlt oltAssignedToEkp2 = new HWOltBuilder().withRandomId().build();

        A10NspPort a10NspPortToCheck = new A10NspPortBuilder()
                .withOlts(ImmutableSet.of(oltAssignedToEkp2))
                .withVbzBuilder(new VerbindungsBezeichnungBuilder())
                .withRandomId()
                .build();
        A10NspPort a10NspPortAssigned = new A10NspPortBuilder()
                .withOlts(ImmutableSet.of(oltAssignedToEkp1, oltAssignedToEkp2))
                .withVbzBuilder(new VerbindungsBezeichnungBuilder())
                .withRandomId()
                .build();
        EkpFrameContract ekpFc = new EkpFrameContractBuilder()
                .addA10NspPort(a10NspPortAssigned, false)
                .build();

        AKWarnings warningsResult = cut.checkA10NspPortAssignableToEkp(ekpFc, a10NspPortToCheck);

        assertThat(warningsResult.isNotEmpty(), equalTo(true));
        assertThat(warningsResult.getAKMessages(), hasSize(1));

        assertThat(warningsResult.getWarningsAsText(), containsString(oltAssignedToEkp2.getGeraeteBez()));
        assertThat(warningsResult.getWarningsAsText(), containsString(a10NspPortToCheck.getId().toString()));
    }

    public void filterNotAssignableOlts() {
        HWOlt oltNotToFilter = new HWOltBuilder().withRandomId().build();
        HWOlt oltToFilter = new HWOltBuilder().withRandomId().build();

        List<HWOlt> olts = Lists.newArrayList(oltNotToFilter, oltToFilter);

        A10NspPort a10NspPort1 = new A10NspPortBuilder()
                .withRandomId()
                .withOlts(Sets.newHashSet(oltToFilter))
                .build();
        A10NspPort a10NspPort2 = new A10NspPortBuilder()
                .withRandomId()
                .build();

        EkpFrameContract ekpFc1 = new EkpFrameContractBuilder()
                .withRandomId()
                .addA10NspPort(a10NspPort1, false)
                .addA10NspPort(a10NspPort2, false)
                .build();
        EkpFrameContract ekpFc2 = new EkpFrameContractBuilder()
                .withRandomId()
                .addA10NspPort(a10NspPort2, false)
                .build();

        when(ekpFrameContractDao.findAll()).thenReturn(ImmutableList.of(ekpFc1, ekpFc2));

        AKWarnings warningsReturned = cut.filterNotAssignableOlts(a10NspPort2, olts);

        assertThat(warningsReturned.isNotEmpty(), equalTo(true));
        assertThat(warningsReturned.getAKMessages(), hasSize(1));

        assertThat(warningsReturned.getWarningsAsText(), containsString(ekpFc1.getFrameContractId()));
        assertThat(warningsReturned.getWarningsAsText(), containsString(oltToFilter.getGeraeteBez()));

        assertThat(olts, hasSize(1));
        assertThat(olts, contains(oltNotToFilter));
    }

    public void findEkpFrameContractsByA10NspPort() {
        A10NspPort a10NspPort = new A10NspPortBuilder().withRandomId().build();
        EkpFrameContract ekpFc1 = new EkpFrameContractBuilder()
                .withRandomId()
                .addA10NspPort(a10NspPort, false)
                .build();
        EkpFrameContract ekpFc2 = new EkpFrameContractBuilder()
                .withRandomId()
                .build();

        when(ekpFrameContractDao.findAll()).thenReturn(ImmutableList.of(ekpFc1, ekpFc2));

        List<EkpFrameContract> frameContractsReturned = cut.findEkpFrameContractsByA10NspPort(a10NspPort);

        assertThat(frameContractsReturned, hasSize(1));
        assertThat(frameContractsReturned, contains(ekpFc1));
    }

    public void deleteA10Nsp() throws FindException {
        A10NspBuilder a10NspBuilder = new A10NspBuilder();
        A10NspPort a10NspPort1 = new A10NspPortBuilder().withA10NspBuilder(a10NspBuilder).withRandomId().build();
        A10NspPort a10NspPort2 = new A10NspPortBuilder().withA10NspBuilder(a10NspBuilder).withRandomId().build();

        doReturn(Arrays.asList(a10NspPort1, a10NspPort2)).when(cut).findA10NspPorts(any(A10Nsp.class));

        cut.deleteA10Nsp(a10NspBuilder.get());

        verify(a10NspPortDao, times(1)).delete(any(A10Nsp.class));
        verify(a10NspPortDao, times(2)).delete(any(A10NspPort.class));
        verify(physikService, times(2)).deleteVerbindungsBezeichnung(any(VerbindungsBezeichnung.class));
    }


    public void cancelEkpFrameContractAssignment() throws StoreException, FindException {
        Auftrag auftrag = new AuftragBuilder().withRandomId().setPersist(false).build();
        AuftragAktion aktion = new AuftragAktionBuilder().withRandomId().setPersist(false).build();

        List<Auftrag2EkpFrameContract> ekpAssignments = ImmutableList.of(
                new Auftrag2EkpFrameContractBuilder().withAuftragAktionsIdRemove(aktion.getId()).withAssignedTo(LocalDate.now()).setPersist(false).build(),
                new Auftrag2EkpFrameContractBuilder().withAuftragAktionsIdAdd(aktion.getId()).setPersist(false).build()
        );

        when(ekpFrameContractDao.queryByExample(any(Auftrag2EkpFrameContract.class), eq(Auftrag2EkpFrameContract.class))).thenReturn(ekpAssignments);

        cut.cancelEkpFrameContractAssignment(auftrag.getId(), aktion);
        verify(ekpFrameContractDao).deleteAuftrag2EkpFrameContract(ekpAssignments.get(1).getId());
        verify(ekpFrameContractDao).store(ekpAssignments.get(0));
        assertNull(ekpAssignments.get(0).getAuftragAktionsIdRemove());
        assertThat(ekpAssignments.get(0).getAssignedTo(), equalTo(LocalDate.from(DateTools.getHurricanEndDate().toInstant().atZone(ZoneId.systemDefault()))));
    }

}



