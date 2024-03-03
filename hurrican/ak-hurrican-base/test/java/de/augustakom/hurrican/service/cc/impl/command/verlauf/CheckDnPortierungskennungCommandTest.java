package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import static org.mockito.Mockito.*;

import java.util.*;

import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.RufnummerBuilder;
import de.augustakom.hurrican.model.TNB;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.service.cc.impl.command.CommandBaseTest;

/**
 * CheckDnPortierungskennungTest
 */
@Test(groups = { BaseTest.UNIT })
public class CheckDnPortierungskennungCommandTest extends CommandBaseTest {

    @Spy
    private CheckDnPortierungskennungCommand cut;

    @BeforeMethod
    public void setup() {
        cut = new CheckDnPortierungskennungCommand();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCheckPortierungskennung() throws Exception {
        List<Rufnummer> rufnummern = new ArrayList<>();
        rufnummern.add((new RufnummerBuilder()).withActCarrier(TNB.AKOM.carrierName, TNB.AKOM.tnbKennung).withOnKz("0821").setPersist(false).build());
        rufnummern.add((new RufnummerBuilder()).withActCarrier(TNB.MNET.carrierName, TNB.MNET.tnbKennung).withOnKz("0821").setPersist(false).build());
        rufnummern.add((new RufnummerBuilder()).withActCarrier("DTAG", "D001").withOnKz("0821").setPersist(false).build());
        rufnummern.add((new RufnummerBuilder()).withActCarrier("T-ONLINE", "D1").withOnKz("0821").setPersist(false).build());

        doReturn((new ProduktBuilder()).withMaxDnCount(1).setPersist(false).build()).when(cut).getProdukt();
        doReturn(rufnummern).when(cut).getRufnummern();

        assertCommandResult(cut.execute(), ServiceCommandResult.CHECK_STATUS_OK, null, cut.getClass());
    }

    @Test
    public void testCheckPortierungskennungFails() throws Exception {
        List<Rufnummer> rufnummern = new ArrayList<>();
        rufnummern.add((new RufnummerBuilder()).withActCarrier(TNB.AKOM.carrierName, TNB.AKOM.tnbKennung).withOnKz("0821").setPersist(false).build());
        rufnummern.add((new RufnummerBuilder()).withActCarrier("TELDA", "TELDA").withOnKz("0170").withDnBase("12345").setPersist(false).build());

        doReturn((new ProduktBuilder()).withMaxDnCount(1).setPersist(false).build()).when(cut).getProdukt();
        doReturn(rufnummern).when(cut).getRufnummern();

        assertCommandResult(cut.execute(), ServiceCommandResult.CHECK_STATUS_INVALID,
                "Die Portierungskennung fuer [Rufnummer-Nr: null, Vorwahl: 0170, Rufnummerstamm: 12345] ist nicht vorhanden oder es handelt sich um eine Mobilfunk-Nummer.", cut.getClass());

        rufnummern.clear();
        rufnummern.add((new RufnummerBuilder()).withActCarrier("T-ONLINE", null).withOnKz("0821").withDnBase("4444").setPersist(false).build());

        doReturn(rufnummern).when(cut).getRufnummern();
        
        assertCommandResult(cut.execute(), ServiceCommandResult.CHECK_STATUS_INVALID,
                "Die Portierungskennung fuer [Rufnummer-Nr: null, Vorwahl: 0821, Rufnummerstamm: 4444] ist nicht vorhanden oder es handelt sich um eine Mobilfunk-Nummer.", cut.getClass());

    }
}
