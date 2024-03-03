package de.augustakom.hurrican.service.cc.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.testng.AssertJUnit.*;

import java.util.*;
import com.google.common.collect.ImmutableList;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.dao.cc.IABudgetDAO;
import de.augustakom.hurrican.dao.cc.IAMaterialDAO;
import de.augustakom.hurrican.dao.cc.LagerDAO;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.innenauftrag.IA;
import de.augustakom.hurrican.model.cc.innenauftrag.IABudget;
import de.augustakom.hurrican.model.cc.innenauftrag.IaLevel3;
import de.augustakom.hurrican.model.cc.innenauftrag.IaLevel5;
import de.augustakom.hurrican.model.cc.innenauftrag.IaLevel5Builder;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.RegistryService;

@Test(groups = BaseTest.UNIT)
public class InnenauftragServiceImplTest {

    static final Float PROJEKTE_SCHWELLWERT = 20000.0f;

    @Mock
    private RegistryService registryService;

    @Mock
    private IAMaterialDAO materialDAO;

    @Mock
    private IABudgetDAO budgetDAO;

    @Mock
    private LagerDAO lagerDAO;

    @Spy
    @InjectMocks
    private InnenauftragServiceImpl cut;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @DataProvider
    public Object[][] testFindLevel3Projekt4PD_DataProvider() {
        IABudget low = createIaBudget(PROJEKTE_SCHWELLWERT - 17500.0f, false);
        IABudget middle = createIaBudget(PROJEKTE_SCHWELLWERT - 10000.0f, false);
        IABudget high = createIaBudget(PROJEKTE_SCHWELLWERT - 1000.0f, false);
        IABudget cancelled = createIaBudget(PROJEKTE_SCHWELLWERT - 1000.0f, true);
        return new Object[][] {
                { 1000.0f, null, true },
                { 1000.0f, Arrays.asList(), true },
                { 1000.0f, Arrays.asList(cancelled), true },
                { 1000.0f, Arrays.asList(low, middle), true },
                { 1000.0f, Arrays.asList(low), true },
                { 1000.0f, Arrays.asList(low, cancelled), true },
                { 1000.0f, Arrays.asList(high), false },
                { 1000.0f, Arrays.asList(low, high), false },
        };
    }

    private IA createIA(Long id) {
        IA innenauftrag = new IA();
        innenauftrag.setId(1L);
        return innenauftrag;
    }

    private IaLevel3 createIaLevel3(String name) {
        IaLevel3 iaLevel3 = new IaLevel3();
        iaLevel3.setName(name);
        return iaLevel3;
    }

    private IABudget createIaBudget(Float budget, boolean isCancelled) {
        IABudget iaBudget = new IABudget();
        iaBudget.setBudget(budget);
        iaBudget.setCancelled(isCancelled);
        return iaBudget;
    }

    @Test(dataProvider = "testFindLevel3Projekt4PD_DataProvider")
    public void testFindLevel3Projekt4PD(float budget, List<IABudget> iaBudgets, boolean expectStandardprojekt)
            throws FindException {
        IA ia = createIA(1L);
        IaLevel3 grossprojekt = createIaLevel3("Grossprojekt");
        IaLevel3 standardprojekt = createIaLevel3("Standardprojekt");

        when(registryService.getIntValue(RegistryService.REGID_IA_BUDGET_PROJEKTE_SCHWELLWERT))
                .thenReturn(PROJEKTE_SCHWELLWERT.intValue());
        doReturn(iaBudgets).when(cut).findBudgets4IA(ia.getId());
        doReturn(grossprojekt).when(cut).findLevel3Projekt(RegistryService.REGID_IA_BUDGET_EBENE3_PD_GROSSPROJEKTE_GK);
        doReturn(standardprojekt).when(cut).findLevel3Projekt(RegistryService.REGID_IA_BUDGET_EBENE3_PD_STANDARDPROJEKTE_GK);

        IaLevel3 result = cut.findLevel3Projekt4PD(ia, budget);
        IaLevel3 expectedProject = (expectStandardprojekt) ? standardprojekt : grossprojekt;
        assertTrue(expectedProject.getName().equals(result.getName()));
    }

    public void testGetLevel5ByTaifunProduktName() {
        final Produkt produkt = new ProduktBuilder().withAnschlussart(UUID.randomUUID().toString()).build();
        final IaLevel5 iaLevel5ToFind = new IaLevel5Builder().withName("abc " + produkt.getAnschlussart() + " xyz").build();
        final IaLevel5 iaLevel5NotToFind = new IaLevel5Builder().withName(UUID.randomUUID().toString()).build();
        final IaLevel5 result = cut.getLevel5ByTaifunProduktName(ImmutableList.of(iaLevel5NotToFind, iaLevel5ToFind), produkt.getAnschlussart());

        assertThat(result, equalTo(iaLevel5ToFind));
    }

    public void testGetLevel5ByTaifunProduktNameNotFound() {
        final Produkt produkt = new ProduktBuilder().withAnschlussart(UUID.randomUUID().toString()).build();
        final IaLevel5 iaLevel5ToFind = new IaLevel5Builder().withName(null).build();
        final IaLevel5 iaLevel5NotToFind = new IaLevel5Builder().withName(UUID.randomUUID().toString()).build();
        final IaLevel5 result = cut.getLevel5ByTaifunProduktName(ImmutableList.of(iaLevel5NotToFind, iaLevel5ToFind), produkt.getAnschlussart());

        assertNull(result);
    }

    public void testGetLevel5ByTaifunProduktNameAmbiguous() {
        final IaLevel5 l5ConnectLan = new IaLevel5Builder().withName("Connect-LAN").build();
        final IaLevel5 l5Connect = new IaLevel5Builder().withName("Connect").build();
        final ImmutableList<IaLevel5> l5Entries = ImmutableList.of(l5ConnectLan, l5Connect);

        final IaLevel5 matchedL5 = cut.getLevel5ByTaifunProduktName(l5Entries, "Connect");
        assertNotNull(matchedL5);
        assertEquals(l5Connect.getName(), matchedL5.getName());
    }
}
