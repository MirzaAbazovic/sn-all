Insert into T_PRODUKT
(PROD_ID, PRODUKTGRUPPE_ID, ANSCHLUSSART, PROD_NAME_PATTERN, AKTIONS_ID,
 MAX_DN_COUNT, DN_BLOCK, DN_TYP, GUELTIG_VON, GUELTIG_BIS,
 AUFTRAGSERSTELLUNG, LTGNR_ANLEGEN, BRAUCHT_BUENDEL, ELVERLAUF, ENDSTELLEN_TYP,
 BESCHREIBUNG, ACCOUNT_VORS, LI_NR, VPN_PHYSIK, PROJEKTIERUNG,
 IS_PARENT, CHECK_CHILD, IS_COMBI_PRODUKT, AUTO_PRODUCT_CHANGE, EXPORT_KDP_M,
 CREATE_KDP_ACCOUNT_REPORT, VERTEILUNG_DURCH, BA_RUECKLAEUFER, VERLAUF_CHAIN_ID, VERLAUF_CANCEL_CHAIN_ID,
 BA_CHANGE_REALDATE, CREATE_AP_ADDRESS, ASSIGN_IAD, CPS_PROVISIONING, CPS_DSL_PRODUCT,
 TDN_KIND_OF_USE_PRODUCT, TDN_KIND_OF_USE_TYPE, VIER_DRAHT, VERSION, MIN_DN_COUNT,
 CPS_IP_DEFAULT, CPS_MULTI_DRAHT, IP_POOL, IP_PURPOSE_V4, IP_PURPOSE_V4_EDITABLE,
 IP_NETMASK_SIZE_V4, IP_NETMASK_SIZE_EDITABLE, AUTOMATION_POSSIBLE, GEOID_SRC, AUTO_HVT_ZUORDNUNG,
 SMS_VERSAND)
Values
  (447, 8, 'ADSL-QSC', 'ADSL-QSC {REALVARIANTE} {DOWNSTREAM}', 1,
   2, '0', 60, TO_DATE('01/08/2014 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'),
   '0', '1', '0', '1', 1,
   'von uns bei Fremdcarrier bestellt', 'XA', 1, '0', '0',
   '0', '0', '0', '0', '1',
   '1', 11, '0', 29, 15,
   '1', '0', '0', '0', '0',
   'V', 'L', '0', 8, 0,
   '0', '0', 1, 22371, '1',
   32, '0', '0', 'HVT', '1',
   '0');

-- PRODUKT_2_SCHNITTSTELLE
CREATE OR REPLACE PROCEDURE clone_prod_2_ss(prod_id_src in NUMBER, prod_id_dest in NUMBER)
IS
  BEGIN
    FOR p2s IN (select p.schnittstelle_id from T_PRODUKT_2_SCHNITTSTELLE p where p.PROD_ID = prod_id_src) LOOP
      INSERT INTO T_PRODUKT_2_SCHNITTSTELLE
      (PROD_ID, SCHNITTSTELLE_ID)
      VALUES
        (prod_id_dest, p2s.SCHNITTSTELLE_ID)
    ;
    END LOOP;
  END;
/

call clone_prod_2_ss(441, 447);
DROP PROCEDURE clone_prod_2_ss;

-- T_PROD_2_SIP_DOMAIN -> nicht noetig da kein VOIP

-- SPERRE_VERTEILUNG
Insert into T_SPERRE_VERTEILUNG
(PROD_ID, ABTEILUNG_ID)
Values
  (447, 2);

-- T_PROD_2_PHYSIKTYP -> explizit in Userstory ausgeschlossen
-- T_PROD_2_PROD -> macht keinen Sinn ohne PHYSIKTYP

-- PROD_2_TECH_LOCATION_TYPE
CREATE OR REPLACE PROCEDURE clone_prod_2_tech_loc_type(prod_id_src in NUMBER, prod_id_dest in NUMBER)
IS
  BEGIN
    FOR p2s IN (select * from T_PRODUKT_2_TECH_LOCATION_TYPE p where p.PROD_ID = prod_id_src) LOOP
      INSERT INTO T_PRODUKT_2_TECH_LOCATION_TYPE
      (ID, PROD_ID, TECH_LOCATION_TYPE_REF_ID, PRIORITY,
       USERW, DATEW, VERSION)
      VALUES
        (S_T_PRODUKT_2_TECH_LOC_TYPE_0.nextval, prod_id_dest, p2s.TECH_LOCATION_TYPE_REF_ID, p2s.priority,
         'dbmaintain', TO_DATE('01/08/2014 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 0)
    ;
    END LOOP;
  END;
/
-- T_PROD_2_DSLAMPROFILE nicht noetig (hat Produkt 441 auch nicht)

call clone_prod_2_tech_loc_type(441, 447);
DROP PROCEDURE clone_prod_2_tech_loc_type;

-- REPORT_2_PROD  / REP2PROD_STATI
CREATE OR REPLACE PROCEDURE clone_report_2_prod(prod_id_src in NUMBER, prod_id_dest in NUMBER)
IS
  id NUMBER(19,0);
  BEGIN
    FOR r_2_p IN (SELECT r2p.ID, r2p.REP_ID FROM T_REPORT_2_PROD r2p WHERE r2p.PROD_ID = prod_id_src) LOOP

      SELECT S_T_REPORT_2_PROD_0.nextVal INTO id FROM DUAL;
      INSERT INTO T_REPORT_2_PROD
      (ID, REP_ID, PROD_ID, VERSION)
      VALUES
        (id, r_2_p.REP_ID, prod_id_dest, 0)
    ;
      FOR r2p_s IN (SELECT STATUS_ID FROM T_REP2PROD_STATI WHERE REP2PROD_ID = r_2_p.ID) LOOP
        INSERT INTO T_REP2PROD_STATI
        (ID, REP2PROD_ID, STATUS_ID, VERSION)
        VALUES
          (S_T_REP2PROD_STATI_0.nextVal, id, r2p_s.STATUS_ID, 0)
      ;
      END LOOP;
    END LOOP;
  END;
/

call clone_report_2_prod(441, 447);
drop procedure clone_report_2_prod;

-- T_CPS_DATA_CHAIN_CONFIG -> keine CPS-Provisionierung gewuenscht

-- T_PRODUKT_MAPPING
Insert into T_PRODUKT_MAPPING
(MAPPING_GROUP, EXT_PROD__NO, PROD_ID, MAPPING_PART_TYPE, VERSION)
Values
  (1300, 447, 447, 'dsl', 0);

-- T_BA_VERL_CONFIG -> nciht vorgesehen fuer Produkt 441

-- T_BA_VERL_AEND_PROD_2_GRUPPE
Insert into T_BA_VERL_AEND_PROD_2_GRUPPE
(PROD_ID, BA_VERL_AEND_GRUPPE_ID)
Values
  (447, 1);
Insert into T_BA_VERL_AEND_PROD_2_GRUPPE
(PROD_ID, BA_VERL_AEND_GRUPPE_ID)
Values
  (447, 2);
