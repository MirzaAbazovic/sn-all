/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.06.2011 16:00:26
 */
package de.mnet.wita.model;

import java.time.*;
import java.time.temporal.*;
import java.util.*;

import de.augustakom.common.model.EntityBuilder;
import de.augustakom.common.model.SessionFactoryAware;
import de.mnet.wita.IOArchiveProperties.IOSource;
import de.mnet.wita.IOArchiveProperties.IOType;
import de.mnet.wita.message.GeschaeftsfallTyp;

@SessionFactoryAware("cc.sessionFactory")
@SuppressWarnings("unused")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "URF_UNREAD_FIELD", justification = "read by reflection within EntityBuilder")
public class IoArchiveBuilder extends EntityBuilder<IoArchiveBuilder, IoArchive> {

    private Date timestampSent = new Date();
    private IOType ioType = IOType.UNDEFINED;
    private IOSource ioSource = IOSource.UNDEFINED;
    private String witaExtOrderNo = "123456";
    private String witaVertragsnummer;

    private Date requestTimestamp = new Date();
    private String requestXml = "<xml/>";
    private String requestGeschaeftsfall = GeschaeftsfallTyp.BEREITSTELLUNG.name();
    private String requestMeldungscode = "0000";
    private String requestMeldungstext = "Alles Ok";
    private String requestMeldungstyp;

    public IoArchiveBuilder withIoType(IOType ioType) {
        this.ioType = ioType;
        return this;
    }

    public IoArchiveBuilder withIoSource(IOSource ioSource) {
        this.ioSource = ioSource;
        return this;
    }

    public IoArchiveBuilder withWitaExtOrderNo(String witaExtOrderNo) {
        this.witaExtOrderNo = witaExtOrderNo;
        return this;
    }

    public IoArchiveBuilder withWitaVertragsnummer(String witaVertragsnummer) {
        this.witaVertragsnummer = witaVertragsnummer;
        return this;
    }

    public IoArchiveBuilder withRequestGeschaeftsfall(GeschaeftsfallTyp requestGeschaeftsfall) {
        this.requestGeschaeftsfall = requestGeschaeftsfall.name();
        return this;
    }

    public IoArchiveBuilder withRequestGeschaeftsfall(de.mnet.wbci.model.GeschaeftsfallTyp requestGeschaeftsfall) {
        this.requestGeschaeftsfall = requestGeschaeftsfall.name();
        return this;
    }


    public IoArchiveBuilder withRequestXml(String requestXml) {
        this.requestXml = requestXml;
        return this;
    }

    public IoArchiveBuilder withRequestMeldungstyp(String meldungstyp) {
        this.requestMeldungstyp = meldungstyp;
        return this;
    }

    public IoArchiveBuilder withRequestTimestamp(LocalDateTime requestTimestamp) {
        this.requestTimestamp = requestTimestamp != null ? new Date(requestTimestamp.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()) : null;
        return this;
    }

    public IoArchiveBuilder withTimestampSent(LocalDateTime timestampSent) {
        this.timestampSent = timestampSent != null ? new Date(timestampSent.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()) : null;
        return this;
    }

}
