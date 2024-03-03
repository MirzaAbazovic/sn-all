/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.10.13
 */

package de.mnet.wbci.model;

import static de.mnet.wbci.model.Severity.*;

import java.util.*;

/**
 * Definiert alle Request(Storno, TV und VA) Zustaende und beim Zustaendswechsel die naechsten erlaubten Zustaende.
 */
public enum WbciRequestStatus {

    //
    // Vorabstimmung Status - abgebender Carrier
    //

    ABBM_TR_VERSENDET(LEVEL_10, "ABBM-TR versendet", false, false),
    AKM_TR_EMPFANGEN(null, "AKM-TR empfangen", true, false),
    ABBM_VERSENDET(LEVEL_20, "ABBM versendet", false, false),
    RUEM_VA_VERSENDET(LEVEL_0, "RUEM-VA versendet", false, false),
    VA_EMPFANGEN(null, "VA empfangen", true, false),

    //
    // Vorabstimmung Status - aufnehmender Carrier
    //

    ABBM_TR_EMPFANGEN(LEVEL_10, "ABBM-TR empfangen", true, false),
    AKM_TR_VERSENDET(null, "AKM-TR versendet", false, false),
    ABBM_EMPFANGEN(LEVEL_20, "ABBM empfangen", true, false),
    RUEM_VA_EMPFANGEN(LEVEL_0, "RUEM-VA empfangen", true, false),
    VA_VERSENDET(null, "VA versendet", false, false),
    VA_VORGEHALTEN(null, "VA vorgehalten", false, true),

    //
    // Terminverschiebung Status - abgebender Carrier
    //

    TV_ERLM_VERSENDET(LEVEL_0, "TV ERLM versendet", false, false),
    TV_ABBM_VERSENDET(LEVEL_10, "TV ABBM versendet", false, false),
    TV_EMPFANGEN(null, "TV empfangen", true, false),

    //
    // Terminverschiebung Status - aufnehmender Carrier
    //

    TV_ERLM_EMPFANGEN(LEVEL_0, "TV ERLM empfangen", true, false),
    TV_ABBM_EMPFANGEN(LEVEL_10, "TV ABBM empfangen", true, false),
    TV_VERSENDET(null, "TV versendet", false, false),
    TV_VORGEHALTEN(null, "TV vorgehalten", false, true),

    //
    // Storno Status - absender (kann von dem abgebender oder aufnehmender Carrier versendet)
    //

    STORNO_ERLM_EMPFANGEN(LEVEL_0, "Storno ERLM empfangen", true, false),
    STORNO_ABBM_EMPFANGEN(LEVEL_20, "Storno ABBM empfangen", true, false),
    STORNO_VERSENDET(null, "Storno versendet", false, false),
    STORNO_VORGEHALTEN(null, "Storno vorgehalten", false, true),

    //
    // Storno Status - empfanger (kann von dem abgebender oder aufnehmender Carrier empfangen)
    //

    STORNO_ERLM_VERSENDET(LEVEL_0, "Storno ERLM versendet", false, false),
    STORNO_ABBM_VERSENDET(null, "Storno ABBM versendet", false, false),
    STORNO_EMPFANGEN(LEVEL_10, "Storno empfangen", true, false);

    private static final Map<WbciRequestStatus, WbciRequestStatus[]> LEGAL_STATUS_CHANGES = new HashMap<>();

