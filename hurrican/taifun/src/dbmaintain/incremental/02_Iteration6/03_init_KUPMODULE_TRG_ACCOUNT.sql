CREATE OR REPLACE FORCE VIEW V_TAIFUN_AUFTRAG4TRAFFIC_MLB
AS
SELECT
    A.SAP_ID,
    A.AUFTRAG__NO,
    A.OE__NO,
    TRIM (d.ACCOUNT_ID) AS ACCOUNT_ID,
    c.name,
    CASE
        WHEN C.BILLING_CODE = 'UDR.MVMSDSL'    THEN 'MVMSDSL'
        WHEN C.BILLING_CODE = 'UDR.MVMBAND'    THEN 'MVMBAND'
        WHEN C.BILLING_CODE = 'UDR.MVMBACKUP'  THEN 'MVMBACKUP'
        WHEN C.BILLING_CODE = 'UDR.MVMVSERVER' THEN 'MVMVSERVER'
        WHEN C.BILLING_CODE = 'UDR.MVMDYNWEB'  THEN 'MVMDYNWEB'
    END
    SS_SERVICE_ID,
    -----------------------------------------------
    CASE WHEN UPPER (c.NAME) LIKE '%FLAT%' THEN 1 ELSE 0 END FLAT,
    -----------------------------------------------
    b.CHARGE_FROM,
    NVL (b.CHARGE_TO, TO_DATE ('31.12.9999', 'dd.mm.yyyy')) AS CHARGE_TO
FROM
    auftrag a,
    auftragpos b,
    leistung c,
    account d
WHERE
    b.order__no = a.auftrag__no
    AND c.leistung__no = b.SERVICE_ELEM__NO
    AND c.hist_status IN ('AKT', 'NEU', 'ALT')
    AND NVL (C.BILLING_CODE, 'x') IN ('UDR.MVMSDSL',
                                      'UDR.MVMBAND',
                                      'UDR.MVMBACKUP',
                                      'UDR.MVMVSERVER',
                                      'UDR.MVMDYNWEB')
    AND d.AUFTRAG_NO(+) = A.AUFTRAG_NO
    AND TRIM (d.ACCOUNT_ID) = A.SAP_ID || CASE
                                              WHEN C.BILLING_CODE = 'UDR.MVMSDSL'     THEN '_VOL'
                                              WHEN C.BILLING_CODE = 'UDR.MVMVSERVER'  THEN '_VSERVER'
                                              WHEN C.BILLING_CODE = 'UDR.MVMDYNWEB'   THEN '_DYNWEB'
                                              WHEN C.BILLING_CODE = 'UDR.MVMBAND'     THEN '_BAND'
                                              WHEN C.BILLING_CODE = 'UDR.MVMBACKUP'   THEN '_BACKUP'
                                          END
    AND b.CHARGE_FROM < SYSDATE
    AND NVL (b.CHARGE_TO, TO_DATE ('31.12.9999', 'dd.mm.yyyy')) > ADD_MONTHS (SYSDATE, -1)
    ---------------- 08.10.2009 VB
    AND NVL (A.VALID_TO, TO_DATE ('31.12.9999', 'dd.mm.yyyy')) > ADD_MONTHS (SYSDATE, -1) AND A.HIST_STATUS <> 'UNG'
    AND NVL (a.FAKTURIERUNG_STOPP_BIS, ADD_MONTHS (SYSDATE, -1)) <= ADD_MONTHS (SYSDATE, -1)
    ----------------
    AND a.OE__NO IN (SELECT x.OE__NO FROM OE x WHERE x.NAME IN   ('BIS',
                                                                  'MIN',
                                                                  'MPR',
                                                                  'PLUS',
                                                                  'SEK',
                                                                  'Maxi S ALT',
                                                                  'Maxi M ALT',
                                                                  'Connect',
                                                                  'Maxi',
                                                                  'Maxi Pur',
                                                                  'Maxi Komplett',
                                                                  'Direct Access',
                                                                  'Premium',
                                                                  'Housing',
                                                                  'M-net SDSL')
                                                           AND x.PRIMARY_INSTANCE = 1
                                                           AND TRIM (x.OETYP) = 'Produkt'
                                                           AND x.BILLING_GROUP_NO = 2)
