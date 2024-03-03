/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.08.2011 17:55:46
 */
package de.augustakom.hurrican.gui.tools.tal.wita;

import java.awt.*;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.CarrierbestellungVormieter;
import de.augustakom.hurrican.service.cc.CarrierService;

public class VormieterDialog extends AbstractServiceOptionDialog {

    private static final long serialVersionUID = -5237120180919500024L;

    private static final Logger LOGGER = Logger.getLogger(VormieterDialog.class);
    private static final String RESOURCE = "de/augustakom/hurrican/gui/tools/tal/wita/resources/VormieterDialog.xml";

    private VormieterPanel vormieterPnl = null;

    private CarrierService carrierService;
    private final Long carrierbestellungId;

    public VormieterDialog(Long carrierbestellungId) {
        super(RESOURCE);
        this.carrierbestellungId = carrierbestellungId;
        try {
            createGUI();
            initServices();
            loadData();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }
    }

    private void initServices() throws ServiceNotFoundException {
        carrierService = getCCService(CarrierService.class);
    }

    @Override
    public void update(Observable o, Object arg) {
        // not needed
    }

    @Override
    protected void doSave() {
        if (vormieterPnl.saveVormieter()) {
            prepare4Close();
            setValue(null);
        }
    }

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));
        configureButton(CMD_SAVE, "Speichern", "Speichert die Vormieterdaten", true, true);

        vormieterPnl = new VormieterPanel(carrierbestellungId);

        AKJPanel child = getChildPanel();
        child.setLayout(new BorderLayout());
        child.add(new AKJPanel(), BorderLayout.NORTH);
        child.add(new AKJPanel(), BorderLayout.WEST);
        child.add(vormieterPnl, BorderLayout.CENTER);
        child.add(new AKJPanel(), BorderLayout.EAST);
        child.add(new AKJPanel(), BorderLayout.SOUTH);
        child.validate();
    }

    @Override
    protected void execute(String command) {
        // not needed
    }

    private void loadData() {
        try {
            Carrierbestellung cb = carrierService.findCB(carrierbestellungId);
            CarrierbestellungVormieter vormieter = cb.getCarrierbestellungVormieter();
            vormieterPnl.setModel(vormieter);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }
    }
}
