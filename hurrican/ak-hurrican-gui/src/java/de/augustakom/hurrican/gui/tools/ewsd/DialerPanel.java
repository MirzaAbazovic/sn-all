/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.03.2006 17:02:39
 */
package de.augustakom.hurrican.gui.tools.ewsd;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableOwner;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.gui.utils.ObserverHelper;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractDataPanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.Dialer;
import de.augustakom.hurrican.service.cc.EWSDService;


/**
 * Panel fuer die Darstellung der Dialer-Kennzahlen.
 *
 *
 */
public class DialerPanel extends AbstractDataPanel implements AKDataLoaderComponent, AKTableOwner {

    private static final Logger LOGGER = Logger.getLogger(DialerPanel.class);

    // GUI-Komponenten
    private AKJTextField tfKennzahl = null;
    private AKJTextArea taDesc = null;
    private AKJComboBox cbTyp = null;
    private AKJDateComponent dcGueltigVon = null;
    private AKJDateComponent dcGueltigBis = null;
    private AKJDateComponent dcGesperrtAb = null;
    private AKJDateComponent dcGesperrtBis = null;
    private AKJButton btnNew = null;
    private AKJButton btnSave = null;
    private AKJTable tbDialer = null;
    private AKReflectionTableModel tbMdlDialer = null;

    // Modelle
    private Dialer actModel = null;

    private AKManageableComponent[] managedComponents;

