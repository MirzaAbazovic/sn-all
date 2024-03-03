/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.12.2008 13:33:32
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.CarrierContact;
import de.augustakom.hurrican.model.cc.CarrierKennung;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.view.HVTGruppeStdView;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.ProfileService;
import de.augustakom.hurrican.service.cc.QueryCCService;
import de.augustakom.hurrican.service.cc.QueryHvtGruppeStandorService;
import de.augustakom.hurrican.service.cc.ReferenceService;


/**
 * Admin-Panel fuer Details der HVT-Standorte.
 */
public class HVTStandortDetailAdminPanel extends AbstractAdminPanel {

    private static final Logger LOGGER = Logger.getLogger(HVTStandortDetailAdminPanel.class);

    private static final String COMBOBOX_PROTOTYPE_DISP_VALUE = "1234567890123456789012345";

    private HVTStandort detail = null;
    private Map<Long, HVTTechnik> hvtTechniken = null;
    private HVTStandortAdminPanel parent = null;

    private AKJComboBox rfHVTGruppe = null;
    private AKJTextField tfASB = null;
    private AKJComboBox cbReal = null;
    private AKJTextField tfEWSD1 = null;
    private AKJTextField tfEWSD2 = null;
    private AKJTextField tfBeschreibung = null;
    private AKJComboBox cbCarrier = null;
    private AKJComboBox cbCarrierKennung = null;
    private AKJComboBox cbCarrierContact = null;
    private AKJDateComponent dcGueltigVon = null;
    private AKJDateComponent dcGueltigBis = null;
    private AKJCheckBox chbVirtuell = null;
    private AKJCheckBox chbCPSProv = null;
    private AKJCheckBox chbAutomatic = null;
    private HVTTechnikTableModel tbMdlTechnik = null;
    private AKJComboBox cbStandortTyp = null;
    private AKJCheckBox chbBreakRang = null;
    private AKReferenceField rfFcRaum = null;
    private AKReferenceField rfBetriebsraum = null;
    private AKJTextField tfClusterId = null;
    private AKJComboBox cbStartFreq = null;
    private AKJButton btnCreateASB = null;


    // Variable enableGUI entscheidet ob GUI-Elemente zum Editieren freigegeben werden können.
    private boolean enableGUI = false;
    private boolean loaded = false;

    /**
     * Standardkonstruktor.
     */
    public HVTStandortDetailAdminPanel(HVTStandortAdminPanel parent) {
        super("de/augustakom/hurrican/gui/hvt/resources/HVTStandortDetailAdminPanel.xml");
        this.hvtTechniken = new HashMap<Long, HVTTechnik>();
        this.parent = parent;
        createGUI();
    }

