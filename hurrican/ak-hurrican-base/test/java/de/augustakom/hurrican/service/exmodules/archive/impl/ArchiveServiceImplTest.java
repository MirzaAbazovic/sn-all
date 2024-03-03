package de.augustakom.hurrican.service.exmodules.archive.impl;

import static de.augustakom.hurrican.service.exmodules.archive.impl.ArchiveDocumentKey.*;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.isNull;
import static org.testng.Assert.*;

import java.math.*;
import java.time.*;
import java.util.*;
import com.google.common.collect.ImmutableMap;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.builder.cdm.archive.v1.GetDocumentResponseBuilder;
import de.augustakom.hurrican.model.builder.cdm.archive.v1.GetDocumentResponseDocumentBuilder;
import de.augustakom.hurrican.model.builder.cdm.archive.v1.ItemBuilder;
import de.augustakom.hurrican.model.builder.cdm.archive.v1.SearchCriteriaBuilder;
import de.augustakom.hurrican.model.builder.cdm.archive.v1.SearchDocumentsResponseBuilder;
import de.augustakom.hurrican.model.builder.cdm.archive.v1.SearchDocumentsResponseDocumentBuilder;
import de.augustakom.hurrican.model.exmodules.archive.ArchiveDocumentDto;
import de.augustakom.hurrican.model.exmodules.archive.ArchiveDocumentType;
import de.augustakom.hurrican.model.exmodules.archive.ArchiveMetaData;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.mnet.esb.cdm.customer.documentarchiveservice.v1.ArchiveDocument;
import de.mnet.esb.cdm.customer.documentarchiveservice.v1.DocumentArchiveService;
import de.mnet.esb.cdm.customer.documentarchiveservice.v1.ESBFault;
import de.mnet.esb.cdm.customer.documentarchiveservice.v1.GetDocument;
import de.mnet.esb.cdm.customer.documentarchiveservice.v1.GetDocumentResponse;
import de.mnet.esb.cdm.customer.documentarchiveservice.v1.SearchDocuments;
import de.mnet.esb.cdm.customer.documentarchiveservice.v1.SearchDocumentsResponse;

@Test(groups = BaseTest.UNIT)
public class ArchiveServiceImplTest extends BaseTest {

    @InjectMocks
    @Spy
    private ArchiveServiceImpl cut;
    @Mock
    private DocumentArchiveService documentArchiveService;
    @Mock
    private DocumentArchiveService documentArchiveServiceTransacted;

    private String scanviewLoginName = "HURRICAN_TEST";
    private String maxHits = "50";

    @BeforeMethod
    public void setUp() throws FindException {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(cut, "scanviewLoginName", scanviewLoginName);
        ReflectionTestUtils.setField(cut, "maxHits", maxHits);
    }

    @Test
    public void retrieveDocuments() throws FindException {
        final String vertragsnummer = "1234";
        String documentId = "123456";
        final ArchiveDocumentType documentType = ArchiveDocumentType.CUDA_KUENDIGUNG;
        String documentName = "name";
        String extension = "PDF";
        List<SearchDocumentsResponse.Document> documents = Arrays.asList(
                new SearchDocumentsResponseDocumentBuilder()
                        .withId(documentId)
                        .withDocumentClass(ArchiveDocumentClass.DOC_CLASS_TOP)
                        .addKey(VERTRAG, vertragsnummer, "type")
                        .addItem(
                                new ItemBuilder()
                                        .withId("12345")
                                        .withCreationDate(LocalDateTime.now())
                                        .withName(documentName)
                                        .withExtension(extension)
                                        .build()
                        )
                        .build()
        );
        doReturn(documents)
                .when(cut).searchDocuments(eq(scanviewLoginName), eq(ArchiveDocumentClass.DOC_CLASS_TOP),
                argThat(new ArgumentMatcher<Map<ArchiveDocumentKey, String>>() {
                    @Override
                    public boolean matches(Object o) {
                        final Map<ArchiveDocumentKey, String> map = (Map<ArchiveDocumentKey, String>) o;
                        assertTrue(map.containsKey(VERTRAG));
                        assertEquals(map.get(VERTRAG), vertragsnummer);
                        assertTrue(map.containsKey(DOCUMENT_TYPE));
                        assertEquals(map.get(DOCUMENT_TYPE), "*" + documentType.getDocumentTypeName() + "*");
                        return true;
                    }
                })
        );
        List<ArchiveDocumentDto> archiveDocumentDtos =
                cut.retrieveDocuments(vertragsnummer, documentType, scanviewLoginName);
        assertNotEmpty(archiveDocumentDtos);
        assertEquals(archiveDocumentDtos.size(), 1);
        ArchiveDocumentDto archiveDocumentDto = archiveDocumentDtos.get(0);
        assertEquals(archiveDocumentDto.getFileExtension(), extension.toLowerCase());
        assertEquals(archiveDocumentDto.getKey(), documentId);
        assertEquals(archiveDocumentDto.getDocumentType(), documentType);
        assertEquals(archiveDocumentDto.getLabel(), documentName);
        assertEquals(archiveDocumentDto.getVertragsNr(), vertragsnummer);
        assertNull(archiveDocumentDto.getStream());
        assertNull(archiveDocumentDto.getMimeType());
    }

