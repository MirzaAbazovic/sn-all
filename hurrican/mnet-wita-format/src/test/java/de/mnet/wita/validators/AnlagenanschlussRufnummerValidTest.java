package de.mnet.wita.validators;

import static de.mnet.wita.TestGroups.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.*;
import javax.validation.*;
import org.mockito.Mock;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wita.message.common.portierung.RufnummernBlock;
import de.mnet.wita.message.common.portierung.RufnummernPortierungAnlagenanschluss;
import de.mnet.wita.validators.AnlagenanschlussRufnummerValid.AnlagenanschlussRufnummerValidator;

@Test(groups = UNIT)
public class AnlagenanschlussRufnummerValidTest {

    @Mock
    ConstraintValidatorContext context;

    @DataProvider
    public Object[][] testCases() {
        // @formatter:off
        return new Object[][] {
                { "89",       "123456",    "0",    Arrays.asList("29"),  true },
                { "89",       "123456",    "0",    Arrays.asList("221"), true },
                { "89",       "123456",    "0000", Arrays.asList("29"),  false },
                { "8233",     "123456",    "0",    Arrays.asList("29"),  false },
                { "8233",     "123456",    "0",    Arrays.asList("2", "99"), false },
                { "12345689", "123456",    "0",    Arrays.asList("1"),       false },
        };
        // @formatter:on
    }

    @Test(dataProvider = "testCases")
    public void test(String onkz, String durchwahl, String abfragestelle, List<String> blockVonList, boolean expected) {
        RufnummernPortierungAnlagenanschluss anlagenAnschluss = new RufnummernPortierungAnlagenanschluss();
        anlagenAnschluss.setOnkz(onkz);
        anlagenAnschluss.setDurchwahl(durchwahl);
        anlagenAnschluss.setAbfragestelle(abfragestelle);
        for (String blockVon : blockVonList) {
            RufnummernBlock rufnummernBlock = new RufnummernBlock();
            rufnummernBlock.setVon(blockVon);
            anlagenAnschluss.addRufnummernBlock(rufnummernBlock);
        }
        assertThat(new AnlagenanschlussRufnummerValidator().isValid(anlagenAnschluss, context), equalTo(expected));

    }
}
