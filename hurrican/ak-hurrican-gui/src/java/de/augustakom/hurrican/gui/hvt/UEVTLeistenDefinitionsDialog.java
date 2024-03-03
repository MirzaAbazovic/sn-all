/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.04.2005 12:20:19
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Map.*;
import java.util.function.*;
import javax.swing.*;
import javax.swing.border.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.IconHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.ResourceReader;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Rangierung.Freigegeben;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.service.cc.HVTToolService;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * Dialog zur Definition des Physik-Typs von Stiften auf einer best. UEVT-Leiste.
 *
 *
 */
public class UEVTLeistenDefinitionsDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(UEVTLeistenDefinitionsDialog.class);
    private static final long serialVersionUID = -8807374907311684362L;

    private final ResourceReader resourceReader =
            new ResourceReader("de.augustakom.hurrican.gui.hvt.resources.Physiktypen");

    private Icon iconOccupied = null;

    private final Color colorFree = new Color(204, 255, 204);
    private final Color colorCuda2H = new Color(255, 204, 153);
    private final Color colorCuda4H = new Color(153, 204, 255);
    private final Color colorADSL = new Color(255, 153, 204);
    private final Color colorSDSL = new Color(204, 255, 255);
    private final Color colorULAF = new Color(252, 252, 110);

    private Long hvtIdStandort = null;
    private String uevt = null;
    private String leiste1 = null;

    private List<StiftLabel> stiftLabelList = null;
    private Map<String, AKJComboBox> stiftTypMap = null;
    private Map<String, AKJComboBox> uetvMap = null;

    /**
     * Konstruktor fuer den Dialog.
     *
     * @param hvtIdStandort ID des zug. HVT-Standorts
     * @param uevt          Bezeichnung des UEVTs
     * @param leiste1       Bezeichnung der Leiste, deren Stifte definiert werden sollen.
     */
    public UEVTLeistenDefinitionsDialog(Long hvtIdStandort, String uevt, String leiste1) {
        super("de/augustakom/hurrican/gui/hvt/resources/UEVTLeistenDefinitionsDialog.xml");
        this.hvtIdStandort = hvtIdStandort;
        this.uevt = uevt;
        this.leiste1 = leiste1;
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle("Hochbitratige CuDA's (ÜVT: " + uevt + " - Leiste: " + leiste1 + ")");
        configureButton(CMD_SAVE, "Speichern", "Speichert die Einstellungen", true, true);

        iconOccupied = new IconHelper().getIcon("de/augustakom/hurrican/gui/images/negative.gif");
        Icon iconEmpty = new IconHelper().getIcon("de/augustakom/hurrican/gui/images/positive.gif");

        stiftLabelList = new ArrayList<>();
        stiftTypMap = new TreeMap<>();
        uetvMap = new TreeMap<>();

        Dimension lblDim = new Dimension(22, 22);
        for (int i = 1; i <= 80; i++) {
            String name = (i < 10) ? "0" + i : "" + i;
            StiftLabel lbl = new StiftLabel(iconEmpty);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.getAccessibleContext().setAccessibleName(name);
            lbl.setPreferredSize(lblDim);
            lbl.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
            lbl.setBackground(getColor(null));

            stiftLabelList.add(lbl);
        }

        AKJPanel matrix = new AKJPanel(new GridLayout(11, 9, 0, 0));
        AKJPanel matrixTyp = new AKJPanel(new GridLayout(11, 1, 0, 0));
        AKJPanel matrixUETV = new AKJPanel(new GridLayout(11, 1, 0, 0));
        int count = -1;
        for (int row = 0; row < 11; row++) {
            for (int col = 0; col < 9; col++) {
                if (row == 0) {
                    AKJLabel lblCol = new AKJLabel();
                    lblCol.setText(((col > 0) && (col < 9)) ? "0" + col : "");
                    lblCol.setHorizontalAlignment(SwingConstants.CENTER);
                    matrix.add(lblCol);

                    if (col == 0) {
                        matrixTyp.add(new AKJLabel());
                        matrixUETV.add(new AKJLabel());
                    }
                }
                else {
                    String rowName = (row < 10) ? "0" + row : "" + row;
                    if (col == 0) {
                        AKJLabel lblRow = new AKJLabel(rowName);
                        lblRow.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
                        lblRow.setOpaque(true);
                        matrix.add(lblRow);

                        AKJComboBox cbTyp = getSwingFactory().createComboBox("physik");
                        cbTyp.getAccessibleContext().setAccessibleName(rowName);
                        cbTyp.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
                        cbTyp.setPreferredSize(new Dimension(70, 22));

                        AKJComboBox cbUETV = getSwingFactory().createComboBox("uetv");
                        cbUETV.getAccessibleContext().setAccessibleName(rowName);
                        cbUETV.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
                        cbUETV.setPreferredSize(new Dimension(70, 22));

                        stiftTypMap.put(rowName, cbTyp);
                        uetvMap.put(rowName, cbUETV);
                        matrixTyp.add(cbTyp);
                        matrixUETV.add(cbUETV);
                    }
                    else {
                        StiftLabel sl = stiftLabelList.get(++count);
                        sl.setRowName(rowName);
                        matrix.add(sl);
                    }
                }
            }
        }

        getChildPanel().setLayout(new GridBagLayout());
        getChildPanel().add(matrix, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        getChildPanel().add(matrixTyp, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        getChildPanel().add(matrixUETV, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(100, 100, 3, 1, 1, 1, GridBagConstraints.BOTH));
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            setWaitCursor();
            HVTToolService hts = getCCService(HVTToolService.class);
            List<Equipment> equipments = hts.findEquipments(hvtIdStandort, uevt, leiste1);
            if (equipments != null) {
                for (Equipment eq : equipments) {
                    try {
                        int stift = Integer.parseInt(eq.getRangStift1());
                        StiftLabel lbl = stiftLabelList.get(stift - 1);
                        if (lbl != null) {
                            String physikText = StringUtils.substringBefore(eq.getRangSSType(), "-");

                            AKJComboBox cbTyp = stiftTypMap.get(lbl.getRowName());
                            cbTyp.selectItem("toString", String.class, physikText);

                            AKJComboBox cbUETV = uetvMap.get(lbl.getRowName());
                            cbUETV.selectItem(eq.getUetv());

                            lbl.setEquipment(eq);
                            lbl.setBackground(getColor(physikText));
                            if (StringUtils.isNotBlank(physikText)) {
                                lbl.setIcon(iconOccupied);
                                lbl.setBelegt(true);
                                cbTyp.setEnabled(false);
                                cbUETV.setEnabled(false);
                            }
                        }
                    }
                    catch (Exception e) {
                        LOGGER.warn(e.getMessage());
                    }
                }

                boolean disableSave = true;
                Iterator<Entry<String, AKJComboBox>> cbTypIt = stiftTypMap.entrySet().iterator();
                while (cbTypIt.hasNext()) {
                    Entry<String, AKJComboBox> entry = cbTypIt.next();
                    AKJComboBox cb = entry.getValue();
                    if (cb.isEnabled()) {
                        disableSave = false;
                        break;
                    }
                }

                if (disableSave) {
                    getButton(CMD_SAVE).setEnabled(false);
                }
            }
            else {
                throw new HurricanGUIException("Es konnten keine Equipments ermittelt werden!");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            setDefaultCursor();
        }
    }

    /*
     * Ermittelt eine Hintergrundfarbe fuer die Labels.
     */
    private Color getColor(String physik) {
        if ("2H".equals(physik)) {
            return colorCuda2H;
        }
        else if ("4H".equals(physik)) {
            return colorCuda4H;
        }
        else if ("ADSL".equals(physik)) {
            return colorADSL;
        }
        else if ("SDSL".equals(physik)) {
            return colorSDSL;
        }
        else if ("ULAF".equals(physik)) {
            return colorULAF;
        }

        return colorFree;
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        try {
            HVTTechnikSelectionDialog dlg = new HVTTechnikSelectionDialog();
            Object result = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
            if (result instanceof HVTTechnik) {
                physikVergeben(((HVTTechnik) result).getId());
            }

            prepare4Close();
            setValue(OK_OPTION);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /*
     * Vergibt den Stiften die ausgewaehlte Physik.
     */
    private void physikVergeben(Long hvtTechnik) throws HurricanGUIException {
        Iterator<String> it = stiftTypMap.keySet().iterator();
        while (it.hasNext()) {
            Object next = it.next();
            if (next instanceof String) {
                String row = (String) next;
                AKJComboBox cbTyp = stiftTypMap.get(row);
                AKJComboBox cbUETV = uetvMap.get(row);
                Uebertragungsverfahren uetv = (Uebertragungsverfahren) cbUETV.getSelectedItem();
                if (cbTyp.isEnabled() && (cbTyp.getSelectedIndex() > 0)) {
                    boolean use4H = false;
                    String rangSSType = cbTyp.getSelectedItem().toString();
                    if (Equipment.RANG_SS_4H.equals(rangSSType)) {
                        String nextRow = it.next();
                        AKJComboBox nextCBTyp = stiftTypMap.get(nextRow);
                        if (!nextCBTyp.isEnabled() || !Equipment.RANG_SS_4H.equals(nextCBTyp.getSelectedItem().toString())) {
                            throw new HurricanGUIException("Die 4-Draht Leitungen muessen in Reihenfolge sein!");
                        }
                        else {
                            use4H = true;
                            // bei 4-Draht muss wird dem zweiten Stift ein anderer Physik-Typ zugeordnet
                            assignPhysiktyp2Row(nextRow, Equipment.RANG_SS_4H_2DA, uetv, false, false, hvtTechnik);
                        }
                    }

                    assignPhysiktyp2Row(row, rangSSType, uetv, use4H, true, hvtTechnik);
                }
            }
        }
    }

    /* Ordnet den Stiften einer best. Zeile eine Physik zu und erstellt
     * eine Rangierung falls notwendig.
     * @param row Bezeichnung der Zeile, deren Stifte einer Physik zugeordnet werden sollen
     * @param rangSSType Bezeichnung des Physik-Typs fuer die Stifte
     * @param setStift2 Flag, ob ein zweiter Stift verwendet werden muss (bei erster Zeile von 4-Draht)
     * @param createRangierung Flag, ob eine Rangierung fuer die Stifte erstellt werden soll
     * @param hvtTechnik ID der HVT-Technik auf die die Stifte rangiert werden.
     */
    private void assignPhysiktyp2Row(String row, String rangSSType, Uebertragungsverfahren uetv, boolean setStift2,
            boolean createRangierung, Long hvtTechnik) {
        try {
            RangierungsService rs = getCCService(RangierungsService.class);

            // Equipments der Zeile ermitteln und Daten eintragen
            StiftLabelPredicate predicate = new StiftLabelPredicate();
            predicate.setRowName(row);
            Collection<StiftLabel> eqLbls = CollectionTools.select(stiftLabelList, predicate);
            if ((eqLbls != null) && (!eqLbls.isEmpty())) {
                Iterator<StiftLabel> lblIt = eqLbls.iterator();
                while (lblIt.hasNext()) {
                    StiftLabel sl = lblIt.next();
                    Equipment equipment = sl.getEquipment();
                    equipment.setRangSSType(getRangSSType(rangSSType));
                    equipment.setUetv(uetv);
                    equipment.setStatus(EqStatus.rang);
                    equipment.setUserW(HurricanSystemRegistry.instance().getCurrentUserName());
                    equipment.setDateW(new Date());
                    if (setStift2) {
                        Integer stift2Int = Integer.valueOf(equipment.getRangStift1());
                        stift2Int = NumberTools.add(stift2Int, 8);
                        String stift2 = (stift2Int < 10) ? "0" + stift2Int : "" + stift2Int;
                        equipment.setRangStift2(stift2);
                        equipment.setRangLeiste2(equipment.getRangLeiste1());
                    }
                    else {
                        equipment.setRangStift2(null);
                        equipment.setRangLeiste2(null);
                    }

                    // Aktualisiere Gueltigkeit, falls noch nicht gesetzt
                    if (equipment.getGueltigVon() == null) {
                        equipment.setGueltigVon(new Date());
                    }
                    if (equipment.getGueltigBis() == null) {
                        equipment.setGueltigBis(DateTools.getHurricanEndDate());
                    }

                    LOGGER.info("update Equipment " + equipment.getId());
                    rs.saveEquipment(equipment);

                    if (createRangierung) {
                        // Rangierung erzeugen
                        Rangierung rangierung = new Rangierung();
                        rangierung.setEqOutId(equipment.getId());
                        rangierung.setHvtIdStandort(hvtIdStandort);
                        rangierung.setPhysikTypId(getPhysiktypId(hvtTechnik, rangSSType));
                        rangierung.setFreigegeben(Freigegeben.freigegeben);
                        rangierung.setUserW(HurricanSystemRegistry.instance().getCurrentUserName());
                        rangierung.setDateW(new Date());
                        LOGGER.info("create Rangierung for Equipment " + equipment.getId());
                        rs.saveRangierung(rangierung, false);
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /* Gibt die Physiktyp-ID in Abhaengigkeit von der HVT-Technik zurueck.
     * @param hvtTechnik
     * @param physik
     * @return
     */
    private Long getPhysiktypId(Long hvtTechnik, String physik) {
        Long physikTyp = null;
        if (HVTTechnik.HUAWEI.equals(hvtTechnik)) {
            if (StringUtils.equals(physik, "ADSL")) {
                physikTyp = PhysikTyp.PHYSIKTYP_ADSL_DA_HUAWEI;
            }
            else if (StringUtils.equals(physik, "SDSL")) {
                physikTyp = PhysikTyp.PHYSIKTYP_SDSL_DA_HUAWEI;
            }
        }
        else {
            if (StringUtils.equals(physik, "2H")) {
                physikTyp = PhysikTyp.PHYSIKTYP_2H;
            }
            else if (StringUtils.equals(physik, "4H")) {
                physikTyp = PhysikTyp.PHYSIKTYP_4H;
            }
            else if (StringUtils.equals(physik, "ADSL")) {
                physikTyp = PhysikTyp.PHYSIKTYP_ADSL_DA;
            }
            else if (StringUtils.equals(physik, "SDSL")) {
                physikTyp = PhysikTyp.PHYSIKTYP_SDSL_DA;
            }
        }

        return physikTyp;
    }

    /*
     * Gibt abhaengig von <code>name</code> die Bezeichnung fuer einen Rangierungs-Typ (RangSSType) zurueck.
     */
    private String getRangSSType(String name) {
        return resourceReader.getValue(name);
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

    /*
     * Label zur Darstellung der Stifte.
     */
    static class StiftLabel extends AKJLabel {
        private static final long serialVersionUID = 977546649786459339L;
        private String rowName = null;
        private Equipment equipment = null;
        private boolean selected = false;
        private boolean belegt = false;

        /**
         * @param image
         */
        public StiftLabel(Icon image) {
            super(image);
            setOpaque(true);
        }

        /**
         * @return Returns the rowName.
         */
        public String getRowName() {
            return rowName;
        }

        /**
         * @param rowName The rowName to set.
         */
        public void setRowName(String rowName) {
            this.rowName = rowName;
        }

        /**
         * Gibt das Flag 'selected' zurueck.
         */
        public boolean getSelected() {
            return selected;
        }

        /**
         * Schaltet das Flag selected um.
         */
        public void changeSelection() {
            selected = !selected;
        }

        /**
         * @return Returns the belegt.
         */
        public boolean isBelegt() {
            return belegt;
        }

        /**
         * @param belegt The belegt to set.
         */
        public void setBelegt(boolean belegt) {
            this.belegt = belegt;
        }

        /**
         * @return Returns the equipment.
         */
        public Equipment getEquipment() {
            return equipment;
        }

        /**
         * @param equipment The equipment to set.
         */
        public void setEquipment(Equipment equipment) {
            this.equipment = equipment;
        }
    }

    /* Predicate, um nach allen Labels einer best. Reihe zu filtern. */
    static class StiftLabelPredicate implements Predicate<StiftLabel> {

        private String rowName = null;

        /**
         * Setzt den Zeilen-Namen, nach dem gefiltert werden soll.
         */
        public void setRowName(String rowName) {
            this.rowName = rowName;
        }

        @Override
        public boolean test(StiftLabel lbl) {
            return StringUtils.equals(rowName, lbl.getRowName());
        }
    }
}


