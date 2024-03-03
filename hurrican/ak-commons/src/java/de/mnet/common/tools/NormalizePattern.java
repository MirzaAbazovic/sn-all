/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.11.13
 */
package de.mnet.common.tools;

import java.util.*;
import java.util.regex.*;

/**
 * Enum mit Normalisierungs-Definitionen. Diese Definitionen koennen z.B. dem {@link PhoneticCheck} uebergeben werden,
 * um Strings vor einem Vergleich zu 'normalisieren'.
 */
public enum NormalizePattern {
    /**
     * Patterns, um Standard-Zeichen zu normalisiern. Aus 'ö' wird z.B. 'oe'.
     */
    STANDARD_PATTERNS(createStandardPatterns()),
    /**
     * Patterns, um Strassennamen zu normalisieren. Aus 'str.' wird z.B. 'str', 'sankt' zu 'st' etc.
     */
    STREET_PATTERNS(createStreetPatterns()),
    /**
     * Patterns, um Ortsnamen zu normalisieren. Fuellwoerter wie 'am', 'in' etc. werden ersetzt.
     */
    CITY_PATTERNS(createCityPatterns()),
    /**
     * Patterns, um Personen-Namen zu normalisieren. Kombinationen wie "von der", "u." etc. werden ersetzt
     */
    NAME_PATTERNS(createNamePatterns());

    private Map<Pattern, String> patterns;

    NormalizePattern(Map<Pattern, String> patterns) {
        this.patterns = patterns;
    }

    public Map<Pattern, String> getPatterns() {
        return patterns;
    }

    private static Map<Pattern, String> createStandardPatterns() {
        Map<Pattern, String> standardPatterns = new LinkedHashMap<>();
        //@formatter:off
        standardPatterns.put(Pattern.compile("\\s|[-/()\\.]"            ), " ");
        standardPatterns.put(Pattern.compile("[']"                      ), "");
        standardPatterns.put(Pattern.compile("\\u0020\\u0020+"          ), " ");
        standardPatterns.put(Pattern.compile("ä"                        ), "ae");
        standardPatterns.put(Pattern.compile("ö"                        ), "oe");
        standardPatterns.put(Pattern.compile("ü"                        ), "ue");
        standardPatterns.put(Pattern.compile("ß"                        ), "ss");
        //@formatter:on
        return standardPatterns;
    }

