/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.12.2004 13:28:23
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.shared.iface.PortierungsartModel;

/**
 * Modell bildet einen Bauauftrags- oder Projektierungs-Verlauf ab.
 *
 *
 */
public class Verlauf extends AbstractCCIDModel implements PortierungsartModel {

    private static final long serialVersionUID = -3782030615147076318L;

    public enum InstallationType {
        SELBSTMONTAGE(900L),
        MNET(901L),
        EXTERN(902L);

        private Long id;

        InstallationType(Long id) {
            this.id = id;
        }

        public Long getId() {
            return id;
        }
    }

    private Long auftragId = null;
    private Long anlass = null;
    private Long verlaufStatusId = null;
    private Long statusIdAlt = null;
    private Boolean projektierung = null;
    private Date realisierungstermin = null;
    private Boolean akt = null;
    private Boolean manuellVerteilt = null;
    private Long portierungsartId = null;
    private Boolean verschoben = null;
    private Boolean observeProcess = null;
    private String bemerkung = null;
    private Long installationRefId = null;
    private Boolean preventCPSProvisioning = null;
    private Boolean notPossible = null;
    private String workforceOrderId = null;
    private Set<Long> subAuftragsIds = new HashSet<>();

    public boolean hasSubOrders() {
        if (subAuftragsIds == null) { return false; }
        return !subAuftragsIds.isEmpty();
    }

    public Set<Long> getAllOrderIdsOfVerlauf() {
        Set<Long> orderIds = new HashSet<>();
        orderIds.add(getAuftragId());
        if (CollectionTools.isNotEmpty(getSubAuftragsIds())) {
            orderIds.addAll(getSubAuftragsIds());
        }
        return orderIds;
    }


    /**
     * Gibt an, ob es sich bei dem Verlauf um einen Bauauftrag (true) oder um eine Projektierung (false) handelt.
     * @return
     */
    public boolean isBauauftrag() {
        return !BooleanTools.nullToFalse(getProjektierung());
    }


    /**
     * Ueberprueft, ob es sich bei dem Verlauf um einen Storno handelt.
     *
     * @return true, wenn der Verlauf storniert wurde.
     */
    public boolean isStorno() {
        return (NumberTools.isIn(getVerlaufStatusId(), new Number[] { VerlaufStatus.VERLAUF_STORNIERT,
                VerlaufStatus.KUENDIGUNG_VERLAUF_STORNIERT }));
    }

    /**
     * Prueft, ob es sich bei dem Verlauf um einen Kuendigungs-Verlauf handelt.
     *
     * @return ture, wenn der Verlauf eine Kuendigung ausloest.
     */
    public boolean isKuendigung() {
        return (NumberTools.isGreaterOrEqual(getVerlaufStatusId(), VerlaufStatus.KUENDIGUNG_BEI_DISPO));
    }

    public boolean isNeuschaltungOrAnschlussuebernahme() {
        return NumberTools.isIn(getAnlass(), new Number[] { BAVerlaufAnlass.NEUSCHALTUNG,
                BAVerlaufAnlass.ABW_TKG46_NEUSCHALTUNG, BAVerlaufAnlass.ANSCHLUSSUEBERNAHME });
    }

    public boolean isVerteilt() {
        return ((NumberTools.isGreaterOrEqual(getVerlaufStatusId(), VerlaufStatus.BEI_TECHNIK) && NumberTools.isLess(
                getVerlaufStatusId(), VerlaufStatus.VERLAUF_STORNIERT)) || NumberTools.isGreaterOrEqual(
                getVerlaufStatusId(), VerlaufStatus.KUENDIGUNG_BEI_TECHNIK));
    }

    public Boolean getAkt() {
        return akt;
    }

    public void setAkt(Boolean akt) {
        this.akt = akt;
    }

    public Long getAnlass() {
        return anlass;
    }

    public void setAnlass(Long anlass) {
        this.anlass = anlass;
    }

    public Long getAuftragId() {
        return auftragId;
    }

    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    public Boolean getProjektierung() {
        return projektierung;
    }

    public void setProjektierung(Boolean projektierung) {
        this.projektierung = projektierung;
    }

    public Date getRealisierungstermin() {
        return realisierungstermin;
    }

    public void setRealisierungstermin(Date realisierungstermin) {
        this.realisierungstermin = realisierungstermin;
    }

    public Long getStatusIdAlt() {
        return statusIdAlt;
    }

    public void setStatusIdAlt(Long statusIdAlt) {
        this.statusIdAlt = statusIdAlt;
    }

    public Long getVerlaufStatusId() {
        return verlaufStatusId;
    }

    public void setVerlaufStatusId(Long verlaufStatusId) {
        this.verlaufStatusId = verlaufStatusId;
    }

    public Boolean getManuellVerteilt() {
        return manuellVerteilt;
    }

    public void setManuellVerteilt(Boolean manuellVerteilt) {
        this.manuellVerteilt = manuellVerteilt;
    }

    @Override
    public Long getPortierungsartId() {
        return portierungsartId;
    }

    public void setPortierungsartId(Long portierungsartId) {
        this.portierungsartId = portierungsartId;
    }

    public Boolean getVerschoben() {
        return verschoben;
    }

    public void setVerschoben(Boolean verschoben) {
        this.verschoben = verschoben;
    }

    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    /**
     * Flag gibt an, dass der Verlauf beobachtet/ueberprueft werden soll. Dadurch wird sichergestellt, dass der
     * Abschluss des Verlaufs nur ueber die Ruecklaeufer (Dispo/NP und AM) erfolgen kann.
     *
     * @return Returns the observeProcess.
     */
    public Boolean getObserveProcess() {
        return this.observeProcess;
    }

    public void setObserveProcess(Boolean observeProcess) {
        this.observeProcess = observeProcess;
    }

    /**
     * Gibt den Typ der gewuenschten Installation an.
     *
     * @return Returns the installationRefId.
     */
    public Long getInstallationRefId() {
        return installationRefId;
    }

    public void setInstallationRefId(Long installationRefId) {
        this.installationRefId = installationRefId;
    }

    public Boolean getPreventCPSProvisioning() {
        return preventCPSProvisioning;
    }

    public void setPreventCPSProvisioning(Boolean preventCPSProvisioning) {
        this.preventCPSProvisioning = preventCPSProvisioning;
    }

    public Boolean getNotPossible() {
        return notPossible;
    }

    public void setNotPossible(Boolean notPossible) {
        this.notPossible = notPossible;
    }

    public String getWorkforceOrderId() {
        return workforceOrderId;
    }

    public void setWorkforceOrderId(String workforceOrderId) {
        this.workforceOrderId = workforceOrderId;
    }

    public Set<Long> getSubAuftragsIds() {
        return subAuftragsIds;
    }

    public void setSubAuftragsIds(Set<Long> subAuftragsIds) {
        this.subAuftragsIds = subAuftragsIds;
    }
}
