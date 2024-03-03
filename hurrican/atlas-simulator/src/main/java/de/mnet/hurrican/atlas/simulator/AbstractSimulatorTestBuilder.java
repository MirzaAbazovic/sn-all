/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.12.14
 */
package de.mnet.hurrican.atlas.simulator;

import java.util.*;
import com.consol.citrus.dsl.CitrusTestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import de.mnet.hurrican.simulator.builder.SimulatorTestBuilder;
import de.mnet.hurrican.simulator.config.SimulatorConfiguration;
import de.mnet.hurrican.simulator.helper.SoapMessageHelper;

/**
 *
 */
public abstract class AbstractSimulatorTestBuilder extends CitrusTestBuilder implements SimulatorTestBuilder {

    /** Soap action header name */
    public static final String SOAP_ACTION = "SoapAction";

    /** Business identifiers  */
    private String orderId;
    private String esbTrackingId = "citrus:randomString(10)";
    private String interfaceVersion;

    @Autowired
    protected SimulatorConfiguration simulatorConfiguration;

    @Autowired
    protected SoapMessageHelper soapMessageHelper;

    protected Logger LOGGER = LoggerFactory.getLogger(getClass()); // NOSONAR squid:S1312


    /**
     * Gets XML template file resource from default template package. Uses file path naming conventions:
     * <p/>
     * {root_template_package}/{class-name}/{name}.xml
     *
     * @param fileName
     * @return
     */
    protected Resource getXmlTemplate(String fileName) {
        return getFileResource(fileName, ".xml");
    }

    /**
     * Gets a file resource from this builders resource package. In case no matching resource was found
     * try to resolve resource in default package.
     *
     * @param fileName
     * @param fileExtension
     * @return
     */
    protected Resource getFileResource(String fileName, String fileExtension) {
        ClassPathResource templateResource = new ClassPathResource(simulatorConfiguration.getTemplatePath() + "/" + getInterfaceName() + "/" + getUseCaseVersion() + "/"
                + getUseCaseName() + "/" + fileName + fileExtension);

        if (templateResource.exists()) {
            return templateResource;
        } else {
            LOGGER.info(String.format("Could not find template resource '%s'", simulatorConfiguration.getTemplatePath() + "/" + getInterfaceName() + "/" + getUseCaseVersion() + "/"
                    + getUseCaseName() + "/" + fileName + fileExtension));
            LOGGER.info(String.format("Using default template resource '%s'", simulatorConfiguration.getTemplatePath() + "/" + getInterfaceName() + "/" + getUseCaseVersion() + "/" + fileName + fileExtension));
            return new ClassPathResource(simulatorConfiguration.getTemplatePath() + "/" + getInterfaceName() + "/" + getUseCaseVersion() + "/" + fileName + fileExtension);
        }
    }

    /**
     * Gets the interface name used for these test builder.
     */
    protected abstract String getInterfaceName();

    /**
     * Adds new test parameter to test case.
     *
     * @param name
     * @param value
     */
    private void setParameter(String name, String value) {
        getTestCase().getParameters().put(name, value);
    }

    @Override
    public String getUseCaseVersion() {
        return interfaceVersion;
    }

    @Override
    public String getUseCaseName() {
        return getClass().getSimpleName();
    }

    @Override
    public Map<String, Object> getTestParameters() {
        return getTestCase().getParameters();
    }

    @Override
    public void setTestBuilderParameter(Map<String, Object> testBuilderParameter) {
        for (Map.Entry<String, Object> paramEntry : testBuilderParameter.entrySet()) {
            if (!getVariables().containsKey(paramEntry.getKey())) {
                variable(paramEntry.getKey(), paramEntry.getValue());
            }
        }
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        setParameter(SimulatorVariableNames.ORDER_ID.name(), orderId);
        this.orderId = orderId;
    }

    public String getInterfaceVersion() {
        return interfaceVersion;
    }

    public void setInterfaceVersion(String interfaceVersion) {
        setParameter(SimulatorVariableNames.INTERFACE_VERSION.name(), interfaceVersion);
        this.interfaceVersion = interfaceVersion;
    }

    public String getEsbTrackingId() {
        return esbTrackingId;
    }

    public void setEsbTrackingId(String esbTrackingId) {
        setParameter(SimulatorVariableNames.ESB_TRACKING_ID.name(), esbTrackingId);
        this.esbTrackingId = esbTrackingId;
    }

    public void setSimulatorConfiguration(SimulatorConfiguration simulatorConfiguration) {
        this.simulatorConfiguration = simulatorConfiguration;
    }
}
