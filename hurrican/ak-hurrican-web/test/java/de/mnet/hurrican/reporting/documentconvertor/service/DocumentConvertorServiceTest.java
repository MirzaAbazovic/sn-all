/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.12.2011 13:20:33
 */
package de.mnet.hurrican.reporting.documentconvertor.service;

import static de.augustakom.common.BaseTest.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.io.*;
import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.DocumentFamily;
import com.artofsolving.jodconverter.DocumentFormat;
import com.artofsolving.jodconverter.XmlDocumentFormatRegistry;
import com.google.common.io.ByteStreams;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ClassPathResource;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.hurrican.reporting.documentconvertor.service.DocumentConvertorService.MimeType;
import de.mnet.hurrican.reporting.service.OpenOfficeService;

@Test(groups = UNIT)
public class DocumentConvertorServiceTest extends BaseTest {

    @Mock
    private OpenOfficeService openOfficeService;

    @Mock
    private DocumentConverter openOfficeDocumentConverter;

    @Captor
    private ArgumentCaptor<DocumentFormat> inputFormatCaptor;

    @Captor
    private ArgumentCaptor<DocumentFormat> outputFormatCaptor;

    @InjectMocks
    private DocumentConvertorService documentConvertorService;

    @BeforeMethod
    public void setup() {
        documentConvertorService = new DocumentConvertorService();
        documentConvertorService.setDocumentFormatRegistry(new XmlDocumentFormatRegistry());
        MockitoAnnotations.initMocks(this);
        when(openOfficeService.getDocumentConverter()).thenReturn(openOfficeDocumentConverter);
    }

    public void convertingJpegShouldWork() throws Exception {
        ClassPathResource input = new ClassPathResource(
                "/de/mnet/hurrican/reporting/documentconvertor/service/lageplan.jpg");
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        MimeType converted = documentConvertorService.convert(input.getInputStream(), output, "image/jpeg");
        output.close();

        assertEquals(converted, MimeType.PDF);
    }

    public void convertingPdfShouldWork() throws Exception {
        ClassPathResource input = new ClassPathResource(
                "/de/mnet/hurrican/reporting/documentconvertor/service/cuda_kuendigung.pdf");
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        MimeType converted = documentConvertorService.convert(input.getInputStream(), output, "application/pdf");
        output.close();

        assertEquals(converted, MimeType.PDF);
        assertEquals(output.toByteArray(), ByteStreams.toByteArray(input.getInputStream()));
    }

    public void convertingTiffShouldWork() throws Exception {
        ClassPathResource input = new ClassPathResource(
                "/de/mnet/hurrican/reporting/documentconvertor/service/outputxml.tif");
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        MimeType converted = documentConvertorService.convert(input.getInputStream(), output, "image/tiff");
        output.close();

        assertEquals(converted, MimeType.TIFF);
        assertEquals(output.toByteArray(), ByteStreams.toByteArray(input.getInputStream()));
    }

    public void convertingXlsShouldWork() throws Exception {
        InputStream input = mock(InputStream.class);
        OutputStream output = mock(OutputStream.class);

        MimeType converted = documentConvertorService.convert(input, output, "application/excel");
        assertEquals(converted, MimeType.PDF);

        verify(openOfficeDocumentConverter).convert(
                eq(input), inputFormatCaptor.capture(),
                eq(output), outputFormatCaptor.capture());

        DocumentFormat inputFormat = inputFormatCaptor.getValue();
        assertEquals(inputFormat.getFileExtension(), "xls");
        assertEquals(inputFormat.getFamily(), DocumentFamily.SPREADSHEET);
        DocumentFormat outputFormat = outputFormatCaptor.getValue();
        assertEquals(outputFormat.getMimeType(), "application/pdf");
        assertTrue(inputFormat.isExportableTo(outputFormat));
    }

    public void convertingDocShouldWork() throws Exception {
        InputStream input = mock(InputStream.class);
        OutputStream output = mock(OutputStream.class);

        MimeType converted = documentConvertorService.convert(input, output, "application/msword");
        assertEquals(converted, MimeType.PDF);

        verify(openOfficeDocumentConverter).convert(
                eq(input), inputFormatCaptor.capture(),
                eq(output), outputFormatCaptor.capture());

        DocumentFormat inputFormat = inputFormatCaptor.getValue();
        assertEquals(inputFormat.getFileExtension(), "doc");
        assertEquals(inputFormat.getFamily(), DocumentFamily.TEXT);
        DocumentFormat outputFormat = outputFormatCaptor.getValue();
        assertEquals(outputFormat.getMimeType(), "application/pdf");
        assertTrue(inputFormat.isExportableTo(outputFormat));
    }

}


