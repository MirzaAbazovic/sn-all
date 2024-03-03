----------------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------------
--
-- MV_TKUNDENDATEN_TAIFUN  (View)
--
----------------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------------

CREATE OR REPLACE FORCE VIEW MV_TKUNDENDATEN_TAIFUN
(
   TKU_SUCHNAME,
   "auftrag_rid",
   "auftragstatus_rid",
   AUFTRAG_NO,
   AUFTRAG__NO,
   GUELTIG_VON,
   TKU_KUENDIGUNGZUM,
   A_HIST_STATUS,
   A_HIST_CNT,
   A_HIST_LAST,
   A_ATYP,
   TKU_STATUS,
   TKU_STATUS_1,
   A_ASTATUS_DATUM,
   OE__NO,
   A_KUNDE__NO,
   A_BEARBEITER_NO,
   A_EINGANGS_ZEIT,
   TKU_RT_WUNSCHTERMIN,
   A_WUNSCH_ZEIT,
   A_AUSFUEHR_ZEIT,
   A_BEMERKUNGEN,
   A_R_INFO__NO,
   A_VERTRAGSENDE,
   TKU_AUFTRAGSNUMMER,
   AP_ADDRESS_NO,
   A_USERW,
   A_DATEW,
   BEARBEITER_KUNDE,
   BEARBEITER_KUNDE_FAX,
   BEARBEITER_KUNDE_RN,
   BEARBEITER_KUNDE_EMAIL,
   TATSAECHLICHER_TERMIN,
   AUFTRAG_SCHRIFTLICH,
   VERTRAGSDATUM,
   CONTRACT_ADDRESS_NO,
   EINGANGSDATUM,
   TKU_UNTERSCHRIFTSDATUM,
   TKU_AUF_ID,
   A_LZT,
   "kunde_rid",
   K_KUNDE_NO,
   K_KUNDE__NO,
   RESELLER_KUNDE_NO,
   K_GUELTIG_VON,
   K_GUELTIG_BIS,
   K_HIST_STATUS,
   K_NACHNAME,
   K_VORNAME,
   TKU_KUNDENART,
   TKU_KUNDE,
   TKU_K_FIRMA,
   PASSWORD,
   HAUPTRUFNUMMER,
   RN_GESCHAEFT,
   RN_FAX,
   RN_MOBILE,
   TKU_K_EMAIL,
   HOMEPAGE,
   KUNDENTYP,
   BONITAET_ID,
   BONITAET_ERFASSER_NO,
   BONITAET_ERFASSDATUM,
   VERTRAG_KUENDIGUNG,
   EXT_CUSTOMER_ID,
   K_USERW,
   K_DATEW,
   TKU_K_TELEFON,
   TKU_P_GEBURTSDATUM,
   TKU_B_BRANCHE,
   KUNDENBETREUER_NO,
   "adresse_rid",
   TKU_K_ANREDE,
   K_TITLE,
   TKU_K_TITLE,
   TKU_K_NACHNAME,
   TKU_K_VORNAME,
   K_TITEL2,
   TKU_K_NACHNAME2,
   TKU_K_VORNAME2,
   TKU_K_STRASSE,
   TKU_K_STRASSE2,
   TKU_K_HAUSNUMMER,
   TKU_K_PLZ,
   TKU_K_ORT,
   TKU_K_LAENDERCODE,
   "adresse1_rid",
   TKU_P_ANREDE,
   TKU_P_TITLE,
   TKU_P_TITLE2,
   TKU_P_ANREDE2,
   TKU_P_VORNAME,
   TKU_P_VORNAME2,
   TKU_P_FAMILIENNAME,
   TKU_P_FAMILIENNAME2,
   TKU_F_FIRMENNAME,
   TKU_F_ANREDE,
   TKU_F_VORNAME,
   TKU_F_FAMILIENNAME,
   TKU_F_REGISTER,
   TKU_F_REGISTERORT,
   TKU_A_STRASSE,
   TKU_A_STRASSE2,
   TKU_A_HAUSNUMMER,
   TKU_A_PLZ,
   TKU_A_ORT,
   TKU_A_LAENDERCODE,
   "r_info_rid",
   "finanz_rid",
   "adresse2_rid",
   R_INFO_NO,
   R_INFO__NO,
   FINANZ_NO,
   TKU_R_DEBITORENNUMMER,
   TKU_R_ANREDE,
   TKU_R_ANREDE2,
   TKU_R_TITLE,
   TKU_R_TITLE2,
   TKU_R_VORNAME,
   TKU_R_VORNAME2,
   TKU_R_NACHNAME,
   TKU_R_NACHNAME2,
   TKU_R_FIRMA,
   TKU_R_UMSATZSTEUER,
   TKU_R_TELEFON,
   TKU_B_ABTEILUNG,
   TKU_R_STRASSE,
   TKU_R_STRASSE2,
   TKU_R_HAUSNUMMER,
   TKU_R_PLZ,
   TKU_R_ORT,
   TKU_R_LAENDERCODE,
   TKU_ZAHLUNGSART,
   TKU_EE_ANREDE,
   TKU_EE_VORNAME,
   TKU_EE_FAMILIENNAME,
   TKU_EE_KONTONUMMER,
   TKU_EE_BLZ,
   TKU_EE_BANK
)
AS
   SELECT                                                    /*+ leading(a) */
         SUBSTR (
             LOWER (
                DECODE (TRIM (B.CUSTOMER_TYPE), 'GEWERBLICH', B.NAME, NULL) ||
                B.NAME ||
                B.FIRSTNAME
             ),
             1,
             150
          )
             AS TKU_Suchname,
          A.ROWID "auftrag_rid",
          I.ROWID "auftragstatus_rid",
          A.AUFTRAG_NO,
          A.AUFTRAG__NO,
          A.VALID_FROM AS GUELTIG_VON,
          CASE TO_CHAR (A.VALID_TO,
                        'dd.mm.yyyy')
             WHEN '31.12.9999'
             THEN
                CAST (NULL AS VARCHAR2 (10))
             ELSE
                TO_CHAR (A.VALID_TO,
                         'dd.mm.yyyy')
          END
             AS TKU_KuendigungZum,
          A.HIST_STATUS AS A_HIST_STATUS,
          A.HIST_CNT AS A_HIST_CNT,
          A.HIST_LAST AS A_HIST_LAST,
          A.ATYP AS A_ATYP,
          A.ASTATUS AS TKU_Status,
          I.NAME AS TKU_Status_1,
          A.ASTATUS_DATUM AS A_ASTATUS_DATUM,
          A.OE__NO,
          a.CUST_NO AS A_KUNDE__NO,
          A.BEARBEITER_NO AS A_BEARBEITER_NO,
          A.EINGANGS_ZEIT AS A_EINGANGS_ZEIT,
          A.WUNSCH_TERMIN AS TKU_RT_Wunschtermin,
          A.WUNSCH_ZEIT AS A_WUNSCH_ZEIT,
          A.AUSFUEHR_ZEIT AS A_AUSFUEHR_ZEIT,
          A.BEMERKUNGEN AS A_BEMERKUNGEN,
          A.BILL_SPEC_NO AS A_R_INFO__NO,
          A.VERTRAGSENDE AS A_VERTRAGSENDE,
          A.SAP_ID AS TKU_AUFTRAGSNUMMER,
          A.AP_ADDRESS_NO,
          A.USERW AS A_USERW,
          A.DATEW AS A_DATEW,
          A.BEARBEITER_KUNDE,
          A.BEARBEITER_KUNDE_FAX,
          A.BEARBEITER_KUNDE_RN,
          A.BEARBEITER_KUNDE_EMAIL,
          A.TATSAECHLICHER_TERMIN,
          A.AUFTRAG_SCHRIFTLICH,
          A.VERTRAGSDATUM,
          A.CONTRACT_ADDRESS_NO,
          A.EINGANGSDATUM,
          A.VERTRAGSDATUM AS TKU_Unterschriftsdatum,
          A.EXT_AUFTRAG_ID AS TKU_AUF_ID,
          A.AUFTRAGSBINDUNG AS A_LZT,
          ------------------------------------------------
          ------------      Kunde         ----------------
          ------------------------------------------------
          B.ROWID "kunde_rid",
          B.CUST_NO AS K_KUNDE_NO,
          B.CUST_NO AS K_KUNDE__NO,
          B.RESELLER_CUST_NO AS RESELLER_KUNDE_NO,
          B.VALID_FROM AS K_GUELTIG_VON,
          CAST (NULL AS DATE) AS K_GUELTIG_BIS,
          CASE B.IS_ACTIVE
             WHEN '1' THEN 'AKT'
             WHEN '0' THEN 'ALT'
          END
             AS K_HIST_STATUS,
          B.NAME AS K_NACHNAME,
          B.FIRSTNAME AS K_VORNAME,
          TRIM (B.CUSTOMER_TYPE) AS TKU_Kundenart,
          DECODE (
             TRIM (B.CUSTOMER_TYPE),
             'GEWERBLICH',
             DECODE (TRIM (B.CUSTOMER_TYPE),
                     'GEWERBLICH', C.NAME,
                     CAST (NULL AS VARCHAR2 (50))),
             CONCAT (
                CONCAT (
                   DECODE (TRIM (B.CUSTOMER_TYPE),
                           'GEWERBLICH', CAST (NULL AS VARCHAR2 (50)),
                           C.FIRSTNAME),
                   ' '
                ),
                DECODE (TRIM (B.CUSTOMER_TYPE),
                        'GEWERBLICH', CAST (NULL AS VARCHAR2 (50)),
                        C.NAME)
             )
          )
             AS TKU_KUNDE,
          DECODE (TRIM (B.CUSTOMER_TYPE),
                  'GEWERBLICH', B.NAME,
                  CAST (NULL AS VARCHAR2 (50)))
             AS TKU_K_FIRMA,
          B.PASSWORD,
          B.PHONE_CENTRAL AS HAUPTRUFNUMMER,
          B.PHONE_BUSINESS AS RN_GESCHAEFT,
          B.FAX AS RN_FAX,
          B.MOBILE AS RN_MOBILE,
          B.EMAIL AS TKU_K_EMAIL,
          B.WWW AS HOMEPAGE,
          B.CUSTOMER_TYPE AS KUNDENTYP,
          B.CREDIT_RATING_ID AS BONITAET_ID,
          B.CREDIT_RATING_USERW AS BONITAET_ERFASSER_NO,
          B.CREDIT_RATING_DATEW AS BONITAET_ERFASSDATUM,
          B.CONTRACT_CANCELED AS VERTRAG_KUENDIGUNG,
          B.EXT_CUSTOMER_ID,
          B.USERW AS K_USERW,
          B.DATEW AS K_DATEW,
          DECODE (
             B.PHONE_CENTRAL,
             NULL,
             DECODE (B.MOBILE, NULL, TRIM (B.PHONE_BUSINESS), TRIM (B.MOBILE)),
             TRIM (B.PHONE_CENTRAL)
          )
             AS TKU_K_TELEFON,
          B.BIRTHDAY AS TKU_P_GEBURTSDATUM,
          B.SECTOR_NO AS TKU_B_BRANCHE,
          B.CSR_NO AS KUNDENBETREUER_NO,
          ---------------------------------------------------
          ---------   VVertragsanschrift    -----------------
          ---------------------------------------------------
          C.ROWID "adresse_rid",
          CASE
             WHEN TRIM (B.CUSTOMER_TYPE) = 'GEWERBLICH'
             THEN
                CAST (NULL AS VARCHAR2 (40))
             ELSE
                CASE C.SALUTATION
                   WHEN 'HERR' THEN 'Herr' --|| DECODE(C.TITLE,NULL,'', ' ' || C.TITLE)
                   WHEN 'FRAU' THEN 'Frau' --|| DECODE(C.TITLE,NULL,'', ' ' || C.TITLE)
                   WHEN 'FRAUHERR' THEN 'Frau und Herr'
                   WHEN 'HERRFRAU' THEN 'Herr und Frau'
                   ELSE NULL
                END
          END
             AS TKU_K_ANREDE,
          C.TITLE AS K_TITLE,
          C.TITLE AS TKU_K_TITLE,
          DECODE (TRIM (B.CUSTOMER_TYPE),
                  'GEWERBLICH', CAST (NULL AS VARCHAR2 (50)),
                  C.NAME)
             AS TKU_K_NACHNAME,
          C.FIRSTNAME AS TKU_K_VORNAME,
          C.TITEL2 AS K_TITEL2,
          C.NAME2 AS TKU_K_NACHNAME2,
          C.VORNAME2 AS TKU_K_VORNAME2,
          C.STREET AS TKU_K_STRASSE,
          C.STREET_ADD AS TKU_K_STRASSE2,
          DECODE (C.HOUSE_NUM_ADD,
                  NULL, TRIM (C.HOUSE_NUM),
                  TRIM (C.HOUSE_NUM) ||
                  ' ' ||
                  TRIM (C.HOUSE_NUM_ADD))
             AS TKU_K_HAUSNUMMER,
          C.ZIP_CODE AS TKU_K_PLZ,
          C.CITY AS TKU_K_ORT,
          C.GEO_COUNTRY_ID AS TKU_K_LAENDERCODE,
          --------------------------------------------------
          --------------   Anschlussort  --------------------
          ---------------------------------------------------
          -- Privat
          D.ROWID "adresse1_rid",
          CASE
             WHEN TRIM (B.CUSTOMER_TYPE) = 'GEWERBLICH'
             THEN
                CAST (NULL AS VARCHAR2 (40))
             ELSE
                CASE D.SALUTATION
                   WHEN 'HERR' THEN 'Herr' --|| DECODE(D.TITLE,NULL,'', ' ' || D.TITLE)
                   WHEN 'FRAU' THEN 'Frau' --|| DECODE(D.TITLE,NULL,'', ' ' || D.TITLE)
                   WHEN 'FRAUHERR' THEN 'Frau und Herr'
                   WHEN 'HERRFRAU' THEN 'Herr und Frau'
                   ELSE NULL
                END
          END
             AS TKU_P_ANREDE,
          D.TITLE AS TKU_P_TITLE,
          D.TITEL2 AS TKU_P_TITLE2,
          CAST (NULL AS VARCHAR2 (40)) AS TKU_P_ANREDE2,
          DECODE (TRIM (B.CUSTOMER_TYPE),
                  'GEWERBLICH', CAST (NULL AS VARCHAR2 (50)),
                  D.FIRSTNAME)
             AS TKU_P_VORNAME,
          DECODE (TRIM (B.CUSTOMER_TYPE),
                  'GEWERBLICH', CAST (NULL AS VARCHAR2 (50)),
                  D.VORNAME2)
             AS TKU_P_VORNAME2,
          DECODE (TRIM (B.CUSTOMER_TYPE),
                  'GEWERBLICH', CAST (NULL AS VARCHAR2 (50)),
                  D.NAME)
             AS TKU_P_FAMILIENNAME,
          DECODE (TRIM (B.CUSTOMER_TYPE),
                  'GEWERBLICH', CAST (NULL AS VARCHAR2 (50)),
                  D.NAME2)
             AS TKU_P_FAMILIENNAME2,
          -- Firma
          DECODE (TRIM (B.CUSTOMER_TYPE),
                  'GEWERBLICH', D.NAME,
                  CAST (NULL AS VARCHAR2 (50)))
             AS TKU_F_Firmenname,
          CAST (NULL AS VARCHAR2 (40)) AS TKU_F_Anrede,
          CAST (NULL AS VARCHAR2 (50)) AS TKU_F_Vorname,
          CAST (NULL AS VARCHAR2 (50)) AS TKU_F_Familienname,
          CAST (NULL AS VARCHAR2 (30)) AS TKU_F_Register,
          CAST (NULL AS VARCHAR2 (30)) AS TKU_F_Registerort,
          --- Kundenanschlußort
          D.STREET AS TKU_A_STRASSE,
          D.STREET_ADD AS TKU_A_STRASSE2,
          DECODE (D.HOUSE_NUM_ADD,
                  NULL, TRIM (D.HOUSE_NUM),
                  TRIM (D.HOUSE_NUM) ||
                  ' ' ||
                  TRIM (D.HOUSE_NUM_ADD))
             AS TKU_A_HAUSNUMMER,
          D.ZIP_CODE AS TKU_A_PLZ,
          D.CITY AS TKU_A_ORT,
          D.GEO_COUNTRY_ID AS TKU_A_LAENDERCODE,
          ---------------------------------------------------
          --- Rechnungsinfo
          ----------------------------------------------------
          e.ROWID "r_info_rid",
          f.ROWID "finanz_rid",
          g.ROWID "adresse2_rid",
          e.BILL_SPEC_NO AS R_INFO_NO,
          e.BILL_SPEC_NO AS R_INFO__NO,
          f.CUST_NO AS FINANZ_NO,
          e.EXT_DEBITOR_ID AS TKU_R_Debitorennummer,
          -- Privat
          CASE
             WHEN TRIM (B.CUSTOMER_TYPE) = 'GEWERBLICH'
             THEN
                CAST (NULL AS VARCHAR2 (40))
             ELSE
                CASE G.SALUTATION
                   WHEN 'HERR' THEN 'Herr' --|| DECODE(G.TITLE,NULL,'', ' ' || G.TITLE)
                   WHEN 'FRAU' THEN 'Frau' --|| DECODE(G.TITLE,NULL,'', ' ' || G.TITLE)
                   WHEN 'FRAUHERR' THEN 'Frau und Herr'
                   WHEN 'HERRFRAU' THEN 'Herr und Frau'
                   ELSE NULL
                END
          END
             AS TKU_R_ANREDE,
          CAST (NULL AS VARCHAR2 (40)) AS TKU_R_ANREDE2,
          G.TITLE AS TKU_R_TITLE,
          G.TITEL2 AS TKU_R_TITLE2,
          DECODE (TRIM (B.CUSTOMER_TYPE),
                  'GEWERBLICH', CAST (NULL AS VARCHAR2 (50)),
                  g.FIRSTNAME)
             AS TKU_R_VORNAME,
          DECODE (TRIM (B.CUSTOMER_TYPE),
                  'GEWERBLICH', CAST (NULL AS VARCHAR2 (50)),
                  g.VORNAME2)
             AS TKU_R_VORNAME2,
          DECODE (TRIM (B.CUSTOMER_TYPE),
                  'GEWERBLICH', CAST (NULL AS VARCHAR2 (50)),
                  g.NAME)
             AS TKU_R_NACHNAME,
          DECODE (TRIM (B.CUSTOMER_TYPE),
                  'GEWERBLICH', CAST (NULL AS VARCHAR2 (50)),
                  g.NAME2)
             AS TKU_R_NACHNAME2,
          -- Firma
          DECODE (TRIM (B.CUSTOMER_TYPE), 'GEWERBLICH', g.NAME, ' ')
             AS TKU_R_Firma,
          CAST (NULL AS VARCHAR2 (20)) AS TKU_R_Umsatzsteuer,
          CAST (NULL AS VARCHAR2 (20)) AS TKU_R_Telefon,
          CAST (NULL AS VARCHAR2 (100)) AS TKU_B_Abteilung,
          ---
          g.STREET AS TKU_R_STRASSE,
          g.STREET_ADD AS TKU_R_STRASSE2,
          DECODE (g.HOUSE_NUM_ADD,
                  NULL, TRIM (g.HOUSE_NUM),
                  TRIM (g.HOUSE_NUM) ||
                  ' ' ||
                  TRIM (g.HOUSE_NUM_ADD))
             AS TKU_R_HAUSNUMMER,
          g.ZIP_CODE AS TKU_R_PLZ,
          g.CITY AS TKU_R_ORT,
          g.GEO_COUNTRY_ID AS TKU_R_LAENDERCODE,
          --- Finanzinfo
          f.PAYMENT_METHOD AS TKU_Zahlungsart,
          CAST (NULL AS VARCHAR2 (20)) AS TKU_EE_Anrede,
          CAST (NULL AS VARCHAR2 (20)) AS TKU_EE_Vorname,
          f.ACCOUNT_HOLDER AS TKU_EE_Familienname,
          f.ACCOUNT_NUMBER AS TKU_EE_Kontonummer,
          f.BIC_NO AS TKU_EE_BLZ,
          --h.rowid "blz_rid",
          CAST (NULL AS VARCHAR2 (80)) AS TKU_EE_BANK
   FROM AUFTRAG a,
        CUSTOMER b                                                --,ADRESSE c
                  ,
        ADDRESS c                                                 --,ADRESSE d
                 ,
        ADDRESS d                                                  --,R_INFO e
                 ,
        BILL_SPEC e                                                --,FINANZ f
                   ,
        CUST_ACCOUNT f                                            --,ADRESSE g
                      ,
        ADDRESS g                                                     --,BLZ h
                 ,
        AUFTRAGSTATUS i
   WHERE                                                         -- AUFTRAG --
         (a.HIST_LAST = 1 OR
          (TRIM (a.HIST_LAST) = 0 AND
           A.HIST_STATUS IN ('UNG')))                             --  KUNDE --
                                     AND
         b.CUST_NO = a.CUST_NO                          --  VERTRAG ADRESSE --
                              AND
         c.ADDR_NO = a.CONTRACT_ADDRESS_NO                 --  ANSCHLUSSORT --
                                          AND
         d.ADDR_NO = a.AP_ADDRESS_NO                                 -- R_INFO
                                    AND
         e.BILL_SPEC_NO = a.BILL_SPEC_NO     --AND e.CUST_NO       = a.CUST_NO
                                                                    --  FINANZ
          AND
         f.CUST_NO = e.CUST_NO               --AND f.CUST_NO       = a.CUST_NO
                              AND
         f.CUST_ACCT_NO = e.DEBIT_ACCOUNT_NO             --  RECHNUNGS ADRESSE
                                            AND
         g.ADDR_NO = e.INV_ADDR_NO                                     --  BLZ
                                              --AND h.BLZ_NO(+)     = f.BIC_NO
                                                             --  AUFTRAGSTATUS
          AND
         a.ASTATUS = i.ASTATUS
