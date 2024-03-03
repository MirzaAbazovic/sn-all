/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.11.2011 09:14:28
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import java.io.*;
import java.util.*;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 *
 */
@XStreamAlias("IAD_PORTS")
public class CPSVoIPIADPortsData implements Serializable {

    private static final long serialVersionUID = 7666559330790605041L;

    public CPSVoIPIADPortsData() {
        this.ports = new LinkedList<>();
    }

    @XStreamImplicit(itemFieldName = "PORT")
    private List<Integer> ports;

    public List<Integer> getPorts() {
        return ports;
    }

    public void setPort(List<Integer> ports) {
        this.ports = ports;
    }

    public void addPort(int port) {
        ports.add(port);
    }
}


