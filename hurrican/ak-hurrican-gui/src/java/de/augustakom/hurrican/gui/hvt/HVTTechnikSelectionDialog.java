/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.04.2005 12:28:30
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJRadioButton;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.exceptions.NoDataFoundException;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.service.cc.HVTService;


/**
 * Dialog zur Auswahl der HVT-Technik. <br> Die gewaehlte HVT-Technik wird gespeichert und kann vom Aufrufer ueber die
 * Methode <code>getValue</code> abgefragt werden.
 *
 *
 */
public class HVTTechnikSelectionDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(HVTTechnikSelectionDialog.class);
    private static final long serialVersionUID = -7214155345374478241L;

    private List<HVTTechnik> hvtTechniken = null;
    private List<AKJRadioButton> buttonList = null;

    /**
     * Default-Konstruktor.
     */
    public HVTTechnikSelectionDialog() throws NoDataFoundException {
        super("de/augustakom/hurrican/gui/hvt/resources/HVTTechnikSelectionDialog.xml");
        loadData();
        createGUI();
    }

    @Override
    protected final void createGUI() {
        setTitle("HVT-Technik auswählen");
        configureButton(CMD_SAVE, "Auswählen", "Verwendet die ausgewählte HVT-Technik", true, true);

        AKJLabel lblHeader = getSwingFactory().createLabel("header");
        AKJLabel lblHeader2 = getSwingFactory().createLabel("header.2");

        buttonList = new ArrayList<>();
        ButtonGroup btnGroup = new ButtonGroup();

        AKJPanel child = getChildPanel();
        child.setLayout(new GridBagLayout());
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        child.add(lblHeader, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblHeader2, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));

        int y = 2;
        for (HVTTechnik technik : hvtTechniken) {
            AKJRadioButton rbTechnik = new AKJRadioButton();
            rbTechnik.setValueObject(technik);
            rbTechnik.setText(technik.getHersteller());
            btnGroup.add(rbTechnik);
            buttonList.add(rbTechnik);

            child.add(rbTechnik, GBCFactory.createGBC(0, 0, 1, ++y, 1, 1, GridBagConstraints.HORIZONTAL));
        }
        child.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 2, ++y, 1, 1, GridBagConstraints.BOTH));
    }

    @Override
    public void loadData() throws NoDataFoundException {
        try {
            HVTService hts = getCCService(HVTService.class);
            hvtTechniken = hts.findHVTTechniken();

            if ((hvtTechniken == null) || (hvtTechniken.isEmpty())) {
                throw new NoDataFoundException("HVT-Techniken konnten nicht ermittelt werden.");
            }
        }
        catch (NoDataFoundException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new NoDataFoundException("Bei der Ermittlung der HVT-Techniken trat ein Fehler auf: " + e.getMessage(), e);
        }
    }

    @Override
    protected void doSave() {
        HVTTechnik selection = null;
        for (AKJRadioButton rb : buttonList) {
            if (rb.isSelected() && (rb.getValueObject() instanceof HVTTechnik)) {
                selection = (HVTTechnik) rb.getValueObject();
                break;
            }
        }

        if (selection != null) {
            prepare4Close();
            setValue(selection);
        }
        else {
            MessageHelper.showInfoDialog(getMainFrame(),
                    "Bitte wählen Sie zuerst eine HVT-Technik aus.", null, true);
        }
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }

}


