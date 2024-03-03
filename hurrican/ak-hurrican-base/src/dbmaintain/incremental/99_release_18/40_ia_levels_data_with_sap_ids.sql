delete from T_IA_LEVEL5;
delete from T_IA_LEVEL3;
delete from T_IA_LEVEL1;

insert into T_IA_LEVEL1 (ID, NAME, SAP_ID) values (S_T_IA_LEVEL1_0.nextVal, 'ZP', 'ZP');
insert into T_IA_LEVEL3 (ID, LEVEL1_ID, NAME, SAP_ID) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL1 where NAME='ZP'),
   'FttBH', 'ZP-PL-FTTB');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='FttBH'),
   'Augsburg', 'ZP-PL-FTTB-AUG-010');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='FttBH'),
   'Augsburg Reese', 'ZP-PL-FTTB-AUG-020');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='FttBH'),
   'Augsburg - Ladeh'||UNISTR('\00F6')||'fe', 'ZP-PL-FTTB-AUG-030');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='FttBH'),
   'M'||UNISTR('\00FC')||'nchen FttB1', 'ZP-PL-FTTB-MUE-010');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='FttBH'),
   'M'||UNISTR('\00FC')||'nchen-S'||UNISTR('\00FC')||'dseite', 'ZP-PL-FTTB-MUE-020');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='FttBH'),
   'Olympiadorf', 'ZP-PL-FTTB-MUE-030');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='FttBH'),
   'Funkkaserne', 'ZP-PL-FTTB-MUE-040');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='FttBH'),
   'Erlangen', 'ZP-PL-FTTB-ERL-010');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='FttBH'),
   'W'||UNISTR('\00FC')||'rzburg', 'ZP-PL-FTTB-SON-010');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='FttBH'),
   'Essenbach', 'ZP-PL-FTTB-SON-020');


insert into T_IA_LEVEL3 (ID, LEVEL1_ID, NAME, SAP_ID) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL1 where NAME='ZP'),
   'FttC', 'ZP-PL-FTTC');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='FttC'),
   'MKK', 'ZP-PL-FTTC-MKK-010');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='FttC'),
   'MKK2', 'ZP-PL-FTTC-MKK-020');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='FttC'),
   'Bayern', 'ZP-PL-FTTC-SON-010');


insert into T_IA_LEVEL3 (ID, LEVEL1_ID, NAME, SAP_ID) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL1 where NAME='ZP'),
   'Zentrale Infrastruktur - Housing', 'ZP-LI-ZIHO');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='Zentrale Infrastruktur - Housing'),
   'Housing Stromversorgung inkl. Montage', 'ZP-LI-ZIHO-HOU-010');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='Zentrale Infrastruktur - Housing'),
   'Housing Klimatechnik inkl. Montage', 'ZP-LI-ZIHO-HOU-020');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='Zentrale Infrastruktur - Housing'),
   'Housing Raum'||UNISTR('\00FC')||'berwachung inkl. Montage', 'ZP-LI-ZIHO-HOU-030');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='Zentrale Infrastruktur - Housing'),
   'Housing Schr'||UNISTR('\00E4')||'nke', 'ZP-LI-ZIHO-HOU-040');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='Zentrale Infrastruktur - Housing'),
   'RZ Weltbild', 'ZP-LI-ZIHO-STH-010');


insert into T_IA_LEVEL3 (ID, LEVEL1_ID, NAME, SAP_ID) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL1 where NAME='ZP'),
   'Zentrale Infrastruktur - Sonstige', 'ZP-LI-ZISO');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='Zentrale Infrastruktur - Sonstige'),
   'Technikcenter Stromversorgung inkl. Montage', 'ZP-LI-ZISO-TEC-010');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='Zentrale Infrastruktur - Sonstige'),
   'Technikcenter Klimatechnik inkl. Montage', 'ZP-LI-ZISO-TEC-020');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='Zentrale Infrastruktur - Sonstige'),
   'Technikcenter Raum'||UNISTR('\00FC')||'berwachung inkl. Montage', 'ZP-LI-ZISO-TEC-030');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='Zentrale Infrastruktur - Sonstige'),
   'Technikcenter Schr'||UNISTR('\00E4')||'nke', 'ZP-LI-ZISO-TEC-040');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='Zentrale Infrastruktur - Sonstige'),
   'Eigener Tiefbau (inkl. Montagen)', 'ZP-LI-ZISO-LTE-010');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='Zentrale Infrastruktur - Sonstige'),
   'Entst'||UNISTR('\00F6')||'rung', 'ZP-LI-ZISO-ENT-010');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='Zentrale Infrastruktur - Sonstige'),
   'Meßger'||UNISTR('\00E4')||'te', 'ZP-LI-ZISO-ENT-020');


