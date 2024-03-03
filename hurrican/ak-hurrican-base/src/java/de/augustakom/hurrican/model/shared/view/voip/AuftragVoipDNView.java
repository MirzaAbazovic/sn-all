/*
 * Copyright (c) 2008 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.10.2008 11:01:42
 */
package de.augustakom.hurrican.model.shared.view.voip;

import java.util.*;
import java.util.stream.*;
import javax.annotation.*;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.model.DebugModel;
import de.augustakom.common.model.HistoryModel;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.OptionalTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.VoipDnPlanView;
import de.augustakom.hurrican.model.cc.cps.serviceorder.AbstractCPSDNData;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;

/**
 * View-Modell, um Rufnummerndaten und Passwort darzustellen. (Die Informationen werden ueber Billing- und CC-Services
 * zusammengetragen.)
 *
 *
 */
public class AuftragVoipDNView extends Observable implements CCAuftragModel, DebugModel, Observer, HistoryModel {

    public static final int MAX_PORTS_SUPPORTED = 8;

    /**
     * Sortiert nach "gueltigAb"
     */
    private static final Ordering<VoipDnPlanView> VOIP_DN_PLAN_VIEW_ORDERING =
            Ordering.natural().onResultOf(new Function<VoipDnPlanView, Date>() {
                @Nullable
                @Override
                public Date apply(@Nullable final VoipDnPlanView input) {
                    return (input == null) ? null : input.getGueltigAb();
                }
            });

    private static final Ordering<VoipDnPlanView> VOIP_DN_PLAN_VIEW_DESC_ORDERING =
            VOIP_DN_PLAN_VIEW_ORDERING.reverse().nullsLast();

    private Long auftragId;
    private String onKz;
    private String dnBase;
    private Long dnNoOrig;
    private Date gueltigVon;
    private Date gueltigBis;
    private String histStatus;
    private Boolean mainNumber;
    private String taifunDescription;
    private BlockDNView block = BlockDNView.NO_BLOCK;
    private Reference sipDomain;
    private String sipPassword;
    private List<VoipDnPlanView> voipDnPlanViews = Lists.newArrayList();
    private List<SelectedPortsView> selectedPorts = Lists.newArrayList();

    public List<VoipDnPlanView> getVoipDnPlanViewsDesc() {
        return VOIP_DN_PLAN_VIEW_DESC_ORDERING.sortedCopy(voipDnPlanViews);
    }

    /**
     * @return alle Plaene, welche ein gueltigAb in der Zukunft haben + ersten Plan mit gueltigAb <= heute (aktueller Plan)
     */
    public List<VoipDnPlanView> getValidVoipDnPlanViews() {
        final Date today = new Date();
        if (DateTools.isDateBefore(gueltigBis, today)) {
            // abgelaufene RN haben keinen gueltigen RN-Plan
            return ImmutableList.of();
        }
        List<VoipDnPlanView> sortedPlans = getVoipDnPlanViewsDesc();
        for (VoipDnPlanView plan : sortedPlans) {
            if (DateTools.isDateBeforeOrEqual(plan.getGueltigAb(), today)) {
                return sortedPlans.subList(0, sortedPlans.indexOf(plan)+1);
            }
        }
        return sortedPlans;
    }

    /**
     * @return den neuesten Plan (mit maximalem gueltigAb)
     */
    @Nullable
    public VoipDnPlanView getLatestVoipDnPlanView() {
        List<VoipDnPlanView> allPlans = getVoipDnPlanViewsDesc();
        return allPlans.isEmpty() ? null : allPlans.get(0);
    }

    /**
     * @return den Plan zum Zeitpunkt {@code when}, fuer den {@code gueltigAb} am naechsten liegt und bereits
     * ueberschritten ist
     */
    public Optional<VoipDnPlanView> getMostCurrentDnPlanView(@Nonnull final Date when) {
        return voipDnPlanViews.stream()
                .filter(plan -> DateTools.isDateBeforeOrEqual(plan.getGueltigAb(), when))
                .max(Comparator.comparing(VoipDnPlanView::getGueltigAb));
    }

    /**
     * @return den zum Zeitpunkt {@code when} aktiven Plan
     */
    public Optional<VoipDnPlanView> getActiveVoipDnPlanView(@Nonnull final Date when) {
        return voipDnPlanViews.stream()
                .filter(plan -> DateTools.isDateBeforeOrEqual(plan.getGueltigAb(), when)
                        && DateTools.isDateAfterOrEqual(getGueltigBis(), when))
                .max(Comparator.comparing(VoipDnPlanView::getGueltigAb));
    }

