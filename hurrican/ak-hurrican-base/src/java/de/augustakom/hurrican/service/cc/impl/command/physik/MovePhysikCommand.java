/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.09.2005 11:12:39
 */
package de.augustakom.hurrican.service.cc.impl.command.physik;

import static de.augustakom.hurrican.model.cc.Ansprechpartner.Typ.*;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.Ansprechpartner;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.AnsprechpartnerService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * Command-Klasse, um die Physik (Rangierungen) vom Ursprungs- auf den Ziel-Auftrag zu uebertragen. <br> Ausserdem
 * werden bei diesem Command die Endstellen-Daten kopiert.
 *
 *
 */
@CcTxRequired
@Component("de.augustakom.hurrican.service.cc.impl.command.physik.MovePhysikCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class MovePhysikCommand extends AbstractPhysikCommand {

    private static final Logger LOGGER = Logger.getLogger(MovePhysikCommand.class);

    private Endstelle endstelleADest = null;
    private Endstelle endstelleBDest = null;
    private Endstelle endstelleASrc = null;
    private Endstelle endstelleBSrc = null;
    private Rangierung rangOfEsASrc = null;
    private Rangierung rangAddOfEsASrc = null;
    private Rangierung rangOfEsBSrc = null;
    private Rangierung rangAddOfEsBSrc = null;
    private Produkt produkt = null;


    @Autowired
    private EndstellenService endstellenService;
    @Autowired
    private RangierungsService rangierungsService;
    @Autowired
    private ProduktService produktService;
    @Autowired
    private AnsprechpartnerService ansprechpartnerService;

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand#execute()
     */
    @Override
    public Object executeAfterFlush() throws Exception {
        loadRequiredData();
        movePhysik();
        return null;
    }

    /*
     * Uebertraegt die Physik des Ursprungs-Auftrags auf den Ziel-Auftrag: <br>
     * <ul>
     *  <li>Endstellendaten kopieren
     *  <li>Rangierung auf die neuen Endstellen setzen
     *  <li>Carrierbestellung auf neue Endstellen schreiben und Kuendigung entfernen
     * </ul>
     */
    private void movePhysik() throws StoreException {
        try {
            movePhysik(endstelleADest, endstelleASrc, rangOfEsASrc, rangAddOfEsASrc);
            movePhysik(endstelleBDest, endstelleBSrc, rangOfEsBSrc, rangAddOfEsBSrc);
        }
        catch (StoreException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Bei der Physikübernahme ist ein Fehler aufgetreten!", e);
        }
    }

    /* Verschiebt alle benoetigten Daten von <code>oldES</code> auf <code>newES</code>. */
    private void movePhysik(Endstelle esDest, Endstelle esSrc, Rangierung rang4ES, Rangierung rangAdd4ES) throws StoreException {
        try {
            // Endstelle kopieren/speichern
            copyES(esDest, esSrc);
            endstellenService.saveEndstelle(esDest);

            if (esSrc.getRangierId() != null) {
                // neue Endstelle in die Rangierung eintragen
                if (rang4ES != null) {
                    rang4ES.setFreigabeAb(null);
                    rang4ES.setBemerkung(null);
                    rang4ES.setEsId(esDest.getId());

                    rangierungsService.saveRangierung(rang4ES, false);
                }

                if (esSrc.getRangierIdAdditional() != null) {
                    // ID der Endstelle in die Zusatz-Rangierung eintragen
                    if (rangAdd4ES != null) {
                        rangAdd4ES.setFreigabeAb(null);
                        rangAdd4ES.setBemerkung(null);
                        rangAdd4ES.setEsId(esDest.getId());

                        rangierungsService.saveRangierung(rangAdd4ES, false);
                    }
                    else {
                        throw new StoreException("Der urspruenglichen Endstelle sind zwei " +
                                "Rangierungen zugeordnet. Es wurde jedoch nur eine Rangierung gefunden!");
                    }
                }
            }

            // Ansprechpartner kopieren
            Ansprechpartner.Typ ansprechpartnerTyp =
                    (esDest.isEndstelleB()) ? ENDSTELLE_B : ENDSTELLE_A;
            ansprechpartnerService.copyAnsprechpartner(ansprechpartnerTyp, getAuftragIdSrc(), getAuftragIdDest());
        }
        catch (Exception e) {
            throw new StoreException("Bei der Physikuebernahme ist ein Fehler aufgetreten:\n" + e.getMessage(), e);
        }
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.physik.AbstractPhysikCommand#loadRequiredData()
     */
    @Override
    protected void loadRequiredData() throws FindException {
        try {
            endstelleASrc = endstellenService.findEndstelle4Auftrag(getAuftragIdSrc(), Endstelle.ENDSTELLEN_TYP_A);
            endstelleBSrc = endstellenService.findEndstelle4Auftrag(getAuftragIdSrc(), Endstelle.ENDSTELLEN_TYP_B);
            endstelleADest = endstellenService.findEndstelle4Auftrag(getAuftragIdDest(), Endstelle.ENDSTELLEN_TYP_A);
            endstelleBDest = endstellenService.findEndstelle4Auftrag(getAuftragIdDest(), Endstelle.ENDSTELLEN_TYP_B);
            if ((endstelleASrc == null) || (endstelleBSrc == null)) {
                throw new FindException("Die Endstellen des Ursprungs-Auftrags (ID: " + getAuftragIdSrc() +
                        ") konnten nicht ermittelt werden.");
            }
            if ((endstelleADest == null) || (endstelleBDest == null)) {
                throw new FindException("Die Endstellen des Ziel-Auftrags (ID: " + getAuftragIdSrc() +
                        ") konnten nicht ermittelt werden.");
            }

            rangOfEsASrc = rangierungsService.findRangierung(endstelleASrc.getRangierId());
            rangAddOfEsASrc = rangierungsService.findRangierung(endstelleASrc.getRangierIdAdditional());
            rangOfEsBSrc = rangierungsService.findRangierung(endstelleBSrc.getRangierId());
            rangAddOfEsBSrc = rangierungsService.findRangierung(endstelleBSrc.getRangierIdAdditional());

            if ((rangOfEsASrc == null) && (rangOfEsBSrc == null)) {
                throw new FindException("Vom Ursprungs-Auftrag (ID: " + getAuftragIdSrc() + ") konnten keine " +
                        "Rangierungen ermittelt werden!");
            }

            produkt = produktService.findProdukt4Auftrag(getAuftragIdDest());
            if (produkt == null) {
                throw new FindException("Das Produkt des Ziel-Auftrags konnte nicht ermittelt werden.");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Fehler beim Ermitteln der benoetigten Daten fuer die Physik-Uebernahme:\n" +
                    e.getMessage(), e);
        }
    }

    /* Kopiert die benoetigten ES-Daten von <code>source</code> nach <code>dest</code>. */
    private void copyES(Endstelle dest, Endstelle source) throws ServiceNotFoundException, StoreException {
        if ((dest != null) && (source != null)) {
            if (BooleanTools.nullToFalse(produkt.getCreateAPAddress())) {
                // Datenuebernahme inkl. Endstellendaten wie Name/PLZ/Ort
                endstellenService.copyEndstelle(source, dest, Endstelle.COPY_DEFAULT_WITH_RANGIERUNG);
            }
            else {
                // Adressdaten werden nicht uebernommen
                endstellenService.copyEndstelle(source, dest, Endstelle.COPY_PHYSIK);
            }
        }
    }

}


