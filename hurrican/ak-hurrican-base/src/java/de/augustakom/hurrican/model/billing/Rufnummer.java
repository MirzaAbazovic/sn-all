/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.06.2004 11:21:35
 */
package de.augustakom.hurrican.model.billing;

import java.util.*;
import javax.persistence.*;
import com.google.common.base.CharMatcher;
import com.google.common.base.Predicate;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.model.TNB;
import de.augustakom.hurrican.model.cc.Portierungsart;

/**
 * Abbildung einer Rufnummer aus dem Billing-System.
 *
 *
 */
public class Rufnummer extends AbstractHistoryModel {

    private static final long serialVersionUID = 369698838303702010L;

    public static final String DN_NO = "dnNo";
    public static final String DN_NO_ORIG = "dnNoOrig";
    public static final String AUFTRAG_NO_ORIG = "auftragNoOrig";
    public static final String ONKZ = "onKz";
    public static final String DN_BASE = "dnBase";
    public static final String DIRECT_DIAL = "directDial";
    public static final String RANGE_FROM = "rangeFrom";
    public static final String RANGE_TO = "rangeTo";
    public static final String NON_BILLABLE = "nonBillable";

    public static final String CFG_REG_EXP_REQUESTED_INFO_ROUTING = "ROUTING_DESTINATION";

    /**
     * Wert fuer oeNoOrig kennzeichnet den Rufnummerntyp 'Standard'.
     */
    public static final Long OE__NO_DEFAULT = Long.valueOf(60);
    /**
     * Wert fuer oeNoOrig kennzeichnet den Rufnummerntyp 'Routing'.
     */
    public static final Long OE__NO_ROUTING = Long.valueOf(65);
    /**
     * Wert fuer oeNoOrig kennzeichnet den Rufnummerntyp 'Ansage geaenderte Rufnummer'.
     */
    public static final Long OE__NO_GEAEND_RUF = Long.valueOf(66);
    /**
     * Wert fuer oeNoOrig kennzeichnet den Rufnummerntyp 'Ansage kein Anschluss'.
     */
    public static final Long OE__NO_KEIN_ANSCHLUSS = Long.valueOf(67);
    /**
     * Wert fuer oeNoOrig kennzeichnet den Rufnummerntyp 'Appartment'.
     */
    public static final Long OE__NO_APPARTMENT = Long.valueOf(68);

    /**
     * Wert fuer portMode kennzeichnet eine abgehende Portierung der Rufnummer.
     */
    public static final String PORT_MODE_ABGEHEND = "PORTIERUNG_A";
    /**
     * Wert fuer portMode kennzeichnet den Rueckfall der Rufnummer.
     */
    public static final String PORT_MODE_RUECKFALL = "RUECKFALL";
    /**
     * Wert fuer portMode kennzeichnet eine kommende Portierung der Rufnummer.
     */
    public static final String PORT_MODE_KOMMEND = "PORTIERUNG_K";
    /**
     * Wert fuer portMode kennzeichnet eine Deaktivierung (DN-Besitzer=M-net).
     */
    public static final String PORT_MODE_DEAKTIVIERUNG = "DEAKTIVIERUNG";

    /**
     * Wert fuer Kennzeichnung abgebender Carrier Dtag
     */
    public static final String LAST_CARRIER_DTAG = "DTAG";
    /**
     * Wert fuer Kennzeichnung aufnehmender Carrier DTAG
     */
    public static final String FUTURE_CARRIER_DTAG = "DTAG";

    /**
     * Suchstrategie, um Rufnummern ueber ein Query-Objekt vom Typ <code>RufnummerQuery</code> zu suchen. <br>
     * Benoetigte Parameter: <br> 0: Objekt vom Typ <code>RufnummerQuery</code>
     */
    public static final short STRATEGY_FIND_BY_QUERY = 0;

    /**
     * Suchstrategie, um die Rufnummern zu einem best. Billing-Auftrag zu suchen. <br> Benoetigte Parameter: <br> 0:
     * Long-Objekt mit der (originalen) Auftragsnummer. <br> 1: Boolean-Objekt um zu definieren, ob nur die aktuellen
     * (true) oder alle (false) Rufnummern zu der Auftragsnummer ermittelt werden sollen. <br> 2: (optional!)
     * Boolean-Objekt um zu definieren, ob nur die letzten (HistLast=1) Rufnummern zu der Auftragsnummer ermittelt
     * werden sollen.
     */
    public static final short STRATEGY_FIND_BY_AUFTRAG_NO_ORIG = 1;

