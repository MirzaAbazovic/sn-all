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

import de.mnet.wita.message.auftrag.Kundenwunschtermin.Zeitfenster;
import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.validators.BasePresenceValidator;
import de.mnet.wita.validators.geschaeftsfall.ZeitfensterSet.ZeitfensterSetValidator;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = ZeitfensterSetValidator.class)
@Documented
public @interface ZeitfensterSet {
    String message() default "";

    boolean mandatory() default true;

    boolean permitted() default true;

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class ZeitfensterSetValidator extends BasePresenceValidator implements
            ConstraintValidator<ZeitfensterSet, Geschaeftsfall> {

        @Override
        public boolean isValid(Geschaeftsfall geschaeftsfall, ConstraintValidatorContext context) {
            Zeitfenster zeitfenster = geschaeftsfall.getKundenwunschtermin().getZeitfenster();
            return checkPresence(context, zeitfenster);
        }

        @Override
        public void initialize(ZeitfensterSet constraintAnnotation) {
            permitted = constraintAnnotation.permitted();
            mandatory = constraintAnnotation.mandatory();
            errorMsgIfRequired = "Das Zeitfenster muss gesetzt sein.";
            errorMsgIfNotAllowed = "Das Zeitfenster darf nicht gesetzt sein.";
        }
    }

    /**
     * Defines several <code>@ZeitfensterSet</code> annotations on the same element
     *
     *
     * @see ZeitfensterSet
     */
    @Target({ TYPE })
    @Retention(RUNTIME)
    @Documented
    @interface List {
        ZeitfensterSet[] value();
    }

}
