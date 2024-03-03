/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.11.13 07:53
 */
package de.augustakom.hurrican.model.cc;

import java.io.*;
import javax.annotation.*;
import javax.persistence.*;
import org.hibernate.validator.constraints.NotEmpty;

import de.mnet.annotation.ObjectsAreNonnullByDefault;

@Embeddable
@ObjectsAreNonnullByDefault
public class VoipDnBlock implements Serializable {

    private static final long serialVersionUID = 1003027074924975319L;

    private String anfang;
    @Nullable private String ende;
    private boolean zentrale;

    protected VoipDnBlock() {
    }

    public VoipDnBlock(final String anfang, @Nullable final String ende, final boolean zentrale) {
        this.anfang = anfang;
        this.ende = ende;
        this.zentrale = zentrale;
    }

    @Column(name = "ANFANG", nullable = false)
    @NotEmpty
    public String getAnfang() {
        return anfang;
    }

    protected void setAnfang(final String start) {
        this.anfang = start;
    }

    @Column(name = "ENDE", nullable = true)
    @Nullable
    public String getEnde() {
        return ende;
    }

    protected void setEnde(final String ende) {
        this.ende = ende;
    }

    @Column(name = "ZENTRALE")
    public boolean getZentrale() {
        return zentrale;
    }

    protected void setZentrale(final boolean zentrale) {
        this.zentrale = zentrale;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final VoipDnBlock that = (VoipDnBlock) o;

        if (zentrale != that.zentrale) {
            return false;
        }
        if (ende != null ? !ende.equals(that.ende) : that.ende != null) {
            return false;
        }
        if (!anfang.equals(that.anfang)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = anfang.hashCode();
        result = 31 * result + (ende != null ? ende.hashCode() : 0);
        result = 31 * result + (zentrale ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return getZentrale() ? getAnfang() : (getAnfang() + " - " + getEnde());
    }
}
