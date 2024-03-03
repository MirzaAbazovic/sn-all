/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.10.13
 */
package de.mnet.wbci.validation.constraints;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;
import static org.apache.commons.lang.StringUtils.*;

import java.lang.annotation.*;
import javax.validation.*;

import de.mnet.wbci.model.Abbruchmeldung;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungPosition;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.validation.helper.ValidationHelper;

/**
 * Checks if the added {@link de.mnet.wbci.model.MeldungsCode} is valid for the {@link
 * de.mnet.wbci.model.MeldungPosition}.
 */
@Target(value = TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = CheckOutgoingAbbmBegruendung.AbbmValidator.class)
@Documented
public @interface CheckOutgoingAbbmBegruendung {

    String message() default "Ist eine Begründung angegeben, muss auch der Meldungscode %s (%s) gesetzt werden";

    String messageMcSet() default "Ist der Meldungscode %s (%s) gesetzt , muss auch eine Begründung angegeben werden";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public class AbbmValidator implements ConstraintValidator<CheckOutgoingAbbmBegruendung, Abbruchmeldung> {
        protected String defaultMsg;
        protected String msgMcSet;

        @Override
        public void initialize(CheckOutgoingAbbmBegruendung constraintAnnotation) {
            defaultMsg = constraintAnnotation.message();
            msgMcSet = constraintAnnotation.messageMcSet();
        }

        @Override
        public boolean isValid(Abbruchmeldung value, ConstraintValidatorContext context) {
            //validate ABBM begruendung only for outgoing messages
            if (IOType.IN.equals(value.getIoType())) {
                return true;
            }
            boolean containsMcSontiges = containsMeldungsCodeSonstiges(value);
            boolean containsOtherValidMc = containsValidMeldungsCodeForBegruendung(value);
            boolean begruendungSet = value.getBegruendung() != null && !isEmpty(value.getBegruendung().trim());

            if ((begruendungSet && (containsMcSontiges || containsOtherValidMc))
                    || (!containsMcSontiges && !begruendungSet)) {
                return true;
            }
            ValidationHelper.addConstraintViolation(context,
                    String.format(
                            begruendungSet ? defaultMsg : msgMcSet,
                            MeldungsCode.SONST.name(),
                            MeldungsCode.SONST.getStandardText()
                    )
            );
            return false;
        }

        private boolean containsMeldungsCodeSonstiges(Abbruchmeldung value) {
            if (value.getMeldungsPositionen() != null) {
                for (MeldungPosition pos : value.getMeldungsPositionen()) {
                    if (MeldungsCode.SONST.equals(pos.getMeldungsCode())) {
                        return true;
                    }
                }
            }
            return false;
        }

        /**
         * Returns true if the meldungsPositionen contains a other valid Meldungscode for adding a Begruendung. Actually
         * these are {@link de.mnet.wbci.model.MeldungsCode#TV_ABG} or {@link de.mnet.wbci.model.MeldungsCode#STORNO_ABG}
         *
         * @param value
         * @return
         */
        private boolean containsValidMeldungsCodeForBegruendung(Abbruchmeldung value) {
            if (value.getMeldungsPositionen() != null) {
                for (MeldungPosition pos : value.getMeldungsPositionen()) {
                    if (MeldungsCode.TV_ABG.equals(pos.getMeldungsCode())
                            || MeldungsCode.STORNO_ABG.equals(pos.getMeldungsCode())
                            || MeldungsCode.BVID.equals(pos.getMeldungsCode())) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