    public static final Predicate<Rufnummer> PORTMODE_KOMMEND = new Predicate<Rufnummer>() {
        @Override
        public boolean apply(Rufnummer rufnummer) {
            return Rufnummer.PORT_MODE_KOMMEND.equals(rufnummer.getPortMode());
        }
    };

    public static final Predicate<Rufnummer> PORTMODE_ABGEHEND = new Predicate<Rufnummer>() {
        @Override
        public boolean apply(Rufnummer rufnummer) {
            return Rufnummer.PORT_MODE_ABGEHEND.equals(rufnummer.getPortMode());
        }
    };

    public static final Predicate<Rufnummer> REAL_DATE_NOT_IN_THE_PAST = new Predicate<Rufnummer>() {
        @Override
        public boolean apply(Rufnummer rufnummer) {
            return !rufnummer.isRealDateInVergangenheit();
        }
    };

    public static final Predicate<Rufnummer> REAL_DATE_NOT_NULL = new Predicate<Rufnummer>() {
        @Override
        public boolean apply(Rufnummer rufnummer) {
            return rufnummer != null;
        }
    };

    /**
     * Zum Ausfiltern der sogenannten Z-Rufnummern.
     * <p/>
     * Rufnummern, die bei einem Anbieterwechsel zurueck an den Carrier fallen, von dem wir eine Rufnummer urspruenglich
     * portiert haben, haben Portmode abgehend aber Future-Carrier null.
     */
    public static final Predicate<Rufnummer> HAS_FUTURE_CARRIER = new Predicate<Rufnummer>() {
        @Override
        public boolean apply(Rufnummer rufnummer) {
            return StringUtils.isNotBlank(rufnummer.getFutureCarrier());
        }
    };

    public static final Predicate<Rufnummer> LAST_CARRIER_NOT_MNET_CARRIER = new Predicate<Rufnummer>() {
        @Override
        public boolean apply(Rufnummer rufnummer) {
            return (rufnummer.getLastCarrier() == null)
                    || StringTools.isNotIn(rufnummer.getLastCarrier(), 
                        new String[] { TNB.AKOM.carrierName, TNB.MNET.carrierName, TNB.NEFKOM.carrierName });
        }
    };

    public static final Predicate<Rufnummer> IS_EINZELANSCHLUSS = new Predicate<Rufnummer>() {
        @Override
        public boolean apply(Rufnummer rufnummer) {
            return StringUtils.isEmpty(rufnummer.getDirectDial());
        }
    };

    private Long dnNo = null;
    private Long dnNoOrig = null;
    private Long auftragNoOrig = null;
    private String onKz = null;
    private String dnBase = null;
    private String directDial = null;
    private String rangeFrom = null;
    private String rangeTo = null;
    private Long dnSize = null;
    private boolean mainNumber = false;
    private Long blockNoOrig = null;
    private Long oeNoOrig = null;
    private String portMode = null;
    private Date realDate = null;
    private Date wishDate = null;
    private String remarks = null;
    private String state = null;
    private Date portierungVon = null;
    private Date portierungBis = null;
    private Boolean nonBillable = null;
    private DNTNB lastCarrierDnTnb;
    private DNTNB actCarrierDnTnb;
    private DNTNB futureCarrierDnTnb;

    public boolean isRealDateInVergangenheit() {
        return DateTools.isDateBefore(realDate, new Date());
    }

    /**
     * Erstellt aus den Rufnummern-Daten einen String, der die Rufnummer darstellt. <br> Zusammensetzung: ONKZ / DN_BASE
     * DIRECT_DIAL _ RANG_FROM - RANG_TO
     *
     * @return
     */
    public String getRufnummer() {
        StringBuilder s = new StringBuilder();
        s.append(getOnKz());
        s.append("/");
        s.append(getDnBase());
        if (StringUtils.isNotBlank(getDirectDial())) {
            s.append(" ");
            s.append(getDirectDial());
        }
        if (isBlock()) {
            s.append(" ");
            s.append(StringUtils.trim(getRangeFrom()));
            s.append(" - ");
            s.append(StringUtils.trim(getRangeTo()));
        }

        return s.toString();
    }

