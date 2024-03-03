/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2005 08:50:48
 */
package de.augustakom.hurrican.gui.kubena;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJRadioButton;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.kubena.Kubena;
import de.augustakom.hurrican.service.cc.KubenaService;


/**
 * Panel zur Definition (bzw. Auswahl) einer Kubena.
 *
 *
 */
public class KubenaDefinitionPanel extends AbstractServicePanel implements AKDataLoaderComponent, ItemListener {

    private static final Logger LOGGER = Logger.getLogger(KubenaDefinitionPanel.class);

    private static final String KRITIERIUM_HVT_PROD = "kritierium.hvt.prod";
    private static final String KRITIERIUM_HVT = "kritierium.hvt";
    private static final String KRITIERIUM_VBZ = "kritierium.vbz";
    private static final String KRITERIUM = "kriterium";
    private static final String SZ_BIS = "sz.bis";
    private static final String SZ_VON = "sz.von";
    private static final String NAME2 = "name";
    private static final String KUBENA_DATUM = "kubena.datum";
    private static final String KUBENA = "kubena";
    private static final String EXECUTE = "execute";
    private static final String SAVE_KUBENA = "save.kubena";
    private static final String NEW_KUBENA = "new.kubena";

    // GUI-Komponenten
    private AKJComboBox cbKubena = null;
    private AKJDateComponent dcDatum = null;
    private AKJTextField tfName = null;
    private AKJDateComponent dcSzVon = null;
    private AKJDateComponent dcSzBis = null;
    private AKJRadioButton rbKritVbz = null;
    private AKJRadioButton rbKritHVT = null;
    private AKJRadioButton rbKritHVTProd = null;
    private AKJButton btnNew = null;
    private AKJButton btnSave = null;
    private AKJButton btnExecute = null;
    private AKJPanel subPanel = null;

    AbstractServicePanel detailPanel = null;

    // Modelle
    private Kubena actKubena = null;

    private AKManageableComponent[] managedComponents;

    /**
     * Default-Konstruktor fuer das Panel.
     */
    public KubenaDefinitionPanel() {
        super("de/augustakom/hurrican/gui/kubena/resources/KubenaDefinitionPanel.xml");
        createGUI();
        loadData();
    }

