package de.mnet.wbci.model.builder;

import de.mnet.wbci.model.Anrede;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.KundenTyp;
import de.mnet.wbci.model.Person;
import de.mnet.wbci.model.WbciCdmVersion;

public class PersonTestBuilder extends PersonBuilder implements WbciTestBuilder<Person> {
    @Override
    public Person buildValid(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp) {
        if (vorname == null) {
            vorname = "Hans";
        }
        if (nachname == null) {
            nachname = "Mayer";
        }
        if (anrede == null) {
            anrede = Anrede.HERR;
        }
        if (kundenTyp == null) {
            kundenTyp = KundenTyp.PK;
        }
        return build();
    }

    @Override
    public PersonTestBuilder withVorname(String vorname) {
        return (PersonTestBuilder) super.withVorname(vorname);
    }

    @Override
    public PersonTestBuilder withNachname(String nachname) {
        return (PersonTestBuilder) super.withNachname(nachname);
    }
}