----------------
--AND a.sap_id = 'sct534.001.O'
--and sap_id = 'sie029.104.O'
GROUP BY  A.SAP_ID,
          A.AUFTRAG__NO,
          A.OE__NO,
          d.ACCOUNT_ID,
          c.NAME,
          CASE
             WHEN C.BILLING_CODE = 'UDR.MVMSDSL'    THEN 'MVMSDSL'
             WHEN C.BILLING_CODE = 'UDR.MVMBAND'    THEN 'MVMBAND'
             WHEN C.BILLING_CODE = 'UDR.MVMBACKUP'  THEN 'MVMBACKUP'
             WHEN C.BILLING_CODE = 'UDR.MVMVSERVER' THEN 'MVMVSERVER'
             WHEN C.BILLING_CODE = 'UDR.MVMDYNWEB'  THEN 'MVMDYNWEB'
          END,
          -----------------------------------------------
          b.CHARGE_FROM,
          b.CHARGE_TO
ORDER BY   A.SAP_ID, SS_SERVICE_ID;

/
GRANT SELECT ON V_TAIFUN_AUFTRAG4TRAFFIC_MLB TO R_TAIFUN_KUP;
/
GRANT DELETE, INSERT, REFERENCES, SELECT, UPDATE, ON COMMIT REFRESH, QUERY REWRITE, DEBUG, FLASHBACK ON V_TAIFUN_AUFTRAG4TRAFFIC_MLB TO TAIFUN_KUP WITH GRANT OPTION;
/
GRANT SELECT ON V_TAIFUN_AUFTRAG4TRAFFIC_MLB TO TAIFUN_KUP_AUSKUNFT WITH GRANT OPTION;
/


CREATE OR REPLACE FORCE VIEW V_TAIFUN_AUFTRAG4TRAFFIC_RTB
AS
SELECT
    A.SAP_ID,
    A.AUFTRAG__NO,
    A.OE__NO,
    TRIM (d.ACCOUNT_ID) AS ACCOUNT_ID,
    c.name,
    CASE
        WHEN C.BILLING_CODE = 'UDR.MVMSDSL'     THEN 'MVMSDSL'
        WHEN C.BILLING_CODE = 'UDR.MVMBAND'     THEN 'MVMBAND'
        WHEN C.BILLING_CODE = 'UDR.MVMBACKUP'   THEN 'MVMBACKUP'
        WHEN C.BILLING_CODE = 'UDR.MVMVSERVER'  THEN 'MVMVSERVER'
        WHEN C.BILLING_CODE = 'UDR.MVMDYNWEB'   THEN 'MVMDYNWEB'
    END
    SS_SERVICE_ID,
    -----------------------------------------------
    CASE WHEN UPPER (c.NAME) LIKE '%FLAT%' THEN 1 ELSE 0 END FLAT,
    -----------------------------------------------
    b.CHARGE_FROM,
    NVL (b.CHARGE_TO, TO_DATE ('31.12.9999', 'dd.mm.yyyy'))
    AS CHARGE_TO
FROM
    auftrag a,
    auftragpos b,
    leistung c,
    account d
