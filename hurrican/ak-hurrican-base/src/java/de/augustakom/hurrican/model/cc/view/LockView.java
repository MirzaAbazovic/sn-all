/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.01.2010 13:11:11
 */
package de.augustakom.hurrican.model.cc.view;

import java.lang.reflect.*;
import org.apache.commons.beanutils.PropertyUtils;

import de.augustakom.hurrican.model.cc.Lock;


/**
 * Erweiterung von {@link Lock}, um den Kundennamen in der GUI darzustellen.
 *
 *
 */
public class LockView extends Lock {

    private String kundenName;
    private Lock lock;
    private String auftragStatus;

    public void setLock(Lock lock) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        this.lock = lock;
        PropertyUtils.copyProperties(this, lock);
    }

    public Lock getLock() {
        return lock;
    }

    public String getKundenName() {
        return kundenName;
    }

    public void setKundenName(String kundenName) {
        this.kundenName = kundenName;
    }

    public String getAuftragStatus() {
        return auftragStatus;
    }

    public void setAuftragStatus(String auftragStatus) {
        this.auftragStatus = auftragStatus;
    }

}


