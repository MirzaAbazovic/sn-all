/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.02.2010
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.common.service.iface.IServiceObject;

/**
 * because of MailAttachment does not know that it has a foreign key to Mail set persist always to false that a Mail is
 * always saved with their corresponding Mail and MailAttachments
 *
 * @see MailAttachmentBuilder
 */
@SuppressWarnings("unused")
public class MailBuilder extends AbstractCCIDModelBuilder<MailBuilder, Mail> implements IServiceObject {

    private List<MailAttachmentBuilder> mailAttachmentBuilders = new ArrayList<>();

    private String from = "usertest@m-net.de";
    private String cc = null;
    private String to = "usertest@m-net.de";
    private String subject = "Testcase";
    private String text = null;
    private String textLong = null;
    private List<MailAttachment> mailAttachments = new ArrayList<>();

    private int numberOfTries = 0;
    private Date sentAt = null;
    private Date createdAt = new Date();
    private String createdBy = "UserTest";
    private Boolean isTextHTML;
    private Boolean isTextLongHTML;

    public MailBuilder addAttachment(MailAttachmentBuilder mailAttachmentBuilder) {
        this.mailAttachmentBuilders.add(mailAttachmentBuilder);
        return this;
    }

    @Override
    protected void beforeBuild() {
        super.beforeBuild();
        super.setPersist(false);
    }

    @Override
    protected void afterBuild(Mail mail) {
        super.afterBuild(mail);
        for (MailAttachmentBuilder mailAttachmentBuilder : mailAttachmentBuilders) {
            mail.addMailAttachment(mailAttachmentBuilder.get());
        }
    }

    @Override
    public boolean getPersist() {
        return false;
    }

    @Override
    public MailBuilder setPersist(boolean persist) {
        throw new UnsupportedOperationException(getClass().getName() + " can not create persistent entities.");
    }

    public MailBuilder withFrom(String from) {
        this.from = from;
        return this;
    }

    public MailBuilder withCc(String cc) {
        this.cc = cc;
        return this;
    }

    public MailBuilder withTo(String to) {
        this.to = to;
        return this;
    }

    public MailBuilder withSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public MailBuilder withText(String text) {
        this.text = text;
        return this;
    }

    public MailBuilder withTextLong(String textLong) {
        this.textLong = textLong;
        return this;
    }

    public MailBuilder withNumberOfTries(int numberOfTries) {
        this.numberOfTries = numberOfTries;
        return this;
    }

    public MailBuilder withSentAt(Date sentAt) {
        this.sentAt = sentAt;
        return this;
    }

    public MailBuilder withCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public MailBuilder withCreatedBy(String createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public MailBuilder withIsTextHTML(Boolean isTextHTML) {
        this.isTextHTML = isTextHTML;
        return this;
    }

    public MailBuilder withIsTextLongHTML(Boolean isTextLongHTML) {
        this.isTextLongHTML = isTextLongHTML;
        return this;
    }

}
