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
import de.mnet.wita.validators.meldung.KundenNummerSet.KundenNummerSetValidator;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = KundenNummerSetValidator.class)
@Documented
public @interface KundenNummerSet {
    String message() default "";

    boolean mandatory() default true;

    boolean permitted() default true;

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class KundenNummerSetValidator extends BasePresenceValidator implements
            ConstraintValidator<KundenNummerSet, Meldung<?>> {

        @Override
        public boolean isValid(Meldung<?> meldungToValidate, ConstraintValidatorContext context) {
            return checkPresence(context, meldungToValidate.getKundenNummer());
        }

        @Override
        public void initialize(KundenNummerSet constraintAnnotation) {
            permitted = constraintAnnotation.permitted();
            mandatory = constraintAnnotation.mandatory();
            errorMsgIfRequired = "Die Kundennummer muss gesetzt sein.";
            errorMsgIfNotAllowed = "Die Kundennummer darf nicht gesetzt sein";
        }
    }

}
