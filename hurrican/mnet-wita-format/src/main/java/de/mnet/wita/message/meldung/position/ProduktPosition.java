/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.06.2011 11:21:33
 */
package de.mnet.wita.message.meldung.position;

import javax.persistence.*;
import javax.validation.constraints.*;

import de.mnet.wita.message.MwfEntity;
import de.mnet.wita.message.auftrag.AktionsCode;
import de.mnet.wita.message.auftrag.Auftragsposition.ProduktBezeichner;
import de.mnet.wita.message.common.Uebertragungsverfahren;
import de.mnet.wita.validators.groups.Workflow;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@Table(name = "T_MWF_PRODUKT_POSITION")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_MWF_PRODUKT_POSITION_0", allocationSize = 1)
public class ProduktPosition extends MwfEntity {

    private static final long serialVersionUID = -7566877267794126144L;

    private AktionsCode aktionsCode;
    private ProduktBezeichner produktBezeichner;
    private Uebertragungsverfahren uebertragungsVerfahren;

    protected ProduktPosition() {
        // required by Hibernate
    }

    public ProduktPosition(AktionsCode aktionsCode, ProduktBezeichner produktBezeichner) {
        this.aktionsCode = aktionsCode;
        this.produktBezeichner = produktBezeichner;
    }

    @Enumerated(EnumType.STRING)
    @Column(length = 3, columnDefinition = "varchar2(3)")
    public Uebertragungsverfahren getUebertragungsVerfahren() {
        return uebertragungsVerfahren;
    }

    public void setUebertragungsVerfahren(Uebertragungsverfahren uebertragungsVerfahren) {
        this.uebertragungsVerfahren = uebertragungsVerfahren;
    }

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 10, columnDefinition = "varchar2(10)")
    public AktionsCode getAktionsCode() {
        return aktionsCode;
    }

    protected void setAktionsCode(AktionsCode aktionsCode) {
        // required by Hibernate
        this.aktionsCode = aktionsCode;
    }

    @NotNull(groups = Workflow.class)
    @Enumerated(EnumType.STRING)
    @Column(length = 10, columnDefinition = "varchar2(10)")
    public ProduktBezeichner getProduktBezeichner() {
        return produktBezeichner;
    }

    protected void setProduktBezeichner(ProduktBezeichner produktBezeichner) {
        // required by Hibernate
        this.produktBezeichner = produktBezeichner;
    }

    @Override
    public String toString() {
        return "ProduktPosition [aktionsCode=" + aktionsCode + ", produktBezeichner=" + produktBezeichner
                + ", uebertragungsVerfahren=" + uebertragungsVerfahren + "]";
    }
}
