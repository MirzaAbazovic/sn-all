/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.11.2016
 */
package de.mnet.hurrican.webservice.customerorder.services;

import java.util.*;
import java.util.stream.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.mnet.esb.cdm.customer.customerorderservice.v1.ESBFault;
import de.mnet.esb.cdm.customer.customerorderservice.v1.LineIdsPerCustomerOrderId;

public class VerbindungsBezeichnungMapperService {
    private static final Logger LOGGER = Logger.getLogger(VerbindungsBezeichnungMapperService.class);

    public static List<Long> mapToLong(List<String> idStringList) throws ESBFault {
        if (CollectionUtils.isNotEmpty(idStringList)) {
            final List<Long> result = new ArrayList<>();
            for (String idStr : idStringList) {
                if (StringUtils.isNotEmpty(idStr)) {
                    try {
                        result.add(Long.valueOf(idStr));
                    }
                    catch (NumberFormatException e) {
                        final String msg = String.format("Auftag Nummer [%s] muss eine Nummer sein", idStr);
                        LOGGER.error(msg);
                        throw new ESBFault(msg, e);
                    }
                }
            }
            return result;
        } else {
            return Collections.emptyList();
        }
    }

    public static LineIdsPerCustomerOrderId mapVerbindungsBezeichnung(Long origOrderId, List<VerbindungsBezeichnung> vbList) {
        final LineIdsPerCustomerOrderId lineIdsPerCustomerOrderId = new LineIdsPerCustomerOrderId();
        lineIdsPerCustomerOrderId.setCustomerOrderId(String.valueOf(origOrderId));
        final List<String> lineIds = vbList.stream().map(VerbindungsBezeichnung::getVbz).collect(Collectors.toList());
        lineIdsPerCustomerOrderId.getLineId().addAll(lineIds);
        return lineIdsPerCustomerOrderId;
    }
}
