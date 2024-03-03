/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.11.2005 12:26:52
 */
package de.augustakom.hurrican.model.cc;

import java.io.*;
import java.util.*;
import javax.persistence.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.model.DebugModel;
import de.augustakom.common.model.HistoryModel;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.model.shared.iface.ExternLeistungAwareModel;
import de.augustakom.hurrican.model.shared.iface.ExternMiscAwareModel;

/**
 * Modell zur Konfiguration von technisch relevanten Leistungen. <br> Die Leistung ist ueber das Feld
 * <code>externLeistungNo</code> mit der Billing-Leistung verlinkt. Im Billing-System steht der entsprechende Wert in
 * <code>LEISTUNG.EXTERN_LEISTUNG__NO</code>.
 *
 *
 */
public class TechLeistung extends BADefault implements HistoryModel, ExternLeistungAwareModel, ExternMiscAwareModel,
        DebugModel, Serializable {

    /**
     * Typ-Definition fuer DSL-Optionen.
     */
    public static final String TYP_DSL_OPTION = "DSL_OPTION";
    /**
     * Typ-Definition fuer DSL Error-Correction Art (Interleaving/Fastpath)
     */
    public static final String TYP_DSL_ERROR_CORRECTION = "DSL_ERR_CORRECT";
    /**
     * Typ-Definition fuer Online-Optionen.
     */
    public static final String TYP_ONLINE = "ONLINE_OPTION";
    /**
     * Typ-Definition fuer Online-Optionen.
     */
    public static final String TYP_ONLINE_IP = "ONL_IP_OPTION";
    /**
     * Typ-Definition fuer Online-IP-Default.
     */
    public static final String TYP_ONL_IP_DEFAULT = "ONL_IP_DEFAULT";
    /**
     * Typ-Definition fuer Downstream-Leistungen.
     */
    public static final String TYP_DOWNSTREAM = "DOWNSTREAM";
    /**
     * Typ-Definition fuer Upstream-Leistungen.
     */
    public static final String TYP_UPSTREAM = "UPSTREAM";
    /**
     * Typ-Definition fuer das Phone-Protokoll.
     */
    public static final String TYP_PHONE_PROTOKOLL = "PHONE_PROTOKOLL";
    /**
     * Typ-Definition fuer das Phone-Protokoll.
     */
    public static final String TYP_ISDN_TYP = "ISDN_TYP";
    /**
     * Typ-Definition fuer Leistungen zu Diensterufnummern.
     */
    public static final String TYP_SERVICERUFNUMMER = "SERVICERUFNUMME";
    /**
     * Typ-Definition fuer VoIP-Leistungen.
     */
    public static final String TYP_VOIP = "VOIP";
    /** Typ-Definition fuer REALVARIANTE */
    public static final String TYP_REALVARIANTE = "REALVARIANTE";
    /**
     * Typ-Definition fuer VoIP_ADD-Leistungen.
     */
    public static final String TYP_VOIP_ADD = "VOIP_ADD";
    /**
     * Typ-Definition fuer Connect-Leitungsarten
     */
    public static final String TYP_CONNECT_LEITUNG = "CONNECT_LEITUNG";
    /**
     * Typ-Definition fuer Cross-Connection Zusatzleistungen
     */
    public static final String TYP_CROSS_CONNECTION = "XCONN";
    /**
     * Typ-Definition fuer Kombi-Leistungen
     */
    public static final String TYP_KOMBI = "KOMBI";
    /**
     * Typ-Definition fuer Endgeraeteport-Leistungen
     */
    public static final String TYP_EG_PORT = "EG_PORT";
    /**
     * Typ-Definition fuer zusaetzliche Endgeraeteport-Leistungen
     */
    public static final String TYP_EG_PORT_ADD = "EG_PORT_ADD";
    /**
     * Typ-Definition fuer Wholesale-spezifische Leistungen
     */
    public static final String TYP_WHOLESALE = "WHOLESALE";
    public static final String TYP_SIPTRUNK_QOS_PROFILE = "QOS_PROFILE";
    public static final String TYP_SPRACHKANAELE = "SPRACHKANAELE";
    public static final String TYP_LAYER2 = "LAYER2";
    public static final String TYP_ENDGERAET = "ENDGERAET";
    public static final String TYP_INHOUSE_VERKABELUNG = "INHOUSE_VERKABELUNG";

    /**
     * Array mit allen moeglichen Typ-Definitionen.
     */
    public static final String[] TYPEN = new String[] { TYP_DSL_OPTION, TYP_DSL_ERROR_CORRECTION, TYP_ONLINE,
            TYP_ONLINE_IP, TYP_ONL_IP_DEFAULT, TYP_DOWNSTREAM, TYP_UPSTREAM, TYP_PHONE_PROTOKOLL, TYP_SERVICERUFNUMMER,
            TYP_VOIP, TYP_VOIP_ADD, TYP_CONNECT_LEITUNG, TYP_CROSS_CONNECTION, TYP_KOMBI, TYP_EG_PORT, TYP_EG_PORT_ADD,
            TYP_WHOLESALE, TYP_SIPTRUNK_QOS_PROFILE, TYP_SPRACHKANAELE, TYP_LAYER2,
            TYP_ENDGERAET, TYP_INHOUSE_VERKABELUNG };
    /**
     * ID fuer die Leistung 'Business-CPE'
     */
    public static final Long ID_BUSINESS_CPE = Long.valueOf(325);
    public static final Long ID_VOIP_MGA = Long.valueOf(300);
    public static final Long ID_VOIP_TK = Long.valueOf(301);
    public static final Long ID_VOIP_PMX = Long.valueOf(302);
    // ONL_IP_OPTION
    public static final Long ID_FIXED_IP_AND_ALWAYS_ON = Long.valueOf(4);
    public static final Long ID_ADDITIONAL_IP = Long.valueOf(5);
    public static final Long ID_FIXED_IP = Long.valueOf(6);
    public static final Long ID_DHCP_V6_PD = Long.valueOf(55);
    public static final Long ID_DYNAMIC_IP_V4 = Long.valueOf(56);
    public static final Long ID_DYNAMIC_IP_V6 = Long.valueOf(57);
    public static final List<Long> TECH_LEISTUNGEN_IPV6 = Arrays.asList(ID_DHCP_V6_PD, ID_DYNAMIC_IP_V6);
    public static final List<Long> TECH_LEISTUNGEN_IPV4 = Arrays.asList(ID_FIXED_IP_AND_ALWAYS_ON, ID_ADDITIONAL_IP,
            ID_FIXED_IP, ID_DYNAMIC_IP_V4);
    public static final long TECH_LEISTUNG_SIPTRUNK_QOS_PROFILE = 460L;
    public static final Long TECH_LEISTUNG_EFM = Long.valueOf(550L);

    /**
     * Array mit allen Typ-Bezeichnungen, die 'unique' sind. Das bedeutet, Leistungen von diesem Typ duerfen auf einem
     * Auftrag nicht zeitlich ueberlappen.
     */
    private static final String[] UNIQUE_TYPES = new String[] {
            TYP_DOWNSTREAM, TYP_UPSTREAM, TYP_PHONE_PROTOKOLL, TYP_VOIP, TYP_DSL_ERROR_CORRECTION, TYP_KOMBI,
            TYP_SIPTRUNK_QOS_PROFILE, TYP_SPRACHKANAELE, TYP_LAYER2, TYP_ENDGERAET,
            TYP_INHOUSE_VERKABELUNG };
    private Long externLeistungNo = null;
    private Long externMiscNo = null;
    private String name = null;
    private String typ = null;
    private Long longValue = null;
    private String strValue = null;
    private String parameter = null;
    private String baHinweis;
    private String prodNameStr = null;
    private String description = null;
    private Boolean checkQuantity = null;
    private Boolean snapshotRel = null;
    private Date gueltigVon = null;
    private Date gueltigBis = null;
    private Boolean aktivBisNullOnSync;
    private Boolean preventAutoDispatch;
    private Boolean autoExpire;

    /**
     * Gibt an, ob Leistungen vom Typ <code>lsTyp</code> ein DSLAM-Profil beeinflussen. <br> Dies ist aktuell bei
     * folgenden Typen der Fall: <ul> <li>DOWNSTREAM <li>UPSTREAM <li>DSL_OPTION </ul>
     *
     * @param lsTyp zu pruefender Typ
     * @return true wenn der Typ ein DSLAM-Profil beeinflusst.
     *
     */
    public static boolean affectsDSLAMProfile(String lsTyp) {
        return StringTools.isIn(lsTyp, new String[] { TYP_DOWNSTREAM, TYP_UPSTREAM, TYP_DSL_OPTION });
    }

    /**
     * Ermittelt aus der Liste <code>leistungen</code> die ID der techn. Leistung vom Typ <code>typ</code>.
     *
     * @param leistungen
     * @param typ
     * @return
     *
     */
    public static Long getTechLsId4Typ(List<TechLeistung> leistungen, String typ) {
        if (CollectionTools.isNotEmpty(leistungen)) {
            for (TechLeistung tl : leistungen) {
                if (StringUtils.equals(tl.getTyp(), typ)) {
                    return tl.getId();
                }
            }
        }
        return null;
    }

    /**
     * Gibt an, ob der Typ der Leistung 'unique' ist. <br> 'Unique' bedeutet in diesem Fall, dass pro Auftrag nur eine
     * aktive Leistung des Typs zulaessig ist.
     *
     * @return true wenn der Leistungstyp 'unique' ist.
     *
     */
    public boolean isTypUnique() {
        return isTypUnique(getTyp());
    }

    public static boolean isTypUnique(String typ) {
        return StringTools.isIn(typ, UNIQUE_TYPES);
    }

    /**
     * Gibt einen String mit dem Leistungstyp und dem Leistungsnamen zurueck. <br> Der Leistungstyp wird wie folgt
     * formatiert: <br> - Underscores '_' werden durch ein Leerzeichen ersetzt  <br> - von jedem Wort wird der erste
     * Buchstabe gross, alle anderen klein geschrieben <br>
     *
     * @return Kombination von Leistungstyp und Leistungsname (z.B. 'Downstream: 1024 kbit/s')
     *
     */
    public String getTypAndName() {
        String typ = WordUtils.capitalizeFully(StringUtils.replace(getTyp(), "_", " "));
        return typ + ": " + getName();
    }

    /**
     * Ueberprueft, ob die aktuelle techn. Leistung vom Typ <code>typ</code> ist
     *
     * @param typ zu pruefender Typ
     * @return true wenn die aktuelle Leistung vom Typ 'typ' ist.
     *
     */
    public boolean isTyp(String typ) {
        return StringUtils.equals(typ, getTyp());
    }

    /**
     * Wenn <code>true</code> wird beim Sync von "einmalige Leistungen" aus Taifun (chargeFrom == chargeTo) das
     * "aktivBis" Datum der techn. Leistung in Hurrican auf null gesetzt (d.h. die Leistung ist bis HurricanEndDate
     * gültig).
     *
     * @return
     */
    public Boolean getAktivBisNullOnSync() {
        return aktivBisNullOnSync;
    }

    public void setAktivBisNullOnSync(Boolean aktivBisNullOnSync) {
        this.aktivBisNullOnSync = aktivBisNullOnSync;
    }

    /**
     * @see de.augustakom.common.model.HistoryModel#getGueltigBis()
     */
    @Override
    public Date getGueltigBis() {
        return gueltigBis;
    }

    /**
     * @see de.augustakom.common.model.HistoryModel#setGueltigBis(java.util.Date)
     */
    @Override
    public void setGueltigBis(Date gueltigBis) {
        this.gueltigBis = gueltigBis;
    }

    /**
     * @see de.augustakom.common.model.HistoryModel#getGueltigVon()
     */
    @Override
    public Date getGueltigVon() {
        return gueltigVon;
    }

    /**
     * @see de.augustakom.common.model.HistoryModel#setGueltigVon(java.util.Date)
     */
    @Override
    public void setGueltigVon(Date gueltigVon) {
        this.gueltigVon = gueltigVon;
    }

    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return Returns the externLeistungNo.
     */
    @Override
    public Long getExternLeistungNo() {
        return this.externLeistungNo;
    }

    /**
     * @param externLeistungNo The externLeistungNo to set.
     */
    public void setExternLeistungNo(Long externLeistungNo) {
        this.externLeistungNo = externLeistungNo;
    }

    /**
     * @return Returns the externMiscNo.
     */
    @Override
    public Long getExternMiscNo() {
        return this.externMiscNo;
    }

    /**
     * @param externMiscNo The externMiscNo to set.
     */
    public void setExternMiscNo(Long externMiscNo) {
        this.externMiscNo = externMiscNo;
    }

    /**
     * @return Returns the longValue.
     */
    public Long getLongValue() {
        return this.longValue;
    }

    /**
     * @param longValue The longValue to set.
     */
    public void setLongValue(Long longValue) {
        this.longValue = longValue;
    }

    @Transient
    public Integer getIntegerValue() {
        return this.longValue == null ? null : Integer.valueOf(this.longValue.intValue());
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the parameter.
     */
    public String getParameter() {
        return this.parameter;
    }

    /**
     * @param parameter The parameter to set.
     */
    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getBaHinweis() {
        return baHinweis;
    }

    public void setBaHinweis(String baHinweis) {
        this.baHinweis = baHinweis;
    }

    public String getProdNameStr() {
        return prodNameStr;
    }

    public void setProdNameStr(String prodNameStr) {
        this.prodNameStr = prodNameStr;
    }

    /**
     * @return Returns the snapshotRel.
     */
    public Boolean getSnapshotRel() {
        return this.snapshotRel;
    }

    /**
     * @param snapshotRel The snapshotRel to set.
     */
    public void setSnapshotRel(Boolean snapshotRel) {
        this.snapshotRel = snapshotRel;
    }

    /**
     * @return Returns the strValue.
     */
    public String getStrValue() {
        return this.strValue;
    }

    /**
     * @param strValue The strValue to set.
     */
    public void setStrValue(String strValue) {
        this.strValue = strValue;
    }

    /**
     * @return Returns the typ.
     */
    public String getTyp() {
        return this.typ;
    }

    /**
     * @param typ The typ to set.
     */
    public void setTyp(String typ) {
        this.typ = typ;
    }

    /**
     * @return Returns the checkQuantity.
     */
    public Boolean getCheckQuantity() {
        return this.checkQuantity;
    }

    /**
     * @param checkQuantity The checkQuantity to set.
     */
    public void setCheckQuantity(Boolean checkQuantity) {
        this.checkQuantity = checkQuantity;
    }

    /**
     * Gibt an, ob ein Bauauftrag, durch den diese Leistung hinzugebucht wird, vom System automatisch verteilt werden
     * darf (=TRUE) oder ueber die Dispo/NP (=FALSE) gehen muss.
     * @return
     */
    public Boolean getPreventAutoDispatch() {
        return preventAutoDispatch;
    }

    public void setPreventAutoDispatch(Boolean preventAutoDispatch) {
        this.preventAutoDispatch = preventAutoDispatch;
    }

    /**
     * Gibt an, ob diese Leistung automatisch abläuft.
     */
    public Boolean getAutoExpire() {
        return autoExpire;
    }

    public void setAutoExpire(Boolean autoExpire) {
        this.autoExpire = autoExpire;
    }

    /**
     * @see de.augustakom.hurrican.model.cc.BADefault#debugModel(org.apache.log4j.Logger)
     */
    @Override
    public void debugModel(Logger logger) {
        if (logger.isDebugEnabled()) {
            logger.debug("Eigenschaften von " + this.getClass().getName());
            logger.debug("   Name       : " + getName());
            logger.debug("   ext Lst No : " + getExternLeistungNo());
            logger.debug("   Long value : " + getLongValue());
            logger.debug("   Str. value : " + getStrValue());
        }
    }

    public enum ExterneLeistung {
        FASTPATH(10001),
        ALWAYS_ON(10003),
        FIXED_IP_WITH_ALWAYS_ON(10004),
        EINWAHLACCOUNT(10007),
        QOS(10050),
        SECOND_ANALOG_PORT(20013), // 2. analog Port bei VoIP
        ISDN_TYP_TK(20003),
        VOIP_MGA(20015),
        VOIP_TK(20016),
        VOIP_PMX(20017),
        EGPORT_ADD(20021), // weiterer Endgeraeteport
        DOWN_16000(10014),
        DOWN_25000(10025),
        DOWN_50000(10026),
        DOWN_100000(10027),
        UP_2000(10028),
        UP_10000(10030),
        UP_20000(10054),
        TP(10300),
        KOMBI_50_10(10226),
        KOMBI_100_40(10227),
        KOMBI_300_50(32000),
        VOIP_IPV6_50(10205),
        VOIP_IPV6_100(10206),
        VOIP_IPV6_300(10211),
        UP_40000(10074),
        DOWN_300000(10066),
        UP_30000(10067);

        public final long leistungNo;

        ExterneLeistung(long leistungNo) {
            this.leistungNo = leistungNo;
        }

        public long getLeistungNo() {
            return leistungNo;
        }
    }

}
