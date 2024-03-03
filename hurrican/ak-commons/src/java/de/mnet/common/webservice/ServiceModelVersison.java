/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.12.2014
 */
package de.mnet.common.webservice;


/**
 * A common Interface to handle all different version types of the external service models, like the CDM version of
 * WITA, WBCI or FFM.
 */
public interface ServiceModelVersison<T extends Enum> {

    String getVersion();

    boolean isGreaterOrEqualThan(T versionToCheck);

    String getName();
}
