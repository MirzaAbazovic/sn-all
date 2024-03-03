/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.09.2005 13:54:24
 */
package de.augustakom.hurrican.service.billing;

import de.augustakom.common.service.iface.ISimpleFindService;
import de.augustakom.hurrican.service.base.iface.QueryService;


/**
 * Service-Interface, um auf dem Billing-System beliebige Abfragen auszufuehren.
 *
 *
 */
public interface QueryBillingService extends IBillingService, QueryService, ISimpleFindService {

}


