/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.12.2008 14:10:26
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.beans.*;
import java.util.*;
import java.util.List;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKNavigationBarListener;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJNavigationBar;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.iface.ISimpleFindService;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;
import de.augustakom.hurrican.model.cc.HVTRaum;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.QueryCCService;
import de.augustakom.hurrican.service.cc.ReferenceService;


/**
 * Admin-Panel fuer die Verwaltung der HW-Racks
 *
 *
 */
public class HWRackAdminPanel extends AbstractAdminPanel implements AKNavigationBarListener,
        PropertyChangeListener {

    private static final Logger LOGGER = Logger.getLogger(HWRackAdminPanel.class);
    private static final long serialVersionUID = -971571274387367838L;

    private AKReferenceField rfType = null;
    private AKJTextField tfAnBez = null;
    private AKJTextField tfGerBez = null;
    private AKJTextField tfMgmBez = null;
    private AKReferenceField rfRaum = null;
    private AKReferenceField rfHvtTechnik = null;
    private AKJDateComponent dcGueltigVon = null;
    private AKJDateComponent dcGueltigBis = null;
    private AKJNavigationBar navBar = null;
    private HWRackDefAdminPanel defPanel = null;

    private HVTStandort hvtStandort = null;
    private HWRack hwRackModel = null;
    private boolean guiCreated = false;

    /**
     * Konstruktor
     */
    public HWRackAdminPanel() {
        super("de/augustakom/hurrican/gui/hvt/resources/HWRackAdminPanel.xml");
        createGUI();
        loadDefaultData();
    }

    @Override
    protected final void createGUI() {
        navBar = new AKJNavigationBar(this, true, true);

        AKJLabel lblType = getSwingFactory().createLabel("type");
        AKJLabel lblAnBez = getSwingFactory().createLabel("anlagen.bezeichnung");
        AKJLabel lblGerBez = getSwingFactory().createLabel("geraete.bezeichnung");
        AKJLabel lblMgmBez = getSwingFactory().createLabel("management.bezeichnung");
        AKJLabel lblRaum = getSwingFactory().createLabel("raum");
        AKJLabel lblProd = getSwingFactory().createLabel("prod");
        AKJLabel lblGueltigVon = getSwingFactory().createLabel("gueltig.von");
        AKJLabel lblGueltigBis = getSwingFactory().createLabel("gueltig.bis");

        rfType = getSwingFactory().createReferenceField("type");
        rfType.addPropertyChangeListener(this);
        rfRaum = getSwingFactory().createReferenceField("raum");
        rfHvtTechnik = getSwingFactory().createReferenceField(
                "prod", HVTTechnik.class, HVTTechnik.ID, HVTTechnik.HERSTELLER, null);

        tfAnBez = getSwingFactory().createTextField("anlagen.bezeichnung");
        tfGerBez = getSwingFactory().createTextField("geraete.bezeichnung");
        tfMgmBez = getSwingFactory().createTextField("management.bezeichnung");
        dcGueltigBis = getSwingFactory().createDateComponent("gueltig.bis");
        dcGueltigVon = getSwingFactory().createDateComponent("gueltig.von");

        AKJPanel left = new AKJPanel(new GridBagLayout(), getSwingFactory().getText("border"));
        left.add(lblGerBez, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        left.add(tfGerBez, GBCFactory.createGBC(100, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblMgmBez, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfMgmBez, GBCFactory.createGBC(100, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblAnBez, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfAnBez, GBCFactory.createGBC(100, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblType, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(rfType, GBCFactory.createGBC(100, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblProd, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(rfHvtTechnik, GBCFactory.createGBC(100, 0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblRaum, GBCFactory.createGBC(0, 0, 1, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(rfRaum, GBCFactory.createGBC(100, 0, 3, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblGueltigVon, GBCFactory.createGBC(0, 0, 1, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(dcGueltigVon, GBCFactory.createGBC(100, 0, 3, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblGueltigBis, GBCFactory.createGBC(0, 0, 1, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(dcGueltigBis, GBCFactory.createGBC(100, 0, 3, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 3, 9, 1, 1, GridBagConstraints.VERTICAL));

        defPanel = new HWRackDefAdminPanel();

        this.setLayout(new GridBagLayout());
        this.add(navBar, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(defPanel, GBCFactory.createGBC(100, 100, 2, 1, 1, 2, GridBagConstraints.HORIZONTAL));
        this.add(left, GBCFactory.createGBC(50, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));

        guiCreated = true;
    }

    @Override
    public void showDetails(Object details) {
        clear();
        if (details instanceof HVTStandort) {
            this.hvtStandort = (HVTStandort) details;
            loadData();
        }
        else {
            this.hvtStandort = null;
        }
    }

    /**
     * 'Loescht' alle Felder
     */
    private void clear() {
        this.hwRackModel = null;
        this.hvtStandort = null;
        if (guiCreated) {
            GuiTools.cleanFields(this);
            navBar.setData(null);
        }
    }

    /**
     * Laedt die Default-Daten fuer das Panel.
     */
    private void loadDefaultData() {
        try {
            ISimpleFindService sfs = getCCService(QueryCCService.class);
            rfHvtTechnik.setFindService(sfs);
            rfRaum.setFindService(sfs);
            rfType.setFindService(sfs);

            ReferenceService refService = getCCService(ReferenceService.class);
            List<Reference> refs = refService.findReferencesByType(Reference.REF_TYPE_HW_RACK_TYPE, Boolean.TRUE);
            rfType.setReferenceList(refs);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    public final void loadData() {
        if (hvtStandort != null) {
            try {
                HVTService hvtService = getCCService(HVTService.class);
                List<HVTRaum> raeume = hvtService.findHVTRaeume4Standort(hvtStandort.getHvtIdStandort());
                rfRaum.setReferenceList(raeume);

                HWService service = getCCService(HWService.class);
                List<HWRack> racks = service.findRacks(hvtStandort.getHvtIdStandort());
                navBar.setData(racks);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(this, e);
            }
        }
    }

    @Override
    public void saveData() {
        boolean isNew = ((hwRackModel != null) && (hwRackModel.getId() == null));
        try {
            if (hvtStandort == null) {
                MessageHelper.showInfoDialog(this, "Bitte zuerst einen HVT-Standort auswählen!");
                return;
            }

            if (hwRackModel == null) {
                MessageHelper.showInfoDialog(this, "Bitte definieren Sie zuerst den Rack-Typ!");
                return;
            }

            hwRackModel.setHvtIdStandort(hvtStandort.getHvtIdStandort());
            hwRackModel.setAnlagenBez(tfAnBez.getText());
            hwRackModel.setGeraeteBez(tfGerBez.getText());
            hwRackModel.setManagementBez(tfMgmBez.getText(null));
            hwRackModel.setRackTyp(rfType.getReferenceIdAs(String.class));
            hwRackModel.setHvtRaumId(rfRaum.getReferenceIdAs(Long.class));
            hwRackModel.setHwProducer(rfHvtTechnik.getReferenceIdAs(Long.class));
            hwRackModel.setGueltigVon(dcGueltigVon.getDate(null));
            hwRackModel.setGueltigBis(dcGueltigBis.getDate(null));

            // HW-Definition speichern
            if (defPanel.isAdminPanelDefined()) {
                defPanel.saveData();
            }
            else {
                HWService service = getCCService(HWService.class);
                service.saveHWRack(hwRackModel);
            }

            // Falls Eintrag neu und mit erfolgreich gespeichert, zu Nav.Bar hinzufuegen
            if (isNew && (hwRackModel.getId() != null)) {
                navBar.addNavigationObject(hwRackModel);
            }
        }
        catch (Exception e) {
            if (isNew) {
                //Wenn bspw. ein Constraint in DB zuschlägt, Datensatz wieder als 'transient' markieren
                hwRackModel.setId(null);
            }

            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    @Override
    public void createNew() {
        this.hwRackModel = null;
        GuiTools.cleanFields(this);
        if (hvtStandort != null) {
            showNavigationObject(null, 0);
            dcGueltigVon.setDate(new Date());
            dcGueltigBis.setDate(DateTools.getHurricanEndDate());
        }
        else {
            MessageHelper.showInfoDialog(this, "Bitte zuerst einen HVT-Standort auswählen!");
        }
    }

    @Override
    public void showNavigationObject(Object obj, int number) {
        if (guiCreated) {
            rfType.setEnabled(true);
            if (obj instanceof HWRack) {
                hwRackModel = (HWRack) obj;
                tfAnBez.setText(hwRackModel.getAnlagenBez());
                tfGerBez.setText(hwRackModel.getGeraeteBez());
                tfMgmBez.setText(hwRackModel.getManagementBez());
                rfHvtTechnik.setReferenceId(hwRackModel.getHwProducer());
                rfRaum.setReferenceId(hwRackModel.getHvtRaumId());
                rfType.setReferenceId(hwRackModel.getRackTyp());
                dcGueltigVon.setDate(hwRackModel.getGueltigVon());
                dcGueltigBis.setDate(hwRackModel.getGueltigBis());
                defPanel.setRack(hwRackModel);

                // Falls ID von HW-Rack gesetzt ist, sperre Rack-Typ
                if (hwRackModel.getId() != null) {
                    rfType.setEnabled(false);
                }
            }
            else {
                GuiTools.cleanFields(this);
                defPanel.deleteRack();
                hwRackModel = null;
            }
        }
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (rfType.getReferenceIdAs(String.class) != null) {
            // User hat eine Auswahl getroffen
            if (hwRackModel != null && hwRackModel.getId() != null) {
                // Das ausgewählte Rack existiert bereits
                return;
            }

            // Die Auswahl hat sich verändert
            try {
                defPanel.deleteRack();
                hwRackModel = null;
                Class<? extends HWRack> rack = HWRack.getRackClass(rfType.getReferenceIdAs(String.class));
                if (rack != null) {
                    hwRackModel = rack.newInstance();
                    defPanel.setRack(hwRackModel);
                    defPanel.createNew();
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(this, e);
            }
        }
        else {
            defPanel.deleteRack();
            hwRackModel = null;
        }
    }

}
