/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.12.2005 11:50:31
 */
package de.mnet.hurrican.scheduler.job.imports;

import de.mnet.hurrican.scheduler.job.base.AKAbstractQuartzJob;

/**
 * Abstrakte Klasse fuer alle Import-Jobs.
 *
 *
 */
public abstract class AbstractImportJob extends AKAbstractQuartzJob {

    /**
     * Key fuer die JobDatamap, um den Namen von einem Unterverzeichnis zu definieren, in dem die verarbeiteten Dateien
     * abgelegt werden.
     */
    protected static final String SUB_DIR_PROCESSED_FILES = "sub.dir.processed.files";

}
