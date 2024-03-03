/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.02.2012 15:35:42
 */
package de.mnet.hurrican.webservice.cps;

import java.util.*;
import javax.xml.namespace.*;
import com.google.common.collect.ImmutableMap;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.util.StringUtils;
import org.springframework.ws.server.endpoint.MethodEndpoint;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.server.endpoint.AbstractSoapFaultDefinitionExceptionResolver;
import org.springframework.ws.soap.server.endpoint.SoapFaultDefinition;
import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;

import de.augustakom.hurrican.service.wholesale.WholesaleException;
import de.mnet.hurrican.wholesale.common.Fault;

public class CpsSoapFaultResolver extends AbstractSoapFaultDefinitionExceptionResolver {
    private static final Logger LOGGER = Logger.getLogger(CpsSoapFaultResolver.class);

    @Autowired
    private Jaxb2Marshaller jaxbMarshaller;

    // @formatter:off
    private static final ImmutableMap<Class<?>, Class<? extends Fault>> method2FaultClass = new ImmutableMap.Builder<Class<?>, Class<? extends Fault>>()
            //.put(GetSoDataByBillingOrderNoResponse.class, GetSoDataByBillingOrderNoFault.class)
            .build();
    // @formatter:on

    @Override
    protected void customizeFault(Object endpoint, Exception ex, SoapFault fault) {
        LOGGER.debug("Customizing Soap Fault");
        if (ex instanceof WholesaleException) {
            try {
                MethodEndpoint methodEndpoint = (MethodEndpoint) endpoint;
                WholesaleException whsException = (WholesaleException) ex;
                Fault jaxbFault = method2FaultClass.get(methodEndpoint.getReturnType().getParameterType())
                        .newInstance();
                jaxbFault.setErrorCode(whsException.fehler.code);
                jaxbFault.setErrorDescription(whsException.getFehlerBeschreibung());
                jaxbMarshaller.marshal(jaxbFault, fault.addFaultDetail().getResult());

                LOGGER.info(String.format("Marshalled WholesaleException: ErrorCode=%s, ErrorMessage=%s",
                        whsException.fehler, whsException.getFehlerBeschreibung()));
            }
            catch (Exception e) {
                LOGGER.error("Error customizing soap Fault", e);
            }
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
            definition.setFaultStringOrReason("");
        }
        return definition;
    }
}


