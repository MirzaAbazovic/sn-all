/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.08.13
 */
package de.mnet.wbci.marshal.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.ObjectFactory;

/**
 * Base marshaller for all other CDM V1 marshalers.
 *
 *
 */
public abstract class AbstractBaseMarshaller {

    protected static final ObjectFactory V1_OBJECT_FACTORY = new ObjectFactory();

}
