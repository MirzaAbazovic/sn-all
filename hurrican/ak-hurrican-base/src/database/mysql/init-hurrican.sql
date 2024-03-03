--
-- Ueber dieses SQL-Script kann eine neue Hurrican-DB initialisiert werden.
--

-- T_CARRIER korrigieren
update t_carrier set ORDER_NO=2 where TEXT='DTAG';
update t_carrier set ORDER_NO=3 where TEXT<>'DTAG';
update t_carrier set ORDER_NO=1 where ID=1;

-- T_RANGIERUNG korrigieren
update t_rangierung set LEITUNG_LOESCHEN=1 where PHYSIK_TYP IN (9,10);
update t_rangierung set ES_ID=-1 where ES_ID=999999;
update t_rangierung set LEITUNG_GESAMT_ID=null, LEITUNG_LFD_NR=null where leitung_loeschen=1 and (es_id is null or es_id = -1);

-- T_UEVT_2_ZIEL initialisieren
LOCK TABLES t_uevt_2_ziel WRITE;
INSERT INTO t_uevt_2_ziel (prod_id, uevt_id, hvt_standort_id_ziel) VALUES (12,1,59),(12,2,59),(12,3,58),(12,4,58),(12,5,58),(12,6,58),(12,7,58),(12,8,58),(12,9,59),(12,10,59),(12,11,59),(12,12,59),(12,13,58),(12,14,59),(12,15,59),(12,16,58),(12,17,58),(12,18,59),(12,19,58),(12,20,58),(12,21,59),(12,22,59),(12,23,58),(12,24,59),(12,25,59),(12,26,59),(12,27,59),(12,28,59),(12,29,59),(12,30,59),(12,31,58),(12,32,58),(12,33,58),(12,34,58),(12,35,58),(12,36,59),(12,38,58),(12,39,59),(12,40,59),(12,41,58),(12,42,58),(12,43,58),(12,44,58),(12,45,58),(12,46,58),(36,1,59),(36,2,59),(36,3,58),(36,4,58),(36,5,58),(36,6,58),(36,7,58),(36,8,58),(36,9,59),(36,10,59),(36,11,59),(36,12,59),(36,13,58),(36,14,59),(36,15,59),(36,16,58),(36,17,58),(36,18,59),(36,19,58),(36,20,58),(36,21,59),(36,22,59),(36,23,58),(36,24,59),(36,25,59),(36,26,59),(36,27,59),(36,28,59),(36,29,59),(36,30,59),(36,31,58),(36,32,58),(36,33,58),(36,34,58),(36,35,58),(36,36,59),(36,38,58),(36,39,59),(36,40,59),(36,41,58),(36,42,58),(36,43,58),(36,44,58),(36,45,58),(36,46,58);
UNLOCK TABLES;

-- T_HVT_TECHNIK initialisieren
LOCK TABLES `t_hvt_technik` WRITE;
INSERT INTO `t_hvt_technik` VALUES (1,'Siemens','Siemens-Technik',''),(2,'Huawei','Huawei-Technik','');
UNLOCK TABLES;

-- T_PHYSIKTYP
LOCK TABLES `t_physiktyp` WRITE;
INSERT INTO `t_physiktyp` VALUES (1,'UK0',NULL,1),(2,'PMX',NULL,1),(3,'AB',NULL,1),(4,'4H','',1),(5,'2H',NULL,1),(7,'ADSL-DA',NULL,1),(8,'ADSL-UK0',NULL,1),(9,'SDSL-DA','',1),(10,'SDSL-UK0',NULL,1),(11,'undefiniert',NULL,NULL),(12,'UK0 (H)','',2),(13,'PMX (H)','',2),(14,'AB (H)','',2),(15,'4H (H)','',2),(16,'2H (H)','',2),(17,'ADSL-DA (H)','',2),(18,'ADSL-UK0 (H)','',2),(19,'SDSL-DA (H)','',2);
UNLOCK TABLES;

