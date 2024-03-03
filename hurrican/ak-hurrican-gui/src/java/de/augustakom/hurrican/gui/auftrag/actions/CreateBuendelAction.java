/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.10.2007 11:19:03
 */
package de.augustakom.hurrican.gui.auftrag.actions;

import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.auftrag.AttachBuendelDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.view.CCKundeAuftragView;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCKundenService;
import de.augustakom.hurrican.service.cc.CounterService;
import de.augustakom.hurrican.service.cc.utils.CCServiceFinder;


/**
 * Action, um mehrere Auftraege eines Kunden zu einem Buendel zusammenzufassen. <br> <br> Die Action 'fragt', ob ein
 * neues Buendel generiert oder der Auftrag einem bestehenden Buendel zugeordnet werden soll. <br> - neues Buendel: es
 * wird eine neue Buendelnummer generiert und dem Auftrag zugeordnet - es erscheint eine Auswahl aller Buendelnummern
 * des Kunden zur Auswahl
 *
 *
 */
public class CreateBuendelAction {

    private static final Logger LOGGER = Logger.getLogger(CreateBuendelAction.class);


    public void perform(CCKundeAuftragView ccKundeAuftragView) {
        try {
            if (ccKundeAuftragView != null) {
                CCAuftragService as = CCServiceFinder.instance().getCCService(CCAuftragService.class);
                AuftragDaten ad = as.findAuftragDatenByAuftragId(ccKundeAuftragView.getAuftragId());
                if (ad == null) {
                    throw new HurricanGUIException("Auftragsdaten konnten nicht ermittelt werden.");
                }

                createBuendel(ad);
            }
            else {
                throw new HurricanGUIException("Auftrag konnte nicht ermittelt werden.");
            }
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), ex);
        }
    }

    /*
     * Funktion, um den ausgewaehlten Auftrag einem Buendel zuzuordnen.
     */
    private void createBuendel(AuftragDaten auftragDaten) throws HurricanGUIException {
        if (auftragDaten.getBuendelNr() != null) {
            throw new HurricanGUIException("Der Auftrag ist bereits einem Buendel zugeordnet!");
        }

        int options = MessageHelper.showOptionDialog(HurricanSystemRegistry.instance().getMainFrame(),
                "Soll ein neues Buendel generiert (ja) oder \nein bestehendes verwendet (nein) werden?",
                "Buendel generieren?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
        if (options == JOptionPane.YES_OPTION) {
            createNewBuendel(auftragDaten);
        }
        else if (options == JOptionPane.NO_OPTION) {
            addToBuendel(auftragDaten);
        }
    }

    /*
     * Generiert eine neue Buendelnummer und ordnet diese dem Auftrag zu.
     */
    private void createNewBuendel(AuftragDaten auftragDaten) throws HurricanGUIException {
        try {
            CounterService cs = CCServiceFinder.instance().getCCService(CounterService.class);
            Integer buendelNr = cs.getNewIntValue(CounterService.COUNTER_BUENDEL);

            attachBuendel(auftragDaten, buendelNr);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanGUIException(
                    "Es konnte keine Buendelnummer erzeugt werden. Grund:\n" + e.getMessage(), e);
        }
    }

    /*
     * Ermittelt alle Buendelnummern des Kunden und bietet diese zur Auswahl an.
     * Die ausgewaehlte Nummer wird dem Auftrag zugeordnet.
     */
    private void addToBuendel(AuftragDaten auftragDaten) throws HurricanGUIException {
        try {
            CCAuftragService as = CCServiceFinder.instance().getCCService(CCAuftragService.class);
            Auftrag auftrag = as.findAuftragById(auftragDaten.getAuftragId());

            CCKundenService ks = CCServiceFinder.instance().getCCService(CCKundenService.class);
            List<CCKundeAuftragView> views = ks.findKundeAuftragViews4Kunde(auftrag.getKundeNo(), true, true);
            if (CollectionTools.isNotEmpty(views)) {
                // Buendelnummern ermitteln
                List<Integer> bnrs = new ArrayList<>();
                for (CCKundeAuftragView view : views) {
                    if ((view.getBuendelNr() != null) && !bnrs.contains(view.getBuendelNr())) {
                        bnrs.add(view.getBuendelNr());
                    }
                }

                Integer selection = showAttachBuendelDialog(bnrs);
                if ((selection != null) && (selection > 0)) {
                    List<AuftragDaten> auftraege4Buendel = as.findAuftragDaten4Buendel(selection, AuftragDaten.BUENDEL_HERKUNFT_HURRICAN);
                    boolean isActive = false;
                    if ((auftraege4Buendel != null) && (!auftraege4Buendel.isEmpty())) {
                        for (AuftragDaten ad : auftraege4Buendel) {
                            if (ad.isAuftragActive()) {
                                isActive = true;
                                break;
                            }
                        }
                    }
                    if (isActive) {
                        attachBuendel(auftragDaten, selection);
                    }
                    else {
                        throw new HurricanGUIException("Zur angegebenen Buendelnummer wurden keine aktiven Kundenauftraege gefunden!");
                    }
                }
            }
            else {
                throw new HurricanGUIException("Die Kundenauftraege konnten nicht ermittelt werden!");
            }
        }
        catch (HurricanGUIException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanGUIException(
                    "Die Zuordnung konnte nicht erzeugt werden. Grund:\n" + e.getMessage(), e);
        }
    }

    /**
     * Zeigt einen Dialog an, in dem eine Buendelnummer entweder aus einem anderen Auftrag des aktuellen Kunden oder
     * manuell aus einem beliebigen anderen aktiven Auftrag zugeordnet werden kann.
     *
     * @param buendelNrn Liste mit Buendelnummern des aktuellen Kunden
     * @return Ausgewaehlte Buendelnummer oder null bei Abbruch
     */
    private Integer showAttachBuendelDialog(List<Integer> buendelNrn) {
        AttachBuendelDialog buendelDialog = new AttachBuendelDialog(buendelNrn);
        Object result = DialogHelper.showDialog(HurricanSystemRegistry.instance().getMainFrame(), buendelDialog, true, true);
        if (result instanceof Integer) {
            return (Integer) result;
        }
        return null;
    }

    /*
     * Ordnet die Buendelnummer 'buendelNr' dem Auftrag zu.
     */
    private void attachBuendel(AuftragDaten auftragDaten, Integer buendelNr) throws HurricanGUIException {
        try {
            CCAuftragService as = CCServiceFinder.instance().getCCService(CCAuftragService.class);

            // falls keine Billing-Auftragsnummer gesetzt, diese ueber das Buendel ermitteln
            if (auftragDaten.getAuftragNoOrig() == null) {
                List<AuftragDaten> ads =
                        as.findAuftragDaten4Buendel(buendelNr, AuftragDaten.BUENDEL_HERKUNFT_HURRICAN);
                if (CollectionTools.isNotEmpty(ads)) {
                    for (AuftragDaten ad : ads) {
                        if (ad.getAuftragNoOrig() != null) {
                            auftragDaten.setAuftragNoOrig(ad.getAuftragNoOrig());
                            break;
                        }
                    }
                }
            }

            auftragDaten.setBuendelNr(buendelNr);
            auftragDaten.setBuendelNrHerkunft(AuftragDaten.BUENDEL_HERKUNFT_HURRICAN);

            as.saveAuftragDaten(auftragDaten, false);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanGUIException(
                    "Die Buendelnummer konnte dem Auftrag nicht zugeordnet werden. Grund:\n" + e.getMessage(), e);
        }
    }

}


