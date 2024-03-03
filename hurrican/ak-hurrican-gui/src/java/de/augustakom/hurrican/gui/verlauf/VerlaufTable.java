/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.01.2005 16:28:07
 */
package de.augustakom.hurrican.gui.verlauf;

import java.awt.*;
import java.util.*;
import javax.swing.table.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.Portierungsart;
import de.augustakom.hurrican.model.cc.view.AbstractVerlaufView;


/**
 * AKJTable-Implementierung fuer eine Bauauftrags-Tabelle. <br> Abhaengig vom Konstruktor-Parameter werden spezielle
 * Verlaeufe fuer den User hervorgehoben.
 *
 *
 */
public class VerlaufTable extends AKJTable implements Observer {

    private static final long serialVersionUID = 8048007148574915950L;

    protected static final Color BG_COLOR_SONDER_PORTIERUNG = new Color(255, 69, 69);    // hellrot
    protected static final Color BG_COLOR_EXPORT_PORTIERUNG = new Color(227, 149, 255);  // lila
    protected static final Color BG_COLOR_FINISH = new Color(0, 175, 0);          // gruen
    protected static final Color BG_COLOR_OBSERVE = new Color(255, 255, 100);     // gelb
    protected static final Color BG_COLOR_NOT_POSSIBLE = new Color(255, 0, 0);    // rot
    protected static final Color BG_COLOR_BA_HINWEIS = Color.CYAN;

    private boolean highlightSpecials = false;
    private boolean highlightBaHinweise = false;

    /**
     * Default-Konstruktor.
     *
     * @param highlightSpecials Flag, ob Spezielverlaeufe hervorgehoben werden sollen. Spezialverlaeufe sind z.B.
     *                          Verlaeufe mit Sonder-Portierung oder auch ueberwachte Verlaeufe.
     */
    public VerlaufTable(boolean highlightSpecials) {
        super();
        this.highlightSpecials = highlightSpecials;
    }

    public VerlaufTable(boolean highlightSpecials,boolean highlightBaHinweise) {
        this.highlightSpecials = highlightSpecials;
        this.highlightBaHinweise = highlightBaHinweise;
    }

    /**
     * Diese Methode wird von JTable immer aufgerufen. <br> In dieser Implementierung wird die Hintergrundfarbe jeder
     * zweiten Zeile geaendert.
     *
     * @see javax.swing.JTable#prepareRenderer(javax.swing.table.TableCellRenderer, int, int)
     */
    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component comp = super.prepareRenderer(renderer, row, column);
        if (getModel() instanceof AKMutableTableModel) {
            @SuppressWarnings("unchecked")
            AKMutableTableModel<AbstractVerlaufView> model = (AKMutableTableModel<AbstractVerlaufView>) getModel();

            AbstractVerlaufView view = model.getDataAtRow(row);

            if (view.getHasSubOrders()) {
                comp.setFont(comp.getFont().deriveFont(Font.BOLD));
            }

            if (view.isGuiFinished()) {
                comp.setBackground(BG_COLOR_FINISH);
                return comp;
            }

            if (highlightSpecials && !isCellSelected(row, column)) {
                if (NumberTools.equal(view.getPortierungsartId(), Portierungsart.PORTIERUNG_EXPORT)) {
                    comp.setBackground(BG_COLOR_EXPORT_PORTIERUNG);
                    return comp;
                }
                else if (NumberTools.equal(view.getPortierungsartId(), Portierungsart.PORTIERUNG_SONDER)) {
                    comp.setBackground(BG_COLOR_SONDER_PORTIERUNG);
                    return comp;
                }

                if (BooleanTools.nullToFalse(view.getVerlaufNotPossible())) {
                    // hervorheben, falls Verlauf als 'NOT_POSSIBLE' gekennzeichnet ist
                    comp.setBackground(BG_COLOR_NOT_POSSIBLE);
                    return comp;
                }

                if (BooleanTools.nullToFalse(view.getObserveProcess())) {
                    // hervorheben, wenn Verlauf als 'beobachten' markiert ist
                    comp.setBackground(BG_COLOR_OBSERVE);
                    return comp;
                }

                if(highlightBaHinweise && StringUtils.isNotBlank(view.getBaHinweise())) {
                    comp.setBackground(BG_COLOR_BA_HINWEIS);
                    return comp;
                }
            }
        }

        return comp;
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
        this.repaint();
    }

}


