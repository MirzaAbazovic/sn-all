/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.11.2007 12:01:37
 */
package de.augustakom.hurrican.gui.verlauf;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.view.AbstractBauauftragView;
import de.augustakom.hurrican.model.cc.view.AbstractVerlaufView;
import de.augustakom.hurrican.service.cc.BAService;


/**
 * Panel zur Darstellung von zugehoerigen/zeitnahen Bauauftraegen eines Kunden.
 *
 *
 */
public class RelatedBAPanel extends AbstractServicePanel {

    private static final Logger LOGGER = Logger.getLogger(RelatedBAPanel.class);

    // Anzahl Wochen fuer die Beruecksichtigung zugehoeriger Bauauftraege
    private static final int WEEK_COUNT_FOR_SHORT_TERM = 2;

    private AKReflectionTableModel tbMdlRelated = null;

    /**
     * Default-Const.
     */
    public RelatedBAPanel() {
        super(null);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblHeader = new AKJLabel("zeitnahe/zugehoerige BAs:");

        tbMdlRelated = new AKReflectionTableModel<AbstractBauauftragView>(
                new String[] { VerbindungsBezeichnung.VBZ_BEZEICHNUNG, "Anlass", "Auftrags-ID", "Produkt", "Real.Datum",
                        "Verlaufsstatus", "Auftragsstatus" },
                new String[] { "vbz", "anlass", "auftragId", "produktName", "realisierungstermin",
                        "verlaufStatus", "auftragStatus" },
                new Class[] { String.class, String.class, Long.class, String.class, Date.class,
                        String.class, String.class }
        );
        AKJTable tbRelated = new AKJTable(tbMdlRelated, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbRelated.attachSorter();
        tbRelated.fitTable(new int[] { 100, 115, 90, 140, 90, 170, 170 });

        this.setLayout(new BorderLayout());
        this.add(lblHeader, BorderLayout.NORTH);
        this.add(new AKJScrollPane(tbRelated, new Dimension(600, 150)), BorderLayout.CENTER);
    }

    /**
     * Veranlasst das Panel dazu, die zugehoerigen/zeitnahen Bauauftraege des Kunden zu laden, zu dem der Bauauftrag
     * 'baView' gehoert.
     *
     * @param baView
     */
    protected void showRelatedBAs(final AbstractBauauftragView baView) {
        try {
            if ((baView != null) && (baView.getKundeNo() != null)) {
                BAService bas = getCCService(BAService.class);
                List<AbstractBauauftragView> views =
                        bas.findBAVerlaufViews4KundeInShortTerm(
                                baView.getKundeNo(), baView.getRealisierungstermin(), WEEK_COUNT_FOR_SHORT_TERM);

                // aktuellen Bauauftrag heraus filtern
                CollectionUtils.filter(views, new Predicate() {
                    @Override
                    public boolean evaluate(Object toCheck) {
                        if (toCheck instanceof AbstractVerlaufView) {
                            AbstractVerlaufView v = (AbstractVerlaufView) toCheck;
                            if (NumberTools.notEqual(baView.getVerlaufId(), v.getVerlaufId())) {
                                return true;
                            }
                        }
                        return false;
                    }
                });

                tbMdlRelated.setData(views);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * Gibt an, ob zu dem aktuellen Bauauftrag weitere zugehoerige Bauauftraege vorhanden sind.
     *
     * @return true wenn noch zugehoerige Bauauftraege vorhanden sind
     *
     */
    protected boolean hasRelatedBAs() {
        return (tbMdlRelated.getRowCount() > 0) ? true : false;
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
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

    /* 'Loescht' alle Felder und Modelle. */
    protected void clear() {
        GuiTools.cleanFields(this);
        tbMdlRelated.setData(null);
    }

}

