package de.mnet.wbci;

import static de.mnet.wbci.TestGroups.*;

import java.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.mnet.wbci.model.WbciVersion;

@Test(groups = UNIT)
public class WbciVersionTest {
    @Test
    public void testGetVersion() throws Exception {
        Assert.assertEquals(WbciVersion.V2.getVersion(), "2");
    }

    @Test
    public void testGetMinorVersion() throws Exception {
        Assert.assertEquals(WbciVersion.V2.getMinorVersion(), "00");
    }

    @Test
    public void testGetDefault() throws Exception {
        Assert.assertEquals(WbciVersion.getDefault(), WbciVersion.V2);
    }

    @Test
    public void testGetWBCIVersion() throws Exception {
        Assert.assertEquals(WbciVersion.getWbciVersion("2"), WbciVersion.V2);
    }

    @Test
    public void testGetWBCIVersionForUnknownVersion() throws Exception {
        Assert.assertEquals(WbciVersion.getWbciVersion("0"), WbciVersion.UNKNOWN);
    }

    @Test
    public void testGetSupportedVersions() throws Exception {
        Collection<WbciVersion> supportedVersions = new HashSet<>();
        Collections.addAll(supportedVersions, new WbciVersion[] { WbciVersion.V2 });
        Assert.assertEquals(WbciVersion.getSupportedVersions(), supportedVersions);
    }

    @Test
    public void testIsGreaterOrEqualThan() throws Exception {
        Assert.assertTrue(WbciVersion.V2.isGreaterOrEqualThan(WbciVersion.V2));
    }
}
