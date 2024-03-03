/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.12.13
 */
package de.augustakom.common.tools.mail;

import java.io.*;
import java.util.*;
import javax.mail.*;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;

import de.augustakom.common.service.iface.IServiceObject;

/**
 * User: guiber
 */
public interface IHurricanMailSender extends JavaMailSender, IServiceObject {

    void send(MailData mailData) throws MailException, MessagingException;

    class AttachmentData implements Serializable {
        public AttachmentData(byte[] stream, String fileName) {
            this.stream = stream;
            this.fileName = fileName;
        }

        public byte[] stream;
        public String fileName;
    }

    class MailData implements Serializable {
        public MailData(String from, String[] to, String[] cc, String subject,
                List<AttachmentData> attachments, String body) {
            this.from = from;
            this.to = to;
            this.cc = cc;
            this.subject = subject;
            this.attachments = attachments;
            this.body = body;
        }

        public String from;
        public String[] to;
        public String[] cc;
        public String subject;
        public List<AttachmentData> attachments;
        public String body;
    }

}
