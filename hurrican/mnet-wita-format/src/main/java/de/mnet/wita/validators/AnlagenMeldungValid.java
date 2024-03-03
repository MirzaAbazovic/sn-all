/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.07.2011
 */
package de.mnet.wita.validators;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import javax.validation.*;

import de.mnet.wita.message.meldung.Meldung;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = AnlagenMeldungValid.AnlagenMeldungValidator.class)
@Documented
public @interface AnlagenMeldungValid {

    String message() default "";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public class AnlagenMeldungValidator extends BaseAnlagenValidator
            implements ConstraintValidator<AnlagenMeldungValid, Meldung<?>> {

        @Override
        public boolean isValid(Meldung<?> meldung, ConstraintValidatorContext context) {
            return filesizesValid(meldung.getAnlagen(), context);
        }

        @Override
        public void initialize(AnlagenMeldungValid constraintAnnotation) {
            // nothing to do here
        }

    }

}
