package de.mnet.hurrican.base;

import static org.testng.Assert.*;

import java.util.*;
import com.google.common.collect.Lists;
import com.qoppa.pdfViewer.PDFViewerBean;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;

public abstract class AbstractHurricanWebServiceTest extends AbstractHurricanBaseServiceTest {

    private static final Logger LOGGER = Logger.getLogger(AbstractHurricanWebServiceTest.class);

    private static final String[] CONFIGS = new String[] {
            "de/mnet/wita/v1/wita-server-context.xml",
            "de/mnet/wbci/wbci-client-context.xml",
            "de/mnet/hurrican/webservice/resource/ngn/ngn-context-test.xml",
    };

    @Override
    protected void afterSetup() {
        PDFViewerBean.setKey("81792639a8ad0ea6");
    }

    @Override
    protected String[] getConfigs() {
        String[] baseConfigs = super.getConfigs();
        List<String> baseConfigList = Lists.newArrayList(baseConfigs);
        List<String> webModuleConfigs = Lists.newArrayList(CONFIGS);
        List<String> allConfigs = Lists.newArrayList();
        allConfigs.addAll(webModuleConfigs);
        allConfigs.addAll(baseConfigList);

        return allConfigs.toArray(new String[allConfigs.size()]);
    }

    @Override
    protected Object getReportServerBean(String beanName) {
        try {
            return getApplicationContext().getBean(beanName);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
        return null;
    }

}
