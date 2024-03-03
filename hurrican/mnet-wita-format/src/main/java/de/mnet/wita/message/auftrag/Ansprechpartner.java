/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2011 17:06:30
 */
package de.mnet.wita.message.auftrag;

import javax.persistence.*;

import de.mnet.wita.message.MwfEntity;
import de.mnet.wita.validators.ansprechpartner.TelefonnummerValid;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@Table(name = "T_MWF_ANSPRECHPARTNER")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_MWF_ANSPRECHPARTNER_0", allocationSize = 1)
public class Ansprechpartner extends MwfEntity {

    // <ansprechpartner>
    // <anrede>2</anrede>    // <vorname>Alexandra</vorname>
    // <nachname>Konsivarov</nachname>
    // <telefonnummer>069 42607734</telefonnummer>
    // <emailadresse>agkonv1@aihs.net</emailadresse>
    // <rolle>Auftragsmanagement</rolle>
    // </ansprechpartner>

    private static final long serialVersionUID = -6572728615739533101L;

    private Anrede anrede;
    private String vorname;
    private String nachname;
    private String telefonnummer;
    private String email;
    private AnsprechpartnerRolle rolle;

    @Enumerated(EnumType.STRING)
    @Column(length = 1, columnDefinition = "varchar2(1)")
    public Anrede getAnrede() {
        return anrede;
    }

    public void setAnrede(Anrede anrede) {
        this.anrede = anrede;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    @TelefonnummerValid
    public String getTelefonnummer() {
        return telefonnummer;
    }

    public void setTelefonnummer(String telefonnummer) {
        this.telefonnummer = telefonnummer;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Enumerated(EnumType.STRING)
    @Column(length = 1, columnDefinition = "varchar2(255)")
    public AnsprechpartnerRolle getRolle() {
        return rolle;
    }

    public void setRolle(AnsprechpartnerRolle rolle) {
        this.rolle = rolle;
    }

    @Override
    public String toString() {
        return "Ansprechpartner [anrede=" + anrede + ", vorname=" + vorname + ", nachname="
                + nachname + ", telefonnummer=" + telefonnummer + ", email=" + email + ", rolle="
                + rolle + "]";
    }

}
