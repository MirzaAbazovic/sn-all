/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.07.2011
 */
package de.mnet.wita.validators;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import javax.validation.*;

import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.message.common.Anlage;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = AnlagenGeschaeftsfallValidV4.AnlagenGeschaeftsfallValidatorV4.class)
@Documented
public @interface AnlagenGeschaeftsfallValidV4 {

    /**
     * Flag to indicate whether an anlage of type lageplan is allowed
     */
    boolean lageplanAllowed() default false;

    /**
     * Flag to indicate whether an anlage of type kuendigung is allowed
     */
    boolean kuendigungAllowed() default false;

    String message() default "";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public class AnlagenGeschaeftsfallValidatorV4 extends BaseAnlagenValidator implements ConstraintValidator<AnlagenGeschaeftsfallValidV4, Geschaeftsfall> {

        boolean lageplanAllowed;
        boolean kuendigungAllowed;

        @Override
        public void initialize(AnlagenGeschaeftsfallValidV4 constraintAnnotation) {
            lageplanAllowed = constraintAnnotation.lageplanAllowed();
            kuendigungAllowed = constraintAnnotation.kuendigungAllowed();
        }

        @Override
        public boolean isValid(Geschaeftsfall geschaeftsfall, ConstraintValidatorContext context) {
            boolean anlagenTypesValid = anlagenTypesValid(geschaeftsfall);
            if (!anlagenTypesValid) {
                ValidationUtils.addConstraintViolation(context, "Es dürfen nur erlaubte Anlagentypen verwendet werden");
            }
            boolean fileSizesValid = filesizesValid(geschaeftsfall.getAnlagen(), context);
            return anlagenTypesValid && fileSizesValid;
        }

        private boolean anlagenTypesValid(Geschaeftsfall geschaeftsfall) {
            boolean lageplanFound = false;
            boolean kuendigungFound = false;

            for (Anlage anlage : geschaeftsfall.getAnlagen()) {
                if (anlage.getAnlagentyp() == null) {
                    return false;
                }

                switch (anlage.getAnlagentyp()) {
                    case LAGEPLAN:
                        if (lageplanFound || !lageplanAllowed) {
                            return false;
                        }
                        lageplanFound = true;
                        break;
                    case KUENDIGUNGSSCHREIBEN:
                        if (kuendigungFound || !kuendigungAllowed) {
                            return false;
                        }
                        kuendigungFound = true;
                        break;
                    default:
                }
            }
            return true;
        }
    }

}
