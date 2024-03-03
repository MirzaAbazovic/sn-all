/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.04.2005 16:46:04
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJRadioButton;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.Produkt;

/**
 * Dialog zur Selektion eines Produkts. <br> Die ID des gewaehlten Produkts wird ueber die Methode setValue gesetzt.
 *
 *
 */
public class ProduktSelectionDialog extends AbstractServiceOptionDialog {

    private List<Produkt> produkte = null;
    private Map<String, Produkt> produktMap = null;
    private List<AKJRadioButton> buttonList = null;

    /**
     * Konstruktor mit den zur Auswahl stehenden Produkte.
     *
     * @param produkte
     */
    public ProduktSelectionDialog(List<Produkt> produkte) {
        super(null);
        this.produkte = produkte;
        produktMap = new HashMap<String, Produkt>();
        CollectionMapConverter.convert2Map(this.produkte, produktMap, "getBezeichnungShort", null);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    protected final void createGUI() {
        configureButton(CMD_CANCEL, null, null, false, false);
        configureButton(CMD_SAVE, "Ok", "Übernimmt als Bündel-Produkt das gewählte Produkt", true, true);

        setTitle("Produkt des Bündel-Auftrags");
        buttonList = new ArrayList<AKJRadioButton>();

        AKJLabel lblTitle = new AKJLabel("Bitte zugehöriges Bündel-Produkt wählen:");

        ButtonGroup group = new ButtonGroup();
        AKJPanel child = getChildPanel();
        child.setLayout(new GridBagLayout());
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        child.add(lblTitle, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        int y = 1;
        for (Produkt p : produkte) {
            AKJRadioButton rb = new AKJRadioButton(p.getBezeichnungShort());
            rb.setToolTipText(p.getBeschreibung());
            group.add(rb);
            buttonList.add(rb);

            child.add(rb, GBCFactory.createGBC(0, 0, 1, ++y, 1, 1, GridBagConstraints.HORIZONTAL));
        }
        child.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 2, ++y, 1, 1, GridBagConstraints.BOTH));
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    protected void doSave() {
        for (AKJRadioButton rb : buttonList) {
            if (rb.isSelected()) {
                Produkt p = produktMap.get(rb.getText());

                prepare4Close();
                setValue(p.getId());
            }
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#validateSaveButton()
     */
    protected void validateSaveButton() {
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    protected void execute(String command) {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
    }

}

