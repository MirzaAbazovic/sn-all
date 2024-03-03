/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.08.2006 13:05:09
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJComboBoxCellEditor;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.Either;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.base.IAuftragStatusValidator;
import de.augustakom.hurrican.gui.shared.EG2AuftragViewTableModel;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.EG;
import de.augustakom.hurrican.model.cc.EGConfig;
import de.augustakom.hurrican.model.cc.Montageart;
import de.augustakom.hurrican.model.cc.view.EG2AuftragView;
import de.augustakom.hurrican.model.internet.IntEndgeraet;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.model.shared.iface.CCProduktModel;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.EndgeraeteService;
import de.augustakom.hurrican.service.cc.HWSwitchService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.SIPDomainService;
import de.augustakom.hurrican.service.cc.VoIPService;
import de.augustakom.hurrican.service.cc.utils.CCServiceFinder;
import de.augustakom.hurrican.service.cc.utils.CalculatedSipDomain4VoipAuftrag;
import de.augustakom.hurrican.service.cc.utils.CalculatedSwitch4VoipAuftrag;


/**
 * Panel fuer die Zuordnung von Endgeraeten zu einem Auftrag.
 *
 *
 */
public class EG2AuftragPanel extends AbstractAuftragPanel implements AKDataLoaderComponent,
        IAuftragStatusValidator, AKObjectSelectionListener {

    private static final Logger LOGGER = Logger.getLogger(EG2AuftragPanel.class);

    private static final String WATCH_EG2AUFTRAG = "watch.eg2auftrag.";
    private static final long serialVersionUID = -8802419861118646780L;

    // GUI-Komponenten
    private AKJButton btnAddDefaults;
    private AKJButton btnNew;
    private AKJButton btnDelete;
    private AKJButton btnImportEg;
    private AKJTable tbEGs;
    private EG2AuftragViewTableModel tbMdlEGs;
    private AKJComboBox cbMontageart;

    // Modelle
    private CCAuftragModel auftragModel;
    private List<EG2AuftragView> eg2AuftragViews;
    private List<Montageart> montagearten;

    public EG2AuftragPanel() {
        super("de/augustakom/hurrican/gui/auftrag/resources/EG2AuftragPanel.xml");
        createGUI();
        loadData();
    }

    @Override
    protected final void createGUI() {
        btnAddDefaults = getSwingFactory().createButton("defaults", getActionListener(), null);
        btnNew = getSwingFactory().createButton("new", getActionListener(), null);
        btnDelete = getSwingFactory().createButton("delete", getActionListener(), null);
        btnImportEg = getSwingFactory().createButton("import.eg", getActionListener(), null);

        AKJPanel btnPnl = new AKJPanel(new GridBagLayout());
        btnPnl.add(btnAddDefaults, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE, new Insets(2, 2, 10, 2)));
        btnPnl.add(btnNew, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnDelete, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.NONE, new Insets(10, 2, 2, 2)));
        btnPnl.add(btnImportEg, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.NONE, new Insets(10, 2, 2, 2)));
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 4, 1, 1, GridBagConstraints.VERTICAL));

        cbMontageart = new AKJComboBox(Montageart.class, "getName");
        cbMontageart.addItemListener(new CBItemListener());

        tbMdlEGs = new EG2AuftragViewTableModel();
        tbEGs = new AKJTable(tbMdlEGs, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbEGs.fitTable(new int[] { 50, 100, 120, 100, 80, 80, 80, 80 });
        tbEGs.addMouseListener(new AKTableDoubleClickMouseListener(this));
        tbEGs.addPopupAction(new ConfigureEGAction());
        tbEGs.setCellEditor4Column(2, new AKJComboBoxCellEditor(cbMontageart));
        AKJScrollPane spEGs = new AKJScrollPane(tbEGs, new Dimension(600, 150));

        this.setLayout(new BorderLayout());
        this.add(btnPnl, BorderLayout.WEST);
        this.add(spEGs, BorderLayout.CENTER);

        manageGUI(btnAddDefaults, btnNew, btnDelete, btnImportEg);
    }

    @Override
    public final void loadData() {
        try {
            PhysikService phs = CCServiceFinder.instance().getCCService(PhysikService.class);
            montagearten = phs.findMontagearten();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }

        cbMontageart.addItems(montagearten, true, Montageart.class, true);
    }

    /**
     * Entfernt alle Daten.
     */
    private void clear() {
        eg2AuftragViews = null;
        tbMdlEGs.removeAll();
    }

    @Override
    public void readModel() {
        clear();
        if (this.auftragModel == null) {
            return;
        }
        try {
            setWaitCursor();
            EndgeraeteService service = getCCService(EndgeraeteService.class);
            eg2AuftragViews = service.findEG2AuftragViews(auftragModel.getAuftragId());
            tbMdlEGs.setData(eg2AuftragViews);

            if (eg2AuftragViews != null) {
                for (int i = 0; i < eg2AuftragViews.size(); i++) {
                    addObjectToWatch(WATCH_EG2AUFTRAG + i, eg2AuftragViews.get(i));
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
        finally {
            setDefaultCursor();
        }
    }

    @Override
    protected void execute(String command) {
        if ("defaults".equals(command)) {
            addDefaultEGs();
        }
        else if ("new".equals(command)) {
            addNewEG();
        }
        else if ("delete".equals(command)) {
            delete();
        }
        else if ("import.eg".equals(command)) {
            importEg();
        }
    }

    /**
     * Fuegt dem Auftrag die Default-Endgeraete des Produkts hinzu.
     */
    private void addDefaultEGs() {
        try {
            if (tbMdlEGs.getRowCount() > 0) {
                throw new HurricanGUIException("Dem Auftrag sind bereits Endgeräte zugeordnet!");
            }

            EndgeraeteService service = getCCService(EndgeraeteService.class);
            List<EG> egs = service.findDefaultEGs4Order(this.auftragModel.getAuftragId());
            if (CollectionTools.isEmpty(egs)) {
                throw new HurricanGUIException("Es konnten keine Endgeräte für das Produkt ermittelt werden!");
            }

            for (EG eg : egs) {
                addEG(eg, false);
            }

            checkEGDifference();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * Fuegt dem Auftrag ein neues Endgeraet hinzu.
     */
    private void addNewEG() {
        try {
            EndgeraeteService service = getCCService(EndgeraeteService.class);
            List<EG> egs = service.findEGs4Produkt(((CCProduktModel) this.auftragModel).getProdId(), false);

            Object result = MessageHelper.showInputDialog(getMainFrame(), egs,
                    new AKCustomListCellRenderer<>(EG.class, EG::getEgName), "Endgerät wählen",
                    "Bitte wählen Sie ein Endgerät aus", "Endgerät:");
            if (result instanceof EG) {
                EG eg = (EG) result;
                addEG(eg, true);

                checkEGDifference();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * Zeigt die Endgeraete der Monline-Datenbank zum Uebernehmen nach Hurrican an.
     */
    private void importEg() {
        try {
            EndgeraeteService service = getCCService(EndgeraeteService.class);
            List<IntEndgeraet> egs = service.findIntEndgeraeteNotInHurrican(auftragModel.getAuftragId());
            Object result = MessageHelper.showInputDialog(getMainFrame(), egs,
                    new AKCustomListCellRenderer<>(IntEndgeraet.class, IntEndgeraet::getTypeAndSerialAndManagementIp), "Endgerät wählen",
                    "Bitte wählen Sie ein Endgerät aus, das nach Hurrican übernommen werden soll.", "Endgerät:");
            if (result instanceof IntEndgeraet) {
                IntEndgeraet eg = (IntEndgeraet) result;
                Long billingOrderNo = null;
                if (auftragModel instanceof AuftragDaten) {
                    billingOrderNo = ((AuftragDaten) this.auftragModel).getAuftragNoOrig();
                }
                service.importEg(auftragModel.getAuftragId(), billingOrderNo,
                        eg, HurricanSystemRegistry.instance().getSessionId());
                clear();
                readModel();
                loadData();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }

    }

    /**
     * Fuegt das Endgeraet der Tabelle und dadurch dem Auftrag hinzu.
     */
    private void addEG(EG endgeraet, boolean checkEG) {
        try {
            if (checkEG) {
                // EG-Check durchfuehren
                EndgeraeteService service = getCCService(EndgeraeteService.class);
                service.executeEGCheckCommand(endgeraet, this.auftragModel.getAuftragId());
            }

            EG2AuftragView model = new EG2AuftragView();
            addObjectToWatch(WATCH_EG2AUFTRAG + tbMdlEGs.getRowCount(), model);
            tbMdlEGs.addObject(model);

            model.setAuftragId(this.auftragModel.getAuftragId());
            model.setEgId(endgeraet.getId());
            model.setEgName(endgeraet.getEgName());

            saveModel();
            readModel();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * Prueft, welche EG-Leistungen im Billing-System eingetragen sind und ob es noch eine Differenz mit den
     * zugeordneten EGs gibt. Ist eine Differenz vorhanden, wird der Benutzer darueber informiert. Wichtig: es werden
     * nur EGs beruecksichtigt, die einen Produkt-Code eingetragen haben. (War notwendig, um Geraete wie NT od.
     * Splitter, die durch Paketdefinitionen angezogen werden aus der Ueberpruefung zu nehmen.)
     */
    private void checkEGDifference() {
        try {
            EndgeraeteService es = getCCService(EndgeraeteService.class);
            List<EG> egsFromBilling = es.findDefaultEGs4Order(this.auftragModel.getAuftragId());
            if (CollectionTools.isNotEmpty(egsFromBilling)) {
                List<EG> missingEGs = new ArrayList<>();
                Collection<EG2AuftragView> assignedEGs = tbMdlEGs.getData();
                for (EG egFromBilling : egsFromBilling) {
                    if (StringUtils.isNotBlank(egFromBilling.getProduktcode())) {
                        boolean found = false;
                        for (EG2AuftragView assignedEG : assignedEGs) {
                            if (NumberTools.equal(egFromBilling.getId(), assignedEG.getEgId())) {
                                found = true;
                                break;
                            }
                        }

                        if (!found) {
                            missingEGs.add(egFromBilling);
                        }
                    }
                }

                if (CollectionTools.isNotEmpty(missingEGs)) {
                    StringBuilder msg = new StringBuilder();
                    msg.append("Folgende Endgeräte sind im Billing gebucht, aber im Hurrican nicht zugeordnet:");
                    msg.append(SystemUtils.LINE_SEPARATOR);
                    for (EG eg : missingEGs) {
                        msg.append(eg.getEgName());
                        msg.append(SystemUtils.LINE_SEPARATOR);
                    }

                    MessageHelper.showInfoDialog(getMainFrame(), msg.toString(), null, true);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Loescht die aktuell selektierte Endgeraete-Zuordnung
     */
    private void delete() {
        try {
            EG2AuftragView selected = (EG2AuftragView)
                    ((AKMutableTableModel) tbEGs.getModel()).getDataAtRow(tbEGs.getSelectedRow());
            if (selected == null) {
                throw new HurricanGUIException("Bitte wählen Sie zuerst einen Datensatz aus.");
            }
            else if (selected.getEg2AuftragId() == null) {
                throw new HurricanGUIException("Bitte Endgeräte zuerst speichern.");
            }
            else {
                // Lade EG2Auftrag neu, um Datensatz zu aktualisieren und sicherzustellen, dass ein EG
                // in der Zwischenzeit nicht exportiert wurde
                int opt = MessageHelper.showYesNoQuestion(getMainFrame(),
                        "Soll das Endgerät wirklich vom Auftrag\nentfernt werden?", "Löschen?");
                if (opt == JOptionPane.YES_OPTION) {
                    if (selected.getEg2AuftragId() != null) {
                        CCAuftragService ccAuftragService = getCCService(CCAuftragService.class);
                        SIPDomainService sipDomainService = getCCService(SIPDomainService.class);

                        Either<CalculatedSwitch4VoipAuftrag, Boolean> switchInitialState =
                                ccAuftragService.calculateSwitch4VoipAuftrag(selected.getAuftragId());
                        CalculatedSipDomain4VoipAuftrag sipDomainInitialState =
                                sipDomainService.calculateSipDomain4VoipAuftrag(selected.getAuftragId());

                        EndgeraeteService endgeraeteService = getCCService(EndgeraeteService.class);
                        endgeraeteService.deleteEG2Auftrag(selected.getEg2AuftragId(), HurricanSystemRegistry.instance()
                                .getSessionId());

                        if (switchInitialState.isLeft()) {
                            updateSwitchAndSipDomains(selected.getAuftragId(), switchInitialState,
                                    sipDomainInitialState);
                        }
                    }
                    removeObjectFromWatch(WATCH_EG2AUFTRAG + tbEGs.getSelectedRow());
                    ((AKMutableTableModel) tbEGs.getModel()).removeObject(selected);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private void updateSwitchAndSipDomains(Long auftragId, Either<CalculatedSwitch4VoipAuftrag, Boolean> switchInitialState,
            CalculatedSipDomain4VoipAuftrag sipDomainInitialState) throws ServiceNotFoundException, FindException {
        AKWarnings messages = new AKWarnings();
        HWSwitchService hwSwitchService = getCCService(HWSwitchService.class);
        Either<String, String> updateSwitch = hwSwitchService.updateHwSwitchBasedComponents(auftragId,
                switchInitialState.getLeft());

        if (updateSwitch.isRight()) {
            if (updateSwitch.getRight() != null) {
                MessageHelper.showInfoDialog(this, updateSwitch.getRight());
            }
            return;
        }
        if (updateSwitch.getLeft() != null) {
            messages.addAKWarning(null, updateSwitch.getLeft());
        }

        VoIPService voIPService = getCCService(VoIPService.class);
        Either<String, String> updateSipDomain = voIPService.migrateSipDomainOfVoipDNs(auftragId,
                (updateSwitch.getLeft() != null), sipDomainInitialState);

        if (updateSipDomain.isRight() && updateSipDomain.getRight() != null) {
            MessageHelper.showInfoDialog(this, updateSwitch.getRight());
        }
        if (updateSipDomain.isLeft() && updateSipDomain.getLeft() != null) {
            messages.addAKWarning(null, updateSipDomain.getLeft());
        }

        if (messages.getAKMessages().size() > 0) {
            StringBuilder message = new StringBuilder();
            message.append(messages.getWarningsAsText());
            message.append(SystemUtils.LINE_SEPARATOR);
            message.append("Der Auftrag muss neu provisioniert werden!");
            MessageHelper.showInfoDialog(this, message.toString());
        }
    }

    @Override
    public boolean hasModelChanged() {
        for (int i = 0; i < tbMdlEGs.getRowCount(); i++) {
            if (hasChanged(WATCH_EG2AUFTRAG + i, tbMdlEGs.getDataAtRow(i))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void saveModel() throws AKGUIException {
        try {
            List<EG2AuftragView> views2Save = new ArrayList<>();
            for (int i = 0; i < tbMdlEGs.getRowCount(); i++) {
                EG2AuftragView model = tbMdlEGs.getDataAtRow(i);
                if (hasChanged(WATCH_EG2AUFTRAG + i, model)) {
                    views2Save.add(model);
                }
            }

            if (!views2Save.isEmpty()) {
                EndgeraeteService service = getCCService(EndgeraeteService.class);
                service.saveEGs2Auftrag(views2Save, HurricanSystemRegistry.instance().getSessionId());
            }

            readModel();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKGUIException("Fehler beim Speichern der Endgeraete: " + e.getMessage(), e);
        }
    }

    @Override
    public void objectSelected(Object selection) {
        try {
            Object tmp = ((AKMutableTableModel) tbEGs.getModel()).getDataAtRow(tbEGs.getSelectedRow());
            if (tmp instanceof EG2AuftragView) {
                EG2AuftragView view = (EG2AuftragView) tmp;
                if (!BooleanTools.nullToFalse(view.getIsConfigurable())) {
                    throw new HurricanGUIException("Das Endgerät kann nicht konfiguriert werden!");
                }
                else if (view.getEg2AuftragId() == null) {
                    throw new HurricanGUIException("Bitte speichern Sie die Endgeräte-Zuordnung zuerst ab.");
                }
                else {
                    EGConfigurationDialog cnfDlg = new EGConfigurationDialog(view.getEg2AuftragId());
                    Object value = DialogHelper.showDialog(getMainFrame(), cnfDlg, true, true);
                    if (value instanceof EGConfig) {
                        view.setHasConfiguration(Boolean.TRUE);
                        tbMdlEGs.fireTableDataChanged();
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    public Object getModel() {
        return null;
    }

    @Override
    public void setModel(Observable model) {
        this.auftragModel = null;
        if (model instanceof CCAuftragModel) {
            this.auftragModel = (CCAuftragModel) model;
        }

        if (this.auftragModel != null) {
            readModel();
        }
        else {
            clear();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    public void validate4Status(Long auftragStatus) {
        if (NumberTools.isLess(auftragStatus, AuftragStatus.TECHNISCHE_REALISIERUNG) ||
                NumberTools.equal(auftragStatus, AuftragStatus.AENDERUNG)) {
            GuiTools.unlockComponents(new Component[] { btnAddDefaults });
        }
        else {
            GuiTools.lockComponents(new Component[] { btnAddDefaults });
        }

        if (NumberTools.isLess(auftragStatus, AuftragStatus.KUENDIGUNG)
                && !NumberTools.equal(auftragStatus, AuftragStatus.ABSAGE)
                && !NumberTools.equal(auftragStatus, AuftragStatus.STORNO)) {
            GuiTools.unlockComponents(new Component[] { btnNew, btnDelete });
        }
        else {
            GuiTools.lockComponents(new Component[] { btnNew, btnDelete, btnImportEg });
        }

        if (NumberTools.isIn(auftragStatus,
                new Number[] { AuftragStatus.TECHNISCHE_REALISIERUNG, AuftragStatus.AENDERUNG_IM_UMLAUF,
                        AuftragStatus.ABSAGE, AuftragStatus.STORNO }
        )) {
            GuiTools.lockComponents(new Component[] { cbMontageart });
        }
        else {
            GuiTools.unlockComponents(new Component[] { cbMontageart });
        }
    }

    /**
     * Action, um ein Endgeraet zu konfigurieren.
     */
    class ConfigureEGAction extends AKAbstractAction {
        private static final long serialVersionUID = 9091122240892003596L;

        public ConfigureEGAction() {
            setName("Konfigurieren");
            setTooltip("Öffnet einen Dialog, um das Endgerät zu konfigurieren");
            setActionCommand("configure.eg");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            objectSelected(((AKMutableTableModel) tbEGs.getModel()).getDataAtRow(tbEGs.getSelectedRow()));
        }
    }


    /**
     * ItemListener fuer die ComboBoxes.
     */
    class CBItemListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
            // EG-Montage
            if ((e.getSource() == cbMontageart) && (e.getStateChange() == ItemEvent.SELECTED)) {
                Long selection = ((Montageart) cbMontageart.getSelectedItem()).getId();
                String message = null;
                if (NumberTools.equal(selection, Montageart.MONTAGEART_AKOM)) {
                    message = getSwingFactory().getText("montageart.akom");
                }
                else if (NumberTools.equal(selection, Montageart.MONTAGEART_ALLGAEUKOM)) {
                    message = getSwingFactory().getText("montageart.allgaeukom");
                }

                if (message != null) {
                    MessageHelper.showInfoDialog(getMainFrame(), message);
                }
            }
        }
    }
}
