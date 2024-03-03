/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.11.2005 11:30:53
 */
package de.augustakom.hurrican.gui.verlauf;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistung;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.utils.CCServiceFinder;


/**
 * TableModel fuer Objekte des Typs <code>VerlaufAction</code>.
 *
 *
 */
public class VerlaufActionTableModel extends AKTableModel<Auftrag2TechLeistung> {

    private static final Logger LOGGER = Logger.getLogger(VerlaufActionTableModel.class);

    private static final int COL_NAME = 0;
    private static final int COL_LS_TYP = 1;
    private static final int COL_ZUGANG = 2;
    private static final int COL_KUENDIGUNG = 3;

    private static final int COL_COUNT = 4;

    private Map<Long, TechLeistung> leistungKonfigs = null;
    private Long verlaufId = null;

    /**
     * Default-Const.
     */
    public VerlaufActionTableModel() {
        init();
    }

    /* Initialisiert das TableModel. */
    private void init() {
        try {
            // LeistungsKonfigurationen laden
            CCLeistungsService ls = CCServiceFinder.instance().getCCService(CCLeistungsService.class);
            List<TechLeistung> konfigs = ls.findTechLeistungen(true);
            Map<Long, TechLeistung> konfigMap = new HashMap<Long, TechLeistung>();
            CollectionMapConverter.convert2Map(konfigs, konfigMap, "getId", null);
            this.setLeistungKonfigs(konfigMap);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Uebergibt dem TableModel eine Map mit allen relevanten/konfigurierten Billing-Leistungen. <br> Als Keys in der
     * Map werden die Leistungs-IDs und als Value Objekte vom Typ <code>TechLeistung</code> erwartet.
     *
     * @param leistungKonfigs
     */
    protected void setLeistungKonfigs(Map<Long, TechLeistung> leistungKonfigs) {
        this.leistungKonfigs = leistungKonfigs;
    }

    public void setData(Long verlaufId, List<Auftrag2TechLeistung> data) {
        this.verlaufId = verlaufId;
        super.setData(data);
    }

    @Override
    public int getColumnCount() {
        return COL_COUNT;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case COL_NAME:
                return "Leistung";
            case COL_LS_TYP:
                return "Leistungstyp";
            case COL_ZUGANG:
                return "Zugang";
            case COL_KUENDIGUNG:
                return "Kuendigung";
            default:
                return null;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        Auftrag2TechLeistung atl = getDataAtRow(row);
        TechLeistung tl = getTechLeistung(atl.getTechLeistungId());

        switch (column) {
            case COL_NAME:
                return (tl != null) ? tl.getName() : null;
            case COL_LS_TYP:
                return (tl != null) ? tl.getTyp() : null;
            case COL_ZUGANG:
                return NumberTools.equal(atl.getVerlaufIdReal(), verlaufId);
            case COL_KUENDIGUNG:
                return NumberTools.equal(atl.getVerlaufIdKuend(), verlaufId);
            default:
                return null;
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case COL_ZUGANG:
                return Boolean.class;
            case COL_KUENDIGUNG:
                return Boolean.class;
            default:
                return String.class;
        }
    }

    /*
     * Ermittelt die technische Leistung zu der angegebenen ID.
     */
    private TechLeistung getTechLeistung(Long techLeistungId) {
        if (leistungKonfigs != null) {
            return leistungKonfigs.get(techLeistungId);
        }
        return null;
    }
}


