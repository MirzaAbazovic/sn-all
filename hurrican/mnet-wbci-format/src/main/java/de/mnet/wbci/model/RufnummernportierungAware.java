/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.08.13
 */
package de.mnet.wbci.model;

/**
 * Marker-Interface fuer WBCI-Objekte, die eine Rufnummernportierung enthalten.
 */
public interface RufnummernportierungAware {

    public Rufnummernportierung getRufnummernportierung();

    public void setRufnummernportierung(Rufnummernportierung rufnummernportierung);
}
