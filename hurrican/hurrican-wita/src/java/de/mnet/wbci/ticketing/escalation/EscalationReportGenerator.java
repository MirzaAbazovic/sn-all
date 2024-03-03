/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.04.2014
 */
package de.mnet.wbci.ticketing.escalation;

import static de.mnet.wbci.model.EscalationPreAgreementVO.*;
import static de.mnet.wbci.ticketing.escalation.EscalationHelper.*;

import java.util.*;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.model.Antwortfrist;
import de.mnet.wbci.model.BasePreAgreementVO;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.CarrierRole;
import de.mnet.wbci.model.EscalationPreAgreementVO;
import de.mnet.wbci.model.PreAgreementVO;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.builder.EscalationPreAgreementVOBuilder;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciDeadlineService;

/**
 *
 */
@Component
public class EscalationReportGenerator {
    private static final Logger LOGGER = Logger.getLogger(EscalationReportGenerator.class);
    @Autowired
    private WbciCommonService wbciCommonService;
    @Autowired
    private WbciDeadlineService wbciDeadlineService;

    protected static <T extends BasePreAgreementVO> List<T> sortVOs(List<T> voList) {
        Collections.sort(voList, new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                Date date1 = o1.getWechseltermin() != null ? o1.getWechseltermin() : o1.getVorgabeDatum();
                Date date2 = o2.getWechseltermin() != null ? o2.getWechseltermin() : o2.getVorgabeDatum();
                if (date1 == date2) {
                    return 0;
                }
                if (date1 == null) {
                    return -1;
                }
                if (date2 == null) {
                    return 1;
                }
                return date1.compareTo(date2);

            }
        });
        return voList;
    }

    /**
     * Generates an excel based overview sheet, which holds informations about the current escalation state of all not
     * completed preagreements on which M-Net is waiting for an response of the partern carrier .
     *
     * @param fileName file name of the expected excel file
     * @return an excel workbook object of type {@link XSSFWorkbook}.
     */
    public XSSFWorkbook generateCarrierEscalationOverviewReport(String fileName) {
        EscalationListConfiguration config = new EscalationListConfiguration(EscalationListType.CARRIER_OVERVIEW, null, null);
        return getEscalationReport(config, fileName);
    }

    /**
     * Generates an excel based overview sheet for the assigned carrier, which holds the specific information about the
     * current escalation state of all not completed preagreements on which M-Net is waiting for an response of the
     * partern carrier.
     *
     * @param partnerEKP filter the the preagreements after the assigned patnerEKP
     * @param fileName   file name of the expected excel file
     * @return an excel workbook object of type {@link XSSFWorkbook}.
     */
    public XSSFWorkbook generateEscalationReportForCarrier(CarrierCode partnerEKP, String fileName) {
        EscalationListConfiguration config = new EscalationListConfiguration(EscalationListType.CARRIER_SPECIFIC, partnerEKP, null);
        return getEscalationReport(config, fileName);
    }

    /**
     * Generates an excel based overview sheet for M-Net, which holds the specific information about the current
     * deadline state of all not completed preagreements on which M-Net has to answer.
     *
     * @param mNetCarrierRole defines the {@link CarrierRole} from the point of view of M-Net
     * @param fileName   file name of the expected excel file
     * @return an excel workbook object of type {@link XSSFWorkbook}.
     */
    public XSSFWorkbook generateInternalDeadlineOverviewReport(CarrierRole mNetCarrierRole, String fileName) {
        EscalationListConfiguration config = new EscalationListConfiguration(EscalationListType.INTERNAL, null, mNetCarrierRole);
        return getEscalationReport(config, fileName);
    }

    protected XSSFWorkbook getEscalationReport(EscalationListConfiguration config, String fileName) {
        try {
            LOGGER.info("start generating escalation file for " + config.toString());
            List<PreAgreementVO> preAgreements = getPreAgreementVOs(config.getMnetRole());
            XSSFWorkbook result = EscalationSpreadsheetConverter.convert(
                    filterAndEnrichVAs(preAgreements, config), // filterAndEnrichVAs for external escalation data
                    config);
            LOGGER.info("end of generating escalation file for " + config.toString());
            return result;
        }
        catch (Exception e) {
            throw new WbciServiceException(String.format("Unexpected error during the generation of file '%s' with %s", fileName, config.toString()), e);
        }
    }

    /**
     * @return a list of {@link PreAgreementVO}s in consideration of the {@link CarrierRole} from the point of M-Net. If
     * mnetCarrierRole is NULL, all preagreements will be returned.
     */
    protected List<PreAgreementVO> getPreAgreementVOs(CarrierRole mnetCarrierRole) {
        if (mnetCarrierRole != null) {
            return wbciCommonService.findPreAgreements(mnetCarrierRole);
        }
        List<PreAgreementVO> preAgreements = new ArrayList<>();
        preAgreements.addAll(wbciCommonService.findPreAgreements(CarrierRole.AUFNEHMEND));
        preAgreements.addAll(wbciCommonService.findPreAgreements(CarrierRole.ABGEBEND));
        return preAgreements;
    }

    /**
     * Enrich the {@link PreAgreementVO}s with the {@link EscalationPreAgreementVO.EscalationType} and {@link
     * EscalationLevel} and filters after external Deadlines.
     *
     * @param preAgreements Lists on {@link PreAgreementVO}s
     * @param configuration config the usage of the current report over an {@link EscalationListConfiguration} object.
     * @return List of {@link EscalationPreAgreementVO}s
     */
    protected List<EscalationPreAgreementVO> filterAndEnrichVAs(final List<PreAgreementVO> preAgreements, EscalationListConfiguration configuration) {
        List<EscalationPreAgreementVO> result = new ArrayList<>();
        if (preAgreements != null) {
            Map<DeadlineKey, Antwortfrist> deadlineMap = getDeadlineMap();
            for (PreAgreementVO vo : preAgreements) {
                //filter after partner ekp if set
                if (isPartnerEkpValid(configuration.getPartnerEKP(), CarrierRole.lookupMNetPartnerCarrierCode(vo.getEkpAuf(), vo.getEkpAbg()))) {

                    //determine escalation type
                    EscalationPreAgreementVO.EscalationType escalationType =
                            getResponsibleEscalationType(vo.getRequestTyp(), vo.getRequestStatus(), vo.getGeschaeftsfallStatus());

                    //if no escalation type is configured=> do nothing
                    if (escalationType != null && isValidForUsage(escalationType, configuration.getType())) {
                        EscalationLevel escalationLevel =
                                escalationType.getEscalationLevelForDeadline(determineDeadlineDays(configuration.getType(), vo));

                        //if no escalationLevel is reached => do nothing
                        if (escalationLevel != null) {

                            //build new escalation vo based on the pre agreement vo
                            EscalationPreAgreementVOBuilder builder = new EscalationPreAgreementVOBuilder()
                                    .withBasePreAgreementVo(vo)
                                    .withEscalationType(escalationType)
                                    .withEscalationLevel(escalationLevel);

                            Antwortfrist antwortfrist = deadlineMap.get(new DeadlineKey(vo.getRequestTyp(), vo.getRequestStatus()));
                            if (antwortfrist != null) {
                                builder.withDeadlineDays(antwortfrist.getFristInTagen());
                            }

                            // for INFO's remove possible deadline data
                            if (EscalationLevel.INFO.equals(escalationLevel)) {
                                removeDeadlineData(builder);
                            }
                            result.add(builder.build());
                        }
                    }
                }
            }
        }
        return sortVOs(result);
    }

    private boolean isPartnerEkpValid(CarrierCode configuredPartnerEkp, CarrierCode currentPartnerEkp) {
        return configuredPartnerEkp == null || configuredPartnerEkp.equals(currentPartnerEkp);
    }

    private void removeDeadlineData(EscalationPreAgreementVOBuilder builder) {
        builder.withDeadlineDays(null)
                .withDaysUntilDeadlineMnet(null)
                .withDaysUntilDeadlinePartner(null);
    }

    /**
     * Validates that the {@link EscalationPreAgreementVO.EscalationType} is valid for expected usage.
     */
    private boolean isValidForUsage(EscalationPreAgreementVO.EscalationType escalationType, EscalationListType escalationListType) {
        if (EscalationListType.INTERNAL.equals(escalationListType)) {
            return escalationType.isInternal();
        }
        return escalationType.isExternal();
    }

    private Map<DeadlineKey, Antwortfrist> getDeadlineMap() {
        Map<DeadlineKey, Antwortfrist> result = new HashMap<>();
        for (EscalationPreAgreementVO.EscalationType escalationType : EscalationPreAgreementVO.EscalationType.values()) {
            RequestTyp typ = escalationType.getTyp();
            WbciRequestStatus status = escalationType.getRequestStatus();
            DeadlineKey key = new DeadlineKey(typ, status);
            if (typ != null && status != null) {
                result.put(key, wbciDeadlineService.findAntwortfrist(typ, status));
            }
        }
        return result;
    }


    public static enum EscalationListType {
        CARRIER_OVERVIEW, CARRIER_SPECIFIC, INTERNAL
    }

    /**
     * Specifies how the escalation Excel sheet should be build up.
     */
    public static class EscalationListConfiguration {
        private EscalationListType type;
        private CarrierCode partnerEKP;
        private CarrierRole mnetRole;

        /**
         * Define the filter and usage of an escalation report
         *
         * @param type       one of the {@link EscalationListType}
         * @param partnerEKP filter the preagreements after the partner {@link CarrierCode}
         * @param mnetRole   filter the preagreements after the m-net {@link CarrierRole}
         */
        public EscalationListConfiguration(EscalationListType type, CarrierCode partnerEKP, CarrierRole mnetRole) {
            if (EscalationListType.INTERNAL.equals(type)) {
                assert (mnetRole != null);
            }
            if (EscalationListType.CARRIER_SPECIFIC.equals(type)) {
                assert (partnerEKP != null);
            }
            this.type = type;
            this.partnerEKP = partnerEKP;
            this.mnetRole = mnetRole;
        }

        public EscalationListType getType() {
            return type;
        }

        public CarrierCode getPartnerEKP() {
            return partnerEKP;
        }

        public CarrierRole getMnetRole() {
            return mnetRole;
        }

        @Override
        public String toString() {
            return "EscalationListConfiguration [" +
                    "type=" + type +
                    ", partnerEKP=" + partnerEKP +
                    ", mnetRole=" + mnetRole +
                    ']';
        }
    }

    public static class DeadlineKey {
        private WbciRequestStatus status;
        private RequestTyp typ;

        public DeadlineKey(RequestTyp typ, WbciRequestStatus status) {
            this.typ = typ;
            this.status = status;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            DeadlineKey that = (DeadlineKey) o;

            if (status != that.status) {
                return false;
            }
            return typ == that.typ;

        }

        @Override
        public int hashCode() {
            int result = status.hashCode();
            result = 31 * result + typ.hashCode();
            return result;
        }
    }
}
