package de.mnet.wita.validators;

import static de.mnet.wita.TestGroups.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import javax.validation.*;
import org.mockito.Mock;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wita.validators.OnkzValid.OnkzValidator;

@Test(groups = UNIT)
public class OnkzValidatorTest {

    @Mock
    ConstraintValidatorContext context;

    @DataProvider
    public Object[][] testCases() {
        // @formatter:off
        return new Object[][] {
                { "", false },
                { "0", false },
                { "089", false },
                { "89", true },
                { "88999", true },
                { "089999", false },
                { "889999", false },
        };
        // @formatter:on
    }

    @Test(dataProvider = "testCases")
    public void test(String onkz, boolean expected) {
        assertThat(new OnkzValidator().isValid(onkz, context), equalTo(expected));

    }
}
