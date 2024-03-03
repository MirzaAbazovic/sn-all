/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.04.2012 15:42:12
 */
package de.mnet.hurrican.e2e.wholesale;

import java.io.*;
import java.nio.charset.*;
import java.util.*;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import org.springframework.util.PropertyPlaceholderHelper;

import de.mnet.hurrican.e2e.common.EkpDataBuilder.EkpData;
import de.mnet.hurrican.e2e.common.StandortDataBuilder.StandortData;
import de.mnet.hurrican.e2e.wholesale.acceptance.BaseWholesaleE2ETest;

public class BaseWholesaleXmlE2ETest extends BaseWholesaleE2ETest {

    public BaseWholesaleXmlE2ETest() {
        super();
    }

    public ImmutableMap<String, String> getProperties(StandortData standortData, EkpData ekpData) {
        return ImmutableMap.of(
                "geoId", standortData.geoId.getId().toString(),
                "ekpId", ekpData.ekpFrameContract.getEkpId(),
                "frameContractId", ekpData.ekpFrameContract.getFrameContractId());
    }


    protected String replaceProperties(String fileName, Map<String, String> properties) throws IOException {
        String xmlTemplateString = Resources.toString(Resources.getResource(fileName),
                Charset.defaultCharset());
        PropertyPlaceholderHelper propertyPlaceholderHelper = new PropertyPlaceholderHelper("${", "}");
        Properties fileProperties = new Properties();
        fileProperties.putAll(properties);

        String xmlString = propertyPlaceholderHelper.replacePlaceholders(xmlTemplateString, fileProperties);
        return xmlString;
    }

}
