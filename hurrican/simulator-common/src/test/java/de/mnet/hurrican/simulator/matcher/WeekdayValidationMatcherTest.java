package de.mnet.hurrican.simulator.matcher;

import java.text.*;
import java.util.*;
import com.consol.citrus.exceptions.ValidationException;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class WeekdayValidationMatcherTest {

    @Test
    public void testWeekdayValidationMatcher() {
        WeekdayValidationMatcher matcher = new WeekdayValidationMatcher();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        Calendar calendar = Calendar.getInstance();
        calendar.setWeekDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.WEEK_OF_YEAR), Calendar.SUNDAY);

        matcher.validate("test", dateFormat.format(calendar.getTime()), "SUNDAY", null);

        calendar.setWeekDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.WEEK_OF_YEAR), Calendar.WEDNESDAY);

        matcher.validate("test", dateFormat.format(calendar.getTime()), "WEDNESDAY", null);
    }

    @Test
    public void testCustomDateFormat() {
        WeekdayValidationMatcher matcher = new WeekdayValidationMatcher();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Calendar calendar = Calendar.getInstance();
        calendar.setWeekDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.WEEK_OF_YEAR), Calendar.MONDAY);

        matcher.validate("test", dateFormat.format(calendar.getTime()), "MONDAY(yyyy-MM-dd)", null);

        calendar.setWeekDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.WEEK_OF_YEAR), Calendar.SATURDAY);

        matcher.validate("test", dateFormat.format(calendar.getTime()), "SATURDAY(yyyy-MM-dd)", null);
    }

    @Test(expectedExceptions = { ValidationException.class })
    public void testUnexpectedWeekday() {
        WeekdayValidationMatcher matcher = new WeekdayValidationMatcher();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        Calendar calendar = Calendar.getInstance();
        calendar.setWeekDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.WEEK_OF_YEAR), Calendar.MONDAY);

        matcher.validate("test", dateFormat.format(calendar.getTime()), "TUESDAY", null);
    }

    @Test(expectedExceptions = { ValidationException.class })
    public void testUnexpectedDateFormat() {
        WeekdayValidationMatcher matcher = new WeekdayValidationMatcher();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        Calendar calendar = Calendar.getInstance();
        calendar.setWeekDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.WEEK_OF_YEAR), Calendar.MONDAY);

        matcher.validate("test", dateFormat.format(calendar.getTime()), "MONDAY(yyyy-MM-dd)", null);
    }
}