    @Override
    protected final void createGUI() {
        AKJLabel lblKubena = getSwingFactory().createLabel(KUBENA);
        AKJLabel lblDatum = getSwingFactory().createLabel(KUBENA_DATUM);
        AKJLabel lblName = getSwingFactory().createLabel(NAME2);
        AKJLabel lblSzVon = getSwingFactory().createLabel(SZ_VON);
        AKJLabel lblSzBis = getSwingFactory().createLabel(SZ_BIS);
        AKJLabel lblKriterium = getSwingFactory().createLabel(KRITERIUM);

        cbKubena = getSwingFactory().createComboBox(KUBENA,
                new AKCustomListCellRenderer<>(Kubena.class, Kubena::getDisplayName));
        cbKubena.addItemListener(this);
        cbKubena.setPreferredSize(new Dimension(200, 21));
        dcDatum = getSwingFactory().createDateComponent(KUBENA_DATUM, false);
        tfName = getSwingFactory().createTextField(NAME2);
        dcSzVon = getSwingFactory().createDateComponent(SZ_VON);
        dcSzBis = getSwingFactory().createDateComponent(SZ_BIS);
        ButtonGroup bg = new ButtonGroup();
        rbKritVbz = getSwingFactory().createRadioButton(KRITIERIUM_VBZ, getActionListener(), false, bg);
        rbKritHVT = getSwingFactory().createRadioButton(KRITIERIUM_HVT, getActionListener(), false, bg);
        rbKritHVTProd = getSwingFactory().createRadioButton(KRITIERIUM_HVT_PROD, getActionListener(), false, bg);
        btnNew = getSwingFactory().createButton(NEW_KUBENA, getActionListener(), null);
        btnSave = getSwingFactory().createButton(SAVE_KUBENA, getActionListener(), null);
        btnExecute = getSwingFactory().createButton(EXECUTE, getActionListener());
        btnExecute.setEnabled(false);

        // @formatter:off
        AKJPanel left = new AKJPanel(new GridBagLayout());
        left.add(btnNew         , GBCFactory.createGBC(  0,  0, 2, 0, 1, 1, GridBagConstraints.NONE));
        left.add(btnSave        , GBCFactory.createGBC(  0,  0, 3, 0, 1, 1, GridBagConstraints.NONE));
        left.add(new AKJPanel() , GBCFactory.createGBC(100,  0, 4, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblKubena      , GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel() , GBCFactory.createGBC(  0,  0, 1, 1, 1, 1, GridBagConstraints.NONE));
        left.add(cbKubena       , GBCFactory.createGBC(100,  0, 2, 1, 3, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblDatum       , GBCFactory.createGBC(  0,  0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(dcDatum        , GBCFactory.createGBC(100,  0, 2, 2, 3, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblName        , GBCFactory.createGBC(  0,  0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfName         , GBCFactory.createGBC(100,  0, 2, 3, 3, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblSzVon       , GBCFactory.createGBC(  0,  0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(dcSzVon        , GBCFactory.createGBC(100,  0, 2, 4, 3, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblSzBis       , GBCFactory.createGBC(  0,  0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(dcSzBis        , GBCFactory.createGBC(100,  0, 2, 5, 3, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel right = new AKJPanel(new GridBagLayout());
        right.add(new AKJPanel(), GBCFactory.createGBC(  0,100, 0, 0, 1, 1, GridBagConstraints.VERTICAL));
        right.add(lblKriterium  , GBCFactory.createGBC(100,  0, 0, 1, 2, 1, GridBagConstraints.HORIZONTAL));
        right.add(new AKJPanel(), GBCFactory.createGBC(  0,  0, 0, 2, 1, 1, GridBagConstraints.NONE));
        right.add(rbKritVbz     , GBCFactory.createGBC(  0,  0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(rbKritHVT     , GBCFactory.createGBC(  0,  0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(rbKritHVTProd , GBCFactory.createGBC(  0,  0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(new AKJPanel(), GBCFactory.createGBC(  0,100, 1, 5, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel north = new AKJPanel(new GridBagLayout());
        north.setBorder(BorderFactory.createTitledBorder("Auswahlkriterien"));
        north.add(left          , GBCFactory.createGBC(  0,100, 0, 0, 1, 2, GridBagConstraints.VERTICAL));
        north.add(new AKJPanel(), GBCFactory.createGBC(  0,  0, 1, 0, 1, 1, GridBagConstraints.NONE, new Insets(2,10,2,10)));
        north.add(right         , GBCFactory.createGBC(  0,100, 2, 0, 1, 2, GridBagConstraints.VERTICAL));

        subPanel = new AKJPanel(new BorderLayout());

        AKJPanel btnPnl = new AKJPanel(new GridBagLayout());
        btnPnl.add(new AKJPanel(),GBCFactory.createGBC(  0,100, 0, 0, 1, 1, GridBagConstraints.VERTICAL));
        btnPnl.add(btnExecute   , GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.NONE));

        this.setLayout(new GridBagLayout());
        this.add(north          , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        this.add(btnPnl         , GBCFactory.createGBC(  0,  0, 1, 0, 1, 1, GridBagConstraints.VERTICAL));
        this.add(subPanel       , GBCFactory.createGBC(100,100, 0, 1, 3, 1, GridBagConstraints.BOTH));
        // @formatter:on

        enableFields(false);
        managedComponents = new AKManageableComponent[] { btnSave, btnNew };
        manageGUI(managedComponents);
    }

    @Override
    public final void loadData() {
        clearFields(true);
        final SwingWorker<List<Kubena>, Void> worker = new SwingWorker<List<Kubena>, Void>() {
            @Override
            protected List<Kubena> doInBackground() throws Exception {
                KubenaService ks = getCCService(KubenaService.class);
                return ks.findKubenas();
            }

            @Override
            protected void done() {
                try {
                    cbKubena.addItems(get(), true, Kubena.class);
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
                finally {
                    enableGuiComponents(true);
                    setDefaultCursor();
                }
            }
        };
        setWaitCursor();
        enableGuiComponents(false);
        worker.execute();
    }

    @Override
    protected void execute(String command) {
        if (NEW_KUBENA.equals(command)) {
            createNewKubena();
        }
        else if (SAVE_KUBENA.equals(command)) {
            saveKubena();
        }
        else if (EXECUTE.equals(command)) {
            executeKubena();
        }
    }

    /**
     * Fuehrt die aktuelle Kubena aus. <br> Das erzeugte Excel-File wird sofort geoeffnet.
     */
    private void executeKubena() {
        if (actKubena == null) {
            MessageHelper.showErrorDialog(getMainFrame(), new HurricanGUIException("Bitte wählen Sie zuerst eine Kubena aus."));
        }
        else {

            final SwingWorker<File, Void> worker = new SwingWorker<File, Void>() {

                final Long kubenaId = actKubena.getId();

                @Override
                protected File doInBackground() throws Exception {
                    KubenaService ks = getCCService(KubenaService.class);
                    return ks.createKubena(kubenaId);
                }

                @Override
                protected void done() {
                    try {
                        File kubena = get();
                        if (kubena != null) {
                            Desktop.getDesktop().open(kubena);
                        }
                        else {
                            throw new HurricanGUIException("Kubena wurde aus unbekanntem Grund nicht erstellt.");
                        }
                    }
                    catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                        MessageHelper.showErrorDialog(getMainFrame(), e);
                    }
                    finally {
                        enableGuiComponents(true);
                        detailPanel.setVisible(true);
                        stopProgressBar();
                        setDefaultCursor();
                    }
                }
            };
            setWaitCursor();
            enableGuiComponents(false);
            detailPanel.setVisible(false);
            showProgressBar("Kubena erstellen...");
            worker.execute();
        }
    }

    /* Erstellt eine neue Kubena. */
    private void createNewKubena() {
        clearFields(true);
        enableFields(true);
        btnExecute.setEnabled(false);

        actKubena = new Kubena();
    }

    /* Speichert die aktuelle Kubena ab. */
    private void saveKubena() {
        if (actKubena == null) {
            MessageHelper.showConfirmDialog(this, "Sie haben keine Kubena gewählt!",
                    "Keine Kubena!", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            boolean add2CB = (actKubena.getId() == null) ? true : false;

            actKubena.setDatum(dcDatum.getDate(new Date()));
            actKubena.setName(tfName.getText(null));
            actKubena.setSchaltzeitVon(dcSzVon.getDate(null));
            actKubena.setSchaltzeitBis(dcSzBis.getDate(null));
            Short kriterium = (rbKritVbz.isSelected()) ? Kubena.KRITERIUM_VBZ :
                    (rbKritHVT.isSelected()) ? Kubena.KRITERIUM_HVT :
                            (rbKritHVTProd.isSelected()) ? Kubena.KRITERIUM_HVT_PROD : null;
            actKubena.setKriterium(kriterium);

            KubenaService ks = getCCService(KubenaService.class);
            ks.saveKubena(actKubena);

            if (add2CB) {
                cbKubena.addItem(actKubena);
            }

            showKubena();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    /* Zeigt die aktuelle Kubena an. */
    private void showKubena() {
        clearFields(false);
        if (actKubena != null) {
            dcDatum.setDate(actKubena.getDatum());
            tfName.setText(actKubena.getName());
            dcSzVon.setDate(actKubena.getSchaltzeitVon());
            dcSzBis.setDate(actKubena.getSchaltzeitBis());

            if (NumberTools.equal(actKubena.getKriterium(), Kubena.KRITERIUM_VBZ)) {
                rbKritVbz.setSelected(true);
                detailPanel = new KubenaVbzSelectionPanel(actKubena);
            }
            else if (NumberTools.equal(actKubena.getKriterium(), Kubena.KRITERIUM_HVT)) {
                rbKritHVT.setSelected(true);
                detailPanel = new KubenaHVTSelectionPanel(actKubena);
            }
            else if (NumberTools.equal(actKubena.getKriterium(), Kubena.KRITERIUM_HVT_PROD)) {
                rbKritHVTProd.setSelected(true);
                detailPanel = new KubenaHVTSelectionPanel(actKubena);
            }

            subPanel.removeAll();
            if (detailPanel != null) {
                subPanel.add(detailPanel, BorderLayout.CENTER);
            }
            subPanel.revalidate();
            btnExecute.setEnabled(true);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    /* Schaltet die Felder enabled/disabled */
    private void enableFields(boolean enable) {
        tfName.setEnabled(enable);
        rbKritVbz.setEnabled(enable);
        rbKritHVT.setEnabled(enable);
        rbKritHVTProd.setEnabled(enable);
    }

    /* 'Loescht' den Inhalt aller Felder */
    private void clearFields(boolean cleanCombo) {
        if (cleanCombo) {
            cbKubena.setSelectedIndex(-1);
        }

        dcDatum.setDate(new Date());
        tfName.setText("");
        dcSzVon.setDate(null);
        dcSzBis.setDate(null);
        rbKritVbz.setSelected(false);
        rbKritHVT.setSelected(false);
        rbKritHVTProd.setSelected(false);

        subPanel.removeAll();
        subPanel.revalidate();
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if ((e.getSource() == cbKubena) && (e.getStateChange() == ItemEvent.SELECTED)) {
            Kubena kubena = (Kubena) cbKubena.getSelectedItem();
            if ((kubena != null) && (kubena.getId() != null)) {
                actKubena = kubena;
            }
            else {
                actKubena = null;
            }

            showKubena();
        }
    }

    private void enableGuiComponents(boolean enable) {
        btnNew.setEnabled(enable);
        btnSave.setEnabled(enable);
        btnExecute.setEnabled(enable);
        if (enable) {
            manageGUI(managedComponents);
        }
    }
}


