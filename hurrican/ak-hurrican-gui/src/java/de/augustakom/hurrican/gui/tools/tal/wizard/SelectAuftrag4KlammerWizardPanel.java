/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.01.2010 16:45:56
 */
package de.augustakom.hurrican.gui.tools.tal.wizard;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.gui.GUISystemRegistry;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableModelXML;
import de.augustakom.common.gui.swing.wizard.AKJWizardComponents;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceWizardPanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.tools.tal.ColoredRowTable;
import de.augustakom.hurrican.gui.tools.tal.XmlImmutableRowTableModel;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.cc.tal.CBVorgangSubOrder;
import de.augustakom.hurrican.model.exmodules.archive.ArchiveDocumentDto;
import de.augustakom.hurrican.model.exmodules.archive.ArchiveDocumentType;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.QueryCCService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.exmodules.archive.ArchiveService;
import de.mnet.wita.model.Vorabstimmung;
import de.mnet.wita.service.WitaVorabstimmungService;

/**
 * Wizard-Panel fuer die Auswahl der Auftraege, fuer die eine TAL-Bestellung per Klammerung mit erfolgen soll.
 *
 *
 */
public class SelectAuftrag4KlammerWizardPanel extends AbstractServiceWizardPanel implements AKDataLoaderComponent,
        ActionListener {

    private static final long serialVersionUID = 7868411289657841363L;

    private static final Logger LOGGER = Logger.getLogger(SelectAuftrag4KlammerWizardPanel.class);

    private static final String RESOURCE = "de/augustakom/hurrican/gui/tools/tal/resources/SelectAuftrag4KlammerWizardPanel.xml";
    private static final String SUB_TITLE = "sub.title";

    private final Integer maxSelection;
    private final boolean isKlammerung;
    private final boolean isVierDraht;

    private XmlImmutableRowTableModel<CBVorgangSubOrder> tbMdlSubOrders;
    private ColoredRowTable tbSubOrders;
    private List<CBVorgangSubOrder> subOrdersForSelection;
    private final Map<Long, Set<ArchiveDocumentDto>> archiveDocuments2AuftragMap = new HashMap<>();

    private CCAuftragService auftragService;
    private PhysikService physikService;
    private RangierungsService rangierungsService;
    private EndstellenService endstellenService;
    private CarrierService carrierService;
    private QueryCCService queryCcService;
    private ArchiveService archiveService;
    private BillingAuftragService billingAuftragService;
    private RufnummerService rufnummerService;
    private WitaVorabstimmungService witaVorabstimmungService;

    public SelectAuftrag4KlammerWizardPanel(AKJWizardComponents wizardComponents, Integer maxSelection,
            boolean isKlammerung,
            boolean isVierDraht) {
        super(RESOURCE, wizardComponents);
        this.maxSelection = maxSelection;
        this.isKlammerung = isKlammerung;
        this.isVierDraht = isVierDraht;
        try {
            init();
            createGUI();
        }
        catch (ServiceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }
    }

    private void init() throws ServiceNotFoundException {
        subOrdersForSelection = new ArrayList<>();

        archiveService = getService(ArchiveService.class);
        auftragService = getCCService(CCAuftragService.class);
        physikService = getCCService(PhysikService.class);
        rangierungsService = getCCService(RangierungsService.class);
        endstellenService = getCCService(EndstellenService.class);
        carrierService = getCCService(CarrierService.class);
        queryCcService = getCCService(QueryCCService.class);
        billingAuftragService = getBillingService(BillingAuftragService.class);
        rufnummerService = getBillingService(RufnummerService.class);
        witaVorabstimmungService = getCCService(WitaVorabstimmungService.class);
    }

    @Override
    protected final void createGUI() {
        AKJLabel lblSubTitle;
        lblSubTitle = getSwingFactory().createLabel(SUB_TITLE, SwingConstants.LEFT, Font.BOLD);
        int selectionModel = ((maxSelection == null) || (maxSelection > 1)) ? ListSelectionModel.MULTIPLE_INTERVAL_SELECTION : ListSelectionModel.SINGLE_SELECTION;

        tbMdlSubOrders = new XmlImmutableRowTableModel<>(
                "de/augustakom/hurrican/gui/tools/tal/resources/TALKlammerungTable.xml");

        tbSubOrders = new ColoredRowTable(tbMdlSubOrders, JTable.AUTO_RESIZE_OFF, selectionModel);
        tbSubOrders.fitTable(tbMdlSubOrders.getFitList());

        Long cbVorgangTyp = (Long) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_CBVORGANG_TYP);

        SelectAnlagenAction selectAnlagenAction = new SelectAnlagenAction();
        selectAnlagenAction.setParentClass(this.getClass());

        if (NumberTools.equal(cbVorgangTyp, CBVorgang.TYP_NEU)) {
            selectAnlagenAction.setComponentExecutable(false);
        }

        tbSubOrders.addPopupAction(selectAnlagenAction);

        AKJScrollPane spSubOrders = new AKJScrollPane(tbSubOrders, new Dimension(500, 200));

        AKJPanel child = getChildPanel();
        child.setLayout(new GridBagLayout());
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        child.add(lblSubTitle, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(spSubOrders, GBCFactory.createGBC(100, 100, 1, 2, 1, 1, GridBagConstraints.BOTH, 5));
    }

    @Override
    public void update() {
        if (getWizardComponents().getDirection() == AKJWizardComponents.DIRECTION_FORWARD) {
            loadData();
        }
    }

    @Override
    protected boolean goNext() {
        AuftragDaten auftragDaten = (AuftragDaten) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_AUFTRAG_DATEN);

        Set<CBVorgangSubOrder> selectedSubOrders = new HashSet<>();
        if (CollectionTools.isNotEmpty(subOrdersForSelection)) {
            for (CBVorgangSubOrder subOrder : subOrdersForSelection) {
                // Ursprungauftrag darf nicht mit uebergeben werden!
                if (NumberTools.notEqual(subOrder.getAuftragId(), auftragDaten.getAuftragId())
                        && BooleanTools.nullToFalse(subOrder.getSelected())) {
                    selectedSubOrders.add(subOrder);
                }
            }
        }

        Long cbVorgangTyp = (Long) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_CBVORGANG_TYP);
        if (NumberTools.equal(cbVorgangTyp, CBVorgang.TYP_ANBIETERWECHSEL) && archiveDocuments2AuftragMap.isEmpty()) {
            Endstelle endstelle = (Endstelle) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_ENDSTELLE);
            Vorabstimmung cbPv = witaVorabstimmungService.findVorabstimmung(endstelle, auftragDaten);
            if (cbPv == null) {
                MessageHelper.showInfoDialog(this,
                        "Bitte füllen Sie zuerst die Vorabstimmung für den Anbieterwechsel aus.", null, true);
                return false;
            }
            if (!cbPv.isCarrierDtag() && cbPv.getCarrier().getCudaKuendigungNotwendig()) {
                MessageHelper.showInfoDialog(this,
                        "Es wurden keine Anhänge für den Anbieterwechsel ausgewählt. Bitte CuDa-Kündigung anhängen.",
                        null, true);
                return false;
            }
        }

        if (CollectionTools.isEmpty(selectedSubOrders) && (isKlammerung || isVierDraht)) {
            MessageHelper.showInfoDialog(this, "Es wurden keine Aufträge für die Klammerung ausgewählt!", null, true);
            return false;
        }

        if (maxSelection != null && maxSelection < selectedSubOrders.size()
                && (isKlammerung || isVierDraht)) {
            String msg = maxSelection == 1 ? "Es darf maximal ein Auftrag für die Klammerung ausgewählt werden!"
                    : String.format("Es dürfen maximal %s Aufträge für die Klammerung ausgewählt werden!", maxSelection);
            MessageHelper.showInfoDialog(this, msg, null, true);
            return false;
        }
        if (isKlammerung || isVierDraht) {
            getWizardComponents().addWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_SUB_ORDERS_4_KLAMMERUNG,
                    selectedSubOrders);
        }
        if (!archiveDocuments2AuftragMap.isEmpty()) {
            getWizardComponents().addWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_AUFTRAG_WITH_ATTACHMENTS,
                    archiveDocuments2AuftragMap);
        }
        return super.goNext();
    }

    @Override
    protected boolean goBack() {
        if (!subOrdersForSelection.isEmpty()) {
            subOrdersForSelection.clear();
        }
        if (!archiveDocuments2AuftragMap.isEmpty()) {
            archiveDocuments2AuftragMap.clear();
        }
        return super.goBack();
    }

    @Override
    public final void loadData() {
        try {
            if (isKlammerung || isVierDraht) {
                Long cbVorgangTyp = (Long) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_CBVORGANG_TYP);
                if (NumberTools.equal(cbVorgangTyp, CBVorgang.TYP_NEU) || maxSelection == null
                        || maxSelection > 1) {
                    // bei Auftragsklammerung und auch bei 96X Neubestellungen
                    loadPossibleOrders4Klammerung();
                }
                else {
                    if (NumberTools.notEqual(cbVorgangTyp, CBVorgang.TYP_ANBIETERWECHSEL)) {
                        // bei Aenderung/Kuendigung von 96X Bestellungen
                        loadExisting96XOrder();
                    }
                }
            }

            if (!subOrdersForSelection.isEmpty()) {
                tbMdlSubOrders.setData(subOrdersForSelection);
                if (isKlammerung) {
                    loadUrsprungsauftrag();
                }
            }
            else {
                loadUrsprungsauftrag();

            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }
    }

    /*
     * Ermittelt die moeglichen techn. Auftraege fuer eine TAL-Klammerung bzw. fuer 96X Neubestellungen. Dabei werden
     * alle techn. Auftraege mit Status < TECHNISCHE_REALISIERUNG ermittelt, die bestimmten Taifun-Auftraegen zugeordnet
     * sind. <br/> Die beruecksichtigten Taifun-Auftraege setzen sich wie folgt zusammen: <br/> <pre> - der
     * Taifun-Auftrag vom aktuell selektierten Hurrican-Auftrag - weitere Taifun-Auftraege, die min. eine identische
     * Rufnummer wie der aktuelle Taifun-Auftrag haben und deren Status NEU (mit HistCnt=0) ist (Sonderfall fuer
     * TK-Anlagen mit mehreren DSL und gedoppelten Rufnummern) </pre>
     */
    private void loadPossibleOrders4Klammerung() throws FindException {
        AuftragDaten auftragDaten = (AuftragDaten) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_AUFTRAG_DATEN);

        // Endstellen-Typ ermitteln
        String esTyp4TalBestellung = null;
        Long carrierbestellungId = (Long) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_CB_ID);
        Carrierbestellung carrierbestellung = carrierService.findCB(carrierbestellungId);
        List<Endstelle> endstellen = endstellenService.findEndstellen4Auftrag(auftragDaten.getAuftragId());
        for (Endstelle endstelle : endstellen) {
            if (NumberTools.equal(endstelle.getCb2EsId(), carrierbestellung.getCb2EsId())) {
                esTyp4TalBestellung = endstelle.getEndstelleTyp();
                break;
            }
        }
        
        // zusammen gehoerende Taifun-Auftraege ermitteln, deren Hurrican-Auftraege fuer eine
        // Klammerung ermittelt werden sollen
        Set<Long> taifunOrderNoOrigs = rufnummerService.getCorrespondingBillingOrders4Klammer(auftragDaten.getAuftragNoOrig());

        for (Long taifunOrderNoOrig : taifunOrderNoOrigs) {
            // techn. Auftraege zum Taifun Auftrag ermitteln
            // Filterung auf noch nicht realisierte Auftraege und Ermittlung der entsprechenden Ports zum Endstellentyp
            List<AuftragDaten> subOrders = auftragService.findAuftragDaten4OrderNoOrigTx(taifunOrderNoOrig);
            if (CollectionTools.isNotEmpty(subOrders)) {
                for (AuftragDaten subOrder : subOrders) {
                    if (NumberTools.notEqual(subOrder.getAuftragId(), auftragDaten.getAuftragId())
                            && subOrder.isAuftragActive()
                            && NumberTools.isLess(subOrder.getStatusId(), AuftragStatus.TECHNISCHE_REALISIERUNG)) {
                        CBVorgangSubOrder cbVorgangSubOrder = new CBVorgangSubOrder(subOrder.getAuftragId(),
                                loadDtagPort(
                                        subOrder.getAuftragId(), esTyp4TalBestellung), Boolean.TRUE, loadVbz(subOrder));
                        cbVorgangSubOrder.setAnzahlSelectedAnlagen(0);
                        subOrdersForSelection.add(cbVorgangSubOrder);
                    }
                }
            }
        }
    }

    /*
     * Ermittelt alle techn. Auftraege zu einer aktuellen/bestehenden 96X Bestellung und stellt den 2. Auftrag in der
     * Tabelle dar. <br> Der 2. Auftrag muss dabei den gleichen Status besitzen wie der Auftrag, auf dem der Vorgang
     * ausgeloest wurde. <br> (Aeltere 96X Bestellungen wurden mit zwei Carrierbestellung (mit gleicher LBZ) realisiert.
     * Neuere 96X Bestellungen dagegen sind mit einer Carrierbestellung abgebildet, die dafuer zwei Endstellen
     * zugeordnet ist. Beide Varianten werden bei der Auftragsermittlung beruecksichtigt.)
     */
    private void loadExisting96XOrder() throws HurricanGUIException {
        try {
            AuftragDaten auftragDaten = (AuftragDaten) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_AUFTRAG_DATEN);
            List<Endstelle> endstellen = loadEndstellen4Carrierbestellung();
            Pair<AuftragDaten, String> possible96XOrder = getCorresponding96XOrder(auftragDaten, endstellen);

            if (possible96XOrder == null || possible96XOrder.getFirst() == null) {
                // Auftragsermittlung ueber LBZ (aeltere 96X Bestellungen)
                endstellen = loadEndstellen4CarrierbestellungByLbz();
                possible96XOrder = getCorresponding96XOrder(auftragDaten, endstellen);
            }

            if (possible96XOrder != null && possible96XOrder.getFirst() != null) {
                CBVorgangSubOrder cbVorgangSubOrder = new CBVorgangSubOrder(possible96XOrder.getFirst().getAuftragId(),
                        loadDtagPort(possible96XOrder.getFirst().getAuftragId(), possible96XOrder.getSecond()),
                        Boolean.TRUE, loadVbz(possible96XOrder.getFirst()));
                cbVorgangSubOrder.setAnzahlSelectedAnlagen(0);
                subOrdersForSelection.add(cbVorgangSubOrder);
            }
            else {
                Endstelle endstelle = (Endstelle) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_ENDSTELLE);
                Equipment dtagEquipment = loadDtagEquipment(endstelle);
                if (dtagEquipment != null && StringUtils.isNotBlank(dtagEquipment.getRangStift2())) {
                    CBVorgangSubOrder cbVorgangSubOrder = new CBVorgangSubOrder(auftragDaten.getAuftragId(),
                            loadDtagPort(auftragDaten.getAuftragId(), endstelle.getEndstelleTyp()), Boolean.TRUE,
                            loadVbz(auftragDaten));
                    cbVorgangSubOrder.setAnzahlSelectedAnlagen(0);
                    subOrdersForSelection.add(cbVorgangSubOrder);
                }
                else {
                    throw new HurricanGUIException("Der zugehoerige 96X Auftrag konnte nicht ermittelt werden!");
                }
            }
        }
        catch (Exception e) {
            throw new HurricanGUIException(
                    "Fehler bei der Ermittlung des zugehoerigen 96X Auftrags: " + e.getMessage(), e);
        }
    }

    private Pair<AuftragDaten, String> getCorresponding96XOrder(AuftragDaten auftragDaten, List<Endstelle> endstellen)
            throws FindException {
        AuftragDaten corresponding96XOrder = null;
        String esTyp = null;
        for (Endstelle endstelle : endstellen) {
            AuftragDaten auftragDatenTmp = auftragService.findAuftragDatenByEndstelle(endstelle.getId());
            if (NumberTools.notEqual(auftragDaten.getAuftragId(), auftragDatenTmp.getAuftragId())
                    && NumberTools.equal(auftragDaten.getStatusId(), auftragDatenTmp.getStatusId())) {
                corresponding96XOrder = auftragDatenTmp;
                esTyp = endstelle.getEndstelleTyp();
                break;
            }
        }
        return Pair.create(corresponding96XOrder, esTyp);
    }

    /* Ermittelt alle Endstellen, die mit der aktuellen Carrierbestellung verbunden sind. */
    private List<Endstelle> loadEndstellen4Carrierbestellung() throws FindException, HurricanGUIException {
        Carrierbestellung carrierbestellung = getActiveCb();

        List<Endstelle> endstellen = endstellenService.findEndstellen4Carrierbestellung(carrierbestellung);
        if (CollectionTools.isEmpty(endstellen)) {
            throw new HurricanGUIException("Es wurden keine Endstellen (und somit Auftraege) gefunden, "
                    + "die der Carrierbestellung zugeordnet sind!");
        }
        return endstellen;
    }

    /* Ermittelt alle Endstellen, die mit der aktuellen Carrierbestellung verbunden sind. */
    private List<Endstelle> loadEndstellen4CarrierbestellungByLbz() throws Exception {
        Carrierbestellung carrierbestellung = getActiveCb();

        Carrierbestellung example = new Carrierbestellung();
        example.setLbz(carrierbestellung.getLbz());
        List<Carrierbestellung> carrierbestellungen = queryCcService.findByExample(example, Carrierbestellung.class);

        List<Endstelle> endstellen = new ArrayList<>();
        if (CollectionTools.isNotEmpty(carrierbestellungen)) {
            for (Carrierbestellung cb : carrierbestellungen) {
                endstellen.addAll(endstellenService.findEndstellen4Carrierbestellung(cb));
            }

            if (CollectionTools.isEmpty(endstellen)) {
                throw new HurricanGUIException("Es wurden keine Endstellen (und somit Auftraege) gefunden, "
                        + "die der Carrierbestellung zugeordnet sind!");
            }
        }

        return endstellen;
    }

    private Carrierbestellung getActiveCb() throws FindException, HurricanGUIException {
        Long cbId = (Long) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_CB_ID);
        Carrierbestellung carrierbestellung = carrierService.findCB(cbId);
        if (carrierbestellung == null) {
            throw new HurricanGUIException("Carrierbestellung konnte nicht ermittelt werden!");
        }
        else if (StringUtils.isBlank(carrierbestellung.getLbz())) {
            throw new HurricanGUIException("Die ermittelte Carrierbestellung besitzt keine LBZ.\n"
                    + "Zugehoeriger 96X Auftrag ist somit nicht ermittelbar!");
        }
        return carrierbestellung;
    }

    private String loadDtagPort(Long auftragId, String esTyp) throws FindException {
        String dtagPort = null;
        Endstelle endstelleOfSubOrder = endstellenService.findEndstelle4Auftrag(auftragId, esTyp);
        if (endstelleOfSubOrder != null && endstelleOfSubOrder.getRangierId() != null) {
            Equipment dtagEquipment = loadDtagEquipment(endstelleOfSubOrder);
            dtagPort = dtagEquipment != null ? dtagEquipment.getDtagVerteilerLeisteStift() : null;
        }

        return dtagPort;
    }

    private Equipment loadDtagEquipment(Endstelle endstelle) throws FindException {
        if (endstelle != null && endstelle.getRangierId() != null) {
            Rangierung rangierungOfSubOrder = rangierungsService.findRangierung(endstelle.getRangierId());
            return rangierungsService.findEquipment(rangierungOfSubOrder.getEqOutId());
        }
        return null;
    }

    private String loadVbz(AuftragDaten auftrag) throws FindException {
        VerbindungsBezeichnung verbindungsBezeichnung = physikService.findVerbindungsBezeichnungByAuftragId(auftrag
                .getAuftragId());
        return verbindungsBezeichnung != null ? verbindungsBezeichnung.getVbz() : null;
    }

    private List<ArchiveDocumentDto> getArchiveDocumentsForAuftrag(Long auftragId) {
        try {
            AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragId(auftragId);
            Preconditions.checkNotNull(auftragDaten, "Auftrag ist nicht definiert!");

            BAuftrag billingAuftrag = billingAuftragService.findAuftrag(auftragDaten.getAuftragNoOrig());
            if (billingAuftrag == null) {
                throw new HurricanGUIException("Der Billing-Auftrag konnte nicht ermittelt werden!");
            }
            Preconditions.checkNotNull(billingAuftrag.getSapId(), "Die Vertragsnummer konnte nicht ermittelt werden!");

            List<ArchiveDocumentDto> foundArchiveDocumentDtos = archiveService
                    .retrieveDocuments(billingAuftrag.getSapId(), ArchiveDocumentType.CUDA_KUENDIGUNG,
                            HurricanSystemRegistry.instance().getCurrentLoginName());
            if (foundArchiveDocumentDtos != null && !foundArchiveDocumentDtos.isEmpty()) {
                return foundArchiveDocumentDtos;
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }
        return null;
    }

    private void loadUrsprungsauftrag() {
        AuftragDaten auftragDaten = (AuftragDaten) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_AUFTRAG_DATEN);
        Endstelle endstelle = (Endstelle) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_ENDSTELLE);
        try {
            Vorabstimmung cbPv = witaVorabstimmungService.findVorabstimmung(endstelle, auftragDaten);
            CBVorgangSubOrder auftragDetails = new CBVorgangSubOrder(auftragDaten.getAuftragId(), loadDtagPort(
                    auftragDaten.getAuftragId(), endstelle.getEndstelleTyp()), Boolean.TRUE, loadVbz(auftragDaten));
            String lbz = null;
            if (cbPv != null) {
                lbz = cbPv.getProviderLbz();
            }
            auftragDetails.setReturnLBZ(lbz);
            auftragDetails.setAnzahlSelectedAnlagen(0);
            tbMdlSubOrders.addObject(auftragDetails);
            tbMdlSubOrders.setImmutableObject(auftragDetails);
            tbSubOrders.setColoredObject(auftragDetails);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // not used
   }

    /**
     * Action, die Attachments auszuwählen.
     */
    class SelectAnlagenAction extends AKAbstractAction {
        private static final long serialVersionUID = 8418875579132978257L;

        public SelectAnlagenAction() {
            setName("Anlagen anhängen...");
            setActionCommand("select.anlagen");
            setTooltip("Oeffnet einen Dialog für die Auswahl der Attachments.");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                CBVorgangSubOrder auftragDetails = getSelectedAuftragDetails();
                if (auftragDetails != null) {

                    Set<ArchiveDocumentDto> selectedArchiveDocumentDtos = new HashSet<>();
                    if (!archiveDocuments2AuftragMap.isEmpty()
                            && archiveDocuments2AuftragMap.containsKey(auftragDetails.getAuftragId())) {
                        selectedArchiveDocumentDtos = archiveDocuments2AuftragMap.get(auftragDetails.getAuftragId());
                    }
                    List<ArchiveDocumentDto> archiveDocumentDtos = getArchiveDocumentsForAuftrag(auftragDetails
                            .getAuftragId());
                    if (archiveDocumentDtos != null) {
                        SelectAttachmentDialog dlg = new SelectAttachmentDialog(auftragDetails, archiveDocumentDtos,
                                selectedArchiveDocumentDtos, archiveService);
                        Object result = DialogHelper.showDialog(GUISystemRegistry.instance().getMainFrame(), dlg, true,
                                true);
                        if (result instanceof Set<?>) {
                            @SuppressWarnings("unchecked")
                            Set<ArchiveDocumentDto> castedResult = (Set<ArchiveDocumentDto>) result;
                            archiveDocuments2AuftragMap.put(auftragDetails.getAuftragId(), castedResult);
                        }
                        tbSubOrders.repaint();
                        repaint();
                    }
                    else {
                        MessageHelper.showInfoDialog(HurricanSystemRegistry.instance().getMainFrame(),
                                getSwingFactory().getText("no.archive.documents.found"),
                                ArchiveDocumentType.CUDA_KUENDIGUNG);
                    }

                }
            }
            catch (Exception exception) {
                MessageHelper.showErrorDialog(GUISystemRegistry.instance().getMainFrame(), exception);
            }
        }
    }

    private CBVorgangSubOrder getSelectedAuftragDetails() {
        int selectedRow = tbSubOrders.getSelectedRow();
        @SuppressWarnings("unchecked")
        AKTableModelXML<CBVorgangSubOrder> model = (AKTableModelXML<CBVorgangSubOrder>) tbSubOrders.getModel();
        return model.getDataAtRow(selectedRow);
    }
}
