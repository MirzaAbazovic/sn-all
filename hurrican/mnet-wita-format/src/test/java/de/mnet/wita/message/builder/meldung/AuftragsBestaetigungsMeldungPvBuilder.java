/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.08.2011 10:24:17
 */
package de.mnet.wita.message.builder.meldung;

import de.mnet.wita.message.builder.meldung.attribute.AufnehmenderProviderBuilder;
import de.mnet.wita.message.builder.meldung.position.LeitungBuilder;
import de.mnet.wita.message.common.portierung.RufnummernPortierung;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldungPv;
import de.mnet.wita.message.meldung.attribute.AufnehmenderProvider;
import de.mnet.wita.message.meldung.position.Leitung;
import de.mnet.wita.message.meldung.position.MeldungsPosition;

public class AuftragsBestaetigungsMeldungPvBuilder extends
        MessageBuilder<AuftragsBestaetigungsMeldungPv, AuftragsBestaetigungsMeldungPvBuilder, MeldungsPosition> {

    private final Leitung leitung = new LeitungBuilder().withLeitungsAbschnittList(null).withMaxBruttoBitrate(null).buildValid();
    private AufnehmenderProvider aufnehmenderProvider = new AufnehmenderProviderBuilder().build();
    private RufnummernPortierung rufnummernPortierung;

    @Override
    public AuftragsBestaetigungsMeldungPv build() {
        AuftragsBestaetigungsMeldungPv abmPv = new AuftragsBestaetigungsMeldungPv();
        addCommonFields(abmPv);

        abmPv.setVertragsNummer(vertragsnummer);
        abmPv.setLeitung(leitung);

        abmPv.setId(id);
        abmPv.setExterneAuftragsnummer(externeAuftragsnummer);
        abmPv.setAufnehmenderProvider(aufnehmenderProvider);
        abmPv.setRufnummernPortierung(rufnummernPortierung);

        abmPv.addMeldungsPosition(getDefaultMeldungsPosition());

        return abmPv;
    }

    @Override
    protected MeldungsPosition getDefaultMeldungsPosition() {
        return new MeldungsPosition(
                "0010",
                "Auftrag ausgeführt.");
    }

    public AuftragsBestaetigungsMeldungPvBuilder withAufnehmenderProvider(AufnehmenderProvider aufnehmenderProvider) {
        this.aufnehmenderProvider = aufnehmenderProvider;
        return this;
    }

    public AuftragsBestaetigungsMeldungPvBuilder withRufnummernPortierung(RufnummernPortierung rufnummernPortierung) {
        this.rufnummernPortierung = rufnummernPortierung;
        return this;
    }
}
