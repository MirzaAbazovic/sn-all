--
-- Add extra Data for BRAS Pools
--

ALTER TABLE T_BRAS_POOLS
 ADD (NAS_IDENTIFIER  VARCHAR2(32 BYTE));

ALTER TABLE T_BRAS_POOLS
 ADD (SLOT  NUMBER(10));

ALTER TABLE T_BRAS_POOLS
 ADD (PORT  NUMBER(10));

ALTER TABLE T_BRAS_POOLS
 ADD (BACKUP_NAS_IDENTIFIER  VARCHAR2(32 BYTE));

ALTER TABLE T_BRAS_POOLS
 ADD (BACKUP_SLOT  NUMBER(10));

ALTER TABLE T_BRAS_POOLS
 ADD (BACKUP_PORT  NUMBER(10));