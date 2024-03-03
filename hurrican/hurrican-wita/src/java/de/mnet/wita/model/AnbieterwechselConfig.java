/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.08.2011 14:03:59
 */
package de.mnet.wita.model;

import javax.persistence.*;
import javax.validation.constraints.*;

import de.mnet.wbci.model.ProduktGruppe;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.common.Carrier;

@Entity
@Table(name = "T_ANBIETERWECHSELCONFIG")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_ANBIETERWECHSELCONFIG_0", allocationSize = 1)
public class AnbieterwechselConfig extends AbstractWitaModel {

    private static final long serialVersionUID = 4665650315855396948L;

    public static enum NeuProdukt {
        TAL, KEINE_DTAG_LEITUNG;
    }

    private Carrier carrierAbgebend;
    private ProduktGruppe altProdukt;
    private NeuProdukt neuProdukt;
    private GeschaeftsfallTyp geschaeftsfallTyp;

    @Enumerated(EnumType.STRING)
    @NotNull
    public GeschaeftsfallTyp getGeschaeftsfallTyp() {
        return geschaeftsfallTyp;
    }

    public void setGeschaeftsfallTyp(GeschaeftsfallTyp geschaeftsfallTyp) {
        this.geschaeftsfallTyp = geschaeftsfallTyp;
    }

    @Enumerated(EnumType.STRING)
    @NotNull
    public NeuProdukt getNeuProdukt() {
        return neuProdukt;
    }

    public void setNeuProdukt(NeuProdukt neuProdukt) {
        this.neuProdukt = neuProdukt;
    }

    @Enumerated(EnumType.STRING)
    @NotNull
    public ProduktGruppe getAltProdukt() {
        return altProdukt;
    }

    public void setAltProdukt(ProduktGruppe altProdukt) {
        this.altProdukt = altProdukt;
    }

    @Enumerated(EnumType.STRING)
    @NotNull
    public Carrier getCarrierAbgebend() {
        return carrierAbgebend;
    }

    public void setCarrierAbgebend(Carrier carrierAbgebend) {
        this.carrierAbgebend = carrierAbgebend;
    }

    @Override
    public String toString() {
        return "AnbieterwechselConfig [carrierAbgebend=" + carrierAbgebend + ", altProdukt=" + altProdukt
                + ", neuProdukt=" + neuProdukt + ", geschaeftsfallTyp=" + geschaeftsfallTyp + "]";
    }

}
