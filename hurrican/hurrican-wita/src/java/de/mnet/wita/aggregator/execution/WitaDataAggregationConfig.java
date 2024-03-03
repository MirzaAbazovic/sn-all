/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.07.2011 10:17:04
 */
package de.mnet.wita.aggregator.execution;

import java.util.*;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMultimap;

import de.mnet.wita.aggregator.AbgebenderProviderAggregator;
import de.mnet.wita.aggregator.AktionsCodeAenderungAggregator;
import de.mnet.wita.aggregator.AuftragspositionAggregator;
import de.mnet.wita.aggregator.AuftragspositionLmaeAggregator;
import de.mnet.wita.aggregator.BestandsSucheRexMkAggregator;
import de.mnet.wita.aggregator.BestandsSucheVblAggregator;
import de.mnet.wita.aggregator.KundeAggregator;
import de.mnet.wita.aggregator.LeitungsbezeichnungAggregator;
import de.mnet.wita.aggregator.LeitungsbezeichnungPvAggregator;
import de.mnet.wita.aggregator.MontageleistungAggregator;
import de.mnet.wita.aggregator.ReferencingLeitungsbezeichnungAggregator;
import de.mnet.wita.aggregator.ReferencingSchaltangabenAggregator;
import de.mnet.wita.aggregator.RufnummernPortierungAggregator;
import de.mnet.wita.aggregator.SchaltangabenAggregator;
import de.mnet.wita.aggregator.StandortKollokationAggregator;
import de.mnet.wita.aggregator.StandortKundeAggregator;
import de.mnet.wita.aggregator.StandortKundeRexMkAggregator;
import de.mnet.wita.aggregator.StandortKundeVblAggregator;
import de.mnet.wita.aggregator.VertragsNummerAggregator;
import de.mnet.wita.aggregator.VertragsNummerPvAggregator;
import de.mnet.wita.aggregator.VormieterAggregator;
import de.mnet.wita.aggregator.ZeitfensterAggregator;
import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.message.auftrag.geschaeftsfall.GeschaeftsfallKueKd;
import de.mnet.wita.message.auftrag.geschaeftsfall.GeschaeftsfallLae;
import de.mnet.wita.message.auftrag.geschaeftsfall.GeschaeftsfallLmae;
import de.mnet.wita.message.auftrag.geschaeftsfall.GeschaeftsfallNeu;
import de.mnet.wita.message.auftrag.geschaeftsfall.GeschaeftsfallPv;
import de.mnet.wita.message.auftrag.geschaeftsfall.GeschaeftsfallRexMk;
import de.mnet.wita.message.auftrag.geschaeftsfall.GeschaeftsfallSerPow;
import de.mnet.wita.message.auftrag.geschaeftsfall.GeschaeftsfallVbl;

/**
 * Konfigurations-Objekt, ueber das die fuer einen UseCase notwendigen Aggregator-Objekte bestimmt werden. Es werden
 * dabei nur die Aggregatoren konfiguriert, die abhaengig vom UseCase sind. Standard-Aggregatoren (z.B. {@link
 * KundeAggregator}), die bei jedem UseCase benoetigt werden, werden (zumindest aktuell) hard-coded angezogen und sind
 * somit in der Konfiguration nicht relevant.
 */
public final class WitaDataAggregationConfig {

    private WitaDataAggregationConfig() {
        // This class should never have an instance
    }

    private static final ImmutableMultimap<Class<? extends Geschaeftsfall>, Class<?>> geschaeftsfall2Config = ImmutableMultimap
            .<Class<? extends Geschaeftsfall>, Class<?>>builder()

                    // @formatter:off

            .putAll(GeschaeftsfallKueKd.class,
                    AuftragspositionAggregator.class,
                    LeitungsbezeichnungAggregator.class,
                    VertragsNummerAggregator.class,
                    ZeitfensterAggregator.class)

