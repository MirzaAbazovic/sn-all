/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.03.2009 09:11:43
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.util.List;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.iface.ISimpleFindService;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.base.IAuftragStatusValidator;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragIntern;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.view.HVTGruppeStdView;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.cc.AuftragInternService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.QueryCCService;


/**
 * Panel fuer die Daten von sog. 'internen Auftraegen'.
 *
 *
 */
public class AuftragInternPanel extends AbstractAuftragPanel implements IAuftragStatusValidator,
        AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(AuftragInternPanel.class);

    private static final String WATCH_AUFTRAG_INTERN = "watch.auftrag.intern";

    // GUI-Objekte
    private AKReferenceField rfHVT = null;
    private AKReferenceField rfWorkType = null;
    private AKJTextField tfProjectDir = null;
    private AKJTextField tfBedarfsNr = null;
    private AKJTextField tfContactName = null;
    private AKJTextField tfContactMail = null;
    private AKJTextField tfContactPhone = null;
    private AKReferenceField rfExtSrvProv = null;
    private AKJDateComponent dcExtSrvDate = null;
    private AKJTextArea taDesc = null;
    private AKJFormattedTextField tfWorkingHours = null;

    // Modelle
    private CCAuftragModel model = null;
    private AuftragIntern auftragIntern = null;

    /**
     * Default-Const.
     */
    public AuftragInternPanel() {
        super("de/augustakom/hurrican/gui/auftrag/resources/AuftragInternPanel.xml");
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblHVT = getSwingFactory().createLabel("hvt.standort");
        AKJLabel lblWorkType = getSwingFactory().createLabel("work.type");
        AKJLabel lblProjectDir = getSwingFactory().createLabel("project.dir");
        AKJLabel lblBedarfsNr = getSwingFactory().createLabel("bedarfs.nr");
        AKJLabel lblContactName = getSwingFactory().createLabel("contact.name");
        AKJLabel lblContactMail = getSwingFactory().createLabel("contact.mail");
        AKJLabel lblContactPhone = getSwingFactory().createLabel("contact.phone");
        AKJLabel lblExtSrvProv = getSwingFactory().createLabel("ext.service.provider");
        AKJLabel lblExtSrvDate = getSwingFactory().createLabel("ext.service.provider.date");
        AKJLabel lblDesc = getSwingFactory().createLabel("description");
        AKJLabel lblWorkingHours = getSwingFactory().createLabel("working.hours");

        rfHVT = getSwingFactory().createReferenceField("hvt.standort");
        rfWorkType = getSwingFactory().createReferenceField("work.type");
        tfProjectDir = getSwingFactory().createTextField("project.dir");
        tfBedarfsNr = getSwingFactory().createTextField("bedarfs.nr");
        tfContactName = getSwingFactory().createTextField("contact.name");
        tfContactMail = getSwingFactory().createTextField("contact.mail");
        tfContactPhone = getSwingFactory().createTextField("contact.phone");
        rfExtSrvProv = getSwingFactory().createReferenceField("ext.service.provider");
        dcExtSrvDate = getSwingFactory().createDateComponent("ext.service.provider.date");
        taDesc = getSwingFactory().createTextArea("description", true, true, true);
        AKJScrollPane spDesc = new AKJScrollPane(taDesc, new Dimension(250, 200));
        tfWorkingHours = getSwingFactory().createFormattedTextField("working.hours");
        AKJButton btnOpenProjDir = getSwingFactory().createButton("open.project.dir", getActionListener());
        btnOpenProjDir.setPreferredSize(new Dimension(20, 20));

        AKJPanel defPnl = new AKJPanel(new GridBagLayout(), getSwingFactory().getText("intern.work.def"));
        defPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        defPnl.add(lblHVT, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        defPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        defPnl.add(rfHVT, GBCFactory.createGBC(100, 0, 3, 0, 2, 1, GridBagConstraints.HORIZONTAL));
        defPnl.add(lblWorkType, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        defPnl.add(rfWorkType, GBCFactory.createGBC(100, 0, 3, 1, 2, 1, GridBagConstraints.HORIZONTAL));
        defPnl.add(lblProjectDir, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        defPnl.add(tfProjectDir, GBCFactory.createGBC(100, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        defPnl.add(btnOpenProjDir, GBCFactory.createGBC(100, 0, 4, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        defPnl.add(lblBedarfsNr, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        defPnl.add(tfBedarfsNr, GBCFactory.createGBC(100, 0, 3, 3, 2, 1, GridBagConstraints.HORIZONTAL));
        defPnl.add(lblDesc, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        defPnl.add(spDesc, GBCFactory.createGBC(100, 0, 3, 4, 2, 6, GridBagConstraints.HORIZONTAL));
        defPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 5, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel conPnl = new AKJPanel(new GridBagLayout(), getSwingFactory().getText("contacts"));
        conPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        conPnl.add(lblContactName, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        conPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        conPnl.add(tfContactName, GBCFactory.createGBC(100, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        conPnl.add(lblContactMail, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        conPnl.add(tfContactMail, GBCFactory.createGBC(100, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        conPnl.add(lblContactPhone, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        conPnl.add(tfContactPhone, GBCFactory.createGBC(100, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        conPnl.add(lblExtSrvProv, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        conPnl.add(rfExtSrvProv, GBCFactory.createGBC(100, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        conPnl.add(lblExtSrvDate, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        conPnl.add(dcExtSrvDate, GBCFactory.createGBC(100, 0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel wrkPnl = new AKJPanel(new GridBagLayout(), getSwingFactory().getText("working.time"));
        wrkPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        wrkPnl.add(lblWorkingHours, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        wrkPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        wrkPnl.add(tfWorkingHours, GBCFactory.createGBC(100, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        this.setLayout(new GridBagLayout());
        this.add(defPnl, GBCFactory.createGBC(0, 0, 0, 0, 1, 2, GridBagConstraints.VERTICAL));
        this.add(wrkPnl, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 3, 1, 1, GridBagConstraints.VERTICAL));
        this.add(conPnl, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL, 15));
        this.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 1, 1, 1, 3, GridBagConstraints.VERTICAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 2, 3, 1, 1, GridBagConstraints.BOTH));
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            HVTService hvts = getCCService(HVTService.class);
            List<HVTGruppeStdView> hvtViews = hvts.findHVTViews();

            ISimpleFindService sfs = getCCService(QueryCCService.class);
            rfHVT.setFindService(sfs);
            rfHVT.setReferenceList(hvtViews);

            Reference workTypeRefEx = new Reference();
            workTypeRefEx.setType(Reference.REF_TYPE_WORKING_TYPE);
            rfWorkType.setReferenceFindExample(workTypeRefEx);
            rfWorkType.setFindService(sfs);

            rfExtSrvProv.setFindService(sfs);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKSimpleModelOwner#setModel(java.util.Observable)
     */
    @Override
    public void setModel(Observable model) throws AKGUIException {
        this.model = null;
        this.auftragIntern = null;
        GuiTools.cleanFields(this);
        if (model instanceof CCAuftragModel) {
            this.model = (CCAuftragModel) model;
            readModel();
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#readModel()
     */
    @Override
    public void readModel() throws AKGUIException {
        if (this.model == null) {
            return;
        }

        AuftragDaten ad = null;
        try {
            AuftragInternService ais = getCCService(AuftragInternService.class);
            auftragIntern = ais.findByAuftragId(model.getAuftragId());
            addObjectToWatch(WATCH_AUFTRAG_INTERN, auftragIntern);

            CCAuftragService as = getCCService(CCAuftragService.class);
            ad = as.findAuftragDatenByAuftragId(model.getAuftragId());

            showValues();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            Long tmpStatusId = (ad != null) ? ad.getStatusId() : null;
            validate4Status(tmpStatusId);
        }
    }

    /* Zeigt die Daten des AuftragDaten-Objekts an. */
    private void showValues() {
        if (auftragIntern == null) {
            return;
        }

        rfHVT.setReferenceId(auftragIntern.getHvtStandortId());
        rfWorkType.setReferenceId(auftragIntern.getWorkingTypeRefId());
        tfProjectDir.setText(auftragIntern.getProjectDirectory());
        tfBedarfsNr.setText(auftragIntern.getBedarfsnummer());
        taDesc.setText(auftragIntern.getDescription());
        tfContactName.setText(auftragIntern.getContactName());
        tfContactMail.setText(auftragIntern.getContactMail());
        tfContactPhone.setText(auftragIntern.getContactPhone());
        rfExtSrvProv.setReferenceId(auftragIntern.getExtServiceProviderId());
        dcExtSrvDate.setDate(auftragIntern.getExtOrderDate());
        tfWorkingHours.setValue(auftragIntern.getWorkingHours());
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    @Override
    public void saveModel() throws AKGUIException {
        try {
            setWaitCursor();
            if (hasModelChanged()) {
                AuftragIntern toSave = (AuftragIntern) getModel();
                AuftragInternService ais = getCCService(AuftragInternService.class);
                this.auftragIntern = ais.saveAuftragIntern(toSave, makeHistory4Auftrag(toSave.getAuftragId()));

                showValues();
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

    /**
     * @see de.augustakom.common.gui.iface.AKSimpleModelOwner#getModel()
     */
    @Override
    public Object getModel() {
        if ((model == null) || (model.getAuftragId() == null)) {
            return null;
        }

        try {
            if (this.auftragIntern == null) {
                auftragIntern = new AuftragIntern();
                auftragIntern.setAuftragId(model.getAuftragId());
            }

            auftragIntern.setHvtStandortId(rfHVT.getReferenceIdAs(Long.class));
            auftragIntern.setWorkingTypeRefId(rfWorkType.getReferenceIdAs(Long.class));
            auftragIntern.setProjectDirectory(tfProjectDir.getText(null));
            auftragIntern.setBedarfsnummer(tfBedarfsNr.getText(null));
            auftragIntern.setDescription(taDesc.getText(null));
            auftragIntern.setContactName(tfContactName.getText(null));
            auftragIntern.setContactMail(tfContactMail.getText(null));
            auftragIntern.setContactPhone(tfContactPhone.getText(null));
            auftragIntern.setExtServiceProviderId(rfExtSrvProv.getReferenceIdAs(Long.class));
            auftragIntern.setExtOrderDate(dcExtSrvDate.getDate(null));
            auftragIntern.setWorkingHours(tfWorkingHours.getValueAsFloat(null));

            return auftragIntern;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }

        return null;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#hasModelChanged()
     */
    @Override
    public boolean hasModelChanged() {
        boolean inLoad = false;
        if (inLoad) {
            return false;
        }

        boolean changed = hasChanged(WATCH_AUFTRAG_INTERN, getModel());
        return changed;
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("open.project.dir".equals(command)) {
            openProjectDir();
        }
    }

    /*
     * Oeffnet den Windows-Explorer mit dem definierten Projekt-Ordner.
     */
    private void openProjectDir() {
        try {
            String projDir = tfProjectDir.getText(null);
            if (StringUtils.isBlank(projDir)) {
                throw new HurricanGUIException("Projektordner ist nicht definiert.");
            }

            Runtime.getRuntime().exec("explorer /n,/e," + projDir);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.IAuftragStatusValidator#validate4Status(java.lang.Integer)
     */
    @Override
    public void validate4Status(Long auftragStatus) {
        if (NumberTools.isGreaterOrEqual(auftragStatus, AuftragStatus.TECHNISCHE_REALISIERUNG)) {
            // im Umlauf --> Felder HVT + WorkingType sperren
            GuiTools.enableComponents(new Component[] { rfHVT, rfWorkType }, false, true);
        }
        else if (NumberTools.isGreaterOrEqual(auftragStatus, AuftragStatus.IN_BETRIEB)) {
            // in Betrieb --> alle Felder ausser Arbeitszeit sperren
            GuiTools.enableFields(this, false);
            tfWorkingHours.setEnabled(true);
        }
        else if (NumberTools.isIn(auftragStatus, new Number[] { AuftragStatus.STORNO, AuftragStatus.ABSAGE })) {
            // alle Felder sperren
            GuiTools.enableFields(this, false);
        }
        else {
            // alle Felder freischalten
            GuiTools.enableFields(this, true);
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

}


