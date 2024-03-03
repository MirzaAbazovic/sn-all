package de.mnet.wbci.model.builder;

import static de.augustakom.common.service.holiday.DateCalculationHelper.*;

import java.time.*;
import java.util.*;

import de.mnet.wbci.model.AutomationTask;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.PersonOderFirma;
import de.mnet.wbci.model.Rufnummernportierung;
import de.mnet.wbci.model.Technologie;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfallKueMrn;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;

public class WbciGeschaeftsfallKueMrnTestBuilder extends WbciGeschaeftsfallKueMrnBuilder implements
        WbciTestBuilder<WbciGeschaeftsfallKueMrn> {

    @Override
    public WbciGeschaeftsfallKueMrn buildValid(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp) {
        if (vorabstimmungsId == null) {
            vorabstimmungsId = IdGenerator.generateVaId(CarrierCode.MNET);
        }
        return buildValidWithoutVorabstimmungsId(wbciCdmVersion, gfTyp);
    }

    public WbciGeschaeftsfallKueMrn buildValidWithoutVorabstimmungsId(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp) {
        assert GeschaeftsfallTyp.VA_KUE_MRN.equals(gfTyp) : String.format(
                "GeschaeftsfallTyp '%s' was not expected here", gfTyp);

        if (rufnummernportierung == null) {
            rufnummernportierung = new RufnummernportierungAnlageTestBuilder().buildValid(wbciCdmVersion, gfTyp);
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

        if (klaerfall == null) {
            klaerfall = Boolean.FALSE;
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
        if (internalStatus == null) {
            internalStatus = "Internal status";
        }
        if (automatable == null) {
            automatable = Boolean.FALSE;
        }

        return build();
    }

    @Override
    public WbciGeschaeftsfallKueMrnTestBuilder withBillingOrderNoOrig(Long billingOrderNoOrig) {
        return (WbciGeschaeftsfallKueMrnTestBuilder) super.withBillingOrderNoOrig(billingOrderNoOrig);
    }

    @Override
    public WbciGeschaeftsfallKueMrnTestBuilder withBearbeiter(Long userId, String bearbeiterName, Long teamId) {
        return (WbciGeschaeftsfallKueMrnTestBuilder) super.withBearbeiter(userId, bearbeiterName, teamId);
    }

    @Override
    public WbciGeschaeftsfallKueMrnTestBuilder withAktuellerBearbeiter(Long userId, String bearbeiterName) {
        return (WbciGeschaeftsfallKueMrnTestBuilder) super.withAktuellerBearbeiter(userId, bearbeiterName);
    }

    @Override
    public WbciGeschaeftsfallKueMrnTestBuilder withBemerkung(String comment) {
        return (WbciGeschaeftsfallKueMrnTestBuilder) super.withBemerkung(comment);
    }

    @Override
    public WbciGeschaeftsfallKueMrnTestBuilder withWeitereAnschlussinhaber(List<PersonOderFirma> weitereAnschlussinhaber) {
        return (WbciGeschaeftsfallKueMrnTestBuilder) super.withWeitereAnschlussinhaber(weitereAnschlussinhaber);
    }

    @Override
    public WbciGeschaeftsfallKueMrnTestBuilder withRufnummernportierung(Rufnummernportierung rufnummernportierung) {
        return (WbciGeschaeftsfallKueMrnTestBuilder) super.withRufnummernportierung(rufnummernportierung);
    }

    @Override
    public WbciGeschaeftsfallKueMrnTestBuilder addAnschlussinhaber(PersonOderFirma anschlussinhaber) {
        return (WbciGeschaeftsfallKueMrnTestBuilder) super.addAnschlussinhaber(anschlussinhaber);
    }

    @Override
    public WbciGeschaeftsfallKueMrnTestBuilder withEndkunde(PersonOderFirma endkunde) {
        return (WbciGeschaeftsfallKueMrnTestBuilder) super.withEndkunde(endkunde);
    }

    @Override
    public WbciGeschaeftsfallKueMrnTestBuilder withKundenwunschtermin(LocalDate kundenwunschtermin) {
        return (WbciGeschaeftsfallKueMrnTestBuilder) super.withKundenwunschtermin(kundenwunschtermin);
    }

    public WbciGeschaeftsfallKueMrnTestBuilder withVorabstimmungsId(String vorabstimmungsId) {
        return (WbciGeschaeftsfallKueMrnTestBuilder) super.withVorabstimmungsId(vorabstimmungsId);
    }

    @Override
    public WbciGeschaeftsfallKueMrnTestBuilder withNonBillingRelevantOrderNos(Set<Long> nonBillingRelevantOrderNos) {
        return (WbciGeschaeftsfallKueMrnTestBuilder) super.withNonBillingRelevantOrderNos(nonBillingRelevantOrderNos);
    }

    @Override
    public WbciGeschaeftsfallKueMrnTestBuilder withStrAenVorabstimmungsId(String strAenVorabstimmungsId) {
        return (WbciGeschaeftsfallKueMrnTestBuilder) super.withStrAenVorabstimmungsId(strAenVorabstimmungsId);
    }

    @Override
    public WbciGeschaeftsfallKueMrnTestBuilder withWechseltermin(LocalDate portingDate) {
        return (WbciGeschaeftsfallKueMrnTestBuilder) super.withWechseltermin(portingDate);
    }

    @Override
    public WbciGeschaeftsfallKueMrnTestBuilder withStatus(WbciGeschaeftsfallStatus status) {
        return (WbciGeschaeftsfallKueMrnTestBuilder) super.withStatus(status);
    }

    @Override
    public WbciGeschaeftsfallKueMrnTestBuilder withKlaerfall(Boolean klaerfall) {
        return (WbciGeschaeftsfallKueMrnTestBuilder) super.withKlaerfall(klaerfall);
    }

    @Override
    public WbciGeschaeftsfallKueMrnTestBuilder withAbgebenderEKP(CarrierCode abgebenderEKP) {
        return (WbciGeschaeftsfallKueMrnTestBuilder) super.withAbgebenderEKP(abgebenderEKP);
    }

    @Override
    public WbciGeschaeftsfallKueMrnTestBuilder withAufnehmenderEKP(CarrierCode abgebenderEKP) {
        return (WbciGeschaeftsfallKueMrnTestBuilder) super.withAufnehmenderEKP(abgebenderEKP);
    }

    @Override
    public WbciGeschaeftsfallKueMrnTestBuilder withMnetTechnologie(Technologie mnetTechnologie) {
        return (WbciGeschaeftsfallKueMrnTestBuilder) super.withMnetTechnologie(mnetTechnologie);
    }

    @Override
    public WbciGeschaeftsfallKueMrnTestBuilder addAutomationTask(AutomationTask automationTask) {
        return (WbciGeschaeftsfallKueMrnTestBuilder) super.addAutomationTask(automationTask);
    }

    public WbciGeschaeftsfallKueMrnTestBuilder withAutomationTasks(List<AutomationTask> automationTasks) {
        return (WbciGeschaeftsfallKueMrnTestBuilder) super.withAutomationTasks(automationTasks);
    }

    @Override
    public WbciGeschaeftsfallKueMrnTestBuilder withAutomatable(Boolean automatable) {
        return (WbciGeschaeftsfallKueMrnTestBuilder) super.withAutomatable(automatable);
    }

    @Override
    public WbciGeschaeftsfallKueMrnTestBuilder withAuftragId(Long auftragId) {
        return (WbciGeschaeftsfallKueMrnTestBuilder) super.withAuftragId(auftragId);
    }

}