    @DataProvider
    public Object[][] retrieveDocumentsByUUID_DataProvider() {
        return new Object[][] {
                { Arrays.asList(
                        new SearchDocumentsResponseDocumentBuilder()
                                .withId("123456")
                                .build()
                ), false},
                { Arrays.asList(
                        new SearchDocumentsResponseDocumentBuilder()
                                .withId("123456")
                                .build(),
                        new SearchDocumentsResponseDocumentBuilder()
                                .withId("123457")
                                .build()
                ), true},
                { Collections.EMPTY_LIST, true},
        };
    }

    @Test(dataProvider = "retrieveDocumentsByUUID_DataProvider")
    public void retrieveDocumentsByUUID(List<SearchDocumentsResponse.Document> documents, boolean expectedException) throws FindException {
        final String uuid = UUID.randomUUID().toString();
        doReturn(documents)
                .when(cut).searchDocuments(eq(scanviewLoginName), eq(ArchiveDocumentClass.DOC_CLASS_AUFTRAG_UNTERLAGEN),
                argThat(new ArgumentMatcher<Map<ArchiveDocumentKey, String>>() {
                    @Override
                    public boolean matches(Object o) {
                        final Map<ArchiveDocumentKey, String> map = (Map<ArchiveDocumentKey, String>) o;
                        assertTrue(map.containsKey(UUID_KEY));
                        assertEquals(map.get(UUID_KEY), uuid);
                        return true;
                    }
                })
        );
        ArchiveDocumentDto archiveDocumentDto = mock(ArchiveDocumentDto.class);
        doReturn(archiveDocumentDto).when(cut).retrieveDocument((String) isNull(), (ArchiveDocumentType) isNull(), eq("123456"), eq(scanviewLoginName));
        try {
            assertEquals(cut.retrieveDocumentByUuid(uuid, scanviewLoginName), archiveDocumentDto);
            assertFalse(expectedException);
            verify(cut).retrieveDocument((String)isNull(), (ArchiveDocumentType)isNull(), eq("123456"), eq(scanviewLoginName));
        }
        catch (FindException e) {
            assertTrue(expectedException);
        }
    }

    @DataProvider
    private Object[][] retrieveDocumentDataProvider() {
        return new Object[][] {
                {scanviewLoginName},
                {null},
                {""},
        };
    }

    @Test(dataProvider = "retrieveDocumentDataProvider")
    public void retrieveDocument(String scanviewLoginName) throws FindException, ESBFault {
        final String vertragsnummer = "1234";
        final String documentId = "123456";
        final ArchiveDocumentType documentType = ArchiveDocumentType.CUDA_KUENDIGUNG;
        String documentName = "name";
        String extension = "PDF";
        GetDocumentResponse getDocumentResponse =
                new GetDocumentResponseBuilder()
                        .withDocument(
                                new GetDocumentResponseDocumentBuilder()
                                        .withId(documentId)
                                        .withDocumentClass(ArchiveDocumentClass.DOC_CLASS_AUFTRAG_UNTERLAGEN)
                                        .addKey(VERTRAG, vertragsnummer, "type")
                                        .addItem(
                                                new ItemBuilder()
                                                        .withId("12345")
                                                        .withCreationDate(LocalDateTime.now())
                                                        .withName(documentName)
                                                        .withExtension(extension)
                                                        .build()
                                        )
                                        .build()
                        )
                        .build();
        when(documentArchiveService.getDocument(argThat(new ArgumentMatcher<GetDocument>() {
            @Override
            public boolean matches(Object o) {
                final GetDocument getDocument = (GetDocument) o;
                assertEquals(getDocument.getEndUserId(), ArchiveServiceImplTest.this.scanviewLoginName);
                assertEquals(getDocument.getDocumentId(), documentId);
                return true;
            }
        }))).thenReturn(getDocumentResponse);

        ArchiveDocumentDto archiveDocumentDto =
                cut.retrieveDocument(vertragsnummer, documentType, documentId, scanviewLoginName);
        assertNotNull(archiveDocumentDto);
        assertEquals(archiveDocumentDto.getFileExtension(), extension.toLowerCase());
        assertEquals(archiveDocumentDto.getKey(), documentId);
        assertEquals(archiveDocumentDto.getDocumentType(), documentType);
        assertEquals(archiveDocumentDto.getLabel(), documentName);
        assertEquals(archiveDocumentDto.getVertragsNr(), vertragsnummer);
        assertNull(archiveDocumentDto.getStream());
        assertNull(archiveDocumentDto.getMimeType());
    }

