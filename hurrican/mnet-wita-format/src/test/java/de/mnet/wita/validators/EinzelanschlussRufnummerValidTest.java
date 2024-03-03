package de.mnet.wita.validators;

import static de.mnet.wita.TestGroups.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import javax.validation.*;
import org.mockito.Mock;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wita.message.common.portierung.EinzelanschlussRufnummer;
import de.mnet.wita.validators.EinzelanschlussRufnummerValid.EinzelanschlussRufnummerValidator;

@Test(groups = UNIT)
public class EinzelanschlussRufnummerValidTest {

    @Mock
    ConstraintValidatorContext context;

    @DataProvider
    public Object[][] testCases() {
        // @formatter:off
        return new Object[][] {
                { "89",          "123456",     true },
                { "12345",       "123456",     true },
                { "89",          "1234567890", false },
                { "82331234981", "1",          false },
                { "123456",      "123456",     false },
        };
        // @formatter:on
    }

    @Test(dataProvider = "testCases")
    public void test(String onkz, String rufnummer, boolean expected) {
        EinzelanschlussRufnummer einzelAnschluss = new EinzelanschlussRufnummer();
        einzelAnschluss.setOnkz(onkz);
        einzelAnschluss.setRufnummer(rufnummer);
        assertThat(new EinzelanschlussRufnummerValidator().isValid(einzelAnschluss, context), equalTo(expected));
    }
}
