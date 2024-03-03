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
import de.mnet.wita.validators.meldung.AufnehmenderProviderSet.AufnehmenderProviderSetValidator;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = AufnehmenderProviderSetValidator.class)
@Documented
public @interface AufnehmenderProviderSet {
    String message() default "";

    boolean mandatory() default true;

    boolean permitted() default true;

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class AufnehmenderProviderSetValidator extends BasePresenceValidator implements
            ConstraintValidator<AufnehmenderProviderSet, Meldung<?>> {

        @Override
        public boolean isValid(Meldung<?> meldungToValidate, ConstraintValidatorContext context) {
            return checkPresence(context, meldungToValidate.getAufnehmenderProvider());
        }

        @Override
        public void initialize(AufnehmenderProviderSet constraintAnnotation) {
            permitted = constraintAnnotation.permitted();
            mandatory = constraintAnnotation.mandatory();
            errorMsgIfRequired = "Der aufnehmende Provider muss gesetzt sein.";
            errorMsgIfNotAllowed = "Der aufnehmende Provider darf nicht gesetzt sein";
        }
    }
}
