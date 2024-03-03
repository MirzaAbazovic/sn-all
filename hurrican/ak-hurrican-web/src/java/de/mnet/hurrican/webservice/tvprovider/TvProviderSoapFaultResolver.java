/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.02.2013 11:25:16
 */
package de.mnet.hurrican.webservice.tvprovider;

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

import de.mnet.hurrican.tvprovider.GetTvAvailabilityInformationFault;
import de.mnet.hurrican.webservice.wholesale.WholesaleSoapFaultResolver;

/**
 * Soapfault-Resolver fuer den Tv Provider Endpoint Warum PriorityOrdered? Siehe {@link WholesaleSoapFaultResolver}
 */
public class TvProviderSoapFaultResolver extends AbstractSoapFaultDefinitionExceptionResolver implements PriorityOrdered {
    private static final Logger LOGGER = Logger.getLogger(TvProviderSoapFaultResolver.class);

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private Jaxb2Marshaller jaxbMarshaller;

    // @formatter:off
    private static final ImmutableMap<String, String> EXCEPTION_2_ERRORCODE = new ImmutableMap.Builder<String, String>()
            .put(TvProviderTechnicalException.class.getName(), "HUR-0001")
            .build();
    // @formatter:on

    @Override
    protected void customizeFault(Object endpoint, Exception ex, SoapFault fault) {
        LOGGER.debug(String.format("Customizing Soap Fault for Exception %s", ex.getClass().getName()));
        try {
            GetTvAvailabilityInformationFault faultObject = new GetTvAvailabilityInformationFault();
            faultObject.setErrorCode(EXCEPTION_2_ERRORCODE.get(ex.getClass().getName()));
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
        org.springframework.ws.soap.server.endpoint.annotation.SoapFault faultAnnotation =
                ex.getClass().getAnnotation(org.springframework.ws.soap.server.endpoint.annotation.SoapFault.class);
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
