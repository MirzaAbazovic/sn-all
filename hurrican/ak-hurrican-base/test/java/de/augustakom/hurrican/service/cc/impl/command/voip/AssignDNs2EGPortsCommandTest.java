/**
 *
 */
package de.augustakom.hurrican.service.cc.impl.command.voip;

import static org.testng.Assert.*;

import java.util.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.AuftragVoIPDN2EGPort;
import de.augustakom.hurrican.model.cc.AuftragVoIPDN2EGPortBuilder;
import de.augustakom.hurrican.model.cc.EndgeraetPortBuilder;
import de.augustakom.hurrican.model.shared.view.voip.AuftragVoipDNView;
import de.augustakom.hurrican.model.shared.view.voip.SelectedPortsView;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;

/**
 * {@link AssignDNs2EGPortsCommand} Unit Test
 */
@Test(groups = BaseTest.UNIT)
public class AssignDNs2EGPortsCommandTest extends AbstractAssignVoIPDNs2EGPortsTest {

    private AssignDNs2EGPortsCommand cmd;

    @BeforeMethod
    public void setUp() {
        cmd = new AssignDNs2EGPortsCommand();
    }

    @Test(expectedExceptions = { HurricanServiceCommandException.class })
    void checkHauptrufnummern_HRNIsZero() throws HurricanServiceCommandException {
        final Collection<AuftragVoipDNView> auftragVoipDNViews = new ArrayList<AuftragVoipDNView>();
        cmd.setPortCount(Integer.valueOf(1));
        cmd.setAuftragVoipDNViews(auftragVoipDNViews);
        cmd.checkHauptrufnummern();
    }

    @Test(expectedExceptions = { HurricanServiceCommandException.class })
    void checkHauptrufnummern_HRNIsTooHigh() throws HurricanServiceCommandException {
        AuftragVoipDNView auftragVoipDNView1 = new AuftragVoipDNView();
        auftragVoipDNView1.setMainNumber(Boolean.TRUE);
        AuftragVoipDNView auftragVoipDNView2 = new AuftragVoipDNView();
        auftragVoipDNView2.setMainNumber(Boolean.TRUE);
        final Collection<AuftragVoipDNView> auftragVoipDNViews = new ArrayList<AuftragVoipDNView>();
        auftragVoipDNViews.add(auftragVoipDNView1);
        auftragVoipDNViews.add(auftragVoipDNView2);
        cmd.setPortCount(Integer.valueOf(1));
        cmd.setAuftragVoipDNViews(auftragVoipDNViews);
        cmd.checkHauptrufnummern();
    }

    @Test
    void checkHauptrufnummern_success() throws HurricanServiceCommandException {
        AuftragVoipDNView auftragVoipDNView = new AuftragVoipDNView();
        auftragVoipDNView.setMainNumber(Boolean.TRUE);
        final Collection<AuftragVoipDNView> auftragVoipDNViews = new ArrayList<AuftragVoipDNView>();
        auftragVoipDNViews.add(auftragVoipDNView);
        cmd.setPortCount(Integer.valueOf(1));
        cmd.setAuftragVoipDNViews(auftragVoipDNViews);
        cmd.checkHauptrufnummern();
    }

