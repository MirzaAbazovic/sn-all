/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.11.13
 */
package de.mnet.wbci.model;

import static de.mnet.wbci.model.CarrierRole.*;
import static de.mnet.wbci.model.WbciGeschaeftsfallStatus.*;
import static de.mnet.wbci.model.WbciRequestStatus.*;

import java.util.*;

import de.mnet.wbci.model.helper.WbciRequestHelper;

/**
 * Defines all possible WBCI actions. This is used in particular by the GUI to determine when a button can be enabled.
 */
public enum WbciAction {

    //
    // Vorabstimmung Actions - abgebender Carrier
    //

    CREATE_ABBM_TR(new CarrierRole[] { ABGEBEND }, ABBM_TR_VERSENDET),
    CREATE_ABBM(new CarrierRole[] { ABGEBEND }, ABBM_VERSENDET),
    CREATE_RUEM_VA(new CarrierRole[] { ABGEBEND }, RUEM_VA_VERSENDET),

    //
    // Vorabstimmung Actions - aufnehmender Carrier
    //

    CREATE_AKM_TR(new CarrierRole[] { AUFNEHMEND }, AKM_TR_VERSENDET),

    //
    // Terminverschiebung Actions - abgebender Carrier
    //

    CREATE_TV_ERLM(new CarrierRole[] { ABGEBEND }, TV_ERLM_VERSENDET),
    CREATE_TV_ABBM(new CarrierRole[] { ABGEBEND }, TV_ABBM_VERSENDET),

    //
    // Terminverschiebung Actions - aufnehmender Carrier
    //

    CREATE_TV(new CarrierRole[] { AUFNEHMEND }, TV_VERSENDET, true, new WbciRequestStatus[] { RUEM_VA_EMPFANGEN,
            AKM_TR_VERSENDET }),

    //
    // Storno Actions - aufnehmend und abgebend
    //

    CREATE_STORNO(new CarrierRole[] { AUFNEHMEND, ABGEBEND }, STORNO_VERSENDET, true, new WbciRequestStatus[] {
            VA_VORGEHALTEN, VA_VERSENDET, RUEM_VA_EMPFANGEN, RUEM_VA_VERSENDET, AKM_TR_EMPFANGEN, AKM_TR_VERSENDET }),
    CREATE_STORNO_AENDERUNG(new CarrierRole[] { AUFNEHMEND, ABGEBEND }, STORNO_VERSENDET, true, new WbciRequestStatus[] {
            RUEM_VA_EMPFANGEN, RUEM_VA_VERSENDET, AKM_TR_EMPFANGEN, AKM_TR_VERSENDET}),

    //
    // Storno Actions - aufnehmend und abgebend
    //

    CREATE_STORNO_ERLM(new CarrierRole[] { AUFNEHMEND, ABGEBEND }, STORNO_ERLM_VERSENDET),
    CREATE_STORNO_ABBM(new CarrierRole[] { AUFNEHMEND, ABGEBEND }, STORNO_ABBM_VERSENDET),

    //
    // Geschaeftsfall Actions - aufnehmend und abgebend
    //

    CLOSE_GF(new CarrierRole[] { AUFNEHMEND, ABGEBEND }, COMPLETE, new WbciGeschaeftsfallStatus[] { ACTIVE, PASSIVE, NEW_VA_EXPIRED }),

    EDIT_AUTOMATED_FLAG(new CarrierRole[] { AUFNEHMEND, ABGEBEND }, null, false,
            new WbciRequestStatus[] { VA_VORGEHALTEN, VA_VERSENDET, VA_EMPFANGEN, RUEM_VA_VERSENDET, RUEM_VA_EMPFANGEN });

    private final CarrierRole[] carrierRolePrerequisites;
    private final boolean createsNewRequest;
    private final WbciGeschaeftsfallStatus newGeschaeftsfallStatus;
    private final WbciRequestStatus newRequestStatus;
    private final WbciRequestStatus[] vaRequestPrerequisites;
    private final WbciGeschaeftsfallStatus[] gfStatusPrerequisites;

    /**
     * Constructor for request actions (VA, STORNO or TV actions)
     *
     * @param carrierRolePrerequisites the m-net role prerequisite for this action
     * @param newRequestStatus         after performing the action, this will be the status of the request on successful
     *                                 completion
     */
    private WbciAction(CarrierRole[] carrierRolePrerequisites, WbciRequestStatus newRequestStatus) {
        this(carrierRolePrerequisites, newRequestStatus, false, new WbciRequestStatus[] { });
    }

