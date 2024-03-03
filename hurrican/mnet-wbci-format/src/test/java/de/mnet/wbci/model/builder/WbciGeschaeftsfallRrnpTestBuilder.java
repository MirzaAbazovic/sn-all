package de.mnet.wbci.model.builder;

import static de.augustakom.common.service.holiday.DateCalculationHelper.*;

import java.time.*;
import java.util.*;

import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.Rufnummernportierung;
import de.mnet.wbci.model.Technologie;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfallRrnp;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;

public class WbciGeschaeftsfallRrnpTestBuilder extends WbciGeschaeftsfallRrnpBuilder implements
        WbciTestBuilder<WbciGeschaeftsfallRrnp> {

    @Override
    public WbciGeschaeftsfallRrnp buildValid(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp) {
        if (vorabstimmungsId == null) {
            vorabstimmungsId = IdGenerator.generateVaId(CarrierCode.MNET);
        }
        return buildValidWithoutVorabstimmungsId(wbciCdmVersion, gfTyp);
    }

    public WbciGeschaeftsfallRrnp buildValidWithoutVorabstimmungsId(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp) {
        assert GeschaeftsfallTyp.VA_RRNP.equals(gfTyp) : String.format("GeschaeftsfallTyp '%s' was not expected here",
                gfTyp);

        if (rufnummernportierung == null) {
            rufnummernportierung = new RufnummernportierungAnlageTestBuilder().buildValid(wbciCdmVersion, gfTyp);
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
    public WbciGeschaeftsfallRrnpTestBuilder withRufnummernportierung(Rufnummernportierung rufnummernportierung) {
        return (WbciGeschaeftsfallRrnpTestBuilder) super.withRufnummernportierung(rufnummernportierung);
    }

    @Override
    public WbciGeschaeftsfallRrnpTestBuilder withWechseltermin(LocalDate portingDate) {
        return (WbciGeschaeftsfallRrnpTestBuilder) super.withWechseltermin(portingDate);
    }

    @Override
    public WbciGeschaeftsfallRrnpTestBuilder withStatus(WbciGeschaeftsfallStatus status) {
        return (WbciGeschaeftsfallRrnpTestBuilder) super.withStatus(status);
    }

    @Override
    public WbciGeschaeftsfallRrnpTestBuilder withAufnehmenderEKP(CarrierCode abgebenderEKP) {
        return (WbciGeschaeftsfallRrnpTestBuilder) super.withAufnehmenderEKP(abgebenderEKP);
    }

    @Override
    public WbciGeschaeftsfallRrnpTestBuilder withAbgebenderEKP(CarrierCode abgebenderEKP) {
        return (WbciGeschaeftsfallRrnpTestBuilder) super.withAbgebenderEKP(abgebenderEKP);
    }

    @Override
    public WbciGeschaeftsfallRrnpTestBuilder withBillingOrderNoOrig(Long billingOrderNoOrig) {
        return (WbciGeschaeftsfallRrnpTestBuilder) super.withBillingOrderNoOrig(billingOrderNoOrig);
    }

    @Override
    public WbciGeschaeftsfallRrnpTestBuilder withKundenwunschtermin(LocalDate kundenwunschtermin) {
        return (WbciGeschaeftsfallRrnpTestBuilder) super.withKundenwunschtermin(kundenwunschtermin);
    }
}
