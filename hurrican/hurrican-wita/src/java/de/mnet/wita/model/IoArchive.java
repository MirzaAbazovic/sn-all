/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.06.2011 11:50:59
 */
package de.mnet.wita.model;

import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Type;

import de.mnet.wita.IOArchiveProperties.IOSource;
import de.mnet.wita.IOArchiveProperties.IOType;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.MeldungsType;

/**
 * Modell fuer die Abbildung eines IO-Archiv-Eintrags.
 */
@Entity
@Table(name = "T_IO_ARCHIVE")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_IO_ARCHIVE_0", allocationSize = 1)
@IoArchiveValid
public class IoArchive extends AbstractWitaModel {

    private static final long serialVersionUID = 1546049346320518019L;

    public static final Function<IoArchive, String> GET_WITA_EXT_ORDER_NO = new Function<IoArchive, String>() {
        @Override
        public String apply(IoArchive input) {
            return input.getWitaExtOrderNo();
        }
    };

    public static final Comparator<IoArchive> COMPARE_BY_REQUEST_TIMESTAMP = new Comparator<IoArchive>() {
        @Override
        public int compare(IoArchive o1, IoArchive o2) {
            return o1.getRequestTimestamp().compareTo(o2.getRequestTimestamp());
        }
    };

    public static Predicate<IoArchive> filterBy(final String extOrderNo) {
        return new Predicate<IoArchive>() {
            @Override
            public boolean apply(IoArchive input) {
                return extOrderNo.equals(input.getWitaExtOrderNo());
            }
        };
    }

    private Date timestampSent;
    private IOType ioType;
    private IOSource ioSource;
    public static final String WITA_EXT_ORDER_NO = "witaExtOrderNo";
    private String witaExtOrderNo;
    public static final String WITA_VERTRAGSNUMMER = "witaVertragsnummer";
    private String witaVertragsnummer;

    private String requestGeschaeftsfall;
    public static final String REQUEST_TIMESTAMP = "requestTimestamp";
    private Date requestTimestamp;
    private String requestXml;
    private String requestMeldungscode;
    private String requestMeldungstext;
    private String requestMeldungstyp;

    @Transient
    public boolean isBereitstellung() {
        return GeschaeftsfallTyp.BEREITSTELLUNG.name().equals(requestGeschaeftsfall);
    }

    @Transient
    public boolean isStorno() {
        return MeldungsType.STORNO.getLongName().equalsIgnoreCase(requestMeldungstyp);
    }

    @Transient
    public boolean isTerminverschiebung() {
        return MeldungsType.TV.getLongName().equalsIgnoreCase(requestMeldungstyp);
    }

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "IO_TYPE")
    public IOType getIoType() {
        return ioType;
    }

    public void setIoType(IOType ioType) {
        this.ioType = ioType;
    }

    public void setIoType(String sendOrRequest) {
        if (StringUtils.isNotBlank(sendOrRequest)) {
            ioType = IOType.valueOf(sendOrRequest);
        }
        else {
            ioType = IOType.UNDEFINED;
        }
    }

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "IO_SOURCE")
    public IOSource getIoSource() {
        return ioSource;
    }

    public void setIoSource(IOSource ioSource) {
        this.ioSource = ioSource;
    }

    public void setIoSource(String sendOrRequest) {
        if (StringUtils.isNotBlank(sendOrRequest)) {
            ioSource = IOSource.valueOf(sendOrRequest);
        }
        else {
            ioSource = IOSource.UNDEFINED;
        }
    }

    @Column(name = "WITA_EXT_ORDER_NO")
    public String getWitaExtOrderNo() {
        return witaExtOrderNo;
    }

    public void setWitaExtOrderNo(String witaExtOrderNo) {
        this.witaExtOrderNo = witaExtOrderNo;
    }

    @NotNull
    @Column(name = "REQUEST_TIMEST")
//    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Type(type="timestamp")
    public Date getRequestTimestamp() {
        return requestTimestamp;
    }

    public void setRequestTimestamp(Date requestTimestamp) {
        this.requestTimestamp = requestTimestamp;
    }

    @Lob
    @NotNull
    @Column(name = "REQUEST_XML")
    public String getRequestXml() {
        return requestXml;
    }

    public void setRequestXml(String requestXml) {
        this.requestXml = requestXml;
    }

    @NotNull
    @Column(name = "TIMESTAMP_SENT")
//    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Type(type="timestamp")
    public Date getTimestampSent() {
        return timestampSent;
    }

    public void setTimestampSent(Date timestampSent) {
        this.timestampSent = timestampSent;
    }

    @NotNull
    @Column(name = "REQUEST_GESCHAEFTSFALL")
    public String getRequestGeschaeftsfall() {
        return requestGeschaeftsfall;
    }

    public void setRequestGeschaeftsfall(String requestGeschaeftsfall) {
        this.requestGeschaeftsfall = requestGeschaeftsfall;
    }

    @Column(name = "REQUEST_MELDUNGSCODE")
    public String getRequestMeldungscode() {
        return requestMeldungscode;
    }

    public void setRequestMeldungscode(String requestMeldungscode) {
        this.requestMeldungscode = requestMeldungscode;
    }

    @Column(name = "REQUEST_MELDUNGSTEXT")
    public String getRequestMeldungstext() {
        return requestMeldungstext;
    }

    public void setRequestMeldungstext(String requestMeldungstext) {
        this.requestMeldungstext = requestMeldungstext;
    }

    @Column(name = "REQUEST_TYP")
    public String getRequestMeldungstyp() {
        return requestMeldungstyp;
    }

    public void setRequestMeldungstyp(String requestMeldungstyp) {
        this.requestMeldungstyp = requestMeldungstyp;
    }

    @Column(name = "WITA_VERTRAGSNUMMER")
    public String getWitaVertragsnummer() {
        return witaVertragsnummer;
    }

    public void setWitaVertragsnummer(String witaVertragsnummer) {
        this.witaVertragsnummer = witaVertragsnummer;
    }

    @Override
    public String toString() {
        return "IoArchiveEntry [timestampSent=" + timestampSent + ", ioType=" + ioType + ", ioSource=" + ioSource
                + ", witaExtOrderNo=" + witaExtOrderNo + ", witaVertragsnummer=" + witaVertragsnummer
                + ", requestGeschaeftsfall=" + requestGeschaeftsfall + ", requestTimestamp=" + requestTimestamp
                + ", requestXml=<XML>, requestMeldungscode=" + requestMeldungscode + ", requestMeldungstext="
                + requestMeldungstext + ", requestMeldungstyp=" + requestMeldungstyp + "]";
    }
}