    /**
     * Constructor for request actions (VA, STORNO or TV actions)
     *
     * @param carrierRolePrerequisites the m-net role prerequisite for this action
     * @param newRequestStatus         after performing the action, this will be the status of the request on successful
     *                                 completion
     * @param createsNewRequest        indicates whether the action leads to the creation of a new WBCI request
     * @param vaRequestPrerequisites   for non-va request actions (TV or STORNO) the va-request status must match one of
     *                                 the following statuses for the action to be permitted.
     */
    private WbciAction(CarrierRole[] carrierRolePrerequisites, WbciRequestStatus newRequestStatus,
            boolean createsNewRequest, WbciRequestStatus[] vaRequestPrerequisites) {
        this.carrierRolePrerequisites = carrierRolePrerequisites;
        this.newGeschaeftsfallStatus = null;
        this.newRequestStatus = newRequestStatus;
        this.createsNewRequest = createsNewRequest;
        this.vaRequestPrerequisites = vaRequestPrerequisites;
        // request actions can only be carried out when the GF has one of the following actions
        this.gfStatusPrerequisites = new WbciGeschaeftsfallStatus[] { ACTIVE, PASSIVE };
    }

    /**
     * Constructor for geschaeftfall actions
     *
     * @param carrierRolePrerequisites the m-net role prerequisite for this action
     * @param newGeschaeftsfallStatus  after performing the action, this will be the status of the geschaeftsfall on
     *                                 successful completion
     * @param gfStatusPrerequisites    the geschaeftsfall status must match one of the following statuses for the action
     *                                 to be permitted.
     */
    private WbciAction(CarrierRole[] carrierRolePrerequisites, WbciGeschaeftsfallStatus newGeschaeftsfallStatus,
            WbciGeschaeftsfallStatus[] gfStatusPrerequisites) {
        this.carrierRolePrerequisites = carrierRolePrerequisites;
        this.newGeschaeftsfallStatus = newGeschaeftsfallStatus;
        this.newRequestStatus = null;
        this.createsNewRequest = false;
        this.vaRequestPrerequisites = new WbciRequestStatus[] { };
        this.gfStatusPrerequisites = gfStatusPrerequisites;
    }

    /**
     * Returns the current VA status
     *
     * @param geschaeftsfallRequestStatuses the current request statuses attached to the geschaeftsfall.
     * @return
     */
    private static WbciRequestStatus getCurrentVaRequestStatus(List<WbciRequestStatus> geschaeftsfallRequestStatuses) {
        for (WbciRequestStatus requestStatus : geschaeftsfallRequestStatuses) {
            if (requestStatus.isVaRequestStatus()) {
                return requestStatus;
            }
        }
        return null;
    }

    /**
     * Returns the current STORNO statuses. If no storno requests are attached to the geschaeftsfall then an empty list
     * is returned.
     *
     * @param geschaeftsfallRequestStatuses the current request statuses attached to the geschaeftsfall.
     * @return
     */
    private static List<WbciRequestStatus> getCurrentStornoRequestStatuses(
            List<WbciRequestStatus> geschaeftsfallRequestStatuses) {
        List<WbciRequestStatus> stornoRequestStatuses = new ArrayList<>();
        for (WbciRequestStatus requestStatus : geschaeftsfallRequestStatuses) {
            if (requestStatus.isStornoRequestStatus()) {
                stornoRequestStatuses.add(requestStatus);
            }
        }
        return stornoRequestStatuses;
    }

    /**
     * Returns the {@link WbciRequestStatus} of the active TV request. A TV is considered to be active if a TV request
     * exists and <b>no</b> ERLM or ABBM has been sent/received for the TV.
     * <p/>
     * Note: Only one active TV can exist for a geschaeftsfall at any one time.
     *
     * @param geschaeftsfallRequestStatuses the current request statuses attached to the geschaeftsfall.
     * @return the active TV status or null if no active TV exists for the geschaeftsfall
     */
    private static WbciRequestStatus getActiveTvStatus(List<WbciRequestStatus> geschaeftsfallRequestStatuses) {
        for (WbciRequestStatus requestStatus : geschaeftsfallRequestStatuses) {
            if (requestStatus.isActiveTvRequestStatus()) {
                return requestStatus;
            }
        }
        return null;
    }

    /**
     * Returns the {@link WbciRequestStatus} of the active STORNO request. A STORNO is considered to be active if a
     * STORNO request exists and <b>no</b> ERLM or ABBM has been sent/received for the STORNO.
     * <p>
     * Note: Only one active STORNO can exist for a geschaeftsfall at any one time.
     *
     * @param geschaeftsfallRequestStatuses the current request statuses attached to the geschaeftsfall.
     * @return the active STORNO status or null if no active STORNO exists for the geschaeftsfall
     */
    private static WbciRequestStatus getActiveStornoStatus(List<WbciRequestStatus> geschaeftsfallRequestStatuses) {
        for (WbciRequestStatus requestStatus : geschaeftsfallRequestStatuses) {
            if (requestStatus.isActiveStornoRequestStatus()) {
                return requestStatus;
            }
        }
        return null;
    }

