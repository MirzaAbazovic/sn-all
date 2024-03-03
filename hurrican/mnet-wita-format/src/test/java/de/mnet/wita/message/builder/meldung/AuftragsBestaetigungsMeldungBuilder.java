/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.06.2011 16:36:34
 */
package de.mnet.wita.message.builder.meldung;

import java.time.*;

import de.mnet.wita.message.auftrag.AktionsCode;
import de.mnet.wita.message.auftrag.Anrede;
import de.mnet.wita.message.auftrag.Auftragsposition.ProduktBezeichner;
import de.mnet.wita.message.builder.common.portierung.RufnummernPortierungBuilder;
import de.mnet.wita.message.builder.meldung.position.LeitungBuilder;
import de.mnet.wita.message.common.Uebertragungsverfahren;
import de.mnet.wita.message.common.portierung.RufnummernPortierung;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldung;
import de.mnet.wita.message.meldung.position.AbmSchaltangaben;
import de.mnet.wita.message.meldung.position.AnsprechpartnerTelekom;
import de.mnet.wita.message.meldung.position.Leitung;
import de.mnet.wita.message.meldung.position.MeldungsPositionWithAnsprechpartner;
import de.mnet.wita.message.meldung.position.ProduktPosition;

public class AuftragsBestaetigungsMeldungBuilder extends
        MessageBuilder<AuftragsBestaetigungsMeldung, AuftragsBestaetigungsMeldungBuilder, MeldungsPositionWithAnsprechpartner> {

    private LocalDate verbindlicherLiefertermin = LocalDate.now();
    private Leitung leitung = new LeitungBuilder().buildValid();
    private AbmSchaltangaben schaltangaben;
    private final ProduktBezeichner produktBezeichner = ProduktBezeichner.HVT_2H;
    //    private List<ProduktPosition> produktPositionen = new ArrayList<ProduktPosition>();
    private final AnsprechpartnerTelekom ansprechpartnerTelekom = new AnsprechpartnerTelekom(Anrede.HERR, "Hans", "Müller",
            "381/1231");
    private RufnummernPortierung rufnummernPortierung = new RufnummernPortierungBuilder().buildMeldungPortierung(true);

    @Override
    public AuftragsBestaetigungsMeldung build() {
        AuftragsBestaetigungsMeldung abm = new AuftragsBestaetigungsMeldung(externeAuftragsnummer, kundennummer,
                verbindlicherLiefertermin);
        addCommonFields(abm);
        abm.setVertragsNummer(vertragsnummer);
        abm.setLeitung(leitung);
        abm.setSchaltangaben(schaltangaben);
        abm.setKundennummerBesteller(kundennummerBesteller);

        if (produktPositionen.isEmpty()) {
            createDefaultProduktPosition();
        }
        abm.getProduktPositionen().addAll(produktPositionen);
        abm.getMeldungsPositionen().addAll(getMeldungspositionen());
        abm.setRufnummernPortierung(rufnummernPortierung);
        return abm;
    }

    public AuftragsBestaetigungsMeldungBuilder withVerbindlicherLiefertermin(LocalDate verbindlicherLiefertermin) {
        this.verbindlicherLiefertermin = verbindlicherLiefertermin;
        return this;
    }

    public AuftragsBestaetigungsMeldungBuilder withRufnummernPortierung(RufnummernPortierung rufnummernPortierung) {
        this.rufnummernPortierung = rufnummernPortierung;
        return this;
    }

    private void createDefaultProduktPosition() {
        ProduktPosition produktPosition = new ProduktPosition(AktionsCode.AENDERUNG, produktBezeichner);
        produktPosition.setUebertragungsVerfahren(Uebertragungsverfahren.H01);
        produktPositionen.add(produktPosition);
    }

    @Override
    protected MeldungsPositionWithAnsprechpartner getDefaultMeldungsPosition() {
        return new MeldungsPositionWithAnsprechpartner("200", "OK", ansprechpartnerTelekom);
    }

    public AuftragsBestaetigungsMeldungBuilder withLeitung(Leitung leitung) {
        this.leitung = leitung;
        return this;
    }
}