WHERE
    b.order__no = a.auftrag__no
    AND c.leistung__no = b.SERVICE_ELEM__NO
    AND c.hist_status IN ('AKT', 'NEU', 'ALT')
    AND NVL (C.BILLING_CODE, 'x') IN   ('UDR.MVMSDSL',
                                        'UDR.MVMBAND',
                                        'UDR.MVMBACKUP',
                                        'UDR.MVMVSERVER',
                                        'UDR.MVMDYNWEB')
    AND d.AUFTRAG_NO(+) = A.AUFTRAG_NO
    AND TRIM (d.ACCOUNT_ID) = A.SAP_ID || CASE
                                            WHEN C.BILLING_CODE = 'UDR.MVMSDSL'     THEN '_VOL'
                                            WHEN C.BILLING_CODE = 'UDR.MVMVSERVER'  THEN '_VSERVER'
                                            WHEN C.BILLING_CODE = 'UDR.MVMDYNWEB'   THEN '_DYNWEB'
                                            WHEN C.BILLING_CODE = 'UDR.MVMBAND'     THEN '_BAND'
                                            WHEN C.BILLING_CODE = 'UDR.MVMBACKUP'   THEN '_BACKUP'
                                          END
      AND b.CHARGE_FROM < SYSDATE
      AND NVL (b.CHARGE_TO, TO_DATE ('31.12.9999', 'dd.mm.yyyy')) > ADD_MONTHS (SYSDATE, -1)
      --------------- 08.10.2009 VB
      AND NVL (A.VALID_TO, TO_DATE ('31.12.9999', 'dd.mm.yyyy')) > ADD_MONTHS (SYSDATE, -1)
      AND A.HIST_STATUS <> 'UNG'
      AND NVL (a.FAKTURIERUNG_STOPP_BIS, ADD_MONTHS (SYSDATE, -1)) <= ADD_MONTHS (SYSDATE, -1)
      ----------------
      AND a.OE__NO IN (SELECT x.OE__NO FROM OE x WHERE x.NAME IN   ('BIS',
                                                                    'MIN',
                                                                    'MPR',
                                                                    'PLUS',
                                                                    'SEK',
                                                                    'Maxi S ALT',
                                                                    'Maxi M ALT',
                                                                    'Connect',
                                                                    'Maxi',
                                                                    'Maxi Pur',
                                                                    'Maxi Komplett',
                                                                    'Direct Access',
                                                                    'Premium',
                                                                    'Housing',
                                                                    'M-net SDSL')
                                                 AND x.PRIMARY_INSTANCE = 1
                                                 AND TRIM (x.OETYP) = 'Produkt'
                                                 AND x.BILLING_GROUP_NO = 3)
--and a.OE__NO IN (2006)
----------------
--AND a.sap_id = 'S20090609003'
GROUP BY
    A.SAP_ID,
    A.AUFTRAG__NO,
    A.OE__NO,
    d.ACCOUNT_ID,
    c.NAME,
    CASE
        WHEN C.BILLING_CODE = 'UDR.MVMSDSL'     THEN 'MVMSDSL'
        WHEN C.BILLING_CODE = 'UDR.MVMBAND'     THEN 'MVMBAND'
        WHEN C.BILLING_CODE = 'UDR.MVMBACKUP'   THEN 'MVMBACKUP'
        WHEN C.BILLING_CODE = 'UDR.MVMVSERVER'  THEN 'MVMVSERVER'
        WHEN C.BILLING_CODE = 'UDR.MVMDYNWEB'   THEN 'MVMDYNWEB'
    END,
    -----------------------------------------------
    C.BILLING_CODE,
    b.CHARGE_FROM,
    b.CHARGE_TO
ORDER BY A.SAP_ID, SS_SERVICE_ID;

/
GRANT SELECT ON V_TAIFUN_AUFTRAG4TRAFFIC_RTB TO R_TAIFUN_KUP;
/
GRANT DELETE, INSERT, REFERENCES, SELECT, UPDATE, ON COMMIT REFRESH, QUERY REWRITE, DEBUG, FLASHBACK ON V_TAIFUN_AUFTRAG4TRAFFIC_RTB TO TAIFUN_KUP WITH GRANT OPTION;
/
GRANT SELECT ON V_TAIFUN_AUFTRAG4TRAFFIC_RTB TO TAIFUN_KUP_AUSKUNFT WITH GRANT OPTION;
/

--
-- TRG_INS_AUFTRAGPOS  (Trigger)
--

CREATE OR REPLACE TRIGGER TRG_INS_AUFTRAGPOS
AFTER INSERT ON AUFTRAGPOS FOR EACH ROW
DECLARE

    iLEISTUNG__NO       LEISTUNG.LEISTUNG__NO%TYPE;
    sBILLING_CODE       LEISTUNG.BILLING_CODE%TYPE;
    iAUFTRAG_NO         AUFTRAG.AUFTRAG_NO%TYPE;
    cSAP_ID             AUFTRAG.SAP_ID%TYPE;
    iRESELLER_KUNDE_NO  CUSTOMER.RESELLER_CUST_NO%TYPE;
    iOE__NO             AUFTRAG.OE__NO%TYPE;