    /**
     * Default-Const.
     */
    public DialerPanel() {
        super("de/augustakom/hurrican/gui/tools/ewsd/resources/DialerPanel.xml");
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblKennzahl = getSwingFactory().createLabel("kennzahl");
        AKJLabel lblDesc = getSwingFactory().createLabel("description");
        AKJLabel lblTyp = getSwingFactory().createLabel("typ");
        AKJLabel lblGueltigVon = getSwingFactory().createLabel("gueltig.von");
        AKJLabel lblGueltigBis = getSwingFactory().createLabel("gueltig.bis");
        AKJLabel lblGesperrtAb = getSwingFactory().createLabel("gesperrt.ab");
        AKJLabel lblGesperrtBis = getSwingFactory().createLabel("gesperrt.bis");
        btnNew = getSwingFactory().createButton("new", getActionListener(), null);
        btnSave = getSwingFactory().createButton("save", getActionListener(), null);

        tfKennzahl = getSwingFactory().createTextField("kennzahl");
        taDesc = getSwingFactory().createTextArea("description", true, true, true);
        AKJScrollPane spDesc = new AKJScrollPane(taDesc, new Dimension(150, 40));
        cbTyp = getSwingFactory().createComboBox("typ");
        dcGueltigVon = getSwingFactory().createDateComponent("gueltig.von");
        dcGueltigBis = getSwingFactory().createDateComponent("gueltig.bis");
        dcGesperrtAb = getSwingFactory().createDateComponent("gesperrt.ab");
        dcGesperrtBis = getSwingFactory().createDateComponent("gesperrt.bis");

        tbMdlDialer = new AKReflectionTableModel<Dialer>(
                new String[] { "Kennzahl", "Beschreibung", "Typ", "Gültig von", "User gültig von", "Gültig bis",
                        "User gültig bis", "gesperrt ab", "User gesp. ab", "gesperrt bis", "User gesp. bis" },
                new String[] { "kennzahl", "beschreibung", "typ", "gueltigVon", "userGueltigVon", "gueltigBis",
                        "userGueltigBis", "gesperrtAb", "userGesperrtAb", "gesperrtBis", "userGesperrtBis" },
                new Class[] { String.class, String.class, String.class, Date.class, String.class, Date.class,
                        String.class, Date.class, String.class, Date.class, String.class }
        );
        tbDialer = new AKJTable(tbMdlDialer, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbDialer.attachSorter();
        tbDialer.addKeyListener(getRefreshKeyListener());
        tbDialer.addTableListener(this);
        AKJScrollPane spTable = new AKJScrollPane(tbDialer, new Dimension(400, 200));

        AKJPanel btnPnl = new AKJPanel(new GridBagLayout());
        btnPnl.add(btnNew, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnSave, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel left = new AKJPanel(new GridBagLayout());
        left.add(btnPnl, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblKennzahl, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfKennzahl, GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblDesc, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.VERTICAL));
        left.add(spDesc, GBCFactory.createGBC(0, 0, 2, 2, 1, 2, GridBagConstraints.BOTH));
        left.add(lblTyp, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(cbTyp, GBCFactory.createGBC(0, 0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel right = new AKJPanel(new GridBagLayout());
        right.add(lblGueltigVon, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        right.add(dcGueltigVon, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblGueltigBis, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(dcGueltigBis, GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblGesperrtAb, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(dcGesperrtAb, GBCFactory.createGBC(0, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblGesperrtBis, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(dcGesperrtBis, GBCFactory.createGBC(0, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel top = new AKJPanel(new GridBagLayout(), "Dialer");
        top.add(left, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.VERTICAL));
        top.add(right, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.VERTICAL, new Insets(2, 25, 2, 2)));
        top.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 2, 1, 1, 1, GridBagConstraints.BOTH));

        this.setLayout(new BorderLayout());
        this.add(top, BorderLayout.NORTH);
        this.add(spTable, BorderLayout.CENTER);
        enableFields(false);
        managedComponents = new AKManageableComponent[] { btnNew, btnSave };
        manageGUI(managedComponents);
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractDataPanel#refresh()
     */
    @Override
    protected void refresh() {
        this.actModel = null;
        showDetails(actModel);
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        final SwingWorker<List<Dialer>, Void> worker = new SwingWorker<List<Dialer>, Void>() {
            @Override
            public List<Dialer> doInBackground() throws Exception {
                EWSDService es = getCCService(EWSDService.class);
                return es.findDialer();
            }

            @Override
            protected void done() {
                try {
                    List<Dialer> dialer = get();
                    tbMdlDialer.setData(dialer);
                    ObserverHelper.addObserver2Objects(DialerPanel.this, dialer.toArray(new Observable[dialer.size()]));
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
                finally {
                    GuiTools.unlockComponents(new Component[] { btnNew, btnSave });
                    manageGUI(managedComponents);
                    stopProgressBar();
                    setDefaultCursor();
                }
            }
        };

        setWaitCursor();
        showProgressBar("Dailer laden...");
        GuiTools.lockComponents(new Component[] { btnNew, btnSave });
        worker.execute();
    }

    /**
     * @see de.augustakom.common.gui.swing.table.AKTableOwner#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {
        actModel = null;
        GuiTools.cleanFields(this);
        if (details instanceof Dialer) {
            enableFields(true);
            this.actModel = (Dialer) details;

            tfKennzahl.setText(actModel.getKennzahl());
            taDesc.setText(actModel.getBeschreibung());
            cbTyp.selectItem("toString", String.class, actModel.getTyp());
            dcGueltigVon.setDate(actModel.getGueltigVon());
            dcGueltigBis.setDate(actModel.getGueltigBis());
            dcGesperrtAb.setDate(actModel.getGesperrtAb());
            dcGesperrtBis.setDate(actModel.getGesperrtBis());
        }
        else {
            enableFields(false);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("new".equals(command)) {
            showDetails(new Dialer());
        }
        else if ("save".equals(command)) {
            saveModel();
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    @Override
    public void saveModel() {
        try {
            setWaitCursor();
            if (actModel == null) {
                throw new HurricanGUIException("Bitte waehlen Sie zuerst ein Dialer-Objekt aus.");
            }
            else {
                EWSDService es = getCCService(EWSDService.class);

                String user = HurricanSystemRegistry.instance().getCurrentUserName();
                actModel.setKennzahl(tfKennzahl.getText());
                actModel.setBeschreibung(taDesc.getText());
                actModel.setTyp((String) cbTyp.getSelectedItemValue("toString", String.class));
                actModel.setGueltigVon(dcGueltigVon.getDate(null), user);
                actModel.setGueltigBis(dcGueltigBis.getDate(null), user);
                actModel.setGesperrtAb(dcGesperrtAb.getDate(null), user);
                actModel.setGesperrtBis(dcGesperrtBis.getDate(null), user);
                boolean addToTable = actModel.getId() == null;
                es.saveDialer(actModel);

                if (addToTable) {
                    tbMdlDialer.addObject(actModel);
                }

                actModel.notifyObservers(true);
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

    /*
     * Schaltet alle Fields auf enabled/disabled.
     */
    private void enableFields(boolean enable) {
        Component[] fields = new Component[] { tfKennzahl, taDesc, cbTyp, dcGueltigVon,
                dcGueltigBis, dcGesperrtAb, dcGesperrtBis };
        GuiTools.enableComponents(fields, enable, true);
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
        tbDialer.repaint();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#readModel()
     */
    @Override
    public void readModel() throws AKGUIException {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#setModel(java.util.Observable)
     */
    @Override
    public void setModel(Observable model) throws AKGUIException {
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

}


