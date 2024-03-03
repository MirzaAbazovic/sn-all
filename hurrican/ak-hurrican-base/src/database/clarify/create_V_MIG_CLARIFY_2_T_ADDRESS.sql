-----------------------------------------------------------------------
--
-- Anlage der ID id = 16010
-- 16010 = neuer Ansprechpartnertyp 'Hotline geplante Arbeiten'
--
-----------------------------------------------------------------------

insert into
t_reference a
(
       A.DESCRIPTION,
       A.FLOAT_VALUE,
       A.GUI_VISIBLE,
       A.ID,
       A.INT_VALUE,
       A.ORDER_NO,
       A.STR_VALUE,
       A.TYPE,
       A.UNIT_ID,
       A.VERSION
)
(
SELECT
replace(A.DESCRIPTION,'Service', 'geplante Arbeiten') as DESCRIPTION,
       A.FLOAT_VALUE,
       A.GUI_VISIBLE,
       16010 as ID,
       A.INT_VALUE,
       A.ORDER_NO,
replace(A.STR_VALUE,'Service', 'geplante Arbeiten') as STR_VALUE,
       A.TYPE,
       A.UNIT_ID,
       A.VERSION
FROM t_reference a
WHERE a.id = 16006
and not exists (
select
aa.id from t_reference aa where aa.id = 16010
)
);
commit;

-----------------------------------------------------------------------
--
-- Anlage der ID id = 205 und 206
-- neue Adressklassifizierung:
--
-- 205 = Hotline Service
-- 206 = Hotline geplante Arbeiten
-----------------------------------------------------------------------

insert into
t_reference a
(
       A.DESCRIPTION,
       A.FLOAT_VALUE,
       A.GUI_VISIBLE,
       A.ID,
       A.INT_VALUE,
       A.ORDER_NO,
       A.STR_VALUE,
       A.TYPE,
       A.UNIT_ID,
       A.VERSION
)
(
SELECT
-- a.*,
       A.DESCRIPTION,
       A.FLOAT_VALUE,
       A.GUI_VISIBLE,
       205 as ID,
       A.INT_VALUE,
       A.ORDER_NO,
       'Hotline Service' as STR_VALUE,
       A.TYPE,
       A.UNIT_ID,
       A.VERSION
FROM t_reference a
WHERE a.id = 204
and not exists (
select
aa.id from t_reference aa where aa.id = 205
)
);
commit;

insert into
t_reference a
(
       A.DESCRIPTION,
       A.FLOAT_VALUE,
       A.GUI_VISIBLE,
       A.ID,
       A.INT_VALUE,
       A.ORDER_NO,
       A.STR_VALUE,
       A.TYPE,
       A.UNIT_ID,
       A.VERSION
)
(
SELECT
-- a.*,
       A.DESCRIPTION,
       A.FLOAT_VALUE,
       A.GUI_VISIBLE,
       206 as ID,
       A.INT_VALUE,
       A.ORDER_NO,
       'Hotline geplante Arbeiten' as STR_VALUE,
       A.TYPE,
       A.UNIT_ID,
       A.VERSION
FROM t_reference a
WHERE a.id = 204
and not exists (
select
aa.id from t_reference aa where aa.id = 206
)
);
commit;

--------------------------------------------------------------
-- done.
--------------------------------------------------------------



--------------------------------------------------------------
-- VIEW v1migcl_ansp0
--
-- das sind die Kandidaten in Hurrican,
-- für die man nach Ansprechpartnerdaten in Clarify sucht
--
-- betrachtet werden nur TDNs mit
-- NIEDERLASSUNG_ID = 3, ungekündigt
--------------------------------------------------------------

CREATE OR REPLACE VIEW v1migcl_ansp0 AS
SELECT
             CAST(NULL as NUMBER) as kunde__no,
             f.tdn || '_' ||
             g.flag
                AS comp,
             d.auftrag_id  ||
             '_' ||
             g.flag
                AS comp_auftragid,
             d.auftrag_id,
             f.tdn,
             d.NIEDERLASSUNG_ID,
             g.id AS TYPE_REF_ID,
             e.prodak_order__no,
             e.status_id
      FROM
      (
      SELECT
             DECODE (b.ID, 16006, 'CONNECT', 16010, 'GA', NULL)
             AS flag,
             b.*
      FROM T_REFERENCE b
      WHERE
      B.ID IN (16006, 16010)
      ) g,
      t_tdn f,
      T_AUFTRAG_DATEN e,
      T_AUFTRAG_TECHNIK d
      WHERE
            -----------------------------
-- !! Connect Zusatzleitung weglassen
            e.prod_id <> 456 AND
            -----------------------------
--          NVL(d.NIEDERLASSUNG_ID,3) = 3 AND
            d.NIEDERLASSUNG_ID = 3 AND
            -----------------------------
            e.GUELTIG_BIS > SYSDATE AND
            e.STATUS_ID < 10000 AND
-- auch 9800 = Kündigung weglassen
            e.STATUS_ID NOT IN (1150, 3400, 9800) AND
            d.GUELTIG_BIS > SYSDATE AND
            d.TDN_ID = f.id AND
            d.AUFTRAG_ID = e.AUFTRAG_ID;

-------------------------------------------------------------
-- und jetzt statusunabhaengig
-------------------------------------------------------------

CREATE OR REPLACE VIEW v1migcl_ansp1 AS
SELECT
             a.kunde__no,
             a.comp,
             a.comp_auftragid,
             a.auftrag_id,
             a.tdn,
             a.NIEDERLASSUNG_ID,
             a.TYPE_REF_ID,
             a.prodak_order__no
      FROM
             v1migcl_ansp0 a
group by
             a.kunde__no,
             a.comp,
             a.comp_auftragid,
             a.auftrag_id,
             a.tdn,
             a.NIEDERLASSUNG_ID,
             a.TYPE_REF_ID,
             a.prodak_order__no;

-------------------------------------------------------------
-- Ansprechpartner aus v1migcl_ansp1
-- die noch keinen Ansprechpartner (16006 oder 16010) in Hurrican haben
-------------------------------------------------------------

CREATE OR REPLACE VIEW v1migcl_ansp2 AS
SELECT
-- etwas dirty / willkürlich
-- DECODE (g.flag, 'A', 16006, 'B', 16010, 'GA', NULL) as TYPE_REF_ID,
a.*
FROM (SELECT a.auftrag_id ||
             '_' ||
             DECODE (A.TYPE_REF_ID, 16006, 'CONNECT', 16010, 'GA', NULL)
                AS comp_auftragid,
             a.*
      FROM T_REFERENCE b,
           t_ansprechpartner a
      WHERE B.ID IN (16006, 16010) AND
            A.TYPE_REF_ID = b.id) b,
           v1migcl_ansp1 a
WHERE a.comp_auftragid = b.comp_auftragid(+) AND
      b.comp_auftragid IS NULL;


