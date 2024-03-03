/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.09.2011 15:11:30
 */
package de.augustakom.hurrican.service.exmodules.archive.impl;

import static de.augustakom.hurrican.service.exmodules.archive.impl.ArchiveDocumentKey.*;

import java.math.*;
import java.time.format.*;
import java.util.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.builder.cdm.archive.v1.ArchiveDocumentBulder;
import de.augustakom.hurrican.model.builder.cdm.archive.v1.ArchiveDocumentDocumentBuilder;
import de.augustakom.hurrican.model.builder.cdm.archive.v1.GetDocumentBuilder;
import de.augustakom.hurrican.model.builder.cdm.archive.v1.SearchCriteriaBuilder;
import de.augustakom.hurrican.model.builder.cdm.archive.v1.SearchDocumentsBuilder;
import de.augustakom.hurrican.model.exmodules.archive.ArchiveDocumentDto;
import de.augustakom.hurrican.model.exmodules.archive.ArchiveDocumentType;
import de.augustakom.hurrican.model.exmodules.archive.ArchiveMetaData;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.exmodules.DefaultExModuleService;
import de.augustakom.hurrican.service.exmodules.archive.ArchiveService;
import de.mnet.esb.cdm.customer.documentarchiveservice.v1.ArchiveDocument;
import de.mnet.esb.cdm.customer.documentarchiveservice.v1.DocumentArchiveService;
import de.mnet.esb.cdm.customer.documentarchiveservice.v1.ESBFault;
import de.mnet.esb.cdm.customer.documentarchiveservice.v1.GetDocumentResponse;
import de.mnet.esb.cdm.customer.documentarchiveservice.v1.Item;
import de.mnet.esb.cdm.customer.documentarchiveservice.v1.SearchDocumentsResponse;

/**
 * Standard-Implementierung des Archiv-Services.
 */
@CcTxRequired
@Component("de.augustakom.hurrican.service.exmodules.archive.ArchiveService")
public class ArchiveServiceImpl extends DefaultExModuleService implements ArchiveService {

    private static final Logger LOGGER = Logger.getLogger(ArchiveServiceImpl.class);

    private static final String SEARCH_KEY_WILDCARD = "*";

    @Autowired
    @Qualifier("documentArchiveService")
    private DocumentArchiveService documentArchiveService;

    @Autowired
    @Qualifier("documentArchiveServiceTransacted")
    /**
     * Don't use the transacted service for synchronous communication, use instead ArchiveServiceImpl#documentArchiveService
     */
    private DocumentArchiveService documentArchiveServiceTransacted;

    @Value("${scanview.login.name}")
    private String scanviewLoginName;

    @Value("${scanview.max.hits}")
    private String maxHits;

    @Override
    public List<ArchiveDocumentDto> retrieveDocuments(String vertragsnummer, ArchiveDocumentType documentType,
            String archiveLoginName) throws FindException {
        Preconditions.checkNotNull(vertragsnummer, "Die Vertragsnummer ist nicht angegeben!");
        Preconditions.checkNotNull(documentType, "Der gesuchte Dokumententyp ist nicht angegeben!");

        List<SearchDocumentsResponse.Document> documents = searchDocuments(archiveLoginName,
                ArchiveDocumentClass.DOC_CLASS_TOP,
                new ImmutableMap.Builder<ArchiveDocumentKey, String>()
                        .put(VERTRAG, vertragsnummer)
                        .put(DOCUMENT_TYPE, SEARCH_KEY_WILDCARD + documentType.getDocumentTypeName() + SEARCH_KEY_WILDCARD)
                        .build()
        );
        return createArchiveDocuments(documents, vertragsnummer, documentType);
    }

    private BigInteger getMaxHits() {
        Long maxHitsAsLong = Long.getLong(maxHits);
        return BigInteger.valueOf(maxHitsAsLong != null ? maxHitsAsLong : 50L);
    }

    @Override
    public ArchiveDocumentDto retrieveDocument(String vertragsnummer, ArchiveDocumentType documentType, String documentId, String archiveLoginName) throws FindException {
        try {
            Preconditions.checkNotNull(documentId, "Die ID des gesuchten Dokumentes ist nicht angegeben!");

            GetDocumentResponse documentResponse = documentArchiveService.getDocument(
                    new GetDocumentBuilder()
                            .withEndUserId(StringUtils.isEmpty(archiveLoginName) ? scanviewLoginName : archiveLoginName)
                            .withDocumentId(documentId)
                            .build()
            );
            return createArchiveDocument(vertragsnummer, documentType, documentResponse.getDocument());
        }
        catch (ESBFault esbFault) {
            LOGGER.error(esbFault.getFaultInfo().getErrorCode() + ": " + esbFault.getFaultInfo().getErrorMessage(), esbFault.getCause());
            throw new FindException(String.format("Beim Zugriff auf dem Archivsystem ist folgende "
                    + "Fehlermeldung aufgetreten: '%s'", esbFault.getFaultInfo().getErrorMessage()), esbFault.getCause());
        }
    }

