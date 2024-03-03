-- +++++++++++++++++++++++++++++++++++++++++++++++++++
-- Copyright (c) 2008 - M-net Telekommunikations GmbH
-- All rights reserved.
-- +++++++++++++++++++++++++++++++++++++++++++++++++++
--
-- View zur Darstellung von TAL-Bestellungen zu
-- Endstellen.
--
-- Verwendete Tabellen:
--   + T_ENDSTELLE
--   + T_CARRIERBESTELLUNG
--   + T_CARRIER
--   + T_CARRIER_KENNUNG
--   + T_TAL_AENDERUNGSTYP
--   + T_HVT_STANDORT
--
-- Besonderheiten:
--   + in Hurrican kann es zu einer Endstelle mehrere TAL-Bestellungen geben; Die
--     View stellt sicher, dass immer nur die neueste TAL-Bestellung ermittelt wird.
--  Filterung wurde auf Wunsch von BSI entfernt.
--

CREATE OR REPLACE FUNCTION F_DTAG_LTG_LEN_2_LEN (VARDTAG_LTG_LEN VARCHAR2)
   RETURN VARCHAR2
   DETERMINISTIC
IS
   IRET   NUMBER (11, 2);
BEGIN
   DECLARE
      VARTMP            VARCHAR2 (127);
      VARABSCHNITT      VARCHAR2 (127);
      IINSTR            NUMBER (11);
      ICONVERT_KM_2_M   NUMBER;
   BEGIN
      IF VARDTAG_LTG_LEN IS NULL
      THEN
         IRET := NULL;
      ELSE
         VARTMP :=
          REPLACE(
          REPLACE(
          REPLACE(
          REPLACE(
          REPLACE(
          REPLACE(
          REPLACE(
          REPLACE(
          REPLACE(
    ----------------------------
          REPLACE(
          VARDTAG_LTG_LEN
          ,'.',',')
    ----------------------------
          ,'''','/')
          ,' ','/')
          ,'*','/')
          ,'^','/')
          ,'&','/')
          ,'_','/')
          ,'(','/')
          ,'//','/')
          ,'m','');

         IINSTR := INSTR (VARTMP, '/');

         IF IINSTR = 0
         THEN
            VARABSCHNITT := VARTMP;

            BEGIN
               SELECT   TO_NUMBER (VARABSCHNITT,
                                   '99999999999D99',
                                   'NLS_NUMERIC_CHARACTERS = '', ''')
                 INTO   IRET
                 FROM   DUAL;

               IF INSTR (VARABSCHNITT, ',') > 0
               THEN
                  IRET := IRET * 1000;
               END IF;
            EXCEPTION
               WHEN OTHERS
               THEN
                  IRET := 0;
            END;
         ELSE
            VARABSCHNITT := SUBSTR (VARTMP, 1, IINSTR - 1);

            BEGIN
               SELECT   TO_NUMBER (VARABSCHNITT,
                                   '99999999999D99',
                                   'NLS_NUMERIC_CHARACTERS = '', ''')
                 INTO   IRET
                 FROM   DUAL;

               IF INSTR (VARABSCHNITT, ',') > 0
               THEN
                  IRET := IRET * 1000;
               END IF;

               IRET :=
                  IRET + F_DTAG_LTG_LEN_2_LEN (SUBSTR (VARTMP, IINSTR + 1));
            EXCEPTION
               WHEN OTHERS
               THEN
                  IRET :=
                     0 + F_DTAG_LTG_LEN_2_LEN (SUBSTR (VARTMP, IINSTR + 1));
            END;
         END IF;
      END IF;

      RETURN IRET;
   END;
END F_DTAG_LTG_LEN_2_LEN;
/

CREATE or REPLACE FORCE VIEW V_HURRICAN_TAL_ORDER
  AS SELECT
    e.ID as ACCESSPOINT_ID,
    cb.CB_ID as TAL_ORDER_ID,
    cb.BESTELLT_AM as ORDER_DATE,
    cb.VORGABEDATUM as WISH_DATE,
    cb.ZURUECK_AM as RETURN_DATE,
    cb.BEREITSTELLUNG_AM as REAL_DATE,
    cb.KUNDE_VOR_ORT as CUSTOMER_AT_LOCATION,
    cb.LBZ as LBZ,
    cb.VTRNR as VTRNR,
    cb.AQS as AQS,
    cb.LL as DISTANCE,
    F_DTAG_LTG_LEN_2_LEN (CB.LL) AS DISTANCE_SUM,
    cb.KUENDIGUNG_AN_CARRIER as CANCEL_DATE,
    cb.KUENDBESTAETIGUNG_CARRIER CANCEL_RETURN_DATE,
    cb.NEGATIVERM as RETURN_DATE_NEG,
    cb.WIEDERVORLAGE as WIEDERVORLAGE,
    cb.AUFTRAG_ID_4_TAL_NA as TECH_ORDER_ID_4_TAL_CHANGE,
    cb.TAL_NA_TYP as TAL_CHANGE_ID,
    cb.AI_ADDRESS_ID as AI_ADDRESS_ID,
    t.NAME as TAL_CHANGE,
    cb.CARRIER_ID as CARRIER_ID,
    c.TEXT as CARRIER,
    ck.KUNDE_NR as MNET_CUSTOMER_NO,
    ck.PORTIERUNGSKENNUNG as MNET_PORT_KEY,
    ck.NAME as MNET_NAME,
    ck.STRASSE as MNET_STREET,
    ck.PLZ as MNET_POSTAL_CODE,
    ck.ORT as MNET_CITY
  FROM
    T_ENDSTELLE e
    INNER JOIN T_CARRIERBESTELLUNG cb on e.CB_2_ES_ID=cb.CB_2_ES_ID
    INNER JOIN T_CARRIER c on cb.CARRIER_ID=c.ID
    LEFT JOIN T_HVT_STANDORT hs on e.HVT_ID_STANDORT=hs.HVT_ID_STANDORT
    LEFT JOIN T_CARRIER_KENNUNG ck on hs.CARRIER_KENNUNG_ID=ck.ID
    LEFT JOIN T_TAL_AENDERUNGSTYP t on cb.TAL_NA_TYP=t.ID
  ORDER BY cb.CB_ID DESC;

CREATE OR REPLACE SYNONYM BSICRM.V_HURRICAN_TAL_ORDER FOR V_HURRICAN_TAL_ORDER;

GRANT SELECT ON V_HURRICAN_TAL_ORDER TO R_HURRICAN_BSI_VIEWS;
