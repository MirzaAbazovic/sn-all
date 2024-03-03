/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.05.2012 16:35:54
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.List;
import javax.annotation.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.fttx.EqVlan;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.fttx.EkpFrameContractService;
import de.augustakom.hurrican.service.cc.fttx.VlanService;

/**
 * Dialog zum Anzeigen und Neuerstellen von VLANs (fuer FTTX).
 *
 *
 */
public class VlanDialog extends AbstractServiceOptionDialog {

    private static final Logger LOGGER = Logger.getLogger(VlanDialog.class);
    private static final String NEW_VLAN = "new.vlans";

    private final Long eqInPortId;
    private final Endstelle endstelle;

    private AKReflectionTableModel<EqVlan> vlanTableModel;
    private List<EqVlan> newEqVlans;

    private VlanService vlanService;
    private CCAuftragService auftragService;
    private AKJButton btnNewVlan;

    public VlanDialog(Long eqInPortId, Endstelle endstelle) {
        super("de/augustakom/hurrican/gui/auftrag/resources/VlanDialog.xml");
        this.eqInPortId = eqInPortId;
        this.endstelle = endstelle;

        initServices();
        createGUI();
        loadData();
    }

    private void initServices() {
        try {
            vlanService = getCCService(VlanService.class);
            EkpFrameContractService ekpFrameContractService = getCCService(EkpFrameContractService.class);
            auftragService = getCCService(CCAuftragService.class);
        }
        catch (ServiceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected final void createGUI() {
        setTitle("VLANs-History");
        configureButton(CMD_CANCEL, "Schliessen", null, true, true);
        configureButton(CMD_SAVE, "Speichern", "Neue VLANs speichern", true, false);

        btnNewVlan = getSwingFactory().createButton(NEW_VLAN, getActionListener(), null);
        AKJPanel btnNewPnl = new AKJPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        btnNewPnl.add(btnNewVlan);

        vlanTableModel = new AKReflectionTableModel<EqVlan>(new String[] {
                "C-VLAN Typ", "Typ", "C-VLAN", "S-VLAN EKP",
                "S-VLAN OLT", "S-VLAN MDU", "Gültig von", "Gültig bis" },
                new String[] { "cvlanTyp", "type", "cvlan", "svlanEkp",
                        "svlanOlt", "svlanMdu", "gueltigVon", "gueltigBis" },
                new Class[] { String.class, String.class, Integer.class, Integer.class,
                        Integer.class, Integer.class, Date.class, Date.class }
        );
        AKJTable vlanTable = new AKJTable(vlanTableModel, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        AKJScrollPane spVlans = new AKJScrollPane(vlanTable, new Dimension(650, 250));

        AKJPanel vlanPnl = new AKJPanel(new BorderLayout());
        vlanPnl.add(btnNewPnl, BorderLayout.WEST);

        vlanPnl.add(spVlans, BorderLayout.CENTER);

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(vlanPnl, BorderLayout.NORTH);
    }

    private void loadData() {
        List<EqVlan> eqVlans = vlanService.findEqVlans(eqInPortId);
        boolean hasNewEqVlans = (newEqVlans != null) && !newEqVlans.isEmpty();
        if (hasNewEqVlans) {
            eqVlans.addAll(newEqVlans);
        }
        vlanTableModel.setData(eqVlans);
        btnNewVlan.setEnabled(!hasFutureVlans());
        getButton(CMD_SAVE).setEnabled(hasNewEqVlans);
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    protected void doSave() {
        if ((newEqVlans != null) && !newEqVlans.isEmpty()) {
            try {
                vlanService.addEqVlans(newEqVlans, null);
                newEqVlans = null;
                loadData();
            }
            catch (StoreException e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }

    }

    @Override
    protected void execute(String command) {
        if (NEW_VLAN.equals(command)) {
            calcNewVlan();
        }
    }

    private boolean hasFutureVlans() {
        Date today = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        for (EqVlan eqVlan : vlanTableModel.getData()) {
            if (eqVlan.getGueltigVon().compareTo(today) > 0) {
                return true;
            }
        }
        return false;
    }

    private void calcNewVlan() {
        try {
            if (hasFutureVlans()) {
                throw new HurricanGUIException(
                        "VLAN Berechnung ist nicht möglich, da es bereits zukünftig anfangende VLANs gibt");
            }
            AuftragDaten auftragDaten = findAuftragDatenByEndstelle();

            newEqVlans = vlanService.calculateEqVlans(auftragDaten.getAuftragId(),
                    auftragDaten.getProdId(), LocalDate.now());
            loadData();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }


    @Nonnull
    private AuftragDaten findAuftragDatenByEndstelle() throws FindException, HurricanGUIException {
        AuftragDaten auftragDaten = auftragService.findAuftragDatenByEndstelle(endstelle.getId());
        if (auftragDaten == null) {
            throw new HurricanGUIException(String.format(
                    "AuftragDaten zur Endstelle %s konnten nicht ermittelt werden!", endstelle.getId()));
        }
        return auftragDaten;
    }

}