--
--AND a.SAP_ID        = '991568';
/
BEGIN
execute immediate('CREATE PUBLIC SYNONYM MV_TKUNDENDATEN_TAIFUN FOR ' || SYS_CONTEXT('USERENV','CURRENT_SCHEMA') || '.MV_TKUNDENDATEN_TAIFUN');
EXCEPTION
   WHEN NO_DATA_FOUND THEN
      NULL;
   WHEN OTHERS THEN
      NULL;
END;
/
GRANT INSERT, SELECT, UPDATE ON MV_TKUNDENDATEN_TAIFUN TO R_TAIFUN_KUP;
GRANT INSERT, SELECT, UPDATE ON MV_TKUNDENDATEN_TAIFUN TO TAIFUN_KUP;
GRANT INSERT, SELECT, UPDATE ON MV_TKUNDENDATEN_TAIFUN TO TAIFUN_KUP WITH GRANT OPTION
/
BEGIN
execute immediate('drop INDEX I1FCN_CUSTOMER');
EXCEPTION
   WHEN NO_DATA_FOUND THEN
      NULL;
   WHEN OTHERS THEN
      NULL;
END;
/
--
-- I1FCN_CUSTOMER  (Index)
--
CREATE INDEX I1FCN_CUSTOMER ON CUSTOMER
(SUBSTR(LOWER(DECODE(TRIM("CUSTOMER_TYPE"),'GEWERBLICH',"NAME",NULL)||"NAME"||"FIRSTNAME"),1,150))
NOLOGGING
TABLESPACE TAIFUN_INDEX
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          64K
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL
/
CREATE OR REPLACE FUNCTION f_str_suchname (varSTR_NAME VARCHAR2)
   RETURN VARCHAR2 DETERMINISTIC IS  cret VARCHAR2(50);
