/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.12.2004 07:45:47
 */
package de.augustakom.common.gui.swing;

import java.awt.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;

import org.apache.commons.lang.StringUtils;

import de.augustakom.common.gui.awt.GBCFactory;

/**
 * Dialog, ueber den ein groesserer Text eingegeben werden kann. <br> Der Titel des Dialogs und eine Ueberschrift fuer
 * die {@link AKJTextArea} koennen frei definiert werden.
 */
public class TextInputDialog extends AKJAbstractOptionDialog {

    private static final long serialVersionUID = 3064330231501766376L;

    public static final String OK = "ok";
    public static final String CANCEL = "cancel";

    private final int maxChars;

    private String subTitle = null;
    private String txtLabel = null;

    private AKJTextArea taText = null;

    /**
     * Konstruktor mit Angabe des Titels und Sub-Titels sowie dem Text fuer das Label der Text-Komponente.
     */
    public TextInputDialog(String title, String subTitle, String txtLabel) {
        this(title, subTitle, txtLabel, -1);
    }

    /**
     * Konstruktor mit Angabe des Titels und Sub-Titels, dem Text fuer das Label der Text-Komponente und die Anzahl der
     * maximal zulaessigen Zeichen der Text-Komponente.
     */
    public TextInputDialog(String title, String subTitle, String txtLabel, int maxChars) {
        super("de/augustakom/common/gui/resources/AKTextInputDialog.xml", false);
        setTitle(title);
        this.subTitle = subTitle;
        this.txtLabel = txtLabel;
        this.maxChars = maxChars;
        createGUI();
    }

    @Override
    protected final void createGUI() {
        AKJLabel lblSubTitle = getSwingFactory().createLabel("sub.title");
        if (StringUtils.isNotBlank(subTitle)) {
            lblSubTitle.setText(subTitle);
        }

        AKJLabel lblText = getSwingFactory().createLabel("text");
        if (StringUtils.isNotBlank(txtLabel)) {
            lblText.setText(txtLabel);
        }

        taText = getSwingFactory().createTextArea("text", true, true, true);
        if (maxChars != -1) {
            taText.setMaxChars(maxChars);
        }
        AKJScrollPane spText = new AKJScrollPane(taText, new Dimension(150, 60));
        AKJButton btnOk = getSwingFactory().createButton("ok", getActionListener());
        AKJButton btnCancel = getSwingFactory().createButton("cancel", getActionListener());

        AKJPanel child = new AKJPanel(new GridBagLayout());
        child.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        // @formatter:off
        child.add(new AKJPanel(), GBCFactory.createGBC(  0,   0, 0, 0, 1, 1, GridBagConstraints.NONE));
        child.add(lblSubTitle,    GBCFactory.createGBC(100,   0, 1, 1, 5, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(  0,   0, 1, 2, 1, 1, GridBagConstraints.NONE));
        child.add(lblText,        GBCFactory.createGBC(  0,   0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(  0,   0, 3, 3, 1, 1, GridBagConstraints.NONE));
        child.add(spText,         GBCFactory.createGBC(  0,   0, 4, 3, 1, 3, GridBagConstraints.BOTH));
        child.add(new AKJPanel(), GBCFactory.createGBC(  0,   0, 5, 3, 1, 1, GridBagConstraints.NONE));
        child.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 5, 6, 1, 1, GridBagConstraints.BOTH));
        // @formatter:on

        AKJPanel btnPnl = new AKJPanel(new GridBagLayout());
        // @formatter:off
        btnPnl.add(btnOk,          GBCFactory.createGBC(  0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnCancel,      GBCFactory.createGBC(  0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(child, BorderLayout.CENTER);
        getChildPanel().add(btnPnl, BorderLayout.SOUTH);
    }

    /**
     * Uebergibt dem Dialog das Datum, das angezeigt werden soll.
     *
     * @param txtToShow Datum fuer die Anzeige.
     */
    public void showText(String txtToShow) {
        taText.setText(txtToShow);
    }

    @Override
    protected void execute(String command) {
        if (OK.equals(command)) {
    prepare4Close();
            setValue(taText.getText());
        }
        else if (CANCEL.equals(command)) {
            prepare4Close();
            setValue(null);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        // not used
    }
}
