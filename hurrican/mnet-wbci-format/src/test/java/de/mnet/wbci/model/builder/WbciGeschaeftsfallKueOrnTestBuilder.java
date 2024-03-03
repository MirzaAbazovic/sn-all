package de.mnet.wbci.model.builder;

import static de.augustakom.common.service.holiday.DateCalculationHelper.*;

import java.time.*;
import java.util.*;

import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.RufnummerOnkz;
import de.mnet.wbci.model.Technologie;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfallKueOrn;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;

public class WbciGeschaeftsfallKueOrnTestBuilder extends WbciGeschaeftsfallKueOrnBuilder implements
        WbciTestBuilder<WbciGeschaeftsfallKueOrn> {

    @Override
    public WbciGeschaeftsfallKueOrn buildValid(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp) {
        if (vorabstimmungsId == null) {
            vorabstimmungsId = IdGenerator.generateVaId(CarrierCode.MNET);
        }
        return buildValidWithoutVorabstimmungsId(wbciCdmVersion, gfTyp);
    }

    public WbciGeschaeftsfallKueOrn buildValidWithoutVorabstimmungsId(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp) {
        assert GeschaeftsfallTyp.VA_KUE_ORN.equals(gfTyp) : String.format(
                "GeschaeftsfallTyp '%s' was not expected here", gfTyp);

        if (anschlussIdentifikation == null) {
            anschlussIdentifikation = new RufnummerOnkzTestBuilder().buildValid(wbciCdmVersion, gfTyp);
        }
        if (standort == null) {
            standort = new StandortTestBuilder().buildValid(wbciCdmVersion, gfTyp);
        }
        if (absender == null) {
            absender = CarrierCode.MNET;
        }
        if (aufnehmenderEKP == null) {
            aufnehmenderEKP = CarrierCode.MNET;
        }
        if (abgebenderEKP == null) {
            abgebenderEKP = CarrierCode.DTAG;
        }
        if (kundenwunschtermin == null) {
            kundenwunschtermin = getDateInWorkingDaysFromNowAndNextDayNotHoliday(11);
        }
        if (endkunde == null) {
            endkunde = new PersonTestBuilder().buildValid(wbciCdmVersion, gfTyp);
        }

        if (mnetTechnologie == null) {
            mnetTechnologie = Technologie.TAL_ISDN;
        }

        if (weitereAnschlussinhaber == null) {
            weitereAnschlussinhaber = Arrays.asList(
                    new PersonTestBuilder().buildValid(wbciCdmVersion, gfTyp),
                    new FirmaTestBuilder().buildValid(wbciCdmVersion, gfTyp));
        }
        if (projekt == null) {
            projekt = new ProjektTestBuilder().buildValid(wbciCdmVersion, gfTyp);
        }
        if (auftragId == null) {
            auftragId = 1L;
        }
        if (billingOrderNoOrig == null) {
            billingOrderNoOrig = 1L;
        }
        if (status == null) {
            status = WbciGeschaeftsfallStatus.ACTIVE;
        }
        return build();
    }

    @Override
    public WbciGeschaeftsfallKueOrnTestBuilder withAnschlussIdentifikation(RufnummerOnkz anschlussIdentifikation) {
        return (WbciGeschaeftsfallKueOrnTestBuilder) super.withAnschlussIdentifikation(anschlussIdentifikation);
    }

    @Override
    public WbciGeschaeftsfallKueOrnTestBuilder withStrAenVorabstimmungsId(String strAenVorabstimmungsId) {
        return (WbciGeschaeftsfallKueOrnTestBuilder) super.withStrAenVorabstimmungsId(strAenVorabstimmungsId);
    }

    @Override
    public WbciGeschaeftsfallKueOrnTestBuilder withAbgebenderEKP(CarrierCode abgebenderEKP) {
        return (WbciGeschaeftsfallKueOrnTestBuilder) super.withAbgebenderEKP(abgebenderEKP);
    }

    @Override
    public WbciGeschaeftsfallKueOrnTestBuilder withWechseltermin(LocalDate portingDate) {
        return (WbciGeschaeftsfallKueOrnTestBuilder) super.withWechseltermin(portingDate);
    }

    @Override
    public WbciGeschaeftsfallKueOrnTestBuilder withStatus(WbciGeschaeftsfallStatus status) {
        return (WbciGeschaeftsfallKueOrnTestBuilder) super.withStatus(status);
    }

    @Override
    public WbciGeschaeftsfallKueOrnTestBuilder withAufnehmenderEKP(CarrierCode abgebenderEKP) {
        return (WbciGeschaeftsfallKueOrnTestBuilder) super.withAufnehmenderEKP(abgebenderEKP);
    }

    @Override
    public WbciGeschaeftsfallKueOrnTestBuilder withVorabstimmungsId(String vorabstimmungsId) {
        return (WbciGeschaeftsfallKueOrnTestBuilder) super.withVorabstimmungsId(vorabstimmungsId);
    }

    @Override
    public WbciGeschaeftsfallKueOrnTestBuilder withBillingOrderNoOrig(Long billingOrderNoOrig) {
        return (WbciGeschaeftsfallKueOrnTestBuilder) super.withBillingOrderNoOrig(billingOrderNoOrig);
    }

    @Override
    public WbciGeschaeftsfallKueOrnTestBuilder withNonBillingRelevantOrderNos(Set<Long> nonBillingRelevantOrderNos) {
        return (WbciGeschaeftsfallKueOrnTestBuilder) super.withNonBillingRelevantOrderNos(nonBillingRelevantOrderNos);
    }

}
