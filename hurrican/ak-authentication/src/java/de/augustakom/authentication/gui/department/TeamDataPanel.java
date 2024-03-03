/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.06.2012 15:11:11
 */
package de.augustakom.authentication.gui.department;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.authentication.gui.basics.AbstractAuthenticationPanel;
import de.augustakom.authentication.gui.basics.SavePanel;
import de.augustakom.authentication.gui.exceptions.GUIException;
import de.augustakom.authentication.model.AKTeam;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJTextField;

/**
 *
 */
public class TeamDataPanel extends AbstractAuthenticationPanel implements SavePanel {

    private static final String TEAM_ID = "team.id";
    private static final String TEAM_NAME = "team.name";

    private AKJFormattedTextField tfId = null;
    private AKJTextField tfName = null;

    private AKTeam model = null;

    /**
     * Konstruktor fuer das Panel mit Angabe eines Team-Objekts.
     */
    public TeamDataPanel(AKTeam model) {
        super("de/augustakom/authentication/gui/department/resources/TeamDataPanel.xml");
        this.model = model;
        createGUI();
        read();
    }

    @Override
    protected final void createGUI() {
        AKJLabel lblId = getSwingFactory().createLabel(TEAM_ID);
        AKJLabel lblName = getSwingFactory().createLabel(TEAM_NAME);

        tfId = getSwingFactory().createFormattedTextField(TEAM_ID);
        tfId.setEditable(false);
        tfName = getSwingFactory().createTextField(TEAM_NAME);

        this.setLayout(new GridBagLayout());
        //@formatter:off
        this.add(new JPanel(), GBCFactory.createGBC(  0,   0, 0, 0, 1, 1, GridBagConstraints.NONE));
        this.add(lblId       , GBCFactory.createGBC(  0,   0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new JPanel(), GBCFactory.createGBC(  0,   0, 2, 1, 1, 1, GridBagConstraints.NONE));
        this.add(tfId        , GBCFactory.createGBC(100,   0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblName     , GBCFactory.createGBC(  0,   0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(tfName      , GBCFactory.createGBC(100,   0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new JPanel(), GBCFactory.createGBC(100, 100, 4, 3, 1, 1, GridBagConstraints.BOTH));
        //@formatter:on
    }

    /**
     * Liest die Model-Daten aus und stellt sie in den TextFields dar.
     */
    private void read() {
        if (model.getId() != null) {
            // Daten vom Modell anzeigen
            tfId.setValue(model.getId());
            tfName.setText(model.getName());
        }
    }

    @Override
    public void doSave() throws GUIException {
        try {
            setWaitCursor();

            model.setName(tfName.getText());

            validateModel(model);
        }
        finally {
            setDefaultCursor();
        }
    }

    /**
     * Validiert das AKTeam-Objekt. <br> Werden ungueltige Daten festgestellt, wird eine GUIException geworfen.
     */
    private void validateModel(AKTeam team) throws GUIException {
        if (StringUtils.isBlank(team.getName())) {
            throw new GUIException(GUIException.TEAM_VALIDATE_NAME);
        }
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }

}


