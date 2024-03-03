-- create new columns for sip login and hauptrufnummer on dn_plan table
ALTER TABLE T_AUFTRAG_VOIP_DN_PLAN ADD (SIP_LOGIN VARCHAR2(130 BYTE));
ALTER TABLE T_AUFTRAG_VOIP_DN_PLAN ADD (SIP_HAUPTRUFNUMMER VARCHAR2(30 BYTE));

-- move sip login and sip hauptrufnummer to dn_plan table
CREATE OR REPLACE PROCEDURE move_sip_login
IS
  BEGIN
    FOR voip_dn IN (SELECT dn.ID, dn.SIP_LOGIN, dn.SIP_HAUPTRUFNUMMER
                    FROM T_AUFTRAG_VOIP_DN dn
                    WHERE dn.SIP_LOGIN IS NOT NULL)
    LOOP
      UPDATE T_AUFTRAG_VOIP_DN_PLAN p
        SET p.SIP_LOGIN = voip_dn.SIP_LOGIN,
            p.SIP_HAUPTRUFNUMMER = voip_dn.SIP_HAUPTRUFNUMMER
        WHERE p.AUFTRAG_VOIP_DN_ID = voip_dn.ID;
    END LOOP;
  END;
/
CALL move_sip_login();
DROP PROCEDURE move_sip_login;

ALTER TABLE T_AUFTRAG_VOIP_DN DROP COLUMN SIP_LOGIN;
ALTER TABLE T_AUFTRAG_VOIP_DN DROP COLUMN SIP_HAUPTRUFNUMMER;