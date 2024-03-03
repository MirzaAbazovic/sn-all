/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.10.13
 */
package de.mnet.wbci.validation.helper;

import javax.validation.*;
import org.springframework.stereotype.Component;

import de.mnet.wbci.model.AbbruchmeldungStornoAen;
import de.mnet.wbci.model.AbbruchmeldungStornoAuf;
import de.mnet.wbci.model.AbbruchmeldungTerminverschiebung;
import de.mnet.wbci.model.ErledigtmeldungStornoAen;
import de.mnet.wbci.model.ErledigtmeldungStornoAuf;
import de.mnet.wbci.model.ErledigtmeldungTerminverschiebung;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciMessage;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.validation.groups.V1Meldung;
import de.mnet.wbci.validation.groups.V1MeldungStorno;
import de.mnet.wbci.validation.groups.V1MeldungTv;
import de.mnet.wbci.validation.groups.V1MeldungVa;
import de.mnet.wbci.validation.groups.V1MeldungVaKueMrn;
import de.mnet.wbci.validation.groups.V1MeldungVaKueMrnWarn;
import de.mnet.wbci.validation.groups.V1MeldungVaKueOrn;
import de.mnet.wbci.validation.groups.V1MeldungVaKueOrnWarn;
import de.mnet.wbci.validation.groups.V1MeldungVaRrnp;
import de.mnet.wbci.validation.groups.V1MeldungVaRrnpWarn;
import de.mnet.wbci.validation.groups.V1MeldungWarn;
import de.mnet.wbci.validation.groups.V1Request;
import de.mnet.wbci.validation.groups.V1RequestTvVaKueMrn;
import de.mnet.wbci.validation.groups.V1RequestTvVaKueMrnWarn;
import de.mnet.wbci.validation.groups.V1RequestTvVaKueOrn;
import de.mnet.wbci.validation.groups.V1RequestTvVaKueOrnWarn;
import de.mnet.wbci.validation.groups.V1RequestTvVaRrnp;
import de.mnet.wbci.validation.groups.V1RequestTvVaRrnpWarn;
import de.mnet.wbci.validation.groups.V1RequestVaKueMrn;
import de.mnet.wbci.validation.groups.V1RequestVaKueMrnWarn;
import de.mnet.wbci.validation.groups.V1RequestVaKueOrn;
import de.mnet.wbci.validation.groups.V1RequestVaKueOrnWarn;
import de.mnet.wbci.validation.groups.V1RequestVaRrnp;
import de.mnet.wbci.validation.groups.V1RequestVaRrnpWarn;
import de.mnet.wbci.validation.groups.V1RequestWarn;

/**
 * Helper class for assisting when validation WBCI Model classes.
 */
@Component
public class ValidationHelper {

