
-- Aenderung fuer Rangierungsfreigabe
ALTER TABLE T_HVT_STANDORT ADD BREAK_RANG char(1);
update T_HVT_STANDORT set BREAK_RANG = 0;
update T_HVT_STANDORT set BREAK_RANG = 1 where hvt_id_standort = 96;