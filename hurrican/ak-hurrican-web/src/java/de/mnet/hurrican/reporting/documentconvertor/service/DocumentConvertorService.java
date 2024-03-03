/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.12.2011 13:06:03
 */
package de.mnet.hurrican.reporting.documentconvertor.service;

import java.io.*;
import java.util.*;
import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.DocumentFormatRegistry;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.ByteStreams;
import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;

import de.mnet.hurrican.reporting.service.OpenOfficeService;

/**
 * Service to Convert a document to pdf
 *
 *
 */
public class DocumentConvertorService {

    public static enum MimeType {
        TIFF("image/tiff", "tif"),
        JPEG("image/jpeg", "jpeg", "jpg", "jpe"),
        MSWORD("application/msword", "doc"),
        EXCEL("application/excel", "xls"),
        PDF("application/pdf", "pdf"),
        UNKNOWN("unknown", "unknown");

        public final String mimeType;
        public final List<String> extensions;

        private MimeType(String mimeType, String... extensions) {
            this.mimeType = mimeType;
            this.extensions = ImmutableList.copyOf(extensions);
        }

        public static MimeType fromMimeTyp(String mimeTyp) {
            for (MimeType dateityp : MimeType.values()) {
                if (dateityp.mimeType.equals(mimeTyp)) {
                    return dateityp;
                }
            }
            return UNKNOWN;
        }

        public static MimeType fromExtension(String extension) {
            for (MimeType dateityp : MimeType.values()) {
                if (dateityp.extensions.contains(extension.toLowerCase())) {
                    return dateityp;
                }
            }
            return UNKNOWN;
        }

    }

    @Autowired
    private OpenOfficeService openOfficeService;
    private DocumentFormatRegistry documentFormatRegistry;

    /**
     * Converts a input Document to PDF/Tiff. If the Document is already a Tiff or PDF, then no conversion takes place.
     * The result is written to the output stream. The resulting MimeType (PDF or Tiff) is returned.
     *
     * @param input          The document to convert
     * @param output         The convert document will be written to this stream
     * @param mimeTypeString The mime-type of the Document
     * @return MimeType of the converted document
     */
    public MimeType convert(InputStream input, OutputStream output, String mimeTypeString) {
        MimeType mimeType = MimeType.fromMimeTyp(mimeTypeString);
        if (mimeType == MimeType.JPEG) {
            convertJpg(input, output);
            return MimeType.PDF;
        }
        else if (mimeType == MimeType.TIFF || mimeType == MimeType.PDF) {
            writeInputToOutput(input, output);
            return mimeType;
        }
        else if (ImmutableSet.of(MimeType.EXCEL, MimeType.MSWORD).contains(mimeType)) {
            DocumentConverter converter = openOfficeService.getDocumentConverter();
            converter.convert(input,
                    documentFormatRegistry.getFormatByFileExtension(mimeType.extensions.iterator().next()),
                    output,
                    documentFormatRegistry.getFormatByFileExtension(MimeType.PDF.extensions.iterator().next()));

            return MimeType.PDF;
        }
        throw new RuntimeException("mimeType " + mimeTypeString + " not yet supported!");
    }

    private void writeInputToOutput(InputStream input, OutputStream output) {
        try {
            output.write(ByteStreams.toByteArray(input));
        }
        catch (IOException e) {
            throw new RuntimeException("Error writing input stream to output stream", e);
        }
    }

    private void convertJpg(InputStream input, OutputStream output) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document,
                    output);
            document.open();
            Image image = Image
                    .getInstance(ByteStreams.toByteArray(input));
            float percentWidth = document.getPageSize().getWidth() / image.getWidth() * 90f;
            float percentHeight = document.getPageSize().getHeight() / image.getHeight() * 90f;
            image.scalePercent(percentHeight > percentWidth ? percentWidth : percentHeight);
            document.add(image);
            document.close();
        }
        catch (Exception e) {
            throw new RuntimeException("Error writing input stream jpg to pdf", e);
        }
    }

    public void setDocumentFormatRegistry(DocumentFormatRegistry documentFormatRegistry) {
        this.documentFormatRegistry = documentFormatRegistry;
    }

}