    /**
     * Retrieves the error validation groups to use when validating a {@link WbciMessage}. The groups returned take the
     * {@link WbciCdmVersion} into account.
     */
    public Class<?>[] getErrorValidationGroups(WbciCdmVersion wbciCdmVersion, WbciMessage wbciMessage) {
        if (wbciCdmVersion == WbciCdmVersion.V1) {
            if (wbciMessage instanceof WbciRequest) {
                switch (((WbciRequest) wbciMessage).getTyp()) {
                    case VA:
                        switch (wbciMessage.getWbciGeschaeftsfall().getTyp()) {
                            case VA_KUE_MRN:
                                return new Class<?>[] { V1RequestVaKueMrn.class };
                            case VA_KUE_ORN:
                                return new Class<?>[] { V1RequestVaKueOrn.class };
                            case VA_RRNP:
                                return new Class<?>[] { V1RequestVaRrnp.class };
                            default:
                                break;
                        }
                        break;
                    case TV:
                        switch (wbciMessage.getWbciGeschaeftsfall().getTyp()) {
                            case VA_KUE_MRN:
                                return new Class<?>[] { V1RequestTvVaKueMrn.class };
                            case VA_KUE_ORN:
                                return new Class<?>[] { V1RequestTvVaKueOrn.class };
                            case VA_RRNP:
                                return new Class<?>[] { V1RequestTvVaRrnp.class };
                            default:
                                break;
                        }
                        break;
                    default:
                        return new Class<?>[] { V1Request.class };
                }
            }
            else if (wbciMessage instanceof Meldung) {
                switch (((Meldung) wbciMessage).getTyp()) {
                    case ABBM:
                        if (wbciMessage instanceof AbbruchmeldungStornoAen || wbciMessage instanceof AbbruchmeldungStornoAuf) {
                            return new Class<?>[] { V1MeldungStorno.class };
                        }
                        else if (wbciMessage instanceof AbbruchmeldungTerminverschiebung) {
                            return new Class<?>[] { V1MeldungTv.class };
                        }
                        return new Class<?>[] { V1MeldungVa.class };
                    case ERLM:
                        if (wbciMessage instanceof ErledigtmeldungStornoAen || wbciMessage instanceof ErledigtmeldungStornoAuf) {
                            return new Class<?>[] { V1MeldungStorno.class };
                        }
                        else if (wbciMessage instanceof ErledigtmeldungTerminverschiebung) {
                            return new Class<?>[] { V1MeldungTv.class };
                        }
                        return new Class<?>[] { V1MeldungVa.class };
                    case AKM_TR:
                        switch (wbciMessage.getWbciGeschaeftsfall().getTyp()) {
                            case VA_KUE_MRN:
                                return new Class<?>[] { V1MeldungVaKueMrn.class };
                            case VA_KUE_ORN:
                                return new Class<?>[] { V1MeldungVaKueOrn.class };
                            case VA_RRNP:
                                return new Class<?>[] { V1MeldungVaRrnp.class };
                            default:
                                break;
                        }
                        break;
                    case RUEM_VA:
                        switch (wbciMessage.getWbciGeschaeftsfall().getTyp()) {
                            case VA_KUE_MRN:
                                return new Class<?>[] { V1MeldungVaKueMrn.class };
                            case VA_KUE_ORN:
                                return new Class<?>[] { V1MeldungVaKueOrn.class };
                            case VA_RRNP:
                                return new Class<?>[] { V1MeldungVaRrnp.class };
                            default:
                                break;
                        }
                        break;
                    default:
                        return new Class<?>[] { V1Meldung.class };
                }
            }
            return null;
        }
        throw new RuntimeException(String.format("Unsupported CDM-Version '%s'!", wbciCdmVersion));
    }

    /**
     * Retrieves the warning validation groups to use when validating a {@link WbciMessage}. The groups returned take
     * the {@link WbciCdmVersion} into account.
     */
    public Class<?>[] getWarningValidationGroups(WbciCdmVersion wbciCdmVersion, WbciMessage wbciMessage) {
        if (wbciCdmVersion == WbciCdmVersion.V1) {
            if (wbciMessage instanceof WbciRequest) {
                switch (((WbciRequest) wbciMessage).getTyp()) {
                    case VA:
                        switch (wbciMessage.getWbciGeschaeftsfall().getTyp()) {
                            case VA_KUE_MRN:
                                return new Class<?>[] { V1RequestVaKueMrnWarn.class };
                            case VA_KUE_ORN:
                                return new Class<?>[] { V1RequestVaKueOrnWarn.class };
                            case VA_RRNP:
                                return new Class<?>[] { V1RequestVaRrnpWarn.class };
                            default:
                                break;
                        }
                        break;
                    case TV:
                        switch (wbciMessage.getWbciGeschaeftsfall().getTyp()) {
                            case VA_KUE_MRN:
                                return new Class<?>[] { V1RequestTvVaKueMrnWarn.class };
                            case VA_KUE_ORN:
                                return new Class<?>[] { V1RequestTvVaKueOrnWarn.class };
                            case VA_RRNP:
                                return new Class<?>[] { V1RequestTvVaRrnpWarn.class };
                            default:
                                break;
                        }
                        break;
                    default:
                        return new Class<?>[] { V1RequestWarn.class };
                }
            }
            else if (wbciMessage instanceof Meldung) {
                switch (((Meldung) wbciMessage).getTyp()) {
                    case RUEM_VA:
                        switch (wbciMessage.getWbciGeschaeftsfall().getTyp()) {
                            case VA_KUE_MRN:
                                return new Class<?>[] { V1MeldungVaKueMrnWarn.class };
                            case VA_KUE_ORN:
                                return new Class<?>[] { V1MeldungVaKueOrnWarn.class };
                            case VA_RRNP:
                                return new Class<?>[] { V1MeldungVaRrnpWarn.class };
                            default:
                                break;
                        }
                        break;
                    default:
                        return new Class<?>[] { V1MeldungWarn.class };
                }
            }
            return null;
        }
        throw new RuntimeException(String.format("Unsupported CDM-Version '%s'!", wbciCdmVersion));
    }

    public static boolean addConstraintViolation(ConstraintValidatorContext context, String errorMsg) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(errorMsg).addConstraintViolation();
        return false;
    }
}
