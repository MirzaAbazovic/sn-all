/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.11.2011 13:25:54
 */
package de.augustakom.common.tools.mail;

import java.io.*;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import org.apache.log4j.Logger;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

public class HurricanMailSender extends JavaMailSenderImpl implements IHurricanMailSender {

    private static final Logger LOGGER = Logger.getLogger(HurricanMailSender.class);

    private boolean sendMail = false;

    public void setSendMail(boolean sendMail) {
        this.sendMail = sendMail;
    }

    /**
     * Getter for the 'mail.sendMail' property
     */
    public boolean isSendMail() {
        return sendMail;
    }

    @Override
    protected void doSend(MimeMessage[] mimeMessages, Object[] originalMessages) throws MailException {
        if (sendMail) {
            super.doSend(mimeMessages, originalMessages);
        }
        else {
            LOGGER.info("Don't send mail: mimeMessages=" + Arrays.toString(mimeMessages) + ",originalMessages="
                    + Arrays.toString(originalMessages));
        }
    }


    @Override
    public void send(MailData mailData) throws MailException, MessagingException {
        if (mailData != null) {
            sendMimeMessage(mailData);
        }
    }

    private void sendMimeMessage(MailData mailData) throws MailException, MessagingException {
        MimeMessage msg = createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true);
        helper.setFrom(mailData.from);
        helper.setTo(mailData.to);
        helper.setCc(mailData.cc);
        helper.setSubject(mailData.subject);
        if (mailData.attachments != null) {
            for (AttachmentData attachment : mailData.attachments) {
                helper.addAttachment(attachment.fileName, new AttachmentInputStream(attachment.stream));
            }
        }
        helper.setText(mailData.body);

        send(msg);
    }

    class AttachmentInputStream implements InputStreamSource {
        private byte[] stream;

        public AttachmentInputStream(byte[] stream) {
            this.stream = stream;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(stream);
        }
    }
}
