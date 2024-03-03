/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.07.2007 13:48:18
 */
package de.augustakom.hurrican.gui.shared;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.iface.AKSearchComponent;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.AKSearchKeyListener;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableSingleClickMouseListener;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.service.cc.CCKundenService;
import de.augustakom.hurrican.service.cc.QueryCCService;


/**
 * Dialog zum Suchen und Auswaehlen einer Adresse. Das gewaehlte Adress-Objekt wird ueber die Methode {@code setValue}
 * an den Caller uebergeben.
 *
 *
 */
public class FindAddressDialog extends AbstractServiceOptionDialog implements AKSearchComponent, AKObjectSelectionListener {

    private static final Logger LOGGER = Logger.getLogger(FindAddressDialog.class);

    private static final String RESOURCE = "de/augustakom/hurrican/gui/shared/resources/FindAddressDialog.xml";

    private CCAddress address = null;

    private AKReflectionTableModel<CCAddress> tbMdlAddresses;
    private AKJTable tbAddresses;
    private AKJButton btnSearch;
    private AKJTextField tfName;
    private AKJTextField tfVorname;
    private AKJTextField tfID;
    private AKJTextField tfStrasse;
    private AKJTextField tfOrt;
    private AKReferenceField rfType;

    private boolean initialized;


    /**
     * Konstruktor
     */
    public FindAddressDialog(Long addressTypeRefId) {
        super(RESOURCE);
        createGUI();
        initFirst();
        rfType.setReferenceId(addressTypeRefId);
    }