            .putAll(GeschaeftsfallLae.class,
                    AktionsCodeAenderungAggregator.class,
                    AuftragspositionAggregator.class,
                    MontageleistungAggregator.class,
                    ReferencingLeitungsbezeichnungAggregator.class,
                    SchaltangabenAggregator.class,
                    StandortKollokationAggregator.class,
                    StandortKundeAggregator.class,
                    VertragsNummerAggregator.class,
                    ZeitfensterAggregator.class)

            .putAll(GeschaeftsfallLmae.class,
                    AktionsCodeAenderungAggregator.class,
                    AuftragspositionLmaeAggregator.class,
                    ReferencingLeitungsbezeichnungAggregator.class,
                    ReferencingSchaltangabenAggregator.class,
                    StandortKollokationAggregator.class,
                    StandortKundeAggregator.class,
                    VertragsNummerAggregator.class,
                    ZeitfensterAggregator.class)

            .putAll(GeschaeftsfallNeu.class,
                    AuftragspositionAggregator.class,
                    MontageleistungAggregator.class,
                    SchaltangabenAggregator.class,
                    StandortKollokationAggregator.class,
                    StandortKundeAggregator.class,
                    VormieterAggregator.class,
                    ZeitfensterAggregator.class)

            .putAll(GeschaeftsfallPv.class,
                    AbgebenderProviderAggregator.class,
                    AuftragspositionAggregator.class,
                    LeitungsbezeichnungPvAggregator.class,
                    MontageleistungAggregator.class,
                    SchaltangabenAggregator.class,
                    StandortKollokationAggregator.class,
                    StandortKundeAggregator.class,
                    VertragsNummerPvAggregator.class,
                    ZeitfensterAggregator.class)

            .putAll(GeschaeftsfallRexMk.class,
                    AbgebenderProviderAggregator.class,
                    AuftragspositionAggregator.class,
                    BestandsSucheRexMkAggregator.class,
                    RufnummernPortierungAggregator.class,
                    StandortKundeRexMkAggregator.class,
                    ZeitfensterAggregator.class)

            .putAll(GeschaeftsfallSerPow.class,
                    AuftragspositionAggregator.class,
                    ReferencingLeitungsbezeichnungAggregator.class,
                    MontageleistungAggregator.class,
                    SchaltangabenAggregator.class,
                    StandortKollokationAggregator.class,
                    VertragsNummerAggregator.class,
                    ZeitfensterAggregator.class)

            .putAll(GeschaeftsfallVbl.class,
                    AbgebenderProviderAggregator.class,
                    AuftragspositionAggregator.class,
                    BestandsSucheVblAggregator.class,
                    MontageleistungAggregator.class,
                    RufnummernPortierungAggregator.class,
                    SchaltangabenAggregator.class,
                    StandortKollokationAggregator.class,
                    StandortKundeVblAggregator.class,
                    VertragsNummerPvAggregator.class,
                    ZeitfensterAggregator.class)

            // @formatter:on

            .build();

    /**
     * Ermittelt das zu dem UseCase gehoerende {@link WitaDataAggregationConfig} Objekt und gibt es zurueck.
     *
     * @return ermitteltes {@link WitaDataAggregationConfig} Objekt
     * @throws WitaDataAggregationException wenn zu dem UseCase kein {@link WitaDataAggregationConfig} Objekt gefunden
     *                                      wurde.
     */
    public static Collection<Class<?>> getWitaDataAggregationConfig(Class<? extends Geschaeftsfall> geschaeftsfaell) {
        if (!geschaeftsfall2Config.containsKey(geschaeftsfaell)) {
            throw new WitaDataAggregationException(
                    String.format("Aggregator-Konfiguration fuer Geschaeftsfall %s ist nicht definiert!",
                            geschaeftsfaell)
            );
        }
        ImmutableCollection<Class<?>> aggregatorClasses = geschaeftsfall2Config.get(geschaeftsfaell);
        return aggregatorClasses;
    }

    public static boolean isAggregatorConfiguredForGeschaeftsfall(Class<? extends Geschaeftsfall> geschaeftsfall, Class<?> aggregator) {
        return geschaeftsfall2Config.get(geschaeftsfall).contains(aggregator);
    }

}
