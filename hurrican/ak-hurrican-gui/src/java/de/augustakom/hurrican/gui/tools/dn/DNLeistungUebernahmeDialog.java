/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.10.2006 08:47:36
 */
package de.augustakom.hurrican.gui.tools.dn;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKDateSelectionDialog;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableOwner;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.shared.DNLeistungsViewTableModel;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.query.RufnummerQuery;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.dn.DNLeistungsView;
import de.augustakom.hurrican.model.cc.dn.Leistungsbuendel;
import de.augustakom.hurrican.model.cc.view.LeistungInLeistungsbuendelView;
import de.augustakom.hurrican.model.shared.view.AuftragDNView;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.CCRufnummernService;


/**
 * Dialog zur Uebernahme von Rufnummernleistungen aus der Historie einer Rufnummer.
 *
 *
 */
public class DNLeistungUebernahmeDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent,
        AKTableOwner {

    private static final Logger LOGGER = Logger.getLogger(DNLeistungUebernahmeDialog.class);

    private static final String TITLE = "Leistungsübernahme für {0}/{1} {2}-{3}";

    private Rufnummer actDN = null;
    private Long ccAuftragId = null;

    // GUI-Elemente
    private AKJTable tbDN = null;
    private AKReflectionTableModel tbMdlDN = null;
    private AKJTable tbLeistungen = null;
    private DNLeistungsViewTableModel tbMdlLeistungen = null;

    /**
     * Konstruktor.
     *
     * @param actDN       Rufnummer, auf die Leistungen uebernommen werden sollen
     * @param ccAuftragId CC-Auftrags ID
     * @throws IllegalArgumentException
     */
    public DNLeistungUebernahmeDialog(Rufnummer actDN, Long ccAuftragId) {
        super(null);
        if (actDN == null) {
            throw new IllegalArgumentException("Keine Rufnummer ausgewaehlt!");
        }
        if (ccAuftragId == null) {
            throw new IllegalArgumentException("Kein Hurrican Auftrag angegeben!");
        }

        this.actDN = actDN;
        this.ccAuftragId = ccAuftragId;

        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        String title = StringTools.formatString(TITLE, new Object[] { actDN.getOnKz(),
                actDN.getDnBase(), (actDN.getRangeFrom() != null) ? actDN.getRangeFrom() : "",
                (actDN.getRangeTo() != null) ? actDN.getRangeTo() : "" }, null);
        setTitle(title);
        configureButton(CMD_SAVE, "Übernehmen", "Übernimmt die DN-Leistungen", true, true);

        tbMdlDN = new AKReflectionTableModel<AuftragDNView>(
                new String[] { "DN_NO", "ONKZ", "DN_BASE", "RANGE_FROM", "RANGE_TO", "Order__NO", "CC-Auftrag", VerbindungsBezeichnung.VBZ_BEZEICHNUNG },
                new String[] { "dnNo", "onKz", "dnBase", "rangeFrom", "rangeTo",
                        "auftragNoOrig", "auftragId", "vbz" },
                new Class[] { Long.class, String.class, String.class, String.class, String.class,
                        Long.class, Long.class, String.class }
        );
        tbDN = new AKJTable(tbMdlDN, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbDN.attachSorter();
        tbDN.addTableListener(this);
        tbDN.fitTable(new int[] { 60, 60, 80, 40, 40, 80, 80, 110 });
        AKJScrollPane spDN = new AKJScrollPane(tbDN, new Dimension(620, 100));

        AKJPanel north = new AKJPanel(new BorderLayout(), "DN-History");
        north.add(spDN, BorderLayout.CENTER);

        tbMdlLeistungen = new DNLeistungsViewTableModel();
        tbLeistungen = new AKJTable(tbMdlLeistungen,
                AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tbLeistungen.attachSorter();
        tbLeistungen.fitTable(new int[] { 30, 5, 150, 65, 65, 65, 65, 65, 65, 65, 65, 150, 50 });
        AKJScrollPane spLeistungen = new AKJScrollPane(tbLeistungen, new Dimension(620, 300));

        AKJPanel center = new AKJPanel(new BorderLayout(), "DN-Leistungen");
        center.add(spLeistungen, BorderLayout.CENTER);

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(north, BorderLayout.NORTH);
        getChildPanel().add(center, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        final SwingWorker<List<AuftragDNView>, Void> worker = new SwingWorker<List<AuftragDNView>, Void>() {

            // Initialize required data from panel in the constructor since this should be done in the EDT thread.
            final Long dnNo = actDN.getDnNo();
            final Long dnNoOrig = actDN.getDnNoOrig();

            @Override
            public List<AuftragDNView> doInBackground() throws Exception {
                final RufnummerQuery query = new RufnummerQuery();
                query.setDnNoOrig(dnNoOrig);
                RufnummerService service = getBillingService(RufnummerService.class);
                List<AuftragDNView> result = service.findAuftragDNViews(query);
                if (CollectionTools.isNotEmpty(result)) {
                    CollectionUtils.filter(result, new Predicate() {
                        @Override
                        public boolean evaluate(Object obj) {
                            if (!(obj instanceof AuftragDNView)) {
                                return false;
                            }
                            return (!NumberTools.equal(((AuftragDNView) obj).getDnNo(), dnNo));
                        }
                    });
                }
                return result;
            }

            @Override
            protected void done() {
                try {
                    tbMdlDN.setData(get());
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
                finally {
                    setDefaultCursor();
                }
            }
        };

        setWaitCursor();
        worker.execute();
    }

    /**
     * Kopiert die ausgewaehlten DN-Leistungen auf die aktuelle Rufnummer. <br> Werden Leistungen mit Kuendigungsdatum
     * kopiert, wird dieses Datum entfernt! Es koennen nur Leistungen kopiert werden, die zum aktuellen Leistungsbuendel
     * passen.
     *
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        try {
            CCRufnummernService ccRS = getCCService(CCRufnummernService.class);
            Leistungsbuendel lb = ccRS.findLeistungsbuendel4Auftrag(ccAuftragId);
            if (lb == null) {
                throw new HurricanGUIException("Es konnte kein aktuelles Leistungsbuendel ermittelt werden!");
            }
            final List<LeistungInLeistungsbuendelView> dnLeistungen = ccRS.findAllLb2Leistung(lb.getId());

            // selektierte DN-Leistungen ermitteln
            AKMutableTableModel mdl = (AKMutableTableModel) tbLeistungen.getModel();
            List<DNLeistungsView> selection = new ArrayList<DNLeistungsView>();
            int[] selRows = tbLeistungen.getSelectedRows();
            for (int i = 0; i < selRows.length; i++) {
                selection.add((DNLeistungsView) mdl.getDataAtRow(selRows[i]));
            }

            // Leistungen heraus filtern, die nicht im aktuellen Leistungsbuendel enthalten sind
            List<DNLeistungsView> toRemove = new ArrayList<DNLeistungsView>();
            for (DNLeistungsView dnLsView : selection) {
                boolean found = false;
                for (LeistungInLeistungsbuendelView leistung : dnLeistungen) {
                    if (NumberTools.equal(leistung.getLeistungId(), dnLsView.getLeistungsId())) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    toRemove.add(dnLsView);
                }
            }
            selection.removeAll(toRemove);

            if (CollectionTools.isNotEmpty(toRemove)) {
                StringBuilder msg = new StringBuilder(
                        "Folgende Leistungen sind fuer das aktuelle Produkt nicht moeglich:");
                for (DNLeistungsView view : toRemove) {
                    msg.append(SystemUtils.LINE_SEPARATOR);
                    msg.append(view.getLeistung());
                }
                MessageHelper.showInfoDialog(getMainFrame(), msg.toString(), null, true);
            }

            if (CollectionTools.isNotEmpty(selection)) {
                AKDateSelectionDialog dlg = new AKDateSelectionDialog("Datum",
                        "Realisierungsdatum fuer die DN-Leistung", "Realisierungsdatum:");
                Object result = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
                if (!(result instanceof Date)) {
                    throw new HurricanGUIException("Bitte ein gueltiges Datum angeben!");
                }

                // alle uebrig gebliebenen Leistungen der aktuellen DN zuordnen
                collateLeftovers2DN(ccRS, lb, selection, (Date) result);

                prepare4Close();
                setValue(OK_OPTION);
            }
            else {
                throw new HurricanGUIException("Es konnten keine DN-Leistungen uebernommen werden!");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private void collateLeftovers2DN(CCRufnummernService ccRS, Leistungsbuendel lb, List<DNLeistungsView> selection, Date result) {
        List<Throwable> exceptions = new ArrayList<Throwable>();
        for (DNLeistungsView dnLsView : selection) {
            try {
                ccRS.saveLeistung2DN(actDN, dnLsView.getLeistungsId(), dnLsView.getParameter(),
                        false, lb.getId(), result, dnLsView.getParameterId(),
                        HurricanSystemRegistry.instance().getSessionId());
            }
            catch (StoreException e) {
                LOGGER.error(e.getMessage(), e);
                exceptions.add(e);
            }
        }

        if (CollectionTools.isNotEmpty(exceptions)) {
            MessageHelper.showErrorDialog(getMainFrame(), exceptions);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.table.AKTableOwner#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {
        AKMutableTableModel mdl = (AKMutableTableModel) tbDN.getModel();
        Object selection = mdl.getDataAtRow(tbDN.getSelectedRow());
        if (selection instanceof AuftragDNView) {
            // Leistungen der gewaehlten Rufnummer laden (ueber die DN_NO)
            try {
                CCRufnummernService ccRS = getCCService(CCRufnummernService.class);
                List<Long> dnNo = new ArrayList<Long>();
                dnNo.add(((AuftragDNView) selection).getDnNo());
                List<DNLeistungsView> views = ccRS.findDNLeistungen(dnNo, null);
                tbMdlLeistungen.setData(views);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

}


