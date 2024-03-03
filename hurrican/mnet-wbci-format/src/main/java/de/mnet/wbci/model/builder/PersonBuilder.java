package de.mnet.wbci.model.builder;

import org.apache.commons.lang.StringUtils;

import de.mnet.wbci.model.Person;

public class PersonBuilder extends PersonOderFirmaBuilder<Person> {

    protected String vorname;
    protected String nachname;

    @Override
    public Person build() {
        Person person = new Person();
        enrich(person);
        person.setVorname(StringUtils.stripToNull(vorname));
        person.setNachname(StringUtils.stripToNull(nachname));
        return person;
    }

    public PersonBuilder withVorname(String vorname) {
        this.vorname = vorname;
        return this;
    }

    public PersonBuilder withNachname(String nachname) {
        this.nachname = nachname;
        return this;
    }

}
