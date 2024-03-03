/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.10.13
 */
package de.mnet.wbci.acceptance.kft;

import de.mnet.wbci.acceptance.AbstractWbciAcceptanceTestBuilder;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.RequestTyp;

/**
 *
 */
public class AbstractWbciKftTestBuilder extends AbstractWbciAcceptanceTestBuilder {

    protected static final String USE_KFT_SIMULATOR = "use.kft.simulator";

    @Override
    public void init() {
        super.init();
        // set KFT test mode to true during the test execution just in case it is disabled
        hurrican().setKftTestMode(true);
        // cleanUp for the Variables at the end of a test.
        kft().cleanUpVariables();
    }

    /**
     * Creates new preAgreementId valid for KFT test system. In simulator mode ids must have a special SIM prefix
     * otherwise the prefix is KFT. The defined preAgreementId pattern is:
     * <p/>
     * DEU.MNET.{requestTyp}{prefix}{useCaseNumberKft} or DEU.MNET.{requestTyp}{prefix}{useCaseNumberSim}
     * <p/>
     * Examples: DEU.MNET.VSIM000001 (Vorabstimmung in simulator mode) DEU.MNET.VKFT000002 (Vorabstimmung in kft mode)
     * DEU.MNET.TSIM000003 (Terminverschiebung in simulator mode) DEU.MNET.SKFT000004 (Storno in kft mode)
     *
     * @param requestTyp
     * @param useCaseNumberKft use case number for the KFT system
     * @param useCaseNumberSim use case number for the KFT simulator
     * @return
     */
    protected String createPreAgreementId(RequestTyp requestTyp, CarrierCode originalSender, String useCaseNumberKft, String useCaseNumberSim) {
        if (useCaseNumberKft.length() > 6) {
            throw new IllegalArgumentException(
                    "Failed to create preAgreementId as kft usecase number is to long (max. 6 chars): '"
                            + useCaseNumberKft + "'"
            );
        }
        if (useCaseNumberSim.length() > 6) {
            throw new IllegalArgumentException(
                    "Failed to create preAgreementId as simulator usecase number is to long (max. 6 chars): '"
                            + useCaseNumberSim + "'"
            );
        }

        String preAgreementId = isUseKftSimulator() ? "SIM" + useCaseNumberSim : "KFT" + useCaseNumberKft;
        return originalSender.getITUCarrierCode() + "." + requestTyp.getPreAgreementIdCode() + preAgreementId;
    }

    /**
     * @return the default VA-ID-Ref "DEU.XXXX.V000000000" of the WBCI-KFT-System.
     */
    protected String createKftDefaultPreAgreementIdRef() {
        return "DEU.XXXX.V000000000";
    }

    private boolean isUseKftSimulator() {
        return Boolean.valueOf(System.getProperty(USE_KFT_SIMULATOR, "false"));
    }
}
