package de.mnet.wbci.validation.constraints;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import java.time.format.*;
import javax.validation.*;
import javax.validation.constraints.*;

import de.augustakom.common.tools.lang.DateTools;
import de.mnet.wbci.model.Erledigtmeldung;
import de.mnet.wbci.validation.helper.ValidationHelper;

/**
 *
 */
@Target(value = TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = CheckErlmTvTermin.WechselterminValidator.class)
@Documented
public @interface CheckErlmTvTermin {

    String message() default "Der Wechseltermin %s in der Erledigtmeldung (ERLM) stimmt nicht mit dem Kundenwunschtermin %s in der Terminverschiebung ueberein";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public class WechselterminValidator implements ConstraintValidator<CheckErlmTvTermin, Erledigtmeldung> {
        protected String defaultMessage;

        @Override
        public void initialize(CheckErlmTvTermin constraintAnnotation) {
            this.defaultMessage = constraintAnnotation.message();
        }

        @Override
        public boolean isValid(@NotNull Erledigtmeldung value, ConstraintValidatorContext context) {
            /***
             * This will be checked with {@link javax.validation.constraints.NotNull} at the specific getters.
             * See {@link de.mnet.wbci.model.Erledigtmeldung#getWechseltermin()} ,
             * {@link de.mnet.wbci.model.RueckmeldungVorabstimmung#getWbciGeschaeftsfall()},
             * {@link de.mnet.wbci.model.WbciGeschaeftsfall#getKundenwunschtermin()} ()}
             */
            if (value.getWechseltermin() == null
                    || value.getWbciGeschaeftsfall() == null
                    || value.getWbciGeschaeftsfall().getKundenwunschtermin() == null
                    || value.getWbciGeschaeftsfall().getKundenwunschtermin()
                    .isEqual(value.getWechseltermin())) {
                return true;
            }

            ValidationHelper.addConstraintViolation(context,
                    String.format(defaultMessage,
                            value.getWechseltermin().format(DateTimeFormatter.ofPattern(DateTools.PATTERN_DAY_MONTH_YEAR)),
                            value.getWbciGeschaeftsfall().getKundenwunschtermin().format(DateTimeFormatter.ofPattern(DateTools.PATTERN_DAY_MONTH_YEAR))
                    )
            );
            return false;
        }
    }

}
