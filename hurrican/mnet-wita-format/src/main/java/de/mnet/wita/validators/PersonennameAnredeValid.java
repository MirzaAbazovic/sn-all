/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.10.2011 11:19:12
 */
package de.mnet.wita.validators;

import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import javax.validation.*;
import com.google.common.base.Joiner;

import de.mnet.wita.message.auftrag.Anrede;
import de.mnet.wita.message.common.Personenname;
import de.mnet.wita.validators.PersonennameAnredeValid.PersonennameAnredeValidator;

@Retention(RUNTIME)
@Target({ ElementType.TYPE })
@Constraint(validatedBy = PersonennameAnredeValidator.class)
@Documented
public @interface PersonennameAnredeValid {

    String message() default "";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class PersonennameAnredeValidator implements
            ConstraintValidator<PersonennameAnredeValid, Personenname> {

        @Override
        public boolean isValid(Personenname personenname, ConstraintValidatorContext context) {
            if (!Anrede.personAnreden.contains(personenname.getAnrede())) {
                // null check is required to avoid NPE within the join method call
                return ValidationUtils.addConstraintViolation(context,
                        "Die Anrede der Person " + Joiner.on(" ").join(
                                personenname.getAnrede(),
                                personenname.getVorname() != null ? personenname.getVorname() : "",
                                personenname.getNachname() != null ? personenname.getNachname() : "") +
                                " ist eine Firmenanrede!"
                );
            }
            if (((personenname.getVorname() != null) && personenname.getVorname().contains("\n"))
                    || ((personenname.getNachname() != null) && personenname.getNachname().contains("\n"))) {
                return ValidationUtils.addConstraintViolation(context, "Namen dürfen keinen Zeilenumbruch enthalten!");
            }
            return true;
        }

        @Override
        public void initialize(PersonennameAnredeValid constraintAnnotation) {
            // nothing to do
        }
    }

}