BEGIN
cret :=
translate(
replace(
replace(
replace(
replace(
replace(
replace(
replace(
replace(
replace(
replace(
replace(
replace(
upper(
varSTR_NAME
),'DOKTOR','DR'
),'ST.','SANKT'
),'PROFESSOR','PROF'
),'STRASSE','STR'
),'STRAßE','STR'
),'STRA¿E','STR'
),' STR','STR'
),'ß','SS'
),'¿','Ü'
),'Ü','UE'
),'Ö','OE'
),'Ä','AE'
),
'0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. ,',
'0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ'
);
   RETURN cret;
END f_str_suchname;
/
CREATE OR REPLACE FUNCTION f_tonumber (in_value VARCHAR2)
   RETURN NUMBER IS  iret NUMBER;
BEGIN
   BEGIN
      iret := TO_NUMBER (in_value);
   EXCEPTION
      WHEN INVALID_NUMBER
      THEN
         iret := NULL;
      WHEN OTHERS
      THEN
         iret := NULL;
   END;
   return iret;
END f_tonumber;
/

------------------------------------------------------------------------------
------------------------------------------------------------------------------
-- drop invalid synonyms
------------------------------------------------------------------------------

BEGIN
  sys.utl_recomp.recomp_serial();
  COMMIT;
END;
/
BEGIN
   FOR one
   IN (  SELECT   object_name
           FROM   dba_objects
          WHERE       status = 'INVALID'
                  AND object_type = 'SYNONYM'
                  AND owner = 'PUBLIC'
       ORDER BY   object_name)
   LOOP
      BEGIN
         EXECUTE IMMEDIATE 'DROP PUBLIC SYNONYM ' || one.object_name;

         DBMS_OUTPUT.put_line ('DROP PUBLIC SYNONYM ' || one.object_name);
      EXCEPTION
         WHEN OTHERS
         THEN
            NULL;                                      -- ignore, and proceed.
      END;
   END LOOP;
