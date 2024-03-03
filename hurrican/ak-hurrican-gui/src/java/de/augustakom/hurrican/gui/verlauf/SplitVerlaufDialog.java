/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.08.2010 14:00:08
 */
package de.augustakom.hurrican.gui.verlauf;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableModelXML;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.temp.SplitSelectSubOrder;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * Dialog stellt die Auftraege zu einem Verlauf (Bauauftrag oder Projektierung) dar, und bietet die Moeglichkeit,
 * einzelne Auftraege aus dem Verlauf zu entfernen. Fuer die gewaehlten Auftraege wird jeweils ein eigener Verlauf
 * generiert. Dabei werden die Daten des urspruenglichen Verlauf-Objekts kopiert.
 */
public class SplitVerlaufDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(SplitVerlaufDialog.class);

    private static final Integer HW_EQN = 1;
    private static final Integer MGMBEZ = 2;
    private static final Integer LBZ = 3;
    private static final String TITLE = "title";
    private static final long serialVersionUID = 2378550585367656279L;

    // Modelle
    private final Long verlaufId;
    private Verlauf verlaufToSplit;

    // Services
    private BAService baService;

    private AKTableModelXML<SplitSelectSubOrder> tbMdlSelection;

    public SplitVerlaufDialog(Long verlaufId) {
        super("de/augustakom/hurrican/gui/verlauf/resources/SplitVerlaufDialog.xml");
        this.verlaufId = verlaufId;
        createGUI();
        loadData();
    }

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText(TITLE));
        AKJLabel lblSubTitle = getSwingFactory().createLabel("sub.title", AKJLabel.LEFT, Font.BOLD);

        tbMdlSelection = new AKTableModelXML<>(
                "de/augustakom/hurrican/gui/shared/resources/SplitSelectSubOrdersTableModel.xml");
        AKJTable tbSelection = new AKJTable(tbMdlSelection, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbSelection.fitTable(new int[] { 100, 120, 120, 120, 100, 180, 120, 100, 180 });
        AKJScrollPane spTable = new AKJScrollPane(tbSelection);
        spTable.setPreferredSize(new Dimension(750, 120));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(lblSubTitle, BorderLayout.NORTH);
        getChildPanel().add(spTable, BorderLayout.CENTER);
    }

    @Override
    public final void loadData() {
        try {
            baService = getCCService(BAService.class);

            verlaufToSplit = baService.findVerlauf(verlaufId);
            if (verlaufToSplit != null) {
                Set<Long> orderIds = new HashSet<>();
                orderIds.addAll(verlaufToSplit.getSubAuftragsIds());  // copy sub orders to new set to use in GUI
                if (CollectionTools.isEmpty(orderIds)) {
                    this.prepare4Close();
                    this.setValue(null);
                    throw new HurricanGUIException(
                            "Dem Verlauf ist nur ein Auftrag zugeordnet. Ein Herausloesen von Auftraegen ist somit nicht moeglich!");
                }

                orderIds.add(verlaufToSplit.getAuftragId());

                for (Long orderId : orderIds) {
                    SplitSelectSubOrder model = fillSelectSubOrder(orderId);
                    tbMdlSelection.addObject(model);
                }
            }
            else {
                throw new HurricanGUIException("Verlauf konnte nicht ermittelt werden!");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * Ermittelt Werte für den SplitSelectSubOrder Datensatz basierend auf der Auftrags ID.
     */
    private SplitSelectSubOrder fillSelectSubOrder(Long orderId)
            throws FindException, ServiceNotFoundException {
        EndstellenService endstellenService = getCCService(EndstellenService.class);
        ProduktService produktService = getCCService(ProduktService.class);

        SplitSelectSubOrder model = new SplitSelectSubOrder();
        model.setSelected(Boolean.FALSE);

        // Auftrag ID
        model.setAuftragId(orderId);

        // Anschlussart
        Produkt produkt = produktService.findProdukt4Auftrag(orderId);
        model.setAnschlussart((produkt != null) ? produkt.getAnschlussart() : null);

        // Endstellen Daten fuer B und A
        List<Endstelle> endstellen = endstellenService.findEndstellen4Auftrag(orderId);

        //Endstelle B
        Endstelle endstelle = getEndstelleOfType(endstellen, Endstelle.ENDSTELLEN_TYP_B);
        Map<Integer, String> properties = getEndstellenRelatedProperties(endstelle);
        if ((properties != null) && (!properties.isEmpty())) {
            model.setEsBHW_EQN(properties.get(HW_EQN));
            model.setEsBMgmBez(properties.get(MGMBEZ));
            model.setEsBLbz(properties.get(LBZ));
        }

        //Endstelle A
        endstelle = getEndstelleOfType(endstellen, Endstelle.ENDSTELLEN_TYP_A);
        properties = getEndstellenRelatedProperties(endstelle);
        if ((properties != null) && (!properties.isEmpty())) {
            model.setEsAHW_EQN(properties.get(HW_EQN));
            model.setEsAMgmBez(properties.get(MGMBEZ));
            model.setEsALbz(properties.get(LBZ));
        }

        return model;
    }

    /**
     * Ermittelt die Endstelle mit dem angegebenen Typ.
     */
    private Endstelle getEndstelleOfType(List<Endstelle> endstellen, String type) {
        if ((endstellen == null) || (endstellen.isEmpty())) {
            return null;
        }

        for (Endstelle endstelle : endstellen) {
            if (endstelle.isEndstelleOfType(type)) {
                return endstelle;
            }
        }

        return null;
    }

    /**
     * Ermittelt HW_EQN, Managementbez und Leitungsbezeichnung.
     */
    private Map<Integer, String> getEndstellenRelatedProperties(Endstelle endstelle)
            throws FindException, ServiceNotFoundException {
        if (endstelle == null) {
            return null;
        }

        RangierungsService rangierungsService = getCCService(RangierungsService.class);
        CarrierService carrierService = getCCService(CarrierService.class);
        Map<Integer, String> properties = new HashMap<>();
        HWService hardwareService = getCCService(HWService.class);

        Rangierung rangierung = rangierungsService.findRangierungWithEQ(endstelle.getRangierId());
        if (rangierung != null) {
            Equipment equipment = rangierung.getEquipmentIn();
            if (equipment != null) {
                //HW_EQN
                properties.put(HW_EQN, equipment.getHwEQN());

                // Managementbez.
                if (equipment.getHwBaugruppenId() != null) {
                    HWRack rack = hardwareService.findRackForBaugruppe(equipment.getHwBaugruppenId());
                    if (rack != null) {
                        properties.put(MGMBEZ, rack.getManagementBez());
                    }
                }
            }
        }

        //Leitungsbezeichnung
        //Da der Service die Bestellungen bereits absteigend sortiert (Sortierkriterium is der PK 'CB_ID')
        //in einer Liste übergibt, sollte die aktuellste Bestellung gleich die erste sein.
        List<Carrierbestellung> bestellungen = carrierService.findCBs4EndstelleTx(endstelle.getId());
        if ((bestellungen != null) && (!bestellungen.isEmpty())) {
            properties.put(LBZ, bestellungen.get(0).getLbz());
        }

        return properties;
    }

    @Override
    protected void doSave() {
        try {
            StringBuilder orderIds4Message = new StringBuilder();
            Set<Long> orderIdsToRemove = new HashSet<>();
            int rowCount = tbMdlSelection.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                SplitSelectSubOrder model = tbMdlSelection.getDataAtRow(i);
                if (BooleanTools.nullToFalse(model.getSelected())) {
                    orderIdsToRemove.add(model.getAuftragId());
                    if (orderIds4Message.length() > 0) {
                        orderIds4Message.append(", ");
                    }
                    orderIds4Message.append(String.format("%s", model.getAuftragId()));
                }
            }

            if (CollectionTools.isEmpty(orderIdsToRemove)) {
                throw new HurricanGUIException("Bitte waehlen Sie die Auftraege aus, die herausgeloest werden sollen.");
            }
            else {
                int option = MessageHelper.showYesNoQuestion(this,
                        getSwingFactory().getText("execute.split.msg", orderIds4Message.toString()),
                        getSwingFactory().getText("execute.split.title"));
                if (option == YES_OPTION) {
                    baService.splitVerlauf(verlaufToSplit, orderIdsToRemove, HurricanSystemRegistry.instance().getSessionId());

                    prepare4Close();
                    setValue(OK_OPTION);
                }
            }
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
    public void update(Observable o, Object arg) {
    }

}
