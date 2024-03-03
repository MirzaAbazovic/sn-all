/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.10.2011 11:43:14
 */
package de.augustakom.hurrican.gui.tools.tal.wita;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.validation.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractDataPanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.CarrierbestellungVormieter;
import de.augustakom.hurrican.service.cc.CarrierService;

/**
 * Wizard-Panel fuer die Erfassung von Vormieter-Daten.
 */
public class VormieterPanel extends AbstractDataPanel implements AKDataLoaderComponent {

    private static final long serialVersionUID = 2182768885033109824L;

    private static final Logger LOGGER = Logger.getLogger(VormieterPanel.class);
    private static final String RESOURCE = "de/augustakom/hurrican/gui/tools/tal/wita/resources/VormieterPanel.xml";

    private static final String SUB_TITLE = "sub.title";

    private static final String VORMIETER_VORNAME = "vormieter.vorname";
    private static final String VORMIETER_NACHNAME = "vormieter.nachname";
    private static final String VORMIETER_ONKZ = "vormieter.onkz";
    private static final String VORMIETER_RUFNUMMER = "vormieter.rufnummer";
    private static final String VORMIETER_UFA_NUMMER = "vormieter.ufa.nummer";

    private CarrierService carrierService;
    private final Long carrierbestellungId;

    private AKJTextField tfVorname;
    private AKJTextField tfNachname;
    private AKJTextField tfOnkz;
    private AKJTextField tfRufnummer;
    private AKJTextField tfUfaNummer;

    public VormieterPanel(Long carrierbestellungId) {
        super(RESOURCE);
        this.carrierbestellungId = carrierbestellungId;
        try {
            initServices();
            createGUI();
        }
        catch (ServiceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }
    }

    private void initServices() throws ServiceNotFoundException {
        carrierService = getCCService(CarrierService.class);
    }

    @Override
    protected final void createGUI() {
        AKJLabel lblSubTitle = getSwingFactory().createLabel(SUB_TITLE, SwingConstants.LEFT, Font.BOLD);
        AKJLabel lblVorname = getSwingFactory().createLabel(VORMIETER_VORNAME);
        AKJLabel lblNachname = getSwingFactory().createLabel(VORMIETER_NACHNAME);
        AKJLabel lblOnkz = getSwingFactory().createLabel(VORMIETER_ONKZ);
        AKJLabel lblRufnummer = getSwingFactory().createLabel(VORMIETER_RUFNUMMER);
        AKJLabel lblUfaNummer = getSwingFactory().createLabel(VORMIETER_UFA_NUMMER);

        tfVorname = getSwingFactory().createTextField(VORMIETER_VORNAME, true, true);
        tfNachname = getSwingFactory().createTextField(VORMIETER_NACHNAME, true, true);
        tfOnkz = getSwingFactory().createTextField(VORMIETER_ONKZ, true, true);
        tfRufnummer = getSwingFactory().createTextField(VORMIETER_RUFNUMMER, true, true);
        tfUfaNummer = getSwingFactory().createTextField(VORMIETER_UFA_NUMMER, true, true);

        // @formatter:off
        this.setLayout(new GridBagLayout());
        this.add(new AKJPanel(), GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        this.add(lblSubTitle   , GBCFactory.createGBC(  0,  0, 1, 1, 3, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblVorname    , GBCFactory.createGBC(  0,  0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(  0,  0, 2, 2, 1, 1, GridBagConstraints.NONE));
        this.add(tfVorname     , GBCFactory.createGBC(100,  0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblNachname   , GBCFactory.createGBC(  0,  0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(tfNachname    , GBCFactory.createGBC(100,  0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblOnkz       , GBCFactory.createGBC(  0,  0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(tfOnkz        , GBCFactory.createGBC(100,  0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblRufnummer  , GBCFactory.createGBC(  0,  0, 1, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(tfRufnummer   , GBCFactory.createGBC(100,  0, 3, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblUfaNummer  , GBCFactory.createGBC(  0,  0, 1, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(tfUfaNummer   , GBCFactory.createGBC(100,  0, 3, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(100,100, 4, 7, 1, 1, GridBagConstraints.BOTH));
        // @formatter:on
    }

    @Override
    public void setModel(Observable model) throws AKGUIException {
        CarrierbestellungVormieter vormieter = null;
        if (model instanceof CarrierbestellungVormieter) {
            vormieter = (CarrierbestellungVormieter) model;
            tfVorname.setText(vormieter.getVorname());
            tfNachname.setText(vormieter.getNachname());
            tfOnkz.setText(vormieter.getOnkz());
            tfRufnummer.setText(vormieter.getRufnummer());
            tfUfaNummer.setText(vormieter.getUfaNummer());
        }
    }

    public boolean saveVormieter() {
        try {
            Carrierbestellung carrierbestellung = carrierService.findCB(carrierbestellungId);
            if (carrierbestellung.getCarrierbestellungVormieter() == null) {
                carrierbestellung.setCarrierbestellungVormieter(new CarrierbestellungVormieter());
            }

            CarrierbestellungVormieter cbVormieter = carrierbestellung.getCarrierbestellungVormieter();
            cbVormieter.setVorname(tfVorname.getText(null));
            cbVormieter.setNachname(tfNachname.getText(null));
            cbVormieter.setOnkz(tfOnkz.getText(null));
            cbVormieter.setRufnummer(tfRufnummer.getText(null));
            cbVormieter.setUfaNummer(tfUfaNummer.getText(null));

            if (!cbVormieter.isEmpty()) {
                carrierService.saveCB(carrierbestellung);
            }
            else if (cbVormieter.getId() != null) {
                carrierbestellung.setCarrierbestellungVormieter(null);
                carrierService.saveCB(carrierbestellung);
            }
            return true;
        }
        catch (ConstraintViolationException cve) {
            LOGGER.error(cve.getMessage(), cve);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), new HurricanGUIException(
                    "Die eingegeben Daten wurden validiert und können leider nicht übernommen werden. Bitte korrigieren Sie die eingegebenen Daten.", cve));
            return false;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), new HurricanGUIException(
                    "Es ist ein unerwarteter Fehler aufgetreten. Bitten wenden Sie sich an Ihren Administrator", e));
            return false;
        }
    }

    @Override
    public boolean hasModelChanged() {
        return false;
    }

    @Override
    public Object getModel() {
        // not used
        return null;
    }

    @Override
    public final void loadData() {
        // not used
    }

    @Override
    public void update(Observable o, Object arg) {
        // not used
    }

    @Override
    protected void execute(String command) {
        // not used
    }

    @Override
    public void readModel() throws AKGUIException {
        // not used
    }

    @Override
    public void saveModel() throws AKGUIException {
        // not used
    }
}
