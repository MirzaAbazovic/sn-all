/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.04.2005 16:56:50
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJToggleButton;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.SpringLayoutUtilities;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.view.UevtCuDAView;
import de.augustakom.hurrican.service.cc.MonitorService;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * Dialog zur graphischen Anzeige einer UEVT-Belegung.
 *
 *
 */
public class UEVTBelegungGraphicalDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(UEVTBelegungGraphicalDialog.class);

    private static final String[] LEISTEN_AKOM =
            new String[] { "01", "09", "17", "02", "10", "18", "03", "11", "19", "04", "12", "20" };
    private static final String[] LEISTEN_CARRIER =
            new String[] { "05", "13", "21", "06", "14", "22", "07", "15", "23", "08", "16", "24" };

    private String uevt = null;
    private Long hvtIdStandort = null;
    private List<UEVTLeistenPanel> leistenPanels = null;
    private boolean inLoad = false;

    private List<UevtCuDAView> uevtCuDAViews = null;

    private AKJPanel leistenPanel = null;
    private AKJPanel dispPnlAKom = null;
    private AKJPanel dispPnlCarrier = null;

    /**
     * Konstruktor mit Angabe des UEVTs, dessen Belegung angezeigt werden soll.
     *
     * @param uevt
     * @param hvtIdStd
     */
    public UEVTBelegungGraphicalDialog(String uevt, Long hvtIdStd) {
        super("de/augustakom/hurrican/gui/hvt/resources/UEVTBelegungGraphicDialog.xml");
        this.uevt = uevt;
        this.hvtIdStandort = hvtIdStd;
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle("Belegung von UEVT " + uevt);
        configureButton(CMD_SAVE, "Ok", "Schliesst den Dialog", true, true);
        configureButton(CMD_CANCEL, null, null, false, false);

        ButtonGroup bg = new ButtonGroup();
        AKJToggleButton btnAKom = getSwingFactory().createToggleButton("leisten.akom", getActionListener(), false, bg);
        AKJToggleButton btnCarrier = getSwingFactory().createToggleButton("leisten.carrier", getActionListener(), true, bg);

        AKJPanel btnPnl = new AKJPanel(new GridBagLayout());
        btnPnl.add(btnAKom, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnCarrier, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        leistenPanels = new ArrayList<>();

        // Panel fuer die Carrier- und AKom-Leisten erzeugen
        LeistenPanelMouseListener panelML = new LeistenPanelMouseListener();
        dispPnlCarrier = createLeistenPanel(LEISTEN_CARRIER, panelML);
        dispPnlAKom = createLeistenPanel(LEISTEN_AKOM, panelML);

        leistenPanel = new AKJPanel(new BorderLayout());
        leistenPanel.add(dispPnlCarrier);

        AKJPanel fill = new AKJPanel();
        fill.setPreferredSize(new Dimension(5, 5));

        getChildPanel().setLayout(new GridBagLayout());
        getChildPanel().add(btnPnl, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(leistenPanel, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.NONE));
        getChildPanel().add(fill, GBCFactory.createGBC(100, 100, 1, 2, 1, 1, GridBagConstraints.BOTH));
    }

    /* Erzeugt ein Panel zur Darstellung von bestimmten UEVT-Leisten.
     * @param leisten Array mit den Leisten-Bezeichnungen, die auf dem Panel dargestellt werden sollen
     * @param panelML MouseListener fuer die Leisten-Panels.
     * @return ein Panel mit graphischer Darstellung der UEVT-Leisten.
     */
    private AKJPanel createLeistenPanel(String[] leisten, LeistenPanelMouseListener panelML) {
        AKJPanel panel = new AKJPanel(new SpringLayout());
        for (String leiste : leisten) {
            UEVTLeistenPanel lp = new UEVTLeistenPanel(leiste);
            lp.addMouseListener(panelML);

            panel.add(lp);
            leistenPanels.add(lp);
        }
        SpringLayoutUtilities.makeGrid(panel, leisten.length / 3, 3, 5, 5, 20, 10);
        return panel;
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#validateSaveButton()
     */
    @Override
    protected void validateSaveButton() {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        inLoad = true;
        setWaitCursor();

        final SwingWorker<List<UevtCuDAView>, Void> worker = new SwingWorker<List<UevtCuDAView>, Void>() {
            final String uevtInput = uevt;
            final Long hvtIdStandortInput = hvtIdStandort;

            @Override
            protected List<UevtCuDAView> doInBackground() throws Exception {
                MonitorService ms = getCCService(MonitorService.class);
                return ms.findViews4UevtBelegung(uevtInput, hvtIdStandortInput);
            }

            @Override
            protected void done() {
                try {
                    uevtCuDAViews = get();
                    if (uevtCuDAViews != null) {
                        showValues();
                    }
                    else {
                        throw new HurricanGUIException("UEVT-Daten konnten nicht ermittelt werden!");
                    }
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
                finally {
                    setDefaultCursor();
                    inLoad = false;
                }
            }

        };
        worker.execute();
    }

    /*
     * Zeigt die Werte in den Leisten-Panels an.
     */
    private void showValues() {
        try {
            RangierungsService rs = getCCService(RangierungsService.class);

            // Stift-Anzahl (insgesamt/belegt) der UEVT-Leisten ermitteln
            // und dem entspr. Panel uebergeben.
            UevtLeistePredicate predicate = new UevtLeistePredicate();
            for (UEVTLeistenPanel lp : leistenPanels) {
                predicate.setLeiste(lp.getLeiste());

                try {
                    Map<String, Integer> eqCounts = rs.getEquipmentCount(hvtIdStandort, uevt, lp.getLeiste());
                    if (eqCounts != null) {
                        lp.setCuDAs(eqCounts);

                        @SuppressWarnings("unchecked")
                        Collection<UevtCuDAView> ucViews = CollectionUtils.select(uevtCuDAViews, predicate);
                        Integer belegt = null;
                        for (UevtCuDAView ucv : ucViews) {
                            belegt = NumberTools.add(belegt, ucv.getCudaBelegt());
                        }
                        lp.setBelegt(belegt);

                        lp.validateBackground();
                        lp.validateTFBackground();
                    }
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        prepare4Close();
        setValue(OK_OPTION);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        // Umschalten zwischen Darstellung von AKom- und Carrier-Leisten
        if ("leisten.akom".equals(command)) {
            showLeistenPanel(dispPnlAKom);
        }
        else if ("leisten.carrier".equals(command)) {
            showLeistenPanel(dispPnlCarrier);
        }
    }

    /* Zeigt das Panel <code>panel2Show</code> an. */
    private void showLeistenPanel(AKJPanel panel2Show) {
        leistenPanel.removeAll();
        leistenPanel.add(panel2Show);
        leistenPanel.repaint();
        leistenPanel.revalidate();
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    /*
     * Predicate, um Objekte des Typs <code>UevtCuDAView</code> auf eine
     * Leiste zu filtern.
     */
    static class UevtLeistePredicate implements Predicate {
        private String leiste = null;

        /**
         * Setzt die Bezeichnung der Leiste, auf die das Predicate filtern soll.
         *
         * @param leiste
         */
        void setLeiste(String leiste) {
            this.leiste = leiste;
        }

        /**
         * @see org.apache.commons.collections.Predicate#evaluate(java.lang.Object)
         */
        @Override
        public boolean evaluate(Object obj) {
            if (obj instanceof UevtCuDAView) {
                UevtCuDAView view = (UevtCuDAView) obj;
                if (StringUtils.equals(leiste, view.getRangLeiste1())) {
                    return true;
                }
                else {
                    if ((view.getRangLeiste1() != null) && (view.getRangLeiste1().length() == 1)) {
                        return StringUtils.equals(leiste, "0" + view.getRangLeiste1());
                    }
                }
            }
            return false;
        }
    }

    /* MouseListener fuer die Leisten-Panels. */
    private class LeistenPanelMouseListener extends MouseAdapter {
        /**
         * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            if ((e.getClickCount() >= 2) && (e.getSource() instanceof UEVTLeistenPanel) && !inLoad) {
                UEVTLeistenPanel lp = (UEVTLeistenPanel) e.getSource();
                if (StringUtils.contains(lp.getCuDAPhysik(), "H")) {
                    UEVTLeistenDefinitionsDialog dlg = new UEVTLeistenDefinitionsDialog(
                            hvtIdStandort, uevt, lp.getLeiste());
                    DialogHelper.showDialog(getMainFrame(), dlg, true, true);
                }
            }
        }
    }
}


