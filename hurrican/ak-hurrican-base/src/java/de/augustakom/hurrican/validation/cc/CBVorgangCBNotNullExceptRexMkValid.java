package de.augustakom.hurrican.validation.cc;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import javax.validation.*;

import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.validation.cc.CBVorgangCBNotNullExceptRexMkValid.CBVorgangCBNotNullExceptRexMkValidator;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = CBVorgangCBNotNullExceptRexMkValidator.class)
@Documented
public @interface CBVorgangCBNotNullExceptRexMkValid {

    String message() default "Wenn der Geschaeftsfalltyp nicht REX-MK ist, muss die Carrierbestellung gesetzt sein !";

    boolean present() default true;

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };


    public static class CBVorgangCBNotNullExceptRexMkValidator implements
            ConstraintValidator<CBVorgangCBNotNullExceptRexMkValid, CBVorgang> {

        @Override
        public boolean isValid(CBVorgang cbVorgang, ConstraintValidatorContext context) {
            if (!cbVorgang.isRexMk() && cbVorgang.getCbId() == null) {
                return false;
            }
            return true;
        }

        @Override
        public void initialize(CBVorgangCBNotNullExceptRexMkValid constraintAnnotation) {
            // not used
        }

    }

}
