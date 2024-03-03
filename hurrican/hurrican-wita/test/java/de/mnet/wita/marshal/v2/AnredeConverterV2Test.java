package de.mnet.wita.marshal.v2;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.mnet.wita.marshal.v1.AnredeConverter;
import de.mnet.wita.message.auftrag.Anrede;

@Test
public class AnredeConverterV2Test {
    public void thatAnredeIsConvertedToWita() {
        doTestThatAnredeIsConverted(Anrede.FIRMA, true, "4");
        doTestThatAnredeIsConverted(Anrede.UNBEKANNT, true, "9");
        doTestThatAnredeIsConverted(Anrede.UNBEKANNT, false, "9");
        doTestThatAnredeIsConverted(Anrede.HERR, false, "1");
        doTestThatAnredeIsConverted(Anrede.FRAU, false, "2");
    }

    private void doTestThatAnredeIsConverted(final Anrede anrede, final boolean isFirma, final String expected) {
        String convertedV2 = AnredeConverterV2.toWita(anrede, isFirma);
        String convertedV1 = AnredeConverter.toWita(anrede, isFirma);
        Assert.assertEquals(convertedV2, expected);
        Assert.assertEquals(convertedV1, convertedV1, "Anrede v1 und v2 stimmt nicht ueberein, " + convertedV1 + " != " + convertedV2);
    }

}