/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.10.13
 */
package de.mnet.wbci.validation.constraints;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import javax.validation.*;

import de.mnet.wbci.model.Abbruchmeldung;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.validation.helper.ValidationHelper;

/**
 * Checks the {@link MeldungsCode}s are valid for the ABBM.
 */
@Target(value = TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = CheckOutgoingAbbmMeldungsCodes.AbbmValidator.class)
@Documented
public @interface CheckOutgoingAbbmMeldungsCodes {

    String message() default "Eine ABBM, die nur ADF MeldungsCodes beinhaltet, ist nicht erlaubt! In diesem Fall ist eine RUEM-VA mit ADA MeldungsCodes zu verwenden";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public class AbbmValidator implements ConstraintValidator<CheckOutgoingAbbmMeldungsCodes, Abbruchmeldung> {
        protected String defaultMsg;

        @Override
        public void initialize(CheckOutgoingAbbmMeldungsCodes constraintAnnotation) {
            defaultMsg = constraintAnnotation.message();
        }

        @Override
        public boolean isValid(Abbruchmeldung value, ConstraintValidatorContext context) {
            //validate ABBM MeldungsCodes for outgoing messages
            if (IOType.IN.equals(value.getIoType()) || !MeldungTyp.ABBM.equals(value.getTyp())) {
                return true;
            }

            // verify that ABBM contains at least one non-ADF code.
            if(!value.isMeldungWithOnlyADFCodes()) {
                return true;
            }

            ValidationHelper.addConstraintViolation(context,defaultMsg);
            return false;
        }
    }
}
