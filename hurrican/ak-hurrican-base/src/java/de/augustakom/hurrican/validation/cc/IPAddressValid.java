/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.01.2012 10:28:46
 */
package de.augustakom.hurrican.validation.cc;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import javax.validation.*;

import de.augustakom.common.tools.messages.AKWarning;
import de.augustakom.common.tools.net.IPValidationTool;
import de.augustakom.common.tools.net.IPValidationTool.ValidationResult;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.validation.cc.IPAddressValid.IPAddressValidator;

/**
 * Validator fuer Objekte des Typs {@link IPAddress}
 */
@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = IPAddressValidator.class)
@Documented
public @interface IPAddressValid {

    String message() default "Format der IP-Addresse ist fehlerhaft";

    boolean present() default true;

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class IPAddressValidator implements ConstraintValidator<IPAddressValid, IPAddress> {

        @Override
        public void initialize(IPAddressValid constraintAnnotation) {
        }

        @Override
        public boolean isValid(IPAddress ip, ConstraintValidatorContext context) {
            return validate(ip) == null;
        }

        /**
         * @param ip
         * @return null im Erfolgsfall, sonst eine AKWArning mit der entsprechenden Fehlermeldung
         */
        public AKWarning validate(IPAddress ip) {
            if (ip == null) {
                return new AKWarning(this, "Ip-Adresse ist null!");
            }
            String ipAddress = ip.getAddress();
            String prefix = (ip.getPrefixRef() != null) ? ip.getPrefixRef().getAddress() : null;
            ValidationResult result = new ValidationResult.ValidationFailedResult("Ungültige IP-Addresse!");
            switch (ip.getIpType()) {
                case IPV4:
                    result = IPValidationTool.validateIPV4WithoutPrefix(ipAddress);
                    break;
                case IPV4_with_prefixlength:
                    result = IPValidationTool.validateIPV4WithPrefix(ipAddress);
                    break;
                case IPV6_full:
                    result = IPValidationTool.validateIPV6WithoutPrefix(ipAddress);
                    break;
                case IPV6_with_prefixlength:
                    result = IPValidationTool.validateIPV6WithPrefix(ipAddress);
                    break;
                case IPV6_full_eui64:
                    result = IPValidationTool.validateIPV6EUI64(ipAddress);
                    break;
                case IPV6_relative:
                    result = IPValidationTool.validateIPV6Relative(prefix, ipAddress);
                    break;
                case IPV6_relative_eui64:
                    result = IPValidationTool.validateIPV6EUI64Relativ(prefix, ipAddress);
                    break;
                case IPV4_prefix:
                    result = IPValidationTool.validateIPV4Prefix(ipAddress);
                    break;
                case IPV6_prefix:
                    result = IPValidationTool.validateIPV6Prefix(ipAddress);
                    break;
                default:
                    break;
            }

            if (!result.isValid()) {
                return new AKWarning(this, result.getMsg());
            }
            else {
                return null;
            }
        }
    }
}
