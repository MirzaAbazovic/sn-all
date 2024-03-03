/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.07.2011 12:40:45
 */
package de.mnet.wita.model;

import static de.augustakom.common.BaseTest.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.testng.Assert.*;

import javax.inject.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.CBVorgangBuilder;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.mnet.common.servicetest.AbstractServiceTest;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.auftrag.Kundenwunschtermin.Zeitfenster;

@Test(groups = SERVICE)
public class WitaCBVorgangServiceTest extends AbstractServiceTest {

    @Autowired
    private CarrierElTALService carrierElTALService;

    @Autowired
    private Provider<CBVorgangBuilder> cbVorgangBuilder;
    @Autowired
    private Provider<WitaCBVorgangBuilder> witaCbVorgangBuilder;

    public void testPersistCBVorgang() throws Exception {
        CBVorgang cbVorgang = cbVorgangBuilder.get().build();

        WitaCBVorgang witaCBVorgang = new WitaCBVorgang();
        witaCBVorgang.setCarrierId(Carrier.ID_DTAG);
        witaCBVorgang.setTyp(CBVorgang.TYP_NEU);
        witaCBVorgang.setStatus(CBVorgang.STATUS_SUBMITTED);
        witaCBVorgang.setVorgabeMnet(DateTools.plusWorkDays(5));
        AKUser user = new AKUser();
        user.setId(15L);
        witaCBVorgang.setBearbeiter(user);
        witaCBVorgang.setWitaGeschaeftsfallTyp(GeschaeftsfallTyp.BEREITSTELLUNG);
        witaCBVorgang.setAuftragId(cbVorgang.getAuftragId());
        witaCBVorgang.setCbId(cbVorgang.getCbId());
        witaCBVorgang.setRealisierungsZeitfenster(Zeitfenster.SLOT_9);
        witaCBVorgang.setAnbieterwechselTkg46(Boolean.FALSE);
        witaCBVorgang.setAutomation(Boolean.FALSE);

        carrierElTALService.saveCBVorgang(witaCBVorgang);

        flushAndClear();

        CBVorgang foundCBVorgang = carrierElTALService.findCBVorgang(witaCBVorgang.getId());

        assertNotNull(foundCBVorgang);
        assertNotSame(foundCBVorgang, witaCBVorgang);
        assertThat(foundCBVorgang, instanceOf(WitaCBVorgang.class));
        assertEquals(((WitaCBVorgang) foundCBVorgang).getWitaGeschaeftsfallTyp(), GeschaeftsfallTyp.BEREITSTELLUNG);
    }

    public void testWitaCBVorgangBuilder() throws Exception {
        WitaCBVorgang witaCbVorgang = witaCbVorgangBuilder.get().build();

        flushAndClear();

        CBVorgang foundCBVorgang = carrierElTALService.findCBVorgang(witaCbVorgang.getId());

        assertNotNull(foundCBVorgang);
        assertThat(foundCBVorgang, instanceOf(WitaCBVorgang.class));
    }

}


