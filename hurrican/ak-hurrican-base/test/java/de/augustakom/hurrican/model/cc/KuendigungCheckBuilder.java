/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.12.13
 */
package de.augustakom.hurrican.model.cc;


import java.util.*;

import de.augustakom.common.model.EntityBuilder;

public class KuendigungCheckBuilder extends EntityBuilder<KuendigungCheckBuilder, KuendigungCheck> {

    private Long oeNoOrig;
    private Boolean durchVertrieb;
    private Set<KuendigungFrist> kuendigungFristen = new HashSet<>();

    public KuendigungCheckBuilder withRandomOeNoOrig() {
        this.oeNoOrig = randomLong(10000);
        return this;
    }

    public KuendigungCheckBuilder withDurchVertrieb() {
        this.durchVertrieb = Boolean.TRUE;
        return this;
    }

    public KuendigungCheckBuilder addKuendiungFrist(KuendigungFrist toAdd) {
        kuendigungFristen.add(toAdd);
        return this;
    }

}
