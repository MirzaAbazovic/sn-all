package de.mnet.hurrican.webservice.resource.serialnumber;

import static org.mockito.Mockito.*;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.hurrican.dao.cc.errorlog.ErrorLogDAO;
import de.augustakom.hurrican.model.cc.hardware.HWOnt;
import de.augustakom.hurrican.service.cc.HWService;
import de.mnet.hurrican.webservice.resource.ResourceProcessException;
import de.mnet.hurrican.webservice.resource.inventory.command.CommandResourceInventoryWebserviceClient;
import de.mnet.hurrican.webservice.resource.inventory.service.ResourceProviderService;

/**
 * UT for {@link SerialNumberFFMServiceImpl}
 *
 */
public class SerialNumberFFMServiceImplTest {

    @Mock
    private CommandResourceInventoryWebserviceClient resourceInventoryClient;
    @Mock
    private ErrorLogDAO errorLogDAO;
    @Mock
    private HWService hwService;
    @Mock
    protected ResourceProviderService resourceProviderService;

    private SerialNumberFFMServiceImpl serialNumberService;


    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        this.serialNumberService = new SerialNumberFFMServiceImpl();
        this.serialNumberService.errorHandler = new SerialNumberErrorHandler();
        this.serialNumberService.errorHandler.errorLogDAO = errorLogDAO;
        this.serialNumberService.cmdSender = new SerialNumberCmdSender();
        this.serialNumberService.cmdSender.resourceInventoryClient = resourceInventoryClient;
        this.serialNumberService.cmdSender.errorHandler = this.serialNumberService.errorHandler;
        this.serialNumberService.hwService = hwService;
        this.serialNumberService.resourceProviderService = resourceProviderService;
    }

    @Test
    public void updateResourceCharacteristics_FalseRouting_OnlyLogError() throws Exception {
        serialNumberService.updateResourceCharacteristics("sth", "sth", true);
        // check error logging
        verify(errorLogDAO, times(1)).store(anyObject());
        // hwService is not called!
        verify(hwService, times(0)).saveHWRack(anyObject());
    }

    @Test
    public void testUpdateResourceCharacteristics_Successful() throws Exception {
        when(hwService.findActiveRackByBezeichnung(anyString())).thenReturn(new HWOnt());
        this.serialNumberService.updateResourceCharacteristics("resource-id", "new-serial-number", false);
        verify(resourceInventoryClient, times(1)).updateOntSerialNumber(anyObject());
        verify(hwService, times(1)).saveHWRack(anyObject());
    }

    @Test
    public void testUpdateResourceCharacteristics_ResourceNotFound() throws Exception {
        // hwService.findActiveRackByBezeichnung() --> null
        this.serialNumberService.updateResourceCharacteristics("resource-id", "new-serial-number", false);
        // log tab is written
        verify(errorLogDAO, times(1)).store(anyObject());
        // resource is NOT sent to cmd
        verify(resourceInventoryClient, times(0)).updateOntSerialNumber(anyObject());
        // hw is NOT updated
        verify(hwService, times(0)).saveHWRack(anyObject());
    }

    @Test
    public void testUpdateResourceCharacteristics_CmdError() throws Exception {
        when(hwService.findActiveRackByBezeichnung(anyString())).thenReturn(new HWOnt());
        doThrow(new ResourceProcessException("cmd error")).when(resourceInventoryClient).updateOntSerialNumber(anyObject());

        this.serialNumberService.updateResourceCharacteristics("resource-id", "new-serial-number", false);
        // resource sent to cmd
        verify(resourceInventoryClient, times(1)).updateOntSerialNumber(anyObject());
        // log tab is written
        verify(errorLogDAO, times(1)).store(anyObject());
        // hw is NOT updated
        verify(hwService, times(0)).saveHWRack(anyObject());
    }
}