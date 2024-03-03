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

import de.mnet.wita.message.auftrag.Schaltangaben;
import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.validators.BasePresenceValidator;
import de.mnet.wita.validators.geschaeftsfall.SchaltangabenSet.SchaltangabenSetValidator;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = SchaltangabenSetValidator.class)
@Documented
public @interface SchaltangabenSet {
    String message() default "";

    boolean mandatory() default true;

    boolean permitted() default true;

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class SchaltangabenSetValidator extends BasePresenceValidator implements
            ConstraintValidator<SchaltangabenSet, Geschaeftsfall> {

        @Override
        public boolean isValid(Geschaeftsfall geschaeftsfall, ConstraintValidatorContext context) {
            Schaltangaben schaltangaben = geschaeftsfall.getAuftragsPosition().getGeschaeftsfallProdukt()
                    .getSchaltangaben();
            return checkPresence(context, schaltangaben);
        }

        @Override
        public void initialize(SchaltangabenSet constraintAnnotation) {
            permitted = constraintAnnotation.permitted();
            mandatory = constraintAnnotation.mandatory();
            errorMsgIfRequired = "Die Schaltangaben müssen gesetzt sein.";
            errorMsgIfNotAllowed = "Die Schaltangaben dürfen nicht gesetzt sein";
        }
    }

}
