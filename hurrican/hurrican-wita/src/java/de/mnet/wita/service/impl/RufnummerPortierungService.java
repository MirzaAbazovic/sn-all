/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.10.2011 17:38:29
 */
package de.mnet.wita.service.impl;

import java.util.*;
import javax.annotation.*;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.TNB;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.common.portierung.EinzelanschlussRufnummer;
import de.mnet.wita.message.common.portierung.RufnummernBlock;
import de.mnet.wita.message.common.portierung.RufnummernPortierung;
import de.mnet.wita.message.common.portierung.RufnummernPortierungAnlagenanschluss;
import de.mnet.wita.message.common.portierung.RufnummernPortierungEinzelanschluss;

@CcTxRequired
public class RufnummerPortierungService {

    private static final ImmutableMap<String, Long> carrierStringToId = ImmutableMap.<String, Long>builder()
            .put(TNB.MNET.carrierNameUC, Carrier.ID_MNET)
            .put(TNB.AKOM.carrierNameUC, Carrier.ID_AKOM)
            .put(TNB.NEFKOM.carrierNameUC, Carrier.ID_NEFKOM)
            .put(TNB.MNET_NGN.carrierNameUC, Carrier.ID_MNET_NGN)
            .build();

    /**
     * public for testing
     */
    @Resource(name = "de.augustakom.hurrican.service.cc.CarrierService")
    public CarrierService carrierService;

    public RufnummernPortierung transformToRufnummerPortierung(Collection<Rufnummer> rufnummern, boolean setPortierungskenner) {
        if (rufnummern.isEmpty()) {
            return null;
        }

        if (Iterables.all(rufnummern, Rufnummer.IS_EINZELANSCHLUSS)) {
            return createEinzelanschluss(rufnummern, setPortierungskenner);
        }
        else if (Iterables.all(rufnummern, Predicates.not(Rufnummer.IS_EINZELANSCHLUSS))) {
            return createAnlagenanschluss(rufnummern, setPortierungskenner);
        }
        else {
            throw new WitaDataAggregationException(
                    "Wita Schnitstelle unterstützt entweder Einzelrufnummern oder Anlagenanschlüsse, aber nicht beides!");
        }
    }

    private RufnummernPortierungEinzelanschluss createEinzelanschluss(Collection<Rufnummer> rufnummern, boolean setPortierungskenner) {
        RufnummernPortierungEinzelanschluss einzelanschluss = new RufnummernPortierungEinzelanschluss();
        List<EinzelanschlussRufnummer> einzelanschlussRufnummern = einzelanschluss.getRufnummern();
        Set<String> carrierSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (Rufnummer rufnummer : rufnummern) {
            EinzelanschlussRufnummer einzelanschlussRufnummer = new EinzelanschlussRufnummer();
            einzelanschlussRufnummer.setOnkz(rufnummer.getOnKzWithoutLeadingZeros());
            einzelanschlussRufnummer.setRufnummer(rufnummer.getDnBase());

            einzelanschlussRufnummern.add(einzelanschlussRufnummer);

            carrierSet.add(rufnummer.getActCarrier());
        }
        if (setPortierungskenner) {
            setPortierungskenner(einzelanschluss, carrierSet);
        }
        return einzelanschluss;
    }

    private RufnummernPortierungAnlagenanschluss createAnlagenanschluss(Collection<Rufnummer> rufnummern, boolean setPortierungskenner) {
        RufnummernPortierungAnlagenanschluss anlagenanschluss = new RufnummernPortierungAnlagenanschluss();

        Set<String> carrierSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        Set<String> abfrageStelleSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        Set<String> durchwahlSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        Set<String> onkzSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

        List<RufnummernBlock> rufnummernBloecke = anlagenanschluss.getRufnummernBloecke();
        for (Rufnummer rufnummer : rufnummern) {
            RufnummernBlock block = new RufnummernBlock();
            block.setVon(rufnummer.getRangeFrom());
            block.setBis(rufnummer.getRangeTo());
            rufnummernBloecke.add(block);

            abfrageStelleSet.add(rufnummer.getDirectDial());
            durchwahlSet.add(rufnummer.getDnBase());
            carrierSet.add(rufnummer.getActCarrier());
            onkzSet.add(rufnummer.getOnKzWithoutLeadingZeros());
        }
        if (setPortierungskenner) {
            setPortierungskenner(anlagenanschluss, carrierSet);
        }
        anlagenanschluss.setAbfragestelle(getOnlyElement(abfrageStelleSet, "Abfragestelle"));
        anlagenanschluss.setDurchwahl(getOnlyElement(durchwahlSet, "Durchwahl"));
        anlagenanschluss.setOnkz(getOnlyElement(onkzSet, "Onkz"));
        return anlagenanschluss;
    }

    private String getOnlyElement(Set<String> set, String entity) {
        if (set.isEmpty()) {
            throw new WitaDataAggregationException(entity + " muss gesetzt sein.");
        }
        if (set.size() > 1) {
            throw new WitaDataAggregationException("Wita Schnittstelle unterstützt nur eine " + entity + " pro Auftrag");
        }
        return Iterables.getOnlyElement(set);
    }

    private void setPortierungskenner(RufnummernPortierung portierung, Set<String> carrierSet) {
        if (carrierSet.size() > 1) {
            throw new WitaDataAggregationException(
                    "Wita Schnittstelle unterstützt nur eine Providerkennung pro Auftrag");
        }
        String carrierString = Iterables.getOnlyElement(carrierSet).toUpperCase();
        if (!carrierStringToId.containsKey(carrierString)) {
            throw new WitaDataAggregationException("Unbekannter Carrier: " + carrierString);
        }
        Long carrierId = carrierStringToId.get(carrierString);
        try {
            Carrier carrier = carrierService.findCarrier(carrierId);
            portierung.setPortierungsKenner(carrier.getElTalEmpfId());
        }
        catch (FindException e) {
            throw new WitaDataAggregationException("Kein Carrier mit Id gefunden: Id: " + carrierId);
        }
    }

}
