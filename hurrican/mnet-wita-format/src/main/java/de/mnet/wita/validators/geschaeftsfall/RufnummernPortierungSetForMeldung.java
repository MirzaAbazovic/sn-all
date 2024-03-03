/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.08.2011 15:06:16
 */
package de.mnet.wita.validators.geschaeftsfall;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import javax.validation.*;
import org.apache.commons.lang.StringUtils;

import de.mnet.wita.message.common.portierung.RufnummernPortierung;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.validators.BasePresenceValidator;
import de.mnet.wita.validators.ValidationUtils;
import de.mnet.wita.validators.geschaeftsfall.RufnummernPortierungSetForMeldung.RufnummernPortierungSetForMeldungValidator;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = RufnummernPortierungSetForMeldungValidator.class)
@Documented
public @interface RufnummernPortierungSetForMeldung {

    String message() default "";

    boolean mandatory() default true;

    boolean permitted() default true;

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class RufnummernPortierungSetForMeldungValidator extends BasePresenceValidator implements
            ConstraintValidator<RufnummernPortierungSetForMeldung, Meldung<?>> {

        @Override
        public boolean isValid(Meldung<?> meldung, ConstraintValidatorContext context) {
            RufnummernPortierung portierung = meldung
                    .getRufnummernPortierung();

            if ((portierung != null) && (portierung.getPortierungsKenner() != null)) {
                ValidationUtils.addConstraintViolation(context,
                        "PortierungsKenner darf an der RufnummernPortierung nicht gesetzt sein!");
            }

            return checkPresence(context, portierung);
        }

        @Override
        public void initialize(RufnummernPortierungSetForMeldung constraintAnnotation) {
            permitted = constraintAnnotation.permitted();
            mandatory = constraintAnnotation.mandatory();
            String message = constraintAnnotation.message();
            if (StringUtils.isNotBlank(message)) {
                errorMsgIfRequired = message;
                errorMsgIfNotAllowed = message;
            }
            else {
                errorMsgIfRequired = "Die RufnummernPortierung muss gesetzt sein.";
                errorMsgIfNotAllowed = "Die RufnummernPortierung darf nicht gesetzt sein";
            }
        }
    }
}
