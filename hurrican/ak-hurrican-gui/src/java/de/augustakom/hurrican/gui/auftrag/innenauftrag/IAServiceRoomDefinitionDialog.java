/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.02.2007 13:57:55
 */
package de.augustakom.hurrican.gui.auftrag.innenauftrag;

import java.awt.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.base.HurricanGUIException;


/**
 * Dialog dient zur Definition der Innenauftragsnummer. <br> Die definierte IA-Nummer wird ueber die Methode
 * <code>setValue</code> gespeichert.
 *
 *
 */
public class IAServiceRoomDefinitionDialog extends IADefinitionDialog {

    private static final Logger LOGGER = Logger.getLogger(IAServiceRoomDefinitionDialog.class);

    /**
     * Konstruktor mit Angabe der zu verwendenden fortlaufenden Nummer.
     *
     * @param number
     */
    public IAServiceRoomDefinitionDialog(String number) {
        super(number);
    }

    public IAServiceRoomDefinitionDialog(String number, String kostenstelle) {
        super(number, kostenstelle);
    }

    protected final void createGUI() {
        super.createGUI();

        child.add(cbServiceRoom, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE, ins));
        child.add(cbEquipment, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE, ins));
        child.add(tfNumber, GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        tfNumber.setEnabled(false);
        tfNumber.setText(number);
    }

    protected String buildIaNumber() {
        StringBuilder iaNumber = new StringBuilder();
        iaNumber.append((String) cbPrefix.getSelectedItemValue("getStrValue", String.class));
        iaNumber.append((String) cbServiceRoom.getSelectedItemValue("getStrValue", String.class));
        iaNumber.append((String) cbEquipment.getSelectedItemValue("getStrValue", String.class));
        iaNumber.append(tfNumber.getText());
        return iaNumber.toString();
    }

    protected boolean validateDialog() {
        try {
            if (null == tfNumber || null == tfNumber.getText() || tfNumber.getText().length() == 0) {
                throw new HurricanGUIException("Keine fortlaufende Nummer definiert.");
            }

            if (null != tfNumber && null != tfNumber.getText() && tfNumber.getText().length() != 8) {
                throw new HurricanGUIException("Länge der fortlaufenden Nummer nicht richtig.");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showInfoDialog(this, e.getMessage(), null, true);
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}
