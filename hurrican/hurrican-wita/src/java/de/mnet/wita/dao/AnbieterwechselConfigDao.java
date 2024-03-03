/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.06.2011 13:15:38
 */
package de.mnet.wita.dao;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.mnet.wbci.model.ProduktGruppe;
import de.mnet.wita.message.common.Carrier;
import de.mnet.wita.model.AnbieterwechselConfig;
import de.mnet.wita.model.AnbieterwechselConfig.NeuProdukt;

/**
 * DAO Interface fuer die Verwaltung von {@link AnbieterwechselConfig} Objekten.
 */
public interface AnbieterwechselConfigDao extends FindDAO, StoreDAO, ByExampleDAO {

    AnbieterwechselConfig findConfig(Carrier carrierAbgebend, ProduktGruppe produktGruppe, NeuProdukt neuProdukt);

}