END;
/
BEGIN
  sys.utl_recomp.recomp_serial();
  COMMIT;
END;
/

------------------------------------------------------------------------------
BEGIN
   FOR one IN (SELECT object_type,
                      object_name
                 FROM user_objects
                WHERE object_type IN ('TABLE'))
   LOOP
      BEGIN
         EXECUTE IMMEDIATE    'CREATE PUBLIC SYNONYM '
                           || one.object_name
                           || ' FOR '
                           || SYS_CONTEXT('USERENV','CURRENT_SCHEMA')
                           || '.'
                           || one.object_name;
--         DBMS_OUTPUT.put_line (   'CREATE PUBLIC SYNONYM '
--                           || one.object_name
--                           || ' FOR '
--                           || SYS_CONTEXT('USERENV','CURRENT_SCHEMA')
--                           || '.'
--                           || one.object_name);
      EXCEPTION
         WHEN OTHERS THEN
            NULL;                                     -- ignore, and proceed.
      END;
   END LOOP;
END;
/
BEGIN
   FOR one IN (SELECT object_type,
                      object_name
                 FROM user_objects
                WHERE object_type IN ('SEQUENCE'))
   LOOP
      BEGIN
         EXECUTE IMMEDIATE    'CREATE PUBLIC SYNONYM '
                           || one.object_name
                           || ' FOR '
                           || SYS_CONTEXT('USERENV','CURRENT_SCHEMA')
                           || '.'
                           || one.object_name;
