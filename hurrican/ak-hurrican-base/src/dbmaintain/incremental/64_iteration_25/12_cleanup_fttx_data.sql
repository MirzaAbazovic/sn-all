-- FTTX Testdaten loeschen (nicht MNET)
-- Aufbau der Testdaten für die Testsysteme erfolgt in modify-hurrican4devel.sql

delete from T_FTTX_CVLAN where EKP_FRAME_CONTRACT_ID != 1;
delete from T_FTTX_EKP_2_A10NSPPORT where EKP_FRAME_CONTRACT_ID != 1;
delete from T_FTTX_EKP_FRAME_CONTRACT  where ID != 1;

create table T_FTTX_A10_NSP_PORT_TDN_TEMP (VBZ_ID decimal(19)); 
insert into T_FTTX_A10_NSP_PORT_TDN_TEMP (VBZ_ID) select VBZ_ID from T_FTTX_A10_NSP_PORT where ID != 1;
delete from T_FTTX_A10_PORT_2_OLT where A10_NSP_PORT_ID != 1;
delete from T_FTTX_A10_NSP_PORT where ID != 1;
delete from T_FTTX_A10_NSP where ID != 1;
delete from T_TDN where ID in (select VBZ_ID from T_FTTX_A10_NSP_PORT_TDN_TEMP);
drop table T_FTTX_A10_NSP_PORT_TDN_TEMP;

-- MNET hat keine PBits
update T_FTTX_CVLAN set PBIT_LIMIT = null;
