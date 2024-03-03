/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.12.2014
 */
package de.mnet.hurrican.acceptance.actions.scanview;

import com.consol.citrus.actions.AbstractTestAction;

import de.augustakom.hurrican.service.exmodules.archive.ArchiveService;

/**
 *
 */
public abstract class AbstractArchiveServiceAction extends AbstractTestAction {

    protected final ArchiveService archiveService;

    public AbstractArchiveServiceAction(ArchiveService archiveService) {
        this.archiveService = archiveService;
    }

}
