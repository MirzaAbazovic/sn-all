/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.07.2011 11:36:10
 */
package de.mnet.wita.validators.reflection;

import static de.mnet.wita.TestGroups.*;
import static de.mnet.wita.message.GeschaeftsfallTyp.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.*;
import javax.validation.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.message.auftrag.Auftragsposition;
import de.mnet.wita.message.builder.AuftragBuilder;
import de.mnet.wita.message.builder.MnetWitaRequestBuilder;
import de.mnet.wita.message.builder.StornoBuilder;
import de.mnet.wita.message.builder.TerminVerschiebungBuilder;
import de.mnet.wita.message.common.Anlage;
import de.mnet.wita.message.common.Anlagentyp;
import de.mnet.wita.message.common.Dateityp;

@Test(groups = UNIT)
public class WitaCharacterValidatorTest {

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private static final WitaCharacterValidator manualValidator = new WitaCharacterValidator();

    // @formatter:off
    private static final char[] INVALID_CHARS = {
            '\u0000', // 0   <control>
            '\u0001', // 1   <control>
            '\u0002', // 2   <control>
            '\u0003', // 3   <control>
            '\u0004', // 4   <control>
            '\u0005', // 5   <control>
            '\u0006', // 6   <control>
            '\u0007', // 7   <control>
            '\u0008', // 8   <control>

            '\u000b', // 0b  <control>
            '\u000c', // 0c  <control>

            '\u000e', // 0e  <control>
            '\u000f', // 0f  <control>
            '\u0010', // 10  <control>
            '\u0011', // 11  <control>
            '\u0012', // 12  <control>
            '\u0013', // 13  <control>
            '\u0014', // 14  <control>
            '\u0015', // 15  <control>
            '\u0016', // 16  <control>
            '\u0017', // 17  <control>
            '\u0018', // 18  <control>
            '\u0019', // 19  <control>
            '\u001a', // 1a  <control>
            '\u001b', // 1b  <control>
            '\u001c', // 1c  <control>
            '\u001d', // 1d  <control>
            '\u001e', // 1e  <control>
            '\u001f', // 1f  <control>

            '\u0060', // `   60  GRAVE ACCENT

            '\u007c', // |   7c  VERTICAL LINE

            '\u007e', // ~   7e  TILDE
            '\u007f', // 7f  <control>
            '\u0080', // c2 80   <control>
            '\u0081', // c2 81   <control>
            '\u0082', // c2 82   <control>
            '\u0083', // c2 83   <control>
            '\u0084', // c2 84   <control>
            '\u0085', // c2 85   <control>
            '\u0086', // c2 86   <control>
            '\u0087', // c2 87   <control>
            '\u0088', // c2 88   <control>
            '\u0089', // c2 89   <control>
            '\u008a', // c2 8a   <control>
            '\u008b', // c2 8b   <control>
            '\u008c', // c2 8c   <control>
            '\u008d', // c2 8d   <control>
            '\u008e', // c2 8e   <control>
            '\u008f', // c2 8f   <control>
            '\u0090', // c2 90   <control>
            '\u0091', // c2 91   <control>
            '\u0092', // c2 92   <control>
            '\u0093', // c2 93   <control>
            '\u0094', // c2 94   <control>
            '\u0095', // c2 95   <control>
            '\u0096', // c2 96   <control>
            '\u0097', // c2 97   <control>
            '\u0098', // c2 98   <control>
            '\u0099', // c2 99   <control>
            '\u009a', // c2 9a   <control>
            '\u009b', // c2 9b   <control>
            '\u009c', // c2 9c   <control>
            '\u009d', // c2 9d   <control>
            '\u009e', // c2 9e   <control>
            '\u009f', // c2 9f   <control>
            '\u00a0', // c2 a0   NO-BREAK SPACE
            '\u00a1', // ¡   c2 a1   INVERTED EXCLAMATION MARK
            '\u00a2', // ¢   c2 a2   CENT SIGN
            '\u00a3', // £   c2 a3   POUND SIGN
            '\u00a4', // ¤   c2 a4   CURRENCY SIGN
            '\u00a5', // ¥   c2 a5   YEN SIGN
            '\u00a6', // ¦   c2 a6   BROKEN BAR

            '\u00a8', // ¨   c2 a8   DIAERESIS
            '\u00a9', // ©   c2 a9   COPYRIGHT SIGN
            '\u00aa', // ª   c2 aa   FEMININE ORDINAL INDICATOR
            '\u00ab', // «   c2 ab   LEFT-POINTING DOUBLE ANGLE QUOTATION MARK
            '\u00ac', // ¬   c2 ac   NOT SIGN
            '\u00ad', // ­   c2 ad   SOFT HYPHEN
            '\u00ae', // ®   c2 ae   REGISTERED SIGN
            '\u00af', // ¯   c2 af   MACRON

            '\u00b4', // ´   c2 b4   ACUTE ACCENT
            '\u00b5', // µ   c2 b5   MICRO SIGN
            '\u00b6', // ¶   c2 b6   PILCROW SIGN
            '\u00b7', // ·   c2 b7   MIDDLE DOT
            '\u00b8', // ¸   c2 b8   CEDILLA
            '\u00b9', // ¹   c2 b9   SUPERSCRIPT ONE
            '\u00ba', // º   c2 ba   MASCULINE ORDINAL INDICATOR
            '\u00bb', // »   c2 bb   RIGHT-POINTING DOUBLE ANGLE QUOTATION MARK
            '\u00bc', // ¼   c2 bc   VULGAR FRACTION ONE QUARTER
            '\u00bd', // ½   c2 bd   VULGAR FRACTION ONE HALF
            '\u00be', // ¾   c2 be   VULGAR FRACTION THREE QUARTERS
            '\u00bf', // ¿   c2 bf   INVERTED QUESTION MARK

            '\u00d7', // ×   c3 97   MULTIPLICATION SIGN

            '\u00f7', // ÷   c3 b7   DIVISION SIGN
        };
    // @formatter:on