    private static Map<Pattern, String> createStreetPatterns() {
        Map<Pattern, String> streetPatterns = new LinkedHashMap<>();
        //@formatter:off
        // Strassen etc mit Varianten am String- bzw. Wortende
        streetPatterns.put(Pattern.compile("strasse$"                  ), "str");
        streetPatterns.put(Pattern.compile(" str$"                     ), "str");
        streetPatterns.put(Pattern.compile(" weg$"                     ), "weg");
        streetPatterns.put(Pattern.compile(" gasse$"                   ), "gasse");
        streetPatterns.put(Pattern.compile(" gaesschen$"               ), "gaesschen");
        streetPatterns.put(Pattern.compile(" platz$"                   ), "pl");
        streetPatterns.put(Pattern.compile("platz$"                    ), "pl");
        streetPatterns.put(Pattern.compile("platz "                    ), "pl ");
        streetPatterns.put(Pattern.compile(" ring$"                    ), "ring");
        streetPatterns.put(Pattern.compile(" allee$"                   ), "allee");

        // Abkürzungen (Wortende)
        streetPatterns.put(Pattern.compile("sankt "                    ), "st ");
        streetPatterns.put(Pattern.compile("doktor "                   ), "dr ");
        streetPatterns.put(Pattern.compile("buergermeister "           ), "bgm ");
        streetPatterns.put(Pattern.compile("buergerm "                 ), "bgm ");
        streetPatterns.put(Pattern.compile("baptist "                  ), "bapt ");
        streetPatterns.put(Pattern.compile("pfarrer "                  ), "pf ");
        streetPatterns.put(Pattern.compile("professor "                ), "prof ");

        // Abkürzungen Namen (Wortende)
        streetPatterns.put(Pattern.compile("josef "                    ), "jos ");
        streetPatterns.put(Pattern.compile("johann "                   ), "joh ");
        streetPatterns.put(Pattern.compile("johannes "                 ), "joh ");
        streetPatterns.put(Pattern.compile("sebastian "                ), "seb ");
        streetPatterns.put(Pattern.compile("michael "                  ), "mich ");
        streetPatterns.put(Pattern.compile("ferdinand "                ), "ferd ");
        streetPatterns.put(Pattern.compile("anton "                    ), "ant ");
        streetPatterns.put(Pattern.compile("nikolaus "                 ), "nik ");
        streetPatterns.put(Pattern.compile("alexander "                ), "alex ");

        // Stringmitte
        streetPatterns.put(Pattern.compile(" am "                      ), " ");
        streetPatterns.put(Pattern.compile(" an "                      ), " ");
        streetPatterns.put(Pattern.compile(" auf "                     ), " ");
        streetPatterns.put(Pattern.compile(" a "                       ), " ");
        streetPatterns.put(Pattern.compile(" bei "                     ), " ");
        streetPatterns.put(Pattern.compile(" b "                       ), " ");
        streetPatterns.put(Pattern.compile(" der "                     ), " ");
        streetPatterns.put(Pattern.compile(" dem "                     ), " ");
        streetPatterns.put(Pattern.compile(" den "                     ), " ");
        streetPatterns.put(Pattern.compile(" d "                       ), " ");
        streetPatterns.put(Pattern.compile(" in "                      ), " ");
        streetPatterns.put(Pattern.compile(" i "                       ), " ");
        streetPatterns.put(Pattern.compile(" von "                     ), " ");
        streetPatterns.put(Pattern.compile(" v "                       ), " ");

        streetPatterns.put(Pattern.compile(" ad "                      ), " "); // "a.d." ohne Punkte
        streetPatterns.put(Pattern.compile(" bd "                      ), " "); // "b.d." ohne Punkte
        streetPatterns.put(Pattern.compile(" id "                      ), " "); // "i.d." ohne Punkte
        streetPatterns.put(Pattern.compile(" vd "                      ), " "); // "v.d." ohne Punkte
        //@formatter:on

        return streetPatterns;
    }


    private static Map<Pattern, String> createCityPatterns() {
        Map<Pattern, String> cityPatterns = new LinkedHashMap<>();
        //@formatter:off

        // Abkürzungen (Wortende)
        cityPatterns.put(Pattern.compile("sankt "                  ), "st ");

        // Stringmitte
        cityPatterns.put(Pattern.compile(" am "                    ), " ");
        cityPatterns.put(Pattern.compile(" an "                    ), " ");
        cityPatterns.put(Pattern.compile(" a "                     ), " ");
        cityPatterns.put(Pattern.compile(" bei "                   ), " ");
        cityPatterns.put(Pattern.compile(" b "                     ), " ");
        cityPatterns.put(Pattern.compile(" der "                   ), " ");
        cityPatterns.put(Pattern.compile(" d "                     ), " ");
        cityPatterns.put(Pattern.compile(" im "                    ), " ");
        cityPatterns.put(Pattern.compile(" in "                    ), " ");
        cityPatterns.put(Pattern.compile(" i "                     ), " ");

        cityPatterns.put(Pattern.compile(" ad "                    ), " "); // "a.d." ohne Punkte
        cityPatterns.put(Pattern.compile(" id "                    ), " "); // "i.d." ohne Punkte

        //@formatter:on

        return cityPatterns;
    }