    List<AuftragVoipDNView> createAuftragVoipViews() {
        List<AuftragVoipDNView> auftragVoipDNViews = new ArrayList<>();
        final Date now = new Date();
        int maxPortCount = 3;
        for (int dnCount = 0; dnCount < 15; dnCount++) {
            final List<AuftragVoIPDN2EGPort> auftragVoIPDN2EGPorts = ImmutableList.of(
                    new AuftragVoIPDN2EGPortBuilder().withRandomId()
                            .withEgPort(new EndgeraetPortBuilder().withNumber(1).build())
                            .withValidFrom(now)
                            .withValidTo(DateTools.getHurricanEndDate())
                            .build(),
                    new AuftragVoIPDN2EGPortBuilder().withRandomId()
                            .withEgPort(new EndgeraetPortBuilder().withNumber(2).build())
                            .withValidFrom(now)
                            .withValidTo(DateTools.getHurricanEndDate())
                            .build(),
                    new AuftragVoIPDN2EGPortBuilder().withRandomId()
                            .withEgPort(new EndgeraetPortBuilder().withNumber(3).build())
                            .withValidFrom(now)
                            .withValidTo(DateTools.getHurricanEndDate())
                            .build()
            );

            AuftragVoipDNView auftragVoipDNView = new AuftragVoipDNView();
            auftragVoipDNView.setMainNumber(Boolean.FALSE);
            auftragVoipDNViews.add(auftragVoipDNView);
        }
        auftragVoipDNViews.get(0).setMainNumber(Boolean.TRUE);
        auftragVoipDNViews.get(auftragVoipDNViews.size() - 1).setMainNumber(Boolean.TRUE);
        return auftragVoipDNViews;
    }

    @Test
    void assignHauptrufnummern_success() throws HurricanServiceCommandException {
        List<AuftragVoipDNView> auftragVoipDNViews = createAuftragVoipViews();
        int portCount = 2;
        cmd.setPortCount(Integer.valueOf(portCount));
        cmd.setAuftragVoipDNViews(auftragVoipDNViews);
        int[] dnCounter = cmd.assignHauptrufnummern();
        assertNotNull(dnCounter);
        assertTrue(dnCounter.length == portCount);
        assertTrue(dnCounter[0] == 1);
        assertTrue(dnCounter[1] == 1);
        for (AuftragVoipDNView voipView : auftragVoipDNViews) {
            if (voipView.getMainNumber() == Boolean.FALSE) {
                assertTrue(voipView.getSelectedPorts().isEmpty());
            }
        }
        // Erste Hauptrufnummer
        SelectedPortsView portSelection = Iterables.getOnlyElement(auftragVoipDNViews.get(0).getSelectedPorts());
        assertTrue(portSelection.isPortSelected(0));
        assertTrue(portSelection.isPortSelected(1) == false);
        // Zweite Hauptrufnummer
        portSelection = Iterables.getOnlyElement(auftragVoipDNViews.get(auftragVoipDNViews.size() - 1)
                .getSelectedPorts());
        assertTrue(portSelection.isPortSelected(0) == false);
        assertTrue(portSelection.isPortSelected(1));
    }

    @Test
    void assignRest_success() throws HurricanServiceCommandException {
        List<AuftragVoipDNView> auftragVoipDNViews = createAuftragVoipViews();
        int portCount = 2;
        int[] dnCounter = new int[portCount];
        dnCounter[0] = 1;
        dnCounter[1] = 1;
        cmd.setPortCount(Integer.valueOf(portCount));
        cmd.setAuftragVoipDNViews(auftragVoipDNViews);
        cmd.assignRest(dnCounter);
        assertTrue(dnCounter[0] == 10);
        assertTrue(dnCounter[1] == 5);
        // Zuordnung nochmals via VoipDNView prüfen
        dnCounter[0] = 1;
        dnCounter[1] = 1;
        for (AuftragVoipDNView voipView : auftragVoipDNViews) {
            if (!voipView.getMainNumber()) {
                SelectedPortsView portSelection = Iterables.getOnlyElement(voipView.getSelectedPorts());
                if ((portSelection.isPortSelected(0)) && (!portSelection.isPortSelected(1))) {
                    dnCounter[0]++;
                }
                else {
                    dnCounter[1]++;
                }
            }
        }
        assertTrue(dnCounter[0] == 10);
        assertTrue(dnCounter[1] == 5);
    }

    @Override
    protected AbstractAssignVoIPDNs2EGPorts getInstanceOfImplToTest() {
        return new AssignDNs2EGPortsCommand();
    }
}