    private void initFirst() {
        if (!initialized) {
            initialized = true;

            try {
                Reference adrTypeEx = new Reference();
                adrTypeEx.setType(Reference.REF_TYPE_ADDRESS_TYPE);
                adrTypeEx.setGuiVisible(Boolean.TRUE);
                rfType.setFindService(getCCService(QueryCCService.class));
                rfType.setReferenceFindExample(adrTypeEx);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));

        configureButton(CMD_SAVE, "Auswählen", "Wählt den markierten Datensatz aus und schliesst den Dialog", true, true);

        AKJLabel lblName = getSwingFactory().createLabel("name");
        AKJLabel lblVorname = getSwingFactory().createLabel("vorname");
        AKJLabel lblID = getSwingFactory().createLabel("addressid");
        AKJLabel lblStrasse = getSwingFactory().createLabel("strasse");
        AKJLabel lblOrt = getSwingFactory().createLabel("ort");
        AKJLabel lblType = getSwingFactory().createLabel("type");

        tbMdlAddresses = new AKReflectionTableModel<CCAddress>(
                new String[] { "ID", "Name", "Vorname", "Name 2", "Vorname 2", "Strasse", "Strassenzusatz", "Nummer", "Nummernzusatz",
                        "PLZ", "Ort", "Postfach", "Bemerkung" },
                new String[] { "id", CCAddress.NAME, CCAddress.VORNAME, CCAddress.NAME2, CCAddress.VORNAME2, CCAddress.STRASSE,
                        CCAddress.STRASSE_ADD, CCAddress.NUMMER, CCAddress.HAUSNUMMER_ZUSATZ,
                        CCAddress.PLZ, CCAddress.ORT, CCAddress.POSTFACH, CCAddress.BEMERKUNG },
                new Class[] { Long.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
                        String.class, String.class, String.class, String.class, String.class }
        );
        tbAddresses = new AKJTable(tbMdlAddresses, AKJTable.AUTO_RESIZE_ALL_COLUMNS, ListSelectionModel.SINGLE_SELECTION);
        tbAddresses.attachSorter();
        tbAddresses.fitTable(new int[] { 50, 90, 90, 90, 90, 120, 40, 30, 25, 50, 100, 50, 100 });
        tbAddresses.addMouseListener(new AKTableSingleClickMouseListener(this));
        tbAddresses.addMouseListener(new SaveOnDoubleClick());
        tbAddresses.addPopupAction(new ShowAuftraege4AddressAction());
        AKJScrollPane spKunden = new AKJScrollPane(tbAddresses, new Dimension(950, 300));

        AKSearchKeyListener searchKeyListener = new AKSearchKeyListener(this, new int[] { KeyEvent.VK_ENTER });
        btnSearch = getSwingFactory().createButton("search", getActionListener());
        tfName = getSwingFactory().createTextField("name", true, true, searchKeyListener);
        tfVorname = getSwingFactory().createTextField("vorname", true, true, searchKeyListener);
        tfID = getSwingFactory().createTextField("addressid", true, true, searchKeyListener);
        tfStrasse = getSwingFactory().createTextField("strasse", true, true, searchKeyListener);
        tfOrt = getSwingFactory().createTextField("ort", true, true, searchKeyListener);
        rfType = getSwingFactory().createReferenceField("type");

        getChildPanel().setLayout(new GridBagLayout());
        getChildPanel().add(lblType, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(rfType, GBCFactory.createGBC(100, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(lblID, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(tfID, GBCFactory.createGBC(100, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(lblName, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(tfName, GBCFactory.createGBC(100, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(lblVorname, GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(tfVorname, GBCFactory.createGBC(100, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(lblStrasse, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(tfStrasse, GBCFactory.createGBC(100, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(lblOrt, GBCFactory.createGBC(0, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(tfOrt, GBCFactory.createGBC(100, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(btnSearch, GBCFactory.createGBC(0, 0, 4, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(spKunden, GBCFactory.createGBC(0, 0, 0, 3, 5, 1, GridBagConstraints.HORIZONTAL));
    }


    /**
     * Sucht nach Adressen ueber die Daten, die in den Text-Feldern definiert sind.
     */
    @Override
    public void doSearch() {
        address = null;
        final SwingWorker<List<CCAddress>, Void> worker = new SwingWorker<List<CCAddress>, Void>() {
            final String idString = tfID.getText();
            final Long typeInput = (Long) rfType.getReferenceId();
            final String vorname = tfVorname.getText();
            final String name = tfName.getText();
            final String ort = tfOrt.getText();
            final String strasse = tfStrasse.getText();


            @Override
            protected List<CCAddress> doInBackground() throws Exception {
                CCKundenService kundenService = getCCService(CCKundenService.class);
                if (StringUtils.isNotBlank(idString) && StringUtils.isNumeric(idString)) {
                    return Arrays.asList(kundenService.findCCAddress(Long.valueOf(idString)));
                }
                return kundenService.findCCAddresses(typeInput, vorname, name, ort, strasse);
            }

            @Override
            protected void done() {
                try {
                    tbMdlAddresses.setData(get());
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
                finally {
                    btnSearch.setEnabled(true);
                    setDefaultCursor();
                }
            }
        };
        setWaitCursor();
        btnSearch.setEnabled(false);
        worker.execute();
    }


    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        prepare4Close();
        setValue(address);
    }


    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("search".equals(command)) {
            doSearch();
        }
    }


    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }


    @Override
    protected void validateSaveButton() {
        // keine Pruefung durchfuehren
    }


    @Override
    public void objectSelected(Object selection) {
        if (selection instanceof CCAddress) {
            address = (CCAddress) selection;
        }
    }


    private final class SaveOnDoubleClick extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if ((e.getClickCount() >= 2) && (e.getSource() instanceof AKJTable)) {
                AKJTable table = (AKJTable) e.getSource();
                @SuppressWarnings("unchecked")
                AKMutableTableModel<CCAddress> tm = (AKMutableTableModel<CCAddress>) table.getModel();
                CCAddress selection = tm.getDataAtRow(table.getSelectedRow());
                objectSelected(selection);
                doSave();
            }
        }
    }

    private final class ShowAuftraege4AddressAction extends AKAbstractAction {

        public ShowAuftraege4AddressAction() {
            setName("Kunden/Aufträge zu Adresse anzeigen");
            setActionCommand("show.kunden");
            setTooltip("Zeigt eine Liste aller Kunden/Aufträge an, denen diese Adresse als Ansprechpartner zugeordnet ist");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object selection = tbMdlAddresses.getDataAtRow(tbAddresses.getSelectedRow());
            if (selection instanceof CCAddress) {
                KundenAuftraege4AddressDialog dlg = new KundenAuftraege4AddressDialog(((CCAddress) selection).getId());
                DialogHelper.showDialog(FindAddressDialog.this, dlg, true, true);
            }
        }

    }
}