    /**
     * Ermittelt die maximale Länge der Rufnummer. <br> Zusammensetzung: ONKZ + DN_BASE.
     * Bei Blockrufnummern wird je nach Länge DIRECT_DIAL oder RANGE_FROM oder
     * RAGNE_TO dazugerechnet.
     *
     * @return
     */
    public int getRufnummerLength() {
        int cnt = getOnKz().length();
        cnt += getDnBase().length();

        if (isBlock()) {
            cnt += StringUtils.trim(getRangeFrom()).length();
        } else if (StringUtils.isNotBlank(getDirectDial())) {
            cnt += getDirectDial().length();
        }

        return cnt;
    }

    public boolean isBlock() {
        return StringUtils.isNotBlank(getRangeFrom()) && StringUtils.isNotBlank(getRangeTo());
    }

    /**
     * Ermittelt das Portierungszeitfenster fuer die Rufnummer. <br> <br> Moegliche Zeitfenster: <ul> <li>6-8 Uhr:
     * PORTIERUNG_EXPORT (=es werden nur Rufnummern portiert, keine TAL-Schaltung) <li>5-7 Uhr: PORTIERUNG_SONDER (=es
     * werden nur Rufnummern portiert) <li>5-12 Uhr: PORTIERUNG_STANDARD (=Portierung mit oder ohne TAL-Schaltung)
     * </ul>
     *
     * @return Konstante aus <code>Portierungsart</code>, die das Portierungsfenster definiert.
     *
     */
    public Long getPortingTimeFrame() {
        Long portierungsart = null;
        int hourPortVon = DateTools.getHourOfDay(getPortierungVon());
        int hourPortTo = DateTools.getHourOfDay(getPortierungBis());

        if ((hourPortVon == 6) && (hourPortTo == 8)) {
            portierungsart = Portierungsart.PORTIERUNG_EXPORT;
        }
        else if ((hourPortVon == 5) && (hourPortTo == 7)) {
            portierungsart = Portierungsart.PORTIERUNG_SONDER;
        }
        else {
            portierungsart = Portierungsart.PORTIERUNG_STANDARD;
        }

        return portierungsart;
    }

    /**
     * Fuegt die beiden Strings <code>dnBase</code> und <code>directDial</code> zusammen und gibt den Gesamt-String
     * zurueck.
     *
     * @return
     */
    public String getDnBaseAndDirect() {
        return StringUtils.join(new Object[] { getDnBase(), getDirectDial() });
    }

    /**
     * Ueberprueft, ob die aktuelle Rufnummer mit der Rufnummer aus <code>toCheck</code> identisch ist. <br> Die
     * Rufnummern sind dann identisch, wenn folgende Parameter die gleichen Werte aufweisen: <br> <ul> <li>onKz
     * <li>dnBase <li>directDial <li>rangeFrom <li>rangeTo </ul>
     *
     * @param toCheck
     * @return true wenn die aktuelle und die zu pruefende Rufnummer identisch sind.
     */
    public boolean isRufnummerEqual(Rufnummer toCheck) {
        return (toCheck != null) &&
                StringUtils.equals(getOnKz(), toCheck.getOnKz()) &&
                StringUtils.equals(getDnBase(), toCheck.getDnBase()) &&
                StringUtils.equals(getDirectDial(), toCheck.getDirectDial()) &&
                StringUtils.equals(getRangeFrom(), toCheck.getRangeFrom()) &&
                StringUtils.equals(getRangeTo(), toCheck.getRangeTo());
    }

    /**
     * Prueft, ob es sich bei der Rufnummer um eine PreSelect-Rufnummer handelt. <br> Dies ist dann der Fall, wenn
     * folgende Bedingungen erfuellt sind: <ul> <li>Rufnummertyp ist <> 'Appartement' (68) <li>Port-Mode ist nicht
     * gefuellt <li>Last-Carrier ist nicht eingetragen </ul>
     *
     * @return true, wenn es sich bei der Rufnummer um eine PreSelect-Rufnummer handelt
     */
    public boolean isPreSelect() {
        return NumberTools.notEqual(getOeNoOrig(), OE__NO_APPARTMENT)
                && StringUtils.isBlank(getPortMode())
                && StringUtils.isBlank(getLastCarrier());
    }

