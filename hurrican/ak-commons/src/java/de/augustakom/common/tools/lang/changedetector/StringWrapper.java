/**
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.03.2011 16:42:18
 */

package de.augustakom.common.tools.lang.changedetector;

import java.io.*;


/**
 * String Wrapper fuer den ObjectChangeDetector (addObjectToWatch() und hasChanged())
 */
public class StringWrapper implements Serializable {

    private String watch;

    public StringWrapper(String watch) {
        setWatch(watch);
    }

    public String getWatch() {
        return watch;
    }

    public void setWatch(String watch) {
        this.watch = watch;
    }

}
