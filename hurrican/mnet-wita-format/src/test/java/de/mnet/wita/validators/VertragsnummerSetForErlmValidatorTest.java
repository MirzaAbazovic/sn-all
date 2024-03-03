package de.mnet.wita.validators;

import static de.mnet.wita.TestGroups.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import javax.validation.*;
import javax.validation.ConstraintValidatorContext.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.builder.meldung.ErledigtMeldungBuilder;
import de.mnet.wita.message.meldung.ErledigtMeldung;
import de.mnet.wita.message.meldung.position.AenderungsKennzeichen;
import de.mnet.wita.validators.VertragsnummerSetForErlm.VertragsnummerSetForErlmValidator;

@Test(groups = UNIT)
public class VertragsnummerSetForErlmValidatorTest extends AbstractValidationTest<ErledigtMeldung> {

    @Mock
    private
    ConstraintValidatorContext context;

    @BeforeTest
    public void setupTest() {
        MockitoAnnotations.initMocks(this);

        ConstraintViolationBuilder builder = mock(ConstraintViolationBuilder.class);
        context = mock(ConstraintValidatorContext.class);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
    }

    @DataProvider
    public Object[][] testCases() {
        ErledigtMeldung erlmStdOk = new ErledigtMeldungBuilder().withAenderungsKennzeichen(AenderungsKennzeichen.STANDARD).withVertragsnummer("123").build();
        ErledigtMeldung erlmStdErr = new ErledigtMeldungBuilder().withAenderungsKennzeichen(AenderungsKennzeichen.STANDARD).withVertragsnummer(null).build();
        ErledigtMeldung erlmTvOk = new ErledigtMeldungBuilder().withAenderungsKennzeichen(AenderungsKennzeichen.TERMINVERSCHIEBUNG).withVertragsnummer("123").build();
        ErledigtMeldung erlmTvErr = new ErledigtMeldungBuilder().withAenderungsKennzeichen(AenderungsKennzeichen.TERMINVERSCHIEBUNG).withVertragsnummer(null).build();
        ErledigtMeldung erlmRexMkOk = new ErledigtMeldungBuilder().withGeschaeftsfallTyp(GeschaeftsfallTyp.RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG).withVertragsnummer("123").build();
        ErledigtMeldung erlmRexMkOk2 = new ErledigtMeldungBuilder().withGeschaeftsfallTyp(GeschaeftsfallTyp.RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG).withVertragsnummer(null).build();

        // @formatter:off
        return new Object[][] {
                { erlmStdOk, true },
                { erlmStdErr, false },
                { erlmTvOk, true },
                { erlmTvErr, false },
                { erlmRexMkOk, true },
                { erlmRexMkOk2, true },
        };
        // @formatter:on
    }

    @Test(dataProvider = "testCases")
    public void test(ErledigtMeldung erlm, boolean expected) {
        VertragsnummerSetForErlm constraintAnnotation = mock(VertragsnummerSetForErlm.class);
        when(constraintAnnotation.mandatory()).thenReturn(true);
        when(constraintAnnotation.permitted()).thenReturn(true);

        VertragsnummerSetForErlmValidator validator = new VertragsnummerSetForErlmValidator();
        validator.initialize(constraintAnnotation);
        assertThat(validator.isValid(erlm, context), equalTo(expected));
    }
}