    /**
     * Prueft, ob es sich bei der Rufnummer um eine Default-Rufnummer (Typ 60) handelt.
     *
     * @return true, wenn es sich bei der Rufnummer um eine Default-Rufnummer handelt
     *
     */
    public boolean isDefaultDN() {
        return NumberTools.equal(getOeNoOrig(), OE__NO_DEFAULT);
    }

    /**
     * Prueft, ob es sich bei der Rufnummer um eine Appartement-Rufnummer (Typ 68) handelt.
     *
     * @return true, wenn es sich bei der Rufnummer um eine Appartement-Rufnummer handelt
     */
    public boolean isAppartement() {
        return NumberTools.equal(getOeNoOrig(), OE__NO_APPARTMENT);
    }

    /**
     * Prueft, ob es sich bei der Rufnummer um eine Routing-Rufnummer (Typ 65) handelt.
     *
     * @return true, wenn es sich bei der Rufnummer um eine Routing-Rufnummer handelt
     *
     */
    public boolean isRouting() {
        return NumberTools.equal(getOeNoOrig(), OE__NO_ROUTING);
    }

    /**
     * Prueft, ob es sich bei der Rufnummer vom Typ 67 (Ansage kein Anschluss).
     *
     * @return
     *
     */
    public boolean isKeinAnschluss() {
        return NumberTools.equal(getOeNoOrig(), OE__NO_KEIN_ANSCHLUSS);
    }

    /**
     * Prueft, ob es sich bei der Rufnummer um eine Nummer vom Typ 66 (AGRU) handelt.
     *
     * @return true, wenn die Rufnummer als AGRU markiert ist.
     */
    public boolean isAGRU() {
        return NumberTools.equal(getOeNoOrig(), OE__NO_GEAEND_RUF);
    }

    /**
     * Prueft, ob es sich bei dem aktuellen Portierungs-Modus um eine 'abgehende' Portierung oder einen 'Rueckfall' der
     * Rufnummer handelt.
     *
     * @return true, wenn in der Rufnummer eine abgehende Portierung bzw. ein Rueckfall eingetragen ist.
     */
    public boolean isPortierungAbgehend() {
        return (StringUtils.equals(getPortMode(), PORT_MODE_ABGEHEND) || StringUtils.equals(getPortMode(),
                PORT_MODE_RUECKFALL));
    }

    public boolean isPortierungRueckfall() {
        return StringUtils.equals(getPortMode(), PORT_MODE_RUECKFALL);
    }

    public boolean isPortierungDeaktivierung() {
        return StringUtils.equals(getPortMode(), PORT_MODE_DEAKTIVIERUNG);
    }

