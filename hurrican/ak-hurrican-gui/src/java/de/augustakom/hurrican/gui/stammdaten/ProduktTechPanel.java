/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.10.2006 09:30:06
 */
package de.augustakom.hurrican.gui.stammdaten;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.ISimpleFindService;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.Leitungsart;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Produkt2Schnittstelle;
import de.augustakom.hurrican.model.cc.ProduktGruppe;
import de.augustakom.hurrican.model.cc.Schnittstelle;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.QueryCCService;


/**
 * Sub-Panel, um die technischen Daten fuer ein Produkt zu konfigurieren.
 *
 */
public class ProduktTechPanel extends AbstractServicePanel implements ChangeListener, AKModelOwner,
        AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(ProduktTechPanel.class);

    /* Aktuelles Modell, das ueber die Methode showDetails(Object) gesetzt wird. */
    private Produkt model = null;
    /* Map mit allen Schnittstellen, die dem aktuellen Produkt zugeordnet sind.
     * Es wird lediglich der Key der Map verwendet (Key = ID der Schnittstelle).
     */
    private Set<Long> produkt2Schnittstellen = null;

    // GUI-Elemente fuer das 'Technik'-Panel
    private AKJFormattedTextField tfId = null;
    private AKJTextField tfProduktNr = null;
    private AKReferenceField rfProduktGruppe = null;
    private AKJCheckBox chbIsParent = null;
    private AKJCheckBox chbCheckChild = null;
    private AKJCheckBox chbIsCombiProd = null;
    private AKJCheckBox chbAutoProdChange = null;
    private AKJCheckBox chbBATerminVerschiebung = null;
    private AKJCheckBox chbCreateAPAddress = null;
    private AKJCheckBox chbAssignIad = null;
    private AKJTextField tfAnschlussart = null;
    private AKJTextField tfNamePattern = null;
    private AKReferenceField rfLeitungsart = null;
    private AKJCheckBox chbExport = null;
    private AKJCheckBox chbCreateKdpReport = null;
    private AKJDateComponent dcGueltigVon = null;
    private AKJDateComponent dcGueltigBis = null;
    private AKJCheckBox chbLtgNrAnlegen = null;
    private AKJTextField tfVbzKOUProdukt = null;
    private AKJTextField tfVbzKOUType = null;
    private AKJTextField tfVbzKOUTypeVpn = null;
    private AKJTextArea taBeschreibung = null;
    private SchnittstellenTableModel tbMdlSchnittstellen = null;
    private AKJComboBox cbErstellStatus;
    private AKJComboBox cbKuendigungStatus;



    /**
     * Konstruktor.
     */
    public ProduktTechPanel() {
        super("de/augustakom/hurrican/gui/stammdaten/resources/ProduktTechPanel.xml");
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        // Erzeuge GUI-Komponenten und ordne diese auf dem Panel an
        AKJLabel lblId = getSwingFactory().createLabel("id");
        AKJLabel lblAnschlussart = getSwingFactory().createLabel("anschlussart");
        AKJLabel lblNamePattern = getSwingFactory().createLabel("prod.name.pattern");
        AKJLabel lblProduktNr = getSwingFactory().createLabel("produkt.nr");
        AKJLabel lblProduktGr = getSwingFactory().createLabel("produkt.gruppe");
        AKJLabel lblIsParent = getSwingFactory().createLabel("produkt.is.parent");
        AKJLabel lblCheckChild = getSwingFactory().createLabel("check.child");
        AKJLabel lblIsCombiProd = getSwingFactory().createLabel("combi.produkt");
        AKJLabel lblAutoProdChange = getSwingFactory().createLabel("auto.product.change");
        AKJLabel lblLeitungsart = getSwingFactory().createLabel("leitungsart");
        AKJLabel lblExport = getSwingFactory().createLabel("export");
        AKJLabel lblCreateKdpReport = getSwingFactory().createLabel("create.kdp.report");
        AKJLabel lblGueltigVon = getSwingFactory().createLabel("gueltig.von");
        AKJLabel lblGueltigBis = getSwingFactory().createLabel("gueltig.bis");
        AKJLabel lblLtgNrAnlegen = getSwingFactory().createLabel("leitungsnr.anlegen");
        AKJLabel lblLtgNrVorsatz = getSwingFactory().createLabel("vbz.kind.of.use");
        AKJLabel lblBeschreibung = getSwingFactory().createLabel("beschreibung");
        AKJLabel lblSchnittstellen = getSwingFactory().createLabel("schnittstellen");
        AKJLabel lblBATerminVerschiebung = getSwingFactory().createLabel("ba.termin");
        AKJLabel lblCreateApAddress = getSwingFactory().createLabel("create.ap.address");
        AKJLabel lblAssignIad = getSwingFactory().createLabel("assign.iad");
        AKJLabel lblErstellStatus = getSwingFactory().createLabel("erstell.status");
        AKJLabel lblKuendigungStatus = getSwingFactory().createLabel("kuendigung.status");


        tfId = getSwingFactory().createFormattedTextField("id");
        tfProduktNr = getSwingFactory().createTextField("produkt.nr");
        rfProduktGruppe = getSwingFactory().createReferenceField(
                "produkt.gruppe", ProduktGruppe.class, "id", "produktGruppe", null);
        chbIsParent = getSwingFactory().createCheckBox("produkt.is.parent", getActionListener(), false);
        chbCheckChild = getSwingFactory().createCheckBox("check.child");
        chbIsCombiProd = getSwingFactory().createCheckBox("combi.produkt");
        chbAutoProdChange = getSwingFactory().createCheckBox("auto.product.change");
        tfAnschlussart = getSwingFactory().createTextField("anschlussart");
        tfNamePattern = getSwingFactory().createTextField("prod.name.pattern");
        rfLeitungsart = getSwingFactory().createReferenceField(
                "leitungsart", Leitungsart.class, "id", "name", null);
        chbExport = getSwingFactory().createCheckBox("export");
        chbCreateKdpReport = getSwingFactory().createCheckBox("create.kdp.report");
        dcGueltigVon = getSwingFactory().createDateComponent("gueltig.von");
        dcGueltigBis = getSwingFactory().createDateComponent("gueltig.bis");
        chbLtgNrAnlegen = getSwingFactory().createCheckBox("leitungsnr.anlegen");
        chbLtgNrAnlegen.addChangeListener(this);
        tfVbzKOUProdukt = getSwingFactory().createTextField("vbz.kind.of.use.product");
        tfVbzKOUType = getSwingFactory().createTextField("vbz.kind.of.use.type");
        tfVbzKOUTypeVpn = getSwingFactory().createTextField("vbz.kind.of.use.type.vpn");
        taBeschreibung = getSwingFactory().createTextArea("beschreibung");
        taBeschreibung.setWrapStyleWord(false);
        taBeschreibung.setLineWrap(true);
        AKJScrollPane spBeschreibung = new AKJScrollPane(taBeschreibung);
        spBeschreibung.setPreferredSize(new Dimension(100, 50));
        chbBATerminVerschiebung = getSwingFactory().createCheckBox("ba.termin");
        chbCreateAPAddress = getSwingFactory().createCheckBox("create.ap.address");
        chbAssignIad = getSwingFactory().createCheckBox("assign.iad");

        tbMdlSchnittstellen = new SchnittstellenTableModel();
        AKJTable tbSchnittstellen = new AKJTable(tbMdlSchnittstellen, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbSchnittstellen.fitTable(new int[] { 120, 50 });
        AKJScrollPane spSchnittstellen = new AKJScrollPane(tbSchnittstellen);
        spSchnittstellen.setPreferredSize(new Dimension(190, 150));

        cbErstellStatus = getSwingFactory().createComboBox("erstell.status");
        cbErstellStatus.setMaximumSize(new Dimension(10, 10));
        cbErstellStatus.setRenderer(new AKCustomListCellRenderer<>(AuftragStatus.class, AuftragStatus::getStatusText));

        cbKuendigungStatus = getSwingFactory().createComboBox("kuendigung.status");
        cbKuendigungStatus.setMaximumSize(new Dimension(10, 10));
        cbKuendigungStatus.setRenderer(new AKCustomListCellRenderer<>(AuftragStatus.class, AuftragStatus::getStatusText));



        AKJPanel left = new AKJPanel(new GridBagLayout());
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        left.add(lblId, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        left.add(tfId, GBCFactory.createGBC(100, 0, 3, 0, 4, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblAnschlussart, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfAnschlussart, GBCFactory.createGBC(100, 0, 3, 1, 4, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblNamePattern, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfNamePattern, GBCFactory.createGBC(100, 0, 3, 2, 4, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblProduktNr, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfProduktNr, GBCFactory.createGBC(100, 0, 3, 3, 4, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblProduktGr, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(rfProduktGruppe, GBCFactory.createGBC(100, 0, 3, 4, 4, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblIsParent, GBCFactory.createGBC(0, 0, 1, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(chbIsParent, GBCFactory.createGBC(100, 0, 3, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 4, 5, 1, 1, GridBagConstraints.NONE));
        left.add(lblCheckChild, GBCFactory.createGBC(0, 0, 5, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(chbCheckChild, GBCFactory.createGBC(100, 0, 6, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblIsCombiProd, GBCFactory.createGBC(0, 0, 1, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(chbIsCombiProd, GBCFactory.createGBC(100, 0, 3, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblAutoProdChange, GBCFactory.createGBC(0, 0, 5, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(chbAutoProdChange, GBCFactory.createGBC(100, 0, 6, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblLeitungsart, GBCFactory.createGBC(0, 0, 1, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(rfLeitungsart, GBCFactory.createGBC(100, 0, 3, 7, 4, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblExport, GBCFactory.createGBC(0, 0, 1, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(chbExport, GBCFactory.createGBC(100, 0, 3, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblCreateKdpReport, GBCFactory.createGBC(0, 0, 5, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(chbCreateKdpReport, GBCFactory.createGBC(100, 0, 6, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblBATerminVerschiebung, GBCFactory.createGBC(0, 0, 1, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(chbBATerminVerschiebung, GBCFactory.createGBC(100, 0, 3, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblCreateApAddress, GBCFactory.createGBC(0, 0, 5, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(chbCreateAPAddress, GBCFactory.createGBC(100, 0, 6, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblAssignIad, GBCFactory.createGBC(0, 0, 1, 10, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(chbAssignIad, GBCFactory.createGBC(100, 0, 3, 10, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblErstellStatus, GBCFactory.createGBC(0, 0, 1, 11, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(cbErstellStatus, GBCFactory.createGBC(100, 0, 3, 11, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblKuendigungStatus, GBCFactory.createGBC(0, 0, 1, 12, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(cbKuendigungStatus, GBCFactory.createGBC(100, 0, 3, 12, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 1, 14, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel middle = new AKJPanel(new GridBagLayout());
        middle.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        middle.add(lblGueltigVon, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        middle.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        middle.add(dcGueltigVon, GBCFactory.createGBC(100, 0, 3, 0, 3, 1, GridBagConstraints.HORIZONTAL));
        middle.add(lblGueltigBis, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        middle.add(dcGueltigBis, GBCFactory.createGBC(100, 0, 3, 1, 3, 1, GridBagConstraints.HORIZONTAL));
        middle.add(lblLtgNrAnlegen, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        middle.add(chbLtgNrAnlegen, GBCFactory.createGBC(100, 0, 3, 2, 3, 1, GridBagConstraints.HORIZONTAL));
        middle.add(lblLtgNrVorsatz, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        middle.add(tfVbzKOUProdukt, GBCFactory.createGBC(100, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        middle.add(tfVbzKOUType, GBCFactory.createGBC(100, 0, 4, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        middle.add(tfVbzKOUTypeVpn, GBCFactory.createGBC(100, 0, 5, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        middle.add(lblBeschreibung, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        middle.add(spBeschreibung, GBCFactory.createGBC(100, 0, 3, 4, 3, 3, GridBagConstraints.BOTH));
        middle.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 1, 5, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel right = new AKJPanel(new GridBagLayout());
        right.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        right.add(lblSchnittstellen, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(spSchnittstellen, GBCFactory.createGBC(100, 100, 1, 1, 1, 1, GridBagConstraints.BOTH));

        this.setLayout(new GridBagLayout());
        this.add(left, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.VERTICAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        this.add(middle, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.VERTICAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.NONE));
        this.add(right, GBCFactory.createGBC(0, 0, 4, 0, 1, 1, GridBagConstraints.VERTICAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 5, 1, 1, 1, GridBagConstraints.BOTH));
        validateFields();

        // Bitte alle GUI Komponenten auf Rechte prüfen, da diverse User nur auf wenige Komponenten rechte haben!
        manageGUI(tfId, tfProduktNr, rfProduktGruppe, chbIsParent,
                chbCheckChild, chbIsCombiProd, chbAutoProdChange, tfAnschlussart,
                tfNamePattern, rfLeitungsart, chbExport, chbCreateKdpReport,
                dcGueltigVon, dcGueltigBis, chbLtgNrAnlegen, tfVbzKOUProdukt,
                tfVbzKOUType, tfVbzKOUTypeVpn, taBeschreibung, chbBATerminVerschiebung,
                chbCreateAPAddress, chbAssignIad, tbSchnittstellen, cbErstellStatus, cbKuendigungStatus);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            ISimpleFindService sfs = getCCService(QueryCCService.class);
            rfProduktGruppe.setFindService(sfs);
            rfLeitungsart.setFindService(sfs);

            // Schnittstellen laden
            loadSchnittstellen();

            loadAuftragStatus();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("produkt.is.parent".equals(command)) {
            chbCheckChild.setEnabled(chbIsParent.isSelected());
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKSimpleModelOwner#setModel(java.util.Observable)
     */
    @Override
    public void setModel(Observable model) {
        this.model = (model instanceof Produkt) ? (Produkt) model : null;
        readModel();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#readModel()
     */
    @Override
    public void readModel() {
        // Lösche alle Einträge
        clearAll();

        // Setze Daten aus Model in GUI-Komponenten
        if (model != null) {
            tfId.setValue(model.getId());
            tfProduktNr.setText(model.getProduktNr());
            rfProduktGruppe.setReferenceId(model.getProduktGruppeId());
            chbIsParent.setSelected(model.getIsParent());
            chbCheckChild.setSelected(model.getCheckChild());
            chbIsCombiProd.setSelected(model.getIsCombiProdukt());
            chbAutoProdChange.setSelected(model.getAutoProductChange());
            tfAnschlussart.setText(model.getAnschlussart());
            tfNamePattern.setText(model.getProductNamePattern());
            rfLeitungsart.setReferenceId(model.getLeitungsart());
            chbExport.setSelected(model.getExportKdpM());
            chbCreateKdpReport.setSelected(model.getCreateKdpAccountReport());
            dcGueltigVon.setDate(model.getGueltigVon());
            dcGueltigBis.setDate(model.getGueltigBis());
            chbLtgNrAnlegen.setSelected(model.getLeitungsNrAnlegen());
            tfVbzKOUProdukt.setText(model.getVbzKindOfUseProduct());
            tfVbzKOUType.setText(model.getVbzKindOfUseType());
            tfVbzKOUTypeVpn.setText(model.getVbzKindOfUseTypeVpn());
            taBeschreibung.setText(model.getBeschreibung());
            chbBATerminVerschiebung.setSelected(model.getBaTerminVerschieben());
            chbCreateAPAddress.setSelected(model.getCreateAPAddress());
            chbAssignIad.setSelected(model.getAssignIad());
            cbErstellStatus.selectItem("getId", AuftragStatus.class, model.getErstellStatusId());
            cbKuendigungStatus.selectItem("getId", AuftragStatus.class, model.getKuendigungStatusId());

            // zugeordnete Schnittstellen laden
            try {
                if (model.getId() != null) {
                    ProduktService service = getCCService(ProduktService.class);
                    List<Produkt2Schnittstelle> schnittstellen = service.findSchnittstellenMappings4Produkt(model.getId());
                    if (schnittstellen != null) {
                        for (Produkt2Schnittstelle element : schnittstellen) {
                            produkt2Schnittstellen.add(element.getSchnittstelleId());
                        }
                    }
                }
                tbMdlSchnittstellen.fireTableDataChanged();
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }

            validateFields();
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    @Override
    public void saveModel() throws AKGUIException {
        try {
            // Schnittstellen-Mappings speichern
            ProduktService service = getCCService(ProduktService.class);
            Collection<Long> p2s = (produkt2Schnittstellen != null) ? produkt2Schnittstellen : new HashSet<Long>();
            service.saveSchnittstellen4Produkt(model.getId(), p2s);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKGUIException(e.getMessage(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#hasModelChanged()
     */
    @Override
    public boolean hasModelChanged() {
        return false;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKSimpleModelOwner#getModel()
     */
    @Override
    public Object getModel() {
        // Setze Daten aus GUI-Komponenten in Model
        model.setId(tfId.getValueAsLong(null));
        model.setProduktNr(tfProduktNr.getText());
        model.setProduktGruppeId(rfProduktGruppe.getReferenceIdAs(Long.class));
        model.setIsParent(chbIsParent.isSelected());
        model.setCheckChild(chbCheckChild.isSelected());
        model.setIsCombiProdukt(chbIsCombiProd.isSelected());
        model.setAutoProductChange(chbAutoProdChange.isSelected());
        model.setAnschlussart(tfAnschlussart.getText());
        model.setProductNamePattern(tfNamePattern.getText());
        model.setLeitungsart(rfLeitungsart.getReferenceIdAs(Long.class));
        model.setExportKdpM(chbExport.isSelected());
        model.setCreateKdpAccountReport(chbCreateKdpReport.isSelected());
        model.setGueltigVon(dcGueltigVon.getDate(new Date()));
        model.setGueltigBis(dcGueltigBis.getDate(DateTools.getHurricanEndDate()));
        model.setLeitungsNrAnlegen(chbLtgNrAnlegen.isSelected());
        model.setVbzKindOfUseProduct(tfVbzKOUProdukt.getText(null));
        model.setVbzKindOfUseType(tfVbzKOUType.getText(null));
        model.setVbzKindOfUseTypeVpn(tfVbzKOUTypeVpn.getText(null));
        model.setBeschreibung(taBeschreibung.getText());
        model.setBaTerminVerschieben(chbBATerminVerschiebung.isSelected());
        model.setCreateAPAddress(chbCreateAPAddress.isSelected());
        model.setAssignIad(chbAssignIad.isSelected());
        model.setErstellStatusId(cbErstellStatus.getSelectedItem() instanceof AuftragStatus
                ? ((AuftragStatus) cbErstellStatus.getSelectedItem()).getId() : null);
        model.setKuendigungStatusId(cbKuendigungStatus.getSelectedItem() instanceof AuftragStatus
                ? ((AuftragStatus) cbKuendigungStatus.getSelectedItem()).getId() : null);
        return model;
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    /**
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == chbLtgNrAnlegen) {
            tfVbzKOUProdukt.setEnabled(chbLtgNrAnlegen.isSelected());
            tfVbzKOUType.setEnabled(chbLtgNrAnlegen.isSelected());
            tfVbzKOUTypeVpn.setEnabled(chbLtgNrAnlegen.isSelected());
        }
    }

    /* Validiert die Felder. */
    private void validateFields() {
        chbCheckChild.setEnabled(chbIsParent.isSelected());
        tfVbzKOUProdukt.setEnabled(chbLtgNrAnlegen.isSelected());
        tfVbzKOUType.setEnabled(chbLtgNrAnlegen.isSelected());
        tfVbzKOUTypeVpn.setEnabled(chbLtgNrAnlegen.isSelected());
    }

    /* Laedt alle verfuegbaren Schnittstellen */
    private void loadSchnittstellen() throws ServiceNotFoundException, FindException {
        ProduktService service = getCCService(ProduktService.class);
        List<Schnittstelle> schnittstellen = service.findSchnittstellen();
        tbMdlSchnittstellen.setData(schnittstellen);
    }

    private void loadAuftragStatus() throws ServiceNotFoundException, FindException {
        CCAuftragService svc = getCCService(CCAuftragService.class);
        List<AuftragStatus> statusList = svc.findAuftragStati();
        cbErstellStatus.addItems(statusList, true, AuftragStatus.class);
        cbKuendigungStatus.addItems(statusList, true, AuftragStatus.class);
    }

    /* 'Loescht' alle Felder */
    private void clearAll() {
        GuiTools.cleanFields(this);
        produkt2Schnittstellen = new HashSet<Long>();
        tbMdlSchnittstellen.fireTableDataChanged();
    }

    /* Setzt Fokus auf Feld tfId */
    public void setFocusTfId() {
        tfId.requestFocus();
    }

    /**
     * TableModel fuer die Darstellung der Mappings zwischen Produkten und Schnittstellen.
     */
    class SchnittstellenTableModel extends AKTableModel<Schnittstelle> {
        static final int COL_NAME = 0;
        static final int COL_POSSIBLE = 1;

        static final int COL_COUNT = 2;

        /**
         * @see javax.swing.table.TableModel#getColumnCount()
         */
        @Override
        public int getColumnCount() {
            return COL_COUNT;
        }

        /**
         * @see javax.swing.table.TableModel#getColumnName(int)
         */
        @Override
        public String getColumnName(int column) {
            switch (column) {
                case COL_NAME:
                    return "Schnittstelle";
                case COL_POSSIBLE:
                    return "möglich";
                default:
                    return "";
            }
        }

        /**
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */
        @Override
        public Object getValueAt(int row, int column) {
            Object o = getDataAtRow(row);
            if (o instanceof Schnittstelle) {
                Schnittstelle s = (Schnittstelle) o;
                switch (column) {
                    case COL_NAME:
                        return s.getSchnittstelle();
                    case COL_POSSIBLE:
                        return
                                (((produkt2Schnittstellen != null) && produkt2Schnittstellen.contains(s.getId()))
                                        ? Boolean.TRUE
                                        : Boolean.FALSE);
                    default:
                        break;
                }
            }
            return null;
        }

        /**
         * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
         */
        @Override
        public void setValueAt(Object aValue, int row, int column) {
            Object o = getDataAtRow(row);
            if (o instanceof Schnittstelle) {
                Schnittstelle s = (Schnittstelle) o;
                if (produkt2Schnittstellen == null) {
                    produkt2Schnittstellen = new HashSet<Long>();
                }

                if (aValue instanceof Boolean) {
                    if (((Boolean) aValue).booleanValue()) {
                        produkt2Schnittstellen.add(s.getId());
                    }
                    else {
                        produkt2Schnittstellen.remove(s.getId());
                    }
                }
            }
        }

        /**
         * @see javax.swing.table.TableModel#isCellEditable(int, int)
         */
        @Override
        public boolean isCellEditable(int row, int column) {
            return (column == COL_POSSIBLE) ? true : false;
        }

        /**
         * @see javax.swing.table.TableModel#getColumnClass(int)
         */
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return (columnIndex == COL_POSSIBLE) ? Boolean.class : super.getColumnClass(columnIndex);
        }
    }

}