--0001 / 18.08.2009 / VB / neu angelegt
--0002 / 19.08.2009 / VB / nur für Münchener Auftträge
--0003 / 28.08.2009 / VB / wegen Performance

BEGIN
    IF INSERTING THEN
        SELECT a.AUFTRAG_NO, a.SAP_ID, a.OE__NO, b.RESELLER_CUST_NO
          INTO iAUFTRAG_NO,  cSAP_ID,   iOE__NO, iRESELLER_KUNDE_NO
          FROM AUFTRAG a,
               CUSTOMER b
         WHERE a.AUFTRAG__NO = :NEW.ORDER__NO
           AND a.CUST_NO = b.CUST_NO
           AND a.OE__NO IN (302,2157,2161,3005,2006,304,305,2000,2002,314,2005,313,306,2001,308)
           AND a.PRIMARY_INSTANCE = 1
           AND b.RESELLER_CUST_NO = 100000081;

        SELECT
            CASE
                WHEN a.BILLING_CODE = 'UDR.MVMSDSL'    THEN '_VOL'
                WHEN a.BILLING_CODE = 'UDR.MVMBAND'    THEN '_BAND'
                WHEN a.BILLING_CODE = 'UDR.MVMBACKUP'  THEN '_BACKUP'
                WHEN a.BILLING_CODE = 'UDR.MVMVSERVER' THEN '_VSERVER'
                WHEN a.BILLING_CODE = 'UDR.MVMDYNWEB'  THEN '_DYNWEB'
            ELSE NULL
            END
        INTO sBILLING_CODE
        FROM LEISTUNG a
        WHERE LEISTUNG__NO = :NEW.SERVICE_ELEM__NO
        AND A.PRIMARY_INSTANCE = 1;

--0002
        IF NVL(sBILLING_CODE, 'x') <> 'x' THEN
            BEGIN
                SELECT AUFTRAG_NO
                  INTO iAUFTRAG_NO
                  FROM ACCOUNT a
                 WHERE AUFTRAG_NO = iAUFTRAG_NO;
            EXCEPTION
                WHEN NO_DATA_FOUND THEN
                    INSERT INTO ACCOUNT (AUFTRAG_NO, ACCOUNT_ID, ACCOUNTNAME, USERW, DATEW)
                    VALUES (iAUFTRAG_NO, NVL(cSAP_ID, to_char(:NEW.ORDER__NO)) || sBILLING_CODE , NVL(cSAP_ID, to_char(:NEW.ORDER__NO)) || sBILLING_CODE, :NEW.USERW, :NEW.DATEW);
                WHEN OTHERS THEN
                    NULL;
            END;
        END IF;
    END IF;
EXCEPTION
    WHEN OTHERS THEN
    NULL;
END;
/

--
-- TRG_DEL_AUFTRAGPOS  (Trigger)
--

CREATE OR REPLACE TRIGGER TRG_DEL_AUFTRAGPOS
AFTER DELETE ON AUFTRAGPOS FOR EACH ROW
DECLARE
    PRAGMA AUTONOMOUS_TRANSACTION;
    iLEISTUNG__NO       LEISTUNG.LEISTUNG__NO%TYPE;
    sBILLING_CODE       LEISTUNG.BILLING_CODE%TYPE;
    iAUFTRAG_NO         AUFTRAG.AUFTRAG_NO%TYPE;
    cSAP_ID             AUFTRAG.SAP_ID%TYPE;
    iRESELLER_KUNDE_NO  CUSTOMER.RESELLER_CUST_NO%TYPE;
    iOE__NO             AUFTRAG.OE__NO%TYPE;
    iRet                NUMBER;
