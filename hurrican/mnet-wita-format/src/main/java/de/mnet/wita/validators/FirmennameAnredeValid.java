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
import de.mnet.wita.message.common.Firmenname;
import de.mnet.wita.validators.FirmennameAnredeValid.FirmennameAnredeValidator;

@Retention(RUNTIME)
@Target({ ElementType.TYPE })
@Constraint(validatedBy = FirmennameAnredeValidator.class)
@Documented
public @interface FirmennameAnredeValid {

    String message() default "";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class FirmennameAnredeValidator implements
            ConstraintValidator<FirmennameAnredeValid, Firmenname> {

        @Override
        public boolean isValid(Firmenname firmenname, ConstraintValidatorContext context) {
            if (!Anrede.firmaAnreden.contains(firmenname.getAnrede())) {
                return ValidationUtils.addConstraintViolation(context,
                        "Die Anrede der Firma " + Joiner.on(" ").join(
                                firmenname.getAnrede(),
                                firmenname.getErsterTeil() +
                                        firmenname.getZweiterTeil()
                        ) +
                                " ist die Anrede einer Privatperson!"
                );
            }
            if (((firmenname.getErsterTeil() != null) && firmenname.getErsterTeil().contains("\n"))
                    || ((firmenname.getZweiterTeil() != null) && firmenname.getZweiterTeil().contains("\n"))) {
                return ValidationUtils.addConstraintViolation(context,
                        "Firmennamen dürfen keinen Zeilenumbruch enthalten!");
            }
            return true;
        }

        @Override
        public void initialize(FirmennameAnredeValid constraintAnnotation) {
            // nothing to do
        }
    }

}


