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
 * Dialog, der eine Datumskomponente darstellt. <br> Der Titel des Dialogs und eine Ueberschrift fuer die
 * Datumskomponente koennen definiert werden.
 *
 *
 */
public class AKDateSelectionDialog extends AKJAbstractOptionDialog {
    public static final Object OK_OPTION = new Object();
    public static final Object CANCEL_OPTION = null;

    private final String subTitle;
    private final String dcLabel;
    private final boolean optional;

    private AKJDateComponent dcDate = null;

    /**
     * Konstruktor mit Angabe des Titels und Sub-Titels sowie dem Text fuer das Label der Date-Komponente.
     */
    public AKDateSelectionDialog(String title, String subTitle, String dcLabel) {
        this(title, subTitle, dcLabel, false);
    }

    /**
     * Konstruktor mit Angabe des Titels und Sub-Titels sowie dem Text fuer das Label der Date-Komponente und einer
     * Angabe, ob die Datumseingabe optional ist
     */
    public AKDateSelectionDialog(String title, String subTitle, String dcLabel, boolean optional) {
        super("de/augustakom/common/gui/resources/AKDateSelectionDialog.xml", false);
        setTitle(title);
        this.optional = optional;
        this.subTitle = subTitle;
        this.dcLabel = dcLabel;
        createGUI();
    }


    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setIconURL("de/augustakom/common/gui/images/datepicker.gif");
        AKJLabel lblSubTitle = getSwingFactory().createLabel("sub.title");
        if (StringUtils.isNotBlank(subTitle)) {
            lblSubTitle.setText(subTitle);
        }

        AKJLabel lblDate = getSwingFactory().createLabel("date");
        if (StringUtils.isNotBlank(dcLabel)) {
            lblDate.setText(dcLabel);
        }

        dcDate = getSwingFactory().createDateComponent("date");
        AKJButton btnOk = getSwingFactory().createButton("ok", getActionListener());
        AKJButton btnCancel = getSwingFactory().createButton("cancel", getActionListener());

        AKJPanel child = new AKJPanel(new GridBagLayout());
        child.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        child.add(lblSubTitle, GBCFactory.createGBC(100, 0, 1, 1, 5, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.NONE));
        child.add(lblDate, GBCFactory.createGBC(0, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 3, 3, 1, 1, GridBagConstraints.NONE));
        child.add(dcDate, GBCFactory.createGBC(100, 0, 4, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 5, 3, 1, 1, GridBagConstraints.NONE));
        child.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 5, 4, 1, 1, GridBagConstraints.BOTH));

        AKJPanel btnPnl = new AKJPanel(new GridBagLayout());
        btnPnl.add(btnOk, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnCancel, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(child, BorderLayout.CENTER);
        getChildPanel().add(btnPnl, BorderLayout.SOUTH);
    }

    /**
     * Uebergibt dem Dialog das Datum, das angezeigt werden soll.
     *
     * @param dateToShow Datum fuer die Anzeige.
     */
    public void showDate(Date dateToShow) {
        dcDate.setDate(dateToShow);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("ok".equals(command)) {
            Object selection = dcDate.getDate(null);
            if ((selection == null) && !optional) {
                MessageHelper.showMessageDialog(this, "Bitte tragen Sie ein gültiges Datum ein.", "Ungültiges Datum",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            else if ((selection == null) && optional) {
                selection = OK_OPTION;
            }
            prepare4Close();
            setValue(selection);
        }
        else if ("cancel".equals(command)) {
            prepare4Close();
            setValue(CANCEL_OPTION);
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }
}


