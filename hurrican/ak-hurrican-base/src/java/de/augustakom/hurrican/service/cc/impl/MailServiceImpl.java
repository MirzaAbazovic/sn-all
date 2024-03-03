/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.02.2010 19:51:05
 */
package de.augustakom.hurrican.service.cc.impl;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import javax.annotation.*;
import javax.mail.*;
import javax.mail.internet.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.Assert;

import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.common.tools.mail.HurricanMailSender;
import de.augustakom.common.tools.validation.EMailValidator;
import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.MailDAO;
import de.augustakom.hurrican.exceptions.ProcessPendingEmailsException;
import de.augustakom.hurrican.model.cc.Mail;
import de.augustakom.hurrican.model.cc.MailAttachment;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.MailService;

@CcTxRequired
public class MailServiceImpl extends DefaultCCService implements MailService {

    private static final Logger LOGGER = Logger.getLogger(MailServiceImpl.class);

    @Resource(name = "mailDAO")
    private MailDAO mailDAO;
    @Autowired
    private HurricanMailSender mailSender;

    @Value("${mail.save.to.disk}")
    private boolean saveMailToDisk;

    @Override
    public void sendMail(Mail mail, Long sessionId) {
        try {
            sendMailWithUserName(mail, getAKUserBySessionId(sessionId).getLoginName());
        }
        catch (AKAuthenticationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void sendMailFromHurricanServer(Mail mail) {
        sendMailWithUserName(mail, "hurrican");
    }

    private void sendMailWithUserName(Mail mail, String userName) {
        Assert.notNull(mail, "Keine zu verschickende Email vorhanden.");

        if (!EMailValidator.getInstance().isValid(mail.getFrom())) {
            throw new IllegalArgumentException("Die Email-Adresse des Absenders ist ungültig.");
        }
        else if ((mail.getCc() != null) && (!EMailValidator.getInstance().isValid(mail.getCc()))) {
            throw new IllegalArgumentException("Die Email-Adresse des CC-Empfängers ist ungültig.");
        }
        else if (mail.getNumberOfTries() >= MailService.MAX_NUMBER_OF_TRIES) {
            throw new IllegalArgumentException("Diese Mail kann nicht gesendet werden. Zu viele ungültigte Versuche.");
        }
        else if (mail.getSentAt() != null) {
            throw new IllegalArgumentException("Diese Mail wurde bereits gesendet.");
        }

        if (StringUtils.containsAny(mail.getTo(), new char[] { ',', ';' })) {
            String[] mails = (StringUtils.contains(mail.getTo(), ";"))
                    ? StringUtils.split(mail.getTo(), ";") : StringUtils.split(mail.getTo(), ",");
            if (mails != null) {
                for (String mailTo : mails) {
                    if (!EMailValidator.getInstance().isValid(mailTo)) {
                        throw new IllegalArgumentException("Die Email-Adresse des Empfängers ist ungültig.");
                    }
                }
            }
        }
        else if (!EMailValidator.getInstance().isValid(mail.getTo())) {
            throw new IllegalArgumentException("Die Email-Adresse des Empfängers ist ungültig.");
        }

        mail.setCreatedAt(new Date());
        mail.setCreatedBy(userName);
        mailDAO.store(mail);
    }

    @Override
    public void sendMail(Mail mail, Long sessionId, MailAttachment... mailAttachments) {
        for (MailAttachment mailAttachment : mailAttachments) {
            mail.addMailAttachment(mailAttachment);
        }
        sendMail(mail, sessionId);
    }

    @Override
    public void sendMailFailed(Mail mail) {
        mail.incrementNumberOfTries();
        mailDAO.store(mail);
    }

    @Override
    public void sendMailSuccessful(Mail mail) throws IllegalArgumentException {
        if (mail.getSentAt() != null) {
            throw new IllegalArgumentException("Mail wurde bereits gesendet.");
        }
        mail.sentMailSuccessfull();
        mailDAO.store(mail);
    }

    @Override
    public List<Mail> findAllPendingMails() throws FindException {
        return mailDAO.findAllPendingMails();
    }

    @Override
    public void processPendingEmails() throws ProcessPendingEmailsException {
        List<Exception> exceptions = new ArrayList<>();
        try {
            List<Mail> pendingMails = findAllPendingMails();
            for (Mail mail : pendingMails) {
                processSinglePendingMail(exceptions, mail);
            }
        }
        catch (FindException e) {
            exceptions.add(e);
        }

        //if expected exceptions are there throw a ProcessPendingEmailsException
        if (CollectionUtils.isNotEmpty(exceptions)) {
            throw new ProcessPendingEmailsException("the execution of the process pending emails service produced some errors", exceptions);
        }
    }

    private void processSinglePendingMail(List<Exception> exceptions, Mail mail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(mail.getFrom());
            if (mail.getCc() != null) {
                helper.setCc(getMailAsArray(mail.getCc()));
            }
            helper.setTo(getMailAsArray(mail.getTo()));
            helper.setSubject(StringUtils.trimToEmpty(mail.getSubject()));
            /*
             * HUR-10545: setText() immer (auch mit leerem String als Stellvertreter fuer null) rufen. Leere
             * MimeMessages ohne Text sind nicht (mehr) erlaubt. Dies ist unanhaengig von der Anzahl der
             * Attachments. Die neue mail.jar (Version 1.4.5) wirft sonst eine Exception.
             */
            final String mailText = StringUtils.trimToEmpty(mail.getText());
            if (StringUtils.isEmpty(mailText)) {
                helper.setText(StringUtils.trimToEmpty(mail.getTextLong()), mail.getIsTextLongHTML());
            }
            else {
                helper.setText(mailText, mail.getIsTextHTML());
            }

            for (MailAttachment mailAttachment : mail.getMailAttachments()) {
                helper.addAttachment(mailAttachment.getFileName(),
                        new ByteArrayResource(mailAttachment.getAttachmentFile()));
            }
            saveAndSendMail(exceptions, mail, message);
        }
        catch (Exception e) {
            exceptions.add(e);
        }
    }

    private void saveAndSendMail(List<Exception> exceptions, Mail mail, MimeMessage message) {
        try {
            saveToLocalTempDir(message);
            sendMailOverSmtp(mail, message);
        }
        catch (MailSendException e) {
            LOGGER.warn(e.getMessage());
            sendMailFailed(mail);

            String msg = String.format("Mail with Id %s sending failed for %d-times", mail.getId(),
                    mail.getNumberOfTries());
            LOGGER.info(msg);
            if (mail.getNumberOfTries() >= MailService.MAX_NUMBER_OF_TRIES) {
                exceptions.add(e);
            }
        }
    }

    /**
     * Check the flag {@link HurricanMailSender#isSendMail()} and send out or log if it won't be sent.
     *
     * @param mail
     * @param mimeMessages
     */
    void sendMailOverSmtp(Mail mail, MimeMessage mimeMessages) {
        if (mailSender.isSendMail()) {
            mailSender.send(mimeMessages);
            sendMailSuccessful(mail);
            LOGGER.info(String.format("Mail with Id %s sent successfull to %s", mail.getId(), mail.getTo()));
        }
        else {
            LOGGER.info(String.format("Don't send mail with Id %s - 'mail.sendMail' is disabled: Subject=%s", mail.getId(), mail.getSubject()));
        }
    }

    /**
     * Saves the message to the local java tmp folder. !!! Save is done only if the system property 'mail.save.to.disk'
     * is set to true !!!
     */
    private void saveToLocalTempDir(MimeMessage message) {
        if (saveMailToDisk) {
            try {
                String file = String.format("%stest-mail_%s.eml", SystemUtils.getJavaIoTmpDir() + File.separator, message.getSubject().trim() + System.currentTimeMillis());
                OutputStream os = Files.newOutputStream(Paths.get(file));
                message.writeTo(os);
                os.close();
                LOGGER.info("save outgoing e-mail to: '" + file + "'");
            }
            catch (MessagingException | IOException e) {
                LOGGER.error("error by storing a MimeMessage to the local java temp dir", e);
            }
        }
    }

    private String[] getMailAsArray(String mail) {
        if (StringUtils.contains(mail, HurricanConstants.EMAIL_SEPARATOR)) {
            return StringUtils.split(mail, HurricanConstants.EMAIL_SEPARATOR);
        }
        else if (StringUtils.contains(mail, ",")) {
            return StringUtils.split(mail, ",");
        }
        return new String[] { mail };
    }

}
