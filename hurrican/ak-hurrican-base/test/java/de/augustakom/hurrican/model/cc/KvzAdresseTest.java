package de.augustakom.hurrican.model.cc;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.Pair;

@Test(groups = { BaseTest.UNIT })
public class KvzAdresseTest extends BaseTest {

    @DataProvider
    public Object[][] dataProviderSuccess() {
        // @formatter:off
        return new Object[][] {
                { "A",        "A"},
                { "A1",    "A001"},
                { "F111",  "F111"},
                { "F012",  "F012"},
                { "A01",   "A001"},
                { "1234",  "1234"},
                {  "123",  "0123"},
                {   "12",  "0012"},
                {    "1",  "0001"},
                {   "01",  "0001"},
                { "0001",  "0001"},
                // Pattern 2
                {  " 1A ", "0001A"},
                {    "1B", "0001B"},
                { "0001B", "0001B"},
                { "0062A", "0062A"},
                { "2032B", "2032B"}
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderSuccess")
    public void formatKvz(String input, String expected) throws Exception {
        Assert.assertEquals(KvzAdresse.formatKvzNr(input), expected);
    }


    @DataProvider
    public Object[][] formatKvzNrForWitaDataProvider() {
        // @formatter:off
        return new Object[][] {
                {    "A",     "A"},
                { "A001",    "A1"},
                { "A010",   "A10"},
                { "A100",  "A100"},
                { "B01" ,    "B1"},
                {  "123",   "123"},
                {  "012",    "12"},
                {  "001",     "1"},
                { "0123",   "123"},
                { "0012",    "12"},
                { "0001",     "1"},
                // Pattern 2
                {  " 1A ",    "1A"},
                { "0001B",    "1B"},
                { "0062A",   "62A"},
                {   "62A",   "62A"},
                { "2032B", "2032B"}
        };
        // @formatter:on
    }

    @Test(dataProvider = "formatKvzNrForWitaDataProvider")
    public void formatKvzNrForWita(String input, String expected) {
        Assert.assertEquals(KvzAdresse.formatKvzNrForWita(input), expected);
    }


    @DataProvider
    public Object[][] dataProviderFailure() {
        // @formatter:off
        return new Object[][] {
                { "A12B"   },
                { "A0001"  },
                { "AB01"   },
                { "00001"  },
                { "1A1"    },
                { "1 A"    },
                { "1\tA"   },
                { "12345A" }
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderFailure", expectedExceptions = IllegalArgumentException.class)
    public void formatKvzFailure(String input) throws Exception {
        KvzAdresse.formatKvzNr(input);
    }

    @DataProvider
    public Object[][] dataProviderSplitStrasseHausNr() {
        // @formatter:off
        return new Object[][] {
                { "Ohne Hausnummer Str.",            "Ohne Hausnummer Str.",            null        },
                { "Heesestraße 13-14/Bergstr. 1",    "Heesestraße 13-14/Bergstr.",      "1"         },
                { "Heesestraße/Bergstr. 1",          "Heesestraße/Bergstr.",            "1"         },
                { "Am Gonsenheimer Spieß 22 VST 31", "Am Gonsenheimer Spieß",           "22 VST 31" },
                { "Erlenbachstr. 64/1",              "Erlenbachstr.",                   "64/1"      },
                { "Meinhardswindener Str. 4A",       "Meinhardswindener Str.",          "4A"        },
                { "Liselotte-Herrmann-Str. 20",      "Liselotte-Herrmann-Str.",         "20"        },
                { "Französische Str. 33 A-C",        "Französische Str.",               "33 A-C"    },
                { "Winterfeldtstr. 21-27",           "Winterfeldtstr.",                 "21-27"     },
                { "Fredericiastr. 17 -19",           "Fredericiastr.",                  "17 -19"    },
                { "Oststr. 2-18/RU",                 "Oststr.",                         "2-18/RU"   },
                { "Hamburger Allee 27/27A",          "Hamburger Allee",                 "27/27A"    },
                { "Waldseer Str. 19 /1",             "Waldseer Str.",                   "19 /1"     },
                { "Waldseer Str. 19 / 1",            "Waldseer Str.",                   "19 / 1"    },
                { "Karthausstr./Neubauerstr. 18/2",  "Karthausstr./Neubauerstr.",       "18/2"      },
                { "Harsefelder Str. 66A/B",          "Harsefelder Str.",                "66A/B"     },
                { "Ebert- 84/Rhein- 59-63/Gökerstr", "Ebert- 84/Rhein- 59-63/Gökerstr", null        },
                { "Am Seefeld 7 A/ VST",             "Am Seefeld",                      "7 A/ VST"  },
                { "Flst. 205/11",                    "Flst.",                           "205/11"    },
                { "Umgehungsstraße Flst. 338/2",     "Umgehungsstraße Flst.",           "338/2"     },
                { "B388 / Greißlstr. 0",             "B388 / Greißlstr.",               "0"         },
                { "Liesel-Beckm./Thalhauser. 0",     "Liesel-Beckm./Thalhauser.",       "0"         },
                { "Ölbergstr./Gesseltshausen 12",    "Ölbergstr./Gesseltshausen",       "12"        },
                { "Hauptstr. 28ZWR/",                "Hauptstr.",                       "28ZWR/"    },
                { "KVZ Bundesstr.11/Hofham 0",       "KVZ Bundesstr.11/Hofham",         "0"         },
                { "KVZ B300 / B13 0",                "KVZ B300 / B",                    "13 0"      }, // ???
                { "KVZ Kreis./KEH 31/km 3,9 0",      "KVZ Kreis./KEH",                  "31/km 3,9 0"}, // ???
                { "Flur 2 Flurstück 132/1 0",        "Flur",                            "2 Flurstück 132/1 0"   }, // entspricht wahrscheinlich nicht der Erwartung !!!
                { "An der B 176/ GEWE",              "An der B",                        "176/ GEWE" }, // ???
                { "Postweg/MO 6A",                   "Postweg/MO",                      "6A"        }, // ???
                { "Pestalozzistr. 2/FÜRST",          "Pestalozzistr.",                  "2/FÜRST"   }, // ???
                { "Au/Inn 0",                        "Au/Inn",                          "0"         },
                { "KVZ Bushaltest./Ausf.A96 0",      "KVZ Bushaltest./Ausf.A96",        "0"         }, // ???
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderSplitStrasseHausNr")
    public void splitStrasseHausNr(String input, String expectedStreet, String expectedNo) {
        Pair<String, String> strasseHausNr = StandortAdresse.splitDTAGStrasseHausNr(input);
        Assert.assertEquals(strasseHausNr.getFirst(), expectedStreet);
        Assert.assertEquals(strasseHausNr.getSecond(), expectedNo);
    }

}
