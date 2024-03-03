/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.04.2012 17:53:24
 */
package de.augustakom.hurrican.model.cc.fttx;

import javax.persistence.*;
import javax.validation.constraints.*;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;
import de.mnet.annotation.ObjectsAreNonnullByDefault;

/**
 * Transportschnittstelle A10-NSP (Network-Service-Provider).
 * <p/>
 * Die A10-NSP (auch A10-ASP)-Schnittstelle ist grundlegend zur Realisierung des offenen Zugangs auf dem Layer 2 und 3.
 * Sie ist eine Vorbedingung für die Interoperabilität des Bitstromzuganges. Sie ist vom Broadband Forum definiert und
 * von anderen Standardisierungsgremien übernommen worden. Die Schnittstelle umschreibt die generellen Aspekte in Bezug
 * auf die Fähigkeiten des Application-Enablements. Sie besitzt Rückwärtskompatibilität zu existierenden IP-Services und
 * Konnektivitätsmodellen als auch Vorwärtskompatibilität mit nativem Ethernet im Zugangsnetz. Sie unterstützt DHCP- und
 * PPP-basierende Zugangsmethoden für die Endkunden und ist daher sehr attraktiv für Diensteanbieter und Hersteller
 * wegen ihrer Architektur für eine Ethernet-basierende Aggregation der Zugangsströme.<br> Quelle: {@link
 * http://www.bmwi.de/Dateien/BBA/PDF/it-gipfel-2010-breitband-zukunft-instrumente-umsetzung,property=pdf
 * ,bereich=bmwi,sprache =de,rwb=true.pdf}
 */
@ObjectsAreNonnullByDefault
@Entity
@Table(name = "T_FTTX_A10_NSP", uniqueConstraints = {
        @UniqueConstraint(columnNames = "A10_NSP_NUMMER"),
        @UniqueConstraint(columnNames = "A10_NSP_NAME") })
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_FTTX_A10_NSP_0", allocationSize = 1)
public class A10Nsp extends AbstractCCIDModel {
    public static final Integer A10_NSP_MNET_NUMMER = Integer.valueOf(1);
    public static final String A10_NSP_MNET_NAME = "WSMNE1";

    /**
     * Eindeutige Nummer des A10-NSP
     */
    private Integer nummer;

    /**
     * Eindeutiger Name des A10-NSP
     */
    private String name;

    @Column(name = "A10_NSP_NUMMER")
    @NotNull
    public Integer getNummer() {
        return nummer;
    }

    public void setNummer(Integer nummer) {
        this.nummer = nummer;
    }

    @Column(name = "A10_NSP_NAME", length = 10)
    @NotNull
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}


