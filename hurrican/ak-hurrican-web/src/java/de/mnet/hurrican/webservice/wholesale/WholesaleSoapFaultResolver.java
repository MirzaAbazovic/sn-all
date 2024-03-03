/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.02.2012 15:35:42
 */
package de.mnet.hurrican.webservice.wholesale;

import java.util.*;
import javax.xml.namespace.*;
import com.google.common.collect.ImmutableMap;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.PriorityOrdered;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.util.StringUtils;
import org.springframework.ws.server.endpoint.MethodEndpoint;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.server.endpoint.AbstractSoapFaultDefinitionExceptionResolver;
import org.springframework.ws.soap.server.endpoint.SoapFaultDefinition;
import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;

import de.augustakom.hurrican.service.wholesale.WholesaleException;
import de.mnet.hurrican.wholesale.common.Fault;
import de.mnet.hurrican.wholesale.fault.clearance.ChangePortFault;
import de.mnet.hurrican.wholesale.fault.clearance.ChangePortResponse;
import de.mnet.hurrican.wholesale.fault.clearance.ChangeVdslProfileFault;
import de.mnet.hurrican.wholesale.fault.clearance.ChangeVdslProfileResponse;
import de.mnet.hurrican.wholesale.fault.clearance.GetAvailablePortsFault;
import de.mnet.hurrican.wholesale.fault.clearance.GetAvailablePortsResponse;
import de.mnet.hurrican.wholesale.fault.clearance.GetVdslProfilesFault;
import de.mnet.hurrican.wholesale.fault.clearance.GetVdslProfilesResponse;
import de.mnet.hurrican.wholesale.workflow.CancelModifyPortFault;
import de.mnet.hurrican.wholesale.workflow.CancelModifyPortResponse;
import de.mnet.hurrican.wholesale.workflow.GetOrderParametersFault;
import de.mnet.hurrican.wholesale.workflow.GetOrderParametersResponse;
import de.mnet.hurrican.wholesale.workflow.ModifyPortFault;
import de.mnet.hurrican.wholesale.workflow.ModifyPortReservationDateFault;
import de.mnet.hurrican.wholesale.workflow.ModifyPortReservationDateResponse;
import de.mnet.hurrican.wholesale.workflow.ModifyPortResponse;
import de.mnet.hurrican.wholesale.workflow.ReleasePortFault;
import de.mnet.hurrican.wholesale.workflow.ReleasePortResponse;
import de.mnet.hurrican.wholesale.workflow.ReservePortFault;
import de.mnet.hurrican.wholesale.workflow.ReservePortResponse;

/**
 * Warum PriorityOrdered?<br> Siehe Spring-WS Ticket https://jira.springsource.org/browse/SWS-772?focusedCommentId=79384#comment-79384<br>
 * <br> This is due to the fact that <sws:annotation-driven/> now adds the SoapFaultAnnotationExceptionResolver (with
 * order 0) and SimpleSoapExceptionResolver (Ordered.LOWEST_PRECEDENCE). Formerly, these two were also registered, but
 * only if you did not have any "custom" exception resolvers in the application context. As such, if you do have custom
 * exception resolvers in the application context (such as the SoapFaultMappingExceptionResolver), you have to make sure
 * they act before the ones provided by <sws:annotation-driven/>, or at least before the SimpleSoapExceptionResolver
 * (which always resolves the exception, making it suitable as a fallback mechanism, hence the low precedence).<br> <br>
 * Ergo, mit Spring-ws-core.2.1.3 muss das Marker Interface PriorityOrdered angezogen werden, damit die Custom Resolver
 * Vorrang bekommen, vor den Spring Default Resolvern.
 */
public class WholesaleSoapFaultResolver extends AbstractSoapFaultDefinitionExceptionResolver implements PriorityOrdered {
    private static final Logger LOGGER = Logger.getLogger(WholesaleSoapFaultResolver.class);

    @Autowired
    private Jaxb2Marshaller jaxbMarshaller;

    // @formatter:off
    private static final ImmutableMap<Class<?>, Class<? extends Fault>> method2FaultClass = new ImmutableMap.Builder<Class<?>, Class<? extends Fault>>()
            .put(ReservePortResponse.class, ReservePortFault.class)
            .put(GetOrderParametersResponse.class, GetOrderParametersFault.class)
            .put(ModifyPortResponse.class, ModifyPortFault.class)
            .put(ReleasePortResponse.class, ReleasePortFault.class)
            .put(ModifyPortReservationDateResponse.class, ModifyPortReservationDateFault.class)
            .put(CancelModifyPortResponse.class, CancelModifyPortFault.class)
            .put(GetAvailablePortsResponse.class, GetAvailablePortsFault.class)
            .put(ChangePortResponse.class, ChangePortFault.class)
            .put(GetVdslProfilesResponse.class, GetVdslProfilesFault.class)
            .put(ChangeVdslProfileResponse.class, ChangeVdslProfileFault.class)
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


