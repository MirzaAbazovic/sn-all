/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.08.2004 13:34:59
 */
package de.augustakom.hurrican.gui.shared;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.iface.AKSearchComponent;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKSearchKeyListener;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.tools.lang.WildcardTools;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.query.GeoIdQuery;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.cc.AvailabilityService;


/**
 * Dialog, um nach einer GeoId zu suchen. <br> Die ausgewaehlte GeoId wird ueber die Methode <code>setValue</code>
 * gespeichert und kann ueber die Methode <code>getValue</code> von dem Aufrufer abgefragt werden.
 */
public class GeoIdSearchDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent,
        AKObjectSelectionListener, AKSearchComponent {

    private static final Logger LOGGER = Logger.getLogger(GeoIdSearchDialog.class);

    private AKJTextField tfStreet = null;
    private AKJTextField tfHouseNum = null;
    private AKJTextField tfCity = null;
    private AKJTextField tfDistrict = null;
    private AKJButton btnSearch = null;
    private AKJTable tbGeoIds = null;
    private AKTableModel<GeoId> tbMdlGeoIds;

    private Long orderNoOrig = null;

    /**
     * Konstruktor.
     */
    public GeoIdSearchDialog() {
        super("de/augustakom/hurrican/gui/shared/resources/GeoIdSearchDialog.xml");
        createGUI();
    }

    /**
     * Konstruktor mit Angabe der Taifun-Auftragsnummer. <br> In den Suchfeldern wird die Anschluss-adresse des Aufrags
     * zur Vorauswahl eingetragen.
     *
     * @param kundeNoOrig
     */
    public GeoIdSearchDialog(Long orderNoOrig) {
        super("de/augustakom/hurrican/gui/shared/resources/GeoIdSearchDialog.xml");
        this.orderNoOrig = orderNoOrig;
        createGUI();
        loadData();
    }

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));
        setIconURL("de/augustakom/hurrican/gui/images/adresse.gif");

        configureButton(CMD_SAVE, "Auswählen", "Übernimmt die selektierte Strasse", true, true);
        configureButton(CMD_CANCEL, "Abbrechen", "Schliesst den Dialog, ohne eine Strasse zu übernehmen", true, true);

        AKJLabel lblStreet = getSwingFactory().createLabel("street");
        AKJLabel lblHouseNum = getSwingFactory().createLabel("housenum");
        AKJLabel lblCity = getSwingFactory().createLabel("city");
        AKJLabel lblDistrict = getSwingFactory().createLabel("district");

        AKSearchKeyListener searchKL = new AKSearchKeyListener(this, new int[] { KeyEvent.VK_ENTER });
        tfStreet = getSwingFactory().createTextField("street", true, true, searchKL);
        tfHouseNum = getSwingFactory().createTextField("housenum", true, true, searchKL);
        tfCity = getSwingFactory().createTextField("city", true, true, searchKL);
        tfDistrict = getSwingFactory().createTextField("district", true, true, searchKL);
        btnSearch = getSwingFactory().createButton("search", getActionListener());

        tbMdlGeoIds = new AKReflectionTableModel<GeoId>(
                new String[] { "Strasse", "HausNr", "Zusatz", "PLZ", "Ort", "Ortsteil", "GeoID" },
                new String[] { GeoId.STREET, GeoId.HOUSENUM, GeoId.HOUSENUM_EXT, GeoId.ZIPCODE, GeoId.CITY, GeoId.DISTRICT, GeoId.ID },
                new Class[] { String.class, String.class, String.class, String.class, String.class, String.class, Long.class });
        tbGeoIds = new AKJTable(tbMdlGeoIds, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbGeoIds.attachSorter();
        tbGeoIds.addMouseListener(new AKTableDoubleClickMouseListener(this));
        tbGeoIds.fitTable(new int[] { 150, 40, 40, 60, 130, 70 });
        AKJScrollPane spTable = new AKJScrollPane(tbGeoIds);
        spTable.setPreferredSize(new Dimension(515, 180));

        AKJPanel search = new AKJPanel(new GridBagLayout());
        search.setBorder(BorderFactory.createTitledBorder("Suchparameter"));
        search.add(lblStreet, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        search.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        search.add(tfStreet, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        search.add(lblHouseNum, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        search.add(tfHouseNum, GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        search.add(lblCity, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        search.add(tfCity, GBCFactory.createGBC(100, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        search.add(lblDistrict, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        search.add(tfDistrict, GBCFactory.createGBC(100, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        search.add(btnSearch, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.NONE));

        getChildPanel().setLayout(new GridBagLayout());
        getChildPanel().add(search, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(100, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(spTable, GBCFactory.createGBC(100, 0, 0, 1, 2, 1, GridBagConstraints.HORIZONTAL));
    }

    @Override
    protected void execute(String command) {
        if ("search".equals(command)) {
            doSearch();
        }
    }

    @Override
    public final void loadData() {
        if (this.orderNoOrig != null) {
            try {
                setWaitCursor();
                GuiTools.lockComponents(new Component[] { tfStreet, tfHouseNum, tfCity });

                BillingAuftragService as = getBillingService(BillingAuftragService.class);
                Adresse adresse = as.findAnschlussAdresse4Auftrag(orderNoOrig, Endstelle.ENDSTELLEN_TYP_B);
                if (adresse != null) {
                    String srcPattern = StringUtils.chomp(adresse.getStrasse(), ".");
                    // Erweitere Search-Pattern um Wildcard
                    srcPattern = (StringUtils.isNotBlank(srcPattern)) ? srcPattern + WildcardTools.SYSTEM_WILDCARD : srcPattern;
                    tfStreet.setText(srcPattern); // Bug-ID: 11472
                    tfHouseNum.setText(adresse.getNummer());
                    tfCity.setText(adresse.getOrt());
                    tfDistrict.setText(adresse.getOrtsteil());
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
            finally {
                GuiTools.unlockComponents(new Component[] { tfStreet, tfHouseNum, tfCity });
                tfStreet.requestFocusInWindow();
                setDefaultCursor();
            }
        }
    }

    @Override
    public void doSearch() {
        final GeoIdQuery query = new GeoIdQuery();
        query.setStreet(tfStreet.getText(null));
        query.setHouseNum(tfHouseNum.getText(null));
        query.setCity(tfCity.getText(null));
        query.setDistrict(tfDistrict.getText(null));
        query.setServiceable(Boolean.TRUE);

        final SwingWorker<List<GeoId>, Void> worker = new SwingWorker<List<GeoId>, Void>() {
            @Override
            protected List<GeoId> doInBackground() throws Exception {
                AvailabilityService service = getCCService(AvailabilityService.class);

                List<GeoId> searchResult = service.findGeoIdsByQuery(query);
                return searchResult;
            }

            @Override
            protected void done() {
                try {
                    tbMdlGeoIds.setData(get());
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

    @Override
    protected void doSave() {
        int row = tbGeoIds.getSelectedRow();
        if (row >= 0) {
            @SuppressWarnings("unchecked")
            AKMutableTableModel<GeoId> mdl = (AKMutableTableModel<GeoId>) tbGeoIds.getModel();
            Object selection = mdl.getDataAtRow(row);
            if (selection instanceof GeoId) {
                prepare4Close();
                setValue((selection));
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    public void objectSelected(Object selection) {
        if (getButton(CMD_SAVE).isEnabled()) {
            doSave();
        }
    }

}


