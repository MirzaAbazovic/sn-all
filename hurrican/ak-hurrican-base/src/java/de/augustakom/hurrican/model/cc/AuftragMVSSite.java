/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.02.2010 15:03:12
 */
package de.augustakom.hurrican.model.cc;

import javax.persistence.*;


/**
 * Modell-Klasse fuer die Definition von AuftragMVSSite-Daten.
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS",
        justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@DiscriminatorValue("Site")
public class AuftragMVSSite extends AuftragMVS {

    private static final long serialVersionUID = -2743528306057749L;

    private String subdomain;
    private AuftragMVSEnterprise parent;

    private String standortKuerzel;

    @ManyToOne
    @JoinColumn(name = "PARENT", nullable = false)
    public AuftragMVSEnterprise getParent() {
        return parent;
    }

    public void setParent(AuftragMVSEnterprise parent) {
        this.parent = parent;
    }

    public String getSubdomain() {
        return subdomain;
    }

    public void setSubdomain(String subdomain) {
        this.subdomain = subdomain;
    }

    // Standort-Kuerzel wird aus Vento gelesen
    @Transient
    public String getStandortKuerzel() {
        return standortKuerzel;
    }

    public void setStandortKuerzel(String standortKuerzel) {
        this.standortKuerzel = standortKuerzel;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("AuftragMVSSite [subdomain=%s, parent=%s]", subdomain, parent);
    }

} // end
