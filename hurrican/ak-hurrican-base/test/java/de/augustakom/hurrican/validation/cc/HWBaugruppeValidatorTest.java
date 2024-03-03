package de.augustakom.hurrican.validation.cc;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import org.apache.log4j.Logger;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.HWDslamBuilder;
import de.augustakom.hurrican.model.cc.HWSubrackBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWSubrack;
import de.augustakom.hurrican.service.cc.QueryCCService;


@Test(groups = BaseTest.UNIT)
public class HWBaugruppeValidatorTest extends BaseTest {
    private static final Logger LOGGER = Logger.getLogger(HWBaugruppeValidatorTest.class);

    private HWBaugruppeValidator validator;
    private QueryCCService queryCcService;

    @BeforeMethod
    public void setUp() throws Exception {
        queryCcService = mock(QueryCCService.class);
        validator = new HWBaugruppeValidator();
        validator.setQueryCcService(queryCcService);
    }

    public void testRequired() {
        HWBaugruppeBuilder baugruppeBuilder = new HWBaugruppeBuilder();

        Errors errors = new BeanPropertyBindingResult(baugruppeBuilder.get(), "Baugruppe");
        validator.validate(baugruppeBuilder.get(), errors);

        assertEquals(errors.getFieldErrorCount(HWBaugruppe.RACK_ID), 1);
        assertEquals(errors.getFieldError(HWBaugruppe.RACK_ID).getDefaultMessage(), "Rack ist nicht definiert.");
        assertEquals(errors.getFieldErrorCount(HWBaugruppe.HW_BAUGRUPPEN_TYP), 1);
        assertEquals(errors.getFieldError(HWBaugruppe.HW_BAUGRUPPEN_TYP).getDefaultMessage(), "Baugruppen-Typ ist nicht definiert.");
        assertEquals(errors.getErrorCount(), 2);
    }

    public void testWrongSubrack() throws Exception {
        HWSubrackBuilder subrackBuilder = new HWSubrackBuilder().withRandomId().init();
        HWBaugruppeBuilder baugruppeBuilder = new HWBaugruppeBuilder().init()
                .withSubrackBuilder(subrackBuilder);
        // now baugruppe has same rack as subrack, so exchanging rack in subrack...
        subrackBuilder.withRackBuilder(new HWDslamBuilder().withRandomId().init());
        baugruppeBuilder.getRackBuilder().withRandomId();
        baugruppeBuilder.getHwBaugruppenTypBuilder().withRandomId();

        // Build subrack, which builds with a rack, and then baugruppe, which
        // supplies subrack builder with another rack builder
        subrackBuilder.build();
        baugruppeBuilder.build();

        when(queryCcService.findById(anyInt(), eq(HWSubrack.class)))
                .thenReturn(subrackBuilder.get());

        Errors errors = new BeanPropertyBindingResult(baugruppeBuilder.get(), "Baugruppe");
        validator.validate(baugruppeBuilder.get(), errors);

        verify(queryCcService, times(1)).findById(anyInt(), eq(HWSubrack.class));
        assertEquals(errors.getFieldErrorCount(HWBaugruppe.SUBRACK_ID), 1);
        assertEquals(errors.getFieldError(HWBaugruppe.SUBRACK_ID).getDefaultMessage(), "Subrack ist nicht dem gleichen Rack zugeordnet.");
    }

    public void testSuccessNoSubrack() throws Exception {
        HWBaugruppeBuilder baugruppeBuilder = new HWBaugruppeBuilder().init();
        baugruppeBuilder.getRackBuilder().withRandomId();
        baugruppeBuilder.getHwBaugruppenTypBuilder().withRandomId();

        Errors errors = new BeanPropertyBindingResult(baugruppeBuilder.get(), "Baugruppe");
        validator.validate(baugruppeBuilder.get(), errors);

        verify(queryCcService, never()).findById(anyInt(), eq(HWSubrack.class));
        if (errors.hasErrors()) {
            LOGGER.error("testValidate() - had errors: " + errors.toString());
            fail("validator had errors");
        }
    }

    public void testSuccess() throws Exception {
        HWSubrackBuilder subrackBuilder = new HWSubrackBuilder().withRandomId();
        HWBaugruppeBuilder baugruppeBuilder = new HWBaugruppeBuilder().init()
                .withSubrackBuilder(subrackBuilder);
        baugruppeBuilder.getRackBuilder().withRandomId();
        baugruppeBuilder.getHwBaugruppenTypBuilder().withRandomId();
        baugruppeBuilder.build();

        when(queryCcService.findById(subrackBuilder.get().getId(), HWSubrack.class))
                .thenReturn(subrackBuilder.get());

        Errors errors = new BeanPropertyBindingResult(baugruppeBuilder.get(), "Baugruppe");
        validator.validate(baugruppeBuilder.get(), errors);

        verify(queryCcService, times(1)).findById(baugruppeBuilder.getSubrackBuilder().get().getId(), HWSubrack.class);
        if (errors.hasErrors()) {
            LOGGER.error("testValidate() - had errors: " + errors.toString());
            fail("validator had errors");
        }
    }
}
