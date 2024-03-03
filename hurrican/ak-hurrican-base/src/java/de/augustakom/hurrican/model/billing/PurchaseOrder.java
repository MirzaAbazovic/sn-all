/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH

 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.02.2013 11:00:35
 */
package de.augustakom.hurrican.model.billing;

import java.util.*;
import javax.persistence.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.base.AbstractTaifunModel;


/**
 * Bildet ein PurchaseOrder aus dem Billing-System ab.
 *
 *
 */

@Entity
@Table(name = "PURCHASE_ORDER")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_PURCHASE_ORDER_0", allocationSize = 1)
public class PurchaseOrder extends AbstractTaifunModel {

    /** Status sagt aus, dass die Bestellung erstellt, aber noch nicht aktiviert wurde. */
    public static final String STATUS_PLANNED = "PLANNED";
    /** Status sagt aus, dass die Bestellung aktiviert, aber noch nicht an KOMSA uebermittelt wurde. */
    public static final String STATUS_NEW = "NEW";
    /** Status sagt aus, dass die Bestellung an Komsa uebermittelt wurde. */
    public static final String STATUS_CREATED = "CREATED";
    /** Status sagt aus, dass die Bestellung bereits teilweise geliefert wurde. */
    public static final String STATUS_PARTIAL = "PARTIAL";
    /** Status sagt aus, dass die Bestellung erledigt ist. */
    public static final String STATUS_CLOSED = "CLOSED";

    static final Long SUPPLIER_KOMSA = Long.valueOf(1);

    private static final long serialVersionUID = 583041784162650920L;

    private Long deliveryAddressNo;
    private Date deliveryDatePlanned;
    private Long purchaseOrderNo;
    // Verbindung mit der AUFTRAG Tabelle durch AUFTRAG__NO i.e. auftragNoOrig
    private Long serviceNoOrig;
    private String status;
    private Long supplierNo;
    private String userW;
    private Date dateW;


    @Transient
    public boolean isSupplierKomsa() {
        return NumberTools.equal(getSupplierNo(), SUPPLIER_KOMSA);
    }


    /**
     * Ueberprueft, ob das PurchaseOrder nicht aktiviert ist. <br> Dies ist dann der Fall, wenn <code>Status</code> den
     * Wert PLANNED besitzt.
     *
     * @return true wenn das PurchaseOrder aktiviert ist.
     */
    @Transient
    public boolean isNotActivated() {
        return StringUtils.containsIgnoreCase(getStatus(), STATUS_PLANNED);
    }


    /**
     * Ueberprueft, ob die PurchaseOrder bereits aktiviert oder erledigt (auch teilweise) ist.
     * @return
     */
    @Transient
    public boolean isActivatedOrDone() {
        return StringUtils.containsIgnoreCase(getStatus(), STATUS_CREATED)
                || StringUtils.containsIgnoreCase(getStatus(), STATUS_NEW)
                || StringUtils.containsIgnoreCase(getStatus(), STATUS_PARTIAL)
                || StringUtils.containsIgnoreCase(getStatus(), STATUS_CLOSED);
    }


    @Column(name = "DATEW", nullable = false)
    public Date getDateW() {
        return dateW;
    }

    public void setDateW(Date dateW) {
        this.dateW = dateW;
    }

    @Column(name = "DELIVERY_ADDRESS_NO", nullable = false)
    public Long getDeliveryAddressNo() {
        return deliveryAddressNo;
    }

    public void setDeliveryAddressNo(Long deliveryAddressNo) {
        this.deliveryAddressNo = deliveryAddressNo;
    }

    @Column(name = "DELIVERY_DATE_PLANNED", nullable = true)
    public Date getDeliveryDatePlanned() {
        return deliveryDatePlanned;
    }

    public void setDeliveryDatePlanned(Date deliveryDatePlanned) {
        this.deliveryDatePlanned = deliveryDatePlanned;
    }

    @Id
    @Column(name = "PURCHASE_ORDER_NO", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
    public Long getPurchaseOrderNo() {
        return purchaseOrderNo;
    }

    public void setPurchaseOrderNo(Long purchaseOrderNo) {
        this.purchaseOrderNo = purchaseOrderNo;
    }

    @Column(name = "SERVICE__NO", nullable = false)
    public Long getServiceNoOrig() {
        return serviceNoOrig;
    }

    public void setServiceNoOrig(Long serviceNoOrig) {
        this.serviceNoOrig = serviceNoOrig;
    }

    @Column(name = "STATUS", nullable = false)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(name = "SUPPLIER_NO", nullable = false)
    public Long getSupplierNo() {
        return supplierNo;
    }

    public void setSupplierNo(Long supplierNo) {
        this.supplierNo = supplierNo;
    }

    @Column(name = "USERW", nullable = false)
    public String getUserW() {
        return userW;
    }

    public void setUserW(String userW) {
        this.userW = userW;
    }
}


