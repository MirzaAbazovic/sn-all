/**
 *
 */
package de.augustakom.hurrican.service.cc.impl.command.voip;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.shared.view.voip.AuftragVoipDNView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.EndgeraeteService;
import de.augustakom.hurrican.service.cc.VoIPService;
import de.mnet.common.service.locator.ServiceLocator;

/**
 * {@link AssignVoIPDNs2EGPortsCommand} Unit Test
 */
@Test(groups = BaseTest.UNIT)
public class AssignVoIPDNs2EGPortsCommandTest extends BaseTest {

    private CCLeistungsService leistungsServiceMock;
    private EndgeraeteService endgeraeteServiceMock;
    private ServiceLocator serviceLocatorMock;
    private VoIPService voipServiceMock;

    private AssignVoIPDNs2EGPortsCommand cut;

    @BeforeMethod
    public void setUp() throws FindException {
        cut = new AssignVoIPDNs2EGPortsCommand();

        leistungsServiceMock = mock(CCLeistungsService.class);
        cut.setLeistungsService(leistungsServiceMock);

        endgeraeteServiceMock = mock(EndgeraeteService.class);
        cut.setEndgeraeteService(endgeraeteServiceMock);

        voipServiceMock = mock(VoIPService.class);
        cut.setVoipService(voipServiceMock);

        serviceLocatorMock = mock(ServiceLocator.class);
        cut.setServiceLocator(serviceLocatorMock);
    }

    @Test(expectedExceptions = { HurricanServiceCommandException.class })
    public void checkValues_auftragIdMissing() throws Exception {
        cut.prepare(AssignVoIPDNs2EGPortsCommand.AUFTRAG_VOIP_DN_VIEWS, new ArrayList<AuftragVoipDNView>());
        cut.execute();
    }

    @Test()
    public void execute_emptyVoipDNView() throws Exception {
        cut.prepare(AssignVoIPDNs2EGPortsCommand.AUFTRAG_VOIP_DN_VIEWS, new ArrayList<AuftragVoipDNView>());
        cut.prepare(AssignVoIPDNs2EGPortsCommand.AUFTRAG_ID, Long.valueOf(1));
        cut.execute(); // execute should process smoothly without any doings
    }

    @Test(expectedExceptions = { HurricanServiceCommandException.class })
    public void checkPortCount_countMissing() throws Exception {
        when(leistungsServiceMock.getCountEndgeraetPort(any(Long.class), any(Date.class))).thenReturn(null);
        when(endgeraeteServiceMock.getMaxDefaultEndgeraetPorts()).thenReturn(null);
        cut.checkPortCount();
    }

    @Test(expectedExceptions = { HurricanServiceCommandException.class })
    public void checkPortCount_countIsZero() throws Exception {
        when(leistungsServiceMock.getCountEndgeraetPort(any(Long.class), any(Date.class))).thenReturn(
                Integer.valueOf(0));
        when(endgeraeteServiceMock.getMaxDefaultEndgeraetPorts()).thenReturn(null);
        cut.checkPortCount();
    }

    @Test(expectedExceptions = { HurricanServiceCommandException.class })
    public void checkPortCount_maxDefaultPortCountMissing() throws Exception {
        when(leistungsServiceMock.getCountEndgeraetPort(any(Long.class), any(Date.class))).thenReturn(
                Integer.valueOf(1));
        when(endgeraeteServiceMock.getMaxDefaultEndgeraetPorts()).thenReturn(null);
        cut.checkPortCount();
    }

    @Test(expectedExceptions = { HurricanServiceCommandException.class })
    public void checkPortCount_morePortsConfiugredThanAllowed() throws Exception {
        when(leistungsServiceMock.getCountEndgeraetPort(any(Long.class), any(Date.class))).thenReturn(2);
        when(endgeraeteServiceMock.getMaxDefaultEndgeraetPorts()).thenReturn(1);
        cut.checkPortCount();
    }

    @Test
    public void checkPortCount_success() throws Exception {
        when(leistungsServiceMock.getCountEndgeraetPort(any(Long.class), any(Date.class))).thenReturn(
                Integer.valueOf(1));
        when(endgeraeteServiceMock.getMaxDefaultEndgeraetPorts()).thenReturn(Integer.valueOf(4));
        cut.checkPortCount();
    }
}
