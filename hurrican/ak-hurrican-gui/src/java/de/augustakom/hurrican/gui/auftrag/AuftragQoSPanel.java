/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.02.2008 16:44:34
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReferenceAwareTableModel;
import de.augustakom.common.gui.swing.table.AKTableOwner;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.iface.ISimpleFindService;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.IAuftragStatusValidator;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragQoS;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.QoSProfilService;
import de.augustakom.hurrican.service.cc.QoSService;
import de.augustakom.hurrican.service.cc.QueryCCService;

/**
 * Panel zur Verwaltung der Quality-of-Service Daten zu einem Auftrag.
 *
 *
 */
public class AuftragQoSPanel extends AbstractAuftragPanel implements AKModelOwner,
        IAuftragStatusValidator, AKTableOwner {

    private static final Logger LOGGER = Logger.getLogger(AuftragQoSPanel.class);

    private AKReferenceAwareTableModel<AuftragQoS> tbMdlQoS = null;
    private AKJButton btnAddQoS = null;
    private AKJButton btnSaveQoS = null;
    private AKReferenceField rfQoSClass = null;
    private AKJFormattedTextField tfPercentage = null;
    private AKJDateComponent dcValidFrom = null;
    private AKJDateComponent dcValidTo = null;
    private AKJTextField tfProfilName;
    private AKJTextField tfPrio;
    private AKJTextField tfPrioBandbreite;
    private AKJDateComponent dcProfilGueltigAb;

    // Modelle
    private CCAuftragModel auftragModel = null;
    private AuftragQoS auftragQoS = null;

    // sonstiges
    private boolean initialized = false;

    private AKManageableComponent[] managedComponents;
    private AKJTextField tfKlasse;

    /**
     * Default-Const.
     */
    public AuftragQoSPanel() {
        super("de/augustakom/hurrican/gui/auftrag/resources/AuftragQoSPanel.xml");
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        tbMdlQoS = new AKReferenceAwareTableModel<AuftragQoS>(
                new String[] { "QoS-Klasse", "%-Wert", "gültig von", "gültig bis", "User" },
                new String[] { "qosClassRefId", "percentage", "gueltigVon", "gueltigBis", "userW" },
                new Class[] { String.class, Integer.class, Date.class, Date.class, String.class });
        AKJTable tbQoS = new AKJTable(tbMdlQoS, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbQoS.addTableListener(this);
        tbQoS.attachSorter();
        tbQoS.fitTable(new int[] { 200, 65, 80, 80, 90 });
        AKJScrollPane spTable = new AKJScrollPane(tbQoS, new Dimension(500, 250));
        AKJPanel tblPnl = new AKJPanel(new BorderLayout(), getSwingFactory().getText("qos"));
        tblPnl.add(spTable, BorderLayout.CENTER);

        AKJLabel lblQosClass = getSwingFactory().createLabel("qos.class");
        AKJLabel lblPercentage = getSwingFactory().createLabel("percentage");
        AKJLabel lblValidFrom = getSwingFactory().createLabel("valid.from");
        AKJLabel lblValidTo = getSwingFactory().createLabel("valid.to");

        rfQoSClass = getSwingFactory().createReferenceField("qos.class");
        tfPercentage = getSwingFactory().createFormattedTextField("percentage");
        dcValidFrom = getSwingFactory().createDateComponent("valid.from");
        dcValidTo = getSwingFactory().createDateComponent("valid.to");

        btnAddQoS = getSwingFactory().createButton("add.qos", getActionListener(), null);
        btnSaveQoS = getSwingFactory().createButton("save.qos", getActionListener(), null);

        AKJPanel detPnl = new AKJPanel(new GridBagLayout(), getSwingFactory().getText("details"));
        detPnl.add(btnAddQoS, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        detPnl.add(btnSaveQoS, GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.NONE));
        detPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 4, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(lblQosClass, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.NONE));
        detPnl.add(rfQoSClass, GBCFactory.createGBC(0, 0, 2, 1, 3, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(lblPercentage, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(tfPercentage, GBCFactory.createGBC(0, 0, 2, 2, 3, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(lblValidFrom, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(dcValidFrom, GBCFactory.createGBC(0, 0, 2, 3, 3, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(lblValidTo, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(dcValidTo, GBCFactory.createGBC(0, 0, 2, 4, 3, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 5, 5, 1, 1, GridBagConstraints.BOTH));

        final AKJLabel lblProfilName = getSwingFactory().createLabel("profil.name");
        tfProfilName = getSwingFactory().createTextField("profil.name");
        tfProfilName.setEditable(false);
        final AKJLabel lblProfilPrio = getSwingFactory().createLabel("profil.prio");
        tfPrio = getSwingFactory().createTextField("profil.prio");
        tfPrio.setEditable(false);
        final AKJLabel lblProfilBandbreite = getSwingFactory().createLabel("profil.bandbreite");
        tfPrioBandbreite = getSwingFactory().createTextField("profil.bandbreite");
        tfPrioBandbreite.setEditable(false);

        final AKJLabel lblKlasse = getSwingFactory().createLabel("profil.klasse");
        tfKlasse = getSwingFactory().createTextField("profil.klasse");
        tfKlasse.setEditable(false);

        final AKJLabel lblGueltigAb = getSwingFactory().createLabel("profil.gueltig.ab");
        dcProfilGueltigAb = getSwingFactory().createDateComponent("profil.gueltig.ab");
        dcProfilGueltigAb.setComponentExecutable(false);

        //@formatter:off
        AKJPanel profilPanel = new AKJPanel(new GridBagLayout(), "QoS-Profil");
        profilPanel.add(lblProfilName,      GBCFactory.createGBC(  0,   0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        profilPanel.add(tfProfilName,       GBCFactory.createGBC(  0,   0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        profilPanel.add(new AKJPanel(),     GBCFactory.createGBC(  0,   0, 4, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        profilPanel.add(lblKlasse,          GBCFactory.createGBC(  0,   0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        profilPanel.add(tfKlasse,           GBCFactory.createGBC(  0,   0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        profilPanel.add(new AKJPanel(),     GBCFactory.createGBC(  0,   0, 4, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        profilPanel.add(lblProfilPrio,      GBCFactory.createGBC(  0,   0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        profilPanel.add(tfPrio,             GBCFactory.createGBC(  0,   0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        profilPanel.add(new AKJPanel(),     GBCFactory.createGBC(  0,   0, 4, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        profilPanel.add(lblProfilBandbreite,GBCFactory.createGBC(  0,   0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        profilPanel.add(tfPrioBandbreite,   GBCFactory.createGBC(  0,   0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        profilPanel.add(new AKJPanel(),     GBCFactory.createGBC(  0,   0, 4, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        profilPanel.add(lblGueltigAb,       GBCFactory.createGBC(  0,   0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        profilPanel.add(dcProfilGueltigAb,  GBCFactory.createGBC(  0,   0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        profilPanel.add(new AKJPanel(),     GBCFactory.createGBC(  0,   0, 4, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        profilPanel.add(new AKJPanel(),     GBCFactory.createGBC(100, 100, 5, 4, 1, 1, GridBagConstraints.BOTH));
        //@formatter:on

        final AKJPanel masterDetailPanel = new AKJPanel(new BorderLayout());
        masterDetailPanel.add(tblPnl, BorderLayout.NORTH);
        masterDetailPanel.add(detPnl, BorderLayout.CENTER);

        this.setLayout(new BorderLayout());
        this.add(profilPanel, BorderLayout.NORTH);
        this.add(masterDetailPanel, BorderLayout.SOUTH);

        managedComponents = new AKManageableComponent[] { btnAddQoS, btnSaveQoS };
        manageGUI(managedComponents);
    }

    /* Initialisiert das Panel (Reference-Felder erhalten Find-Parameter) */
    private void init() throws Exception {
        if (!initialized) {
            initialized = true;

            ISimpleFindService sfs = getCCService(QueryCCService.class);
            Reference qosClassesEx = new Reference();
            qosClassesEx.setType(Reference.REF_TYPE_QOS_CLASS);
            rfQoSClass.setReferenceFindExample(qosClassesEx);
            rfQoSClass.setFindService(sfs);

            List<Reference> qosClasses = sfs.findByExample(qosClassesEx, Reference.class);

            Map<Long, Reference> qosRefMap = CollectionMapConverter.convert2Map(qosClasses, "getId", null);
            tbMdlQoS.addReference(0, qosRefMap, "strValue");
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKSimpleModelOwner#setModel(java.util.Observable)
     */
    @Override
    public void setModel(Observable model) throws AKGUIException {
        try {
            init();
            Component[] compsToValidate = new Component[] { btnAddQoS, btnSaveQoS };
            if (model instanceof CCAuftragModel) {
                this.auftragModel = (CCAuftragModel) model;
                GuiTools.unlockComponents(compsToValidate);
            }
            else {
                this.auftragModel = null;
                GuiTools.lockComponents(compsToValidate);
            }

            readModel();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#readModel()
     */
    @Override
    public void readModel() throws AKGUIException {
        auftragQoS = null;
        GuiTools.cleanFields(this);
        if (auftragModel != null) {
            setWaitCursor();
            enableGui(false);
            final Long auftragId = auftragModel.getAuftragId();
            final SwingWorker<Pair<List<AuftragQoS>, QoSProfilService.QosProfileWithValidFromAndDownstream>, Void> worker =
                    new SwingWorker<Pair<List<AuftragQoS>, QoSProfilService.QosProfileWithValidFromAndDownstream>, Void>() {
                        @Override
                        protected Pair<List<AuftragQoS>, QoSProfilService.QosProfileWithValidFromAndDownstream> doInBackground()
                                throws Exception {
                            List<AuftragQoS> qos = getCCService(QoSService.class).findQoS4Auftrag(auftragId, false);

                            QoSProfilService.QosProfileWithValidFromAndDownstream qosProfilAndDownStream = getCCService(
                                    QoSProfilService.class)
                                    .getQoSProfilDownStreamAndValidDate4Auftrag(auftragId, new Date());

                            return Pair.create(qos, qosProfilAndDownStream);
                        }

                        @Override
                        public void done() {
                            try {
                                tbMdlQoS.setData(get().getFirst());
                                CCAuftragService as = getCCService(CCAuftragService.class);
                                AuftragDaten ad = as.findAuftragDatenByAuftragId(auftragId);
                                Long tmpStatusId = (ad != null) ? ad.getStatusId() : null;
                                enableGui(true);
                                validate4Status(tmpStatusId);
                                setQosProfileValues(get().getSecond());
                            }
                            catch (Exception e) {
                                LOGGER.error(e.getMessage(), e);
                                MessageHelper.showErrorDialog(getMainFrame(), e);
                            }
                            finally {
                                setDefaultCursor();
                            }
                        }

                        private void setQosProfileValues(
                                final QoSProfilService.QosProfileWithValidFromAndDownstream qosProfilAndDownStream) {
                            if (qosProfilAndDownStream != null) {

                                final TechLeistung qosProfil = qosProfilAndDownStream.qosProfile;
                                final Long downStream = qosProfilAndDownStream.downstream;

                                if (qosProfil != null) {
                                    tfProfilName.setText(qosProfil.getStrValue());
                                    tfPrio.setText(qosProfil.getLongValue());
                                    if (downStream != null) {
                                        tfPrioBandbreite.setText(downStream);
                                    }
                                    tfKlasse.setText("VOICE");
                                    if (qosProfilAndDownStream.validFrom != null) {
                                        dcProfilGueltigAb.setDate(qosProfilAndDownStream.validFrom);
                                    }
                                }
                            }
                        }
                    };
            worker.execute();
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.table.AKTableOwner#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {
        GuiTools.cleanFields(this);
        if (details instanceof AuftragQoS) {
            auftragQoS = (AuftragQoS) details;
            rfQoSClass.setReferenceId(auftragQoS.getQosClassRefId());
            tfPercentage.setValue(auftragQoS.getPercentage());
            dcValidFrom.setDate(auftragQoS.getGueltigVon());
            dcValidTo.setDate(auftragQoS.getGueltigBis());

            Component[] compsToValidate = new Component[] { rfQoSClass, tfPercentage, dcValidFrom, dcValidTo };
            if ((auftragQoS.getId() != null) && auftragQoS.isHistorised()) {
                GuiTools.lockComponents(compsToValidate);
            }
            else {
                GuiTools.unlockComponents(compsToValidate);
            }
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("add.qos".equals(command)) {
            createNewQoS();
        }
        else if ("save.qos".equals(command)) {
            saveQoS();
        }
    }

    /* Erstellt ein neues QoS-Objekt zu dem Auftrag. */
    private void createNewQoS() {
        AuftragQoS newQoS = new AuftragQoS();
        newQoS.setGueltigVon(new Date());
        newQoS.setGueltigBis(DateTools.getHurricanEndDate());
        showDetails(newQoS);
    }

    /* Speichert das aktuelle QoS-Objekt zu dem Auftrag. */
    private void saveQoS() {
        try {
            AuftragQoS toSave = new AuftragQoS();
            if (auftragQoS != null) {
                PropertyUtils.copyProperties(toSave, auftragQoS);
            }
            else {
                toSave.setAuftragId(auftragModel.getAuftragId());
            }

            toSave.setQosClassRefId((Long) rfQoSClass.getReferenceId());
            toSave.setPercentage(tfPercentage.getValueAsInt(null));
            toSave.setGueltigVon(dcValidFrom.getDate(null));
            toSave.setGueltigBis(dcValidTo.getDate(null));

            QoSService qosService = getCCService(QoSService.class);
            qosService.saveAuftragQoS(toSave, HurricanSystemRegistry.instance().getSessionId());

            readModel();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
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
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    @Override
    public void saveModel() throws AKGUIException {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKSimpleModelOwner#getModel()
     */
    @Override
    public Object getModel() {
        return null;
    }

    private void enableGui(boolean enabled) {
        btnAddQoS.setEnabled(enabled);
        btnSaveQoS.setEnabled(enabled);
        rfQoSClass.setEnabled(enabled);
        tfPercentage.setEnabled(enabled);
        dcValidFrom.setEnabled(enabled);
        dcValidTo.setEnabled(enabled);
        if (enabled) {
            manageGUI(managedComponents);
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.IAuftragStatusValidator#validate4Status(java.lang.Long)
     */
    @Override
    public void validate4Status(Long auftragStatus) {
        if (NumberTools.isLess(auftragStatus, AuftragStatus.TECHNISCHE_REALISIERUNG) ||
                NumberTools.isIn(auftragStatus, new Number[] { AuftragStatus.AENDERUNG })) {
            GuiTools.unlockComponents(new Component[] { btnAddQoS, btnSaveQoS });
        }
        else {
            GuiTools.lockComponents(new Component[] { btnAddQoS, btnSaveQoS });
        }

        if (AuftragStatus.isNotValid(auftragStatus)) {
            GuiTools.lockComponents(new Component[] { btnAddQoS, btnSaveQoS });
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

}
