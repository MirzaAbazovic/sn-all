package de.mnet.wbci.validation.constraints;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import java.time.format.*;
import javax.validation.*;
import javax.validation.constraints.*;

import de.augustakom.common.tools.lang.DateTools;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.validation.helper.ValidationHelper;

/**
 *
 */
@Target(value = TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = CheckRuemVaTerminBeforeKwt.KundenwunschterminValidator.class)
@Documented
public @interface CheckRuemVaTerminBeforeKwt {

    String message() default "Der angegebene Wechseltermin %s in der Rueckmeldung zur Vorabstimmung (RUEM-VA) liegt vor dem Kundenwunschtermin %s der Vorabstimmung";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public class KundenwunschterminValidator implements
            ConstraintValidator<CheckRuemVaTerminBeforeKwt, RueckmeldungVorabstimmung> {

        protected String defaultMessage;

        @Override
        public void initialize(CheckRuemVaTerminBeforeKwt constraintAnnotation) {
            this.defaultMessage = constraintAnnotation.message();
        }

        @Override
        public boolean isValid(@NotNull RueckmeldungVorabstimmung value, ConstraintValidatorContext context) {
            /***
             * This will be checked with {@link NotNull} at the specific getters.
             * See {@link de.mnet.wbci.model.RueckmeldungVorabstimmung#getWechseltermin()} ,
             * {@link de.mnet.wbci.model.RueckmeldungVorabstimmung#getWbciGeschaeftsfall()},
             * {@link de.mnet.wbci.model.WbciGeschaeftsfall#getKundenwunschtermin()}
             */
            if (value.getWechseltermin() == null
                    || value.getWbciGeschaeftsfall() == null
                    || value.getWbciGeschaeftsfall().getKundenwunschtermin() == null) {
                return true;
            }

            if (!value.getWechseltermin().isBefore(value.getWbciGeschaeftsfall().getKundenwunschtermin())) {
                return true;
            }

            ValidationHelper.addConstraintViolation(context,
                    String.format(defaultMessage,
                            value.getWechseltermin().format(DateTimeFormatter.ofPattern(DateTools.PATTERN_DAY_MONTH_YEAR)),
                            value.getWbciGeschaeftsfall().getKundenwunschtermin().format(DateTimeFormatter.ofPattern(DateTools.PATTERN_DAY_MONTH_YEAR)))
            );
            return false;
        }
    }

}
