/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.08.2004 10:03:15
 */
package de.augustakom.common.gui.swing.wizard;

import java.awt.*;
import javax.swing.*;

import de.augustakom.common.gui.swing.AKJPanel;

/**
 * Button-Panel fuer einen Wizard.
 */

public class AKJWizardButtonPanel extends AKJPanel {

    JLabel statusLabel = new JLabel();

    public AKJWizardButtonPanel(AKJWizardComponents wizardComponents) {
        this.setLayout(new GridBagLayout());
        this.add(statusLabel, new GridBagConstraints(0, 0, 1, 1, 0.7, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 0, 2, 0), 0, 0));
        this.add(wizardComponents.getBackButton(), new GridBagConstraints(1, 0, 1, 1, 0.1, 0.0
                , GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(2, 0, 2, 0), 0, 0));
        this.add(wizardComponents.getNextButton(), new GridBagConstraints(2, 0, 1, 1, 0.1, 0.0
                , GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(2, 0, 2, 0), 0, 0));
        this.add(wizardComponents.getFinishButton(), new GridBagConstraints(3, 0, 1, 1, 0.1, 0.0
                , GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(2, 0, 2, 0), 0, 0));
        this.add(wizardComponents.getCancelButton(), new GridBagConstraints(4, 0, 1, 1, 0.1, 0.0
                , GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(2, 3, 2, 2), 0, 0));

    }

}
