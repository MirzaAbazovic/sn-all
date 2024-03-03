/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.08.2011 12:33:57
 */
package de.mnet.wita.message.builder.meldung.attribute;

import de.mnet.wita.RuemPvAntwortCode;
import de.mnet.wita.message.meldung.position.AbgebenderProvider;

public class AbgebenderProviderBuilder {

    private boolean zustimmung = true;
    private RuemPvAntwortCode antwortCode;
    private String antwortText;

    public AbgebenderProvider build() {
        AbgebenderProvider abgebenderProvider = new AbgebenderProvider();
        abgebenderProvider.setAntwortCode(antwortCode);
        abgebenderProvider.setAntwortText(antwortText);
        abgebenderProvider.setProviderName("M-net Telekommunikations GmbH");
        abgebenderProvider.setZustimmungProviderWechsel(zustimmung);
        return abgebenderProvider;
    }

    public AbgebenderProviderBuilder withZustimmungProviderWechsel(boolean zustimmung) {
        this.zustimmung = zustimmung;
        return this;
    }

    public AbgebenderProviderBuilder withAntwortCode(RuemPvAntwortCode antwortCode) {
        this.antwortCode = antwortCode;
        return this;
    }

    public AbgebenderProviderBuilder withAntwortText(String antwortText) {
        this.antwortText = antwortText;
        return this;
    }

}
