/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.08.13
 */
package de.mnet.wbci.model.builder;

import de.mnet.wbci.model.TechnischeRessource;

/**
 *
 */
public class TechnischeRessourceBuilder implements WbciBuilder<TechnischeRessource> {

    protected String vertragsnummer;
    protected String lineId;
    protected String identifizierer;
    protected String tnbKennungAbg;

    @Override
    public TechnischeRessource build() {
        TechnischeRessource technischeRessource = new TechnischeRessource();

        technischeRessource.setVertragsnummer(vertragsnummer);
        technischeRessource.setIdentifizierer(identifizierer);
        technischeRessource.setLineId(lineId);
        technischeRessource.setTnbKennungAbg(tnbKennungAbg);

        return technischeRessource;
    }

    public TechnischeRessourceBuilder withVertragsnummer(String vertragsnummer) {
        this.vertragsnummer = vertragsnummer;
        return this;
    }

    public TechnischeRessourceBuilder withIdentifizierer(String identifizierer) {
        this.identifizierer = identifizierer;
        return this;
    }

    public TechnischeRessourceBuilder withLineId(String lineId) {
        this.lineId = lineId;
        return this;
    }

    public TechnischeRessourceBuilder withTnbKennungAbg(String tnbKennungAbg) {
        this.tnbKennungAbg = tnbKennungAbg;
        return this;
    }
}
