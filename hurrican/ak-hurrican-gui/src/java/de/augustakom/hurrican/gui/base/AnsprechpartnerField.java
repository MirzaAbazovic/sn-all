/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.10.2009 16:49:35
 */
package de.augustakom.hurrican.gui.base;

import java.awt.*;
import java.util.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.gui.swing.AKJAbstractPanel;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.shared.CCAnsprechpartnerDialog;
import de.augustakom.hurrican.model.cc.Ansprechpartner;


/**
 * Klasse zur Darstellung eines Ansprechpartners. Das Panel besitzt ein (nicht editierbares) Text-Field sowie einen
 * Button. Der Button oeffnet den Ansprechpartner-Dialog, sofern ein Ansprechpartner vorhanden ist.
 *
 *
 */
public class AnsprechpartnerField extends AKJAbstractPanel {

    private static final String CONTACT = "contact";
    private static final String OPEN = "open";

    private static final String RESOURCE = "de/augustakom/hurrican/gui/base/resources/AnsprechpartnerField.xml";
    private static final Dimension SEARCH_BUTTON_DIMENSION = new Dimension(22, 22);

    // GUI-Elemente
    private AKJTextField tfContact;
    private AKJButton btnOpen;

    private int maxContactLength = -1;

    // Modell
    private Ansprechpartner ansprechpartner;

    /**
     * Default-Const.
     */
    public AnsprechpartnerField() {
        super(RESOURCE);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    public final void createGUI() {
        tfContact = getSwingFactory().createTextField(CONTACT, false);
        btnOpen = getSwingFactory().createButton(OPEN, getActionListener());
        btnOpen.setPreferredSize(SEARCH_BUTTON_DIMENSION);
        btnOpen.setEnabled(false);

        this.setLayout(new BorderLayout());
        this.add(tfContact, BorderLayout.CENTER);
        this.add(btnOpen, BorderLayout.EAST);
    }

    /**
     * Angabe des Ansprechpartners, der in dem Feld dargestellt werden soll
     *
     * @param ansprechpartner
     */
    public void setAnsprechpartner(Ansprechpartner ansprechpartner) {
        this.ansprechpartner = ansprechpartner;
        GuiTools.cleanFields(this);
        if (this.ansprechpartner != null) {
            btnOpen.setEnabled(true);
            StringBuilder text = new StringBuilder();
            if (maxContactLength > 0) {
                text.append(StringUtils.abbreviate(ansprechpartner.getDisplayText(), maxContactLength));
            }
            else {
                text.append(ansprechpartner.getDisplayText());
            }
            tfContact.setText(text.toString());
        }
    }

    @Override
    protected void execute(String command) {
        if (OPEN.equals(command)) {
            CCAnsprechpartnerDialog dlg = new CCAnsprechpartnerDialog(ansprechpartner);
            Object result = DialogHelper.showDialog(HurricanSystemRegistry.instance().getMainFrame(), dlg, true, true);
            if (result instanceof Ansprechpartner) {
                setAnsprechpartner((Ansprechpartner) result);
            }
        }
    }

    @Override
    public void update(Observable arg0, Object arg1) {
    }

    /**
     * Definiert die maximal Laenge der darzustellenden Zeichen im TextField.
     *
     * @param maxContactLength
     */
    public void setMaxContactLength(int maxContactLength) {
        this.maxContactLength = maxContactLength;
    }

}


