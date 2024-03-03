package de.mnet.hurrican.acceptance.actions.scanview;

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
public class CreateGetDocumentRequestAction extends AbstractArchiveServiceAction {

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(CreateGetDocumentRequestAction.class);

    private final String vertragsnummer;
    private final ArchiveDocumentType archiveDocumentType;
    private final String documentId;

    public CreateGetDocumentRequestAction(ArchiveService archiveService, String vertragsnummer,
            ArchiveDocumentType archiveDocumentType, String documentId) {
        super(archiveService);
        setName("CreateGetDocumentRequest");
        this.vertragsnummer = vertragsnummer;
        this.archiveDocumentType = archiveDocumentType;
        this.documentId = documentId;
    }

    @Override
    public void doExecute(final TestContext context) {
        new SimpleAsyncTaskExecutor().execute(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ArchiveDocumentDto archiveDocumentDto =
                                    archiveService.retrieveDocument(vertragsnummer, archiveDocumentType, documentId, "AIL_HURRICAN");
                            context.setVariable(VariableNames.ARCHIVE_DOCUMENT, archiveDocumentDto);
                            context.setVariable(VariableNames.ARCHIVE_SERVICE_ERROR_OCCURRED, Boolean.FALSE);
                        }
                        catch (FindException e) {
                            LOG.error("Error while retrieving document from archive service ", e);
                            context.setVariable(VariableNames.ARCHIVE_SERVICE_ERROR_OCCURRED, Boolean.TRUE);
                            context.setVariable(VariableNames.ARCHIVE_SERVICE_ERROR_TEXT, e.getMessage());
                        }
                    }
                }
        );
    }

}
