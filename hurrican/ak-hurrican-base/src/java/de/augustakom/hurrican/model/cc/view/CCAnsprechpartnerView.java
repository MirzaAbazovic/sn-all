/*

 * Copyright (c) 2015 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 20.11.2015

 */

package de.augustakom.hurrican.model.cc.view;


import java.io.*;
import javax.persistence.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * View-Modell fuer einen Ansprechpartner (HUR-23636).
 */
@Entity
@Table(name = "V_ANSPRECHPARTNER_PRO_KUNDE")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class CCAnsprechpartnerView implements Serializable {

    @Id
    @Column(name = "ANSPRECHPARTNER_ID")
    private Long ansprechpartnerId = null;
    @Column(name = "AUFTRAG_ID")
    private Long auftragId = null;
    @Column(name = "KUNDE_NR")
    private Long kundeNo = null;
    @Column(name = "BUENDEL_NR")
    private Integer buendelNr = null;
    @Column(name = "VORNAME")
    private String vorname = null;
    @Column(name = "NAME")
    private String name = null;
    @Column(name = "TYP")
    private String ansprechpartnerType = null;
    @Column(name = "TYPE_REF_ID")
    private Long typeRefId = null;
    @Column(name = "ORDER_NR")
    private Long orderNo = null;
    @Column(name = "TELEFON")
    private String telefon = null;
    @Column(name = "HANDY")
    private String handy = null;
    @Column(name = "EMAIL")
    private String email = null;
    @Column(name = "ADDRESS_ID")
    private Long addressId = null;
    @Transient
    private Boolean takeOver = Boolean.FALSE;
    @Transient
    private Boolean editable = Boolean.TRUE;

    public Long getAnsprechpartnerId() {
        return ansprechpartnerId;
    }

    public void setAnsprechpartnerId(Long ansprechpartnerId) {
        this.ansprechpartnerId = ansprechpartnerId;
    }

    public Long getAuftragId() {
        return auftragId;
    }

    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    public Long getKundeNo() {
        return kundeNo;
    }

    public void setKundeNo(Long kundeNo) {
        this.kundeNo = kundeNo;
    }

    public Integer getBuendelNr() {
        return buendelNr;
    }

    public void setBuendelNr(Integer buendelNr) {
        this.buendelNr = buendelNr;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAnsprechpartnerType() {
        return ansprechpartnerType;
    }

    public void setAnsprechpartnerType(String ansprechpartnerType) {
        this.ansprechpartnerType = ansprechpartnerType;
    }

    public Long getTypeRefId() {
        return typeRefId;
    }

    public void setTypeRefId(Long typeRefId) {
        this.typeRefId = typeRefId;
    }

    public Long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getHandy() {
        return handy;
    }

    public void setHandy(String handy) {
        this.handy = handy;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public Boolean getTakeOver() {
        return takeOver;
    }

    public void setTakeOver(Boolean takeOver) {
        this.takeOver = takeOver;
    }

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }
}
