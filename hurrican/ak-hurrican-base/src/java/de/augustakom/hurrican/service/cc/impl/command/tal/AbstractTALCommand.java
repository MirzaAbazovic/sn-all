/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.07.2007 07:56:11
 */
package de.augustakom.hurrican.service.cc.impl.command.tal;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.shared.iface.AddressModel;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand;

/**
 * Abstrakte Command-Klasse fuer alle Commands, die Logik fuer die el. TAL-Bestellungen besitzen.
 *
 *
 */
public abstract class AbstractTALCommand extends AbstractServiceCommand {

    private static final Logger LOGGER = Logger.getLogger(AbstractTALCommand.class);

    /**
     * Key fuer die Uebergabe der Carrierbestellungs-ID.
     */
    public static final String KEY_CARRIERBESTELLUNG_ID = "cb.id";
    /**
     * Key fuer die Uebergabe der Hurrican AuftragId.
     */
    public static final String KEY_AUFTRAG_ID = "auftrag.id";
    /**
     * Key fuer die Uebergabe der Sub-Orders fuer die Carrierbestellung.
     */
    public static final String KEY_SUB_ORDERS = "sub.orders";
    /**
     * Key fuer die Uebergabe des Vorgangs-TYPS fuer den el. Vorgang.
     */
    public static final String KEY_CBVORGANG_TYP = "cb.vorgang.typ";
    /**
     * Key fuer die Uebergabe der Usecase-ID fuer die el. Bestellung.
     */
    public static final String KEY_CBUSECASE_ID = "cb.usecase.id";
    /**
     * Key fuer die Uebergabe der Carrier-Kennung (Absender).
     */
    public static final String KEY_CARRIER_KENNUNG_ABS = "carrier.kennung";

    /**
     * Datumsformat der DTAG fuer die el. TAL-Bestellung
     */
    protected String DATE_FORMAT_DTAG = "yyyyMMdd";
    /**
     * Format fuer Zeitangaben bei el. TAL-Bestellung DTAG
     */
    protected String TIME_FORMAT_DTAG = "HHmm";

    /**
     * Ermittelt die aktuell betroffene Carrierbestellung.
     *
     * @return Objekt vom Typ <code>Carrierbestellung</code>
     * @throws HurricanServiceCommandException
     */
    protected Carrierbestellung getCarrierbestellung() throws HurricanServiceCommandException {
        try {
            Long cbId = getPreparedValue(KEY_CARRIERBESTELLUNG_ID, Long.class, false,
                    "Die ID der betroffenen Carrierbestellung wurde nicht gesetzt!");

            CarrierService cs = getCCService(CarrierService.class);
            Carrierbestellung cb = cs.findCB(cbId);
            if (cb == null) {
                throw new HurricanServiceCommandException(
                        "Carrierbestellung konnte nicht ermittelt werden!");
            }
            return cb;
        }
        catch (HurricanServiceCommandException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Fehler bei der Ermittlung der Carrierbestellung: "
                    + e.getMessage(), e);
        }
    }

    /**
     * Ermittelt die Endstelle fuer die angegebenen Werte.
     *
     * @param auftragID
     * @param cb2EsId
     * @return Objekt vom Typ Endstelle
     * @throws HurricanServiceCommandException
     *
     */
    protected Endstelle getEndstelle(Long auftragID, Long cb2EsId) throws HurricanServiceCommandException {
        try {
            EndstellenService esSrv = getCCService(EndstellenService.class);
            List<Endstelle> endstellen4Auftrag = esSrv.findEndstellen4Auftrag(auftragID);
            for (Endstelle endstelle : endstellen4Auftrag) {
                if (NumberTools.equal(endstelle.getCb2EsId(), cb2EsId)) { return endstelle; }
            }
            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Fehler bei der Ermittlung der Endstelle: " + e.getMessage(), e);
        }
    }

    /**
     * Ermittelt die Standortadresse der angegebenen Endstelle. <br> Je nach Produkt-Konfiguration wird die
     * Standortadresse von Taifun oder von Hurrican ermittelt. <br> ACHTUNG: aktuell wird bei Taifun nur die
     * Standortadresse der Endstelle B beruecksichtigt!
     *
     * @param es        Endstelle, deren Standortadresse ermittelt werden soll
     * @param auftragId die ID des zugehoerigen Hurrican-Auftrags
     * @return Objekt vom Typ <code>AddressModel</code>, das die Standortadresse der Endstelle darstellt.
     * @throws HurricanServiceCommandException wenn bei der Ermittlung der Standortadresse ein Fehler auftritt.
     *
     */
    protected AddressModel getAPAddress(Endstelle es, Long auftragId) throws HurricanServiceCommandException {
        try {
            EndstellenService endstellenService = getCCService(EndstellenService.class);
            return endstellenService.findAnschlussadresse4Auftrag(auftragId, es.getEndstelleTyp());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Fehler bei der Ermittlung der Standortadresse: "
                    + e.getMessage(), e);
        }
    }

}