    @Override
    public String archiveDocument(byte[] document, ArchiveMetaData metaData) {
        String uuid = UUID.randomUUID().toString();
        ArchiveDocument archiveDocument =
                new ArchiveDocumentBulder()
                        .withEndUserId(scanviewLoginName)
                        .withDocument(
                                new ArchiveDocumentDocumentBuilder()
                                        .withDocumentClass(ArchiveDocumentClass.DOC_CLASS_AUFTRAG_UNTERLAGEN)
                                        .addItem(metaData.getDateiname(), document)
                                        .build()
                        )
                        .addArchiveKey(DOCUMENT_TYPE, metaData.getDocumentType().getDocumentTypeName())
                        .addArchiveKey(VERTRAG, metaData.getSapAuftragsnummer())
                        .addArchiveKey(CUSTOMER_NR, metaData.getKundennr().toString())
                        .addArchiveKey(DEBITOR_NR, metaData.getDebitornr())
                        .addArchiveKey(TAIFUN_ORDER_NR, metaData.getTaifunAuftragsnr().toString())
                        .addArchiveKey(DOCUMENT_DATE, metaData.getDokumentenDatum().format(DateTimeFormatter.ofPattern("dd.MM.YYYY")))
                        .addArchiveKey(UUID_KEY, uuid)
                        .build();
        documentArchiveServiceTransacted.archiveDocumentAsync(archiveDocument);
        return uuid;
    }

    private List<ArchiveDocumentDto> createArchiveDocuments(List<SearchDocumentsResponse.Document> documents,
            String vertragsnummer, ArchiveDocumentType documentType) {
        List<ArchiveDocumentDto> archiveDocumentDtos = new ArrayList<>();
        for (SearchDocumentsResponse.Document document : documents) {
            archiveDocumentDtos.add(createArchiveDocument(vertragsnummer, documentType, document));
        }
        return archiveDocumentDtos;
    }

    private ArchiveDocumentDto createArchiveDocument(String vertragsnummer, ArchiveDocumentType documentType,
            SearchDocumentsResponse.Document document) {
        Item item = document.getItem().get(0);
        ArchiveDocumentDto archiveDocumentDto = new ArchiveDocumentDto();
        archiveDocumentDto.setKey(document.getId());
        archiveDocumentDto.setLabel(item.getName());
        archiveDocumentDto.setFileExtension(item.getExtension().toLowerCase());
        archiveDocumentDto.setVertragsNr(vertragsnummer);
        archiveDocumentDto.setDocumentType(documentType);
        return archiveDocumentDto;
    }

    private ArchiveDocumentDto createArchiveDocument(String vertragsnummer, ArchiveDocumentType documentType,
            GetDocumentResponse.Document document) {
        Item item = document.getItem().get(0);
        ArchiveDocumentDto archiveDocumentDto = new ArchiveDocumentDto();
        archiveDocumentDto.setKey(document.getId());
        archiveDocumentDto.setLabel(item.getName());
        archiveDocumentDto.setStream(item.getData());
        archiveDocumentDto.setFileExtension(item.getExtension().toLowerCase());
        archiveDocumentDto.setVertragsNr(vertragsnummer);
        archiveDocumentDto.setDocumentType(documentType);
        return archiveDocumentDto;
    }

    @Override
    public ArchiveDocumentDto retrieveDocumentByUuid(String uuid, String archiveLoginName) throws FindException {
        Preconditions.checkNotNull(uuid, "Die UUID ist nicht angegeben!");

        List<SearchDocumentsResponse.Document> documents = searchDocuments(archiveLoginName,
                ArchiveDocumentClass.DOC_CLASS_AUFTRAG_UNTERLAGEN,
                new ImmutableMap.Builder<ArchiveDocumentKey, String>()
                        .put(UUID_KEY, uuid)
                        .build()
        );
        if (CollectionUtils.isEmpty(documents)) {
            throw new FindException(String.format("Fuer die angegebene uuid '%s' konnte kein Dokument im " +
                    "Archivsystem gefunden werden.", uuid));
        }
        else if (documents.size() > 1) {
            throw new FindException(String.format("Fuer die angegebene uuid '%s' wurden '%s' Dokumente im " +
                    "Archivsystem gefunden.", uuid, documents.size()));
        }
        return retrieveDocument(null, null, documents.get(0).getId(), archiveLoginName);
    }

    protected List<SearchDocumentsResponse.Document> searchDocuments(String archiveLoginName, ArchiveDocumentClass docClass,
            Map<ArchiveDocumentKey, String> searchKeys) throws FindException {
        try {
            SearchDocumentsResponse searchDocumentsResponse = documentArchiveService.searchDocuments(
                    new SearchDocumentsBuilder()
                            .withEndUserId(archiveLoginName)
                            .withDocClass(docClass)
                            .withSearchCriteria(
                                    new SearchCriteriaBuilder()
                                            .withMaxHits(getMaxHits())
                                            .withOperator(SearchCriteriaBuilder.Operator.AND)
                                            .addKeys(searchKeys)
                                            .build()
                            )
                            .build()
            );
            return searchDocumentsResponse.getDocument();
        }
        catch (ESBFault esbFault) {
            LOGGER.error(esbFault.getFaultInfo().getErrorCode() + ": " + esbFault.getFaultInfo().getErrorMessage(), esbFault.getCause());
            throw new FindException(String.format("Beim Zugriff auf dem Archivsystem ist folgende "
                    + "Fehlermeldung aufgetreten: '%s'", esbFault.getFaultInfo().getErrorMessage()), esbFault.getCause());
        }
    }

}
