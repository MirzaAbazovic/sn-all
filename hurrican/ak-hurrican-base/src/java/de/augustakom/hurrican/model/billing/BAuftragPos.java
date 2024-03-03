/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.06.2004 11:00:35
 */
package de.augustakom.hurrican.model.billing;

import java.io.*;
import java.time.*;
import java.util.*;
import javax.persistence.*;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.base.AbstractTaifunModel;
import de.mnet.common.tools.DateConverterUtils;


/**
 * Bildet eine Auftragsposition aus dem Billing-System ab.
 *
 *
 */

@Entity
@Table(name = "AUFTRAGPOS")
public class BAuftragPos extends AbstractTaifunModel implements Serializable {
    private static final long serialVersionUID = -7863383644443090136L;

    private Long itemNo;
    private Long itemNoOrig;
    private Long auftragNoOrig;
    private Long createAuftragNo;
    private Long terminateAuftragNo;
    private Boolean isPlanned;
    private Long leistungNoOrig;
    private String parameter;
    private Long menge;
    private Date chargeFrom;
    private Date chargeTo;
    private Date chargedUntil;
    private Float preis;
    private Float listenpreis;
    private String freeText;
    private Long deviceNo;
    private Long timestamp;

    public static BAuftragPos createBAuftragPos(LocalDate executionDate) {
        BAuftragPos pos = new BAuftragPos();
        pos.setMenge(1L);
        pos.setChargeTo(null);
        pos.setChargeFrom(DateConverterUtils.asDate(executionDate));
        return pos;
    }


    /**
     * Ueberprueft, ob die Auftragsposition aktiv ist. <br> Dies ist dann der Fall, wenn <code>chargeTo</code>
     * <code>null</code> ist oder den Wert '31.12.9999' besitzt.
     *
     * @return true wenn die Auftragsposition aktiv ist.
     *
     */
    @Transient
    public boolean isActive() {
        boolean active =
                ((getChargeTo() == null) || DateTools.isDateEqual(getChargeTo(), DateTools.getBillingEndDate()));
        return active;
    }

    /**
     * Ueberprueft, ob die Auftragsposition zur aktuellen Zeit aktiv ist. <br> Dies ist dann der Fall, wenn
     * <code>chargeTo</code> <code>null</code> ist oder der Wert >= 'jetzt' ist.
     *
     * @return true wenn die Auftragsposition zur aktuellen Zeit aktiv ist.
     *
     */
    @Transient
    public boolean isCurrentlyActive() {
        boolean currentlyActive =
                ((getChargeTo() == null) || DateTools.isDateAfterOrEqual(getChargeTo(), new Date()));
        return currentlyActive;
    }

    /**
     * Funktion liefert den Preis einer Position.
     *
     * @return Wert der Variable preis, falls diese gesetzt ist, sonst Wert der Variable listenpreis
     *
     */
    @Transient
    public Float getDefinedPrice() {
        return (getPreis() != null) ? getPreis() : getListenpreis();
    }


    /**
     * @return Returns the itemNo.
     */
    @Column(name = "ITEM_NO", insertable = true, updatable = true)
    @Id
    public Long getItemNo() {
        return this.itemNo;
    }

    /**
     * @param itemNo The itemNo to set.
     */
    public void setItemNo(Long itemNo) {
        this.itemNo = itemNo;
    }

    /**
     * @return Returns the itemNoOrig.
     */
    @Column(name = "ITEM__NO")
    public Long getItemNoOrig() {
        return this.itemNoOrig;
    }

    /**
     * @param itemNoOrig The itemNoOrig to set.
     */
    public void setItemNoOrig(Long itemNoOrig) {
        this.itemNoOrig = itemNoOrig;
    }

    /**
     * @return Returns the auftragNoOrig.
     */
    @Column(name = "ORDER__NO", insertable = true, updatable = true)
    public Long getAuftragNoOrig() {
        return auftragNoOrig;
    }

    /**
     * @param auftragNoOrig The auftragNoOrig to set.
     */
    public void setAuftragNoOrig(Long auftragNoOrig) {
        this.auftragNoOrig = auftragNoOrig;
    }

