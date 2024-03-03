/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.02.2010 08:20:37
 */
package de.augustakom.hurrican.gui.tools.physik;

import java.awt.*;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.service.cc.EndgeraeteService;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * Dialog, um die Optionen festzulegen, wie eine bestehende Rangierung aufzubrechen ist.
 *
 *
 */
public class BreakRangierungDialog extends AbstractServiceOptionDialog {

    private static final String RESOURCE = "de/augustakom/hurrican/gui/tools/physik/resources/BreakRangierungDialog.xml";
    private static final String EQ_OUT_ADD = "eq.out.add";
    private static final String EQ_IN_ADD = "eq.in.add";
    private static final String EQ_OUT = "eq.out";
    private static final String EQ_IN = "eq.in";
    private static final String HEADER = "header";
    private static final String TITLE = "title";

    private static final Logger LOGGER = Logger.getLogger(BreakRangierungDialog.class);

    private AKJCheckBox chbEqIn = null;
    private AKJCheckBox chbEqOut = null;
    private AKJCheckBox chbEqIn2 = null;
    private AKJCheckBox chbEqOut2 = null;

    private final Rangierung rangierungDefault;
    private final Rangierung rangierungAdd;

    /**
     * @param rangierungDefault
     * @param rangierungAdd
     */
    public BreakRangierungDialog(Rangierung rangierungDefault, Rangierung rangierungAdd) {
        super(RESOURCE);
        this.rangierungDefault = rangierungDefault;
        this.rangierungAdd = rangierungAdd;
        createGUI();
    }

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText(TITLE));

        AKJLabel lblHeader = getSwingFactory().createLabel(HEADER, AKJLabel.LEFT, Font.BOLD);
        AKJLabel lblEqIn = getSwingFactory().createLabel(EQ_IN);
        AKJLabel lblEqOut = getSwingFactory().createLabel(EQ_OUT);
        AKJLabel lblEqIn2 = getSwingFactory().createLabel(EQ_IN_ADD);
        AKJLabel lblEqOut2 = getSwingFactory().createLabel(EQ_OUT_ADD);

        chbEqIn = getSwingFactory().createCheckBox(EQ_IN, rangierungDefault != null);
        chbEqOut = getSwingFactory().createCheckBox(EQ_OUT, rangierungDefault != null);
        chbEqIn2 = getSwingFactory().createCheckBox(EQ_IN_ADD, rangierungAdd != null);
        chbEqOut2 = getSwingFactory().createCheckBox(EQ_OUT_ADD, rangierungAdd != null);

        AKJPanel panel = new AKJPanel(new GridBagLayout());
        panel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        panel.add(lblHeader, GBCFactory.createGBC(100, 0, 1, 1, 3, 1, GridBagConstraints.HORIZONTAL));
        panel.add(lblEqIn, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 2, 1, 1, GridBagConstraints.NONE));
        panel.add(chbEqIn, GBCFactory.createGBC(0, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(lblEqOut, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(chbEqOut, GBCFactory.createGBC(0, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(lblEqIn2, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(chbEqIn2, GBCFactory.createGBC(0, 0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(lblEqOut2, GBCFactory.createGBC(0, 0, 1, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(chbEqOut2, GBCFactory.createGBC(0, 0, 3, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 4, 6, 1, 1, GridBagConstraints.BOTH));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(panel, BorderLayout.CENTER);
    }

    private boolean doBreak() {
        int option = MessageHelper.showYesNoQuestion(getMainFrame(),
                "Sollen die gewählten Ports wirklich entfernt werden?",
                "Ports entfernen?");
        return (option == OK_OPTION) ? true : false;
    }

    @Override
    protected void doSave() {
        try {
            if (chbEqIn.isSelected() && chbEqOut.isSelected() && chbEqIn2.isSelected() && chbEqOut2.isSelected()) {
                throw new HurricanGUIException("Es dürfen nicht alle Ports entfernt werden!");
            }

            if (doBreak()) {
                RangierungsService rangierungsService = getCCService(RangierungsService.class);
                EndgeraeteService endgeraeteService = getCCService(EndgeraeteService.class);
                if (rangierungDefault != null) {
                    Rangierung rangierung = rangierungsService.breakRangierung(rangierungDefault, chbEqIn.isSelected(), chbEqOut.isSelected(), true);
                    rangierungsService.reAttachAccessPoint(rangierung, true);
                    endgeraeteService.updateSchicht2Protokoll4Rangierungen(Arrays.asList(rangierung));
                }

                if (rangierungAdd != null) {
                    Rangierung rangierung = rangierungsService.breakRangierung(rangierungAdd, chbEqIn2.isSelected(), chbEqOut2.isSelected(), true);
                    rangierungsService.reAttachAccessPoint(rangierung, false);
                }

                prepare4Close();
                setValue(OK_OPTION);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable o, Object arg) {
        // TODO BreakRangierungDialog.update

    }

}


