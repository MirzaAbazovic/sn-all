/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.05.2013 13:19:03
 */
package de.mnet.hurrican.scheduler.job.wita;

import java.util.*;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;

import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.billing.Leistung;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CreateVerlaufParameter;
import de.mnet.hurrican.scheduler.HurricanScheduler;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * Scheduler-Job, der alle positiv bestätigten WITA-Bestellungen (Neu|Aenderung|Anbieterwechsel) mit gesetztem
 * Automatismus-Flag ermittelt und abschliesst.
 */
public class AutomaticallyProcessWitaOrdersJob extends AbstractProcessWitaResponseJob {

    private static final Logger LOGGER = Logger.getLogger(AutomaticallyProcessWitaOrdersJob.class);

    @Override
    protected Long[] getCbVorgangTypes() {
        return new Long[] { CBVorgang.TYP_NEU, CBVorgang.TYP_PORTWECHSEL, CBVorgang.TYP_ANBIETERWECHSEL };
    }

    @Override
    protected void closeWitaAutomatically(WitaCBVorgang witaCbVorgang, JobExecutionContext context)
            throws StoreException, ValidationException, FindException {
        WitaCBVorgang witaCbVorgang1 = witaCbVorgang;
        AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragId(witaCbVorgang1.getAuftragId());
        AuftragDaten referencedOrder = carrierService.findReferencingOrder(witaCbVorgang1);
        Long referencedOrderNoOrig = referencedOrder != null ? referencedOrder.getAuftragNoOrig() : null;

        elektraFacadeService.changeOrderRealDate(
                auftragDaten, referencedOrderNoOrig, witaCbVorgang1, null);
        witaTalOrderService.writeDataOntoCarrierbestellung(HurricanScheduler.getSessionId(), witaCbVorgang1);

        // Bauauftrag erstellen
        boolean bauauftragCreated = createBauauftrag(auftragDaten, witaCbVorgang1);
        if (referencedOrder != null) {
            createBauauftragKuendigung(referencedOrder, witaCbVorgang1.getReturnRealDate());
        }

        if (bauauftragCreated) {
            // nur wenn der BA erstellt wurde wird auch das Kundenanschreiben erstellt und der Vorgang geschlossen!
            elektraFacadeService.generateAndPrintReportWithEvaluation(auftragDaten, witaCbVorgang1);

            // HUR-23864 Auftragsart auf ABW_TKG46_NEUSCHALTUNG ändern, wenn es sich um Anbieterwechsel handelt
            if (BooleanTools.nullToFalse(witaCbVorgang1.getAnbieterwechselTkg46())) {
                AuftragTechnik auftragTechnik = auftragService.findAuftragTechnikByAuftragId(witaCbVorgang1.getAuftragId());
                if (BAVerlaufAnlass.NEUSCHALTUNG.equals(auftragTechnik.getAuftragsart())) {
                    auftragTechnik.setAuftragsart(BAVerlaufAnlass.ABW_TKG46_NEUSCHALTUNG);
                    auftragService.saveAuftragTechnik(auftragTechnik, false);
                }
            }

            // WITA Vorgang erst zum Schluss schliessen!
            witaCbVorgang1 = (WitaCBVorgang) carrierElTalService.findCBVorgang(witaCbVorgang1.getId());
            witaCbVorgang1.close();
            carrierElTalService.saveCBVorgang(witaCbVorgang1);
        }
    }

    private boolean createBauauftrag(AuftragDaten auftragDaten, CBVorgang cbVorgang) throws StoreException, FindException {
        CreateVerlaufParameter parameter = new CreateVerlaufParameter();
        parameter.setAnlass(Boolean.TRUE.equals(cbVorgang.getAnbieterwechselTkg46()) ?
                BAVerlaufAnlass.ABW_TKG46_NEUSCHALTUNG : BAVerlaufAnlass.NEUSCHALTUNG);
        parameter.setAuftragId(auftragDaten.getAuftragId());
        parameter.setSubAuftragsIds(null);
        parameter.setRealisierungsTermin(cbVorgang.getReturnRealDate());
        parameter.setInstallType(getBaInstallType(auftragDaten));
        parameter.setAnZentraleDispo(false);
        parameter.setSessionId(HurricanScheduler.getSessionId());

        Pair<Verlauf, AKWarnings> result = baService.createVerlauf(parameter);
        return result.getFirst() != null;
    }

    private boolean createBauauftragKuendigung(AuftragDaten auftragDaten, Date realDate) throws StoreException, FindException {
        CreateVerlaufParameter parameter = new CreateVerlaufParameter();
        parameter.setAnlass(BAVerlaufAnlass.KUENDIGUNG);
        parameter.setAuftragId(auftragDaten.getAuftragId());
        parameter.setSubAuftragsIds(null);
        parameter.setRealisierungsTermin(realDate);
        parameter.setAnZentraleDispo(false);
        parameter.setSessionId(HurricanScheduler.getSessionId());

        Pair<Verlauf, AKWarnings> result = baService.createVerlauf(parameter);
        return result.getFirst() != null;
    }


    /*
     * Ermittelt den Installations-Typ (Selbstmontage / Montage M-net) zu dem Auftrag.
     * Eine Montage M-net wird dann verwendet, wenn der Auftrag eine noch nicht abgerechnete Montage-Leistung besitzt!
     */
    private Long getBaInstallType(AuftragDaten auftragDaten) {
        try {
            if (billingAuftragService.hasUnchargedServiceElementsWithExtMiscNo(auftragDaten.getAuftragNoOrig(), Leistung.EXT_MISC_NO_MONTAGE_MNET)) {
                return Reference.REF_ID_MONTAGE_MNET;
            }

            return Reference.REF_ID_MONTAGE_CUSTOMER;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

}


