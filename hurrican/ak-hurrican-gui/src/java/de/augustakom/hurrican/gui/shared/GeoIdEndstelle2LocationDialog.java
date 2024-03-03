/**
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.01.2011 11:57:10
 */

package de.augustakom.hurrican.gui.shared;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.GeoId2TechLocation;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.query.HVTQuery;
import de.augustakom.hurrican.model.cc.view.HVTGruppeStdView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.augustakom.hurrican.service.cc.HVTService;


/**
 * Dialog um die Zuordnung einer Endstelle zu einem Standort bezogen auf die gegebene Geo ID bearbeiten zu können.
 * Hintergrund: Die Endstelle bekommt eine Geo ID zugewiesen. Damit erhält die Endstelle über eine Prioritätenliste
 * automatisch auch ihren technischen Standort (HVT, KVZ, FTTB etc.). Sollte diese Zuweisung geändert werden müssen, so
 * kann ein Hurrican Administrator (entsprechende Rolle gesetzt) in diesem Dialog die neue Einstellung vornehmen.
 */
public class GeoIdEndstelle2LocationDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(GeoIdSearchDialog.class);

    private HVTGruppeStdView primaryHVTViewSelection = null;
    private final Endstelle endstelle;
    private final Produkt produkt;

    // GUI
    private AKJTextField tfGeoId;
    private AKJComboBox cbPrioListe;

    public GeoIdEndstelle2LocationDialog(Endstelle endstelle, Produkt produkt) {
        super("de/augustakom/hurrican/gui/shared/resources/GeoIdEndstelle2LocationDialog.xml");
        this.endstelle = endstelle;
        this.produkt = produkt;
        createGUI();
        loadData();
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    protected void doSave() {
        try {
            HVTGruppeStdView selection = (HVTGruppeStdView) cbPrioListe.getSelectedItem();
            if ((selection != null) && (primaryHVTViewSelection != selection)) {
                if (selection.getHvtIdStandort() == null) {
                    throw new StoreException("Der zugeordnete Standort hat keine Standort ID!");
                }
                endstelle.setHvtIdStandort(selection.getHvtIdStandort());
                prepare4Close();
                setValue(Boolean.TRUE);
            }
            else {
                prepare4Close();
                setValue(Boolean.FALSE);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));

        configureButton(CMD_SAVE, "Speichern", "Speichert die neue Zuordnung und schließt den Dialog", true, true);
        configureButton(CMD_CANCEL, "Abbrechen", "Schliesst den Dialog, ohne die gesetzte Zuordnung zu speichern", true, true);

        AKJLabel lblGeoId = getSwingFactory().createLabel("geoid.description");
        AKJLabel lblPrioListe = getSwingFactory().createLabel("standort.prio.liste");

        Dimension cbDim = new Dimension(200, 22);
        tfGeoId = getSwingFactory().createTextField("geoid.description", false);
        tfGeoId.setEnabled(false);
        cbPrioListe = getSwingFactory().createComboBox("standort.prio.liste",
                new AKCustomListCellRenderer<>(HVTGruppeStdView.class, HVTGruppeStdView::getOrtsteil));
        cbPrioListe.setPreferredSize(cbDim);

        AKJPanel editPanel = new AKJPanel(new GridBagLayout());
        editPanel.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("edit.panel.title.border")));
        editPanel.add(lblGeoId, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        editPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        editPanel.add(tfGeoId, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        editPanel.add(lblPrioListe, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        editPanel.add(cbPrioListe, GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));

        getChildPanel().setLayout(new GridBagLayout());
        getChildPanel().add(editPanel, GBCFactory.createGBC(100, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public final void loadData() {
        if ((produkt != null) && (produkt.getId() != null)
                && (endstelle != null) && (endstelle.getGeoId() != null) && (endstelle.getHvtIdStandort() != null)) {
            try {
                setWaitCursor();
                GuiTools.lockComponents(new Component[] { tfGeoId, cbPrioListe });

                AvailabilityService availabilityService = getCCService(AvailabilityService.class);

                // Prio Liste
                GeoId geoId = availabilityService.findGeoId(endstelle.getGeoId());
                if (geoId != null) {
                    tfGeoId.setText(geoId.getStreetAndHouseNum());

                    List<GeoId2TechLocation> geoId2TechLocations = availabilityService.findPossibleGeoId2TechLocations(geoId, produkt.getId());
                    List<HVTGruppeStdView> hvtViews = loadHVTViews(geoId2TechLocations);
                    if (CollectionTools.isNotEmpty(hvtViews)) {
                        for (HVTGruppeStdView hvtView : hvtViews) {
                            if (NumberTools.equal(hvtView.getHvtIdStandort(), endstelle.getHvtIdStandort())
                                    && (primaryHVTViewSelection == null)) {
                                primaryHVTViewSelection = hvtView;
                            }
                            cbPrioListe.addItem(hvtView);
                        }
                    }
                    cbPrioListe.setSelectedItem(primaryHVTViewSelection);
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
            finally {
                GuiTools.unlockComponents(new Component[] { tfGeoId, cbPrioListe });
                cbPrioListe.requestFocusInWindow();
                setDefaultCursor();
            }
        }
    }

    private List<HVTGruppeStdView> loadHVTViews(List<GeoId2TechLocation> geoId2TechLocations)
            throws FindException, ServiceNotFoundException {
        if (CollectionTools.isEmpty(geoId2TechLocations)) {
            return null;
        }

        List<HVTGruppeStdView> result = new ArrayList<HVTGruppeStdView>();
        HVTService hvtService = getCCService(HVTService.class);
        for (GeoId2TechLocation geoId2TechLocation : geoId2TechLocations) {
            HVTQuery query = new HVTQuery();
            query.setHvtIdStandort(geoId2TechLocation.getHvtIdStandort());
            List<HVTGruppeStdView> hvtViews = hvtService.findHVTViews(query);
            if ((hvtViews != null) && (hvtViews.size() == 1)) {
                result.add(hvtViews.get(0));
            }
        }
        return result;
    }

}
