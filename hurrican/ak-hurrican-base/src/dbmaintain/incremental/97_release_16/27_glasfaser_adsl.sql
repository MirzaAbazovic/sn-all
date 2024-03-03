INSERT INTO T_PRODUKT
(PROD_ID, PRODUKTGRUPPE_ID, ANSCHLUSSART, PROD_NAME_PATTERN,
 AKTIONS_ID, MAX_DN_COUNT, DN_BLOCK, DN_TYP,
 GUELTIG_VON, GUELTIG_BIS, AUFTRAGSERSTELLUNG, LTGNR_ANLEGEN,
 BRAUCHT_BUENDEL, ELVERLAUF, ABRECHNUNG_IN_HURRICAN, ENDSTELLEN_TYP,
 ACCOUNT_VORS, LI_NR, VPN_PHYSIK, PROJEKTIERUNG, IS_PARENT,
 CHECK_CHILD, IS_COMBI_PRODUKT, AUTO_PRODUCT_CHANGE, EXPORT_KDP_M, CREATE_KDP_ACCOUNT_REPORT,
 VERTEILUNG_DURCH, BA_RUECKLAEUFER, VERLAUF_CHAIN_ID, PROJEKTIERUNG_CHAIN_ID,
 VERLAUF_CANCEL_CHAIN_ID, BA_CHANGE_REALDATE, CREATE_AP_ADDRESS, ASSIGN_IAD, CPS_PROVISIONING,
 CPS_PROD_NAME, CPS_AUTO_CREATION, CPS_ACCOUNT_TYPE, CPS_DSL_PRODUCT,
 TDN_KIND_OF_USE_PRODUCT, TDN_KIND_OF_USE_TYPE, VIER_DRAHT, VERSION, MIN_DN_COUNT, CPS_IP_DEFAULT, CPS_MULTI_DRAHT,
 IP_POOL, IP_PURPOSE_V4, IP_PURPOSE_V4_EDITABLE, IP_NETMASK_SIZE_V4, IP_NETMASK_SIZE_V6, IP_NETMASK_SIZE_EDITABLE,
 AUTOMATION_POSSIBLE, GEOID_SRC, AUTO_HVT_ZUORDNUNG, SMS_VERSAND)
VALUES
  (542, 33, 'Glasfaser ADSL', 'Glasfaser ADSL {DOWNSTREAM}',
   1, 0, NULL, NULL,
   SYSDATE, TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), '0', '1',
   '0', '1', '0', 1,
   'X9', 1, '1', '0', '0',
   '0', '0', '0', '1', '1',
   4, '1', 125, 170,
   15, '1', '0', '0', '1',
   'GlasfaserADSL', '0', 'SDSL', '1',
   'W', 'V', '0', 0, 0, '1', '0',
   1, 22371, '1', 32, 48, '1',
   '0', 'HVT', '1', '0'
  );

-- 50000 down
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, IS_DEFAULT, VERSION)
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 542, 24, '0', 0);
-- 5000 up
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 542, 27, 24, '1', 0);
-- feste IP
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, IS_DEFAULT, VERSION)
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 542, 6, '1', 0);
-- Quality-of-Service - QoS
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, IS_DEFAULT, VERSION)
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 542, 50, '0', 0);
-- Business-CPE
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, IS_DEFAULT, VERSION)
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 542, 325, '1', 0);
-- VOIP_READY_66
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, IS_DEFAULT, VERSION)
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 542, 460, '0', 0);


INSERT INTO T_PRODUKT_2_SCHNITTSTELLE (PROD_ID, SCHNITTSTELLE_ID) VALUES (542, 38);

-- Equipment-Konfig definieren
INSERT INTO T_PRODUKT_EQ_CONFIG
(ID, PROD_ID, CONFIG_GROUP, EQ_TYP, EQ_PARAM, EQ_VALUE, VERSION)
VALUES (S_T_PRODUKT_EQ_CONFIG_0.nextVal, 542, 1, 'EQ_OUT', 'carrier', 'MNET', 0);
INSERT INTO T_PRODUKT_EQ_CONFIG
(ID, PROD_ID, CONFIG_GROUP, EQ_TYP, EQ_PARAM, EQ_VALUE, VERSION)
VALUES (S_T_PRODUKT_EQ_CONFIG_0.nextVal, 542, 1, 'EQ_OUT', 'rangSSType', 'FttB', 0);

