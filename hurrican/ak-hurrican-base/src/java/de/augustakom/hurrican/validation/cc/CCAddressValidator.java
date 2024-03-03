/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.07.2004 09:05:04
 */
package de.augustakom.hurrican.validation.cc;

import static java.util.Arrays.*;
import static org.apache.commons.lang.StringUtils.*;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;

import de.augustakom.common.tools.validation.AbstractValidator;
import de.augustakom.hurrican.model.cc.CCAddress;


/**
 * Validator fuer Objekte des Typs <code>CCAddress</code>
 */
public class CCAddressValidator extends AbstractValidator {

    private static final List<? extends CCAddressValidationStrategy> strategies = asList(
            new CCAddressValidationStrategyAll(),
            new CCAddressValidationStrategyEmail(),
            new CCAddressValidationStrategyWithPostalAddress());

    /**
     * @see org.springframework.validation.Validator#supports(java.lang.Class)
     */
    @Override
    public boolean supports(Class clazz) {
        return CCAddress.class.isAssignableFrom(clazz);
    }

    /**
     * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
     */
    @Override
    public void validate(Object object, Errors errors) {
        CCAddress address = (CCAddress) object;
        strategies.forEach(strategy -> doValidate(strategy, address, errors));
    }

    private void doValidate(CCAddressValidationStrategy ccAddressValidationStrategy, CCAddress address, Errors errors) {
        if (ccAddressValidationStrategy.isResponsible(address)) {
            ccAddressValidationStrategy.validate(address, errors);
        }
    }

    /**
     * Interface for all CCAddressValidation strategies
     */
    private interface CCAddressValidationStrategy {
        /**
         * Validates certain fields on CCAddress and writes errors if necessary.
         *
         * @param address The object to validate.
         * @param errors  The object on which errors are to be registered.
         */
        void validate(CCAddress address, Errors errors);

        /**
         * States if this strategy should validate a given address.
         *
         * @param address The CCAddress object to check.
         * @return True if this strategy should validate the given address, false otherwise.
         */
        boolean isResponsible(CCAddress address);
    }

    /**
     * A ValidationStrategy for addressType Email.
     */
    private static class CCAddressValidationStrategyEmail implements CCAddressValidationStrategy {

        @Override
        public void validate(CCAddress address, Errors errors) {
            if (address.getAddressType() != null && address.getAddressType().equals(CCAddress.ADDRESS_TYPE_EMAIL)
                    && isBlank(address.getEmail())) {
                errors.rejectValue(CCAddress.EMAIL, ERRCODE_REQUIRED, "E-Mail-Adresse ist nicht angegeben.");
            }
        }

        @Override
        public boolean isResponsible(CCAddress address) {
            return CCAddress.ADDRESS_TYPE_EMAIL.equals(address.getAddressType());
        }
    }

    /**
     * A ValidationStrategy for all addressTypes.
     */
    private static class CCAddressValidationStrategyAll implements CCAddressValidationStrategy {

        @Override
        public void validate(CCAddress address, Errors errors) {
            if (address.getAddressType() == null) {
                errors.rejectValue(CCAddress.ADDRESS_TYPE, ERRCODE_REQUIRED, "Adress-Typ ist nicht definiert.");
            }
            if (isBlank(address.getFormatName())) {
                errors.rejectValue(CCAddress.FORMAT_NAME, ERRCODE_REQUIRED, "Format ist nicht definiert.");
            }
        }

        @Override
        public boolean isResponsible(CCAddress address) {
            return true;
        }
    }

    /**
     * A ValidationStrategy for all addressTypes that should have a postal address.
     */
    private static class CCAddressValidationStrategyWithPostalAddress implements CCAddressValidationStrategy {

        @Override
        public void validate(CCAddress address, Errors errors) {
            if (isBlank(address.getOrt())) {
                errors.rejectValue(CCAddress.ORT, ERRCODE_REQUIRED, "Ort ist nicht angegeben.");
            }
            if (isBlank(address.getPlz())) {
                errors.rejectValue(CCAddress.PLZ, ERRCODE_REQUIRED, "PLZ muss eingetragen sein.");
            }
            if (isBlank(address.getLandId())) {
                errors.rejectValue(CCAddress.LAND_ID, ERRCODE_REQUIRED, "Landeskennung ist nicht angegeben.");
            }

            if (StringUtils.isNotBlank(address.getPostfach()) && StringUtils.isNotBlank(address.getStrasse())) {
                errors.rejectValue(CCAddress.POSTFACH, ERRCODE_REQUIRED, "Postfach und Strasse duerfen nicht gleichzeitig gesetzt sein.");
            }
            if (isBlank(address.getPostfach()) && isBlank(address.getStrasse())) {
                errors.rejectValue(CCAddress.POSTFACH, ERRCODE_REQUIRED, "Es muss eine Strasse oder ein Postfach definiert werden..");
            }
        }

        @Override
        public boolean isResponsible(CCAddress address) {
            return !asList(CCAddress.ADDRESS_TYPE_EMAIL, CCAddress.ADDRESS_TYPE_WHOLESALE_FFM_DATA)
                    .contains(address.getAddressType());
        }
    }
}
