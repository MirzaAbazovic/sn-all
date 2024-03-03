/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.06.2010
 */
package de.augustakom.hurrican.gui.shared;

import java.awt.*;
import java.util.*;

import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.CCAddress;

/**
 * Dialog fuer die Anzeige aller {@link Kunde}n und {@link Auftrag}e, die einer best. {@link CCAddress} zugeordnet sind
 */
public class KundenAuftraege4AddressDialog extends AbstractServiceOptionDialog {

    private final Long addressId;

    /**
     * @param addressId die Id einer {@link CCAddress} zu der die Kunden und Aufträge angezeigt werden sollen
     */
    public KundenAuftraege4AddressDialog(Long addressId) {
        super(null);
        this.addressId = addressId;

        createGUI();
    }

    @Override
    protected final void createGUI() {
        setTitle((addressId == null) ? "" : "Kunden und Aufträge zu der Adresse " + addressId);

        configureButton(CMD_SAVE, "OK", "Schliesst den Dialog", true, true);
        configureButton(CMD_CANCEL, null, null, false, false);

        KundenAuftraege4AddressPanel kundenAuftraege4AddressPanel = new KundenAuftraege4AddressPanel(addressId);

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(kundenAuftraege4AddressPanel, BorderLayout.CENTER);
    }

    @Override
    protected void validateSaveButton() {
        // Save-Button nicht validieren!
    }

    @Override
    protected void doSave() {
        prepare4Close();
        setValue(Integer.valueOf(OK_OPTION));
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }
}