    public Optional<VoipDnPlanView> getFirstInFutureVoipDnPlanView() {
        final Date now = new Date();
        return getFirstInFutureVoipDnPlanView(now);
    }

    public List<VoipDnPlanView> getVoipDnPlanViewsAfterDate(@Nonnull final Date afterDate) {
        return voipDnPlanViews.stream()
                .filter(plan -> DateTools.isDateAfter(plan.getGueltigAb(), afterDate))
                .sorted(Comparator.comparing(VoipDnPlanView::getGueltigAb))
                .collect(Collectors.toList());
    }

    public Optional<VoipDnPlanView> getFirstInFutureVoipDnPlanView(@Nonnull final Date when) {
        return voipDnPlanViews.stream()
                .filter(plan -> DateTools.isDateAfter(plan.getGueltigAb(), when))
                .min(Comparator.comparing(VoipDnPlanView::getGueltigAb));
    }

    public Optional<VoipDnPlanView> getFirstInFutureOrLatestVoipDnPlanView() {
        final Date now = new Date();
        return OptionalTools.orElse(getFirstInFutureVoipDnPlanView(), () -> getActiveVoipDnPlanView(now));
    }

    public Optional<VoipDnPlanView> getActiveOrFirstInFutureVoipDnPlanView(@Nonnull final Date when) {
        return OptionalTools.orElse(getActiveVoipDnPlanView(when), () -> getFirstInFutureVoipDnPlanView(when));
    }

    public void addVoipDnPlanView(final VoipDnPlanView voipDnPlanView) {
        if (!this.isBlock()) {
            throw new RuntimeException("Rufnummernpläne dürfen nur Blockrufnummern zugewiesen werden!");
        }
        setChanged();
        voipDnPlanView.addObserver(this);
        this.voipDnPlanViews.add(voipDnPlanView);
        notifyObservers();
    }

    public void removeVoipDnPlanView(final VoipDnPlanView voipDnPlanView) {
        if (this.voipDnPlanViews.contains(voipDnPlanView)) {
            setChanged();
            voipDnPlanView.deleteObserver(this);
            this.voipDnPlanViews.remove(voipDnPlanView);
            notifyObservers();
        }
    }

    public List<SelectedPortsView> getSelectedPorts() {
        return ImmutableList.copyOf(selectedPorts);
    }

    public void addSelectedPort(@Nonnull final SelectedPortsView selectedPort) {
        setChanged();
        selectedPort.addObserver(this);
        selectedPorts.add(selectedPort);
        notifyObservers();
    }

    /**
     * @return Returns the isBlock.
     */
    public boolean isBlock() {
        return (!block.equals(BlockDNView.NO_BLOCK));
    }

    public void setBlock(@Nullable BlockDNView block) {
        if (block == null) {
            this.block = BlockDNView.NO_BLOCK;
        }
        else {
            this.block = block;
        }
    }

    /**
     * @return Returns the isMain.
     */
    public Boolean getMainNumber() {
        return mainNumber;
    }

    public void setMainNumber(Boolean mainNumber) {
        this.mainNumber = mainNumber;
    }

    public Reference getSipDomain() {
        return sipDomain;
    }

    public void setSipDomain(Reference sipDomain) {
        setChanged();
        this.sipDomain = sipDomain;
        notifyObservers();
    }

    /**
     * Auftrag-ID aus dem CC-System.
     *
     * @return Returns the auftragId.
     */
    @Override
    public Long getAuftragId() {
        return auftragId;
    }

    /**
     * Auftrag-ID aus dem CC-System.
     *
     * @param auftragId The auftragId to set.
     */
    @Override
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    /**
     * @return Returns the sipPassword.
     */
    public String getSipPassword() {
        return sipPassword;
    }

    /**
     * @param sipPassword The sipPassword to set.
     */
    public void setSipPassword(String sipPassword) {
        setChanged();
        this.sipPassword = sipPassword;
        notifyObservers();
    }

    /**
     * @return Returns the onKz.
     */
    public String getOnKz() {
        return onKz;
    }

    /**
     * @param onKz The onKz to set.
     */
    public void setOnKz(String onKz) {
        this.onKz = onKz;
    }

    /**
     * @return Returns the dnBase.
     */
    public String getDnBase() {
        return dnBase;
    }

    /**
     * @param dnBase The dnBase to set.
     */
    public void setDnBase(String dnBase) {
        this.dnBase = dnBase;
    }

    /**
     * @return the dnNoOrig
     */
    public Long getDnNoOrig() {
        return dnNoOrig;
    }

    /**
     * @param dnNoOrig the dnNoOrig to set
     */
    public void setDnNoOrig(Long dnNoOrig) {
        this.dnNoOrig = dnNoOrig;
    }

