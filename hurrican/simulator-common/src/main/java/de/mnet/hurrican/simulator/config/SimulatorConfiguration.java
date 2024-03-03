/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.07.13
 */
package de.mnet.hurrican.simulator.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class SimulatorConfiguration {

    protected Logger LOGGER = LoggerFactory.getLogger(getClass()); // NOSONAR squid:S1312

    private String templatePath = "de/mnet/hurrican/simulator/template";

    /**
     * Default test builder chosen in case of unknown use case
     */
    private String defaultBuilder = "DEFAULT_BUILDER";

    /**
     * Default timeout when waiting for incoming messages
     */
    private Long defaultTimeout = 50000L;

    /**
     * property that en/disables template validation, default value is true
     */
    private boolean templateValidation = true;

    /**
     * Gets the template path property.
     *
     * @return
     */
    public String getTemplatePath() {
        return templatePath;
    }

    /**
     * Sets the template path property.
     *
     * @param templatePath
     */
    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    /**
     * Gets the default builder name.
     *
     * @return
     */
    public String getDefaultBuilder() {
        return defaultBuilder;
    }

    /**
     * Sets the default builder name.
     *
     * @param defaultBuilder
     */
    public void setDefaultBuilder(String defaultBuilder) {
        this.defaultBuilder = defaultBuilder;
    }

    /**
     * Checks System property on template validation setting. By default enabled (e.g. if System property is not set).
     *
     * @return
     */
    public boolean isTemplateValidationActive() {
        return templateValidation;
    }

    /**
     * En- or disables the template validation.
     *
     * @param templateValidation
     */
    public void setTemplateValidation(boolean templateValidation) {
        LOGGER.debug("set template validation to " + String.valueOf(templateValidation).toUpperCase());
        this.templateValidation = templateValidation;
    }

    /**
     * Sets the default timeout property.
     *
     * @param timout
     */
    public void setDefaultTimeout(Long timout) {
        this.defaultTimeout = timout;
    }

    /**
     * Gets the default timeout property.
     *
     * @return
     */
    public Long getDefaultTimeout() {
        return defaultTimeout;
    }
}
