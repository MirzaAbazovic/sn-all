package de.mnet.wita.message.builder;

import de.mnet.wita.WitaCdmVersion;

/**
 *
 */
public abstract class AbstractWitaBuilder<T> {

    private final WitaCdmVersion witaCdmVersion;

    protected AbstractWitaBuilder(WitaCdmVersion witaCdmVersion) {
        this.witaCdmVersion = witaCdmVersion;
    }

    protected WitaCdmVersion getWitaVersion() {
        return witaCdmVersion;
    }

    public abstract T buildValid();

    public abstract T build();

}