-------------------------------------------------------------
-------------------------------------------------------------
-------------------------------------------------------------
-------------------------------------------------------------
-------------------------------------------------------------
-- und hier gehts Richtung Clarify.
-- und zwar über den DB Link @kupvis
-- der seiterseits einen DB Link auf die Claryfy DB hat.
-- Kup Basisviews die hier verwendet werden see create_MIG_CLARIFY_views.sql
-------------------------------------------------------------
-------------------------------------------------------------
-------------------------------------------------------------
-------------------------------------------------------------
-------------------------------------------------------------
-------------------------------------------------------------

-------------------------------------------------------------
-- Overview Clarify Ansprechpartner
-------------------------------------------------------------

CREATE OR REPLACE VIEW v1migcl_clarify_ansp1_A AS
SELECT
a.serial_no || '_' || a.x_role  AS comp,
a.*
FROM
mnetcall.MIGCL_ANSP_ENDSTELLEN_HUR_A@kupvis a
WHERE
-- a.kunde__no is NOT NULL AND
-- a.serial_no = 'lhm051.004.001' and
-- nur irgendeinen DS der beiden Endstellen holen
a.ENDSTELLENTYP = 'A' AND
-- nur diese beiden interessieren
a.x_role IN  ('CONNECT', 'GA');

-------------------------------------------------------------
-- bzw. fast gleich, nur eine Spalte mehr: Clarify Ansprechpartner
-------------------------------------------------------------

CREATE OR REPLACE VIEW v1migcl_clarify_ansp1 AS
SELECT
a.serial_no || '_' || a.x_role  AS comp,
a.*
FROM
mnetcall.MIGCL_ANSP_ENDSTELLEN_HUR@kupvis a
WHERE
-- a.kunde__no is NOT NULL AND
-- a.serial_no = 'lhm051.004.001' and
-- nur irgendeinen DS der beiden Endstellen holen
a.ENDSTELLENTYP = 'A' AND
-- nur diese beiden interessieren
a.x_role IN  ('CONNECT', 'GA');

-------------------------------------------------------------
-- ein Plausi Chk
-------------------------------------------------------------

CREATE OR REPLACE VIEW v1migcl_clarify_ansp1_err1 AS
SELECT a.*
FROM
v1migcl_ansp2 b,
v1migcl_clarify_ansp1 a
WHERE
-- A.serial_no = 'lhm051.004.001' AND
a.comp = b.comp;

-------------------------------------------------------------
-- Ansprechpartner in Hurrican fehlt, aber in Clarify ist ein Datensatz vorhanden
-------------------------------------------------------------

CREATE OR REPLACE VIEW v1migcl_clarify_ansp2_A AS
SELECT
  a.AUFTRAG_ID,
  a.TYPE_REF_ID,
  1 AS PREFERRED,
  NULL AS TEXT,
  1 AS PRIO,
  0 AS VERSION,
b.*,
a.TDN,
a.niederlassung_ID,
a.prodak_order__no,
a.kunde__no
FROM
v1migcl_clarify_ansp1_A b,
v1migcl_ansp2 a
WHERE
-- a.tdn = 'lhm051.004.001' AND
-- b.serial_no = 'lhm051.004.001' AND
a.tdn = b.serial_no AND
a.comp = b.comp;

-------------------------------------------------------------
-- Ansprechpartner in Taifun fehlt, aber in Clarify ist ein Datensatz vorhanden
-- fast gleich wie v1migcl_clarify_ansp2_A, nue eine zus. Spalte
-------------------------------------------------------------

CREATE OR REPLACE VIEW v1migcl_clarify_ansp2 AS
SELECT
  a.AUFTRAG_ID,
  a.TYPE_REF_ID,
  1 AS PREFERRED,
  NULL AS TEXT,
  1 AS PRIO,
  0 AS VERSION,
b.*,
a.TDN,
a.niederlassung_ID,
a.prodak_order__no,
a.kunde__no
FROM
v1migcl_clarify_ansp1 b,
v1migcl_ansp2 a
WHERE
-- a.tdn = 'lhm051.004.001' AND
-- b.serial_no = 'lhm051.004.001' AND
a.tdn = b.serial_no AND
a.comp = b.comp;

-------------------------------------------------------------
-- darüber gruppiert erhält man alle Clarify-Ansprechpartner die man noch einspielen muss
-- ein objid_contact = die Adresse in Clarify, über die
-- bei FAX Erstellung in BSI gruppiert=zusammengefasst wird
--
-- gruppiert nach a.TYPE_REF_ID meint:
-- aus einer objid_contact entstehen in der Migration 2 Datensätze
-- so dass man
--  Hotline Service
--  Hotline geplante Arbeiten
-- getrennt/ohne Seiteneffekte adminstrieren kann
-------------------------------------------------------------

CREATE OR REPLACE VIEW v1migcl_clarify_ansp3 AS
SELECT
a.objid_contact,
a.TYPE_REF_ID
FROM
v1migcl_clarify_ansp2_A a
GROUP BY
a.objid_contact,
a.TYPE_REF_ID;

-------------------------------------------------------------
-- baut die Struktur eines T_Address Objekts in Hurrican nach
-- blöd ist dass man die address_HOTLINE_STANDORT parsen
-- muesst und in Strasse/Hausnr/Zusatz zerlegen
-- (der Ansatz hier ist etwas simpel)
-------------------------------------------------------------

CREATE OR REPLACE VIEW v1migcl_clarify_2_address AS
SELECT
-------------------------------------------
a.objid_contact,
a.TYPE_REF_ID,
-------------------------------------------
NULL AS  ID,
A.KUNDE__NO AS   KUNDE__NO,
decode(a.TYPE_REF_ID,
16006,205,
16010,206,
-- default - here unused
204
) AS ADDRESS_TYPE,
'BUSINESS' AS   FORMAT_NAME,
a.first_name AS   NAME,
NULL AS   VORNAME,
NULL AS   NAME2,
NULL AS   VORNAME2,
--
trim(
          TRANSLATE
             (a.address_HOTLINE_STANDORT,
              'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzäöüß?+,. ;-0123456789',
              'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzäöüß?+,. ;-'
             )
)
             AS   STRASSE,
NULL AS   STRASSE_ADD,
trim(
          TRANSLATE
             (a.address_HOTLINE_STANDORT,
              '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzäöüß?+,. ;-',
              '0123456789'
             )
)
AS   HAUSNUMMER,
NULL AS   HAUSNUMMER_ZUSATZ,
a.zipcode_HOTLINE_STANDORT AS  PLZ,
a.city_HOTLINE_STANDORT AS   ORT,
NULL AS   ORT_ZUSATZ,
NULL AS   POSTFACH,
'DE' AS   LAND_ID,
DECODE(a.x_phone,'XXXX',NULL,a.x_phone) AS TELEFON,
NULL AS   HANDY,
DECODE(a.x_fax,'XXXX',NULL,a.x_fax)  AS   FAX,
substr(DECODE(a.x_email,'XXXX',NULL,a.x_email),1,100) AS   EMAIL,
NULL AS   TITEL,
NULL AS   TITEL2,
trim(a.address || ' ' || a.x_room) AS BEMERKUNG,
NULL AS PRIO_BRIEF,
DECODE(a.x_email,
   'XXXX',NULL,
    NULL, NULL,
1) AS   PRIO_EMAIL,
DECODE(a.x_fax,
   'XXXX',NULL,
    NULL, NULL,
1) AS   PRIO_FAX,
1 AS   PRIO_SMS,
DECODE(a.x_phone,
   'XXXX',NULL,
    NULL, NULL,
1) AS   PRIO_TEL,
0 AS   VERSION
FROM
v1migcl_clarify_ansp2 a;

