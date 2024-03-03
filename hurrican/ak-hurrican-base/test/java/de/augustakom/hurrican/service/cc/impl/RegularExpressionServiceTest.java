package de.augustakom.hurrican.service.cc.impl;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.CfgRegularExpression;
import de.augustakom.hurrican.model.cc.CfgRegularExpressionBuilder;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.cc.RegularExpressionService;


public class RegularExpressionServiceTest extends AbstractHurricanBaseServiceTest {

    @Test(groups = BaseTest.SERVICE)
    public void testMatch() throws ClassNotFoundException {
        CfgRegularExpression regExp = getBuilder(CfgRegularExpressionBuilder.class).build();
        RegularExpressionService regularExpressionService = getCCService(RegularExpressionService.class);

        // match
        assertMatch(regExp, regularExpressionService, "9", "12945");
        // no match
        assertMatch(regExp, regularExpressionService, null, "a");
        // no match, as whole region is matched
        assertMatch(regExp, regularExpressionService, null, "012945");
    }

    @Test(groups = BaseTest.SERVICE)
    public void testGroup0() throws ClassNotFoundException {
        CfgRegularExpression regExp = getBuilder(CfgRegularExpressionBuilder.class).withMatchGroup(0).build();
        RegularExpressionService regularExpressionService = getCCService(RegularExpressionService.class);

        assertMatch(regExp, regularExpressionService, "12345", "12345");
    }

    @Test(groups = BaseTest.SERVICE)
    public void testNotEnoughGroups() throws ClassNotFoundException {
        CfgRegularExpression regExp = getBuilder(CfgRegularExpressionBuilder.class).withMatchGroup(2).build();
        RegularExpressionService regularExpressionService = getCCService(RegularExpressionService.class);

        assertMatch(regExp, regularExpressionService, null, "12345");
    }

    @Test(groups = BaseTest.SERVICE)
    public void testWrongFormat() throws ClassNotFoundException {
        CfgRegularExpression regExp = getBuilder(CfgRegularExpressionBuilder.class).withRegExp(".*(").build();
        RegularExpressionService regularExpressionService = getCCService(RegularExpressionService.class);

        assertMatch(regExp, regularExpressionService, null, "asdf");
    }

    @Test(groups = BaseTest.SERVICE)
    public void testPhoneNumberMatch() throws ClassNotFoundException {
        CfgRegularExpression regExp = getBuilder(CfgRegularExpressionBuilder.class)
                .withRegExp("^\\D*\\s?\\D*\\s?(0\\d*\\s?[\\/\\\\]?\\s?\\d*[-]?\\d*)\n?.*$")
                .withMatchGroup(1)
                .build();
        RegularExpressionService regularExpressionService = getCCService(RegularExpressionService.class);

        assertMatch(regExp, regularExpressionService, "08216507820", "Routing auf 08216507820");
        assertMatch(regExp, regularExpressionService, "0821/450129-30", "Routing auf 0821/450129-30 jki");
        assertMatch(regExp, regularExpressionService, "08216507821 ", "Routing auf 08216507821 xyz");
        assertMatch(regExp, regularExpressionService, "08216507822", "Routing auf 08216507822(fjdskl 59)");
        assertMatch(regExp, regularExpressionService, "08216507823\n", "Routing auf 08216507823\n(fjdskl 59)");
        assertMatch(regExp, regularExpressionService, "0821 / 74035-0", "Routing auf 0821 / 74035-0");

    }

    private void assertMatch(CfgRegularExpression regExp, RegularExpressionService regularExpressionService,
            String expected, String matchString) throws ClassNotFoundException {
        String matchResult = regularExpressionService.match(regExp.getRefId(), Class.forName(regExp.getRefClass()),
                CfgRegularExpression.Info.valueOf(regExp.getRequestedInfo()), matchString);
        assertEquals(matchResult, expected);
    }

}
