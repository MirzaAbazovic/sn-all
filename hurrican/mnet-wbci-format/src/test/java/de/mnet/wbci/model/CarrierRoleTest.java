/* 
 * Copyright (c) 2013 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 11.10.13 
 */
package de.mnet.wbci.model;

import static de.mnet.wbci.TestGroups.*;
import static de.mnet.wbci.model.CarrierCode.*;
import static de.mnet.wbci.model.CarrierRole.*;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnTestBuilder;

@Test(groups = UNIT)
public class CarrierRoleTest {

    @Test
    public void testLookupMNetCarrierRoleByITU() throws Exception {
        String iTU_DTAG = DTAG.getITUCarrierCode();
        String iTU_MNET = MNET.getITUCarrierCode();

        Assert.assertEquals(lookupMNetCarrierRoleByITU(iTU_DTAG, iTU_MNET), CarrierRole.ABGEBEND);
        Assert.assertEquals(lookupMNetCarrierRoleByITU(iTU_MNET, iTU_DTAG), CarrierRole.AUFNEHMEND);
        Assert.assertNull(lookupMNetCarrierRoleByITU(iTU_DTAG, iTU_DTAG));
        Assert.assertNull(lookupMNetCarrierRoleByITU(null, null));
    }

    @Test
    public void testLookupMNetCarrierRoleByCarrierCode() throws Exception {
        Assert.assertEquals(lookupMNetCarrierRoleByCarrierCode(DTAG, MNET), CarrierRole.ABGEBEND);
        Assert.assertEquals(lookupMNetCarrierRoleByCarrierCode(MNET, DTAG), CarrierRole.AUFNEHMEND);
        Assert.assertNull(lookupMNetCarrierRoleByCarrierCode(null, null));
    }

    @Test
    public void testLookupMNetPartnerCarrierCode() throws Exception {
        Assert.assertEquals(lookupMNetPartnerCarrierCode(DTAG, MNET), CarrierCode.DTAG);
        Assert.assertEquals(lookupMNetPartnerCarrierCode(MNET, DTAG), CarrierCode.DTAG);
        Assert.assertNull(lookupMNetPartnerCarrierCode(null, null));
    }

    @Test
    public void testLookupMNetCarrierRole() throws Exception {
        WbciGeschaeftsfall geschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder().withAbgebenderEKP(MNET).withAufnehmenderEKP(DTAG).build();
        Assert.assertEquals(lookupMNetCarrierRole(geschaeftsfall), CarrierRole.ABGEBEND);

        geschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder().withAbgebenderEKP(DTAG).withAufnehmenderEKP(MNET).build();
        Assert.assertEquals(lookupMNetCarrierRole(geschaeftsfall), CarrierRole.AUFNEHMEND);
    }
}