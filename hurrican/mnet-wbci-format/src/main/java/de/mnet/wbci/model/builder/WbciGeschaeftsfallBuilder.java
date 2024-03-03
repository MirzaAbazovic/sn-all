package de.mnet.wbci.model.builder;

import java.time.*;
import java.util.*;
import org.springframework.util.StringUtils;

import de.augustakom.authentication.model.AKUser;
import de.mnet.wbci.model.AutomationTask;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.PersonOderFirma;
import de.mnet.wbci.model.Projekt;
import de.mnet.wbci.model.Technologie;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;

public abstract class WbciGeschaeftsfallBuilder<T extends WbciGeschaeftsfall> implements WbciBuilder<T> {

    protected CarrierCode absender;
    protected CarrierCode aufnehmenderEKP;
    protected CarrierCode abgebenderEKP;
    protected String vorabstimmungsId;
    protected LocalDate kundenwunschtermin;
    private LocalDate wechseltermin;
    protected PersonOderFirma endkunde;
    protected List<PersonOderFirma> weitereAnschlussinhaber;
    protected Projekt projekt;
    protected Long auftragId;
    protected Long billingOrderNoOrig;
    protected WbciGeschaeftsfallStatus status = WbciGeschaeftsfallStatus.ACTIVE;
    private Long userId;
    private String bearbeiterName;
    private Long teamId;
    private Long currentUserId;
    private String aktuellerBearbeiterName;
    protected Technologie mnetTechnologie;
    private String comment;
    protected Boolean klaerfall = Boolean.FALSE;
    private String strAenVorabstimmungsId;
    protected String internalStatus;
    protected Boolean automatable = Boolean.FALSE;
    private List<AutomationTask> automationTasks;
    private Set<Long> nonBillingRelevantOrderNos;

    protected void enrich(T wbciGeschaeftsfall) {
        wbciGeschaeftsfall.setVorabstimmungsId(vorabstimmungsId);
        wbciGeschaeftsfall.setAbsender(absender);
        wbciGeschaeftsfall.setAbgebenderEKP(abgebenderEKP);
        wbciGeschaeftsfall.setAufnehmenderEKP(aufnehmenderEKP);
        wbciGeschaeftsfall.setKundenwunschtermin(kundenwunschtermin != null ? kundenwunschtermin : null);
        wbciGeschaeftsfall.setWechseltermin(wechseltermin != null ? wechseltermin : null);
        wbciGeschaeftsfall.setEndkunde(endkunde);
        wbciGeschaeftsfall.setProjekt(projekt);
        wbciGeschaeftsfall.setWeitereAnschlussinhaber(weitereAnschlussinhaber);
        wbciGeschaeftsfall.setAuftragId(auftragId);
        wbciGeschaeftsfall.setBillingOrderNoOrig(billingOrderNoOrig);
        wbciGeschaeftsfall.setStatus(status);
        wbciGeschaeftsfall.setUserId(userId);
        wbciGeschaeftsfall.setUserName(bearbeiterName);
        wbciGeschaeftsfall.setTeamId(teamId);
        wbciGeschaeftsfall.setCurrentUserId(currentUserId);
        wbciGeschaeftsfall.setCurrentUserName(aktuellerBearbeiterName);
        wbciGeschaeftsfall.setMnetTechnologie(mnetTechnologie);
        wbciGeschaeftsfall.setBemerkungen(comment);
        wbciGeschaeftsfall.setKlaerfall(klaerfall);
        wbciGeschaeftsfall.setStrAenVorabstimmungsId(strAenVorabstimmungsId);
        wbciGeschaeftsfall.setNonBillingRelevantOrderNoOrigs(nonBillingRelevantOrderNos);
        wbciGeschaeftsfall.setInternalStatus(internalStatus);
        wbciGeschaeftsfall.setAutomatable(automatable);
        wbciGeschaeftsfall.setAutomationTasks(automationTasks);
    }

    public WbciGeschaeftsfallBuilder<T> withVorabstimmungsId(String vorabstimmungsId) {
        this.vorabstimmungsId = vorabstimmungsId;
        return this;
    }

    public WbciGeschaeftsfallBuilder<T> withAbsender(CarrierCode absender) {
        this.absender = absender;
        return this;
    }

    public WbciGeschaeftsfallBuilder<T> withAufnehmenderEKP(CarrierCode aufnehmenderEKP) {
        this.aufnehmenderEKP = aufnehmenderEKP;
        return this;
    }

