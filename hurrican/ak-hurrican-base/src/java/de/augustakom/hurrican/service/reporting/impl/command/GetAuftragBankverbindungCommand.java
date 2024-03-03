/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.02.2007 10:00:24
 */
package de.augustakom.hurrican.service.reporting.impl.command;

import java.util.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.BLZ;
import de.augustakom.hurrican.model.billing.Finanz;
import de.augustakom.hurrican.model.billing.RInfo;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.billing.RechnungsService;


/**
 * Command-Klasse, um die Bankverbindung zu einem best. Auftrag zu sammeln und diese in einer HashMap zu speichern.
 *
 *
 */
public class GetAuftragBankverbindungCommand extends AbstractReportCommand {

    private static final Logger LOGGER = Logger.getLogger(GetAuftragBankverbindungCommand.class);

    public static final String ZAHLUNGSWEISE = "Zahlungsweise";
    public static final String BLZ = "BLZ";
    public static final String BANK = "Bankname";
    public static final String KONTO_INHABER = "Kontoinhaber";
    public static final String KONTO_NUMMER = "Kontonummer";
    public static final String ZAHLUNG_ZIEL = "Zahlungsziel";


    private Long auftragNoOrig = null;
    private Map map = null;

    /**
     * @see de.augustakom.common.service.iface.IServiceCommand#execute()
     */
    public Object execute() throws HurricanServiceCommandException {
        try {
            setCmdPrefix(getPrefix());
            checkValues();
            map = new HashMap();

            readAuftragBank();
            return map;
        }
        catch (Exception e) {
            throw new HurricanServiceCommandException(e);
        }
    }

    /**
     * @see de.augustakom.hurrican.service.reporting.impl.command.AbstractReportCommand#getPrefix()
     */
    public String getPrefix() {
        return AUFTRAG_BANK;
    }

    /* Ermittelt die Stammdaten eines Auftrags und schreibt diese in die HashMap. */
    protected void readAuftragBank() throws HurricanServiceCommandException {
        try {
            // Ermittle Taifun-Finanz-Objekt
            Finanz finanz = new Finanz();
            BillingAuftragService as = (BillingAuftragService) getBillingService(BillingAuftragService.class);
            RechnungsService rs = (RechnungsService) getBillingService(RechnungsService.class);
            BAuftrag bAuftrag = as.findAuftrag(auftragNoOrig);
            if (bAuftrag != null) {
                RInfo rInfo = rs.findRInfo(bAuftrag.getRechInfoNoOrig());
                if (rInfo != null) {
                    Finanz result = rs.findFinanz(rInfo.getFinanzNo());
                    if (finanz != null) {
                        PropertyUtils.copyProperties(finanz, result);
                    }
                }
            }
            map.put(getPropName(ZAHLUNGSWEISE), StringUtils.trimToEmpty(finanz.getZahlungArt()));
            map.put(getPropName(BLZ), NumberTools.convertToString(finanz.getBlz(), null));
            map.put(getPropName(KONTO_NUMMER), StringUtils.trimToEmpty(finanz.getKontoNo()));
            map.put(getPropName(KONTO_INHABER), StringUtils.trimToEmpty(finanz.getKontoInhaber()));
            map.put(getPropName(ZAHLUNG_ZIEL), NumberTools.convertToString(finanz.getZahlungsFrist(), null));

            // Ermittle Banknamen
            BLZ blz = rs.findBLZ(finanz.getBlz());
            map.put(getPropName(BANK), (blz != null) ? StringUtils.trimToEmpty(blz.getName()) : null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(e);
        }
    }

    /* Ueberprueft, ob die Command-Klasse richtig konfiguriert wurde. */
    private void checkValues() throws HurricanServiceCommandException {
        Object tmpId = getPreparedValue(ORDER_NO_ORIG);
        auftragNoOrig = (tmpId instanceof Long) ? (Long) tmpId : null;
        if (auftragNoOrig == null) {
            throw new HurricanServiceCommandException("AuftragNoOrig wurde dem Command-Objekt nicht uebergeben!");
        }
    }

    /**
     * @see de.augustakom.hurrican.service.reporting.impl.command.AbstractReportCommand#getPropertyFile()
     */
    public String getPropertyFile() {
        return "/de/augustakom/hurrican/service/reporting/impl/command/resources/GetAuftragBankverbindungCommand.properties";
    }
}


