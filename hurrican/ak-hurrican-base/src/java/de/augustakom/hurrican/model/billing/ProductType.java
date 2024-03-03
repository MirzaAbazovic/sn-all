/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.03.2006 09:44:56
 */
package de.augustakom.hurrican.model.billing;


/**
 * Modellklasse zur Abbildung der Konfiguration eines Billing-Produkts. <br> Achtung: die Konfiguration der
 * Rufnummerneigenschaften (max. Anzahl, Typ, Block) wird z.Z. ueber das Model de.augustakom.hurrican.model.cc.Produkt
 * abgebildet. Grund: in der geplanten Produktstruktur fuer Taifun sollen unter einem Produkt (=OE) sowohl analoge, S0M,
 * S0A und PMXer definiert werden koennen. Da ProductType immer an einer OE angehaengt ist, ist die Konfiguration
 * darueber nicht mehr moeglich. Das Modell und die zugehoerigen Service-Methoden bleiben jedoch noch weiterhin
 * bestehen, falls zukuenftig noch andere Daten von dieser Tabelle benoetigt werden. <br>
 *
 *
 */
public class ProductType extends AbstractHistoryModel {

    private Long productOeNo = null;
    private String productType = null;
    private String connectionType = null;
    private Long oeNo = null;
    private Integer maxCountDN = null;
    private Boolean block = null;

    /**
     * @return Returns the connectionType.
     */
    public String getConnectionType() {
        return connectionType;
    }

    /**
     * @param connectionType The connectionType to set.
     */
    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    /**
     * @return Returns the oeNo.
     */
    public Long getOeNo() {
        return oeNo;
    }

    /**
     * @param oeNo The oeNo to set.
     */
    public void setOeNo(Long oeNo) {
        this.oeNo = oeNo;
    }

    /**
     * @return Returns the productOeNo.
     */
    public Long getProductOeNo() {
        return productOeNo;
    }

    /**
     * @param productOeNo The productOeNo to set.
     */
    public void setProductOeNo(Long produktOeNoOrig) {
        this.productOeNo = produktOeNoOrig;
    }

    /**
     * @return Returns the productType.
     */
    public String getProductType() {
        return productType;
    }

    /**
     * @param productType The productType to set.
     */
    public void setProductType(String produktType) {
        this.productType = produktType;
    }

    /**
     * @return Returns the maxCountDN.
     */
    public Integer getMaxCountDN() {
        return maxCountDN;
    }

    /**
     * @param maxCountDN The maxCountDN to set.
     */
    public void setMaxCountDN(Integer maxCountDN) {
        this.maxCountDN = maxCountDN;
    }

    /**
     * @return Returns the block.
     */
    public Boolean getBlock() {
        return block;
    }

    /**
     * @param block The block to set.
     */
    public void setBlock(Boolean block) {
        this.block = block;
    }

}


