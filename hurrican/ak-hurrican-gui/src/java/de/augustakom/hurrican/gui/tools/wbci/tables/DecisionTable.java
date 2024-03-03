/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.10.13
 */
package de.augustakom.hurrican.gui.tools.wbci.tables;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import de.augustakom.common.gui.swing.AKAbstractAction;
import de.mnet.wbci.model.DecisionAttribute;
import de.mnet.wbci.model.DecisionResult;
import de.mnet.wbci.model.DecisionVO;
import de.mnet.wbci.model.DecisionVOHelper;
import de.mnet.wbci.model.MeldungsCode;

/**
 * Table used to display all decision attributes for deciding whether a Vorabstimmung is accepted with RUEMVA or
 * declined with ABBM. List of MeldungsCodes should be decided here. Table compares Vorabstimmungs data with M-net
 * database data.
 * <p/>
 * Each row represents a decision attribute.
 *
 *
 */
public class DecisionTable extends AbstractWbciTable {

    private static final long serialVersionUID = 7992678763607431673L;

    public DecisionTable(DecisionTableModel tableModel) {
        super(tableModel, JTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION, false);

        fitTable(tableModel.getColumnDimensions());
        getPopupMouseListener().setShowCopyActions(false);

        addPopupAction(new OverwriteDecisionAction(DecisionResult.OK, MeldungsCode.ZWA));
        addPopupAction(new OverwriteDecisionAction(DecisionResult.ABWEICHEND, MeldungsCode.NAT,
                DecisionAttribute.KUNDENWUNSCHTERMIN));
        addPopupSeparator();
        addPopupAction(new OverwriteDecisionAction(DecisionResult.NICHT_OK, MeldungsCode.AIF,
                DecisionAttribute.FIRMEN_NAME, DecisionAttribute.NACHNAME));
        addPopupAction(new OverwriteDecisionAction(DecisionResult.NICHT_OK, MeldungsCode.AIFVN,
                DecisionAttribute.FIRMENZUSATZ, DecisionAttribute.VORNAME));
        addPopupSeparator();
        addPopupAction(new OverwriteDecisionAction(DecisionResult.NICHT_OK, MeldungsCode.WAI,
                DecisionAttribute.NACHNAME_WAI, DecisionAttribute.FIRMEN_NAME_WAI, DecisionAttribute.VORNAME_WAI,
                DecisionAttribute.FIRMENZUSATZ_WAI));
        addPopupSeparator();
        addPopupAction(new OverwriteDecisionAction(DecisionResult.ABWEICHEND, MeldungsCode.ADAPLZ,
                DecisionAttribute.PLZ));
        addPopupAction(new OverwriteDecisionAction(DecisionResult.ABWEICHEND, MeldungsCode.ADAORT,
                DecisionAttribute.ORT));
        addPopupAction(new OverwriteDecisionAction(DecisionResult.ABWEICHEND, MeldungsCode.ADASTR,
                DecisionAttribute.STRASSENNAME));
        addPopupAction(new OverwriteDecisionAction(DecisionResult.ABWEICHEND, MeldungsCode.ADAHSNR,
                DecisionAttribute.HAUSNUMMER));
        addPopupSeparator();
        addPopupAction(new OverwriteDecisionAction(DecisionResult.NICHT_OK, MeldungsCode.ADFPLZ, DecisionAttribute.PLZ));
        addPopupAction(new OverwriteDecisionAction(DecisionResult.NICHT_OK, MeldungsCode.ADFORT, DecisionAttribute.ORT));
        addPopupAction(new OverwriteDecisionAction(DecisionResult.NICHT_OK, MeldungsCode.ADFSTR,
                DecisionAttribute.STRASSENNAME));
        addPopupAction(new OverwriteDecisionAction(DecisionResult.NICHT_OK, MeldungsCode.ADFHSNR,
                DecisionAttribute.HAUSNUMMER));
        addPopupSeparator();
        addPopupAction(new OverwriteDecisionAction(DecisionResult.NICHT_OK, MeldungsCode.RNG,
                DecisionAttribute.RUFNUMMER, DecisionAttribute.RUFNUMMERN_BLOCK));
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component component = super.prepareRenderer(renderer, row, column);

        DecisionVO modelData = getModel().getDataAtRow(row);

        if (DecisionTableModel.DecisionTableMetaData.SUGGESTED_RESULT.colNumber == column) {
            setBackgroundColor(component, modelData.getSuggestedResult());
        }
        else if (DecisionTableModel.DecisionTableMetaData.FINAL_RESULT.colNumber == column) {
            setBackgroundColor(component, modelData.getFinalResult());
        }

        if (column == 0) {
            component.setForeground(BG_COLOR_BLACK);
            component.setBackground(BG_COLOR_GREY);
        }

        return component;
    }