------------------------------------------------------------
-- und aus diesem Address Objekt wird gruppiert in Ansprechpartner Objekte
-- d.h.
-- jeder Auftrag erhält eigene Ansprechpartner
-- diese Ansprechpartner sharen sich das (kundenunabhängige) Adress Objekt
--
-- hier muss man noch genau testen wie BSI gruppiert:
-- abgesprochen ist: über die ID des Adressobjkekts
------------------------------------------------------------

CREATE OR REPLACE FORCE VIEW V1MIGCL_CLARIFY_2_ADR_KUNDE AS
SELECT
         A.objid_contact,
         A.TYPE_REF_ID,
         max(A.KUNDE__NO) as KUNDE__NO,
         min(A.ADDRESS_TYPE) as ADDRESS_TYPE,
         min(A.FORMAT_NAME) as FORMAT_NAME,
         min(A.NAME) as name,
         min(A.VORNAME) as vorname,
         min(A.NAME2) as name2,
         min(A.VORNAME2) as vorname2,
         min(A.STRASSE) as STRASSE,
         NULL as STRASSE_ADD,
         min(A.HAUSNUMMER) as HAUSNUMMER,
         NULL as HAUSNUMMER_ZUSATZ,
         min(A.PLZ) as PLZ,
         min(A.ORT) as ORT,
         min(A.ORT_ZUSATZ) as ORT_ZUSATZ,
         min(A.POSTFACH) as POSTFACH,
         min(A.LAND_ID) as LAND_ID,
         min(A.TELEFON) as TELEFON,
         min(A.HANDY) as HANDY,
         min(A.FAX) as FAX,
         min(A.EMAIL) as EMAIL,
         min(A.TITEL) as TITEL,
         min(A.TITEL2) as TITEL2,
         CAST (NULL as VARCHAR2(255)) as BEMERKUNG,
--       decode(min(A.BEMERKUNG), max(A.BEMERKUNG),min(A.BEMERKUNG), min(A.BEMERKUNG) || ' ...  ' || max(A.BEMERKUNG)) as BEMERKUNG,
         min(A.PRIO_BRIEF) as PRIO_BRIEF,
         min(A.PRIO_EMAIL) as PRIO_EMAIL,
         min(A.PRIO_FAX) as PRIO_FAX,
         min(A.PRIO_SMS) as PRIO_SMS,
         min(A.PRIO_TEL) as PRIO_TEL,
         min(A.VERSION) as VERSION
FROM v1migcl_clarify_2_address a
-- WHERE a.objid_contact = 268439459
GROUP BY
         A.objid_contact,
         A.TYPE_REF_ID;

-------------------------------------------------------------------
-- API
-- und das ist die Installationsprocedure, die man starten muesste
-- Info:
-- kann jederzeit 'repeatable' ausgeführt werden, weil sie nur das delta betrachtet
-- d.h. man kann die procedure auch für eine Nachmigration neu in Clarify erfasster
-- Ansprechpartner verwenden. (kann aber keinen automatischen Update bei Änderungen)
--
-- ACHTUNG:
-- wird hier NICHT automatisch aufgerufen
--
-------------------------------------------------------------------
--
-- geht mit cursor1 über alle a.objid_contacts in Clarify
-- die noch angefasst werden muessen, weils noch nichts dazu in Hurrican gibt
--
-- cursor2 holt dafür die anzulegenden Addressen
--
-- cursor3 holt dafür die anzulegenden Ansprechpartner
--
-------------------------------------------------------------------

