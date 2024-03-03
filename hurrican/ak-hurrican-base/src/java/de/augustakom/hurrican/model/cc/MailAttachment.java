/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.02.2010 18:33:29
 */

package de.augustakom.hurrican.model.cc;

import java.io.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

@Entity
@Table(name = "T_MAIL_ATTACHMENT")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_MAIL_ATTACHMENT_0", allocationSize = 1)
public class MailAttachment extends AbstractCCIDModel {

    private String fileName;
    private byte[] attachmentFile;

    @Column(name = "FILE_NAME")
    @NotNull
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Column(name = "ATTACHMENT_FILE")
    @NotNull
    @Lob
    @Basic(fetch = FetchType.LAZY)
    public byte[] getAttachmentFile() {
        return attachmentFile;
    }

    public void setAttachmentFile(byte[] attachmentFile) {
        this.attachmentFile = attachmentFile;
    }

    public void setAttachmentFile(File fileAttachment) throws IOException {
        this.attachmentFile = FileUtils.readFileToByteArray(fileAttachment);
    }

    public void setAttachmentFile(InputStream fileAttachment) throws IOException {
        this.attachmentFile = IOUtils.toByteArray(fileAttachment);
    }
}