-- Sperren
INSERT INTO T_SPERRE_VERTEILUNG (PROD_ID, ABTEILUNG_ID) VALUES (542, 2);

-- keine Telefonie keine - keine T_PROD_2_SIP_DOMAIN

-- Physikzuordnung
INSERT INTO T_PRODUKT_2_PHYSIKTYP
(ID, PROD_ID, PHYSIKTYP, VIRTUELL, VERSION)
VALUES (S_T_PRODUKT_2_PHYSIKTYP_0.nextVal, 542, 800, '0', 0);
INSERT INTO T_PRODUKT_2_PHYSIKTYP
(ID, PROD_ID, PHYSIKTYP, VIRTUELL, VERSION)
VALUES (S_T_PRODUKT_2_PHYSIKTYP_0.nextVal, 542, 804, '0', 0);
INSERT INTO T_PRODUKT_2_PHYSIKTYP
(ID, PROD_ID, PHYSIKTYP, VIRTUELL, VERSION)
VALUES (S_T_PRODUKT_2_PHYSIKTYP_0.nextVal, 542, 808, '0', 0);

-- Standorttypen definieren
INSERT INTO T_PRODUKT_2_TECH_LOCATION_TYPE
(ID, PROD_ID, TECH_LOCATION_TYPE_REF_ID, PRIORITY, USERW, DATEW, VERSION)
VALUES (S_T_PRODUKT_2_TECH_LOC_TYPE_0.nextVal, 542, 11011, 1, 'IMPORT', SYSDATE, 0);
INSERT INTO T_PRODUKT_2_TECH_LOCATION_TYPE
(ID, PROD_ID, TECH_LOCATION_TYPE_REF_ID, PRIORITY, USERW, DATEW, VERSION)
VALUES (S_T_PRODUKT_2_TECH_LOC_TYPE_0.nextVal, 542, 11002, 2, 'IMPORT', SYSDATE, 0);
INSERT INTO T_PRODUKT_2_TECH_LOCATION_TYPE
(ID, PROD_ID, TECH_LOCATION_TYPE_REF_ID, PRIORITY, USERW, DATEW, VERSION)
VALUES (S_T_PRODUKT_2_TECH_LOC_TYPE_0.nextVal, 542, 11013, 3, 'IMPORT', SYSDATE, 0);
INSERT INTO T_PRODUKT_2_TECH_LOCATION_TYPE
(ID, PROD_ID, TECH_LOCATION_TYPE_REF_ID, PRIORITY, USERW, DATEW, VERSION)
VALUES (S_T_PRODUKT_2_TECH_LOC_TYPE_0.nextVal, 542, 11000, 4, 'IMPORT', SYSDATE, 0);

-- Produktaenderungen konfigurieren
INSERT INTO T_PROD_2_PROD (ID, PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID, VERSION)
VALUES (S_T_PROD_2_PROD_0.nextVal, 513, 542, 5000, 1, 0);
INSERT INTO T_PROD_2_PROD (ID, PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID, VERSION)
VALUES (S_T_PROD_2_PROD_0.nextVal, 512, 542, 5000, 1, 0);
INSERT INTO T_PROD_2_PROD (ID, PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID, VERSION)
VALUES (S_T_PROD_2_PROD_0.nextVal, 540, 542, 5000, 1, 0);
INSERT INTO T_PROD_2_PROD (ID, PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID, VERSION)
VALUES (S_T_PROD_2_PROD_0.nextVal, 542, 512, 5000, 1, 0);
INSERT INTO T_PROD_2_PROD (ID, PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID, VERSION)
VALUES (S_T_PROD_2_PROD_0.nextVal, 542, 513, 5000, 1, 0);
INSERT INTO T_PROD_2_PROD (ID, PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID, VERSION)
VALUES (S_T_PROD_2_PROD_0.nextVal, 542, 540, 5000, 1, 0);
INSERT INTO T_PROD_2_PROD (ID, PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID, VERSION)
VALUES (S_T_PROD_2_PROD_0.nextVal, 542, 541, 5000, 1, 0);
INSERT INTO T_PROD_2_PROD (ID, PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID, VERSION)
VALUES (S_T_PROD_2_PROD_0.nextVal, 541, 542, 5000, 1, 0);