CREATE OR REPLACE PROCEDURE P_FDB_MIGCL_ALL
IS
BEGIN
   DECLARE
      iRet               NUMBER;

      iSEQ_ID            NUMBER;
      iSEQ_ANSP_ID       NUMBER;
      varobjid_contact   NUMBER;
      varTYPE_REF_ID     NUMBER;
      iAnz               NUMBER;

      varPREFERRED       CHAR (1);

      CURSOR c1
      IS
         SELECT a.objid_contact,
                a.TYPE_REF_ID
         FROM v1migcl_clarify_ansp3 a
         --         where
         --                  a.objid_contact = 268439459
         ORDER BY a.objid_contact DESC,
                  a.TYPE_REF_ID;

      r1                 c1%ROWTYPE;

      CURSOR c2
      IS
         SELECT                    -------------------------------------------
               A.objid_contact,
                A.TYPE_REF_ID,
                -------------------------------------------
                -- ID,
                A.KUNDE__NO,
                A.ADDRESS_TYPE,
                A.FORMAT_NAME,
                A.NAME,
                A.VORNAME,
                A.NAME2,
                A.VORNAME2,
                A.STRASSE,
                A.STRASSE_ADD,
                A.HAUSNUMMER,
                A.HAUSNUMMER_ZUSATZ,
                A.PLZ,
                A.ORT,
                A.ORT_ZUSATZ,
                A.POSTFACH,
                A.LAND_ID,
                A.TELEFON,
                A.HANDY,
                A.FAX,
                A.EMAIL,
                A.TITEL,
                A.TITEL2,
                A.BEMERKUNG,
                A.PRIO_BRIEF,
                A.PRIO_EMAIL,
                A.PRIO_FAX,
                A.PRIO_SMS,
                A.PRIO_TEL,
                A.VERSION
         FROM V1MIGCL_CLARIFY_2_ADR_KUNDE a
         WHERE                                               -- ROWNUM = 1 AND
              a.objid_contact = varobjid_contact AND
               a.TYPE_REF_ID = varTYPE_REF_ID
         ORDER BY a.objid_contact DESC,
                  a.TYPE_REF_ID DESC;


      r2                 c2%ROWTYPE;

      CURSOR c3
      IS
         SELECT                                                             --
               a.serial_no,
                --
                a.AUFTRAG_ID,
                a.TYPE_REF_ID,
                a.PREFERRED,
                a.TEXT,
                a.PRIO,
                a.VERSION
         FROM v1migcl_clarify_ansp2 a
         WHERE
               a.objid_contact = varobjid_contact AND
               a.TYPE_REF_ID = varTYPE_REF_ID
         ORDER BY a.serial_no DESC,
                  a.TYPE_REF_ID DESC,
                  a.objid_contact DESC;

      r3                 c3%ROWTYPE;
   BEGIN
      FOR r1 IN c1
      LOOP
         varobjid_contact := r1.objid_contact;
         varTYPE_REF_ID := r1.TYPE_REF_ID;

         FOR r2 IN c2
         LOOP

            -- seq holen
            SELECT S_T_ADDRESS_0.NEXTVAL INTO iSEQ_ID FROM DUAL;
            INSERT INTO t_address a (A.ID,
                                     A.KUNDE__NO,
                                     A.ADDRESS_TYPE,
                                     A.FORMAT_NAME,
                                     A.NAME,
                                     A.VORNAME,
                                     A.NAME2,
                                     A.VORNAME2,
                                     A.STRASSE,
                                     A.STRASSE_ADD,
                                     A.HAUSNUMMER,
                                     A.HAUSNUMMER_ZUSATZ,
                                     A.PLZ,
                                     A.ORT,
                                     A.ORT_ZUSATZ,
                                     A.POSTFACH,
                                     A.LAND_ID,
                                     A.TELEFON,
                                     A.HANDY,
                                     A.FAX,
                                     A.EMAIL,
                                     A.TITEL,
                                     A.TITEL2,
                                     A.BEMERKUNG,
                                     A.PRIO_BRIEF,
                                     A.PRIO_EMAIL,
                                     A.PRIO_FAX,
                                     A.PRIO_SMS,
                                     A.PRIO_TEL,
                                     A.VERSION)
            VALUES (iSEQ_ID,
                    r2.KUNDE__NO,
                    r2.ADDRESS_TYPE,
                    r2.FORMAT_NAME,
                    r2.NAME,
                    r2.VORNAME,
                    r2.NAME2,
                    r2.VORNAME2,
                    r2.STRASSE,
                    r2.STRASSE_ADD,
                    r2.HAUSNUMMER,
                    r2.HAUSNUMMER_ZUSATZ,
                    r2.PLZ,
                    r2.ORT,
                    r2.ORT_ZUSATZ,
                    r2.POSTFACH,
                    r2.LAND_ID,
                    r2.TELEFON,
                    r2.HANDY,
                    r2.FAX,
                    r2.EMAIL,
                    r2.TITEL,
                    r2.TITEL2,
                    r2.BEMERKUNG,
                    r2.PRIO_BRIEF,
                    r2.PRIO_EMAIL,
                    r2.PRIO_FAX,
                    r2.PRIO_SMS,
                    r2.PRIO_TEL,
                    r2.VERSION);

            varPREFERRED := '1';
            FOR r3 IN c3
            LOOP
               --  check unique constraint in T_ANSPRECHPARTNER
               SELECT DECODE (COUNT ( * ), 0, '1', '0')
               INTO varPREFERRED
               FROM T_ANSPRECHPARTNER a
               WHERE a.PREFERRED = 1 AND
                     A.AUFTRAG_ID = r3.AUFTRAG_ID;

               -- seq holen !!1
               SELECT S_T_ANSPRECHPARTNER_0.NEXTVAL
               INTO iSEQ_ANSP_ID
               FROM DUAL;

               INSERT INTO T_ANSPRECHPARTNER a (A.ID,
                                                A.ADDRESS_ID,
                                                A.AUFTRAG_ID,
                                                A.TYPE_REF_ID,
                                                A.PREFERRED,
                                                A.TEXT,
                                                A.PRIO,
                                                A.VERSION)
               VALUES (iSEQ_ANSP_ID,
                       iSEQ_ID,
                       r3.AUFTRAG_ID,
                       r3.TYPE_REF_ID,
                       varPREFERRED,
                       r3.TEXT,
                       r3.PRIO,
                       r3.VERSION);

               varPREFERRED := '0';
            END LOOP;
         END LOOP;
         COMMIT;
      END LOOP;

      COMMIT;
   END;
END P_FDB_MIGCL_ALL;
/

---------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------

-------------------------------------------------------------
-- Ansprechpartner aus Hurrican
-- die einen Ansprechpartner haben
-- und einen abweichenden in Clarify
--
-- Clarify ist der Master und ersetzt die Hurrican Daten
-------------------------------------------------------------

-- das waere besser als v1migHUR_UPD_ansp2
-- ist aber furchtabr langsam: deshalb verwendet P_FDB_MIG_UPD_ANSP_BESTAND_1
-- stattdessen v1migHUR_UPD_ansp2; und wenn nicht in Clarify steht
-- wir der Datensatz geskippt

-- unused:
CREATE OR REPLACE VIEW v1migHUR_UPD_ansp2_CL AS
SELECT  /*+ first_rows */
       a.auftrag_id,
       b.address_id,
       b.id AS id_ansp,
       DECODE (a.type_ref_id, 16006, 205, 16010, 206, -1) AS ADDRESS_TYPE_NEW,
       a.tdn
--       , d.*
       FROM
    v1migcl_clarify_ansp1_A d,
-- v1migcl_clarify_ansp1 d,
      (
      SELECT aa.id,
             DECODE (AA.TYPE_REF_ID, 16006, 'CONNECT', 16010, 'GA', NULL)
                AS comp_auftragid
            FROM t_address cc,
           T_REFERENCE bb,
           t_ansprechpartner aa
      WHERE
      cc.address_type IN (205,206) AND
      BB.ID IN (16006, 16010) AND
            AA.address_id = cc.ID AND
            AA.TYPE_REF_ID = bb.id
      ) c,
(SELECT a.auftrag_id ||
             '_' ||
             DECODE (A.TYPE_REF_ID, 16006, 'CONNECT', 16010, 'GA', NULL)
                AS comp_auftragid,
             c.address_type,
             a.*
      FROM t_address c,
           T_REFERENCE b,
           t_ansprechpartner a
      WHERE B.ID IN (16006, 16010) AND
            A.address_id = c.ID AND
            A.TYPE_REF_ID = b.id) b,
     v1migcl_ansp1 a
WHERE
---------------------------------------------
--    a.tdn = 'rud565.109.V' AND
---------------------------------------------
      d.x_role = 'CONNECT' AND
      a.TDN = d.serial_no AND
      a.comp_auftragid = c.comp_auftragid(+) AND
      c.comp_auftragid IS NULL AND
      a.comp_auftragid = b.comp_auftragid AND
      DECODE (a.type_ref_id, 16006, 205, 16010, 206, -1) <> -1 AND
      DECODE (a.type_ref_id, 16006, 205, 16010, 206, -1) <> b.address_type;

-------------------------------------------------------------
-- Ansprechpartner aus Hurrican
-- die einen Ansprechpartner haben
-- und einen abweichenden in Clarify
--
-- Clarify ist der Master und ersetzt die Hurrican Daten
-------------------------------------------------------------
CREATE OR REPLACE VIEW v1migHUR_UPD_ansp2 AS
SELECT /* first_rows */
a.auftrag_id,
       b.address_id,
       b.id AS id_ansp,
       DECODE (a.type_ref_id, 16006, 205, 16010, 206, -1) AS ADDRESS_TYPE_NEW,
       a.tdn
