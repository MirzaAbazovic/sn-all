/*

 * Copyright (c) 2017 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 07.03.2017

 */

package de.mnet.hurrican.wholesale.ws;


/**
 * Created by wieran on 07.03.2017.
 */
public enum MessageTypeSpri {
    ABBM("ABBM"), ABM("ABM"), ENTM("ENTM"), ERLM("ERLM"), QEB("QEB"), TAM("TAM"), MTAM("MTAM"), ZWMLE("ZWM-LE"), AKMPV("AKM-PV"), ABMPV("ABM-PV"), ABBMPV("ABBM-PV"), ERLMPV("ERLM-PV"), ENTMPV("ENTM-PV"), VZM("VZM"), KUELE("KUE-LE"), RUEMPV("RUEM-PV");

    private final String label;

    MessageTypeSpri(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
