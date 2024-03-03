/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.07.2004 14:44:42
 */
package de.augustakom.hurrican.model.cc;

import javax.persistence.*;
import com.google.common.base.Function;

import de.augustakom.hurrican.model.shared.iface.LongIdModel;


/**
 * Basisklasse fuer alle CC-Modelle, deren PrimaryKey ein Integer-Objekt ist.
 *
 *
 */
@MappedSuperclass
public class AbstractCCIDModel extends AbstractCCModel implements LongIdModel {

    private static final long serialVersionUID = 3603917458993388247L;
    public static final String ID = "id";

    public static final Function<AbstractCCIDModel, Long> GET_ID = new Function<AbstractCCIDModel, Long>() {
        @Override
        public Long apply(AbstractCCIDModel input) {
            return input.getId();
        }
    };

    public static final Function<AbstractCCIDModel, Boolean> IS_TRANSIENT = new Function<AbstractCCIDModel, Boolean>() {
        @Override
        public Boolean apply(AbstractCCIDModel input) {
            return (input.getId() == null) ? Boolean.TRUE : Boolean.FALSE;
        }
    };

    private Long id;

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    @SuppressWarnings("PMD.UselessOverridingMethod")
    public int hashCode() {
        // Bis auf weiteres Delegation an Object.hashCode(),
        // da es Probleme mit wechselden Hashes gibt wenn Hibernate eine ID zuweist
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        AbstractCCIDModel model = (AbstractCCIDModel) obj;
        if (getId() != null) {
            if (!getId().equals(model.getId())) {
                return false;
            }
        }
        else {
            return false;
        }

        return true;
    }
}


