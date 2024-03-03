/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.10.13
 */
package de.mnet.wbci.validation.constraints;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;
import static org.apache.commons.lang.StringUtils.*;

import java.lang.annotation.*;
import javax.validation.*;
import javax.validation.constraints.*;

import de.mnet.wbci.model.TechnischeRessource;
import de.mnet.wbci.validation.helper.ValidationHelper;

/**
 * Checks if ether the field "WITA-Vertragsnummer" or "WBCI-Line-ID" have been set.
 */
@Target(value = TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = CheckTechnischeRessourceMandatoryFields.MandatoryFieldsValidator.class)
@Documented
public @interface CheckTechnischeRessourceMandatoryFields {

    String message() default "Bei der technischen Ressource muss eines der Felder 'vertragsnummer' (WITA-Vertragsnr.), 'lineId' (WBCI-Line-Id) oder 'identifizierer' (des EKP) angegeben werden";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public class MandatoryFieldsValidator implements
            ConstraintValidator<CheckTechnischeRessourceMandatoryFields, TechnischeRessource> {

        protected String defaultMessage;

        @Override
        public void initialize(CheckTechnischeRessourceMandatoryFields constraintAnnotation) {
            this.defaultMessage = constraintAnnotation.message();
        }

        @Override
        public boolean isValid(@NotNull TechnischeRessource technischeRessource, ConstraintValidatorContext context) {
            boolean containsLineId = !isEmpty(technischeRessource.getLineId());
            boolean containsWitaVtrNr = !isEmpty(technischeRessource.getVertragsnummer());
            boolean containsIdentifier = !isEmpty(technischeRessource.getIdentifizierer());

            if ((containsLineId && containsWitaVtrNr) || (!containsLineId && !containsWitaVtrNr && !containsIdentifier)) {
                ValidationHelper.addConstraintViolation(context, defaultMessage);
                return false;
            }
            return true;
        }
    }

}
