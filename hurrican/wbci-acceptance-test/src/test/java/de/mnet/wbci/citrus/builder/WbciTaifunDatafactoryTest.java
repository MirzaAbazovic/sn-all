package de.mnet.wbci.citrus.builder;

import static org.mockito.Mockito.*;

import javax.sql.*;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.billing.AdresseBuilder;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.billing.factory.GeneratedTaifunData;
import de.augustakom.hurrican.model.billing.factory.TaifunDataHandler;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.WbciCdmVersion;

@Test(groups = BaseTest.UNIT)
public class WbciTaifunDatafactoryTest {

    @Mock
    private TaifunDataHandler taifunDataHandler;
    @InjectMocks
    @Spy
    private WbciTaifunDatafactory testling = new WbciTaifunDatafactory(mock(DataSource.class), mock(DataSource.class), WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateKunde() throws Exception {
        doNothing().when(testling).checkKunde(any(Kunde.class));
        when(taifunDataHandler.getNextId(any())).thenReturn(12345L);
        GeneratedTaifunData kunde = testling.createKunde(true, null, false);
        Assert.assertNotNull(kunde);
    }

    @Test(expectedExceptions = CitrusRuntimeException.class, expectedExceptionsMessageRegExp = "unable to create valid taifun data.*")
    public void testCreateKundeException() throws Exception {
        doThrow(WbciTaifunDatafactory.InvalidRandomDataException.class).when(testling).checkKunde(any(Kunde.class));
        when(taifunDataHandler.getNextId(any())).thenReturn(12345L);
        testling.createKunde(true, null, false);
    }

    @Test(expectedExceptions = WbciTaifunDatafactory.InvalidRandomDataException.class, expectedExceptionsMessageRegExp = ".*nachname.*")
    public void testCheckKundeException() throws Exception {
        Kunde kunde = new Kunde();
        kunde.setVorname("bla");
        kunde.setName(null);
        testling.checkKunde(kunde);
    }

    @Test
    public void testCheckKunde() throws Exception {
        Kunde kunde = new Kunde();
        kunde.setName("Hans");
        kunde.setVorname("Maier");
        testling.checkKunde(kunde);
    }

    @Test
    public void testCeckAddres() throws Exception {
        Adresse address = new AdresseBuilder().build();
        address.setNummer("43");
        testling.checkAddress(address);
    }

    @Test(expectedExceptions = WbciTaifunDatafactory.InvalidRandomDataException.class, expectedExceptionsMessageRegExp = ".*5 nummerische Zeichen")
    public void testCeckAddresException() throws Exception {
        Adresse address = new AdresseBuilder().build();
        address.setPlz("3343");
        address.setNummer("43");
        testling.checkAddress(address);
    }
}