-- kein DSLAM Profile noetig

-- DSLAM Profile zuordnen
INSERT INTO T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID)
VALUES (542, (SELECT
                d.id
              FROM T_DSLAM_PROFILE d
              WHERE d.NAME = 'MD_50000_5000_H' AND d.BAUGRUPPEN_TYP IS NULL));

-- Produktmapping
INSERT INTO T_PRODUKT_MAPPING (MAPPING_GROUP, EXT_PROD__NO, PROD_ID, MAPPING_PART_TYPE, VERSION)
VALUES (1047, 542, 542, 'dsl', 0);

-- BA Anlaesse konfigurieren
INSERT INTO T_BA_VERL_AEND_PROD_2_GRUPPE (PROD_ID, BA_VERL_AEND_GRUPPE_ID) VALUES (542, 1);
INSERT INTO T_BA_VERL_AEND_PROD_2_GRUPPE (PROD_ID, BA_VERL_AEND_GRUPPE_ID) VALUES (542, 2);

-- BA Konfiguration
-- Neuschaltung
INSERT INTO T_BA_VERL_CONFIG
(ID, PROD_ID, ANLASS, ABT_CONFIG_ID, GUELTIG_VON, GUELTIG_BIS, DATEW, USERW, VERSION, AUTO_VERTEILEN, CPS_NECESSARY)
VALUES
  (S_T_BA_VERL_CONFIG_0.nextVal, 542, 27, 10, SYSDATE,
   TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), SYSDATE, 'Import', 0, '0', '0');
-- Anbieterwechsel §46TKG Neuschaltung
INSERT INTO T_BA_VERL_CONFIG
(ID, PROD_ID, ANLASS, ABT_CONFIG_ID, GUELTIG_VON, GUELTIG_BIS, DATEW, USERW, VERSION, AUTO_VERTEILEN, CPS_NECESSARY)
VALUES
  (S_T_BA_VERL_CONFIG_0.nextVal, 542, 72, 10, SYSDATE,
   TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), SYSDATE, 'Import', 0, '1', '0');
-- Kündigung
INSERT INTO T_BA_VERL_CONFIG
(ID, PROD_ID, ANLASS, ABT_CONFIG_ID, GUELTIG_VON, GUELTIG_BIS, DATEW, USERW, VERSION, AUTO_VERTEILEN, CPS_NECESSARY)
VALUES
  (S_T_BA_VERL_CONFIG_0.nextVal, 542, 13, 10, SYSDATE,
   TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), SYSDATE, 'Import', 0, '1', '1');


-- CPS-Chains
INSERT INTO T_SERVICE_CHAIN (ID, NAME, TYPE, DESCRIPTION, VERSION)
VALUES (S_T_SERVICE_CHAIN_0.nextVal, 'CPS - FttX ADSL', 'CPS_DATA',
        'Chain-Definition, um Daten fuer ADSL FttX-Produkte zu ermitteln.', 0);

-- CPS
-- create subscriber
INSERT INTO T_CPS_DATA_CHAIN_CONFIG (ID, PROD_ID, SO_TYPE_REF_ID, CHAIN_ID, VERSION)
VALUES (S_T_CPS_DATA_CHAIN_CONFIG_0.nextVal, 542, 14000, (SELECT
                                                            id
                                                          FROM T_SERVICE_CHAIN
                                                          WHERE name = 'CPS - FttX ADSL'), 0);
-- modify subscriber
INSERT INTO T_CPS_DATA_CHAIN_CONFIG (ID, PROD_ID, SO_TYPE_REF_ID, CHAIN_ID, VERSION)
VALUES (S_T_CPS_DATA_CHAIN_CONFIG_0.nextVal, 542, 14001, (SELECT
                                                            id
                                                          FROM T_SERVICE_CHAIN
                                                          WHERE name = 'CPS - FttX ADSL'), 0);