--       , d.*
FROM -- v1migcl_clarify_ansp1 d,
     (SELECT aa.id,
             DECODE (AA.TYPE_REF_ID, 16006, 'CONNECT', 16010, 'GA', NULL)
                AS comp_auftragid
      FROM t_address cc,
           T_REFERENCE bb,
           t_ansprechpartner aa
      WHERE cc.address_type IN (205, 206) AND
            BB.ID IN (16006, 16010) AND
            AA.address_id = cc.ID AND
            AA.TYPE_REF_ID = bb.id) c,
     (SELECT a.auftrag_id ||
             '_' ||
             DECODE (A.TYPE_REF_ID, 16006, 'CONNECT', 16010, 'GA', NULL)
                AS comp_auftragid,
             c.address_type,
             a.*
      FROM t_address c,
           T_REFERENCE b,
           t_ansprechpartner a
      WHERE B.ID IN (16006, 16010) AND
            A.address_id = c.ID AND
            A.TYPE_REF_ID = b.id) b,
     v1migcl_ansp1 a
WHERE ---------------------------------------------
--      a.tdn = 'rud565.109.V' AND
      ---------------------------------------------
      a.comp_auftragid = c.comp_auftragid(+) AND
      c.comp_auftragid IS NULL AND
      a.comp_auftragid = b.comp_auftragid AND
      DECODE (a.type_ref_id, 16006, 205, 16010, 206, -1) <> -1 AND
      DECODE (a.type_ref_id, 16006, 205, 16010, 206, -1) <> b.address_type;

-------------------------------------------------------------
-- die gibts schon in Hurrican UND einen Eintrag dazu in Clarify
-------------------------------------------------------------

CREATE OR REPLACE PROCEDURE P_FDB_MIG_UPD_ANSP_BESTAND_1
IS
BEGIN
   DECLARE
      iSEQ_ID               NUMBER;

      varID_ANSP            NUMBER;
      varADDRESS_ID         NUMBER;
      varADDRESS_TYPE_NEW   NUMBER;

      varTDN                VARCHAR2 (50);

      CURSOR c1
      IS
         SELECT A.ADDRESS_ID,
                A.ADDRESS_TYPE_NEW
         FROM v1migHUR_UPD_ansp2 a
         GROUP BY A.ADDRESS_ID,
                  A.ADDRESS_TYPE_NEW
         ORDER BY A.ADDRESS_ID DESC;

      r1                    c1%ROWTYPE;


      CURSOR c2
      IS
         SELECT A.ID_ANSP,
                A.ADDRESS_ID,
                A.ADDRESS_TYPE_NEW,
                A.TDN
         --              , a.*
         FROM v1migHUR_UPD_ansp2 a
         WHERE A.ADDRESS_ID = varADDRESS_ID AND
               A.ADDRESS_TYPE_NEW = varADDRESS_TYPE_NEW
         ORDER BY A.ID_ANSP DESC,
                  A.ADDRESS_ID DESC,
                  A.TDN DESC;

      r2                    c2%ROWTYPE;

      CURSOR c3
      IS
         SELECT NULL AS KUNDE__NO,
                -- b.TYPE_REF_ID_NEW as TYPE_REF_ID,
                'BUSINESS' AS FORMAT_NAME,
                a.first_name AS NAME,
                NULL AS VORNAME,
                NULL AS NAME2,
                NULL AS VORNAME2,
                --
                TRIM (
                   TRANSLATE (
                      a.address_HOTLINE_STANDORT,
                      'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzäöüß?+,. ;-0123456789',
                      'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzäöüß?+,. ;-'
                   )
                )
                   AS STRASSE,
                NULL AS STRASSE_ADD,
                TRIM (
                   TRANSLATE (
                      a.address_HOTLINE_STANDORT,
                      '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzäöüß?+,. ;-',
                      '0123456789'
                   )
                )
                   AS HAUSNUMMER,
                NULL AS HAUSNUMMER_ZUSATZ,
                a.zipcode_HOTLINE_STANDORT AS PLZ,
                a.city_HOTLINE_STANDORT AS ORT,
                NULL AS ORT_ZUSATZ,
                NULL AS POSTFACH,
                'DE' AS LAND_ID,
                DECODE (a.x_phone, 'XXXX', NULL, a.x_phone) AS TELEFON,
                NULL AS HANDY,
                DECODE (a.x_fax, 'XXXX', NULL, a.x_fax) AS FAX,
                SUBSTR (DECODE (a.x_email, 'XXXX', NULL, a.x_email),
                        1,
                        100)
                   AS EMAIL,
                NULL AS TITEL,
                NULL AS TITEL2,
                TRIM (a.address ||
                      ' ' ||
                      a.x_room)
                   AS BEMERKUNG,
                NULL AS PRIO_BRIEF,
                DECODE (a.x_email, 'XXXX', NULL, NULL, NULL, 1) AS PRIO_EMAIL,
                DECODE (a.x_fax, 'XXXX', NULL, NULL, NULL, 1) AS PRIO_FAX,
                1 AS PRIO_SMS,
                DECODE (a.x_phone, 'XXXX', NULL, NULL, NULL, 1) AS PRIO_TEL,
                0 AS VERSION
         FROM v1migcl_clarify_ansp1 a
         WHERE a.x_role IN ('CONNECT') AND
               a.serial_no = varTDN;

      r3                    c3%ROWTYPE;
   BEGIN
      FOR r1 IN c1
      LOOP
         varADDRESS_ID := r1.ADDRESS_ID;
         varADDRESS_TYPE_NEW := r1.ADDRESS_TYPE_NEW;

         FOR r2 IN c2
         LOOP
            varTDN := r2.TDN;
            varID_ANSP := r2.ID_ANSP;

            FOR r3 IN c3
            LOOP
               -- seq holen
               SELECT S_T_ADDRESS_0.NEXTVAL INTO iSEQ_ID FROM DUAL;
               INSERT INTO t_address a (A.ID,
                                        A.KUNDE__NO,
                                        A.ADDRESS_TYPE,
                                        A.FORMAT_NAME,
                                        A.NAME,
                                        A.VORNAME,
                                        A.NAME2,
                                        A.VORNAME2,
                                        A.STRASSE,
                                        A.STRASSE_ADD,
                                        A.HAUSNUMMER,
                                        A.HAUSNUMMER_ZUSATZ,
                                        A.PLZ,
                                        A.ORT,
                                        A.ORT_ZUSATZ,
                                        A.POSTFACH,
                                        A.LAND_ID,
                                        A.TELEFON,
                                        A.HANDY,
                                        A.FAX,
                                        A.EMAIL,
                                        A.TITEL,
                                        A.TITEL2,
                                        A.BEMERKUNG,
                                        A.PRIO_BRIEF,
                                        A.PRIO_EMAIL,
                                        A.PRIO_FAX,
                                        A.PRIO_SMS,
                                        A.PRIO_TEL,
                                        A.VERSION)
               VALUES (iSEQ_ID,
                       r3.KUNDE__NO,
                       r1.ADDRESS_TYPE_NEW,
                       r3.FORMAT_NAME,
                       r3.NAME,
                       r3.VORNAME,
                       r3.NAME2,
                       r3.VORNAME2,
                       r3.STRASSE,
                       r3.STRASSE_ADD,
                       r3.HAUSNUMMER,
                       r3.HAUSNUMMER_ZUSATZ,
                       r3.PLZ,
                       r3.ORT,
                       r3.ORT_ZUSATZ,
                       r3.POSTFACH,
                       r3.LAND_ID,
                       r3.TELEFON,
                       r3.HANDY,
                       r3.FAX,
                       r3.EMAIL,
                       r3.TITEL,
                       r3.TITEL2,
                       r3.BEMERKUNG,
                       r3.PRIO_BRIEF,
                       r3.PRIO_EMAIL,
                       r3.PRIO_FAX,
                       r3.PRIO_SMS,
                       r3.PRIO_TEL,
                       r3.VERSION);

               UPDATE T_ANSPRECHPARTNER a
               SET A.ADDRESS_ID = iSEQ_ID
               WHERE A.ID = varID_ANSP;
            END LOOP;
         END LOOP;
         COMMIT;
      END LOOP;

      COMMIT;
   END;
