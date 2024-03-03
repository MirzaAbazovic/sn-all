/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.08.2009 11:47:00
 */
package de.mnet.common.servicetest;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.lang.PropertyUtil;
import de.augustakom.common.tools.system.SystemPropertyTools;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;

/**
 * Abstrakte Klasse zum Hochfahren des Hurrican-Servers mit Wita-Komponenten.
 */
public abstract class AbstractServiceTest extends AbstractHurricanBaseServiceTest {

    protected static final Logger LOGGER = Logger.getLogger(AbstractServiceTest.class);

    private static final String[] WITA_SERVER_CONFIGS = new String[] {
            "de/mnet/common/servicetest/servicetest-mocks.xml",
            "de/augustakom/hurrican/service/cc/resources/CCServices.xml",
            "de/augustakom/hurrican/service/cc/resources/CCTestService.xml",
            "de/augustakom/hurrican/service/cc/resources/CCServerServices.xml",
            "de/augustakom/hurrican/service/billing/resources/BillingServices.xml",
            "de/augustakom/hurrican/service/internet/resources/InternetServices.xml",
            "de/augustakom/hurrican/service/cc/resources/ESAATalServices.xml",
            "de/mnet/common/servicetest/cxf-test-mocks.xml",
            "de/mnet/common/servicetest/atlas-test-connection-mocks.xml",
            "de/mnet/common/common-server-context.xml",
            "de/mnet/common/common-camel.xml",
            "de/mnet/wita/v1/wita-server-context.xml",
            "de/mnet/wita/v1/wita-activiti-context.xml",
            "de/mnet/wita/wita-test-context.xml",
            "de/mnet/wita/wita-test-tx-context.xml",
            "de/mnet/wita/wita-test-jms-mocks.xml",
            "de/mnet/wbci/wbci-server-context.xml",
            "de/mnet/wbci/wbci-client-context.xml",
            "de/augustakom/common/service/resources/PropertyPlaceholderConfigurer.xml",
            "de/augustakom/common/service/resources/HttpClient.xml",
            "de/augustakom/hurrican/service/exmodules/archive/resources/ArchiveServerServices.xml",
            "de/augustakom/hurrican/service/wholesale/resources/wholesale-test-context.xml"
    };

    @Override
    protected String[] getConfigs() {
        return WITA_SERVER_CONFIGS;
    }

    @Override
    protected void loadProperties() {
        LOGGER.info("Starting Wita Servicetest Context, started by: " + this.getClass());
        Properties defaultProps = PropertyUtil.loadPropertyHierarchy(Arrays.asList("common", "hurrican-wita-test"),
                "properties", true);
        SystemPropertyTools.instance().setSystemProperties(defaultProps);
        SystemPropertyTools.instance().setSystemProperty("test.application.name", "hurrican.web");
        SystemPropertyTools.instance().setSystemProperty("test.user", "WitaUnitTest");
        SystemPropertyTools.instance().setSystemProperty("test.password", "1Wit@UnitTest");
        super.loadProperties();
    }

}
