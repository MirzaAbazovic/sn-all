/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.07.2004 14:41:28
 */
package de.augustakom.hurrican.gui.verlauf;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.BAVerlaufConfig;
import de.augustakom.hurrican.model.cc.BAVerlaufZusatz;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.service.cc.BAConfigService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.NiederlassungService;
import de.augustakom.hurrican.service.cc.ReferenceService;


/**
 * Panel, in dem die Bauauftragsverlauf-Zusaetze fuer einen best. Bauauftragsverlauf dargestellt werden.
 *
 *
 */
public class BAVerlaufZusatzPanel extends AbstractAdminPanel implements AKObjectSelectionListener {

    private static final Logger LOGGER = Logger.getLogger(BAVerlaufZusatzPanel.class);

    private AKJTable tbZusatz = null;
    private BAVZusatzTableModel tbMdlZusatz = null;

    private List<HVTGruppe> hvtGruppenList = null;
    private List abteilungenList = null;

    /* Map, in der alle HVT-Gruppen gespeichert werden. (Key: ID der HVT-Gruppe) */
    private Map hvtGruppen = null;
    /* Map, in der alle Abteilungen gespeichert werden. (Key: ID der Abteilung) */
    private Map abteilungen = null;

    private BAVerlaufConfig baVerlConf = null;

    /**
     * Konstruktor
     */
    public BAVerlaufZusatzPanel() {
        super("de/augustakom/hurrican/gui/verlauf/resources/BAVerlaufZusatzPanel.xml");
        createGUI();
        loadData();
    }

