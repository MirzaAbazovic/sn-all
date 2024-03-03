/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.09.2010
 */
package de.augustakom.hurrican.gui.shared;

import java.awt.*;
import java.beans.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKNavigationBarListener;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJNavigationBar;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.RangierungsService;

/**
 * Dialog zur Anzeige von Rangierungen, die zu einem Taifun-Auftrag oder einem Bauauftrag gehoeren.
 */
public final class PortsDialog extends AbstractServiceOptionDialog implements AKNavigationBarListener, AKDataLoaderComponent {
    private static final Logger LOGGER = Logger.getLogger(PortsDialog.class);

    private AKJNavigationBar navBar = null;
    private Equipment4RangierungTableModel tbMdlEquipment = null;

    private AKJTextField tfAuftragId = null;
    private AKJTextField tfAuftragStatus = null;
    private AKJTextField tfEsTyp = null;
    private AKJTextField tfVbz = null;

    private Long verlaufId;
    private Long auftragNoOrig;
    private BAService baService;
    private CCAuftragService auftragService;
    private EndstellenService endstellenService;
    private RangierungsService rangierungsService;
    private PhysikService physikService;

    private PortsDialog() {
        super("de/augustakom/hurrican/gui/shared/resources/PortsDialog.xml");
        initServices();
        createGUI();
    }

    /**
     * Erzeugt einen neuen PortsDialog der alle Ports anzeigt die zu (Subaufträgen) eines Bauauftrags gehören.
     *
     * @param verlaufId
     */
    public static PortsDialog createWithVerlaufId(Long verlaufId) {
        PortsDialog dialog = new PortsDialog();
        dialog.verlaufId = verlaufId;
        dialog.loadData();
        return dialog;
    }

    /**
     * Erzeugt einen neuen PortsDialog der alle Ports anzeigt die zu einem Taifun-Auftrag gehören.
     *
     * @param auftragNoOrig
     */
    public static PortsDialog createWithAuftragNoOrig(Long auftragNoOrig) {
        PortsDialog dialog = new PortsDialog();
        dialog.auftragNoOrig = auftragNoOrig;
        dialog.loadData();
        return dialog;
    }


