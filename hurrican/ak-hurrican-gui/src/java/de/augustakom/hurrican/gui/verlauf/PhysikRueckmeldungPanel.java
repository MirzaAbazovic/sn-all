/*
 * Copyright (c) 2009 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.10.2009 08:38:20
 */
package de.augustakom.hurrican.gui.verlauf;

import java.awt.*;
import java.util.List;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.iface.ISimpleFindService;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.service.cc.QueryCCService;
import de.augustakom.hurrican.service.cc.ReferenceService;

/**
 * Abstraktes Panel fuer die Rueckmeldung von Physikdaten.
 *
 *
 */
public class PhysikRueckmeldungPanel extends AbstractServicePanel {

    private static final Logger LOGGER = Logger.getLogger(PhysikRueckmeldungPanel.class);

    // GUI-Komponenten
    protected AKReferenceField rfZusatz = null;
    private final Long hvtStandortTypRefId;

    /**
     * Konstruktor fuer das Bauauftrags-Panel der Abteilung EXTERN.
     */
    public PhysikRueckmeldungPanel(Long hvtStandortTypRefId) {
        super("de/augustakom/hurrican/gui/verlauf/resources/PhysikRueckmeldungPanel.xml");
        this.hvtStandortTypRefId = hvtStandortTypRefId;
        createGUI();
        loadData();
    }

    @Override
    protected final void createGUI() {
        AKJLabel lblZusatz = getSwingFactory().createLabel("zusatz");
        lblZusatz.setFontStyle(Font.BOLD);
        rfZusatz = getSwingFactory().createReferenceField("zusatz");

        AKJPanel pnl = new AKJPanel(new GridBagLayout(), getTitle(hvtStandortTypRefId));
        pnl.add(lblZusatz, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        pnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        pnl.add(rfZusatz, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        this.setLayout(new BorderLayout());
        this.add(pnl, BorderLayout.CENTER);
    }

    private String getTitle(Long hvtStandortTypRefId) {
        String standortTyp = null;
        if (NumberTools.equal(hvtStandortTypRefId, HVTStandort.HVT_STANDORT_TYP_FTTH)) {
            standortTyp = "FttH";
        }
        else if (NumberTools.equal(hvtStandortTypRefId, HVTStandort.HVT_STANDORT_TYP_FTTB)) {
            standortTyp = "FttB";
        }
        else if (NumberTools.equal(hvtStandortTypRefId, HVTStandort.HVT_STANDORT_TYP_FTTB_H)) {
            standortTyp = "FttB_H";
        }
        else {
            MessageHelper.showErrorDialog(getMainFrame(),
                    new AKGUIException(String.format("HvtStandortTyp '%s' ist nicht unterstützt!", hvtStandortTypRefId)));
        }
        return String.format("Zusatzinformation %s", standortTyp);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    public final void loadData() {
        try {
            setWaitCursor();

            // ReferenceField fuellen
            ISimpleFindService sfs = getCCService(QueryCCService.class);
            rfZusatz.setFindService(sfs);

            ReferenceService refService = getCCService(ReferenceService.class);
            List<Reference> refs = refService.findReferencesByType(Reference.REF_TYPE_ZUSATZ_AUFWAND, Boolean.TRUE);
            rfZusatz.setReferenceList(refs);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            setDefaultCursor();
        }
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

    /**
     * Gibt einen Zusatzaufwand zum BA zurueck.
     *
     * @return
     */
    protected Long getZusatzAufwand() {
        Object obj = rfZusatz.getReferenceObject();
        return ((obj != null) && (obj instanceof Reference)) ? ((Reference) obj).getId() : null;
    }

}


