CREATE OR REPLACE FORCE VIEW V_MIG_CB_KONSOL AS
SELECT   a.auftrag_id AS auftrag_id_connect,
         b.auftrag_id AS auftrag_id_adsl,
         a.dest_value AS cb_vorgang_id_connect,
         b.dest_value AS cb_vorgang_id_adsl,
         a.cb_id AS cb_id_connect,
         b.cb_id AS cb_id_adsl,
         a.id AS es_id_connect,
         b.id AS es_id_adsl,
         a.lbz as lbz
  FROM      (SELECT   L2.DEST_VALUE,
                      V2.AUFTRAG_ID,
                      CB2.CB_ID,
                      ES2.ID,
                      CB2.LBZ
               FROM            MIG_LOG l2
                            JOIN
                               T_CB_VORGANG v2
                            ON L2.DEST_VALUE = v2.id
                         JOIN
                            T_CARRIERBESTELLUNG cb2
                         ON CB2.CB_ID = V2.CB_ID
                      JOIN
                         T_ENDSTELLE es2
                      ON ES2.CB_2_ES_ID = CB2.CB_2_ES_ID
              WHERE   L2.MIGRESULT_ID = 1452 AND l2.DEST_VALUE IS NOT NULL) a
         JOIN
            (SELECT   L.DEST_VALUE,
                      V.AUFTRAG_ID,
                      CB.CB_ID,
                      ES.ID,
                      CB.LBZ
               FROM            MIG_LOG l
                            JOIN
                               T_CB_VORGANG v
                            ON L.DEST_VALUE = v.id
                         JOIN
                            T_CARRIERBESTELLUNG cb
                         ON CB.CB_ID = V.CB_ID
                      JOIN
                         T_ENDSTELLE es
                      ON ES.CB_2_ES_ID = CB.CB_2_ES_ID
              WHERE   l.MIGRESULT_ID = 1440 AND DEST_VALUE IS NOT NULL) b
         ON a.lbz = b.lbz;



GRANT SELECT ON V_MIG_CB_KONSOL TO R_HURRICAN_USER;
