/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.08.2014
 */
package de.augustakom.hurrican.model.cc;

import static com.google.common.base.Preconditions.*;

import java.io.*;
import java.util.*;
import javax.annotation.*;
import javax.persistence.*;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;

/**
 * Bandbreite einer Datenverbindung. Vereint Up- und Downstream in kbit/s.
 * <p/>
 * Mindestens Up- oder Downstream muss gesetzt sein. Ist keines von beiden gegeben, so darf Bandwidth nicht instantiert
 * werden und ist somit null. BTW: so behandelt das auch Hibernate null-values beim Laden von
 * component/embeddet-Objekten.
 */
@Embeddable
public class Bandwidth implements Comparable<Bandwidth>, Serializable {
    @Nonnull
    private Integer downstream;
    @CheckForNull
    private Integer upstream = null;

    // wird von hibernate benoetigt
    Bandwidth() {
    }

    public Bandwidth(@Nonnull Integer downstream, Integer upstream) {
        checkArgument(downstream != null, "Es muss mindestens Downstream gesetzt sein");

        this.downstream = downstream;
        this.upstream = upstream;
    }

    @CheckForNull
    public static Bandwidth create(Integer downstream, Integer upstream) {
        if (downstream == null && upstream == null) {
            return null;
        }
        return new Bandwidth(downstream, upstream);
    }

    @CheckForNull
    public static Bandwidth create(Integer downstream) {
        if (downstream == null) {
            return null;
        }
        return new Bandwidth(downstream, null);
    }

    @Nonnull
    public Integer getDownstream() {
        return downstream;
    }

    @Nonnull
    @Transient
    public String getDownstreamAsString() {
        return downstream.toString();
    }

    void setDownstream(Integer downstream) {
        checkArgument(downstream != null, "Downstream muss gesetzt (!null) sein");
        this.downstream = downstream;
    }

    @CheckForNull
    public Integer getUpstream() {
        return upstream;
    }

    @CheckForNull
    @Transient
    public String getUpstreamAsString() {
        return upstream == null ? null : upstream.toString();
    }

    void setUpstream(Integer upstream) {
        this.upstream = upstream;
    }

    @Override
    public int hashCode() {
        return Objects.hash(downstream, upstream);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {return true;}
        if (obj == null || getClass() != obj.getClass()) {return false;}
        final Bandwidth other = (Bandwidth) obj;
        return Objects.equals(this.downstream, other.downstream) && Objects.equals(this.upstream, other.upstream);
    }

    @Override
    public String toString() {
        return com.google.common.base.Objects.toStringHelper(this)
                .add("downstream", downstream)
                .add("upstream", upstream)
                .toString();
    }

    /**
     * Vergleicht zwei Bandwidth Objekte unter Beruecksichtigung folgender Regeln:
     * <p/>
     * <ul> <li>downstream zaehlt mehr als upstream</li> <li>ist down- upder upstream nicht gesetzt, so wird dies wie
     * unbegrenzt (= sehr hoch) gewertet</li> </ul>
     * <p/>
     * Beispiele: <ul> <li>Bandwidth(down=6000, up=1000) &gt; Bandwidth(down=5000, up=2000)</li>
     * <li>Bandwidth(down=5000, up=1000) &lt; Bandwidth(down=5000, up=2000)</li> <li>Bandwidth(down=null, up=1000) &gt;
     * Bandwidth(down=5000, up=2000)</li> </ul>
     *
     * @param other
     * @return
     */
    @Override
    public int compareTo(Bandwidth other) {
        return ComparisonChain.start()
                .compare(this.downstream, other.downstream, Ordering.natural().nullsLast())
                .compare(this.upstream, other.upstream, Ordering.natural().nullsLast())
                .result();
    }

    /**
     * Liefert true, wenn diese Bandbreite innerhalb von {@code other} liegt, d.h. Down- und Upstream dieser Bandbreite
     * sind jeweils kleiner gleich Down- und Upstream von {@code other}. Beachte, dass aus {@code a.isWithin(b) ==
     * false} nicht {@code b.isWithin(a) == true} folgt. D.h. diese Methode induziert keine totale Ordnung für
     * Bandbreite. Falls Down- oder Upstream null sind, werden diese nicht in den Vergleich miteinbezogen.
     */
    public boolean isWithin(Bandwidth other) {
        if (other == null) {
            return true;
        }
        final boolean dsLessThan =
                this.downstream == null || other.downstream == null || this.downstream <= other.downstream;
        final boolean usLessThan =
                this.upstream == null || other.upstream == null || this.upstream <= other.upstream;
        return dsLessThan && usLessThan;
    }
}