    @Override
    protected final void createGUI() {
        tbMdlTechnik = new HVTTechnikTableModel();
        AKJTable tbTechnik = new AKJTable(tbMdlTechnik, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbTechnik.fitTable(new int[] { 100, 50 });
        AKJScrollPane spTechnik = new AKJScrollPane(tbTechnik, new Dimension(200, 50));

        AKJLabel lblGruppe = getSwingFactory().createLabel("hvt.standort.gruppe");
        AKJLabel lblASB = getSwingFactory().createLabel("hvt.standort.asb");
        AKJLabel lblReal = getSwingFactory().createLabel("hvt.standort.realisierung");
        AKJLabel lblBeschreibung = getSwingFactory().createLabel("hvt.standort.beschreibung");
        AKJLabel lblGueltigVon = getSwingFactory().createLabel("hvt.standort.gueltig.von");
        AKJLabel lblGueltigBis = getSwingFactory().createLabel("hvt.standort.gueltig.bis");
        AKJLabel lblEWSD1 = getSwingFactory().createLabel("hvt.standort.ewsd1");
        AKJLabel lblEWSD2 = getSwingFactory().createLabel("hvt.standort.ewsd2");
        AKJLabel lblVirtuell = getSwingFactory().createLabel("hvt.standort.virtuell");
        AKJLabel lblCPSProv = getSwingFactory().createLabel("hvt.standort.cps.provisioning");
        AKJLabel lblCarrier = getSwingFactory().createLabel("hvt.standort.carrier");
        AKJLabel lblCKennung = getSwingFactory().createLabel("hvt.standort.carrier.kennung");
        AKJLabel lblCContact = getSwingFactory().createLabel("hvt.standort.carrier.contact");
        AKJLabel lblStandortTyp = getSwingFactory().createLabel("hvt.standort.standortTyp");
        AKJLabel lblBreakRang = getSwingFactory().createLabel("hvt.standort.breakRang");
        AKJLabel lblFcRaum = getSwingFactory().createLabel("hvt.standort.fc");
        AKJLabel lblBetriebsraum = getSwingFactory().createLabel("hvt.standort.br");
        AKJLabel lblAutomatic = getSwingFactory().createLabel("hvt.standort.automatic");
        AKJLabel lblClusterId = getSwingFactory().createLabel("hvt.standort.clusterId");
        AKJLabel lblStartFreq = getSwingFactory().createLabel("hvt.standort.gfast.startfreq");

        rfHVTGruppe = getSwingFactory().createComboBox("hvt.standort.gruppe",
                new AKCustomListCellRenderer<>(HVTGruppe.class, HVTGruppe::getOrtsteil));
        tfASB = getSwingFactory().createTextField("hvt.standort.asb");
        btnCreateASB = getSwingFactory().createButton("create.asb.kennung", this.getActionListener());
        cbReal = getSwingFactory().createComboBox("hvt.standort.realisierung");
        tfBeschreibung = getSwingFactory().createTextField("hvt.standort.beschreibung");
        dcGueltigVon = getSwingFactory().createDateComponent("hvt.standort.gueltig.von");
        dcGueltigBis = getSwingFactory().createDateComponent("hvt.standort.gueltig.bis");
        tfEWSD1 = getSwingFactory().createTextField("hvt.standort.ewsd1");
        tfEWSD2 = getSwingFactory().createTextField("hvt.standort.ewsd2");
        chbVirtuell = getSwingFactory().createCheckBox("hvt.standort.virtuell");
        chbCPSProv = getSwingFactory().createCheckBox("hvt.standort.cps.provisioning");
        chbAutomatic = getSwingFactory().createCheckBox("hvt.standort.automatic");
        cbCarrier = getSwingFactory().createComboBox("hvt.standort.carrier",
                new AKCustomListCellRenderer<>(Carrier.class, Carrier::getName));
        cbCarrier.addItemListener(new CBCarrierItemListener());
        cbCarrierKennung = getSwingFactory().createComboBox("hvt.standort.carrier.kennung",
                new AKCustomListCellRenderer<>(CarrierKennung.class, CarrierKennung::getBezeichnung));
        cbCarrierContact = getSwingFactory().createComboBox("hvt.standort.carrier.contact",
                new AKCustomListCellRenderer<>(CarrierContact.class, CarrierContact::getDescription));
        cbStandortTyp = getSwingFactory().createComboBox("hvt.standort.standortTyp",
                new AKCustomListCellRenderer<>(Reference.class, Reference::getStrValue));
        chbBreakRang = getSwingFactory().createCheckBox("hvt.standort.breakRang");
        rfFcRaum = getSwingFactory().createReferenceField("hvt.standort.fc");
        rfFcRaum.setPreferredSize(new Dimension(200, 25));
        rfBetriebsraum = getSwingFactory().createReferenceField("hvt.standort.br");
        rfBetriebsraum.setPreferredSize(new Dimension(200, 25));
        tfClusterId = getSwingFactory().createTextField("hvt.standort.clusterId");
        cbStartFreq = getSwingFactory().createComboBox("hvt.standort.gfast.startfreq",
                new AKCustomListCellRenderer<>(String.class, String::intern));


        // @formatter:off
        AKJPanel left = new AKJPanel(new GridBagLayout());
        left.setMaximumSize(new Dimension(100, 400));
        left.add(new AKJPanel(),   GBCFactory.createGBC(  0,   0, 0, 0, 1, 1, GridBagConstraints.NONE));
        left.add(lblGruppe,        GBCFactory.createGBC(  0,   0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(),   GBCFactory.createGBC(  0,   0, 2, 0, 1, 1, GridBagConstraints.NONE));
        left.add(rfHVTGruppe,      GBCFactory.createGBC(100,   0, 3, 0, 2, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblASB,           GBCFactory.createGBC(  0,   0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfASB,            GBCFactory.createGBC(100,   0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(btnCreateASB,     GBCFactory.createGBC(  0,   0, 4, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblReal,          GBCFactory.createGBC(  0,   0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(cbReal,           GBCFactory.createGBC(100,   0, 3, 2, 2, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblBeschreibung,  GBCFactory.createGBC(  0,   0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfBeschreibung,   GBCFactory.createGBC(100,   0, 3, 3, 2, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblCarrier,       GBCFactory.createGBC(  0,   0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(cbCarrier,        GBCFactory.createGBC(100,   0, 3, 4, 2, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblCKennung,      GBCFactory.createGBC(  0,   0, 1, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(cbCarrierKennung, GBCFactory.createGBC(100,   0, 3, 5, 2, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblCContact,      GBCFactory.createGBC(  0,   0, 1, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(cbCarrierContact, GBCFactory.createGBC(100,   0, 3, 6, 2, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblStandortTyp,   GBCFactory.createGBC(  0,   0, 1, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(cbStandortTyp,    GBCFactory.createGBC(100,   0, 3, 7, 2, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblStartFreq,     GBCFactory.createGBC(  0,   0, 1, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(cbStartFreq,      GBCFactory.createGBC(100,   0, 3, 9, 2, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(),   GBCFactory.createGBC(  0,   0, 3, 9, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel mid = new AKJPanel(new GridBagLayout());
        mid.setMaximumSize(new Dimension(100, 400));
        mid.add(new AKJPanel(),    GBCFactory.createGBC(  0,   0, 0, 0, 1, 1, GridBagConstraints.NONE));
        mid.add(lblGueltigVon,     GBCFactory.createGBC(  0,   0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(new AKJPanel(),    GBCFactory.createGBC(  0,   0, 2, 0, 1, 1, GridBagConstraints.NONE));
        mid.add(dcGueltigVon,      GBCFactory.createGBC(  0,   0, 3, 0, 5, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblGueltigBis,     GBCFactory.createGBC(  0,   0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(dcGueltigBis,      GBCFactory.createGBC(  0,   0, 3, 1, 5, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblEWSD1,          GBCFactory.createGBC(  0,   0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(tfEWSD1,           GBCFactory.createGBC(100,   0, 3, 2, 5, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblEWSD2,          GBCFactory.createGBC(  0,   0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(tfEWSD2,           GBCFactory.createGBC(100,   0, 3, 3, 5, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblVirtuell,       GBCFactory.createGBC(  0,   0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(chbVirtuell,       GBCFactory.createGBC(  0,   0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblCPSProv,        GBCFactory.createGBC(  0,   0, 5, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(chbCPSProv,        GBCFactory.createGBC(  0,   0, 7, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblAutomatic,      GBCFactory.createGBC(  0,   0, 5, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(chbAutomatic,      GBCFactory.createGBC(  0,   0, 7, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblBreakRang,      GBCFactory.createGBC(  0,   0, 1, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(chbBreakRang,      GBCFactory.createGBC(  0,   0, 3, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblFcRaum,         GBCFactory.createGBC(  0,   0, 1, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(rfFcRaum,          GBCFactory.createGBC(100,   0, 3, 6, 5, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblBetriebsraum,   GBCFactory.createGBC(  0,   0, 1, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(rfBetriebsraum,    GBCFactory.createGBC(100,   0, 3, 7, 5, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblClusterId,      GBCFactory.createGBC(  0,   0, 1, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(tfClusterId,       GBCFactory.createGBC(100,   0, 3, 8, 5, 1, GridBagConstraints.HORIZONTAL));
        mid.add(new AKJPanel(),    GBCFactory.createGBC(  0,   0, 3, 9, 1, 1, GridBagConstraints.BOTH));

        AKJPanel right = new AKJPanel(new GridBagLayout());
        right.add(spTechnik,       GBCFactory.createGBC(100, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));

        AKJPanel detailPanel = new AKJPanel(new GridBagLayout());
        detailPanel.getAccessibleContext().setAccessibleName("hvt.standort");
        detailPanel.add(left,           GBCFactory.createGBC(  0,   0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPanel.add(new AKJPanel(), GBCFactory.createGBC(  0,   0, 1, 0, 1, 1, GridBagConstraints.NONE));
        detailPanel.add(mid,            GBCFactory.createGBC(  0,   0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPanel.add(new AKJPanel(), GBCFactory.createGBC(  0,   0, 3, 0, 1, 1, GridBagConstraints.NONE));
        detailPanel.add(right,          GBCFactory.createGBC(100,   0, 4, 0, 1, 1, GridBagConstraints.BOTH));
        detailPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 0, 1, 1, 1, GridBagConstraints.BOTH));
        // @formatter:on

        this.setLayout(new BorderLayout());
        this.add(detailPanel, BorderLayout.CENTER);

        //GUI Elemente sperren
        enableGUI(false);
    }

    @Override
    public final void loadData() {
        if (!loaded) {
            loaded = true;
            try {
                setWaitCursor();
                showProgressBar("laden...");

                HVTService service = getCCService(HVTService.class);

                CarrierService cs = getCCService(CarrierService.class);
                List<Carrier> carrier = cs.findCarrier();
                cbCarrier.addItems(carrier, true, Carrier.class);
                cbCarrier.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISP_VALUE);

                List<HVTTechnik> techniken = service.findHVTTechniken();
                tbMdlTechnik.setData(techniken);

                ReferenceService rs = getCCService(ReferenceService.class);
                List<Reference> typen = rs.findReferencesByType(Reference.REF_TYPE_STANDORT_TYP, true);
                cbStandortTyp.addItems(typen, true, Reference.class);
                cbStandortTyp.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISP_VALUE);

                List<HVTGruppe> view = service.findHVTGruppen();
                Collections.sort(view); //HUR-23815 - Alphabetical sorting of drop-down entries for HVT selection
                rfHVTGruppe.addItems(view, true, HVTGruppe.class);
                rfHVTGruppe.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISP_VALUE);

                QueryHvtGruppeStandorService queryService = getCCService(QueryHvtGruppeStandorService.class);
                rfBetriebsraum.setFindService(queryService);
                rfFcRaum.setFindService(queryService);

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
    }

    protected void refreshHVTGruppen() {
        try {
            setWaitCursor();
            showProgressBar("laden...");
            if (detail != null) {
                rfHVTGruppe.removeAllItems();
                HVTService service = getCCService(HVTService.class);
                List<HVTGruppe> view = service.findHVTGruppen();
                Collections.sort(view); //HUR-23815 - Alphabetical sorting of drop-down entries for HVT selection
                rfHVTGruppe.addItems(view, true, HVTGruppe.class);
                rfHVTGruppe.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISP_VALUE);
                rfHVTGruppe.selectItem("getId", HVTGruppe.class, detail.getHvtGruppeId());
            }
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

    @Override
    public void showDetails(Object details) {
        // load data first on show details. in loaded only once, protected by "loaded" flag
        loadData();

        if (details instanceof HVTStandort) {
            this.detail = (HVTStandort) details;
            tfASB.setText(detail.getAsb());
            cbReal.selectItem("toString", String.class, detail.getGesicherteRealisierung());
            tfBeschreibung.setText(detail.getBeschreibung());
            dcGueltigVon.setDate(detail.getGueltigVon());
            dcGueltigBis.setDate(detail.getGueltigBis());
            tfEWSD1.setText(detail.getEwsdOr1());
            tfEWSD2.setText(detail.getEwsdOr2());
            chbVirtuell.setSelected(detail.getVirtuell());
            chbCPSProv.setSelected(detail.getCpsProvisioning());
            chbAutomatic.setSelected(detail.getAutoVerteilen());
            chbBreakRang.setSelected(detail.getBreakRangierung());
            cbCarrier.selectItem("getId", Carrier.class, detail.getCarrierId());
            cbCarrierKennung.selectItem("getId", CarrierKennung.class, detail.getCarrierKennungId());
            cbCarrierContact.selectItem("getId", CarrierContact.class, detail.getCarrierContactId());
            rfFcRaum.setReferenceId(detail.getFcRaumId());
            rfBetriebsraum.setReferenceId(detail.getBetriebsraumId());
            rfHVTGruppe.selectItem("getId", HVTGruppe.class, detail.getHvtGruppeId());
            cbStandortTyp.selectItem("getId", Reference.class, detail.getStandortTypRefId());
            tfClusterId.setText(detail.getClusterId());

            try {
                HWService hwService = getCCService(HWService.class);
                boolean isGfastStandort = hwService.standortContainsGFastTech(detail.getHvtIdStandort());
                cbStartFreq.setVisible(isGfastStandort);
                if (isGfastStandort) {
                    cbStartFreq.setVisible(true);
                    ProfileService profileService = getCCService(ProfileService.class);
                    List<String> lineSpectrumValues = profileService.findLineSpectrumValues();
                    cbStartFreq.addItems(lineSpectrumValues);
                    cbStartFreq.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISP_VALUE);
                    cbStartFreq.setSelectedItem(detail.getGfastStartfrequenz() != null ?
                            detail.getGfastStartfrequenz() : profileService.findLineSpectrumDefaultValue());
                }
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }

            try {
                hvtTechniken.clear();
                HVTService hvts = getCCService(HVTService.class);
                List<HVTTechnik> techniken = hvts.findHVTTechniken4Standort(detail.getId());
                if (techniken != null) {
                    CollectionMapConverter.convert2Map(techniken, hvtTechniken, "getId", null);
                }
                tbMdlTechnik.fireTableDataChanged();
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        else {
            clear();
        }

        // Setze editable-Attribute aller GUI-Elemente
        enableGUI = true;
        enableGUI(enableGUI);
    }

    /* 'Loescht' alle Felder */
    private void clear() {
        this.detail = null;
        GuiTools.cleanFields(this);
        hvtTechniken.clear();
        cbCarrier.setSelectedIndex(0);
        cbCarrierKennung.setSelectedIndex(0);
        cbCarrierContact.setSelectedIndex(0);
        rfFcRaum.setReferenceId(null);
        rfBetriebsraum.setReferenceId(null);
        cbStartFreq.removeAllItems();
    }

    @Override
    public void createNew() {
        if (detail == null) {
            loadData();
        }
        clear();
        // Setze editable-Attribute aller GUI-Elemente
        enableGUI = true;
        enableGUI(enableGUI);
        parent.lockForNewStandort();
        dcGueltigVon.setDate(new Date());
        dcGueltigBis.setDate(DateTools.getHurricanEndDate());
    }

    @Override
    public void saveData() {
        if ( rfHVTGruppe.getSelectedItem() == null
                || cbCarrier.getSelectedItem() == null) {
            LOGGER.warn("Cannot save data due to missing required fields ");
            return;
        }

        boolean isNew = false;
        try {
            if (this.detail == null) {
                this.detail = new HVTStandort();
                isNew = true;
            }

            detail.setHvtGruppeId(((HVTGruppe) rfHVTGruppe.getSelectedItem()).getId());
            detail.setCarrierId(((Carrier) cbCarrier.getSelectedItem()).getId());
            detail.setCarrierKennungId(((CarrierKennung) cbCarrierKennung.getSelectedItem()).getId());
            detail.setCarrierContactId(((CarrierContact) cbCarrierContact.getSelectedItem()).getId());
            detail.setFcRaumId(getFcRaumId());
            detail.setBetriebsraumId(getBetriebsraumId());
            detail.setStandortTypRefId(((Reference) cbStandortTyp.getSelectedItem()).getId());
            detail.setGesicherteRealisierung((String) cbReal.getSelectedItemValue("toString", String.class));
            detail.setBeschreibung(tfBeschreibung.getText());
            detail.setAsb(tfASB.getTextAsInt(null));
            detail.setEwsdOr1(tfEWSD1.getTextAsInt(null));
            detail.setEwsdOr2(tfEWSD2.getTextAsInt(null));
            detail.setGueltigVon(dcGueltigVon.getDate(null));
            detail.setGueltigBis(dcGueltigBis.getDate(null));
            detail.setVirtuell(chbVirtuell.isSelectedBoolean());
            detail.setCpsProvisioning(chbCPSProv.isSelectedBoolean());
            detail.setAutoVerteilen(chbAutomatic.isSelectedBoolean());
            detail.setBreakRangierung(chbBreakRang.isSelectedBoolean());
            detail.setClusterId(tfClusterId.getText());
            if (cbStartFreq.getModel().getSize() > 0)
                detail.setGfastStartfrequenz(cbStartFreq.getSelectedItem().toString());

            HVTService service = getCCService(HVTService.class);
            service.saveHVTStandort(detail);

            List<Long> technikIds = new ArrayList<Long>();
            Iterator<Long> technikIt = hvtTechniken.keySet().iterator();
            while (technikIt.hasNext()) {
                Long next = technikIt.next();
                technikIds.add(next);
            }
            service.saveHVTTechniken4Standort(detail.getId(), technikIds);

            if (isNew) {
                final HVTGruppe hvtGruppe = service.findHVTGruppeById(((HVTGruppe) rfHVTGruppe.getSelectedItem()).getId());
                parent.hvtGruppen.put(hvtGruppe.getId(), hvtGruppe);
                parent.hvtGruppenAdminPanel.addToGruppenList(hvtGruppe);
                parent.updateTable(detail);
                parent.showDetails(detail);
            }
        }
        catch (Exception e) {
            if (isNew) { // speichern des neu erstellten Objektes fehlgeschlagen, Ausgangszustand wieder herstellen
                this.detail = null;
            }
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    private Long getFcRaumId() {
        HVTGruppeStdView hvtGruppeStdView = (HVTGruppeStdView) rfFcRaum.getReferenceObject();
        return (hvtGruppeStdView != null)? hvtGruppeStdView.getHvtIdStandort(): null;
    }

    private Long getBetriebsraumId() {
        HVTGruppeStdView hvtGruppeStdView = (HVTGruppeStdView) rfBetriebsraum.getReferenceObject();
        return (hvtGruppeStdView != null)? hvtGruppeStdView.getHvtIdStandort(): null;
    }

    // Funktion setzt die editable-Attribute der einzelnen GUI-Elemente
    private void enableGUI(boolean enable) {
        GuiTools.enableContainerComponents(this, enable);
        btnCreateASB.setEnabled(enable);
    }

    @Override
    protected void execute(String command) {
        if ("create.asb.kennung".equals(command)) {
            if (Carrier.ID_DTAG.equals(((Carrier) cbCarrier.getSelectedItem()).getId())) {
                try {
                    String result = MessageHelper.showInputDialog(this,
                            "Bitte die ASB-Kennung für diesen HVT eingeben (3-stellige Zahl):", "000");
                    if ((StringUtils.isNotBlank(result)) && (result.length() <= 3)) {
                        Integer asb;
                        try {
                            asb = Integer.valueOf(result);
                        }
                        catch (NumberFormatException e) {
                            LOGGER.error(e, e);
                            MessageHelper.showInfoDialog(this, "Es wurde keine gültige ASB-Kennung für diesen HVT eingegeben.");
                            return;
                        }
                        Integer asbVal = getCCService(HVTService.class).generateAsb4HVTStandort(asb);
                        tfASB.setText(asbVal.toString());
                    }
                    else {
                        MessageHelper.showInfoDialog(this, "Es wurde keine gültige ASB-Kennung für diesen HVT eingegeben.");
                    }
                }
                catch (Exception e) {
                    LOGGER.error(e, e);
                    MessageHelper.showErrorDialog(this, e);
                }
            }
            else {
                MessageHelper.showInfoDialog(this, "Generierung einer ASB-Kennung nur für HVTs von DTAG möglich.");
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        // intentionally left blank
    }


    /* TableModel zur Anzeige der HVT-Techniken, die dem Standort zugeordnet sind. */
    class HVTTechnikTableModel extends AKTableModel<HVTTechnik> {
        static final int COL_NAME = 0;
        static final int COL_ATTACHED = 1;

        static final int COL_COUNT = 2;

        @Override
        public int getColumnCount() {
            return COL_COUNT;
        }

        @Override
        public String getColumnName(int column) {
            switch (column) {
                case COL_NAME:
                    return "HVT-Technik";
                case COL_ATTACHED:
                    return "vorhanden";
                default:
                    return "";
            }
        }

        @Override
        public Object getValueAt(int row, int column) {
            Object o = getDataAtRow(row);
            if (o instanceof HVTTechnik) {
                HVTTechnik technik = (HVTTechnik) o;
                switch (column) {
                    case COL_NAME:
                        return technik.getHersteller();
                    case COL_ATTACHED:
                        return
                                (((hvtTechniken != null) && hvtTechniken.containsKey(technik.getId())))
                                        ? Boolean.TRUE : Boolean.FALSE;
                    default:
                        break;
                }
            }
            return null;
        }

        @Override
        public void setValueAt(Object aValue, int row, int column) {
            Object o = getDataAtRow(row);
            if (o instanceof HVTTechnik) {
                HVTTechnik h2t = (HVTTechnik) o;
                if (hvtTechniken == null) {
                    hvtTechniken = new HashMap<Long, HVTTechnik>();
                }

                if (aValue instanceof Boolean) {
                    if (((Boolean) aValue).booleanValue()) {
                        hvtTechniken.put(h2t.getId(), null);
                    }
                    else {
                        hvtTechniken.remove(h2t.getId());
                    }
                }
            }
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return (column == COL_ATTACHED) ? true : false;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return (columnIndex == COL_ATTACHED) ? Boolean.class : super.getColumnClass(columnIndex);
        }
    }


    /**
     * Item-Listener, um auf Aenderungen der ComboBox 'Carrier' zu reagieren. Ermittelt die zum Carrier gehoerenden
     * Kontakte und uebergibt diese der ComboBox 'Carrier-Kontakt'.
     */
    class CBCarrierItemListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if ((e.getSource() == cbCarrier) && (e.getStateChange() == ItemEvent.SELECTED)) {
                cbCarrierContact.removeAllItems();
                cbCarrierKennung.removeAllItems();

                try {
                    List<CarrierContact> carrierContacts = null;
                    List<CarrierKennung> carrierKennungen = null;
                    if (cbCarrier.getSelectedItem() != null) {
                        Carrier carrier = (Carrier) cbCarrier.getSelectedItem();
                        CarrierContact example = new CarrierContact();
                        example.setCarrierId(carrier.getId());

                        QueryCCService queryService = getCCService(QueryCCService.class);
                        carrierContacts = queryService.findByExample(example, CarrierContact.class);

                        CarrierKennung carrierKennungExample = new CarrierKennung();
                        carrierKennungExample.setCarrierId(carrier.getId());
                        carrierKennungen = queryService.findByExample(carrierKennungExample, CarrierKennung.class);
                    }

                    cbCarrierContact.addItems(carrierContacts, true, CarrierContact.class);
                    cbCarrierKennung.addItems(carrierKennungen, true, CarrierKennung.class);
                }
                catch (Exception ex) {
                    LOGGER.error(ex.getMessage(), ex);
                    MessageHelper.showErrorDialog(getMainFrame(), ex);
                }
            }
        }
    }

}


