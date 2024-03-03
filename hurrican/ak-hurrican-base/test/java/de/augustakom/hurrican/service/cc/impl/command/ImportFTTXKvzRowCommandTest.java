package de.augustakom.hurrican.service.cc.impl.command;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.ReferenceService;

@Test(groups = { BaseTest.UNIT })
public class ImportFTTXKvzRowCommandTest extends BaseTest {
    @InjectMocks
    @Spy
    private ImportFTTXKvzRowCommand testling;

    @Mock
    private ReferenceService referenceService;

    @BeforeMethod
    public void setUp() {
        testling = new ImportFTTXKvzRowCommand();
        initMocks(this);
    }

    private void setupReferenceServiceMock(String dslamType) throws FindException {
        Reference referenceMock = Mockito.mock(Reference.class);
        when(referenceMock.getStrValue()).thenReturn(dslamType);
        when(referenceService.findReferencesByType(Reference.REF_TYPE_HW_DSLAM_TYPE, false))
                .thenReturn(Collections.singletonList(referenceMock));
    }

    @Test
    public void shouldSucceedForValidDslamType() throws Exception {
        String dslamType = "dslamType";
        setupReferenceServiceMock(dslamType);
        testling.assertValidDslamType(dslamType);
    }

    @Test(expectedExceptions = FindException.class, expectedExceptionsMessageRegExp = "DSLAM Modell 'invalid' ist nicht bekannt")
    public void shouldThrowExceptionForInvalidDslamType() throws Exception {
        setupReferenceServiceMock("valid");
        testling.assertValidDslamType("invalid");
    }

}
