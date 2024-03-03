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
import de.mnet.wita.validators.meldung.LeitungAbschnittSet.LeitungAbschnittSetValidator;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = LeitungAbschnittSetValidator.class)
@Documented
public @interface LeitungAbschnittSet {

    String message() default "";

    boolean mandatory() default true;

    boolean permitted() default true;

    int maxOccurrences() default -1;

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class LeitungAbschnittSetValidator extends BasePresenceValidator implements
            ConstraintValidator<LeitungAbschnittSet, Meldung<?>> {

        @Override
        public boolean isValid(Meldung<?> meldungToValidate, ConstraintValidatorContext context) {
            if (meldungToValidate.getLeitung() == null) {
                return true;
            }

            return checkOccurrences(context, meldungToValidate.getLeitung().getLeitungsAbschnitte())
                    && checkPresence(context, meldungToValidate.getLeitung().getMaxBruttoBitrate());
        }

        @Override
        public void initialize(LeitungAbschnittSet constraintAnnotation) {
            permitted = constraintAnnotation.permitted();
            mandatory = constraintAnnotation.mandatory();
            maxOccurrences = constraintAnnotation.maxOccurrences();
            errorMsgIfRequired = "Leitungsabschnitt und MaxBruttoBitRate müssen gesetzt sein.";
            errorMsgIfNotAllowed = "Leitungsabschnitt und MaxBruttoBitRate darf nicht gesetzt sein";
            errorMsgIfMaxOccurrencesExceeded =
                    String.format("Die Anzahl der gesetzten Leitungsabschnitte darf nicht %s ueberschreiten.", maxOccurrences);
        }
    }

}
