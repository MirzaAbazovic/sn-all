/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.08.2004 14:06:33
 */
package de.augustakom.hurrican.gui.auftrag.wizards.shared;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceAction;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.Wohnheim;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.NiederlassungService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.WohnheimService;


/**
 * Action, um Endstellen fuer einen Auftrag anzulegen.
 *
 *
 */
public class CreateEndstellenAction extends AbstractServiceAction {

    private static final Logger LOGGER = Logger.getLogger(CreateEndstellenAction.class);
    private static final long serialVersionUID = -482732710550724672L;

    private Auftrag auftrag = null;
    private Component owner = null;

    private AuftragTechnik auftragTechnik = null;
    private AuftragDaten auftragDaten = null;
    private Produkt produkt = null;

    /**
     * Konstruktor fuer die Action mit Angabe des Auftrags, fuer den die Endstellen angelegt werden sollen.
     *
     * @param auftrag Auftrag, fuer den Endstellen angelegt werden sollen.
     * @param owner   Owner fuer Dialoge.
     */
    public CreateEndstellenAction(Auftrag auftrag, Component owner) {
        super();
        this.auftrag = auftrag;
        this.owner = owner;
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        try {
            init();
            search4Produkt();

            if ((produkt != null) && !Produkt.ES_TYP_KEINE_ENDSTELLEN.equals(produkt.getEndstellenTyp())) {
                List<Endstelle> endstellen = createEndstellen();
                if ((endstellen != null) && (!endstellen.isEmpty())) {
                    if (Produkt.PROD_ID_APPARTEMENT.equals(produkt.getId())) {
                        // bei Appartement-Anschluss die Endstellendaten von Wohnheim eintragen
                        transferWohnheim2Endstelle(endstellen);
                    }
                    else if (BooleanTools.nullToFalse(produkt.getBuendelProdukt()) && (auftragDaten.getBuendelNr() != null)
                            && (auftragDaten.getBuendelNr() > 0)) {
                        // bei Buendel-Produkt Endstellendaten evtl. kopieren - falls von anderem Auftrag vorhanden.
                        EndstellenService es = getCCService(EndstellenService.class);
                        es.transferBuendel2Endstellen(endstellen, auftragDaten.getBuendelNr(), auftragDaten.getBuendelNrHerkunft());
                    }
                }
                else {
                    throw new HurricanGUIException(
                            "Es wurden keine Endstellen für den Auftrag angelegt! Ursache: unbekannt.");
                }
            }
            // Ordne dem Auftrag eine Niederlassung zu
            NiederlassungService ns = getCCService(NiederlassungService.class);
            ns.setNiederlassung4Auftrag(auftrag.getId());
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            MessageHelper.showErrorDialog(owner, ex);
        }
    }


    /*
     * Legt die Endstellen zu dem Auftrag an. <br>
     * @return die angelegten Endstellen.
     */
    private List<Endstelle> createEndstellen() throws ServiceNotFoundException, StoreException {
        EndstellenService esSrv = getCCService(EndstellenService.class);
        List<Endstelle> endstellen = esSrv.createEndstellen(auftragTechnik, produkt.getEndstellenTyp(),
                HurricanSystemRegistry.instance().getSessionId());

        try {
            if (endstellen != null) {
                for (Endstelle endstelle : endstellen) {
                    esSrv.saveEndstelle(endstelle);

                    // Falls Endstelle B, kopiere Adressdaten von AP-Adresse aus Taifun auf die Endstelle und
                    // lege aufgrund der Daten aus Taifun einen Ansprechpartner an, wenn dieser noch nicht vorhanden ist
                    if (endstelle.isEndstelleB()) {
                        esSrv.copyAPAddress(auftragDaten, HurricanSystemRegistry.instance().getSessionId());
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error("Fehler bei der Zuordnung der HVT-Schaltung oder Anschlussart der Endstellen!");
            LOGGER.error(e.getMessage(), e);
        }

        return endstellen;
    }


    /*
     * Sucht nach Wohnheim-Daten und uebergibt diese der Endstelle-B
     * aus der Liste <code>endstellen</code>.
     * @param endstellen Liste mit den Endstellen.
     */
    private void transferWohnheim2Endstelle(List<Endstelle> endstellen) throws ServiceNotFoundException,
            FindException, HurricanGUIException {
        Endstelle endstelle = null;
        for (Endstelle anEndstellen : endstellen) {
            endstelle = anEndstellen;
            if ("B".equals(endstelle.getEndstelleTyp())) {
                break;
            }
        }

        if (endstelle == null) {
            throw new HurricanGUIException("Es wurde keine Endstelle 'B' angelegt, der die Wohnheim-Daten " +
                    "uebergeben werden konnten.");
        }

        PhysikService ps = getCCService(PhysikService.class);
        VerbindungsBezeichnung verbindungsBezeichnung = ps.findVerbindungsBezeichnungById(auftragTechnik.getVbzId());
        if (verbindungsBezeichnung == null) {
            throw new HurricanGUIException("Den Endstellen konnten die Wohnheim-Daten nicht uebergeben werden, " +
                    "da die Leitungsnummer nicht gefunden wurde.");
        }

        WohnheimService ws = getCCService(WohnheimService.class);
        Wohnheim wohnheim = ws.findByVbz(verbindungsBezeichnung.getVbz());
        if (wohnheim == null) {
            throw new HurricanGUIException("Den Endstellen konnten die Wohnheim-Daten nicht uebergeben werden, " +
                    "da das Wohnheim nicht ermittelt werden konnte.");
        }

        try {
            EndstellenService esSrv = getCCService(EndstellenService.class);
            endstelle.setEndstelle(wohnheim.getStrasse());
            endstelle.setPlz(wohnheim.getPlz());
            endstelle.setOrt(wohnheim.getOrt());
            esSrv.saveEndstelle(endstelle);
        }
        catch (Exception e) {
            throw new HurricanGUIException("Beim Uebertragen der Wohnheim-Daten auf die Endstellen ist " +
                    "ein Fehler aufgetreten.", e);
        }
    }


    /* Liest die Auftragsdaten (AuftragDaten und AuftragTechnik) zu dem Auftrag aus */
    private void init() throws ServiceNotFoundException, FindException {
        CCAuftragService as = getCCService(CCAuftragService.class);
        auftragDaten = as.findAuftragDatenByAuftragId(auftrag.getId());
        auftragTechnik = as.findAuftragTechnikByAuftragId(auftrag.getId());
    }

    /* Sucht nach dem Produkt, das dem Auftrag zugeordnet ist. */
    private void search4Produkt() throws ServiceNotFoundException, FindException {
        ProduktService ps = getCCService(ProduktService.class);
        produkt = ps.findProdukt(auftragDaten.getProdId());
    }

}


