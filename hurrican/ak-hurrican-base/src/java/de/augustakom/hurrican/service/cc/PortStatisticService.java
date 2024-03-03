package de.augustakom.hurrican.service.cc;

import java.util.*;

import de.augustakom.hurrican.model.cc.view.PortStatisticsView;


public interface PortStatisticService extends ICCService {

    void generatePortUsageStatistics();

    List<PortStatisticsView> retrievePortStatistics();

}
