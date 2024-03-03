/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2011 17:06:33
 */
package de.mnet.wita.message.auftrag;

import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.validator.constraints.Email;

import de.mnet.wita.message.MwfEntity;
import de.mnet.wita.validators.ansprechpartner.TelefonnummerValid;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@Table(name = "T_MWF_AUFTRAGSMANAGEMENT")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_MWF_AUFTRAGSMANAGEMENT_0", allocationSize = 1)
public class Auftragsmanagement extends MwfEntity {

    // <auftragsmanagement>
    // <anrede>1</anrede>
    // <vorname>Alexander</vorname>
    // <nachname>Konsivarov</nachname>
    // <telefonnummer>069 42607734</telefonnummer>
    // <emailadresse>agkonv1@aihs.net</emailadresse>
    // </auftragsmanagement>

    private static final long serialVersionUID = 8285165099159034255L;

    private Anrede anrede;
    private String vorname;
    private String nachname;
    private String telefonnummer;
    private String email;

    @Override
    public String toString() {
        return "Auftragsmanagement [anrede=" + anrede + ", vorname=" + vorname + ", nachname=" + nachname
                + ", telefonnummer=" + telefonnummer + ", email=" + email + "]";
    }

    @NotNull
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

    @NotNull
    @Size(min = 1, max = 30)
    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    @TelefonnummerValid
    @NotNull(message = "Die Telefonnummer ihres Hurrican-Benutzers muss gesetzt sein.")
    @Size(min = 0, max = 30, message = "Die Telefonnummer ihres Hurrican-Benutzers muss zwischen 1 und 30 Zeichen lang sein.")
    public String getTelefonnummer() {
        return telefonnummer;
    }

    public void setTelefonnummer(String telefonnummer) {
        this.telefonnummer = telefonnummer;
    }

    @Size(min = 0, max = 320, message = "Die Email-Adresse ihres Hurrican-Benutzers muss zwischen 0 und 320 Zeichen lang sein.")
    @Email(message = "Die Email-Adresse ihres Hurrican-Benutzers muss eine gültige Email-Adresse sein.")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