    /**
     * Returns true if an active TV or STORNO request is found
     *
     * @param geschaeftsfallRequestStatuses the current request statuses attached to the geschaeftsfall.
     * @return
     */
    private static boolean isActiveStornoOrTvRequestFound(List<WbciRequestStatus> geschaeftsfallRequestStatuses) {
        return WbciRequestHelper.isActiveStornoOrTvRequestStatusIncluded(geschaeftsfallRequestStatuses);
    }

    /**
     * Returns true if an STR-ERLM status is found.
     * @param geschaeftsfallRequestStatuses
     * @return
     */
    private static boolean isStornoErlmFound(List<WbciRequestStatus> geschaeftsfallRequestStatuses) {
        for (WbciRequestStatus requestStatus : geschaeftsfallRequestStatuses) {
            if (requestStatus.isStornoErledigtRequestStatus()) {
                return true;
            }
        }
        return false;
    }

    private static boolean isActiveStornoOrTvRequestVorgehalten(List<WbciRequestStatus> geschaeftsfallRequestStatuses) {
        for (WbciRequestStatus requestStatus : geschaeftsfallRequestStatuses) {
            if (WbciRequestStatus.TV_VORGEHALTEN.equals(requestStatus)
                    || WbciRequestStatus.STORNO_VORGEHALTEN.equals(requestStatus)) {
                return true;
            }
        }
        return false;
    }

    private static List<WbciAction> getVaActions() {
        return Arrays.asList(CREATE_ABBM_TR,
                CREATE_ABBM,
                CREATE_RUEM_VA,
                CREATE_AKM_TR,
                EDIT_AUTOMATED_FLAG);
    }

    private static List<WbciAction> getStornoActions() {
        return Arrays.asList(CREATE_STORNO,
                CREATE_STORNO_AENDERUNG,
                CREATE_STORNO_ERLM,
                CREATE_STORNO_ABBM);
    }

    private static List<WbciAction> getCreateStornoActions() {
        return Arrays.asList(CREATE_STORNO,
                CREATE_STORNO_AENDERUNG);
    }

    private static List<WbciAction> getCreateStornoErlmActions() {
        return Arrays.asList(CREATE_STORNO_ERLM);
    }

    private static List<WbciAction> getTvActions() {
        return Arrays.asList(CREATE_TV_ERLM,
                CREATE_TV_ABBM,
                CREATE_TV);
    }

    private static List<WbciAction> getGfActions() {
        return Arrays.asList(CLOSE_GF);
    }

    /**
     * Checks if the action is permitted. This check requires 3 pieces of information to make the decision: <ol>
     * <li>M-Net's role in the geschaeftsfall: {@link CarrierRole#ABGEBEND} or {@link CarrierRole#AUFNEHMEND}</li>
     * <li>The status of all requests attached to the geschaeftsfall</li> <li>The status of the geschaeftsfall</li>
     * <li>the current meldung types attached to the VA</li><li>the current request</li> </ol>
     *
     * @param mnetCarrierRole               the M-Net role
     * @param geschaeftsfallRequestStatuses the current request statuses attached to the geschaeftsfall.
     * @param geschaeftsfallStatus          the current status of the geschaeftsfall.
     * @param vaMeldungTypen                the current meldung types attached to the VA.
     * @param wbciRequest                   the current request.
     * @return
     */
    public boolean isActionPermitted(CarrierRole mnetCarrierRole,
            List<WbciRequestStatus> geschaeftsfallRequestStatuses,
            WbciGeschaeftsfallStatus geschaeftsfallStatus,
            List<MeldungTyp> vaMeldungTypen,
            WbciRequest wbciRequest) {

        // check that the m-net role is correct
        if (!Arrays.asList(carrierRolePrerequisites).contains(mnetCarrierRole)) {
            return false;
        }

        // checks that the gf status is correct
        if (!Arrays.asList(gfStatusPrerequisites).contains(geschaeftsfallStatus)) {
            return false;
        }

        // check that the action is allowed with current VA status
        WbciRequestStatus currentVaStatus = getCurrentVaRequestStatus(geschaeftsfallRequestStatuses);
        if (vaRequestPrerequisites.length > 0 && !Arrays.asList(vaRequestPrerequisites).contains(currentVaStatus)) {
            return false;
        }

        if (isVaAction()) {
            return isVaActionPermitted(geschaeftsfallRequestStatuses);
        }
        else if (isGfAction()) {
            return isGfActionPermitted(geschaeftsfallRequestStatuses, geschaeftsfallStatus);
        }
        else if (isCreateStornoAction()) {
            boolean permitted = isNonVaActionPermitted(geschaeftsfallRequestStatuses);
            if (!permitted) {
                // vorgehaltene Stornos/TVs duerfen storniert (=geloescht) werden
                permitted = isActiveStornoOrTvRequestVorgehalten(geschaeftsfallRequestStatuses);
            }
            return permitted;
        }
        else if (isCreateStornoErlmAction()) {
            return isCreateStornoErlmActionPermitted(vaMeldungTypen, wbciRequest)
                    && isNonVaActionPermitted(geschaeftsfallRequestStatuses);
        }
        else {
            return isNonVaActionPermitted(geschaeftsfallRequestStatuses);
        }
    }