    private static final GeschaeftsfallTyp[] GESCHAEFTSFAELLE = { BEREITSTELLUNG, KUENDIGUNG_KUNDE,
            LEISTUNGS_AENDERUNG, LEISTUNGSMERKMAL_AENDERUNG, PORTWECHSEL, PROVIDERWECHSEL,
            RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG, VERBUNDLEISTUNG };

    @DataProvider
    Object[][] requestProvider() {
        List<Object[]> data = new ArrayList<>();
        for (GeschaeftsfallTyp typ : GESCHAEFTSFAELLE) {
            MnetWitaRequestBuilder<?> builders[] = { new AuftragBuilder(typ), new StornoBuilder(typ),
                    new TerminVerschiebungBuilder(typ) };
            for (MnetWitaRequestBuilder<?> builder : builders) {
                for (char c = '\u0001'; c < '\u00ff'; c++) {
                    boolean validChar = Arrays.binarySearch(INVALID_CHARS, c) < 0; // valid if not found
                    data.add(new Object[] { builder, c, validChar });
                }
            }
        }
        return data.toArray(new Object[data.size()][]);
    }

    @Test(dataProvider = "requestProvider")
    public void testValidation(MnetWitaRequestBuilder<?> builder, char append, boolean valid) {
        MnetWitaRequest request = builder.buildValid();
        request.setExterneAuftragsnummer(request.getExterneAuftragsnummer() + append);

        Set<ConstraintViolation<MnetWitaRequest>> violations = validator.validate(request);
        assertThat("Char '" + append + "'; Unexpected violation set for bean validation: " + violations,
                violations.isEmpty(), equalTo(valid));

        Set<String> manualViolations = manualValidator.validate(request);
        assertThat("Char '" + append + "'; Unexpected violation set: " + manualViolations, manualViolations.isEmpty(),
                equalTo(valid));
    }

    @DataProvider
    public Object[][] kuendigungRequestProvider() {
        Object[][] requests = requestProvider();
        List<Object[]> result = new ArrayList<>();
        for (Object[] request : requests) {
            if (((MnetWitaRequestBuilder<?>) request[0]).getGeschaeftsfallTyp() == GeschaeftsfallTyp.KUENDIGUNG_KUNDE) {
                result.add(request);
            }
        }
        return result.toArray(new Object[][] { });
    }

    @Test(dataProvider = "kuendigungRequestProvider")
    public void testValidationOfLists(MnetWitaRequestBuilder<?> builder, char append, boolean expected) {
        MnetWitaRequest request = builder.buildValid();
        Anlage anlage = new Anlage("test", Dateityp.PDF, "Test" + append, "test".getBytes(),
                Anlagentyp.KUENDIGUNGSSCHREIBEN);
        request.getGeschaeftsfall().getAnlagen().add(anlage);

        Set<ConstraintViolation<MnetWitaRequest>> violations = validator.validate(request);
        assertThat("Unexpected violation set for bean validation: " + violations, violations.isEmpty(),
                equalTo(expected));

        Set<String> manualViolations = manualValidator.validate(request);
        assertThat("Unexpected violation set: " + manualViolations, manualViolations.isEmpty(), equalTo(expected));
    }

    @Test
    public void testRecursive() {
        Auftrag auftrag = new AuftragBuilder(BEREITSTELLUNG).buildValid();

        Auftragsposition position = auftrag.getGeschaeftsfall().getAuftragsPosition();
        auftrag.getGeschaeftsfall().getAuftragsPosition().setPosition(position);

        Set<ConstraintViolation<Auftrag>> violations = validator.validate(auftrag);
        assertThat("Unexpected violation set for bean validation: " + violations, violations.isEmpty(), equalTo(true));

        Set<String> manualViolations = manualValidator.validate(auftrag);
        assertThat("Unexpected violation set: " + manualViolations, manualViolations.isEmpty(), equalTo(true));
    }
}
