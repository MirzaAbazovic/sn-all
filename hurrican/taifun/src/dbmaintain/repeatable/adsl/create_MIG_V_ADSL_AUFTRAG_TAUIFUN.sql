----------------------------------------------------------------------------------------
-- LKe 24.11.2009
-- abgeleitet aus der Kup Basisview MV_TKUNDENDATEN_TAIFUN
-- für die ADSL Migraion KuP nach Hurrican
--
-- abgespeckt um überflüssige Spalten
--
-- Achtung:
-- ausgenommen sind alle deluxe Produkte
-- denn diese sind zwar in der KuP sichtbar, aber technisch realisiert in Hurrican
-- see:      a.OE__NO not in (2011,2012,2013,2014) AND
----------------------------------------------------------------------------------------

CREATE OR REPLACE FORCE VIEW MV_MIG_TKUNDENDATEN_TAIFUN AS
   SELECT A.EXT_AUFTRAG_ID AS TKU_AUF_ID,
          b.NAME AS STATUS_NAME,
          A.AUFTRAG_NO,
          A.AUFTRAG__NO,
          A.HIST_STATUS,
          A.HIST_LAST,
          A.VALID_FROM,
          A.VALID_TO,
          A.SAP_ID,
          A.CUST_NO,
          A.BEARBEITER_NO,
          A.EINGANGS_ZEIT,
          A.WUNSCH_TERMIN,
          A.AUSFUEHR_ZEIT,
          C.username AS BEARBEITER_USERNAME,
          A.AP_ADDRESS_NO
   FROM
        AUFTRAG_CONNECT D,
        PERSON C,
        AUFTRAGSTATUS B,
        AUFTRAG A
   WHERE
--------------------------------------------------------------
   a.lock_date is NULL and   
--------------------------------------------------------------
-- ohne CONNECT
   a.auftrag_no = d.auftrag_no(+) and
   d.auftrag_no is NULL and
--------------------------------------------------------------
-- die deluxe Produkte sind schon in Hurr dont touch
  a.OE__NO NOT IN (2006, 3005, 2192, 2011, 2012, 2013, 2014) AND
--------------------------------------------------------------
  a.BEARBEITER_NO = c.PERSON_NO(+) AND
  a.ext_auftrag_id IS NOT NULL AND
-------------------------------------------------------------
  (a.HIST_LAST = 1 OR
   (TRIM (a.HIST_LAST) = 0 AND A.HIST_STATUS IN ('UNG', 'AKT', 'NEU'))) AND
-------------------------------------------------------------
  a.ASTATUS = b.ASTATUS
/

BEGIN
execute immediate('CREATE PUBLIC SYNONYM MV_MIG_TKUNDENDATEN_TAIFUN FOR ' || SYS_CONTEXT('USERENV','CURRENT_SCHEMA') || '.MV_MIG_TKUNDENDATEN_TAIFUN');
EXCEPTION
   WHEN NO_DATA_FOUND THEN
      NULL;
   WHEN OTHERS THEN
      NULL;
END;
/

GRANT INSERT, SELECT, UPDATE ON MV_MIG_TKUNDENDATEN_TAIFUN TO R_TAIFUN_KUP;
GRANT INSERT, SELECT, UPDATE ON MV_MIG_TKUNDENDATEN_TAIFUN TO TAIFUN_KUP;
GRANT INSERT, SELECT, UPDATE ON MV_MIG_TKUNDENDATEN_TAIFUN TO TAIFUN_KUP WITH GRANT OPTION
/


