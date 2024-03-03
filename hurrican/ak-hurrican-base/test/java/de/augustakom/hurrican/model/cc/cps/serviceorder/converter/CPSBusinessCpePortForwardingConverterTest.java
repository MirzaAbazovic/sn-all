package de.augustakom.hurrican.model.cc.cps.serviceorder.converter;

import java.io.*;
import java.util.*;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.hurrican.model.cc.PortForwarding;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSBusinessCpePortForwardingData;

public class CPSBusinessCpePortForwardingConverterTest {

    @Test
    public void testMarshalIPV4() throws Exception {
        PortForwarding pf = new PortForwarding();
        CPSBusinessCpePortForwardingData data = new CPSBusinessCpePortForwardingData(pf);
        data.setSourceIpV4("192.168.0.1");
        data.setDestinationIpV4("192.168.1.1");

        StringWriter sw = new StringWriter();
        new CPSBusinessCpePortForwardingConverter().marshal(Arrays.asList(data), new PrettyPrintWriter(sw), null);

        Assert.assertEquals("<CONFIG>\n"
                + "  <SOURCE__IPV4>192.168.0.1</SOURCE__IPV4>\n"
                + "  <DESTINATION__IPV4>192.168.1.1</DESTINATION__IPV4>\n"
                + "</CONFIG>", sw.getBuffer().toString());
    }

    @Test
    public void testMarshalIPV4NoSource() throws Exception {
        PortForwarding pf = new PortForwarding();
        CPSBusinessCpePortForwardingData data = new CPSBusinessCpePortForwardingData(pf);
        data.setDestinationIpV4("192.168.1.1");

        StringWriter sw = new StringWriter();
        new CPSBusinessCpePortForwardingConverter().marshal(Arrays.asList(data), new PrettyPrintWriter(sw), null);

        Assert.assertEquals("<CONFIG>\n"
                + "  <DESTINATION__IPV4>192.168.1.1</DESTINATION__IPV4>\n"
                + "</CONFIG>", sw.getBuffer().toString());
    }

    @Test
    public void testMarshalIPV4NoDestination() throws Exception {
        PortForwarding pf = new PortForwarding();
        CPSBusinessCpePortForwardingData data = new CPSBusinessCpePortForwardingData(pf);
        data.setSourceIpV4("192.168.0.1");

        StringWriter sw = new StringWriter();
        new CPSBusinessCpePortForwardingConverter().marshal(Arrays.asList(data), new PrettyPrintWriter(sw), null);

        Assert.assertEquals("<CONFIG>\n"
                + "  <SOURCE__IPV4>192.168.0.1</SOURCE__IPV4>\n"
                + "</CONFIG>", sw.getBuffer().toString());
    }

    @Test
    public void testMarshalIPV6() throws Exception {
        PortForwarding pf = new PortForwarding();
        CPSBusinessCpePortForwardingData data = new CPSBusinessCpePortForwardingData(pf);
        data.setSourceIpV6("2001::2001");
        data.setDestinationIpV6("2001::2999");

        StringWriter sw = new StringWriter();
        new CPSBusinessCpePortForwardingConverter().marshal(Arrays.asList(data), new PrettyPrintWriter(sw), null);

        Assert.assertEquals("<CONFIG>\n"
                + "  <SOURCE__IPV6>2001::2001</SOURCE__IPV6>\n"
                + "  <DESTINATION__IPV6>2001::2999</DESTINATION__IPV6>\n"
                + "</CONFIG>", sw.getBuffer().toString());
    }

    @Test
    public void testMarshalIPV6NoSource() throws Exception {
        PortForwarding pf = new PortForwarding();
        CPSBusinessCpePortForwardingData data = new CPSBusinessCpePortForwardingData(pf);
        data.setDestinationIpV6("2001::2999");

        StringWriter sw = new StringWriter();
        new CPSBusinessCpePortForwardingConverter().marshal(Arrays.asList(data), new PrettyPrintWriter(sw), null);

        Assert.assertEquals("<CONFIG>\n"
                + "  <DESTINATION__IPV6>2001::2999</DESTINATION__IPV6>\n"
                + "</CONFIG>", sw.getBuffer().toString());
    }

    @Test
    public void testMarshalIPV6NoDestination() throws Exception {
        PortForwarding pf = new PortForwarding();
        CPSBusinessCpePortForwardingData data = new CPSBusinessCpePortForwardingData(pf);
        data.setSourceIpV6("2001::2001");

        StringWriter sw = new StringWriter();
        new CPSBusinessCpePortForwardingConverter().marshal(Arrays.asList(data), new PrettyPrintWriter(sw), null);

        Assert.assertEquals("<CONFIG>\n"
                + "  <SOURCE__IPV6>2001::2001</SOURCE__IPV6>\n"
                + "</CONFIG>", sw.getBuffer().toString());
    }
}
