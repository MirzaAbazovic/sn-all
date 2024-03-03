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
import de.mnet.wita.validators.meldung.ProduktPositionenSet.ProduktPositionenSetValidator;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = ProduktPositionenSetValidator.class)
@Documented
public @interface ProduktPositionenSet {
    String message() default "";

    boolean mandatory() default true;

    boolean permitted() default true;

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    int maxOccurrences() default -1;

    public static class ProduktPositionenSetValidator extends BasePresenceValidator implements
            ConstraintValidator<ProduktPositionenSet, Meldung<?>> {

        @Override
        public boolean isValid(Meldung<?> meldungToValidate, ConstraintValidatorContext context) {
            return checkOccurrences(context, meldungToValidate.getProduktPositionen());
        }

        @Override
        public void initialize(ProduktPositionenSet constraintAnnotation) {
            permitted = constraintAnnotation.permitted();
            mandatory = constraintAnnotation.mandatory();
            maxOccurrences = constraintAnnotation.maxOccurrences();
            errorMsgIfRequired = "ProduktPositionen müssen gesetzt sein.";
            errorMsgIfNotAllowed = "ProduktPositionen dürfen nicht gesetzt sein";
            errorMsgIfMaxOccurrencesExceeded =
                    String.format("Die Anzahl der gesetzten Produktpositionen darf nicht %s ueberschreiten.", maxOccurrences);
        }
    }

    /**
     * Defines several <code>@ProduktPositionenSet</code> annotations on the same element
     *
     *
     * @see ProduktPositionenSet
     */
    @Target({ TYPE })
    @Retention(RUNTIME)
    @Documented
    @interface List {
        ProduktPositionenSet[] value();
    }

}
