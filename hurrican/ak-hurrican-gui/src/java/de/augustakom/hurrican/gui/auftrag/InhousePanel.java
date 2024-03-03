/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.08.2004 09:22:40
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.auftrag.shared.InhouseTableModel;
import de.augustakom.hurrican.gui.base.AbstractDataPanel;
import de.augustakom.hurrican.gui.base.IAuftragStatusValidator;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Inhouse;
import de.augustakom.hurrican.service.cc.PhysikService;


/**
 * Panel fuer die Darstellung der Inhouse-Daten einer Endstelle.
 *
 *
 */
public class InhousePanel extends AbstractDataPanel implements IAuftragStatusValidator,
        AKObjectSelectionListener {

    private static final Logger LOGGER = Logger.getLogger(InhousePanel.class);

    /* Key, um ein Inhouse-Objekt der Ueberwachung hinzuzufuegen. */
    private static final String WATCH_INHOUSE = "watch.inhouse";

    private AKJButton btnNew = null;
    private AKJButton btnEdit = null;
    private AKJTextField tfRaumnummer = null;
    private AKJTextField tfVerkabelung = null;
    private AKJTextField tfAnsprech = null;
    private AKJTextArea taBemerkung = null;
    private InhouseTableModel tbMdlInhouse = null;

    private Endstelle endstelle = null;
    private Inhouse actInhouse = null;

    /**
     * Konstruktor.
     */
    public InhousePanel() {
        super("de/augustakom/hurrican/gui/auftrag/resources/InhousePanel.xml");
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblRaumnummer = getSwingFactory().createLabel("raumnummer");
        AKJLabel lblVerkabelung = getSwingFactory().createLabel("verkabelung");
        AKJLabel lblAnsprech = getSwingFactory().createLabel("ansprechpartner");
        AKJLabel lblBemerkung = getSwingFactory().createLabel("bemerkung");

        btnNew = getSwingFactory().createButton("new.inhouse", getActionListener(), null);
        btnEdit = getSwingFactory().createButton("edit.inhouse", getActionListener(), null);
        tfRaumnummer = getSwingFactory().createTextField("raumnummer");
        tfVerkabelung = getSwingFactory().createTextField("verkabelung");
        tfAnsprech = getSwingFactory().createTextField("ansprechpartner");
        taBemerkung = getSwingFactory().createTextArea("bemerkung");
        AKJScrollPane spBemerkung = new AKJScrollPane(taBemerkung);
        spBemerkung.setPreferredSize(new Dimension(190, 60));

        Insets topInsets = new Insets(0, 2, 2, 2);
        AKJPanel btnPnl = new AKJPanel(new GridBagLayout());
        btnPnl.add(btnNew, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE, topInsets));
        btnPnl.add(btnEdit, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.NONE, topInsets));
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 2, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel left = new AKJPanel(new GridBagLayout());
        left.add(lblRaumnummer, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL, topInsets));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE, topInsets));
        left.add(tfRaumnummer, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL, topInsets));
        left.add(lblVerkabelung, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfVerkabelung, GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblAnsprech, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfAnsprech, GBCFactory.createGBC(100, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel right = new AKJPanel(new GridBagLayout());
        right.add(lblBemerkung, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2)));
        right.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE, topInsets));
        right.add(spBemerkung, GBCFactory.createGBC(100, 100, 2, 0, 1, 2, GridBagConstraints.BOTH, topInsets));

        AKJPanel middle = new AKJPanel();
        middle.setPreferredSize(new Dimension(40, 1));

        // @formatter:off
        AKJPanel details = new AKJPanel(new GridBagLayout());
        Insets ti = new Insets(0, 2, 0, 2);
        details.setBorder(BorderFactory.createTitledBorder("aktuelle Inhouse-Daten"));
        details.add(btnPnl          , GBCFactory.createGBC(  0, 0, 0, 0, 1, 1, GridBagConstraints.VERTICAL, ti));
        details.add(left            , GBCFactory.createGBC(  0, 0, 1, 0, 1, 1, GridBagConstraints.VERTICAL, ti));
        details.add(middle          , GBCFactory.createGBC(  0, 0, 2, 0, 1, 1, GridBagConstraints.NONE, ti));
        details.add(right           , GBCFactory.createGBC(  0, 0, 3, 0, 1, 1, GridBagConstraints.VERTICAL, ti));
        details.add(new AKJPanel()  , GBCFactory.createGBC(100, 0, 4, 0, 1, 1, GridBagConstraints.HORIZONTAL, ti));
        // @formatter:on

        tbMdlInhouse = new InhouseTableModel();
        AKJTable tbInhouse = new AKJTable(tbMdlInhouse, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tbInhouse.fitTable(new int[] { 190, 200, 190, 60, 60 });
        tbInhouse.addMouseListener(new AKTableDoubleClickMouseListener(this));
        AKJScrollPane spTable = new AKJScrollPane(tbInhouse);
        spTable.setPreferredSize(new Dimension(700, 90));

        AKJPanel tablePanel = new AKJPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("historisierte Inhouse-Daten"));
        tablePanel.add(spTable, BorderLayout.CENTER);

        // @formatter:off
        this.setLayout(new GridBagLayout());
        this.add(details    , GBCFactory.createGBC(100,  0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(0, 2, 1, 2)));
        this.add(tablePanel , GBCFactory.createGBC(100,100, 0, 1, 1, 1, GridBagConstraints.BOTH, new Insets(2, 2, 0, 2)));
        // @formatter:on

        GuiTools.lockComponents(new Component[] { tfAnsprech, tfRaumnummer, tfVerkabelung, taBemerkung });
        manageGUI(btnNew, btnEdit);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#setModel(java.util.Observable)
     */
    @Override
    public void setModel(Observable model) {
        if (model instanceof Endstelle) {
            this.endstelle = (Endstelle) model;
        }
        else {
            this.endstelle = null;
        }
        readModel();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#readModel()
     */
    @Override
    public void readModel() {
        try {
            setWaitCursor();
            clear();

            if (endstelle != null) {
                PhysikService ps = getCCService(PhysikService.class);

                // Alle Inhouse-Daten der Endstelle laden
                List inhouses = ps.findInhouses4ES(endstelle.getId());
                tbMdlInhouse.setData(inhouses);

                // Aktuelle Inhouse-Daten der Endstelle laden
                actInhouse = ps.findInhouse4ES(endstelle.getId());
                if (actInhouse == null) {
                    actInhouse = new Inhouse();
                    actInhouse.setEndstelleId(endstelle.getId());
                }
                addObjectToWatch(WATCH_INHOUSE, actInhouse);
                showValues(actInhouse);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
        finally {
            setDefaultCursor();
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    @Override
    public void saveModel() throws AKGUIException {
        // nothing to do...
        // Inhouse-Daten werden ueber den Dialog <code>InhouseDialog</code> gespeichert.
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

    /* Zeigt die Daten des Objekts <code>inhouse</code> an. */
    private void showValues(Inhouse inhouse) {
        if (inhouse != null) {
            tfRaumnummer.setText(inhouse.getRaumnummer());
            tfVerkabelung.setText(inhouse.getVerkabelung());
            tfAnsprech.setText(inhouse.getAnsprechpartner());
            taBemerkung.setText(inhouse.getBemerkung());
        }
        validateButtons();
    }

    /* Validiert die beiden Buttons 'new' und 'edit'. */
    private void validateButtons() {
        if ((actInhouse == null) || (actInhouse.getId() == null)) {
            btnEdit.setVisible(false);
            btnNew.setVisible(true);
        }
        else {
            btnEdit.setVisible(true);
            btnNew.setVisible(false);
        }
    }

    /* 'Loescht' alle Feldinhalte */
    private void clear() {
        tfRaumnummer.setText("");
        tfVerkabelung.setText("");
        tfAnsprech.setText("");
        taBemerkung.setText("");
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("new.inhouse".equals(command) || "edit.inhouse".equals(command)) {
            InhouseDialog dlg = new InhouseDialog(actInhouse, true);
            Object value = DialogHelper.showDialog(this, dlg, true, true);
            if (value instanceof Inhouse) {
                readModel();
            }
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKObjectSelectionListener#objectSelected(java.lang.Object)
     */
    @Override
    public void objectSelected(Object selection) {
        if (selection instanceof Inhouse) {
            InhouseDialog dlg = new InhouseDialog((Inhouse) selection, false);
            DialogHelper.showDialog(this, dlg, true, true);
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    /**
     * @see de.augustakom.hurrican.gui.base.IAuftragStatusValidator#validate4Status(java.lang.Integer)
     */
    @Override
    public void validate4Status(Long auftragStatus) {
        if (NumberTools.isIn(auftragStatus, new Long[] { AuftragStatus.ABSAGE, AuftragStatus.AUFTRAG_GEKUENDIGT })) {
            GuiTools.lockComponents(new Component[] { btnNew, btnEdit });
        }
        else {
            GuiTools.unlockComponents(new Component[] { btnNew, btnEdit });
        }
    }
}


