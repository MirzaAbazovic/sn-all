/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.03.2007 15:35:39
 */
package de.augustakom.hurrican.model.billing;


/**
 * Modell zur Abbildung der PrintSets aus Taifun.
 *
 *
 */
public class ArchPrintSet extends AbstractBillingModel {

    private Long printSetNo = null;
    private String name = null;

    /**
     * @return Returns the printSetNo.
     */
    public Long getPrintSetNo() {
        return printSetNo;
    }

    /**
     * @param printSetNo The printSetNo to set.
     */
    public void setPrintSetNo(Long printSetNo) {
        this.printSetNo = printSetNo;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

}


