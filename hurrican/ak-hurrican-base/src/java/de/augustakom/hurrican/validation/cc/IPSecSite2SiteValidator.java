/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.05.2006 16:27:51
 */
package de.augustakom.hurrican.validation.cc;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;

import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.net.IPToolsV4;
import de.augustakom.common.tools.net.IPValidationTool;
import de.augustakom.common.tools.net.IPValidationTool.ValidationResult;
import de.augustakom.common.tools.validation.AbstractValidator;
import de.augustakom.hurrican.model.cc.IPSecSite2Site;


/**
 * Validator fuer Objekte des Typs <code>IPSecSite2Site</code>.
 *
 *
 */
public class IPSecSite2SiteValidator extends AbstractValidator {

    /**
     * @see de.augustakom.common.tools.validation.AbstractValidatior#supports(java.lang.Class)
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return IPSecSite2Site.class.isAssignableFrom(clazz);
    }

    /**
     * @see de.augustakom.common.tools.validation.AbstractValidatior#validate(java.lang.Object,
     * org.springframework.validation.Errors)
     */
    @Override
    public void validate(Object object, Errors errors) {
        IPSecSite2Site toValidate = (IPSecSite2Site) object;

        if (toValidate.getAuftragId() == null) {
            errors.rejectValue("auftragId", ERRCODE_REQUIRED, "Auftrags-ID muss definiert werden!");
        }
        validateWanGateway(toValidate.getWanGateway(), errors);
        validateLoopbackIp(toValidate.getLoopbackIp(), errors);
        validateLoopbackIpPassive(toValidate.getLoopbackIpPassive(), errors);
        validateVirtualLanIp(toValidate.getVirtualLanIp(), errors);
        validateVirtualLanToScramble(toValidate.getVirtualLan2Scramble(), errors);
        validateVirtualWanIp(toValidate.getVirtualWanIp(), errors);

        if (isNotBlank(toValidate.getVirtualLanSubmask()) && !validateNetmask(toValidate.getVirtualLanSubmask())) {
            errors.rejectValue("virtualLanSubmask", ERRCODE_INVALID, "Virtual LAN Submask ist ungueltig!");
        }
        if (isNotBlank(toValidate.getVirtualWanSubmask()) && !validateNetmask(toValidate.getVirtualWanSubmask())) {
            errors.rejectValue("virtualWanSubmask", ERRCODE_INVALID, "Virtual WAN Submask ist ungueltig!");
        }
        if (BooleanTools.nullToFalse(toValidate.getHasCertificate()) && BooleanTools.nullToFalse(toValidate.getHasPresharedKey())) {
            errors.rejectValue("hasCertificate", ERRCODE_REQUIRED, "Zertifikat und Preshared-Key schliessen sich gegenseitig aus!");
        }
    }

    void validateWanGateway(String wanGateway, Errors errors) {
        if (isNotBlank(wanGateway)) {
            ValidationResult validationResultWanGateway = validateIpAddress(wanGateway);
            if (!validationResultWanGateway.isValid()) {
                errors.rejectValue("wanGateway", ERRCODE_INVALID, "WAN Gateway ist ungueltig! Grund: "
                        + validationResultWanGateway.getMsg());
            }
        }
    }

    void validateLoopbackIp(String loopbackIp, Errors errors) {
        if (isNotBlank(loopbackIp)) {
            ValidationResult validationResultLoopbackIp = validateIpAddress(loopbackIp);
            if (!validationResultLoopbackIp.isValid()) {
                errors.rejectValue("loopbackIp", ERRCODE_INVALID, "Die angegebene Loopback IP ist ungueltig! Grund: "
                        + validationResultLoopbackIp.getMsg());
            }
        }
    }

    void validateLoopbackIpPassive(String loopbackIpPassive, Errors errors) {
        if (isNotBlank(loopbackIpPassive)) {
            ValidationResult validationResultLoopbackIpPassive = validateIpAddress(loopbackIpPassive);
            if (!validationResultLoopbackIpPassive.isValid()) {
                errors.rejectValue(
                        "loopbackIpPassive",
                        ERRCODE_INVALID,
                        "Die angegebene Loopback IP passiv ist ungueltig! Grund: "
                                + validationResultLoopbackIpPassive.getMsg()
                );
            }
        }
    }

    void validateVirtualLanIp(String virtualLanIp, Errors errors) {
        if (isNotBlank(virtualLanIp)) {
            ValidationResult validationResultVirtualLanIp = validateIpAddress(virtualLanIp);
            if (!validationResultVirtualLanIp.isValid()) {
                errors.rejectValue("virtualLanIp", ERRCODE_INVALID, "Virtual LAN IP ist ungueltig! Grund: "
                        + validationResultVirtualLanIp.getMsg());
            }
        }
    }

    void validateVirtualLanToScramble(String virtualLanToScramble, Errors errors) {
        if (isNotBlank(virtualLanToScramble)) {
            ValidationResult validationResultVirtualLan2Scramble = validateIpAddressPlusPrefix(virtualLanToScramble);
            if (!validationResultVirtualLan2Scramble.isValid()) {
                errors.rejectValue(
                        "virtualLan2Scramble",
                        ERRCODE_INVALID,
                        "zu verschlüsselndes Netz ist ungueltig! Grund: "
                                + validationResultVirtualLan2Scramble.getMsg()
                );
            }
        }
    }

    void validateVirtualWanIp(String virtualWanIp, Errors errors) {
        if (isNotBlank(virtualWanIp)) {
            ValidationResult validationResultVirtualWanIp = validateIpAddress(virtualWanIp);
            if (!validationResultVirtualWanIp.isValid()) {
                errors.rejectValue("virtualWanIp", ERRCODE_INVALID, "Virtual WAN IP ist ungueltig! Grund: "
                        + validationResultVirtualWanIp.getMsg());
            }
        }
    }

    /**
     * @see StringUtils#isNotBlank(String).
     */
    boolean isNotBlank(String string) {
        return StringUtils.isNotBlank(string);
    }

    /**
     * prueft die angegebene IP-Adresse in Form eines Strings, ob es eine gueltige Adresse ist. Wenn nicht, wird ein
     * Fehlercode geliefert.
     *
     * @param ipAddress
     * @return
     */
    ValidationResult validateIpAddress(String ipAddress) {
        return IPValidationTool.validateIPV4(ipAddress);
    }

    /**
     * wie {@link #validateIpAddress(String)} nur, dass der angegebene String ein Praefix mitbringen muss.
     *
     * @param ipAddress
     * @return
     */
    ValidationResult validateIpAddressPlusPrefix(String ipAddress) {
        return IPValidationTool.validateIPV4WithPrefix(ipAddress);
    }

    /**
     * @see IPToolsV4#validateNetmask(String).
     */
    boolean validateNetmask(String netmask) {
        return IPToolsV4.instance().validateNetmask(netmask);
    }

} // end
