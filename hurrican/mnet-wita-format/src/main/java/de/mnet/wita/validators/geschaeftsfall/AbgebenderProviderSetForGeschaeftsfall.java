/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.07.2011 09:36:34
 */
package de.mnet.wita.validators.geschaeftsfall;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import javax.validation.*;

import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.message.common.Carrier;
import de.mnet.wita.validators.BasePresenceValidator;
import de.mnet.wita.validators.geschaeftsfall.AbgebenderProviderSetForGeschaeftsfall.AbgebenderProviderSetValidator;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = AbgebenderProviderSetValidator.class)
@Documented
public @interface AbgebenderProviderSetForGeschaeftsfall {

    String message() default "";

    boolean mandatory() default true;

    boolean permitted() default true;

    boolean checkCarrier() default true;

    Carrier expectedCarrier() default Carrier.DTAG;

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class AbgebenderProviderSetValidator extends BasePresenceValidator implements
            ConstraintValidator<AbgebenderProviderSetForGeschaeftsfall, Geschaeftsfall> {

        private boolean checkCarrier;
        private Carrier expectedCarrier;

        @Override
        public boolean isValid(Geschaeftsfall geschaeftsfall, ConstraintValidatorContext context) {
            Carrier carrier = geschaeftsfall.getAbgebenderProvider();

            boolean valid = checkPresence(context, carrier);

            if (permitted && valid && checkCarrier && (carrier != expectedCarrier)) {
                return notValid(context, String.format(
                        "erwarteter Carrier %s entspricht nicht dem angegebenen Carrier %s", expectedCarrier, carrier));
            }
            return valid;
        }

        @Override
        public void initialize(AbgebenderProviderSetForGeschaeftsfall constraintAnnotation) {
            checkCarrier = constraintAnnotation.checkCarrier();
            expectedCarrier = constraintAnnotation.expectedCarrier();
            mandatory = constraintAnnotation.mandatory();
            permitted = constraintAnnotation.permitted();
            errorMsgIfRequired = "Der abgebende Provider muss korrekt gesetzt sein.";
            errorMsgIfNotAllowed = "Der abgebende Provider darf nicht gesetzt sein";
        }
    }

}
