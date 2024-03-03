DELETE FROM T_PRODUKT_2_TECH_LOCATION_TYPE WHERE PROD_ID = 600;
DELETE FROM T_RANGIERUNGSMATRIX WHERE PROD_ID = 600;
DELETE FROM T_PROD_2_DSLAMPROFILE WHERE PROD_ID = 600;
DELETE FROM T_PROD_2_TECH_LEISTUNG WHERE PROD_ID = 600;

-- Umbenennung Wholesale Produkt
UPDATE T_PRODUKT SET ANSCHLUSSART = 'Wholesale FTTX', BESCHREIBUNG = 'Wholesale FttX Auftrag' WHERE PROD_ID = 600;

-- Schnittstelle anlegen (analog zu Produkt 512, FTTX DSL)
INSERT INTO T_PRODUKT_2_SCHNITTSTELLE (PROD_ID, SCHNITTSTELLE_ID) VALUES (600, 38);

-- Sperre anlegen (analog zu Produkt 512, FTTX DSL)
INSERT INTO T_SPERRE_VERTEILUNG (PROD_ID, ABTEILUNG_ID) VALUES (600, 2);
INSERT INTO T_SPERRE_VERTEILUNG (PROD_ID, ABTEILUNG_ID) VALUES (600, 3);

-- Produkt dem Physiktyp zuordnen
INSERT INTO T_PRODUKT_2_PHYSIKTYP (ID, PROD_ID, PHYSIKTYP, VIRTUELL, PRIORITY, VERSION, USE_IN_RANG_MATRIX)
 VALUES (S_T_PRODUKT_2_PHYSIKTYP_0.nextVal, 600, 804, 0, 200, 1, 1);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (ID, PROD_ID, PHYSIKTYP, VIRTUELL, PRIORITY, VERSION, USE_IN_RANG_MATRIX)
 VALUES (S_T_PRODUKT_2_PHYSIKTYP_0.nextVal, 600, 808, 0, 200, 1, 0);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (ID, PROD_ID, PHYSIKTYP, VIRTUELL, PRIORITY, VERSION, USE_IN_RANG_MATRIX)
 VALUES (S_T_PRODUKT_2_PHYSIKTYP_0.nextVal, 600, 809, 0, 100, 1, 1);

-- Produkt_2_Tech_Location_Typ (analog zu Produkt 512, FTTX DSL)
CREATE OR REPLACE PROCEDURE create_p2tecloctype_dslvoip
IS
   BEGIN
    FOR p2tl IN (select p.TECH_LOCATION_TYPE_REF_ID, p.PRIORITY from T_PRODUKT_2_TECH_LOCATION_TYPE p where p.PROD_ID = 512) LOOP
      INSERT INTO T_PRODUKT_2_TECH_LOCATION_TYPE
       (ID, PROD_ID, TECH_LOCATION_TYPE_REF_ID, PRIORITY, USERW, DATEW, VERSION)
       VALUES
	   (S_T_PRODUKT_2_TECH_LOC_TYPE_0.nextVal, 600, p2tl.TECH_LOCATION_TYPE_REF_ID, p2tl.PRIORITY, 'IMPORT', SYSDATE, 0)
	  ;
    END LOOP;
   END;
/

call create_p2tecloctype_dslvoip();

DROP PROCEDURE create_p2tecloctype_dslvoip;

--Physiktypen anlegen (physiktyp_id zu Produkt 512, FTTX DSL)
CREATE OR REPLACE PROCEDURE rangmatrix_dslvoip_adsl2plus_h (physiktyp_id IN NUMBER, prio IN NUMBER)
IS
   BEGIN
    FOR uevtid IN (select UEVT.UEVT_ID, hvt.HVT_ID_STANDORT from T_UEVT uevt
      join T_HVT_STANDORT hvt on (uevt.HVT_ID_STANDORT = hvt.HVT_ID_STANDORT)
      join T_REFERENCE refer on (REFER.ID = hvt.STANDORT_TYP_REF_ID)
      where REFER.STR_VALUE IN ('FTTH', 'FTTB', 'FTTB_H', 'FTTC_KVZ')) LOOP
          INSERT INTO T_RANGIERUNGSMATRIX matrix
            (ID, PROD_ID, UEVT_ID, PRODUKT2PHYSIKTYP_ID, PRIORITY, HVT_STANDORT_ID_ZIEL, PROJEKTIERUNG, GUELTIG_VON, GUELTIG_BIS, BEARBEITER)
            VALUES
            (S_T_RANGIERUNGSMATRIX_0.NEXTVAL, 600, uevtid.UEVT_ID,
            (select prod_2_physik.ID from T_PRODUKT_2_PHYSIKTYP prod_2_physik
            where PROD_2_PHYSIK.PROD_ID = 600
            AND PROD_2_PHYSIK.PHYSIKTYP = physiktyp_id
            AND PARENTPHYSIKTYP_ID IS NULL),
            prio, uevtid.hvt_id_standort, null, to_date('21/06/2016','DD/MM/YYYY'), to_date('01/01/2200','DD/MM/YYYY'), 'Import')
          ;
    END LOOP;
   END;