--         DBMS_OUTPUT.put_line (   'CREATE PUBLIC SYNONYM '
--                           || one.object_name
--                           || ' FOR '
--                           || SYS_CONTEXT('USERENV','CURRENT_SCHEMA')
--                           || '.'
--                           || one.object_name);
      EXCEPTION
         WHEN OTHERS THEN
            NULL;                                     -- ignore, and proceed.
      END;
   END LOOP;
END;
/

----------------------------------------------------------------------------
-- grant select auf alle synonyms
----------------------------------------------------------------------------

BEGIN
   FOR one IN (SELECT object_type,
                      object_name
                 FROM user_objects
                WHERE object_type IN ('TABLE'))
   LOOP
      BEGIN
         EXECUTE IMMEDIATE    'GRANT SELECT ON '
                           || one.object_name
                           || '  TO R_TAIFUN_KUP';

         DBMS_OUTPUT.put_line (   'GRANT SELECT ON '
                           || one.object_name
                           || '  TO R_TAIFUN_KUP' );
      EXCEPTION
         WHEN OTHERS THEN
            NULL;                                     -- ignore, and proceed.
      END;
   END LOOP;
END;
/

----------------------------------------------------------------------------

BEGIN
  sys.utl_recomp.recomp_serial();
  COMMIT;
