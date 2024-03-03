package de.augustakom.hurrican.validation.cc;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import org.apache.log4j.Logger;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.CfgRegularExpression;
import de.augustakom.hurrican.model.cc.HWDslamBuilder;
import de.augustakom.hurrican.model.cc.HWSubrackBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.hardware.HWSubrack;
import de.augustakom.hurrican.model.cc.hardware.HWSubrackTyp;
import de.augustakom.hurrican.service.cc.QueryCCService;
import de.augustakom.hurrican.service.cc.RegularExpressionService;


@Test(groups = BaseTest.UNIT)
public class HWSubrackValidatorTest extends BaseTest {
    private static final Logger LOGGER = Logger.getLogger(HWSubrackValidatorTest.class);

    private HWSubrackValidator validator;
    private QueryCCService queryCcService;
    private RegularExpressionService regularExpressionService;

    @BeforeMethod
    public void setUp() throws Exception {
        queryCcService = mock(QueryCCService.class);
        regularExpressionService = mock(RegularExpressionService.class);

        validator = new HWSubrackValidator();
        validator.setQueryCcService(queryCcService);
        validator.setRegularExpressionService(regularExpressionService);
    }

    public void testRequired() {
        HWSubrackBuilder subrackBuilder = new HWSubrackBuilder();

        Errors errors = new BeanPropertyBindingResult(subrackBuilder.get(), "Subrack");
        validator.validate(subrackBuilder.get(), errors);

        assertEquals(errors.getFieldErrorCount(HWSubrack.RACK_ID), 1);
        assertEquals(errors.getFieldError(HWSubrack.RACK_ID).getDefaultMessage(), "Rack ist nicht definiert.");
        assertEquals(errors.getFieldErrorCount(HWSubrack.SUBRACK_TYP), 1);
        assertEquals(errors.getFieldError(HWSubrack.SUBRACK_TYP).getDefaultMessage(), "Subrack-Typ ist nicht definiert.");
        assertEquals(errors.getErrorCount(), 2);
    }

    public void testRackNull() {
        HWSubrackBuilder subrackBuilder = new HWSubrackBuilder().init();
        subrackBuilder.getRackBuilder().init().withRandomId();

        Errors errors = new BeanPropertyBindingResult(subrackBuilder.get(), "Subrack");
        validator.validate(subrackBuilder.get(), errors);

        assertEquals(errors.getFieldErrorCount(HWSubrack.SUBRACK_TYP), 1);
        assertEquals(errors.getFieldError(HWSubrack.SUBRACK_TYP).getDefaultMessage(), "Fehler bei der Validierung des Subracks aufgetreten.");
        assertEquals(errors.getErrorCount(), 1);
    }

    public void testSubrackInvalid() throws Exception {
        HWSubrackBuilder subrackBuilder = new HWSubrackBuilder().init();
        subrackBuilder.getSubrackTypBuilder().withRackTyp("BLA");
        subrackBuilder.getRackBuilder().init().withRandomId();
        when(queryCcService.findById(subrackBuilder.get().getRackId(), HWRack.class))
                .thenReturn(new HWDslamBuilder().get());

        Errors errors = new BeanPropertyBindingResult(subrackBuilder.get(), "Subrack");
        validator.validate(subrackBuilder.get(), errors);

        assertEquals(errors.getFieldErrorCount(HWSubrack.SUBRACK_TYP), 1);
        assertEquals(errors.getFieldError(HWSubrack.SUBRACK_TYP).getDefaultMessage(), "Subrack ist für dieses Rack nicht zulässig.");
        assertEquals(errors.getErrorCount(), 1);
    }

    public void testRegexp() throws Exception {
        HWSubrackBuilder subrackBuilder = new HWSubrackBuilder().init();
        subrackBuilder.getSubrackTypBuilder().withHwTypeName("DSLAM").init();
        subrackBuilder.getRackBuilder().init().withRandomId();

        when(queryCcService.findById(subrackBuilder.get().getRackId(), HWRack.class))
                .thenReturn(subrackBuilder.getRackBuilder().get());
        when(regularExpressionService.matches(subrackBuilder.getSubrackTypBuilder().get().getHwTypeName(),
                HWSubrackTyp.class,
                CfgRegularExpression.Info.SUBRACK_MOD_NUMBER,
                subrackBuilder.get().getModNumber()))
                .thenReturn("Bla");

        Errors errors = new BeanPropertyBindingResult(subrackBuilder.get(), "Subrack");
        validator.validate(subrackBuilder.get(), errors);

        assertEquals(errors.getFieldErrorCount(HWSubrack.MOD_NUMBER), 1);
        assertEquals(errors.getErrorCount(), 1);
    }

    public void testSuccess() throws Exception {
        HWSubrackBuilder subrackBuilder = new HWSubrackBuilder().init();
        subrackBuilder.getSubrackTypBuilder().init();
        subrackBuilder.getRackBuilder().init().withRandomId();

        when(queryCcService.findById(subrackBuilder.get().getRackId(), HWRack.class))
                .thenReturn(subrackBuilder.getRackBuilder().get());

        Errors errors = new BeanPropertyBindingResult(subrackBuilder.get(), "Subrack");
        validator.validate(subrackBuilder.get(), errors);

        if (errors.hasErrors()) {
            LOGGER.error("testValidate() - had errors: " + errors.toString());
            fail("validator had errors");
        }
    }
}
