/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.01.2010 08:28:58
 */
package de.augustakom.hurrican.gui.tools.physik;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.auftrag.PhysikZuordnungDialog;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.view.RangierungsEquipmentView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.QueryCCService;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * Dialog, um die Ports (EQ_IN) einer Rangierung zu aendern.
 *
 *
 */
public class PortChangeDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(PortChangeDialog.class);

    private static final String TITLE_DETAILS = "title.details";
    private static final String DIALOG_TITLE = "dialog.title";
    private static final String TITLE_DEST_PORTS = "title.dest.ports";
    private static final String TITLE_SOURCE_PORTS = "title.source.ports";
    private static final String TITLE_RANGIERUNG = "title.rangierung";
    private static final String TITLE_RANGIERUNG_ADD = "title.rangierung.add";
    private static final String EQ_IN = "eq.in";
    private static final String EQ_OUT = "eq.out";
    private static final String EQ_IN_ADD = "eq.in.add";
    private static final String EQ_OUT_ADD = "eq.out.add";
    private static final String EQ_IN_CHANGE = "eq.in.change";
    private static final String EQ_OUT_CHANGE = "eq.out.change";
    private static final String EQ_IN_ADD_CHANGE = "eq.in.add.change";
    private static final String EQ_OUT_ADD_CHANGE = "eq.out.add.change";
    private static final String CHANGE_REASON = "change.reason";
    private static final String SELECT_PORT = "select.port";

    private AKJTextField tfEqIn;
    private AKJTextField tfEqOut;
    private AKJTextField tfEqInAdd;
    private AKJTextField tfEqOutAdd;
    private AKJTextField tfEqInChange;
    private AKJTextField tfEqOutChange;
    private AKJTextField tfEqInAddChange;
    private AKJTextField tfEqOutAddChange;
    private AKReferenceField rfChangeReason;

    private final Endstelle endstelle;
    private final Rangierung rangierungToChange;
    private final Rangierung rangierungAddToChange;
    private Rangierung selectedRangierung;
    private Rangierung selectedRangierungAdd;
    private Equipment eqInAddChange;
    private Equipment eqOutAddChange;
    private RangierungsService rangierungsService;

    /**
     * Konstruktor fuer den Dialog mit Angabe der urspruenglichen Rangierung.
     *
     * @param endstelle
     * @param rangierungToChange    Rangierung, der ein anderer EQ_IN Port zugewiesen werden soll
     * @param rangierungAddToChange
     * @throws IllegalArgumentException wenn keine Rangierung oder eine Rangierung mit fehlender EQ_OUT/EQ_IN
     *                                  Information angegeben wird.
     */
    public PortChangeDialog(Endstelle endstelle, Rangierung rangierungToChange, Rangierung rangierungAddToChange) {
        super("de/augustakom/hurrican/gui/tools/physik/resources/PortChangeDialog.xml");
        this.endstelle = endstelle;
        this.rangierungToChange = rangierungToChange;
        this.rangierungAddToChange = rangierungAddToChange;
        checkIfRangierungIsValid();
        createGUI();
        loadData();
    }

    /* Ueberprueft, ob fuer die Rangierung ein Port-Wechsel durchgefuehrt werden darf. */
    private void checkIfRangierungIsValid() {
        if (rangierungToChange == null) {
            throw new IllegalArgumentException("Es muss eine Rangierung angegeben werden!");
        }
        else if (rangierungToChange.isFttBOrH() && (rangierungToChange.getEqInId() == null)) {
            throw new IllegalArgumentException(
                    "Die Rangierung besitzt keinen EQ_IN Port! Port-Wechsel kann nicht durchgefuehrt werden!");
        }
        else if ((!rangierungToChange.isFttBOrH() && (rangierungToChange.getEqOutId() == null)) || (rangierungToChange.getEqInId() == null)) {
            throw new IllegalArgumentException(
                    "Die Rangierung besitzt keinen EQ_OUT und/oder EQ_IN Port! Port-Wechsel kann nicht durchgefuehrt werden!");
        }
        else if (DateTools.isDateBefore(rangierungToChange.getGueltigBis(), DateTools.getHurricanEndDate())) {
            throw new IllegalArgumentException("Die Rangierung ist bereits historisiert. Port-Wechsel daher nicht erlaubt!");
        }
    }

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText(DIALOG_TITLE));
        AKJLabel lblRangierung = getSwingFactory().createLabel(TITLE_RANGIERUNG, SwingConstants.LEFT, Font.BOLD);
        AKJLabel lblRangierungAdd = getSwingFactory().createLabel(TITLE_RANGIERUNG_ADD, SwingConstants.LEFT, Font.BOLD);
        AKJLabel lblSourcePorts = getSwingFactory().createLabel(TITLE_SOURCE_PORTS, SwingConstants.LEFT, Font.BOLD);
        AKJLabel lblDestPorts = getSwingFactory().createLabel(TITLE_DEST_PORTS, SwingConstants.LEFT, Font.BOLD);
        AKJLabel lblEqIn = getSwingFactory().createLabel(EQ_IN);
        AKJLabel lblEqOut = getSwingFactory().createLabel(EQ_OUT);
        AKJLabel lblEqInChange = getSwingFactory().createLabel(EQ_IN_CHANGE);
        AKJLabel lblEqOutChange = getSwingFactory().createLabel(EQ_OUT_CHANGE);
        AKJLabel lblDetails = getSwingFactory().createLabel(TITLE_DETAILS, SwingConstants.LEFT, Font.BOLD);
        AKJLabel lblChangeReason = getSwingFactory().createLabel(CHANGE_REASON);

        tfEqIn = getSwingFactory().createTextField(EQ_IN);
        tfEqOut = getSwingFactory().createTextField(EQ_OUT);
        tfEqInAdd = getSwingFactory().createTextField(EQ_IN_ADD);
        tfEqOutAdd = getSwingFactory().createTextField(EQ_OUT_ADD);
        tfEqInChange = getSwingFactory().createTextField(EQ_IN_CHANGE);
        tfEqOutChange = getSwingFactory().createTextField(EQ_OUT_CHANGE);
        tfEqInAddChange = getSwingFactory().createTextField(EQ_IN_ADD_CHANGE);
        tfEqOutAddChange = getSwingFactory().createTextField(EQ_OUT_ADD_CHANGE);
        rfChangeReason = getSwingFactory().createReferenceField(CHANGE_REASON);
        AKJButton btnSelectPort = getSwingFactory().createButton(SELECT_PORT, getActionListener());

        AKJPanel panel = new AKJPanel(new GridBagLayout());
        panel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        panel.add(lblRangierung, GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(lblRangierungAdd, GBCFactory.createGBC(0, 0, 4, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(lblSourcePorts, GBCFactory.createGBC(0, 0, 1, 1, 2, 1, GridBagConstraints.HORIZONTAL));
        panel.add(lblEqIn, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 2, 1, 1, GridBagConstraints.NONE));
        panel.add(tfEqIn, GBCFactory.createGBC(0, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(tfEqInAdd, GBCFactory.createGBC(0, 0, 4, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(lblEqOut, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(tfEqOut, GBCFactory.createGBC(0, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(tfEqOutAdd, GBCFactory.createGBC(0, 0, 4, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(lblDestPorts, GBCFactory.createGBC(0, 0, 1, 4, 2, 1, GridBagConstraints.HORIZONTAL));
        panel.add(lblEqInChange, GBCFactory.createGBC(0, 0, 1, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(tfEqInChange, GBCFactory.createGBC(0, 0, 3, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(tfEqInAddChange, GBCFactory.createGBC(0, 0, 4, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(lblEqOutChange, GBCFactory.createGBC(0, 0, 1, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(tfEqOutChange, GBCFactory.createGBC(0, 0, 3, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(tfEqOutAddChange, GBCFactory.createGBC(0, 0, 4, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(btnSelectPort, GBCFactory.createGBC(0, 0, 3, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(lblDetails, GBCFactory.createGBC(0, 0, 1, 8, 2, 1, GridBagConstraints.HORIZONTAL));
        panel.add(lblChangeReason, GBCFactory.createGBC(0, 0, 1, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(rfChangeReason, GBCFactory.createGBC(0, 0, 3, 9, 2, 1, GridBagConstraints.HORIZONTAL));
        panel.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 5, 10, 1, 1, GridBagConstraints.BOTH));

        getChildPanel().add(panel);
    }

    @Override
    public final void loadData() {
        try {
            Reference soTypeEx = new Reference();
            soTypeEx.setType(Reference.REF_TYPE_PORT_CHANGE_REASON);
            rfChangeReason.setReferenceFindExample(soTypeEx);
            rfChangeReason.setFindService(getCCService(QueryCCService.class));

            rangierungsService = getCCService(RangierungsService.class);
            Equipment eqIn = rangierungsService.findEquipment(rangierungToChange.getEqInId());
            Equipment eqOut = rangierungsService.findEquipment(rangierungToChange.getEqOutId());
            Equipment eqInAdd = (rangierungAddToChange != null) ? rangierungsService.findEquipment(rangierungAddToChange.getEqInId()) : null;
            Equipment eqOutAdd = (rangierungAddToChange != null) ? rangierungsService.findEquipment(rangierungAddToChange.getEqOutId()) : null;

            tfEqIn.setText((eqIn != null) ? eqIn.getHwEQN() : null);
            tfEqOut.setText((eqOut != null) ? eqOut.getVerteilerLeisteStift() : null);
            tfEqInAdd.setText((eqInAdd != null) ? eqInAdd.getHwEQN() : null);
            tfEqOutAdd.setText((eqOutAdd != null) ? eqOutAdd.getHwEQN() : null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected void doSave() {
        try {
            if (selectedRangierung == null) {
                throw new HurricanGUIException("Es wurde kein Port fuer den Wechsel ausgewaehlt!");
            }
            if (rfChangeReason.getReferenceId() == null) {
                throw new HurricanGUIException("Es wurde kein Grund fuer den Wechsel angegeben!");
            }

            int option = MessageHelper.showYesNoQuestion(this, "Sollen die Ports wirklich gekreuzt werden?", "Port wechseln?");
            if (option == JOptionPane.YES_OPTION) {
                if (!checkSameUevtCluster(rangierungToChange, selectedRangierung)) {
                    option = MessageHelper.showWarningDialog(this,
                            "Die Ursprungs- u. Ziel-Rangierung sind nicht im selben ÜVt-Cluster des HVTs!\n" +
                                    "Soll der Port-Wechsel trotzdem durchgefuehrt werden?",
                            "Unterschiedliche ÜVt-Cluster!", JOptionPane.YES_NO_OPTION
                    );
                }

                if (option == JOptionPane.YES_OPTION) {
                    rangierungsService.changeEqOut((Reference) rfChangeReason.getReferenceObject(), endstelle,
                            rangierungToChange, rangierungAddToChange,
                            selectedRangierung, selectedRangierungAdd, HurricanSystemRegistry.instance().getSessionId());

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

    /**
     * @return {@code true}, falls die beiden Rangierungen (EqOut-Stifte) im selben HVT und ÜVt-Cluster sind (Anmerkung:
     * falls eine EqOut-Stift keine ÜVt-Cluster-Nr (= {@code null}) hat, ist das Ergebnis {@code false}).
     */
    private boolean checkSameUevtCluster(Rangierung rangierungToChange, Rangierung selectedRangierung) throws FindException {
        if (!rangierungToChange.isFttBOrH()) {
            Equipment srcEqOut = rangierungsService.findEquipment(rangierungToChange.getEqOutId());
            Equipment destEqOut = rangierungsService.findEquipment(selectedRangierung.getEqOutId());

            return (srcEqOut.getHvtIdStandort().equals(destEqOut.getHvtIdStandort()))
                    && ((srcEqOut.getUevtClusterNo() != null) && srcEqOut.getUevtClusterNo().equals(destEqOut.getUevtClusterNo()));
        }
        return true;
    }

    @Override
    protected void execute(String command) {
        if (SELECT_PORT.equals(command)) {
            PhysikZuordnungDialog portSelectionDlg = new PhysikZuordnungDialog(endstelle, false, false);
            Object result = DialogHelper.showDialog(getMainFrame(), portSelectionDlg, true, true);
            if (result instanceof RangierungsEquipmentView) {
                try {
                    RangierungsEquipmentView selectedRang = (RangierungsEquipmentView) result;
                    selectedRangierung = rangierungsService.findRangierung(selectedRang.getRangierId());
                    Equipment eqInChange = rangierungsService.findEquipment(selectedRangierung.getEqInId());
                    Equipment eqOutChange = rangierungsService.findEquipment(selectedRangierung.getEqOutId());

                    if (selectedRangierung.getLeitungGesamtId() != null) {
                        List<Rangierung> combined = rangierungsService.findByLtgGesId(selectedRangierung.getLeitungGesamtId());
                        if (CollectionTools.isNotEmpty(combined)) {
                            for (Rangierung rangierungAdd : combined) {
                                if (NumberTools.notEqual(selectedRangierung.getId(), rangierungAdd.getId())
                                        && DateTools.isDateEqual(rangierungAdd.getGueltigBis(), DateTools.getHurricanEndDate())) {
                                    selectedRangierungAdd = rangierungAdd;
                                    eqInAddChange = rangierungsService.findEquipment(selectedRangierungAdd.getEqInId());
                                    eqOutAddChange = rangierungsService.findEquipment(selectedRangierungAdd.getEqOutId());
                                    break;
                                }
                            }
                        }
                    }

                    tfEqInChange.setText((eqInChange != null) ? eqInChange.getHwEQN() : null);
                    tfEqOutChange.setText((eqOutChange != null) ? eqOutChange.getVerteilerLeisteStift() : null);
                    tfEqInAddChange.setText((eqInAddChange != null) ? eqInAddChange.getHwEQN() : null);
                    tfEqOutAddChange.setText((eqOutAddChange != null) ? eqOutAddChange.getHwEQN() : null);
                }
                catch (FindException e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
    }

}


