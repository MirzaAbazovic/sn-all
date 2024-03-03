/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.09.2011 16:57:25
 */
package de.augustakom.hurrican.gui.base.ip;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJRadioButton;
import de.augustakom.common.gui.swing.SwingFactory;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.model.cc.AddressTypeEnum;
import de.augustakom.hurrican.model.cc.IPAddress;

/**
 * Panel auf dem der Typ der gewuenschten IP-Adresse ausgewaehlt werden kann.
 *
 *
 * @since Release 10
 */
class ProtocolVersionSelectionPanel extends AKJPanel {

    private final ButtonGroup bgProtocolVersion;

    public ProtocolVersionSelectionPanel(String titleKey, ActionListener actionListener, SwingFactory swingFactory,
            List<AddressTypeEnum> allowedIpTypes) {
        bgProtocolVersion = new ButtonGroup();
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder(swingFactory.getText(titleKey)));

        int row = 0;
        for (AddressTypeEnum addressType : AddressTypeEnum.values()) {
            if (addressType != AddressTypeEnum.NOT_VALID) {
                AKJRadioButton btn = swingFactory.createRadioButton(addressType.name(), actionListener, true,
                        bgProtocolVersion);
                addIpTypeMnemonic(addressType, btn);
                add(btn, GBCFactory.createGBC(0, 0, 1, row++, 1, 1, GridBagConstraints.HORIZONTAL));
                if (CollectionTools.isNotEmpty(allowedIpTypes) && !allowedIpTypes.contains(addressType)) {
                    btn.setEnabled(false);
                }
            }
        }
        setDefaultSelection();
    }

    private void addIpTypeMnemonic(AddressTypeEnum addressType, AKJRadioButton radioButton) {
        radioButton.setText(String.format("%s (%s)", radioButton.getText(), addressType.mnemonic));
    }

    /**
     * Default Auswahl setzen. Sucht nach dem ersten aktiven Typen. Wenn keiner aktiv ist, bleibt der erste der gesamten
     * Liste selektiert
     */
    private void setDefaultSelection() {
        for (Enumeration<AbstractButton> e = bgProtocolVersion.getElements(); e.hasMoreElements(); ) {
            AbstractButton button = e.nextElement();
            if (button.isEnabled()) {
                button.setSelected(true);
                break;
            }
        }
    }

    /**
     * Liefert den {@link IPAddress.AddressType} fuer das selektierte {@link ButtonModel} des Panels.
     */
    AddressTypeEnum getSelectedAddressType() {
        return AddressTypeEnum.valueOf(bgProtocolVersion.getSelection().getActionCommand());
    }

    /**
     * Liefert {@code true} wenn der selektierte Adresstyp aktiv ist, andernfalls {@code false}
     */
    boolean isSelectedAddressTypeEnabled() {
        return bgProtocolVersion.getSelection().isEnabled();
    }

    /**
     * Selektiert eine ProtocolVersion.
     *
     * @param selection
     * @return Wird einen nicht gültige (= deaktivierte) Version selektiert, so liefert diese Funktion false zurück.
     */
    boolean setSelection(String selection) {
        for (Enumeration<AbstractButton> e = bgProtocolVersion.getElements(); e.hasMoreElements(); ) {
            AbstractButton button = e.nextElement();
            if (button.getActionCommand().equals(selection)) {
                button.setSelected(true);
                return button.isEnabled();
            }
        }
        return true;
    }

    public boolean requiresPrefix() {
        return ((getSelectedAddressType() == AddressTypeEnum.IPV6_relative)
                || (getSelectedAddressType() == AddressTypeEnum.IPV6_relative_eui64));
    }
}