insert into T_IA_LEVEL3 (ID, LEVEL1_ID, NAME, SAP_ID) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL1 where NAME='ZP'),
   'IP-Systeme', 'ZP-LI-IPSY');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='IP-Systeme'),
   'Access u. Aggregation', 'ZP-LI-IPSY-NET-010');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='IP-Systeme'),
   'ISP-Service', 'ZP-LI-IPSY-NET-020');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='IP-Systeme'),
   'BRAS', 'ZP-LI-IPSY-NET-030');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='IP-Systeme'),
   'IP-MPLS-Network', 'ZP-LI-IPSY-NET-040');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='IP-Systeme'),
   'Netzwerk-Security', 'ZP-LI-IPSY-NET-050');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='IP-Systeme'),
   'DCN, Testing, etc.', 'ZP-LI-IPSY-NET-060');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='IP-Systeme'),
   'D-DOS', 'ZP-LI-IPSY-STH-010');


insert into T_IA_LEVEL3 (ID, LEVEL1_ID, NAME, SAP_ID) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL1 where NAME='ZP'),
   'Technologie - TV', 'ZP-LI-TETV');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='Technologie - TV'),
   'TV- Backbone (Non FttX)', 'ZP-LI-TETV-TV-010');


insert into T_IA_LEVEL3 (ID, LEVEL1_ID, NAME, SAP_ID) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL1 where NAME='ZP'),
   'Technologie - Sonstiges', 'ZP-LI-TESO');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='Technologie - Sonstiges'),
   'HVT-Nachr'||UNISTR('\00FC')||'stung', 'ZP-LI-TESO-NET-010');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='Technologie - Sonstiges'),
   'Sprachvermittlungssysteme', 'ZP-LI-TESO-NET-020');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='Technologie - Sonstiges'),
   'Sonstiges', 'ZP-LI-TESO-NET-090');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='Technologie - Sonstiges'),
   'ATM-DSLAM Abl'||UNISTR('\00F6')||'sung', 'ZP-LI-TESO-STH-010');


insert into T_IA_LEVEL3 (ID, LEVEL1_ID, NAME, SAP_ID) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL1 where NAME='ZP'),
   'Ressourcen '||UNISTR('\0026')||' Dokumentation', 'ZP-LI-R+D');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='Ressourcen '||UNISTR('\0026')||' Dokumentation'),
   'Netzerhaltung Augsburg', 'ZP-LI-R+D-NEH-010');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='Ressourcen '||UNISTR('\0026')||' Dokumentation'),
   'Netzerhaltung M'||UNISTR('\00FC')||'nchen', 'ZP-LI-R+D-NEH-020');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='Ressourcen '||UNISTR('\0026')||' Dokumentation'),
   'Netzerhaltung N'||UNISTR('\00FC')||'rnberg', 'ZP-LI-R+D-NEH-030');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='Ressourcen '||UNISTR('\0026')||' Dokumentation'),
   'Netzerhaltung '||UPPER(UNISTR('\00FC'))||'berregionale Verbindungen', 'ZP-LI-R+D-NEH-040');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='Ressourcen '||UNISTR('\0026')||' Dokumentation'),
   'Netzerweiterungen Augsburg', 'ZP-LI-R+D-NEW-010');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='Ressourcen '||UNISTR('\0026')||' Dokumentation'),
   'Netzerweiterungen M'||UNISTR('\00FC')||'nchen', 'ZP-LI-R+D-NEW-020');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='Ressourcen '||UNISTR('\0026')||' Dokumentation'),
   'Netzerweiterungen N'||UNISTR('\00FC')||'rnberg', 'ZP-LI-R+D-NEW-030');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='Ressourcen '||UNISTR('\0026')||' Dokumentation'),
   'Netzerweiterungen '||UPPER(UNISTR('\00FC'))||'berregionale Verbindungen', 'ZP-LI-R+D-NEW-040');



-- Produktion und Dokumentation
insert into T_IA_LEVEL1 (ID, NAME, SAP_ID) values (S_T_IA_LEVEL1_0.nextVal, 'Produktion und Dokumentation', 'PD');
insert into T_IA_LEVEL3 (ID, LEVEL1_ID, NAME, SAP_ID) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL1 where NAME='Produktion und Dokumentation'),
   'Großprojekte GK', 'PD-PL-GPGK');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='Großprojekte GK'),
   'Großprojekte GK', 'PD-PL-GPGK-GPGK-010');


insert into T_IA_LEVEL3 (ID, LEVEL1_ID, NAME, SAP_ID) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL1 where NAME='Produktion und Dokumentation'),
   'Tech-IT', 'PD-LI-TEIT');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='Tech-IT'),
   'Tech-IT OSS', 'PD-LI-TEIT-TEIT-010');


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



-- Portfolio- '||UNISTR('\0026')||' Produktmanagement
insert into T_IA_LEVEL1 (ID, NAME, SAP_ID) values (S_T_IA_LEVEL1_0.nextVal, 
   'Portfolio- '||UNISTR('\0026')||' Produktmanagement', 'PPM');
