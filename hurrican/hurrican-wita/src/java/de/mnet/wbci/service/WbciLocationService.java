package de.mnet.wbci.service;

import java.util.*;

import de.mnet.wbci.config.WbciConstants;
import de.mnet.wbci.model.Standort;

/**
 *
 */
public interface WbciLocationService extends WbciService, WbciConstants {

    /**
     * Location search operation on standort returning a list of geo ids coming from Atlas ESB.
     *
     * @param standort
     * @return
     */
    List<Long> getLocationGeoIds(Standort standort);
}
