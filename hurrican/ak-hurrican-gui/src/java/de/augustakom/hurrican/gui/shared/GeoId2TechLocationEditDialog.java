/**
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.02.2011 12:04:59
 */

package de.augustakom.hurrican.gui.shared;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.iface.ISimpleFindService;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.GeoId2TechLocation;
import de.augustakom.hurrican.model.cc.GeoId2TechLocationView;
import de.augustakom.hurrican.model.cc.view.HVTGruppeStdView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.QueryCCService;


/**
 * Dialog zum Erstellen oder Bearbeiten der {@link GeoId2TechLocation} Entitäten.
 */
public class GeoId2TechLocationEditDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(GeoId2TechLocationEditDialog.class);

    private AKReferenceField rfLocation = null;
    private AKJFormattedTextField tfTALLength = null;
    private AKJCheckBox chbTALLengthTrusted = null;
    private AKJTextField tfKVZNo = null;
    private AKJFormattedTextField tfMaxBandwidthADSL = null;
    private AKJFormattedTextField tfMaxBandwidthSDSL = null;
    private AKJFormattedTextField tfMaxBandwidthVDSL = null;
    private AKJDateComponent tfVdslAnHvtAvailableSince = null;

    private GeoId2TechLocationView geoId2TechLocationHVTView = null;
    private GeoId2TechLocation geoId2TechLocation = null;

    public GeoId2TechLocationEditDialog(GeoId2TechLocationView geoId2TechLocationHVTView) {
        super("de/augustakom/hurrican/gui/shared/resources/GeoId2TechLocationEditDialog.xml");
        this.geoId2TechLocationHVTView = geoId2TechLocationHVTView;
        createGUI();
        loadData();
    }

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));

        configureButton(CMD_SAVE, "Speichern", "Speichert den Standort", true, true);
        configureButton(CMD_CANCEL, "Abbrechen", "Schliesst den Dialog, ohne den bearbeiteten Standort zu speichern", true, true);

        AKJLabel lblLocation = getSwingFactory().createLabel("ref.location");
        AKJLabel lblTALLength = getSwingFactory().createLabel("tf.tal.length");
        AKJLabel lblTALLengthTrusted = getSwingFactory().createLabel("tf.tal.length.trusted");
        AKJLabel lblKVZNo = getSwingFactory().createLabel("tf.kvz");
        AKJLabel lblMaxBandwidthADSL = getSwingFactory().createLabel("tf.max.bandwidth.adsl");
        AKJLabel lblMaxBandwidthSDSL = getSwingFactory().createLabel("tf.max.bandwidth.sdsl");
        AKJLabel lblMaxBandwidthVDSL = getSwingFactory().createLabel("tf.max.bandwidth.vdsl");
        AKJLabel lblVdslAnHvtAvailableSince = getSwingFactory().createLabel("tf.vdsl.on.hvt.available.since");

        rfLocation = getSwingFactory().createReferenceField("ref.location");
        rfLocation.setPreferredSize(new Dimension(200, 25));
        tfTALLength = getSwingFactory().createFormattedTextField("tf.tal.length");
        chbTALLengthTrusted = getSwingFactory().createCheckBox("tf.tal.length.trusted");
        tfKVZNo = getSwingFactory().createTextField("tf.kvz");
        tfMaxBandwidthADSL = getSwingFactory().createFormattedTextField("tf.max.bandwidth.adsl");
        tfMaxBandwidthSDSL = getSwingFactory().createFormattedTextField("tf.max.bandwidth.sdsl");
        tfMaxBandwidthVDSL = getSwingFactory().createFormattedTextField("tf.max.bandwidth.vdsl");
        tfVdslAnHvtAvailableSince = getSwingFactory().createDateComponent("tf.vdsl.on.hvt.available.since");

        AKJPanel editPanel = new AKJPanel(new GridBagLayout());
        editPanel.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("edit.panel.border.title")));
        editPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        editPanel.add(lblLocation, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        editPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        editPanel.add(rfLocation, GBCFactory.createGBC(100, 0, 3, 0, 5, 1, GridBagConstraints.HORIZONTAL));
        editPanel.add(lblTALLength, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        editPanel.add(tfTALLength, GBCFactory.createGBC(100, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        editPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 4, 1, 1, 1, GridBagConstraints.NONE));
        editPanel.add(lblTALLengthTrusted, GBCFactory.createGBC(0, 0, 5, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        editPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 6, 1, 1, 1, GridBagConstraints.NONE));
        editPanel.add(chbTALLengthTrusted, GBCFactory.createGBC(100, 0, 7, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        editPanel.add(lblKVZNo, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        editPanel.add(tfKVZNo, GBCFactory.createGBC(100, 0, 3, 3, 5, 1, GridBagConstraints.HORIZONTAL));
        editPanel.add(lblMaxBandwidthADSL, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        editPanel.add(tfMaxBandwidthADSL, GBCFactory.createGBC(100, 0, 3, 4, 5, 1, GridBagConstraints.HORIZONTAL));
        editPanel.add(lblMaxBandwidthSDSL, GBCFactory.createGBC(0, 0, 1, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        editPanel.add(tfMaxBandwidthSDSL, GBCFactory.createGBC(100, 0, 3, 5, 5, 1, GridBagConstraints.HORIZONTAL));
        editPanel.add(lblMaxBandwidthVDSL, GBCFactory.createGBC(0, 0, 1, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        editPanel.add(tfMaxBandwidthVDSL, GBCFactory.createGBC(100, 0, 3, 6, 5, 1, GridBagConstraints.HORIZONTAL));
        editPanel.add(lblVdslAnHvtAvailableSince, GBCFactory.createGBC(0, 0, 1, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        editPanel.add(tfVdslAnHvtAvailableSince, GBCFactory.createGBC(100, 0, 3, 7, 5, 1, GridBagConstraints.HORIZONTAL));

        getChildPanel().setLayout(new GridBagLayout());
        getChildPanel().add(editPanel, GBCFactory.createGBC(100, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));
    }

    @Override
    public final void loadData() {
        try {
            HVTService hvts = getCCService(HVTService.class);
            List<HVTGruppeStdView> hvtViews = hvts.findHVTViews();

            ISimpleFindService simpleFindService = getCCService(QueryCCService.class);
            rfLocation.setFindService(simpleFindService);
            rfLocation.setReferenceList(hvtViews);

            if (geoId2TechLocationHVTView != null) {
                AvailabilityService availabilityService = getCCService(AvailabilityService.class);
                geoId2TechLocation = availabilityService.findGeoId2TechLocation(
                        geoId2TechLocationHVTView.getGeoId(),
                        geoId2TechLocationHVTView.getHvtIdStandort());
                if (geoId2TechLocation == null) {
                    throw new FindException("Der selektierte Standort konnte nicht eindeutig ermittelt werden!");
                }
                rfLocation.setReferenceId(geoId2TechLocationHVTView.getHvtIdStandort());
                tfTALLength.setValue(geoId2TechLocationHVTView.getTalLength());
                chbTALLengthTrusted.setSelected(BooleanTools.nullToFalse(geoId2TechLocationHVTView.getTalLengthTrusted()));
                tfKVZNo.setText(geoId2TechLocationHVTView.getKvzNumber());
                tfMaxBandwidthADSL.setValue(geoId2TechLocationHVTView.getMaxBandwidthAdsl());
                tfMaxBandwidthSDSL.setValue(geoId2TechLocationHVTView.getMaxBandwidthSdsl());
                tfMaxBandwidthVDSL.setValue(geoId2TechLocationHVTView.getMaxBandwidthVdsl());
                tfVdslAnHvtAvailableSince.setDate(geoId2TechLocationHVTView.getVdslAnHvtAvailableSince());
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
            getButton(CMD_SAVE).setEnabled(false);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    protected void doSave() {
        try {
            Long hvtIdStandort = rfLocation.getReferenceIdAs(Long.class);
            if (hvtIdStandort != null) {
                if (geoId2TechLocation == null) {
                    geoId2TechLocation = new GeoId2TechLocation();
                }

                geoId2TechLocation.setHvtIdStandort(hvtIdStandort);

                if ((tfTALLength.getValueAsLong(null) != null) && (tfTALLength.getValueAsLong(null) <= 0)) {
                    throw new StoreException("TAL-Länge kann nicht negativ oder 0 sein!");
                }
                if (tfTALLength.getValueAsLong(null) == null) {
                    geoId2TechLocation.setTalLengthTrusted(false);
                }
                else {
                    geoId2TechLocation.setTalLengthTrusted(chbTALLengthTrusted.isSelectedBoolean());
                }
                geoId2TechLocation.setTalLength(tfTALLength.getValueAsLong(null));
                geoId2TechLocation.setKvzNumber(tfKVZNo.getText(null));
                geoId2TechLocation.setMaxBandwidthAdsl(tfMaxBandwidthADSL.getValueAsLong(null));
                geoId2TechLocation.setMaxBandwidthSdsl(tfMaxBandwidthSDSL.getValueAsLong(null));
                geoId2TechLocation.setMaxBandwidthVdsl(tfMaxBandwidthVDSL.getValueAsLong(null));
                Calendar cal = null;
                if (tfVdslAnHvtAvailableSince.getDate(null) != null) {
                    cal = Calendar.getInstance();
                    cal.setTime(tfVdslAnHvtAvailableSince.getDate(null));
                }
                geoId2TechLocation.setVdslAnHvtAvailableSince(cal);

                prepare4Close();
                setValue(geoId2TechLocation);
            }
            else {
                throw new StoreException("Bitte Standort auswählen!");
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

}
