package de.mnet.wbci.model;

import java.io.*;
import javax.persistence.*;
import com.google.common.base.Function;

/**
 * Basisklasse fuer alle Objekte des M-net WBCI Formats.
 */
@MappedSuperclass
public abstract class WbciEntity implements Serializable {
    public final static Function<WbciEntity, Long> GET_ID_FUNCTION = new Function<WbciEntity, Long>() {
        @Override
        public Long apply(WbciEntity entity) {
            return entity.getId();
        }
    };

    public static final String ID_FIELD = "id";
    private static final long serialVersionUID = -6507564584446407232L;

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
        WbciEntity other = (WbciEntity) obj;
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

    @Override
    public String toString() {
        return "WbciEntity{" +
                "id=" + id +
                ", version=" + version +
                '}';
    }

    @Transient
    protected String stripLeadingZero(String onkz) {
        if (onkz == null || onkz.isEmpty() || onkz.charAt(0) != '0') {
            return onkz;
        }
        else {
            return onkz.substring(1, onkz.length());
        }
    }

    @Transient
    protected String addLeadingZero(String onkz) {
        return onkz != null ? "0" + onkz : null;
    }

}
