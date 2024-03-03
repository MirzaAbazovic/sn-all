/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.02.2015
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.*;

import de.augustakom.common.model.HistoryModel;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;

/**
 *
 */
@Entity
@Table(name = "T_AUFTRAG_2_PEERING_PARTNER")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_AUFTRAG_2_PP_0", allocationSize = 1)
public class Auftrag2PeeringPartner extends AbstractCCIDModel implements CCAuftragModel, HistoryModel {

    private static final long serialVersionUID = -4798213665851577753L;

    public static final String PEERING_PARTNER_ID = "peeringPartnerId";

    private Long auftragId = null;
    private Long peeringPartnerId = null;
    private Date gueltigVon = null;
    private Date gueltigBis = null;

    // @formatter:off
    /**
     * Bemerkungen zu 'GUELTIG_VON' und 'GUELTIG_BIS' Die Datumswerte werden in den Services als tagesgenau betrachtet.
     * Ein 'GUELTIG_VON' ist <b>inklusive</b> der gesetzten Zeit, 'GUELTIG_BIS' dagegen wird <b>exklusive</b> dem
     * gesetzten Zeitpunkt betrachtet. Das hat verschiedene Konsequenzen:
     * <ul>
     *     <li>Ein PP mit 'GUELTIG_VON' == 'GUELTIG_BIS' spannt <b>keinen</b> Zeitraum auf (der PP ist bspw.
     *         versehentlich eingestellt und wieder geloescht worden)</li>
     *     <li>Ein PP der einen bisher gueltigen ablöst hat 'GUELTIG_VON(neu)' == 'GUELTIG_BIS(alt)'. Da 'VON'
     *         <b>inklusive</b> und 'BIS' <b>exklusive</b> ist, gibt es einen nahtlosen Uebergang ohne Ueberschneidung.
     *         </li>
     * </ul>
     */
    // @formatter:on
    @Override
    @NotNull
    @Column(name = "GUELTIG_VON")
    public Date getGueltigVon() {
        return gueltigVon;
    }

    @Override
    public void setGueltigVon(Date gueltigVon) {
        this.gueltigVon = gueltigVon;
    }

    @Override
    @NotNull
    @Column(name = "GUELTIG_BIS")
    public Date getGueltigBis() {
        return gueltigBis;
    }

    @Override
    public void setGueltigBis(Date gueltigBis) {
        this.gueltigBis = gueltigBis;
    }

    @Override
    @NotNull
    @Column(name = "AUFTRAG_ID")
    public Long getAuftragId() {
        return auftragId;
    }

    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    @NotNull
    @Column(name = "PEERING_PARTNER_ID")
    public Long getPeeringPartnerId() {
        return peeringPartnerId;
    }

    public void setPeeringPartnerId(Long peeringPartnerId) {
        this.peeringPartnerId = peeringPartnerId;
    }

}
