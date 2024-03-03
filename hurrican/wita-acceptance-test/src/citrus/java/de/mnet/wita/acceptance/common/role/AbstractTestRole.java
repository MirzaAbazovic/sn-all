/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.07.2015
 */
package de.mnet.wita.acceptance.common.role;

import com.consol.citrus.dsl.TestBuilder;
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.acceptance.WitaAcceptanceUseCase;

/**
 *
 */
public class AbstractTestRole {
    public final Logger LOGGER = Logger.getLogger(getClass());

    /** Currently loaded simulator use case - important for template path generation */
    protected WitaAcceptanceUseCase useCase;

    /** Test builder that receives all building method calls */
    protected TestBuilder testBuilder;

    /** CdmVersion used for this role */
    protected WitaCdmVersion cdmVersion;

    /**
     * Gets XML template file resource from template package.
     *
     * @param fileName
     * @return
     */
    public Resource getXmlTemplate(String fileName) {
        return getXmlTemplate(getXmlTemplateBasePackage(), fileName, ".xml");
    }

    /**
     * Gets a file resource from this builders resource package. Uses file path naming conventions:
     * <p/>
     * {test_builder_package}/cdm/{cdm_version}/{sim_usecase_name}/{filename}.xml
     *
     * @param fileName
     * @param fileExtension
     * @return
     */
    protected Resource getXmlTemplate(String resourcePackage, String fileName, String fileExtension) {
        return new ClassPathResource(resourcePackage + "/" + fileName + fileExtension);
    }

    /**
     * Gets the base package for xml template files.
     * @return
     */
    protected String getXmlTemplateBasePackage() {
        return getTemplatePackage() + "/cdm/" + cdmVersion.name().toLowerCase() + "/" + useCase;
    }

    /**
     * Gets the base template package in classpath.
     * @return
     */
    private String getTemplatePackage() {
        return "templates";
    }

    public void setTestBuilder(TestBuilder testBuilder) {
        this.testBuilder = testBuilder;
    }

    public void setCdmVersion(WitaCdmVersion cdmVersion) {
        this.cdmVersion = cdmVersion;
    }

    public WitaCdmVersion getCdmVersion() {
        return cdmVersion;
    }

    public void setUseCase(WitaAcceptanceUseCase useCase) {
        this.useCase = useCase;
    }

}
