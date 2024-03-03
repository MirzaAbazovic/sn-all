/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.10.2011 17:22:34
 */
package de.mnet.wita.common.portierung;

import static de.mnet.wita.TestGroups.*;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.mnet.wita.message.common.portierung.EinzelanschlussRufnummer;
import de.mnet.wita.message.common.portierung.RufnummernPortierungEinzelanschluss;

@Test(groups = UNIT)
public class RufnummernPortierungEinzelanschlussTest {

    public void isFachlichEqual() {
        RufnummernPortierungEinzelanschluss p1 = new RufnummernPortierungEinzelanschluss();
        RufnummernPortierungEinzelanschluss p2 = new RufnummernPortierungEinzelanschluss();

        Assert.assertTrue(p1.isFachlichEqual(p2));
        p1.addRufnummer(new EinzelanschlussRufnummer("89", "532"));
        Assert.assertFalse(p1.isFachlichEqual(p2));
        p2.addRufnummer(new EinzelanschlussRufnummer("89", "532"));
        Assert.assertTrue(p1.isFachlichEqual(p2));
        p1.addRufnummer(new EinzelanschlussRufnummer("11", "532"));
        Assert.assertFalse(p1.isFachlichEqual(p2));
    }

    public void isFachlichEqualIgnoreOrdering() {
        RufnummernPortierungEinzelanschluss p1 = new RufnummernPortierungEinzelanschluss();
        p1.addRufnummer(new EinzelanschlussRufnummer("89", "532"));
        p1.addRufnummer(new EinzelanschlussRufnummer("11", "532"));

        RufnummernPortierungEinzelanschluss p2 = new RufnummernPortierungEinzelanschluss();
        p2.addRufnummer(new EinzelanschlussRufnummer("11", "532"));
        p2.addRufnummer(new EinzelanschlussRufnummer("89", "532"));

        Assert.assertTrue(p1.isFachlichEqual(p2));
    }

}