    /**
     * Prueft, ob es sich bei der Rufnummer um eine Mobilrufnummer handelt. Das wird anhand der ONKZ festgestellt.
     *
     * @return true, wenn die Rufnummer eine Mobilnummer ist, ansonsten false.
     */
    public boolean isMobile() {
        return onKz.startsWith("01");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[Rufnummer-Nr: ").append(dnNoOrig);
        if (onKz != null) {
            sb.append(", Vorwahl: ").append(onKz);
        }
        if (dnBase != null) {
            sb.append(", Rufnummerstamm: ").append(dnBase);
        }
        if (directDial != null) {
            sb.append(", Durchwahl: ").append(directDial);
        }
        if (rangeFrom != null) {
            sb.append(", Bereich von: ").append(rangeFrom);
        }
        if (rangeTo != null) {
            sb.append(", Bereich bis: ").append(rangeTo);
        }
        if (realDate != null) {
            sb.append(", Tatsächlicher Termin: ").append(DateTools.formatDate(realDate, DateTools.PATTERN_DAY_MONTH_YEAR));
        }
        sb.append("]");
        return sb.toString();
    }


    public Long getAuftragNoOrig() {
        return auftragNoOrig;
    }

    public void setAuftragNoOrig(Long auftragNoOrig) {
        this.auftragNoOrig = auftragNoOrig;
    }

    public String getDirectDial() {
        return directDial;
    }

    public void setDirectDial(String directDial) {
        this.directDial = directDial;
    }

    public String getDnBase() {
        return dnBase;
    }

    public void setDnBase(String dnBase) {
        this.dnBase = dnBase;
    }

    public Long getDnNo() {
        return dnNo;
    }

    public void setDnNo(Long dnNo) {
        this.dnNo = dnNo;
    }

    public Long getDnNoOrig() {
        return dnNoOrig;
    }

    public void setDnNoOrig(Long dnNoOrig) {
        this.dnNoOrig = dnNoOrig;
    }

    public boolean isMainNumber() {
        return mainNumber;
    }

    public void setMainNumber(boolean mainNumber) {
        this.mainNumber = mainNumber;
    }

    public String getOnKz() {
        return onKz;
    }

    public void setOnKz(String onKz) {
        this.onKz = onKz;
    }

    @Transient
    public String getOnKzWithoutLeadingZeros() {
        return CharMatcher.is('0').trimLeadingFrom(onKz);
    }

    /**
     * Gibt den Rufnummerntyp zurueck.
     *
     * @return Returns the orderEntryNoOrig.
     */
    public Long getOeNoOrig() {
        return oeNoOrig;
    }

    public void setOeNoOrig(Long orderEntryNoOrig) {
        this.oeNoOrig = orderEntryNoOrig;
    }

    public String getPortMode() {
        return portMode;
    }

    public void setPortMode(String portMode) {
        this.portMode = portMode;
    }

    public String getRangeFrom() {
        return rangeFrom;
    }

    public void setRangeFrom(String rangeFrom) {
        this.rangeFrom = rangeFrom;
    }

    public String getRangeTo() {
        return rangeTo;
    }

    public void setRangeTo(String rangeTo) {
        this.rangeTo = rangeTo;
    }

    public Date getRealDate() {
        return realDate;
    }

    public void setRealDate(Date realDate) {
        this.realDate = realDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getFutureCarrier() {
        return (futureCarrierDnTnb != null) ? futureCarrierDnTnb.getTnb() : null;
    }

    public String getFutureCarrierPortKennung() {
        return (futureCarrierDnTnb != null) ? futureCarrierDnTnb.getPortKennung() : null;
    }

    public DNTNB getFutureCarrierDnTnb() {
        return futureCarrierDnTnb;
    }

    public void setFutureCarrierDnTnb(DNTNB futureCarrierDnTnb) {
        this.futureCarrierDnTnb = futureCarrierDnTnb;
    }

    public String getActCarrier() {
        return (actCarrierDnTnb != null) ? actCarrierDnTnb.getTnb() : null;
    }

    public String getActCarrierPortKennung() {
        return (actCarrierDnTnb != null) ? actCarrierDnTnb.getPortKennung() : null;
    }

    public DNTNB getActCarrierDnTnb() {
        return actCarrierDnTnb;
    }

    public void setActCarrierDnTnb(DNTNB actCarrierDnTnb) {
        this.actCarrierDnTnb = actCarrierDnTnb;
    }

    public String getLastCarrier() {
        return (lastCarrierDnTnb != null) ? lastCarrierDnTnb.getTnb() : null;
    }

    public String getLastCarrierPortKennung() {
        return (lastCarrierDnTnb != null) ? lastCarrierDnTnb.getPortKennung() : null;
    }

    public DNTNB getLastCarrierDnTnb() {
        return lastCarrierDnTnb;
    }

    public void setLastCarrierDnTnb(DNTNB lastCarrierDnTnb) {
        this.lastCarrierDnTnb = lastCarrierDnTnb;
    }

    public Date getPortierungBis() {
        return portierungBis;
    }

    public void setPortierungBis(Date portierungBis) {
        this.portierungBis = portierungBis;
    }

    public Date getPortierungVon() {
        return portierungVon;
    }

    public void setPortierungVon(Date portierungVon) {
        this.portierungVon = portierungVon;
    }

    public Date getWishDate() {
        return wishDate;
    }

    public void setWishDate(Date wishDate) {
        this.wishDate = wishDate;
    }

    public Long getBlockNoOrig() {
        return blockNoOrig;
    }

    public void setBlockNoOrig(Long blockNoOrig) {
        this.blockNoOrig = blockNoOrig;
    }

    public Boolean getNonBillable() {
        return nonBillable;
    }

    public void setNonBillable(Boolean nonBillable) {
        this.nonBillable = nonBillable;
    }

    public Long getDnSize() {
        return dnSize;
    }

    public void setDnSize(Long dnSize) {
        this.dnSize = dnSize;
    }

}
