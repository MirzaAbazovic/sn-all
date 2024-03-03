insert into T_IA_LEVEL1 (ID, NAME) values (S_T_IA_LEVEL1_0.nextVal, 'ZP');
insert into T_IA_LEVEL2 (ID, LEVEL1_ID, NAME) values (S_T_IA_LEVEL2_0.nextVal,
   (select ID from T_IA_LEVEL1 where NAME='ZP'),
   'FttBH');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='FttBH'),
   'Augsburg');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='FttBH'),
   'Augsburg Reese');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='FttBH'),
   'Augsburg - Ladeh'||UNISTR('\00F6')||'fe');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='FttBH'),
   'M'||UNISTR('\00FC')||'nchen FttB1');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='FttBH'),
   'M'||UNISTR('\00FC')||'nchen-S'||UNISTR('\00FC')||'dseite');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='FttBH'),
   'Olympiadorf');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='FttBH'),
   'Funkkaserne');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='FttBH'),
   'Erlangen');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='FttBH'),
   'W'||UNISTR('\00FC')||'rzburg');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='FttBH'),
   'Essenbach');



insert into T_IA_LEVEL2 (ID, LEVEL1_ID, NAME) values (S_T_IA_LEVEL2_0.nextVal,
   (select ID from T_IA_LEVEL1 where NAME='ZP'),
   'FttC');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='FttC'),
   'MKK');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='FttC'),
   'MKK2');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='FttC'),
   'Bayern');



insert into T_IA_LEVEL2 (ID, LEVEL1_ID, NAME) values (S_T_IA_LEVEL2_0.nextVal,
   (select ID from T_IA_LEVEL1 where NAME='ZP'),
   'Zentrale Infrastruktur - Housing');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='Zentrale Infrastruktur - Housing'),
   'Housing Stromversorgung inkl. Montage');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='Zentrale Infrastruktur - Housing'),
   'Housing Klimatechnik inkl. Montage');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='Zentrale Infrastruktur - Housing'),
   'Housing Raum'||UNISTR('\00FC')||'berwachung inkl. Montage');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='Zentrale Infrastruktur - Housing'),
   'Housing Schr'||UNISTR('\00E4')||'nke');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='Zentrale Infrastruktur - Housing'),
   'RZ Weltbild');


insert into T_IA_LEVEL2 (ID, LEVEL1_ID, NAME) values (S_T_IA_LEVEL2_0.nextVal,
   (select ID from T_IA_LEVEL1 where NAME='ZP'),
   'Zentrale Infrastruktur - Sonstige');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='Zentrale Infrastruktur - Sonstige'),
   'Technikcenter Stromversorgung inkl. Montage');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='Zentrale Infrastruktur - Sonstige'),
   'Technikcenter Klimatechnik inkl. Montage');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='Zentrale Infrastruktur - Sonstige'),
   'Technikcenter Raum'||UNISTR('\00FC')||'berwachung inkl. Montage');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='Zentrale Infrastruktur - Sonstige'),
   'Technikcenter Schr'||UNISTR('\00E4')||'nke');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='Zentrale Infrastruktur - Sonstige'),
   'Eigener Tiefbau (inkl. Montagen)');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='Zentrale Infrastruktur - Sonstige'),
   'derzeit keine');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='Zentrale Infrastruktur - Sonstige'),
   'Entst'||UNISTR('\00F6')||'rung');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='Zentrale Infrastruktur - Sonstige'),
   'Meßger'||UNISTR('\00E4')||'te');


insert into T_IA_LEVEL2 (ID, LEVEL1_ID, NAME) values (S_T_IA_LEVEL2_0.nextVal,
   (select ID from T_IA_LEVEL1 where NAME='ZP'),
   'IP-Systeme');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='IP-Systeme'),
   'Access u. Aggregation');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='IP-Systeme'),
   'ISP-Service');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='IP-Systeme'),
   'BRAS');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='IP-Systeme'),
   'IP-MPLS-Network');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='IP-Systeme'),
   'Netzwerk-Security');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='IP-Systeme'),
   'DCN, Testing, etc.');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='IP-Systeme'),
   'D-DOS');