END P_FDB_MIG_UPD_ANSP_BESTAND_1;
/

-------------------------------------------------------------------------------
-- interne Migration
-------------------------------------------------------------------------------

CREATE OR REPLACE VIEW v1migHUR_ansp0 AS
SELECT
       h.id AS id_ansp,
       h.address_id,
       DECODE(d.ADDRESS_TYPE, '205','U','I') AS ANSP_I_or_U,
       h.TYPE_REF_ID,
       d.auftrag_id,
       CAST (NULL AS NUMBER) AS kunde__no,
       h.auftrag_id ||
       '_' ||
       d.type_ref ||
       '_' ||
       d.ADDRESS_TYPE AS comp_auftragid,
       d.ADDRESS_TYPE AS ADDRESS_TYPE_NEW,
       d.TYPE_REF_ID AS TYPE_REF_ID_NEW,
       f.tdn,
       d.NIEDERLASSUNG_ID,
       e.prodak_order__no,
       e.status_id,
       h.ADDRESS_TYPE
FROM
--   t_produkt i,
     --
     (SELECT 205 AS ADDRESS_TYPE, 16006 AS TYPE_REF_ID, 'CONNECT' AS TYPE_REF FROM DUAL UNION
     SELECT 206 AS ADDRESS_TYPE, 16010 AS TYPE_REF_ID, 'GA' AS TYPE_REF FROM DUAL) d,
     (SELECT b.TYPE,
             b.str_value,
           C.ADDRESS_TYPE,
           a.*
      FROM
           t_address c,
           T_REFERENCE b,
           t_ansprechpartner a
      WHERE
--          C.ADDRESS_TYPE <> 205 AND
            A.TYPE_REF_ID = 16006 AND
            A.ADDRESS_ID = c.ID AND
            A.TYPE_REF_ID = b.id) h,
     t_tdn f,
     T_AUFTRAG_DATEN e,
     T_AUFTRAG_TECHNIK d
WHERE
--   tdn = 'DV62487-2009' AND
--   tdn = 'S20091104106' AND
--   d.auftrag_id = 411081 and
--   e.prod_id = i.prod_id AND
      d.auftrag_id = h.auftrag_id AND
      -----------------------------
-- auskommentiert denn man kann auch alle nehmen:
      -----------------------------
--      -- 'Connect Niederlassung 3 München' weglassen
--      DECODE (e.prod_id,
--              450, DECODE (NVL (d.NIEDERLASSUNG_ID, 1), 3, 0, 1),
--              1) = 1 AND
      -----------------------------
      --  NVL(d.NIEDERLASSUNG_ID,1) <> 3 AND
      --  d.NIEDERLASSUNG_ID = 3 AND
      -----------------------------
      e.GUELTIG_BIS > SYSDATE AND
      e.STATUS_ID < 10000 AND
      -- auch 9800 = Kündigung weglassen
      e.STATUS_ID NOT IN (1150, 3400, 9800) AND
      d.GUELTIG_BIS > SYSDATE AND
      d.TDN_ID = f.id AND
      d.AUFTRAG_ID = e.AUFTRAG_ID;

-------------------------------------------------------------
-- und jetzt statusunabhaengig
-------------------------------------------------------------
CREATE OR REPLACE VIEW v1migHUR_ansp1 AS
SELECT
             a.id_ansp,
             a.address_id,
             a.ANSP_I_or_U,
             a.kunde__no,
             a.comp_auftragid,
             a.ADDRESS_TYPE_NEW,
             a.TYPE_REF_ID_NEW,
             a.auftrag_id,
             a.tdn,
             a.NIEDERLASSUNG_ID,
             a.TYPE_REF_ID,
             a.prodak_order__no
      FROM
             v1migHUR_ansp0 a
GROUP BY
             a.id_ansp,
             a.address_id,
             a.ANSP_I_or_U,
             a.kunde__no,
             a.comp_auftragid,
             a.ADDRESS_TYPE_NEW,
             a.TYPE_REF_ID_NEW,
             a.auftrag_id,
             a.tdn,
             a.NIEDERLASSUNG_ID,
             a.TYPE_REF_ID,
             a.prodak_order__no;


-------------------------------------------------------------
-- Ansprechpartner aus v1migHUR_ansp1
-- die einen Ansprechpartner 16006 haben
-- oder 16010) in Hurrican haben
-------------------------------------------------------------
CREATE OR REPLACE VIEW v1migHUR_ansp2 AS
SELECT
-- etwas dirty / willkürlich
-- DECODE (g.flag, 'A', 16006, 'B', 16010, 'GA', NULL) as TYPE_REF_ID,
a.*
FROM
     (SELECT a.auftrag_id ||
             '_' ||
             DECODE (A.TYPE_REF_ID, 16006, 'CONNECT', 16010, 'GA', NULL) ||
             '_' ||
             C.ADDRESS_TYPE
                AS comp_auftragid,
             a.*,
             b.*
      FROM
           t_address c,
           T_REFERENCE b,
           t_ansprechpartner a
      WHERE
      C.ADDRESS_TYPE IN (205,206) AND
      B.ID IN (16006, 16010) AND
            A.ADDRESS_ID = c.ID AND
            A.TYPE_REF_ID = b.id) b,
           v1migHUR_ansp1 a
WHERE
      a.comp_auftragid = b.comp_auftragid(+) AND
      b.comp_auftragid IS NULL;


