-- ANF-449: Konfiguration fuer den MUC06 auf IMS/NSP anpassen

-- Premium Glasfaser DSL auf MGA umstellen
UPDATE T_PROD_2_SIP_DOMAIN SET SIP_DOMAIN_REF_ID = 22348 WHERE PROD_ID = 540 AND SIP_DOMAIN_REF_ID = 22346;

-- Glasfaser SDSL auf MGA umstellen
UPDATE T_PROD_2_SIP_DOMAIN SET SIP_DOMAIN_REF_ID = 22348 WHERE PROD_ID = 541 AND SIP_DOMAIN_REF_ID = 22346;


-- Beziehung zu den Endgeraeten konfigurieren
INSERT INTO T_PROD_2_SIP_DOMAIN
  (ID, PROD_ID, HW_SWITCH, SIP_DOMAIN_REF_ID, USERW, VERSION, DEFAULT_DOMAIN, EG_TYPE_ID)
VALUES
  (S_T_PROD_2_SIP_DOMAIN_0.nextval, null, 1, 22346, 'migration', 0, null, 281);

INSERT INTO T_PROD_2_SIP_DOMAIN
  (ID, PROD_ID, HW_SWITCH, SIP_DOMAIN_REF_ID, USERW, VERSION, DEFAULT_DOMAIN, EG_TYPE_ID)
VALUES
  (S_T_PROD_2_SIP_DOMAIN_0.nextval, null, 1, 22346, 'migration', 0, null, 282);

INSERT INTO T_PROD_2_SIP_DOMAIN
  (ID, PROD_ID, HW_SWITCH, SIP_DOMAIN_REF_ID, USERW, VERSION, DEFAULT_DOMAIN, EG_TYPE_ID)
VALUES
  (S_T_PROD_2_SIP_DOMAIN_0.nextval, null, 1, 22346, 'migration', 0, null, 341);

INSERT INTO T_PROD_2_SIP_DOMAIN
  (ID, PROD_ID, HW_SWITCH, SIP_DOMAIN_REF_ID, USERW, VERSION, DEFAULT_DOMAIN, EG_TYPE_ID)
VALUES
  (S_T_PROD_2_SIP_DOMAIN_0.nextval, null, 1, 22346, 'migration', 0, null, 342);
