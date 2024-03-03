package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.hurrican.model.cc.view.PortStatisticsView;

public interface PortStatisticDAO {

    int generatePortUsageStatistics();

    List<PortStatisticsView> retrievePortStatistics();

}