-- T_PRODUKT_2_PHYSIKTYP
LOCK TABLES `t_produkt_2_physiktyp` WRITE;
INSERT INTO `t_produkt_2_physiktyp` VALUES (28,1,1,NULL,NULL),(29,2,1,NULL,NULL),(30,37,1,0,9),(31,38,1,0,9),(32,3,2,NULL,NULL),(33,4,2,NULL,NULL),(34,5,3,NULL,NULL),(35,8,4,NULL,NULL),(36,22,4,NULL,NULL),(37,23,4,NULL,NULL),(38,24,4,NULL,NULL),(39,25,4,NULL,NULL),(40,6,5,NULL,NULL),(41,7,5,NULL,NULL),(42,19,5,NULL,NULL),(43,20,5,NULL,NULL),(44,21,5,NULL,NULL),(45,9,7,0,NULL),(46,55,7,0,NULL),(47,56,7,0,NULL),(48,57,7,0,NULL),(49,10,8,0,7),(50,11,9,0,NULL),(51,16,9,0,NULL),(52,17,9,0,NULL),(53,18,9,0,NULL),(54,32,9,0,NULL),(55,33,9,0,NULL),(56,34,9,0,NULL),(57,35,9,0,NULL),(58,51,9,NULL,NULL),(59,52,9,NULL,NULL),(60,53,9,NULL,NULL),(61,54,9,NULL,NULL),(62,302,9,NULL,NULL),(63,303,9,NULL,NULL),(64,304,9,NULL,NULL),(65,305,9,NULL,NULL),(66,12,10,1,9),(67,36,10,1,9),(109,9,17,0,NULL),(110,56,17,0,NULL),(111,57,17,0,NULL),(112,55,17,0,NULL),(113,10,18,0,17),(114,16,19,0,NULL),(115,17,19,0,NULL),(116,18,19,0,NULL),(117,11,19,0,NULL);
UNLOCK TABLES;

-- T_ANSCHLUSSART erweitern
INSERT INTO T_ANSCHLUSSART (ID, ANSCHLUSSART) values (8, 'Netzkopplung');
update t_endstelle set ANSCHLUSSART=8 where ANBINDUNG='Netzkopplung';

-- T_SCHNITTSTELLE erweitern
INSERT INTO T_SCHNITTSTELLE (ID, SCHNITTSTELLE) values (16, 'ATM');


-- VERLAUFS-Tabellen
INSERT INTO T_VERLAUF_STATUS (id, verlauf_status) values (1, 'im Umlauf');
INSERT INTO T_VERLAUF_STATUS (id, verlauf_status) values (2, 'in Bearbeitung');
INSERT INTO T_VERLAUF_STATUS (id, verlauf_status) values (3, 'erledigt');
--INSERT INTO T_VERLAUF_STATUS (id, verlauf_status) values (4, 'abgebrochen');

UPDATE T_BA_VERL_AEND set configurable=1;
INSERT INTO T_BA_VERL_AEND (id, text, ba_verl_aend_gruppe_id, configurable, is_auftragsart) values (50, 'Bandbreitenänderung', 0, 0, 1);
INSERT INTO T_BA_VERL_AEND (id, text, ba_verl_aend_gruppe_id, configurable, is_auftragsart) values (51, 'Up-/Downgrade', 0, 0, 1);
INSERT INTO T_BA_VERL_AEND (id, text, ba_verl_aend_gruppe_id, configurable, is_auftragsart) values (52, 'Absage', 0, 0, 0);
INSERT INTO T_BA_VERL_AEND (id, text, ba_verl_aend_gruppe_id, configurable, is_auftragsart) values (53, 'Projektierung', 0, 0, 0);
INSERT INTO T_BA_VERL_AEND (id, text, ba_verl_aend_gruppe_id, configurable, is_auftragsart) values (54, 'Sonderportierung', 0, 0, 0);
-- Kuendigung, Neuschaltung und Uebernahme nicht konfigurierbar
update T_BA_VERL_AEND set configurable=0, is_auftragsart=1 where id=13; 
update T_BA_VERL_AEND set configurable=0, is_auftragsart=1 where id=27; 
update T_BA_VERL_AEND set configurable=0, is_auftragsart=1 where id=28; 
update T_BA_VERL_AEND set configurable=0, is_auftragsart=0 where id=41; 
update T_BA_VERL_AEND set configurable=0, is_auftragsart=0 where id=42; 


