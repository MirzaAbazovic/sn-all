/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.09.2009 09:06:07
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSRadiusAccountData;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.BAService;


/**
 * DataCommand, um Radius-Daten fuer SDSL Produkte zu ermitteln. Fuer dieses Produkte muessen keine DSL-Optionen geladen
 * werden!
 *
 *
 */
public class CPSGetRadiusSDSLDataCommand extends CPSGetRadiusDataCommand {

    private static final Logger LOGGER = Logger.getLogger(CPSGetRadiusSDSLDataCommand.class);

    private BAService baService;

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.cps.CPSGetRadiusDataCommand#loadDSLOptions()
     */
    @Override
    void loadDSLOptions(CPSRadiusAccountData radiusAcc, Date execDate, Produkt produkt) throws HurricanServiceCommandException {
        // Bei SDSL-Produkten wird zuerst geprueft, ob eine Downstream-Leistung vorhanden ist.
        // Ist dies der Fall, wird dieser Werte als PRODUCT_DATA_RATE_DOWN verwendet.
        // Wird keine Downstream-Bandbreite gefunden, wird die Bandbreite aus dem CPS-Produktnamen ermittelt.

        try {
            Date dateToLoadDownstreamOption = calculateDateToLoadDownstreamOption();
            loadDownstreamTechLs(radiusAcc, dateToLoadDownstreamOption);

            radiusAcc.setAlwaysOn(BooleanTools.getBooleanAsString(Boolean.TRUE));
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
        }

        // Produktbandbreite aus CPS_NAME vom Produkt ermitteln
        if (StringUtils.isBlank(radiusAcc.getProductDataRateDown()) && (produkt != null) && StringUtils.isNotBlank(produkt.getCpsProductName())) {
            String dataRateDown = StringUtils.substringAfterLast(produkt.getCpsProductName(), "_");
            radiusAcc.setProductDataRateDown(dataRateDown);
        }

        if (!NumberUtils.isNumber(radiusAcc.getProductDataRateDown())) {
            throw new HurricanServiceCommandException("PRODUCT_DATA_RATE_DOWN could not be loaded!");
        }
    }

    @Override
    void loadIP(CPSRadiusAccountData radiusAcc, Produkt produkt, Date execDate) throws HurricanServiceCommandException {
        super.loadIP(radiusAcc, produkt, execDate);

        // feste IP-Adresse pruefen - muss vorhanden sein
        if ((radiusAcc.getIpv4() == null) && (radiusAcc.getIpv6() == null)) {
            throw new HurricanServiceCommandException("No IP address found for order!");
        }
    }

    /*
     * Ermittelt das Datum, zu dem die Downstream-Leistung ermittelt werden soll.
     * Das Datum wird wie folgt definiert:
     *  - Auftragsstatus = techn. Realisierung && execDate < Realisierungsdatum --> Datum des Bauauftrags verwenden
     *  - Datum nicht gesetzt und Auftragsstatus < in Betrieb --> Datum aus Zukunft verwenden!
     *  - sonst: Exec-Date der CPS-Tx verwenden
     *
     * Hintergrund fuer die Funktion:
     * In MUC werden die RadiusAccounts fuer SDSL bereits VOR dem Bauauftrag per CPS eingerichtet. Somit
     * kann fuer die Ermittlung der Bandbreite nicht das Ausfuehrungsdatum der CPS-Tx verwendet werden, da
     * dieses in solchen Faellen VOR dem aktiv-von Datum der Downstream-Leistung liegt.
     */
    private Date calculateDateToLoadDownstreamOption() throws FindException {
        Date dateToLoadDownstreamOption = null;
        Date execDate = getCPSTransaction().getEstimatedExecTime();

        AuftragDaten auftragDaten = getAuftragDatenTx(getCPSTransaction().getAuftragId());
        if (NumberTools.equal(auftragDaten.getStatusId(), AuftragStatus.TECHNISCHE_REALISIERUNG)) {
            Verlauf actVerlauf = baService.findActVerlauf4Auftrag(getCPSTransaction().getAuftragId(), false);
            if ((actVerlauf != null) && DateTools.isDateBefore(execDate, actVerlauf.getRealisierungstermin())) {
                dateToLoadDownstreamOption = actVerlauf.getRealisierungstermin();
            }
        }

        if ((dateToLoadDownstreamOption == null) && NumberTools.isLess(auftragDaten.getStatusId(), AuftragStatus.IN_BETRIEB)) {
            dateToLoadDownstreamOption = DateTools.changeDate(DateTools.getHurricanEndDate(), Calendar.DATE, -1);
        }

        return (dateToLoadDownstreamOption != null) ? dateToLoadDownstreamOption : execDate;
    }


    /**
     * Injected
     */
    public void setBaService(BAService baService) {
        this.baService = baService;
    }

}


