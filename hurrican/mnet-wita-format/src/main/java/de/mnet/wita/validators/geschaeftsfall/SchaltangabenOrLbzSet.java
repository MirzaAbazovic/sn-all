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

import de.mnet.wita.message.auftrag.GeschaeftsfallProdukt;
import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.validators.geschaeftsfall.SchaltangabenOrLbzSet.SchaltangabenOrLBZSetValidator;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = SchaltangabenOrLBZSetValidator.class)
@Documented
public @interface SchaltangabenOrLbzSet {
    String message() default "Weder Schaltangaben noch Leitungsbezeichnung vorhanden.";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class SchaltangabenOrLBZSetValidator implements
            ConstraintValidator<SchaltangabenOrLbzSet, Geschaeftsfall> {

        @Override
        public boolean isValid(Geschaeftsfall geschaeftsfall, ConstraintValidatorContext context) {
            GeschaeftsfallProdukt gp = geschaeftsfall.getAuftragsPosition().getGeschaeftsfallProdukt();
            return (gp.getLeitungsBezeichnung() != null) || (gp.getSchaltangaben() != null);
        }

        @Override
        public void initialize(SchaltangabenOrLbzSet constraintAnnotation) {
            // nothing to do
        }
    }

}
