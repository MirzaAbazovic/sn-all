/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.08.2009 13:33:01
 */
package de.mnet.migration.common.result;

/**
 * Stati fuer die Migration eines Datensatzes, auch fuer Logging in die Datenbank genutzt. Stati, die 'schlimmer' sind,
 * sollen weiter hinten in der Liste stehen, damit die 'schlimmheit' programmatisch einfach zu bestimmen ist.
 */
public enum TransformationStatus {
    /**
     * Everything went well
     */
    OK(true, true),
    /**
     * Dataset migrated, with some information messages.
     */
    INFO(true, true),
    /**
     * Dataset migrated, but maybe there was something wrong and it is not complete/correct
     */
    WARNING(true, true),
    /**
     * Dataset skipped (maybe because it was already migrated/present in target database)
     */
    SKIPPED(false, true),
    /**
     * Dataset was NOT migrated, because data is horribly wrong. Has NO target IDs
     */
    BAD_DATA(false, false),
    /**
     * There was an error in the migration code while migrating the dataset. Has NO target IDs
     */
    ERROR(false, false),
    /**
     * Uninitialized TransformationResult
     */
    UNKNOWN(false, false);

    private final boolean bCommitAllowed;
    private final boolean bHasTargetIds;

    private TransformationStatus(boolean commitAllowed, boolean hasTargetIds) {
        this.bCommitAllowed = commitAllowed;
        this.bHasTargetIds = hasTargetIds;

    }

    public boolean commitAllowed() {
        return bCommitAllowed;
    }

    public boolean hasTargetIds() {
        return bHasTargetIds;
    }

    /**
     * @return the worse status comparing this and the supplied status
     */
    public TransformationStatus worse(TransformationStatus status) {
        if (this.ordinal() > status.ordinal()) {
            return this;
        }
        return status;
    }
}
