----------------------------------
-- KUP-332 Basisview
----------------------------------

CREATE OR REPLACE FORCE VIEW V_RESTL_BESTELL as
SELECT
       (SELECT MAX (c.CB_ID)
        FROM t_carrierbestellung c
        WHERE SUBSTR (
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
                                                              REPLACE (c.LBZ,
                                                                       '/0000',
                                                                       '/'),
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
              ) =
                 SUBSTR (
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
                                                                    a.B009_3,
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
                 ))
          AS CB_ID,
       a.B001_5 AS BEZEICHNUNG_MNET,
       TO_DATE (a.B002_4,
                'yyyymmdd')
          AS VORGABE_MNET,
       a.B017_2 AS BEMERKUNG_MNET,
       '0' ||
       a.B001_4 AS CARRIER_REF_NR,
       a.TBS_TS_SND AS SUBMITTED_AT,
       TO_DATE (NVL (a.B005_4, a.B002_4),
                'yyyymmdd')
          AS RET_REAL_DATE,
       a.TBS_TS AS ANSWERED_AT,
       a.tbs_recipient as carrier_kennung_abs,
       a.TBS_TBS_FIRST_ID as exm_id,
       a.TBS_TBV_ID,
-----------------------------------------------------------------------
--      b.exm_id,
       f_tbs_datei_2_ts (a.TBS_DATEI) AS ts,
       --to_date(NVL(a.B005_4,a.B002_4), 'yyyymmdd') as termin_zum,
--       b.status,
--       b.usecase_id,
--       b.exm_tbv_id,
--       b.str_value,
       a.TBS_TBS_FIRST_ID,
       a.B001_6,
       a.B009_2,
       a.B009_3,
       a.B009_11,
       a.B009_12,
       a.B009_13,
       a.B005_ID,
       a.B005_2,
       a.B005_3,
       a.B005_4,
       a.TBS_ID,
       a.TBS_AUF_ID,
       a.TBS_LTG_ID,
       a.TBS_SRC_ID,
       a.TBS_TBS_ID,
       a.TBS_STATUS,
       a.TBS_DATEI,
       a.TBS_SENDER,
       a.TBS_RECIPIENT,
       a.TBS_SENDEVERSUCH,
       a.TBS_TS,
--      a.B017_2,
       a.TBS_ID_SND,
       a.TBS_TS_SND,
       a.TFE_ID,
       a.TFE_KLASSE,
       a.TFE_BESCHREIBUNG,
       a.B002_ID,
       a.B002_2,
       a.B002_4,
       a.B001_ID,
       a.B001_2,
       a.B001_4,
       a.B001_5,
       a.B015_ID,
       a.B015_2,
       a.B015_3,
       a.B015_4,
       a.B015_5,
       a.B015_6,
       a.B015_ID_SND,
       a.B015_2_SND,
       a.B015_3_SND,
       a.B015_4_SND,
       a.B015_5_SND,
       a.B015_6_SND,
       a.AUF_NR,
       A.PORTINFO
FROM                                      -- mnetcall.TTALBESTELLUNG@kupvis c,
     -- T_CB_VORGANG b,
     (SELECT b.id,
             c.exm_tbv_id,
             c.str_value,
             a.*
      FROM                 ---------------------------------------------------
           (SELECT bb.id,
                   bb.exm_tbv_id,
                   aa.str_value
            FROM t_cb_usecase bb,
                 T_REFERENCE aa
            WHERE aa.ID = bb.reference_id) c,
           T_REFERENCE b,
           T_CB_VORGANG a
      WHERE a.USECASE_ID = c.ID AND a.status = b.id) b,
     mnetcall.V1TTALBEST_NACHMIGRATE@kupvis A
WHERE
--    A.TBS_AUF_ID IN (793840, 713218) AND
      A.tbs_auf_id IS NOT NULL AND
      a.tbs_status <> 6 AND
      a.tbs_auf_id IS NOT NULL AND
      a.tbs_tbs_first_id = b.exm_id(+) AND
      b.exm_id IS NULL AND
      a.tbs_id IN (SELECT x.tbs_id
                   FROM mnetcall.V1TTALBESTELLUNG_S_MAX_I1@kupvis x)
/
