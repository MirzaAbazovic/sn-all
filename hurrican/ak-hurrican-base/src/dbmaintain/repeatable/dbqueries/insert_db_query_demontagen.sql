delete from T_DB_QUERIES where ID in (20,21,22,23);

Insert into T_DB_QUERIES
   (ID, NAME, DESCRIPTION, SERVICE, SQL_QUERY,
    PARAMS, VERSION)
 Values
   (20, 'Demontagen', 'Ermittlung der durchzufuehrenden Demontagen.', 'CC',
   'SELECT l.CUSTOMER_NO as KUNDE__NO, l.DEB_ID as DEBITOR_ID, l.TAIFUN_ORDER__NO as TAIFUN_ORDER__NO,
    l.AUFTRAG_ID as AUFTRAG_ID, e.NAME as ENDSTELLEN_NAME
    from T_LOCK l
    join   T_AUFTRAG_TECHNIK t on L.AUFTRAG_ID = T.AUFTRAG_ID
    left join T_ENDSTELLE e on T.AT_2_ES_ID=E.ES_GRUPPE
    where l.LOCK_MODE_REF_ID=1504 and l.LOCK_STATE_REF_ID=1510 and (E.ES_TYP is null or e.ES_TYP=''B'')',
    NULL, 0);

Insert into T_DB_QUERIES
   (ID, NAME, DESCRIPTION, SERVICE, SQL_QUERY,
    PARAMS, VERSION)
 Values
   (21, 'Demontagen NL Augsburg', 'Ermittlung der durchzufuehrenden Demontagen für NL Augsburg.', 'CC',
   'SELECT   l.CUSTOMER_NO AS KUNDE__NO,
         l.DEB_ID AS DEBITOR_ID,
         l.TAIFUN_ORDER__NO AS TAIFUN_ORDER__NO,
         l.AUFTRAG_ID AS AUFTRAG_ID,
         e.NAME as ENDSTELLEN_NAME
  FROM   T_LOCK l
  join   T_AUFTRAG_TECHNIK t on L.AUFTRAG_ID = T.AUFTRAG_ID
  left join T_ENDSTELLE e on T.AT_2_ES_ID=E.ES_GRUPPE
 WHERE   l.LOCK_MODE_REF_ID = 1504 AND l.LOCK_STATE_REF_ID = 1510 AND T.NIEDERLASSUNG_ID IN (1,2) and (E.ES_TYP is null or e.ES_TYP=''B'')',
    NULL, 0);

Insert into T_DB_QUERIES
   (ID, NAME, DESCRIPTION, SERVICE, SQL_QUERY,
    PARAMS, VERSION)
 Values
   (22, 'Demontagen NL München', 'Ermittlung der durchzufuehrenden Demontagen für NL München.', 'CC',
   'SELECT   l.CUSTOMER_NO AS KUNDE__NO,
         l.DEB_ID AS DEBITOR_ID,
         l.TAIFUN_ORDER__NO AS TAIFUN_ORDER__NO,
         l.AUFTRAG_ID AS AUFTRAG_ID,
         e.NAME as ENDSTELLEN_NAME
  FROM   T_LOCK l
  join   T_AUFTRAG_TECHNIK t on L.AUFTRAG_ID = T.AUFTRAG_ID
  left join T_ENDSTELLE e on T.AT_2_ES_ID=E.ES_GRUPPE
 WHERE   l.LOCK_MODE_REF_ID = 1504 AND l.LOCK_STATE_REF_ID = 1510 AND T.NIEDERLASSUNG_ID IN (3,5) and (E.ES_TYP is null or e.ES_TYP=''B'')',
    NULL, 0);

Insert into T_DB_QUERIES
   (ID, NAME, DESCRIPTION, SERVICE, SQL_QUERY,
    PARAMS, VERSION)
 Values
   (23, 'Demontagen NL Nürnberg', 'Ermittlung der durchzufuehrenden Demontagen für NL Nürnberg.', 'CC',
   'SELECT   l.CUSTOMER_NO AS KUNDE__NO,
         l.DEB_ID AS DEBITOR_ID,
         l.TAIFUN_ORDER__NO AS TAIFUN_ORDER__NO,
         l.AUFTRAG_ID AS AUFTRAG_ID,
         e.NAME as ENDSTELLEN_NAME
  FROM   T_LOCK l
  join   T_AUFTRAG_TECHNIK t on L.AUFTRAG_ID = T.AUFTRAG_ID
  left join T_ENDSTELLE e on T.AT_2_ES_ID=E.ES_GRUPPE
 WHERE   l.LOCK_MODE_REF_ID = 1504 AND l.LOCK_STATE_REF_ID = 1510 AND T.NIEDERLASSUNG_ID IN (4) and (E.ES_TYP is null or e.ES_TYP=''B'')',
    NULL, 0);
