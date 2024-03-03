/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.02.2007 16:27:24
 */
package de.augustakom.hurrican.service.reporting.impl.command;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.model.billing.view.BAuftragLeistungView;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.billing.LeistungService;
import de.augustakom.hurrican.service.cc.AccountService;
import de.augustakom.hurrican.service.cc.CCAuftragService;


/**
 * Command-Klasse, um Email-Account-Daten zu einem best. Auftrag zu sammeln und diese in einer HashMap zu speichern.
 *
 *
 */
public class GetOnlineAccountDatenCommand extends AbstractReportCommand {

    private static final Logger LOGGER = Logger.getLogger(GetOnlineAccountDatenCommand.class);

    public static final String MENGE = "Menge";
    public static final String RECHNUNGSTEXT = "Rechnungstext";
    public static final String LEISTUNGEN = "Leistungen";
    public static final String PRINT_LEISTUNGEN = "PrintLeistungen";
    public static final String ACCOUNT = "Account";
    public static final String PASSWORT = "Passwort";

    private Long kundeNoOrig = null;
    private Long auftragId = null;
    private Map map = null;

    /**
     * @see de.augustakom.common.service.iface.IServiceCommand#execute()
     */
    @Override
    public Object execute() throws HurricanServiceCommandException {
        try {
            setCmdPrefix(getPrefix());
            checkValues();
            map = new HashMap();

            readEmailDaten();
            return map;
        }
        catch (Exception e) {
            throw new HurricanServiceCommandException(e);
        }
    }

    /**
     * @see de.augustakom.hurrican.service.reporting.impl.command.AbstractReportCommand#getPrefix()
     */
    @Override
    public String getPrefix() {
        return ONLINE_ACCOUNT;
    }

    /*
     * Lese Email-Daten für Account-Anschreiben
     */
    private void readEmailDaten() throws HurricanServiceCommandException {
        try {
            CCAuftragService auftragService = getCCService(CCAuftragService.class);
            AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragId(auftragId);

            if (auftragDaten != null) {
                BillingAuftragService ls = getBillingService(BillingAuftragService.class);
                List views = ls.findAuftragLeistungViews4Auftrag(auftragDaten.getAuftragNoOrig(), true, true);

                if ((views != null) && (!views.isEmpty())) {
                    Iterator dataIterator = views.iterator();
                    List list = new ArrayList();
                    while (dataIterator.hasNext()) {
                        BAuftragLeistungView l = (BAuftragLeistungView) dataIterator.next();
                        LeistungService leistungService = getBillingService(LeistungService.class);
                        Map map2 = new HashMap();
                        map2.put(MENGE, (l != null) ? l.getMenge() : null);
                        map2.put(RECHNUNGSTEXT, (l != null) ? leistungService.findRechnungstext(l.getLeistungNoOrig(), l.getAuftragPosParameter(), null) : null);
                        list.add(map2);
                    }
                    map.put(getPropName(LEISTUNGEN), (!list.isEmpty()) ? list : null);

                    if (!list.isEmpty()) {
                        map.put(getPropName(PRINT_LEISTUNGEN), "true");
                    }
                    else {
                        map.put(getPropName(PRINT_LEISTUNGEN), "false");
                        return;
                    }
                }
            }

            AccountService accs = getCCService(AccountService.class);
            IntAccount intAccount = accs.findIntAccount("" + kundeNoOrig, IntAccount.LINR_VERWALTUNGSACCOUNT);
            map.put(getPropName(ACCOUNT), (intAccount != null) ? intAccount.getAccount() : null);
            map.put(getPropName(PASSWORT), (intAccount != null) ? intAccount.getPasswort() : null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(e);
        }
    }

    /* Ueberprueft, ob die Command-Klasse richtig konfiguriert wurde. */
    private void checkValues() throws HurricanServiceCommandException {
        Object tmpId = getPreparedValue(AUFTRAG_ID);
        auftragId = (tmpId instanceof Long) ? (Long) tmpId : null;
        tmpId = getPreparedValue(KUNDE_NO_ORIG);
        kundeNoOrig = (tmpId instanceof Long) ? (Long) tmpId : null;
        if ((auftragId == null) || (kundeNoOrig == null)) {
            throw new HurricanServiceCommandException("AuftragId und KundeNo wurde dem Command-Objekt nicht uebergeben!");
        }
    }

    /**
     * @see de.augustakom.hurrican.service.reporting.impl.command.AbstractReportCommand#getPropertyFile()
     */
    @Override
    public String getPropertyFile() {
        return "/de/augustakom/hurrican/service/reporting/impl/command/resources/GetOnlineAccountDatenCommand.properties";
    }
}


