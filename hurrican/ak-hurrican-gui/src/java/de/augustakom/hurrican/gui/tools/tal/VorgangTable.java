/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.09.2011 11:42:40
 */
package de.augustakom.hurrican.gui.tools.tal;

import java.awt.*;
import javax.annotation.*;
import javax.swing.table.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.model.AbgebendeLeitungenVorgang;
import de.mnet.wita.model.Vorgang;

/**
 * JTable-Implementierung fuer die Anzeige von {@link Vorgang} Objekten bzw. Spezialisierungen davon.
 */
public class VorgangTable<T extends Vorgang> extends AKJTable {

    private static final long serialVersionUID = 8788741966081724046L;

    private static final Color SKY_BLUE = new Color(135, 206, 250);
    private static final Color YELLOW = new Color(255, 255, 0);
    static final Color DTAG_MAGENTA = new Color(226, 0, 116);
    private static final Color BG_COLOR_CLOSED = new Color(255, 153, 0); // orange
    protected static final Color BG_COLOR_FINISH_OK = new Color(0, 175, 0); // gruen
    protected static final Color BG_COLOR_FINISH_ERR = new Color(255, 100, 100); // helles rot
    protected static final Color BG_COLOR_RED = new Color(255, 0, 0); // rot
    private static final Color BG_COLOR_GREY = new Color(180, 180, 180);

    private final String columnToColor;

    public VorgangTable(TableModel tableModel, int autoResizeMode, int selectionMode, @Nonnull String columnToColor) {
        super(tableModel, autoResizeMode, selectionMode);
        this.columnToColor = columnToColor;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component comp = super.prepareRenderer(renderer, row, column);
        if ((getModel() instanceof AKMutableTableModel) && !isRowSelected(row)) {
            String name = getColumnName(column);
            if (columnToColor.equals(name)) {
                T data = ((AKMutableTableModel<T>) getModel()).getDataAtRow(row);

                if (data instanceof AbgebendeLeitungenVorgang) {
                    defineBackgroundForAbgebendeLeitungStatus(comp, (AbgebendeLeitungenVorgang) data);
                }
                else {
                    if (data.isImportant()) {
                        comp.setBackground(DTAG_MAGENTA);
                        comp.setForeground(Color.WHITE);
                    }

                    // should always be the last check and overwrite previous color definitions!
                    defineBackgroundForClaimedUser(comp, data);
                }
            }
        }
        return comp;
    }

    private void defineBackgroundForAbgebendeLeitungStatus(Component comp, AbgebendeLeitungenVorgang abgebendeLeitungenVorgang) {
        MeldungsType lastMeldungsType = abgebendeLeitungenVorgang.getLastMeldungsType();
        if (lastMeldungsType != null) {
            switch (lastMeldungsType) {
                case AKM_PV:
                    comp.setBackground(BG_COLOR_GREY);
                    break;
                case RUEM_PV:
                    if (abgebendeLeitungenVorgang.getZustimmungProviderWechsel() != null && abgebendeLeitungenVorgang.getZustimmungProviderWechsel()) {
                        comp.setBackground(BG_COLOR_FINISH_OK);
                    }
                    else if (abgebendeLeitungenVorgang.getZustimmungProviderWechsel() != null) {
                        comp.setBackground(BG_COLOR_RED);
                    }
                    break;
                case VZM_PV:
                    comp.setBackground(YELLOW);
                    break;
                case ABM_PV:
                    comp.setBackground(BG_COLOR_FINISH_OK);
                    break;
                case ABBM_PV:
                    comp.setBackground(BG_COLOR_RED);
                    break;
                default:
                    break;
            }
        }
    }

    private void defineBackgroundForClaimedUser(Component comp, T vorgang) {
        String loginNameCurrentUser = HurricanSystemRegistry.instance().getCurrentUser().getLoginName();
        if (loginNameCurrentUser.equalsIgnoreCase(vorgang.getTaskBearbeiter())) {
            comp.setBackground(SKY_BLUE);
            comp.setForeground(Color.WHITE);
        }
        else if (StringUtils.isNotBlank(vorgang.getTaskBearbeiter())) {
            comp.setBackground(YELLOW);
            comp.setForeground(Color.BLACK);
        }
    }

}
