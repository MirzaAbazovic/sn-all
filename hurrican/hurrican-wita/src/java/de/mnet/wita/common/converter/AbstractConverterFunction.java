/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.02.2012 09:58:38
 */
package de.mnet.wita.common.converter;

import com.google.common.base.Function;

import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.mnet.common.tools.ReflectionTools;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.model.AkmPvUserTask;

/**
 * Basis Klasse fuer Konverterfunktionen, die den Boilerplate mit Klassen-Speichern und netteres Interface zum
 * implementieren.
 */
public abstract class AbstractConverterFunction<BASE, SOURCE extends BASE, TARGET> implements Function<SOURCE, TARGET> {
    private Class<SOURCE> typeToConvert;

    @SuppressWarnings("unchecked")
    protected AbstractConverterFunction() {
        typeToConvert =
                (Class<SOURCE>) ReflectionTools.getTypeArguments(AbstractConverterFunction.class, this.getClass())
                        .get(1);
    }

    /**
     * @throws RuntimeException falls keine oder mehrere {@link AuftragDaten} zum {@link AkmPvUserTask} der zu
     *                          protokollierenden PV-{@link Meldung} zugeordnet
     */
    @Override
    public abstract TARGET apply(SOURCE source);

    /**
     * @throws RuntimeException falls keine oder mehrere {@link AuftragDaten} zum {@link AkmPvUserTask} der zu
     *                          protokollierenden PV-{@link Meldung} zugeordnet
     */
    public TARGET convert(BASE source) {
        @SuppressWarnings("unchecked")
        SOURCE specificSource = (SOURCE) source;
        return apply(specificSource);
    }

    public Class<SOURCE> getTypeToConvert() {
        return typeToConvert;
    }
}