    private static Map<Pattern, String> createNamePatterns() {
        Map<Pattern, String> namePatterns = new LinkedHashMap<>();
        //@formatter:off

        /**
         * Alle derzeit gültigen Namenszusätz (siehe WITA-2011):
         * ACHTUNG: diese sind bereits normalisiert!!!
         */
        //  nicht normalisiert: Bar
        namePatterns.put(Pattern.compile(" bar "                           ), " ");
        //  nicht normalisiert: Baron
        namePatterns.put(Pattern.compile(" baron "                         ), " ");
        //  nicht normalisiert: Baroness
        namePatterns.put(Pattern.compile(" baroness "                      ), " ");
        //  nicht normalisiert: Baronesse
        namePatterns.put(Pattern.compile(" baronesse "                     ), " ");
        //  nicht normalisiert: Baronin
        namePatterns.put(Pattern.compile(" baronin "                       ), " ");
        //  nicht normalisiert: Brand
        namePatterns.put(Pattern.compile(" brand "                         ), " ");
        //  nicht normalisiert: Burggraf
        namePatterns.put(Pattern.compile(" burggraf "                      ), " ");
        //  nicht normalisiert: Burggräfin
        namePatterns.put(Pattern.compile(" burggraefin "                   ), " ");
        //  nicht normalisiert: Condesa
        namePatterns.put(Pattern.compile(" condesa "                       ), " ");
        //  nicht normalisiert: Earl
        namePatterns.put(Pattern.compile(" earl "                          ), " ");
        //  nicht normalisiert: Edle
        namePatterns.put(Pattern.compile(" edle "                          ), " ");
        //  nicht normalisiert: Edler
        namePatterns.put(Pattern.compile(" edler "                         ), " ");
        //  nicht normalisiert: Erbgraf
        namePatterns.put(Pattern.compile(" erbgraf "                       ), " ");
        //  nicht normalisiert: Erbgräfin
        namePatterns.put(Pattern.compile(" erbgraefin "                    ), " ");
        //  nicht normalisiert: Erbprinz
        namePatterns.put(Pattern.compile(" erbprinz "                      ), " ");
        //  nicht normalisiert: Erbprinzessin
        namePatterns.put(Pattern.compile(" erbprinzessin "                 ), " ");
        //  nicht normalisiert: Ffr
        namePatterns.put(Pattern.compile(" ffr "                           ), " ");
        //  nicht normalisiert: Freifr
        namePatterns.put(Pattern.compile(" freifr "                        ), " ");
        //  nicht normalisiert: Freifräulein
        namePatterns.put(Pattern.compile(" freifraeulein "                 ), " ");
        //  nicht normalisiert: Freifrau
        namePatterns.put(Pattern.compile(" freifrau "                      ), " ");
        //  nicht normalisiert: Freih
        namePatterns.put(Pattern.compile(" freih "                         ), " ");
        //  nicht normalisiert: Freiherr
        namePatterns.put(Pattern.compile(" freiherr "                      ), " ");
        //  nicht normalisiert: Freiin
        namePatterns.put(Pattern.compile(" freiin "                        ), " ");
        //  nicht normalisiert: Frf
        namePatterns.put(Pattern.compile(" frf "                           ), " ");
        //  nicht normalisiert: Frf.
        namePatterns.put(Pattern.compile(" frf "                           ), " ");
        //  nicht normalisiert: Frfr
        namePatterns.put(Pattern.compile(" frfr "                          ), " ");
        //  nicht normalisiert: Frfr.
        namePatterns.put(Pattern.compile(" frfr "                          ), " ");
        //  nicht normalisiert: Frh
        namePatterns.put(Pattern.compile(" frh "                           ), " ");
        //  nicht normalisiert: Frh.
        namePatterns.put(Pattern.compile(" frh "                           ), " ");
        //  nicht normalisiert: Frhr
        namePatterns.put(Pattern.compile(" frhr "                          ), " ");
        //  nicht normalisiert: Frhr.
        namePatterns.put(Pattern.compile(" frhr "                          ), " ");
        //  nicht normalisiert: Fst
        namePatterns.put(Pattern.compile(" fst "                           ), " ");
        //  nicht normalisiert: Fst.
        namePatterns.put(Pattern.compile(" fst "                           ), " ");
        //  nicht normalisiert: Fstn
        namePatterns.put(Pattern.compile(" fstn "                          ), " ");
        //  nicht normalisiert: Fstn.
        namePatterns.put(Pattern.compile(" fstn "                          ), " ");
        //  nicht normalisiert: Fürst
        namePatterns.put(Pattern.compile(" fuerst "                        ), " ");
        //  nicht normalisiert: Fürstin
        namePatterns.put(Pattern.compile(" fuerstin "                      ), " ");
        //  nicht normalisiert: Gr
        namePatterns.put(Pattern.compile(" gr "                            ), " ");
        //  nicht normalisiert: Graf
        namePatterns.put(Pattern.compile(" graf "                          ), " ");
        //  nicht normalisiert: Gräfin
        namePatterns.put(Pattern.compile(" graefin "                       ), " ");
        //  nicht normalisiert: Grf
        namePatterns.put(Pattern.compile(" grf "                           ), " ");
        //  nicht normalisiert: Grfn
        namePatterns.put(Pattern.compile(" grfn "                          ), " ");
        //  nicht normalisiert: Grossherzog
        namePatterns.put(Pattern.compile(" grossherzog "                   ), " ");
        //  nicht normalisiert: Großherzog
        namePatterns.put(Pattern.compile(" grossherzog "                   ), " ");
        //  nicht normalisiert: Grossherzogin
        namePatterns.put(Pattern.compile(" grossherzogin "                 ), " ");
        //  nicht normalisiert: Großherzogin
        namePatterns.put(Pattern.compile(" grossherzogin "                 ), " ");
        //  nicht normalisiert: Herzog
        namePatterns.put(Pattern.compile(" herzog "                        ), " ");
        //  nicht normalisiert: Herzogin
        namePatterns.put(Pattern.compile(" herzogin "                      ), " ");
        //  nicht normalisiert: Jhr
        namePatterns.put(Pattern.compile(" jhr "                           ), " ");
        //  nicht normalisiert: Jhr.
        namePatterns.put(Pattern.compile(" jhr "                           ), " ");
        //  nicht normalisiert: Jonkheer
        namePatterns.put(Pattern.compile(" jonkheer "                      ), " ");
        //  nicht normalisiert: Junker
        namePatterns.put(Pattern.compile(" junker "                        ), " ");
        //  nicht normalisiert: Landgraf
        namePatterns.put(Pattern.compile(" landgraf "                      ), " ");
        //  nicht normalisiert: Landgräfin
        namePatterns.put(Pattern.compile(" landgraefin "                   ), " ");
        //  nicht normalisiert: Markgraf
        namePatterns.put(Pattern.compile(" markgraf "                      ), " ");
        //  nicht normalisiert: Markgräfin
        namePatterns.put(Pattern.compile(" markgraefin "                   ), " ");
        //  nicht normalisiert: Marques
        namePatterns.put(Pattern.compile(" marques "                       ), " ");
        //  nicht normalisiert: Marquis
        namePatterns.put(Pattern.compile(" marquis "                       ), " ");
        //  nicht normalisiert: Marschall
        namePatterns.put(Pattern.compile(" marschall "                     ), " ");
        //  nicht normalisiert: Ostoja
        namePatterns.put(Pattern.compile(" ostoja "                        ), " ");
        //  nicht normalisiert: Prinz
        namePatterns.put(Pattern.compile(" prinz "                         ), " ");
        //  nicht normalisiert: Prinzessin
        namePatterns.put(Pattern.compile(" prinzessin "                    ), " ");
        //  nicht normalisiert: Przin
        namePatterns.put(Pattern.compile(" przin "                         ), " ");
        //  nicht normalisiert: Rabe
        namePatterns.put(Pattern.compile(" rabe "                          ), " ");
        //  nicht normalisiert: Reichsgraf
        namePatterns.put(Pattern.compile(" reichsgraf "                    ), " ");
        //  nicht normalisiert: Reichsgräfin
        namePatterns.put(Pattern.compile(" reichsgraefin "                 ), " ");
        //  nicht normalisiert: Ritter
        namePatterns.put(Pattern.compile(" ritter "                        ), " ");
        //  nicht normalisiert: Rr
        namePatterns.put(Pattern.compile(" rr "                            ), " ");
        //  nicht normalisiert: Truchsess
        namePatterns.put(Pattern.compile(" truchsess "                     ), " ");
        //  nicht normalisiert: Truchseß
        namePatterns.put(Pattern.compile(" truchsess "                     ), " ");


        /**
         * Alle derzeit gültigen Vorsatzworte (siehe WITA-2011):
         * ACHTUNG: diese sind bereits normalisiert!!!
         *
         * => sortiert nach den meist enthaltenen Leerzeichen, damit verhindert wird,
         *    dass z.B. 'und' das matching für 'von und zu' kapputt macht.
         */
        namePatterns.put(Pattern.compile(" von und zu der "                 ), " ");
        namePatterns.put(Pattern.compile(" vom und zu "                     ), " ");
        namePatterns.put(Pattern.compile(" von und zu "                     ), " ");
        namePatterns.put(Pattern.compile(" von und zur "                    ), " ");
        namePatterns.put(Pattern.compile(" aan de "                         ), " ");
        namePatterns.put(Pattern.compile(" aan den "                        ), " ");
        namePatterns.put(Pattern.compile(" an der "                         ), " ");
        namePatterns.put(Pattern.compile(" auf dem "                        ), " ");
        namePatterns.put(Pattern.compile(" auf der "                        ), " ");
        namePatterns.put(Pattern.compile(" auf m "                          ), " ");
        namePatterns.put(Pattern.compile(" auff m "                         ), " ");
        namePatterns.put(Pattern.compile(" aus dem "                        ), " ");
        namePatterns.put(Pattern.compile(" aus den "                        ), " ");
        namePatterns.put(Pattern.compile(" aus der "                        ), " ");
        namePatterns.put(Pattern.compile(" bei der "                        ), " ");
        namePatterns.put(Pattern.compile(" bey der "                        ), " ");
        namePatterns.put(Pattern.compile(" da costa "                       ), " ");
        namePatterns.put(Pattern.compile(" da las "                         ), " ");
        namePatterns.put(Pattern.compile(" da silva "                       ), " ");
        namePatterns.put(Pattern.compile(" de la "                          ), " ");
        namePatterns.put(Pattern.compile(" de las "                         ), " ");
        namePatterns.put(Pattern.compile(" de le "                          ), " ");
        namePatterns.put(Pattern.compile(" de los "                         ), " ");
        namePatterns.put(Pattern.compile(" del coz "                        ), " ");
        namePatterns.put(Pattern.compile(" do ceu "                         ), " ");
        namePatterns.put(Pattern.compile(" don le "                         ), " ");
        namePatterns.put(Pattern.compile(" dos santos "                     ), " ");
        namePatterns.put(Pattern.compile(" in de "                          ), " ");
        namePatterns.put(Pattern.compile(" in den "                         ), " ");
        namePatterns.put(Pattern.compile(" in der "                         ), " ");
        namePatterns.put(Pattern.compile(" in het "                         ), " ");
        namePatterns.put(Pattern.compile(" op de "                          ), " ");
        namePatterns.put(Pattern.compile(" op den "                         ), " ");
        namePatterns.put(Pattern.compile(" op gen "                         ), " ");
        namePatterns.put(Pattern.compile(" op het "                         ), " ");
        namePatterns.put(Pattern.compile(" op te "                          ), " ");
        namePatterns.put(Pattern.compile(" op ten "                         ), " ");
        namePatterns.put(Pattern.compile(" v d "                            ), " ");
        namePatterns.put(Pattern.compile(" v dem "                          ), " ");
        namePatterns.put(Pattern.compile(" v den "                          ), " ");
        namePatterns.put(Pattern.compile(" v der "                          ), " ");
        namePatterns.put(Pattern.compile(" van de "                         ), " ");
        namePatterns.put(Pattern.compile(" van dem "                        ), " ");
        namePatterns.put(Pattern.compile(" van den "                        ), " ");
        namePatterns.put(Pattern.compile(" van der "                        ), " ");
        namePatterns.put(Pattern.compile(" van gen "                        ), " ");
        namePatterns.put(Pattern.compile(" van het "                        ), " ");
        namePatterns.put(Pattern.compile(" van t "                          ), " ");
        namePatterns.put(Pattern.compile(" ven der "                        ), " ");
        namePatterns.put(Pattern.compile(" von de "                         ), " ");
        namePatterns.put(Pattern.compile(" von dem "                        ), " ");
        namePatterns.put(Pattern.compile(" von den "                        ), " ");
        namePatterns.put(Pattern.compile(" von der "                        ), " ");
        namePatterns.put(Pattern.compile(" von einem "                      ), " ");
        namePatterns.put(Pattern.compile(" von la "                         ), " ");
        namePatterns.put(Pattern.compile(" von mast "                       ), " ");
        namePatterns.put(Pattern.compile(" von zu "                         ), " ");
        namePatterns.put(Pattern.compile(" von zum "                        ), " ");
        namePatterns.put(Pattern.compile(" von zur "                        ), " ");
        namePatterns.put(Pattern.compile(" vor dem "                        ), " ");
        namePatterns.put(Pattern.compile(" vor den "                        ), " ");
        namePatterns.put(Pattern.compile(" vor der "                        ), " ");
        namePatterns.put(Pattern.compile(" y dela "                         ), " ");
        namePatterns.put(Pattern.compile(" al "                             ), " ");
        namePatterns.put(Pattern.compile(" am "                             ), " ");
        namePatterns.put(Pattern.compile(" an "                             ), " ");
        namePatterns.put(Pattern.compile(" auf "                            ), " ");
        namePatterns.put(Pattern.compile(" aufm "                           ), " ");
        namePatterns.put(Pattern.compile(" aus "                            ), " ");
        namePatterns.put(Pattern.compile(" b "                              ), " ");
        namePatterns.put(Pattern.compile(" be "                             ), " ");
        namePatterns.put(Pattern.compile(" bei "                            ), " ");
        namePatterns.put(Pattern.compile(" beim "                           ), " ");
        namePatterns.put(Pattern.compile(" ben "                            ), " ");
        namePatterns.put(Pattern.compile(" bey "                            ), " ");
        namePatterns.put(Pattern.compile(" che "                            ), " ");
        namePatterns.put(Pattern.compile(" cid "                            ), " ");
        namePatterns.put(Pattern.compile(" d "                              ), " ");
        namePatterns.put(Pattern.compile(" da "                             ), " ");
        namePatterns.put(Pattern.compile(" dal "                            ), " ");
        namePatterns.put(Pattern.compile(" dall "                           ), " ");
        namePatterns.put(Pattern.compile(" dalla "                          ), " ");
        namePatterns.put(Pattern.compile(" dalle "                          ), " ");
        namePatterns.put(Pattern.compile(" dallo "                          ), " ");
        namePatterns.put(Pattern.compile(" das "                            ), " ");
        namePatterns.put(Pattern.compile(" de "                             ), " ");
        namePatterns.put(Pattern.compile(" degli "                          ), " ");
        namePatterns.put(Pattern.compile(" dei "                            ), " ");
        namePatterns.put(Pattern.compile(" del "                            ), " ");
        namePatterns.put(Pattern.compile(" deli "                           ), " ");
        namePatterns.put(Pattern.compile(" dell "                           ), " ");
        namePatterns.put(Pattern.compile(" della "                          ), " ");
        namePatterns.put(Pattern.compile(" delle "                          ), " ");
        namePatterns.put(Pattern.compile(" delli "                          ), " ");
        namePatterns.put(Pattern.compile(" dello "                          ), " ");
        namePatterns.put(Pattern.compile(" den "                            ), " ");
        namePatterns.put(Pattern.compile(" der "                            ), " ");
        namePatterns.put(Pattern.compile(" des "                            ), " ");
        namePatterns.put(Pattern.compile(" di "                             ), " ");
        namePatterns.put(Pattern.compile(" dit "                            ), " ");
        namePatterns.put(Pattern.compile(" do "                             ), " ");
        namePatterns.put(Pattern.compile(" don "                            ), " ");
        namePatterns.put(Pattern.compile(" dos "                            ), " ");
        namePatterns.put(Pattern.compile(" du "                             ), " ");
        namePatterns.put(Pattern.compile(" dy "                             ), " ");
        namePatterns.put(Pattern.compile(" el "                             ), " ");
        namePatterns.put(Pattern.compile(" g "                              ), " ");
        namePatterns.put(Pattern.compile(" gen "                            ), " ");
        namePatterns.put(Pattern.compile(" gil "                            ), " ");
        namePatterns.put(Pattern.compile(" gli "                            ), " ");
        namePatterns.put(Pattern.compile(" grosse "                         ), " ");
        namePatterns.put(Pattern.compile(" i "                              ), " ");
        namePatterns.put(Pattern.compile(" im "                             ), " ");
        namePatterns.put(Pattern.compile(" in "                             ), " ");
        namePatterns.put(Pattern.compile(" int "                            ), " ");
        namePatterns.put(Pattern.compile(" kl "                             ), " ");
        namePatterns.put(Pattern.compile(" kleine "                         ), " ");
        namePatterns.put(Pattern.compile(" l "                              ), " ");
        namePatterns.put(Pattern.compile(" la "                             ), " ");
        namePatterns.put(Pattern.compile(" le "                             ), " ");
        namePatterns.put(Pattern.compile(" lee "                            ), " ");
        namePatterns.put(Pattern.compile(" li "                             ), " ");
        namePatterns.put(Pattern.compile(" lo "                             ), " ");
        namePatterns.put(Pattern.compile(" m "                              ), " ");
        namePatterns.put(Pattern.compile(" mac "                            ), " ");
        namePatterns.put(Pattern.compile(" mc "                             ), " ");
        namePatterns.put(Pattern.compile(" n "                              ), " ");
        namePatterns.put(Pattern.compile(" o "                              ), " ");
        namePatterns.put(Pattern.compile(" op "                             ), " ");
        namePatterns.put(Pattern.compile(" oude "                           ), " ");
        namePatterns.put(Pattern.compile(" pla "                            ), " ");
        namePatterns.put(Pattern.compile(" pro "                            ), " ");
        namePatterns.put(Pattern.compile(" s "                              ), " ");
        namePatterns.put(Pattern.compile(" st "                             ), " ");
        namePatterns.put(Pattern.compile(" t "                              ), " ");
        namePatterns.put(Pattern.compile(" te "                             ), " ");
        namePatterns.put(Pattern.compile(" ten "                            ), " ");
        namePatterns.put(Pattern.compile(" ter "                            ), " ");
        namePatterns.put(Pattern.compile(" thi "                            ), " ");
        namePatterns.put(Pattern.compile(" tho "                            ), " ");
        namePatterns.put(Pattern.compile(" thom "                           ), " ");
        namePatterns.put(Pattern.compile(" thor "                           ), " ");
        namePatterns.put(Pattern.compile(" thum "                           ), " ");
        namePatterns.put(Pattern.compile(" to "                             ), " ");
        namePatterns.put(Pattern.compile(" tom "                            ), " ");
        namePatterns.put(Pattern.compile(" tor "                            ), " ");
        namePatterns.put(Pattern.compile(" tu "                             ), " ");
        namePatterns.put(Pattern.compile(" tum "                            ), " ");
        namePatterns.put(Pattern.compile(" unten "                          ), " ");
        namePatterns.put(Pattern.compile(" unter "                          ), " ");
        namePatterns.put(Pattern.compile(" unterm "                         ), " ");
        namePatterns.put(Pattern.compile(" v "                              ), " ");
        namePatterns.put(Pattern.compile(" van "                            ), " ");
        namePatterns.put(Pattern.compile(" vande "                          ), " ");
        namePatterns.put(Pattern.compile(" vandem "                         ), " ");
        namePatterns.put(Pattern.compile(" vanden "                         ), " ");
        namePatterns.put(Pattern.compile(" vander "                         ), " ");
        namePatterns.put(Pattern.compile(" ven "                            ), " ");
        namePatterns.put(Pattern.compile(" ver "                            ), " ");
        namePatterns.put(Pattern.compile(" vo "                             ), " ");
        namePatterns.put(Pattern.compile(" vom "                            ), " ");
        namePatterns.put(Pattern.compile(" von "                            ), " ");
        namePatterns.put(Pattern.compile(" vonde "                          ), " ");
        namePatterns.put(Pattern.compile(" vonden "                         ), " ");

        //some special name patterns
        namePatterns.put(Pattern.compile(" und "                    ), " ");
        namePatterns.put(Pattern.compile(" u "                      ), " ");
        namePatterns.put(Pattern.compile(" & "                      ), " ");

        //@formatter:on

        return namePatterns;
    }

}
