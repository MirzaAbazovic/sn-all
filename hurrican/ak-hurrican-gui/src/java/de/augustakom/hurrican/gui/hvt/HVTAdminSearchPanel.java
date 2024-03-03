/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.07.2015
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.ReferenceService;

/**
 * Panel fuer die Darstellung von HVT Admin Suchmaske
 *
 */
public abstract class HVTAdminSearchPanel extends AbstractServicePanel {

    private static final Logger LOGGER = Logger.getLogger(HVTAdminSearchPanel.class);

    private AKJTextField tfOnkz;
    private AKJTextField tfAsb;
    private AKJTextField tfName;
    private AKJTextField tfOrt;
    private AKJComboBox cbStandortTyp;
    private AKJTextField tfCluster;

    public HVTAdminSearchPanel() {
        super("de/augustakom/hurrican/gui/hvt/resources/HVTAdminSearchPanel.xml");
        this.createGUI();
        this.initialize();
    }

    @Override
    protected void createGUI() {
        this.setLayout(new GridBagLayout());
        final AKJLabel lblOnkz = getSwingFactory().createLabel("hvt.search.onkz");
        tfOnkz = getSwingFactory().createTextField("hvt.search.onkz");
        final AKJLabel lblAsb = getSwingFactory().createLabel("hvt.search.asb");
        tfAsb = getSwingFactory().createTextField("hvt.search.asb");
        final AKJLabel lblName = getSwingFactory().createLabel("hvt.search.name");
        tfName = getSwingFactory().createTextField("hvt.search.name");
        final AKJLabel lblOrt = getSwingFactory().createLabel("hvt.search.ort");
        tfOrt = getSwingFactory().createTextField("hvt.search.ort");
        final AKJLabel lblTyp = getSwingFactory().createLabel("hvt.search.standortTyp");
        cbStandortTyp = getSwingFactory().createComboBox("hvt.standort.standortTyp",
                new AKCustomListCellRenderer<>(Reference.class, Reference::getStrValue));
        final AKJLabel lblCluster = getSwingFactory().createLabel("hvt.search.clusterId");
        tfCluster = getSwingFactory().createTextField("hvt.search.clusterId");
        final AKJButton brnSearch = getSwingFactory().createButton("hvt.search.doSearch", getActionListener());

        final AKJPanel standortePanel= new AKJPanel(new GridBagLayout());
        standortePanel.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("hvt.search.standorte")));
        standortePanel.add(lblAsb, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        standortePanel.add(tfAsb, GBCFactory.createGBC(100, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        standortePanel.add(lblTyp, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        standortePanel.add(cbStandortTyp, GBCFactory.createGBC(100, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        standortePanel.add(lblCluster, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        standortePanel.add(tfCluster, GBCFactory.createGBC(100, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));

        final AKJPanel gruppenPanel= new AKJPanel(new GridBagLayout());
        gruppenPanel.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("hvt.search.gruppen")));
        gruppenPanel.add(lblOnkz, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        gruppenPanel.add(tfOnkz, GBCFactory.createGBC(100, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        gruppenPanel.add(lblName, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        gruppenPanel.add(tfName, GBCFactory.createGBC(100, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        gruppenPanel.add(lblOrt, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        gruppenPanel.add(tfOrt, GBCFactory.createGBC(100, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));

        this.add(standortePanel, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.BOTH));
        this.add(gruppenPanel, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(brnSearch, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
    }

    public void initialize() {
        try {
            setWaitCursor();
            showProgressBar("laden...");

            ReferenceService rs = getCCService(ReferenceService.class);
            List<Reference> typen = rs.findReferencesByType(Reference.REF_TYPE_STANDORT_TYP, true);
            cbStandortTyp.addItems(typen, true, Reference.class);

        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
        finally {
            setDefaultCursor();
            stopProgressBar();
        }
    }


    public final void reloadFilteredData() {
        try {
            setWaitCursor();
            showProgressBar("laden...");

            final String onkz = tfOnkz.getText();
            final Integer asb = tfAsb.getTextAsInt(null);
            final String ortsteil = tfName.getText();
            final String ort = tfOrt.getText();
            final Reference standortTyp = (Reference)cbStandortTyp.getSelectedItem();
            final Long standortTypId = standortTyp != null ? standortTyp.getId() : null;
            final String clusterId = tfCluster.getText();

            final HVTService service = getCCService(HVTService.class);
            final Pair<List<HVTStandort>, List<HVTGruppe>> hvtStandorteAndGruppen =
                    service.findHVTStandorteAndGruppen(onkz, asb, ortsteil, ort, standortTypId, clusterId);
            final List<HVTStandort> standortList = hvtStandorteAndGruppen.getFirst();
            final List<HVTGruppe> gruppenList = hvtStandorteAndGruppen.getSecond();

            final Map<Long, HVTGruppe> hvtGruppenMap = new HashMap<>();
            CollectionMapConverter.convert2Map(gruppenList, hvtGruppenMap, "getId", null);

            onSearchResultFetched(hvtGruppenMap, gruppenList, standortList);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
        finally {
            setDefaultCursor();
            stopProgressBar();
        }
    }


    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("hvt.search.doSearch".equals(command)) {
            reloadFilteredData();
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable observable, Object o) {

    }

    public abstract void onSearchResultFetched(Map<Long, HVTGruppe> hvtGruppenMap, List<HVTGruppe> hvtGruppenList, List<HVTStandort> hvtStandorts);
}
