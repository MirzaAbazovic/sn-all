/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.10.2011 10:43:38
 */
package de.mnet.wita.validators.ansprechpartner;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import java.util.regex.*;
import javax.validation.*;

import de.mnet.wita.validators.ansprechpartner.TelefonnummerValid.TelefonnummerValidator;

@Retention(RUNTIME)
@Target({ METHOD, FIELD })
@Constraint(validatedBy = TelefonnummerValidator.class)
@Documented
public @interface TelefonnummerValid {

    String message() default "Angegebene Rufnummer des Ansprechpartners ist keine valide Rufnummer!\n"
            + "Beispiele für gültige Rufnummern: <0821 1815678> oder <+49 89 452003461>";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class TelefonnummerValidator implements ConstraintValidator<TelefonnummerValid, String> {

        /**
         * aus oss-type-simple.xsd bei <xsd:simpleType name="TelefonnummerType">
         */
        private static final String DTAG_PHONE_FORMAT = "((\\+\\d{2} |0)\\d{2,5} )?\\d{1,14}";

        @Override
        public boolean isValid(String rufnummer, ConstraintValidatorContext context) {
            return rufnummer == null || isMatchingDtagPhoneFormat(rufnummer);
        }

        public static boolean isMatchingDtagPhoneFormat(String rufnummer) {
            return Pattern.compile(DTAG_PHONE_FORMAT).matcher(rufnummer).matches();
        }

        @Override
        public void initialize(TelefonnummerValid constraintAnnotation) {
            // nothing to do
        }
    }
}
