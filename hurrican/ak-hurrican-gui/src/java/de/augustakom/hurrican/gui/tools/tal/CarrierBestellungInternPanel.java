/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.06.2007 14:04:58
 */
package de.augustakom.hurrican.gui.tools.tal;

import static javax.swing.JTable.*;
import static javax.swing.ListSelectionModel.*;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJSplitPane;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReferenceAwareTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.gui.swing.table.AKTableOwner;
import de.augustakom.common.gui.swing.table.ReflectionTableMetaData;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.auftrag.AuftragDataFrame;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.base.TableOpenOrderAction;
import de.augustakom.hurrican.gui.shared.AssignEquipmentDialog;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.cc.view.CBVorgangView;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.ReferenceService;


/**
 * Panel fuer die Darstellung und Bearbeitung der internen TAL-Bestellungen.
 */
public class CarrierBestellungInternPanel extends AbstractServicePanel implements AKDataLoaderComponent,
        AKTableOwner, AKObjectSelectionListener {

    private static final Logger LOGGER = Logger.getLogger(CarrierBestellungInternPanel.class);
    private static final long serialVersionUID = 2561745576586689123L;
    public static final String STATUS_COL_NAME = "Status";

    // GUI-Komponenten
    private CBVorgangTable tbOrders = null;
    private AKReferenceAwareTableModel<CBVorgangView> tbMdlOrders = null;
    private AKJButton btnAssignPort = null;
    private AKJButton btnFreePort = null;
    private AKJButton btnWorkOn = null;
    private AKJComboBox cbConfirm = null;
    private AKJTextField tfLBZ = null;
    private AKJTextField tfVtrnr = null;
    private AKJTextField tfAQS = null;
    private AKJTextField tfLL = null;
    private AKJComboBox cbVorOrt = null;
    private AKJDateComponent dcVorgabeMnet = null;
    private AKJDateComponent dcRealDate = null;
    private AKJTextArea taBemerkung = null;
    private AKJTextArea taMontagehinweis = null;
    private AKJTextField tfEName = null;
    private AKJTextField tfEEndstelle = null;
    private AKJTextField tfEPLZ = null;
    private AKJTextField tfEOrt = null;
    private AKJTextField tfEHvt = null;
    private AKJTextField tfEqInVert = null;
    private AKJTextField tfEqInBucht = null;
    private AKJTextField tfEqInLeiste = null;
    private AKJTextField tfEqInStift = null;
    private AKJTextField tfEqInEQN = null;
    private AKJTextField tfEqOutVert = null;
    private AKJTextField tfEqOutBucht = null;
    private AKJTextField tfEqOutLeiste = null;
    private AKJTextField tfEqOutStift = null;
    private AKJTextField tfEqOutEQN = null;

    // sonstiges
    private CBVorgang actCBVorgang = null;
    private CBVorgangView actCBVorgangView = null;
    private boolean defaultsLoaded = false;

    /**
     * Default-Konst.
     */
    public CarrierBestellungInternPanel() {
        super("de/augustakom/hurrican/gui/tools/tal/resources/CarrierBestellungInternPanel.xml");
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        tbMdlOrders = new AKReferenceAwareTableModel<>(
                new ReflectionTableMetaData(VerbindungsBezeichnung.VBZ_BEZEICHNUNG, "vbz", String.class),
                new ReflectionTableMetaData("Tech. Auftragsnr.", "auftragId", String.class),
                new ReflectionTableMetaData("Produkt", "produkt", String.class),
                new ReflectionTableMetaData("eingestellt am", "submittedAt", Date.class),
                new ReflectionTableMetaData("Vorgabe", "vorgabeMnet", Date.class),
                new ReflectionTableMetaData("Typ", "typ", String.class),
                new ReflectionTableMetaData(STATUS_COL_NAME, "status", String.class),
                new ReflectionTableMetaData("HVT", "hvt", String.class));
        tbOrders = new CBVorgangTable(tbMdlOrders, AUTO_RESIZE_OFF, SINGLE_SELECTION, STATUS_COL_NAME);
        tbOrders.attachSorter();
        tbOrders.fitTable(new int[] { 100, 90, 110, 80, 80, 110, 110, 200 });
        tbOrders.addTableListener(this);
        tbOrders.addPopupAction(new TableOpenOrderAction());
        tbOrders.addMouseListener(new AKTableDoubleClickMouseListener(this));
        AKJScrollPane spOrders = new AKJScrollPane(tbOrders, new Dimension(700, 180));

        AKJLabel lblConfirm = getSwingFactory().createLabel("confirm");
        AKJLabel lblVorgabeMnet = getSwingFactory().createLabel("vorgabe.mnet");
        AKJLabel lblRealDate = getSwingFactory().createLabel("real.date");
        AKJLabel lblLBZ = getSwingFactory().createLabel("lbz");
        AKJLabel lblVtrnr = getSwingFactory().createLabel("vtrnr");
        AKJLabel lblAQS = getSwingFactory().createLabel("aqs");
        AKJLabel lblLL = getSwingFactory().createLabel("laenge");
        AKJLabel lblVorOrt = getSwingFactory().createLabel("kunde.vor.ort");
        AKJLabel lblBemerkung = getSwingFactory().createLabel("description");

        AKJLabel lblMontagehinweis;
        lblMontagehinweis = getSwingFactory().createLabel("montagehinweis");

        AKJLabel lblEndstelle = getSwingFactory().createLabel("endstelle", AKJLabel.LEFT, Font.BOLD);
        AKJLabel lblEName = getSwingFactory().createLabel("es.name");
        AKJLabel lblEEndstelle = getSwingFactory().createLabel("es.strasse");
        AKJLabel lblEPLZ = getSwingFactory().createLabel("es.plz.ort");
        AKJLabel lblEHvt = getSwingFactory().createLabel("es.hvt");
        AKJLabel lblEqIn = getSwingFactory().createLabel("eq.in", AKJLabel.LEFT, Font.BOLD);
        AKJLabel lblEqInVert = getSwingFactory().createLabel("eq.in.verteiler");
        AKJLabel lblEqInLeiste = getSwingFactory().createLabel("eq.in.leiste");
        AKJLabel lblEqInEQN = getSwingFactory().createLabel("eq.in.eqn");
        AKJLabel lblEqOut = getSwingFactory().createLabel("eq.out", AKJLabel.LEFT, Font.BOLD);
        AKJLabel lblEqOutVert = getSwingFactory().createLabel("eq.out.verteiler");
        AKJLabel lblEqOutLeiste = getSwingFactory().createLabel("eq.out.leiste");
        AKJLabel lblEqOutEQN = getSwingFactory().createLabel("eq.out.eqn");

        cbConfirm = getSwingFactory().createComboBox("confirm");
        dcVorgabeMnet = getSwingFactory().createDateComponent("vorgabe.mnet", false);
        dcRealDate = getSwingFactory().createDateComponent("real.date");
        tfLBZ = getSwingFactory().createTextField("lbz");
        tfVtrnr = getSwingFactory().createTextField("vtrnr");
        tfAQS = getSwingFactory().createTextField("aqs");
        tfLL = getSwingFactory().createTextField("laenge");
        cbVorOrt = getSwingFactory().createComboBox("kunde.vor.ort");
        taBemerkung = getSwingFactory().createTextArea("description");
        AKJScrollPane spBemerkung = new AKJScrollPane(taBemerkung);
        taMontagehinweis = getSwingFactory().createTextArea("montagehinweis", false);

        AKJScrollPane spMontagehinweis = new AKJScrollPane(taMontagehinweis);
        tfEName = getSwingFactory().createTextField("es.name", false);
        tfEEndstelle = getSwingFactory().createTextField("es.strasse", false);
        tfEPLZ = getSwingFactory().createTextField("es.plz", false);
        tfEOrt = getSwingFactory().createTextField("es.ort", false);
        tfEHvt = getSwingFactory().createTextField("es.hvt", false);
        tfEqInVert = getSwingFactory().createTextField("eq.in.verteiler", false);
        tfEqInBucht = getSwingFactory().createTextField("eq.in.bucht", false);
        tfEqInLeiste = getSwingFactory().createTextField("eq.in.leite", false);
        tfEqInStift = getSwingFactory().createTextField("eq.in.stift", false);
        tfEqInEQN = getSwingFactory().createTextField("eq.in.eqn", false);
        tfEqOutVert = getSwingFactory().createTextField("eq.out.verteiler", false);
        tfEqOutBucht = getSwingFactory().createTextField("eq.out.bucht", false);
        tfEqOutLeiste = getSwingFactory().createTextField("eq.out.leite", false);
        tfEqOutStift = getSwingFactory().createTextField("eq.out.stift", false);
        tfEqOutEQN = getSwingFactory().createTextField("eq.out.eqn", false);

        btnAssignPort = getSwingFactory().createButton("assign.port", getActionListener());
        btnFreePort = getSwingFactory().createButton("free.port", getActionListener());
        btnWorkOn = getSwingFactory().createButton("work.on", getActionListener());
        AKJButton btnSave = getSwingFactory().createButton("save", getActionListener());

        AKJPanel fctPnl = new AKJPanel(new GridBagLayout(), getSwingFactory().getText("functions"));
        fctPnl.add(btnWorkOn, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        fctPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        fctPnl.add(btnAssignPort, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        fctPnl.add(btnFreePort, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        fctPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 4, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel esPnl = new AKJPanel(new GridBagLayout());
        esPnl.add(lblEndstelle, GBCFactory.createGBC(0, 0, 0, 0, 4, 1, GridBagConstraints.HORIZONTAL));
        esPnl.add(lblEName, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        esPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.NONE));
        esPnl.add(tfEName, GBCFactory.createGBC(0, 0, 2, 1, 2, 1, GridBagConstraints.HORIZONTAL));
        esPnl.add(lblEEndstelle, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        esPnl.add(tfEEndstelle, GBCFactory.createGBC(0, 0, 2, 2, 2, 1, GridBagConstraints.HORIZONTAL));
        esPnl.add(lblEPLZ, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        esPnl.add(tfEPLZ, GBCFactory.createGBC(0, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        esPnl.add(tfEOrt, GBCFactory.createGBC(0, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        esPnl.add(lblEHvt, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        esPnl.add(tfEHvt, GBCFactory.createGBC(0, 0, 2, 4, 2, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel eqPnl = new AKJPanel(new GridBagLayout());
        eqPnl.add(lblEqIn, GBCFactory.createGBC(0, 0, 0, 0, 4, 1, GridBagConstraints.HORIZONTAL));
        eqPnl.add(lblEqInVert, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        eqPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.NONE));
        eqPnl.add(tfEqInVert, GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        eqPnl.add(tfEqInBucht, GBCFactory.createGBC(0, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        eqPnl.add(lblEqInLeiste, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        eqPnl.add(tfEqInLeiste, GBCFactory.createGBC(0, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        eqPnl.add(tfEqInStift, GBCFactory.createGBC(0, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        eqPnl.add(lblEqInEQN, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        eqPnl.add(tfEqInEQN, GBCFactory.createGBC(0, 0, 2, 3, 2, 1, GridBagConstraints.HORIZONTAL));
        eqPnl.add(lblEqOut, GBCFactory.createGBC(0, 0, 0, 4, 4, 1, GridBagConstraints.HORIZONTAL));
        eqPnl.add(lblEqOutVert, GBCFactory.createGBC(0, 0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        eqPnl.add(tfEqOutVert, GBCFactory.createGBC(0, 0, 2, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        eqPnl.add(tfEqOutBucht, GBCFactory.createGBC(0, 0, 3, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        eqPnl.add(lblEqOutLeiste, GBCFactory.createGBC(0, 0, 0, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        eqPnl.add(tfEqOutLeiste, GBCFactory.createGBC(0, 0, 2, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        eqPnl.add(tfEqOutStift, GBCFactory.createGBC(0, 0, 3, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        eqPnl.add(lblEqOutEQN, GBCFactory.createGBC(0, 0, 0, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        eqPnl.add(tfEqOutEQN, GBCFactory.createGBC(0, 0, 2, 7, 2, 1, GridBagConstraints.HORIZONTAL));
        eqPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 8, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel detPnl = new AKJPanel(new GridBagLayout(), getSwingFactory().getText("details"));
        detPnl.add(lblConfirm, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        detPnl.add(cbConfirm, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.NONE));
        detPnl.add(lblVorOrt, GBCFactory.createGBC(0, 0, 4, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 5, 0, 1, 1, GridBagConstraints.NONE, 10));
        detPnl.add(cbVorOrt, GBCFactory.createGBC(0, 0, 6, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(lblVorgabeMnet, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(dcVorgabeMnet, GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(lblBemerkung, GBCFactory.createGBC(0, 0, 4, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(spBemerkung, GBCFactory.createGBC(0, 0, 6, 1, 1, 3, GridBagConstraints.HORIZONTAL));
        detPnl.add(lblRealDate, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(dcRealDate, GBCFactory.createGBC(0, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(lblLBZ, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(tfLBZ, GBCFactory.createGBC(0, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(lblVtrnr, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(tfVtrnr, GBCFactory.createGBC(0, 0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(lblMontagehinweis, GBCFactory.createGBC(0, 0, 4, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(spMontagehinweis, GBCFactory.createGBC(0, 0, 6, 4, 1, 3, GridBagConstraints.HORIZONTAL));
        detPnl.add(lblLL, GBCFactory.createGBC(0, 0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(tfLL, GBCFactory.createGBC(0, 0, 2, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(lblAQS, GBCFactory.createGBC(0, 0, 0, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(tfAQS, GBCFactory.createGBC(0, 0, 2, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 7, 7, 1, 1, GridBagConstraints.BOTH));
        detPnl.add(btnSave, GBCFactory.createGBC(0, 0, 0, 8, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel hwPnl = new AKJPanel(new GridBagLayout(), getSwingFactory().getText("physik"));
        hwPnl.add(esPnl, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        hwPnl.add(eqPnl, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        hwPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 1, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel downPnl = new AKJPanel(new BorderLayout());
        downPnl.add(fctPnl, BorderLayout.WEST);
        downPnl.add(detPnl, BorderLayout.CENTER);
        downPnl.add(hwPnl, BorderLayout.EAST);

        AKJSplitPane split = new AKJSplitPane(AKJSplitPane.VERTICAL_SPLIT, true);
        split.setTopComponent(spOrders);
        split.setBottomComponent(new AKJScrollPane(downPnl));
        split.setDividerSize(2);
        split.setResizeWeight(1d);  // Top-Panel erhaelt komplette Ausdehnung!

        this.setLayout(new BorderLayout());
        this.add(split, BorderLayout.CENTER);

        manageGUI(btnSave, btnAssignPort, btnFreePort);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            setWaitCursor();
            tbMdlOrders.removeAll();
            showDetails(null);
            if (!defaultsLoaded) {
                ReferenceService rs = getCCService(ReferenceService.class);
                List<Reference> typen = rs.findReferencesByType(Reference.REF_TYPE_TAL_BESTELLUNG_TYP, Boolean.FALSE);
                Map<Long, Reference> typMap = new HashMap<>();
                CollectionMapConverter.convert2Map(typen, typMap, "getId", null);

                List<Reference> stati = rs.findReferencesByType(Reference.REF_TYPE_TAL_BESTELLUNG_STATUS, Boolean.FALSE);
                Map<Long, Reference> statiMap = new HashMap<>();
                CollectionMapConverter.convert2Map(stati, statiMap, "getId", null);

                tbMdlOrders.addReference(5, typMap, "strValue");
                tbMdlOrders.addReference(6, statiMap, "strValue");

                defaultsLoaded = true;
            }

            CarrierElTALService talService = getCCService(CarrierElTALService.class);
            List<CBVorgangView> openCBVorgaenge = talService.findOpenCBVorgaenge4Intern();
            tbMdlOrders.setData(openCBVorgaenge);

            doValidate();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            setDefaultCursor();
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.table.AKTableOwner#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {
        GuiTools.cleanFields(this);
        this.actCBVorgang = null;
        this.actCBVorgangView = null;
        if (details instanceof CBVorgangView) {
            try {
                setWaitCursor();
                this.actCBVorgangView = (CBVorgangView) details;

                CarrierElTALService talService = getCCService(CarrierElTALService.class);
                actCBVorgang = talService.findCBVorgang(actCBVorgangView.getCbVorgangId());
                if (actCBVorgang == null) {
                    throw new HurricanGUIException("Der Bestellvorgang konnte nicht ermittelt werden!");
                }

                cbConfirm.selectItemWithValue(actCBVorgang.getReturnOk());
                dcVorgabeMnet.setDate(actCBVorgang.getVorgabeMnet());
                dcRealDate.setDate(actCBVorgang.getReturnRealDate());
                tfLBZ.setText(actCBVorgang.getReturnLBZ());
                tfVtrnr.setText(actCBVorgang.getReturnVTRNR());
                tfAQS.setText(actCBVorgang.getReturnAQS());
                tfLL.setText(actCBVorgang.getReturnLL());
                cbVorOrt.selectItemWithValue(actCBVorgang.getReturnKundeVorOrt());
                taBemerkung.setText(actCBVorgang.getReturnBemerkung());
                taMontagehinweis.setText(actCBVorgang.getMontagehinweis());

                EndstellenService esSrv = getCCService(EndstellenService.class);
                Endstelle esB = esSrv.findEndstelle4Auftrag(actCBVorgang.getAuftragId(), Endstelle.ENDSTELLEN_TYP_B);
                HVTService hvts = getCCService(HVTService.class);
                HVTGruppe hvtGruppe = hvts.findHVTGruppe4Standort(esB.getHvtIdStandort());
                tfEName.setText(esB.getName());
                tfEEndstelle.setText(esB.getEndstelle());
                tfEPLZ.setText(esB.getPlz());
                tfEOrt.setText(esB.getOrt());
                tfEHvt.setText(hvtGruppe.getOrtsteil());

                RangierungsService rs = getCCService(RangierungsService.class);
                Rangierung rang = rs.findRangierung(esB.getRangierId());
                if ((rang != null) && (rang.getEqInId() != null)) {
                    Equipment eqIn = rs.findEquipment(rang.getEqInId());
                    if (eqIn != null) {
                        tfEqInVert.setText(eqIn.getRangVerteiler());
                        tfEqInBucht.setText(eqIn.getRangBucht());
                        tfEqInLeiste.setText(eqIn.getRangLeiste1());
                        tfEqInStift.setText(eqIn.getRangStift1());
                        tfEqInEQN.setText(eqIn.getHwEQN());
                    }

                    if (rang.getEqOutId() != null) {
                        Equipment eqOut = rs.findEquipment(rang.getEqOutId());
                        if (eqOut != null) {
                            tfEqOutVert.setText(eqOut.getRangVerteiler());
                            tfEqOutBucht.setText(eqOut.getRangBucht());
                            tfEqOutLeiste.setText(eqOut.getRangLeiste1());
                            tfEqOutStift.setText(eqOut.getRangStift1());
                            tfEqOutEQN.setText(eqOut.getHwEQN());
                        }
                    }
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
            finally {
                setDefaultCursor();
            }
        }
        doValidate();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("assign.port".equals(command)) {
            assignPort();
        }
        else if ("free.port".equals(command)) {
            freePort();
        }
        else if ("work.on".equals(command)) {
            workOn();
        }
        else if ("save".equals(command)) {
            save();
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKObjectSelectionListener#objectSelected(java.lang.Object)
     */
    @Override
    public void objectSelected(Object selection) {
        if (selection instanceof CCAuftragModel) {
            AuftragDataFrame.openFrame((CCAuftragModel) selection);
        }
    }

    /*
     * Setzt den aktuellen CB-Vorgang in den 'transferred' Status und
     * oeffnet dadurch die Felder.
     */
    private void workOn() {
        if (actCBVorgang != null) {
            try {
                setWaitCursor();
                CarrierElTALService talService = getCCService(CarrierElTALService.class);
                actCBVorgang.setStatus(CBVorgang.STATUS_TRANSFERRED);
                actCBVorgang.setCarrierBearbeiter(HurricanSystemRegistry.instance().getCurrentLoginName());
                actCBVorgang = talService.saveCBVorgang(actCBVorgang);

                actCBVorgangView.setStatus(actCBVorgang.getStatus());

                doValidate();
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
            finally {
                setDefaultCursor();
            }
        }
    }

    /**
     * Speichert das aktuelle Modell ab.
     */
    private void save() {
        try {
            setWaitCursor();
            if (actCBVorgang == null) {
                throw new HurricanGUIException("Waehlen Sie bitte zuerst eine TAL-Bestellung aus.");
            }

            String bemerkung = appendUserAndDateIfChanged(actCBVorgang.getReturnBemerkung(), taBemerkung.getText(null));
            if (CBVorgang.STATUS_SUBMITTED.equals(actCBVorgang.getStatus())) {
                // nur Bemerkung speichern, keine Validierung
                actCBVorgang.setReturnBemerkung(bemerkung);
            }
            else {
                actCBVorgang.setReturnOk((Boolean) cbConfirm.getSelectedItemValue());
                actCBVorgang.setReturnRealDate(dcRealDate.getDate(null));
                actCBVorgang.setReturnLBZ(tfLBZ.getText(null));
                actCBVorgang.setReturnAQS(tfAQS.getText(null));
                actCBVorgang.setReturnLL(tfLL.getText(null));
                actCBVorgang.setReturnVTRNR(tfVtrnr.getText(null));
                actCBVorgang.setReturnKundeVorOrt((Boolean) cbVorOrt.getSelectedItemValue());
                actCBVorgang.setReturnBemerkung(bemerkung);
                actCBVorgang.setCarrierBearbeiter(HurricanSystemRegistry.instance().getCurrentLoginName());
            }

            CarrierElTALService talService = getCCService(CarrierElTALService.class);
            CBVorgang cb = talService.saveCBVorgang(actCBVorgang);
            PropertyUtils.copyProperties(actCBVorgang, cb);

            actCBVorgangView.setStatus(actCBVorgang.getStatus());
            actCBVorgangView.setReturnOk(actCBVorgang.getReturnOk());
            taBemerkung.setText(actCBVorgang.getReturnBemerkung());
            tbOrders.repaint();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            setDefaultCursor();
        }
    }

    /* Bietet die Moeglichkeit, dem aktuellen CB-Vorgang einen Port zuzuordnen. */
    private void assignPort() {
        try {
            if (actCBVorgangView == null) {
                throw new HurricanGUIException("Bitte zuerst eine TAL-Bestellung auswaehlen!");
            }

            AssignEquipmentHelper.checkPortEQin(actCBVorgangView);

            RangierungsService rs = getCCService(RangierungsService.class);
            Rangierung[] rangs = rs.findRangierungen(
                    actCBVorgangView.getAuftragId(), Endstelle.ENDSTELLEN_TYP_B);
            if ((rangs == null) || (rangs.length < 1)) {
                throw new HurricanGUIException("Die Rangierung des Auftrags konnte nicht ermittelt werden!");
            }
            else if (rangs[0].getEqOutId() != null) {
                throw new HurricanGUIException("Die Rangierung des Auftrags hat bereits einen EQ-Out Port!");
            }

            EndstellenService esSrv = getCCService(EndstellenService.class);
            Endstelle endstelle = esSrv.findEndstelle(rangs[0].getEsId());

            AssignEquipmentDialog dlg = new AssignEquipmentDialog(endstelle,
                    AssignEquipmentDialog.ENABLE_RANG_STD, AssignEquipmentDialog.ENABLE_EQ_OUT);
            DialogHelper.showDialog(getMainFrame(), dlg, true, true);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /* Gibt einen EQ-Out Port frei. Die Rangierung wird dabei historisiert. */
    private void freePort() {
        try {
            if (actCBVorgangView == null) {
                throw new HurricanGUIException("Bitte zuerst eine TAL-Bestellung auswaehlen!");
            }

            RangierungsService rs = getCCService(RangierungsService.class);
            Rangierung[] rangs = rs.findRangierungen(
                    actCBVorgangView.getAuftragId(), Endstelle.ENDSTELLEN_TYP_B);
            if ((rangs == null) || (rangs.length < 1)) {
                throw new HurricanGUIException("Die Rangierung des Auftrags konnte nicht ermittelt werden!");
            }
            else if (rangs[0].getEqOutId() == null) {
                throw new HurricanGUIException("Die Rangierung besitzt keinen EQ-Out Port!");
            }

            Rangierung rangierung = rangs[0];
            Equipment eqOut = rs.findEquipment(rangierung.getEqOutId());
            if (!eqOut.isManagedByMNet()) {
                throw new HurricanGUIException("Der EQ-Out Port wird nicht von M-net verwaltet. " +
                        "Der Port kann deshalb nicht freigegeben werden!");
            }

            int option = MessageHelper.showYesNoQuestion(getMainFrame(),
                    getSwingFactory().getText("free.port.msg"), getSwingFactory().getText("free.port.title"));
            if (option == JOptionPane.YES_OPTION) {
                rs.breakRangierung(rangierung, false, true, true);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /* Validiert die Buttons und Bearbeitungsfelder */
    private void doValidate() {
        validateButtons();
        enableFields();
    }

    /* Validiert die Buttons fuer die Zuweisung bzw. Freigabe von Ports */
    private void validateButtons() {
        btnWorkOn.setEnabled(false);
        btnAssignPort.setEnabled(false);
        btnFreePort.setEnabled(false);
        if (actCBVorgang != null) {
            if (NumberTools.equal(actCBVorgang.getStatus(), CBVorgang.STATUS_SUBMITTED)) {
                btnWorkOn.setEnabled(true);
                btnAssignPort.setEnabled(false);
                btnFreePort.setEnabled(false);
            }
            else if (actCBVorgang.isKuendigung() || actCBVorgang.isStorno()) {
                btnWorkOn.setEnabled(false);
                btnAssignPort.setEnabled(false);
                btnFreePort.setEnabled(true);
            }
            else {
                btnWorkOn.setEnabled(false);
                btnAssignPort.setEnabled(true);
                btnFreePort.setEnabled(false);
            }
        }
    }

    /* Setzt die Bearbeitungsfelder je nach Status auf enabled/disabled */
    private void enableFields() {
        if (actCBVorgang != null) {
            if (CBVorgang.STATUS_SUBMITTED.equals(actCBVorgang.getStatus())) {
                // nur Bemerkung
                GuiTools.enableComponents(new Component[] { taBemerkung }, true, true);
                Component[] comps = new Component[] { cbConfirm, tfLBZ, tfVtrnr, tfAQS, tfLL,
                        cbVorOrt, dcVorgabeMnet, dcRealDate };
                GuiTools.enableComponents(comps, false, true);
            }
            else {
                boolean enableFields = NumberTools.isGreater(actCBVorgang.getStatus(), CBVorgang.STATUS_SUBMITTED);
                Component[] comps = new Component[] { cbConfirm, tfLBZ, tfVtrnr, tfAQS, tfLL,
                        cbVorOrt, dcVorgabeMnet, dcRealDate, taBemerkung };
                GuiTools.enableComponents(comps, enableFields, true);
            }
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
        // not used
    }
}