-------------------------------------------------------------
-------------------------------------------------------------
-------------------------------------------------------------
-------------------------------------------------------------
-------------------------------------------------------------
-------------------------------------------------------------
-------------------------------------------------------------
CREATE OR REPLACE PROCEDURE P_FDB_MIG_ANSP_BESTAND_1
IS
BEGIN
   DECLARE
      iSEQ_ID               NUMBER;
      iSEQ_ANSP_ID          NUMBER;

      varPREFERRED          CHAR (1);

      varID_ANSP            NUMBER;
      varADDRESS_ID         NUMBER;
      varANSP_I_OR_U        CHAR (1);
      varADDRESS_TYPE_NEW   NUMBER;


      CURSOR c1
      IS
         SELECT A.ADDRESS_ID,
                A.ADDRESS_TYPE_NEW,
                A.ANSP_I_or_U
         FROM v1migHUR_ansp2 a
         GROUP BY A.ADDRESS_ID,
                  A.ADDRESS_TYPE_NEW,
                  A.ANSP_I_or_U
         ORDER BY A.ADDRESS_ID DESC,
                  A.ANSP_I_or_U;

      r1                    c1%ROWTYPE;

      CURSOR c2
      IS
         SELECT CAST (NULL AS NUMBER (11)) AS KUNDE__NO,
                --              A.ADDRESS_TYPE,
                A.FORMAT_NAME,
                A.NAME,
                A.VORNAME,
                A.NAME2,
                A.VORNAME2,
                A.STRASSE,
                A.STRASSE_ADD,
                A.HAUSNUMMER,
                A.HAUSNUMMER_ZUSATZ,
                A.PLZ,
                A.ORT,
                A.ORT_ZUSATZ,
                A.POSTFACH,
                A.LAND_ID,
                A.TELEFON,
                A.HANDY,
                A.FAX,
                A.EMAIL,
                A.TITEL,
                A.TITEL2,
                A.BEMERKUNG,
                NULL AS PRIO_BRIEF,
                DECODE (a.email, 'XXXX', NULL, NULL, NULL, 1) AS PRIO_EMAIL,
                DECODE (a.fax, 'XXXX', NULL, NULL, NULL, 1) AS PRIO_FAX,
                DECODE (a.TELEFON, 'XXXX', NULL, NULL, NULL, 1) AS PRIO_TEL,
                DECODE (A.HANDY, 'XXXX', NULL, NULL, NULL, 1) AS PRIO_SMS,
                A.VERSION
         FROM t_address a
         WHERE a.ID = varADDRESS_ID;

      r2                    c2%ROWTYPE;

      CURSOR c3
      IS
         SELECT A.ID_ANSP,
                A.ADDRESS_ID,
                A.ANSP_I_OR_U,
                A.ADDRESS_TYPE_NEW,
                A.TYPE_REF_ID_NEW
         --              , a.*
         FROM v1migHUR_ansp2 a
         WHERE A.ADDRESS_ID = varADDRESS_ID AND
               A.ADDRESS_TYPE_NEW = varADDRESS_TYPE_NEW AND
               A.ANSP_I_OR_U = varANSP_I_OR_U
         ORDER BY A.ID_ANSP DESC,
                  A.ADDRESS_ID DESC,
                  A.ANSP_I_OR_U;

      r3                    c3%ROWTYPE;


      CURSOR c4
      IS
         SELECT                                                             --
               a.AUFTRAG_ID,
                --              a.TYPE_REF_ID,
                a.PREFERRED,
                a.TEXT,
                a.PRIO,
                a.VERSION
         FROM t_ansprechpartner a
         WHERE a.id = varID_ANSP;

      r4                    c4%ROWTYPE;
   BEGIN
      FOR r1 IN c1
      LOOP
         varADDRESS_ID := r1.ADDRESS_ID;
         varADDRESS_TYPE_NEW := r1.ADDRESS_TYPE_NEW;
         varANSP_I_OR_U := r1.ANSP_I_OR_U;

         FOR r2 IN c2
         LOOP
            -- seq holen
            SELECT S_T_ADDRESS_0.NEXTVAL INTO iSEQ_ID FROM DUAL;
            INSERT INTO t_address a (A.ID,
                                     A.KUNDE__NO,
                                     A.ADDRESS_TYPE,
                                     A.FORMAT_NAME,
                                     A.NAME,
                                     A.VORNAME,
                                     A.NAME2,
                                     A.VORNAME2,
                                     A.STRASSE,
                                     A.STRASSE_ADD,
                                     A.HAUSNUMMER,
                                     A.HAUSNUMMER_ZUSATZ,
                                     A.PLZ,
                                     A.ORT,
                                     A.ORT_ZUSATZ,
                                     A.POSTFACH,
                                     A.LAND_ID,
                                     A.TELEFON,
                                     A.HANDY,
                                     A.FAX,
                                     A.EMAIL,
                                     A.TITEL,
                                     A.TITEL2,
                                     A.BEMERKUNG,
                                     A.PRIO_BRIEF,
                                     A.PRIO_EMAIL,
                                     A.PRIO_FAX,
                                     A.PRIO_SMS,
                                     A.PRIO_TEL,
                                     A.VERSION)
            VALUES (iSEQ_ID,
                    r2.KUNDE__NO,
                    r1.ADDRESS_TYPE_NEW,
                    r2.FORMAT_NAME,
                    r2.NAME,
                    r2.VORNAME,
                    r2.NAME2,
                    r2.VORNAME2,
                    r2.STRASSE,
                    r2.STRASSE_ADD,
                    r2.HAUSNUMMER,
                    r2.HAUSNUMMER_ZUSATZ,
                    r2.PLZ,
                    r2.ORT,
                    r2.ORT_ZUSATZ,
                    r2.POSTFACH,
                    r2.LAND_ID,
                    r2.TELEFON,
                    r2.HANDY,
                    r2.FAX,
                    r2.EMAIL,
                    r2.TITEL,
                    r2.TITEL2,
                    r2.BEMERKUNG,
                    r2.PRIO_BRIEF,
                    r2.PRIO_EMAIL,
                    r2.PRIO_FAX,
                    r2.PRIO_SMS,
                    r2.PRIO_TEL,
                    r2.VERSION);

            varPREFERRED := '1';
            FOR r3 IN c3
            LOOP
               varID_ANSP := r3.ID_ANSP;

               FOR r4 IN c4
               LOOP
                  --  check unique constraint in T_ANSPRECHPARTNER
                  SELECT DECODE (COUNT ( * ), 0, '1', '0')
                  INTO varPREFERRED
                  FROM T_ANSPRECHPARTNER a
                  WHERE a.PREFERRED = 1 AND
                        A.AUFTRAG_ID = r4.AUFTRAG_ID;

                  IF varANSP_I_OR_U = 'I'
                  THEN
                     -- seq holen
                     SELECT S_T_ANSPRECHPARTNER_0.NEXTVAL
                     INTO iSEQ_ANSP_ID
                     FROM DUAL;

                     INSERT INTO T_ANSPRECHPARTNER a (A.ID,
                                                      A.ADDRESS_ID,
                                                      A.AUFTRAG_ID,
                                                      A.TYPE_REF_ID,
                                                      A.PREFERRED,
                                                      A.TEXT,
                                                      A.PRIO,
                                                      A.VERSION)
                     VALUES (iSEQ_ANSP_ID,
                             iSEQ_ID,
                             r4.AUFTRAG_ID,
                             r3.TYPE_REF_ID_NEW,
                             varPREFERRED,
                             r4.TEXT,
                             r4.PRIO,
                             r4.VERSION);
                  ELSE
                     UPDATE T_ANSPRECHPARTNER a
                     SET A.ADDRESS_ID = iSEQ_ID
                     WHERE A.ID = varID_ANSP;
                  END IF;
                  varPREFERRED := '0';
               END LOOP;
            END LOOP;
         END LOOP;
         COMMIT;
      END LOOP;

      COMMIT;
   END;
