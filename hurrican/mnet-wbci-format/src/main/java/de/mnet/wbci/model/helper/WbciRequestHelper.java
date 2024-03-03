/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.11.13
 */
package de.mnet.wbci.model.helper;

import java.util.*;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;

import de.mnet.wbci.model.StornoAnfrage;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.WbciRequestStatus;

/**
 *
 */
public class WbciRequestHelper {

    public static String extractAenderungIds(List<WbciRequest> requests) {
        StringBuilder result = new StringBuilder();

        for (WbciRequest request : requests) {
            if (request instanceof StornoAnfrage) {
                result.append(String.format("%s  (%s)%n", ((StornoAnfrage) request).getAenderungsId(), request.getTyp().getShortName()));
            }
            else if (request instanceof TerminverschiebungsAnfrage) {
                result.append(String.format("%s  (%s)%n", ((TerminverschiebungsAnfrage) request).getAenderungsId(), request.getTyp().getShortName()));
            }
        }
        return result.toString();
    }

    public static String extractTaifonOrderIds(WbciGeschaeftsfall wbciGeschaeftsfall) {
        StringBuilder result = new StringBuilder();
        if (wbciGeschaeftsfall.getBillingOrderNoOrig() != null) {
            result.append(wbciGeschaeftsfall.getBillingOrderNoOrig()).append(" (Hauptauftrag)");
        }

        if (CollectionUtils.isNotEmpty(wbciGeschaeftsfall.getNonBillingRelevantOrderNoOrigs())) {
            for (Long billingOrderNo : Sets.newTreeSet(wbciGeschaeftsfall.getNonBillingRelevantOrderNoOrigs())) {
                result.append("\n").append(billingOrderNo);
            }
        }
        return result.toString();
    }

    public static List<WbciRequestStatus> getAllGeschaeftsfallRequestStatuses(List<WbciRequest> requests) {
        List<WbciRequestStatus> allGfStatuses = new ArrayList<>();

        if (requests != null) {
            for (WbciRequest request : requests) {
                allGfStatuses.add(request.getRequestStatus());
            }
        }

        return allGfStatuses;
    }

    /**
     * Returns true if an active TV or STORNO request is found
     *
     * @param geschaeftsfallRequestStatuses the current request statuses attached to the geschaeftsfall.
     * @return
     */
    public static boolean isActiveStornoOrTvRequestStatusIncluded(List<WbciRequestStatus> geschaeftsfallRequestStatuses) {
        for (WbciRequestStatus requestStatus : geschaeftsfallRequestStatuses) {
            if (requestStatus.isActiveStornoRequestStatus() || requestStatus.isActiveTvRequestStatus()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if an active TV or STORNO request is found
     *
     * @param requests the current requests attached to the geschaeftsfall.
     * @return
     */
    public static boolean isActiveStornoOrTvRequestIncluded(List<WbciRequest> requests) {
        List<WbciRequestStatus> geschaeftsfallRequestStatuses = getAllGeschaeftsfallRequestStatuses(requests);
        return isActiveStornoOrTvRequestStatusIncluded(geschaeftsfallRequestStatuses);
    }

}
