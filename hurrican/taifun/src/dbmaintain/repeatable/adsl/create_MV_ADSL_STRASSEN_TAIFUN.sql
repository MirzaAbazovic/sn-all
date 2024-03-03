--
-- Legt einen View fuer die Migration der Strassen an
--
CREATE OR REPLACE FORCE VIEW MV_ADSL_STRASSEN_TAIFUN
AS
   SELECT
          a.street_section_no AS STREET_SECTION_NO,
          d.asb AS ASB,
          d.kvz_nummer AS KVZ_NUMMER,
          d.onkz AS ONKZ,
          c.city AS ORT,
          NULL AS ORTSZUSATZ,
          b.street AS STRASSE,
          DECODE (
             TRIM (A.NUMBERING_KIND) ||
             '_' ||
             A.HOUSE_NUM_START ||
             '_' ||
             A.HOUSE_NUM_END,
             'all_1_9999',
             NULL,
             A.HOUSE_NUM_START ||
             DECODE (A.HOUSE_NUM_END,
                     NULL, NULL,
                     '-' ||
                     A.HOUSE_NUM_END) ||
             DECODE (
                TRIM (A.NUMBERING_KIND),
                'all',
                NULL,
                DECODE (TRIM (A.NUMBERING_KIND),
                        'even', ' G',
                        'odd', ' U',
                        '')
             )
          )
             AS ABSCHNITT,
          c.zip_code AS PLZ,
          -- replace(ltrim(to_char(NVL(ROUND (a.distance / 1000, 2),0),'999990.99')), '.', ',') AS ENTFERNUNG_DEC,
          NVL (ROUND (a.distance / 1000,
                      2),
               0)
             AS ENTFERNUNG_DEC,
          SYSDATE AS last_change
   FROM service_room d,
        geo_city c,
        geo_street b,
        street_section a
   WHERE
         d.area_no IN (2, 7, 9, 10) AND
         D.TAG_CONNTYPE_NO in (1,2,16) AND
         A.TAG_CONNTYPE_NO in (1,16) AND
         a.service_room_no = d.service_room_no AND
         B.GEO_CITY_NO = C.GEO_CITY_NO AND
         a.GEO_STREET_NO = B.GEO_STREET_NO
/

BEGIN
execute immediate('CREATE PUBLIC SYNONYM MV_ADSL_STRASSEN_TAIFUN FOR ' || SYS_CONTEXT('USERENV','CURRENT_SCHEMA') || '.MV_ADSL_STRASSEN_TAIFUN');
EXCEPTION
   WHEN NO_DATA_FOUND THEN
      NULL;
   WHEN OTHERS THEN
      NULL;
END;
/

GRANT INSERT, SELECT, UPDATE ON MV_ADSL_STRASSEN_TAIFUN TO R_TAIFUN_KUP;
GRANT INSERT, SELECT, UPDATE ON MV_ADSL_STRASSEN_TAIFUN TO TAIFUN_KUP;
GRANT INSERT, SELECT, UPDATE ON MV_ADSL_STRASSEN_TAIFUN TO TAIFUN_KUP WITH GRANT OPTION;

