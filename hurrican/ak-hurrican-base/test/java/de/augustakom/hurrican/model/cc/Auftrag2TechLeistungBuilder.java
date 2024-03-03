/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.10.2009 08:59:34
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.common.tools.lang.DateTools;


/**
 * Entity-Builder fuer Auftrag2TechLeistung Objekte.
 *
 *
 */
@SuppressWarnings("unused")
public class Auftrag2TechLeistungBuilder extends AbstractCCIDModelBuilder<Auftrag2TechLeistungBuilder, Auftrag2TechLeistung> {

    private Long id;
    private Long auftragId;
    private AuftragBuilder auftragBuilder;
    private Long techLeistungId;
    private TechLeistungBuilder techLeistungBuilder;
    private Long quantity = Long.valueOf(1);
    private Date aktivVon;
    private Date aktivBis;
    private Long verlaufIdReal;
    private Long verlaufIdKuend;
    private Long auftragAktionsIdAdd;
    private Long auftragAktionsIdRemove;

    /**
     * @see de.augustakom.common.model.EntityBuilder#initialize()
     */
    @Override
    protected void initialize() {
        super.initialize();
        this.aktivVon = DateTools.createDate(2009, 0, 1);
        this.aktivBis = DateTools.getHurricanEndDate();
    }


    public AuftragBuilder getAuftragBuilder() {
        return auftragBuilder;
    }

    public TechLeistungBuilder getTechLeistungBuilder() {
        return techLeistungBuilder;
    }


    public Auftrag2TechLeistungBuilder withAuftragBuilder(AuftragBuilder auftragBuilder) {
        this.auftragBuilder = auftragBuilder;
        return this;
    }

    public Auftrag2TechLeistungBuilder withAuftragId(Long auftragId) {
        this.auftragId = auftragId;
        return this;
    }

    public Auftrag2TechLeistungBuilder withTechleistungBuilder(TechLeistungBuilder techLeistungBuilder) {
        this.techLeistungBuilder = techLeistungBuilder;
        return this;
    }

    public Auftrag2TechLeistungBuilder withAktivVon(Date aktivVon) {
        this.aktivVon = aktivVon;
        return this;
    }

    public Auftrag2TechLeistungBuilder withVerlaufIdReal(Long verlaufIdReal) {
        this.verlaufIdReal = verlaufIdReal;
        return this;
    }

    public Auftrag2TechLeistungBuilder withVerlaufIdKuend(Long verlaufIdKuend) {
        this.verlaufIdKuend = verlaufIdKuend;
        return this;
    }

    public Auftrag2TechLeistungBuilder withAuftragAktionsIdAdd(Long auftragAktionsIdAdd) {
        this.auftragAktionsIdAdd = auftragAktionsIdAdd;
        return this;
    }

    public Auftrag2TechLeistungBuilder withAuftragAktionsIdRemove(Long auftragAktionsIdRemove) {
        this.auftragAktionsIdRemove = auftragAktionsIdRemove;
        return this;
    }

    public Auftrag2TechLeistungBuilder withAktivBis(Date aktivBis) {
        this.aktivBis = aktivBis;
        return this;
    }

    public Auftrag2TechLeistungBuilder withQuantity(Long quantity) {
        this.quantity = quantity;
        return this;
    }

    public Auftrag2TechLeistungBuilder withTechLeistungId(Long techLeistungId) {
        this.techLeistungId = techLeistungId;
        return this;
    }

}


