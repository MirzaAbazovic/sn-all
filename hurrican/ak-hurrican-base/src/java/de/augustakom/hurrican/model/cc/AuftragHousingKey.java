/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.05.2011 13:20:21
 */
package de.augustakom.hurrican.model.cc;

import javax.persistence.*;
import javax.validation.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cascade;

import de.augustakom.hurrican.model.cc.housing.Transponder;
import de.augustakom.hurrican.model.cc.housing.TransponderGroup;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.validation.cc.housing.AuftragHousingKeyValid;

/**
 * Ueber das Modell sind einzelne Transponer (oder eine Transponder-Gruppe) einem Housing-Auftrag zugeordnet.
 */
@Entity
@Table(name = "T_AUFTRAG_HOUSING_KEY")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_AUFTRAG_HOUSING_KEY_0", allocationSize = 1)
@AuftragHousingKeyValid
public class AuftragHousingKey extends AbstractCCHistoryModel implements CCAuftragModel {

    private Long auftragId;
    private Long auftragHousingId;
    public static final String TRANSPONDER = "transponder";
    private Transponder transponder;
    public static final String TRANSPONDER_GROUP = "transponderGroup";
    private TransponderGroup transponderGroup;

    // Bezeichnungen fuer die GUI
    public static final String TRANSPONDER_GROUP_BEZEICHNUNG = "Transp.-Gruppe";
    public static final String TRANSPONDER_BEZEICHNUNG = "Transponder ID";
    public static final String CUSTOMERFIRSTNAME_BEZEICHNUNG = "Vorname";
    public static final String CUSTOMERLASTNAME_BEZEICHNUNG = "Nachname";

    @Column(name = "AUFTRAG_HOUSING_ID")
    @NotNull
    public Long getAuftragHousingId() {
        return auftragHousingId;
    }

    public void setAuftragHousingId(Long auftragHousingId) {
        this.auftragHousingId = auftragHousingId;
    }

    @Override
    @Column(name = "AUFTRAG_ID")
    @NotNull
    public Long getAuftragId() {
        return this.auftragId;
    }

    @Override
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TRANSPONDER_ID", nullable = true)
    @Valid
    @Cascade({ org.hibernate.annotations.CascadeType.ALL })
    public Transponder getTransponder() {
        return transponder;
    }

    public void setTransponder(Transponder transponder) {
        this.transponder = transponder;
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TRANSPONDER_GROUP_ID", nullable = true)
    @Valid
    public TransponderGroup getTransponderGroup() {
        return transponderGroup;
    }

    public void setTransponderGroup(TransponderGroup transponderGroup) {
        this.transponderGroup = transponderGroup;
    }

}