    private void setBackgroundColor(Component component, DecisionResult decisionResult) {
        if (DecisionResult.NICHT_OK.equals(decisionResult)) {
            component.setBackground(BG_COLOR_RED);
        }
        else if (DecisionResult.OK.equals(decisionResult)) {
            component.setBackground(BG_COLOR_GREEN);
        }
        else if (DecisionResult.ABWEICHEND.equals(decisionResult)) {
            component.setBackground(BG_COLOR_YELLOW);
        }
        else if (DecisionResult.MANUELL.equals(decisionResult)) {
            component.setBackground(BG_COLOR_ORANGE);
        }
    }

    @Override
    public DecisionTableModel getModel() {
        return (DecisionTableModel) super.getModel();
    }

    /* Action, overwrites decision with final result and meldungs code */
    private class OverwriteDecisionAction extends AKAbstractAction {

        private static final long serialVersionUID = -327984511099113535L;
        private final Set<DecisionAttribute> decisionAttributes;
        private final DecisionResult finalResult;
        private final MeldungsCode finalCode;

        public OverwriteDecisionAction(DecisionResult finalResult, MeldungsCode finalCode,
                DecisionAttribute... decisionAttributes) {
            super();

            this.decisionAttributes = new HashSet<>();
            if (decisionAttributes != null) {
                Collections.addAll(this.decisionAttributes, decisionAttributes);
            }
            this.finalResult = finalResult;
            this.finalCode = finalCode;

            setName(finalResult.name() + " - " + finalCode.name() + " (" + finalCode.getStandardText() + ")");
            setTooltip(finalCode.getStandardText());
            setActionCommand("overwrite.decision");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            DecisionVO decisionVO = (DecisionVO) getValue(OBJECT_4_ACTION);
            decisionVO.setFinalMeldungsCode(finalCode);
            DecisionVOHelper.updateFinalResult(decisionVO, finalResult);

            getModel().fireTableDataChanged();
        }

        @Override
        public void putValue(String key, Object newValue) {
            super.putValue(key, newValue);

            if (key.equals(OBJECT_4_ACTION)) {
                DecisionVO decisionVO = (DecisionVO) newValue;

                boolean enable = true;
                if (!decisionAttributes.isEmpty() && !decisionAttributes.contains(decisionVO.getAttribute())) {
                    enable = false;
                }

                if (enable && MeldungsCode.RNG == decisionVO.getSuggestedMeldungsCode()) {
                    enable = false;
                }

                if (enable && finalCode.equals(decisionVO.getFinalMeldungsCode())) {
                    enable = false;
                }

                if (enable
                        &&
                        (DecisionAttribute.RUFNUMMER.equals(decisionVO.getAttribute()) || DecisionAttribute.RUFNUMMERN_BLOCK
                                .equals(decisionVO.getAttribute()))
                        && DecisionResult.INFO.equals(decisionVO.getSuggestedResult())) {
                    enable = false;
                }

                if (enable && DecisionAttribute.PORTIERUNGSZEITFENSTER.equals(decisionVO.getAttribute())) {
                    enable = false;
                }

                setEnabled(enable);
            }
        }
    }

}
