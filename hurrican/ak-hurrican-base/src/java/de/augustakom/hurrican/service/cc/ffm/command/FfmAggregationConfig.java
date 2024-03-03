/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.09.14
 */
package de.augustakom.hurrican.service.cc.ffm.command;

import java.util.*;
import com.google.common.collect.ImmutableMultimap;

import de.augustakom.hurrican.exceptions.FFMServiceException;
import de.augustakom.hurrican.model.cc.ffm.FfmAggregationStrategy;

/**
 * Konfigurations-Klasse, um verschiedene Varianten von FFM-Aggregatoren zu definieren.
 */
public final class FfmAggregationConfig {

    private FfmAggregationConfig() {
        // This class should never have an instance
    }

    @SuppressWarnings("unchecked")
    private static final ImmutableMultimap<FfmAggregationStrategy, Class<? extends AbstractFfmCommand>> ffmAggregatorConfig =
            ImmutableMultimap.<FfmAggregationStrategy, Class<? extends AbstractFfmCommand>>builder()

            // @formatter:off

            .putAll(FfmAggregationStrategy.HEADER_ONLY_WITH_TIMESLOT,
                    AggregateFfmHeaderIdsCommand.class,
                    AggregateFfmHeaderDescription4InterneArbeitCommand.class,
                    AggregateFfmHeaderContactsCommand.class,
                    AggregateFfmHeaderLocation4InterneArbeitCommand.class,
                    AggregateFfmHeaderTimeslotCommand.class
            )

            .putAll(FfmAggregationStrategy.HOUSING,
                    AggregateFfmHeaderIdsCommand.class,
                    AggregateFfmHeaderDescriptionCommand.class,
                    AggregateFfmHeaderContactsCommand.class,
                    AggregateFfmHeaderLocationHousingCommand.class,
                    AggregateFfmHeaderTimeslotCommand.class,
                    AggregateFfmTechnicalCommand.class,
                    AggregateFfmTechnicalCommonCommand.class,
                    AggregateFfmTechnicalHousingCommand.class,
                    AggregateFfmTechnicalLeistungenCommand.class
            )

            .putAll(FfmAggregationStrategy.TECHNICAL_PARAMS_INCLUDED_WITH_TIMESLOT,
                    AggregateFfmHeaderIdsCommand.class,
                    AggregateFfmHeaderDescriptionCommand.class,
                    AggregateFfmHeaderContactsCommand.class,
                    AggregateFfmHeaderLocationCommand.class,
                    AggregateFfmHeaderTimeslotCommand.class,
                    AggregateFfmTechnicalCommand.class,
                    AggregateFfmTechnicalCommonCommand.class,
                    AggregateFfmTechnicalDialUpAccessCommand.class,
                    AggregateFfmTechnicalDialUpAccessVoipCommand.class,
                    AggregateFfmTechnicalSiteCommand.class,
                    AggregateFfmTechnicalDialNumberCommand.class,
                    AggregateFfmTechnicalIpAddressCommand.class,
                    AggregateFfmTechnicalCpeCommand.class,
                    AggregateFfmTechnicalVpnCommand.class,
                    AggregateFfmTechnicalIPSecS2SCommand.class,
                    AggregateFfmTechnicalLeistungenCommand.class,
                    AggregateFfmTechnicalSbcCommand.class,
                    AggregateFfmTechnicalPseCommand.class
            )

            .build();
            // @formatter:on


    /**
     * Ermittelt die Aggregation-Klassen fuer die angegebene Strategie.
     *
     * @param aggregationStrategy
     * @return
     */
    public static Collection<Class<? extends AbstractFfmCommand>> getFfmAggregationConfig(
            FfmAggregationStrategy aggregationStrategy) {

        if (!ffmAggregatorConfig.containsKey(aggregationStrategy)) {
            throw new FFMServiceException(
                    String.format("FFM Aggregator-Konfiguration %s ist nicht definiert!", aggregationStrategy)
            );
        }
        return ffmAggregatorConfig.get(aggregationStrategy);
    }

}
