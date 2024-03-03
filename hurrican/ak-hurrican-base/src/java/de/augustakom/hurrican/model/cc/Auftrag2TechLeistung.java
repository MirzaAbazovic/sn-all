/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.06.2006 07:47:37
 */
package de.augustakom.hurrican.model.cc;

import java.time.*;
import java.util.*;
import javax.validation.constraints.*;
import com.google.common.base.Function;
import org.apache.log4j.Logger;

import de.augustakom.common.model.DebugModel;
import de.augustakom.hurrican.model.shared.iface.AuftragAktionAwareModel;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;


/**
 * Modell, um eine techn. Leistung einem Auftrag zuzuordnen. <br>
 *
 *
 */
public class Auftrag2TechLeistung extends AbstractCCIDModel implements CCAuftragModel, AuftragAktionAwareModel, DebugModel {

    public static final Function<Auftrag2TechLeistung, Date> GET_AKTIV_VON = new Function<Auftrag2TechLeistung, Date>() {

        @Override
        public Date apply(@NotNull Auftrag2TechLeistung input) {
            return input.getAktivVon();
        }

    };
    public static final Function<Auftrag2TechLeistung, Date> GET_AKTIV_BIS = new Function<Auftrag2TechLeistung, Date>() {

        @Override
        public Date apply(@NotNull Auftrag2TechLeistung input) {
            return input.getAktivBis();
        }

    };

    private Long auftragId = null;
    private Long techLeistungId = null;
    private Long quantity = null;
    private Date aktivVon = null;
    private Date aktivBis = null;
    private Long verlaufIdReal = null;
    private Long verlaufIdKuend = null;
    private Long auftragAktionsIdAdd = null;
    private Long auftragAktionsIdRemove = null;

    public Date getAktivBis() {
        return this.aktivBis;
    }

    public void setAktivBis(Date aktivBis) {
        this.aktivBis = aktivBis;
    }

    public Date getAktivVon() {
        return this.aktivVon;
    }

    public void setAktivVon(Date aktivVon) {
        this.aktivVon = aktivVon;
    }

    @Override
    public Long getAuftragId() {
        return this.auftragId;
    }

    @Override
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    public Long getTechLeistungId() {
        return this.techLeistungId;
    }

    public void setTechLeistungId(Long techLeistungId) {
        this.techLeistungId = techLeistungId;
    }

    /**
     * Gibt die ID des Verlaufs zurueck, zu dem die Leistung gekuendigt wurde.
     *
     * @return Returns the verlaufIdAbgang.
     */
    public Long getVerlaufIdKuend() {
        return this.verlaufIdKuend;
    }

    public void setVerlaufIdKuend(Long verlaufIdAbgang) {
        this.verlaufIdKuend = verlaufIdAbgang;
    }

    /**
     * Gibt die ID des Verlaufs zurueck, zu dem die Leistung eingerichtet wurde.
     *
     * @return Returns the verlaufIdZugang.
     */
    public Long getVerlaufIdReal() {
        return this.verlaufIdReal;
    }

    public void setVerlaufIdReal(Long verlaufIdZugang) {
        this.verlaufIdReal = verlaufIdZugang;
    }

    public Long getQuantity() {
        return this.quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    @Override
    public Long getAuftragAktionsIdAdd() {
        return auftragAktionsIdAdd;
    }

    @Override
    public void setAuftragAktionsIdAdd(Long auftragAktionsIdAdd) {
        this.auftragAktionsIdAdd = auftragAktionsIdAdd;
    }

    @Override
    public Long getAuftragAktionsIdRemove() {
        return auftragAktionsIdRemove;
    }

    @Override
    public void setAuftragAktionsIdRemove(Long auftragAktionsIdRemove) {
        this.auftragAktionsIdRemove = auftragAktionsIdRemove;
    }

    @Override
    public void debugModel(Logger logger) {
        if (logger.isDebugEnabled()) {
            logger.debug("Eigenschaften von " + this.getClass().getName());
            logger.debug("  Auftrag-ID : " + getAuftragId());
            logger.debug("  TechLs-ID  : " + getTechLeistungId());
            logger.debug("  Quantity   : " + getQuantity());
            logger.debug("  akt von/bis: " + getAktivVon() + " / " + getAktivBis());
        }
    }

    public boolean isAktiveAt(LocalDate date) {
        final LocalDate aktiveVonLocalDate = aktivVon != null ? LocalDateTime.ofInstant(Instant.ofEpochMilli(aktivVon.getTime()),
                ZoneId.systemDefault()).toLocalDate() : LocalDate.now();
        if (date.isBefore(aktiveVonLocalDate)) {
            return false;
        }

        final LocalDate aktivBisLocalDate = aktivBis != null ? LocalDateTime.ofInstant(Instant.ofEpochMilli(aktivBis.getTime()),
                ZoneId.systemDefault()).toLocalDate() : LocalDate.now();
        return (getAktivBis() == null) || date.isBefore(aktivBisLocalDate);
    }

}


