package de.mnet.hurrican.webservice.resource.inventory;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.GetResourceSpecs;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.GetResourceUsage;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.ServiceFault_Exception;
import de.mnet.hurrican.webservice.resource.inventory.service.ResourceProviderService;

@Test(groups = BaseTest.UNIT)
public class ResourceInventoryServiceProviderTest {

    @InjectMocks
    private ResourceInventoryServiceProvider cut;

    @Mock
    private ResourceProviderService resourceProviderService;

    @Mock
    private ResourceValidator resourceValidator;

    @BeforeMethod
    private void setUp(){
        cut = new ResourceInventoryServiceProvider();
        MockitoAnnotations.initMocks(this);
    }

    @Test(expectedExceptions = ServiceFault_Exception.class, expectedExceptionsMessageRegExp = "not implemented")
    public void getResourceSpecs() throws ServiceFault_Exception {
        cut.getResourceSpecs(new GetResourceSpecs());
    }

    @Test(expectedExceptions = ServiceFault_Exception.class, expectedExceptionsMessageRegExp = "not implemented")
    public void getResourceUsage() throws ServiceFault_Exception {
        cut.getResourceUsage(new GetResourceUsage());
    }

/*
    public void updateResource() throws ServiceFault_Exception {
        UpdateResource updateResource = new UpdateResource()
        cut.getResourceUsage(new GetResourceUsage());
    }

    @Override
    public UpdateResourceResponse updateResource(UpdateResource in) throws ServiceFault_Exception {
        try {
            LOGGER.trace("Processing 'UpdateResourceRequest'");
            final UpdateResourceResponse response = new UpdateResourceResponse();
            if (in.getResource() != null) {
                AKWarnings validationErrors = new AKWarnings();
                // Phase eins: alle Resourcen validieren und alle Validierungsfehler sammeln
                for (Resource resource : in.getResource()) {
                    try {
                        resourceValidator.validateResource(resource);
                    }
                    catch (ResourceValidationException e) {
                        LOGGER.error(e.getMessage());
                        validationErrors.addAKWarning(this, e.getMessage());
                    }
                }
                if (validationErrors.isNotEmpty()) {
                    throwServiceFaultException(new ResourceValidationException(validationErrors.getWarningsAsText()),
                            CODE_VALIDATION_ERROR);
                }
                // Phase zwei: alle Resourcen verarbeiten
                for (Resource resource : in.getResource()) {
                    resourceProviderService.updateResource(resource, (Long) servletContext
                            .getAttribute(HurricanConstants.HURRICAN_SESSION_ID));
                }
            }
            return response;
        }
        catch (ServiceFault_Exception e) {
            throw e;
        }
        catch (ResourceProcessException e) {
            LOGGER.error(String.format("Resource Process Exception occured:\n%s", e.getMessage()));
            throwServiceFaultException(e, CODE_PROCESS_ERROR);
        }
        catch (Exception e) {
            LOGGER.error(String.format("Unexpected Exception occured:\n%s", e.getMessage()));
            throwServiceFaultException(e, CODE_UNEXPECTED_ERROR);
        }
        return null; //this return is not necessary, but suppresses a compile error
    }
*/

}
