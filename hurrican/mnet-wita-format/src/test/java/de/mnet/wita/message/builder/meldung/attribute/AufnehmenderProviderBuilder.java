/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.08.2011 12:33:57
 */
package de.mnet.wita.message.builder.meldung.attribute;

import java.time.*;

import de.mnet.wita.message.meldung.attribute.AufnehmenderProvider;

public class AufnehmenderProviderBuilder {

    private final LocalDate now = LocalDate.now();
    private LocalDate uebernahmeDatumVerbindlich = now.plusDays(14);
    private LocalDate uebernahmeDatumGeplant = now.plusDays(14);
    private String providernameAufnehmend = "O2";

    public AufnehmenderProvider build() {
        return createAufnehmenderProvider();
    }

    private AufnehmenderProvider createAufnehmenderProvider() {
        AufnehmenderProvider aufnehmenderProvider = new AufnehmenderProvider();
        aufnehmenderProvider.setProvidernameAufnehmend(providernameAufnehmend);
        aufnehmenderProvider.setUebernahmeDatumGeplant(uebernahmeDatumGeplant);
        aufnehmenderProvider.setAntwortFrist(now.plusDays(7));
        aufnehmenderProvider.setUebernahmeDatumVerbindlich(uebernahmeDatumVerbindlich);
        return aufnehmenderProvider;
    }

    public AufnehmenderProviderBuilder withUebernahmeDatumVerbindlich(LocalDate uebernahmeDatumVerbindlich) {
        this.uebernahmeDatumVerbindlich = uebernahmeDatumVerbindlich;
        return this;
    }

    public AufnehmenderProviderBuilder withUebernahmeDatumGeplant(LocalDate uebernahmeDatumGeplant) {
        this.uebernahmeDatumGeplant = uebernahmeDatumGeplant;
        return this;
    }

    public AufnehmenderProviderBuilder withProvidernameAufnehmend(String providernameAufnehmend) {
        this.providernameAufnehmend = providernameAufnehmend;
        return this;
    }
}
