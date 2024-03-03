package de.augustakom.hurrican.service.cc.impl;

import java.util.*;

import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.PortStatisticDAO;
import de.augustakom.hurrican.model.cc.view.PortStatisticsView;
import de.augustakom.hurrican.service.cc.PortStatisticService;

@CcTxRequired
public class PortStatisticServiceImpl extends DefaultCCService implements PortStatisticService {

    private PortStatisticDAO portStatisticDAO;

    @Override
    public void generatePortUsageStatistics() {
        portStatisticDAO.generatePortUsageStatistics();
    }

    public void setPortStatisticDAO(PortStatisticDAO portStatisticDAO) {
        this.portStatisticDAO = portStatisticDAO;
    }

    @Override
    public List<PortStatisticsView> retrievePortStatistics() {
        return portStatisticDAO.retrievePortStatistics();
    }

}
