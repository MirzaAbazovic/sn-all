-- Per default soll die Spalte CBV_TYPE = ESAA_INTERN sein
-- Constraint f�r Discriminator Spalte hinzugef�gt

ALTER TABLE T_CB_VORGANG
MODIFY(CBV_TYPE  DEFAULT 'ESAA_INTERN');

ALTER TABLE T_CB_VORGANG
 ADD CONSTRAINT T_CB_VORGANG_TYPE
  CHECK (CBV_TYPE IN ('ESAA_INTERN','WITA'));