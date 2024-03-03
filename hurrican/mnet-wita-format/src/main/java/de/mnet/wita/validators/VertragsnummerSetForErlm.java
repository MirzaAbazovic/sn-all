/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.07.2011 09:36:34
 */
package de.mnet.wita.validators;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import javax.validation.*;

import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.meldung.ErledigtMeldung;
import de.mnet.wita.validators.VertragsnummerSetForErlm.VertragsnummerSetForErlmValidator;

/**
 * Validator fuer Sonderfall: Bei ERLM muss die Vertragsnummer abhaengig von den Aenderungskennzeichen gesetzt sein.
 *
 *
 */
@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = VertragsnummerSetForErlmValidator.class)
@Documented
public @interface VertragsnummerSetForErlm {

    String message() default "";

    boolean mandatory() default true;

    boolean permitted() default true;

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class VertragsnummerSetForErlmValidator extends BasePresenceValidator implements
            ConstraintValidator<VertragsnummerSetForErlm, ErledigtMeldung> {

        @Override
        public boolean isValid(ErledigtMeldung toValidate, ConstraintValidatorContext context) {

            if (GeschaeftsfallTyp.RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG.equals(toValidate.getGeschaeftsfallTyp())) {
                // bei REX-MK benoetigt ERLM keine Vertragsnummer!
                return true;
            }

            switch (toValidate.getAenderungsKennzeichen()) {
                case STANDARD:
                case TERMINVERSCHIEBUNG:
                    String vertragsNummer = toValidate.getVertragsNummer();
                    return checkPresence(context, vertragsNummer);
                default:
                    return true;
            }
        }

        @Override
        public void initialize(VertragsnummerSetForErlm constraintAnnotation) {
            permitted = constraintAnnotation.permitted();
            mandatory = constraintAnnotation.mandatory();
            errorMsgIfRequired = "Die Vertragsnummer muss gesetzt sein.";
            errorMsgIfNotAllowed = "Die Vertragsnummer darf nicht gesetzt sein";
        }
    }

}
