/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.03.2016
 */
package de.augustakom.hurrican.service.cc;

/**
 * dummy service for debugging socket problems Created by zolleiswo on 02.03.2016.
 */
public interface DummySocketService extends ICCService {
    public String doSomething(long waitTimeInMs);
}