    static {
        LEGAL_STATUS_CHANGES.put(AKM_TR_EMPFANGEN, new WbciRequestStatus[] { ABBM_TR_VERSENDET });
        LEGAL_STATUS_CHANGES.put(ABBM_TR_VERSENDET, new WbciRequestStatus[] { AKM_TR_EMPFANGEN });
        LEGAL_STATUS_CHANGES.put(RUEM_VA_VERSENDET, new WbciRequestStatus[] { AKM_TR_EMPFANGEN });
        LEGAL_STATUS_CHANGES.put(VA_EMPFANGEN, new WbciRequestStatus[] { RUEM_VA_VERSENDET, ABBM_VERSENDET });
        LEGAL_STATUS_CHANGES.put(AKM_TR_VERSENDET, new WbciRequestStatus[] { ABBM_TR_EMPFANGEN });
        LEGAL_STATUS_CHANGES.put(ABBM_TR_EMPFANGEN, new WbciRequestStatus[] { AKM_TR_VERSENDET });
        LEGAL_STATUS_CHANGES.put(RUEM_VA_EMPFANGEN, new WbciRequestStatus[] { AKM_TR_VERSENDET });
        LEGAL_STATUS_CHANGES.put(VA_VERSENDET, new WbciRequestStatus[] { RUEM_VA_EMPFANGEN, ABBM_EMPFANGEN });
        LEGAL_STATUS_CHANGES.put(VA_VORGEHALTEN, new WbciRequestStatus[] { VA_VERSENDET });
        LEGAL_STATUS_CHANGES.put(TV_EMPFANGEN, new WbciRequestStatus[] { TV_ERLM_VERSENDET, TV_ABBM_VERSENDET });
        LEGAL_STATUS_CHANGES.put(TV_VERSENDET, new WbciRequestStatus[] { TV_ERLM_EMPFANGEN, TV_ABBM_EMPFANGEN });
        LEGAL_STATUS_CHANGES.put(TV_VORGEHALTEN, new WbciRequestStatus[] { TV_VERSENDET });
        LEGAL_STATUS_CHANGES.put(STORNO_VERSENDET, new WbciRequestStatus[] { STORNO_ERLM_EMPFANGEN, STORNO_ABBM_EMPFANGEN });
        LEGAL_STATUS_CHANGES.put(STORNO_VORGEHALTEN, new WbciRequestStatus[] { STORNO_VERSENDET });
        LEGAL_STATUS_CHANGES.put(STORNO_EMPFANGEN, new WbciRequestStatus[] { STORNO_ERLM_VERSENDET, STORNO_ABBM_VERSENDET });
    }

    private final Severity severity;
    private final String description;
    private final boolean inbound;
    private final boolean vorgehalten;

    private WbciRequestStatus(Severity severity, String description, boolean inbound, boolean vorgehalten) {
        this.severity = severity;
        this.description = description;
        this.inbound = inbound;
        this.vorgehalten = vorgehalten;
    }

    public String getDescription() {
        return description;
    }

    public boolean isInbound() {
        return inbound;
    }

    public boolean isVorgehalten() {
        return vorgehalten;
    }

    public WbciRequestStatus[] getNextLegalStatusChanges() {
        WbciRequestStatus[] nextLegalStatusChanges = LEGAL_STATUS_CHANGES.get(this);
        if (nextLegalStatusChanges != null) {
            return Arrays.copyOf(nextLegalStatusChanges, nextLegalStatusChanges.length);
        }

        // There are no legal status changes from this status
        return new WbciRequestStatus[] { };
    }

