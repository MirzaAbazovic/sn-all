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
import de.mnet.wita.validators.meldung.AnlagenSet.AnlagenSetValidator;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = AnlagenSetValidator.class)
@Documented
public @interface AnlagenSet {

    String message() default "";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    boolean mandatory() default true;

    boolean permitted() default true;

    int maxOccurrences() default -1;

    int minOccurrences() default 0;

    public static class AnlagenSetValidator extends BasePresenceValidator implements
            ConstraintValidator<AnlagenSet, Meldung<?>> {

        @Override
        public boolean isValid(Meldung<?> meldungToValidate, ConstraintValidatorContext context) {
            return checkOccurrences(context, meldungToValidate.getAnlagen());
        }

        @Override
        public void initialize(AnlagenSet constraintAnnotation) {
            mandatory = constraintAnnotation.mandatory();
            permitted = constraintAnnotation.permitted();
            maxOccurrences = constraintAnnotation.maxOccurrences();
            minOccurrences = constraintAnnotation.minOccurrences();
            errorMsgIfRequired = "Anlagen müssen gesetzt sein.";
            errorMsgIfNotAllowed = "Anlagen darf nicht gesetzt sein";
            errorMsgIfMaxOccurrencesExceeded =
                    String.format("Die Anzahl der gesetzten Anlagen darf nicht %s ueberschreiten.", maxOccurrences);
        }
    }

    /**
     * Defines several <code>@AnlagenSet</code> annotations on the same element
     *
     *
     * @see AnlagenSet
     */
    @Target({ TYPE })
    @Retention(RUNTIME)
    @Documented
    @interface List {
        AnlagenSet[] value();
    }

}
