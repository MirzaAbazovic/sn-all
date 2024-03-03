/**
 * Copyright (c) 2010 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.10.2010
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.io.*;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import org.apache.commons.io.IOUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.mail.HurricanMailSender;
import de.augustakom.hurrican.exceptions.ProcessPendingEmailsException;
import de.augustakom.hurrican.model.cc.Mail;
import de.augustakom.hurrican.model.cc.MailAttachment;
import de.augustakom.hurrican.model.cc.MailAttachmentBuilder;
import de.augustakom.hurrican.model.cc.MailBuilder;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.cc.MailService;

@Test(groups = { "service" })
public class MailServiceTest extends AbstractHurricanBaseServiceTest {

    private MailService mailService;

    @Mock
    private HurricanMailSender mailSenderMock;
    @Spy
    @InjectMocks
    private MailServiceImpl spiedMailService;

    @BeforeMethod
    public void initMailService() {
        MockitoAnnotations.initMocks(this);
        mailService = getCCService(MailService.class);
    }

    @DataProvider(name = "dataProviderSendMail")
    protected Object[][] dataProviderSendMail() {
        return new Object[][] {
                { "test@m-net.de", "test2@m-net.de", "Subject 1", new Object[][] { } },
                { "test@m-net.de", "test2@m-net.de", "Subject 2",
                        new Object[][] { { "test1.txt", new byte[] { 22, 33, 44 } } } },
                {
                        "test@m-net.de",
                        "test2@m-net.de",
                        "Subject 3",
                        new Object[][] { { "test1.txt", new byte[] { 22, 33, 44 } },
                                { "very long filename with some blanks bla blub.pdf", new byte[] { 22, 12, 44 } },
                                { "test123.123", new byte[] { 0, 0, 0 } }, } }
        };
    }

    @Test(dataProvider = "dataProviderSendMail")
    public void testSendMail(String from, String to, String subject, Object[][] attachments) throws Exception {
        MailBuilder mailBuilder = getMailBuilder().withFrom(from).withTo(to).withSubject(subject);
        for (Object[] attachment : attachments) {
            String fileName = (String) attachment[0];
            byte[] attachmentFile = (byte[]) attachment[1];
            mailBuilder.addAttachment(createMailAttachmentBuilder(fileName, attachmentFile));
        }

        Mail mail = mailBuilder.get();
        mailService.sendMail(mail, getSessionId());
        flushAndClear();

        assertMailData(from, to, subject, attachments, mail);
        assertAttachmentData(attachments, mail);
    }

    @Test(dataProvider = "dataProviderSendMail")
    public void testSendMailAndAttachments(String from, String to, String subject, Object[][] attachments) throws Exception {
        MailBuilder mailBuilder = getMailBuilder().withFrom(from).withTo(to).withSubject(subject);
        MailAttachment[] mailAttachments = new MailAttachment[attachments.length];
        for (int idx = 0; idx < attachments.length; idx++) {
            String fileName = (String) attachments[idx][0];
            byte[] attachmentFile = (byte[]) attachments[idx][1];
            mailAttachments[idx] = createMailAttachmentBuilder(fileName, attachmentFile).build();
        }

        Mail mail = mailBuilder.get();
        mailService.sendMail(mail, getSessionId(), mailAttachments);
        flushAndClear();

        assertMailData(from, to, subject, attachments, mail);
        assertAttachmentData(attachments, mail);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSendToInvalidAddress() throws Exception {
        MailBuilder mailBuilder = getMailBuilder().withFrom("test@m-net.de").withTo("invalid");
        mailService.sendMail(mailBuilder.get(), getSessionId());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSendFromInvalidAddress() throws Exception {
        MailBuilder mailBuilder = getMailBuilder().withFrom("invalid").withTo("test@m-net.de");
        mailService.sendMail(mailBuilder.get(), getSessionId());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSendCcInvalidAddress() throws Exception {
        MailBuilder mailBuilder = getMailBuilder().withFrom("test@m-net.de").withTo("test@m-net.de").withCc("invalid");
        mailService.sendMail(mailBuilder.get(), getSessionId());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSendWithoutFrom() throws Exception {
        MailBuilder mailBuilder = getMailBuilder().withFrom(null).withTo("test@m-net.de");
        mailService.sendMail(mailBuilder.get(), getSessionId());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSendInvalidNumberOfTries() throws Exception {
        MailBuilder mailBuilder = getMailBuilder().withFrom("test@m-net.de").withTo("test@m-net.de")
                .withNumberOfTries(MailService.MAX_NUMBER_OF_TRIES).withSentAt(null);
        mailService.sendMail(mailBuilder.get(), getSessionId());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSendAlreadySent() throws Exception {
        MailBuilder mailBuilder = getMailBuilder().withFrom("test@m-net.de").withTo("test@m-net.de")
                .withNumberOfTries(0).withSentAt(new Date());
        mailService.sendMail(mailBuilder.get(), getSessionId());
    }

    @Test
    public void testSendMailSuccessful() {
        Mail mail = getMailBuilder().withNumberOfTries(0).withSentAt(null).build();
        mailService.sendMailSuccessful(mail);
        flushAndClear();

        assertNotNull(mail.getSentAt());
        assertEquals(mail.getNumberOfTries(), 0);
    }

    @Test
    public void testSendWithMultipleReceivers() throws Exception {
        Mail mail = getMailBuilder().withTo("test@m-net.de;test2@m-net.de").withNumberOfTries(0).withSentAt(null).build();
        mailService.sendMailSuccessful(mail);
        flushAndClear();

        assertNotNull(mail.getSentAt());
        assertEquals(mail.getNumberOfTries(), 0);
    }

    @Test
    public void testSendMailFailedOnce() {
        Mail mail = getMailBuilder().withNumberOfTries(0).withSentAt(null).build();
        mailService.sendMailFailed(mail);
        flushAndClear();

        assertNull(mail.getSentAt());
        assertEquals(mail.getNumberOfTries(), 1);
    }

    @Test
    public void testFindAllPendingMails() throws Exception {
        Mail mail1 = getMailBuilder().withNumberOfTries(0).withSentAt(null).build();
        Mail mail2 = getMailBuilder().withNumberOfTries(MailService.MAX_NUMBER_OF_TRIES - 1).withSentAt(null).build();
        Mail mail3 = getMailBuilder().withNumberOfTries(MailService.MAX_NUMBER_OF_TRIES - 1).withSentAt(null).build();
        Mail mail4 = getMailBuilder().withNumberOfTries(0).withSentAt(null).build();
        Mail mail5 = getMailBuilder().withNumberOfTries(MailService.MAX_NUMBER_OF_TRIES - 1).withSentAt(null).build();
        Mail mail6 = getMailBuilder().withNumberOfTries(1).withSentAt(null).build();
        Mail mail7 = getMailBuilder().withNumberOfTries(0).withSentAt(null)
                .addAttachment(createMailAttachmentBuilder("test.txt", new byte[] { 22, 33, 44 }))
                .addAttachment(createMailAttachmentBuilder("test2.txt", new byte[] { 22, 33, 44 }))
                .build();

        mailService.sendMail(mail1, getSessionId());
        mailService.sendMail(mail2, getSessionId());
        mailService.sendMail(mail3, getSessionId());
        mailService.sendMailFailed(mail3);
        mailService.sendMail(mail4, getSessionId());
        mailService.sendMailSuccessful(mail4);
        mailService.sendMail(mail5, getSessionId());
        mailService.sendMailFailed(mail5);
        mailService.sendMailSuccessful(mail5);
        mailService.sendMail(mail6, getSessionId());
        mailService.sendMail(mail7, getSessionId());
        flushAndClear();

        List<Mail> results = mailService.findAllPendingMails();
        //        assertEquals(results.size(), 4); // don't check because there could be others in database as well
        assertTrue(results.contains(mail1));
        assertTrue(results.contains(mail2));
        assertFalse(results.contains(mail3));
        assertFalse(results.contains(mail4));
        assertFalse(results.contains(mail5));
        assertTrue(results.contains(mail6));
        assertTrue(results.contains(mail7));
        assertTrue(NumberTools.equal(results.indexOf(mail7), results.lastIndexOf(mail7)));
    }


    @Test
    public void testSendPendingEmailSuccessful() throws Exception {
        when(mailSenderMock.isSendMail()).thenReturn(true);
        MimeMessage mimeMessage = new JavaMailSenderImpl().createMimeMessage();

        // create mails to send
        List<Mail> mails = new ArrayList<>();
        mails.add(getBuilder(MailBuilder.class).withFrom("mail1from@m-net.de").withTo("mail1to@m-net.de").withCc("m@b.de")
                .withSubject("Subject Test").withNumberOfTries(0).withSentAt(null)
                .withIsTextHTML(null)
                .addAttachment(getBuilder(MailAttachmentBuilder.class).withFileName("testfile1")
                                .withAttachmentFile(new byte[] { 12, 34, 56 })
                ).build());

        // create, stub mocks and spies
        doReturn(mails).when(spiedMailService).findAllPendingMails();
        doNothing().when(spiedMailService).sendMailSuccessful(mails.get(0));
        when(mailSenderMock.createMimeMessage()).thenReturn(mimeMessage);

        spiedMailService.processPendingEmails();
        verifyMailServiceAndMailSender(mimeMessage, mails.get(0), mailSenderMock, spiedMailService, true);
        assertMimeMessage(mimeMessage, mails.get(0), false);
    }

    @Test
    public void testSendPendingEmailFailed() throws Exception {
        when(mailSenderMock.isSendMail()).thenReturn(true);
        MimeMessage mimeMessage = new JavaMailSenderImpl().createMimeMessage();

        // create mails to send
        List<Mail> mails = new ArrayList<>();
        mails.add(getBuilder(MailBuilder.class).withFrom("mail2from@m-net.de").withTo("mail2to@m-net.de")
                .withSubject("Subject Test neu").withText("sehr kurzer Text").withNumberOfTries(0).withSentAt(null)
                .withIsTextHTML(true)
                .addAttachment(getBuilder(MailAttachmentBuilder.class).withFileName("testfile2")
                                .withAttachmentFile(new byte[] { 102, 98, 76 })
                ).build());

        doReturn(mails).when(spiedMailService).findAllPendingMails();
        doNothing().when(spiedMailService).sendMailFailed(mails.get(0));
        when(mailSenderMock.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new MailSendException("Exception for test")).when(mailSenderMock).send(mimeMessage);

        // create and config job, execute method which processes mails
        spiedMailService.processPendingEmails();
        verifyMailServiceAndMailSender(mimeMessage, mails.get(0), mailSenderMock, spiedMailService, false);
        assertMimeMessage(mimeMessage, mails.get(0), true);
    }

    @Test
    public void testSendPendingEmailNotEnabled() throws Exception {
        when(mailSenderMock.isSendMail()).thenReturn(false);
        MimeMessage mimeMessage = new JavaMailSenderImpl().createMimeMessage();

        // create mails to send
        List<Mail> mails = new ArrayList<>();
        mails.add(getBuilder(MailBuilder.class).withFrom("mail2from@m-net.de").withTo("mail2to@m-net.de")
                .withSubject("Subject Test neu").withText("sehr kurzer Text").withNumberOfTries(0).withSentAt(null)
                .withIsTextHTML(true)
                .addAttachment(getBuilder(MailAttachmentBuilder.class).withFileName("testfile2")
                                .withAttachmentFile(new byte[] { 102, 98, 76 })
                ).build());

        doReturn(mails).when(spiedMailService).findAllPendingMails();
        doNothing().when(spiedMailService).sendMailFailed(mails.get(0));
        when(mailSenderMock.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new MailSendException("Exception for test")).when(mailSenderMock).send(mimeMessage);

        // create and config job, execute method which processes mails
        spiedMailService.processPendingEmails();
        //        verifyMailServiceAndMailSender(mimeMessage, mails.get(0), mailSenderMock, spiedMailService, false);
        Mail mail = mails.get(0);
        verify(spiedMailService, times(1)).findAllPendingMails();
        verify(spiedMailService, never()).sendMailSuccessful(mail);
        verify(spiedMailService, never()).sendMailFailed(mail);
        verify(mailSenderMock, times(1)).createMimeMessage();
        verify(mailSenderMock, never()).send(mimeMessage);
        assertMimeMessage(mimeMessage, mails.get(0), true);
    }


    @Test
    public void testSendPendingEmailSendLimitException() throws Exception {
        when(mailSenderMock.isSendMail()).thenReturn(true);
        MailSendException expectedException = new MailSendException("Initial - Exception for test");
        MimeMessage mimeMessage = new JavaMailSenderImpl().createMimeMessage();

        // create mails to send
        List<Mail> mails = new ArrayList<>();
        mails.add(getBuilder(MailBuilder.class).withFrom("mail2from@m-net.de").withTo("mail2to@m-net.de")
                .withSubject("Subject Test neu").withText("sehr kurzer Text").withNumberOfTries(0).withSentAt(null)
                .withIsTextHTML(true)
                .withNumberOfTries(5)
                .addAttachment(getBuilder(MailAttachmentBuilder.class).withFileName("testfile2")
                                .withAttachmentFile(new byte[] { 102, 98, 76 })
                ).build());

        doReturn(mails).when(spiedMailService).findAllPendingMails();
        doNothing().when(spiedMailService).sendMailFailed(mails.get(0));
        when(mailSenderMock.createMimeMessage()).thenReturn(mimeMessage);

        doThrow(expectedException).when(mailSenderMock).send(mimeMessage);

        // create and config job, execute method which processes mails
        try {
            spiedMailService.processPendingEmails();
        }
        catch (ProcessPendingEmailsException e) {
            verify(spiedMailService).sendMailFailed(mails.get(0));
            Exception nestedExp = e.getNestedExceptions().iterator().next();
            assertEquals(nestedExp, expectedException);
            assertTrue(e.getMessage().contains(expectedException.getMessage()));
            return;
        }
        assertTrue(false, "expected ProcessPendingEmailsException hasn't been thrown!");
    }

    @Test
    public void testSendPendingOtherException() throws Exception {
        when(mailSenderMock.isSendMail()).thenReturn(true);
        MimeMessage mimeMessage = new JavaMailSenderImpl().createMimeMessage();

        // create mails to send
        List<Mail> mails = new ArrayList<>();
        mails.add(getBuilder(MailBuilder.class)
                .withFrom("invalid?$.de")
                .withTo(null)
                .withSubject("Subject Test neu").withText("sehr kurzer Text").withNumberOfTries(0).withSentAt(null)
                .withIsTextHTML(true)
                .addAttachment(getBuilder(MailAttachmentBuilder.class).withFileName("testfile2")
                                .withAttachmentFile(new byte[] { 102, 98, 76 })
                ).build());

        doReturn(mails).when(spiedMailService).findAllPendingMails();
        when(mailSenderMock.createMimeMessage()).thenReturn(mimeMessage);

        // create and config job, execute method which processes mails
        try {
            spiedMailService.processPendingEmails();
        }
        catch (ProcessPendingEmailsException e) {
            verify(spiedMailService, never()).sendMailFailed(any(Mail.class));
            Exception nestedExp = e.getNestedExceptions().iterator().next();
            assertTrue(nestedExp instanceof NullPointerException);
            return;
        }
        assertTrue(false, "expected ProcessPendingEmailsException hasn't been thrown!");
    }

    private void verifyMailServiceAndMailSender(MimeMessage mimeMsg, Mail mail, JavaMailSender mailSenderSpy,
            MailService mailService, boolean success) throws Exception {
        verify(mailService, times(1)).findAllPendingMails();
        verify(mailService, times((success) ? 1 : 0)).sendMailSuccessful(mail);
        verify(mailService, times((success) ? 0 : 1)).sendMailFailed(mail);
        verify(mailSenderSpy, times(1)).createMimeMessage();
        verify(mailSenderSpy, times(1)).send(mimeMsg);
    }

    /**
     * ATTENTION: works just for one attachment
     */
    private void assertMimeMessage(MimeMessage mimeMessage, Mail mailData, boolean html) throws MessagingException, IOException {
        assertEquals(mailData.getIsTextHTML(), html);
        assertEquals(mimeMessage.getFrom()[0].toString(), mailData.getFrom());
        assertEquals(mimeMessage.getRecipients(Message.RecipientType.TO)[0].toString(), mailData.getTo());
        if (mailData.getCc() == null) {
            assertNull(mimeMessage.getRecipients(Message.RecipientType.CC));
        }
        else {
            assertEquals(mimeMessage.getRecipients(Message.RecipientType.CC).length, 1);
        }
        assertNull(mimeMessage.getRecipients(Message.RecipientType.BCC));
        assertEquals(mimeMessage.getSubject(), mailData.getSubject());
        assertEquals(mimeMessage.getContentType(), "text/plain");

        if (mimeMessage.getContent() instanceof MimeMultipart) {
            MimeMultipart mimeMultipart = ((MimeMultipart) mimeMessage.getContent());

            boolean attachmentChecked = false;
            boolean contentChecked = false;
            for (int idx = 0; idx < mimeMultipart.getCount(); idx++) {
                BodyPart bodyPart = mimeMultipart.getBodyPart(idx);
                if (bodyPart.getDisposition() == null) {
                    // do not know how to check for text
                    contentChecked = true;
                }
                else if (Part.ATTACHMENT.equals(bodyPart.getDisposition())) {
                    if (bodyPart.getContent() instanceof ByteArrayInputStream) {
                        assertEquals(IOUtils.toByteArray((ByteArrayInputStream) bodyPart.getContent()),
                                mailData.getMailAttachments().get(0).getAttachmentFile());
                    }
                    else {
                        fail("Type of MimeBodyPart.getContent() for MimeBodyPart.ATTACHMENT changed --> adjust test case");
                    }

                    assertEquals(bodyPart.getFileName(), mailData.getMailAttachments().get(0).getFileName());
                    attachmentChecked = true;
                }
            }

            if ((mailData.getText() != null) && (!contentChecked)) {
                fail("text not in mail");
            }
            if ((!mailData.getMailAttachments().isEmpty()) && (!attachmentChecked)) {
                fail("attachments not in mail");
            }
        }
        else {
            fail("Type of MimeMessage.getContent() changed --> adjust test case");
        }
    }

    private MailBuilder getMailBuilder() {
        return getBuilder(MailBuilder.class);
    }

    private MailAttachmentBuilder createMailAttachmentBuilder(String fileName, byte[] attachmentFile) {
        return getBuilder(MailAttachmentBuilder.class).withFileName(fileName).withAttachmentFile(attachmentFile);
    }

    private void assertAttachmentData(Object[][] attachments, Mail mail) {
        int idx = 0;
        for (MailAttachment mailAttachment : mail.getMailAttachments()) {
            assertNotNull(mailAttachment.getId());

            assertEquals(mailAttachment.getFileName(), attachments[idx][0]);
            assertEquals(mailAttachment.getAttachmentFile(), (byte[]) attachments[idx][1]);

            idx++;
        }
    }

    private void assertMailData(String from, String to, String subject, Object[][] attachments, Mail mail) {
        assertNotNull(mail.getId());
        assertEquals(mail.getFrom(), from);
        assertEquals(mail.getTo(), to);
        assertEquals(mail.getSubject(), subject);
        assertNotNull(mail.getCreatedAt());
        assertNotNull(mail.getCreatedBy());
        assertEquals(mail.getMailAttachments().size(), attachments.length);
    }

}