    /**
     * If the status change is legal with respect to the current request status, then true is returned.
     *
     * @param nextRequestStatus
     * @return
     */
    public boolean isLegalStatusChange(WbciRequestStatus nextRequestStatus) {
        WbciRequestStatus[] legalStatusChanges = this.getNextLegalStatusChanges();
        if (legalStatusChanges != null) {
            for (WbciRequestStatus legalStatusChange : legalStatusChanges) {
                if (legalStatusChange.equals(nextRequestStatus)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns a list of all the vorabstimmung request statuses
     *
     * @return
     */
    public static List<WbciRequestStatus> getVaRequestStatuses() {
        return Arrays.asList(ABBM_TR_VERSENDET,
                AKM_TR_EMPFANGEN,
                ABBM_VERSENDET,
                RUEM_VA_VERSENDET,
                VA_EMPFANGEN,
                ABBM_TR_EMPFANGEN,
                AKM_TR_VERSENDET,
                ABBM_EMPFANGEN,
                RUEM_VA_EMPFANGEN,
                VA_VERSENDET,
                VA_VORGEHALTEN);
    }

    /**
     * Returns a list of all the storno request statuses
     *
     * @return
     */
    public static List<WbciRequestStatus> getStornoRequestStatuses() {
        return Arrays.asList(STORNO_ERLM_EMPFANGEN,
                STORNO_ABBM_EMPFANGEN,
                STORNO_VERSENDET,
                STORNO_VORGEHALTEN,
                STORNO_ERLM_VERSENDET,
                STORNO_ABBM_VERSENDET,
                STORNO_EMPFANGEN);
    }

    /**
     * Returns a list of all the <b>active</b> storno request statuses
     *
     * @return
     */
    public static List<WbciRequestStatus> getActiveStornoRequestStatuses() {
        return Arrays.asList(STORNO_VERSENDET, STORNO_VORGEHALTEN, STORNO_EMPFANGEN);
    }

    /**
     * Returns a list of all the <b>active</b> storno request statuses
     *
     * @return
     */
    public static List<WbciRequestStatus> getActiveTvRequestStatuses() {
        return Arrays.asList(TV_VERSENDET, TV_VORGEHALTEN, TV_EMPFANGEN);
    }

    /**
     * Returns a list of all the <b>active</b> storno and tv request statuses
     *
     * @return
     */
    public static List<WbciRequestStatus> getActiveChangeRequestStatuses() {
        List<WbciRequestStatus> activeChangeStatuses = new ArrayList<>();
        activeChangeStatuses.addAll(getActiveStornoRequestStatuses());
        activeChangeStatuses.addAll(getActiveTvRequestStatuses());
        return activeChangeStatuses;
    }

    /**
     * Returns a list of all the terminverschiebung request statuses
     *
     * @return
     */
    public static List<WbciRequestStatus> getTvRequestStatuses() {
        return Arrays.asList(TV_ERLM_VERSENDET,
                TV_ABBM_VERSENDET,
                TV_EMPFANGEN,
                TV_ERLM_EMPFANGEN,
                TV_ABBM_EMPFANGEN,
                TV_VERSENDET,
                TV_VORGEHALTEN);
    }


    /**
     * Returns a list of all stati, which represents a new WBCI request (VA, TV or Storno).
     * @return
     */
    public static List<WbciRequestStatus> getInitialStatuses() {
        return Arrays.asList(VA_VERSENDET,
                VA_EMPFANGEN,
                VA_VORGEHALTEN,
                TV_VERSENDET,
                TV_EMPFANGEN,
                TV_VORGEHALTEN,
                STORNO_VERSENDET,
                STORNO_EMPFANGEN,
                STORNO_VORGEHALTEN);
    }

    /**
     * returns true if the status corresponds to an initial status.
     * @return
     */
    public boolean isInitialStatus() {
        return WbciRequestStatus.getInitialStatuses().contains(this);
    }

    /**
     * returns true if the status corresponds to a vorabstimmung status
     *
     * @return
     */
    public boolean isVaRequestStatus() {
        return WbciRequestStatus.getVaRequestStatuses().contains(this);
    }

    /**
     * returns true if the status corresponds to a terminverschiebung status
     *
     * @return
     */
    public boolean isTvRequestStatus() {
        return WbciRequestStatus.getTvRequestStatuses().contains(this);
    }

    /**
     * Checks if the status corresponds to an active tv request. A TV request is considered active as long as no ERLM or
     * ABBM has been received or sent
     *
     * @return true when active, otherwise false
     */
    public boolean isActiveTvRequestStatus() {
        return Arrays.asList(TV_EMPFANGEN, TV_VERSENDET, TV_VORGEHALTEN).contains(this);
    }

    /**
     * returns true if the status corresponds to a storno status
     *
     * @return
     */
    public boolean isStornoRequestStatus() {
        return WbciRequestStatus.getStornoRequestStatuses().contains(this);
    }

    /**
     * returns true if the status corresponds to a storno status
     *
     * @return
     */
    public boolean isStornoErledigtRequestStatus() {
        return Arrays.asList(STORNO_ERLM_EMPFANGEN, STORNO_ERLM_VERSENDET).contains(this);
    }

    /**
     * Checks if the status corresponds to an active storno request. A storno request is considered active as long as no
     * ERLM or ABBM has been received or sent
     *
     * @return true when active, otherwise false
     */
    public boolean isActiveStornoRequestStatus() {
        return getActiveStornoRequestStatuses().contains(this);
    }

    /**
     * Checks if the status corresponds to an active change request (storno or tv ). A request is considered active
     * as long as no ERLM or ABBM has been received or sent
     *
     * @return true when active, otherwise false
     */
    public boolean isActiveChangeRequestStatus() {
        return getActiveStornoRequestStatuses().contains(this) || getActiveTvRequestStatuses().contains(this);
    }

    public Severity getSeverity(CarrierRole mnetCarrierRole) {
        // in the case of storno requests & notifications, the severity depends on whether M-Net is the donating or
        // receiving carrier. By default the storno severity is set according to when M-Net is the receiving carrier and
        // therefore must be overridden when M-Net is the donating carrier
        if (CarrierRole.ABGEBEND.equals(mnetCarrierRole)) {
            if (STORNO_ABBM_EMPFANGEN.equals(this)) {
                return LEVEL_10;
            }
            else if (STORNO_ABBM_VERSENDET.equals(this)) {
                return LEVEL_10;
            }
            else if (STORNO_EMPFANGEN.equals(this)) {
                return null;
            }
        }

        return severity;
    }

}
