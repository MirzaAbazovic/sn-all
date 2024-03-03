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
import de.mnet.wita.validators.meldung.ExterneAuftragsnummerSet.ExterneAuftragsnummerSetValidator;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = ExterneAuftragsnummerSetValidator.class)
@Documented
public @interface ExterneAuftragsnummerSet {
    String message() default "";

    boolean mandatory() default true;

    boolean permitted() default true;

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class ExterneAuftragsnummerSetValidator extends BasePresenceValidator implements
            ConstraintValidator<ExterneAuftragsnummerSet, Meldung<?>> {

        @Override
        public boolean isValid(Meldung<?> meldungToValidate, ConstraintValidatorContext context) {
            return checkPresence(context, meldungToValidate.getExterneAuftragsnummer());
        }

        @Override
        public void initialize(ExterneAuftragsnummerSet constraintAnnotation) {
            permitted = constraintAnnotation.permitted();
            mandatory = constraintAnnotation.mandatory();
            errorMsgIfRequired = "Die externe Auftragsnummer muss gesetzt sein.";
            errorMsgIfNotAllowed = "Die externe Auftragsnummer darf nicht gesetzt sein";
        }
    }
}
