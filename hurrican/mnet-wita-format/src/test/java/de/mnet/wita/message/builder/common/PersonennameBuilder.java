package de.mnet.wita.message.builder.common;

import de.mnet.wita.message.auftrag.Anrede;
import de.mnet.wita.message.common.Personenname;

/**
 *
 */
public class PersonennameBuilder {

    private String vorname;
    private String nachname;
    private Anrede anrede;

    public Personenname buildValid() {
        if (nachname == null) {
            nachname = "Huber";
        }
        if (anrede == null) {
            anrede = Anrede.HERR;
        }
        return build();
    }

    Personenname build() {
        Personenname personenname = new Personenname();
        personenname.setVorname(vorname);
        personenname.setNachname(nachname);
        personenname.setAnrede(anrede);
        return personenname;
    }

    public PersonennameBuilder withVorname(String vorname) {
        this.vorname = vorname;
        return this;
    }

    public PersonennameBuilder withNachname(String nachname) {
        this.nachname = nachname;
        return this;
    }

    public PersonennameBuilder withAnrede(Anrede anrede) {
        this.anrede = anrede;
        return this;
    }

}
