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

import de.mnet.wita.message.auftrag.BestandsSuche;
import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.validators.geschaeftsfall.VertragsnummerOrBestandsSucheSet.VertragsnummerOrBestandsSucheSetValidator;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = VertragsnummerOrBestandsSucheSetValidator.class)
@Documented
public @interface VertragsnummerOrBestandsSucheSet {
    String message() default "Die Vertragsnummer oder die Bestandssuche muss gesetzt sein.";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class VertragsnummerOrBestandsSucheSetValidator implements
            ConstraintValidator<VertragsnummerOrBestandsSucheSet, Geschaeftsfall> {

        @Override
        public boolean isValid(Geschaeftsfall geschaeftsfall, ConstraintValidatorContext context) {
            String vertragsNummer = geschaeftsfall.getVertragsNummer();
            BestandsSuche bestandsSuche = geschaeftsfall.getAuftragsPosition().getGeschaeftsfallProdukt()
                    .getBestandsSuche();
            return ((vertragsNummer != null) || (bestandsSuche != null));
        }

        @Override
        public void initialize(VertragsnummerOrBestandsSucheSet constraintAnnotation) {
            // nothing to do
        }
    }

}
