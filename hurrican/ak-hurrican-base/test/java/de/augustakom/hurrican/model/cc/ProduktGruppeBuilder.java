/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.11.2009 12:15:57
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.model.EntityBuilder;


/**
 * Entity-Builder fuer Objekte des Typs {@link ProduktGruppe}
 *
 *
 */
@SuppressWarnings("unused")
public class ProduktGruppeBuilder extends EntityBuilder<ProduktGruppeBuilder, ProduktGruppe> {

    private Long id = randomLong(10000, 1000000);
    private String produktGruppe = randomString(30);
    private String realm;

    public ProduktGruppeBuilder withRealm(String realm) {
        this.realm = realm;
        return this;
    }

    public ProduktGruppeBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public ProduktGruppeBuilder withProduktGruppe(String produktGruppe) {
        this.produktGruppe = produktGruppe;
        return this;
    }

}