    private void initServices() {
        try {
            baService = getCCService(BAService.class);
            auftragService = getCCService(CCAuftragService.class);
            endstellenService = getCCService(EndstellenService.class);
            rangierungsService = getCCService(RangierungsService.class);
            physikService = getCCService(PhysikService.class);
        }
        catch (ServiceNotFoundException e) {
            LOGGER.error(e, e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected final void createGUI() {
        setTitle("Ports");
        configureButton(CMD_SAVE, "OK", null, true, true);
        configureButton(CMD_CANCEL, null, null, false, false);

        navBar = new AKJNavigationBar(this, false, false);

        tbMdlEquipment = new Equipment4RangierungTableModel();
        AKJTable tbEquipment = new AKJTable(tbMdlEquipment, JTable.AUTO_RESIZE_OFF, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tbMdlEquipment.setTable(tbEquipment);
        tbEquipment.fitTable(new int[] { 105, 125, 125, 125, 125 });
        tbEquipment.setFilterEnabled(false);
        tbEquipment.setPopupChangeSelection(false);

        AKJScrollPane spTable = new AKJScrollPane(tbEquipment);
        spTable.setPreferredSize(new Dimension(615, 385));

        tfAuftragId = getSwingFactory().createTextField("auftrag.id", false);
        tfAuftragStatus = getSwingFactory().createTextField("auftrag.status", false);
        tfEsTyp = getSwingFactory().createTextField("es.typ", false);
        tfVbz = getSwingFactory().createTextField("vbz", false);

        AKJLabel lblEsTyp = getSwingFactory().createLabel("es.typ");
        AKJLabel lblAuftragId = getSwingFactory().createLabel("auftrag.id");
        AKJLabel lblAuftragStatus = getSwingFactory().createLabel("auftrag.status");
        AKJLabel lblVbz = getSwingFactory().createLabel("vbz");

        AKJPanel navPanel = new AKJPanel();
        navPanel.setLayout(new GridBagLayout());
        navPanel.add(navBar, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        navPanel.add(lblEsTyp, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        navPanel.add(tfEsTyp, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        navPanel.add(lblAuftragId, GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.NONE));
        navPanel.add(tfAuftragId, GBCFactory.createGBC(0, 0, 4, 0, 1, 1, GridBagConstraints.NONE));
        navPanel.add(lblAuftragStatus, GBCFactory.createGBC(0, 0, 5, 0, 1, 1, GridBagConstraints.NONE));
        navPanel.add(tfAuftragStatus, GBCFactory.createGBC(0, 0, 6, 0, 1, 1, GridBagConstraints.NONE));
        navPanel.add(lblVbz, GBCFactory.createGBC(0, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        navPanel.add(tfVbz, GBCFactory.createGBC(100, 0, 4, 1, 3, 1, GridBagConstraints.HORIZONTAL));
        navPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 7, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(navPanel, BorderLayout.NORTH);
        getChildPanel().add(spTable, BorderLayout.CENTER);
    }

    @Override
    public final void loadData() {
        try {
            List<Long> auftragsIds = new ArrayList<Long>();
            if (verlaufId != null) {
                Verlauf verlauf = baService.findVerlauf(verlaufId);
                auftragsIds.add(verlauf.getAuftragId());
                auftragsIds.addAll(verlauf.getSubAuftragsIds());
            }
            else if (auftragNoOrig != null) {
                List<AuftragDaten> auftragDaten = auftragService.findAuftragDaten4OrderNoOrig(auftragNoOrig);
                for (AuftragDaten daten : auftragDaten) {
                    auftragsIds.add(daten.getAuftragId());
                }
            }

            List<Endstelle> endstellen = new ArrayList<Endstelle>();
            for (Long auftragId : auftragsIds) {
                List<Endstelle> endstellen4Auftrag = endstellenService.findEndstellen4Auftrag(auftragId);
                for (Endstelle endstelle : endstellen4Auftrag) {
                    if ((endstelle.getRangierId() != null) || (endstelle.getRangierIdAdditional() != null)) {
                        endstellen.add(endstelle); // nur Endstellen betrachten die sinnvolle Infos enthalten
                    }
                }
            }
            navBar.setData(endstellen);
        }
        catch (Exception e) {
            LOGGER.error(e, e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    public void showNavigationObject(Object obj, int number) throws PropertyVetoException {
        if (obj == null) {
            return;
        }
        tbMdlEquipment.removeAll();
        try {
            setWaitCursor();

            Endstelle endstelle = (Endstelle) obj;
            tfEsTyp.setText(endstelle.getEndstelleTyp());
            AuftragDaten auftragDaten = auftragService.findAuftragDatenByEndstelle(endstelle.getId());
            tfAuftragId.setText(auftragDaten.getAuftragId().toString());
            AuftragStatus auftragStatus = auftragService.findAuftragStatus(auftragDaten.getAuftragStatusId());
            tfAuftragStatus.setText(auftragStatus.getStatusText());
            VerbindungsBezeichnung vbz = physikService.findVerbindungsBezeichnungByAuftragId(auftragDaten.getAuftragId());
            tfVbz.setText((vbz != null) ? vbz.getVbz() : null);

            Rangierung rangierung = null;
            Rangierung rangierungAdd = null;
            tbMdlEquipment.setEndstelle(endstelle);

            // Rangierung zur Endstelle laden
            rangierung = rangierungsService.findRangierungWithEQ(endstelle.getRangierId());
            rangierungAdd = (endstelle.getRangierIdAdditional() != null)
                    ? rangierungsService.findRangierungWithEQ(endstelle.getRangierIdAdditional()) : null;

            if ((rangierung != null) || (rangierungAdd != null)) {
                tbMdlEquipment.setRangierung(rangierung, rangierungAdd);
            }
        }
        catch (Exception e) {
            LOGGER.error(e, e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            setDefaultCursor();
        }
    }

    @Override
    protected void doSave() {
        prepare4Close();
        setValue(Integer.valueOf(OK_OPTION));
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    protected void execute(String command) {
    }

}