END;
/

----------------------------------------------------------------------------

BEGIN
   FOR one
   IN ( SELECT  a.object_name
           FROM   dba_objects a
          WHERE
          (
          object_name like 'AUFTRAGPOS' OR
          object_name like 'SERVICE_VALUE_PRICE' OR
          object_name like 'LEISTUNG' OR
          object_name like 'LEISTUNG_LANG' OR
          object_name like 'PERSON' OR
          object_name like 'PERSON' OR
          object_name like 'DN' OR
          object_name like 'PURCHASE_ORDER' OR
          object_name like 'BILL_SPEC' OR
          object_name like 'CUSTOMER'  OR
          object_name like '%ADDRESS%' OR
          object_name like '%ADDRESS%' OR
          object_name like '%AUFTRAG%'
          ) and
          instr(object_name,'HIST') = 0 and
          instr(object_name,'/') = 0 and
          instr(object_name,'$') = 0 and
          status = 'VALID' and
          object_type = 'SYNONYM' and
          owner = 'PUBLIC'
       ORDER BY   a.object_name)
   LOOP
      BEGIN
         EXECUTE IMMEDIATE 'GRANT ALL ON ' || one.object_name  || ' TO TAIFUN_KUP WITH GRANT OPTION';
      EXCEPTION
         WHEN OTHERS
         THEN
            NULL;
      END;
   END LOOP;
