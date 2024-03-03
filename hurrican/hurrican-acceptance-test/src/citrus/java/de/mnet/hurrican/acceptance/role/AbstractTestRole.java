/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.09.14
 */
package de.mnet.hurrican.acceptance.role;

import com.consol.citrus.TestActor;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.TestBuilder;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import de.mnet.common.webservice.ServiceModelVersison;
import de.mnet.hurrican.acceptance.common.SimulatorUseCase;
import de.mnet.hurrican.acceptance.customer.CustomerTestVersion;
import de.mnet.hurrican.acceptance.ffm.FfmTestVersion;
import de.mnet.hurrican.acceptance.location.LocationTestVersion;
import de.mnet.hurrican.acceptance.resource.ResourceInventoryTestVersion;
import de.mnet.hurrican.acceptance.resourceorder.ResourceOrderTestVersion;
import de.mnet.hurrican.acceptance.scanview.DocumentArchiveTestVersion;
import de.mnet.hurrican.acceptance.tv.TvFeedTestVersion;
import de.mnet.hurrican.acceptance.utils.TestUtils;
import de.mnet.hurrican.acceptance.wholesale.WholesaleOrderOutboundTestVersion;
import de.mnet.hurrican.acceptance.workforcedata.WorkforceDataTestVersion;

/**
 *
 */
public class AbstractTestRole implements ApplicationContextAware {

    public static final Logger LOGGER = Logger.getLogger(AbstractTestRole.class);
    private static final String TEMPLATES_PATH_PATTERN = "templates/%s/%s/%s";

    @Autowired
    @Qualifier("hurricanTestActor")
    protected TestActor hurricanTestActor;

    @Autowired
    @Qualifier("atlasEsbTestActor")
    protected TestActor atlasEsbTestActor;

    @Autowired
    @Qualifier("cpsTestActor")
    protected TestActor cpsTestActor;

    /**
     * Test builder that receives all building method calls
     */
    protected TestBuilder testBuilder;

    /**
     * ServiceModelVersison used for this role
     */
    protected ServiceModelVersison serviceModelVersison;

    /**
     * Currently loaded simulator use case - important for template path generation
     */
    protected SimulatorUseCase simulatorUseCase;
    protected ApplicationContext applicationContext;

    /**
     * Gets XML template file resource from template package.
     *
     * @param fileName
     * @return
     */
    protected Resource getXmlTemplate(String fileName) {
        return getXmlTemplate(getXmlTemplateBasePackage(), fileName, ".xml");
    }

    /**
     * Gets a file resource from this builders resource package. Uses file path naming conventions:
     * <p/>
     * {test_builder_package}/cdm/{cdm_version}/{sim_usecase_name}/{filename}.xml
     * <p/>
     * In case no resource is found this method returns a default template which is located in default template folder.
     *
     * @param fileName
     * @param fileExtension
     * @return
     */
    protected Resource getXmlTemplate(String resourcePackage, String fileName, String fileExtension) {
        ClassPathResource templateResource = new ClassPathResource(resourcePackage + "/" + fileName + fileExtension);

        if (templateResource.exists()) {
            return templateResource;
        }
        else {
            LOGGER.info(String.format("Could not find template resource '%s'", resourcePackage + "/" + fileName + fileExtension));
            LOGGER.info(String.format("Using default template resource '%s'", getXmlTemplateDefaultPackage() + "/" + fileName + fileExtension));
            return new ClassPathResource(getXmlTemplateDefaultPackage() + "/" + fileName + fileExtension);
        }
    }

    /**
     * Gets the base package for xml template files.
     *
     * @return
     */
    protected String getXmlTemplateBasePackage() {
        return String.format(TEMPLATES_PATH_PATTERN,
                getTemplateFolderName(serviceModelVersison),
                serviceModelVersison.getName().toLowerCase(),
                simulatorUseCase
        );
    }

    /**
     * Gets the default package for xml template files, used when no matching use case template folder was found.
     *
     * @return
     */
    protected String getXmlTemplateDefaultPackage() {
        SimulatorUseCase defaultCase;
        if (FfmTestVersion.class.equals(serviceModelVersison.getClass())) {
            defaultCase = SimulatorUseCase.FFM_Default;
        }
        else {
            throw new CitrusRuntimeException(String.format("No default SimulatorUseCase for the test version '%s' defined!",
                    serviceModelVersison.getName()));
        }
        return String.format(TEMPLATES_PATH_PATTERN,
                getTemplateFolderName(serviceModelVersison),
                serviceModelVersison.getName().toLowerCase(),
                defaultCase
        );
    }

    /**
     * Returns the specific folder name of the assigend {@link ServiceModelVersison}.
     */
    protected <T extends Enum> String getTemplateFolderName(ServiceModelVersison<T> serviceModelVersison) {
        if (serviceModelVersison.getClass().equals(FfmTestVersion.class)) {
            return "ffm";
        }
        if (serviceModelVersison.getClass().equals(ResourceInventoryTestVersion.class)) {
            return "resource";
        }
        if (serviceModelVersison.getClass().equals(TvFeedTestVersion.class)) {
            return "tv";
        }
        if (serviceModelVersison.getClass().equals(DocumentArchiveTestVersion.class)) {
            return "scanview";
        }
        if(serviceModelVersison.getClass().equals(WorkforceDataTestVersion.class)) {
            return "workforcedata";
        }
        if(serviceModelVersison.getClass().equals(LocationTestVersion.class)) {
            return "location";
        }
        if(serviceModelVersison.getClass().equals(CustomerTestVersion.class)) {
            return "customer";
        }
        if (serviceModelVersison.getClass().equals(ResourceOrderTestVersion.class)) {
            return "resourceorder";
        }
        if (serviceModelVersison.getClass().equals(WholesaleOrderOutboundTestVersion.class)) {
            return "wholesaleorderoutbound";
        }
        throw new CitrusRuntimeException(String.format("No template folder path for test version '%s' defined!",
                serviceModelVersison.getName()));
    }

    public void setTestBuilder(TestBuilder testBuilder) {
        this.testBuilder = testBuilder;
    }

    public void setSimulatorUseCase(SimulatorUseCase simulatorUseCase) {
        this.simulatorUseCase = simulatorUseCase;
    }

    public void setServiceModelVersison(ServiceModelVersison serviceModelVersison) {
        this.serviceModelVersison = serviceModelVersison;
    }

    /**
     * Reads the variable value from the test context.
     *
     * @param context      the test context holding the test variables.
     * @param variableName the name of the variable.
     * @return the non-null value of the variable
     */
    protected String readMandatoryVariableFromTestContext(TestContext context, String variableName) {
        return TestUtils.readMandatoryVariableFromTestContext(context, variableName);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
