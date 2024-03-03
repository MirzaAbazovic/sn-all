/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.08.2011 14:47:34
 */
package de.mnet.wita.bpm.tasks;

import static de.mnet.wita.bpm.WitaTaskVariables.*;

import java.util.*;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.hurrican.model.cc.CarrierKennung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.mnet.wita.RuemPvAntwortCode;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.bpm.converter.usertask.RuemPvUserTaskConverter;
import de.mnet.wita.bpm.variables.ActivitiVariableUtils;
import de.mnet.wita.exceptions.WitaBpmException;
import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.meldung.AnkuendigungsMeldungPv;
import de.mnet.wita.message.meldung.RueckMeldungPv;
import de.mnet.wita.message.meldung.position.AbgebenderProvider;
import de.mnet.wita.message.meldung.position.MeldungsPosition;

public class SendRuemPvTask extends AbstractSendingWitaTask {

    @Autowired
    private CarrierService carrierService;
    @Autowired
    private RuemPvUserTaskConverter ruemPvUserTaskConverter;

    @Override
    protected void send(DelegateExecution execution, WitaCdmVersion witaCdmVersion) throws Exception {
        verifyMeldungsType(execution, MeldungsType.RUEM_PV);
        RuemPvAntwortCode antwortCode = ActivitiVariableUtils.extractVariableSilent(execution, RUEM_PV_ANTWORTCODE,
                RuemPvAntwortCode.class, null);
        if (antwortCode == null) {
            throw new WitaBpmException("RuemPvAntwortCode ist nicht im Workflow hinterlegt!");
        }

        // Optional variables
        String antwortText = (String) execution.getVariable(RUEM_PV_ANTWORTTEXT.id);

        AnkuendigungsMeldungPv akmPv = extractAkmPv(execution);
        RueckMeldungPv ruemPv = createRuemPv(akmPv, antwortCode, antwortText, witaCdmVersion);
        workflowTaskService.sendToWita(ruemPv);
        ruemPvUserTaskConverter.write(ruemPv);
    }

    private RueckMeldungPv createRuemPv(AnkuendigungsMeldungPv akmPv, RuemPvAntwortCode antwortCode,
            String antwortText, WitaCdmVersion witaCdmVersion) throws FindException {
        RueckMeldungPv ruemPv = new RueckMeldungPv();
        ruemPv.setExterneAuftragsnummer(akmPv.getExterneAuftragsnummer());

        ruemPv.setKundenNummer(akmPv.getKundenNummer());
        if (witaCdmVersion.isGreaterOrEqualThan(WitaCdmVersion.V2) &&
                !CarrierKennung.DTAG_KUNDEN_NR_MNET.equals(akmPv.getKundenNummer())) {
            ruemPv.setKundennummerBesteller(CarrierKennung.DTAG_KUNDEN_NR_MNET);
        }

        ruemPv.setVertragsNummer(akmPv.getVertragsNummer());
        ruemPv.setGeschaeftsfallTyp(akmPv.getGeschaeftsfallTyp());
        ruemPv.setAenderungsKennzeichen(akmPv.getAenderungsKennzeichen());
        ruemPv.setLeitung(akmPv.getLeitung());
        ruemPv.setAnschlussOnkz(akmPv.getAnschlussOnkz());
        ruemPv.setAnschlussRufnummer(akmPv.getAnschlussRufnummer());
        ruemPv.setVersandZeitstempel(new Date());
        ruemPv.setCdmVersion(witaCdmVersion);

        ruemPv.setAbgebenderProvider(getAbgebenderProvider(antwortCode, antwortText, getProviderName(akmPv)));

        MeldungsPosition meldungsPosition = new MeldungsPosition(RueckMeldungPv.MELDUNGSCODE,
                RueckMeldungPv.MELDUNGSTEXT);
        ruemPv.getMeldungsPositionen().add(meldungsPosition);

        return ruemPv;
    }

    private String getProviderName(final AnkuendigungsMeldungPv akmPv) throws FindException {
        Iterable<CarrierKennung> carrierKennungen = carrierService.findCarrierKennungen();
        carrierKennungen = Iterables.filter(carrierKennungen, new Predicate<CarrierKennung>() {

            @Override
            public boolean apply(CarrierKennung input) {
                return StringUtils.equals(input.getKundenNr(), akmPv.getKundenNummer());
            }
        });

        if (Iterables.size(carrierKennungen) > 1) {
            // Kundennummer Muenchen
            carrierKennungen = Iterables.filter(carrierKennungen, new Predicate<CarrierKennung>() {

                @Override
                public boolean apply(CarrierKennung input) {
                    return input.getOrt().equals("München");
                }
            });
        }

        CarrierKennung carrierKennung = Iterables.getOnlyElement(carrierKennungen);
        if (carrierKennung == null) {
            throw new WitaDataAggregationException("Keine Carrierkennung gefunden fuer Kundennummer "
                    + akmPv.getKundenNummer());
        }
        return carrierKennung.getBezeichnung();
    }

    private AnkuendigungsMeldungPv extractAkmPv(DelegateExecution execution) {
        Long akmPvId = (Long) execution.getVariable(AKM_PV_ID.id);
        if (akmPvId == null) {
            throw new WitaBpmException("Missing akmPvId");
        }

        AnkuendigungsMeldungPv akmPv = mwfEntityDao.findById(akmPvId, AnkuendigungsMeldungPv.class);

        if (akmPv == null) {
            throw new WitaBpmException("Could not find corresponding akmPv");
        }
        return akmPv;
    }

    private AbgebenderProvider getAbgebenderProvider(RuemPvAntwortCode antwortCode, String antwortText,
            String providerName) {
        AbgebenderProvider abgebenderProvider = new AbgebenderProvider();

        if (StringUtils.isNotBlank(antwortText)) {
            if (!antwortCode.antwortTextRequired) {
                throw new WitaDataAggregationException("Kein Antworttext erlaubt für Antwortcode " + antwortCode);
            }
            abgebenderProvider.setAntwortText(antwortText);
        }
        if (!RuemPvAntwortCode.OK.equals(antwortCode)) {
            abgebenderProvider.setAntwortCode(antwortCode);
        }
        abgebenderProvider.setProviderName(providerName);
        abgebenderProvider.setZustimmungProviderWechsel(antwortCode.zustimmung);

        return abgebenderProvider;
    }

}