END;
/
BEGIN
  sys.utl_recomp.recomp_serial();
  COMMIT;
END;
/

--------------------------------------------------------
GRANT SELECT ON A_CDR_DELETE TO TAIFUN_KUP;
GRANT INSERT, SELECT, UPDATE ON AUFTRAG TO R_TAIFUN_KUP;
GRANT INSERT, SELECT, UPDATE ON CUSTOMER TO R_TAIFUN_KUP;
GRANT INSERT, SELECT, UPDATE ON CUST_ACCOUNT TO R_TAIFUN_KUP;
GRANT INSERT, SELECT, UPDATE ON AUFTRAGPOS TO R_TAIFUN_KUP;
GRANT INSERT, SELECT, UPDATE ON AUFTRAG__KOMBI TO R_TAIFUN_KUP;
GRANT INSERT, SELECT, UPDATE ON AUFTRAG__CENTREX TO R_TAIFUN_KUP;
GRANT INSERT, SELECT, UPDATE ON AUFTRAG__CONNECT TO R_TAIFUN_KUP;
GRANT INSERT, SELECT, UPDATE ON BLZ TO R_TAIFUN_KUP;
GRANT INSERT, SELECT, UPDATE ON DN TO R_TAIFUN_KUP;
GRANT INSERT, SELECT, UPDATE ON DN_BLOCK TO R_TAIFUN_KUP;
GRANT INSERT, SELECT, UPDATE ON DN_ONKZ TO R_TAIFUN_KUP;
GRANT INSERT, SELECT, UPDATE ON DN_POS TO R_TAIFUN_KUP;
GRANT INSERT, SELECT, UPDATE ON DN_TNB TO R_TAIFUN_KUP;
GRANT INSERT, SELECT, UPDATE ON AUFTRAG__MOBIL TO R_TAIFUN_KUP;
GRANT INSERT, SELECT, UPDATE ON AUFTRAG__IAC TO R_TAIFUN_KUP;
GRANT INSERT, SELECT, UPDATE ON AUFTRAG__IHO TO R_TAIFUN_KUP;
GRANT INSERT, SELECT, UPDATE ON AUFTRAG__BN_FC TO R_TAIFUN_KUP;
GRANT INSERT, SELECT, UPDATE ON OE TO R_TAIFUN_KUP;
GRANT INSERT, SELECT, UPDATE ON OE_LANG TO R_TAIFUN_KUP;
GRANT INSERT, SELECT, UPDATE ON LEISTUNG TO R_TAIFUN_KUP;
GRANT INSERT, SELECT, UPDATE ON LEISTUNG_DEPENDENCY TO R_TAIFUN_KUP;
GRANT INSERT, SELECT, UPDATE ON LEISTUNG_LANG TO R_TAIFUN_KUP;
GRANT INSERT, SELECT, UPDATE ON ACCOUNT TO R_TAIFUN_KUP;
GRANT INSERT, SELECT, UPDATE ON PERSON TO R_TAIFUN_KUP;
GRANT INSERT, SELECT, UPDATE ON WFE_TASK TO R_TAIFUN_KUP;
GRANT INSERT, SELECT, UPDATE ON BILL_SPEC TO R_TAIFUN_KUP;
GRANT INSERT, SELECT, UPDATE ON HIST_ADDRESS TO R_TAIFUN_KUP;
GRANT INSERT, SELECT, UPDATE ON HIST_BILL_SPEC TO R_TAIFUN_KUP;
GRANT INSERT, SELECT, UPDATE ON HIST_CUST_ACCOUNT  TO R_TAIFUN_KUP;
GRANT INSERT, SELECT, UPDATE ON HIST_CUSTOMER TO R_TAIFUN_KUP;
GRANT INSERT, SELECT, UPDATE ON CHANGE_BILL_SPEC TO R_TAIFUN_KUP;
GRANT INSERT, SELECT, UPDATE ON CHANGE_CUST_ACCOUNT  TO R_TAIFUN_KUP;
GRANT INSERT, SELECT, UPDATE ON CHANGE_CUSTOMER TO R_TAIFUN_KUP;
GRANT INSERT, SELECT, UPDATE ON AUFTRAG_KOMBI TO R_TAIFUN_KUP;
GRANT INSERT, SELECT, UPDATE ON OE TO R_TAIFUN_KUP;
GRANT UPDATE(CREDIT_RATING_ID) on customer TO R_TAIFUN_KUP;
GRANT UPDATE(CREDIT_RATING_USERW) on customer TO R_TAIFUN_KUP;
GRANT UPDATE(CREDIT_RATING_DATEW) on customer TO R_TAIFUN_KUP
/

