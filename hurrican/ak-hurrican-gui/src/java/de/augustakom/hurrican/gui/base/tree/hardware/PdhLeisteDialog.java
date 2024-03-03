/**
  * Copyright (c) 2010 - M-net Telekommunikations GmbH
  * All rights reserved.
  * -------------------------------------------------------
  * File created: 15.02.2010 18:53:47
  */
package de.augustakom.hurrican.gui.base.tree.hardware;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * Dialog zum Anlegen einer PDH-Leiste
 */
public class PdhLeisteDialog extends AbstractServiceOptionDialog {

    private static final Logger LOGGER = Logger.getLogger(PdhLeisteDialog.class);

    private AKJFormattedTextField ftfLeiste = null;
    private AKJFormattedTextField ftfStifte = null;

    private final HVTStandort hvtStandort;
    private String leiste = null;
    private Integer stifte = null;

    private final RangierungsService rangierungsService;

    /**
     * @param hvtStandort zugehöriger HvtStandort (not null)
     * @throws ServiceNotFoundException falls RangierungsService nicht gefunden
     */
    public PdhLeisteDialog(HVTStandort hvtStandort) throws ServiceNotFoundException {
        super("de/augustakom/hurrican/gui/base/tree/hardware/resources/PdhLeisteDialog.xml");
        if (hvtStandort == null) {
            throw new IllegalArgumentException("Kein HvtStandort übergeben");
        }
        this.hvtStandort = hvtStandort;

        this.rangierungsService = getCCService(RangierungsService.class);

        createGUI();
    }

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));

        AKJLabel lblLeiste = getSwingFactory().createLabel("leiste");
        ftfLeiste = getSwingFactory().createFormattedTextField("leiste");

        AKJLabel lblStifte = getSwingFactory().createLabel("stifte");
        ftfStifte = getSwingFactory().createFormattedTextField("stifte");

        AKJPanel child = new AKJPanel(new GridBagLayout());
        child.add(lblLeiste, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        child.add(ftfLeiste, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblStifte, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(ftfStifte, GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(child, BorderLayout.CENTER);
    }

    @Override
    protected void doSave() {
        try {
            retrieveFieldValues();

            Long sessionId = HurricanSystemRegistry.instance().getSessionId();
            rangierungsService.createPdhLeisten(hvtStandort, leiste, stifte, sessionId);

            prepare4Close();
            setValue(JOptionPane.OK_OPTION);

            MessageHelper.showInfoDialog(this, "PDH-Leiste {0} mit {1} Stiften erfolgreich angelegt!",
                    leiste, stifte);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    /**
     * Validiere die Eingaben und wirf Exception mit der Meldung, falls nicht ok
     */
    private void validateFieldValues() throws HurricanGUIException {

        String leisteStr = ftfLeiste.getText();
        if (StringUtils.isBlank(leisteStr)) {
            throw new HurricanGUIException("Bitte geben Sie eine Leiste an.");
        }
        else {
            try {
                if (!rangierungsService.findEQByLeiste(hvtStandort.getId(), leisteStr).isEmpty()) {
                    throw new HurricanGUIException(
                            "Es existiert bereits eine Leiste '" + leisteStr + "' für diesen HVT.");
                }
            }
            catch (FindException e) {
                throw new HurricanGUIException(e);
            }
        }

        String stifteStr = ftfStifte.getText();
        if (StringUtils.isBlank(stifteStr)) {
            throw new HurricanGUIException("Bitte geben Sie die Anzahl der Stifte an.");
        }
        else if (!StringUtils.isNumeric(stifteStr)) {
            throw new HurricanGUIException(
                    "Bitte geben Sie eine gültige Zahl für die Anzahl der Stifte an.");
        }
    }

    /**
     * Überträgt die im Dialog eingegebenen Daten in das Objekt <code>leiste<code>
     */
    private void retrieveFieldValues() throws HurricanGUIException {
        validateFieldValues();

        leiste = ftfLeiste.getText();
        stifte = Integer.valueOf(ftfStifte.getText());
    }
}
