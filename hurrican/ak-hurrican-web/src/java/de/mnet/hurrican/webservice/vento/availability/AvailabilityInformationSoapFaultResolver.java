/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.09.2012 13:33:07
 */
package de.mnet.hurrican.webservice.vento.availability;

import java.util.*;
import javax.xml.namespace.*;
import com.google.common.collect.ImmutableMap;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.PriorityOrdered;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.util.StringUtils;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.server.endpoint.AbstractSoapFaultDefinitionExceptionResolver;
import org.springframework.ws.soap.server.endpoint.SoapFaultDefinition;
import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;

import de.mnet.hurrican.vento.availability.GetAvailabilityInformationFault;
import de.mnet.hurrican.webservice.wholesale.WholesaleSoapFaultResolver;

/**
 * Soap Resolver fuer den Availability Information Endpoint Warum PriorityOrdered? Siehe {@link
 * WholesaleSoapFaultResolver}
 */
public class AvailabilityInformationSoapFaultResolver extends AbstractSoapFaultDefinitionExceptionResolver implements PriorityOrdered {
    private static final Logger LOGGER = Logger.getLogger(AvailabilityInformationSoapFaultResolver.class);

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private Jaxb2Marshaller jaxbMarshaller;

    // @formatter:off
    private static final ImmutableMap<String, String> exception2ErrorCode = new ImmutableMap.Builder<String, String>()
            .put(TechnicalException.class.getName(), "HUR-0001")
            .build();
    // @formatter:on

    @Override
    protected void customizeFault(Object endpoint, Exception ex, SoapFault fault) {
        LOGGER.debug(String.format("Customizing Soap Fault for Exception %s", ex.getClass().getName()));
        try {
            GetAvailabilityInformationFault faultObject = new GetAvailabilityInformationFault();
            faultObject.setErrorCode(exception2ErrorCode.get(ex.getClass().getName()));
            faultObject.setErrorDescription(ex.getMessage());
            jaxbMarshaller.marshal(faultObject, fault.addFaultDetail().getResult());
            LOGGER.info(String.format("Marshalled Exception: Exception=%s, ErrorMessage=%s", ex.getClass().getName(),
                    ex.getMessage()));
        }
        catch (Exception e) {
            LOGGER.error("Error customizing soap Fault", e);
        }
    }

    @Override
    protected SoapFaultDefinition getFaultDefinition(Object endpoint, Exception ex) {
        org.springframework.ws.soap.server.endpoint.annotation.SoapFault faultAnnotation = ex.getClass().getAnnotation(
                org.springframework.ws.soap.server.endpoint.annotation.SoapFault.class);
        SoapFaultDefinition definition = new SoapFaultDefinition();
        if (faultAnnotation != null) {
            if (faultAnnotation.faultCode() != FaultCode.CUSTOM) {
                definition.setFaultCode(faultAnnotation.faultCode().value());
            }
            else if (StringUtils.hasLength(faultAnnotation.customFaultCode())) {
                definition.setFaultCode(QName.valueOf(faultAnnotation.customFaultCode()));
            }
            definition.setFaultStringOrReason(faultAnnotation.faultStringOrReason());
            definition.setLocale(StringUtils.parseLocaleString(faultAnnotation.locale()));
        }
        else {
            definition.setFaultCode(FaultCode.SERVER.value());
            definition.setLocale(Locale.ENGLISH);
            definition.setFaultStringOrReason(ex.getMessage());
        }
        return definition;
    }
}