    private boolean isVaActionPermitted(List<WbciRequestStatus> geschaeftsfallRequestStatuses) {
        WbciRequestStatus currentVaStatus = getCurrentVaRequestStatus(geschaeftsfallRequestStatuses);

        // check if the status change is legal
        if (this.newRequestStatus != null && !currentVaStatus.isLegalStatusChange(this.newRequestStatus)) {
            return false;
        }

        // check no active TV or STORNO request exists for the geschaeftsfall
        return !isActiveStornoOrTvRequestFound(geschaeftsfallRequestStatuses);

    }

    private boolean isGfActionPermitted(List<WbciRequestStatus> geschaeftsfallRequestStatuses,
            WbciGeschaeftsfallStatus geschaeftsfallStatus) {
        if (!geschaeftsfallStatus.isLegalStatusChange(newGeschaeftsfallStatus)) {
            return false;
        }

        if (CLOSE_GF.equals(this)) {
            WbciRequestStatus currentVaStatus = getCurrentVaRequestStatus(geschaeftsfallRequestStatuses);
            if (ABBM_EMPFANGEN.equals(currentVaStatus) || ABBM_VERSENDET.equals(currentVaStatus)) {
                return true;
            }

            List<WbciRequestStatus> stornoStatuses = getCurrentStornoRequestStatuses(geschaeftsfallRequestStatuses);
            for (WbciRequestStatus stornoStatus : stornoStatuses) {
                if (STORNO_ERLM_EMPFANGEN.equals(stornoStatus) || STORNO_ERLM_VERSENDET.equals(stornoStatus)) {
                    return true;
                }
            }
            return false;
        }

        return true;
    }

    /**
     * Siehe HUR-23700 bzw. HUR-23278
     */
    private boolean isCreateStornoErlmActionPermitted(List<MeldungTyp> vaMeldungTypen, WbciRequest wbciRequest) {
        if (wbciRequest != null
                && vaMeldungTypen != null
                && wbciRequest instanceof StornoAenderungAufAnfrage
                && !vaMeldungTypen.contains(MeldungTyp.RUEM_VA)) {
            return false;
        }
        return true; // permission is granted if it's not possible to determine if this is a special case
    }

    private boolean isNonVaActionPermitted(List<WbciRequestStatus> geschaeftsfallRequestStatuses) {
        if (createsNewRequest) {
            // perform checks for actions that lead to the creation of a new request...

            // check that there are no other active requests
            if (isActiveStornoOrTvRequestFound(geschaeftsfallRequestStatuses)) {
                return false;
            }

            // check that there are no finished stornos on the GF
            if (isStornoErlmFound(geschaeftsfallRequestStatuses)) {
                return false;
            }
        }
        else {
            // check if the status change is legal
            WbciRequestStatus currentStatus;

            if (isTvAction()) {
                currentStatus = getActiveTvStatus(geschaeftsfallRequestStatuses);
            }
            else {
                currentStatus = getActiveStornoStatus(geschaeftsfallRequestStatuses);
            }

            if (currentStatus == null || !currentStatus.isLegalStatusChange(this.newRequestStatus)) {
                return false;
            }
        }

        return true;
    }

    public boolean isVaAction() {
        return WbciAction.getVaActions().contains(this);
    }

    public boolean isTvAction() {
        return WbciAction.getTvActions().contains(this);
    }

    public boolean isStornoAction() {
        return WbciAction.getStornoActions().contains(this);
    }

    public boolean isCreateStornoAction() {
        return WbciAction.getCreateStornoActions().contains(this);
    }

    public boolean isCreateStornoErlmAction() {
        return WbciAction.getCreateStornoErlmActions().contains(this);
    }
    public boolean isGfAction() {
        return WbciAction.getGfActions().contains(this);
    }
}
