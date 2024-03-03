/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.02.2010
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.service.iface.IServiceObject;

/**
 * because of MailAttachment does not know that it has a foreign key to Mail set persist always to false that
 * MailAttachment is only saved by using saving Mail
 */
@SuppressWarnings("unused")
public class MailAttachmentBuilder extends AbstractCCIDModelBuilder<MailAttachmentBuilder, MailAttachment> implements IServiceObject {

    private String fileName = "attachment";
    private byte[] attachmentFile = new byte[] { 12 };

    @Override
    protected void beforeBuild() {
        super.beforeBuild();
        super.setPersist(false);
    }

    @Override
    public boolean getPersist() {
        return false;
    }

    @Override
    public MailAttachmentBuilder setPersist(boolean persist) {
        throw new UnsupportedOperationException(getClass().getName() + " can not create persistent entities.");
    }

    public MailAttachmentBuilder withFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public MailAttachmentBuilder withAttachmentFile(byte[] attachmentFile) {
        this.attachmentFile = attachmentFile;
        return this;
    }
}
