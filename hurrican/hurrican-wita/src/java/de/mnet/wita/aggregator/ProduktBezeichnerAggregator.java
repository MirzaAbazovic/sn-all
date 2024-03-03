/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.05.2011 12:57:08
 */
package de.mnet.wita.aggregator;

import static com.google.common.base.Predicates.*;

import java.util.*;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import de.mnet.wita.aggregator.predicates.HvtTalPredicate;
import de.mnet.wita.aggregator.predicates.KvzTalPredicate;
import de.mnet.wita.aggregator.predicates.RexMkAnlagenrufnummerPredicate;
import de.mnet.wita.aggregator.predicates.RexMkEinzelrufnummerPredicate;
import de.mnet.wita.aggregator.predicates.RexMkMehrfachrufnummerPredicate;
import de.mnet.wita.aggregator.predicates.Tal2DrahtKvzPredicate;
import de.mnet.wita.aggregator.predicates.Tal2DrahtPredicate;
import de.mnet.wita.aggregator.predicates.Tal4DrahtKvzPredicate;
import de.mnet.wita.aggregator.predicates.Tal4DrahtPredicate;
import de.mnet.wita.aggregator.predicates.TalHochbitPredicate;
import de.mnet.wita.aggregator.predicates.TalNiederbitPredicate;
import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.auftrag.Auftragsposition.ProduktBezeichner;

/**
 * Spezieller Aggregator fuer die Ermittlung des {@link ProduktBezeichner}. Dieser spezielle Aggregator benoetigt als
 * Input den WITA Auftrag; er ermittelt keine Daten aus der Datenbank! <br> <br> Pro ProduktBezeichner sind Predicates
 * definiert, die aus einem WITA {@link Auftrag} Objekt Daten ermitteln (z.B. die Schaltungsangaben) und an Hand dieser
 * Daten eine true/false Entscheidung treffen. Die Kombination der Predicates muss/soll dazu fuehren, dass genau ein
 * {@link ProduktBezeichner} ermittelt wird.
 */
public class ProduktBezeichnerAggregator {

    @SuppressWarnings("unchecked")
    private static Multimap<ProduktBezeichner, Predicate<Auftrag>> produktBezeichnerConfig = ImmutableMultimap.<ProduktBezeichner, Predicate<Auftrag>>builder()
            // @formatter:off
            .putAll(ProduktBezeichner.HVT_2N,
                    new HvtTalPredicate(),
                    not(new KvzTalPredicate()),
                    new Tal2DrahtPredicate(),
                    not(new Tal4DrahtPredicate()),
                    new TalNiederbitPredicate(),
                    not(new TalHochbitPredicate()))

            .putAll(ProduktBezeichner.HVT_2H,
                    new HvtTalPredicate(),
                    not(new KvzTalPredicate()),
                    new Tal2DrahtPredicate(),
                    not(new Tal4DrahtPredicate()),
                    not(new TalNiederbitPredicate()),
                    new TalHochbitPredicate())

            .putAll(ProduktBezeichner.HVT_4H,
                    new HvtTalPredicate(),
                    not(new KvzTalPredicate()),
                    not(new Tal2DrahtPredicate()),
                    new Tal4DrahtPredicate(),
                    not(new TalNiederbitPredicate()),
                    new TalHochbitPredicate())

            .putAll(ProduktBezeichner.KVZ_2H,
                    not(new HvtTalPredicate()),
                    new KvzTalPredicate(),
                    new Tal2DrahtKvzPredicate())

            .putAll(ProduktBezeichner.KVZ_4H,
                    not(new HvtTalPredicate()),
                    new KvzTalPredicate(),
                    new Tal4DrahtKvzPredicate())

            .putAll(ProduktBezeichner.TAL_REXMK_ONE,
                    not(new HvtTalPredicate()),
                    not(new KvzTalPredicate()),
                    new RexMkEinzelrufnummerPredicate())

            .putAll(ProduktBezeichner.TAL_REXMK_TWO,
                    not(new HvtTalPredicate()),
                    not(new KvzTalPredicate()),
                    new RexMkAnlagenrufnummerPredicate())

            .putAll(ProduktBezeichner.TAL_REXMK_MUL,
                    not(new HvtTalPredicate()),
                    not(new KvzTalPredicate()),
                    new RexMkMehrfachrufnummerPredicate())

            .build();
            // @formatter:on

    /**
     * Durchlaeuft die Konfigurationen und speichert die matchenden {@link ProduktBezeichner} ab. <br>
     *
     * @param auftrag WITA {@link Auftrag}, von dem die Daten ermittelt werden sollen
     * @return der ermittelte {@link ProduktBezeichner}
     * @throws WitaDataAggregationException wenn kein {@link ProduktBezeichner} oder mehr als ein {@link
     *                                      ProduktBezeichner} ermittelt wurde
     */
    public ProduktBezeichner aggregate(Auftrag auftrag) throws WitaDataAggregationException {
        List<ProduktBezeichner> matchingProduktBezeichner = new ArrayList<ProduktBezeichner>();

        for (ProduktBezeichner currentProduktBezeichner : produktBezeichnerConfig.keySet()) {
            Collection<Predicate<Auftrag>> predicates = produktBezeichnerConfig.get(currentProduktBezeichner);
            if (Predicates.and(predicates).apply(auftrag)) {
                matchingProduktBezeichner.add(currentProduktBezeichner);
            }
        }

        if (matchingProduktBezeichner.size() > 2) {
            StringBuilder produktBezeichner = new StringBuilder();
            for (ProduktBezeichner pb : matchingProduktBezeichner) {
                if (produktBezeichner.length() > 0) {
                    produktBezeichner.append(", ");
                }
                produktBezeichner.append(pb.name());
            }

            throw new WitaDataAggregationException(String.format(
                    "Es wurden mehrere moegliche ProduktBezeichner gefunden: %s", produktBezeichner.toString()));
        }
        else if (matchingProduktBezeichner.size() == 1) {
            ProduktBezeichner produktBezeichner = matchingProduktBezeichner.get(0);
            return produktBezeichner;
        }

        throw new WitaDataAggregationException("ProduktBezeichner konnte nicht ermittelt werden!");
    }

}
