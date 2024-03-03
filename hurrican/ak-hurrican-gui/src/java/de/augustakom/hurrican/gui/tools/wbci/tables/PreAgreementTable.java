/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.07.2007 09:47:15
 */
package de.augustakom.hurrican.gui.tools.wbci.tables;

import static de.augustakom.hurrican.gui.tools.wbci.tables.PreAgreementTableModel.ColumnMetaData.*;
import static de.mnet.wbci.converter.MeldungsCodeConverter.*;
import static de.mnet.wbci.model.MeldungsCode.*;
import static de.mnet.wbci.model.Severity.*;
import static de.mnet.wbci.model.WbciRequestStatus.*;

import java.awt.*;
import java.util.List;
import javax.swing.table.*;

import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.mnet.wbci.model.CarrierRole;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.PreAgreementType;
import de.mnet.wbci.model.PreAgreementVO;
import de.mnet.wbci.model.Severity;
import de.mnet.wbci.model.WbciRequestStatus;

/**
 * AKJTable-Implementierung fuer Tables, die Objekte vom Typ {@link de.augustakom.hurrican.gui.tools.wbci.tables.PreAgreementTableModel}
 * darzustellen. <br> Je nach Status des Vorgangs wird die Hintergrundfarbe der Zeile anders dargestellt.
 */
public class PreAgreementTable extends AbstractWbciTable {

    private static final long serialVersionUID = -4149384581996683848L;
    private MeldungsCode[] meldungsCodesLightGreen;

    public PreAgreementTable() {
        super();
        initMeldungsCodeColors();
    }

    public PreAgreementTable(PreAgreementTableModel dm, int autoResizeMode, int selectionMode) {
        super(dm, autoResizeMode, selectionMode);
        initMeldungsCodeColors();
    }

    private void initMeldungsCodeColors() {
        List<MeldungsCode> adaCodes = getADACodes();
        meldungsCodesLightGreen = adaCodes.toArray(new MeldungsCode[adaCodes.size() + 1]);
        meldungsCodesLightGreen[meldungsCodesLightGreen.length - 1] = NAT;
    }

    /**
     * This method is always invoked by JTable.<br> Normally each alternating row in the table is displayed with a
     * different background color. This method overwrites the default behavior by setting the background color,
     * according to the {@link de.mnet.wbci.model.WbciRequestStatus}. Each request status has a severity which is mapped
     * to a bg color as follows: <ul> <li>&lt; 9  =>  {@link #BG_COLOR_GREEN}</li> <li>10 - 19  =>  {@link
     * #BG_COLOR_ORANGE}</li> <li>&gt; 20   =>  {@link #BG_COLOR_RED}</li> </ul> If no severity is set for the status
     * the default bg color is not overwritten.
     *
     * @see javax.swing.JTable#prepareRenderer(javax.swing.table.TableCellRenderer, int, int)
     */
    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component comp = super.prepareRenderer(renderer, row, column);
        if ((getModel() instanceof AKMutableTableModel) && !isRowSelected(row)) {
            Object data = ((AKMutableTableModel<?>) getModel()).getDataAtRow(row);
            if (data instanceof PreAgreementVO) {
                final PreAgreementVO preAgreementVO = (PreAgreementVO) data;
                if (COL_STATUS.colNumber == column) {
                    setBackgroundColor(preAgreementVO, comp);
                }
                else if (preAgreementVO.isAutomationErrors()) {
                    comp.setBackground(BG_COLOR_GREY);
                    comp.setForeground(Color.RED);
                }
                else if (COL_PRE_AGREEMENT_TYPE.colNumber == column
                        && PreAgreementType.WS.equals(preAgreementVO.getPreAgreementType())) {
                    comp.setBackground(BG_COLOR_LIGHT_GREEN);
                }
            }
        }
        return comp;
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        if (COL_STATUS.colNumber == column) {
            return new WbciRequestStatusTableCellRenderer();
        }
        return super.getCellRenderer(row, column);
    }

    protected void setBackgroundColor(PreAgreementVO preAgreementVO, Component comp) {
        if (preAgreementVO.getRequestStatus() != null) {
            Color bgColor = lookupBackgroundColor(preAgreementVO);
            if (bgColor != null) {
                comp.setBackground(bgColor);
            }
        }
    }

    protected Color lookupBackgroundColor(PreAgreementVO preAgreementVO) {
        CarrierRole carrierRole = CarrierRole.lookupMNetCarrierRoleByCarrierCode(preAgreementVO.getEkpAuf(), preAgreementVO.getEkpAbg());
        WbciRequestStatus requestStatus = preAgreementVO.getRequestStatus();
        String code = preAgreementVO.getMeldungCodes();
        Severity severity = requestStatus.getSeverity(carrierRole);

        // In the case of RUEM_VA_EMPFANGEN, the color is overwritten if certain meldungcodes are present
        if (RUEM_VA_EMPFANGEN.equals(requestStatus) && isMeldungscodeInCodeString(code, meldungsCodesLightGreen)) {
            return BG_COLOR_LIGHT_GREEN;
        }
        else if (LEVEL_0.equals(severity)) {
            return BG_COLOR_GREEN;
        }
        else if (LEVEL_10.equals(severity)) {
            return BG_COLOR_ORANGE;
        }
        else if (LEVEL_20.equals(severity)) {
            return BG_COLOR_RED;
        }

        return null;
    }

    private class WbciRequestStatusTableCellRenderer extends DefaultTableCellRenderer {

        private static final long serialVersionUID = -268487275819975241L;

        public WbciRequestStatusTableCellRenderer() {
            super();
        }

        @Override
        protected void setValue(Object value) {
            if (value != null) {
                if (WbciRequestStatus.class.isAssignableFrom(value.getClass())) {
                    setText(((WbciRequestStatus) value).getDescription());
                }
                else {
                    throw new IllegalArgumentException("Only objects of type WbciRequestStatus expected here!");
                }
            }
        }

    }

}
