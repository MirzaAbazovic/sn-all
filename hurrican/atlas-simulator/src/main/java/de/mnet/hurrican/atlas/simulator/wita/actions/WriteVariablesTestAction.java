/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.01.2015
 */
package de.mnet.hurrican.atlas.simulator.wita.actions;

import java.util.*;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import org.apache.commons.lang.StringUtils;

import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.shared.iface.AddressModel;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.hurrican.atlas.simulator.wita.WitaLineOrderVariableNames;

/**
 *
 */
public class WriteVariablesTestAction extends AbstractWitaTestAction {

    private String externalOrderId;
    private CarrierElTALService carrierElTALService;
    private EndstellenService endstellenService;
    private CCAuftragService ccAuftragService;
    private RufnummerService rufnummerService;

    public WriteVariablesTestAction(String externalOrderId, CarrierElTALService carrierElTALService,
            EndstellenService endstellenService, CCAuftragService ccAuftragService, RufnummerService rufnummerService) {
        super("WriteVariablesTestAction");
        this.externalOrderId = externalOrderId;
        assert !StringUtils.isEmpty(externalOrderId);
        this.carrierElTALService = carrierElTALService;
        this.endstellenService = endstellenService;
        this.ccAuftragService = ccAuftragService;
        this.rufnummerService = rufnummerService;
    }

    @Override
    public void doExecute(TestContext context) {
        WitaCBVorgang cbVorgang = (WitaCBVorgang) carrierElTALService.findCBVorgangByCarrierRefNr(externalOrderId);
        assert cbVorgang.getAuftragId() != null;
        try {
            exportAddressDataToVariables(context, cbVorgang.getAuftragId());
            exportRufnummernToVariables(context, cbVorgang.getAuftragId());
        }
        catch (FindException e) {
            throw new CitrusRuntimeException("Unexpected error occured during setting test variables", e);
        }
    }

    private void exportAddressDataToVariables(TestContext context, Long auftragId) throws FindException {
        AddressModel adresseStandort = endstellenService.findAnschlussadresse4Auftrag(auftragId,
                Endstelle.ENDSTELLEN_TYP_B);
        context.setVariable(WitaLineOrderVariableNames.ENDKUNDE_NACHNAME, adresseStandort.getName());
        if (adresseStandort.getVorname() != null) {
            context.setVariable(WitaLineOrderVariableNames.ENDKUNDE_VORNAME, adresseStandort.getVorname());
        }
        context.setVariable(WitaLineOrderVariableNames.STANDORT_STRASSE, adresseStandort.getStrasse());
        context.setVariable(WitaLineOrderVariableNames.STANDORT_HNR, adresseStandort.getNummer());
        String hausnummerZusatz = adresseStandort.getHausnummerZusatz() == null ? "" : adresseStandort.getHausnummerZusatz();
        context.setVariable(WitaLineOrderVariableNames.STANDORT_HNR_ZUSATZ, hausnummerZusatz);
        context.setVariable(WitaLineOrderVariableNames.STANDORT_PLZ, adresseStandort.getPlz());
        context.setVariable(WitaLineOrderVariableNames.STANDORT_ORT, adresseStandort.getOrt());
        String ortsteil = adresseStandort.getOrtsteil() == null ? "" : adresseStandort.getOrtsteil();
        context.setVariable(WitaLineOrderVariableNames.STANDORT_ORTSTEIL, ortsteil);
    }

    private void exportRufnummernToVariables(TestContext context, Long auftragId) throws FindException {
        AuftragDaten ad = ccAuftragService.findAuftragDatenByAuftragIdTx(auftragId);
        List<Rufnummer> dns = rufnummerService.findRNs4Auftrag(ad.getAuftragNoOrig());
        int count = 0;
        for (Rufnummer dn : dns) {
            context.setVariable(String.format(WitaLineOrderVariableNames.RUFNUMMER_ONKZ, count), dn.getOnKzWithoutLeadingZeros());
            context.setVariable(String.format(WitaLineOrderVariableNames.RUFNUMMER_DNBASE, count), dn.getDnBase());
            if (dn.isBlock()) {
                context.setVariable(String.format(WitaLineOrderVariableNames.RUFNUMMER_DIRECT_DIAL, count), dn.getDirectDial());
                context.setVariable(String.format(WitaLineOrderVariableNames.RUFNUMMER_RANGE_FROM, count), dn.getRangeFrom());
                context.setVariable(String.format(WitaLineOrderVariableNames.RUFNUMMER_RANGE_TO, count), dn.getRangeTo());
            }
            count++;
        }
    }

}
