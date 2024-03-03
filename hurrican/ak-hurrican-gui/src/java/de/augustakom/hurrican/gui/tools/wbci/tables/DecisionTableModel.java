/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.10.13
 */
package de.augustakom.hurrican.gui.tools.wbci.tables;

import de.augustakom.common.gui.swing.table.AKReferenceAwareTableModel;
import de.mnet.wbci.model.DecisionAttribute;
import de.mnet.wbci.model.DecisionVO;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.MeldungsCode;

/**
 *
 */
public class DecisionTableModel extends AKReferenceAwareTableModel<DecisionVO> {

    private static final long serialVersionUID = -789956700835668608L;
    private final GeschaeftsfallTyp typ;


    public DecisionTableModel(GeschaeftsfallTyp typ) {
        super();
        this.typ = typ;
    }

    public int[] getColumnDimensions() {
        return new int[] { 110, 150, 150, 120, 120, 120, 120 };
    }

    @Override
    public int getColumnCount() {
        return DecisionTableMetaData.COUNT_COL.colNumber;
    }

    @Override
    public Object getValueAt(int row, int column) {
        DecisionVO vo = getDataAtRow(row);
        if (vo != null) {
            if (DecisionAttribute.KUNDENWUNSCHTERMIN.equals(vo.getAttribute()) && column == 1 && !GeschaeftsfallTyp.VA_RRNP.equals(typ)) {
                return vo.getControlObject();
            }
            return getValueForColumn(column, vo);
        }
        return super.getValueAt(row, column);
    }

    @Override
    public String getColumnName(int column) {
        return DecisionTableMetaData.forColumn(column).colName;
    }

    protected String getValueForColumn(int column, DecisionVO decisionVO) {
        // if both Meldungscodes are ZWA leave it blank
        boolean bothMeldeCodesZWA = MeldungsCode.ZWA.equals(decisionVO.getSuggestedMeldungsCode())
                && MeldungsCode.ZWA.equals(decisionVO.getFinalMeldungsCode());

        switch (DecisionTableMetaData.forColumn(column)) {
            case ATTRIBUTE:
                return decisionVO.getName();
            case CONTROL_VALUE:
                return decisionVO.getControlValue();
            case PROPERTY_VALUE:
                return decisionVO.getPropertyValue();
            case SUGGESTED_RESULT:
                return decisionVO.getSuggestedResult().name();
            case FINAL_RESULT:
                return decisionVO.getFinalResult().name();
            case SUGGESTED_CODE:
                MeldungsCode suggestedMC = decisionVO.getSuggestedMeldungsCode();
                if (suggestedMC == null || (MeldungsCode.ZWA.equals(suggestedMC) && !showMeldungsCodeZWA(decisionVO.getAttribute()))) {
                    return "";
                }
                else {
                    return suggestedMC.name();
                }
            case FINAL_CODE:
                MeldungsCode finalMC = decisionVO.getFinalMeldungsCode();
                if (finalMC == null || bothMeldeCodesZWA || (MeldungsCode.ZWA.equals(finalMC) && !showMeldungsCodeZWA(decisionVO.getAttribute()))) {
                    return "";
                }
                else {
                    return finalMC.name();
                }
            default:
                return null;
        }
    }

    /**
     * Checks if meldungsCode ZWA should be displayed for attribute.
     *
     * @param attribute
     * @return
     */
    private boolean showMeldungsCodeZWA(DecisionAttribute attribute) {
        return attribute.equals(DecisionAttribute.KUNDENWUNSCHTERMIN);
    }

    /**
     * Represents the matching between column number and column name.
     */
    protected enum DecisionTableMetaData {
        ATTRIBUTE(0, "Eigenschaft"),
        CONTROL_VALUE(1, "M-net Bestand"),
        PROPERTY_VALUE(2, "Inhalt Vorabstimmung"),
        SUGGESTED_RESULT(3, "Prüfergebnis System"),
        FINAL_RESULT(4, "Entscheidung User"),
        SUGGESTED_CODE(5, "MeldeCode System"),
        FINAL_CODE(6, "MeldeCode User"),
        COUNT_COL(7, null);
        protected final int colNumber;
        protected final String colName;

        DecisionTableMetaData(int colNumber, String colName) {
            this.colNumber = colNumber;
            this.colName = colName;
        }

        public static DecisionTableMetaData forColumn(int column) {
            if (column <= 7) {
                for (DecisionTableMetaData data : DecisionTableMetaData.values()) {
                    if (data.colNumber == column) {
                        return data;
                    }
                }
            }
            return null;
        }
    }
}
