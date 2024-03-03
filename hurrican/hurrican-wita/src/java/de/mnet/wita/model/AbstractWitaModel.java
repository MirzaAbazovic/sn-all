/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.05.2011 09:34:33
 */
package de.mnet.wita.model;

import java.io.*;
import javax.persistence.*;
import com.google.common.base.Function;

/**
 * Abstraktes Basis-Modell fuer alle WITA Modelle.
 */
@MappedSuperclass
public abstract class AbstractWitaModel implements Serializable {

    private static final long serialVersionUID = -5982745012107418243L;
    public static final String ID = "id";
    public final static Function<AbstractWitaModel, Long> GET_ID_FUNCTION = new Function<AbstractWitaModel, Long>() {

        @Override
        public Long apply(AbstractWitaModel entity) {
            return entity.getId();
        }
    };

    private Long id;
    private Long version = null;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Version
    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AbstractWitaModel other = (AbstractWitaModel) obj;
        if ((getId() == null) && (other.getId() == null)) {
            return this == other;
        }
        else if (getId() == null) {
            if (other.getId() != null) {
                return false;
            }
        }
        else if (!getId().equals(other.getId())) {
            return false;
        }
        return true;
    }

}


