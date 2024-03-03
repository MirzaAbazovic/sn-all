/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.02.2006 10:09:37
 */
package de.augustakom.hurrican.service.cc.impl.command.physik;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.ObjectTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.Anschlussart;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Produkt2PhysikTyp;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * Command, um zu pruefen, ob der Physiktyp (von Endstelle B) des alten Auftrags fuer den neuen Auftrag verwendet werden
 * kann. <br> Sollte der Physiktyp des alten Auftrags nicht fuer den neuen Auftrag verwendet werden koennen, wird eine
 * FindException geworfen.
 *
 *
 */
@CcTxRequired
@Component("de.augustakom.hurrican.service.cc.impl.command.physik.CheckPhysiktypCompatibilityCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CheckPhysiktypCompatibilityCommand extends AbstractPhysikCommand {

    // Command aktuell in Chain 'Anschlussuebernahme' und 'Bandbreitenaenderung (std.)'
    // Pruefen, ob Command nicht auch in 'Anschlussueb. SDSL' bzw. 'BBaenderung SDSL' verwendet werden sollte

    private static final Logger LOGGER = Logger.getLogger(CheckPhysiktypCompatibilityCommand.class);

    private List<Produkt2PhysikTyp> possiblePhysiktypes = null;
    private boolean isDirektanschluss = false;

    private Rangierung rangOld = null;
    private Rangierung rangAddOld = null;

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand#execute()
     */
    @Override
    public Object executeAfterFlush() throws Exception {
        loadRequiredData();
        if (!isDirektanschluss) {  // Physiktyp nur pruefen, wenn nicht Direktanschluss!
            for (Produkt2PhysikTyp p2pt : possiblePhysiktypes) {
                if (p2pt.isCompatible(rangOld, rangAddOld)) {
                    // Bandbreitenpruefung mit CheckDownstreamBandwidthCommand
                    return null;
                }
            }
            throw new FindException("Die Physiktypen der beiden Auftraege sind nicht kompatibel!\n" +
                    "Physikaenderung kann nicht durchgefuehrt werden.");
        }

        return null;
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.physik.AbstractPhysikCommand#loadRequiredData()
     */
    @Override
    protected void loadRequiredData() throws FindException {
        try {
            AuftragDaten adDest = getAuftragDatenTx(getAuftragIdDest());
            if (adDest == null) {
                throw new FindException("Auftrag konnte nicht ermittelt werden!");
            }

            PhysikService ps = getCCService(PhysikService.class);
            possiblePhysiktypes = ps.findP2PTs4Produkt(adDest.getProdId(), null);
            if ((possiblePhysiktypes == null) || (possiblePhysiktypes.isEmpty())) {
                throw new FindException(
                        "Die moeglichen Physiktypen fuer den Ziel-Auftrag konnten nicht ermittelt werden!");
            }

            EndstellenService esSrv = getCCService(EndstellenService.class);
            Endstelle esBSrc = esSrv.findEndstelle4Auftrag(getAuftragIdSrc(), Endstelle.ENDSTELLEN_TYP_B);
            isDirektanschluss = NumberTools.equal(esBSrc.getAnschlussart(), Anschlussart.ANSCHLUSSART_DIREKT);

            RangierungsService rs = getCCService(RangierungsService.class);
            Rangierung[] rangierungen = rs.findRangierungen(getAuftragIdSrc(), Endstelle.ENDSTELLEN_TYP_B);

            AuftragDaten adSrc = getAuftragDatenTx(getAuftragIdSrc());
            rangOld = (Rangierung) ObjectTools.getObjectAtIndex(rangierungen, 0, null, Rangierung.class);

            ProduktService prodService = getCCService(ProduktService.class);
            Produkt destProd = prodService.findProdukt(adDest.getProdId());
            if (BooleanTools.nullToFalse(destProd.getIsCombiProdukt())) {
                rangAddOld = (Rangierung) ObjectTools.getObjectAtIndex(rangierungen, 1, null, Rangierung.class);

                // zusaetzliche Rangierung ueber die Buendel-Nr laden
                if ((rangAddOld == null) && (adSrc.getBuendelNr() != null) &&
                        StringUtils.equals(adSrc.getBuendelNrHerkunft(), AuftragDaten.BUENDEL_HERKUNFT_BILLING_PRODUKT)) {
                    // Buendel-Auftrag laden
                    CCAuftragService as = getCCService(CCAuftragService.class);
                    List<AuftragDaten> buendelADs =
                            as.findAuftragDaten4BuendelTx(adSrc.getBuendelNr(), AuftragDaten.BUENDEL_HERKUNFT_BILLING_PRODUKT);
                    if (CollectionTools.isNotEmpty(buendelADs)) {
                        for (AuftragDaten adSrcBuendel : buendelADs) {
                            if (NumberTools.notEqual(adSrc.getAuftragId(), adSrcBuendel.getAuftragId())) {
                                Rangierung[] rangBuendel =
                                        rs.findRangierungen(adSrcBuendel.getAuftragId(), Endstelle.ENDSTELLEN_TYP_B);
                                rangAddOld = (rangBuendel != null) ? rangBuendel[0] : null;
                                break;
                            }
                        }
                    }
                }
            }

            Long physiktypOld = (rangOld != null) ? rangOld.getPhysikTypId() : null;
            if ((physiktypOld == null) && !isDirektanschluss) {
                throw new FindException("Der Physiktyp des Ursprung-Auftrags konnte nicht ermittelt werden!");
            }
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(
                    "Bei der Ueberpruefung der Physiktypen der Auftraege ist ein Fehler aufgetreten: " +
                            e.getMessage(), e
            );
        }
    }
}


