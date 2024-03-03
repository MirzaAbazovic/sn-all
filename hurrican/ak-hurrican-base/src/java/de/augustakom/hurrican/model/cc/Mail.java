/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.02.2010 18:31:00
 */

package de.augustakom.hurrican.model.cc;

import java.io.*;
import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Table(name = "T_MAIL")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_MAIL_0", allocationSize = 1)
public class Mail extends AbstractCCIDModel {

    public static final int MAIL_TEXT_MAX_COLUMN_SIZE = 4000;

    private static final long serialVersionUID = -4032679876317750959L;
    private Long auftragId;
    private Long verlaufId;
    private String from;
    private String cc;
    private String to;
    private String subject;
    private String text;
    private Boolean isTextHTML = false;
    private String textLong;
    private Boolean isTextLongHTML = false;
    private List<MailAttachment> mailAttachments = new ArrayList<MailAttachment>();

    private int numberOfTries = 0;
    private Date sentAt;
    private Date createdAt = new Date();
    private String createdBy;

    /**
     * Fügt das übergebene Attachment zur Mail hinzu
     */
    public void addMailAttachment(MailAttachment mailAttachment) {
        mailAttachments.add(mailAttachment);
    }

    /**
     * Fügt das übergebene Attachment zur Mail hinzu
     */
    public void addMailAttachment(String fileName, byte[] fileAttachment) {
        MailAttachment mailAttachment = new MailAttachment();
        mailAttachment.setFileName(fileName);
        mailAttachment.setAttachmentFile(fileAttachment);
        addMailAttachment(mailAttachment);
    }

    /**
     * Fügt das übergebene Attachment zur Mail hinzu
     */
    public void addMailAttachment(String fileName, File fileAttachment) throws IOException {
        MailAttachment mailAttachment = new MailAttachment();
        mailAttachment.setFileName(fileName);
        mailAttachment.setAttachmentFile(fileAttachment);
        addMailAttachment(mailAttachment);
    }

    /**
     * Zählt die Anzahl der Versuche um eins hoch
     */
    public void incrementNumberOfTries() {
        numberOfTries++;
    }

    /**
     * Das Datum, dass die Mail erfolgreich gesendet wird gesetzt, falls nicht bereits gesetzt
     */
    public void sentMailSuccessfull() {
        if (sentAt == null) {
            sentAt = new Date();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Mail other = (Mail) obj;
        if (!getId().equals(other.getId())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + getId().intValue();
        return result;
    }

    @Column(name = "AUFTRAG_ID")
    public Long getAuftragId() {
        return auftragId;
    }

    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    @Column(name = "VERLAUF_ID")
    public Long getVerlaufId() {
        return verlaufId;
    }

    public void setVerlaufId(Long verlaufId) {
        this.verlaufId = verlaufId;
    }

    @Column(name = "MAIL_FROM")
    @NotNull
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    @Column(name = "MAIL_TO")
    @NotNull
    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @NotNull
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    /**
     * for HTMl set the Text like "{@code <html> <body><h1>TEST HTML</h1> </body></html>}" and {link #setIsTextHTML}
     * TRUE.
     *
     * @param text
     */
    public void setText(String text) {
        this.text = text;
    }

    @Column(name = "TEXT_HTML")
    public boolean getIsTextHTML() {
        return isTextHTML != null && isTextHTML;
    }

    /**
     * for HTMl set the Text like "{@code <html> <body><h1>TEST HTML</h1> </body></html>}"
     *
     * @param isTextHTML
     */
    public void setIsTextHTML(Boolean isTextHTML) {
        this.isTextHTML = isTextHTML;
    }

    /**
     * for HTMl set the Text like "{@code <html> <body><h1>TEST HTML</h1> </body></html>}" and {link #setIsTextHTML}
     * TRUE.
     *
     * @param textLong
     */
    public void setTextLong(String textLong) {
        this.textLong = textLong;
    }

    @Column(name = "TEXT_LONG", columnDefinition = "CLOB NOT NULL")
    @Lob
    public String getTextLong() {
        return textLong;
    }

    @Column(name = "TEXT_LONG_HTML")
    public boolean getIsTextLongHTML() {
        return isTextLongHTML != null && isTextLongHTML;
    }

    /**
     * for HTMl set the Text like "{@code <html> <body><h1>TEST HTML</h1> </body></html>}"
     *
     * @param isTextLongHTML
     */
    public void setIsTextLongHTML(Boolean isTextLongHTML) {
        this.isTextLongHTML = isTextLongHTML;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "MAIL_ID", nullable = false)
    public List<MailAttachment> getMailAttachments() {
        return mailAttachments;
    }

    public void setMailAttachments(List<MailAttachment> mailAttachments) {
        this.mailAttachments = mailAttachments;
    }

    @Column(name = "NUMBER_OF_TRIES")
    @NotNull
    public int getNumberOfTries() {
        return numberOfTries;
    }

    public void setNumberOfTries(int numberOfTries) {
        this.numberOfTries = numberOfTries;
    }

    @Column(name = "SENT_AT")
    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }

    @Column(name = "CREATED_AT")
    @NotNull
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Column(name = "CREATED_By")
    @NotNull
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
