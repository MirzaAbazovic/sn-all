package de.mnet.wita.message.builder.auftrag;

import de.mnet.wita.message.StandortWithPerson;
import de.mnet.wita.message.auftrag.Anrede;
import de.mnet.wita.message.common.Kundenname;
import de.mnet.wita.message.common.Personenname;

/**
 *
 */
class StandortWithPersonBuilder extends AbstractStandortBuilder {

    private final Kundenname kundenname;

    StandortWithPersonBuilder() {
        Personenname personenname = new Personenname();
        personenname.setNachname("Mustermann");
        personenname.setVorname("Max");
        personenname.setAnrede(Anrede.HERR);
        this.kundenname = personenname;
    }

    void enrich(StandortWithPerson standortWithPerson) {
        super.enrich(standortWithPerson);
        standortWithPerson.setKundenname(kundenname);
    }

    public StandortWithPerson build() {
        StandortWithPerson standortWithPerson = new StandortWithPerson();
        enrich(standortWithPerson);
        return standortWithPerson;
    }

}