    /**
     * @return Returns the createAuftragNo.
     */
    @Column(name = "CREATE_ORDER_NO")
    public Long getCreateAuftragNo() {
        return this.createAuftragNo;
    }

    /**
     * @param createAuftragNo The createAuftragNo to set.
     */
    public void setCreateAuftragNo(Long createAuftragNo) {
        this.createAuftragNo = createAuftragNo;
    }

    /**
     * @return Returns the isPlanned.
     */
    @Column(name = "IS_PLANNED")
    public Boolean getIsPlanned() {
        return this.isPlanned;
    }

    /**
     * @param isPlanned The isPlanned to set.
     */
    public void setIsPlanned(Boolean isPlanned) {
        this.isPlanned = isPlanned;
    }

    /**
     * @return Returns the terminateAuftragNo.
     */
    @Column(name = "TERMINATE_ORDER_NO")
    public Long getTerminateAuftragNo() {
        return this.terminateAuftragNo;
    }

    /**
     * @param terminateAuftragNo The terminateAuftragNo to set.
     */
    public void setTerminateAuftragNo(Long terminateAuftragNo) {
        this.terminateAuftragNo = terminateAuftragNo;
    }

    /**
     * @return Returns the leistungNoOrig.
     */
    @Column(name = "SERVICE_ELEM__NO")
    public Long getLeistungNoOrig() {
        return leistungNoOrig;
    }

    /**
     * @param leistungNoOrig The leistungNoOrig to set.
     */
    public void setLeistungNoOrig(Long leistungNoOrig) {
        this.leistungNoOrig = leistungNoOrig;
    }

    /**
     * @return Returns the parameter.
     */
    @Column(name = "PARAMETER")
    public String getParameter() {
        return this.parameter;
    }

    /**
     * @param parameter The parameter to set.
     */
    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    /**
     * @return Returns the menge.
     */
    @Column(name = "QUANTITY")
    public Long getMenge() {
        return menge;
    }

    /**
     * @param menge The menge to set.
     */
    public void setMenge(Long menge) {
        this.menge = menge;
    }

    /**
     * @return Returns the chargeTo.
     */
    @Column(name = "CHARGE_TO")
    public Date getChargeTo() {
        return chargeTo;
    }

    /**
     * @param chargeTo The chargeTo to set.
     */
    public void setChargeTo(Date gueltigBis) {
        this.chargeTo = gueltigBis;
    }

    /**
     * @return Returns the chargeFrom.
     */
    @Column(name = "CHARGE_FROM")
    public Date getChargeFrom() {
        return chargeFrom;
    }

    /**
     * @param chargeFrom The chargeFrom to set.
     */
    public void setChargeFrom(Date gueltigVon) {
        this.chargeFrom = gueltigVon;
    }

    /**
     * @return Returns the chargedUntil.
     */
    @Column(name = "CHARGED_UNTIL")
    public Date getChargedUntil() {
        return this.chargedUntil;
    }

    /**
     * @param chargedUntil The chargedUntil to set.
     */
    public void setChargedUntil(Date chargedUntil) {
        this.chargedUntil = chargedUntil;
    }

    /**
     * @return preis
     */
    @Column(name = "PRICE")
    public Float getPreis() {
        return preis;
    }

    /**
     * @param preis Festzulegender preis
     */
    public void setPreis(Float preis) {
        this.preis = preis;
    }

    /**
     * @return listenpreis
     */
    @Column(name = "LIST_PRICE")
    public Float getListenpreis() {
        return listenpreis;
    }

    /**
     * @param listenpreis Festzulegender listenpreis
     */
    public void setListenpreis(Float listenpreis) {
        this.listenpreis = listenpreis;
    }

    @Column(name = "FREE_TEXT")
    public String getFreeText() {
        return freeText;
    }

    public void setFreeText(String freeText) {
        this.freeText = freeText;
    }

    @Column(name = "DEV_NO")
    public Long getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(Long devNo) {
        this.deviceNo = devNo;
    }

    @Column(name = "TIMESTAMP")
    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Transient
    public Long getVersion() {
        return null;
    }

}


