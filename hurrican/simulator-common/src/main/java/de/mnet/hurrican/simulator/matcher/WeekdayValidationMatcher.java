package de.mnet.hurrican.simulator.matcher;

import java.text.*;
import java.util.*;
import java.util.regex.*;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.ValidationException;
import com.consol.citrus.validation.matcher.ValidationMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Special validation matcher implementation checks that a given date matches an expected weekday.
 * <p/>
 * Control weekday value is one of these strings: MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
 * <p/>
 * In addition to that user can specify the date format to parse: MONDAY(YYYY-MM-DD)
 *
 *
 */
public class WeekdayValidationMatcher implements ValidationMatcher {

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WeekdayValidationMatcher.class);

    @Override
    public void validate(String fieldName, String value, String control, TestContext context) throws ValidationException {
        SimpleDateFormat dateFormat;
        String formatString = "dd.MM.yyyy";
        String weekday = control;

        if (control.contains("(")) {
            formatString = control.substring(control.indexOf('(') + 1, control.length() - 1);
            weekday = control.substring(0, control.indexOf('('));
        }

        try {
            dateFormat = new SimpleDateFormat(formatString);
        }
        catch (PatternSyntaxException e) {
            throw new ValidationException(this.getClass().getSimpleName() + " failed for field '" + fieldName + "' " +
                    ". Found invalid date format", e);
        }

        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateFormat.parse(value));

            if (cal.get(Calendar.DAY_OF_WEEK) == Weekday.valueOf(weekday).getConstantValue()) {
                LOGGER.info("Successful weekday validation matcher - All values OK");
            }
            else {
                throw new ValidationException(this.getClass().getSimpleName() + " failed for field '" + fieldName + "'" +
                        ". Received invalid week day '" + value + "', expected date to be a '" + weekday + "'");
            }
        }
        catch (ParseException e) {
            throw new ValidationException(this.getClass().getSimpleName() + " failed for field '" + fieldName + "'" +
                    ". Received invalid date format for value '" + value + "', expected date format is '" + formatString + "'", e);
        }
    }

    /**
     * Weekday enumeration links names to Java util Calendar constants.
     */
    private static enum Weekday {
        MONDAY(Calendar.MONDAY),
        TUESDAY(Calendar.TUESDAY),
        WEDNESDAY(Calendar.WEDNESDAY),
        THURSDAY(Calendar.THURSDAY),
        FRIDAY(Calendar.FRIDAY),
        SATURDAY(Calendar.SATURDAY),
        SUNDAY(Calendar.SUNDAY);

        private int constantValue;

        Weekday(int constant) {
            this.constantValue = constant;
        }

        public int getConstantValue() {
            return this.constantValue;
        }
    }
}
