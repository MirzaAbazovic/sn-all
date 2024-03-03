/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.08.2011 14:09:42
 */
package de.mnet.wita.service.impl;

import static de.mnet.wita.message.GeschaeftsfallTyp.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.TNB;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.mnet.common.servicetest.AbstractServiceTest;
import de.mnet.wbci.model.ProduktGruppe;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.model.VorabstimmungBuilder;
import de.mnet.wita.service.TalAnbieterwechseltypService;

@Test(groups = BaseTest.SERVICE)
public class TalAnbieterwechseltypServiceTest extends AbstractServiceTest {

    @Autowired
    private TalAnbieterwechseltypService underTest;

    @DataProvider
    public Object[][] vorabstimmungProvider() {
        // @formatter:off
        return new Object[][] {
                // Abgebender Provider DTAG
                { new VorabstimmungBuilder().withCarrierDtag(), new EquipmentBuilder().withCarrier(Carrier.CARRIER_DTAG), VERBUNDLEISTUNG },
                { new VorabstimmungBuilder().withCarrierDtag(), new EquipmentBuilder().withCarrier(TNB.MNET.carrierNameUC), RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG },

                // Andere abgebende Provider, Wechsel auf TAL
                { new VorabstimmungBuilder().withCarrierO2().withProduktGruppe(ProduktGruppe.ADSL_SA), new EquipmentBuilder().withCarrier(Carrier.CARRIER_DTAG), VERBUNDLEISTUNG },
                { new VorabstimmungBuilder().withCarrierO2().withProduktGruppe(ProduktGruppe.ADSL_SH), new EquipmentBuilder().withCarrier(Carrier.CARRIER_DTAG), VERBUNDLEISTUNG },
                { new VorabstimmungBuilder().withCarrierO2().withProduktGruppe(ProduktGruppe.CLS), new EquipmentBuilder().withCarrier(Carrier.CARRIER_DTAG), VERBUNDLEISTUNG },
                { new VorabstimmungBuilder().withCarrierO2().withProduktGruppe(ProduktGruppe.SDSL), new EquipmentBuilder().withCarrier(Carrier.CARRIER_DTAG), BEREITSTELLUNG },
                { new VorabstimmungBuilder().withCarrierO2().withProduktGruppe(ProduktGruppe.TAL), new EquipmentBuilder().withCarrier(Carrier.CARRIER_DTAG), PROVIDERWECHSEL },
                { new VorabstimmungBuilder().withCarrierO2().withProduktGruppe(ProduktGruppe.VDSL), new EquipmentBuilder().withCarrier(Carrier.CARRIER_DTAG), VERBUNDLEISTUNG },
                { new VorabstimmungBuilder().withCarrierO2().withProduktGruppe(ProduktGruppe.FTTH), new EquipmentBuilder().withCarrier(Carrier.CARRIER_DTAG), BEREITSTELLUNG },

                // Andere abgebende Provider, Wechsel auf M-net
                { new VorabstimmungBuilder().withCarrierO2().withProduktGruppe(ProduktGruppe.ADSL_SA), new EquipmentBuilder().withCarrier(TNB.MNET.carrierNameUC), RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG },
                { new VorabstimmungBuilder().withCarrierO2().withProduktGruppe(ProduktGruppe.ADSL_SH), new EquipmentBuilder().withCarrier(TNB.MNET.carrierNameUC), RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG },
                { new VorabstimmungBuilder().withCarrierO2().withProduktGruppe(ProduktGruppe.CLS), new EquipmentBuilder().withCarrier(TNB.MNET.carrierNameUC), RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG },
                { new VorabstimmungBuilder().withCarrierO2().withProduktGruppe(ProduktGruppe.SDSL), new EquipmentBuilder().withCarrier(TNB.MNET.carrierNameUC), RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG },
                { new VorabstimmungBuilder().withCarrierO2().withProduktGruppe(ProduktGruppe.TAL), new EquipmentBuilder().withCarrier(TNB.MNET.carrierNameUC), RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG },
                { new VorabstimmungBuilder().withCarrierO2().withProduktGruppe(ProduktGruppe.VDSL), new EquipmentBuilder().withCarrier(TNB.MNET.carrierNameUC), RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG },
                { new VorabstimmungBuilder().withCarrierO2().withProduktGruppe(ProduktGruppe.FTTH), new EquipmentBuilder().withCarrier(TNB.MNET.carrierNameUC), RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG },
        };
        // @formatter:on
    }

    @Test(dataProvider = "vorabstimmungProvider")
    public void test(VorabstimmungBuilder vorabstimmungBuilder, EquipmentBuilder equipmentBuilder,
            GeschaeftsfallTyp expectedGeschaeftsfallTyp) {
        GeschaeftsfallTyp geschaeftsfallTyp = underTest.determineAnbieterwechseltyp(vorabstimmungBuilder.build(),
                equipmentBuilder.build());
        assertThat(geschaeftsfallTyp, equalTo(expectedGeschaeftsfallTyp));
    }

}
