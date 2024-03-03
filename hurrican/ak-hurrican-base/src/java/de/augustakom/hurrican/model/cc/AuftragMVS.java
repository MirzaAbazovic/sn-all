/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.02.2010 15:03:12
 */
package de.augustakom.hurrican.model.cc;

import javax.persistence.*;
import javax.validation.constraints.*;

import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;


/**
 * Modell-Klasse fuer die Definition von MVS-Daten.
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "mvs_type", discriminatorType = DiscriminatorType.STRING)
@Table(name = "T_AUFTRAG_MVS")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_AUFTRAG_MVS_0", allocationSize = 1)
public abstract class AuftragMVS extends AbstractCCIDModel implements CCAuftragModel {

    private static final long serialVersionUID = -4556196399857905313L;
    private Long auftragId;
    private String userName;
    private String password;

    @Override
    @Column(name = "AUFTRAG_ID")
    @NotNull
    public Long getAuftragId() {
        return auftragId;
    }

    @Override
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    @NotNull(message = "UserName muss fuer MVS Auftrag gesetzt sein")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @NotNull(message = "Passwort muss fuer MVS Auftrag gesetzt sein")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}


