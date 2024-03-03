/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.03.2005 11:04:56
 */
package de.augustakom.hurrican.gui.auftrag;

import java.util.*;
import javax.validation.constraints.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.AKJInternalFrame;
import de.augustakom.common.gui.swing.AbstractMDIMainFrame;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractDataPanel;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.service.cc.CCAuftragService;


/**
 * Abstrakte Klasse fuer alle Auftrag-Panels.
 *
 *
 */
public abstract class AbstractAuftragPanel extends AbstractDataPanel {

    private static final Logger LOGGER = Logger.getLogger(AbstractAuftragPanel.class);

    /**
     * @param resource
     */
    public AbstractAuftragPanel(String resource) {
        super(resource);
    }

    /**
     * Kann von den Ableitungen ueberschrieben werden, um Positions-Parameter des Panels zurueck zu liefern. <br/>
     * Damit sind Informationen wie z.B. der aktuelle Index einer NavigationBar oder eines TabbedPanes gemeint, die
     * zum Zeitpunkt des Aufrufs aktiv sind. <br/>
     * Diese Information wird von {@link de.augustakom.hurrican.gui.auftrag.AuftragDataFrame} wieder verwendet, um bei
     * einem Refresh des Frames die Informationen wieder an das jeweilige Panel zu uebergeben.
     * @return
     */
    protected @NotNull List<PositionParameter> getPositionParameters() {
        return Collections.emptyList();
    }


    /**
     * Kann von Ableitungen ueberschrieben werden, um 'von aussen' Positions-Parameter zu empfangen und somit z.B. den
     * letzten Index einer NavigationBar oder eines TabbedPanes wieder herzustellen. <br/>
     * @param positionParameters
     */
    protected void setPositionParameters(@NotNull List<PositionParameter> positionParameters) {
    }

    /**
     * Ueberprueft, ob die Datensaetze des Auftrags mit der ID <code>auftragId</code> historisiert werden sollen.
     *
     * @param auftragId
     * @return true, wenn die Datensaetze historisiert werden sollen.
     * @see de.augustakom.hurrican.gui.auftrag.AbstractAuftragPanel#makeHistory4Status(java.lang.Long)
     */
    protected boolean makeHistory4Auftrag(Long auftragId) {
        try {
            CCAuftragService as = getCCService(CCAuftragService.class);
            AuftragDaten ad = as.findAuftragDatenByAuftragId(auftragId);
            if (ad != null) {
                return makeHistory4Status(ad.getStatusId());
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return true;
    }

    /**
     * Ueberprueft an Hand der Status-ID, ob die Datensaetze fuer einen Auftrag historisiert werden sollen.
     *
     * @param statusId Status-ID des Auftrags, fuer den geprueft werden soll, ob die Datensaetze historisiert werden
     *                 sollen.
     * @return true, wenn die Datensaetze historisiert werden sollen.
     */
    protected boolean makeHistory4Status(Long statusId) {
        if (statusId != null) {
            if (statusId.intValue() >= AuftragStatus.TECHNISCHE_REALISIERUNG) {
                return true;
            }
            return false;
        }
        return true;
    }

    /**
     * Veranlasst das Auftrags-Frame dazu, sich zu aktualisieren (die Daten werden neu geladen).
     */
    protected void refreshAuftragFrame() {
        AbstractMDIMainFrame mainFrame = HurricanSystemRegistry.instance().getMainFrame();
        AuftragDataFrame dataFrame = getAuftragDataFrame();
        if (dataFrame != null) {
            mainFrame.activateInternalFrame(dataFrame.getUniqueName());
            dataFrame.refresh();
        }
    }

    /**
     * Gibt das Auftrags-Frame zurueck
     *
     * @return das aktuelle AuftragDataFrame oder <code>null</code>.
     */
    protected AuftragDataFrame getAuftragDataFrame() {
        AbstractMDIMainFrame mainFrame = HurricanSystemRegistry.instance().getMainFrame();
        AKJInternalFrame[] frames =
                mainFrame.findInternalFrames(AuftragDataFrame.class);
        AuftragDataFrame dataFrame = null;
        if ((frames != null) && (frames.length == 1)) {
            dataFrame = (AuftragDataFrame) frames[0];
        }

        return dataFrame;
    }


    static class PositionParameter {
        String name;
        int position;

        public PositionParameter(String name, int position) {
            this.name = name;
            this.position = position;
        }

        @Override
        public String toString() {
            return new StringBuilder("PositionParameter=[")
                    .append("name=").append(name)
                    .append(", position=").append(position)
                    .append(")]")
                    .toString();
        }
    }

}


