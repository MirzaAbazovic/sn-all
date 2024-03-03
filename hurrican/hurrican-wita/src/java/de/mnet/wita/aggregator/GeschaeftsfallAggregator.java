/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.05.2011 15:34:05
 */
package de.mnet.wita.aggregator;

import static de.mnet.wita.aggregator.execution.WitaDataAggregationExecuter.*;

import java.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.model.cc.CarrierKennung;
import de.augustakom.hurrican.model.cc.Equipment;
import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.auftrag.GeschaeftsfallAnsprechpartner;
import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.message.auftrag.geschaeftsfall.GeschaeftsfallRexMk;
import de.mnet.wita.model.KollokationsTyp;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaSendLimit;

/**
 * Aggregator-Klasse, um die Daten des aktuellen Geschaeftsfalls zu laden.
 */
public class GeschaeftsfallAggregator extends AbstractWitaDataAggregator<Geschaeftsfall> {

    @Autowired
    AnlagenAggregator anlagenAggregator;
    @Autowired
    AnsprechpartnerAmAggregator ansprechpartnerAmAggregator;
    @Autowired
    AnsprechpartnerTechnikAggregator ansprechpartnerTechnikAggregator;
    @Autowired
    KundenwunschterminAggregator kundenwunschterminAggregator;
    @Autowired
    AuftragspositionAggregator auftragspositionAggregator;
    @Autowired
    AuftragspositionLmaeAggregator auftragspositionLmaeAggregator;
    @Autowired
    VertragsNummerAggregator vertragsNummerAggregator;
    @Autowired
    VertragsNummerPvAggregator vertragsNummerPvAggregator;
    @Autowired
    AbgebenderProviderAggregator abgebenderProviderAggregator;

    @Override
    public Geschaeftsfall aggregate(WitaCBVorgang cbVorgang) {
        Class<? extends Geschaeftsfall> geschaeftsfallClazz = cbVorgang.getWitaGeschaeftsfallTyp().getClazz();
        if (geschaeftsfallClazz == null) {
            throw new RuntimeException(
                    "Class of Geschaeftsfall must be set before aggregating data.");
        }
        Geschaeftsfall geschaeftsfall;
        try {
            geschaeftsfall = geschaeftsfallClazz.newInstance();
        }
        catch (Exception e) {
            throw new RuntimeException("could not instantiate " + geschaeftsfallClazz.getCanonicalName()
                    + " using the default constructor");
        }

        if (!isGeschaeftsfallAllowed(geschaeftsfall, cbVorgang)) {
            throw new WitaDataAggregationException(
                    String.format("Geschaeftsfall %s ist fuer die gewaehlte TAL nicht erlaubt!", geschaeftsfall.getGeschaeftsfallTyp().name()));
        }

        CarrierKennung carrierKennung = (cbVorgang.isRexMk())
                ? witaDataService.loadCarrierKennungForRexMk(cbVorgang)
                : witaDataService.loadCarrierKennung(cbVorgang);
        geschaeftsfall.setBktoFatkura(carrierKennung.getBktoNummer());

        // nachfolgende Entitaeten sind bei jedem Geschaeftsfall notwendig bzw. erlaubt - daher nicht ueber
        // WitaDataAggregationConfig ermittelt!
        GeschaeftsfallAnsprechpartner ansprechpartner = new GeschaeftsfallAnsprechpartner();
        ansprechpartner.setAuftragsmanagement(ansprechpartnerAmAggregator.aggregate(cbVorgang));
        ansprechpartner.setAnsprechpartner(ansprechpartnerTechnikAggregator.aggregate(cbVorgang));
        geschaeftsfall.setGfAnsprechpartner(ansprechpartner);
        geschaeftsfall.setAnlagen(anlagenAggregator.aggregate(cbVorgang));

        geschaeftsfall.setKundenwunschtermin(kundenwunschterminAggregator.aggregate(cbVorgang));
        executeAggregator(cbVorgang, auftragspositionAggregator, geschaeftsfall);
        executeAggregator(cbVorgang, auftragspositionLmaeAggregator, geschaeftsfall);
        executeAggregator(cbVorgang, abgebenderProviderAggregator, geschaeftsfall);
        executeAggregator(cbVorgang, vertragsNummerAggregator, geschaeftsfall, Geschaeftsfall.VERTRAGS_NUMMER_SETTER);
        executeAggregator(cbVorgang, vertragsNummerPvAggregator, geschaeftsfall, Geschaeftsfall.VERTRAGS_NUMMER_SETTER);

        return geschaeftsfall;
    }


    /**
     * Ueberprueft, ob die Kombination aus {@link GeschaeftsfallTyp} und {@link KollokationsTyp} grundsaetzlich erlaubt
     * ist.
     *
     * @param geschaeftsfall
     * @param cbVorgang
     * @return
     */
    boolean isGeschaeftsfallAllowed(Geschaeftsfall geschaeftsfall, WitaCBVorgang cbVorgang) {
        if (geschaeftsfall instanceof GeschaeftsfallRexMk) {
            // REX-MK wird immer zugelassen!
            return true;
        }

        List<Equipment> dtagEquipments = witaDataService.loadDtagEquipments(cbVorgang);
        if (CollectionUtils.isNotEmpty(dtagEquipments)) {
            KollokationsTyp kollokationsTyp = (dtagEquipments.iterator().next().isPortForKvzTal()) ? KollokationsTyp.FTTC_KVZ : KollokationsTyp.HVT;
            WitaSendLimit sendLimit = witaConfigService.findWitaSendLimit(geschaeftsfall.getGeschaeftsfallTyp().name(), kollokationsTyp, null);
            if ((sendLimit != null) && !BooleanTools.nullToFalse(sendLimit.getAllowed())) {
                return false;
            }
        }
        return true;
    }

}
