/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.08.2011 15:42:01
 */
package de.mnet.wita.validators.meldung;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import javax.validation.*;

import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.validators.BasePresenceValidator;
import de.mnet.wita.validators.meldung.LeitungSchleifenwiderstandSet.LeitungSchleifenwiderstandSetValidator;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = LeitungSchleifenwiderstandSetValidator.class)
@Documented
public @interface LeitungSchleifenwiderstandSet {

    String message() default "";

    boolean mandatory() default true;

    boolean permitted() default true;

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class LeitungSchleifenwiderstandSetValidator extends BasePresenceValidator implements
            ConstraintValidator<LeitungSchleifenwiderstandSet, Meldung<?>> {

        @Override
        public boolean isValid(Meldung<?> meldungToValidate, ConstraintValidatorContext context) {
            if (meldungToValidate.getLeitung() == null) {
                return true;
            }

            return checkPresence(context, meldungToValidate.getLeitung().getSchleifenWiderstand());
        }

        @Override
        public void initialize(LeitungSchleifenwiderstandSet constraintAnnotation) {
            permitted = constraintAnnotation.permitted();
            mandatory = constraintAnnotation.mandatory();
            errorMsgIfRequired = "Schleifenwiderstand muss gesetzt sein.";
            errorMsgIfNotAllowed = "Schleifenwiderstand darf nicht gesetzt sein";
        }
    }
}
