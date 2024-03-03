package de.augustakom.hurrican.gui.geoid;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKSearchComponent;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKSearchKeyListener;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKTableModelXML;
import de.augustakom.common.gui.swing.table.AKTableOwner;
import de.augustakom.common.model.AbstractObservable;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.query.GeoIdSearchQuery;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.AvailabilityService;


/**
 * Panel fuer die Suche nach Geo IDs.
 */
public class GeoIdSearchPanel extends AbstractServicePanel implements AKSearchComponent, AKTableOwner {

    private static final Logger LOGGER = Logger.getLogger(GeoIdSearchPanel.class);

    private static final String CMD_FILTER = "filter";
    private static final long serialVersionUID = 997509293663875652L;

    private AKTableModelXML<GeoId> tbMdlSearch = null;
    private Publisher publisher = null;
    private AKJTable tbSearch = null;

    private AKJTextField tfGeoId = null;
    private AKJTextField tfStreet = null;
    private AKJTextField tfHouseNo = null;
    private AKJTextField tfZipCode = null;
    private AKJTextField tfCity = null;
    private AKJTextField tfDistrict = null;
    private AKJTextField tfOnkz = null;
    private AKJTextField tfAsb = null;
    private AKJButton btnFilter = null;
    private AKJPanel resultPanel = null;

    public GeoIdSearchPanel() {
        super("de/augustakom/hurrican/gui/geoid/resources/GeoIdSearchPanel.xml");
        init();
        createGUI();
    }

    private void init() {
        publisher = new Publisher();
    }

