/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.02.2007 12:06:12
 */
package de.augustakom.common.gui.swing;

import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.tools.mail.IHurricanMailSender;
import de.augustakom.common.tools.validation.EMailValidator;


/**
 * Dialog, um eine eMail zu schreiben. <br> Wurde die eMail erfolgreich geschickt, wird ueber die setValue-Methode ein
 * Objekt vom Typ <code>MailMessage</code> uebergeben.
 *
 *
 */
public class SimpleMailDialog extends AKJAbstractOptionDialog {

    private AKJTextField tfSubjet = null;
    private AKJTextField tfTo = null;
    private AKJTextField tfCc = null;
    private AKJTextPane tpAttachments = null;
    private AKJTextPane tpBody = null;

    private String subject = null;
    private String[] to = null;
    private String[] cc = null;
    private String from = null;
    private String body = null;
    private File[] attachments = null;
    private IHurricanMailSender mailSender = null;

    /**
     * Default-Const.
     *
     * @param sender MailSender-Instanz, ueber die die Mail verschickt wird.
     */
    public SimpleMailDialog(IHurricanMailSender sender) {
        super("de/augustakom/common/gui/resources/SimpleMailDialog.xml");
        this.mailSender = sender;
        if (this.mailSender == null) {
            throw new IllegalArgumentException("Kein MailSender definiert!");
        }
        createGUI();
    }

    /**
     * Konstruktor mit Angabe verschiedener Daten.
     *
     * @param sender      MailSender-Instanz, ueber die die Mail verschickt wird.
     * @param subject     Subject fuer die Mail
     * @param to          Array mit den Empfaenger-Adressen
     * @param cc          Array mit den CC-Adressen
     * @param from        Absender-Adresse
     * @param body        Text fuer die Mail
     * @param attachments mit der Mail zu versendende Anhänge
     */
    public SimpleMailDialog(IHurricanMailSender sender, String subject, String[] to, String[] cc, String from, String body,
            File[] attachments) {
        super("de/augustakom/common/gui/resources/SimpleMailDialog.xml");
        this.mailSender = sender;
        if (this.mailSender == null) {
            throw new IllegalArgumentException("Kein MailSender definiert!");
        }

        this.subject = subject;
        this.to = (String[]) ArrayUtils.clone(to);
        this.cc = (String[]) ArrayUtils.clone(cc);
        this.from = from;
        this.body = body;
        this.attachments = (attachments != null)? attachments: new File[]{};
        createGUI();
        showValues();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));
        setIcon(getSwingFactory().getIcon("icon"));

        AKJLabel lblTo = getSwingFactory().createLabel("mail.to");
        AKJLabel lblCc = getSwingFactory().createLabel("mail.cc");
        AKJLabel lblSubject = getSwingFactory().createLabel("mail.subject");
        AKJLabel lblAttachments = getSwingFactory().createLabel("mail.attachments");

        tfTo = getSwingFactory().createTextField("mail.to");
        tfCc = getSwingFactory().createTextField("mail.cc");
        tfSubjet = getSwingFactory().createTextField("mail.subject");
        tpAttachments = getSwingFactory().createTextPane("mail.attachments");
        tpAttachments.setEnabled(false);
        AKJScrollPane spAttachments = new AKJScrollPane(tpAttachments, new Dimension(200, 70));
        tpBody = getSwingFactory().createTextPane("mail.body");
        AKJScrollPane spBody = new AKJScrollPane(tpBody, new Dimension(250, 140));

        AKJButton btnSend = getSwingFactory().createButton("send.mail", getActionListener());

        AKJPanel child = getChildPanel();
        // @formatter:off
        child.setLayout(new GridBagLayout());
        child.add(new AKJPanel(), GBCFactory.createGBC(  0,   0, 0, 0, 1, 1, GridBagConstraints.NONE));
        child.add(lblTo,          GBCFactory.createGBC(  0,   0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(  0,   0, 2, 1, 1, 1, GridBagConstraints.NONE));
        child.add(tfTo,           GBCFactory.createGBC(100,   0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblCc,          GBCFactory.createGBC(  0,   0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(tfCc,           GBCFactory.createGBC(100,   0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblSubject,     GBCFactory.createGBC(  0,   0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(tfSubjet,       GBCFactory.createGBC(100,   0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblAttachments, GBCFactory.createGBC(  0,   0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(spAttachments,  GBCFactory.createGBC(100,   0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(spBody,         GBCFactory.createGBC(100, 100, 1, 5, 3, 1, GridBagConstraints.BOTH));
        child.add(btnSend,        GBCFactory.createGBC(  0,   0, 1, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on

        this.add(child);
    }

    private String getAttachmentsFormatted() {
        StringBuilder attachmentNames = new StringBuilder();
        for (File attachment : attachments) {
            if (attachmentNames.length() > 0) {
                attachmentNames.append("\n");
            }
            attachmentNames.append(attachment.getName());
        }
        return attachmentNames.toString();
    }

    /* Zeigt die uebergebenen Daten an */
    private void showValues() {
        tfTo.setText((to != null) ? StringUtils.join(to, ",") : "");
        tfCc.setText((cc != null) ? StringUtils.join(cc, ",") : "");
        tfSubjet.setText(subject);
        tpAttachments.setText(getAttachmentsFormatted());
        tpBody.setText(body);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("send.mail".equals(command)) {
            try {
                String[] eMailTo = StringUtils.splitPreserveAllTokens(tfTo.getText(), ",");
                if (!EMailValidator.getInstance().areValid(eMailTo)) {
                    throw new Exception("EMail receiver are invalid!");
                }

                String[] eMailCc = StringUtils.splitPreserveAllTokens(tfCc.getText(), ",");
                if (!EMailValidator.getInstance().areValid(eMailCc)) {
                    throw new Exception("EMail receiver Cc are invalid!");
                }

                List<IHurricanMailSender.AttachmentData> attachmentDatas = new ArrayList<>(attachments.length);
                for (File attachment : attachments) {
                    attachmentDatas.add(new IHurricanMailSender.AttachmentData(Files.readAllBytes(attachment.toPath()),
                            attachment.getName()));
                }
                IHurricanMailSender.MailData mailData = new IHurricanMailSender.MailData(from, eMailTo, eMailCc,
                        tfSubjet.getText(), attachmentDatas, tpBody.getText());

                mailSender.send(mailData);

                prepare4Close();
                setValue(mailData);
            }
            catch (Exception e) {
                MessageHelper.showErrorDialog(this, e);
            }
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
        // intentionally left blank
    }

}