BEGIN
    SELECT a.AUFTRAG_NO, a.SAP_ID, a.OE__NO, b.RESELLER_CUST_NO
      INTO iAUFTRAG_NO,  cSAP_ID,   iOE__NO, iRESELLER_KUNDE_NO
      FROM AUFTRAG a,
           CUSTOMER b
     WHERE a.AUFTRAG__NO = :OLD.ORDER__NO
       AND a.CUST_NO = b.CUST_NO
       AND a.OE__NO IN (302,2157,2161,3005,2006,304,305,2000,2002,314,2005,313,306,2001,308)
       AND a.PRIMARY_INSTANCE = 1;

    SELECT
        CASE
            WHEN a.BILLING_CODE = 'UDR.MVMSDSL'    THEN '_VOL'
            WHEN a.BILLING_CODE = 'UDR.MVMBAND'    THEN '_BAND'
            WHEN a.BILLING_CODE = 'UDR.MVMBACKUP'  THEN '_BACKUP'
            WHEN a.BILLING_CODE = 'UDR.MVMVSERVER' THEN '_VSERVER'
            WHEN a.BILLING_CODE = 'UDR.MVMDYNWEB'  THEN '_DYNWEB'
        ELSE NULL
        END
    INTO sBILLING_CODE
    FROM LEISTUNG a
    WHERE LEISTUNG__NO = :OLD.SERVICE_ELEM__NO
    AND A.PRIMARY_INSTANCE = 1;

    IF NVL(sBILLING_CODE, 'x') <> 'x' AND iRESELLER_KUNDE_NO = 100000081 THEN
        BEGIN
            BEGIN
-- wenn gibt es keine andere Pos mit gleichem BillingCode - dann löschen
                SELECT ITEM__NO
                INTO iRet
                FROM AUFTRAGPOS a,
                     LEISTUNG b
                WHERE b.LEISTUNG__NO = a.SERVICE_ELEM__NO
                  AND b.PRIMARY_INSTANCE = 1
                  AND A.ORDER__NO = :OLD.ORDER__NO
                  AND A.ITEM__NO <> :OLD.ITEM__NO
                  AND b.BILLING_CODE = CASE
                                        WHEN sBILLING_CODE = '_VOL'     THEN 'UDR.MVMSDSL'
                                        WHEN sBILLING_CODE = '_BAND'    THEN 'UDR.MVMBAND'
                                        WHEN sBILLING_CODE = '_BACKUP'  THEN 'UDR.MVMBACKUP'
                                        WHEN sBILLING_CODE = '_VSERVER' THEN 'UDR.MVMVSERVER'
                                        WHEN sBILLING_CODE = '_DYNWEB'  THEN 'UDR.MVMDYNWEB'
                                       END
                  AND rownum = 1;

            EXCEPTION
                WHEN NO_DATA_FOUND THEN

                DELETE FROM ACCOUNT a
                 WHERE a.AUFTRAG_NO = iAUFTRAG_NO
                  AND a.ACCOUNT_ID LIKE '%'|| sBILLING_CODE||'%';
                COMMIT;
            END;
        EXCEPTION
            WHEN OTHERS THEN
                NULL;
        END;
    END IF;

EXCEPTION
    WHEN OTHERS THEN
    NULL;
END;
/

--
-- TRG_UPD_AUFTRAG  (Trigger)
--

CREATE OR REPLACE TRIGGER TRG_UPD_AUFTRAG
AFTER UPDATE ON AUFTRAG FOR EACH ROW
DECLARE
    iLEISTUNG__NO       LEISTUNG.LEISTUNG__NO%TYPE;
    sBILLING_CODE       LEISTUNG.BILLING_CODE%TYPE;
    iAUFTRAG_NO         AUFTRAG.AUFTRAG_NO%TYPE;
    cSAP_ID             AUFTRAG.SAP_ID%TYPE;
    iRESELLER_KUNDE_NO  CUSTOMER.RESELLER_CUST_NO%TYPE;
    iOE__NO             AUFTRAG.OE__NO%TYPE;
    iRet                NUMBER;