    /**
     * @return the gueltigVon
     */
    public Date getGueltigVon() {
        return gueltigVon;
    }

    /**
     * @param gueltigVon the gueltigVon to set
     */
    public void setGueltigVon(Date gueltigVon) {
        setChanged();
        this.gueltigVon = gueltigVon;
        notifyObservers();
    }

    /**
     * @return the gueltigBis
     */
    public Date getGueltigBis() {
        return gueltigBis;
    }

    /**
     * @param gueltigBis the gueltigBis to set
     */
    public void setGueltigBis(Date gueltigBis) {
        setChanged();
        this.gueltigBis = gueltigBis;
        notifyObservers();
    }

    /**
     * @return the histStatus
     */
    public String getHistStatus() {
        return histStatus;
    }

    /**
     * @param histStatus the histStatus to set
     */
    public void setHistStatus(String histStatus) {
        this.histStatus = histStatus;
    }

    /**
     * @return Returns the taifunDescription.
     */
    public String getTaifunDescription() {
        return taifunDescription;
    }

    /**
     * @param taifunDescription The taifunDescription to set.
     */
    public void setTaifunDescription(String taifunDescription) {
        this.taifunDescription = taifunDescription;
    }

    public String getDirectDial() {
        return block.getDirectDial();
    }

    public String getRangeFrom() {
        return block.getRangeFrom();
    }

    public String getRangeTo() {
        return block.getRangeTo();
    }

    /**
     * @return Gibt eine String-Repräsentation der zugrunde liegenden Rufnummer zurück.
     * <p/>
     * Implementierung ähnelt {@link de.augustakom.hurrican.model.billing.Rufnummer#getRufnummer}.
     */
    @Nonnull
    public String getFormattedRufnummer() {
        StringBuilder sb = new StringBuilder();
        sb.append(getOnKz());
        sb.append(" ");
        sb.append(getDnBase());
        if (StringUtils.isNotBlank(getDirectDial())) {
            sb.append(" ");
            sb.append(getDirectDial());
        }
        if (isBlock()) {
            sb.append(" ");
            sb.append(StringUtils.trim(getRangeFrom()));
            sb.append("-");
            sb.append(StringUtils.trim(getRangeTo()));
        }
        return sb.toString();
    }

    /**
     * Baut den SIP Login entsprechend Vorgaben von CPS auf.
     *
     * @return
     */
    public String getFormattedSipLogin() {
        if (isBlock() && getLatestVoipDnPlanView() != null) {
            return getLatestVoipDnPlanView().getSipLogin();
        }
        // else
        StringBuilder sb = new StringBuilder();
        sb.append(AbstractCPSDNData.COUNTRY_CODE_GERMANY);
        sb.append(StringTools.removeStartToEmpty(getOnKz(), '0'));
        sb.append(getDnBase());
        return sb.toString();
    }

    /**
     * @see de.augustakom.common.model.DebugModel#debugModel(org.apache.log4j.Logger)
     */
    @Override
    public void debugModel(@Nullable Logger logger) {
        if ((logger != null) && logger.isDebugEnabled()) {
            logger.debug("  Ruf-Nr     : " + getOnKz() + "/" + getDnBase() + "   Passwort   : " + getSipPassword());
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof SelectedPortsView || o instanceof VoipDnPlanView) {
            setChanged();
            notifyObservers();
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(auftragId, onKz, dnBase, dnNoOrig, gueltigVon, gueltigBis, histStatus, mainNumber,
                taifunDescription, block, sipDomain, sipPassword, voipDnPlanViews, selectedPorts);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final AuftragVoipDNView other = (AuftragVoipDNView) obj;
        return Objects.equals(this.auftragId, other.auftragId) && Objects.equals(this.onKz, other.onKz)
                && Objects.equals(this.dnBase, other.dnBase) && Objects.equals(this.dnNoOrig, other.dnNoOrig)
                && Objects.equals(this.gueltigVon, other.gueltigVon)
                && Objects.equals(this.gueltigBis, other.gueltigBis)
                && Objects.equals(this.histStatus, other.histStatus)
                && Objects.equals(this.mainNumber, other.mainNumber)
                && Objects.equals(this.taifunDescription, other.taifunDescription)
                && Objects.equals(this.block, other.block)
                && Objects.equals(this.sipDomain, other.sipDomain)
                && Objects.equals(this.sipPassword, other.sipPassword)
                && Objects.equals(this.voipDnPlanViews, other.voipDnPlanViews)
                && Objects.equals(this.selectedPorts, other.selectedPorts);
    }
}
