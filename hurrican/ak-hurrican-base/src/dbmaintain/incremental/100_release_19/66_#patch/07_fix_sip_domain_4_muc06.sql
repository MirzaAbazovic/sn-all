-- MGA war fuer MUC06 nicht konfiguriert -> MGA wieder auf BIZ umkonfigurieren
-- 22348 = mga.m-call.de
-- 22346 = biz.m-call.de
UPDATE T_PROD_2_SIP_DOMAIN
SET SIP_DOMAIN_REF_ID = 22346
WHERE PROD_ID IN (540, 541)
      AND SIP_DOMAIN_REF_ID = 22348
      AND HW_SWITCH = (SELECT ID
                       FROM T_HW_SWITCH
                       WHERE NAME = 'MUC06');
