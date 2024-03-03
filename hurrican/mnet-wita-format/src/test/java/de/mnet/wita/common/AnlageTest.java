package de.mnet.wita.common;

import static de.mnet.wita.TestGroups.*;
import static org.testng.Assert.*;

import org.testng.annotations.Test;

import de.mnet.wita.message.common.Anlage;

@Test(groups = UNIT)
public class AnlageTest {
    public void testToStringWithNullInhalt() {
        Anlage anlage = new Anlage();
        assertEquals(anlage.toString(), "Anlage [dateiname=null, dateityp=null, beschreibung=null, anlagentyp=null, inhalt=null, id=null]");
    }

    public void testToStringWithNonNullInhalt() {
        Anlage anlage = new Anlage();
        anlage.setInhalt(new byte[] { 1, 2, 3 });
        assertFalse(anlage.toString().contains("inhalt=null") || anlage.toString().contains("1, 2, 3"), anlage.toString());
    }
}