    @Override
    protected final void createGUI() {
        AKJLabel lblZusatz = getSwingFactory().createLabel("zusatz");
        AKJButton btnAdd = getSwingFactory().createButton("add", getActionListener());
        btnAdd.setBorder(null);
        AKJButton btnDelete = getSwingFactory().createButton("delete", getActionListener());
        btnDelete.setBorder(null);

        AKJPanel btnPanel = new AKJPanel(new GridBagLayout());
        btnPanel.add(btnAdd, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(btnDelete, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 2, 1, 1, GridBagConstraints.VERTICAL));

        tbMdlZusatz = new BAVZusatzTableModel();
        tbZusatz = new AKJTable(tbMdlZusatz);
        tbZusatz.addMouseListener(new AKTableDoubleClickMouseListener(this));
        tbZusatz.addKeyListener(getTableListener());
        tbZusatz.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbZusatz.setAutoResizeMode(AKJTable.AUTO_RESIZE_OFF);
        tbZusatz.fitTable(new int[] { 130, 80, 150, 130 });

        AKJScrollPane spZusatz = new AKJScrollPane(tbZusatz);
        spZusatz.setPreferredSize(new Dimension(530, 120));

        this.setLayout(new GridBagLayout());
        this.add(lblZusatz, GBCFactory.createGBC(0, 0, 1, 0, 2, 1, GridBagConstraints.HORIZONTAL));
        this.add(btnPanel, GBCFactory.createGBC(0, 100, 0, 1, 1, 2, GridBagConstraints.VERTICAL));
        this.add(spZusatz, GBCFactory.createGBC(100, 100, 1, 1, 1, 1, GridBagConstraints.BOTH));
        this.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 3, 2, 1, 1, GridBagConstraints.NONE));

        manageGUI(btnAdd, btnDelete);
    }

    @Override
    public void showDetails(Object details) {
        if (details instanceof BAVerlaufConfig) {
            baVerlConf = (BAVerlaufConfig) details;

            // BAVerlaufZusatz-Objekte laden
            try {
                setWaitCursor();

                BAConfigService service = getCCService(BAConfigService.class);
                List<BAVerlaufZusatz> result = service.findBAVerlaufZusaetze4BAVerlaufConfig(baVerlConf.getId(), null, null);
                tbMdlZusatz.setData(result);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(this, e);
            }
            finally {
                setDefaultCursor();
            }
        }
        else {
            tbMdlZusatz.removeAll();
        }
    }

    public void objectSelected(Object selection) {
        if (selection instanceof BAVerlaufZusatz) {
            BAVerlaufZusatzDialog dlg = new BAVerlaufZusatzDialog(
                    hvtGruppenList, abteilungenList, (BAVerlaufZusatz) selection);
            DialogHelper.showDialog(this, dlg, true, true);
            tbMdlZusatz.fireTableDataChanged();
        }
    }

    /* Oeffnet einen Dialog, um einen neuen BAVerlauf-Zusatz anzulegen. */
    private void addBAVerlaufZusatz() {
        if (baVerlConf != null) {
            BAVerlaufZusatzDialog dlg = new BAVerlaufZusatzDialog(
                    hvtGruppenList, abteilungenList, baVerlConf);
            Object result = DialogHelper.showDialog(this, dlg, true, true);
            if (result instanceof BAVerlaufZusatz) {
                tbMdlZusatz.addObject(result);
            }
        }
        else {
            String msg = "Bitte wählen Sie zuerst einen Bauauftrags-Verlauf aus.";
            MessageHelper.showInfoDialog(this, msg, null, true);
        }
    }

    /* Loescht den aktuell selektierten BAVerlauf-Zusatz. */
    private void deleteBAVerlaufZusatz() {
        try {
            AKMutableTableModel tm = (AKMutableTableModel) tbZusatz.getModel();
            Object selection = tm.getDataAtRow(tbZusatz.getSelectedRow());

            if (selection instanceof BAVerlaufZusatz) {
                String msg = "Soll der gewählte Datensatz wirklich gelöscht werden?";
                String title = "Löschen?";
                int result = MessageHelper.showConfirmDialog(
                        this, msg, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (result == JOptionPane.YES_OPTION) {
                    BAConfigService service = getCCService(BAConfigService.class);

                    // BAVerlauf-Zusatz loeschen
                    BAVerlaufZusatz toDelete = (BAVerlaufZusatz) selection;
                    service.deleteBAVerlaufZusatz(toDelete, HurricanSystemRegistry.instance().getSessionId());
                    tbMdlZusatz.removeObject(selection);
                }
            }
            else {
                String msg = "Bitte wählen Sie zuerst einen Datensatz, der gelöscht werden soll.";
                MessageHelper.showInfoDialog(this, msg, null, true);
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

    @Override
    public final void loadData() {
        try {
            hvtGruppen = new HashMap();
            abteilungen = new HashMap();

            HVTService hvtService = getCCService(HVTService.class);
            hvtGruppenList = hvtService.findHVTGruppen();
            // sort hvt groups by displayed value (ortsteil) NPE safe
            hvtGruppenList.sort((g1, g2) -> StringUtils.trimToEmpty(g1.getOrtsteil())
                    .compareTo(StringUtils.trimToEmpty(g2.getOrtsteil())));
            for (Iterator iter = hvtGruppenList.iterator(); iter.hasNext(); ) {
                HVTGruppe hvt = (HVTGruppe) iter.next();
                hvtGruppen.put(hvt.getId(), hvt);
            }

            NiederlassungService nlService = getCCService(NiederlassungService.class);
            abteilungenList = nlService.findAbteilungen4Ba();
            for (Iterator iter = abteilungenList.iterator(); iter.hasNext(); ) {
                Abteilung abt = (Abteilung) iter.next();
                abteilungen.put(abt.getId(), abt);
            }

            ReferenceService referenceService = getCCService(ReferenceService.class);
            List<Reference> standortTypes = referenceService.findReferencesByType(Reference.REF_TYPE_STANDORT_TYP, Boolean.FALSE);
            Map<Long, Reference> standortTypMap = new HashMap<>();
            CollectionMapConverter.convert2Map(standortTypes, standortTypMap, "getId", null);
            tbMdlZusatz.setStandortTypMap(standortTypMap);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public void createNew() {
    }

    @Override
    public void saveData() {
    }

    @Override
    protected void execute(String command) {
        if ("add".equals(command)) {
            addBAVerlaufZusatz();
        }
        else if ("delete".equals(command)) {
            deleteBAVerlaufZusatz();
        }
    }

    public void update(Observable o, Object arg) {
    }

    /**
     * TableModel fuer die Darstellung von BAVerlaufZusatz-Objekten.
     */
    class BAVZusatzTableModel extends AKTableModel {

        private static final int COL_HVT_GRUPPE = 0;
        private static final int COL_STANDORT_TYP = 1;
        private static final int COL_ABTEILUNG = 2;
        private static final int COL_AUCH_SM = 3;

        private static final int COL_COUNT = 4;

        private Map<Long, Reference> standortTypMap;

        public void setStandortTypMap(Map<Long, Reference> standortTypMap) {
            this.standortTypMap = standortTypMap;
        }

        @Override
        public int getColumnCount() {
            return COL_COUNT;
        }

        @Override
        public String getColumnName(int column) {
            switch (column) {
                case COL_HVT_GRUPPE:
                    return "HVT-Gruppe";
                case COL_ABTEILUNG:
                    return "Abteilung";
                case COL_AUCH_SM:
                    return "auch bei Selbstmontage";
                case COL_STANDORT_TYP:
                    return "Standorttyp";
                default:
                    return "";
            }
        }

        @Override
        public Object getValueAt(int row, int column) {
            Object o = getDataAtRow(row);
            if (o instanceof BAVerlaufZusatz) {
                BAVerlaufZusatz zusatz = (BAVerlaufZusatz) o;
                switch (column) {
                    case COL_HVT_GRUPPE:
                        return
                                ((hvtGruppen != null) && (hvtGruppen.get(zusatz.getHvtGruppeId()) instanceof HVTGruppe))
                                        ? ((HVTGruppe) hvtGruppen.get(zusatz.getHvtGruppeId())).getOrtsteil()
                                        : (zusatz.getHvtGruppeId() != null) ? zusatz.getHvtGruppeId().toString() : "";
                    case COL_ABTEILUNG:
                        return
                                ((abteilungen != null) && (abteilungen.get(zusatz.getAbtId()) instanceof Abteilung))
                                        ? ((Abteilung) abteilungen.get(zusatz.getAbtId())).getName()
                                        : (zusatz.getAbtId() != null) ? zusatz.getAbtId().toString() : "";
                    case COL_AUCH_SM:
                        return zusatz.getAuchSelbstmontage();
                    case COL_STANDORT_TYP:
                        if (zusatz.getStandortTypRefId() != null) {
                            return (standortTypMap.get(zusatz.getStandortTypRefId()) != null)
                                    ? standortTypMap.get(zusatz.getStandortTypRefId()).getStrValue()
                                    : null;
                        }
                        return null;
                    default:
                        break;
                }
            }
            return null;
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case COL_AUCH_SM:
                    return Boolean.class;
                case COL_STANDORT_TYP:
                    return String.class;
                default:
                    return super.getColumnClass(columnIndex);
            }
        }
    }
}
