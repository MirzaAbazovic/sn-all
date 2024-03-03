package de.augustakom.hurrican.validation.cc;


import static org.mockito.Mockito.*;

import org.apache.commons.lang.ArrayUtils;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.Errors;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.CCAddressBuilder;

/**
 * Tests the class CCAddressValidator
 * <p>
 * Created by zieglerch on 29.07.2016.
 */
@Test(groups = BaseTest.UNIT)
public class CCAddressValidatorTest {

    private CCAddressValidator testee = new CCAddressValidator();

    @Mock
    private Errors errors;

    @BeforeMethod
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void validateAddressTypeEmailWithBlankEmail() throws Exception {
        CCAddress address = createAddress().withAddressType(CCAddress.ADDRESS_TYPE_EMAIL).withEmail(" ").build();

        testee.validate(address, errors);

        verify(errors).rejectValue(eq(CCAddress.EMAIL), eq(CCAddressValidator.ERRCODE_REQUIRED), anyString());
        verifyNoMoreInteractions(errors);
    }

    /**
     * CCAddress objects with addressTypes that should have a street or P.O. box address.
     */
    @DataProvider(name = "addressTypesWithAddress")
    protected Object[][] addressTypesWithAddress() {
        return new Object[][] {
                { createAddress().withAddressType(CCAddress.ADDRESS_TYPE_ACCESSPOINT) },
                { createAddress().withAddressType(CCAddress.ADDRESS_TYPE_ACCESSPOINT_OWNER) },
                { createAddress().withAddressType(CCAddress.ADDRESS_TYPE_CUSTOMER_CONTACT) },
                { createAddress().withAddressType(CCAddress.ADDRESS_TYPE_HOTLINE_GA) },
                { createAddress().withAddressType(CCAddress.ADDRESS_TYPE_HOTLINE_SERVICE) },
                { createAddress().withAddressType(CCAddress.ADDRESS_TYPE_SHIPPING) },
                { createAddress().withAddressType(CCAddress.ADDRESS_TYPE_VARIOUS) }
        };
    }

    /**
     * CCAddress objects with addressTypes that should have a street or P.O. box address.
     */
    @DataProvider(name = "addressTypesWithoutAddress")
    protected Object[][] addressTypesWithoutAddress() {
        return new Object[][] {
                { createAddress().withAddressType(CCAddress.ADDRESS_TYPE_EMAIL) },
                { createAddress().withAddressType(CCAddress.ADDRESS_TYPE_WHOLESALE_FFM_DATA) }
        };
    }

    /**
     * CCAddress objects with all possible addressTypes.
     */
    @DataProvider(name = "addressTypesAll")
    protected Object[][] addressTypesAll() {
        return ((Object[][]) ArrayUtils.addAll(addressTypesWithAddress(), addressTypesWithoutAddress()));
    }

    /**
     * Validates that an empty Ort registers an error for all addressTypes that need an address.
     */
    @Test(dataProvider = "addressTypesWithAddress")
    public void validateNoOrt(CCAddressBuilder addressBuilder) throws Exception {
        CCAddress address = addressBuilder.withOrt(" ").build();

        testee.validate(address, errors);

        verify(errors).rejectValue(eq(CCAddress.ORT), eq(CCAddressValidator.ERRCODE_REQUIRED), anyString());
        verifyNoMoreInteractions(errors);
    }

    /**
     * Validates that an empty PLZ registers an error for all addressTypes that need an address.
     */
    @Test(dataProvider = "addressTypesWithAddress")
    public void validateNoPlz(CCAddressBuilder addressBuilder) {
        CCAddress address = addressBuilder.withPlz(" ").build();

        testee.validate(address, errors);

        verify(errors).rejectValue(eq(CCAddress.PLZ), eq(CCAddressValidator.ERRCODE_REQUIRED), anyString());
        verifyNoMoreInteractions(errors);
    }

    /**
     * Validates that an empty landId registers an error for all addressTypes that need an address.
     */
    @Test(dataProvider = "addressTypesWithAddress")
    public void validateNoLandId(CCAddressBuilder addressBuilder) {
        CCAddress address = addressBuilder.withLandId(" ").build();

        testee.validate(address, errors);

        verify(errors).rejectValue(eq(CCAddress.LAND_ID), eq(CCAddressValidator.ERRCODE_REQUIRED), anyString());
        verifyNoMoreInteractions(errors);
    }

    /**
     *  Validates that an empty formatName registers an error for all possible addressTypes.
     */
    @Test(dataProvider = "addressTypesAll")
    public void validateNoFormatName(CCAddressBuilder addressBuilder) {
        CCAddress address = addressBuilder.withFormatName(" ").build();

        testee.validate(address, errors);

        verify(errors).rejectValue(eq(CCAddress.FORMAT_NAME), eq(CCAddressValidator.ERRCODE_REQUIRED), anyString());
        verifyNoMoreInteractions(errors);
    }

    /**
     * Validates that an address with both strasse and postfach registers an error for all addressTypes that need an
     * address.
     */
    @Test(dataProvider = "addressTypesWithAddress")
    public void validateStrasseAndPostfach(CCAddressBuilder addressBuilder) throws Exception {
        CCAddress address = addressBuilder.withStrasse("STRASSE").withPostfach("POSTFACH").build();

        testee.validate(address, errors);

        verify(errors).rejectValue(eq(CCAddress.POSTFACH), eq(CCAddressValidator.ERRCODE_REQUIRED), anyString());
        verifyNoMoreInteractions(errors);
    }

    /**
     * Validates that an address with neither strasse nor postfach registers an error for all addressTypes that need an
     * address.
     */
    @Test(dataProvider = "addressTypesWithAddress")
    public void validateNeitherStrasseNorPostfach(CCAddressBuilder addressBuilder) throws Exception {
        CCAddress address = addressBuilder.withStrasse(" ").withPostfach(" ").build();

        testee.validate(address, errors);

        verify(errors).rejectValue(eq(CCAddress.POSTFACH), eq(CCAddressValidator.ERRCODE_REQUIRED), anyString());
        verifyNoMoreInteractions(errors);
    }

    /**
     * Validates that a null addressType registers an error for all possible addressTypes.
     */
    @Test(dataProvider = "addressTypesAll")
    public void validateNoAddressType(CCAddressBuilder addressBuilder) throws Exception {
        CCAddress address = addressBuilder.withAddressType(null).build();

        testee.validate(address, errors);

        verify(errors).rejectValue(eq(CCAddress.ADDRESS_TYPE), eq(CCAddressValidator.ERRCODE_REQUIRED), anyString());
        verifyNoMoreInteractions(errors);
    }

    private static CCAddressBuilder createAddress() {
        return new CCAddressBuilder()
                .withStrasse("STRASSE")
                .withOrt("ORT")
                .withEmail("EMAIL");
    }
}