/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.12.2014
 */
package de.mnet.hurrican.acceptance.actions.scanview;

import static de.augustakom.hurrican.model.exmodules.archive.ArchiveMetaData.ArchiveFileExtension.*;

import java.time.*;
import com.consol.citrus.context.TestContext;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import de.augustakom.hurrican.model.exmodules.archive.ArchiveDocumentType;
import de.augustakom.hurrican.model.exmodules.archive.ArchiveMetaData;
import de.augustakom.hurrican.service.exmodules.archive.ArchiveService;

/**
 *
 */
public class CreateArchiveDocumentRequestAction extends AbstractArchiveServiceAction {

    private final String vertragsnummer;
    private final ArchiveDocumentType archiveDocumentType;

    public CreateArchiveDocumentRequestAction(ArchiveService archiveService, String vertragsnummer, ArchiveDocumentType archiveDocumentType) {
        super(archiveService);
        setName("CreateArchiveDocumentRequest");
        this.vertragsnummer = vertragsnummer;
        this.archiveDocumentType = archiveDocumentType;
    }

    @Override
    public void doExecute(TestContext context) {
        final ArchiveMetaData metaData = new ArchiveMetaData();
        metaData.setUseCaseId("Wita");
        metaData.setDocumentType(archiveDocumentType);
        metaData.setSapAuftragsnummer(vertragsnummer);
        metaData.setKundennr(500331323L);
        metaData.setTaifunAuftragsnr(1679340L);
        metaData.setDebitornr("M500596221");
        metaData.setDokumentenDatum(LocalDateTime.now());
        metaData.setFileExtension(PDF);
        metaData.setDateiname("kundenauftrag.pdf");
        new SimpleAsyncTaskExecutor().execute(
                new Runnable() {
                    @Override
                    public void run() {
                        archiveService.archiveDocument(new byte[10], metaData);
                    }
                }
        );
    }

}