    @Override
    protected final void createGUI() {
        AKSearchKeyListener searchKL = new AKSearchKeyListener(this, new int[] { KeyEvent.VK_ENTER });

        AKJLabel lblGeoId = getSwingFactory().createLabel("filter.geoid");
        AKJLabel lblStreet = getSwingFactory().createLabel("filter.street");
        AKJLabel lblZipCode = getSwingFactory().createLabel("filter.zip");
        AKJLabel lblCity = getSwingFactory().createLabel("filter.city");
        AKJLabel lblOnkz = getSwingFactory().createLabel("filter.onkz");

        tfGeoId = getSwingFactory().createTextField("filter.geoid", true, true, searchKL);
        tfStreet = getSwingFactory().createTextField("filter.street", true, true, searchKL);
        tfHouseNo = getSwingFactory().createTextField("filter.house.no", true, true, searchKL);
        tfZipCode = getSwingFactory().createTextField("filter.zip", true, true, searchKL);
        tfCity = getSwingFactory().createTextField("filter.city", true, true, searchKL);
        tfDistrict = getSwingFactory().createTextField("filter.district", true, true, searchKL);
        tfOnkz = getSwingFactory().createTextField("filter.onkz", true, true, searchKL);
        tfAsb = getSwingFactory().createTextField("filter.asb", true, true, searchKL);
        btnFilter = getSwingFactory().createButton(CMD_FILTER, getActionListener());

        tbMdlSearch = new AKTableModelXML<>("de/augustakom/hurrican/gui/geoid/resources/"
                + "GeoIdSearchPanelTableModel.xml");
        tbSearch = new AKJTable(tbMdlSearch, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbSearch.attachSorter();
        tbSearch.addTableListener(this);
        tbSearch.fitTable(tbMdlSearch.getFitList());
        AKJScrollPane spSearchResult = new AKJScrollPane(tbSearch, new Dimension(350, 200));

        ToggleNoDTAGTALAction toggleNoDTAGTALAction = new ToggleNoDTAGTALAction();
        toggleNoDTAGTALAction.setParentClass(this.getClass());
        tbSearch.addPopupAction(toggleNoDTAGTALAction);

        AKJPanel filterPanel = new AKJPanel(new GridBagLayout());
        filterPanel.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("border.filter")));
        filterPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        filterPanel.add(lblGeoId, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        filterPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        filterPanel.add(tfGeoId, GBCFactory.createGBC(100, 0, 3, 0, 2, 1, GridBagConstraints.HORIZONTAL));
        filterPanel.add(lblStreet, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        filterPanel.add(tfStreet, GBCFactory.createGBC(80, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        filterPanel.add(tfHouseNo, GBCFactory.createGBC(20, 0, 4, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        filterPanel.add(lblZipCode, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        filterPanel.add(tfZipCode, GBCFactory.createGBC(100, 0, 3, 2, 2, 1, GridBagConstraints.HORIZONTAL));
        filterPanel.add(lblCity, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        filterPanel.add(tfCity, GBCFactory.createGBC(80, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        filterPanel.add(tfDistrict, GBCFactory.createGBC(20, 0, 4, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        filterPanel.add(lblOnkz, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        filterPanel.add(tfOnkz, GBCFactory.createGBC(100, 0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        filterPanel.add(tfAsb, GBCFactory.createGBC(100, 0, 4, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        filterPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 5, 5, 1, 1, GridBagConstraints.BOTH));
        filterPanel.add(btnFilter, GBCFactory.createGBC(0, 0, 1, 6, 1, 1, GridBagConstraints.HORIZONTAL));

        resultPanel = new AKJPanel(new GridBagLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("border.search.result")));
        resultPanel.add(spSearchResult, GBCFactory.createGBC(100, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));

        this.setLayout(new GridBagLayout());
        this.add(filterPanel, GBCFactory.createGBC(40, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));
        this.add(resultPanel, GBCFactory.createGBC(60, 100, 1, 0, 1, 1, GridBagConstraints.BOTH));
    }

    @Override
    protected void execute(String command) {
        if (CMD_FILTER.equals(command)) {
            doSearch();
        }
    }

    @Override
    public void doSearch() {
        final GeoIdSearchQuery query = new GeoIdSearchQuery();
        if (!StringUtils.isBlank(tfGeoId.getText())) {
            query.setId(tfGeoId.getTextAsLong(null));
        }
        query.setStreet(tfStreet.getText());
        query.setHouseNum(tfHouseNo.getText());
        query.setZipCode(tfZipCode.getText());
        query.setCity(tfCity.getText());
        query.setDistrict(tfDistrict.getText());
        query.setOnkz(tfOnkz.getText());
        if (!StringUtils.isBlank(tfAsb.getText())) {
            query.setAsb(tfAsb.getTextAsInt(null));
        }

        final SwingWorker<List<GeoId>, Void> worker = new SwingWorker<List<GeoId>, Void>() {

            final GeoIdSearchQuery localGeoIdSearchQuery = query;

            @Override
            protected List<GeoId> doInBackground() throws Exception {
                AvailabilityService availabilityService = getCCService(AvailabilityService.class);
                return availabilityService.findGeoIdsBySearchQuery(localGeoIdSearchQuery);
            }

            @Override
            protected void done() {
                try {
                    List<GeoId> results = get();
                    tbMdlSearch.setData(results);
                    showResultCount((results != null) ? results.size() : 0);
                    notifyObservers();
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
                finally {
                    btnFilter.setEnabled(true);
                    stopProgressBar();
                    setDefaultCursor();
                }
            }
        };

        setWaitCursor();
        showProgressBar("suchen...");
        btnFilter.setEnabled(false);

        worker.execute();
    }

    private void showResultCount(int count) {
        TitledBorder border = (TitledBorder) resultPanel.getBorder();
        StringBuilder title = new StringBuilder();
        title.append(getSwingFactory().getText("border.search.result"));
        if (count > 0) {
            title.append(" (");
            title.append(count);
            title.append(")");
        }
        border.setTitle(title.toString());
        resultPanel.repaint();
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    public void showDetails(Object details) {
        notifyObservers();
    }

    public void addObserver(Observer observer) {
        publisher.addObserver(observer);
    }

    public void notifyObservers() {
        if (tbSearch.getSelectedRow() != -1) {
            @SuppressWarnings("unchecked")
            AKMutableTableModel<GeoId> tbMdl = (AKMutableTableModel<GeoId>) tbSearch.getModel();
            GeoId row = tbMdl.getDataAtRow(tbSearch.getSelectedRow());
            publisher.notifyObservers(true, (row.getId() != null) ? row.getId() : null);
        }
        else {
            publisher.notifyObservers(true, null);
        }
    }

    private void toggleNoDTAGTALAction() {
        try {
            int selectedRow = tbSearch.getSelectedRow();
            if (selectedRow != -1) {
                @SuppressWarnings("unchecked")
                AKMutableTableModel<GeoId> tbMdl = (AKMutableTableModel<GeoId>) tbSearch.getModel();
                GeoId row = tbMdl.getDataAtRow(tbSearch.getSelectedRow());
                AvailabilityService availabilityService = getCCService(AvailabilityService.class);
                GeoId geoId = availabilityService.findGeoId(row.getId());
                if (geoId == null) {
                    throw new FindException("Zu toggelnde Geo ID konnte nicht im Cache ermittelt werden.");
                }
                geoId.setNoDTAGTAL((BooleanTools.nullToFalse(geoId.getNoDTAGTAL())) ? Boolean.FALSE : Boolean.TRUE);
                availabilityService.saveGeoId(geoId);
                row.setNoDTAGTAL(geoId.getNoDTAGTAL());
                tbSearch.repaint();
                tbMdlSearch.fireTableRowsUpdated(selectedRow, selectedRow);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    static protected class Publisher extends AbstractObservable {
    }

    /* Action, um das Flag 'keine DTAG Versorgung' zu toggeln. */
    protected class ToggleNoDTAGTALAction extends AKAbstractAction {
        private static final long serialVersionUID = -8289940815646420532L;

        public ToggleNoDTAGTALAction() {
            super();
            setName("'keine DTAG Versorgung' toggeln");
            setActionCommand("toggle.no.dtag.tal");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            toggleNoDTAGTALAction();
        }
    }

}
