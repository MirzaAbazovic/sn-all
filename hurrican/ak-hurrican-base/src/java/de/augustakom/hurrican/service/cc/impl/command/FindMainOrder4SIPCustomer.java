/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.12.2010 15:01:05
 */

package de.augustakom.hurrican.service.cc.impl.command;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.ProduktGruppe;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCKundenService;
import de.augustakom.hurrican.service.cc.ProduktService;


/**
 * Command-Klasse, um einen neuen CC-Auftrag anzulegen.
 *
 *
 */
@CcTxRequired
public class FindMainOrder4SIPCustomer extends AbstractServiceCommand {

    private static final Logger LOGGER = Logger.getLogger(FindMainOrder4SIPCustomer.class);

    public static final String KEY_AUFTRAG_ID = "auftrag.id";

    private Long auftragId = null;

    private CCAuftragService ccAuftragService;
    private CCKundenService ccKundenService;
    private ProduktService produktService;
    private KundenService billingKundenService;

    @Override
    public Object execute() throws ServiceCommandException {
        try {
            init();
            loadValues();
            checkValues();

            Long kundeNo = findKundeNo();
            Long hauptKundenNo = findHauptKundenNo(kundeNo);
            AuftragDaten auftragDaten = findMainOrder(hauptKundenNo);
            return auftragDaten;
        }
        catch (HurricanServiceCommandException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(e);
        }
    }

    private void init() throws ServiceNotFoundException {
        ccAuftragService = getCCService(CCAuftragService.class);
        ccKundenService = getCCService(CCKundenService.class);
        produktService = getCCService(ProduktService.class);
        billingKundenService = getBillingService(KundenService.class);
    }

    private void loadValues() throws HurricanServiceCommandException {
        if ((getPreparedValue(KEY_AUFTRAG_ID) != null)
                && (getPreparedValue(KEY_AUFTRAG_ID) instanceof Long)) {
            auftragId = (Long) getPreparedValue(KEY_AUFTRAG_ID);
        }
    }

    private void checkValues() throws HurricanServiceCommandException {
        if (auftragId == null) {
            throw new HurricanServiceCommandException("Auftrag ID wurde nicht an Command-Objekt übergeben!");
        }
    }

    private Long findKundeNo() throws ServiceNotFoundException, FindException, HurricanServiceCommandException {
        Auftrag auftrag = ccAuftragService.findAuftragById(auftragId);
        AuftragDaten auftragDaten = ccAuftragService.findAuftragDatenByAuftragIdTx(auftragId);
        if ((auftrag == null) || (auftragDaten == null)) {
            String message = "Der SIP Inter Trunk Endkundenauftrag mit der ID {0} konnte nicht ermittelt werden.";
            throw new HurricanServiceCommandException(StringTools.formatString(message,
                    new Object[] { String.format("%s", auftragId) }));
        }

        ProduktGruppe produktGruppe = produktService.findPG4Auftrag(auftragDaten.getAuftragId());
        if ((produktGruppe == null)
                || NumberTools.notEqual(produktGruppe.getId(), ProduktGruppe.SIP_INTER_TRUNK_ENDKUNDE)) {
            String message = "Der ermittelte Auftrag mit der ID {0} ist kein SIP Inter Trunk Endkunden Auftrag.";
            throw new HurricanServiceCommandException(StringTools.formatString(message,
                    new Object[] { String.format("%s", auftragId) }));
        }

        if (auftrag.getKundeNo() == null) {
            String message = "Der ermittelte Auftrag mit der ID {0} hat keine gültige Kunden ID.";
            throw new HurricanServiceCommandException(StringTools.formatString(message,
                    new Object[] { String.format("%s", auftragId) }));
        }
        return auftrag.getKundeNo();
    }

    Long findHauptKundenNo(Long kundeNo) throws ServiceNotFoundException, FindException, HurricanServiceCommandException {
        Kunde kunde = billingKundenService.findKunde(kundeNo);
        if (kunde == null) {
            String message = "Der Kunde mit der ID {0} konnte nicht ermittelt werden.";
            throw new HurricanServiceCommandException(StringTools.formatString(message,
                    new Object[] { String.format("%s", kundeNo) }));
        }
        if (kunde.isHauptkunde()) {
            String message = "Der Kunde mit der ID {0} ist bereits Hauptkunde.";
            throw new HurricanServiceCommandException(StringTools.formatString(message,
                    new Object[] { String.format("%s", kundeNo) }));
        }
        if (kunde.getHauptKundenNo() == null) {
            String message = "Zum Kunden mit der ID {0} ist kein Hauptkunde zugeordnet.";
            throw new HurricanServiceCommandException(StringTools.formatString(message,
                    new Object[] { String.format("%s", kundeNo) }));
        }
        return kunde.getHauptKundenNo();
    }

    AuftragDaten findMainOrder(Long hauptKundenNo) throws ServiceNotFoundException, FindException, HurricanServiceCommandException {
        List<Long> produktGruppen = new ArrayList<Long>();
        produktGruppen.add(ProduktGruppe.SIP_INTER_TRUNK);
        List<Long> auftragIds = ccKundenService.findActiveAuftragInProdGruppe(hauptKundenNo, produktGruppen);
        if ((auftragIds == null) || (auftragIds.size() != 1)) {
            String message = "Dem Hauptkunden mit der ID {0} ist kein gültiger Auftrag zugeordnet.";
            throw new HurricanServiceCommandException(StringTools.formatString(message,
                    new Object[] { String.format("%s", hauptKundenNo) }));
        }
        AuftragDaten auftragDaten = ccAuftragService.findAuftragDatenByAuftragIdTx(auftragIds.get(0));
        if (auftragDaten == null) {
            String message = "Der Hauptauftrag mit der ID {0} konnte nicht geladen werden.";
            throw new HurricanServiceCommandException(StringTools.formatString(message,
                    new Object[] { String.format("%s", auftragIds.get(0)) }));
        }
        return auftragDaten;
    }


    public void setCcAuftragService(CCAuftragService ccAuftragService) {
        this.ccAuftragService = ccAuftragService;
    }

    public void setCcKundenService(CCKundenService ccKundenService) {
        this.ccKundenService = ccKundenService;
    }

    public void setProduktService(ProduktService produktService) {
        this.produktService = produktService;
    }

    public void setBillingKundenService(KundenService billingKundenService) {
        this.billingKundenService = billingKundenService;
    }

}
