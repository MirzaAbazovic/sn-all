/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.05.2012 11:54:40
 */
package de.augustakom.hurrican.service.wholesale.impl;

import java.time.*;
import javax.annotation.*;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.wholesale.WholesaleProductName;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.DSLAMService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.wholesale.LineIdNotFoundException;
import de.augustakom.hurrican.service.wholesale.NoWholesaleProductException;

/**
 * Abstrakte Klasse fuer alle Wholesale-Services
 */
public abstract class AbstractWholesaleService {

    private static final Logger LOGGER = Logger.getLogger(AbstractWholesaleService.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    protected CCAuftragService auftragService;
    @Resource(name = "de.augustakom.hurrican.service.cc.EndstellenService")
    protected EndstellenService endstellenService;
    @Resource(name = "de.augustakom.hurrican.service.cc.RangierungsService")
    protected RangierungsService rangierungsService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CCLeistungsService")
    protected CCLeistungsService ccLeistungsService;
    @Resource(name = "de.augustakom.hurrican.service.cc.DSLAMService")
    protected DSLAMService dslamService;
    @Resource(name = "de.augustakom.hurrican.service.cc.PhysikService")
    protected PhysikService physikService;

    /**
     * Ermittelt zur angegebenen LineId den zugehoerigen (aktiven) Hurrican-Auftrag und die {@link AuftragDaten}.
     *
     * @param lineId
     * @param when
     * @return Pair mit den zur LineId gehoerenden {@link Auftrag} und {@link AuftragDaten}
     * @throws LineIdNotFoundException     wenn kein aktiver(!) Auftrag zu der LineId gefunden wird
     * @throws NoWholesaleProductException wenn es sich bei dem Auftrag um keinen Wholesale-Auftrag handelt
     */
    Pair<Auftrag, AuftragDaten> findActiveOrderByLineIdAndCheckWholesaleProduct(String lineId, LocalDate when) {
        Auftrag auftrag;
        try {
            auftrag = auftragService.findActiveOrderByLineId(lineId, when);
        }
        catch (FindException e) {
            LOGGER.warn(e.getMessage(), e);
            throw new LineIdNotFoundException(lineId);
        }

        if (auftrag == null) {
            throw new LineIdNotFoundException(lineId);
        }

        AuftragDaten auftragDaten = checkForWholesaleProduct(lineId, auftrag);

        return Pair.create(auftrag, auftragDaten);
    }

    @Nonnull
    AuftragDaten findOrderByLineIdAndStatus(final String lineId, final Long... auftragStatus)   {
        try {
            final AuftragDaten auftragDaten = auftragService.findAuftragDatenByLineIdAndStatus(lineId, auftragStatus);
            if(auftragDaten == null)    {
                throw new LineIdNotFoundException(lineId);
            }
            else if (!WholesaleProductName.existsWithProductId(auftragDaten.getProdId())) {
                throw new NoWholesaleProductException(lineId);
            }
            return auftragDaten;
        }
        catch (FindException e) {
            throw new RuntimeException(e);
        }

    }


    protected final AuftragDaten checkForWholesaleProduct(String lineId, Auftrag auftrag) {
        AuftragDaten auftragDaten;
        try {
            auftragDaten = auftragService.findAuftragDatenByAuftragIdTx(auftrag.getAuftragId());
        }
        catch (FindException e) {
            throw new NoWholesaleProductException(lineId, e.getMessage());
        }

        if (auftragDaten == null) {
            throw new NoWholesaleProductException(lineId,
                    String.format("AuftragDaten not found for Auftrag-Id %d", auftrag.getAuftragId()));
        }

        if (!WholesaleProductName.existsWithProductId(auftragDaten.getProdId())) {
            throw new NoWholesaleProductException(lineId);
        }
        return auftragDaten;
    }

    /**
     * Ermittelt die Endstelle B des angegebenen Auftrags.
     *
     * @param auftrag
     * @return die Endstelle B zu dem angegebenen Auftrag.
     * @throws FindException wenn die Endstelle B nicht gefunden wird bzw. wenn der Endstelle keine Rangierung
     *                       zugeordnet ist!
     */
    protected Endstelle findEndstelle(Auftrag auftrag) throws FindException {
        Endstelle endstelleB = endstellenService.findEndstelle4Auftrag(auftrag.getAuftragId(),
                Endstelle.ENDSTELLEN_TYP_B);
        if (endstelleB == null) {
            throw new FindException("Accesspoint not found for order!");
        }

        if (endstelleB.getRangierId() == null) {
            throw new FindException("Accesspoint has no port assigned!");
        }
        return endstelleB;
    }

    protected static LocalDate today() {
        return LocalDate.now();
    }

}


