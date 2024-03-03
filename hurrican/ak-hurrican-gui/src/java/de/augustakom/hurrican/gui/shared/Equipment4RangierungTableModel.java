/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.07.2005 10:53:20
 */
package de.augustakom.hurrican.gui.shared;

import java.util.*;
import javax.annotation.*;
import javax.swing.*;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.IconHelper;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.model.cc.hardware.HWDlu;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.utils.CCServiceFinder;


/**
 * TableModel fuer die Darstellung der Equipment-Details zu einer Rangierung.
 *
 *
 */
public class Equipment4RangierungTableModel extends AKTableModel<Rangierung> {

    private static final Logger LOGGER = Logger.getLogger(Equipment4RangierungTableModel.class);

    private static final Icon ICON_WARNING = new IconHelper().getIcon("de/augustakom/hurrican/gui/images/warning.gif");

    private static final String UNKNOWN = "?";
    private static final String RANGIERUNG_FREE = "frei";
    private static final String RANGIERUNG_FREIGABEBEREIT = "freigabebereit";
    private static final String RANGIERUNG_ACTIVE = "aktiv";
    private static final String RANGIERUNG_NOT_ACTIVE = "nicht aktiv";
    private static final String RANGIERUNG_DIFFERENT_AUFTRAG = "auf anderem Auftrag";
    private static final String RANGIERUNG_HISTORISIERT = "historisiert";

    private static final int COL_DESC = 0;
    private static final int COL_EQ_IN = 1;
    private static final int COL_EQ_OUT = 2;
    private static final int COL_EQ_IN2 = 3;
    private static final int COL_EQ_OUT2 = 4;
    private static final int COL_COUNT = 5;

    private static final int ROW_ACTIVE = 0;
    private static final int ROW_VERTEILER = 1;
    private static final int ROW_BUCHT = 2;
    private static final int ROW_REIHE = 3;
    private static final int ROW_LEISTE_STIFT_1 = 4;
    private static final int ROW_LEISTE_STIFT_2 = 5;
    private static final int ROW_KVZ_DA = 6;
    private static final int ROW_HW_EQN = 7;
    private static final int ROW_SWITCH = 8;
    private static final int ROW_V5_PORT = 9;
    private static final int ROW_PHYSIKTYP = 10;
    private static final int ROW_UETV = 11;
    private static final int ROW_BEZEICH = 12;
    private static final int ROW_MANAGEMENT_NAME = 13;
    private static final int ROW_TYP = 14;
    private static final int ROW_LAYER2 = 15;
    private static final int ROW_ONT_ID = 16;
    private static final int ROW_ACCESS_CONTROLLER = 17;
    private static final int ROW_GUELTIG_VON = 18;
    private static final int ROW_GUELTIG_BIS = 19;
    private static final int ROW_RANGIER_ID = 20;

    private static final int ROW_COUNT = 21;

    private AKJTable table = null;
    private Endstelle endstelle = null;
    private Rangierung rangierung = null;
    private Rangierung rangierungAdd = null;
    private Map<Long, PhysikTyp> physiktypMap = null;
    private HWRack eqInRack;
    private HWRack eqOutRack;
    private HWRack eqIn2Rack;
    private HWRack eqOut2Rack;
    private HWBaugruppenTyp eqInBaugruppenTyp;
    private HWBaugruppenTyp eqOutBaugruppenTyp;
    private HWBaugruppenTyp eqIn2BaugruppenTyp;
    private HWBaugruppenTyp eqOut2BaugruppenTyp;
    private final Map<HWRack, String> herstellerByRack = Maps.newHashMap();