    @Test
    public void retrieveDocumentWithEsbFault() throws ESBFault {
        de.mnet.esb.cdm.shared.common.v1.ESBFault esbFaultInfo = new de.mnet.esb.cdm.shared.common.v1.ESBFault();
        String errorMessage = "Error message occurred during calling getDocument service.";
        esbFaultInfo.setErrorMessage(errorMessage);
        NullPointerException cause = new NullPointerException();
        ESBFault esbFault = new ESBFault("Error message", esbFaultInfo, cause);
        when(documentArchiveService.getDocument(any(GetDocument.class))).thenThrow(esbFault);

        try {
            cut.retrieveDocument("1234", ArchiveDocumentType.CUDA_KUENDIGUNG, "123456", scanviewLoginName);
            fail("FindException expected here due to an ESBFault.");
        }
        catch (FindException e) {
            e.printStackTrace();
            assertEquals(e.getCause(), cause);
            assertTrue(e.getMessage().contains(errorMessage));
        }
    }

    @Test
    public void searchDocuments() throws FindException, ESBFault {
        final String vertragsnummer = "1234";
        final String documentId = "123456";
        final ArchiveDocumentType documentType = ArchiveDocumentType.CUDA_KUENDIGUNG;
        String documentName = "name";
        String extension = "PDF";
        final ArchiveDocumentClass documentClass = ArchiveDocumentClass.DOC_CLASS_TOP;
        when(documentArchiveService.searchDocuments(any(SearchDocuments.class)))
                .thenReturn(
                        new SearchDocumentsResponseBuilder()
                                .addDocument(
                                        new SearchDocumentsResponseDocumentBuilder()
                                                .withId(documentId)
                                                .withDocumentClass(documentClass)
                                                .addKey(VERTRAG, vertragsnummer, "type")
                                                .addKey(DOCUMENT_TYPE, documentType.getDocumentTypeName(), "type")
                                                .addItem(
                                                        new ItemBuilder()
                                                                .withId("12345")
                                                                .withCreationDate(LocalDateTime.now())
                                                                .withName(documentName)
                                                                .withExtension(extension)
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                );

        List<SearchDocumentsResponse.Document> searchDocuments =
                cut.searchDocuments(scanviewLoginName, documentClass,
                        new ImmutableMap.Builder<ArchiveDocumentKey, String>()
                                .put(VERTRAG, vertragsnummer)
                                .put(DOCUMENT_TYPE, documentType.getDocumentTypeName())
                                .build()
                );
        assertNotEmpty(searchDocuments);
        assertEquals(searchDocuments.size(), 1);
        verify(documentArchiveService)
                .searchDocuments(argThat(new ArgumentMatcher<SearchDocuments>() {
                    @Override
                    public boolean matches(Object o) {
                        final SearchDocuments searchDocuments = (SearchDocuments) o;
                        assertEquals(searchDocuments.getEndUserId(), scanviewLoginName);
                        assertEquals(searchDocuments.getDocClass(), documentClass.getDocumentClass());
                        SearchDocuments.SearchCriteria searchCriteria = searchDocuments.getSearchCriteria();
                        assertNotNull(searchCriteria);
                        assertEquals(searchCriteria.getOperator(), SearchCriteriaBuilder.Operator.AND.name());
                        assertEquals(searchCriteria.getMaxHits(), new BigInteger("50"));
                        List<SearchDocuments.SearchCriteria.Key> keys = searchCriteria.getKey();
                        assertEquals(keys.size(), 2);
                        verifySearchCriteriaKey(keys, VERTRAG, vertragsnummer);
                        verifySearchCriteriaKey(keys, DOCUMENT_TYPE, documentType.getDocumentTypeName());
                        return true;
                    }
                }));
    }

    @Test
    public void searchDocumentsWithEsbFault() throws ESBFault {
        de.mnet.esb.cdm.shared.common.v1.ESBFault esbFaultInfo = new de.mnet.esb.cdm.shared.common.v1.ESBFault();
        String errorMessage = "Error message occurred during calling searchDocuments service.";
        esbFaultInfo.setErrorMessage(errorMessage);
        NullPointerException cause = new NullPointerException();
        ESBFault esbFault = new ESBFault("Error message", esbFaultInfo, cause);
        when(documentArchiveService.searchDocuments(any(SearchDocuments.class))).thenThrow(esbFault);

        try {
            cut.searchDocuments(scanviewLoginName, ArchiveDocumentClass.DOC_CLASS_TOP,
                    new ImmutableMap.Builder<ArchiveDocumentKey, String>()
                            .put(VERTRAG, "1234")
                            .put(DOCUMENT_TYPE, ArchiveDocumentType.CUDA_KUENDIGUNG.getDocumentTypeName())
                            .build()
            );
            fail("FindException expected here due to an ESBFault.");
        }
        catch (FindException e) {
            e.printStackTrace();
            assertEquals(e.getCause(), cause);
            assertTrue(e.getMessage().contains(errorMessage));
        }
    }

    @Test
    public void archiveDocument() throws FindException, ESBFault {
        final String vertragsnummer = "1234";
        final Long customerNumber = 12345678L;
        final String debitorNumber = "123456";
        final Long taifunOrderNr = 123456789L;
        final ArchiveDocumentType documentType = ArchiveDocumentType.CUDA_KUENDIGUNG;
        final String fileName = "asdlhjflksad";

        final byte[] data = null;
        ArchiveMetaData metaData = new ArchiveMetaData();
        metaData.setDateiname(fileName);
        metaData.setKundennr(customerNumber);
        metaData.setDebitornr(debitorNumber);
        metaData.setTaifunAuftragsnr(taifunOrderNr);
        metaData.setSapAuftragsnummer(vertragsnummer);
        metaData.setDocumentType(documentType);
        metaData.setDokumentenDatum(LocalDateTime.of(1970, 1, 1, 0, 0, 0, 0));
        final String uuid = cut.archiveDocument(data, metaData);
        assertNotNull(uuid);
        verify(documentArchiveServiceTransacted)
                .archiveDocumentAsync(argThat(new ArgumentMatcher<ArchiveDocument>() {
                    @Override
                    public boolean matches(Object o) {
                        final ArchiveDocument archiveDocument = (ArchiveDocument) o;
                        assertEquals(archiveDocument.getEndUserId(), scanviewLoginName);
                        ArchiveDocument.Document document = archiveDocument.getDocument();
                        assertEquals(document.getClazz(), ArchiveDocumentClass.DOC_CLASS_AUFTRAG_UNTERLAGEN.getDocumentClass());
                        List<ArchiveDocument.Document.Item> items = document.getItem();
                        assertEquals(items.size(), 1);
                        assertEquals(items.get(0).getFileName(), fileName);
                        assertEquals(items.get(0).getFileData(), data);
                        List<ArchiveDocument.ArchiveKey> archiveKeys = archiveDocument.getArchiveKey();
                        assertEquals(archiveKeys.size(), 7);
                        verifyArchiveKey(archiveKeys, VERTRAG, vertragsnummer);
                        verifyArchiveKey(archiveKeys, DOCUMENT_TYPE, documentType.getDocumentTypeName());
                        verifyArchiveKey(archiveKeys, CUSTOMER_NR, customerNumber.toString());
                        verifyArchiveKey(archiveKeys, DEBITOR_NR, debitorNumber);
                        verifyArchiveKey(archiveKeys, TAIFUN_ORDER_NR, taifunOrderNr.toString());
                        verifyArchiveKey(archiveKeys, DOCUMENT_DATE, "01.01.1970");
                        verifyArchiveKey(archiveKeys, UUID_KEY, uuid);
                        return true;
                    }
                }));
    }

    private void verifyArchiveKey(List<ArchiveDocument.ArchiveKey> keys, ArchiveDocumentKey key, String value) {
        boolean found = false;
        for (ArchiveDocument.ArchiveKey criteriaKey : keys) {
            if (criteriaKey.getName().equals(key.getKey())) {
                assertEquals(criteriaKey.getValue(), value);
                found = true;
                break;
            }
        }
        if (!found) {
            fail(String.format("ArchiveKey with the key '%s' could not be found!", key));
        }
    }

    private void verifySearchCriteriaKey(List<SearchDocuments.SearchCriteria.Key> keys, ArchiveDocumentKey key, String value) {
        boolean found = false;
        for (SearchDocuments.SearchCriteria.Key criteriaKey : keys) {
            if (criteriaKey.getName().equals(key.getKey())) {
                assertEquals(criteriaKey.getValue(), value);
                found = true;
                break;
            }
        }
        if (!found) {
            fail(String.format("SearchCriteriaKey with the key '%s' could not be found!", key));
        }
    }

}
