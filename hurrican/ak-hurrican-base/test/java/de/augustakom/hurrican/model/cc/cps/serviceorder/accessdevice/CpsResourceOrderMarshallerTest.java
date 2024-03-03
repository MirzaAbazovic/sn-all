/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.05.2014
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder.accessdevice;

import java.util.*;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSPBITData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSVlanData;
import de.augustakom.hurrican.service.cc.impl.command.cps.AbstractCPSCommand;

/**
 * XStream Marshalling Test fuer CpsResourceOrder.
 *
 *
 */
@Test(groups = { BaseTest.UNIT })
public class CpsResourceOrderMarshallerTest extends BaseTest {

    public void testMarshal() throws Exception {
        XStreamMarshaller xmlMarshaller = new XStreamMarshaller();
        xmlMarshaller.setAnnotatedClasses(CpsResourceOrder.class);

        CpsResourceOrder cpsResourceOrder = new CpsResourceOrder();
        CpsAccessDevice accessDevice = new CpsAccessDevice();
        CpsItem item1 = new CpsItem();
        CpsEndpointDevice endpointDevice = new CpsEndpointDevice();
        endpointDevice.setName("ONT-401111");
        endpointDevice.setTechId("MUC-PFEUF-010");
        item1.setEndpointDevice(endpointDevice);
        CpsNetworkDevice networkDevice = new CpsNetworkDevice();
        networkDevice.setName("OLT-400001");
        item1.setNetworkDevice(networkDevice);
        CpsLayer2Config layer2Config = new CpsLayer2Config();
        CPSPBITData pbit1 = new CPSPBITData("VOIP", 756);
        layer2Config.setPbits(Arrays.asList(pbit1, new CPSPBITData()));
        CPSVlanData vlan1 = new CPSVlanData();
        vlan1.setService("HSI");
        vlan1.setSvlan(249);
        layer2Config.setVlans(Arrays.asList(vlan1, new CPSVlanData()));
        item1.setLayer2Config(layer2Config);

        CpsItem item2 = new CpsItem();
        accessDevice.setItems(Arrays.asList(item1, item2));
        cpsResourceOrder.setAccessDevice(accessDevice);
        String xml = AbstractCPSCommand.transformSOData2XML(cpsResourceOrder, xmlMarshaller);

        Assert.assertEquals(xml, "<RESOURCE_ORDER>\n"
                + "  <ACCESS_DEVICE>\n"
                + "    <ITEM>\n"
                + "      <DEACTIVATE>0</DEACTIVATE>\n"
                + "      <NETWORK_DEVICE>\n"
                + "        <NAME>OLT-400001</NAME>\n"
                + "      </NETWORK_DEVICE>\n"
                + "      <ENDPOINT_DEVICE>\n"
                + "        <NAME>ONT-401111</NAME>\n"
                + "        <TECH_ID>MUC-PFEUF-010</TECH_ID>\n"
                + "      </ENDPOINT_DEVICE>\n"
                + "      <LAYER2_CONFIG>\n"
                + "        <PBIT>\n"
                + "          <SERVICE>VOIP</SERVICE>\n"
                + "          <LIMIT>756</LIMIT>\n"
                + "        </PBIT>\n"
                + "        <PBIT>\n"
                + "          <LIMIT>0</LIMIT>\n"
                + "        </PBIT>\n"
                + "        <VLAN>\n"
                + "          <SERVICE>HSI</SERVICE>\n"
                + "          <TYPE></TYPE>\n"
                + "          <CVLAN>0</CVLAN>\n"
                + "          <SVLAN>249</SVLAN>\n"
                + "          <SVLAN_BACKBONE>0</SVLAN_BACKBONE>\n"
                + "        </VLAN>\n"
                + "        <VLAN>\n"
                + "          <SERVICE></SERVICE>\n"
                + "          <TYPE></TYPE>\n"
                + "          <CVLAN>0</CVLAN>\n"
                + "          <SVLAN>0</SVLAN>\n"
                + "          <SVLAN_BACKBONE>0</SVLAN_BACKBONE>\n"
                + "        </VLAN>\n"
                + "      </LAYER2_CONFIG>\n"
                + "    </ITEM>\n"
                + "    <ITEM>\n"
                + "      <DEACTIVATE>0</DEACTIVATE>\n"
                + "    </ITEM>\n"
                + "  </ACCESS_DEVICE>\n"
                + "</RESOURCE_ORDER>");

    }
}