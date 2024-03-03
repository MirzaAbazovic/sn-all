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

import de.mnet.wita.message.auftrag.AktionsCode;
import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.validators.BasePresenceValidator;
import de.mnet.wita.validators.geschaeftsfall.AktionsCodeSet.AktionsCodeSetValidator;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = AktionsCodeSetValidator.class)
@Documented
public @interface AktionsCodeSet {
    String message() default "";

    boolean mandatory() default true;

    boolean permitted() default true;

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class AktionsCodeSetValidator extends BasePresenceValidator implements
            ConstraintValidator<AktionsCodeSet, Geschaeftsfall> {

        @Override
        public boolean isValid(Geschaeftsfall geschaeftsfall, ConstraintValidatorContext context) {
            AktionsCode aktionsCode = geschaeftsfall.getAuftragsPosition().getAktionsCode();
            return checkPresence(context, aktionsCode);
        }

        @Override
        public void initialize(AktionsCodeSet constraintAnnotation) {
            permitted = constraintAnnotation.permitted();
            mandatory = constraintAnnotation.mandatory();
            errorMsgIfRequired = "Der Aktionscode muss gesetzt sein.";
            errorMsgIfNotAllowed = "Der Aktionscode darf nicht gesetzt sein";
        }
    }

}
