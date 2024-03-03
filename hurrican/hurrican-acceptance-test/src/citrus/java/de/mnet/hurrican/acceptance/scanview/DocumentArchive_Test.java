/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.12.2014
 */
package de.mnet.hurrican.acceptance.scanview;

import static org.testng.Assert.*;

import java.util.*;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.exmodules.archive.ArchiveDocumentDto;
import de.augustakom.hurrican.model.exmodules.archive.ArchiveDocumentType;
import de.mnet.hurrican.acceptance.common.SimulatorUseCase;
import de.mnet.hurrican.ffm.citrus.VariableNames;
import de.mnet.hurrican.ffm.citrus.actions.AbstractFFMAction;

/**
 *
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class DocumentArchive_Test extends AbstractDocumentArchiveTestBuilder {

    @CitrusTest
    public void DocumentArchive_01_Test() {
        simulatorUseCase(SimulatorUseCase.DocumentArchive_01);

        final String vertragsnummer = "1153101";
        final ArchiveDocumentType archiveDocumentType = ArchiveDocumentType.AUFTRAG;

        hurrican().createAndSendArchiveDocumentRequest(vertragsnummer, archiveDocumentType);

        variable(VariableNames.VERTRAGSNUMMER, vertragsnummer);
        variable(VariableNames.ARCHIVE_DOCUMENT_TYPE, archiveDocumentType.getDocumentTypeName());

        atlas().receiveArchiveDocumentRequest("archiveDocumentRequest");
    }

    @CitrusTest
    public void DocumentArchive_02_Test() {
        simulatorUseCase(SimulatorUseCase.DocumentArchive_02);

        final String vertragsnummer = "1153101";
        final ArchiveDocumentType archiveDocumentType = ArchiveDocumentType.AUFTRAG;
        final String documentId = "123456789";

        hurrican().createAndSendGetDocumentRequest(vertragsnummer, archiveDocumentType, documentId);

        variable(VariableNames.VERTRAGSNUMMER, vertragsnummer);
        variable(VariableNames.ARCHIVE_DOCUMENT_TYPE, archiveDocumentType.getDocumentTypeName());
        variable(VariableNames.DOCUMENT_ID, documentId);

        atlas().receiveGetDocumentRequest("getDocumentRequest");
        atlas().sendGetDocumentResponse("getDocumentResponse");

        repeatOnError(
                new AbstractFFMAction("Validate result of getDocument-Service") {
                    @Override
                    public void doExecute(TestContext context) {
                        Boolean errorOccured = (Boolean) context.getVariableObject(VariableNames.ARCHIVE_SERVICE_ERROR_OCCURRED);
                        assertFalse(errorOccured);
                        ArchiveDocumentDto archiveDocument =
                                (ArchiveDocumentDto) context.getVariableObject(VariableNames.ARCHIVE_DOCUMENT);
                        assertNotNull(archiveDocument);
                        assertNotNull(archiveDocument.getStream());
                        assertEquals(archiveDocument.getVertragsNr(), vertragsnummer);
                        assertEquals(archiveDocument.getDocumentType(), archiveDocumentType);
                        assertNotNull(archiveDocument.getLabel());
                        assertEquals(archiveDocument.getKey(), documentId);
                    }
                })
                .until("i gt 15");
    }

    @CitrusTest
    public void DocumentArchive_03_Test() {
        simulatorUseCase(SimulatorUseCase.DocumentArchive_03);

        final String vertragsnummer = "1153101";
        final ArchiveDocumentType archiveDocumentType = ArchiveDocumentType.AUFTRAG;

        hurrican().createAndSendSearchDocumentsRequest(vertragsnummer, archiveDocumentType);

        variable(VariableNames.VERTRAGSNUMMER, vertragsnummer);
        variable(VariableNames.ARCHIVE_DOCUMENT_TYPE, archiveDocumentType.getDocumentTypeName());

        atlas().receiveSearchDocumentsRequest("searchDocumentsRequest");
        atlas().sendSearchDocumentsResponse("searchDocumentsResponse");

        repeatOnError(
                new AbstractFFMAction("Validate result of searchDocuments-Service") {
                    @Override
                    public void doExecute(TestContext context) {
                        Boolean errorOccured = (Boolean) context.getVariableObject(VariableNames.ARCHIVE_SERVICE_ERROR_OCCURRED);
                        assertFalse(errorOccured);
                        List<ArchiveDocumentDto> archiveDocuments =
                                (List<ArchiveDocumentDto>) context.getVariableObject(VariableNames.ARCHIVE_DOCUMENTS);
                        assertNotNull(archiveDocuments);
                        assertEquals(archiveDocuments.size(), 2);
                        for (ArchiveDocumentDto archiveDocument : archiveDocuments) {
                            assertNull(archiveDocument.getStream());
                            assertEquals(archiveDocument.getVertragsNr(), vertragsnummer);
                            assertEquals(archiveDocument.getDocumentType(), archiveDocumentType);
                            assertNotNull(archiveDocument.getLabel());
                            assertNotNull(archiveDocument.getKey());
                        }
                    }
                })
                .until("i gt 15");
    }

    /**
     * Tests sending an ESBFault from AtlasESB on calling the DocumentArchiveService
     */
    @CitrusTest
    public void DocumentArchive_04_Test() {
        simulatorUseCase(SimulatorUseCase.DocumentArchive_04);

        final String vertragsnummer = "1153101";
        final ArchiveDocumentType archiveDocumentType = ArchiveDocumentType.AUFTRAG;

        hurrican().createAndSendSearchDocumentsRequest(vertragsnummer, archiveDocumentType);

        variable(VariableNames.VERTRAGSNUMMER, vertragsnummer);
        variable(VariableNames.ARCHIVE_DOCUMENT_TYPE, archiveDocumentType.getDocumentTypeName());

        atlas().receiveSearchDocumentsRequest("searchDocumentsRequest");
        atlas().sendSearchDocumentsResponse("searchDocumentsResponse");

        final String expectedErrorText = "Falscher Benutzername oder Passwort. Bitte versuchen Sie es noch einmal.";
        repeatOnError(
                new AbstractFFMAction("Validate result of searchDocuments-Service") {
                    @Override
                    public void doExecute(TestContext context) {
                        Boolean errorOccured = (Boolean) context.getVariableObject(VariableNames.ARCHIVE_SERVICE_ERROR_OCCURRED);
                        assertTrue(errorOccured);
                        String errorText = context.getVariable(VariableNames.ARCHIVE_SERVICE_ERROR_TEXT);
                        assertNotNull(errorText);
                        assertTrue(errorText.contains(expectedErrorText));
                    }
                })
                .until("i gt 15");
    }

}