    private void init() {
        try {
            PhysikService ps = CCServiceFinder.instance().getCCService(PhysikService.class);
            List<PhysikTyp> physiktypen = ps.findPhysikTypen();
            physiktypMap = new HashMap<>();
            CollectionMapConverter.convert2Map(physiktypen, physiktypMap, "getId", null);

            eqInRack = (rangierung != null) ? getHWRack(rangierung.getEquipmentIn()) : null;
            eqOutRack = (rangierung != null) ? getHWRack(rangierung.getEquipmentOut()) : null;
            eqIn2Rack = (rangierungAdd != null) ? getHWRack(rangierungAdd.getEquipmentIn()) : null;
            eqOut2Rack = (rangierungAdd != null) ? getHWRack(rangierungAdd.getEquipmentOut()) : null;

            eqInBaugruppenTyp = (rangierung != null) ? getBaugruppenTyp(rangierung.getEquipmentIn()) : null;
            eqOutBaugruppenTyp = (rangierung != null) ? getBaugruppenTyp(rangierung.getEquipmentOut()) : null;
            eqIn2BaugruppenTyp = (rangierungAdd != null) ? getBaugruppenTyp(rangierungAdd.getEquipmentIn()) : null;
            eqOut2BaugruppenTyp = (rangierungAdd != null) ? getBaugruppenTyp(rangierungAdd.getEquipmentOut()) : null;

            herstellerByRack.put(eqInRack, getHersteller(getHvtTechnik(eqInRack)));
            herstellerByRack.put(eqOutRack, getHersteller(getHvtTechnik(eqOutRack)));
            herstellerByRack.put(eqIn2Rack, getHersteller(getHvtTechnik(eqIn2Rack)));
            herstellerByRack.put(eqOut2Rack, getHersteller(getHvtTechnik(eqOut2Rack)));
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Nullable
    private String getHersteller(@CheckForNull final HVTTechnik hvtTechnik) {
        return (hvtTechnik == null) ? null : hvtTechnik.getHersteller();
    }

    /**
     * @param endstelle The endstelle to set.
     */
    public void setEndstelle(Endstelle endstelle) {
        this.endstelle = endstelle;
        fireTableDataChanged();
    }

    /**
     * @param rangierung    The rangierung to set.
     * @param rangierungAdd Zusatz-Rangierung
     */
    public void setRangierung(Rangierung rangierung, Rangierung rangierungAdd) {
        this.rangierung = rangierung;
        this.rangierungAdd = rangierungAdd;
        init();
        fireTableDataChanged();
    }

    /**
     * @param table The table to set.
     */
    public void setTable(AKJTable table) {
        this.table = table;
    }

    /**
     * @see de.augustakom.common.gui.swing.table.AKMutableTableModel#removeAll()
     */
    @Override
    public void removeAll() {
        this.endstelle = null;
        this.rangierung = null;
        this.rangierungAdd = null;
        removeWarning();
        fireTableDataChanged();
    }

    /**
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    @Override
    public int getColumnCount() {
        return COL_COUNT;
    }

    /**
     * @see javax.swing.table.TableModel#getRowCount()
     */
    @Override
    public int getRowCount() {
        return ROW_COUNT;
    }

    /**
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    @Override
    public String getColumnName(int column) {
        switch (column) {
            case COL_EQ_IN:
                return "EQ-In";
            case COL_EQ_OUT:
                return "EQ-Out";
            case COL_EQ_IN2:
                return "EQ-In 2";
            case COL_EQ_OUT2:
                return "EQ-Out 2";
            default:
                return " ";
        }
    }

    /* Zeigt ein Image an, das auf ein Problem hindeutet. */
    private void showWarning() {
        table.getColumnModel().getColumn(0).setHeaderValue(ICON_WARNING);
        table.getTableHeader().repaint();
    }

    /* Entfernt den Warnhinweis. */
    private void removeWarning() {
        table.getColumnModel().getColumn(0).setHeaderValue(null);
        table.getTableHeader().repaint();
    }

    /**
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    @Override
    public Object getValueAt(int row, int column) {
        if (column == COL_DESC) {
            return getTitleValue(row);
        }
        else {
            return getColumnValue(row, column);
        }
    }

    /* Gibt den Zeilen-Titel zurueck. */
    private String getTitleValue(int row) {
        switch (row) {
            case ROW_ACTIVE:
                return "Aktiv:";
            case ROW_VERTEILER:
                return "Verteiler:";
            case ROW_BUCHT:
                return "Bucht:";
            case ROW_REIHE:
                return "Reihe:";
            case ROW_LEISTE_STIFT_1:
                return "Leiste - Stift 1:";
            case ROW_LEISTE_STIFT_2:
                return "Leiste - Stift 2:";
            case ROW_KVZ_DA:
                return "KVZ Doppelader";
            case ROW_HW_EQN:
                return "HW EQN:";
            case ROW_SWITCH:
                return "Switch-MG:";
            case ROW_PHYSIKTYP:
                return "Physik-Typ";
            case ROW_UETV:
                return "Übertragungsv.:";
            case ROW_GUELTIG_VON:
                return "Gültig von:";
            case ROW_GUELTIG_BIS:
                return "Gültig bis:";
            case ROW_RANGIER_ID:
                return "Rangier ID:";
            case ROW_BEZEICH:
                return "Gerätebezeichnung:";
            case ROW_MANAGEMENT_NAME:
                return "Management-Bez.:";
            case ROW_TYP:
                return "Port/Typ:";
            case ROW_LAYER2:
                return "Schicht2-Protokoll:";
            case ROW_ONT_ID:
                return "ONT-ID";
            case ROW_ACCESS_CONTROLLER:
                return "AccessController";
            case ROW_V5_PORT:
                return "V5-Port";
            default:
                return null;
        }
    }

    /* Ermittelt den darzustellenden Wert der Rangierungen. */
    private Object getColumnValue(int row, int column) {
        Rangierung rang = (column <= COL_EQ_OUT) ? rangierung : rangierungAdd;
        if (rang == null) {
            return "";
        }

        Equipment eq = getEquipmentForCol(column);
        if (eq != null) {
            HWRack hwRack = getRackForCol(column);
            HWBaugruppenTyp bgTyp = getBGTypForCol(column);
            switch (row) {
                case ROW_ACTIVE:
                    if (endstelle == null) {
                        if (rang.isRangierungFrei(false)) {
                            return RANGIERUNG_FREE;
                        }
                        else if (rang.isRangierungFrei(true)) {
                            return RANGIERUNG_FREIGABEBEREIT;
                        }
                        else {
                            return UNKNOWN;
                        }
                    }
                    else if (DateTools.isDateBefore(rang.getGueltigBis(), DateTools.getHurricanEndDate())) {
                        showWarning();
                        return RANGIERUNG_HISTORISIERT;
                    }
                    else if (NumberTools.equal(rang.getEsId(), endstelle.getId())) {
                        removeWarning();
                        return RANGIERUNG_ACTIVE;
                    }
                    else if ((rang.getEsId() == null) ||
                            (rang.getEsId().intValue() == Rangierung.RANGIERUNG_NOT_ACTIVE.intValue())) {
                        showWarning();
                        return RANGIERUNG_NOT_ACTIVE;
                    }
                    else {
                        showWarning();
                        return RANGIERUNG_DIFFERENT_AUFTRAG;
                    }
                case ROW_VERTEILER:
                    if (StringUtils.isNotBlank(eq.getKvzNummer())) {
                        return StringUtils.join(
                                new Object[] { eq.getRangVerteiler(), "  (KVZ: ", eq.getKvzNummer(), ")" });
                    }
                    return eq.getRangVerteiler();
                case ROW_BUCHT:
                    return eq.getRangBucht();
                case ROW_REIHE:
                    return eq.getRangReihe();
                case ROW_LEISTE_STIFT_1:
                    return StringTools.join(new String[] { eq.getRangLeiste1(), eq.getRangStift1() }, " - ", true);
                case ROW_LEISTE_STIFT_2:
                    return StringTools.join(new String[] { eq.getRangLeiste2(), eq.getRangStift2() }, " - ", true);
                case ROW_KVZ_DA:
                    return eq.getKvzDoppelader();
                case ROW_HW_EQN:
                    return eq.getHwEQN();
                case ROW_SWITCH:
                    StringBuilder switchBuffer = new StringBuilder();
                    if (eq.getHwSwitch() != null) {
                        switchBuffer.append(eq.getHwSwitch().getName());
                    }

                    if ((hwRack instanceof HWDlu) && (((HWDlu) hwRack).getMediaGatewayName() != null)) {
                        switchBuffer.append("-").append(((HWDlu) hwRack).getMediaGatewayName());
                    }

                    return switchBuffer.toString();
                case ROW_PHYSIKTYP:
                    if ((physiktypMap != null) && (rang.getPhysikTypId() != null)) {
                        PhysikTyp pt = physiktypMap.get(rang.getPhysikTypId());
                        return (pt != null) ? pt.getName() : rang.getPhysikTypId();
                    }
                    return null;
                case ROW_UETV:
                    StringBuilder result = new StringBuilder();
                    if (eq.getUetv() != null) {
                        result.append(eq.getUetv());
                        if (eq.getRangSSType() != null) {
                            result.append(" (");
                            result.append(eq.getRangSSType());
                            result.append(")");
                        }
                    }
                    return result.toString();
                case ROW_GUELTIG_VON:
                    return DateTools.formatDate(rang.getGueltigVon(), DateTools.PATTERN_DAY_MONTH_YEAR);
                case ROW_GUELTIG_BIS:
                    return DateTools.formatDate(rang.getGueltigBis(), DateTools.PATTERN_DAY_MONTH_YEAR);
                case ROW_RANGIER_ID:
                    return rang.getId();
                case ROW_TYP:
                    return eq.getRangSchnittstelle() != null ? eq.getRangSchnittstelle().name() : "";
                case ROW_LAYER2:
                    return eq.getSchicht2Protokoll();
                case ROW_BEZEICH:
                    if (eq.getHwBaugruppenId() != null) {
                        try {
                            String rackBez = (hwRack != null) ? hwRack.getGeraeteBez() : null;
                            String bgTypName = (bgTyp != null) ? bgTyp.getName() : null;
                            return StringTools.join(new String[] { rackBez, bgTypName, herstellerByRack.get(hwRack) },
                                    " - ", true);
                        }
                        catch (Exception e) {
                            LOGGER.warn(e);
                        }
                    }
                    return null;
                case ROW_MANAGEMENT_NAME:
                    return (hwRack != null) ? hwRack.getManagementBez() : null;
                case ROW_ONT_ID:
                    return rang.getOntId();
                case ROW_ACCESS_CONTROLLER:
                    return (hwRack instanceof HWDlu) ? ((HWDlu) hwRack).getAccessController() : null;
                case ROW_V5_PORT:
                    return eq.getV5Port();
                default:
                    break;
            }
        }
        return null;
    }

    /* Gibt das Equipment-Objekt fuer die entspr. Spalte zurueck. */
    private Equipment getEquipmentForCol(int column) {
        switch (column) {
            case COL_EQ_IN:
                return rangierung.getEquipmentIn();
            case COL_EQ_OUT:
                return rangierung.getEquipmentOut();
            case COL_EQ_IN2:
                return rangierungAdd != null ? rangierungAdd.getEquipmentIn() : null;
            case COL_EQ_OUT2:
                return rangierungAdd != null ? rangierungAdd.getEquipmentOut() : null;
            default:
                return null;
        }
    }

    /* Gibt das HW-Rack Modell fuer eine Spalte zurueck. */
    private HWRack getRackForCol(int column) {
        switch (column) {
            case COL_EQ_IN:
                return eqInRack;
            case COL_EQ_OUT:
                return eqOutRack;
            case COL_EQ_IN2:
                return eqIn2Rack;
            case COL_EQ_OUT2:
                return eqOut2Rack;
            default:
                return null;
        }
    }

    /* Gibt den Baugruppen-Typ fuer eine Spalte zurueck. */
    private HWBaugruppenTyp getBGTypForCol(int column) {
        switch (column) {
            case COL_EQ_IN:
                return eqInBaugruppenTyp;
            case COL_EQ_OUT:
                return eqOutBaugruppenTyp;
            case COL_EQ_IN2:
                return eqIn2BaugruppenTyp;
            case COL_EQ_OUT2:
                return eqOut2BaugruppenTyp;
            default:
                return null;
        }
    }

    /* Ermittelt das HW-Rack Modell fuer ein Equipment. */
    private HWRack getHWRack(Equipment equipment) {
        if ((equipment != null) && (equipment.getHwBaugruppenId() != null)) {
            try {
                HWService hardwareService = CCServiceFinder.instance().getCCService(HWService.class);
                return hardwareService.findRackForBaugruppe(equipment.getHwBaugruppenId());
            }
            catch (Exception e) {
                LOGGER.warn(e);
            }
        }
        return null;
    } /* Ermittelt das HW-Rack Modell fuer ein Equipment. */

    @CheckForNull
    private HVTTechnik getHvtTechnik(@CheckForNull HWRack hwRack) {
        if (hwRack != null) {
            try {
                return CCServiceFinder.instance().getCCService(HVTService.class).findHVTTechnik(hwRack.getHwProducer());
            }
            catch (Exception e) {
                LOGGER.warn(e);
            }
        }
        return null;
    }

    /* Ermittelt den Baugruppen-Typ fuer ein Equipment. */
    private HWBaugruppenTyp getBaugruppenTyp(Equipment equipment) {
        if ((equipment != null) && (equipment.getHwBaugruppenId() != null)) {
            try {
                HWService hardwareService = CCServiceFinder.instance().getCCService(HWService.class);
                HWBaugruppe bg = hardwareService.findBaugruppe(equipment.getHwBaugruppenId());
                if (bg != null) {
                    return bg.getHwBaugruppenTyp();
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * @see javax.swing.table.TableModel#isCellEditable(int, int)
     */
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

}
