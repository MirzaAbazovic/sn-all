/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.12.2014
 */
package de.mnet.hurrican.acceptance.actions.scanview;

import java.util.*;
import com.consol.citrus.context.TestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import de.augustakom.hurrican.model.exmodules.archive.ArchiveDocumentDto;
import de.augustakom.hurrican.model.exmodules.archive.ArchiveDocumentType;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.exmodules.archive.ArchiveService;
import de.mnet.hurrican.ffm.citrus.VariableNames;

/**
 *
 */
public class CreateSearchDocumentsRequestAction extends AbstractArchiveServiceAction {

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(CreateSearchDocumentsRequestAction.class);

    private final String vertragsnummer;
    private final ArchiveDocumentType archiveDocumentType;

    public CreateSearchDocumentsRequestAction(ArchiveService archiveService, String vertragsnummer, ArchiveDocumentType archiveDocumentType) {
        super(archiveService);
        setName("CreateSearchDocumentsRequest");
        this.vertragsnummer = vertragsnummer;
        this.archiveDocumentType = archiveDocumentType;
    }

    @Override
    public void doExecute(final TestContext context) {
        new SimpleAsyncTaskExecutor().execute(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            List<ArchiveDocumentDto> archiveDocumentDtos =
                                    archiveService.retrieveDocuments(vertragsnummer, archiveDocumentType, "AIL_HURRICAN");
                            context.setVariable(VariableNames.ARCHIVE_DOCUMENTS, archiveDocumentDtos);
                            context.setVariable(VariableNames.ARCHIVE_SERVICE_ERROR_OCCURRED, Boolean.FALSE);
                        }
                        catch (FindException e) {
                            LOG.error("Error while searching documents with archive service ", e);
                            context.setVariable(VariableNames.ARCHIVE_SERVICE_ERROR_OCCURRED, Boolean.TRUE);
                            context.setVariable(VariableNames.ARCHIVE_SERVICE_ERROR_TEXT, e.getMessage());
                        }
                    }
                }
        );
    }

}