END P_FDB_MIG_ANSP_BESTAND_1;
/
CREATE OR REPLACE PROCEDURE P_FDB_MIG_ANSP_BESTAND_FIX1
IS
BEGIN
   DECLARE
      CURSOR c1
      IS
         SELECT A.ID
         FROM T_ANSPRECHPARTNER a
         WHERE NVL (a.PREFERRED, '0') = '0' AND
               A.TYPE_REF_ID IN (16006, 16010) AND
               NOT EXISTS (SELECT AA.ID
                           FROM T_ANSPRECHPARTNER aa
                           WHERE aa.auftrag_id = a.auftrag_id AND
                                 aa.TYPE_REF_ID = a.TYPE_REF_ID AND
                                 aa.PREFERRED = '1' AND
                                 aa.TYPE_REF_ID IN (16006, 16010))
         ORDER BY a.id;

      r1   c1%ROWTYPE;
   BEGIN
      FOR r1 IN c1
      LOOP
         BEGIN
            UPDATE t_ansprechpartner a
            SET a.preferred = '1'
            WHERE A.ID = r1.ID;
         EXCEPTION
            WHEN NO_DATA_FOUND
            THEN
               NULL;
            WHEN OTHERS
            THEN
               NULL;
         END;

         COMMIT;
      END LOOP;

      COMMIT;
   END;
END P_FDB_MIG_ANSP_BESTAND_FIX1;
/
CREATE OR REPLACE PROCEDURE P_FDB_MIG_ANSP_BESTAND_FIX2
IS
BEGIN
   DECLARE
      CURSOR c1
      IS
         SELECT COUNT ( * ) AS anz,
                MIN (A.ID) AS ADDRESS_ID1,
                MAX (A.ID) AS ADDRESS_ID2,
                A.KUNDE__NO,
                A.ADDRESS_TYPE,
                A.FORMAT_NAME,
                A.NAME,
                A.VORNAME,
                A.NAME2,
                A.VORNAME2,
                A.STRASSE,
                A.STRASSE_ADD,
                A.HAUSNUMMER,
                A.HAUSNUMMER_ZUSATZ,
                A.PLZ,
                A.ORT,
                A.ORT_ZUSATZ,
                A.POSTFACH,
                A.LAND_ID,
                A.TELEFON,
                A.HANDY,
                A.FAX,
                A.EMAIL
         FROM t_address a
         WHERE a.address_type IN (205, 206)
         GROUP BY A.KUNDE__NO,
                  A.ADDRESS_TYPE,
                  A.FORMAT_NAME,
                  A.NAME,
                  A.VORNAME,
                  A.NAME2,
                  A.VORNAME2,
                  A.STRASSE,
                  A.STRASSE_ADD,
                  A.HAUSNUMMER,
                  A.HAUSNUMMER_ZUSATZ,
                  A.PLZ,
                  A.ORT,
                  A.ORT_ZUSATZ,
                  A.POSTFACH,
                  A.LAND_ID,
                  A.TELEFON,
                  A.HANDY,
                  A.FAX,
                  A.EMAIL
         HAVING COUNT ( * ) > 1
         ORDER BY 1 DESC,
                  2,
                  3;

      r1   c1%ROWTYPE;
   BEGIN
      FOR i IN 1 .. 1000
      LOOP
         FOR r1 IN c1
         LOOP
            UPDATE t_ansprechpartner a
            SET A.ADDRESS_ID = r1.ADDRESS_ID1
            WHERE A.ADDRESS_ID = r1.ADDRESS_ID2;

            DELETE FROM t_address a
            WHERE a.id = r1.ADDRESS_ID2;
            COMMIT;
         END LOOP;

         COMMIT;
      END LOOP;
   END;
END P_FDB_MIG_ANSP_BESTAND_FIX2;
/
CREATE OR REPLACE PROCEDURE P_FDB_MIG_ANSP_BESTAND
IS
BEGIN
   P_FDB_MIG_ANSP_BESTAND_1;
   P_FDB_MIG_ANSP_BESTAND_FIX1;
   P_FDB_MIG_ANSP_BESTAND_FIX2;
   COMMIT;
END P_FDB_MIG_ANSP_BESTAND;
/

----------------------------------------------------------
CREATE OR REPLACE PROCEDURE P_FDB_MIG_UPD_ANSP_BESTAND
IS
BEGIN
   P_FDB_MIG_UPD_ANSP_BESTAND_1;
   P_FDB_MIG_ANSP_BESTAND_FIX1;
   P_FDB_MIG_ANSP_BESTAND_FIX2;
   COMMIT;
END P_FDB_MIG_UPD_ANSP_BESTAND;
/

----------------------------------------------------------
----------------------------------------------------------
-- API is:
----------------------------------------------------------

CREATE OR REPLACE PROCEDURE P_FDB_MIG_HUR_UND_CL
IS
BEGIN

-- neue Sicherungskopie erstellen
BEGIN
  EXECUTE IMMEDIATE 'DROP table T_TMP_LK1_ANSP';
EXCEPTION
  WHEN OTHERS
    THEN
  NULL;
END;

BEGIN
  EXECUTE IMMEDIATE 'create table T_TMP_LK1_ANSP as select * from t_ansprechpartner';
EXCEPTION
  WHEN OTHERS
    THEN
  NULL;
END;

-- erst mal die Daten innerhalb Hurricans umschreiben die auch in Clarify vorhanden sind
-- der Master Clarify ueberschreibt den Hurrican Bestand

   P_FDB_MIG_UPD_ANSP_BESTAND;

-- dann alle Clarify Daten uebernehmen

   P_FDB_MIGCL_ALL;

-- dann den Rest in die neue Struktur

   P_FDB_MIG_ANSP_BESTAND;

   COMMIT;
END P_FDB_MIG_HUR_UND_CL;
/


--------------------------------------------------------------------------
CREATE OR REPLACE PROCEDURE P_FDB_MIG_HUR_UND_CL_TMP
IS
BEGIN

-- erst mal die Daten innerhalb Hurricans umschreiben die auch in Clarify vorhanden sind
-- der Master Clarify ueberschreibt den Hurrican Bestand

   P_FDB_MIG_UPD_ANSP_BESTAND;

-- dann alle Clarify Daten uebernehmen

   P_FDB_MIGCL_ALL;

-- dann den Rest in die neue Struktur

   P_FDB_MIG_ANSP_BESTAND;

   COMMIT;
END P_FDB_MIG_HUR_UND_CL_TMP;
/

