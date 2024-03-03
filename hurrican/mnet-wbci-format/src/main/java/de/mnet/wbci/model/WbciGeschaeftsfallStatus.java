/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.08.13
 */
package de.mnet.wbci.model;

import java.util.*;

/**
 * Definiert alle Geschaeftsfall Zustaende und beim Zustaendswechsel die naechsten erlaubten Zustaende.
 */
public enum WbciGeschaeftsfallStatus {
    ACTIVE("in Bearbeitung"),                   // a task has to be performed on the GF
    PASSIVE("Bearbeitung abgeschl."),           // no active task has to be performed
    NEW_VA("neue VA erwartet"),                 // a new VA must be created - set when the GF is cancelled with STR-AEN
    NEW_VA_EXPIRED("VA aufgehoben"),            // the new VA was not created within the permitted time frame
    COMPLETE("Vorgang abgeschl.");              // the VA is complete - either through STRORNO, ABBM or successfull completion

    private final String description;

    private static Map<WbciGeschaeftsfallStatus, List<WbciGeschaeftsfallStatus>> legalStatusChanges;

    static {
        legalStatusChanges = new HashMap<>();
        legalStatusChanges.put(ACTIVE, Arrays.asList(ACTIVE, PASSIVE, NEW_VA, COMPLETE));
        legalStatusChanges.put(PASSIVE, Arrays.asList(PASSIVE, ACTIVE, NEW_VA, COMPLETE));
        legalStatusChanges.put(NEW_VA, Arrays.asList(NEW_VA, NEW_VA_EXPIRED, COMPLETE));
        legalStatusChanges.put(NEW_VA_EXPIRED, Arrays.asList(NEW_VA_EXPIRED, COMPLETE));
        legalStatusChanges.put(COMPLETE, Arrays.asList(COMPLETE));
    }

    private WbciGeschaeftsfallStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public List<WbciGeschaeftsfallStatus> getNextLegalStatusChanges() {
        return new ArrayList<>(legalStatusChanges.get(this));
    }

    /**
     * If the status change is legal with respect to the current geschaeftsfall status, then true is returned.
     *
     * @param nextGeschaeftsfallStatus
     * @return
     */
    public boolean isLegalStatusChange(WbciGeschaeftsfallStatus nextGeschaeftsfallStatus) {
        return getNextLegalStatusChanges().contains(nextGeschaeftsfallStatus);
    }

    /**
     * Returns the list of statuses that are deemed active.
     *
     * @return
     */
    public static List<WbciGeschaeftsfallStatus> getActiveStatuses() {
        return Arrays.asList(PASSIVE, ACTIVE);
    }

    public static List<WbciGeschaeftsfallStatus> getActiveAndCompleteStatuses() {
        return Arrays.asList(PASSIVE, ACTIVE, COMPLETE);
    }

    /**
     * Returns the list of statuses that are deemed non-active.
     *
     * @return
     */
    public static List<WbciGeschaeftsfallStatus> getNonActiveStatuses() {
        return Arrays.asList(COMPLETE, NEW_VA);
    }

}