insert into T_IA_LEVEL3 (ID, LEVEL1_ID, NAME, SAP_ID) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL1 where NAME='Portfolio- '||UNISTR('\0026')||' Produktmanagement'),
   'Endger'||UNISTR('\00E4')||'te', 'PPM-LI-EG');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='Endger'||UNISTR('\00E4')||'te'),
   'Endger'||UNISTR('\00E4')||'te', 'PPM-LI-EG-EG-010');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='Endger'||UNISTR('\00E4')||'te'),
   'Mitarbeiter Endger'||UNISTR('\00E4')||'te', 'PPM-LI-EG-EG-020');



-- IT Systeme und Projekte
insert into T_IA_LEVEL1 (ID, NAME, SAP_ID) values (S_T_IA_LEVEL1_0.nextVal, 'IT Systeme und Projekte', 'IT');
insert into T_IA_LEVEL3 (ID, LEVEL1_ID, NAME, SAP_ID) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL1 where NAME='IT Systeme und Projekte'),
   'IT Planvorhaben', 'IT-PL-IT');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='IT Planvorhaben'),
   'Retail', 'IT-PL-IT-RET-010');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='IT Planvorhaben'),
   'Online + Portale', 'IT-PL-IT-O+P-010');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='IT Planvorhaben'),
   'Sonstige Projekte', 'IT-PL-IT-SON-010');


insert into T_IA_LEVEL3 (ID, LEVEL1_ID, NAME, SAP_ID) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL1 where NAME='IT Systeme und Projekte'),
   'IT Linie', 'IT-LI-IT');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='IT Linie'),
   'Hardware Infrastruktur', 'IT-LI-IT-HW-010');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='IT Linie'),
   'Softwarelizenzen', 'IT-LI-IT-SW-010');



-- Marketing '||UNISTR('\0026')||' Vertrieb (BGA)
insert into T_IA_LEVEL1 (ID, NAME, SAP_ID) values (S_T_IA_LEVEL1_0.nextVal, 
   'Marketing '||UNISTR('\0026')||' Vertrieb (BGA)', 'MV');
insert into T_IA_LEVEL3 (ID, LEVEL1_ID, NAME, SAP_ID) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL1 where NAME='Marketing '||UNISTR('\0026')||' Vertrieb (BGA)'),
   'BU Gesch'||UNISTR('\00E4')||'ftskunden', 'MV-LI-BUGK');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='BU Gesch'||UNISTR('\00E4')||'ftskunden'),
   'BU Gesch'||UNISTR('\00E4')||'ftskunden', 'MV-LI-BUGK-GK-010');


insert into T_IA_LEVEL3 (ID, LEVEL1_ID, NAME, SAP_ID) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL1 where NAME='Marketing '||UNISTR('\0026')||' Vertrieb (BGA)'),
   'BU Privatkunden', 'MV-LI-BUPK');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='BU Privatkunden'),
   'Vertriebspartner PK', 'MV-LI-BUPK-PK-010');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='BU Privatkunden'),
   'Marketing PK', 'MV-LI-BUPK-PK-020');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='BU Privatkunden'),
   'Shops PK', 'MV-LI-BUPK-PK-030');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='BU Privatkunden'),
   'Online', 'MV-LI-BUPK-PK-040');



-- Einkauf/ MaWi/Zentrale Dienste (BGA)
insert into T_IA_LEVEL1 (ID, NAME, SAP_ID) values (S_T_IA_LEVEL1_0.nextVal, 
   'Einkauf/ MaWi/Zentrale Dienste (BGA)', 'EMZ');
insert into T_IA_LEVEL3 (ID, LEVEL1_ID, NAME, SAP_ID) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL1 where NAME='Einkauf/ MaWi/Zentrale Dienste (BGA)'),
   'Einkauf/ MaWi/Zentrale Dienste (BGA)', 'EMZ-LI-EMZ');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='Einkauf/ MaWi/Zentrale Dienste (BGA)'),
   'BGA', 'EMZ-LI-EMZ-BGA-010');



-- Controlling/ BICC
insert into T_IA_LEVEL1 (ID, NAME, SAP_ID) values (S_T_IA_LEVEL1_0.nextVal, 'Controlling/ BICC', 'CO');
insert into T_IA_LEVEL3 (ID, LEVEL1_ID, NAME, SAP_ID) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL1 where NAME='Controlling/ BICC'),
   'Controlling/ BICC', 'CO-PL-BICC');
insert into T_IA_LEVEL5 (ID, LEVEL3_ID, NAME, SAP_ID) values (S_T_IA_LEVEL5_0.nextVal,
   (select ID from T_IA_LEVEL3 where name='Controlling/ BICC'),
   'DWH', 'CO-PL-BICC-DWH-010');

