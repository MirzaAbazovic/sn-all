/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.01.14
 */
package de.mnet.wbci.dao;

import java.io.*;
import java.util.*;

import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.WbciRequestStatus;

/**
 * This class is used as a container for specifying the criteria to use when searching for vorabstimmungIds. Refer to
 * {@link WbciDao#findVorabstimmungIdsByBillingOrderNoOrig(VorabstimmungIdsByBillingOrderNoCriteria)} for details.
 */
public class VorabstimmungIdsByBillingOrderNoCriteria implements Serializable {

    private static final long serialVersionUID = 4613554451153759854L;

    private final Long matchingBillingOrderNoOrig;
    private final Class<? extends WbciRequest> wbciRequestClassType;
    private List<WbciGeschaeftsfallStatus> matchingGeschaeftsfallStatuses = new ArrayList<>();
    private List<WbciRequestStatus> matchingRequestStatuses = new ArrayList<>();

    public VorabstimmungIdsByBillingOrderNoCriteria(Long matchingBillingOrderNoOrig, Class<? extends WbciRequest> wbciRequestClassType) {
        this.matchingBillingOrderNoOrig = matchingBillingOrderNoOrig;
        this.wbciRequestClassType = wbciRequestClassType;
    }

    public Long getMatchingBillingOrderNoOrig() {
        return matchingBillingOrderNoOrig;
    }

    public Class<? extends WbciRequest> getWbciRequestClassType() {
        return wbciRequestClassType;
    }

    public List<WbciGeschaeftsfallStatus> getMatchingGeschaeftsfallStatuses() {
        return matchingGeschaeftsfallStatuses;
    }

    public VorabstimmungIdsByBillingOrderNoCriteria addMatchingGeschaeftsfallStatus(WbciGeschaeftsfallStatus... geschaeftsfallStatus) {
        this.matchingGeschaeftsfallStatuses.addAll(Arrays.asList(geschaeftsfallStatus));
        return this;
    }

    public List<WbciRequestStatus> getMatchingRequestStatuses() {
        return matchingRequestStatuses;
    }

    public VorabstimmungIdsByBillingOrderNoCriteria addMatchingRequestStatus(WbciRequestStatus... matchingRequestStatus) {
        this.matchingRequestStatuses.addAll(Arrays.asList(matchingRequestStatus));
        return this;
    }
}