BEGIN
    IF :NEW.OE__NO IN (302,2157,2161,3005,2006,304,305,2000,2002,314,2005,313,306,2001,308)
        AND :NEW.PRIMARY_INSTANCE = 1 THEN
        -- S T O R N O --
        IF :NEW.ASTATUS = 7 THEN
            BEGIN
                SELECT b.RESELLER_CUST_NO
                  INTO iRESELLER_KUNDE_NO
                  FROM CUSTOMER b
                 WHERE b.CUST_NO = :NEW.CUST_NO
                   AND b.RESELLER_CUST_NO = 100000081;

                DELETE FROM ACCOUNT a
                 WHERE a.AUFTRAG_NO = :NEW.AUFTRAG_NO
                   AND (a.ACCOUNT_ID  LIKE '%_VOL%' OR
                        a.ACCOUNT_ID  LIKE '%_BAND%' OR
                        a.ACCOUNT_ID  LIKE '%_BACKUP%' OR
                        a.ACCOUNT_ID  LIKE '%_VSERVER%' OR
                        a.ACCOUNT_ID  LIKE '%_DYNWEB%');
            EXCEPTION
                WHEN OTHERS THEN
                    NULL;
            END;
        END IF;
        IF NVL(:NEW.SAP_ID, to_char(:NEW.AUFTRAG__NO)) <> NVL(:OLD.SAP_ID, 'x') THEN
            BEGIN
                SELECT b.RESELLER_CUST_NO
                  INTO iRESELLER_KUNDE_NO
                  FROM CUSTOMER b
                 WHERE b.CUST_NO = :NEW.CUST_NO
                   AND b.RESELLER_CUST_NO = 100000081;

                UPDATE ACCOUNT a SET
                       a.ACCOUNT_ID = CASE
                                        WHEN a.ACCOUNT_ID LIKE '%_VOL%'      THEN NVL(:NEW.SAP_ID, to_char(:NEW.AUFTRAG__NO)) || '_VOL'
                                        WHEN a.ACCOUNT_ID LIKE '%_BAND%'     THEN NVL(:NEW.SAP_ID, to_char(:NEW.AUFTRAG__NO)) || '_BAND'
                                        WHEN a.ACCOUNT_ID LIKE '%_BACKUP%'   THEN NVL(:NEW.SAP_ID, to_char(:NEW.AUFTRAG__NO)) || '_BACKUP'
                                        WHEN a.ACCOUNT_ID LIKE '%_VSERVER%'  THEN NVL(:NEW.SAP_ID, to_char(:NEW.AUFTRAG__NO)) || '_VSERVER'
                                        WHEN a.ACCOUNT_ID LIKE '%_DYNWEB%'   THEN NVL(:NEW.SAP_ID, to_char(:NEW.AUFTRAG__NO)) || '_DYNWEB'
                                      ELSE a.ACCOUNT_ID
                                      END,
                       a.ACCOUNTNAME = CASE
                                        WHEN a.ACCOUNT_ID LIKE '%_VOL%'      THEN NVL(:NEW.SAP_ID, to_char(:NEW.AUFTRAG__NO)) || '_VOL'
                                        WHEN a.ACCOUNT_ID LIKE '%_BAND%'     THEN NVL(:NEW.SAP_ID, to_char(:NEW.AUFTRAG__NO)) || '_BAND'
                                        WHEN a.ACCOUNT_ID LIKE '%_BACKUP%'   THEN NVL(:NEW.SAP_ID, to_char(:NEW.AUFTRAG__NO)) || '_BACKUP'
                                        WHEN a.ACCOUNT_ID LIKE '%_VSERVER%'  THEN NVL(:NEW.SAP_ID, to_char(:NEW.AUFTRAG__NO)) || '_VSERVER'
                                        WHEN a.ACCOUNT_ID LIKE '%_DYNWEB%'   THEN NVL(:NEW.SAP_ID, to_char(:NEW.AUFTRAG__NO)) || '_DYNWEB'
                                      ELSE a.ACCOUNT_ID
                                      END
                 WHERE a.AUFTRAG_NO = :NEW.AUFTRAG_NO;
            EXCEPTION
                WHEN OTHERS THEN
                    NULL;
            END;
        END IF;
    END IF;
EXCEPTION
    WHEN OTHERS THEN
    NULL;
END;
/
BEGIN
   sys.UTL_RECOMP.recomp_serial ();
   COMMIT;
END;
/
