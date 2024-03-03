/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.07.2010
 */
package de.augustakom.hurrican.model.cc.view;

import java.util.*;

import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.model.cc.AbstractCCModel;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.Rangierung.Freigegeben;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;

/**
 * View-Klasse für die Darstellung einer Rangierung mit dessen zugehörigen Equipmentdaten
 */
public class RangierungWithEquipmentView extends AbstractCCModel {

    // Auftrag- und Endstellen-Id
    private Long auftragId = null;
    private Long endstelleId = null;
    private Integer leitungGesamtId = null;

    // Daten der Rangierung
    private Long rangierId = null;
    private PhysikTyp physikTyp = null;
    private Freigegeben freigegeben = null;
    private Date freigabeAb = null;
    private String ontId = null;
    private String bemerkung = null;
    private String hwEqn = null;
    private String bgTyp = null;
    private String rangVerteilerEqIn = null;
    private String rangBuchtEqIn = null;
    private String switchEqIn = null;
    private String leisteEqIn = null;
    private String stiftEqIn = null;
    private String rangVerteilerEqOut = null;
    private String leisteEqOut = null;
    private String stiftEqOut = null;
    private Uebertragungsverfahren uetvEqOut = null;
    private String rangSSTypeEqOut = null;

    // Daten der zugehörigen Rangierung (oder null falls keine vorhanden)
    private Long rangierId2 = null;
    private PhysikTyp physikTyp2 = null;
    private Freigegeben freigegeben2 = null;
    private Date freigabeAb2 = null;
    private String hwEqn2 = null;
    private String bgTyp2 = null;
    private String rangVerteilerEqIn2 = null;
    private String rangBuchtEqIn2 = null;
    private String switchEqIn2 = null;
    private String leisteEqIn2 = null;
    private String stiftEqIn2 = null;
    private String rangVerteilerEqOut2 = null;
    private String leisteEqOut2 = null;
    private String stiftEqOut2 = null;
    private Uebertragungsverfahren uetvEqOut2 = null;
    private String rangSSTypeEqOut2 = null;

    public Long getAuftragId() {
        return auftragId;
    }

    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    public Long getEndstelleId() {
        return endstelleId;
    }

    public void setEndstelleId(Long endstelleId) {
        this.endstelleId = endstelleId;
    }

    public Integer getLeitungGesamtId() {
        return leitungGesamtId;
    }

    public void setLeitungGesamtId(Integer leitungGesamtId) {
        this.leitungGesamtId = leitungGesamtId;
    }

    public Long getRangierId() {
        return rangierId;
    }

    public void setRangierId(Long rangierId) {
        this.rangierId = rangierId;
    }

    public PhysikTyp getPhysikTyp() {
        return physikTyp;
    }

    public void setPhysikTyp(PhysikTyp physikTyp) {
        this.physikTyp = physikTyp;
    }

    public Freigegeben getFreigegeben() {
        return freigegeben;
    }

    public void setFreigegeben(Freigegeben freigegeben) {
        this.freigegeben = freigegeben;
    }

    public Date getFreigabeAb() {
        return freigabeAb;
    }

    public void setFreigabeAb(Date freigabeAb) {
        this.freigabeAb = freigabeAb;
    }

    public Uebertragungsverfahren getUetvEqOut() {
        return uetvEqOut;
    }

    public void setUetvEqOut(Uebertragungsverfahren uetvEqOut) {
        this.uetvEqOut = uetvEqOut;
    }

    public String getRangSSTypeEqOut() {
        return rangSSTypeEqOut;
    }

    public void setRangSSTypeEqOut(String rangSSTypeEqOut) {
        this.rangSSTypeEqOut = rangSSTypeEqOut;
    }

    public String getUetvEqOutExtended() {
        if (uetvEqOut != null) {
            String result = uetvEqOut.name();
            if (rangSSTypeEqOut != null) {
                result += " (" + rangSSTypeEqOut + ")";
            }
            return result;
        }
        return "";
    }

    public String getLeisteEqOut() {
        return leisteEqOut;
    }

    public void setLeisteEqOut(String leisteEqOut) {
        this.leisteEqOut = leisteEqOut;
    }

    public String getStiftEqOut() {
        return stiftEqOut;
    }

    public void setStiftEqOut(String stiftEqOut) {
        this.stiftEqOut = stiftEqOut;
    }

    public String getLeisteStiftEqOut() {
        return StringTools.join(new String[] { leisteEqOut, stiftEqOut }, " - ", true);
    }

    public String getOntId() {
        return ontId;
    }

    public void setOntId(String ontId) {
        this.ontId = ontId;
    }

    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    public String getHwEqn() {
        return hwEqn;
    }

    public void setHwEqn(String hwEqn) {
        this.hwEqn = hwEqn;
    }

    public String getBgTyp() {
        return bgTyp;
    }

    public void setBgTyp(String bgTyp) {
        this.bgTyp = bgTyp;
    }

    public String getLeisteEqIn() {
        return leisteEqOut;
    }

    public void setLeisteEqIn(String leisteEqIn) {
        this.leisteEqIn = leisteEqIn;
    }

    public String getStiftEqIn() {
        return stiftEqIn;
    }

