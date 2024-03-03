/*

 * Copyright (c) 2017 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 01.03.2017

 */

package de.mnet.hurrican.wholesale.ws.outbound.testdata;


import de.mnet.wbci.model.Anrede;
import de.mnet.wbci.model.KundenTyp;
import de.mnet.wbci.model.Person;

/**
 * Created by wieran on 01.03.2017.
 */

public class PersonTestdata {

    /**
     * @return test person with static data
     */
    public static Person createPerson() {
        Person person = new Person();
        person.setNachname("Müller");
        person.setVorname("Franz");
        person.setAnrede(Anrede.HERR);
        person.setKundenTyp(KundenTyp.PK);
        return person;
    }
}
