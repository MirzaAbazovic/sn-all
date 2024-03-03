/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.01.2012 09:35:20
 */
package de.augustakom.hurrican.service.cc.impl.command.cps.mvs;

import java.util.*;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.billing.Leistung;
import de.augustakom.hurrican.model.billing.view.BAuftragLeistungView;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragMVSEnterprise;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.impl.command.cps.AbstractCPSGetDNDataCommand;

/**
 *
 */
public abstract class AbstractGetMVSDataCommand extends AbstractCPSGetDNDataCommand {

    /**
     * Ermittelt die tatsächliche Anzahl aller zum Ausführungszeitpunkt aktiven Leistungen/Auftragspositionen auf dem
     * MVS Enterprise Auftrag aus Taifun, die fuer die CPS-Provisionierun relevant sind.
     *
     * @return die Anzahl an CPS-relevanten Taifun - Leistungen je Auftragsposition unter Beruecksichtigung von
     * Wertelisten (Wertelisten werden in diesem Kontext z.B. fuer Lizenzpakete oder Kanaele verwendet und geben die
     * Anzahl an Leistungen pro Mengenwert je Auftragsposition an)
     * @throws FindException            im Falle eines unerwarteten Fehlers
     * @throws ServiceNotFoundException falls ein benoetigter Service nicht gefunden wurde
     */
    Map<Long, Long> findLeistungen(Long produktId) throws FindException, ServiceNotFoundException {
        Map<Long, Long> lsCntByMiscNo = new HashMap<Long, Long>();

        BillingAuftragService billingAuftragService = getBillingService(BillingAuftragService.class);
        final Long auftragNoOrig = getAuftragDaten().getAuftragNoOrig();
        List<BAuftragLeistungView> auftragLsViews = billingAuftragService
                .findAuftragLeistungViewsByExtProdNoAndExtMiscNOs(auftragNoOrig,
                        produktId, Arrays.asList(Leistung.getExtMiscNos4Mvs()));

        for (BAuftragLeistungView auftragLsView : auftragLsViews) {
            if (auftragLsView.isActiveAt(getCPSTransaction().getEstimatedExecTime())) {
                addToValueForKey(lsCntByMiscNo, auftragLsView.getExternMiscNo(),
                        auftragLsView.calculateLeistungsMenge());
            }
        }

        return lsCntByMiscNo;
    }

    abstract Long getCCAuftragIdMVSEp() throws FindException, ServiceNotFoundException, HurricanServiceCommandException;

    abstract AuftragMVSEnterprise getAuftragMVSEnterprise() throws FindException, ServiceNotFoundException, HurricanServiceCommandException;

    Long findResellerId() throws FindException, ServiceNotFoundException, HurricanServiceCommandException {
        Auftrag auftrag = (getCCAuftragIdMVSEp() != null) ?
                getCCService(CCAuftragService.class).findAuftragById(getCCAuftragIdMVSEp()) : null;
        Long kundeNo = (auftrag != null) ? auftrag.getKundeNo() : null;
        Kunde kunde = (kundeNo != null) ?
                getBillingService(KundenService.class).findKunde(kundeNo) : null;
        Long resellerId = (kunde != null) ? kunde.getResellerKundeNo() : null;
        if (resellerId == null) {
            throw new HurricanServiceCommandException(String.format("Zum techn. Auftrag %s konnte keine Reseller "
                    + "ID ermittelt werden!", getCCAuftragIdMVSEp()));
        }
        return resellerId;
    }

    protected void addToValueForKey(Map<Long, Long> lsCntByMiscNo, Long key, Long value) {
        if ((lsCntByMiscNo.get(key) == null) && (value != null)) {
            lsCntByMiscNo.put(key, value);
        }
        else if (value != null) {
            lsCntByMiscNo.put(key, lsCntByMiscNo.get(key) + value);
        }
    }

}