    public WbciGeschaeftsfallBuilder<T> withAbgebenderEKP(CarrierCode abgebenderEKP) {
        this.abgebenderEKP = abgebenderEKP;
        return this;
    }

    public WbciGeschaeftsfallBuilder<T> withProjekt(Projekt projekt) {
        this.projekt = projekt;
        return this;
    }

    public WbciGeschaeftsfallBuilder<T> withKundenwunschtermin(LocalDate kundenwunschtermin) {
        this.kundenwunschtermin = kundenwunschtermin;
        return this;
    }

    public WbciGeschaeftsfallBuilder<T> withWechseltermin(LocalDate wechseltermin) {
        this.wechseltermin = wechseltermin;
        return this;
    }

    public WbciGeschaeftsfallBuilder<T> withEndkunde(PersonOderFirma endkunde) {
        this.endkunde = endkunde;
        return this;
    }

    public WbciGeschaeftsfallBuilder<T> withAuftragId(Long auftragId) {
        this.auftragId = auftragId;
        return this;
    }

    public WbciGeschaeftsfallBuilder<T> withBillingOrderNoOrig(Long billingOrderNoOrig) {
        this.billingOrderNoOrig = billingOrderNoOrig;
        return this;
    }

    public WbciGeschaeftsfallBuilder<T> withStatus(WbciGeschaeftsfallStatus status) {
        this.status = status;
        return this;
    }

    public WbciGeschaeftsfallBuilder<T> withWeitereAnschlussinhaber(List<PersonOderFirma> weitereAnschlussinhaber) {
        this.weitereAnschlussinhaber = weitereAnschlussinhaber;
        return this;
    }

    public WbciGeschaeftsfallBuilder<T> withAutomationTasks(List<AutomationTask> automationTasks) {
        this.automationTasks = automationTasks;
        return this;
    }

    public WbciGeschaeftsfallBuilder<T> withBearbeiter(Long userId, String bearbeiterName, Long teamId) {
        this.userId = userId;
        this.bearbeiterName = bearbeiterName;
        this.teamId = teamId;
        return this;
    }

    public WbciGeschaeftsfallBuilder<T> withBearbeiter(AKUser user) {
        if (user != null) {
            this.userId = user.getId();
            this.bearbeiterName = user.getLoginName();
            this.teamId = (user.getTeam() != null) ? user.getTeam().getId() : null;
        }
        return this;
    }

    public WbciGeschaeftsfallBuilder<T> withAktuellerBearbeiter(Long currentUserId, String aktuellerBearbeiterName) {
        this.currentUserId = currentUserId;
        this.aktuellerBearbeiterName = aktuellerBearbeiterName;
        return this;
    }

    public WbciGeschaeftsfallBuilder<T> withMnetTechnologie(Technologie mnetTechnologie) {
        this.mnetTechnologie = mnetTechnologie;
        return this;
    }

    public WbciGeschaeftsfallBuilder<T> withKlaerfall(Boolean klaerfall) {
        this.klaerfall = klaerfall;
        return this;
    }

    public WbciGeschaeftsfallBuilder<T> withNonBillingRelevantOrderNos(Set<Long> nonBillingRelevantOrderNos) {
        this.nonBillingRelevantOrderNos = nonBillingRelevantOrderNos;
        return this;
    }

    public WbciGeschaeftsfallBuilder<T> withBemerkung(String comment) {
        if (StringUtils.hasText(this.comment)) {
            this.comment += comment;
        }
        else {
            this.comment = comment;
        }

        return this;
    }

    public WbciGeschaeftsfallBuilder<T> addAnschlussinhaber(PersonOderFirma anschlussinhaber) {
        if (weitereAnschlussinhaber == null) {
            weitereAnschlussinhaber = new ArrayList<>();
        }
        this.weitereAnschlussinhaber.add(anschlussinhaber);
        return this;
    }

    public WbciGeschaeftsfallBuilder<T> withAutomatable(Boolean automatable) {
        this.automatable = automatable;
        return this;
    }

    public WbciGeschaeftsfallBuilder<T> addAutomationTask(AutomationTask automationTask) {
        if (this.automationTasks == null) {
            this.automationTasks = new ArrayList<>();
        }
        this.automationTasks.add(automationTask);
        return this;
    }

    public WbciGeschaeftsfallBuilder<T> withStrAenVorabstimmungsId(String strAenVorabstimmungsId) {
        this.strAenVorabstimmungsId = strAenVorabstimmungsId;
        return this;
    }

}