    public void setStiftEqIn(String stiftEqIn) {
        this.stiftEqIn = stiftEqIn;
    }

    public String getLeisteStiftEqIn() {
        return StringTools.join(new String[] { leisteEqIn, stiftEqIn }, " - ", true);
    }

    public String getSwitchEqIn() {
        return switchEqIn;
    }

    public void setSwitchEqIn(String switchEqIn) {
        this.switchEqIn = switchEqIn;
    }

    public String getRangVerteilerEqIn() {
        return rangVerteilerEqIn;
    }

    public void setRangVerteilerEqIn(String rangVerteilerEqIn) {
        this.rangVerteilerEqIn = rangVerteilerEqIn;
    }

    public String getRangBuchtEqIn() {
        return rangBuchtEqIn;
    }

    public void setRangBuchtEqIn(String rangBuchtEqIn) {
        this.rangBuchtEqIn = rangBuchtEqIn;
    }

    public String getRangVerteilerEqOut() {
        return rangVerteilerEqOut;
    }

    public void setRangVerteilerEqOut(String rangVerteilerEqOut) {
        this.rangVerteilerEqOut = rangVerteilerEqOut;
    }

    public Long getRangierId2() {
        return rangierId2;
    }

    public void setRangierId2(Long rangierId2) {
        this.rangierId2 = rangierId2;
    }

    public PhysikTyp getPhysikTyp2() {
        return physikTyp2;
    }

    public void setPhysikTyp2(PhysikTyp physikTyp2) {
        this.physikTyp2 = physikTyp2;
    }

    public Freigegeben getFreigegeben2() {
        return freigegeben2;
    }

    public void setFreigegeben2(Freigegeben freigegeben2) {
        this.freigegeben2 = freigegeben2;
    }

    public Date getFreigabeAb2() {
        return freigabeAb2;
    }

    public void setFreigabeAb2(Date freigabeAb2) {
        this.freigabeAb2 = freigabeAb2;
    }

    public String getLeisteEqOut2() {
        return leisteEqOut2;
    }

    public void setLeisteEqOut2(String leisteEqOut2) {
        this.leisteEqOut2 = leisteEqOut2;
    }

    public String getStiftEqOut2() {
        return stiftEqOut2;
    }

    public void setStiftEqOut2(String stiftEqOut2) {
        this.stiftEqOut2 = stiftEqOut2;
    }

    public String getLeisteStiftEqOut2() {
        return StringTools.join(new String[] { leisteEqOut2, stiftEqOut2 }, " - ", true);
    }

    public String getHwEqn2() {
        return hwEqn2;
    }

    public void setHwEqn2(String hwEqn2) {
        this.hwEqn2 = hwEqn2;
    }

    public String getBgTyp2() {
        return bgTyp2;
    }

    public void setBgTyp2(String bgTyp2) {
        this.bgTyp2 = bgTyp2;
    }

    public String getLeisteEqIn2() {
        return leisteEqOut2;
    }

    public void setLeisteEqIn2(String leisteEqIn2) {
        this.leisteEqIn2 = leisteEqIn2;
    }

    public String getStiftEqIn2() {
        return stiftEqIn;
    }

    public void setStiftEqIn2(String stiftEqIn2) {
        this.stiftEqIn2 = stiftEqIn2;
    }

    public String getLeisteStiftEqIn2() {
        return StringTools.join(new String[] { leisteEqIn2, stiftEqIn2 }, " - ", true);
    }

    public String getSwitchEqIn2() {
        return switchEqIn2;
    }

    public void setSwitchEqIn2(String switchEqIn2) {
        this.switchEqIn2 = switchEqIn2;
    }

    public String getRangVerteilerEqIn2() {
        return rangVerteilerEqIn2;
    }

    public void setRangVerteilerEqIn2(String rangVerteilerEqIn2) {
        this.rangVerteilerEqIn2 = rangVerteilerEqIn2;
    }

    public String getRangBuchtEqIn2() {
        return rangBuchtEqIn2;
    }

    public void setRangBuchtEqIn2(String rangBuchtEqIn2) {
        this.rangBuchtEqIn2 = rangBuchtEqIn2;
    }

    public String getRangVerteilerEqOut2() {
        return rangVerteilerEqOut2;
    }

    public void setRangVerteilerEqOut2(String rangVerteilerEqOut2) {
        this.rangVerteilerEqOut2 = rangVerteilerEqOut2;
    }

    public Uebertragungsverfahren getUetvEqOut2() {
        return uetvEqOut2;
    }

    public void setUetvEqOut2(Uebertragungsverfahren uetvEqOut2) {
        this.uetvEqOut2 = uetvEqOut2;
    }

    public String getRangSSTypeEqOut2() {
        return rangSSTypeEqOut2;
    }

    public void setRangSSTypeEqOut2(String rangSSTypeEqOut2) {
        this.rangSSTypeEqOut2 = rangSSTypeEqOut2;
    }

    public String getUetvEqOutExtended2() {
        if (uetvEqOut2 != null) {
            String result = uetvEqOut2.name();
            if (rangSSTypeEqOut2 != null) {
                result += " (" + rangSSTypeEqOut2 + ")";
            }
            return result;
        }
        return "";
    }
}
