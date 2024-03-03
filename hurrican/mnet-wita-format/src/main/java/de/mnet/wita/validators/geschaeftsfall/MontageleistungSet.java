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

import de.mnet.wita.message.auftrag.Montageleistung;
import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.validators.BasePresenceValidator;
import de.mnet.wita.validators.geschaeftsfall.MontageleistungSet.MontageleistungSetValidator;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = MontageleistungSetValidator.class)
@Documented
public @interface MontageleistungSet {
    String message() default "";

    boolean mandatory() default true;

    boolean permitted() default true;

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class MontageleistungSetValidator extends BasePresenceValidator implements
            ConstraintValidator<MontageleistungSet, Geschaeftsfall> {

        @Override
        public boolean isValid(Geschaeftsfall geschaeftsfall, ConstraintValidatorContext context) {
            Montageleistung montageleistung = geschaeftsfall.getAuftragsPosition().getGeschaeftsfallProdukt()
                    .getMontageleistung();
            return checkPresence(context, montageleistung);
        }

        @Override
        public void initialize(MontageleistungSet constraintAnnotation) {
            permitted = constraintAnnotation.permitted();
            mandatory = constraintAnnotation.mandatory();
            errorMsgIfRequired = "Die Montageleistung muss gesetzt sein.";
            errorMsgIfNotAllowed = "Die Montageleistung darf nicht gesetzt sein";
        }
    }

}
