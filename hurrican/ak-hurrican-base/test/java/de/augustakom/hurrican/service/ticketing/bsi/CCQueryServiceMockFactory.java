/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.02.2012 09:23:51
 */
package de.augustakom.hurrican.service.ticketing.bsi;

import static org.mockito.Mockito.*;

import de.augustakom.hurrican.model.cc.WebServiceConfig;
import de.augustakom.hurrican.service.cc.QueryCCService;

public class CCQueryServiceMockFactory {
    public static QueryCCService createMockCCServiceWithWsUrl(Long wsId, WebServiceConfig wsConfig)
            throws Exception {
        QueryCCService queryService = mock(QueryCCService.class);
        when(queryService.findById(wsId, WebServiceConfig.class)).thenReturn(wsConfig);
        return queryService;
    }
}


