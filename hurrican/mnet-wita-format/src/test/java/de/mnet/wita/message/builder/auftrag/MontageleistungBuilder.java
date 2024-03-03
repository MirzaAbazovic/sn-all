package de.mnet.wita.message.builder.auftrag;

import de.mnet.wita.message.auftrag.Montageleistung;
import de.mnet.wita.message.builder.common.PersonennameBuilder;
import de.mnet.wita.message.common.Personenname;

/**
 *
 */
public class MontageleistungBuilder {

    private Personenname personenname;
    private String telefonnummer;
    private String emailadresse;
    private String montagehinweis;

    public Montageleistung buildValid() {
        if (personenname == null) {
            personenname = new PersonennameBuilder().buildValid();
        }
        if (telefonnummer == null) {
            telefonnummer = "089123456789";
        }
        return build();
    }

    Montageleistung build() {
        Montageleistung montageleistung = new Montageleistung();
        montageleistung.setPersonenname(personenname);
        montageleistung.setTelefonnummer(telefonnummer);
        montageleistung.setEmailadresse(emailadresse);
        montageleistung.setMontagehinweis(montagehinweis);
        return montageleistung;
    }

    public MontageleistungBuilder withPersonenname(Personenname personenname) {
        this.personenname = personenname;
        return this;
    }

    public MontageleistungBuilder withMontagehinweis(String montagehinweis) {
        this.montagehinweis = montagehinweis;
        return this;
    }

    public MontageleistungBuilder withTelefonnummer(String telefonnummer) {
        this.telefonnummer = telefonnummer;
        return this;
    }

    public MontageleistungBuilder withEmailadresse(String emailadresse) {
        this.emailadresse = emailadresse;
        return this;
    }

}
