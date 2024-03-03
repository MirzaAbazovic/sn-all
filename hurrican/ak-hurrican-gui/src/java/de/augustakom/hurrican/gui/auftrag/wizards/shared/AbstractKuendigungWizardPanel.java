/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.02.2005 08:24:55
 */
package de.augustakom.hurrican.gui.auftrag.wizards.shared;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.SwingFactory;
import de.augustakom.common.gui.swing.wizard.AKJWizardComponents;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.auftrag.AuftragDataFrame;
import de.augustakom.hurrican.gui.base.AbstractServiceWizardPanel;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCAuftragStatusService;
import de.augustakom.hurrican.service.cc.EndstellenService;


/**
 * Basisklasse fuer die unterschiedlichen Kuendigung-Panels.
 *
 *
 */
public abstract class AbstractKuendigungWizardPanel extends AbstractServiceWizardPanel {

    private static final Logger LOGGER = Logger.getLogger(AbstractKuendigungWizardPanel.class);
    private static final long serialVersionUID = -7111757833999842847L;

    /**
     * Default-Konstruktor
     *
     * @param resource
     * @param wizardComponents
     */
    public AbstractKuendigungWizardPanel(String resource, AKJWizardComponents wizardComponents) {
        super(resource, wizardComponents);
        SwingFactory internalSF = SwingFactory.getInstance(
                "de/augustakom/hurrican/gui/auftrag/wizards/shared/AbstractKuendigungWizardPanel.xml");
    }

    /**
     * Kuendigt einen best. Auftrag und dessen Physik. <br> Ablauf der Kuendigung: <ul> <li>zu jedem Auftrag die
     * Endstellen suchen <li>zu jeder Endstelle die Carrierbestellung suchen und kuendigen <li>die Rangierungen zu jeder
     * Endstelle freigeben <li>Auftrag kuendigen </ul> (siehe VB-Modul 'Auftragsmonitor.Auftragsmonitor_Kuendigung')
     *
     * @throws ServiceNotFoundException
     * @throws FindException
     * @throws StoreException
     */
    protected void cancelOrderAndPhysic(Long auftragId, Date kuendigungsDatum) throws ServiceNotFoundException {
        setWaitCursor();
        final CCAuftragStatusService auftragStatusService = getCCService(CCAuftragStatusService.class);
        try {
            final AKWarnings warnings = auftragStatusService.kuendigeAuftragUndPhysik(auftragId, kuendigungsDatum, getSessionId());
            if (!warnings.isEmpty()) {
                MessageHelper.showInfoDialog(this, warnings.getWarningsAsText());
            }
        }
        catch (Exception e) {
            MessageHelper.showErrorDialog(this, e);
        }
    }

    /**
     * Kuendigt den Auftrag mit der ID <code>auftragId</code>.
     *
     * @param auftragId        ID des zu kuendigenden Auftrags
     * @param kuendigungsDatum Kuendigungsdatum
     * @throws ServiceNotFoundException
     * @throws StoreException
     */
    protected void cancelOrder(Long auftragId, Date kuendigungsDatum) throws ServiceNotFoundException, StoreException {
        final CCAuftragStatusService auftragStatusService = getCCService(CCAuftragStatusService.class);
        auftragStatusService.kuendigeAuftrag(auftragId, kuendigungsDatum, HurricanSystemRegistry.instance().getSessionId());

        // pruefen, ob Auftrag in (gueltigem) VPN_KONF verwendet wird! Wenn ja,
        // Warnhinweis, dass VPN-Konfig zu Kuendigungstermin auf anderen phys. Auftrag verweisen muss!
        try {
            final AKWarnings akWarnings = auftragStatusService.checkVpn(auftragId, new AKWarnings());
            if (akWarnings.isNotEmpty()) {
                MessageHelper.showInfoDialog(HurricanSystemRegistry.instance().getMainFrame(),
                        akWarnings.getWarningsAsText(), null, true);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Methode prueft, ob auf min. einer Endstelle eine Rangierung zugeordnet ist. Ist dies der Fall, wird
     * <code>true</code> zurueck gegeben und die Physiken koennen gekuendigt werden.
     *
     * @param auftragIds Liste mit den IDs der zu kuendigenden Auftraegen. Sollte das Produkt der zu kuendigenden
     *                   Auftraege ohne Endstellen konfiguriert sein oder keine Physik zugeordnet sein, wird
     *                   <code>false</code> zurueck geliefert.
     * @return true wenn die Physiken gekuendigt werden koennen.
     */
    protected boolean cancelPhysic(List<Long> auftragIds) {
        boolean cancelPhysic = false;
        if ((auftragIds != null) && (!auftragIds.isEmpty())) {
            try {
                EndstellenService esSrv = getCCService(EndstellenService.class);
                for (Long aId : auftragIds) {
                    List<Endstelle> endstellen = esSrv.findEndstellen4Auftrag(aId);
                    if (endstellen != null) {
                        for (Endstelle es : endstellen) {
                            if (es.getRangierId() != null) {
                                cancelPhysic = true;
                                break;
                            }
                        }
                    }
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        return cancelPhysic;
    }

    /**
     * Oeffnet den Auftrag mit der ID <code>auftragId</code>. (Bug-ID 11457)
     */
    protected void openOrder(Long auftragId) {
        try {
            CCAuftragService as = getCCService(CCAuftragService.class);
            Auftrag auftrag = as.findAuftragById(auftragId);
            AuftragDataFrame.openFrame(auftrag);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}


