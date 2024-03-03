package de.mnet.wbci.validation.constraints;

import static de.mnet.wbci.model.GeschaeftsfallTyp.*;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import java.util.*;

import javax.validation.*;
import javax.validation.constraints.*;

import org.apache.commons.collections.CollectionUtils;

import de.mnet.wbci.helper.RufnummerHelper;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.Rufnummernportierung;
import de.mnet.wbci.model.RufnummernportierungAnlage;
import de.mnet.wbci.model.RufnummernportierungAware;
import de.mnet.wbci.model.RufnummernportierungEinzeln;
import de.mnet.wbci.model.RufnummernportierungTyp;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.validation.helper.ValidationHelper;

/**
 *
 */
@Target(value = TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = CheckRuemVaRufnummer.RufnummernValidator.class)
@Documented
public @interface CheckRuemVaRufnummer {

    String message() default "";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public class RufnummernValidator implements
            ConstraintValidator<CheckRuemVaRufnummer, RueckmeldungVorabstimmung> {

        protected static final String NO_SINGLE_MATCHING_RNR = "Keine, der innerhalb der RUEM-VA angegebenen " +
                "Rufnummern, ist in der Vorabstimmung enthalten";
        protected static final String RUFNUMMERN_PORTIERUNGS_TYP = "Der Rufnummerportierungstyp in der RUEM-VA " +
                "stimmt nicht mit dem Rufnummerportierungstyp in der Vorabstimmung überein";
        protected static final String RNR_NOT_EQUAL = "Die angegebenen Rufnummern innerhalb der RUEM-VA %s  " +
                "stimmen nicht mit den angefragten Rufnummern der Vorabstimmung %s überein (\"Alle Nummern der Anschlüsse portieren\" ist nicht ausgewählt)";
        protected static final String RNR_MISSING = "Mindestens eine der angefragten Rufnummern aus der Vorabstimmung " +
                "muss in der RUEM-VA bestätigt werden (\"Alle Nummern der Anschlüsse portieren\" ist ausgewählt)";
        protected String defaultMessage;

        @Override
        public void initialize(CheckRuemVaRufnummer constraintAnnotation) {
            this.defaultMessage = constraintAnnotation.message();
        }

        @Override
        public boolean isValid(@NotNull RueckmeldungVorabstimmung ruemVa, ConstraintValidatorContext context) {
            final WbciGeschaeftsfall wbciGeschaeftsfall = ruemVa.getWbciGeschaeftsfall();
            if (wbciGeschaeftsfall != null) {
                final GeschaeftsfallTyp typ = wbciGeschaeftsfall.getTyp();
                if (VA_KUE_MRN.equals(typ) || VA_RRNP.equals(typ)) {
                    final Rufnummernportierung rnpGf =
                            ((RufnummernportierungAware) wbciGeschaeftsfall).getRufnummernportierung();
                    final Rufnummernportierung rnpRuemVa = ruemVa.getRufnummernportierung();
                    if (rnpGf == null || rnpRuemVa == null) {
                        return true;
                    }
                    if (rnpGf.getTyp() != rnpRuemVa.getTyp()) {
                        ValidationHelper.addConstraintViolation(context, RUFNUMMERN_PORTIERUNGS_TYP);
                        return false;
                    }

                    //Check RUEM-VA numbers
                    Set<String> wbciDnsGf;
                    Set<String> wbciDnsRuemVa;
                    Boolean alleRufnummern = null;

                    //Normalize Numbers
                    if (RufnummernportierungTyp.EINZEL.equals(rnpGf.getTyp())) {
                        alleRufnummern = ((RufnummernportierungEinzeln) rnpGf).getAlleRufnummernPortieren();
                        wbciDnsGf = RufnummerHelper.convertWbciEinzelrufnummer((RufnummernportierungEinzeln) rnpGf);
                        wbciDnsRuemVa = RufnummerHelper.convertWbciEinzelrufnummer((RufnummernportierungEinzeln) rnpRuemVa);
                    }
                    else {
                        wbciDnsGf = RufnummerHelper.convertWbciRufnummerAnlage((RufnummernportierungAnlage) rnpGf);
                        wbciDnsRuemVa = RufnummerHelper.convertWbciRufnummerAnlage((RufnummernportierungAnlage) rnpRuemVa);
                    }

                    //if one check fails return false,
                    //else true (all checks passed)
                    if (!checkAtLeastOneRufnummerMatch(wbciDnsGf, wbciDnsRuemVa, context)
                            || !checkRufnummerMatchCompletely(wbciDnsGf, wbciDnsRuemVa, alleRufnummern, context)) {
                        return false;
                    }
                }
            }
            return true;
        }

        //@formatter:off
        /**
         * Checks if at least one Rufnummer is matching in the RUEM-VA from the requested Rufnummern in the VA
         *
         * @return false if the check is unsuccessful.
         */
        private boolean checkAtLeastOneRufnummerMatch(Set<String> wbciDnsGf, Set<String> wbciDnsRuemVa, ConstraintValidatorContext context) {
            if (Collections.disjoint(wbciDnsGf, wbciDnsRuemVa)) {
                ValidationHelper.addConstraintViolation(context, NO_SINGLE_MATCHING_RNR);
                return false;
            }
            return true;
        }

        /**
         * Checks that requested and confirmed phone numbers are acceptable in case of the following logic:
         * <ul>
         *     <li>
         *       <b>"alle Rufnummern protieren" enabled</b>
         *       <br>=> At least one requested number has to be in the confirmed numbers. Additional numbers will be accepted.</br>
         *     </li>
         *     <li>
         *       <b>"alle Rufnummern protieren" disabled</b>
         *       <br>=> All requested number have to be in the confirmed  numbers. Additional numbers won't be accepted.</br>
         *     </li>
         * </ul>
         *
         * @param wbciDnsGf requested phone numbers of the {@link Rufnummernportierung} ({@link VorabstimmungsAnfrage})
         * @param wbciDnsRuemVa confirmed phone numbers of the {@link Rufnummernportierung} ({@link RueckmeldungVorabstimmung})
         * @param alleRufnummern boolean value for "alle Rufnummern protieren"
         * @param context of the bean validator
         * @return true if the check is ok
         */
        //@formatter:on
        private boolean checkRufnummerMatchCompletely(Set<String> wbciDnsGf, Set<String> wbciDnsRuemVa, Boolean alleRufnummern, ConstraintValidatorContext context) {
            if (Boolean.TRUE.equals(alleRufnummern)) {
                if (!CollectionUtils.containsAny(wbciDnsGf, wbciDnsRuemVa)) {
                    ValidationHelper.addConstraintViolation(context, RNR_MISSING);
                    return false;
                }
            }
            else {
                //all numbers must be equal
                if (!CollectionUtils.isEqualCollection(wbciDnsGf, wbciDnsRuemVa)) {
                    ValidationHelper.addConstraintViolation(context,
                            String.format(RNR_NOT_EQUAL, wbciDnsRuemVa, wbciDnsGf));
                    return false;
                }
            }
            return true;
        }
    }

}
