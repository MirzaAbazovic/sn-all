/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.05.2014
 */
package de.mnet.wbci.citrus.actions;

import java.time.*;
import java.util.regex.*;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import org.testng.Assert;

import de.augustakom.hurrican.model.cc.Endstelle;
import de.mnet.wbci.model.CarrierRole;
import de.mnet.wbci.model.UebernahmeRessourceMeldung;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wita.dao.VorabstimmungAbgebendDao;
import de.mnet.wita.model.VorabstimmungAbgebend;

/**
 * Verifies that a correct {@link de.mnet.wita.model.VorabstimmungAbgebend} have been set.
 *
 *
 */
public class VerifyWitaVorabstimmungAbgebendAction extends AbstractWbciTestAction {
    private WbciCommonService wbciCommonService;
    private VorabstimmungAbgebendDao vorabstimmungAbgebendDao;
    private Boolean active;
    private String regExComment;

    public VerifyWitaVorabstimmungAbgebendAction(WbciCommonService wbciCommonService, VorabstimmungAbgebendDao vorabstimmungAbgebendDao, Boolean active, String regExComment) {
        super("verify \"WITA-Vorabstimmung abgebend\" has been created");
        this.wbciCommonService = wbciCommonService;
        this.vorabstimmungAbgebendDao = vorabstimmungAbgebendDao;
        this.active = active;
        this.regExComment = regExComment;
    }

    @Override
    public void doExecute(TestContext testContext) {
        String vorabstimmungsId = getVorabstimmungsId(testContext);
        WbciGeschaeftsfall wbciGeschaeftsfall = wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId);
        LocalDateTime abgestimmterTermin = (wbciGeschaeftsfall.getWechseltermin() != null)
                ? wbciGeschaeftsfall.getWechseltermin().atStartOfDay()
                : wbciGeschaeftsfall.getKundenwunschtermin().atStartOfDay();

        if (CarrierRole.lookupMNetCarrierRole(wbciGeschaeftsfall).equals(CarrierRole.ABGEBEND)
                && wbciGeschaeftsfall.getAuftragId() != null) {
            VorabstimmungAbgebend vorabstimmungAbgebend = vorabstimmungAbgebendDao.findVorabstimmung(
                    Endstelle.ENDSTELLEN_TYP_B,
                    wbciGeschaeftsfall.getAuftragId());

            Assert.assertNotNull(vorabstimmungAbgebend,
                    "no VorabstimmungAbgebend found for '" + wbciGeschaeftsfall.getAuftragId() + "'");
            Assert.assertEquals(vorabstimmungAbgebend.getRueckmeldung(), active,
                    "expected value of 'rueckmeldung' in the VorabstimmungAbgebend' was not equal");

            if (regExComment != null && !Pattern.matches(regExComment, vorabstimmungAbgebend.getBemerkung())) {
                throw new CitrusRuntimeException("the WITA vorabstimmung abgebend bemerkung \"" + vorabstimmungAbgebend.getBemerkung() + "\" didn't match to the regular expression '" + regExComment + "'");
            }

            final UebernahmeRessourceMeldung akmtr =
                    wbciCommonService.findLastForVaId(vorabstimmungsId, UebernahmeRessourceMeldung.class);
            if (akmtr != null && akmtr.isUebernahme()) {
                Assert.assertEquals(vorabstimmungAbgebend.getCarrier().getPortierungskennung(), akmtr.getPortierungskennungPKIauf(),
                        "expected value of 'carrier' in the VorabstimmungAbgebend' was not equal");
            }
            else {
                Assert.assertEquals(vorabstimmungAbgebend.getCarrier().getItuCarrierCode(), wbciGeschaeftsfall.getEKPPartner().getITUCarrierCode(),
                        "expected value of 'carrier' in the VorabstimmungAbgebend' was not equal");
            }
            Assert.assertEquals(vorabstimmungAbgebend.getAbgestimmterProdiverwechsel(), abgestimmterTermin.toLocalDate(),
                    "expected value of 'providerWechselDatum' in the VorabstimmungAbgebend' was not equal");
        }
    }

}
