/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.09.14
 */
package de.mnet.hurrican.atlas.simulator.ffm;

/**
 * All Xpath expressions used in simulator.
 *
 */
public enum FFMXpathExpressions {

    ORDER_ID("//wfs:order/wfs:id"),
    ORDER_DETAILS("//wfs:description/wfs:details");

    /**
     * XPath expression
     */
    private final String xpath;

    /**
     * Constructor using xpath expression.
     *
     * @param xpath
     */
    FFMXpathExpressions(String xpath) {
        this.xpath = xpath;
    }

    /**
     * Gets the xpath expression.
     *
     * @return
     */
    public String getXpath() {
        return xpath;
    }
}
