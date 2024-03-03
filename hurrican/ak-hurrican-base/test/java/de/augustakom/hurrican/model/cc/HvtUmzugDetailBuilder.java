/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.05.2015
 */
package de.augustakom.hurrican.model.cc;

import java.time.*;

import de.augustakom.common.model.EntityBuilder;
import de.augustakom.common.model.ReferencedEntityId;
import de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzugDetail;

public class HvtUmzugDetailBuilder extends EntityBuilder<HvtUmzugDetailBuilder, HvtUmzugDetail> {

    private Long id = null;
    @ReferencedEntityId("auftragId")
    private AuftragBuilder auftragBuilder;
    private String endstellenTyp = Endstelle.ENDSTELLEN_TYP_B;
    private ProduktBuilder produktBuilder = null;
    private LocalDate witaBereitstellungAm = LocalDateTime.now().minusDays(150).toLocalDate();
    private String lbz = randomString(15);
    private String uevtStiftAlt = "0201-13-7";
    private String uevtIdStiftNeu = null;
    @ReferencedEntityId("rangierIdNeu")
    private RangierungBuilder rangierBuilderNeu = null;
    @ReferencedEntityId("rangierAddIdNeu")
    private RangierungBuilder rangierBuilderAddNeu = null;
    private Boolean additionalOrder = Boolean.FALSE;
    private Boolean rangNeuErzeugt = Boolean.FALSE;
    @ReferencedEntityId("cpsTxId")
    private CPSTransactionBuilder cpsTransactionBuilder;
    private Boolean manualCc = null;


    public HvtUmzugDetailBuilder withRangNeuErzeugt() {
        this.rangNeuErzeugt = Boolean.TRUE;
        return this;
    }

    public HvtUmzugDetailBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public HvtUmzugDetailBuilder withAuftragBuilder(AuftragBuilder auftragBuilder) {
        this.auftragBuilder = auftragBuilder;
        return this;
    }

    public HvtUmzugDetailBuilder withEndstellenTyp(String endstellenTyp) {
        this.endstellenTyp = endstellenTyp;
        return this;
    }

    public HvtUmzugDetailBuilder withProdBuilder(ProduktBuilder prodBuilder) {
        this.produktBuilder = prodBuilder;
        return this;
    }

    public HvtUmzugDetailBuilder withWitaBereitstellungAm(LocalDate witaBereitstellungAm) {
        this.witaBereitstellungAm = witaBereitstellungAm;
        return this;
    }

    public HvtUmzugDetailBuilder withLbz(String lbz) {
        this.lbz = lbz;
        return this;
    }

    public HvtUmzugDetailBuilder withUevtStiftAlt(String uevtStiftAlt) {
        this.uevtStiftAlt = uevtStiftAlt;
        return this;
    }

    public HvtUmzugDetailBuilder withUevtIdStiftNeu(String uevtIdStiftNeu) {
        this.uevtIdStiftNeu = uevtIdStiftNeu;
        return this;
    }

    public HvtUmzugDetailBuilder withRangierBuilderNeu(RangierungBuilder rangierBuilderNeu) {
        this.rangierBuilderNeu = rangierBuilderNeu;
        return this;
    }

    public HvtUmzugDetailBuilder withRangierBuilderAddNeu(RangierungBuilder rangierBuilderAddNeu) {
        this.rangierBuilderAddNeu = rangierBuilderAddNeu;
        return this;
    }

    public HvtUmzugDetailBuilder withCpsTransactionBuilder(CPSTransactionBuilder cpsTransactionBuilder) {
        this.cpsTransactionBuilder = cpsTransactionBuilder;
        return this;
    }

    public HvtUmzugDetailBuilder withManualCc(Boolean manualCc) {
        this.manualCc = manualCc;
        return this;
    }
}