/
call rangmatrix_dslvoip_adsl2plus_h(800, 1);
call rangmatrix_dslvoip_adsl2plus_h(804, 1);
call rangmatrix_dslvoip_adsl2plus_h(808, 0);
call rangmatrix_dslvoip_adsl2plus_h(809, 1);

DROP PROCEDURE rangmatrix_dslvoip_adsl2plus_h;

-- DSLAM-Profile (analog zu Produkt 512, FTTX DSL)
CREATE OR REPLACE PROCEDURE create_prod_2_dslam_profile
IS
    BEGIN
     FOR dslam_profile_id IN (select P2D.DSLAM_PROFILE_ID from T_PROD_2_DSLAMPROFILE p2d where p2d.PROD_ID = 512) LOOP
         INSERT INTO T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID)
             VALUES (600, dslam_profile_id.DSLAM_PROFILE_ID)
         ;
     END LOOP;
    END;
/

call create_prod_2_dslam_profile();

DROP PROCEDURE create_prod_2_dslam_profile;

-- REPORT_2_PROD  / REP2PROD_STATI (analog zu Produkt 512, FTTX DSL)
CREATE OR REPLACE PROCEDURE clone_report_2_prod
IS
  id NUMBER(19,0);
  BEGIN
    FOR r_2_p IN (SELECT r2p.ID, r2p.REP_ID FROM T_REPORT_2_PROD r2p WHERE r2p.PROD_ID = 512) LOOP

      SELECT S_T_REPORT_2_PROD_0.nextVal INTO id FROM DUAL;
      INSERT INTO T_REPORT_2_PROD(ID, REP_ID, PROD_ID, VERSION) VALUES (id, r_2_p.REP_ID, 600, 0);

      FOR r2p_s IN (SELECT STATUS_ID FROM T_REP2PROD_STATI WHERE REP2PROD_ID = r_2_p.ID) LOOP
        INSERT INTO T_REP2PROD_STATI (ID, REP2PROD_ID, STATUS_ID, VERSION) VALUES (S_T_REP2PROD_STATI_0.nextVal, id, r2p_s.STATUS_ID, 0)
      ;
      END LOOP;
    END LOOP;
  END;
/

call clone_report_2_prod();
drop procedure clone_report_2_prod;

--Neue Kombileistung fuer 300/30
INSERT INTO T_TECH_LEISTUNG
(ID, NAME, EXTERN_LEISTUNG__NO, TYP, PROD_NAME_STR, DESCRIPTION,
 DISPO, EWSD, SDH, IPS, SCT,
 SNAPSHOT_REL, GUELTIG_VON, GUELTIG_BIS, VERSION, PREVENT_AUTO_DISPATCH,
 AUTO_EXPIRE)
  SELECT
  546, '300/30_VOIP_IPV6', 32000, 'KOMBI', '.', 'KOMBI-LEISTUNG, FUEHRT ZU 300 MBIT, 30 MBIT, VOIP IPV6',
        '0', '0', '0', '0', '0',
   '1', TO_DATE('01/06/2016 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 0, '0', '0'
  FROM DUAL WHERE NOT EXISTS (SELECT * FROM T_TECH_LEISTUNG WHERE ID = 546 AND EXTERN_LEISTUNG__NO = 32000) ;

--Tech. Leistungen fuer Produkt konfigurieren
--100000 kbit/s (downstream)
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
 VALUES (S_T_PROD_2_TECH_LEISTUNG_0.NEXTVAL, 600, 25, 542, '0', 0);
--40.000 kbit/s (upstream)
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
 VALUES (S_T_PROD_2_TECH_LEISTUNG_0.NEXTVAL, 600, 74, 542, '0', 0);
--dynamische IPv6
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
 VALUES (S_T_PROD_2_TECH_LEISTUNG_0.NEXTVAL, 600, 57, 542, '0', 0);
--50000 kbit/s (downstream)
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
 VALUES (S_T_PROD_2_TECH_LEISTUNG_0.NEXTVAL, 600, 24, 541, '0', 0);
--10000 kbit/s (upstream)
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
 VALUES (S_T_PROD_2_TECH_LEISTUNG_0.NEXTVAL, 600, 28, 541, '0', 0);
--dynamische IPv6
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
 VALUES (S_T_PROD_2_TECH_LEISTUNG_0.NEXTVAL, 600, 57, 541, '0', 0);
--300000 kbit/s (downstream)
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
 VALUES (S_T_PROD_2_TECH_LEISTUNG_0.NEXTVAL, 600, 66, 546, '0', 0);
--30000 kbit/s (upstream)
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
 VALUES (S_T_PROD_2_TECH_LEISTUNG_0.NEXTVAL, 600, 67, 546, '0', 0);
--dynamische IPv6
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
 VALUES (S_T_PROD_2_TECH_LEISTUNG_0.NEXTVAL, 600, 57, 546, '0', 0);