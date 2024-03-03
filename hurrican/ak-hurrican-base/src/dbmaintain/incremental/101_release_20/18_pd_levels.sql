-- Ebenen 1,3,5 der Produktion und Dokumentation
-- In Taifun ermittelte Produkt Namen (OE)
--    Direct Access
--    M-net IP-VPN
--    Connect-Classic
--    Connect-LAN
--    MetroEthernet
--    M-net MetroEthernet
--    dark fibre
--    Housing
--    Premium

delete from T_IA_LEVEL5;
delete from T_IA_LEVEL3;
delete from T_IA_LEVEL1;

-- Produktion und Dokumentation
insert into T_IA_LEVEL1 (ID, NAME, SAP_ID, BEREICH_NAME) values (S_T_IA_LEVEL1_0.nextVal, 'Produktion und Dokumentation', 'PD', 'TE-PD');
insert into T_IA_LEVEL3 (ID, LEVEL1_ID, NAME, SAP_ID) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL1 where NAME='Produktion und Dokumentation'),
   'Gro'||UNISTR('\00DF')||'projekte GK', 'PD-PL-GPGK');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='Gro'||UNISTR('\00DF')||'projekte GK'),
   'Gro'||UNISTR('\00DF')||'projekte GK', 'PD-PL-GPGK-GPGK-010');


insert into T_IA_LEVEL3 (ID, LEVEL1_ID, NAME, SAP_ID) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL1 where NAME='Produktion und Dokumentation'),
   'Standardprojekte GK', 'PD-LI-SPGK');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='Standardprojekte GK'),
   'Direct Access', 'PD-LI-SPGK-SPGK-010');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='Standardprojekte GK'),
   'IP-VPN', 'PD-LI-SPGK-SPGK-020');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='Standardprojekte GK'),
   'Connect-Classic', 'PD-LI-SPGK-SPGK-030');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='Standardprojekte GK'),
   'Connect-LAN', 'PD-LI-SPGK-SPGK-040');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='Standardprojekte GK'),
   'MetroEthernet', 'PD-LI-SPGK-SPGK-050');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='Standardprojekte GK'),
   'Dark Fiber', 'PD-LI-SPGK-SPGK-060');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='Standardprojekte GK'),
   'Housing (PD)', 'PD-LI-SPGK-SPGK-070');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='Standardprojekte GK'),
   'Premium', 'PD-LI-SPGK-SPGK-080');