insert into T_IA_LEVEL2 (ID, LEVEL1_ID, NAME) values (S_T_IA_LEVEL2_0.nextVal,
   (select ID from T_IA_LEVEL1 where NAME='ZP'),
   'Technologie - TV');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='Technologie - TV'),
   'TV- Backbone (Non FttX)');


insert into T_IA_LEVEL2 (ID, LEVEL1_ID, NAME) values (S_T_IA_LEVEL2_0.nextVal,
   (select ID from T_IA_LEVEL1 where NAME='ZP'),
   'Technologie - Sonstiges');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='Technologie - Sonstiges'),
   'HVT-Nachr'||UNISTR('\00FC')||'stung');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='Technologie - Sonstiges'),
   'Sprachvermittlungssysteme');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='Technologie - Sonstiges'),
   'Sonstiges');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='Technologie - Sonstiges'),
   'ATM-DSLAM Abl'||UNISTR('\00F6')||'sung');

insert into T_IA_LEVEL2 (ID, LEVEL1_ID, NAME) values (S_T_IA_LEVEL2_0.nextVal,
   (select ID from T_IA_LEVEL1 where NAME='ZP'),
   'Ressourcen '||UNISTR('\0026')||' Dokumentation');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='Ressourcen '||UNISTR('\0026')||' Dokumentation'),
   'Netzerhaltung Augsburg');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='Ressourcen '||UNISTR('\0026')||' Dokumentation'),
   'Netzerhaltung M'||UNISTR('\00FC')||'nchen');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='Ressourcen '||UNISTR('\0026')||' Dokumentation'),
   'Netzerhaltung N'||UNISTR('\00FC')||'rnberg');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='Ressourcen '||UNISTR('\0026')||' Dokumentation'),
   'Netzerhaltung '||UPPER(UNISTR('\00FC'))||'berregionale Verbindungen');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='Ressourcen '||UNISTR('\0026')||' Dokumentation'),
   'Netzerweiterungen Augsburg');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='Ressourcen '||UNISTR('\0026')||' Dokumentation'),
   'Netzerweiterungen M'||UNISTR('\00FC')||'nchen');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='Ressourcen '||UNISTR('\0026')||' Dokumentation'),
   'Netzerweiterungen N'||UNISTR('\00FC')||'rnberg');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='Ressourcen '||UNISTR('\0026')||' Dokumentation'),
   'Netzerweiterungen '||UPPER(UNISTR('\00FC'))||'berregionale Verbindungen');



-- Produktion und Dokumentation
insert into T_IA_LEVEL1 (ID, NAME) values (S_T_IA_LEVEL1_0.nextVal, 'Produktion und Dokumentation');
insert into T_IA_LEVEL2 (ID, LEVEL1_ID, NAME) values (S_T_IA_LEVEL2_0.nextVal,
   (select ID from T_IA_LEVEL1 where NAME='Produktion und Dokumentation'),
   'Großprojekte GK');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='Großprojekte GK'),
   'Großprojekte GK');

insert into T_IA_LEVEL2 (ID, LEVEL1_ID, NAME) values (S_T_IA_LEVEL2_0.nextVal,
   (select ID from T_IA_LEVEL1 where NAME='Produktion und Dokumentation'),
   'Tech-IT');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='Tech-IT'),
   'Tech-IT OSS');

insert into T_IA_LEVEL2 (ID, LEVEL1_ID, NAME) values (S_T_IA_LEVEL2_0.nextVal,
   (select ID from T_IA_LEVEL1 where NAME='Produktion und Dokumentation'),
   'Standardprojekte GK');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='Standardprojekte GK'),
   'Direct Access');


