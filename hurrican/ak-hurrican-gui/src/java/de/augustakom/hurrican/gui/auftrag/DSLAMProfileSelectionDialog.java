/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.04.2007 10:14:56
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.util.*;
import java.util.List;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.Auftrag2DSLAMProfile;
import de.augustakom.hurrican.model.cc.DSLAMProfile;
import de.augustakom.hurrican.service.cc.DSLAMService;
import de.augustakom.hurrican.service.cc.QueryCCService;


/**
 * Dialog zur Auswahl eines DSLAM-Profils. <br> Das Profil und das zu verwendende Datum werden in einem Objekt vom Typ
 * <code>Auftrag2DSLAMProfile</code> gespeichert und ueber die Methode <code>setValue</code> abgelegt. <br> <br>
 * Wichtig: der Dialog ordnet das Profil dem Auftrag nicht persistent zu! Dafuer ist der Client zustaendig.
 *
 *
 */
public class DSLAMProfileSelectionDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(DSLAMProfileSelectionDialog.class);

    private AKReferenceField rfProfile = null;
    private AKJDateComponent dcVon = null;
    private AKReferenceField rfReason = null;
    private AKJTextArea taBemerkung = null;

    private Long auftragId = null;

    /**
     * Default-Const.
     *
     * @param auftragId
     */
    public DSLAMProfileSelectionDialog(Long auftragId) {
        super("de/augustakom/hurrican/gui/auftrag/resources/DSLAMProfileSelectionDialog.xml");
        this.auftragId = auftragId;
        createGUI();
        loadData();
    }

    @Override
    protected final void createGUI() {
        setTitle("DSLAM-Profil");
        AKJLabel lblProfile = getSwingFactory().createLabel("dslam.profile");
        AKJLabel lblVon = getSwingFactory().createLabel("gueltig.von");
        AKJLabel lblReason = getSwingFactory().createLabel("change.reason");
        AKJLabel lblBemerkung = getSwingFactory().createLabel("bemerkung");

        rfProfile = getSwingFactory().createReferenceField("dslam.profile");
        dcVon = getSwingFactory().createDateComponent("gueltig.von");
        rfReason = getSwingFactory().createReferenceField("change.reason");
        taBemerkung = getSwingFactory().createTextArea("bemerkung");
        AKJScrollPane spBemerkung = new AKJScrollPane(taBemerkung, new Dimension(120, 50));

        AKJPanel child = getChildPanel();
        child.setLayout(new GridBagLayout());
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        child.add(lblProfile, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.NONE));
        child.add(rfProfile, GBCFactory.createGBC(100, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblVon, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(dcVon, GBCFactory.createGBC(100, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblReason, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(rfReason, GBCFactory.createGBC(100, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblBemerkung, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(spBemerkung, GBCFactory.createGBC(100, 0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 4, 5, 1, 1, GridBagConstraints.NONE));
    }

    @Override
    public final void loadData() {
        try {
            DSLAMService ds = getCCService(DSLAMService.class);
            List<DSLAMProfile> profiles = ds.findValidDSLAMProfiles4Auftrag(auftragId);

            if (CollectionTools.isNotEmpty(profiles)) {
                // in Absprache mit SDH immer alle (gueltigen) Profile zur Auswahl bieten!
                QueryCCService queryService = getCCService(QueryCCService.class);
                rfProfile.setReferenceList(profiles);
                rfProfile.setFindService(queryService);
                rfReason.setFindService(queryService);
            }
            else {
                getButton(CMD_SAVE).setEnabled(false);
                throw new HurricanGUIException("Fuer den BaugruppenTyp gibt es keine DSLAM-Profile!");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected void doSave() {
        try {
            Long profId = rfProfile.getReferenceIdAs(Long.class);
            Date von = dcVon.getDate(null);

            if ((profId == null) || (von == null)) {
                throw new HurricanGUIException("Es muss ein Profil und ein Datum ausgewaehlt sein!");
            }

            Auftrag2DSLAMProfile a2dp = new Auftrag2DSLAMProfile();
            a2dp.setDslamProfileId(profId);
            a2dp.setGueltigVon(von);
            a2dp.setChangeReasonId(rfReason.getReferenceIdAs(Long.class));
            a2dp.setBemerkung(taBemerkung.getText(null));

            if (a2dp.getChangeReasonId() == null) {
                throw new HurricanGUIException("Bitte geben Sie einen Grund fuer die Profil-Änderung an.");
            }

            prepare4Close();
            setValue(a2dp);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable arg0, Object arg1) {
    }

}


