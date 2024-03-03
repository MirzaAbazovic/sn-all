/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.03.2005 17:04:30
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.iface.IServiceCallback;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractDataPanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HVTBestellung;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.view.EquipmentBelegungView;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HVTToolService;


/**
 * Panel fuer die Darstellung der Stiftverteilung und -belegung eines UEVTs.
 *
 *
 */
public class UEVTStiftVergabePanel extends AbstractDataPanel implements IServiceCallback {

    private static final Logger LOGGER = Logger.getLogger(UEVTStiftVergabePanel.class);
    private static final long serialVersionUID = -4391277470719634474L;

    // GUI-Elemente
    private AKJTable tbEQ = null;
    private AKReflectionTableModel<EquipmentBelegungView> tbMdlEQ = null;
    private AKJButton btnFill = null;
    private AKJButton btnNew = null;

    // Modelle
    private HVTBestellung model = null;

    private AKManageableComponent[] managedComponents;

    /**
     * Default-Konstruktor.
     */
    public UEVTStiftVergabePanel() {
        super("de/augustakom/hurrican/gui/hvt/resources/UEVTStiftVergabePanel.xml");
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        tbMdlEQ = new AKReflectionTableModel<>(
                new String[] { "UEVT", "Bucht", "vorhanden", "von", "bis", "Art", "rangiert" },
                new String[] { "uevt", "leiste1", "stifteGesamt", "stiftMin", "stiftMax", "physiktyp", "stifteRangiert" },
                new Class[] { String.class, String.class, Integer.class, String.class, String.class, String.class,
                        Integer.class }
        );
        tbEQ = new AKJTable(tbMdlEQ, JTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbEQ.fitTable(new int[] { 90, 90, 70, 40, 40, 35, 55, 55 });
        tbEQ.addKeyListener(getRefreshKeyListener());
        AKJScrollPane spTable = new AKJScrollPane(tbEQ);
        spTable.setPreferredSize(new Dimension(450, 200));

        btnFill = getSwingFactory().createButton("fill.leiste", getActionListener());
        btnNew = getSwingFactory().createButton("new.leiste", getActionListener());

        AKJPanel btnPnl = new AKJPanel(new GridBagLayout());
        btnPnl.add(btnNew, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnFill, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 2, 1, 1, GridBagConstraints.VERTICAL));

        this.setLayout(new GridBagLayout());
        this.add(spTable, GBCFactory.createGBC(0, 100, 0, 0, 1, 1, GridBagConstraints.VERTICAL));
        this.add(btnPnl, GBCFactory.createGBC(0, 100, 1, 0, 1, 1, GridBagConstraints.VERTICAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        managedComponents = new AKManageableComponent[] { btnFill, btnNew };
        manageGUI(managedComponents);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#setModel(java.util.Observable)
     */
    @Override
    public void setModel(Observable model) {
        this.model = null;
        if (model instanceof HVTBestellung) {
            this.model = (HVTBestellung) model;
        }

        readModel();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#readModel()
     */
    @Override
    public void readModel() {
        tbMdlEQ.removeAll();
        if (this.model != null) {
            final SwingWorker<List<EquipmentBelegungView>, Void> worker = new SwingWorker<List<EquipmentBelegungView>, Void>() {
                final Long localUevtId = model.getUevtId();

                @Override
                protected List<EquipmentBelegungView> doInBackground() throws Exception {
                    HVTToolService hts = getCCService(HVTToolService.class);
                    return hts.findEquipmentBelegung(localUevtId);
                }

                @Override
                protected void done() {
                    try {
                        tbMdlEQ.setData(get());
                    }
                    catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                        MessageHelper.showErrorDialog(getMainFrame(), e);
                    }
                    finally {
                        GuiTools.enableComponents(new Component[] { btnFill, btnNew });
                        manageGUI(managedComponents);
                        stopProgressBar();
                        setDefaultCursor();
                    }
                }
            };
            setWaitCursor();
            showProgressBar("ermittle UEVT...");
            GuiTools.disableComponents(new Component[] { btnFill, btnNew });
            worker.execute();
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        try {
            if ("new.leiste".equals(command)) {
                String leiste = MessageHelper.showInputDialog(this,
                        "Bitte geben Sie die Bezeichnung\nfür die neue Leiste ein (2-stellig).",
                        "Neue Leiste", JOptionPane.QUESTION_MESSAGE);

                if (StringUtils.isBlank(leiste) || (leiste.length() < 2)) {
                    throw new HurricanGUIException("Bitte geben Sie eine korrekte Leistenbezeichnung ein. " +
                            "Die Bezeichnung muss zweistellig (z.B. '09' erfolgen).");
                }

                final Optional<Integer> stifte = askForAnzahlStifte();
                if (!stifte.isPresent()) {
                    return;
                }
                fillLeiste(leiste, true, stifte.get());
            }
            else if ("fill.leiste".equals(command)) {
                final EquipmentBelegungView eqView = tbMdlEQ.getDataAtRow(tbEQ.getSelectedRow());
                if (eqView == null) {
                    throw new HurricanGUIException("Bitte wählen Sie zuerst eine Leiste aus.");
                }

                fillLeiste(eqView.getLeiste1(), false, eqView.getStifteGesamt());
            }
        }
        catch (Exception e) {
            MessageHelper.showErrorDialog(this, e);
        }
    }

    private Optional<Integer> askForAnzahlStifte() {
        final String n80 = Integer.toString(Equipment.MAX_EQUIPMENT_COUNT_4_H);
        final String n100 = Integer.toString(Equipment.MAX_EQUIPMENT_COUNT_4_N);
        final String[] values = { n80, n100 };

        final Object result = MessageHelper.showInputDialog(this,
                "Wie viele Stifte sollen auf der Leiste angelegt werden?", "Anzahl der Stifte festlegen",
                JOptionPane.PLAIN_MESSAGE, null, values, n100);

        if (result instanceof String) {
            return Optional.of(Integer.valueOf((String) result));
        }
        return Optional.empty();
    }

    /*
     * Fuellt die Stifte der selektierten Leiste mit Stiften
     * aus der aktuellen HVT-Bestellung auf.
     */
    private void fillLeiste(String leiste, boolean createLeiste, int createStifte) {
        try {
            setWaitCursor();
            HVTToolService hvtToolService = getCCService(HVTToolService.class);
            HVTService hvtService = getCCService(HVTService.class);

            HVTStandort hvtStandort = hvtService.findHVTStandort4UEVT(model.getUevtId());

            String kvzNummer = null;
            if (hvtStandort.isStandortType(HVTStandort.HVT_STANDORT_TYP_KVZ)
                    || hvtStandort.isStandortType(HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ)) {
                // KVZ-Nummer muss abgefragt werden
                kvzNummer = MessageHelper.showInputDialog(this,
                        "Bitte geben Sie die KVZ-Nummer an,\nzu der die Ports geschalten sind (z.B. A031).",
                        "KVZ Nummer", JOptionPane.QUESTION_MESSAGE);
            }

            Integer uevtClusterNo = null;
            while (uevtClusterNo == null) {
                String result = MessageHelper.showInputDialog(
                        this, "Bitte geben Sie die ÜVt-Cluster-Nr an,\nzu der den ÜVt-Ports zugeordnet werden soll.\n" +
                                "Der Standard-Wert ist 1.", "1"
                );
                if (result == null) {
                    return;
                }
                else if (NumberUtils.isNumber(result) && (Integer.parseInt(result) > 0)) {
                    uevtClusterNo = Integer.valueOf(result);
                }
                else {
                    MessageHelper.showInfoDialog(this, "Bitte geben Sie ein gültige Zahl größer als 0 ein.");
                }
            }

            List<Equipment> result = hvtToolService.fillUevt(model.getId(), leiste, kvzNummer, uevtClusterNo,
                    this, createLeiste, createStifte, HurricanSystemRegistry.instance().getSessionId());

            if ((result != null) && (!result.isEmpty())) {
                MessageHelper.showMessageDialog(this, "Es wurden " + result.size() + " Stifte eingespielt.",
                        "Stife eingespielt", JOptionPane.INFORMATION_MESSAGE);

                refresh();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            setDefaultCursor();
        }
    }

    /**
     * @see de.augustakom.common.service.iface.IServiceCallback#doServiceCallback(java.lang.Object, int, java.util.Map)
     */
    @Override
    public Object doServiceCallback(Object source, int callbackAction, Map<String, ?> parameters) {
        if (callbackAction == HVTToolService.CALLBACK_CONFIRM) {
            Integer count = (Integer) parameters.get(HVTToolService.CALLBACK_PARAM_ANZAHL_STIFTE);
            Integer open = (Integer) parameters.get(HVTToolService.CALLBACK_PARAM_ANZAHL_OFFEN);
            String leiste = (String) parameters.get(HVTToolService.CALLBACK_PARAM_LEISTE);

            StringBuilder msg = new StringBuilder("Sollen ");
            msg.append(count);
            msg.append(" Stifte auf die Leiste ");
            msg.append(leiste);
            msg.append("\neingespielt werden?\n\n");
            msg.append("Es würden noch " + open + " Stifte in der\n");
            msg.append("HVT-Bestellung verbleiben.");

            int option = MessageHelper.showConfirmDialog(this, msg.toString(), "Stifte einspielen?", JOptionPane.YES_NO_OPTION);
            return (option == JOptionPane.YES_OPTION) ? Boolean.TRUE : Boolean.FALSE;
        }
        return null;
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractDataPanel#refresh()
     */
    @Override
    protected void refresh() {
        readModel();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    @Override
    public void saveModel() throws AKGUIException {
        // not used
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#getModel()
     */
    @Override
    public Object getModel() {
        return null;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#hasModelChanged()
     */
    @Override
    public boolean hasModelChanged() {
        return false;
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
        // not used
    }
}
