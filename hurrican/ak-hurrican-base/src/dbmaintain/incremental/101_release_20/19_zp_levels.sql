-- Ebenen 1,3,5 der Zentralen Planung

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
   'Me'||UNISTR('\00DF')||'ger'||UNISTR('\00E4')||'te', 'ZP-LI-ZISO-ENT-020');


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