------------------------------------------------------------------------
-- views vor trigger
------------------------------------------------------------------------

select object_type, object_name from user_objects
  where object_type in ('FUNCTION','PACKAGE','VIEW','TRIGGER','PROCEDURE')
  and status = 'INVALID'
/
begin
  for one in (select object_type, object_name from user_objects
  where object_type in ('VIEW')
  and status = 'INVALID') loop
  -- dbms_output.put_line('Updating '||one.object_type||' '||one.object_name);
  begin
    execute immediate 'alter '||one.object_type||' '||one.object_name||' compile';
    exception
    when others then
    null; -- ignore, and proceed.
  end;
end loop;
end;
/
commit
/
begin
  for one in (select object_type, object_name from user_objects
  where object_type in ('FUNCTION','PACKAGE','VIEW','TRIGGER','PROCEDURE')
  and status = 'INVALID') loop
  -- dbms_output.put_line('Updating '||one.object_type||' '||one.object_name);
  begin
    execute immediate 'alter '||one.object_type||' '||one.object_name||' compile';
    exception
    when others then
    null; -- ignore, and proceed.
  end;
end loop;
end;
/
commit
/
select object_type, object_name from user_objects
  where object_type in ('FUNCTION','PACKAGE','VIEW','TRIGGER','PROCEDURE')
  and status = 'INVALID'
/
