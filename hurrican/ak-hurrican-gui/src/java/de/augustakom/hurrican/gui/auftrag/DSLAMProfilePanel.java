/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.04.2007 09:04:02
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReferenceAwareTableModel;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.IAuftragStatusValidator;
import de.augustakom.hurrican.model.cc.Auftrag2DSLAMProfile;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.DSLAMProfile;
import de.augustakom.hurrican.model.cc.DSLAMProfileChangeReason;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.cc.DSLAMService;
import de.augustakom.hurrican.service.cc.QueryCCService;


/**
 * Panel fuer die Darstellung und Modifikation der DSLAM-Profile eines Auftrags.
 *
 *
 */
public class DSLAMProfilePanel extends AbstractAuftragPanel implements AKDataLoaderComponent,
        IAuftragStatusValidator {

    private static final Logger LOGGER = Logger.getLogger(DSLAMProfilePanel.class);

    private CCAuftragModel auftragModel = null;

    private AKReferenceAwareTableModel<Auftrag2DSLAMProfile> tbMdlProfiles = null;
    private AKJButton btnNew = null;

    /**
     * Default-Const.
     */
    public DSLAMProfilePanel() {
        super("de/augustakom/hurrican/gui/auftrag/resources/DSLAMProfilePanel.xml");
        createGUI();
        loadData();
    }

    @Override
    protected final void createGUI() {
        btnNew = getSwingFactory().createButton("new.profile", getActionListener(), null);

        AKJPanel btnPnl = new AKJPanel(new GridBagLayout());
        btnPnl.add(btnNew, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE, new Insets(20, 2, 10, 2)));
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 1, 1, 1, GridBagConstraints.VERTICAL));

        tbMdlProfiles = new AKReferenceAwareTableModel<>(
                new String[] { "DSLAM-Profil", "gueltig von", "gueltig bis", "zugeordnet durch", "Bemerkung", "Grund" },
                new String[] { "dslamProfileId", "gueltigVon", "gueltigBis", "userW", "bemerkung", "changeReasonId" },
                new Class[] { String.class, Date.class, Date.class, String.class, String.class, String.class });
        AKJTable tbProfiles = new AKJTable(tbMdlProfiles, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbProfiles.attachSorter();
        tbProfiles.fitTable(new int[] { 200, 90, 90, 100, 200, 80 });
        AKJScrollPane spProfiles = new AKJScrollPane(tbProfiles, new Dimension(520, 100));

        this.setLayout(new BorderLayout());
        this.add(btnPnl, BorderLayout.WEST);
        this.add(spProfiles, BorderLayout.CENTER);

        manageGUI(btnNew);
    }

    @Override
    public final void loadData() {
        try {
            DSLAMService service = getCCService(DSLAMService.class);
            List<DSLAMProfile> profiles = service.findDSLAMProfiles();

            QueryCCService queryService = getCCService(QueryCCService.class);
            List<DSLAMProfileChangeReason> reasons = queryService.findAll(DSLAMProfileChangeReason.class);

            tbMdlProfiles.addReference(0, CollectionMapConverter.convert2Map(profiles, "getId", null), "name");
            tbMdlProfiles.addReference(5, CollectionMapConverter.convert2Map(reasons, "getId", null), "name");
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public void setModel(Observable model) {
        if (model != null) {
            if (model instanceof CCAuftragModel) {
                this.auftragModel = (CCAuftragModel) model;
            }
            else {
                this.auftragModel = null;
            }

            readModel();
        }
        else {
            this.auftragModel = null;
            clear();
        }
    }

    /* Entfernt alle Daten. */
    private void clear() {
        tbMdlProfiles.removeAll();
    }

    @Override
    public void readModel() {
        clear();
        if (this.auftragModel == null) {
            return;
        }
        try {
            setWaitCursor();
            DSLAMService service = getCCService(DSLAMService.class);
            List<Auftrag2DSLAMProfile> profiles =
                    service.findAuftrag2DSLAMProfiles(auftragModel.getAuftragId());

            tbMdlProfiles.setData(profiles);
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
        if ("new.profile".equals(command)) {
            assignNewProfile();
        }
    }

    /*
     * Oeffnet einen Dialog, um dem Auftrag ein neues DSLAM-Profil zuzuordnen.
     * Die Methode fuegt das Profil lediglich der Tabelle hinzu. Die eigentliche
     * Speicherung erfolgt ueber <code>saveModel</code>.
     */
    private void assignNewProfile() {
        if (auftragModel == null) { return; }
        try {
            DSLAMProfileSelectionDialog dlg = new DSLAMProfileSelectionDialog(auftragModel.getAuftragId());
            Object result = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
            if (result instanceof Auftrag2DSLAMProfile) {
                Auftrag2DSLAMProfile a2dp = (Auftrag2DSLAMProfile) result;
                tbMdlProfiles.addObject(a2dp);

                DSLAMService service = getCCService(DSLAMService.class);
                service.changeDSLAMProfile(auftragModel.getAuftragId(), a2dp.getDslamProfileId(),
                        a2dp.getGueltigVon(), HurricanSystemRegistry.instance().getCurrentUserName(),
                        a2dp.getChangeReasonId(), a2dp.getBemerkung());
                readModel();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    public boolean hasModelChanged() {
        return false;
    }

    @Override
    public void saveModel() throws AKGUIException {
    }

    @Override
    public Object getModel() {
        return null;
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    public void validate4Status(Long auftragStatus) {
        if (NumberTools.equal(auftragStatus, AuftragStatus.AUFTRAG_GEKUENDIGT)) {
            if ((this.auftragModel instanceof AuftragDaten) &&
                    DateTools.isDateAfter(((AuftragDaten) this.auftragModel).getKuendigung(), new Date())) {
                // so lange Kuendigungsdatum noch nicht erreicht ist, darf das DSLAM-Profil geaendert werden
                btnNew.setEnabled(true);
            }
            else {
                btnNew.setEnabled(false);
            }
        }
        else {
            btnNew.setEnabled(true);
        }
    }

}


