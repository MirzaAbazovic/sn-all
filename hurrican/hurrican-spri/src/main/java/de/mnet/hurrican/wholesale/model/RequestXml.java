/*

 * Copyright (c) 2017 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 09.02.2017

 */

package de.mnet.hurrican.wholesale.model;


/**
 * A wrapper for an XML message.
 *
 * Created by zieglerch on 09.02.2017.
 */
public class RequestXml {

    private String xml;

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        RequestXml that = (RequestXml) o;

        return xml != null ? xml.equals(that.xml) : that.xml == null;
    }

    @Override
    public int hashCode() {
        return xml != null ? xml.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "RequestXml{" +
                "xml='" + xml + '\'' +
                '}';
    }
}
