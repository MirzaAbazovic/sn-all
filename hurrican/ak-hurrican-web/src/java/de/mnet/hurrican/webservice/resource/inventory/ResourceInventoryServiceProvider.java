/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.04.2014 13:29
 */
package de.mnet.hurrican.webservice.resource.inventory;

import javax.inject.*;
import javax.servlet.*;
import com.google.common.base.Throwables;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.GetResourceSpecs;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.GetResourceSpecsResponse;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.GetResourceUsage;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.GetResourceUsageResponse;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.Resource;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceInventoryService;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.ServiceFault;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.ServiceFault_Exception;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.UpdateResource;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.UpdateResourceResponse;
import de.mnet.hurrican.webservice.resource.ResourceProcessException;
import de.mnet.hurrican.webservice.resource.ResourceValidationException;
import de.mnet.hurrican.webservice.resource.inventory.service.ResourceProviderService;

/**
 *
 */
@Component
@CcTxRequired
public class ResourceInventoryServiceProvider implements ResourceInventoryService, ServletContextAware {

    public static final String CODE_UNEXPECTED_ERROR = "HUR-001";
    public static final String CODE_VALIDATION_ERROR = "HUR-002";
    public static final String CODE_PROCESS_ERROR = "HUR-100";
    private static final Logger LOGGER = Logger.getLogger(ResourceInventoryServiceProvider.class);

    private ServletContext servletContext;

    @Inject
    private ResourceProviderService resourceProviderService;

    @Inject
    private ResourceValidator resourceValidator;

    @Override
    public GetResourceSpecsResponse getResourceSpecs(GetResourceSpecs in) throws ServiceFault_Exception {
        throw new ServiceFault_Exception("not implemented");
    }

    @Override
    public GetResourceUsageResponse getResourceUsage(GetResourceUsage in) throws ServiceFault_Exception {
        throw new ServiceFault_Exception("not implemented");
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
            LOGGER.error(String.format("Resource Process Exception occured:%n%s", e.getMessage()));
            throwServiceFaultException(e, CODE_PROCESS_ERROR);
        }
        catch (Exception e) {
            LOGGER.error(String.format("Unexpected Exception occured:%n%s", e.getMessage()));
            throwServiceFaultException(e, CODE_UNEXPECTED_ERROR);
        }
        return null; //this return is not necessary, but suppresses a compile error
    }

    private void throwServiceFaultException(Exception e, String errorCode) throws ServiceFault_Exception {
        final ServiceFault fault = new ServiceFault();
        fault.setErrorCode(errorCode);
        fault.setErrorMessage(Throwables.getStackTraceAsString(e));
        throw new ServiceFault_Exception(e.getMessage(), fault, e);
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
