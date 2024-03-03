/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.10.2008 11:40:50
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.iface.AKSearchComponent;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.AKSearchKeyListener;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReferenceAwareTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.service.iface.ISimpleFindService;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.query.HVTQuery;
import de.augustakom.hurrican.model.cc.view.HVTGruppeStdView;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.QueryCCService;


/**
 * Dialog zur Suche und Selektion eines HVT-Standorts. <br> Der ausgewaehlte HVT-Standort wird ueber die Methode
 * setValue(..) gesetzt und kann vom Caller per getValue() abgefragt werden.
 *
 *
 */
public class HVTSelectionDialog extends AbstractServiceOptionDialog implements AKSearchComponent,
        AKObjectSelectionListener {

    private static final Logger LOGGER = Logger.getLogger(HVTSelectionDialog.class);
    private static final long serialVersionUID = 3690925298376364613L;

    // GUI-Komponenten
    private AKJTextField tfONKZ = null;
    private AKJFormattedTextField tfASB = null;
    private AKJTextField tfName = null;
    private AKJTextField tfOrt = null;
    private AKReferenceField rfHVTType = null;
    private AKReferenceField rfNiederlassung = null;
    private AKReferenceAwareTableModel<HVTGruppeStdView> tbMdlHVT = null;
    private AKJTable tbHVT = null;
    private AKJLabel lbFeedback = null;
    private AKJPanel btnPnl = null;


    // sonstiges
    private boolean initialized = false;

    /**
     * Konstruktor fuer den Dialog.
     */
    public HVTSelectionDialog() {
        super("de/augustakom/hurrican/gui/hvt/resources/HVTSelectionDialog.xml");
        createGUI();
        init();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));

        AKSearchKeyListener searchKL = new AKSearchKeyListener(this, new int[] { KeyEvent.VK_ENTER });
        AKJLabel lblONKZ = getSwingFactory().createLabel("onkz");
        AKJLabel lblASB = getSwingFactory().createLabel("asb");
        AKJLabel lblName = getSwingFactory().createLabel("name");
        AKJLabel lblOrt = getSwingFactory().createLabel("ort");
        AKJLabel lblHVTType = getSwingFactory().createLabel("hvt.typ");
        AKJLabel lblNiederlassung = getSwingFactory().createLabel("niederlassung");
        lbFeedback = new AKJLabel();

        tfONKZ = getSwingFactory().createTextField("onkz", true, true, searchKL);
        tfASB = getSwingFactory().createFormattedTextField("asb", searchKL);
        tfName = getSwingFactory().createTextField("name", true, true, searchKL);
        tfOrt = getSwingFactory().createTextField("ort", true, true, searchKL);
        rfHVTType = getSwingFactory().createReferenceField("hvt.typ");
        rfNiederlassung = getSwingFactory().createReferenceField("niederlassung");
        AKJButton btnSearch = getSwingFactory().createButton("search", getActionListener());

        tbMdlHVT = new AKReferenceAwareTableModel<>(
                new String[] { "ONKZ", "ASB", "Name", "Typ" },
                new String[] { "onkz", "asb", "ortsteil", "standortTypRefId" },
                new Class[] { String.class, Integer.class, String.class, String.class });
        tbHVT = new AKJTable(tbMdlHVT, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbHVT.attachSorter();
        tbHVT.fitTable(new int[] { 60, 70, 180, 60 });
        tbHVT.addMouseListener(new AKTableDoubleClickMouseListener(this));
        AKJScrollPane tableSP = new AKJScrollPane(tbHVT, new Dimension(340, 300));

        btnPnl = new AKJPanel(new GridBagLayout());
        btnPnl.add(btnSearch,      GBCFactory.createGBC(  0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(  0, 0, 0, 1, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(lbFeedback,     GBCFactory.createGBC(100, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel filterPnl = new AKJPanel(new GridBagLayout());
        filterPnl.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("border.filter")));
        filterPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        filterPnl.add(lblOrt, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        filterPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        filterPnl.add(tfOrt, GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        filterPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 4, 0, 1, 1, GridBagConstraints.NONE));
        filterPnl.add(lblName, GBCFactory.createGBC(0, 0, 5, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        filterPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 6, 0, 1, 1, GridBagConstraints.NONE));
        filterPnl.add(tfName, GBCFactory.createGBC(0, 0, 7, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        filterPnl.add(lblONKZ, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        filterPnl.add(tfONKZ, GBCFactory.createGBC(0, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        filterPnl.add(lblHVTType, GBCFactory.createGBC(0, 0, 5, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        filterPnl.add(rfHVTType, GBCFactory.createGBC(0, 0, 7, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        filterPnl.add(lblASB, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        filterPnl.add(tfASB, GBCFactory.createGBC(0, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        filterPnl.add(lblNiederlassung, GBCFactory.createGBC(0, 0, 5, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        filterPnl.add(rfNiederlassung, GBCFactory.createGBC(0, 0, 7, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        filterPnl.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 8, 3, 1, 1, GridBagConstraints.BOTH));

        AKJPanel child = getChildPanel();
        child.setLayout(new GridBagLayout());
        child.add(filterPnl, GBCFactory.createGBC(  0,   0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(btnPnl,    GBCFactory.createGBC(  0,   0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(tableSP,   GBCFactory.createGBC(100, 100, 0, 2, 1, 1, GridBagConstraints.BOTH));
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    @SuppressWarnings("squid:UnusedProtectedMethod")
    protected void execute(String command) {
        if ("search".equals(command)) {
            doSearch();
        }
    }

    /* Initialisiert das Panel (Reference-Felder erhalten Find-Parameter) */
    private void init() {
        if (!initialized) {
            try {
                ISimpleFindService sfs = getCCService(QueryCCService.class);
                Reference hvtTypEx = new Reference();
                hvtTypEx.setType(Reference.REF_TYPE_STANDORT_TYP);
                rfHVTType.setReferenceFindExample(hvtTypEx);
                rfHVTType.setFindService(sfs);
                rfNiederlassung.setFindService(sfs);

                List<Reference> hvtTypes = sfs.findByExample(hvtTypEx, Reference.class);
                Map<Long, Reference> hvtTypesMap = new HashMap<>();
                CollectionMapConverter.convert2Map(hvtTypes, hvtTypesMap, "getId", null);
                tbMdlHVT.addReference(3, hvtTypesMap, "strValue");

                initialized = true;

                doSearch();
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKSearchComponent#doSearch()
     */
    @Override
    public final void doSearch() {
        tbMdlHVT.removeAll();
        try {
            setWaitCursor();

            HVTService service = getCCService(HVTService.class);

            HVTQuery query = new HVTQuery();
            query.setMaxResultSize(500);
            query.setAsb(tfASB.getValueAsInt(null));
            query.setOnkz(tfONKZ.getText(null));
            query.setOrt(tfOrt.getText(null));
            query.setOrtsteil(tfName.getText(null));
            query.setStandortTypRefId(rfHVTType.getReferenceIdAs(Long.class));
            query.setNiederlassungId(rfNiederlassung.getReferenceIdAs(Long.class));

            List<HVTGruppeStdView> result = service.findHVTViews(query);
            tbMdlHVT.setData(result);
            tbMdlHVT.fireTableDataChanged();
            updateFeedback(result);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
        finally {
            setDefaultCursor();
        }
    }

    private void updateFeedback(List<HVTGruppeStdView> result) {
        if (result.size() >= 500) {
            lbFeedback.setText("Das Suchergebnis übersteigt 500 Einträge. Bitte Suchkriterien verfeinern.");
            lbFeedback.setForeground(Color.RED);
        }
        else {
            lbFeedback.setText("Alle Suchergebnisse werden angezeigt.");
            lbFeedback.setForeground(new Color(34, 139, 34));
        }
        revalidate();
        repaint();
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
        // not needed for this dialog
    }

    /**
     * @see de.augustakom.common.gui.iface.AKObjectSelectionListener#objectSelected(java.lang.Object)
     */
    @Override
    public void objectSelected(Object selection) {
        try {
            if (selection == null) {
                throw new HurricanGUIException("Bitte wählen Sie einen Standort aus.");
            }
            else {
                prepare4Close();
                setValue(selection);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        @SuppressWarnings("unchecked")
        AKMutableTableModel<HVTGruppeStdView> tableModel = (AKMutableTableModel<HVTGruppeStdView>) tbHVT.getModel();
        HVTGruppeStdView view = tableModel.getDataAtRow(tbHVT.getSelectedRow());
        objectSelected(view);
    }

}

