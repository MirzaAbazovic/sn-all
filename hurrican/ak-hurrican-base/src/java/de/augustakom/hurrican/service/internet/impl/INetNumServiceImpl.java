/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.12.2011 09:08:16
 */
package de.augustakom.hurrican.service.internet.impl;

import java.util.*;
import javax.annotation.*;

import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.annotation.CcTxRequiredReadOnly;
import de.augustakom.hurrican.dao.internet.INetNumDao;
import de.augustakom.hurrican.service.internet.INetNumService;


public class INetNumServiceImpl extends DefaultInternetService implements INetNumService {

    @Resource(name = "internet.inetNumDao")
    private INetNumDao inetNumDao;

    @Override
    @CcTxRequiredReadOnly
    public List<Pair<Long, String>> findAllNetIdsWithPoolName() {
        return inetNumDao.findAllNetIdsWithPoolName();
    }

}


