--
-- VIEW mit Zugriff auf die KuP-Tabelle TDA_BESTELLUNGEN
--

CREATE OR REPLACE FORCE VIEW VKUP_TDA_BESTELLUNGEN AS
   SELECT a."LBZ_COMP",
          a."C2T_ID",
          a."AUFTRAG_ID_HURRICAN",
          a."VERBINDUNGSNUMMER",
          a."ASB",
          a."ONKZ",
          a."BESTELLT_AM",
          a."VORGABEDATUM",
          a."ZURUECK_AM",
          a."BEREITSTELLUNG_AM",
          a."LBZ",
          a."VTRNR",
          a."AQS",
          a."LL",
          a."TPU_UEVT_SCHRANK",
          a."TDA_EV",
          a."TDA_DA",
          a."TDA_DRAHT",
          a."TDA_UEBTRAGVERFAHREN",
          a."TDA_GLASFASER",
          a."TDA_PLZ",
          a."TDA_ORT",
          a."TDA_STRASSE",
          a."TDA_HAUSNR"
   FROM (SELECT SUBSTR (
                   REPLACE (
                      REPLACE (
                         REPLACE (
                            REPLACE (
                               REPLACE (
                                  REPLACE (
                                     REPLACE (
                                        REPLACE (
                                           REPLACE (
                                              REPLACE (
                                                 REPLACE (
                                                    REPLACE (
                                                       REPLACE (
                                                          REPLACE (
                                                             REPLACE (
                                                                REPLACE (
                                                                   a.LBZ,
                                                                   '/0000',
                                                                   '/'
                                                                ),
                                                                '/000',
                                                                '/'
                                                             ),
                                                             '/00',
                                                             '/'
                                                          ),
                                                          '/0',
                                                          '/'
                                                       ),
                                                       '/89001',
                                                       '/89000'
                                                    ),
                                                    '/89002',
                                                    '/89000'
                                                 ),
                                                 '/89003',
                                                 '/89000'
                                              ),
                                              '/89004',
                                              '/89000'
                                           ),
                                           '/89005',
                                           '/89000'
                                        ),
                                        '/89006',
                                        '/89000'
                                     ),
                                     '/89007',
                                     '/89000'
                                  ),
                                  '/89008',
                                  '/89000'
                               ),
                               '/89009',
                               '/89000'
                            ),
                            '000/',
                            '/'
                         ),
                         '00/',
                         '/'
                      ),
                      '0/',
                      '/'
                   ),
                   1,
                   40
                )
                   AS lbz_comp
         FROM t_carrierbestellung a) b,
        (SELECT SUBSTR (
                   REPLACE (
                      REPLACE (
                         REPLACE (
                            REPLACE (
                               REPLACE (
                                  REPLACE (
                                     REPLACE (
                                        REPLACE (
                                           REPLACE (
                                              REPLACE (
                                                 REPLACE (
                                                    REPLACE (
                                                       REPLACE (
                                                          REPLACE (
                                                             REPLACE (
                                                                REPLACE (
                                                                   e.tda_bez,
                                                                   '/0000',
                                                                   '/'
                                                                ),
                                                                '/000',
                                                                '/'
                                                             ),
                                                             '/00',
                                                             '/'
                                                          ),
                                                          '/0',
                                                          '/'
                                                       ),
                                                       '/89001',
                                                       '/89000'
                                                    ),
                                                    '/89002',
                                                    '/89000'
                                                 ),
                                                 '/89003',
                                                 '/89000'
                                              ),
                                              '/89004',
                                              '/89000'
                                           ),
                                           '/89005',
                                           '/89000'
                                        ),
                                        '/89006',
                                        '/89000'
                                     ),
                                     '/89007',
                                     '/89000'
                                  ),
                                  '/89008',
                                  '/89000'
                               ),
                               '/89009',
                               '/89000'
                            ),
                            '000/',
                            '/'
                         ),
                         '00/',
                         '/'
                      ),
                      '0/',
                      '/'
                   ),
                   1,
                   40
                )
                   AS LBZ_COMP,
                A.C2T_ID,
                b.AUFTRAG_ID_HURRICAN,
                c.VERBINDUNGSNUMMER,
                f.ovo_asb AS asb,
                f.ovo_onkz AS onkz,
                e.tda_bestelldatum AS BESTELLT_AM,
                NVL (
                   NVL (NVL (e.tda_termin, e.tda_bestelldatum),
                        E.TDA_BESTAETIGT_ZUM),
                   E.TDA_KUENDIGUNG_ZUM
                )
                   AS VORGABEDATUM,
                NVL (E.TDA_BESTAETIGT_ZUM, E.TDA_KUENDIGUNG_ZUM) AS ZURUECK_AM,
                NVL (E.TDA_BESTAETIGT_ZUM, E.TDA_KUENDIGUNG_ZUM)
                   AS BEREITSTELLUNG_AM,
                -- null as KUNDE_VOR_ORT,
                e.tda_bez AS LBZ,
                e.tda_vertragsnr AS VTRNR,
                e.QUERSCHNITT AS AQS,
                DECODE (e.leitungslaenge,
                        'keine Angaben', NULL,
                        e.leitungslaenge)
                   AS LL,
                g.TPU_UEVT_SCHRANK,
                DECODE (g.TPU_UEVT_DA, NULL, e.tda_ev, g.TPU_UEVT_BUCHT)
                   AS tda_ev,
                DECODE (g.TPU_UEVT_DA, NULL, e.TDA_DA, g.TPU_UEVT_DA) AS TDA_DA,
                e.TDA_DRAHT,
                e.TDA_UEBTRAGVERFAHREN,
                e.TDA_GLASFASER,
                e.tda_plz,
                e.tda_ort,
                e.tda_strasse,
                e.tda_hausnr
         -- g.TPU_UEVT_DA
         FROM mnetcall.VINS_TPORT_SDH_PDH_ALL@kupvis g,
              mnetcall.tovststandort@kupvis f,
              mnetcall.TDA_BESTELLUNGEN@kupvis e,
              technik.auftrag@kupvis d,
              technik.verbindung@kupvis c,
              MIG_AUFTRAG_MAPPING b,
              --   hurrican.MIG_AUFTRAG_MAPPING@hurrican b,
              mnetcall.T_MIG_CONN_2_TAIFUN@kupvis a
         WHERE               --    c.VERBINDUNGSNUMMER  = 'clt137.057.001' and
                                     --    c.VERBINDUNGSNUMMER like 'gva%' and
        c.VERBINDUNGSNUMMER = g.TPU_VERB_NR AND
        --    g.TPU_VERB_NR is NULL and
        e.TDA_LOESCHLISTE = 0 AND
        b.is_connect_auftrag = 1 AND
        e.tda_asb = f.ovo_id(+) AND
        c.VERBINDUNGSNUMMER = e.TDA_LEITUNGSNR AND
        a.C2T_AUFTRAG_ID = d.ID AND
        a.C2T_VERB_ID = c.id AND
        a.c2t_id = b.AUFTRAG_ID_KUP) A
   WHERE a.lbz_comp = b.LBZ_COMP(+) AND b.LBZ_COMP IS NULL
   ORDER BY a.lbz_comp,
            a.VERBINDUNGSNUMMER,
            A.C2T_ID DESC,
            a.AUFTRAG_ID_HURRICAN
/

GRANT SELECT ON VKUP_TDA_BESTELLUNGEN TO R_HURRICAN_USER
/
