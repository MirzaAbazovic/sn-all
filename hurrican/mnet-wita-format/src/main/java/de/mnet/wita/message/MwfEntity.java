/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.05.2011 15:19:05
 */
package de.mnet.wita.message;

import java.io.*;
import javax.persistence.*;
import com.google.common.base.Function;

/**
 * Basisklasse fuer alle Objekte des M-net WITA Formats (MWF).
 */
@MappedSuperclass
public class MwfEntity implements Serializable {
    public final static Function<MwfEntity, Long> GET_ID_FUNCTION = new Function<MwfEntity, Long>() {
        @Override
        public Long apply(MwfEntity entity) {
            return entity.getId();
        }
    };

    public static final String ID_FIELD = "id";
    private static final long serialVersionUID = -6507564584446407212L;

    private Long id;
    private Long version;

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
        result = (prime * result) + ((getId() == null) ? 0 : getId().hashCode());
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
        MwfEntity other = (MwfEntity) obj;
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
