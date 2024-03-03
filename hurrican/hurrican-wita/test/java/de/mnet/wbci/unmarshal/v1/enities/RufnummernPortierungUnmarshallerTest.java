/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.09.13
 */
package de.mnet.wbci.unmarshal.v1.enities;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.OnkzDurchwahlAbfragestelleType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.OnkzRufNrType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PortierungDurchwahlanlageType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PortierungEinzelanschlussType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PortierungszeitfensterEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernListeType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernblockType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernportierungMitPortierungskennungType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernportierungType;
import de.mnet.wbci.model.Portierungszeitfenster;
import de.mnet.wbci.model.Rufnummernportierung;
import de.mnet.wbci.model.RufnummernportierungAnlage;
import de.mnet.wbci.model.RufnummernportierungEinzeln;
import de.mnet.wbci.unmarshal.v1.AbstractWbciUnmarshallerContext;

@Test(groups = BaseTest.UNIT)
public class RufnummernPortierungUnmarshallerTest extends AbstractWbciUnmarshallerContext {

    @Autowired
    private RufnummernPortierungUnmarshaller testling;

    public void testApplyNull() {
        Assert.assertNull(testling.apply(null));
    }

    public void testApplyEinzel() {
        OnkzRufNrType onkz1 = new OnkzRufNrType();
        onkz1.setONKZ("089");
        onkz1.setRufnummer("123456");

        OnkzRufNrType onkz2 = new OnkzRufNrType();
        onkz2.setONKZ("0821");
        onkz2.setRufnummer("123457");

        RufnummernListeType listType = new RufnummernListeType();
        listType.getZuPortierendeOnkzRnr().add(onkz1);
        listType.getZuPortierendeOnkzRnr().add(onkz2);

        PortierungEinzelanschlussType einzel = new PortierungEinzelanschlussType();
        einzel.setAlleRufnummern(Boolean.TRUE);
        einzel.setRufnummernliste(listType);

        RufnummernportierungType type = new RufnummernportierungType();
        type.setPortierungszeitfenster(PortierungszeitfensterEnumType.ZF_2);
        type.setEinzelanschluss(einzel);

        Rufnummernportierung result = testling.apply(type);
        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof RufnummernportierungEinzeln);
        RufnummernportierungEinzeln einzeln = (RufnummernportierungEinzeln) result;
        Assert.assertEquals(einzeln.getPortierungszeitfenster(), Portierungszeitfenster.ZF2);
        Assert.assertEquals(einzeln.getRufnummernOnkz().size(), 2);
        Assert.assertEquals(einzeln.getRufnummernOnkz().get(0).getOnkz(), "89");
        Assert.assertEquals(einzeln.getRufnummernOnkz().get(0).getRufnummer(), onkz1.getRufnummer());
        Assert.assertEquals(einzeln.getRufnummernOnkz().get(1).getOnkz(), "821");
        Assert.assertEquals(einzeln.getRufnummernOnkz().get(1).getRufnummer(), onkz2.getRufnummer());
    }

    public void testApplyBlock() {
        RufnummernblockType block1 = new RufnummernblockType();
        block1.setRnrBlockVon("100");
        block1.setRnrBlockBis("399");

        RufnummernblockType block2 = new RufnummernblockType();
        block2.setRnrBlockVon("600");
        block2.setRnrBlockBis("999");

        OnkzDurchwahlAbfragestelleType onkzType = new OnkzDurchwahlAbfragestelleType();
        onkzType.setONKZ("089");
        onkzType.setAbfragestelle("3456");
        onkzType.setDurchwahlnummer("0");

        PortierungDurchwahlanlageType anlageType = new PortierungDurchwahlanlageType();
        anlageType.setOnkzDurchwahlAbfragestelle(onkzType);
        anlageType.getZuPortierenderRufnummernblock().add(block1);
        anlageType.getZuPortierenderRufnummernblock().add(block2);

        RufnummernportierungMitPortierungskennungType type = new RufnummernportierungMitPortierungskennungType();
        type.setPortierungszeitfenster(PortierungszeitfensterEnumType.ZF_1);
        type.setAnlagenanschluss(anlageType);
        type.setPortierungskennungPKIauf("D001");

        Rufnummernportierung result = testling.apply(type);
        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof RufnummernportierungAnlage);
        RufnummernportierungAnlage anlage = (RufnummernportierungAnlage) result;
        Assert.assertEquals(anlage.getPortierungskennungPKIauf(), type.getPortierungskennungPKIauf());
        Assert.assertEquals(anlage.getPortierungszeitfenster(), Portierungszeitfenster.ZF1);
        Assert.assertEquals(anlage.getOnkz(), "89");
        Assert.assertEquals(anlage.getAbfragestelle(), onkzType.getAbfragestelle());
        Assert.assertEquals(anlage.getDurchwahlnummer(), onkzType.getDurchwahlnummer());
        Assert.assertEquals(anlage.getRufnummernbloecke().size(), 2);
        Assert.assertEquals(anlage.getRufnummernbloecke().get(0).getRnrBlockVon(), block1.getRnrBlockVon());
        Assert.assertEquals(anlage.getRufnummernbloecke().get(0).getRnrBlockBis(), block1.getRnrBlockBis());
        Assert.assertEquals(anlage.getRufnummernbloecke().get(1).getRnrBlockVon(), block2.getRnrBlockVon());
        Assert.assertEquals(anlage.getRufnummernbloecke().get(1).getRnrBlockBis(), block2.getRnrBlockBis());
    }

}