-- lock subscriber
INSERT INTO T_CPS_DATA_CHAIN_CONFIG (ID, PROD_ID, SO_TYPE_REF_ID, CHAIN_ID, VERSION)
VALUES (S_T_CPS_DATA_CHAIN_CONFIG_0.nextVal, 542, 14006, (SELECT
                                                            id
                                                          FROM T_SERVICE_CHAIN
                                                          WHERE name = 'CPS - FttX ADSL'), 0);

INSERT INTO T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, (SELECT
                                                id
                                              FROM T_SERVICE_COMMANDS
                                              WHERE name = 'cps.radius.sdsl.data'),
        (SELECT
           id
         FROM T_SERVICE_CHAIN
         WHERE name = 'CPS - FttX ADSL'), 'de.augustakom.hurrican.model.cc.command.ServiceChain', 10, 0);
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, (SELECT
                                                id
                                              FROM T_SERVICE_COMMANDS
                                              WHERE name = 'cps.fttx.data'),
        (SELECT
           id
         FROM T_SERVICE_CHAIN
         WHERE name = 'CPS - FttX ADSL'), 'de.augustakom.hurrican.model.cc.command.ServiceChain', 30, 0);
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, (SELECT
                                                id
                                              FROM T_SERVICE_COMMANDS
                                              WHERE name = 'cps.dsl.data'),
        (SELECT
           id
         FROM T_SERVICE_CHAIN
         WHERE name = 'CPS - FttX ADSL'), 'de.augustakom.hurrican.model.cc.command.ServiceChain', 40, 0);
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, (SELECT
                                                id
                                              FROM T_SERVICE_COMMANDS
                                              WHERE name = 'cps.business.cpe.data'),
        (SELECT
           id
         FROM T_SERVICE_CHAIN
         WHERE name = 'CPS - FttX ADSL'), 'de.augustakom.hurrican.model.cc.command.ServiceChain', 50, 0);

-- EG Konfig
INSERT INTO T_PROD_2_EG (ID, PROD_ID, EG_ID, IS_DEFAULT, IS_ACTIVE, VERSION)
VALUES (S_T_PROD_2_EG_0.nextVal, 542, 5, '1', '1', 0);

-- T_REPORT_2_PROD / T_REP2PROD_STATI
CREATE OR REPLACE PROCEDURE clone_report_2_prod(prod_id_src IN NUMBER, prod_id_dest IN NUMBER)
IS
  id NUMBER(19, 0);
  BEGIN
    FOR r_2_p IN (SELECT
                    r2p.ID,
                    r2p.REP_ID
                  FROM T_REPORT_2_PROD r2p
                  WHERE r2p.PROD_ID = prod_id_src) LOOP
      SELECT
        S_T_REPORT_2_PROD_0.nextVal
      INTO id
      FROM DUAL;
      INSERT INTO T_REPORT_2_PROD (ID, REP_ID, PROD_ID, VERSION)
      VALUES (id, r_2_p.REP_ID, prod_id_dest, 0);

      FOR r2p_s IN (SELECT
                      STATUS_ID
                    FROM T_REP2PROD_STATI
                    WHERE REP2PROD_ID = r_2_p.ID) LOOP
        INSERT INTO T_REP2PROD_STATI (ID, REP2PROD_ID, STATUS_ID, VERSION)
        VALUES (S_T_REP2PROD_STATI_0.nextVal, id, r2p_s.STATUS_ID, 0);
      END LOOP;
    END LOOP;
  END;
/

CALL clone_report_2_prod(541, 542);
DROP PROCEDURE clone_report_2_prod;

-- CVLAN: HSIPLUS + IAD
INSERT INTO T_PROD_2_CVLAN (ID, VERSION, PROD_ID, CVLAN_TYP, TECH_LOCATION_TYPE_REF_ID, HVT_TECHNIK_ID, IS_MANDATORY)
VALUES (S_T_PROD_2_CVLAN_0.nextVal, 0, 542, 'HSIPLUS', NULL, NULL, '1');
INSERT INTO T_PROD_2_CVLAN (ID, VERSION, PROD_ID, CVLAN_TYP, TECH_LOCATION_TYPE_REF_ID, HVT_TECHNIK_ID, IS_MANDATORY)
VALUES (S_T_PROD_2_CVLAN_0.nextVal, 0, 542, 'IAD', NULL, NULL, '1');
