package de.augustakom.hurrican.gui.tools.tal.ioarchive.xml;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

import java.io.*;
import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;
import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;
import org.springframework.core.io.ClassPathResource;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.xml.XmlFormatter;

@Test(groups = BaseTest.UNIT)
public class HistoryFormatterTest extends BaseTest {

    @DataProvider
    public Object[][] fileProviderWita() {
        // @formatter:off
		return new Object[][] {
				{ "valid-tal-neu.xml", true },
				{ "valid-tal-neu-with-anlage.xml", true },
				{ "valid-pv-with-anlagen.xml", true },
				{ "valid-ser-pow-with-anlage.xml", false },  // BKTO Faktura not included in xml
				{ "valid-abm.xml", false },
				{ "valid-teq.xml", false },
		};
		// @formatter:on
    }

    @DataProvider
    public Object[][] fileProviderWbci() {
        // @formatter:off
        return new Object[][] {
                { "valid-va-kue-mrn.xml", "true" },
                { "valid-va-kue-orn.xml", "true" },
                { "valid-va-rrnp.xml", "true" },
                { "valid-teq.xml", "true" },
        };
        // @formatter:on
    }

    @Test(dataProvider = "fileProviderWita")
    public void format(String file, boolean expectBkto) throws IOException, SAXException {
        String formatted = HistoryFormatter.formatSilent(readWita(file));

        // No exception, so still valid
        XmlFormatter.parseXmlFile(formatted);

        // Check that soap envelope, header and signature are still there
        assertFalse(formatted.contains("<?xml"));
        assertTrue(formatted.contains("SOAP-ENV:Envelope"));
        assertTrue(formatted.contains("SOAP-ENV:Header"));
        assertTrue(formatted.contains("<signaturId>"));
        assertThat(formatted.contains("BKTOFaktura"), equalTo(expectBkto));

        // Anlageninhalt is always removed
        assertFalse(formatted.contains("<inhalt>"));
    }

    @Test(dataProvider = "fileProviderWita")
    public void formatAndFilterWita(String file, boolean ignored) throws IOException, SAXException {
        String formatted = HistoryFormatter.filterAndFormatSilentWita(readWita(file));

        // No exception, so still valid
        XmlFormatter.parseXmlFile(formatted);

        assertFalse(formatted.contains("<?xml"));
        assertFalse(formatted.contains("SOAP-ENV:Envelope"));
        assertFalse(formatted.contains("SOAP-ENV:Header"));
        assertFalse(formatted.contains("<signaturId>"));
        assertFalse(formatted.contains("BKTOFaktura"));
        assertFalse(formatted.contains("<inhalt>"));
    }

    @Test(dataProvider = "fileProviderWbci")
    public void formatAndFilterWbci(String file, String expected) throws IOException, SAXException {
        String formatted = HistoryFormatter.filterAndFormatSilentWbci(readWbci(file));

        // No exception, so still valid
        XmlFormatter.parseXmlFile(formatted);

        assertFalse(formatted.contains("<?xml"));
        assertFalse(formatted.contains("SOAP-ENV:Envelope"));
        assertFalse(formatted.contains("SOAP-ENV:Header"));
        assertFalse(formatted.contains("<signaturId>"));
        assertFalse(formatted.contains("<inhalt>"));
    }

    private String readWita(final String filename) throws IOException {
        return read("wita/" + filename);
    }

    private String readWbci(final String filename) throws IOException {
        return read("wbci/" + filename);
    }

    private String read(final String filename) throws IOException {
        final ByteSource byteSource = new ByteSource() {
            @Override
            public InputStream openStream() throws IOException {
                return new ClassPathResource("de/augustakom/common/tools/xml/"
                        + filename).getInputStream();
            }
        };

        final CharSource charSource = byteSource.asCharSource(Charsets.UTF_8);
        return charSource.read();
    }

}