-- Portfolio- '||UNISTR('\0026')||' Produktmanagement
insert into T_IA_LEVEL1 (ID, NAME) values (S_T_IA_LEVEL1_0.nextVal, 'Portfolio- '||UNISTR('\0026')||' Produktmanagement');
insert into T_IA_LEVEL2 (ID, LEVEL1_ID, NAME) values (S_T_IA_LEVEL2_0.nextVal,
   (select ID from T_IA_LEVEL1 where NAME='Portfolio- '||UNISTR('\0026')||' Produktmanagement'),
   'Endger'||UNISTR('\00E4')||'te');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='Endger'||UNISTR('\00E4')||'te'),
   'Endger'||UNISTR('\00E4')||'te');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='Endger'||UNISTR('\00E4')||'te'),
   'Mitarbeiter Endger'||UNISTR('\00E4')||'te');



-- IT Systeme und Projekte
insert into T_IA_LEVEL1 (ID, NAME) values (S_T_IA_LEVEL1_0.nextVal, 'IT Systeme und Projekte');
insert into T_IA_LEVEL2 (ID, LEVEL1_ID, NAME) values (S_T_IA_LEVEL2_0.nextVal,
   (select ID from T_IA_LEVEL1 where NAME='IT Systeme und Projekte'),
   'IT');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='IT'),
   'Retail');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='IT'),
   'Online + Portale');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='IT'),
   'Sonstige Projekte');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='IT'),
   'Hardware Infrastruktur');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='IT'),
   'Softwarelizenzen');



-- Marketing '||UNISTR('\0026')||' Vertrieb (BGA)
insert into T_IA_LEVEL1 (ID, NAME) values (S_T_IA_LEVEL1_0.nextVal, 'Marketing '||UNISTR('\0026')||' Vertrieb (BGA)');
insert into T_IA_LEVEL2 (ID, LEVEL1_ID, NAME) values (S_T_IA_LEVEL2_0.nextVal,
   (select ID from T_IA_LEVEL1 where NAME='Marketing '||UNISTR('\0026')||' Vertrieb (BGA)'),
   'BU Gesch'||UNISTR('\00E4')||'ftskunden');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='BU Gesch'||UNISTR('\00E4')||'ftskunden'),
   'BU Gesch'||UNISTR('\00E4')||'ftskunden');

insert into T_IA_LEVEL2 (ID, LEVEL1_ID, NAME) values (S_T_IA_LEVEL2_0.nextVal,
   (select ID from T_IA_LEVEL1 where NAME='Marketing '||UNISTR('\0026')||' Vertrieb (BGA)'),
   'BU Privatkunden');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='BU Privatkunden'),
   'Vertriebspartner PK');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='BU Privatkunden'),
   'Marketing PK');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='BU Privatkunden'),
   'Shops PK');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='BU Privatkunden'),
   'Online');



-- Einkauf/ MaWi/Zentrale Dienste (BGA)
insert into T_IA_LEVEL1 (ID, NAME) values (S_T_IA_LEVEL1_0.nextVal, 'Einkauf/ MaWi/Zentrale Dienste (BGA)');
insert into T_IA_LEVEL2 (ID, LEVEL1_ID, NAME) values (S_T_IA_LEVEL2_0.nextVal,
   (select ID from T_IA_LEVEL1 where NAME='Einkauf/ MaWi/Zentrale Dienste (BGA)'),
   'Einkauf/ MaWi/Zentrale Dienste (BGA)');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='Einkauf/ MaWi/Zentrale Dienste (BGA)'),
   'BGA');


-- Controlling/ BICC
insert into T_IA_LEVEL1 (ID, NAME) values (S_T_IA_LEVEL1_0.nextVal, 'Controlling/ BICC');
insert into T_IA_LEVEL2 (ID, LEVEL1_ID, NAME) values (S_T_IA_LEVEL2_0.nextVal,
   (select ID from T_IA_LEVEL1 where NAME='Controlling/ BICC'),
   'Controlling/ BICC');
insert into T_IA_LEVEL3 (ID, LEVEL2_ID, NAME) values (S_T_IA_LEVEL3_0.nextVal,
   (select ID from T_IA_LEVEL2 where name='Controlling/ BICC'),
   'DWH');


