/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2006 09:26:13
 */
package de.augustakom.hurrican.service.cc.impl.command.physik;

import java.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.Anschlussart;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * Command, um die Physik eines Buendel-Auftrags auf den Ziel-Auftrag zu uebernehmen. <br> Die Physik des
 * Buendel-Auftrags wird als 'rangierIdAdditional' verwendet.
 *
 *
 */
@CcTxRequired
@Component("de.augustakom.hurrican.service.cc.impl.command.physik.MovePhysikFromBuendelCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class MovePhysikFromBuendelCommand extends AbstractPhysikCommand {

    private static final Logger LOGGER = Logger.getLogger(MovePhysikFromBuendelCommand.class);

    private Endstelle endstelleADest = null;
    private Endstelle endstelleBDest = null;
    private Rangierung rangOfEsABuendelSrc = null;
    private Rangierung rangOfEsBBuendelSrc = null;

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand#execute()
     */
    public Object executeAfterFlush() throws Exception {
        loadRequiredData();
        moveBuendelPhysik();
        return null;
    }

    /*
     * Verschiebt die Rangierung des urspruenglichen Buendel-Auftrags
     * auf die 'rangierIdAdditional' des Ziel-Auftrags.
     */
    private void moveBuendelPhysik() throws StoreException {
        moveBuendelPhysik(endstelleADest, rangOfEsABuendelSrc);
        moveBuendelPhysik(endstelleBDest, rangOfEsBBuendelSrc);
    }

    /*
     * Ordnet der Ednstelle 'esDest' die Rangierung 'rangAdd4ES' als
     * Zusatz-Rangierung zu.
     */
    private void moveBuendelPhysik(Endstelle esDest, Rangierung rangAdd4ES) throws StoreException {
        try {
            if (rangAdd4ES != null) {
                EndstellenService esSrv = getCCService(EndstellenService.class);
                esDest.setRangierIdAdditional(rangAdd4ES.getId());
                esSrv.saveEndstelle(esDest);

                RangierungsService rs = getCCService(RangierungsService.class);
                rangAdd4ES.setFreigabeAb(null);
                rangAdd4ES.setBemerkung(null);
                rangAdd4ES.setEsId(esDest.getId());
                rs.saveRangierung(rangAdd4ES, false);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Fehler beim Uebernehmen der Rangierung des Buendel-Auftrags: " + e.getMessage(), e);
        }
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.physik.AbstractPhysikCommand#loadRequiredData()
     */
    protected void loadRequiredData() throws FindException {
        try {
            CCAuftragService as = getCCService(CCAuftragService.class);
            AuftragDaten adSrc = getAuftragDatenTx(getAuftragIdSrc());
            List<AuftragDaten> buendelADs =
                    as.findAuftragDaten4BuendelTx(adSrc.getBuendelNr(), AuftragDaten.BUENDEL_HERKUNFT_BILLING_PRODUKT);
            if (CollectionTools.isEmpty(buendelADs)) {
                throw new FindException("Keine Buendel-Auftraege gefunden!");
            }

            EndstellenService esSrv = getCCService(EndstellenService.class);
            endstelleADest = esSrv.findEndstelle4Auftrag(getAuftragIdDest(), Endstelle.ENDSTELLEN_TYP_A);
            endstelleBDest = esSrv.findEndstelle4Auftrag(getAuftragIdDest(), Endstelle.ENDSTELLEN_TYP_B);

            for (AuftragDaten ad : buendelADs) {
                if (NumberTools.notEqual(ad.getAuftragId(), getAuftragIdSrc())) {
                    Endstelle esABuendelSrc = esSrv.findEndstelle4Auftrag(ad.getAuftragId(), Endstelle.ENDSTELLEN_TYP_A);
                    Endstelle esBBuendelSrc = esSrv.findEndstelle4Auftrag(ad.getAuftragId(), Endstelle.ENDSTELLEN_TYP_B);

                    RangierungsService rs = getCCService(RangierungsService.class);
                    rangOfEsABuendelSrc = rs.findRangierung(esABuendelSrc.getRangierId());
                    rangOfEsBBuendelSrc = rs.findRangierung(esBBuendelSrc.getRangierId());

                    boolean isDirektanschluss =
                            NumberTools.equal(endstelleBDest.getAnschlussart(), Anschlussart.ANSCHLUSSART_DIREKT);

                    if (!isDirektanschluss && rangOfEsABuendelSrc == null && rangOfEsBBuendelSrc == null) {
                        throw new FindException("Vom Buendel-Auftrag (ID: " + ad.getAuftragId() + ") konnten keine " +
                                "Rangierungen ermittelt werden!");
                    }

                    break;
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Die Physik des Buendel-Auftrags konnte nicht uebernommen werden. Grund: "
                    + e.getMessage(), e);
        }
    }

}


