/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.05.2011 09:14:04
 */
package de.augustakom.hurrican.gui.stammdaten;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.iface.AKSaveSelectedTableRow;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.gui.swing.table.FilterOperator;
import de.augustakom.common.gui.swing.table.FilterOperators;
import de.augustakom.common.gui.swing.table.FilterRelation;
import de.augustakom.common.gui.swing.table.FilterRelations;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.gui.auftrag.AuftragDataFrame;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.model.cc.RangierungFreigabeInfo;
import de.augustakom.hurrican.model.cc.view.PhysikFreigebenView;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.cc.RangierungFreigabeService;


/**
 * Panel fuer die Darstellung und Freigabe aller Rangierungen, die nicht durch die automatische Rangierungsfreigabe
 * freigegeben werden konnten. <br>
 *
 *
 */
public class RangierungsKlaerfaellePanel extends AbstractServicePanel implements AKDataLoaderComponent,
        AKObjectSelectionListener, AKSaveSelectedTableRow {

    private static final Logger LOGGER = Logger.getLogger(RangierungsKlaerfaellePanel.class);

    private AKJTable tbRangierung = null;
    private RangierungFreigabeTableModel tbMdlRangierung = null;
    private List<PhysikFreigebenView> freigabeViews = null;
    private Map<Long, List<PhysikFreigebenView>> rangierungRelationen = null;

    private RangierungFreigabeService rangierungFreigabeService;

    static class KlaerfaelleResultSet {
        List<PhysikFreigebenView> freigabeViews = null;
        Map<Long, List<PhysikFreigebenView>> rangierungRelationen = null;
    }

    /**
     * Konstruktor
     */
    public RangierungsKlaerfaellePanel() {
        super("de/augustakom/hurrican/gui/stammdaten/resources/RangierungsKlaerfaellePanel.xml");
        init();
        createGUI();
        loadData();
    }

    private void init() {
        try {
            rangierungFreigabeService = getCCService(RangierungFreigabeService.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    protected final void createGUI() {
        AKJButton btnAlleAusw = getSwingFactory().createButton("alle.auswaehlen", getActionListener());
        AKJButton btnAuswFreig = getSwingFactory().createButton("freigeben", getActionListener());
        AKJButton btnAuswahlCancel = getSwingFactory().createButton("auswahl.cancel", getActionListener());

        tbMdlRangierung = new RangierungFreigabeTableModel(this);
        tbRangierung = new AKJTable(tbMdlRangierung, AKJTable.AUTO_RESIZE_OFF,
                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tbRangierung.attachSorter();
        tbRangierung.addMouseListener(new AKTableDoubleClickMouseListener(this));
        tbRangierung.addPopupAction(new OpenAuftragAction());
        tbRangierung.fitTable(new int[] { 75, 80, 70, 70, 60, 80, 80, 80, 70, 80, 80, 60, 200, 200 });
        AKJScrollPane spTable = new AKJScrollPane(tbRangierung);

        AKJPanel bottom = new AKJPanel(new GridBagLayout());
        bottom.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE, new Insets(2, 50, 2, 50)));
        bottom.add(btnAlleAusw, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(2, 10, 2, 10)));
        bottom.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        bottom.add(btnAuswahlCancel, GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(2, 10, 2, 10)));
        bottom.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 4, 0, 1, 1, GridBagConstraints.NONE));
        bottom.add(btnAuswFreig, GBCFactory.createGBC(0, 0, 5, 0, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(2, 10, 2, 10)));
        bottom.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 6, 0, 1, 1, GridBagConstraints.NONE, new Insets(2, 50, 2, 50)));

        this.setLayout(new GridBagLayout());
        this.add(spTable, GBCFactory.createGBC(100, 100, 0, 2, 3, 1, GridBagConstraints.BOTH));
        this.add(bottom, GBCFactory.createGBC(100, 0, 0, 3, 3, 1, GridBagConstraints.HORIZONTAL));

        manageGUI(btnAlleAusw, btnAuswFreig, btnAuswahlCancel);
    }

    @Override
    public final void loadData() {
        final SwingWorker<KlaerfaelleResultSet, Void> worker = new SwingWorker<KlaerfaelleResultSet, Void>() {

            @Override
            public KlaerfaelleResultSet doInBackground() throws Exception {
                KlaerfaelleResultSet klaerfaelleResultSet = new KlaerfaelleResultSet();
                klaerfaelleResultSet.freigabeViews = new ArrayList<PhysikFreigebenView>();
                klaerfaelleResultSet.rangierungRelationen = rangierungFreigabeService.createPhysikFreigabeView(DateTools
                        .getPreviousDay(new Date()), klaerfaelleResultSet.freigabeViews, true);
                return klaerfaelleResultSet;
            }

            @Override
            protected void done() {
                try {
                    KlaerfaelleResultSet klaerfaelleResultSet = get();
                    freigabeViews = klaerfaelleResultSet.freigabeViews;
                    rangierungRelationen = klaerfaelleResultSet.rangierungRelationen;
                    tbMdlRangierung.setData(freigabeViews);
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
                finally {
                    stopProgressBar();
                    setDefaultCursor();
                }
            }
        };

        setWaitCursor();
        showProgressBar("Datensätze suchen...");
        worker.execute();
    }

    @Override
    protected void execute(String command) {
        if ("alle.auswaehlen".equals(command)) {
            doAlleAuswaehlen();
        }
        else if ("auswahl.cancel".equals(command)) {
            doAuswahlCancel();
        }
        else if ("freigeben".equals(command)) {
            doFreigeben();
        }
    }


    /**
     * Selektiert alle in der Tabellenansicht gezeigten Rangierungen zur Freigabe
     */
    private void doAlleAuswaehlen() {
        int freigabeCanceled = 0;
        if (freigabeViews != null) {
            for (PhysikFreigebenView view : freigabeViews) {
                if (tbMdlRangierung.isFreigabeErlaubt(view)) {
                    view.setFreigeben(true);
                }
                else {
                    freigabeCanceled++;
                }
            }

            tbRangierung.revalidate();
            tbRangierung.repaint();
        }
        if (freigabeCanceled > 0) {
            MessageHelper.showWarningDialog(this, "Mindestens eine oder mehrere Rangierungen können "
                    + "auf Grund des Auftragsstatuses nicht freigegeben werden!", true);
        }
    }

    /**
     * Loescht die Selektion in der Tabellenansicht (Feld Freigeben)
     */
    private void doAuswahlCancel() {
        if (freigabeViews != null) {
            for (PhysikFreigebenView view : freigabeViews) {
                view.setFreigeben(false);
            }

            tbRangierung.revalidate();
            tbRangierung.repaint();
        }
    }

    /**
     * Gibt die selektierten Rangierungen frei
     */
    private void doFreigeben() {
        Boolean readyForFreigabe = true;
        try {
            if (freigabeViews != null) {
                for (PhysikFreigebenView freigabeView : freigabeViews) {
                    if (BooleanTools.nullToFalse(freigabeView.getFreigeben())) {
                        readyForFreigabe = isReadyForFreigabe(readyForFreigabe, freigabeView);
                    }
                    if (!readyForFreigabe) {
                        break;
                    }
                }
                if (readyForFreigabe) {
                    rangierungFreigabeService.rangierungenFreigeben(rangierungRelationen);
                    tbMdlRangierung.clearFilter();
                    loadData();
                    MessageHelper.showInfoDialog(this, "Es wurden alle ausgewählten Rangierungen freigeben!");
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private Boolean isReadyForFreigabe(Boolean readyForFreigabeIn, PhysikFreigebenView freigabeView) {
        Boolean readyForFreigabe = readyForFreigabeIn;
        List<PhysikFreigebenView> auftraegeZurRangierung = rangierungRelationen.get(freigabeView.getRangierId());
        for (PhysikFreigebenView auftrag : auftraegeZurRangierung) {
            if (!BooleanTools.nullToFalse(auftrag.getFreigeben())) {
                readyForFreigabe = false;

                FilterRelation relation = new FilterRelation(FilterRelations.AND);
                relation.addChild(new FilterOperator(FilterOperators.EQ, auftrag.getRangierId(), RangierungFreigabeTableModel.COL_RANGIER_ID));
                relation.filter(tbMdlRangierung, tbMdlRangierung.getData());
                relation.setName("RangierungsFilter");
                tbMdlRangierung.clearFilter();
                tbMdlRangierung.setFilter(relation);

                MessageHelper.showInfoDialog(this, "Es wurden nicht alle Aufträge der selektierten Rangierung ("
                        + auftrag.getRangierId() +
                        ") ausgewählt, bitte geben Sie alle Aufträge frei. Für eine verbesserte Übersicht werden "
                        + "nun alle Aufträge zu der betreffenden Rangierung angezeigt."
                        + SystemUtils.LINE_SEPARATOR + SystemUtils.LINE_SEPARATOR
                        + "Bitte beachten: Aktuell sind noch keine Rangierungen freigeben!");

                break;
            }
        }
        return readyForFreigabe;
    }

    @Override
    public void update(Observable o, Object arg) {
        // intentionally left blank
    }


    @Override
    public void objectSelected(Object selection) {
        if (selection instanceof CCAuftragModel) {
            AuftragDataFrame.openFrame((CCAuftragModel) selection);
        }
    }

    @Override
    public void saveRow(Object Row) {
        // wenn bearbeiten-checkbox angeklickt wird
        if (Row instanceof PhysikFreigebenView) {
            try {
                PhysikFreigebenView view = (PhysikFreigebenView) Row;
                RangierungFreigabeInfo freigabeInfo = rangierungFreigabeService.findRangierungFreigabeInfo(view.getRangierId(),
                        view.getAuftragId());
                freigabeInfo.setInBearbeitung(view.getInBearbeitung());
                rangierungFreigabeService.saveRangierungFreigabeInfo(freigabeInfo);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage());
                MessageHelper.showErrorDialog(this, e);
            }
        }
    }

    /**
     * Action fuer das Table-PopUp Menu, um den aktuell selektierten Auftrag zu oeffnen.
     */
    class OpenAuftragAction extends AKAbstractAction {
        public OpenAuftragAction() {
            super();
            setName("Auftrag öffnen");
            setTooltip("Öffnet den aktuellen Auftrag");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int row = tbRangierung.getSelectedRow();
            @SuppressWarnings("unchecked")
            AKMutableTableModel<PhysikFreigebenView> model = (AKMutableTableModel<PhysikFreigebenView>) tbRangierung.getModel();
            Object selection = model.getDataAtRow(row);
            objectSelected(selection);
        }
    }

}


