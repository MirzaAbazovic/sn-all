/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.07.2011 09:36:34
 */
package de.augustakom.hurrican.validation.cc;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import javax.validation.*;

import de.augustakom.hurrican.validation.cc.LbzValid.LbzValidator;

@Retention(RUNTIME)
@Target({ METHOD, FIELD })
@Constraint(validatedBy = LbzValidator.class)
@Documented
public @interface LbzValid {

    String message() default "Die Leitungsbezeichnung muss dem Schema {leitungsSchluesselZahl}/{onkzKunde}/{onkzKollokation}/{ordnungsNummer} entsprechen.";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class LbzValidator implements ConstraintValidator<LbzValid, String> {

        @Override
        public boolean isValid(String lbz, ConstraintValidatorContext context) {
            if (lbz == null) {
                return true;
            }
            return lbz.replace(" ", "").matches("\\w{1,5}/\\d{2,5}/\\d{2,5}/\\w{1,10}");
        }

        @Override
        public void initialize(LbzValid constraintAnnotation) {
            // nothing to do
        }
    }

}
