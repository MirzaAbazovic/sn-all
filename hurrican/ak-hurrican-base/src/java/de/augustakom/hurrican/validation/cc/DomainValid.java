/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.01.2012 15:05:07
 */
package de.augustakom.hurrican.validation.cc;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import java.util.regex.*;
import javax.validation.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.messages.AKWarning;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.MVSService;
import de.augustakom.hurrican.validation.cc.DomainValid.DomainValidator;

/**
 * Validierung fuer eine Domain.
 */
@Retention(RUNTIME)
@Target({ METHOD, FIELD })
@Constraint(validatedBy = DomainValidator.class)
@Documented
public @interface DomainValid {

    String message() default "Die Domain darf maximal 150 Zeichen beinhalten. Es dürfen darüberhinaus keine " +
            "Sonderzeichen, Leerzeichen oder Umlaute enhalten sein. Es muss zudem mindestens einen '.' enthalten sein.";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class DomainValidator implements ConstraintValidator<DomainValid, String> {

        private MVSService mvsService;

        public DomainValidator(MVSService mvsService) {
            this.mvsService = mvsService;
        }

        /**
         * @return Returns the mvsService.
         */
        protected MVSService getMvsService() {
            return mvsService;
        }

        // Regular expression strings for hostnames (derived from RFC2396 and RFC 1123)
        protected static final String LOWER_OR_DIGIT_REGEX = "[\\p{Lower}\\p{Digit}]";
        protected static final String DOMAIN_LABEL_REGEX = LOWER_OR_DIGIT_REGEX + "(?>[" + LOWER_OR_DIGIT_REGEX + "-]*" + LOWER_OR_DIGIT_REGEX + ")*";
        protected static final String TOP_LABEL_REGEX = LOWER_OR_DIGIT_REGEX + "{2,}";
        protected static final String DOMAIN_NAME_REGEX = "^(?:" + DOMAIN_LABEL_REGEX + "\\.)+" + "(" + TOP_LABEL_REGEX
                + ")$";
        protected static final Pattern DOMAIN_PATTERN = Pattern.compile(DOMAIN_NAME_REGEX);

        /**
         * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
         */
        @Override
        public void initialize(DomainValid constraintAnnotation) {
        }

        /**
         * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
         */
        @Override
        public boolean isValid(String domain, ConstraintValidatorContext context) {
            try {
                return validate(domain) == null;
            }
            catch (FindException e) {
                return false;
            }
        }

        private static final String NOT_SET = "Domain ist nicht gesetzt!";
        private static final String BIGGER_THAN_IT_SHOULD_BE = "Domain ist länger als die erlaubten 150 Zeichen!";
        private static final String PATTERN_MISSMATCH = "Die Domain darf maximal 150 Zeichen beinhalten. Es dürfen "
                + "darüberhinaus keine Sonderzeichen, Leerzeichen, Umlaute oder Großbuchstaben enhalten sein. Sie muss zudem mindestens "
                + "einen '.' enthalten. Die Top-Level Domain (alles hinter dem letzten Punkt) muss mindestens zwei Zeichen beinhalten.";

        /**
         * @return Returns the notSet.
         */
        protected String getNotSet() {
            return NOT_SET;
        }

        /**
         * @return Returns the biggerThanItShouldBe.
         */
        protected String getBiggerThanItShouldBe() {
            return BIGGER_THAN_IT_SHOULD_BE;
        }

        /**
         * @return Returns the patternMissmatch.
         */
        protected String getPatternMissmatch() {
            return PATTERN_MISSMATCH;
        }

        /**
         * @return Returns the domainPattern.
         */
        protected Pattern getDomainPattern() {
            return DOMAIN_PATTERN;
        }

        public AKWarning validate(String domain) throws FindException {
            AKWarning warning = null;

            try {
                checkNotSet(domain);
                checkBiggerThanAllowed(domain, 150);
                checkIsValidDomain(domain);
                checkDomainAlreadyUsed(domain);
            }
            catch (IllegalArgumentException e) {
                warning = new AKWarning(this, e.getMessage());
            }
            return warning;
        }

        protected void checkDomainAlreadyUsed(String domain) throws FindException {
            if (getMvsService().isDomainAlreadyUsed(domain)) {
                Long ccAuftragId = mvsService.findAuftragIdByEnterpriseDomain(domain);
                throw new IllegalArgumentException(String.format("Die angegebene Domain (%s) existiert bereits für den Auftrag mit Id %d!",
                        domain, ccAuftragId));
            }
        }

        protected void checkBiggerThanAllowed(String domain, int maxSize) {
            if (StringUtils.length(domain) > maxSize) {
                throw new IllegalArgumentException(getBiggerThanItShouldBe());
            }
        }

        protected void checkNotSet(String domain) {
            if (StringUtils.isBlank(domain)) {
                throw new IllegalArgumentException(getNotSet());
            }
        }

        protected void checkIsValidDomain(String domain) {
            boolean result = getDomainPattern().matcher(domain).matches();
            if (!result) {
                throw new IllegalArgumentException(getPatternMissmatch());
            }
        }

    } // end

} // end
