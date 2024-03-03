/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.06.2004 13:55:46
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.model.DebugModel;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.model.TNB;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.model.shared.iface.HvtIdStandortModel;
import de.augustakom.hurrican.model.shared.iface.HwEqnAwareModel;

/**
 * Modell bildet ein Equipment ab.
 *
 *
 */
public class Equipment extends AbstractCCHistoryModel implements HvtIdStandortModel, DebugModel, Cloneable,
        HwEqnAwareModel {

    private static final long serialVersionUID = 3448825797773971889L;

    private static final Logger LOGGER = Logger.getLogger(Equipment.class);

    /**
     * Anzahl der maximal verfuegbaren hoch-bitratigen Stifte auf einer Leiste.
     */
    public static final int MAX_EQUIPMENT_COUNT_4_H = 80;

    /**
     * Anzahl der maximal verfuegbaren hoch-bitratigen Stifte auf einer Leiste fuer einen KVZ:
     */
    public static final int MAX_EQUIPMENT_COUNT_4_H_KVZ = 100;

    /**
     * Anzahl der maximal verfuegbaren nieder-bitratigen Stifte auf einer Leiste.
     */
    public static final int MAX_EQUIPMENT_COUNT_4_N = 100;

    /**
     * Konstante fuer Feld <code>rangSSType</code>.
     */
    public static final String RANG_SS_2N = "2N";
    /**
     * Konstante fuer Feld <code>rangSSType</code>.
     */
    public static final String RANG_SS_2H = "2H";
    /**
     * Konstante fuer Feld <code>rangSSType</code>.
     */
    public static final String RANG_SS_4H = "4H";
    /**
     * Konstante fuer Feld <code>rangSSType</code>: zweite DA eines 4H-Ports
     */
    public static final String RANG_SS_4H_2DA = "4H-2DA";

    /**
     * Regular-Expression Konstante fuer Feld <code>rangSSType</code> fuer die Kennzeichnung von FttX-Equipments.
     */
    public static final String RANG_SS_FTTX_REG_EXP = "FTT[BH]";
    /**
     * Prefix der Leistenbezeichnung fuer Zwischenverteiler-Leisten
     */
    public static final String RANG_LEISTE_PREFIX_LZV = "LZV";
    /**
     * Konstante fuer <code>RANG_LEISTE_1</code> um die PDH Leiste 'P02' zu kennzeichnen.
     */
    public static final String RANG_LEISTE_P02 = "P02";
    /**
     * KKonstante fuer Feld <code>rangSSType</code> fuer die Kennzeichnung von ADSL-In-Equipments.
     */
    public static final String RANG_SS_ADSL_IN = "ADSL-IN";
    /**
     * Wildcard-Konstante fuer Feld <code>rangSSType</code> fuer die Kennzeichnung von In-Equipments.
     */
    public static final String RANG_SS_IN_WILDCARD = "*-IN";
    /**
     * Wildcard-Konstante fuer Feld <code>rangSSType</code> fuer die Kennzeichnung von Out-Equipments.
     */
    public static final String RANG_SS_OUT_WILDCARD = "*-OUT";

    /**
     * Definition, dass es sich bei dem Port um einen SDSL-Port handelt.
     */
    public static final String HW_SCHNITTSTELLE_SDSL_OUT = "SDSL-OUT";
    /**
     * Definition, dass es sich bei dem Port um einen ADSL-Port handelt.
     */
    public static final String HW_SCHNITTSTELLE_ADSL_OUT = "ADSL-OUT";
    /**
     * Definition, dass es sich bei dem Port um einen duplizierten ADSL-Port handelt.
     */
    public static final String HW_SCHNITTSTELLE_ADSL_IN = "ADSL-IN";
    /**
     * Definition, dass es sich bei dem Port um einen AB-Port handelt.
     */
    public static final String HW_SCHNITTSTELLE_AB = "AB";
    /**
     * Definition, dass es sich bei dem Port um einen UK0-Port handelt.
     */
    public static final String HW_SCHNITTSTELLE_UK0 = "UK0";
    /**
     * Definition, dass es sich bei dem Port um einen AB-Port fuer ADSL-Kombination handelt.
     */
    public static final String HW_SCHNITTSTELLE_ADSL_AB = "ADSL-AB";
    /**
     * Definition, dass es sich bei dem Port um einen UK0-Port fuer ADSL-Kombination handelt.
     */
    public static final String HW_SCHNITTSTELLE_ADSL_UK0 = "ADSL-UK0";
    /**
     * Definition, dass es sich bei dem Port um einen MVO-Port handelt (Mnet-vor-Ort).
     */
    public static final String HW_SCHNITTSTELLE_MVO = "MVO";
    /**
     * Definition, dass es sich bei dem Port um einen V5.2-Port handelt.
     */
    public static final String HW_SCHNITTSTELLE_V52 = "V52";
    /**
     * Definition, dass es sich bei dem Port um einen PDH-Eingangs-Port handelt.
     */
    public static final String HW_SCHNITTSTELLE_PDH_IN = "PDH-IN";
    /**
     * Definition, dass es sich bei dem Port um einen PDH-Ausgangs-Port handelt.
     */
    public static final String HW_SCHNITTSTELLE_PDH_OUT = "PDH-OUT";
    /**
     * Definition, dass es sich bei dem Port um einen SDH-Port handelt.
     */
    public static final String HW_SCHNITTSTELLE_SDH = "SDH";
    /**
     * Definition, dass es sich bei dem Port um einen VDSL-Port handelt.
     */
    public static final String HW_SCHNITTSTELLE_VDSL2 = "VDSL2";

    /**
     * Trennzeichen fuer HW-EQN zwischen Einbauplatz, Slot und Port.
     */
    public static final String HW_EQN_SEPARATOR = "-";

    /**
     * Ermittelt aus HW_EQN den EWSD-Port (= letzte zwei Stellen von HW_EQN)
     */
    public static final int HWEQNPART_EWSD_PORT = 0;
    /**
     * Ermittelt aus HW_EQN den DSLAM Einbauplatz (= erste 5 Zeichen von HW-EQN) Gueltig fuer DSLAMs der Hersteller
     * Siemens / Huawei am Standort AGB
     */
    public static final int HWEQNPART_DSLAM_EINBAU = 1;
    /**
     * Ermittelt aus HW_EQN den DSLAM Slot und Port (= Zeichen ab Index 6). Gueltig fuer DSLAMs der Hersteller Siemens /
     * Huawei am Standort AGB
     */
    public static final int HWEQNPART_DSLAM_SLOT_AND_PORT = 2;
    /**
     * Ermittelt aus HW_EQN den DSLAM Slot (= erster Split von DSLAM Slot-and-Port). Gueltig fuer DSLAMs der Hersteller
     * Siemens / Huawei am Standort AGB
     */
    public static final int HWEQNPART_DSLAM_SLOT = 3;
    /**
     * Ermittelt aus HW_EQN den DSLAM Port (= zweiter Split von DSLAM Slot-and-Port). Gueltig fuer DSLAMs der Hersteller
     * Siemens / Huawei am Standort AGB
     */
    public static final int HWEQNPART_DSLAM_PORT = 4;
    /**
     * Ermittelt aus HW_EQN das OLT Frame (=erster Split von HW_EQN)
     */
    public static final int HWEQNPART_FTTX_OLT_FRAME = 5;
    /**
     * Ermittelt aus HW_EQN den OLT SLOT/SHELF (=dritter bzw. zweiter Split von HW_EQN)
     */
    public static final int HWEQNPART_FTTX_OLT_GPON_SLOT = 7;
    /**
     * Ermittelt aus HW_EQN die OLT GPonID (=letzter Split von HW_EQN)
     */
    public static final int HWEQNPART_FTTX_OLT_GPON_ID = 8;
    /**
     * Ermittelt aus HW_EQN den MDU Port (=fuenfter Split von HW_EQN)
     */
    public static final int HWEQNPART_FTTX_MDU_PORT = 9;
    /**
     * Ermittelt aus HW_EQN den OLT GPonPort (=vierter bzw. dritter Split von HW_EQN)
     */
    public static final int HWEQNPART_FTTX_OLT_GPON_PORT = 15;
    /**
     * Ermittelt aus HW_EQN das OLT Subrack (Alcatel) (=zweiter Split von HW_EQN, wenn HW_EQN mindestens 5 Splits gross
     * ist)
     */
    public static final int HWEQNPART_FTTX_OLT_SUBRACK = 16;
    /**
     * Die Schnittstelle des ONT Ports 1 = ETH, 2 = POTS, 3 = RF
     */
    public static final int HWEQNPART_FTTX_ONT_PORT_SS = 17;
    /**
     * Die Portnummer von 1 - PortCount
     */
    public static final int HWEQNPART_FTTX_ONT_PORT_NUMBER = 18;

    /**
     * Ermittelt aus HW_EQN den DSLAM Einbauplatz Gueltig fuer DSLAMs des Herstellers Alcatel am Standort MUC
     */
    public static final int HWEQNPART_DSLAM_EINBAU_ALCATEL = 10;
    /**
     * Ermittelt aus HW_EQN den DSLAM Slot Gueltig fuer DSLAMs des Herstellers Alcatel am Standort MUC
     */
    public static final int HWEQNPART_DSLAM_SLOT_ALCATEL = 11;
    /**
     * Ermittelt aus HW_EQN den DSLAM Port Gueltig fuer DSLAMs des Herstellers Alcatel am Standort MUC
     */
    public static final int HWEQNPART_DSLAM_PORT_ALCATEL = 12;
    /**
     * Ermittelt aus HW_EQN das DSLAM RACK Gueltig fuer DSLAMs des Herstellers Alcatel am Standort MUC
     */
    public static final int HWEQNPART_DSLAM_RACK_ALCATEL = 13;
    /**
     * Ermittelt aus HW_EQN das DSLAM Shelf Gueltig fuer DSLAMs des Herstellers Alcatel am Standort MUC
     */
    public static final int HWEQNPART_DSLAM_SHELF_ALCATEL = 14;

    public static final String HW_EQN_PART_SEPARATOR = "-";
    public static final String HW_EQN_PATTERN_XDSL_AGB = "[UÜ][0-9X]{2}[-/][0-9X]-\\d+-\\d+";
    public static final String HW_EQN_PATTERN_XDSL_MUC = "\\d+-\\d+-\\d+-\\d+";
    public static final String HW_EQN_PATTERN_EWSD_DLU = "\\d+-\\d+-\\d+-\\d+";
    public static final String HW_EQN_PATTERN_SDH_PDH = "\\d+-\\d+";

    private Long hvtIdStandort = null;
    public static final String HVT_ID_STANDORT = "hvtIdStandort";
    private HWSwitch hwSwitch = null;
    private String hwEQN = null;
    public static final String HW_EQN = "hwEQN";
    private String hwSchnittstelle = null;
    public static final String HW_SCHNITTSTELLE = "hwSchnittstelle";
    private String ts1 = null;
    private String ts2 = null;
    private String kvzNummer = null;
    public static final String KVZ_NUMMER = "kvzNummer";
    private String kvzDoppelader = null;
    public static final String KVZ_DOPPELADER = "kvzDoppelader";
    private String rangVerteiler = null;
    public static final String RANG_VERTEILER = "rangVerteiler";
    private String rangReihe = null;
    private String rangBucht = null;
    public static final String RANG_BUCHT = "rangBucht";
    private String rangLeiste1 = null;
    public static final String RANG_LEISTE1 = "rangLeiste1";
    private String rangStift1 = null;
    public static final String RANG_STIFT1 = "rangStift1";
    private String rangLeiste2 = null;
    private String rangStift2 = null;
    private RangSchnittstelle rangSchnittstelle = null;
    public static final String RANG_SCHNITTSTELLE = "rangSchnittstelle";
    private String carrier = null;
    public static final String CARRIER = "carrier";
    private EqStatus status = null;
    public static final String STATUS = "status";
    private String bemerkung = null;
    private String userW = null;
    private Date dateW = null;
    private String rangSSType = null;
    public static final String RANG_SS_TYPE = "rangSSType";
    private Uebertragungsverfahren uetv = null;
    public static final String UETV = "uetv";
    private Long hwBaugruppenId = null;
    public static final String HW_BAUGRUPPEN_ID = "hwBaugruppenId";
    private Integer cvlan = null;
    private Boolean manualConfiguration = null;
    private String v5Port = null;
    private EqVerwendung verwendung;
    public static final String VERWENDUNG = "verwendung";
    private Schicht2Protokoll schicht2Protokoll;
    private Integer uevtClusterNo = null;

    /**
     * Gibt den von DTAG erwarteten Wert fuer einen Port zurueck. Ist die Port-Angabe "100", wird "00" zurueck gegeben,
     * ansonsten der uebergebene Port.
     *
     * @param port
     * @return
     */
    public static String getDtagValue4Port(String port) {
        return ("100".equals(port)) ? "00" : port;
    }

    /**
     * Ueberprueft, ob es sich bei dem Port um einen zweiten ADSL-Port handelt. Dies ist dann der Fall, wenn die
     * HW_SCHNITTSTELLE als 'ADSL-IN' markiert ist.
     *
     * @return
     */
    public boolean isSecondDslPort() {
        return StringUtils.equals(getHwSchnittstelle(), HW_SCHNITTSTELLE_ADSL_IN);
    }

    public boolean isFirstDslPort() {
        return StringUtils.equals(getHwSchnittstelle(), HW_SCHNITTSTELLE_ADSL_OUT);
    }

    /**
     * Prueft, ob die beiden Ports "zusammen passen". Dies ist dann der Fall, wenn sie die gleiche Baugruppe und HW_EQN
     * besitzen und die HW_SCHNITTSTELLE unterschiedlich ist.
     *
     * @param toMatch
     * @return
     */
    public boolean isMatchingAdslPort(Equipment toMatch) {
        return NumberTools.equal(getHwBaugruppenId(), toMatch.getHwBaugruppenId())
                && StringUtils.equals(getHwEQN(), toMatch.getHwEQN())
                && !StringUtils.equals(getHwSchnittstelle(), toMatch.getHwSchnittstelle());
    }

    public Equipment getMatchingAdslPort(List<Equipment> equipments) {
        if (CollectionTools.isNotEmpty(equipments)) {
            Equipment matchingEq = null;
            for (Equipment equipment : equipments) {
                if (isMatchingAdslPort(equipment)) {
                    matchingEq = equipment;
                    break;
                }
            }
            return matchingEq;
        }
        return null;
    }

    /**
     * Gibt an, ob es sich bei dem zugeordneten Uebertragungsverfahren um ein hochbit-ratiges Uebertragungsverfahren
     * handelt.
     *
     * @return
     */
    public boolean isUetvHochbit() {
        return (uetv != null) && uetv.isHochbit();
    }

    /**
     * Ueberprueft, ob der Port von AKom verwaltet wird.
     *
     * @return true, wenn der Port von AKom verwaltet wird.
     *
     */
    public boolean isManagedByMNet() {
        return (StringUtils.equalsIgnoreCase(getCarrier(), TNB.AKOM.carrierName) ||
                StringUtils.equalsIgnoreCase(getCarrier(), TNB.MNET.carrierName) ||
                StringUtils.equalsIgnoreCase(getCarrier(), TNB.NEFKOM.carrierName));
    }

    /**
     * Ueberprueft, ob der Port von DTAG verwaltet wird.
     *
     * @return true, wenn der Port von DTAG verwaltet wird.
     */
    public boolean isManagedByDtag() {
        return StringUtils.equalsIgnoreCase(getCarrier(), Carrier.CARRIER_DTAG);
    }

    public boolean isPortForHvtTal() {
        return (isManagedByDtag() && (isKupferPort()) && !isPortForKvzTal());
    }

    public boolean isPortForKvzTal() {
        return isKupferPort() && StringUtils.isNotBlank(getKvzNummer());
    }

    public boolean isKupferPort() {
        return RangSchnittstelle.N.equals(getRangSchnittstelle())
                || RangSchnittstelle.H.equals(getRangSchnittstelle());
    }

    /**
     * Prueft, ob es sich bei dem Equipment um einen POTS Port handelt.
     * @return
     */
    public boolean isPotsPort() {
        return HWBaugruppenTyp.HW_SCHNITTSTELLE_POTS.equals(getHwSchnittstelle());
    }

    /**
     * Joined die Werte 'rangVerteiler', 'rangLeiste1' und 'rangStift1'
     *
     * @return
     */
    public String getVerteilerLeisteStift() {
        return StringTools.join(new String[] { getRangVerteiler(), getRangLeiste1(), getRangStift1() }, " ", true);
    }

    /**
     * Joined die Werte 'rangVerteiler', 'rangLeiste1' und 'rangStift1'
     *
     * @return
     */
    public String getDtagVerteilerLeisteStift() {
        String stift1 = Equipment.getDtagValue4Port(getRangStift1());
        return StringTools.join(new String[] { getRangVerteiler(), getRangLeiste1(), stift1 }, " ", true);
    }

    /**
     * Ermittelt einen bestimmten Wert aus dem Parameter <code>hwEQN</code>.
     *
     * @param requestHwEQNPart Angabe des gewuenschten Werts
     * @return
     */
    public String getHwEQNPart(int requestHwEQNPart) {
        if (StringUtils.isBlank(getHwEQN())) {
            return null;
        }
        switch (requestHwEQNPart) {
            case HWEQNPART_EWSD_PORT:
                // EWSD-Port == letzte zwei Stellen von HW-EQN
                return StringUtils.right(getHwEQN(), 2);
            case HWEQNPART_DSLAM_EINBAU:
                return StringUtils.substring(getHwEQN(), 0, 5);
            case HWEQNPART_DSLAM_SLOT_AND_PORT:
                // Slot und Port von DSLAM == Zeichen ab Index 6
                return StringUtils.substring(getHwEQN(), 6);
            case HWEQNPART_DSLAM_SLOT:
                // Slot vom DSLAM (erster Split von DSLAM Slot-and-Port)
                String slotAndPort4Slot = getHwEQNPart(HWEQNPART_DSLAM_SLOT_AND_PORT);
                String[] split4Slot = StringUtils.split(slotAndPort4Slot, HW_EQN_SEPARATOR, 2);
                if ((split4Slot != null) && (split4Slot.length == 2)) {
                    return StringUtils.trimToEmpty(split4Slot[0]);
                }
                return null;
            case HWEQNPART_DSLAM_PORT:
                // Port vom DSLAM (zweiter Split von DSLAM Slot-and-Port)
                String slotAndPort4Port = getHwEQNPart(HWEQNPART_DSLAM_SLOT_AND_PORT);
                String[] split4Port = StringUtils.split(slotAndPort4Port, HW_EQN_SEPARATOR, 2);
                if ((split4Port != null) && (split4Port.length == 2)) {
                    return StringUtils.trimToEmpty(split4Port[1]);
                }
                return null;
            case HWEQNPART_FTTX_OLT_FRAME:
                // OLT Frame ermitteln (erster Split von HW_EQN)
                String[] split4OltFrame = StringUtils.split(getHwEQN(), HW_EQN_SEPARATOR);
                return ((split4OltFrame != null) && (split4OltFrame.length > 0)) ? split4OltFrame[0] : null;
            case HWEQNPART_FTTX_OLT_SUBRACK:
                // OLT Subrack ermitteln (zweiter Split von HW_EQN, nur wenn 5-stellig d.h. Alcatel)
                String[] split4OltSubrack = StringUtils.split(getHwEQN(), HW_EQN_SEPARATOR);
                return ((split4OltSubrack != null) && (split4OltSubrack.length >= 5)) ? split4OltSubrack[1] : null;
            case HWEQNPART_FTTX_OLT_GPON_SLOT:
                // OLT SLOT/SHELF ermitteln (3 Split von 5 des HW_EQN für Alcatel, 2 Split von 4 für Huawei)
                // Funktioniert fuer Alcatel sowie fuer Huawei
                String[] split4OltSlot = StringUtils.split(getHwEQN(), HW_EQN_SEPARATOR);
                return ((split4OltSlot != null) && (split4OltSlot.length >= 4)) ? split4OltSlot[split4OltSlot.length - 3]
                        : null;
            case HWEQNPART_FTTX_OLT_GPON_PORT:
                // OLT GPonPort ermitteln (4 Split von 5 des HW_EQN für Alcatel, 3 Split von 4 für Huawei)
                // Funktioniert fuer Alcatel sowie fuer Huawei
                String[] split4OltGPPort = StringUtils.split(getHwEQN(), HW_EQN_SEPARATOR);
                return ((split4OltGPPort != null) && (split4OltGPPort.length >= 4))
                        ? split4OltGPPort[split4OltGPPort.length - 2] : null;
            case HWEQNPART_FTTX_OLT_GPON_ID:
                // OLT GPonID ermitteln (letzter Split von HW_EQN)
                String[] split4OltGPID = StringUtils.split(getHwEQN(), HW_EQN_SEPARATOR);
                return ((split4OltGPID != null) && (split4OltGPID.length >= 4))
                        ? split4OltGPID[split4OltGPID.length - 1] : null;
            case HWEQNPART_FTTX_ONT_PORT_SS:
                // ONT Port Schnittstelle
                String[] split4OntPortSS = StringUtils.split(getHwEQN(), HW_EQN_SEPARATOR);
                return ((split4OntPortSS != null) && (split4OntPortSS.length > 0)) ? split4OntPortSS[0] : null;
            case HWEQNPART_FTTX_ONT_PORT_NUMBER:
                // ONT Port Nummer
                String[] split4OntPortNo = StringUtils.split(getHwEQN(), HW_EQN_SEPARATOR);
                return ((split4OntPortNo != null) && (split4OntPortNo.length > 1)) ? split4OntPortNo[1] : null;
            case HWEQNPART_FTTX_MDU_PORT:
                // MDU Port ermitteln == gesamter EQN-String
                return getHwEQN();
            case HWEQNPART_DSLAM_EINBAU_ALCATEL:
                // Einbauplatz von Alcatel DSLAM Ports ermitteln: erste 3 Zeichen
                return StringUtils.left(getHwEQN(), 3);
            case HWEQNPART_DSLAM_SLOT_ALCATEL:
                // Slot vom Alcatel DSLAM ermitteln: 3 Split von HW_EQN
                String[] split4SlotAlcatel = StringUtils.split(getHwEQN(), HW_EQN_SEPARATOR);
                return ((split4SlotAlcatel != null) && (split4SlotAlcatel.length > 2)) ? split4SlotAlcatel[2] : null;
            case HWEQNPART_DSLAM_PORT_ALCATEL:
                // Port vom Alcatel DSLAM ermitteln: 4 Split von HW_EQN
                String[] split4PortAlcatel = StringUtils.split(getHwEQN(), HW_EQN_SEPARATOR);
                return ((split4PortAlcatel != null) && (split4PortAlcatel.length > 3)) ? split4PortAlcatel[3] : null;
            case HWEQNPART_DSLAM_RACK_ALCATEL:
                // Rack vom Alcatel DSLAM ermitteln: 1 Split von HW_EQN
                String[] split4RackAlcatel = StringUtils.split(getHwEQN(), HW_EQN_SEPARATOR);
                return ((split4RackAlcatel != null) && (split4RackAlcatel.length > 0)) ? split4RackAlcatel[0] : null;
            case HWEQNPART_DSLAM_SHELF_ALCATEL:
                // Port vom Alcatel DSLAM ermitteln: 2 Split von HW_EQN
                String[] split4ShelfAlcatel = StringUtils.split(getHwEQN(), HW_EQN_SEPARATOR);
                return ((split4ShelfAlcatel != null) && (split4ShelfAlcatel.length > 1)) ? split4ShelfAlcatel[1] : null;
            default:
                return HurricanConstants.UNKNOWN;
        }
    }

    public String getVerteilerpunktString() {
        return getRangVerteiler() + " / " + getRangLeiste1() + " / " + getRangStift1();
    }

    /**
     * @param requestHwEQNPart
     * @return int-Value zu dem angeforderten HW-EQN Part oder -1, falls der Wert keinem int-Value entspricht
     * @see String getHwEQNPart(int) Ermittelter Wert ueber getHwEQNPart(int) wird in einen int-Value umgewandelt.
     */
    public int getHwEQNPartAsInt(int requestHwEQNPart) {
        String hwEQNPart = getHwEQNPart(requestHwEQNPart);
        try {
            return Integer.parseInt(hwEQNPart);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return -1;
    }

    /**
     * @return
     * @deprecated use getHwEQNPart(int) Ermittelt den DSLAM-Slot aus der HW-EQN. <br> Als Slot/Port werden die Zeichen
     * ab Index 6 von HW-EQN verwendet.
     */
    @Deprecated
    public String getSlotAndPort() {
        return getHwEQNPart(HWEQNPART_DSLAM_SLOT_AND_PORT);
    }

    /**
     * @return
     * @deprecated Verwendung fuer FttX-Prototyp (wird entfernt, wenn Prototyp umgestellt ist) Ermittelt den DSLAM-Slot
     * aus der HW-EQN. <br> Als Slot/Port werden die letzten 6 Zeichen von HW-EQN verwendet.
     */
    @Deprecated
    public String getSlotAndPort4MUC() {
        if (getHwEQN() != null) {
            int i = (getHwEQN()).length();
            return StringUtils.substring(getHwEQN(), i - 6);
        }
        return null;
    }

    /**
     * @param niederlassungId ID der Niederlassung, die fuer den HVT verantwortlich ist
     * @return Slot-Angabe als String
     *
     * @deprecated Verwendung fuer FttX-Prototyp (wird entfernt, wenn Prototyp umgestellt ist) Ermittelt den DSLAM-Slot
     * aus HW-EQN. Liefert Ergebnis als String
     */
    @Deprecated
    public String getSlotAsString(Long niederlassungId) {
        if (Niederlassung.ID_MUENCHEN.equals(niederlassungId)) {
            String slotAndPort = getSlotAndPort4MUC();
            if (StringUtils.isNotBlank(slotAndPort)) {
                String[] split = StringUtils.split(slotAndPort, HW_EQN_SEPARATOR, 2);
                if ((split != null) && (split.length == 2)) {
                    try {
                        return StringUtils.trimToEmpty(split[0]);
                    }
                    catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
            }
        }
        else {
            return getHwEQNPart(HWEQNPART_DSLAM_SLOT);
        }

        return null;
    }

    /**
     * @param niederlassungId ID der Niederlassung, die fuer den HVT verantwortlich ist
     * @return Port-Angabe als String.
     *
     * @deprecated Verwendung fuer FttX-Prototyp (wird entfernt, wenn Prototyp umgestellt ist) Ermittelt den DSLAM-Port
     * aus HW-EQN. Liefert Ergebnis als String
     */
    @Deprecated
    public String getPortAsString(Long niederlassungId) {
        if (Niederlassung.ID_MUENCHEN.equals(niederlassungId)) {
            String slotAndPort = getSlotAndPort4MUC();
            if (StringUtils.isNotBlank(slotAndPort)) {
                String[] split = StringUtils.split(slotAndPort, HW_EQN_SEPARATOR, 2);
                if ((split != null) && (split.length == 2)) {
                    try {
                        return StringUtils.trimToEmpty(split[1]);
                    }
                    catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
            }
        }
        else {
            return getHwEQNPart(HWEQNPART_DSLAM_PORT);
        }

        return null;
    }

    /**
     * Gibt eine Kombination der Strings Bucht+Leiste1+Stift1 zurueck.
     *
     * @return String aus Bucht, Leiste1 und Stift1 - getrennt durch Leerzeichen
     *
     */
    public String getEinbau1() {
        return StringUtils.join(new Object[] { getRangBucht(), getRangLeiste1(), getRangStift1() }, "  ");
    }

    /**
     * Gibt eine Kombination der Strings Bucht+Leiste1+Stift1 fuer DTAG zurueck. Ist der Wert von 'rangStift1'=100, wird
     * '00' verwendet.
     *
     * @return
     *
     */
    public String getEinbau1DTAG() {
        String schaltUevt1 = StringUtils.join(new Object[] { getRangBucht(), getRangLeiste1() }, "");
        schaltUevt1 += Equipment.getDtagValue4Port(getRangStift1());
        return schaltUevt1;
    }

    /**
     * Gibt eine Kombination der Strings Leiste2+Stift2 zurueck.
     *
     * @return String aus Leiste2 und Stift2 - getrennt durch Leerzeichen
     *
     */
    public String getEinbau2() {
        return StringUtils.join(new Object[] { getRangLeiste2(), getRangStift2() }, "  ");
    }

    /**
     * Gibt eine Kombination der Strings Bucht+Leiste2+Stift2 zurueck. Ist der Wert von 'rangStift1'=100, wird '00'
     * verwendet.
     *
     * @return
     *
     */
    public String getEinbau2DTAG() {
        String schaltUevt2 = StringUtils.join(new Object[] { getRangBucht(), getRangLeiste2() }, "");
        schaltUevt2 += Equipment.getDtagValue4Port(getRangStift2());
        return schaltUevt2;
    }

    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public Date getDateW() {
        return dateW;
    }

    public void setDateW(Date dateW) {
        this.dateW = dateW;
    }

    @Override
    public Long getHvtIdStandort() {
        return hvtIdStandort;
    }

    public void setHvtIdStandort(Long hvtIdStandort) {
        this.hvtIdStandort = hvtIdStandort;
    }

    /**
     * Aufbau der HW_EQN <ul> <li>Phone: dlu-shelf-module-port (z.B. '0020-01-04-01') <li>DSLAM: einbauplatz-slot-port
     * (z.B. 'U05-3-014-07' oder 'U05/3-014-07') <li>FTTB: MDUFrame-MDUShelf-MDUPort (z.B. '0-1-4') <li>FTTH:
     * frame-shelf-GPonPort-GPonID (z.B. '03-01-09-61') <li>SDH/PDH: karte-port (z.B. '123-01') </ul>
     *
     * @return Returns the hwEQN.
     */
    @Override
    public String getHwEQN() {
        return hwEQN;
    }

    public void setHwEQN(String hwEQN) {
        this.hwEQN = hwEQN;
    }

    public String getHwSchnittstelle() {
        return hwSchnittstelle;
    }

    public void setHwSchnittstelle(String hwSchnittstelle) {
        this.hwSchnittstelle = hwSchnittstelle;
    }

    public String getKvzNummer() {
        return kvzNummer;
    }

    public void setKvzNummer(String kvzNummer) {
        this.kvzNummer = kvzNummer;
    }

    public String getKvzDoppelader() {
        return kvzDoppelader;
    }

    public void setKvzDoppelader(String kvzDoppelader) {
        this.kvzDoppelader = kvzDoppelader;
    }

    public String getRangBucht() {
        return rangBucht;
    }

    public void setRangBucht(String rangBucht) {
        this.rangBucht = rangBucht;
    }

    public String getRangLeiste1() {
        return rangLeiste1;
    }

    public void setRangLeiste1(String rangLeiste1) {
        this.rangLeiste1 = rangLeiste1;
    }

    public String getRangLeiste2() {
        return rangLeiste2;
    }

    public void setRangLeiste2(String rangLeiste2) {
        this.rangLeiste2 = rangLeiste2;
    }

    public String getRangReihe() {
        return rangReihe;
    }

    public void setRangReihe(String rangReihe) {
        this.rangReihe = rangReihe;
    }

    public RangSchnittstelle getRangSchnittstelle() {
        return rangSchnittstelle;
    }

    public void setRangSchnittstelle(RangSchnittstelle rangSchnittstelle) {
        this.rangSchnittstelle = rangSchnittstelle;
    }

    public String getRangSSType() {
        return rangSSType;
    }

    public void setRangSSType(String rangSSType) {
        this.rangSSType = rangSSType;
    }

    public String getRangStift1() {
        return rangStift1;
    }

    public void setRangStift1(String rangStift1) {
        this.rangStift1 = rangStift1;
    }

    public String getRangStift2() {
        return rangStift2;
    }

    public void setRangStift2(String rangStift2) {
        this.rangStift2 = rangStift2;
    }

    public String getRangVerteiler() {
        return rangVerteiler;
    }

    public void setRangVerteiler(String rangVerteiler) {
        this.rangVerteiler = rangVerteiler;
    }

    public EqStatus getStatus() {
        return status;
    }

    public void setStatus(EqStatus status) {
        this.status = status;
    }

    public HWSwitch getHwSwitch() {
        return hwSwitch;
    }

    public void setHwSwitch(HWSwitch hwSwitch) {
        this.hwSwitch = hwSwitch;
    }

    public String getTs1() {
        return ts1;
    }

    public void setTs1(String ts1) {
        this.ts1 = ts1;
    }

    public String getTs2() {
        return ts2;
    }

    public void setTs2(String ts2) {
        this.ts2 = ts2;
    }

    /**
     * Gibt das Uebertragungsverfahren (z.B. 'H04') der Leitung an.
     */
    public Uebertragungsverfahren getUetv() {
        return uetv;
    }

    public void setUetv(Uebertragungsverfahren uetv) {
        this.uetv = uetv;
    }

    public Long getHwBaugruppenId() {
        return hwBaugruppenId;
    }

    public void setHwBaugruppenId(Long hwBaugruppenId) {
        this.hwBaugruppenId = hwBaugruppenId;
    }

    public String getUserW() {
        return userW;
    }

    public void setUserW(String userW) {
        this.userW = userW;
    }

    public Integer getCvlan() {
        return cvlan;
    }

    /**
     * Defines the CVLAN (or VC) value
     */
    public void setCvlan(Integer cvlan) {
        this.cvlan = cvlan;
    }

    public Boolean getManualConfiguration() {
        return manualConfiguration;
    }

    /**
     * Set to <code>true</code> if the port has a manual configuration of the cross connections.
     */
    public void setManualConfiguration(Boolean manualConfiguration) {
        this.manualConfiguration = manualConfiguration;
    }

    public String getV5Port() {
        return v5Port;
    }

    public void setV5Port(String v5Port) {
        this.v5Port = v5Port;
    }

    public EqVerwendung getVerwendung() {
        return verwendung;
    }

    public void setVerwendung(EqVerwendung verwendung) {
        this.verwendung = verwendung;
    }

    public Schicht2Protokoll getSchicht2Protokoll() {
        return schicht2Protokoll;
    }

    public void setSchicht2Protokoll(Schicht2Protokoll schicht2Protokoll) {
        this.schicht2Protokoll = schicht2Protokoll;
    }

    /**
     * Liefert das Schicht2Protokoll des Equipments oder ATM falls kein Protokoll gesetzt ist.
     */
    public Schicht2Protokoll getSchicht2ProtokollOrAtm() {
        return (schicht2Protokoll == null) ? Schicht2Protokoll.ATM : schicht2Protokoll;
    }

    public Integer getUevtClusterNo() {
        return uevtClusterNo;
    }

    public void setUevtClusterNo(Integer uevtClusterNo) {
        this.uevtClusterNo = uevtClusterNo;
    }

    /**
     * @see de.augustakom.common.model.DebugModel#debugModel(org.apache.log4j.Logger)
     */
    @Override
    public void debugModel(Logger logger) {
        if (!logger.isDebugEnabled()) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("-------------------------------------------------\n");
        sb.append("  ID       : ").append(getId()).append("\n");
        sb.append("  Verteiler: ").append(getRangVerteiler()).append("\n");
        sb.append("  HVT-ID   : ").append(getHvtIdStandort()).append("\n");
        sb.append("  Leiste 1 : ").append(getRangLeiste1()).append("\n");
        sb.append("  Stift 1  : ").append(getRangStift1()).append("\n");
        sb.append("  Leiste 2 : ").append(getRangLeiste2()).append("\n");
        sb.append("  Stift 2  : ").append(getRangStift2()).append("\n");
        sb.append("  SS-Type  : ").append(getRangSSType()).append("\n");
        sb.append("  UETV     : ").append(getUetv()).append("\n");
        sb.append("  Status   : ").append(getStatus());
        logger.debug(sb.toString());
    }

    @Override
    public Equipment clone() throws CloneNotSupportedException {
        Equipment clone = (Equipment) super.clone();
        clone.setId(null);
        return clone;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = (prime * result) + ((bemerkung == null) ? 0 : bemerkung.hashCode());
        result = (prime * result) + ((carrier == null) ? 0 : carrier.hashCode());
        result = (prime * result) + ((cvlan == null) ? 0 : cvlan.hashCode());
        result = (prime * result) + ((dateW == null) ? 0 : dateW.hashCode());
        result = (prime * result) + ((hvtIdStandort == null) ? 0 : hvtIdStandort.hashCode());
        result = (prime * result) + ((hwBaugruppenId == null) ? 0 : hwBaugruppenId.hashCode());
        result = (prime * result) + ((hwEQN == null) ? 0 : hwEQN.hashCode());
        result = (prime * result) + ((hwSchnittstelle == null) ? 0 : hwSchnittstelle.hashCode());
        result = (prime * result) + ((kvzDoppelader == null) ? 0 : kvzDoppelader.hashCode());
        result = (prime * result) + ((kvzNummer == null) ? 0 : kvzNummer.hashCode());
        result = (prime * result) + ((manualConfiguration == null) ? 0 : manualConfiguration.hashCode());
        result = (prime * result) + ((rangBucht == null) ? 0 : rangBucht.hashCode());
        result = (prime * result) + ((rangLeiste1 == null) ? 0 : rangLeiste1.hashCode());
        result = (prime * result) + ((rangLeiste2 == null) ? 0 : rangLeiste2.hashCode());
        result = (prime * result) + ((rangReihe == null) ? 0 : rangReihe.hashCode());
        result = (prime * result) + ((rangSSType == null) ? 0 : rangSSType.hashCode());
        result = (prime * result) + ((rangSchnittstelle == null) ? 0 : rangSchnittstelle.hashCode());
        result = (prime * result) + ((rangStift1 == null) ? 0 : rangStift1.hashCode());
        result = (prime * result) + ((rangStift2 == null) ? 0 : rangStift2.hashCode());
        result = (prime * result) + ((rangVerteiler == null) ? 0 : rangVerteiler.hashCode());
        result = (prime * result) + ((schicht2Protokoll == null) ? 0 : schicht2Protokoll.hashCode());
        result = (prime * result) + ((status == null) ? 0 : status.hashCode());
        result = (prime * result) + ((hwSwitch == null) ? 0 : hwSwitch.hashCode());
        result = (prime * result) + ((ts1 == null) ? 0 : ts1.hashCode());
        result = (prime * result) + ((ts2 == null) ? 0 : ts2.hashCode());
        result = (prime * result) + ((uetv == null) ? 0 : uetv.hashCode());
        result = (prime * result) + ((uevtClusterNo == null) ? 0 : uevtClusterNo.hashCode());
        result = (prime * result) + ((userW == null) ? 0 : userW.hashCode());
        result = (prime * result) + ((v5Port == null) ? 0 : v5Port.hashCode());
        result = (prime * result) + ((verwendung == null) ? 0 : verwendung.hashCode());
        return result;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof Equipment)) {
            return false;
        }
        Equipment other = (Equipment) obj;
        if (bemerkung == null) {
            if (other.bemerkung != null) {
                return false;
            }
        }
        else if (!bemerkung.equals(other.bemerkung)) {
            return false;
        }
        if (carrier == null) {
            if (other.carrier != null) {
                return false;
            }
        }
        else if (!carrier.equals(other.carrier)) {
            return false;
        }
        if (cvlan == null) {
            if (other.cvlan != null) {
                return false;
            }
        }
        else if (!cvlan.equals(other.cvlan)) {
            return false;
        }
        if (dateW == null) {
            if (other.dateW != null) {
                return false;
            }
        }
        else if (!dateW.equals(other.dateW)) {
            return false;
        }
        if (hvtIdStandort == null) {
            if (other.hvtIdStandort != null) {
                return false;
            }
        }
        else if (!hvtIdStandort.equals(other.hvtIdStandort)) {
            return false;
        }
        if (hwBaugruppenId == null) {
            if (other.hwBaugruppenId != null) {
                return false;
            }
        }
        else if (!hwBaugruppenId.equals(other.hwBaugruppenId)) {
            return false;
        }
        if (hwEQN == null) {
            if (other.hwEQN != null) {
                return false;
            }
        }
        else if (!hwEQN.equals(other.hwEQN)) {
            return false;
        }
        if (hwSchnittstelle == null) {
            if (other.hwSchnittstelle != null) {
                return false;
            }
        }
        else if (!hwSchnittstelle.equals(other.hwSchnittstelle)) {
            return false;
        }
        if (kvzDoppelader == null) {
            if (other.kvzDoppelader != null) {
                return false;
            }
        }
        else if (!kvzDoppelader.equals(other.kvzDoppelader)) {
            return false;
        }
        if (kvzNummer == null) {
            if (other.kvzNummer != null) {
                return false;
            }
        }
        else if (!kvzNummer.equals(other.kvzNummer)) {
            return false;
        }
        if (manualConfiguration == null) {
            if (other.manualConfiguration != null) {
                return false;
            }
        }
        else if (!manualConfiguration.equals(other.manualConfiguration)) {
            return false;
        }
        if (rangBucht == null) {
            if (other.rangBucht != null) {
                return false;
            }
        }
        else if (!rangBucht.equals(other.rangBucht)) {
            return false;
        }
        if (rangLeiste1 == null) {
            if (other.rangLeiste1 != null) {
                return false;
            }
        }
        else if (!rangLeiste1.equals(other.rangLeiste1)) {
            return false;
        }
        if (rangLeiste2 == null) {
            if (other.rangLeiste2 != null) {
                return false;
            }
        }
        else if (!rangLeiste2.equals(other.rangLeiste2)) {
            return false;
        }
        if (rangReihe == null) {
            if (other.rangReihe != null) {
                return false;
            }
        }
        else if (!rangReihe.equals(other.rangReihe)) {
            return false;
        }
        if (rangSSType == null) {
            if (other.rangSSType != null) {
                return false;
            }
        }
        else if (!rangSSType.equals(other.rangSSType)) {
            return false;
        }
        if (rangSchnittstelle != other.rangSchnittstelle) {
            return false;
        }
        if (rangStift1 == null) {
            if (other.rangStift1 != null) {
                return false;
            }
        }
        else if (!rangStift1.equals(other.rangStift1)) {
            return false;
        }
        if (rangStift2 == null) {
            if (other.rangStift2 != null) {
                return false;
            }
        }
        else if (!rangStift2.equals(other.rangStift2)) {
            return false;
        }
        if (rangVerteiler == null) {
            if (other.rangVerteiler != null) {
                return false;
            }
        }
        else if (!rangVerteiler.equals(other.rangVerteiler)) {
            return false;
        }
        if (schicht2Protokoll != other.schicht2Protokoll) {
            return false;
        }
        if (status != other.status) {
            return false;
        }
        if (hwSwitch == null) {
            if (other.hwSwitch != null) {
                return false;
            }
        }
        else if (!hwSwitch.equals(other.hwSwitch)) {
            return false;
        }
        if (ts1 == null) {
            if (other.ts1 != null) {
                return false;
            }
        }
        else if (!ts1.equals(other.ts1)) {
            return false;
        }
        if (ts2 == null) {
            if (other.ts2 != null) {
                return false;
            }
        }
        else if (!ts2.equals(other.ts2)) {
            return false;
        }
        if (uetv != other.uetv) {
            return false;
        }
        if (uevtClusterNo == null) {
            if (other.uevtClusterNo != null) {
                return false;
            }
        }
        else if (!uevtClusterNo.equals(other.uevtClusterNo)) {
            return false;
        }
        if (userW == null) {
            if (other.userW != null) {
                return false;
            }
        }
        else if (!userW.equals(other.userW)) {
            return false;
        }
        if (v5Port == null) {
            if (other.v5Port != null) {
                return false;
            }
        }
        else if (!v5Port.equals(other.v5Port)) {
            return false;
        }
        return verwendung == other.verwendung;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String
                .format("Equipment [hvtIdStandort=%s, hwSwitch=%s, hwEQN=%s, hwSchnittstelle=%s, ts1=%s, ts2=%s, kvzNummer=%s, kvzDoppelader=%s, rangVerteiler=%s, rangReihe=%s, rangBucht=%s, rangLeiste1=%s, rangStift1=%s, rangLeiste2=%s, rangStift2=%s, rangSchnittstelle=%s, carrier=%s, status=%s, bemerkung=%s, userW=%s, dateW=%s, rangSSType=%s, uetv=%s, hwBaugruppenId=%s, cvlan=%s, manualConfiguration=%s, v5Port=%s, verwendung=%s, schicht2Protokoll=%s, uevtClusterNo=%s]",
                        hvtIdStandort, hwSwitch, hwEQN, hwSchnittstelle, ts1, ts2, kvzNummer, kvzDoppelader,
                        rangVerteiler, rangReihe, rangBucht, rangLeiste1, rangStift1, rangLeiste2, rangStift2,
                        rangSchnittstelle, carrier, status, bemerkung, userW, dateW, rangSSType, uetv, hwBaugruppenId,
                        cvlan, manualConfiguration, v5Port, verwendung, schicht2Protokoll, uevtClusterNo);
    }

}
